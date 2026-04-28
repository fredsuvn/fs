package space.sunqian.fs.object.builder;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.builder.handlers.CommonBuilderHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class BuilderBack {

    static @Nonnull BuilderManager defaultManager() {
        return BuilderManagerImpl.DEFAULT;
    }

    static @Nonnull BuilderManager newManager(
        @Nonnull @RetainedParam List<BuilderManager.@Nonnull Handler> handlers,
        @Nonnull CacheFunction<@Nonnull Type, @Nullable BuilderOperator> cacheFunction
    ) {
        return new BuilderManagerImpl(handlers, cacheFunction);
    }

    static @Nullable BuilderOperator operatorForType(
        @Nonnull Type target,
        @Nonnull List<BuilderManager.@Nonnull Handler> handlers
    ) throws Exception {
        for (BuilderManager.Handler handler : handlers) {
            BuilderOperator operator = handler.newOperator(target);
            if (operator != null) {
                return operator;
            }
        }
        return null;
    }

    private static final class BuilderManagerImpl implements BuilderManager, BuilderManager.Handler {

        private static final @Nonnull SimpleCache<@Nonnull Type, @Nullable BuilderOperator> DEFAULT_CACHE =
            SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(DEFAULT_CACHE);
        }

        private static final @Nonnull BuilderManager DEFAULT = new BuilderManagerImpl(
            FsLoader.loadInstances(
                FsLoader.loadClassByDependent(
                    ThirdKit.thirdClassName("protobuf", "ProtobufCreatorHandler"),
                    "com.google.protobuf.Message"
                ),
                CommonBuilderHandler.getInstance()
            ),
            DEFAULT_CACHE
        );

        private final @Nonnull List<@Nonnull Handler> handlers;
        private final @Nonnull CacheFunction<@Nonnull Type, @Nullable BuilderOperator> cacheFunction;

        private BuilderManagerImpl(
            @Nonnull @RetainedParam List<@Nonnull Handler> handlers,
            @Nonnull CacheFunction<@Nonnull Type, @Nullable BuilderOperator> cacheFunction
        ) {
            this.handlers = handlers;
            this.cacheFunction = cacheFunction;
        }

        @Override
        public @Nullable BuilderOperator getOperator(@Nonnull Type target) throws ObjectBuilderException {
            return cacheFunction.get(target, t -> {
                try {
                    return newOperator(t);
                } catch (Exception e) {
                    throw new ObjectBuilderException(e);
                }
            });
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
        public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
            return operatorForType(target, handlers);
        }
    }

    private BuilderBack() {
    }
}
