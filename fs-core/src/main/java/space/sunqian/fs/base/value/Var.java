package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;

/**
 * This interface represents a mutable container holding a value, typically used for sharing values across scope
 * boundaries (e.g., lambda expressions).
 * <p>
 * There are also primitive type versions: {@link BooleanVar}, {@link ByteVar}, {@link CharVar}, {@link ShortVar},
 * {@link IntVar}, {@link LongVar}, {@link DoubleVar} and {@link FloatVar}.
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
    static <T> @Nonnull Var<T> of(T value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this {@link Var} itself.
     *
     * @param value the specified value
     * @return this {@link Var} itself
     */
    @Nonnull
    Var<T> set(T value);

    /**
     * Clears the value to {@code null}.
     *
     * @return this {@link Var} itself
     */
    default Var<T> clear() {
        return set(null);
    }
}
