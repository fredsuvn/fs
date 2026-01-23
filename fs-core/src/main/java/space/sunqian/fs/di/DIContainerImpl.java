package space.sunqian.fs.di;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.OutParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.dynamic.aop.AspectMaker;
import space.sunqian.fs.dynamic.aop.AspectSpec;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.invoke.InvocationException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class DIContainerImpl implements DIContainer {

    private static final Runnable EMPTY_RUNNABLE = () -> {};

    private final @Nonnull Map<@Nonnull Type, @Nonnull DIComponent> components;
    private final @Nonnull Map<@Nonnull Type, @Nonnull DIComponent> localComponents;
    private final @Nonnull List<@Nonnull DIContainer> parentContainers;

    private volatile int state = 0;

    public DIContainerImpl(
        @Nonnull Collection<@Nonnull Type> componentTypes,
        @Nonnull Collection<@Nonnull DIContainer> parentContainers,
        @Nonnull Collection<@Nonnull String> componentAnnotations,
        @Nonnull Collection<@Nonnull String> postConstructAnnotations,
        @Nonnull Collection<@Nonnull String> preDestroyAnnotations,
        @Nonnull DIComponent.Resolver resolver,
        @Nonnull DIComponent.FieldSetter fieldSetter
    ) throws DIInitializeException, DIException {
        Map<Type, Res> resMap = new LinkedHashMap<>();
        // add components from parent containers
        for (DIContainer container : parentContainers) {
            for (DIComponent component : container.components().values()) {
                resMap.put(component.type(), new Res(component.type(), component.instance()));
            }
        }
        Set<ResField> fieldSet = new LinkedHashSet<>();
        // generate instances
        for (Type componentType : componentTypes) {
            doDependencyInjection(
                componentType,
                componentAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                resolver,
                resMap,
                fieldSet
            );
        }
        // base injects:
        for (ResField resField : fieldSet) {
            setField(
                fieldSetter,
                resField.field,
                resField.owner.instance,
                getRes(resField.field.getGenericType(), resMap).instance
            );
        }
        // aop
        doAop(fieldSetter, resMap, fieldSet);
        // rewrite fields
        for (ResField resField : fieldSet) {
            boolean needsRewrite = false;
            Object owner;
            if (resField.owner.advisedInstance != null) {
                needsRewrite = true;
                owner = resField.owner.advisedInstance;
            } else {
                owner = resField.owner.instance;
            }
            Res valueRes = getRes(resField.field.getGenericType(), resMap);
            Object value;
            if (valueRes.advisedInstance != null) {
                needsRewrite = true;
                value = valueRes.advisedInstance;
            } else {
                value = valueRes.instance;
            }
            if (needsRewrite) {
                setField(fieldSetter, resField.field, owner, value);
            }
        }
        // components
        LinkedHashMap<Type, DIComponentImpl> components = new LinkedHashMap<>(resMap.size());
        LinkedHashMap<Type, DIComponentImpl> localComponents = new LinkedHashMap<>(resMap.size());
        for (Res res : resMap.values()) {
            Object inst = getResInstance(res);
            DIComponentImpl component = new DIComponentImpl(
                res.type,
                inst,
                res.local,
                res.postConstructMethod(),
                res.preDestroyMethod()
            );
            components.put(res.type, component);
            if (res.local) {
                localComponents.put(res.type, component);
            }
        }
        // complete
        this.parentContainers = ListKit.toList(parentContainers);
        this.components = Collections.unmodifiableMap(components);
        this.localComponents = Collections.unmodifiableMap(localComponents);
        // set dependencies
        configureComponentDependencies(resMap);
        // check cycle dependencies for post-construct methods and pre-destroy methods
        for (DIComponent component : localComponents.values()) {
            DIKit.checkCycleDependencies(component, DIComponent::postConstructDependencies);
            DIKit.checkCycleDependencies(component, DIComponent::preDestroyDependencies);
        }
    }

    private void configureComponentDependencies(
        @Nonnull Map<@Nonnull Type, @Nonnull Res> resMap
    ) {
        for (DIComponent value : localComponents.values()) {
            DIComponentImpl component = (DIComponentImpl) value;
            // @Resource
            Res res = getRes(component.type(), resMap);
            DIComponent.Descriptor descriptor = Fs.asNonnull(res.descriptor);
            component.dependenciesVar.addAll(
                descriptor.dependencyFields().stream()
                    .map(field -> getNonnullComponent(field.getGenericType()))
                    .collect(Collectors.toList())
            );
            // post-construct method parameters
            Method postConstructMethod = descriptor.postConstructMethod();
            if (postConstructMethod != null) {
                component.postConstructDependenciesVar.addAll(
                    Arrays.stream(postConstructMethod.getParameterTypes())
                        .map(this::getNonnullComponent)
                        .collect(Collectors.toList())
                );
            }
            // pre-destroy method parameters
            Method preDestroyMethod = descriptor.preDestroyMethod();
            if (preDestroyMethod != null) {
                component.preDestroyDependenciesVar.addAll(
                    Arrays.stream(preDestroyMethod.getParameterTypes())
                        .map(this::getNonnullComponent)
                        .collect(Collectors.toList())
                );
            }
            component.dependenciesVar.trimToSize();
            component.postConstructDependenciesVar.trimToSize();
            component.preDestroyDependenciesVar.trimToSize();
        }
    }

    private @Nonnull DIComponent getNonnullComponent(@Nonnull Type type) {
        return Fs.asNonnull(getComponent(type));
    }

    private void doDependencyInjection(
        @Nonnull Type type,
        @Nonnull Collection<@Nonnull String> componentAnnotations,
        @Nonnull Collection<@Nonnull String> postConstructAnnotations,
        @Nonnull Collection<@Nonnull String> preDestroyAnnotations,
        @Nonnull DIComponent.Resolver resolver,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> componentMap,
        @Nonnull @OutParam Set<@Nonnull ResField> fieldSet
    ) throws DIException {
        if (componentMap.containsKey(type)) {
            return;
        }
        DIComponent.Descriptor descriptor = Fs.uncheck(() ->
                resolver.resolve(type, componentAnnotations, postConstructAnnotations, preDestroyAnnotations),
            DIException::new
        );
        if (!canInstantiate(descriptor.rawClass())) {
            return;
        }
        Res res = new Res(descriptor);
        componentMap.put(type, res);
        // dependency fields
        for (Field dependencyField : descriptor.dependencyFields()) {
            Type dependencyType = dependencyField.getGenericType();
            if (dependencyType.equals(type)) {
                fieldSet.add(new ResField(dependencyField, res));
                continue;
            }
            doDependencyInjection(
                dependencyType,
                componentAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                resolver,
                componentMap,
                fieldSet
            );
            fieldSet.add(new ResField(dependencyField, res));
        }
        // dependency parameters of post-construct method
        Method postConstructMethod = descriptor.postConstructMethod();
        if (postConstructMethod != null) {
            for (Type parameterType : postConstructMethod.getGenericParameterTypes()) {
                doDependencyInjection(
                    parameterType,
                    componentAnnotations,
                    postConstructAnnotations,
                    preDestroyAnnotations,
                    resolver,
                    componentMap,
                    fieldSet
                );
            }
        }
        // dependency parameters of pre-destroy method
        Method preDestroyMethod = descriptor.preDestroyMethod();
        if (preDestroyMethod != null) {
            for (Type parameterType : preDestroyMethod.getGenericParameterTypes()) {
                doDependencyInjection(
                    parameterType,
                    componentAnnotations,
                    postConstructAnnotations,
                    preDestroyAnnotations,
                    resolver,
                    componentMap,
                    fieldSet
                );
            }
        }
    }

    private boolean canInstantiate(@Nonnull Class<?> type) {
        if (type.isInterface()) {
            return false;
        }
        return !Modifier.isAbstract(type.getModifiers());
    }

    private void doAop(
        @Nonnull DIComponent.FieldSetter fieldSetter,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> componentMap,
        @Nonnull @OutParam Set<@Nonnull ResField> fieldSet
    ) throws DIException {
        List<DIAspectHandler> aspects = new ArrayList<>();
        for (Res res : componentMap.values()) {
            if (!res.local) {
                continue;
            }
            Object instance = res.instance;
            if (instance instanceof DIAspectHandler) {
                aspects.add((DIAspectHandler) instance);
                res.isAspectHandler = true;
            }
        }
        if (aspects.isEmpty()) {
            return;
        }
        AspectMaker aspectMaker = AspectMaker.byAsm();
        for (Res res : componentMap.values()) {
            if (!res.local) {
                continue;
            }
            if (res.isAspectHandler) {
                continue;
            }
            for (DIAspectHandler aspect : aspects) {
                if (aspect.needsAspect(res.type)) {
                    AspectSpec spec = aspectMaker.make(Fs.asNonnull(res.descriptor).rawClass(), aspect);
                    res.advisedInstance = spec.newInstance();
                    break;
                }
            }
        }
    }

    private void setField(
        @Nonnull DIComponent.FieldSetter fieldSetter,
        @Nonnull Field field, @Nonnull Object owner, @Nonnull Object value
    ) throws DIException {
        Fs.uncheck(
            () -> {
                fieldSetter.set(field, owner, value);
            },
            DIException::new
        );
    }

    private @Nonnull Res getRes(
        @Nonnull Type type,
        @Nonnull Map<@Nonnull Type, @Nonnull Res> componentMap
    ) throws DIException {
        Res res = componentMap.get(type);
        if (res != null) {
            return res;
        }
        for (Res resource : componentMap.values()) {
            if (TypeKit.isAssignable(type, resource.type)) {
                return resource;
            }
        }
        throw new DIException("Can not find resource instance for type :" + type.getTypeName() + ".");
    }

    private @Nonnull Object getResInstance(
        @Nonnull Res res
    ) {
        return res.advisedInstance != null ? res.advisedInstance : res.instance;
    }

    @Override
    public synchronized DIContainer initialize() throws DIInitializeException, DIException {
        if (state > 0) {
            throw new DIException("DIContainer is already initialized.");
        }
        try {
            Set<DIComponent> unprocessed = new LinkedHashSet<>(localComponents.values());
            Set<DIComponent> processed = new LinkedHashSet<>(unprocessed.size());
            for (DIComponent component : localComponents.values()) {
                try {
                    postConstruct(component, unprocessed, processed);
                } catch (Exception e) {
                    unprocessed.remove(component);
                    throw new DIInitializeException(component, e, new ArrayList<>(processed), new ArrayList<>(unprocessed));
                }
            }
        } finally {
            this.state = 1;
        }
        return this;
    }

    private void postConstruct(
        @Nonnull DIComponent component,
        @Nonnull Set<@Nonnull DIComponent> unprocessed,
        @Nonnull Set<@Nonnull DIComponent> processed
    ) {
        if (component.isInitialized()) {
            return;
        }
        for (DIComponent postConstructDependency : component.postConstructDependencies()) {
            postConstruct(postConstructDependency, unprocessed, processed);
        }
        component.postConstruct();
        unprocessed.remove(component);
        processed.add(component);
    }

    @Override
    public boolean isInitialized() {
        return state > 0;
    }

    @Override
    public synchronized DIContainer shutdown() throws DIShutdownException, DIException {
        if (state < 1) {
            throw new DIException("DIContainer is not initialized.");
        }
        if (state >= 2) {
            throw new DIException("DIContainer is already shutdown.");
        }
        try {
            Set<DIComponent> unprocessed = new LinkedHashSet<>(localComponents.values());
            Set<DIComponent> processed = new LinkedHashSet<>(unprocessed.size());
            for (DIComponent component : localComponents.values()) {
                try {
                    preDestroy(component, unprocessed, processed);
                } catch (Exception e) {
                    unprocessed.remove(component);
                    throw new DIShutdownException(component, e, new ArrayList<>(processed), new ArrayList<>(unprocessed));
                }
            }
        } finally {
            this.state = 2;
        }
        return this;
    }

    private void preDestroy(
        @Nonnull DIComponent component,
        @Nonnull Set<@Nonnull DIComponent> unprocessed,
        @Nonnull Set<@Nonnull DIComponent> processed
    ) {
        if (component.isDestroyed()) {
            return;
        }
        for (DIComponent preDestroyDependency : component.preDestroyDependencies()) {
            preDestroy(preDestroyDependency, unprocessed, processed);
        }
        component.preDestroy();
        unprocessed.remove(component);
        processed.add(component);
    }

    @Override
    public boolean isShutdown() {
        return state >= 2;
    }

    @Override
    public @Nonnull List<@Nonnull DIContainer> parentContainers() {
        return parentContainers;
    }

    @Override
    public @Nonnull Map<@Nonnull Type, @Nonnull DIComponent> localComponents() {
        return localComponents;
    }

    @Override
    public @Nonnull Map<@Nonnull Type, @Nonnull DIComponent> components() {
        return components;
    }

    @Override
    public @Nullable DIComponent getComponent(@Nonnull Type type) {
        DIComponent component = components.get(type);
        if (component != null) {
            return component;
        }
        for (DIComponent sr : components.values()) {
            if (TypeKit.isAssignable(type, sr.type())) {
                return sr;
            }
        }
        return null;
    }

    private static @Nonnull Class<?> rawClass(@Nonnull Type type) {
        Class<?> raw = TypeKit.getRawClass(type);
        if (raw == null) {
            throw new DIException("Unsupported DI type: " + type.getTypeName() + ".");
        }
        return raw;
    }

    private static final class Res {

        private final @Nonnull Type type;
        private final boolean local;
        private final @Nonnull Object instance;
        private final @Nullable DIComponent.Descriptor descriptor;

        private Object advisedInstance;
        private boolean isAspectHandler = false;

        private Res(
            @Nonnull DIComponent.Descriptor descriptor
        ) throws DIException {
            this.type = descriptor.type();
            this.local = true;
            this.descriptor = descriptor;
            try {
                this.instance = Invocable.of(descriptor.rawClass().getConstructor()).invoke(null);
            } catch (Exception e) {
                throw new DIException("Creates instance for " + type.getTypeName() + " failed.", e);
            }
        }

        private Res(@Nonnull Type type, @Nonnull Object instance) {
            this.type = type;
            this.local = false;
            this.descriptor = null;
            this.instance = instance;
        }

        public @Nullable Method postConstructMethod() {
            return descriptor != null ? descriptor.postConstructMethod() : null;
        }

        public @Nullable Method preDestroyMethod() {
            return descriptor != null ? descriptor.preDestroyMethod() : null;
        }
    }

    private static final class ResField {

        private final @Nonnull Field field;
        private final @Nonnull Res owner;

        private ResField(@Nonnull Field field, @Nonnull Res owner) {
            this.field = field;
            this.owner = owner;
        }
    }

    private final class DIComponentImpl implements DIComponent {

        private final @Nonnull Type type;
        private final @Nonnull Object instance;
        private final boolean local;
        private final @Nullable Method postConstructMethod;
        private final @Nonnull Runnable postConstruct;
        private final @Nullable Method preDestroyMethod;
        private final @Nonnull Runnable preDestroy;

        final @Nonnull ArrayList<@Nonnull DIComponent> dependenciesVar = new ArrayList<>();
        final @Nonnull ArrayList<@Nonnull DIComponent> postConstructDependenciesVar = new ArrayList<>();
        final @Nonnull ArrayList<@Nonnull DIComponent> preDestroyDependenciesVar = new ArrayList<>();
        private final @Nonnull List<@Nonnull DIComponent> dependencies =
            Collections.unmodifiableList(dependenciesVar);
        private final @Nonnull List<@Nonnull DIComponent> postConstructDependencies =
            Collections.unmodifiableList(postConstructDependenciesVar);
        private final @Nonnull List<@Nonnull DIComponent> preDestroyDependencies =
            Collections.unmodifiableList(preDestroyDependenciesVar);

        private volatile int state = 0;

        private DIComponentImpl(
            @Nonnull Type type,
            @Nonnull Object instance,
            boolean local,
            @Nullable Method postConstructMethod,
            @Nullable Method preDestroyMethod
        ) {
            this.type = type;
            this.instance = instance;
            this.local = local;
            this.postConstructMethod = postConstructMethod;
            this.preDestroyMethod = preDestroyMethod;
            this.postConstruct = postConstructMethod == null ?
                EMPTY_RUNNABLE
                :
                () -> {
                    Invocable invocable = Invocable.of(postConstructMethod);
                    Type[] paramTypes = postConstructMethod.getGenericParameterTypes();
                    Object[] args = new Object[paramTypes.length];
                    for (int i = 0; i < paramTypes.length; i++) {
                        args[i] = getObject(paramTypes[i]);
                    }
                    invocable.invoke(instance, args);
                };
            this.preDestroy = preDestroyMethod == null ?
                EMPTY_RUNNABLE
                :
                () -> {
                    Invocable invocable = Invocable.of(preDestroyMethod);
                    Type[] paramTypes = preDestroyMethod.getGenericParameterTypes();
                    Object[] args = new Object[paramTypes.length];
                    for (int i = 0; i < paramTypes.length; i++) {
                        args[i] = getObject(paramTypes[i]);
                    }
                    invocable.invoke(instance, args);
                };
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull Object instance() {
            return instance;
        }

        @Override
        public boolean isLocal() {
            return local;
        }

        @Override
        public @Nonnull List<DIComponent> dependencies() {
            return dependencies;
        }

        @Override
        public @Nullable Method postConstructMethod() {
            return postConstructMethod;
        }

        @Override
        public @Nonnull List<@Nonnull DIComponent> postConstructDependencies() {
            return postConstructDependencies;
        }

        @Override
        public synchronized void postConstruct() throws InvocationException {
            postConstruct.run();
            state = 1;
        }

        @Override
        public boolean isInitialized() {
            return state >= 1;
        }

        @Override
        public @Nullable Method preDestroyMethod() {
            return preDestroyMethod;
        }

        @Override
        public @Nonnull List<@Nonnull DIComponent> preDestroyDependencies() {
            return preDestroyDependencies;
        }

        @Override
        public synchronized void preDestroy() throws InvocationException {
            preDestroy.run();
            state = 2;
        }

        @Override
        public boolean isDestroyed() {
            return state >= 2;
        }

        @Override
        public @Nonnull String toString() {
            return "DIComponent-" + instance();
        }
    }

    enum Resolver implements DIComponent.Resolver {

        INST;

        @Override
        public DIComponent.@Nonnull Descriptor resolve(
            @Nonnull Type type,
            @Nonnull Collection<@Nonnull String> componentAnnotations,
            @Nonnull Collection<@Nonnull String> postConstructAnnotations,
            @Nonnull Collection<@Nonnull String> preDestroyAnnotations
        ) throws Exception {
            Class<?> rawClass = rawClass(type);
            // fields
            Field[] fields = rawClass.getDeclaredFields();
            ArrayList<Field> dependencyFields = new ArrayList<>();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isFinal(mod)) {
                    continue;
                }
                for (Annotation annotation : field.getAnnotations()) {
                    if (componentAnnotations.contains(annotation.annotationType().getName())) {
                        dependencyFields.add(field);
                    }
                }
            }
            dependencyFields.trimToSize();
            List<Field> depFields = Collections.unmodifiableList(dependencyFields);
            // methods
            Method postConstruct = null;
            Method preDestroy = null;
            for (Method method : rawClass.getMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (postConstructAnnotations.contains(annotation.annotationType().getName())) {
                        postConstruct = method;
                    }
                    if (preDestroyAnnotations.contains(annotation.annotationType().getName())) {
                        preDestroy = method;
                    }
                }
            }
            Method postConstructMethod = postConstruct;
            Method preDestroyMethod = preDestroy;
            return new DIComponent.Descriptor() {
                @Override
                public @Nonnull Type type() {
                    return type;
                }

                @Override
                public @Nonnull Class<?> rawClass() {
                    return rawClass;
                }

                @Override
                public @Nullable Method postConstructMethod() {
                    return postConstructMethod;
                }

                @Override
                public @Nullable Method preDestroyMethod() {
                    return preDestroyMethod;
                }

                @Override
                public @Nonnull @Immutable List<@Nonnull Field> dependencyFields() {
                    return depFields;
                }
            };
        }
    }

    enum FieldSetter implements DIComponent.FieldSetter {

        INST;

        @Override
        public void set(@Nonnull Field field, @Nonnull Object owner, @Nonnull Object value) throws Exception {
            field.setAccessible(true);
            field.set(owner, value);
        }
    }
}
