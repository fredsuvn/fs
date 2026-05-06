package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.meta.handlers.AbstractObjectMetaHandler;
import space.sunqian.fs.object.meta.handlers.CommonObjectMetaHandler;
import space.sunqian.fs.object.meta.handlers.RecordMetaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to introspect {@link Type} to {@link ObjectMeta}.
 * <p>
 * It uses a list of {@link Handler}s to execute the introspection operations, where each {@link Handler} possesses its
 * own specific introspection logic. And the default {@link ObjectMetaManager} is based on
 * {@link CommonObjectMetaHandler#getInstance()}.
 * <p>
 * There is a skeletal {@link Handler} implementation: {@link AbstractObjectMetaHandler}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectMetaManager {

    /**
     * Returns the default {@link ObjectMetaManager}. Here are handlers in the default {@link ObjectMetaManager}:
     * <ul>
     *     <li>
     *         {@link RecordMetaHandler#getInstance()}, if the current JVM version supports {@code record} classes
     *         ({@code >= 16});
     *     </li>
     *     <li>{@link CommonObjectMetaHandler#getInstance()};</li>
     * </ul>
     * <p>
     * Note the default {@link ObjectMetaManager} is singleton, and will cache the returned {@link ObjectMeta} instances by a
     * {@link SimpleCache} registered in {@link Fs#registerGlobalCache(SimpleCache)}.
     *
     * @return the default {@link ObjectMetaManager}
     * @see CommonObjectMetaHandler
     * @see RecordMetaHandler
     */
    static @Nonnull ObjectMetaManager defaultManager() {
        return ObjectMetaBack.defaultManager();
    }

    /**
     * Creates and returns a new {@link ObjectMetaManager} with given cache function and handlers.
     *
     * @param cacheFunction the cache function to cache the generated {@link ObjectMeta} instances
     * @param handlers      the given handlers
     * @return a new {@link ObjectMetaManager} with the given cache function and handlers
     */
    static @Nonnull ObjectMetaManager newManager(
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull ObjectMeta> cacheFunction,
        @Nonnull Handler @Nonnull @RetainedParam ... handlers
    ) {
        return newManager(cacheFunction, ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectMetaManager} with given cache function and handlers.
     *
     * @param cacheFunction the cache function to cache the generated {@link ObjectMeta} instances
     * @param handlers      the given handlers
     * @return a new {@link ObjectMetaManager} with the given cache function and handlers
     */
    static @Nonnull ObjectMetaManager newManager(
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull ObjectMeta> cacheFunction,
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers
    ) {
        return ObjectMetaBack.newManager(cacheFunction, handlers);
    }

    /**
     * Introspects the given type and returns the introspected {@link ObjectMeta}.
     *
     * @param type the given type
     * @return the introspected {@link ObjectMeta}
     * @throws DataMetaException if any problem occurs
     * @implNote The default implementation of this method invokes the
     * {@link Handler#introspect(ObjectMetaManager.Context)} in the order of {@link #handlers()} until one of the
     * handlers returns {@code false}. The codes are similar to:
     * <pre>{@code
     * for (Handler handler : handlers()) {
     *     if (!handler.introspect(context)) {
     *         break;
     *     }
     * }
     * }</pre>
     */
    default @Nonnull ObjectMeta introspect(@Nonnull Type type) throws DataMetaException {
        try {
            ObjectMetaBack.MetaBuilder builder = new ObjectMetaBack.MetaBuilder(type);
            for (Handler handler : handlers()) {
                if (!handler.introspect(builder)) {
                    break;
                }
            }
            return builder.build(this);
        } catch (Exception e) {
            throw new DataMetaException(type, e);
        }
    }

    /**
     * Returns all handlers of this {@link ObjectMetaManager}.
     *
     * @return all handlers of this {@link ObjectMetaManager}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns this {@link ObjectMetaManager} as a {@link Handler}.
     *
     * @return this {@link ObjectMetaManager} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link ObjectMetaManager}, provides the actual introspecting logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Introspects the given type and returns the introspected {@link ObjectMeta} using its specified introspecting
         * logic.
         * <p>
         * The given {@link Context} instance provides the introspected type via {@link Context#objectType()}. The
         * introspected properties should be stored in {@link Context#propertyBaseMap()}. Subsequent handlers can read
         * the property base info placed by the previous handler and then replace or reprocess it.
         * <p>
         * Returns {@code false} to prevent subsequent handlers from introspecting further, or {@code true} to continue
         * introspecting.
         *
         * @param context the given context
         * @return whether to continue introspecting
         * @throws Exception for any error during introspection
         */
        boolean introspect(@Nonnull Context context) throws Exception;
    }

    /**
     * Context for introspecting the specified type.
     *
     * @author sunqian
     */
    interface Context {

        /**
         * Returns the type of the object to be introspected.
         *
         * @return the type of the object to be introspected
         */
        @Nonnull
        Type objectType();

        /**
         * Returns a mutable map for storing and reading property base info.
         * <p>
         * Throughout the whole introspection process, the map stores and shares property base info for all handlers.
         * Each handler can add, remove, or reprocess that info.
         *
         * @return a mutable map for storing and reading property base info
         */
        @Nonnull
        Map<@Nonnull String, @Nonnull PropertyMetaBase> propertyBaseMap();
    }
}
