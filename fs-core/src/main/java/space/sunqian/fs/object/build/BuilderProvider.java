package space.sunqian.fs.object.build;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.build.handlers.CommonBuilderHandler;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface is the provider of {@link BuilderExecutor}.
 * <p>
 * The default {@link BuilderProvider} is {@link BuilderProvider#defaultProvider()}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface BuilderProvider {

    /**
     * Returns the default {@link BuilderProvider}. Here are handlers in the default provider:
     * <ul>
     *     <li>{@link CommonBuilderHandler#getInstance()}</li>
     * </ul>
     * <p>
     * Note the default {@link BuilderProvider} is singleton, and never caches the returned {@link BuilderExecutor}
     * instances.
     *
     * @return the default {@link BuilderProvider}
     * @see CommonBuilderHandler
     */
    static @Nonnull BuilderProvider defaultProvider() {
        return BuilderProviderBack.defaultProvider();
    }

    /**
     * Returns the default cached {@link BuilderProvider}, which is based on {@link #defaultProvider()} and caches the
     * results with a {@link SimpleCache#ofSoft()}.
     * <p>
     * Note the default cached {@link BuilderProvider} is singleton.
     *
     * @return the default {@link BuilderProvider}
     * @see #defaultProvider()
     */
    static @Nonnull BuilderProvider defaultCachedProvider() {
        return BuilderProviderBack.defaultCachedProvider();
    }

    /**
     * Creates and returns a new {@link BuilderProvider} with the given handlers.
     * <p>
     * Note the created {@link BuilderProvider} never caches the returned {@link BuilderExecutor} instances.
     *
     * @param handlers the given handlers
     * @return a new {@link BuilderProvider} with the given handlers
     */
    static @Nonnull BuilderProvider newProvider(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newProvider(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link BuilderProvider} with given handlers.
     * <p>
     * Note the created {@link BuilderProvider} never caches the returned {@link BuilderExecutor} instances.
     *
     * @param handlers given handlers
     * @return a new {@link BuilderProvider} with given handlers
     */
    static @Nonnull BuilderProvider newProvider(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return BuilderProviderBack.newProvider(handlers);
    }

    /**
     * Returns a new {@link BuilderProvider} that caches the returned {@link BuilderExecutor} instances with the
     * specified cache.
     * <p>
     * Note the behavior of the non-creating methods of the returned {@link BuilderProvider}, such as
     * {@link #handlers()}, {@link #asHandler()} and {@link #withFirstHandler(Handler)}, will directly invoke the
     * underlying {@link BuilderProvider}.
     *
     * @param cache    the specified cache to store the results
     * @param provider the underlying {@link BuilderProvider} to create the type
     * @return a new {@link BuilderProvider} that caches the results with the specified cache
     */
    static @Nonnull BuilderProvider newCachedProvider(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull BuilderExecutor> cache,
        @Nonnull BuilderProvider provider
    ) {
        return BuilderProviderBack.newCachedProvider(cache, provider);
    }

    /**
     * Returns an instance of {@link BuilderExecutor} for the target type, or {@code null} if the target type is
     * unsupported.
     *
     * @param target the target type
     * @return a new {@link BuilderExecutor}, or {@code null} if the target type is unsupported
     * @throws ObjectBuildingException if an error occurs while creating the {@link BuilderExecutor}
     * @implNote The default implementation of this method invokes the {@link Handler#newExecutor(Type)} in the order of
     * {@link #handlers()} until one of the handlers returns a non-{@code null} {@link BuilderExecutor}. The codes are
     * similar to:
     * <pre>{@code
     * for (Handler handler : handlers()) {
     *     BuilderExecutor executor = handler.newExecutor(target);
     *     if (executor != null) {
     *         return executor;
     *     }
     * }
     * return null;
     * }</pre>
     */
    default @Nullable BuilderExecutor forType(@Nonnull Type target) throws ObjectBuildingException {
        try {
            return BuilderProviderBack.executorForType(target, handlers());
        } catch (Exception e) {
            throw new ObjectBuildingException(e);
        }
    }

    /**
     * Returns all handlers of this {@link BuilderProvider}.
     *
     * @return all handlers of this {@link BuilderProvider}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link BuilderProvider} of which first handler is the given handler and the next handler is this
     * {@link BuilderProvider} as a {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newProvider(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new {@link BuilderProvider} of which first handler is the given handler and the next handler is this
     * {@link BuilderProvider} as a {@link Handler}
     */
    default @Nonnull BuilderProvider withFirstHandler(@Nonnull Handler firstHandler) {
        return newProvider(firstHandler, this.asHandler());
    }

    /**
     * Returns this {@link BuilderProvider} as a {@link Handler}.
     *
     * @return this {@link BuilderProvider} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link BuilderProvider}, provides the actual {@link BuilderExecutor} generating logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Creates and returns a new {@link BuilderExecutor} for the target type, or {@code null} if the target type is
         * unsupported.
         *
         * @param target the target type
         * @return a new {@link BuilderExecutor}, or {@code null} if the target type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        BuilderExecutor newExecutor(@Nonnull Type target) throws Exception;
    }
}
