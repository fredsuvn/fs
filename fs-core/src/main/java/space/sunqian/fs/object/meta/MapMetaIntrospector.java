package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.meta.handlers.CommonMapMetaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to introspect {@link Type} of {@link Map} to {@link MapMeta}.
 * <p>
 * It uses a list of {@link Handler}s to execute the introspection operations, where each {@link Handler} possesses its
 * own specific introspection logic. And the default {@link MapMetaIntrospector} is based on
 * {@link CommonMapMetaHandler#getInstance()}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface MapMetaIntrospector {

    /**
     * Returns the default {@link MapMetaIntrospector}. Here are handlers in the default {@link MapMetaIntrospector}:
     * <ul>
     *     <li>{@link CommonMapMetaHandler#getInstance()}</li>
     * </ul>
     * <p>
     * Note the default {@link MapMetaIntrospector} is singleton, and will cache the returned {@link MapMeta} instances by a
     * {@link SimpleCache} registered in {@link Fs#registerGlobalCache(SimpleCache)}.
     *
     * @return the default {@link MapMetaIntrospector}
     */
    static @Nonnull MapMetaIntrospector defaultIntrospector() {
        return MapMetaBack.defaultIntrospector();
    }

    /**
     * Creates and returns a new {@link MapMetaIntrospector} with given cache function and handlers.
     *
     * @param cacheFunction the cache function to cache the generated {@link MapMeta} instances
     * @param handlers      the given handlers
     * @return a new {@link MapMetaIntrospector} with the given cache function and handlers
     */
    static @Nonnull MapMetaIntrospector newIntrospector(
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cacheFunction,
        @Nonnull Handler @Nonnull @RetainedParam ... handlers
    ) {
        return newIntrospector(cacheFunction, ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link MapMetaIntrospector} with given cache function and handlers.
     *
     * @param cacheFunction the cache function to cache the generated {@link MapMeta} instances
     * @param handlers      the given handlers
     * @return a new {@link MapMetaIntrospector} with the given cache function and handlers
     */
    static @Nonnull MapMetaIntrospector newIntrospector(
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cacheFunction,
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers
    ) {
        return MapMetaBack.newIntrospector(cacheFunction, handlers);
    }

    /**
     * Introspects the given {@link Map} type and returns the introspected {@link MapMeta}.
     *
     * @param type the given type
     * @return the introspected {@link MapMeta}
     * @throws DataMetaException if the given type is not a {@link Map} type, or any other error occurs
     */
    @Nonnull
    MapMeta introspect(@Nonnull Type type) throws DataMetaException;

    /**
     * Returns all handlers of this {@link MapMetaIntrospector}.
     *
     * @return all handlers of this {@link MapMetaIntrospector}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns this {@link MapMetaIntrospector} as a {@link Handler}.
     *
     * @return this {@link MapMetaIntrospector} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link MapMetaIntrospector}, provides the actual introspecting logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Introspects the given type and returns the introspected {@link MapMeta}, or {@code null} if the given type is
         * unsupported.
         *
         * @param type         the given type
         * @param introspector the {@link MapMetaIntrospector} where this handler is used
         * @return a new {@link MapMeta}, or {@code null} if the given type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        MapMeta introspect(@Nonnull Type type, @Nonnull MapMetaIntrospector introspector) throws Exception;
    }
}
