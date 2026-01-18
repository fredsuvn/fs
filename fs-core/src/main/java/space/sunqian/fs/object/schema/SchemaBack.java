package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.schema.handlers.SimpleBeanSchemaHandler;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SchemaBack {

    enum MapParserImpl implements MapParser {

        INST;

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type) throws DataSchemaException {
            try {
                return new MapSchemaImpl(type);
            } catch (Exception e) {
                throw new DataSchemaException(e);
            }
        }

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type, @Nonnull Type keyType, @Nonnull Type valueType) {
            return new MapSchemaImpl(type, keyType, valueType);
        }

        private final class MapSchemaImpl implements MapSchema {

            private final @Nonnull Type type;
            private final @Nonnull Type keyType;
            private final @Nonnull Type valueType;

            private MapSchemaImpl(@Nonnull Type type) throws ReflectionException {
                this.type = type;
                List<Type> actualTypes = TypeKit.resolveActualTypeArguments(type, Map.class);
                this.keyType = actualTypes.get(0);
                this.valueType = actualTypes.get(1);
            }

            private MapSchemaImpl(@Nonnull Type type, @Nonnull Type keyType, @Nonnull Type valueType) {
                this.type = type;
                this.keyType = keyType;
                this.valueType = valueType;
            }

            @Override
            public @Nonnull Type type() {
                return type;
            }

            @Override
            public @Nonnull MapParser parser() {
                return MapParserImpl.this;
            }

            @Override
            public @Nonnull Type keyType() {
                return keyType;
            }

            @Override
            public @Nonnull Type valueType() {
                return valueType;
            }

            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            @Override
            public boolean equals(Object o) {
                return DataSchemaKit.equals(this, o);
            }

            @Override
            public int hashCode() {
                return DataSchemaKit.hashCode(this);
            }

            @Override
            public @Nonnull String toString() {
                return DataSchemaKit.toString(this);
            }
        }
    }

    static final class ObjectParserImpl implements ObjectParser, ObjectParser.Handler {

        static @Nonnull SchemaBack.ObjectParserImpl DEFAULT = new ObjectParserImpl(FsLoader.loadInstances(
            FsLoader.loadClassByDependent(
                ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler"),
                "com.google.protobuf.Message"
            ),
            SimpleBeanSchemaHandler.INSTANCE
        ));

        private final @Nonnull List<@Nonnull Handler> handlers;

        ObjectParserImpl(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return handlers;
        }

        @Override
        public @Nonnull Handler asHandler() {
            return this;
        }

        @Override
        public boolean parse(@Nonnull Context context) throws Exception {
            for (Handler handler : handlers) {
                if (!handler.parse(context)) {
                    return false;
                }
            }
            return true;
        }
    }

    static final class ObjectSchemaBuilder implements ObjectParser.Context {

        private final @Nonnull Type type;
        private final @Nonnull Map<@Nonnull String, @Nonnull ObjectPropertyBase> properties = new LinkedHashMap<>();

        ObjectSchemaBuilder(@Nonnull Type type) {
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
        ObjectSchema build(@Nonnull ObjectParser parser) {
            return new ObjectSchemaImpl(parser, type, properties);
        }

        private static final class ObjectSchemaImpl implements ObjectSchema {

            private final @Nonnull ObjectParser parser;
            private final @Nonnull Type type;
            private final @Nonnull Map<@Nonnull String, @Nonnull ObjectProperty> properties;

            private ObjectSchemaImpl(
                @Nonnull ObjectParser parser,
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
            public @Nonnull ObjectParser parser() {
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
                return DataSchemaKit.equals(this, o);
            }

            @Override
            public int hashCode() {
                return DataSchemaKit.hashCode(this);
            }

            @Override
            public @Nonnull String toString() {
                return DataSchemaKit.toString(this);
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
                    return DataSchemaKit.equals(this, o);
                }

                @Override
                public int hashCode() {
                    return DataSchemaKit.hashCode(this);
                }

                @Override
                public @Nonnull String toString() {
                    return DataSchemaKit.toString(this);
                }
            }
        }
    }
}
