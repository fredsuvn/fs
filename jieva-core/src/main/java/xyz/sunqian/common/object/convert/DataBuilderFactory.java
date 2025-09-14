package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This interface is used to construct a data object with the target type through the following methods:
 * <ol>
 *     <li>
 *         Calls {@link #newBuilder(Class)} to create a builder for the target type.
 *     </li>
 *     <li>
 *         Sets the properties in the builder.
 *     </li>
 *     <li>
 *         Calls {@link #build(Object)} to make the builder generate the target object.
 *     </li>
 * </ol>
 * Note the builder object and the target object may be the same one.
 */
public interface DataBuilderFactory {

    /**
     * Returns the default data builder factory.
     * <p>
     * The default data builder factory will cache the constructors created into a {@link ConcurrentHashMap} if needed.
     *
     * @return the default data builder factory
     */
    static @Nonnull DataBuilderFactory defaultFactory() {
        return DataBuilderFactoryImpl.DEFAULT;
    }

    /**
     * Returns a new data builder factory with the given constructor cache.
     *
     * @param constructorCache the given constructor cache
     * @return a new data builder factory with the given constructor cache
     */
    static @Nonnull DataBuilderFactory newFactory(@Nonnull ConstructorCache constructorCache) {
        return new DataBuilderFactoryImpl(constructorCache);
    }

    /**
     * Returns a new data builder factory with the given map as constructor cache.
     *
     * @param map the given map as constructor cache
     * @return a new data builder factory with the given map as constructor cache.
     */
    static @Nonnull DataBuilderFactory newFactory(@Nonnull Map<@Nonnull Class<?>, @Nonnull Invocable> map) {
        return newFactory(newConstructorCache(map));
    }

    /**
     * Returns a new constructor cache with the given map.
     *
     * @param map the given map
     * @return a new constructor cache with the given map
     */
    static @Nonnull ConstructorCache newConstructorCache(@Nonnull Map<@Nonnull Class<?>, @Nonnull Invocable> map) {
        return new DataBuilderFactoryImpl.ConstructorCacheImpl(map);
    }

    /**
     * Creates and returns a new builder, or {@code null} if the target type is unsupported.
     *
     * @param target the target type
     * @return a new builder object, or {@code null} if the target type is unsupported
     * @throws Exception for any exception
     */
    @Nullable
    Object newBuilder(@Nonnull Class<?> target) throws Exception;

    /**
     * Makes the given builder, which is returned by {@link #newBuilder(Class)}, generate the target object.
     *
     * @param builder the builder from {@link #newBuilder(Class)}
     * @return the target object
     * @throws Exception for any exception
     */
    @Nonnull
    Object build(@Nonnull Object builder) throws Exception;

    /**
     * Cache for constructors created during the instantiating process.
     */
    interface ConstructorCache {

        /**
         * Returns the constructor for the given type. If the constructor is not cached, it will be loaded by the given
         * loader. The semantics of this method are the same as {@link Map#computeIfAbsent(Object, Function)}.
         *
         * @param type   the given type to be parsed to {@link Invocable} as constructor
         * @param loader the loader for loading new {@link Invocable}
         * @return the {@link Invocable} as constructor for the given type
         * @throws ObjectConvertException if an error occurs during parsing
         */
        @Nonnull
        Invocable get(
            @Nonnull Class<?> type,
            @Nonnull Function<? super @Nonnull Class<?>, ? extends @Nonnull Invocable> loader
        ) throws ObjectConvertException;
    }
}
