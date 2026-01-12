package space.sunqian.fs.object;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.collect.ListKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The provider to provide {@link ObjectCreator}s.
 * <p>
 * The default {@link ObjectCreatorProvider} is {@link ObjectCreatorProvider#defaultProvider()}.
 *
 * @author sunqian
 */
public interface ObjectCreatorProvider {

    /**
     * Returns the default {@link ObjectCreatorProvider}.
     * <p>
     * The default {@link ObjectCreatorProvider} uses a cache function based on a {@link ConcurrentHashMap} (so it is
     * thread-safe) and the default {@link Handler} ({@link ObjectCreatorProvider#defaultHandler()}).
     *
     * @return the default {@link ObjectCreatorProvider}
     */
    static @Nonnull ObjectCreatorProvider defaultProvider() {
        return ObjectCreatorProviderImpl.DEFAULT;
    }

    /**
     * Returns the default {@link Handler}. The default {@link Handler} calls the empty constructor of the target class
     * (if it has) to implement {@link ObjectCreator#createBuilder()} and returns the builder object itself on
     * {@link ObjectCreator#createTarget(Object)}.
     *
     * @return the default {@link Handler}
     */
    static @Nonnull Handler defaultHandler() {
        return ObjectCreatorProviderImpl.DEFAULT_HANDLER;
    }

    /**
     * Creates and returns a new {@link ObjectCreatorProvider} with a cache function based on a
     * {@link ConcurrentHashMap} and the given handlers. This method is equivalent to:
     * <pre>{@code
     *     newProvider(CacheFunction.ofMap(new ConcurrentHashMap<>()), ListKit.list(handlers));
     * }</pre>
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectCreatorProvider} with a cache function based on a {@link ConcurrentHashMap} and the
     * given handlers
     */
    static @Nonnull ObjectCreatorProvider newProvider(
        @Nonnull @RetainedParam Handler @Nonnull ... handlers
    ) {
        return newProvider(CacheFunction.ofMap(new ConcurrentHashMap<>()), ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectCreatorProvider} with the given cache function and handlers.
     *
     * @param cache    the given cache function
     * @param handlers the given handlers
     * @return a new {@link ObjectCreatorProvider} with the given cache function and handlers
     */
    static @Nonnull ObjectCreatorProvider newProvider(
        @Nonnull CacheFunction<@Nonnull Type, @Nullable ObjectCreator> cache,
        @Nonnull @RetainedParam Handler @Nonnull ... handlers
    ) {
        return newProvider(cache, ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectCreatorProvider} with the given cache function and handlers.
     *
     * @param cache    the given cache function
     * @param handlers the given handlers
     * @return a new {@link ObjectCreatorProvider} with the given cache function and handlers
     */
    static @Nonnull ObjectCreatorProvider newProvider(
        @Nonnull CacheFunction<@Nonnull Type, @Nullable ObjectCreator> cache,
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers
    ) {
        return new ObjectCreatorProviderImpl(cache, handlers);
    }

    /**
     * Returns an instance of {@link ObjectCreator} (maybe cached) for the target type, or {@code null} if the target
     * type is unsupported.
     *
     * @param target the target type
     * @return a new {@link ObjectCreator}, or {@code null} if the target type is unsupported
     * @throws ObjectException if an error occurs while creating the {@link ObjectCreator}
     * @implSpec The default implementation iterates all handlers of this {@link ObjectCreatorProvider} and calls
     * {@link Handler#newCreator(Type)} for each handler. If a handler returns a non-{@code null} {@link ObjectCreator},
     * it will be returned. Otherwise, {@code null} will be returned. The result value will not be cached.
     */
    default @Nullable ObjectCreator creatorForType(@Nonnull Type target) throws ObjectException {
        try {
            for (Handler handler : handlers()) {
                ObjectCreator creator = handler.newCreator(target);
                if (creator != null) {
                    return creator;
                }
            }
            return null;
        } catch (Exception e) {
            throw new ObjectException(e);
        }
    }

    /**
     * Returns all handlers of this {@link ObjectCreatorProvider}.
     *
     * @return all handlers of this {@link ObjectCreatorProvider}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns this {@link ObjectCreatorProvider} as a {@link Handler}.
     *
     * @return this {@link ObjectCreatorProvider} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link ObjectCreatorProvider}, provides the actual {@link ObjectCreator} generating logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Creates and returns a new {@link ObjectCreator} for the target type, or {@code null} if the target type is
         * unsupported.
         *
         * @param target the target type
         * @return a new {@link ObjectCreator}, or {@code null} if the target type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        ObjectCreator newCreator(@Nonnull Type target) throws Exception;
    }
}
