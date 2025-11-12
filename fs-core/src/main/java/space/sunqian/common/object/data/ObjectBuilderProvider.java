package space.sunqian.common.object.data;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.annotations.ThreadSafe;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.runtime.invoke.Invocable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Provider for getting {@link ObjectBuilder}.
 * <p>
 * It contains and uses a list of {@link Handler}s to sequentially attempt get the instance of {@link ObjectBuilder}.
 * The thread safety of the methods in this interface is determined by its dependent {@link BuilderCache}. By default,
 * they are thread-safe.
 */
public interface ObjectBuilderProvider {

    /**
     * Returns the default {@link ObjectBuilderProvider}.
     * <p>
     * The default {@link ObjectBuilderProvider} will cache the {@link ObjectBuilder}s, which are created by its
     * handlers, into a cache based on a {@link ConcurrentHashMap} (so it is thread-safe). And it has only one handler,
     * which uses {@link Invocable#of(Constructor)} with the empty constructor of the target class (if it has) to
     * implement {@link ObjectBuilder#newBuilder()} and directly returns the builder object itself on
     * {@link ObjectBuilder#build(Object)}.
     *
     * @return the default {@link ObjectBuilderProvider}
     */
    static @Nonnull ObjectBuilderProvider defaultProvider() {
        return ObjectBuilderProviderImpl.DEFAULT;
    }

    /**
     * Creates and returns a new {@link ObjectBuilderProvider} with the given handlers. The returned
     * {@link ObjectBuilderProvider} has a {@link BuilderCache} based on a {@link ConcurrentHashMap}, so it is
     * thread-safe.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectBuilderProvider} with the given builder cache and handlers
     */
    static @Nonnull ObjectBuilderProvider newProvider(
        @Nonnull @RetainedParam Handler @Nonnull ... handlers
    ) {
        return newProvider(newBuilderCache(new ConcurrentHashMap<>()), ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectBuilderProvider} with the given builder cache and handlers.
     *
     * @param cache    the given builder cache
     * @param handlers the given handlers
     * @return a new {@link ObjectBuilderProvider} with the given builder cache and handlers
     */
    static @Nonnull ObjectBuilderProvider newProvider(
        @Nonnull BuilderCache cache,
        @Nonnull @RetainedParam Handler @Nonnull ... handlers
    ) {
        return newProvider(cache, ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectBuilderProvider} with the given builder cache and handlers.
     *
     * @param cache    the given builder cache
     * @param handlers the given handlers
     * @return a new {@link ObjectBuilderProvider} with the given builder cache and handlers
     */
    static @Nonnull ObjectBuilderProvider newProvider(
        @Nonnull BuilderCache cache,
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers
    ) {
        return new ObjectBuilderProviderImpl(cache, handlers);
    }

    /**
     * Returns a new builder cache with the given map. The thread safety is determined by the given map.
     *
     * @param map the given map
     * @return a new builder cache with the given map
     */
    static @Nonnull BuilderCache newBuilderCache(
        @Nonnull Map<@Nonnull Type, @Nonnull ObjectBuilder> map
    ) {
        return new ObjectBuilderProviderImpl.BuilderCacheImpl(map);
    }

    /**
     * Returns an instance of {@link ObjectBuilder}, or {@code null} if the target type is unsupported.
     *
     * @param target the target type
     * @return an instance of {@link ObjectBuilder}, or {@code null} if the target type is unsupported
     * @throws DataObjectException if an error occurs
     */
    @Nullable
    ObjectBuilder builder(@Nonnull Type target) throws DataObjectException;

    /**
     * Returns all handlers of this builder provider.
     *
     * @return all handlers of this builder provider
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link ObjectBuilderProvider} of which handler list consists of the given handler as the first
     * element, followed by {@link #handlers()} of the current builder provider.
     * <p>
     * The builder cache will be shared with the current builder provider.
     *
     * @param handler the given handler
     * @return a new {@link ObjectBuilderProvider} of which handler list consists of the given handler as the first
     * element, followed by {@link #handlers()} of the current builder provider
     */
    @Nonnull
    ObjectBuilderProvider withFirstHandler(@Nonnull Handler handler);

    /**
     * Returns a new {@link ObjectBuilderProvider} of which handler list consists of {@link #handlers()} of the current
     * builder provider, followed by the given handler as the last element.
     * <p>
     * The builder cache will be shared with the current builder provider.
     *
     * @param handler the given handler
     * @return a {@link ObjectBuilderProvider} of which handler list consists of {@link #handlers()} of the current
     * builder provider, followed by the given handler as the last element
     */
    @Nonnull
    ObjectBuilderProvider withLastHandler(@Nonnull Handler handler);

    /**
     * Returns this builder provider as a {@link Handler}.
     *
     * @return this builder provider as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Cache of {@link ObjectBuilder} for an instance of {@link ObjectBuilderProvider}.
     */
    interface BuilderCache {

        /**
         * Returns the {@link ObjectBuilder} for the target type. If the {@link ObjectBuilder} is not cached, it will be
         * loaded by the given loader. The semantics of this method are the same as
         * {@link Map#computeIfAbsent(Object, Function)}.
         *
         * @param target the target type
         * @param loader the loader for loading new {@link ObjectBuilder}
         * @return the {@link ObjectBuilder} for the target type, or {@code null} if no mapping and no loading for the
         * target type
         * @throws DataObjectException if an error occurs during loading
         */
        @Nullable
        ObjectBuilder get(
            @Nonnull Type target,
            @Nonnull Function<? super @Nonnull Type, ? extends @Nullable ObjectBuilder> loader
        ) throws DataObjectException;
    }

    /**
     * Handler for {@link ObjectBuilderProvider}, provides the specific builder generating logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Creates and returns a new {@link ObjectBuilder}, or {@code null} if the target type is unsupported.
         *
         * @param target the target type
         * @return a new {@link ObjectBuilder}, or {@code null} if the target type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        ObjectBuilder newBuilder(@Nonnull Type target) throws Exception;
    }
}
