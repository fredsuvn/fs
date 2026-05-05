package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.beans.BeanInfo;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the meta info of non-map object, provides meta info about the object's properties, and
 * introspected by a {@link ObjectMetaManager}.
 * <p>
 * It is very similar to the {@link BeanInfo} used to describe
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but it only includes
 * simple properties, without indexed properties, events, methods, or other more complex components. And the rules for
 * introspecting properties are defined by the implementation of {@link ObjectMetaManager}, rather than a public rules.
 * <p>
 * Two {@link ObjectMeta}s are considered equal if, and only if both object types and managers from {@link #manager()}
 * are equal.
 *
 * @author sunqian
 */
@Immutable
public interface ObjectMeta extends DataMeta {

    /**
     * Parse the given type to {@link ObjectMeta} using {@link ObjectMetaManager#defaultManager()}.
     * <p>
     * Note this method never caches the parsed results.
     *
     * @param type the given type
     * @return the {@link ObjectMeta} parsed from the given type using {@link ObjectMetaManager#defaultManager()}
     * @throws DataMetaException if any problem occurs
     */
    static @Nonnull ObjectMeta of(@Nonnull Type type) throws DataMetaException {
        return ObjectMetaManager.defaultManager().introspect(type);
    }

    /**
     * Returns the {@link ObjectMetaManager} where this {@link ObjectMeta} is introspected.
     *
     * @return the {@link ObjectMetaManager} where this {@link ObjectMeta} is introspected
     */
    @Nonnull
    ObjectMetaManager manager();

    /**
     * Returns an immutable map contains all properties meta infos of this {@link ObjectMeta}.
     *
     * @return an immutable map contains all properties meta infos of this {@link ObjectMeta}
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull PropertyMeta> properties();

    /**
     * Returns the specified property meta info with the specified name in this {@link ObjectMeta}.
     *
     * @param name the specified name
     * @return the specified property meta info with the specified name in this {@link ObjectMeta}, or {@code null} if not found
     */
    default @Nullable PropertyMeta getProperty(String name) {
        return properties().get(name);
    }

    @Override
    default boolean isMapMeta() {
        return false;
    }

    @Override
    default boolean isObjectMeta() {
        return true;
    }

    /**
     * Returns whether this {@link ObjectMeta} is equal to the other {@link ObjectMeta}. They are considered equal if,
     * and only if both object types and managers from {@link #manager()} are equal.
     *
     * @param other the other {@link ObjectMeta}
     * @return whether this {@link ObjectMeta} is equal to the other {@link ObjectMeta}
     */
    boolean equals(Object other);

    /**
     * Returns the hash code of this {@link ObjectMeta}. The hash code is generated via {@link #type()} and
     * {@link #manager()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + type().hashCode();
     * result = 31 * result + manager().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link ObjectMeta}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link ObjectMeta}. The string is generated like following codes:
     * <pre>{@code
     * return type().getTypeName() + "[" +
     *     properties().values().stream()
     *         .map(DataProperty::toString)
     *         .collect(Collectors.joining(", "))
     *     + "]";
     * }</pre>
     *
     * @return a string representation of this {@link ObjectMeta}
     */
    @Nonnull
    String toString();
}
