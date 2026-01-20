package space.sunqian.fs.object.create;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.create.handlers.CommonCreatorHandler;

import java.lang.reflect.Type;
import java.util.List;

/**
 * The provider to provide {@link ObjectCreator}s.
 * <p>
 * The default {@link CreatorProvider} is {@link CreatorProvider#defaultProvider()}.
 *
 * @author sunqian
 */
public interface CreatorProvider {

    /**
     * Returns the default {@link CreatorProvider}. Here are handlers in the default provider:
     * <ul>
     *     <li>{@link CommonCreatorHandler#INSTANCE}</li>
     * </ul>
     * <p>
     * Note the default {@link CreatorProvider} is singleton, and never caches the returned {@link ObjectCreator}
     * instances.
     *
     * @return the default {@link CreatorProvider}
     */
    static @Nonnull CreatorProvider defaultProvider() {
        return CreateBack.defaultProvider();
    }

    /**
     * Creates and returns a new {@link CreatorProvider} with the given handlers.
     * <p>
     * Note the created {@link CreatorProvider} never caches the returned {@link ObjectCreator} instances.
     *
     * @param handlers the given handlers
     * @return a new {@link CreatorProvider} with the given handlers
     */
    static @Nonnull CreatorProvider newProvider(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newProvider(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link CreatorProvider} with given handlers.
     * <p>
     * Note the created {@link CreatorProvider} never caches the returned {@link ObjectCreator} instances.
     *
     * @param handlers given handlers
     * @return a new {@link CreatorProvider} with given handlers
     */
    static @Nonnull CreatorProvider newProvider(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return CreateBack.newProvider(handlers);
    }

    /**
     * Returns a new {@link CreatorProvider} that caches the returned {@link ObjectCreator} instances with the specified
     * cache.
     * <p>
     * Note the behavior of the non-creating methods of the returned {@link CreatorProvider}, such as
     * {@link #handlers()}, {@link #asHandler()} and {@link #withFirstHandler(Handler)}, will directly invoke the
     * underlying {@link CreatorProvider}.
     *
     * @param cache    the specified cache to store the results
     * @param provider the underlying {@link CreatorProvider} to create the type
     * @return a new {@link CreatorProvider} that caches the results with the specified cache
     */
    static @Nonnull CreatorProvider cachedProvider(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectCreator> cache,
        @Nonnull CreatorProvider provider
    ) {
        return CreateBack.cachedProvider(cache, provider);
    }

    /**
     * Returns an instance of {@link ObjectCreator} for the target type, or {@code null} if the target type is
     * unsupported.
     *
     * @param target the target type
     * @return a new {@link ObjectCreator}, or {@code null} if the target type is unsupported
     * @throws ObjectCreateException if an error occurs while creating the {@link ObjectCreator}
     * @implSpec The default implementation iterates all handlers of this {@link CreatorProvider} and calls
     * {@link Handler#newCreator(Type)} for each handler. If a handler returns a non-{@code null} {@link ObjectCreator},
     * it will be returned. Otherwise, {@code null} will be returned. The result value will not be cached.
     */
    default @Nullable ObjectCreator forType(@Nonnull Type target) throws ObjectCreateException {
        try {
            return CreateBack.creatorForType(target, handlers());
        } catch (Exception e) {
            throw new ObjectCreateException(e);
        }
    }

    /**
     * Returns all handlers of this {@link CreatorProvider}.
     *
     * @return all handlers of this {@link CreatorProvider}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link CreatorProvider} of which first handler is the given handler and the next handler is this
     * {@link CreatorProvider} as a {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newProvider(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new {@link CreatorProvider} of which first handler is the given handler and the next handler is this
     * {@link CreatorProvider} as a {@link Handler}
     */
    default @Nonnull CreatorProvider withFirstHandler(@Nonnull Handler firstHandler) {
        return newProvider(firstHandler, this.asHandler());
    }

    /**
     * Returns this {@link CreatorProvider} as a {@link Handler}.
     *
     * @return this {@link CreatorProvider} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link CreatorProvider}, provides the actual {@link ObjectCreator} generating logic.
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
