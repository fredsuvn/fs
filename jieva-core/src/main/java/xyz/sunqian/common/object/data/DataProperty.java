package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This interface represents the property info of {@link DataSchema}. It is very similar to the simple property of
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>.
 * <p>
 * Two {@link DataProperty}s are considered equal if, and only if both the property's name and owner are equal.
 *
 * @author sunqian
 */
@Immutable
public interface DataProperty extends DataPropertyBase {

    /**
     * Returns the owner {@link DataSchema} of this property.
     *
     * @return the owner {@link DataSchema} of this property
     */
    @Nonnull
    DataSchema owner();

    /**
     * Returns whether this property is readable.
     *
     * @return whether this property is readable
     */
    default boolean isReadable() {
        return getter() != null;
    }

    /**
     * Returns whether this property is writeable.
     *
     * @return whether this property is writeable
     */
    default boolean isWriteable() {
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
     * @throws DataObjectException if this property is not writeable
     */
    default void setValue(@Nonnull Object inst, @Nullable Object value) throws DataObjectException {
        Invocable setter = setter();
        if (setter == null) {
            throw new DataObjectException("The property is not writeable: " + name() + ".");
        }
        setter.invoke(inst, value);
    }

    /**
     * Returns all annotations on the field backing this property, may be empty if none exist.
     *
     * @return all annotations on the field backing this property, may be empty if none exist
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> fieldAnnotations();

    /**
     * Returns all annotations on getter method, may be empty if none exist.
     *
     * @return all annotations on getter method, may be empty if none exist
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> getterAnnotations();

    /**
     * Returns all annotations on setter method, may be empty if none exist.
     *
     * @return all annotations on setter method, may be empty if none exist
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> setterAnnotations();

    /**
     * Returns all annotations on this property, a combination of {@link #fieldAnnotations()},
     * {@link #getterAnnotations()}, and {@link #setterAnnotations()}, int this order.
     *
     * @return all annotations on this property
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> annotations();

    /**
     * Returns the annotation of the specified type on this property, or {@code null} if it doesn't exist. The searching
     * in the iterative order of the {@link #annotations()}.
     *
     * @param type the specified type
     * @param <A>  the type of the annotation
     * @return the annotation of the specified type on this property, or {@code null} if it doesn't exist
     */
    @Nullable
    default <A extends Annotation> A getAnnotation(Class<A> type) {
        for (Annotation annotation : annotations()) {
            if (type.isInstance(annotation)) {
                return Jie.as(annotation);
            }
        }
        return null;
    }

    /**
     * Returns whether this {@link DataProperty} is equal to the other {@link DataProperty}. They are considered equal
     * if, and only if both the data object's name and owner are equal.
     *
     * @param other the other {@link DataProperty}
     * @return whether this {@link DataProperty} is equal to the other {@link DataProperty}
     */
    boolean equals(@Nullable Object other);

    /**
     * Returns the hash code of this {@link DataProperty}. The hash code is generated via {@link #name()} and
     * {@link #owner()} like following codes:
     * <pre>{@code
     * int result = 1;
     * result = 31 * result + name().hashCode();
     * result = 31 * result + owner().hashCode();
     * return result;
     * }</pre>
     *
     * @return the hash code of this {@link DataProperty}
     */
    int hashCode();

    /**
     * Returns a string representation of this {@link DataProperty}. The string is generated like following codes:
     * <pre>{@code
     * return "property[" +
     *     "name=" + name() + ", " +
     *     "type=" + type().typeName() + ", " +
     *     "ownerType=" + owner().type().typeName() +
     *     "]";
     * }</pre>
     *
     * @return a string representation of this {@link DataProperty}
     */
    @Nonnull
    String toString();
}
