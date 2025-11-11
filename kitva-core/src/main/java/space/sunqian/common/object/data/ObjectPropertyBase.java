package space.sunqian.common.object.data;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Kit;
import space.sunqian.common.runtime.invoke.Invocable;
import space.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * This interface provides the base info for {@link ObjectProperty}, typically used in the parsing process of
 * {@link ObjectSchema}.
 *
 * @author sunqian
 */
@Immutable
public interface ObjectPropertyBase {

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
     * return Kit.nonnull(TypeKit.getRawClass(type()), Object.class);
     * }</pre>
     *
     * @return the raw type of this property
     */
    default @Nonnull Class<?> rawType() {
        return Kit.nonnull(TypeKit.getRawClass(type()), Object.class);
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
