package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class DataSchemaBuilder implements DataSchemaParser.Context {

    private final @Nonnull Type type;
    private final @Nonnull Map<@Nonnull String, @Nonnull DataPropertyBase> properties = new LinkedHashMap<>();

    DataSchemaBuilder(Type type) {
        this.type = type;
    }

    @Override
    public @Nonnull Type dataType() {
        return type;
    }

    @Override
    public @Nonnull Map<@Nonnull String, @Nonnull DataPropertyBase> propertyBaseMap() {
        return properties;
    }

    @Nonnull
    DataSchema build(@Nonnull DataSchemaParser parser) {
        return new DataSchemaImpl(parser, type, properties);
    }

    private static final class DataSchemaImpl implements DataSchema {

        private final @Nonnull DataSchemaParser parser;
        private final @Nonnull Type type;
        private final @Nonnull Map<@Nonnull String, @Nonnull DataProperty> properties;

        private DataSchemaImpl(
            @Nonnull DataSchemaParser parser,
            @Nonnull Type type,
            @Nonnull Map<@Nonnull String, @Nonnull DataPropertyBase> propBases
        ) {
            this.parser = parser;
            this.type = type;
            Map<@Nonnull String, @Nonnull DataProperty> props = new LinkedHashMap<>();
            propBases.forEach((name, propBase) -> props.put(name, new PropertyImpl(propBase)));
            this.properties = Collections.unmodifiableMap(props);
        }

        @Override
        public @Nonnull DataSchemaParser parser() {
            return parser;
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull Map<@Nonnull String, @Nonnull DataProperty> properties() {
            return properties;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            return DataObjectKit.equals(this, o);
        }

        @Override
        public int hashCode() {
            return DataObjectKit.hashCode(this);
        }

        @Override
        public @Nonnull String toString() {
            return DataObjectKit.toString(this);
        }

        private final class PropertyImpl implements DataProperty {

            private final @Nonnull String name;
            private final @Nonnull Type type;
            private final @Nullable Method getterMethod;
            private final @Nullable Method setterMethod;
            private final @Nullable Field field;
            private final @Nullable Invocable getter;
            private final @Nullable Invocable setter;

            // annotations
            private final @Nonnull List<@Nonnull Annotation> fieldAnnotations;
            private final @Nonnull List<@Nonnull Annotation> getterAnnotations;
            private final @Nonnull List<@Nonnull Annotation> setterAnnotations;
            private final @Nonnull List<@Nonnull Annotation> annotations;

            private PropertyImpl(@Nonnull DataPropertyBase propertyBase) {
                this.name = propertyBase.name();
                this.type = propertyBase.type();
                this.getterMethod = propertyBase.getterMethod();
                this.setterMethod = propertyBase.setterMethod();
                this.field = propertyBase.field();
                this.getter = propertyBase.getter();
                this.setter = propertyBase.setter();

                // annotations
                this.fieldAnnotations = getAnnotations(field);
                this.getterAnnotations = getAnnotations(getterMethod);
                this.setterAnnotations = getAnnotations(setterMethod);
                this.annotations = ListKit.compositeView(fieldAnnotations, getterAnnotations, setterAnnotations);
            }

            private @Nonnull List<@Nonnull Annotation> getAnnotations(@Nullable AnnotatedElement element) {
                if (element == null) {
                    return Collections.emptyList();
                }
                Annotation[] as = element.getAnnotations();
                return as.length == 0 ? Collections.emptyList() : ListKit.list(as);
            }

            @Override
            public @Nonnull DataSchema owner() {
                return DataSchemaImpl.this;
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
            public @Nonnull List<@Nonnull Annotation> fieldAnnotations() {
                return fieldAnnotations;
            }

            @Override
            public @Nonnull List<@Nonnull Annotation> getterAnnotations() {
                return getterAnnotations;
            }

            @Override
            public @Nonnull List<@Nonnull Annotation> setterAnnotations() {
                return setterAnnotations;
            }

            @Override
            public @Nonnull List<@Nonnull Annotation> annotations() {
                return annotations;
            }

            @Override
            public @Nullable Invocable getter() {
                return getter;
            }

            @Override
            public @Nullable Invocable setter() {
                return setter;
            }

            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            @Override
            public boolean equals(Object o) {
                return DataObjectKit.equals(this, o);
            }

            @Override
            public int hashCode() {
                return DataObjectKit.hashCode(this);
            }

            @Override
            public @Nonnull String toString() {
                return DataObjectKit.toString(this);
            }
        }
    }
}
