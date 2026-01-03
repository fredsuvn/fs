package space.sunqian.fs.object.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ObjectSchemaBuilder implements ObjectSchemaParser.Context {

    private final @Nonnull Type type;
    private final @Nonnull Map<@Nonnull String, @Nonnull ObjectPropertyBase> properties = new LinkedHashMap<>();

    ObjectSchemaBuilder(Type type) {
        this.type = type;
    }

    @Override
    public @Nonnull Type dataType() {
        return type;
    }

    @Override
    public @Nonnull Map<@Nonnull String, @Nonnull ObjectPropertyBase> propertyBaseMap() {
        return properties;
    }

    @Nonnull
    ObjectSchema build(@Nonnull ObjectSchemaParser parser) {
        return new ObjectSchemaImpl(parser, type, properties);
    }

    private static final class ObjectSchemaImpl implements ObjectSchema {

        private final @Nonnull ObjectSchemaParser parser;
        private final @Nonnull Type type;
        private final @Nonnull Map<@Nonnull String, @Nonnull ObjectProperty> properties;

        private ObjectSchemaImpl(
            @Nonnull ObjectSchemaParser parser,
            @Nonnull Type type,
            @Nonnull Map<@Nonnull String, @Nonnull ObjectPropertyBase> propBases
        ) {
            this.parser = parser;
            this.type = type;
            Map<@Nonnull String, @Nonnull ObjectProperty> props = new LinkedHashMap<>();
            propBases.forEach((name, propBase) -> props.put(name, new PropertyImpl(propBase)));
            this.properties = Collections.unmodifiableMap(props);
        }

        @Override
        public @Nonnull ObjectSchemaParser parser() {
            return parser;
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull Map<@Nonnull String, @Nonnull ObjectProperty> properties() {
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

        private final class PropertyImpl implements ObjectProperty {

            private final @Nonnull String name;
            private final @Nonnull Type type;
            private final @Nullable Method getterMethod;
            private final @Nullable Method setterMethod;
            private final @Nullable Field field;
            private final @Nullable Invocable getter;
            private final @Nullable Invocable setter;

            private PropertyImpl(@Nonnull ObjectPropertyBase propertyBase) {
                this.name = propertyBase.name();
                this.type = propertyBase.type();
                this.getterMethod = propertyBase.getterMethod();
                this.setterMethod = propertyBase.setterMethod();
                this.field = propertyBase.field();
                this.getter = propertyBase.getter();
                this.setter = propertyBase.setter();
            }

            @Override
            public @Nonnull ObjectSchema owner() {
                return ObjectSchemaImpl.this;
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
