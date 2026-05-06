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
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cacheFunction,
        @Nonnull @RetainedParam List<MapMetaManager.@Nonnull Handler> handlers
    ) {
        return new MapMetaManagerImpl(cacheFunction, handlers);
    }

    private static final class MapMetaManagerImpl implements MapMetaManager, MapMetaManager.Handler {

        private static final @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapMeta> GLOBAL_CACHE =
            SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(GLOBAL_CACHE);
        }

        private static final @Nonnull MapMetaManager DEFAULT = new MapMetaManagerImpl(
            GLOBAL_CACHE,
            ListKit.list(CommonMapMetaHandler.getInstance())
        );

        private final @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cache;
        private final @Nonnull List<@Nonnull Handler> handlers;

        private MapMetaManagerImpl(
            @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cache,
            @Nonnull @RetainedParam List<@Nonnull Handler> handlers
        ) {
            this.cache = cache;
            this.handlers = handlers;
        }

        @Override
        public @Nonnull MapMeta introspect(@Nonnull Type type) throws DataMetaException {
            return cache.get(type, this::introspect0);
        }

        private @Nonnull MapMeta introspect0(@Nonnull Type type) throws DataMetaException {
            MapMeta mapMeta;
            try {
                mapMeta = introspect(type, this);
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
        public @Nullable MapMeta introspect(@Nonnull Type type, @Nonnull MapMetaManager manager) throws Exception {
            for (Handler handler : handlers) {
                MapMeta mapMeta = handler.introspect(type, manager);
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
