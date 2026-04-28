package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.meta.handlers.CommonMetaHandler;
import space.sunqian.fs.object.meta.handlers.RecordMetaHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class ObjectMetaBack {

    static @Nonnull ObjectMetaManager defaultParser() {
        return ObjectMetaManagerImpl.DEFAULT;
    }


    static @Nonnull ObjectMetaManager defaultCachedParser() {
        return CachedObjectMetaManager.DEFAULT;
    }

    static @Nonnull ObjectMetaManager newParser(
        @Nonnull @RetainedParam List<ObjectMetaManager.@Nonnull Handler> handlers
    ) {
        return new ObjectMetaManagerImpl(handlers);
    }

    static @Nonnull ObjectMetaBack.CachedObjectMetaManager newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectMeta> cache,
        @Nonnull ObjectMetaManager parser
    ) {
        return new CachedObjectMetaManager(cache, parser);
    }

    private static final class ObjectMetaManagerImpl implements ObjectMetaManager, ObjectMetaManager.Handler {

        private static final @Nonnull ObjectMetaBack.ObjectMetaManagerImpl DEFAULT = new ObjectMetaManagerImpl(FsLoader.loadInstances(
            FsLoader.loadClassByDependent(
                ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler"),
                "com.google.protobuf.Message"
            ),
            FsLoader.supplyByDependent(
                RecordMetaHandler::getInstance, RecordMetaHandler.class.getName() + "ImplByJ16"
            ),
            CommonMetaHandler.getInstance()
        ));

        private final @Nonnull List<@Nonnull Handler> handlers;

        private ObjectMetaManagerImpl(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
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

    private static final class CachedObjectMetaManager implements ObjectMetaManager {

        private static final @Nonnull ObjectMetaBack.CachedObjectMetaManager DEFAULT = newCachedParser(
            SimpleCache.ofSoft(),
            ObjectMetaManager.defaultParser()
        );

        private final @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectMeta> cache;
        private final @Nonnull ObjectMetaManager parser;

        private CachedObjectMetaManager(
            @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectMeta> cache,
            @Nonnull ObjectMetaManager parser
        ) {
            this.cache = cache;
            this.parser = parser;
        }

        @Override
        public @Nonnull ObjectMeta parse(@Nonnull Type type) throws DataMetaException {
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

    private ObjectMetaBack() {
    }
}
