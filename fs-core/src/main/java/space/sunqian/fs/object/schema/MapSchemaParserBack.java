package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

final class MapSchemaParserBack {

    static @Nonnull MapSchemaParser defaultParser() {
        return DefaultMapSchemaParser.INST;
    }

    static @Nonnull MapSchemaParser defaultCachedParser() {
        return CachedMapSchemaParser.DEFAULT;
    }

    static @Nonnull MapSchemaParserBack.CachedMapSchemaParser newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
        @Nonnull MapSchemaParser parser
    ) {
        return new CachedMapSchemaParser(cache, parser);
    }

    private enum DefaultMapSchemaParser implements MapSchemaParser {

        INST;

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type) throws DataSchemaException {
            try {
                return new MapSchemaImpl(type);
            } catch (Exception e) {
                throw new DataSchemaException(e);
            }
        }

        private final class MapSchemaImpl implements MapSchema {

            private final @Nonnull Type type;
            private final @Nonnull Type keyType;
            private final @Nonnull Type valueType;

            private MapSchemaImpl(@Nonnull Type type) throws ReflectionException {
                if (type instanceof MapType) {
                    MapType mapType = (MapType) type;
                    this.type = mapType.mapType();
                    this.keyType = mapType.keyType();
                    this.valueType = mapType.valueType();
                } else {
                    this.type = type;
                    List<Type> actualTypes = TypeKit.resolveActualTypeArguments(type, Map.class);
                    this.keyType = actualTypes.get(0);
                    this.valueType = actualTypes.get(1);
                }
            }

            @Override
            public @Nonnull Type type() {
                return type;
            }

            @Override
            public @Nonnull MapSchemaParser parser() {
                return DefaultMapSchemaParser.this;
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

    private static final class CachedMapSchemaParser implements MapSchemaParser {

        private static final @Nonnull MapSchemaParserBack.CachedMapSchemaParser DEFAULT = newCachedParser(
            SimpleCache.ofSoft(),
            MapSchemaParser.defaultParser()
        );

        private final @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache;
        private final @Nonnull MapSchemaParser parser;

        private CachedMapSchemaParser(
            @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
            @Nonnull MapSchemaParser parser
        ) {
            this.cache = cache;
            this.parser = parser;
        }

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type) {
            return cache.get(type, parser::parse);
        }
    }

    private MapSchemaParserBack() {
    }
}
