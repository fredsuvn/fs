package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class SimpleAppImpl implements SimpleApp {

    private final @Nonnull List<@Nonnull SimpleResource> localResources;
    private final @Nonnull List<@Nonnull SimpleResource> allResources;
    private final @Nonnull Map<@Nonnull Type, @Nonnull SimpleResource> resources;
    private final @Nonnull List<@Nonnull ForPreDestroy> destroyList;
    private final @Nonnull List<@Nonnull SimpleApp> parents;

    public SimpleAppImpl(
        @Nonnull List<@Nonnull Type> resourceTypes,
        @Nonnull SimpleApp @Nonnull [] parents,
        boolean enableAspect,
        @Nonnull String @Nonnull [] resourceAnnotations,
        @Nonnull String @Nonnull [] postConstructAnnotations,
        @Nonnull String @Nonnull [] preDestroyAnnotations
    ) throws SimpleAppException {
        Map<Type, Res> resourceMap = new HashMap<>();
        Set<FieldRes> fieldSet = new HashSet<>();
        // generate instances
        for (Type resourceType : resourceTypes) {
            dependencyInjection(
                resourceType,
                resourceAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                parents,
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
        this.parents = ListKit.list(parents);
        this.resources = Collections.unmodifiableMap(resources);
        this.localResources = ListKit.list(localResources);
        this.allResources = ListKit.list(allResources);
        // destroy list
        ArrayList<ForPreDestroy> destroyList = new ArrayList<>();
        // for (Res res : resourceMap.values()) {
        //     Object instance = res.advisedInstance != null ? res.advisedInstance : res.instance;
        //     Method postConstruct = res.postConstruct;
        //     if (postConstruct != null) {
        //         try {
        //             postConstruct.invoke(instance);
        //         } catch (Exception e) {
        //             throw new SimpleAppException("Executes Post-Construct failed on :" + postConstruct + ".", e);
        //         }
        //     }
        //     allResources.put(res.type, instance);
        //     if (res.local) {
        //         resources.put(res.type, instance);
        //     }
        //     Method preDestroy = res.preDestroy;
        //     if (preDestroy != null) {
        //         destroyList.add(new ForPreDestroy(instance, preDestroy));
        //     }
        // }
        destroyList.trimToSize();
        this.destroyList = destroyList;
    }

    private void dependencyInjection(
        @Nonnull Type type,
        @Nonnull String @Nonnull [] resourceAnnotations,
        @Nonnull String @Nonnull [] postConstructAnnotations,
        @Nonnull String @Nonnull [] preDestroyAnnotations,
        @Nonnull SimpleApp @Nonnull [] parents,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet
    ) throws SimpleAppException {
        if (resourceMap.containsKey(type)) {
            return;
        }
        for (SimpleApp parent : parents) {
            Object instance = parent.getResource(type);
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
                            parents,
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
    ) {
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

    private void checkDependency(
        @Nonnull List<@Nonnull SimpleResource> resources
    ) throws SimpleAppException {
        Set<Type> stack = new HashSet<>();
        for (SimpleResource resource : resources) {
            Method postConstruct = resource.postConstructMethod();
            // while (postConstruct != null) {
            //     SimpleDependency sd = postConstruct.getAnnotation(SimpleDependency.class);
            //     if (sd != null) {
            //         if (!stack.add(resource.type())){
            //             throw new SimpleAppException("Circular dependency detected on :" + resource.type() + ".");
            //         }
            //         //Class<?>
            //     } else {
            //         break;
            //     }
            // }
        }
    }

    private void checkPostConstruct(@Nonnull Class<?> type) throws SimpleAppException {
    }

    private @Nonnull Object getResInstance(
        @Nonnull Res res
    ) {
        return res.advisedInstance != null ? res.advisedInstance : res.instance;
    }

    @Override
    public void shutdown() throws SimpleAppException {
        for (ForPreDestroy destroy : destroyList) {
            try {
                destroy.preDestroy.invoke(destroy.instance);
            } catch (Exception e) {
                throw new SimpleAppException("Executes Pre-Destroy failed on :" + destroy.preDestroy + ".", e);
            }
        }
    }

    @Override
    public @Nonnull List<@Nonnull SimpleApp> parents() {
        return parents;
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

    private static final class ForPreDestroy {

        private final @Nonnull Object instance;
        private final @Nonnull Method preDestroy;

        private ForPreDestroy(@Nonnull Object instance, @Nonnull Method preDestroy) {
            this.instance = instance;
            this.preDestroy = preDestroy;
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
}
