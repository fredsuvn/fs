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
public interface MapSchemaParser {

    /**
     * Returns the default {@link MapSchemaParser}.
     * <p>
     * Note the default {@link MapSchemaParser} is singleton, and never caches the parsed results.
     *
     * @return the default {@link MapSchemaParser}
     */
    static @Nonnull MapSchemaParser defaultParser() {
        return MapSchemaParserBack.defaultParser();
    }

    /**
     * Returns the default cached {@link MapSchemaParser}, which is based on {@link #defaultParser()} and caches the
     * parsed results with a {@link SimpleCache#ofSoft()}.
     * <p>
     * Note the default cached {@link MapSchemaParser} is singleton.
     *
     * @return the default cached {@link MapSchemaParser}
     * @see #defaultParser()
     */
    static @Nonnull MapSchemaParser defaultCachedParser() {
        return MapSchemaParserBack.defaultCachedParser();
    }

    /**
     * Returns a new {@link MapSchemaParser} that caches the parsed results with the specified cache.
     *
     * @param cache  the specified cache to store the parsed results
     * @param parser the underlying {@link MapSchemaParser} to parse the type
     * @return a new {@link MapSchemaParser} that caches the parsed results with the specified cache
     */
    static @Nonnull MapSchemaParser newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
        @Nonnull MapSchemaParser parser
    ) {
        return MapSchemaParserBack.newCachedParser(cache, parser);
    }

    /**
     * Parses the given {@link Map} type or {@link MapType} to an instance of {@link MapSchemaParser}, and returns the
     * parsed {@link MapSchemaParser}.
     *
     * @param type the given type
     * @return the parsed {@link MapSchemaParser}
     * @throws DataSchemaException if the given type is not a {@link Map} type or {@link MapType}, or any other problem
     *                             occurs
     */
    @Nonnull
    MapSchema parse(@Nonnull Type type) throws DataSchemaException;
}
