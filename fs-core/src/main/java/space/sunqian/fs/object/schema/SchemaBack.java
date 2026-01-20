package space.sunqian.fs.object.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.schema.handlers.CommonSchemaHandler;
import space.sunqian.fs.object.schema.handlers.RecordSchemaHandler;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

final class SchemaBack {

    static @Nonnull MapParser defaultMapParser() {
        return DefaultMapParser.INST;
    }

    static @Nonnull MapParser cachedMapParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
        @Nonnull MapParser parser
    ) {
        return new CachedMapParser(cache, parser);
    }

    static @Nonnull ObjectParser defaultObjectParser() {
        return ObjectParserImpl.DEFAULT;
    }

    static @Nonnull ObjectParser newObjectParser(
        @Nonnull @RetainedParam List<ObjectParser.@Nonnull Handler> handlers
    ) {
        return new ObjectParserImpl(handlers);
    }

    static @Nonnull ObjectParser cachedObjectParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache,
        @Nonnull ObjectParser parser
    ) {
        return new CachedObjectParser(cache, parser);
    }

    private enum DefaultMapParser implements MapParser {

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
                return DefaultMapParser.this;
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
                return SchemaKit.equals(this, o);
            }

            @Override
            public int hashCode() {
                return SchemaKit.hashCode(this);
            }

            @Override
            public @Nonnull String toString() {
                return SchemaKit.toString(this);
            }
        }
    }

    private static final class CachedMapParser implements MapParser {

        private final @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache;
        private final @Nonnull MapParser parser;

        private CachedMapParser(
            @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
            @Nonnull MapParser parser
        ) {
            this.cache = cache;
            this.parser = parser;
        }

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type) {
            return cache.get(type, parser::parse);
        }

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type, @Nonnull Type keyType, @Nonnull Type valueType) {
            Type actualType = new MapType(type, keyType, valueType);
            return cache.get(actualType, t -> parser.parse(t, keyType, valueType));
        }

        @Data
        @AllArgsConstructor
        @EqualsAndHashCode(callSuper = false)
        @ToString
        private static final class MapType implements Type {
            private final @Nonnull Type mapType;
            private final @Nonnull Type keyType;
            private final @Nonnull Type valueType;
        }
    }

    private static final class ObjectParserImpl implements ObjectParser, ObjectParser.Handler {

        private static final @Nonnull ObjectParserImpl DEFAULT = new ObjectParserImpl(FsLoader.loadInstances(
            FsLoader.loadClassByDependent(
                ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler"),
                "com.google.protobuf.Message"
            ),
            FsLoader.supplyByDependent(
                RecordSchemaHandler::getInstance, RecordSchemaHandler.class.getName() + "ImplByJ16"
            ),
            CommonSchemaHandler.INSTANCE
        ));

        private final @Nonnull List<@Nonnull Handler> handlers;

        private ObjectParserImpl(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
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

    private static final class CachedObjectParser implements ObjectParser {

        private final @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache;
        private final @Nonnull ObjectParser parser;

        private CachedObjectParser(
            @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache,
            @Nonnull ObjectParser parser
        ) {
            this.cache = cache;
            this.parser = parser;
        }

        @Override
        public @Nonnull ObjectSchema parse(@Nonnull Type type) throws DataSchemaException {
            return cache.get(type, parser::parse);
        }

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return parser.handlers();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return parser.asHandler();
        }
    }

    private SchemaBack() {
    }
}
