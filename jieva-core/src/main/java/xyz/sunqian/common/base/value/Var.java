package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Nullable;

/**
 * This interface represents a mutable container holding a value. There are also primitive type versions:
 * {@link BooleanVar}, {@link ByteVar}, {@link CharVar}, {@link ShortVar}, {@link IntVar}, {@link LongVar},
 * {@link DoubleVar} and {@link FloatVar}.
 *
 * @param <T> type of the held value
 * @author sunqian
 */
public interface Var<T> extends Val<T> {

    /**
     * Returns a {@link Var} initialized with the specified value.
     *
     * @param value the specified value
     * @param <T>   type of the held value
     * @return a {@link Var} initialized with the specified value
     */
    static <T> Var<T> of(@Nullable T value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value of type {@code R}. Returns this {@link Var} itself, but its generic
     * type is now {@code Var<R>}.
     *
     * @param value the specified value
     * @param <R>   type of the specified value
     * @return this {@link Var} itself, but its generic type is now {@code Var<R>}
     */
    <R> Var<R> set(@Nullable R value);
}
