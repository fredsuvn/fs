package space.sunqian.fs.object;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.reflect.TypeKit;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

final class ObjectCreatorProviderImpl implements ObjectCreatorProvider, ObjectCreatorProvider.Handler {

    static final @Nonnull ObjectCreatorProvider.Handler DEFAULT_HANDLER = DefaultHandler.INST;

    static final @Nonnull ObjectCreatorProvider DEFAULT = new ObjectCreatorProviderImpl(
        CacheFunction.ofMap(new ConcurrentHashMap<>()),
        FsLoader.loadInstances(
            FsLoader.loadClassByDependent(
                ThirdKit.thirdClassName("protobuf", "ProtobufCreatorHandler"),
                "com.google.protobuf.Message"
            ),
            DEFAULT_HANDLER
        )
    );

    private final @Nonnull CacheFunction<@Nonnull Type, @Nullable ObjectCreator> cache;
    private final @Nonnull List<@Nonnull Handler> handlers;

    ObjectCreatorProviderImpl(
        @Nonnull CacheFunction<@Nonnull Type, @Nullable ObjectCreator> cache,
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers
    ) {
        this.cache = cache;
        this.handlers = handlers;
    }

    @Override
    public @Nullable ObjectCreator creatorForType(@Nonnull Type target) throws ObjectException {
        return cache.get(target, ObjectCreatorProvider.super::creatorForType);
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
        return ObjectCreatorProvider.super.creatorForType(target);
    }

    private enum DefaultHandler implements Handler {

        INST;

        @Override
        public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
            Class<?> rawTarget = TypeKit.getRawClass(target);
            if (rawTarget == null) {
                return null;
            }
            try {
                Constructor<?> cst = rawTarget.getConstructor();
                return new SimpleCreator(target, cst);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
    }

    private static final class SimpleCreator implements ObjectCreator {

        private final @Nonnull Type targetType;
        private final @Nonnull Invocable constructor;

        private SimpleCreator(
            @Nonnull Type targetType,
            @Nonnull Constructor<?> constructor
        ) {
            this.targetType = targetType;
            this.constructor = Invocable.of(constructor);
        }

        @Override
        public @Nonnull Type builderType() {
            return targetType;
        }

        @Override
        public @Nonnull Type targetType() {
            return targetType;
        }

        @Override
        public @Nonnull Object createBuilder() throws ObjectException {
            try {
                return constructor.invoke(null);
            } catch (Exception e) {
                throw new ObjectException(e);
            }
        }

        @Override
        public @Nonnull Object createTarget(@Nonnull Object builder) throws ObjectException {
            return builder;
        }
    }
}
