package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.runtime.invoke.Invocable;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface provides the base info for {@link DataProperty}, typically used in the parsing process of
 * {@link DataSchema}.
 *
 * @author sunqian
 */
@Immutable
public interface DataPropertyBase {

    /**
     * Returns the name of this property.
     *
     * @return the name of this property
     */
    @Nonnull
    String name();

    /**
     * Returns the type of this property.
     *
     * @return the type of this property
     */
    @Nonnull
    Type type();

    /**
     * Returns the raw type of this property. The default implementation is:
     * <pre>{@code
     * return Jie.nonnull(TypeKit.getRawClass(type()), Object.class);
     * }</pre>
     *
     * @return the raw type of this property
     */
    default @Nonnull Class<?> rawType() {
        return Jie.nonnull(TypeKit.getRawClass(type()), Object.class);
    }

    /**
     * Returns the method backing the {@link #getter()}, or {@code null} if it doesn't have a getter.
     *
     * @return the method backing the {@link #getter()}, or {@code null} if it doesn't have a getter
     */
    @Nullable
    Method getterMethod();

    /**
     * Returns the method backing the {@link #setter()}, or {@code null} if it doesn't have a setter.
     *
     * @return the method backing the {@link #setter()}, or {@code null} if it doesn't have a setter
     */
    @Nullable
    Method setterMethod();

    /**
     * Returns the field backing this property, or {@code null} if it doesn't exist.
     *
     * @return the field backing this property, or {@code null} if it doesn't exist
     */
    @Nullable
    Field field();

    /**
     * Returns an invocable getter to read the value of this property, or {@code null} if it doesn't have a getter.
     *
     * @return an invocable getter to read the value of this property, or {@code null} if it doesn't have a getter
     */
    @Nullable
    Invocable getter();

    /**
     * Returns an invocable setter to write the value of this property, or {@code null} if it doesn't have a setter.
     *
     * @return an invocable setter to write the value of this property, or {@code null} if it doesn't have a setter
     */
    @Nullable
    Invocable setter();
}
