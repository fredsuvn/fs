package space.sunqian.fs.object.build;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.build.handlers.CommonBuilderHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class BuilderProviderBack {

    static @Nonnull BuilderProvider defaultProvider() {
        return BuilderProviderImpl.DEFAULT;
    }

    static @Nonnull BuilderProvider defaultCachedProvider() {
        return CachedProvider.DEFAULT;
    }

    static @Nonnull BuilderProvider newProvider(
        @Nonnull @RetainedParam List<BuilderProvider.@Nonnull Handler> handlers
    ) {
        return new BuilderProviderImpl(handlers);
    }

    static @Nonnull CachedProvider newCachedProvider(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull BuilderExecutor> cache,
        @Nonnull BuilderProvider provider
    ) {
        return new CachedProvider(cache, provider);
    }

    static @Nullable BuilderExecutor executorForType(
        @Nonnull Type target,
        @Nonnull List<BuilderProvider.@Nonnull Handler> handlers
    ) throws Exception {
        for (BuilderProvider.Handler handler : handlers) {
            BuilderExecutor executor = handler.newExecutor(target);
            if (executor != null) {
                return executor;
            }
        }
        return null;
    }

    private static final class BuilderProviderImpl implements BuilderProvider, BuilderProvider.Handler {

        private static final @Nonnull BuilderProvider DEFAULT = new BuilderProviderImpl(
            FsLoader.loadInstances(
                FsLoader.loadClassByDependent(
                    ThirdKit.thirdClassName("protobuf", "ProtobufCreatorHandler"),
                    "com.google.protobuf.Message"
                ),
                CommonBuilderHandler.getInstance()
            )
        );

        private final @Nonnull List<@Nonnull Handler> handlers;

        private BuilderProviderImpl(
            @Nonnull @RetainedParam List<@Nonnull Handler> handlers
        ) {
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
        public @Nullable BuilderExecutor newExecutor(@Nonnull Type target) throws Exception {
            return BuilderProviderBack.executorForType(target, handlers);
        }
    }

    private static final class CachedProvider implements BuilderProvider {

        private static final @Nonnull CachedProvider DEFAULT = newCachedProvider(
            SimpleCache.ofSoft(),
            BuilderProvider.defaultProvider()
        );

        private final @Nonnull SimpleCache<@Nonnull Type, @Nullable BuilderExecutor> cache;
        private final @Nonnull BuilderProvider provider;

        private CachedProvider(
            @Nonnull SimpleCache<@Nonnull Type, @Nullable BuilderExecutor> cache,
            @Nonnull BuilderProvider provider
        ) {
            this.cache = cache;
            this.provider = provider;
        }

        @Override
        public @Nullable BuilderExecutor forType(@Nonnull Type target) throws ObjectBuildingException {
            return cache.get(target, provider::forType);
        }

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return provider.handlers();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return provider.asHandler();
        }
    }

    private BuilderProviderBack() {
    }
}
