package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;

/**
 * This interface represents the property info of {@link DataSchema}. It is very similar to the simple property of
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>.
 * <p>
 * Two {@link DataProperty}s are considered equal if and only if both the property's name and owner are equal.
 *
 * @author sunqian
 */
@Immutable
public interface DataProperty extends DataPropertyBase {

    /**
     * Returns owner {@link DataSchema} of this property.
     *
     * @return owner {@link DataSchema} of this property
     */
    DataSchema getOwner();

    /**
     * Returns whether this {@link DataProperty} is equal to specified other {@link DataProperty}. They are considered
     * equal if and only if both the data object's name and owner are equal.
     *
     * @param other specified other {@link DataProperty}
     * @return whether this {@link DataProperty} is equal to specified other {@link DataProperty}
     */
    boolean equals(@Nullable Object other);

    /**
     * Returns hash code of this {@link DataProperty}. The hash code is generated via {@link #getName()} and
     * {@link #getOwner()} like following codes:
     * <pre>{@code
     *     int result = 1;
     *     result = 31 * result + getName().hashCode();
     *     result = 31 * result + getOwner().hashCode();
     *     return result;
     * }</pre>
     *
     * @return hash code of this {@link DataProperty}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link DataProperty}. The string is generated like following codes:
     * <pre>{@code
     *     return "property[" +
     *             "name=" + getName() + ", " +
     *             "type=" + getType().getTypeName() + ", " +
     *             "ownerType=" + getOwner().getType().getTypeName() +
     *             "]";
     * }</pre>
     *
     * @return a string representation of this {@link DataProperty}
     */
    String toString();
}
