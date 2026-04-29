package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.meta.handlers.CommonMapMetaHandler;

import java.lang.reflect.Type;
import java.util.List;

final class MapMetaBack {

    static @Nonnull MapMetaManager defaultManager() {
        return MapMetaManagerImpl.DEFAULT;
    }

    static @Nonnull MapMetaManager newManager(
        @Nonnull @RetainedParam List<MapMetaManager.@Nonnull Handler> handlers,
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cacheFunction
    ) {
        return new MapMetaManagerImpl(handlers, cacheFunction);
    }

    private static final class MapMetaManagerImpl implements MapMetaManager, MapMetaManager.Handler {

        private static final @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapMeta> DEFAULT_CACHE =
            SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(DEFAULT_CACHE);
        }

        private static final @Nonnull MapMetaManager DEFAULT = new MapMetaManagerImpl(
            ListKit.list(CommonMapMetaHandler.getInstance()),
            DEFAULT_CACHE
        );

        private final @Nonnull List<@Nonnull Handler> handlers;
        private final @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cache;

        private MapMetaManagerImpl(
            @Nonnull @RetainedParam List<@Nonnull Handler> handlers,
            @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cache) {
            this.handlers = handlers;
            this.cache = cache;
        }

        @Override
        public @Nonnull MapMeta introspect(@Nonnull Type type) throws DataMetaException {
            return cache.get(type, this::introspect0);
        }

        private @Nonnull MapMeta introspect0(@Nonnull Type type) throws DataMetaException {
            MapMeta mapMeta;
            try {
                mapMeta = newMapMeta(type, this);
            } catch (Exception e) {
                throw new DataMetaException(e);
            }
            if (mapMeta == null) {
                throw new DataMetaException(type);
            }
            return mapMeta;
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
        public @Nullable MapMeta newMapMeta(@Nonnull Type type, @Nonnull MapMetaManager manager) throws Exception {
            for (Handler handler : handlers) {
                MapMeta mapMeta = handler.newMapMeta(type, manager);
                if (mapMeta != null) {
                    return mapMeta;
                }
            }
            return null;
        }
    }

    private MapMetaBack() {
    }
}
