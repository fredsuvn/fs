package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.meta.handlers.CommonObjectMetaHandler;
import space.sunqian.fs.object.meta.handlers.RecordMetaHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class ObjectMetaBack {

    static @Nonnull ObjectMetaManager defaultManager() {
        return ObjectMetaManagerImpl.DEFAULT;
    }

    static @Nonnull ObjectMetaManager newManager(
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull ObjectMeta> cache,
        @Nonnull @RetainedParam List<ObjectMetaManager.@Nonnull Handler> handlers
    ) {
        return new ObjectMetaManagerImpl(cache, handlers);
    }

    private static final class ObjectMetaManagerImpl implements ObjectMetaManager, ObjectMetaManager.Handler {

        private static final @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectMeta> GLOBAL_CACHE =
            SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(GLOBAL_CACHE);
        }

        private static final @Nonnull ObjectMetaBack.ObjectMetaManagerImpl DEFAULT = new ObjectMetaManagerImpl(
            GLOBAL_CACHE,
            FsLoader.loadInstances(
                FsLoader.loadClassByDependent(
                    ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler"),
                    "com.google.protobuf.Message"
                ),
                FsLoader.supplyByDependent(
                    RecordMetaHandler::getInstance, RecordMetaHandler.class.getName() + "ImplByJ16"
                ),
                CommonObjectMetaHandler.getInstance()
            )
        );

        private final @Nonnull CacheFunction<@Nonnull Type, @Nonnull ObjectMeta> cache;
        private final @Nonnull List<@Nonnull Handler> handlers;

        private ObjectMetaManagerImpl(
            @Nonnull CacheFunction<@Nonnull Type, @Nonnull ObjectMeta> cache,
            @Nonnull @RetainedParam List<@Nonnull Handler> handlers
        ) {
            this.handlers = handlers;
            this.cache = cache;
        }

        @Override
        public @Nonnull ObjectMeta introspect(@Nonnull Type type) throws DataMetaException {
            return cache.get(type, this::introspect0);
        }

        private @Nonnull ObjectMeta introspect0(@Nonnull Type type) throws DataMetaException {
            return ObjectMetaManager.super.introspect(type);
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
        public boolean introspect(@Nonnull Context context) throws Exception {
            for (Handler handler : handlers) {
                if (!handler.introspect(context)) {
                    return false;
                }
            }
            return true;
        }
    }

    private ObjectMetaBack() {
    }
}
