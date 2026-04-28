package space.sunqian.fs.object.builder;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.builder.handlers.CommonBuilderHandler;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface represents the manager of {@link BuilderOperator}.
 * <p>
 * The default {@link BuilderManager} is {@link BuilderManager#defaultManager()}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface BuilderManager {

    /**
     * Returns the default {@link BuilderManager}. Here are handlers in the default {@link BuilderManager}:
     * <ul>
     *     <li>{@link CommonBuilderHandler#getInstance()}</li>
     * </ul>
     * <p>
     * Note the default {@link BuilderManager} is singleton, and will cache the returned {@link BuilderOperator}
     * instances by a {@link SimpleCache#ofSoft()} registered in {@link Fs#registerGlobalCache(SimpleCache)}.
     *
     * @return the default {@link BuilderManager}
     * @see CommonBuilderHandler
     */
    static @Nonnull BuilderManager defaultManager() {
        return BuilderBack.defaultManager();
    }

    /**
     * Creates and returns a new {@link BuilderManager} with given handlers and cache function.
     *
     * @param handlers      the given handlers
     * @param cacheFunction the cache function to cache the generated {@link BuilderOperator} instances
     * @return a new {@link BuilderManager} with the given handlers and cache function
     */
    static @Nonnull BuilderManager newManager(
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers,
        @Nonnull CacheFunction<@Nonnull Type, @Nullable BuilderOperator> cacheFunction
    ) {
        return BuilderBack.newManager(handlers, cacheFunction);
    }

    /**
     * Returns an instance of {@link BuilderOperator} for the target type, or {@code null} if the target type is
     * unsupported.
     *
     * @param target the target type
     * @return a new {@link BuilderOperator}, or {@code null} if the target type is unsupported
     * @throws ObjectBuilderException if an error occurs while creating the {@link BuilderOperator}
     * @implNote The default implementation of this method, without considering cache, invokes the
     * {@link Handler#newOperator(Type)} in the order of {@link #handlers()} until one of the handlers returns a
     * non-{@code null} {@link BuilderOperator}. The codes are similar to:
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
    default @Nullable BuilderOperator getOperator(@Nonnull Type target) throws ObjectBuilderException {
        try {
            return BuilderBack.operatorForType(target, handlers());
        } catch (Exception e) {
            throw new ObjectBuilderException(e);
        }
    }

    /**
     * Returns all handlers of this {@link BuilderManager}.
     *
     * @return all handlers of this {@link BuilderManager}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns this {@link BuilderManager} as a {@link Handler}.
     *
     * @return this {@link BuilderManager} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link BuilderManager}, provides the actual {@link BuilderOperator} generating logic.
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
