package space.sunqian.fs.object.builder;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.builder.handlers.CommonBuilderHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class BuilderOperatorProviderBack {

    static @Nonnull BuilderOperatorProvider defaultProvider() {
        return BuilderOperatorProviderImpl.DEFAULT;
    }

    static @Nonnull BuilderOperatorProvider defaultCachedProvider() {
        return CachedBuilderOperatorProvider.DEFAULT;
    }

    static @Nonnull BuilderOperatorProvider newProvider(
        @Nonnull @RetainedParam List<BuilderOperatorProvider.@Nonnull Handler> handlers
    ) {
        return new BuilderOperatorProviderImpl(handlers);
    }

    static @Nonnull BuilderOperatorProviderBack.CachedBuilderOperatorProvider newCachedProvider(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull BuilderOperator> cache,
        @Nonnull BuilderOperatorProvider provider
    ) {
        return new CachedBuilderOperatorProvider(cache, provider);
    }

    static @Nullable BuilderOperator operatorForType(
        @Nonnull Type target,
        @Nonnull List<BuilderOperatorProvider.@Nonnull Handler> handlers
    ) throws Exception {
        for (BuilderOperatorProvider.Handler handler : handlers) {
            BuilderOperator operator = handler.newOperator(target);
            if (operator != null) {
                return operator;
            }
        }
        return null;
    }

    private static final class BuilderOperatorProviderImpl implements BuilderOperatorProvider, BuilderOperatorProvider.Handler {

        private static final @Nonnull BuilderOperatorProvider DEFAULT = new BuilderOperatorProviderImpl(
            FsLoader.loadInstances(
                FsLoader.loadClassByDependent(
                    ThirdKit.thirdClassName("protobuf", "ProtobufCreatorHandler"),
                    "com.google.protobuf.Message"
                ),
                CommonBuilderHandler.getInstance()
            )
        );

        private final @Nonnull List<@Nonnull Handler> handlers;

        private BuilderOperatorProviderImpl(
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
        public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
            return BuilderOperatorProviderBack.operatorForType(target, handlers);
        }
    }

    private static final class CachedBuilderOperatorProvider implements BuilderOperatorProvider {

        private static final @Nonnull BuilderOperatorProviderBack.CachedBuilderOperatorProvider DEFAULT = newCachedProvider(
            SimpleCache.ofSoft(),
            BuilderOperatorProvider.defaultProvider()
        );

        private final @Nonnull SimpleCache<@Nonnull Type, @Nullable BuilderOperator> cache;
        private final @Nonnull BuilderOperatorProvider provider;

        private CachedBuilderOperatorProvider(
            @Nonnull SimpleCache<@Nonnull Type, @Nullable BuilderOperator> cache,
            @Nonnull BuilderOperatorProvider provider
        ) {
            this.cache = cache;
            this.provider = provider;
        }

        @Override
        public @Nullable BuilderOperator forType(@Nonnull Type target) throws ObjectBuilderException {
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

    private BuilderOperatorProviderBack() {
    }
}
