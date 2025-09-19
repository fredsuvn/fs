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
            Map<@Nonnull Type, @Nonnull ResourceObject> resourceMap = new HashMap<>();
            for (Type resourceClass : resourceClasses) {
                scanResourceTypes(
                    resourceClass,
                    null,
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
        @Nullable Field field,
        @Nonnull Class<? extends @Nonnull Annotation> resourceAnnotation,
        @Nonnull Class<? extends @Nonnull Annotation> postConstructAnnotation,
        @Nonnull Class<? extends @Nonnull Annotation> preDestroyAnnotation,
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull ResourceObject> resourceMap
    ) throws Exception {
        if (resourceMap.containsKey(type)) {
            return;
        }
        Class<?> rawClass = TypeKit.getRawClass(type);
        if (rawClass == null) {
            throw new UnsupportedOperationException("Unsupported DI type: " + type.getTypeName() + ".");
        }
        ResourceObject ro = new ResourceObject(type, rawClass);
        if (field != null) {
            ro.field = field;
        }
        for (Method method : rawClass.getMethods()) {
            if (method.isAnnotationPresent(postConstructAnnotation)) {
                ro.postConstruct = method;
            }
            if (method.isAnnotationPresent(preDestroyAnnotation)) {
                ro.preDestroy = method;
            }
        }
        Class<?> cur = rawClass;
        while (cur != null) {
            for (Field declaredField : cur.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(resourceAnnotation)) {
                    Type fieldType = declaredField.getGenericType();
                    scanResourceTypes(
                        fieldType,
                        declaredField,
                        resourceAnnotation,
                        postConstructAnnotation,
                        preDestroyAnnotation,
                        resourceMap
                    );
                }
            }
            cur = cur.getSuperclass();
        }
    }

    private void scanResourceAspects(
        @Nonnull @OutParam Map<@Nonnull Type, @Nonnull ResourceObject> resourceMap
    ) throws Exception {

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

    private static final class ResourceObject {

        private final @Nonnull Type type;
        private final @Nonnull Class<?> rawType;
        private Field field;
        private Object object;
        private Method postConstruct;
        private Method preDestroy;
        private AspectHandler aspectHandler;

        private ResourceObject(@Nonnull Type type, @Nonnull Class<?> rawType) {
            this.type = type;
            this.rawType = rawType;
        }
    }
}
