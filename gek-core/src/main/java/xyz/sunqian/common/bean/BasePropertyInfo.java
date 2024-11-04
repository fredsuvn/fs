package xyz.sunqian.common.bean;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Base info of {@link PropertyInfo}.
 *
 * @author fredsuvn
 */
@Immutable
public interface BasePropertyInfo {

    /**
     * Returns name of this property.
     *
     * @return name of this property
     */
    String getName();

    /**
     * Returns property value of given bean.
     *
     * @param bean given bean
     * @return property value of given bean
     */
    @Nullable
    Object getValue(Object bean);

    /**
     * Sets property value of given bean.
     *
     * @param bean  given bean
     * @param value property value
     */
    void setValue(Object bean, @Nullable Object value);

    /**
     * Returns type of this property.
     *
     * @return type of this property
     */
    Type getType();

    /**
     * Returns raw type of this property:
     * <pre>
     *     return JieReflect.getRawType(getType());
     * </pre>
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
     * @return getter method of this property, or null
     */
    @Nullable
    Method getGetter();

    /**
     * Returns setter method of this property, or null if it doesn't exist.
     *
     * @return setter method of this property, or null
     */
    @Nullable
    Method getSetter();

    /**
     * Returns backing field of this property, or null if it doesn't exist.
     *
     * @return backing field of this property, or null
     */
    @Nullable
    Field getField();

    /**
     * Returns annotations on backing field.
     *
     * @return annotations on backing field
     */
    List<Annotation> getFieldAnnotations();

    /**
     * Returns annotations on getter.
     *
     * @return annotations on getter
     */
    List<Annotation> getGetterAnnotations();

    /**
     * Returns annotations on setter.
     *
     * @return annotations on setter
     */
    List<Annotation> getSetterAnnotations();

    /**
     * Returns annotations on getter, setter and backing field (in this order).
     *
     * @return annotations on getter, setter and backing field
     */
    List<Annotation> getAnnotations();

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
