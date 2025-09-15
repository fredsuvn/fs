package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class DataBuilderFactoryImpl implements DataBuilderFactory, DataBuilderFactory.Handler {

    static final @Nonnull DataBuilderFactory DEFAULT = new DataBuilderFactoryImpl(
        new ConstructorCacheImpl(new ConcurrentHashMap<>()),
        Collections.singletonList(DefaultHandler.INST)
    );

    private final @Nonnull DataBuilderFactory.ConstructorCache constructorCache;
    private final @Nonnull List<DataBuilderFactory.@Nonnull Handler> handlers;

    DataBuilderFactoryImpl(
        @Nonnull DataBuilderFactory.ConstructorCache constructorCache,
        @Nonnull List<@Nonnull Handler> handlers
    ) {
        this.constructorCache = constructorCache;
        this.handlers = handlers;
    }

    @Override
    public @Nullable Object newBuilder(@Nonnull Class<?> target) {
        Invocable instantiator = constructorCache.get(target, t -> {
            try {
                for (Handler handler : handlers) {
                    Invocable constructor = handler.newConstructor(t);
                    if (constructor != null) {
                        return constructor;
                    }
                }
                return UnsupportedConstructor.INST;
            } catch (Exception e) {
                throw new ObjectConvertException(e);
            }
        });
        return instantiator.invoke(null);
    }

    @Override
    public @Nullable Invocable newConstructor(@Nonnull Class<?> target) throws Exception {
        for (Handler handler : handlers) {
            Invocable constructor = handler.newConstructor(target);
            if (constructor != null) {
                return constructor;
            }
        }
        return null;
    }

    @Override
    public @Nonnull Object build(@Nonnull Object builder) throws ObjectConvertException {
        try {
            for (Handler handler : handlers) {
                Object result = handler.build(builder);
                if (result != null) {
                    return result;
                }
            }
            throw new UnsupportedOperationException(builder.getClass().getTypeName());
        } catch (Exception e) {
            throw new ObjectConvertException(e);
        }
    }

    @Override
    public @Nonnull List<@Nonnull Handler> handlers() {
        return handlers;
    }

    public @Nonnull DataBuilderFactory withFirstHandler(@Nonnull Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        newHandlers[i++] = handler;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        return new DataBuilderFactoryImpl(constructorCache, ListKit.list(newHandlers));
    }

    public @Nonnull DataBuilderFactory withLastHandler(@Nonnull Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        newHandlers[i] = handler;
        return new DataBuilderFactoryImpl(constructorCache, ListKit.list(newHandlers));
    }

    @Override
    public @Nonnull Handler asHandler() {
        return this;
    }

    static final class ConstructorCacheImpl implements ConstructorCache {

        private final @Nonnull Map<@Nonnull Class<?>, @Nonnull Invocable> map;

        ConstructorCacheImpl(@Nonnull Map<@Nonnull Class<?>, @Nonnull Invocable> map) {
            this.map = map;
        }

        @Override
        public @Nonnull Invocable get(
            @Nonnull Class<?> type,
            @Nonnull Function<? super @Nonnull Class<?>, ? extends @Nonnull Invocable> loader
        ) throws ObjectConvertException {
            return map.computeIfAbsent(type, loader);
        }
    }

    private enum DefaultHandler implements Handler {

        INST;

        @Override
        public @Nullable Invocable newConstructor(@Nonnull Class<?> target) {
            try {
                Constructor<?> emptyCOnstructor = target.getConstructor();
                return Invocable.of(emptyCOnstructor);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public @Nonnull Object build(@Nonnull Object builder) {
            return builder;
        }
    }

    private enum UnsupportedConstructor implements Invocable {

        INST;

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) {
            return null;
        }
    }
}
