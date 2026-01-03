package space.sunqian.fs.object.data;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;

/**
 * This interface represents the property info of {@link ObjectSchema}. It is very similar to the simple property of
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>.
 * <p>
 * Two {@link ObjectProperty}s are considered equal if, and only if both their names and both their owners are equal.
 *
 * @author sunqian
 */
@Immutable
public interface ObjectProperty extends ObjectPropertyBase {

    /**
     * Returns the owner {@link ObjectSchema} of this property.
     *
     * @return the owner {@link ObjectSchema} of this property
     */
    @Nonnull
    ObjectSchema owner();

    /**
     * Returns whether this property is readable.
     *
     * @return whether this property is readable
     */
    default boolean isReadable() {
        return getter() != null;
    }

    /**
     * Returns whether this property is writable.
     *
     * @return whether this property is writable
     */
    default boolean isWritable() {
        return setter() != null;
    }

    /**
     * Returns the property value of the specified instance.
     *
     * @param inst the specified instance
     * @return the property value of the specified instance
     * @throws DataObjectException if this property is not readable
     */
    default @Nullable Object getValue(@Nonnull Object inst) throws DataObjectException {
        Invocable getter = getter();
        if (getter == null) {
            throw new DataObjectException("The property is not readable: " + name() + ".");
        }
        return getter.invoke(inst);
    }

    /**
     * Sets the property value of the specified instance.
     *
     * @param inst  the specified instance
     * @param value the property value
     * @throws DataObjectException if this property is not writable
     */
    default void setValue(@Nonnull Object inst, @Nullable Object value) throws DataObjectException {
        Invocable setter = setter();
        if (setter == null) {
            throw new DataObjectException("The property is not writable: " + name() + ".");
        }
        setter.invoke(inst, value);
    }

    /**
     * Returns whether this {@link ObjectProperty} is equal to the other {@link ObjectProperty}. They are considered
     * equal if, and only if both their names and both their owners are equal.
     *
     * @param other the other {@link ObjectProperty}
     * @return whether this {@link ObjectProperty} is equal to the other {@link ObjectProperty}
     */
    boolean equals(Object other);

    /**
     * Returns the hash code of this {@link ObjectProperty}. The hash code is generated via {@link #name()} and
     * {@link #owner()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + name().hashCode();
     * result = 31 * result + owner().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link ObjectProperty}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link ObjectProperty}. The string is generated like following codes:
     * <pre>{@code
     * return name() + ": " + type().getTypeName();
     * }</pre>
     *
     * @return a string representation of this {@link ObjectProperty}
     */
    @Nonnull
    String toString();
}
