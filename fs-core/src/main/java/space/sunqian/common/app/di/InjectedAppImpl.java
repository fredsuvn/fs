package space.sunqian.common.app.di;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.OutParam;
import space.sunqian.common.Fs;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.dynamic.aspect.AspectMaker;
import space.sunqian.common.dynamic.aspect.AspectSpec;
import space.sunqian.common.invoke.Invocable;
import space.sunqian.common.invoke.InvocationException;
import space.sunqian.common.reflect.TypeKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class InjectedAppImpl implements InjectedApp {

    private final @Nonnull List<@Nonnull InjectedResource> localResources;
    private final @Nonnull List<@Nonnull InjectedResource> allResources;
    private final @Nonnull Map<@Nonnull Type, @Nonnull InjectedResource> resources;
    private final @Nonnull List<@Nonnull InjectedResource> preDestroyList;
    private final @Nonnull List<@Nonnull InjectedApp> parentApps;

    public InjectedAppImpl(
        @Nonnull Collection<@Nonnull Type> resourceTypes,
        @Nonnull Collection<@Nonnull InjectedApp> parentApps,
        @Nonnull Collection<@Nonnull String> resourceAnnotations,
        @Nonnull Collection<@Nonnull String> postConstructAnnotations,
        @Nonnull Collection<@Nonnull String> preDestroyAnnotations,
        @Nonnull InjectedResource.Resolver resolver,
        @Nonnull InjectedResource.FieldSetter fieldSetter
    ) throws InjectedResourceInitializationException, InjectedAppException {
        Map<Type, Res> resourceMap = new LinkedHashMap<>();
        // add parent resource into this app
        for (InjectedApp parentApp : parentApps) {
            for (InjectedResource resource : parentApp.resources()) {
                resourceMap.put(resource.type(), new Res(resource.type(), resource.instance()));
            }
        }
        Set<FieldRes> fieldSet = new LinkedHashSet<>();
        // generate instances
        for (Type resourceType : resourceTypes) {
            doDependencyInjection(
                resourceType,
                resourceAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                resolver,
                resourceMap,
                fieldSet
            );
        }
        // base injects:
        for (FieldRes fieldRes : fieldSet) {
            setField(
                fieldSetter,
                fieldRes.field,
                fieldRes.owner.instance,
                getRes(fieldRes.field.getGenericType(), resourceMap).instance
            );
        }
        // aop
        doAop(fieldSetter, resourceMap, fieldSet);
        // resources
        Map<Type, InjectedResource> resources = new LinkedHashMap<>();
        InjectedResource[] allResources = new InjectedResource[resourceMap.size()];
        int localCount = 0;
        int i = 0;
        for (Res res : resourceMap.values()) {
            Object inst = getResInstance(res);
            InjectedResource simpleResource = new InjectedRes(
                res.type,
                inst,
                res.local,
                res.postConstructMethod(),
                res.preDestroyMethod()
            );
            resources.put(res.type, simpleResource);
            allResources[i++] = simpleResource;
            if (res.local) {
                localCount++;
            }
        }
        InjectedResource[] localResources = new InjectedResource[localCount];
        i = 0;
        for (InjectedResource res : allResources) {
            if (res.isLocal()) {
                localResources[i++] = res;
            }
        }
        this.parentApps = ListKit.toList(parentApps);
        this.resources = Collections.unmodifiableMap(resources);
        this.localResources = ListKit.list(localResources);
        this.allResources = ListKit.list(allResources);
        // post-construct and pre-destroy
        Set<InjectedResource> postConstructSet = new LinkedHashSet<>();
        Set<InjectedResource> preDestroySet = new LinkedHashSet<>();
        Set<Type> stack = new HashSet<>();
        for (InjectedResource resource : localResources) {
            checkDependencyForPostConstruct(resource, stack, postConstructSet);
            stack.clear();
            checkDependencyForPreDestroy(resource, stack, preDestroySet);
            stack.clear();
        }
        List<InjectedResource> postConstructList = new ArrayList<>(postConstructSet);
        postConstructList.sort(PostConstructComparator.INST);
        List<InjectedResource> preDestroyList = new ArrayList<>(preDestroySet);
        preDestroyList.sort(PreDestroyComparator.INST);
        this.preDestroyList = preDestroyList;
        // execute post-construct
        List<InjectedResource> uninitializedResources = new ArrayList<>(postConstructList);
        List<InjectedResource> initializedResources = new ArrayList<>(postConstructList.size());
        Iterator<InjectedResource> uninitializedIt = uninitializedResources.iterator();
        while (uninitializedIt.hasNext()) {
            InjectedResource resource = uninitializedIt.next();
            try {
                resource.postConstruct();
            } catch (Exception e) {
                throw new InjectedResourceInitializationException(resource, e, initializedResources, uninitializedResources);
            } finally {
                uninitializedIt.remove();
            }
            initializedResources.add(resource);
        }
    }

    private void doDependencyInjection(
        @Nonnull Type type,
        @Nonnull Collection<@Nonnull String> resourceAnnotations,
        @Nonnull Collection<@Nonnull String> postConstructAnnotations,
        @Nonnull Collection<@Nonnull String> preDestroyAnnotations,
        @Nonnull InjectedResource.Resolver resolver,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet
    ) throws InjectedAppException {
        if (resourceMap.containsKey(type)) {
            return;
        }
        InjectedResource.Descriptor descriptor = Fs.uncheck(() ->
                resolver.resolve(type, resourceAnnotations, postConstructAnnotations, preDestroyAnnotations),
            InjectedAppException::new
        );
        if (!canInstantiate(descriptor.rawClass())) {
            return;
        }
        Res res = new Res(descriptor);
        resourceMap.put(type, res);
        for (Field dependencyField : descriptor.dependencyFields()) {
            Type dependencyType = dependencyField.getGenericType();
            if (dependencyType.equals(type)) {
                fieldSet.add(new FieldRes(dependencyField, res));
                continue;
            }
            doDependencyInjection(
                dependencyType,
                resourceAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                resolver,
                resourceMap,
                fieldSet
            );
            fieldSet.add(new FieldRes(dependencyField, res));
        }
    }

    private boolean canInstantiate(@Nonnull Class<?> type) {
        if (type.isInterface()) {
            return false;
        }
        return !Modifier.isAbstract(type.getModifiers());
    }

    private void doAop(
        @Nonnull InjectedResource.FieldSetter fieldSetter,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet
    ) throws InjectedAppException {
        List<InjectedAspect> aspects = new ArrayList<>();
        for (Res res : resourceMap.values()) {
            if (!res.local) {
                continue;
            }
            Object instance = res.instance;
            if (instance instanceof InjectedAspect) {
                aspects.add((InjectedAspect) instance);
                res.isAspectHandler = true;
            }
        }
        if (aspects.isEmpty()) {
            return;
        }
        AspectMaker aspectMaker = AspectMaker.byAsm();
        for (Res res : resourceMap.values()) {
            if (!res.local) {
                continue;
            }
            if (res.isAspectHandler) {
                continue;
            }
            for (InjectedAspect aspect : aspects) {
                if (aspect.needsAspect(res.type)) {
                    AspectSpec spec = aspectMaker.make(Fs.asNonnull(res.descriptor).rawClass(), aspect);
                    res.advisedInstance = spec.newInstance();
                    break;
                }
            }
        }
        // rewrite fields
        for (FieldRes fieldRes : fieldSet) {
            boolean needsRewrite = false;
            Object owner;
            if (fieldRes.owner.advisedInstance != null) {
                needsRewrite = true;
                owner = fieldRes.owner.advisedInstance;
            } else {
                owner = fieldRes.owner.instance;
            }
            Res valueRes = getRes(fieldRes.field.getGenericType(), resourceMap);
            Object value;
            if (valueRes.advisedInstance != null) {
                needsRewrite = true;
                value = valueRes.advisedInstance;
            } else {
                value = valueRes.instance;
            }
            if (needsRewrite) {
                setField(fieldSetter, fieldRes.field, owner, value);
            }
        }
    }

    private void setField(
        @Nonnull InjectedResource.FieldSetter fieldSetter,
        @Nonnull Field field, @Nonnull Object owner, @Nonnull Object value
    ) throws InjectedAppException {
        Fs.uncheck(
            () -> {
                fieldSetter.set(field, owner, value);
            },
            InjectedAppException::new
        );
    }

    private @Nonnull Res getRes(
        @Nonnull Type type,
        @Nonnull Map<@Nonnull Type, @Nonnull Res> resourceMap
    ) throws InjectedAppException {
        Res res = resourceMap.get(type);
        if (res != null) {
            return res;
        }
        for (Res resource : resourceMap.values()) {
            if (TypeKit.isAssignable(type, resource.type)) {
                return resource;
            }
        }
        throw new InjectedAppException("Can not find resource instance for type :" + type.getTypeName() + ".");
    }

    private void checkDependencyForPostConstruct(
        @Nonnull InjectedResource curRes,
        @Nonnull Set<@Nonnull Type> stack,
        @Nonnull @OutParam Set<@Nonnull InjectedResource> postConstructSet
    ) throws InjectedAppException {
        Type curType = curRes.type();
        Method postConstruct = curRes.postConstructMethod();
        if (postConstruct == null) {
            return;
        }
        postConstructSet.add(curRes);
        InjectedDependsOn sdo = postConstruct.getAnnotation(InjectedDependsOn.class);
        if (sdo == null) {
            return;
        }
        if (!stack.add(curType)) {
            throw new InjectedAppException(
                "Circular post-construct dependency detected: " +
                    stack.stream().map(Type::getTypeName).collect(Collectors.joining(" -> ")) + "."
            );
        }
        for (Class<?> depType : sdo.value()) {
            InjectedResource depRes = resources.get(depType);
            if (depRes == null) {
                throw new InjectedAppException("Unknown post-construct dependency type: " + depType.getTypeName() + ".");
            }
            checkDependencyForPostConstruct(depRes, stack, postConstructSet);
            stack.remove(depType);
        }
    }

    private void checkDependencyForPreDestroy(
        @Nonnull InjectedResource curRes,
        @Nonnull Set<@Nonnull Type> stack,
        @Nonnull @OutParam Set<@Nonnull InjectedResource> preDestroySet
    ) throws InjectedAppException {
        Type curType = curRes.type();
        Method preDestroy = curRes.preDestroyMethod();
        if (preDestroy == null) {
            return;
        }
        preDestroySet.add(curRes);
        InjectedDependsOn sdo = preDestroy.getAnnotation(InjectedDependsOn.class);
        if (sdo == null) {
            return;
        }
        if (!stack.add(curType)) {
            throw new InjectedAppException(
                "Circular pre-destroy dependency: " +
                    stack.stream().map(Type::getTypeName).collect(Collectors.joining(" -> ")) + "."
            );
        }
        for (Class<?> depType : sdo.value()) {
            InjectedResource depRes = resources.get(depType);
            if (depRes == null) {
                throw new InjectedAppException("Unknown pre-destroy dependency type: " + depType.getTypeName() + ".");
            }
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
    public void shutdown() throws InjectedResourceDestructionException, InjectedAppException {
        List<InjectedResource> undestroyedResources = new ArrayList<>(preDestroyList);
        List<InjectedResource> destroyedResources = new ArrayList<>(preDestroyList.size());
        Iterator<InjectedResource> undestroyedIt = undestroyedResources.iterator();
        while (undestroyedIt.hasNext()) {
            InjectedResource resource = undestroyedIt.next();
            try {
                resource.preDestroy();
                destroyedResources.add(resource);
            } catch (Exception e) {
                throw new InjectedResourceDestructionException(resource, e, destroyedResources, undestroyedResources);
            } finally {
                undestroyedIt.remove();
            }
        }
    }

    @Override
    public @Nonnull List<@Nonnull InjectedApp> parentApps() {
        return parentApps;
    }

    @Override
    public @Nonnull List<@Nonnull InjectedResource> localResources() {
        return localResources;
    }

    @Override
    public @Nonnull List<@Nonnull InjectedResource> resources() {
        return allResources;
    }

    @Override
    public @Nullable Object getResource(@Nonnull Type type) {
        InjectedResource resource = resources.get(type);
        if (resource != null) {
            return resource.instance();
        }
        for (InjectedResource sr : allResources) {
            if (TypeKit.isAssignable(type, sr.type())) {
                return sr.instance();
            }
        }
        return null;
    }

    private static @Nonnull Class<?> rawClass(@Nonnull Type type) {
        Class<?> raw = TypeKit.getRawClass(type);
        if (raw == null) {
            throw new InjectedAppException("Unsupported DI type: " + type.getTypeName() + ".");
        }
        return raw;
    }

    private static final class Res {

        private final @Nonnull Type type;
        private final boolean local;
        private final @Nonnull Object instance;
        private final @Nullable InjectedResource.Descriptor descriptor;

        private Object advisedInstance;
        private boolean isAspectHandler = false;

        private Res(
            @Nonnull InjectedResource.Descriptor descriptor
        ) throws InjectedAppException {
            this.type = descriptor.type();
            this.local = true;
            this.descriptor = descriptor;
            try {
                this.instance = Invocable.of(descriptor.rawClass().getConstructor()).invoke(null);
            } catch (Exception e) {
                throw new InjectedAppException("Creates instance for " + type.getTypeName() + " failed.", e);
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

    private static final class FieldRes {

        private final @Nonnull Field field;
        private final @Nonnull Res owner;

        private FieldRes(@Nonnull Field field, @Nonnull Res owner) {
            this.field = field;
            this.owner = owner;
        }
    }

    private static final class InjectedRes implements InjectedResource {

        private final @Nonnull Type type;
        private final @Nonnull Object instance;
        private final boolean local;
        private final @Nullable Method postConstructMethod;
        private final @Nonnull Invocable postConstruct;
        private final @Nullable Method preDestroyMethod;
        private final @Nonnull Invocable preDestroy;
        private volatile int state = 0;

        private InjectedRes(
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
            this.postConstruct = postConstructMethod == null ? Invocable.empty() : Invocable.of(postConstructMethod);
            this.preDestroyMethod = preDestroyMethod;
            this.preDestroy = preDestroyMethod == null ? Invocable.empty() : (
                postConstructMethod == preDestroyMethod ? postConstruct : Invocable.of(preDestroyMethod)
            );
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
            postConstruct.invoke(instance);
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
            preDestroy.invoke(instance);
            state = 2;
        }

        @Override
        public boolean isDestroyed() {
            return state == 2;
        }
    }

    enum Resolver implements InjectedResource.Resolver {

        INST;

        @Override
        public InjectedResource.@Nonnull Descriptor resolve(
            @Nonnull Type type,
            @Nonnull Collection<@Nonnull String> resourceAnnotations,
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
                    if (resourceAnnotations.contains(annotation.annotationType().getName())) {
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
            return new InjectedResource.Descriptor() {
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

    enum FieldSetter implements InjectedResource.FieldSetter {

        INST;

        @Override
        public void set(@Nonnull Field field, @Nonnull Object owner, @Nonnull Object value) throws Exception {
            field.setAccessible(true);
            field.set(owner, value);
        }
    }

    private enum PostConstructComparator implements Comparator<InjectedResource> {

        INST;

        @Override
        public int compare(@Nonnull InjectedResource sr1, @Nonnull InjectedResource sr2) {
            Method pc1 = Fs.asNonnull(sr1.postConstructMethod());
            Method pc2 = Fs.asNonnull(sr2.postConstructMethod());
            InjectedDependsOn sd1 = pc1.getAnnotation(InjectedDependsOn.class);
            InjectedDependsOn sd2 = pc2.getAnnotation(InjectedDependsOn.class);
            return compareDependsOn(sr1, sd1, sr2, sd2);
        }
    }

    private enum PreDestroyComparator implements Comparator<InjectedResource> {

        INST;

        @Override
        public int compare(@Nonnull InjectedResource sr1, @Nonnull InjectedResource sr2) {
            Method pd1 = Fs.asNonnull(sr1.preDestroyMethod());
            Method pd2 = Fs.asNonnull(sr2.preDestroyMethod());
            InjectedDependsOn sd1 = pd1.getAnnotation(InjectedDependsOn.class);
            InjectedDependsOn sd2 = pd2.getAnnotation(InjectedDependsOn.class);
            return compareDependsOn(sr1, sd1, sr2, sd2);
        }
    }

    private static int compareDependsOn(
        @Nonnull InjectedResource sr1, @Nullable InjectedDependsOn sd1,
        @Nonnull InjectedResource sr2, @Nullable InjectedDependsOn sd2
    ) {
        if (sd1 != null) {
            for (Class<?> c1 : sd1.value()) {
                if (c1.equals(sr2.type())) {
                    return 1;
                }
            }
        }
        if (sd2 != null) {
            for (Class<?> c2 : sd2.value()) {
                if (c2.equals(sr1.type())) {
                    return -1;
                }
            }
        }
        return 0;
    }
}
