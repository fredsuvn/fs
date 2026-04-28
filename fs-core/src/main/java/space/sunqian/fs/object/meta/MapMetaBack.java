package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

final class MapMetaBack {

    static @Nonnull MapMetaManager defaultManager() {
        return MapMetaManagerImpl.DEFAULT;
    }

    // static @Nonnull MapMetaManager newCachedParser(
    //     @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapMeta> cache,
    //     @Nonnull MapMetaManager parser
    // ) {
    //     return new CachedMapMetaManager(cache, parser);
    // }

    private static final class MapMetaManagerImpl implements MapMetaManager {

        private static final @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapMeta> DEFAULT_CACHE =
            SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(DEFAULT_CACHE);
        }

        private static final @Nonnull MapMetaManager DEFAULT = new MapMetaManagerImpl(DEFAULT_CACHE);

        private final @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cache;

        private MapMetaManagerImpl(@Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cache) {
            this.cache = cache;
        }

        @Override
        public @Nonnull MapMeta introspect(@Nonnull Type type) throws DataMetaException {
            return cache.get(type, this::introspect0);
        }

        private @Nonnull MapMeta introspect0(@Nonnull Type type) throws DataMetaException {
            try {
                return new MapMetaImpl(type);
            } catch (Exception e) {
                throw new DataMetaException(e);
            }
        }

        private final class MapMetaImpl implements MapMeta {

            private final @Nonnull Type type;
            private final @Nonnull Type keyType;
            private final @Nonnull Type valueType;

            private MapMetaImpl(@Nonnull Type type) throws ReflectionException {
                if (type instanceof MapType) {
                    @SuppressWarnings("PatternVariableCanBeUsed")
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
            public @Nonnull MapMetaManager manager() {
                return MapMetaManagerImpl.this;
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

    private MapMetaBack() {
    }
}
