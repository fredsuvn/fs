package space.sunqian.fs.di;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.OutParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ArrayKit;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        LinkedHashMap<Type, DIComponent> components = new LinkedHashMap<>(resMap.size());
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
        // check cycle dependencies
        for (DIComponentImpl component : localComponents.values()) {
            DIKit.checkCycleDependencies(component);
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
            component.depList.addAll(
                descriptor.dependencyFields().stream()
                    .map(field -> getComponent(field.getGenericType()))
                    //.filter(Objects::nonNull)
                    .filter(c -> c != component)
                    .collect(Collectors.toList())
            );
            // post-construct method parameters
            Method postConstructMethod = descriptor.postConstructMethod();
            if (postConstructMethod != null) {
                component.depList.addAll(
                    Arrays.stream(postConstructMethod.getParameterTypes())
                        .map(this::getComponent)
                        .filter(c -> c != component)
                        .collect(Collectors.toList())
                );
            }
            // pre-destroy method parameters
            Method preDestroyMethod = descriptor.preDestroyMethod();
            if (preDestroyMethod != null) {
                component.depList.addAll(
                    Arrays.stream(preDestroyMethod.getParameterTypes())
                        .map(this::getComponent)
                        .filter(c -> c != component)
                        .collect(Collectors.toList())
                );
            }
            component.depList.trimToSize();
        }
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

    private void checkDependencyForPostConstruct(
        @Nonnull DIComponent curRes,
        @Nonnull Set<@Nonnull Type> stack,
        @Nonnull @OutParam Set<@Nonnull DIComponent> postConstructSet
    ) throws DIException {
        Type curType = curRes.type();
        Method postConstructMethod = curRes.postConstructMethod();
        if (postConstructMethod == null) {
            return;
        }
        postConstructSet.add(curRes);
        Type[] sdo = postConstructMethod.getGenericParameterTypes();
        if (ArrayKit.isEmpty(sdo)) {
            return;
        }
        if (!stack.add(curType)) {
            throw new DIException(
                "Circular post-construct dependency detected: " +
                    stack.stream().map(Type::getTypeName).collect(Collectors.joining(" -> ")) + "."
            );
        }
        for (Type depType : sdo) {
            DIComponent depRes = components.get(depType);
            checkDependencyForPostConstruct(depRes, stack, postConstructSet);
            stack.remove(depType);
        }
    }

    private void checkDependencyForPreDestroy(
        @Nonnull DIComponent curRes,
        @Nonnull Set<@Nonnull Type> stack,
        @Nonnull @OutParam Set<@Nonnull DIComponent> preDestroySet
    ) throws DIException {
        Type curType = curRes.type();
        Method preDestroyMethod = curRes.preDestroyMethod();
        if (preDestroyMethod == null) {
            return;
        }
        preDestroySet.add(curRes);
        Type[] sdo = preDestroyMethod.getGenericParameterTypes();
        if (ArrayKit.isEmpty(sdo)) {
            return;
        }
        if (!stack.add(curType)) {
            throw new DIException(
                "Circular pre-destroy dependency: " +
                    stack.stream().map(Type::getTypeName).collect(Collectors.joining(" -> ")) + "."
            );
        }
        for (Type depType : sdo) {
            DIComponent depRes = components.get(depType);
            checkDependencyForPreDestroy(depRes, stack, preDestroySet);
            stack.remove(depType);
        }
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
            Set<DIComponent> postConstructSet = new LinkedHashSet<>();
            Set<Type> stack = new HashSet<>();
            for (DIComponent component : localComponents.values()) {
                checkDependencyForPostConstruct(component, stack, postConstructSet);
                stack.clear();
            }
            List<DIComponent> postConstructList = new ArrayList<>(postConstructSet);
            postConstructList.sort(PostConstructComparator.INST);
            doPostConstruct(postConstructList);
        } finally {
            this.state = 1;
        }
        return this;
    }

    private void doPostConstruct(@Nonnull List<@Nonnull DIComponent> postConstructList) {
        List<DIComponent> uninitializedComponents = new ArrayList<>(postConstructList);
        List<DIComponent> initializedComponents = new ArrayList<>(postConstructList.size());
        Iterator<DIComponent> uninitializedIt = uninitializedComponents.iterator();
        while (uninitializedIt.hasNext()) {
            DIComponent component = uninitializedIt.next();
            try {
                component.postConstruct();
            } catch (Exception e) {
                throw new DIInitializeException(component, e, initializedComponents, uninitializedComponents);
            } finally {
                uninitializedIt.remove();
            }
            initializedComponents.add(component);
        }
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
            Set<DIComponent> preDestroySet = new LinkedHashSet<>();
            Set<Type> stack = new HashSet<>();
            for (DIComponent component : localComponents.values()) {
                checkDependencyForPreDestroy(component, stack, preDestroySet);
                stack.clear();
            }
            List<DIComponent> preDestroyList = new ArrayList<>(preDestroySet);
            preDestroyList.sort(PreDestroyComparator.INST);
            doPreDestroy(preDestroyList);
        } finally {
            this.state = 2;
        }
        return this;
    }

    private void doPreDestroy(@Nonnull List<DIComponent> preDestroyList) {
        List<DIComponent> undestroyedComponents = new ArrayList<>(preDestroyList);
        List<DIComponent> destroyedComponents = new ArrayList<>(preDestroyList.size());
        Iterator<DIComponent> undestroyedIt = undestroyedComponents.iterator();
        while (undestroyedIt.hasNext()) {
            DIComponent component = undestroyedIt.next();
            try {
                component.preDestroy();
                destroyedComponents.add(component);
            } catch (Exception e) {
                throw new DIShutdownException(component, e, destroyedComponents, undestroyedComponents);
            } finally {
                undestroyedIt.remove();
            }
        }
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
        final @Nonnull ArrayList<@Nonnull DIComponent> depList = new ArrayList<>();
        private final @Nonnull List<@Nonnull DIComponent> dependencies = Collections.unmodifiableList(depList);
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
            this.preDestroyMethod = preDestroyMethod;
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
        public @Nullable Method postConstructMethod() {
            return postConstructMethod;
        }

        @Override
        public void postConstruct() throws InvocationException {
            postConstruct.run();
            state = 1;
        }

        @Override
        public boolean isInitialized() {
            return state == 1;
        }

        @Override
        public @Nullable Method preDestroyMethod() {
            return preDestroyMethod;
        }

        @Override
        public void preDestroy() throws InvocationException {
            preDestroy.run();
            state = 2;
        }

        @Override
        public boolean isDestroyed() {
            return state == 2;
        }

        @Override
        public @Nonnull List<DIComponent> dependencies() {
            return dependencies;
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

    private enum PostConstructComparator implements Comparator<DIComponent> {

        INST;

        @Override
        public int compare(@Nonnull DIComponent sr1, @Nonnull DIComponent sr2) {
            Method pc1 = Fs.asNonnull(sr1.postConstructMethod());
            Method pc2 = Fs.asNonnull(sr2.postConstructMethod());
            Type[] sd1 = pc1.getGenericParameterTypes();
            Type[] sd2 = pc2.getGenericParameterTypes();
            return compareDependsOn(sr1, sd1, sr2, sd2);
        }
    }

    private enum PreDestroyComparator implements Comparator<DIComponent> {

        INST;

        @Override
        public int compare(@Nonnull DIComponent sr1, @Nonnull DIComponent sr2) {
            Method pd1 = Fs.asNonnull(sr1.preDestroyMethod());
            Method pd2 = Fs.asNonnull(sr2.preDestroyMethod());
            Type[] sd1 = pd1.getGenericParameterTypes();
            Type[] sd2 = pd2.getGenericParameterTypes();
            return compareDependsOn(sr1, sd1, sr2, sd2);
        }
    }

    private static int compareDependsOn(
        @Nonnull DIComponent sr1, Type @Nonnull [] sd1,
        @Nonnull DIComponent sr2, Type @Nonnull [] sd2
    ) {
        for (Type c1 : sd1) {
            if (c1.equals(sr2.type())) {
                return 1;
            }
        }
        for (Type c2 : sd2) {
            if (c2.equals(sr1.type())) {
                return -1;
            }
        }
        return 0;
    }
}
