package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the meta info of a map, provides meta info about the map's entries.
 * <p>
 * Two {@link MapMeta}s are considered equal if, and only if both map types and managers from {@link #manager()} are
 * equal.
 *
 * @author sunqian
 */
@Immutable
public interface MapMeta extends DataMeta {

    /**
     * Introspects the given {@link Map} type to {@link MapMeta} using {@link MapMetaManager#defaultManager()}.
     *
     * @param type the given type to be introspected
     * @return the {@link MapMeta} introspected from the given type using {@link MapMetaManager#defaultManager()}
     * @throws DataMetaException if the given type is not a {@link Map} type, or any other error occurs
     */
    static @Nonnull MapMeta of(@Nonnull Type type) throws DataMetaException {
        return MapMetaManager.defaultManager().introspect(type);
    }

    /**
     * Returns the {@link MapMetaManager} where this {@link MapMeta} is introspected.
     *
     * @return the {@link MapMetaManager} where this {@link MapMeta} is introspected
     */
    @Nonnull
    MapMetaManager manager();

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
    default boolean isMapMeta() {
        return true;
    }

    @Override
    default boolean isObjectMeta() {
        return false;
    }

    /**
     * Returns whether this {@link MapMeta} is equal to the other {@link MapMeta}. They are considered equal if, and
     * only if both map types and managers from {@link #manager()} are equal.
     *
     * @param other the other {@link MapMeta}
     * @return whether this {@link MapMeta} is equal to the other {@link MapMeta}
     */
    boolean equals(Object other);

    /**
     * Returns the hash code of this {@link MapMeta}. The hash code is generated via {@link #type()} and
     * {@link #manager()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + type().hashCode();
     * result = 31 * result + parser().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link MapMeta}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link MapMeta}. The string is generated like following codes:
     * <pre>{@code
     * return type().getTypeName();
     * }</pre>
     *
     * @return a string representation of this {@link MapMeta}
     */
    @Nonnull
    String toString();
}
