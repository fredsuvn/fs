package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface represents the introspection info for an object property, and typically provides introspection
 * functions for generating {@link PropertyDef}.
 *
 * @author sunqian
 */
@Immutable
public interface PropertyIntro {

    /**
     * Returns name of this property.
     *
     * @return name of this property
     */
    String getName();

    /**
     * Returns property value of specified instance.
     *
     * @param inst specified instance
     * @return property value of specified instance
     */
    @Nullable
    Object getValue(Object inst);

    /**
     * Sets property value of specified instance.
     *
     * @param inst  specified instance
     * @param value property value
     */
    void setValue(Object inst, @Nullable Object value);

    /**
     * Returns type of this property.
     *
     * @return type of this property
     */
    Type getType();

    /**
     * Returns raw type of this property:
     * <pre>{@code
     *     return JieReflect.getRawType(getType());
     * }</pre>
     *
     * @return raw type of this property
     */
    @Nullable
    default Class<?> getRawType() {
        return JieReflect.getRawType(getType());
    }

    /**
     * Returns getter method of this property, or null if it doesn't exist.
     *
     * @return getter method of this property, or null if it doesn't exist
     */
    @Nullable
    Method getGetter();

    /**
     * Returns setter method of this property, or null if it doesn't exist.
     *
     * @return setter method of this property, or null if it doesn't exist
     */
    @Nullable
    Method getSetter();

    /**
     * Returns the field backing this property, or null if it doesn't exist.
     *
     * @return the field backing this property, or null if it doesn't exist
     */
    @Nullable
    Field getField();

    /**
     * Returns annotations on the field backing this property, may be empty if none exist.
     *
     * @return annotations on the field backing this property, may be empty if none exist
     */
    List<Annotation> getFieldAnnotations();

    /**
     * Returns annotations on getter method, may be empty if none exist.
     *
     * @return annotations on getter method, may be empty if none exist
     */
    List<Annotation> getGetterAnnotations();

    /**
     * Returns annotations on setter method, may be empty if none exist.
     *
     * @return annotations on setter method, may be empty if none exist
     */
    List<Annotation> getSetterAnnotations();

    /**
     * Returns annotations on this property, including those on the getter method, setter method and field backing this
     * property, in that order. It is similar to a combination of {@link #getGetterAnnotations()},
     * {@link #getSetterAnnotations()}, and {@link #getFieldAnnotations()}.
     *
     * @return annotations on this property, may be empty if none exist
     */
    List<Annotation> getAnnotations();

    /**
     * Returns annotation of specified type on this property, or null if it doesn't exist. The search behavior is
     * equivalent to that in the {@link #getAnnotations()}.
     *
     * @param type specified type
     * @param <A>  type of annotation
     * @return annotation of specified type on this property, or null if it doesn't exist
     */
    @Nullable
    <A extends Annotation> A getAnnotation(Class<A> type);

    /**
     * Returns whether this property is readable.
     *
     * @return whether this property is readable
     */
    boolean isReadable();

    /**
     * Returns whether this property is writeable.
     *
     * @return whether this property is writeable
     */
    boolean isWriteable();
}
