package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;

import java.lang.annotation.Annotation;
import java.util.List;

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
     * Returns the annotations on the backing field of this property. If this property doesn't have a backing field, an
     * empty list will be returned.
     *
     * @return the annotations on the backing field of this property
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> fieldAnnotations();

    /**
     * Returns the annotations on the getter method of this property. If this property doesn't have a getter method, an
     * empty list will be returned.
     *
     * @return the annotations on the getter method of this property
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> getterAnnotations();

    /**
     * Returns the annotations on the setter method of this property. If this property doesn't have a setter method, an
     * empty list will be returned.
     *
     * @return the annotations on the setter method of this property
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> setterAnnotations();

    /**
     * Returns the property value of the specified instance.
     *
     * @param inst the specified instance
     * @return the property value of the specified instance
     * @throws DataSchemaException if this property is not readable
     */
    default @Nullable Object getValue(@Nonnull Object inst) throws DataSchemaException {
        Invocable getter = getter();
        if (getter == null) {
            throw new DataSchemaException("The property is not readable: " + name() + ".");
        }
        return getter.invoke(inst);
    }

    /**
     * Sets the property value of the specified instance.
     *
     * @param inst  the specified instance
     * @param value the property value
     * @throws DataSchemaException if this property is not writable
     */
    default void setValue(@Nonnull Object inst, @Nullable Object value) throws DataSchemaException {
        Invocable setter = setter();
        if (setter == null) {
            throw new DataSchemaException("The property is not writable: " + name() + ".");
        }
        setter.invoke(inst, value);
    }

    /**
     * Finds and returns the annotation of the specified type on getter method, setter method or backing field of this
     * property, the searching order is that order. If the annotation is not found, {@code null} will be returned.
     *
     * @param annotationType the specified annotation type
     * @return the annotation of the specified type on this property, or {@code null} if not found
     */
    default <T extends Annotation> @Nullable T getAnnotation(@Nonnull Class<T> annotationType) {
        List<Annotation> getters = getterAnnotations();
        for (Annotation g : getters) {
            if (annotationType.isAssignableFrom(g.getClass())) {
                return annotationType.cast(g);
            }
        }
        List<Annotation> setters = setterAnnotations();
        for (Annotation s : setters) {
            if (annotationType.isAssignableFrom(s.getClass())) {
                return annotationType.cast(s);
            }
        }
        List<Annotation> fields = fieldAnnotations();
        for (Annotation f : fields) {
            if (annotationType.isAssignableFrom(f.getClass())) {
                return annotationType.cast(f);
            }
        }
        return null;
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
