package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.CachedResult;
import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.JieReflect;

import java.beans.BeanInfo;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface represents the structure of the data object, parsed by a {@link DataSchemaParser}, and provides
 * information about the properties of this structure.
 * <p>
 * It is very similar to the {@link BeanInfo} used to describe
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but it only includes
 * simple properties, without indexed properties, events, methods, or other more complex components. And the rules for
 * parsing properties are defined by the implementation of {@link DataSchemaParser}, rather than a public set of rules.
 * <p>
 * Two {@link DataSchema}s are considered equal if and only if both the data object's type and {@link DataSchemaParser}
 * are equal.
 *
 * @author sunqian
 */
@Immutable
public interface DataSchema {

    /**
     * Returns the {@link DataSchema} parsed from the specified type using {@link DataSchemaParser#defaultParser()}.
     *
     * @param type specified type
     * @return the {@link DataSchema} parsed from the specified type using {@link DataSchemaParser#defaultParser()}
     */
    @CachedResult
    static DataSchema get(Type type) {
        return get(type, null);
    }

    /**
     * Returns the {@link DataSchema} parsed from the specified type using specified {@link DataSchemaParser}.
     *
     * @param type   specified type
     * @param parser specified {@link DataSchemaParser}
     * @return the {@link DataSchema} parsed from the specified type using specified {@link DataSchemaParser}
     */
    @CachedResult
    static DataSchema get(Type type, @Nullable DataSchemaParser parser) {
        return DataObjectBack.getDataSchema(type, parser);
    }

    /**
     * Returns the {@link DataSchemaParser} of this {@link DataSchema}.
     *
     * @return the {@link DataSchemaParser} of this {@link DataSchema}
     */
    DataSchemaParser getParser();

    /**
     * Returns type of the data object represented by this {@link DataSchema}, typically is an instance of {@link Class}
     * or {@link ParameterizedType}.
     *
     * @return type of the data object represented by this {@link DataSchema}
     */
    Type getType();

    /**
     * Returns raw type of the {@link #getType()}.
     *
     * @return raw type of the {@link #getType()}
     */
    default Class<?> getRawType() {
        return JieReflect.getRawType(getType());
    }

    /**
     * Returns a map contains all properties information of this {@link DataSchema}.
     *
     * @return a map contains all properties information of this {@link DataSchema}
     */
    @Immutable
    Map<String, DataProperty> getProperties();

    /**
     * Returns property info with specified name in this {@link DataSchema}.
     *
     * @param name specified name
     * @return property info with specified name in this {@link DataSchema}
     */
    @Nullable
    default DataProperty getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * Returns whether this {@link DataSchema} is equal to the other {@link DataSchema}. They are considered equal if
     * and only if both the data object's type and {@link DataSchemaParser} are equal.
     *
     * @param other the other {@link DataSchema}
     * @return whether this {@link DataSchema} is equal to the other {@link DataSchema}
     */
    boolean equals(@Nullable Object other);

    /**
     * Returns hash code of this {@link DataSchema}. The hash code is generated via {@link #getType()} and
     * {@link #getParser()} like following codes:
     * <pre>{@code
     *     int result = 1;
     *     result = 31 * result + getType().hashCode();
     *     result = 31 * result + getParser().hashCode();
     *     return result;
     * }</pre>
     *
     * @return hash code of this {@link DataSchema}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link DataSchema}. The string is generated like following codes:
     * <pre>{@code
     *     return "data[" +
     *             "type=" + getType().getTypeName() + ", " +
     *             "properties=[" +
     *             getProperties().values().stream().map(it ->
     *                 it.getName() + ": " + it.getType().getTypeName()
     *             ).collect(Collectors.joining("; ")) +
     *             "]]";
     * }</pre>
     *
     * @return a string representation of this {@link DataSchema}
     */
    String toString();
}
