package space.sunqian.fs.object.builder;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.builder.handlers.CommonBuilderHandler;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface is the provider of {@link BuilderOperator}.
 * <p>
 * The default {@link BuilderOperatorProvider} is {@link BuilderOperatorProvider#defaultProvider()}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface BuilderOperatorProvider {

    /**
     * Returns the default {@link BuilderOperatorProvider}. Here are handlers in the default provider:
     * <ul>
     *     <li>{@link CommonBuilderHandler#getInstance()}</li>
     * </ul>
     * <p>
     * Note the default {@link BuilderOperatorProvider} is singleton, and never caches the returned {@link BuilderOperator}
     * instances.
     *
     * @return the default {@link BuilderOperatorProvider}
     * @see CommonBuilderHandler
     */
    static @Nonnull BuilderOperatorProvider defaultProvider() {
        return BuilderOperatorProviderBack.defaultProvider();
    }

    /**
     * Returns the default cached {@link BuilderOperatorProvider}, which is based on {@link #defaultProvider()} and
     * caches the results with a {@link SimpleCache#ofSoft()}.
     * <p>
     * Note the default cached {@link BuilderOperatorProvider} is singleton.
     *
     * @return the default {@link BuilderOperatorProvider}
     * @see #defaultProvider()
     */
    static @Nonnull BuilderOperatorProvider defaultCachedProvider() {
        return BuilderOperatorProviderBack.defaultCachedProvider();
    }

    /**
     * Creates and returns a new {@link BuilderOperatorProvider} with the given handlers.
     * <p>
     * Note the created {@link BuilderOperatorProvider} never caches the returned {@link BuilderOperator} instances.
     *
     * @param handlers the given handlers
     * @return a new {@link BuilderOperatorProvider} with the given handlers
     */
    static @Nonnull BuilderOperatorProvider newProvider(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newProvider(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link BuilderOperatorProvider} with given handlers.
     * <p>
     * Note the created {@link BuilderOperatorProvider} never caches the returned {@link BuilderOperator} instances.
     *
     * @param handlers given handlers
     * @return a new {@link BuilderOperatorProvider} with given handlers
     */
    static @Nonnull BuilderOperatorProvider newProvider(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return BuilderOperatorProviderBack.newProvider(handlers);
    }

    /**
     * Returns a new {@link BuilderOperatorProvider} that caches the returned {@link BuilderOperator} instances with the
     * specified cache.
     * <p>
     * Note the behavior of the non-creating methods of the returned {@link BuilderOperatorProvider}, such as
     * {@link #handlers()}, {@link #asHandler()} and {@link #withFirstHandler(Handler)}, will directly invoke the
     * underlying {@link BuilderOperatorProvider}.
     *
     * @param cache    the specified cache to store the results
     * @param provider the underlying {@link BuilderOperatorProvider} to create the type
     * @return a new {@link BuilderOperatorProvider} that caches the results with the specified cache
     */
    static @Nonnull BuilderOperatorProvider newCachedProvider(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull BuilderOperator> cache,
        @Nonnull BuilderOperatorProvider provider
    ) {
        return BuilderOperatorProviderBack.newCachedProvider(cache, provider);
    }

    /**
     * Returns an instance of {@link BuilderOperator} for the target type, or {@code null} if the target type is
     * unsupported.
     *
     * @param target the target type
     * @return a new {@link BuilderOperator}, or {@code null} if the target type is unsupported
     * @throws ObjectBuilderException if an error occurs while creating the {@link BuilderOperator}
     * @implNote The default implementation of this method invokes the {@link Handler#newOperator(Type)} in the order of
     * {@link #handlers()} until one of the handlers returns a non-{@code null} {@link BuilderOperator}. The codes are
     * similar to:
     * <pre>{@code
     * for (Handler handler : handlers()) {
     *     BuilderOperator operator = handler.newOperator(target);
     *     if (operator != null) {
     *         return operator;
     *     }
     * }
     * return null;
     * }</pre>
     */
    default @Nullable BuilderOperator forType(@Nonnull Type target) throws ObjectBuilderException {
        try {
            return BuilderOperatorProviderBack.operatorForType(target, handlers());
        } catch (Exception e) {
            throw new ObjectBuilderException(e);
        }
    }

    /**
     * Returns all handlers of this {@link BuilderOperatorProvider}.
     *
     * @return all handlers of this {@link BuilderOperatorProvider}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link BuilderOperatorProvider} of which first handler is the given handler and the next handler is
     * this {@link BuilderOperatorProvider} as a {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newProvider(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new {@link BuilderOperatorProvider} of which first handler is the given handler and the next handler is
     * this {@link BuilderOperatorProvider} as a {@link Handler}
     */
    default @Nonnull BuilderOperatorProvider withFirstHandler(@Nonnull Handler firstHandler) {
        return newProvider(firstHandler, this.asHandler());
    }

    /**
     * Returns this {@link BuilderOperatorProvider} as a {@link Handler}.
     *
     * @return this {@link BuilderOperatorProvider} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link BuilderOperatorProvider}, provides the actual {@link BuilderOperator} generating logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Creates and returns a new {@link BuilderOperator} for the target type, or {@code null} if the target type is
         * unsupported.
         *
         * @param target the target type
         * @return a new {@link BuilderOperator}, or {@code null} if the target type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        BuilderOperator newOperator(@Nonnull Type target) throws Exception;
    }
}
