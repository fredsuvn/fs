package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.SimpleCache;

import java.lang.reflect.Type;

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
        return SchemaBack.defaultMapParser();
    }

    /**
     * Returns a new {@link MapParser} that caches the parsed results with the specified cache.
     *
     * @param cache  the specified cache to store the parsed results
     * @param parser the underlying {@link MapParser} to parse the type
     * @return a new {@link MapParser} that caches the parsed results with the specified cache
     */
    static @Nonnull MapParser cachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull MapSchema> cache,
        @Nonnull MapParser parser
    ) {
        return SchemaBack.cachedMapParser(cache, parser);
    }

    /**
     * Parses the given type to an instance of {@link MapParser}, and returns the parsed {@link MapParser}.
     *
     * @param type the given type
     * @return the parsed {@link MapParser}
     * @throws SchemaException if any problem occurs
     */
    @Nonnull
    MapSchema parse(@Nonnull Type type) throws SchemaException;

    /**
     * Parses the given type to an instance of {@link MapParser} with the specified key type and value type, and returns
     * the parsed {@link MapParser}.
     *
     * @param type      the given type
     * @param keyType   the specified key type
     * @param valueType the specified value type
     * @return the parsed {@link MapParser}
     * @throws SchemaException if any problem occurs
     */
    @Nonnull
    MapSchema parse(@Nonnull Type type, @Nonnull Type keyType, @Nonnull Type valueType) throws SchemaException;
}
