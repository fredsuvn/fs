package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.common.runtime.aspect.AspectHandler;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


final class SimpleAppImpl implements SimpleApp {

    public SimpleAppImpl(
        @Nonnull List<@Nonnull Type> resourceClasses,
        @Nonnull List<@Nonnull SimpleApp> parents,
        boolean enableAspect,
        @Nonnull Class<? extends @Nonnull Annotation> resourceAnnotation,
        @Nonnull Class<? extends @Nonnull Annotation> postConstructAnnotation,
        @Nonnull Class<? extends @Nonnull Annotation> preDestroyAnnotation
    ) throws SimpleAppException {
        try {
            Map<@Nonnull Type, @Nonnull ResourceBuilder> resourceMap = new HashMap<>();
            for (Type resourceClass : resourceClasses) {
                scanResourceTypes(
                    resourceClass,
                    resourceAnnotation,
                    postConstructAnnotation,
                    preDestroyAnnotation,
                    resourceMap
                );
            }
        } catch (Exception e) {
            throw new SimpleAppException(e);
        }
    }

    private void scanResourceTypes(
        @Nonnull Type type,
        @Nonnull Class<? extends @Nonnull Annotation> resourceAnnotation,
        @Nonnull Class<? extends @Nonnull Annotation> postConstructAnnotation,
        @Nonnull Class<? extends @Nonnull Annotation> preDestroyAnnotation,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull ResourceBuilder> resourceMap
    ) throws Exception {
        if (resourceMap.containsKey(type)) {
            return;
        }
        Class<?> rawClass = TypeKit.getRawClass(type);
        if (rawClass == null) {
            throw new UnsupportedOperationException("Unsupported DI type: " + type.getTypeName() + ".");
        }
        Method postConstruct = null;
        Method preDestroy = null;
        for (Method method : rawClass.getMethods()) {
            if (method.isAnnotationPresent(postConstructAnnotation)) {
                postConstruct = method;
            }
            if (method.isAnnotationPresent(preDestroyAnnotation)) {
                preDestroy = method;
            }
        }
        List<Field> fields = new ArrayList<>();
        Class<?> cur = rawClass;
        while (cur != null) {
            for (Field declaredField : cur.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(resourceAnnotation)) {
                    fields.add(declaredField);
                }
            }
            cur = cur.getSuperclass();
        }
        ResourceBuilder rb = new ResourceBuilder(type, rawClass, fields, postConstruct, preDestroy);
        resourceMap.put(type, rb);
        for (Field field : fields) {
            scanResourceTypes(
                field.getGenericType(),
                resourceAnnotation,
                postConstructAnnotation,
                preDestroyAnnotation,
                resourceMap
            );
        }
    }

    private void scanResourceAspects(
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull ResourceBuilder> resourceMap
    ) throws Exception {
        List<ResourceBuilder> aspects = new ArrayList<>();
        for (ResourceBuilder rb : resourceMap.values()) {
            Class<?> rawType = rb.rawType;
            if (SimpleAppAspect.class.isAssignableFrom(rawType)) {
                aspects.add(rb);
            }
        }
        if (aspects.isEmpty()) {
            return;
        }
        for (ResourceBuilder rb : aspects) {
            for (ResourceBuilder aspect : aspects) {
                //if (aspect.)
            }
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public @Nonnull List<@Nonnull SimpleApp> parents() {
        return Collections.emptyList();
    }

    @Override
    public @Nonnull Map<@Nonnull Type, @Nonnull Object> resources() {
        return Collections.emptyMap();
    }

    @Override
    public <T> @Nullable T getResource(@Nonnull Type type) {
        return null;
    }

    private static final class ResourceBuilder {

        private final @Nonnull Type type;
        private final @Nonnull Class<?> rawType;
        private final @Nonnull List<@Nonnull Field> fields;
        private final @Nullable Method postConstruct;
        private final @Nullable Method preDestroy;
        private @Nullable SimpleAppAspect aspect;

        private ResourceBuilder(
            @Nonnull Type type,
            @Nonnull Class<?> rawType,
            @Nonnull List<@Nonnull Field> fields,
            @Nullable Method postConstruct,
            @Nullable Method preDestroy
        ) {
            this.type = type;
            this.rawType = rawType;
            this.fields = fields;
            this.postConstruct = postConstruct;
            this.preDestroy = preDestroy;
        }
    }
}
