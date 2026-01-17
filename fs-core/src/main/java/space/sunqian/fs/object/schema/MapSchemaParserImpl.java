package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

enum MapSchemaParserImpl implements MapSchemaParser {

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