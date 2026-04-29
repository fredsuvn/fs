package space.sunqian.fs.object.meta.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.MapMetaManager;
import space.sunqian.fs.object.meta.MetaKit;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This is a common implementation of {@link MapMetaManager.Handler}, and it is the default handler of
 * {@link MapMetaManager#defaultManager()}.
 * <p>
 * This implementation introspects the {@link Type} object to {@link MapMeta}.
 * <p>
 * Using {@link #getInstance()} can get a same one instance of this handler.
 *
 * @author sunqian
 */
public class CommonMapMetaHandler implements MapMetaManager.Handler {

    private static final @Nonnull CommonMapMetaHandler INST = new CommonMapMetaHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull CommonMapMetaHandler getInstance() {
        return INST;
    }

    @Override
    public @Nullable MapMeta newMapMeta(@Nonnull Type type, @Nonnull MapMetaManager manager) throws Exception {
        return new MapMetaImpl(type, manager);
    }

    private static final class MapMetaImpl implements MapMeta {

        private final @Nonnull Type type;
        private final @Nonnull Type keyType;
        private final @Nonnull Type valueType;
        private final @Nonnull MapMetaManager manager;

        private MapMetaImpl(@Nonnull Type type, @Nonnull MapMetaManager manager) throws ReflectionException {
            this.manager = manager;
            // if (type instanceof MapType) {
            //     @SuppressWarnings("PatternVariableCanBeUsed")
            //     MapType mapType = (MapType) type;
            //     this.type = mapType.mapType();
            //     this.keyType = mapType.keyType();
            //     this.valueType = mapType.valueType();
            // } else {
            this.type = type;
            List<Type> actualTypes = TypeKit.resolveActualTypeArguments(type, Map.class);
            this.keyType = actualTypes.get(0);
            this.valueType = actualTypes.get(1);
            //}
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull MapMetaManager manager() {
            return manager;
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
