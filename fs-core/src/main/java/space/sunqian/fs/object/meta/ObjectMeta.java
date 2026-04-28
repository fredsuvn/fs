package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.beans.BeanInfo;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the schema of non-map object, parsed by a {@link ObjectMetaManager}, and provides
 * information about the object's properties.
 * <p>
 * It is very similar to the {@link BeanInfo} used to describe
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but it only includes
 * simple properties, without indexed properties, events, methods, or other more complex components. And the rules for
 * parsing properties are defined by the implementation of {@link ObjectMetaManager}, rather than a public rules.
 * <p>
 * Two {@link ObjectMeta}s are considered equal if, and only if both types of objects and both parsers from
 * {@link #parser()} are equal.
 *
 * @author sunqian
 */
@Immutable
public interface ObjectMeta extends DataMeta {

    /**
     * Parse the given type to {@link ObjectMeta} using {@link ObjectMetaManager#defaultParser()}.
     * <p>
     * Note this method never caches the parsed results.
     *
     * @param type the given type
     * @return the {@link ObjectMeta} parsed from the given type using {@link ObjectMetaManager#defaultParser()}
     * @throws DataMetaException if any problem occurs
     */
    static @Nonnull ObjectMeta parse(@Nonnull Type type) throws DataMetaException {
        return ObjectMetaManager.defaultParser().parse(type);
    }

    /**
     * Returns the {@link ObjectMetaManager} of this {@link ObjectMeta}.
     *
     * @return the {@link ObjectMetaManager} of this {@link ObjectMeta}
     */
    @Nonnull
    ObjectMetaManager parser();

    /**
     * Returns a map contains all properties of this {@link ObjectMeta}.
     *
     * @return a map contains all properties of this {@link ObjectMeta}
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull PropertyMetaMeta> properties();

    /**
     * Returns the specified property with the specified name in this {@link ObjectMeta}.
     *
     * @param name the specified name
     * @return the specified property with the specified name in this {@link ObjectMeta}
     */
    default @Nullable PropertyMetaMeta getProperty(String name) {
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
     * Returns whether this {@link ObjectMeta} is equal to the other {@link ObjectMeta}. They are considered equal
     * if, and only if both types of objects and both parsers from {@link #parser()} are equal.
     *
     * @param other the other {@link ObjectMeta}
     * @return whether this {@link ObjectMeta} is equal to the other {@link ObjectMeta}
     */
    boolean equals(Object other);

    /**
     * Returns the hash code of this {@link ObjectMeta}. The hash code is generated via {@link #type()} and
     * {@link #parser()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + type().hashCode();
     * result = 31 * result + parser().hashCode();
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
