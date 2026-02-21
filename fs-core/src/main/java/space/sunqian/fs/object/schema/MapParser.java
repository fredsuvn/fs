package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.SimpleCache;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface is used to parse {@link Type} to {@link MapSchema}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface MapParser {

    /**
     * Returns the default {@link MapParser}.
     * <p>
     * Note the default {@link MapParser} is singleton, and never caches the parsed results.
     *
     * @return the default {@link MapParser}
     */
    static @Nonnull MapParser defaultParser() {
        return MapParserBack.defaultParser();
    }

    /**
     * Returns the default cached {@link MapParser}, which is based on {@link #defaultParser()} and caches the parsed
     * results with a {@link SimpleCache#ofSoft()}.
     * <p>
     * Note the default cached {@link MapParser} is singleton.
     *
     * @return the default cached {@link MapParser}
     * @see #defaultParser()
     */
    static @Nonnull MapParser defaultCachedParser() {
        return MapParserBack.defaultCachedParser();
    }

    /**
     * Returns a new {@link MapParser} that caches the parsed results with the specified cache.
     *
     * @param cache  the specified cache to store the parsed results
     * @param parser the underlying {@link MapParser} to parse the type
     * @return a new {@link MapParser} that caches the parsed results with the specified cache
     */
    static @Nonnull MapParser newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
        @Nonnull MapParser parser
    ) {
        return MapParserBack.newCachedParser(cache, parser);
    }

    /**
     * Parses the given {@link Map} type or {@link MapType} to an instance of {@link MapParser}, and returns the parsed
     * {@link MapParser}.
     *
     * @param type the given type
     * @return the parsed {@link MapParser}
     * @throws DataSchemaException if the given type is not a {@link Map} type or {@link MapType}, or any other problem
     *                             occurs
     */
    @Nonnull
    MapSchema parse(@Nonnull Type type) throws DataSchemaException;
}
