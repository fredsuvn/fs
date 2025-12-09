package space.sunqian.common.object.data;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.FsLoader;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.invoke.Invocable;
import space.sunqian.common.invoke.InvocationException;
import space.sunqian.common.reflect.TypeKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class ObjectBuilderProviderImpl implements ObjectBuilderProvider, ObjectBuilderProvider.Handler {

    static final @Nonnull ObjectBuilderProvider DEFAULT = new ObjectBuilderProviderImpl(
        new BuilderCacheImpl(new ConcurrentHashMap<>()),
        // Collections.singletonList(DefaultHandler.INST)
        FsLoader.loadInstances(
            FsLoader.loadClassByDependent(
                "space.sunqian.common.third.protobuf.ProtobufBuilderHandler",
                "com.google.protobuf.Message"
            ),
            DefaultHandler.INST
        )
    );

    private final @Nonnull ObjectBuilderProvider.BuilderCache builderCache;
    private final @Nonnull List<ObjectBuilderProvider.@Nonnull Handler> handlers;

    ObjectBuilderProviderImpl(
        @Nonnull ObjectBuilderProvider.BuilderCache builderCache,
        @Nonnull List<@Nonnull Handler> handlers
    ) {
        this.builderCache = builderCache;
        this.handlers = handlers;
    }

    @Override
    public @Nullable ObjectBuilder builder(@Nonnull Type target) throws DataObjectException {
        return builderCache.get(target, t -> {
            try {
                for (Handler handler : handlers) {
                    ObjectBuilder ob = handler.newBuilder(t);
                    if (ob != null) {
                        return ob;
                    }
                }
                return null;
            } catch (Exception e) {
                throw new DataObjectException(e);
            }
        });
    }

    @Override
    public @Nullable ObjectBuilder newBuilder(@Nonnull Type target) {
        return builder(target);
    }

    @Override
    public @Nonnull List<@Nonnull Handler> handlers() {
        return handlers;
    }

    public @Nonnull ObjectBuilderProvider withFirstHandler(@Nonnull Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        newHandlers[i++] = handler;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        return new ObjectBuilderProviderImpl(builderCache, ListKit.list(newHandlers));
    }

    public @Nonnull ObjectBuilderProvider withLastHandler(@Nonnull Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        newHandlers[i] = handler;
        return new ObjectBuilderProviderImpl(builderCache, ListKit.list(newHandlers));
    }

    @Override
    public @Nonnull Handler asHandler() {
        return this;
    }

    static final class BuilderCacheImpl implements BuilderCache {

        private final @Nonnull Map<@Nonnull Type, @Nonnull ObjectBuilder> map;

        BuilderCacheImpl(@Nonnull Map<@Nonnull Type, @Nonnull ObjectBuilder> map) {
            this.map = map;
        }

        @Override
        public @Nullable ObjectBuilder get(
            @Nonnull Type target,
            @Nonnull Function<? super @Nonnull Type, ? extends @Nullable ObjectBuilder> loader
        ) throws DataObjectException {
            return map.computeIfAbsent(target, loader);
        }
    }

    private enum DefaultHandler implements Handler {

        INST;

        @Override
        public @Nullable ObjectBuilder newBuilder(@Nonnull Type target) throws Exception {
            Class<?> rawTarget = TypeKit.getRawClass(target);
            if (rawTarget == null) {
                return null;
            }
            try {
                Constructor<?> cst = rawTarget.getConstructor();
                return new SimpleBuilder(cst, target);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
    }

    private static final class SimpleBuilder implements ObjectBuilder {

        private final @Nonnull Invocable constructor;
        private final @Nonnull Type target;

        private SimpleBuilder(@Nonnull Constructor<?> constructor, @Nonnull Type target) {
            this.constructor = Invocable.of(constructor);
            this.target = target;
        }

        @Override
        public @Nonnull Object newBuilder() throws DataObjectException {
            try {
                return constructor.invoke(null);
            } catch (InvocationException e) {
                throw new DataObjectException(e.getCause());
            }
        }

        @Override
        public @Nonnull Type builderType() {
            return target;
        }

        @Override
        public @Nonnull Object build(@Nonnull Object builder) throws DataObjectException {
            return builder;
        }
    }
}
