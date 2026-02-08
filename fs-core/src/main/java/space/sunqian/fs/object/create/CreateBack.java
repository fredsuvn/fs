package space.sunqian.fs.object.create;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.create.handlers.CommonCreatorHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class CreateBack {

    static @Nonnull CreatorProvider defaultProvider() {
        return CreatorProviderImpl.DEFAULT;
    }

    static @Nonnull CreatorProvider newProvider(
        @Nonnull @RetainedParam List<CreatorProvider.@Nonnull Handler> handlers
    ) {
        return new CreatorProviderImpl(handlers);
    }

    static @Nonnull CreatorProvider cachedProvider(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectCreator> cache,
        @Nonnull CreatorProvider provider
    ) {
        return new CachedProvider(cache, provider);
    }

    static @Nullable ObjectCreator creatorForType(
        @Nonnull Type target,
        @Nonnull List<CreatorProvider.@Nonnull Handler> handlers
    ) throws Exception {
        for (CreatorProvider.Handler handler : handlers) {
            ObjectCreator creator = handler.newCreator(target);
            if (creator != null) {
                return creator;
            }
        }
        return null;
    }

    private static final class CreatorProviderImpl implements CreatorProvider, CreatorProvider.Handler {

        private static final @Nonnull CreatorProvider DEFAULT = new CreatorProviderImpl(
            FsLoader.loadInstances(
                FsLoader.loadClassByDependent(
                    ThirdKit.thirdClassName("protobuf", "ProtobufCreatorHandler"),
                    "com.google.protobuf.Message"
                ),
                CommonCreatorHandler.getInstance()
            )
        );

        private final @Nonnull List<@Nonnull Handler> handlers;

        private CreatorProviderImpl(
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
        public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
            return CreateBack.creatorForType(target, handlers);
        }
    }

    private static final class CachedProvider implements CreatorProvider {

        private final @Nonnull SimpleCache<@Nonnull Type, @Nullable ObjectCreator> cache;
        private final @Nonnull CreatorProvider provider;

        private CachedProvider(
            @Nonnull SimpleCache<@Nonnull Type, @Nullable ObjectCreator> cache,
            @Nonnull CreatorProvider provider
        ) {
            this.cache = cache;
            this.provider = provider;
        }

        @Override
        public @Nullable ObjectCreator forType(@Nonnull Type target) throws ObjectCreateException {
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

    private CreateBack() {
    }
}
