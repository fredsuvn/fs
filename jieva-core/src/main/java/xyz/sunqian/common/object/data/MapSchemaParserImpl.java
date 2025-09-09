package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

class MapSchemaParserImpl implements MapSchemaParser {

    static final @Nonnull MapSchemaParser SINGLETON = new MapSchemaParserImpl();

    @Override
    public @Nonnull MapSchema parse(@Nonnull Type type) throws DataObjectException {
        try {
            return new MapSchemaImpl(type);
        } catch (Exception e) {
            throw new DataObjectException(e);
        }
    }

    private final class MapSchemaImpl implements MapSchema {

        private final @Nonnull Type type;
        private final @Nonnull Type keyType;
        private final @Nonnull Type valueType;

        MapSchemaImpl(@Nonnull Type type) {
            this.type = type;
            List<Type> actualTypes = TypeKit.resolveActualTypeArguments(type, Map.class);
            this.keyType = actualTypes.get(0);
            this.valueType = actualTypes.get(1);
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull MapSchemaParser parser() {
            return MapSchemaParserImpl.this;
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