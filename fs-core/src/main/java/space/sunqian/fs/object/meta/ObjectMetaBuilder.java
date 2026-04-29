package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.invoke.Invocable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ObjectMetaBuilder implements ObjectMetaManager.Context {

    private final @Nonnull Type type;
    private final @Nonnull Map<@Nonnull String, @Nonnull PropertyMetaBase> properties = new LinkedHashMap<>();

    ObjectMetaBuilder(@Nonnull Type type) {
        this.type = type;
    }

    @Override
    public @Nonnull Type parsedType() {
        return type;
    }

    @Override
    public @Nonnull Map<@Nonnull String, @Nonnull PropertyMetaBase> propertyBaseMap() {
        return properties;
    }

    @Nonnull
    ObjectMeta build(@Nonnull ObjectMetaManager parser) {
        return new ObjectMetaImpl(parser, type, properties);
    }

    private static final class ObjectMetaImpl implements ObjectMeta {

        private final @Nonnull ObjectMetaManager parser;
        private final @Nonnull Type type;
        private final @Nonnull Map<@Nonnull String, @Nonnull PropertyMeta> properties;

        private ObjectMetaImpl(
            @Nonnull ObjectMetaManager parser,
            @Nonnull Type type,
            @Nonnull Map<@Nonnull String, @Nonnull PropertyMetaBase> propBases
        ) {
            this.parser = parser;
            this.type = type;
            Map<@Nonnull String, @Nonnull PropertyMeta> props = new LinkedHashMap<>();
            propBases.forEach((name, propBase) -> props.put(name, new PropertyMetaImpl(propBase)));
            this.properties = Collections.unmodifiableMap(props);
        }

        @Override
        public @Nonnull ObjectMetaManager manager() {
            return parser;
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull Map<@Nonnull String, @Nonnull PropertyMeta> properties() {
            return properties;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            return MetaKit.equals(this, o);
        }

        @Override
        public int hashCode() {
            return MetaKit.hashCode(this);
        }

        @Override
        public @Nonnull String toString() {
            return MetaKit.toString(this);
        }

        private final class PropertyMetaImpl implements PropertyMeta {

            private final @Nonnull String name;
            private final @Nonnull Type type;
            private final @Nullable Method getterMethod;
            private final @Nullable Method setterMethod;
            private final @Nullable Field field;
            private final @Nullable Invocable getter;
            private final @Nullable Invocable setter;

            // annotations:
            private final @Nonnull List<@Nonnull Annotation> getterAnnotations;
            private final @Nonnull List<@Nonnull Annotation> setterAnnotations;
            private final @Nonnull List<@Nonnull Annotation> fieldAnnotations;
            private final @Nonnull Map<@Nonnull Class<?>, @Nonnull Annotation> annotations;

            private PropertyMetaImpl(@Nonnull PropertyMetaBase propertyBase) {
                this.name = propertyBase.name();
                this.type = propertyBase.type();
                this.getterMethod = propertyBase.getterMethod();
                this.setterMethod = propertyBase.setterMethod();
                this.field = propertyBase.field();
                this.getter = propertyBase.getter();
                this.setter = propertyBase.setter();
                this.getterAnnotations = getterMethod == null ?
                    Collections.emptyList() : ListKit.list(getterMethod.getAnnotations());
                this.setterAnnotations = setterMethod == null ?
                    Collections.emptyList() : ListKit.list(setterMethod.getAnnotations());
                this.fieldAnnotations = field == null ?
                    Collections.emptyList() : ListKit.list(field.getAnnotations());
                Map<Class<?>, Annotation> annMap = new HashMap<>();
                fieldAnnotations.forEach(ann -> annMap.put(ann.annotationType(), ann));
                setterAnnotations.forEach(ann -> annMap.put(ann.annotationType(), ann));
                getterAnnotations.forEach(ann -> annMap.put(ann.annotationType(), ann));
                annotations = Collections.unmodifiableMap(annMap);
            }

            @Override
            public @Nonnull ObjectMeta owner() {
                return ObjectMetaImpl.this;
            }

            @Override
            public @Nonnull String name() {
                return name;
            }

            @Override
            public @Nonnull Type type() {
                return type;
            }

            @Override
            public @Nullable Method getterMethod() {
                return getterMethod;
            }

            @Override
            public @Nullable Method setterMethod() {
                return setterMethod;
            }

            @Override
            public @Nullable Field field() {
                return field;
            }

            @Override
            public @Nullable Invocable getter() {
                return getter;
            }

            @Override
            public @Nullable Invocable setter() {
                return setter;
            }

            @Override
            public @Nonnull List<@Nonnull Annotation> fieldAnnotations() {
                return fieldAnnotations;
            }

            @Override
            public <T extends Annotation> @Nullable T getAnnotation(@Nonnull Class<T> annotationType) {
                return Fs.as(annotations.get(annotationType));
            }

            @Override
            public @Nonnull List<@Nonnull Annotation> getterAnnotations() {
                return getterAnnotations;
            }

            @Override
            public @Nonnull List<@Nonnull Annotation> setterAnnotations() {
                return setterAnnotations;
            }

            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            @Override
            public boolean equals(Object o) {
                return MetaKit.equals(this, o);
            }

            @Override
            public int hashCode() {
                return MetaKit.hashCode(this);
            }

            @Override
            public @Nonnull String toString() {
                return MetaKit.toString(this);
            }
        }
    }
}
