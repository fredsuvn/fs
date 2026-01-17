package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ThreadSafe;

import java.lang.reflect.Type;

/**
 * This interface is used to parse {@link Type} to {@link MapSchema}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface MapSchemaParser {

    /**
     * Returns the default {@link MapSchemaParser}.
     *
     * @return the default {@link MapSchemaParser}
     */
    static @Nonnull MapSchemaParser defaultParser() {
        return MapSchemaParserImpl.INST;
    }

    /**
     * Parses the given type to an instance of {@link MapSchemaParser}, and returns the parsed {@link MapSchemaParser}.
     * <p>
     * Note that this method does not cache the results and will generate new instances every invocation.
     *
     * @param type the given type
     * @return the parsed {@link MapSchemaParser}
     * @throws DataSchemaException if any problem occurs
     */
    @Nonnull
    MapSchema parse(@Nonnull Type type) throws DataSchemaException;

    /**
     * Parses the given type to an instance of {@link MapSchemaParser} with the specified key type and value type, and
     * returns the parsed {@link MapSchemaParser}.
     * <p>
     * Note that this method does not cache the results and will generate new instances every invocation.
     *
     * @param type      the given type
     * @param keyType   the specified key type
     * @param valueType the specified value type
     * @return the parsed {@link MapSchemaParser}
     * @throws DataSchemaException if any problem occurs
     */
    @Nonnull
    MapSchema parse(@Nonnull Type type, @Nonnull Type keyType, @Nonnull Type valueType) throws DataSchemaException;
}
