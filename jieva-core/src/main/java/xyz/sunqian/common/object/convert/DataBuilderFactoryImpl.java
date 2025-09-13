package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class DataBuilderFactoryImpl implements DataBuilderFactory {

    static final @Nonnull DataBuilderFactory SINGLETON =
        new DataBuilderFactoryImpl(new ConstructorCacheImpl(new ConcurrentHashMap<>()));

    private final @Nonnull ConstructorCache constructorCache;

    DataBuilderFactoryImpl(@Nonnull ConstructorCache constructorCache) {
        this.constructorCache = constructorCache;
    }

    @Override
    public @Nullable Object newBuilder(@Nonnull Class<?> target) throws Exception {
        Invocable instantiator = constructorCache.get(target, t -> {
            try {
                return Invocable.of(t.getConstructor());
            } catch (NoSuchMethodException e) {
                throw new ObjectConvertException(e);
            }
        });
        return instantiator.invoke(null);
    }

    @Override
    public @Nonnull Object build(@Nonnull Object builder) throws Exception {
        return builder;
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
}
