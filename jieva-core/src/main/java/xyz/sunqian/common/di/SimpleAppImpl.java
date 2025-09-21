package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
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

    private final @Nonnull Map<@Nonnull Type, @Nonnull Object> resources;
    private final @Nonnull Map<@Nonnull Type, @Nonnull Object> allResources;
    private final @Nonnull List<@Nonnull ForPreDestroy> destroyList;
    private final @Nonnull List<@Nonnull SimpleApp> parents;

    public SimpleAppImpl(
        @Nonnull List<@Nonnull Type> resourceTypes,
        @Nonnull List<@Nonnull SimpleApp> parents,
        boolean enableAspect,
        @Nullable String resourceAnnotation,
        @Nullable String postConstructAnnotation,
        @Nullable String preDestroyAnnotation
    ) throws SimpleAppException {
        Map<Type, Res> resourceMap = new HashMap<>();
        Set<FieldRes> fieldSet = new HashSet<>();
        // generate instances
        for (Type resourceType : resourceTypes) {
            dependencyInjection(
                resourceType,
                resourceAnnotation,
                postConstructAnnotation,
                preDestroyAnnotation,
                parents,
                resourceMap,
                fieldSet,
                new HashSet<>()
            );
        }
        // base injects:
        for (FieldRes fieldRes : fieldSet) {
            setField(fieldRes.field, fieldRes.owner.instance, fieldRes.reference.instance);
        }
        // aop
        if (enableAspect) {
            aop(resourceMap, fieldSet);
        }
        // execute post construct
        Map<Type, Object> resources = new HashMap<>();
        Map<Type, Object> allResources = new HashMap<>();
        ArrayList<ForPreDestroy> destroyList = new ArrayList<>();
        for (Res res : resourceMap.values()) {
            Object instance = res.advisedInstance != null ? res.advisedInstance : res.instance;
            Method postConstruct = res.postConstruct;
            if (postConstruct != null) {
                try {
                    postConstruct.invoke(instance);
                } catch (Exception e) {
                    throw new SimpleAppException("Executes Post-Construct failed on :" + postConstruct + ".", e);
                }
            }
            allResources.put(res.type, instance);
            if (res.local) {
                resources.put(res.type, instance);
            }
            Method preDestroy = res.preDestroy;
            if (preDestroy != null) {
                destroyList.add(new ForPreDestroy(instance, preDestroy));
            }
        }
        this.resources = Collections.unmodifiableMap(resources);
        this.allResources = Collections.unmodifiableMap(allResources);
        destroyList.trimToSize();
        this.destroyList = destroyList;
        this.parents = Collections.unmodifiableList(new ArrayList<>(parents));
    }

    private void dependencyInjection(
        @Nonnull Type type,
        @Nullable String resourceAnnotation,
        @Nullable String postConstructAnnotation,
        @Nullable String preDestroyAnnotation,
        @Nonnull List<@Nonnull SimpleApp> parents,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet,
        @Nonnull @OutParam Set<@Nonnull Type> interfaceTypes
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
        if (rawClass.isInterface()) {
            interfaceTypes.add(type);
            return;
        }
        int rawClassMod = rawClass.getModifiers();
        if (Modifier.isAbstract(rawClassMod)) {
            interfaceTypes.add(type);
            return;
        }
        Res res = new Res(type, rawClass, postConstructAnnotation, preDestroyAnnotation);
        resourceMap.put(type, res);
        Class<?> cur = res.rawClass;
        while (cur != null) {
            for (Field declaredField : cur.getDeclaredFields()) {
                int mod = declaredField.getModifiers();
                if (Modifier.isFinal(mod)) {
                    continue;
                }
                if (declaredField.isAnnotationPresent(resourceAnnotation)) {
                    dependencyInjection(
                        declaredField.getGenericType(),
                        resourceAnnotation,
                        postConstructAnnotation,
                        preDestroyAnnotation,
                        parents,
                        resourceMap,
                        fieldSet,
                        interfaceTypes
                    );
                    fieldSet.add(new FieldRes(declaredField, res));
                }
            }
            cur = cur.getSuperclass();
        }
    }

    private void aop(
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull Res> resourceMap,
        @Nonnull @OutParam Set<@Nonnull FieldRes> fieldSet
    ) throws SimpleAppException {
        List<SimpleAppAspect> aspects = new ArrayList<>();
        for (Res res : resourceMap.values()) {
            Object instance = res.instance;
            if (instance instanceof SimpleAppAspect) {
                aspects.add((SimpleAppAspect) instance);
                res.isAspectHandler = true;
            }
        }
        AspectMaker aspectMaker = AspectMaker.byAsm();
        for (Res res : resourceMap.values()) {
            if (res.isAspectHandler || !res.local) {
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
            Object reference;
            if (fieldRes.reference.advisedInstance != null) {
                needsRewrite = true;
                reference = fieldRes.reference.advisedInstance;
            } else {
                reference = fieldRes.reference.instance;
            }
            if (needsRewrite) {
                setField(fieldRes.field, owner, reference);
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
    public @Nonnull Map<@Nonnull Type, @Nonnull Object> resources() {
        return resources;
    }

    @Override
    public @Nonnull Map<@Nonnull Type, @Nonnull Object> allResources() {
        return allResources;
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
        private final @Nonnull Class<?> rawClass;
        private final @Nullable Method postConstruct;
        private final @Nullable Method preDestroy;
        private final boolean local;
        private final @Nonnull Object instance;

        private Object advisedInstance;
        private boolean isAspectHandler = false;

        private Res(
            @Nonnull Type type,
            @Nonnull Class<?> rawClass,
            @Nullable String postConstructAnnotation,
            @Nullable String preDestroyAnnotation
        ) throws SimpleAppException {
            this.type = type;
            this.rawClass = rawClass;
            this.local = true;
            Method postConstruct = null;
            Method preDestroy = null;
            for (Method method : rawClass.getMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType().getName().equals(postConstructAnnotation)) {
                        postConstruct = method;
                    }
                    if (annotation.annotationType().getName().equals(preDestroyAnnotation)) {
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
            this.rawClass = rawClass(type);
            this.local = false;
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
}
