package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.lang.reflect.Constructor;
import java.util.List;
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
 * It contains and uses a list of {@link Handler}s to sequentially attempt creating new builder or building target
 * object. The thread safety of the methods in this interface is determined by its dependent {@link ConstructorCache}.
 * By default, they are thread-safe.
 */
public interface DataBuilderFactory {

    /**
     * Returns the default data builder factory.
     * <p>
     * The default data builder factory will cache the constructors created into a {@link ConcurrentHashMap} if needed.
     * And it has only one handler, which uses {@link Invocable#of(Constructor)} with the empty constructor (if it has)
     * of the target class to implement {@link Handler#newConstructor(Class)} and directly returns the builder object
     * itself on {@link Handler#build(Object)}.
     *
     * @return the default data builder factory
     */
    static @Nonnull DataBuilderFactory defaultFactory() {
        return DataBuilderFactoryImpl.DEFAULT;
    }

    /**
     * Creates and returns a new {@link DataBuilderFactory} with the given constructor cache and handlers.
     *
     * @param cache    the given constructor cache
     * @param handlers the given handlers
     * @return a new {@link DataBuilderFactory} with the given constructor cache and handlers
     */
    static @Nonnull DataBuilderFactory newFactory(
        @Nonnull ConstructorCache cache,
        @Nonnull @RetainedParam Handler @Nonnull ... handlers
    ) {
        return newFactory(cache, ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link DataBuilderFactory} with the given constructor cache and handlers.
     *
     * @param cache    the given constructor cache
     * @param handlers the given handlers
     * @return a new {@link DataBuilderFactory} with the given constructor cache and handlers
     */
    static @Nonnull DataBuilderFactory newFactory(
        @Nonnull ConstructorCache cache,
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers
    ) {
        return new DataBuilderFactoryImpl(cache, handlers);
    }

    /**
     * Returns a new constructor cache with the given map. The thread safety is determined by the given map.
     *
     * @param map the given map
     * @return a new constructor cache with the given map
     */
    static @Nonnull DataBuilderFactory.ConstructorCache newConstructorCache(
        @Nonnull Map<@Nonnull Class<?>, @Nonnull Invocable> map
    ) {
        return new DataBuilderFactoryImpl.ConstructorCacheImpl(map);
    }

    /**
     * Creates and returns a new builder object, or {@code null} if the target type is unsupported.
     *
     * @param target the target type
     * @return a new builder object, or {@code null} if the target type is unsupported
     * @throws ObjectConvertException if failed to create a new builder
     */
    @Nullable
    Object newBuilder(@Nonnull Class<?> target) throws ObjectConvertException;

    /**
     * Makes the given builder, which is returned by {@link #newBuilder(Class)} of the same instance of
     * {@link DataBuilderFactory}, generate the target object.
     *
     * @param builder the builder from {@link #newBuilder(Class)}
     * @return the target object
     * @throws ObjectConvertException if failed to build the target object
     */
    @Nonnull
    Object build(@Nonnull Object builder) throws ObjectConvertException;

    /**
     * Returns all handlers of this builder factory.
     *
     * @return all handlers of this builder factory
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link DataBuilderFactory} of which handler list consists of the given handler as the first
     * element, followed by {@link #handlers()} of the current builder factory.
     * <p>
     * The constructor cache will be shared with the current builder factory.
     *
     * @param handler the given handler
     * @return a new {@link DataBuilderFactory} of which handler list consists of the given handler as the first
     * element, followed by {@link #handlers()} of the current builder factory
     */
    @Nonnull
    DataBuilderFactory withFirstHandler(@Nonnull Handler handler);

    /**
     * Returns a new {@link DataBuilderFactory} of which handler list consists of {@link #handlers()} of the current
     * builder factory, followed by the given handler as the last element.
     * <p>
     * The constructor cache will be shared with the current builder factory.
     *
     * @param handler the given handler
     * @return a {@link DataBuilderFactory} of which handler list consists of {@link #handlers()} of the current builder
     * factory, followed by the given handler as the last element
     */
    @Nonnull
    DataBuilderFactory withLastHandler(@Nonnull Handler handler);

    /**
     * Returns this builder factory as a {@link Handler}.
     *
     * @return this builder factory as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Cache for constructors created during the building process.
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

    /**
     * Handler for {@link DataBuilderFactory}, provides the specific generating logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Creates and returns a new builder constructor, or {@code null} if the target type is unsupported.
         *
         * @param target the target type
         * @return a new builder constructor, or {@code null} if the target type is unsupported
         * @throws Exception for any exception
         */
        @Nullable
        Invocable newConstructor(@Nonnull Class<?> target) throws Exception;

        /**
         * Makes the given builder generate the target object, or {@code null} if the given builder is unsupported.
         *
         * @param builder the given builder
         * @return the object built by the given builder and this handler, or {@code null} if the given builder is
         * unsupported
         * @throws Exception for any exception
         */
        @Nullable
        Object build(@Nonnull Object builder) throws Exception;
    }
}
