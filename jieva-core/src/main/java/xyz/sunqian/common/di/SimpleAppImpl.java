package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ArrayKit;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.runtime.aspect.AspectMaker;
import xyz.sunqian.common.runtime.aspect.AspectSpec;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class SimpleAppImpl implements SimpleApp {

    private final @Nonnull List<@Nonnull SimpleResource> localResources;
    private final @Nonnull List<@Nonnull SimpleResource> allResources;
    private final @Nonnull Map<@Nonnull Type, @Nonnull SimpleResource> resources;
    private final @Nonnull List<@Nonnull SimpleResource> preDestroyList;
    private final @Nonnull List<@Nonnull SimpleApp> dependencyApps;

    public SimpleAppImpl(
        @Nonnull Set<@Nonnull Type> resourceTypes,
        @Nonnull SimpleApp @Nonnull [] dependencyApps,
        boolean enableAspect,
        @Nonnull String @Nonnull [] resourceAnnotations,
        @Nonnull String @Nonnull [] postConstructAnnotations,
        @Nonnull String @Nonnull [] preDestroyAnnotations
    ) throws SimpleResourceInitialException, SimpleAppException {
        Map<Type, Res> resourceMap = new HashMap<>();
        Set<FieldRes> fieldSet = new HashSet<>();
        // generate instances
        for (Type resourceType : resourceTypes) {
            dependencyInjection(
                resourceType,
                resourceAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                dependencyApps,
                resourceMap,
                fieldSet
            );
        }
        // base injects:
        for (FieldRes fieldRes : fieldSet) {
            setField(
                fieldRes.field,
                fieldRes.owner.instance,
                getRes(fieldRes.field.getGenericType(), resourceMap).instance
            );
        }
        // aop
        if (enableAspect) {
            aop(resourceMap, fieldSet);
        }
        // resources
        Map<Type, SimpleResource> resources = new HashMap<>();
        SimpleResource[] allResources = new SimpleResource[resourceMap.size()];
        int localCount = 0;
        int i = 0;
        for (Res res : resourceMap.values()) {
            Object inst = getResInstance(res);
            SimpleResource simpleResource = new SimpleRes(res.type, inst, res.local, res.postConstruct, res.preDestroy);
            resources.put(res.type, simpleResource);
            allResources[i++] = simpleResource;
            if (res.local) {
                localCount++;
            }
        }
        SimpleResource[] localResources = new SimpleResource[localCount];
        i = 0;
        for (SimpleResource res : allResources) {
            if (res.isLocal()) {
                localResources[i++] = res;
            }
        }
        this.dependencyApps = ListKit.list(dependencyApps);
        this.resources = Collections.unmodifiableMap(resources);
        this.localResources = ListKit.list(localResources);
        this.allResources = ListKit.list(allResources);
        // post-construct and pre-destroy
        Set<SimpleResource> postConstructSet = new LinkedHashSet<>();
        Set<SimpleResource> preDestroySet = new LinkedHashSet<>();
        Set<Type> stack = new HashSet<>();
        for (SimpleResource resource : localResources) {
            checkDependencyForPostConstruct(resource, resource, stack, postConstructSet);
            stack.clear();
            checkDependencyForPreDestroy(resource, resource, stack, preDestroySet);
            stack.clear();
        }
        List<SimpleResource> postConstructList = new ArrayList<>(postConstructSet);
        postConstructList.sort(PostConstructComparator.INST);
        List<SimpleResource> preDestroyList = new ArrayList<>(postConstructSet);
        preDestroyList.sort(PreDestroyComparator.INST);
        this.preDestroyList = preDestroyList;
        // execute post-construct
        List<SimpleResource> uninitializedResources = new ArrayList<>(postConstructList);
        List<SimpleResource> initializedResources = new ArrayList<>(postConstructList);
        Iterator<SimpleResource> uninitializedIt = uninitializedResources.iterator();
        while (uninitializedIt.hasNext()) {
            SimpleResource resource = uninitializedIt.next();
            Method postConstruct = Jie.asNonnull(resource.postConstructMethod());
            try {
                postConstruct.invoke(resource.instance());
                initializedResources.add(resource);
                uninitializedIt.remove();
            } catch (Exception e) {
                throw new SimpleResourceInitialException(resource, e, initializedResources, uninitializedResources);
            }
        }
    }

    private void dependencyInjection(
        @Nonnull Type type,
        @Nonnull String @Nonnull [] resourceAnnotations,
        @Nonnull String @Nonnull [] postConstructAnnotations,
        @Nonnull String @Nonnull [] preDestroyAnnotations,
        @Nonnull SimpleApp @Nonnull [] dependencyApps,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet
    ) throws SimpleAppException {
        if (resourceMap.containsKey(type)) {
            return;
        }
        for (SimpleApp dependency : dependencyApps) {
            Object instance = dependency.getResource(type);
            if (instance != null) {
                Res res = new Res(type, instance);
                resourceMap.put(type, res);
                return;
            }
        }
        Class<?> rawClass = rawClass(type);
        if (!canInstantiate(rawClass)) {
            return;
        }
        Res res = new Res(type, rawClass, postConstructAnnotations, preDestroyAnnotations);
        resourceMap.put(type, res);
        Class<?> cur = rawClass;
        while (cur != null) {
            FIELD:
            for (Field declaredField : cur.getDeclaredFields()) {
                int mod = declaredField.getModifiers();
                if (Modifier.isFinal(mod)) {
                    continue;
                }
                for (Annotation annotation : declaredField.getAnnotations()) {
                    if (ArrayKit.indexOf(resourceAnnotations, annotation.annotationType().getName()) >= 0) {
                        dependencyInjection(
                            declaredField.getGenericType(),
                            resourceAnnotations,
                            postConstructAnnotations,
                            preDestroyAnnotations,
                            dependencyApps,
                            resourceMap,
                            fieldSet
                        );
                        fieldSet.add(new FieldRes(declaredField, res));
                        continue FIELD;
                    }
                }
            }
            cur = cur.getSuperclass();
        }
    }

    private boolean canInstantiate(@Nonnull Class<?> type) {
        if (type.isInterface()) {
            return false;
        }
        return !Modifier.isAbstract(type.getModifiers());
    }

    private void aop(
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet
    ) throws SimpleAppException {
        List<SimpleAppAspect> aspects = new ArrayList<>();
        for (Res res : resourceMap.values()) {
            if (!res.local) {
                continue;
            }
            Object instance = res.instance;
            if (instance instanceof SimpleAppAspect) {
                aspects.add((SimpleAppAspect) instance);
                res.isAspectHandler = true;
            }
        }
        AspectMaker aspectMaker = AspectMaker.byAsm();
        for (Res res : resourceMap.values()) {
            if (!res.local) {
                continue;
            }
            if (res.isAspectHandler) {
                continue;
            }
            for (SimpleAppAspect aspect : aspects) {
                if (aspect.needsAspect(res.type)) {
                    AspectSpec spec = aspectMaker.make(res.rawClass, aspect);
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
                setField(fieldRes.field, owner, value);
            }
        }
    }

    private void setField(
        @Nonnull Field field, @Nonnull Object owner, @Nonnull Object value
    ) throws SimpleAppException {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(owner, value);
            field.setAccessible(accessible);
        } catch (Exception e) {
            throw new SimpleAppException("Set field failed on :" + field + ".", e);
        }
    }

    private @Nonnull Res getRes(
        @Nonnull Type type,
        @Nonnull Map<@Nonnull Type, @Nonnull Res> resourceMap
    ) throws SimpleAppException {
        Res res = resourceMap.get(type);
        if (res != null) {
            return res;
        }
        for (Res resource : resourceMap.values()) {
            if (TypeKit.isAssignable(type, resource.type)) {
                return resource;
            }
        }
        throw new SimpleAppException("Can not find resource instance for type :" + type.getTypeName() + ".");
    }

    private void checkDependencyForPostConstruct(
        @Nonnull SimpleResource firstRes,
        @Nonnull SimpleResource curRes,
        @Nonnull Set<@Nonnull Type> stack,
        @Nonnull @OutParam Set<@Nonnull SimpleResource> postConstructSet
    ) throws SimpleAppException {
        Type curType = curRes.type();
        Method postConstruct = curRes.postConstructMethod();
        if (postConstruct == null) {
            return;
        }
        postConstructSet.add(curRes);
        SimpleDependsOn sdo = postConstruct.getAnnotation(SimpleDependsOn.class);
        if (sdo == null) {
            return;
        }
        if (!stack.add(curType)) {
            throw new SimpleAppException(
                "Circular post-construct dependency detected: " +
                    stack.stream().map(Type::getTypeName).collect(Collectors.joining(" -> ")) + "."
            );
        }
        for (Class<?> depType : sdo.value()) {
            SimpleResource depRes = resources.get(depType);
            if (depRes == null) {
                throw new SimpleAppException("Unknown post-construct dependency type: " + depType.getTypeName() + ".");
            }
            checkDependencyForPostConstruct(firstRes, depRes, stack, postConstructSet);
            stack.remove(depType);
        }
    }

    private void checkDependencyForPreDestroy(
        @Nonnull SimpleResource firstRes,
        @Nonnull SimpleResource curRes,
        @Nonnull Set<@Nonnull Type> stack,
        @Nonnull @OutParam Set<@Nonnull SimpleResource> preDestroySet
    ) throws SimpleAppException {
        Type curType = curRes.type();
        Method preDestroy = curRes.preDestroyMethod();
        if (preDestroy == null) {
            return;
        }
        preDestroySet.add(curRes);
        SimpleDependsOn sdo = preDestroy.getAnnotation(SimpleDependsOn.class);
        if (sdo == null) {
            return;
        }
        if (!stack.add(curType)) {
            throw new SimpleAppException(
                "Circular pre-destroy dependency: " +
                    stack.stream().map(Type::getTypeName).collect(Collectors.joining(" -> ")) + "."
            );
        }
        for (Class<?> depType : sdo.value()) {
            SimpleResource depRes = resources.get(depType);
            if (depRes == null) {
                throw new SimpleAppException("Unknown pre-destroy dependency type: " + depType.getTypeName() + ".");
            }
            checkDependencyForPreDestroy(firstRes, depRes, stack, preDestroySet);
            stack.remove(depType);
        }
    }

    private @Nonnull Object getResInstance(
        @Nonnull Res res
    ) {
        return res.advisedInstance != null ? res.advisedInstance : res.instance;
    }

    @Override
    public void shutdown() throws SimpleResourceDestroyException, SimpleAppException {
        List<SimpleResource> undestroyedResources = new ArrayList<>(preDestroyList);
        List<SimpleResource> destroyedResources = new ArrayList<>(preDestroyList);
        Iterator<SimpleResource> undestroyedIt = undestroyedResources.iterator();
        while (undestroyedIt.hasNext()) {
            SimpleResource resource = undestroyedIt.next();
            Method preDestroy = Jie.asNonnull(resource.preDestroyMethod());
            try {
                preDestroy.invoke(resource.instance());
                destroyedResources.add(resource);
                undestroyedIt.remove();
            } catch (Exception e) {
                throw new SimpleResourceDestroyException(resource, e, destroyedResources, undestroyedResources);
            }
        }
    }

    @Override
    public @Nonnull List<@Nonnull SimpleApp> dependencyApps() {
        return dependencyApps;
    }

    @Override
    public @Nonnull List<@Nonnull SimpleResource> localResources() {
        return localResources;
    }

    @Override
    public @Nonnull List<@Nonnull SimpleResource> allResources() {
        return allResources;
    }

    @Override
    public @Nullable Object getResource(@Nonnull Type type) {
        SimpleResource resource = resources.get(type);
        if (resource != null) {
            return resource.instance();
        }
        for (SimpleResource sr : allResources) {
            if (TypeKit.isAssignable(type, sr.type())) {
                return sr.instance();
            }
        }
        return null;
    }

    private static @Nonnull Class<?> rawClass(@Nonnull Type type) {
        Class<?> raw = TypeKit.getRawClass(type);
        if (raw == null) {
            throw new UnsupportedOperationException("Unsupported DI type: " + type.getTypeName() + ".");
        }
        return raw;
    }

    private static final class Res {

        private final @Nonnull Type type;
        private final boolean local;
        private final Class<?> rawClass;
        private final @Nullable Method postConstruct;
        private final @Nullable Method preDestroy;
        private final @Nonnull Object instance;

        private Object advisedInstance;
        private boolean isAspectHandler = false;

        private Res(
            @Nonnull Type type,
            @Nonnull Class<?> rawClass,
            @Nonnull String @Nonnull [] postConstructAnnotations,
            @Nonnull String @Nonnull [] preDestroyAnnotations
        ) throws SimpleAppException {
            this.type = type;
            this.local = true;
            this.rawClass = rawClass;
            Method postConstruct = null;
            Method preDestroy = null;
            for (Method method : rawClass.getMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (ArrayKit.indexOf(postConstructAnnotations, annotation.annotationType().getName()) >= 0) {
                        postConstruct = method;
                    }
                    if (ArrayKit.indexOf(preDestroyAnnotations, annotation.annotationType().getName()) >= 0) {
                        preDestroy = method;
                    }
                }
                if (postConstruct != null && preDestroy != null) {
                    break;
                }
            }
            this.postConstruct = postConstruct;
            this.preDestroy = preDestroy;
            try {
                this.instance = rawClass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new SimpleAppException("Creates instance for " + type.getTypeName() + " failed.", e);
            }
        }

        private Res(@Nonnull Type type, @Nonnull Object instance) {
            this.type = type;
            this.local = false;
            this.rawClass = null;
            this.postConstruct = null;
            this.preDestroy = null;
            this.instance = instance;
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

    private static final class SimpleRes implements SimpleResource {

        private final @Nonnull Type type;
        private final @Nonnull Object instance;
        private final boolean local;
        private final @Nullable Method postConstruct;
        private final @Nullable Method preDestroy;

        private SimpleRes(
            @Nonnull Type type,
            @Nonnull Object instance,
            boolean local,
            @Nullable Method postConstruct,
            @Nullable Method preDestroy
        ) {
            this.type = type;
            this.instance = instance;
            this.local = local;
            this.postConstruct = postConstruct;
            this.preDestroy = preDestroy;
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
            return postConstruct;
        }

        @Override
        public @Nullable Method preDestroyMethod() {
            return preDestroy;
        }
    }

    enum PostConstructComparator implements Comparator<SimpleResource> {

        INST;

        @Override
        public int compare(@Nonnull SimpleResource sr1, @Nonnull SimpleResource sr2) {
            Method pc1 = Jie.asNonnull(sr1.postConstructMethod());
            Method pc2 = Jie.asNonnull(sr2.postConstructMethod());
            SimpleDependsOn sd1 = pc1.getAnnotation(SimpleDependsOn.class);
            SimpleDependsOn sd2 = pc2.getAnnotation(SimpleDependsOn.class);
            return compareDependsOn(sr1, sd1, sr2, sd2);
        }
    }

    enum PreDestroyComparator implements Comparator<SimpleResource> {

        INST;

        @Override
        public int compare(@Nonnull SimpleResource sr1, @Nonnull SimpleResource sr2) {
            Method pd1 = Jie.asNonnull(sr1.preDestroyMethod());
            Method pd2 = Jie.asNonnull(sr2.preDestroyMethod());
            SimpleDependsOn sd1 = pd1.getAnnotation(SimpleDependsOn.class);
            SimpleDependsOn sd2 = pd2.getAnnotation(SimpleDependsOn.class);
            return compareDependsOn(sr1, sd1, sr2, sd2);
        }
    }

    private static int compareDependsOn(
        @Nonnull SimpleResource sr1, @Nullable SimpleDependsOn sd1,
        @Nonnull SimpleResource sr2, @Nullable SimpleDependsOn sd2
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
