package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.meta.handlers.CommonMapMetaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to introspect {@link Type} of {@link Map} to {@link MapMeta}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface MapMetaManager {

    /**
     * Returns the default {@link MapMetaManager}. Here are handlers in the default {@link MapMetaManager}:
     * <ul>
     *     <li>{@link CommonMapMetaHandler#getInstance()}</li>
     * </ul>
     * <p>
     * Note the default {@link MapMetaManager} is singleton, and will cache the returned {@link MapMeta} instances by a
     * {@link SimpleCache#ofSoft()} registered in {@link Fs#registerGlobalCache(SimpleCache)}.
     *
     * @return the default {@link MapMetaManager}
     */
    static @Nonnull MapMetaManager defaultManager() {
        return MapMetaBack.defaultManager();
    }

    /**
     * Creates and returns a new {@link MapMetaManager} with given handlers and cache function.
     *
     * @param handlers      the given handlers
     * @param cacheFunction the cache function to cache the generated {@link MapMeta} instances
     * @return a new {@link MapMetaManager} with the given handlers and cache function
     */
    static @Nonnull MapMetaManager newManager(
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers,
        @Nonnull CacheFunction<@Nonnull Type, @Nonnull MapMeta> cacheFunction
    ) {
        return MapMetaBack.newManager(handlers, cacheFunction);
    }

    /**
     * Introspects and returns the given {@link Map} type to an instance of {@link MapMeta}.
     *
     * @param type the given type
     * @return the introspected {@link MapMeta}
     * @throws DataMetaException if the given type is not a {@link Map} type, or any other error occurs
     */
    @Nonnull
    MapMeta introspect(@Nonnull Type type) throws DataMetaException;

    /**
     * Returns all handlers of this {@link MapMetaManager}.
     *
     * @return all handlers of this {@link MapMetaManager}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns this {@link MapMetaManager} as a {@link Handler}.
     *
     * @return this {@link MapMetaManager} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link MapMetaManager}, provides the actual introspecting logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Introspects and returns a new {@link MapMeta} for the given type, or {@code null} if the given type is
         * unsupported.
         *
         * @param type    the given type
         * @param manager the {@link MapMetaManager} where this handler is used
         * @return a new {@link MapMeta}, or {@code null} if the given type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        MapMeta newMapMeta(@Nonnull Type type, @Nonnull MapMetaManager manager) throws Exception;
    }
}
