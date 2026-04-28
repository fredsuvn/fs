package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.builder.BuilderManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to introspect {@link Type} and {@link MapType} to {@link MapMeta}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface MapMetaManager {

    /**
     * Returns the default {@link MapMetaManager}.
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
     * Returns the default cached {@link MapMetaManager}, which is based on {@link #defaultManager()} and caches the
     * parsed results with a {@link SimpleCache#ofSoft()}.
     * <p>
     * Note the default cached {@link MapMetaManager} is singleton.
     *
     * @return the default cached {@link MapMetaManager}
     * @see #defaultManager()
     */
    static @Nonnull MapMetaManager defaultCachedParser() {
        return MapMetaBack.defaultCachedParser();
    }

    /**
     * Returns a new {@link MapMetaManager} that caches the parsed results with the specified cache.
     *
     * @param cache  the specified cache to store the parsed results
     * @param parser the underlying {@link MapMetaManager} to parse the type
     * @return a new {@link MapMetaManager} that caches the parsed results with the specified cache
     */
    static @Nonnull MapMetaManager newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapMeta> cache,
        @Nonnull MapMetaManager parser
    ) {
        return MapMetaBack.newCachedParser(cache, parser);
    }

    /**
     * Introspects and returns the given {@link Map} type or {@link MapType} to an instance of {@link MapMeta}.
     *
     * @param type the given type
     * @return the introspected {@link MapMeta}
     * @throws DataMetaException if the given type is not a {@link Map} type or {@link MapType}, or any other error
     *                           occurs
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
         * @param type the given type
         * @return a new {@link MapMeta}, or {@code null} if the given type is unsupported
         * @throws Exception if an error occurs
         */
        @Nullable
        MapMeta newMapMeta(@Nonnull Type type) throws Exception;
    }
}
