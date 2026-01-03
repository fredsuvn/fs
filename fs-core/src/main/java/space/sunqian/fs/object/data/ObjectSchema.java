package space.sunqian.fs.object.data;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.beans.BeanInfo;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the schema of non-map object, parsed by a {@link ObjectSchemaParser}, and provides
 * information about the object's properties.
 * <p>
 * It is very similar to the {@link BeanInfo} used to describe
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but it only includes
 * simple properties, without indexed properties, events, methods, or other more complex components. And the rules for
 * parsing properties are defined by the implementation of {@link ObjectSchemaParser}, rather than a public rules.
 * <p>
 * Two {@link ObjectSchema}s are considered equal if, and only if both types of objects and both parsers from
 * {@link #parser()} are equal.
 *
 * @author sunqian
 */
@Immutable
public interface ObjectSchema extends DataSchema {

    /**
     * Parse the given type to {@link ObjectSchema} using {@link ObjectSchemaParser#defaultParser()}.
     * <p>
     * Note that this method does not cache the results and will generate new instances every invocation.
     *
     * @param type the given type
     * @return the {@link ObjectSchema} parsed from the given type using {@link ObjectSchemaParser#defaultParser()}
     */
    static @Nonnull ObjectSchema parse(@Nonnull Type type) {
        return ObjectSchemaParser.defaultParser().parse(type);
    }

    /**
     * Returns the {@link ObjectSchemaParser} of this {@link ObjectSchema}.
     *
     * @return the {@link ObjectSchemaParser} of this {@link ObjectSchema}
     */
    @Nonnull
    ObjectSchemaParser parser();

    /**
     * Returns a map contains all properties of this {@link ObjectSchema}.
     *
     * @return a map contains all properties of this {@link ObjectSchema}
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull ObjectProperty> properties();

    /**
     * Returns the specified property with the specified name in this {@link ObjectSchema}.
     *
     * @param name the specified name
     * @return the specified property with the specified name in this {@link ObjectSchema}
     */
    default @Nullable ObjectProperty getProperty(String name) {
        return properties().get(name);
    }

    @Override
    default boolean isMapSchema() {
        return false;
    }

    @Override
    default boolean isObjectSchema() {
        return true;
    }

    /**
     * Returns whether this {@link ObjectSchema} is equal to the other {@link ObjectSchema}. They are considered equal
     * if, and only if both types of objects and both parsers from {@link #parser()} are equal.
     *
     * @param other the other {@link ObjectSchema}
     * @return whether this {@link ObjectSchema} is equal to the other {@link ObjectSchema}
     */
    boolean equals(Object other);

    /**
     * Returns the hash code of this {@link ObjectSchema}. The hash code is generated via {@link #type()} and
     * {@link #parser()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + type().hashCode();
     * result = 31 * result + parser().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link ObjectSchema}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link ObjectSchema}. The string is generated like following codes:
     * <pre>{@code
     * return type().getTypeName() + "[" +
     *     properties().values().stream()
     *         .map(DataProperty::toString)
     *         .collect(Collectors.joining(", "))
     *     + "]";
     * }</pre>
     *
     * @return a string representation of this {@link ObjectSchema}
     */
    @Nonnull
    String toString();
}
