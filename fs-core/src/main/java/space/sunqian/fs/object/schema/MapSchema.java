package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the schema of map, provides information about the map's entries.
 * <p>
 * Two {@link MapSchema}s are considered equal if, and only if both types of maps and both parsers from
 * {@link #parser()} are equal.
 *
 * @author sunqian
 */
@Immutable
public interface MapSchema extends DataSchema {

    /**
     * Parse the given type to {@link MapSchema} using {@link MapParser#defaultParser()}.
     * <p>
     * Note this method never caches the parsed results.
     *
     * @param type the given type
     * @return the {@link MapSchema} parsed from the given type using {@link MapParser#defaultParser()}
     * @throws DataSchemaException if the given type is not a {@link Map} type, or any other problem occurs
     */
    static @Nonnull MapSchema parse(@Nonnull Type type) throws DataSchemaException {
        return MapParser.defaultParser().parse(type);
    }

    /**
     * Parse the given type to {@link MapSchema} using {@link MapParser#defaultParser()}, with the specified key type
     * and value type.
     * <p>
     * Note this method never caches the parsed results.
     *
     * @param type      the given type
     * @param keyType   the specified key type
     * @param valueType the specified value type
     * @return the {@link MapSchema} parsed from the given type using {@link MapParser#defaultParser()}
     * @throws DataSchemaException if the given type is not a {@link Map} type, or any other problem occurs
     */
    static @Nonnull MapSchema parse(
        @Nonnull Type type,
        @Nonnull Type keyType,
        @Nonnull Type valueType
    ) throws DataSchemaException {
        return MapParser.defaultParser().parse(type, keyType, valueType);
    }

    /**
     * Returns the {@link MapParser} of this {@link MapSchema}.
     *
     * @return the {@link MapParser} of this {@link MapSchema}
     */
    @Nonnull
    MapParser parser();

    /**
     * Returns the key type of this map type.
     *
     * @return the key type of this map type
     */
    @Nonnull
    Type keyType();

    /**
     * Returns the value type of this map type.
     *
     * @return the value type of this map type
     */
    @Nonnull
    Type valueType();

    @Override
    default boolean isMapSchema() {
        return true;
    }

    @Override
    default boolean isObjectSchema() {
        return false;
    }

    /**
     * Returns whether this {@link MapSchema} is equal to the other {@link MapSchema}. They are considered equal if, and
     * only if both types of maps and both parsers from {@link #parser()} are equal.
     *
     * @param other the other {@link MapSchema}
     * @return whether this {@link MapSchema} is equal to the other {@link MapSchema}
     */
    boolean equals(Object other);

    /**
     * Returns the hash code of this {@link MapSchema}. The hash code is generated via {@link #type()} and
     * {@link #parser()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + type().hashCode();
     * result = 31 * result + parser().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link MapSchema}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link MapSchema}. The string is generated like following codes:
     * <pre>{@code
     * return type().getTypeName();
     * }</pre>
     *
     * @return a string representation of this {@link MapSchema}
     */
    @Nonnull
    String toString();
}
