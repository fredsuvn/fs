package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.beans.BeanInfo;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the structure of data object, parsed by a {@link DataSchemaParser}, and provides
 * information about data's properties.
 * <p>
 * It is very similar to the {@link BeanInfo} used to describe
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but it only includes
 * simple properties, without indexed properties, events, methods, or other more complex components. And the rules for
 * parsing properties are defined by the implementation of {@link DataSchemaParser}, rather than the public rules.
 * <p>
 * Two {@link DataSchema}s are considered equal if, and only if both the data object's type and {@link DataSchemaParser}
 * are equal.
 *
 * @author sunqian
 */
@Immutable
public interface DataSchema {

    /**
     * Parse the given type to {@link DataSchema} using {@link DataSchemaParser#defaultParser()}.
     * <p>
     * Note that this method does not cache the results and will generate new instances every invocation.
     *
     * @param type the given type
     * @return the {@link DataSchema} parsed from the given type using {@link DataSchemaParser#defaultParser()}
     */
    static DataSchema parse(Type type) {
        return DataSchemaParser.defaultParser().parse(type);
    }

    /**
     * Returns the {@link DataSchemaParser} of this {@link DataSchema}.
     *
     * @return the {@link DataSchemaParser} of this {@link DataSchema}
     */
    @Nonnull
    DataSchemaParser parser();

    /**
     * Returns the type of the data object described by this {@link DataSchema}, typically is an instance of
     * {@link Class} or {@link ParameterizedType}.
     *
     * @return the type of the data object described by this {@link DataSchema}
     */
    @Nonnull
    Type type();

    /**
     * Returns the raw type of the {@link #type()}. The default implementation is:
     * <pre>{@code
     * return Jie.nonnull(TypeKit.getRawClass(type()), Object.class);
     * }</pre>
     *
     * @return the raw type of the {@link #type()}
     */
    default @Nonnull Class<?> rawType() {
        return Jie.nonnull(TypeKit.getRawClass(type()), Object.class);
    }

    /**
     * Returns a map contains all properties of this {@link DataSchema}.
     *
     * @return a map contains all properties of this {@link DataSchema}
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull DataProperty> properties();

    /**
     * Returns the specified property with the specified name in this {@link DataSchema}.
     *
     * @param name the specified name
     * @return the specified property with the specified name in this {@link DataSchema}
     */
    default @Nullable DataProperty getProperty(String name) {
        return properties().get(name);
    }

    /**
     * Returns whether this {@link DataSchema} is equal to the other {@link DataSchema}. They are considered equal if,
     * and only if both the data object's type and {@link DataSchemaParser} are equal.
     *
     * @param other the other {@link DataSchema}
     * @return whether this {@link DataSchema} is equal to the other {@link DataSchema}
     */
    boolean equals(@Nullable Object other);

    /**
     * Returns the hash code of this {@link DataSchema}. The hash code is generated via {@link #type()} and
     * {@link #parser()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + type().hashCode();
     * result = 31 * result + parser().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link DataSchema}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link DataSchema}. The string is generated like following codes:
     * <pre>{@code
     * return type().getTypeName() + "[" +
     *     properties().values().stream()
     *         .map(DataProperty::toString)
     *         .collect(Collectors.joining(", "))
     *     + "]";
     * }</pre>
     *
     * @return a string representation of this {@link DataSchema}
     */
    @Nonnull
    String toString();
}
