package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Kit;

/**
 * This interface represents an immutable container holding a value, similar to a {@code final} variable.
 * <p>
 * There are also primitive type versions: {@link BooleanVal}, {@link ByteVal}, {@link CharVal}, {@link ShortVal},
 * {@link IntVal}, {@link LongVal}, {@link DoubleVal} and {@link FloatVal}.
 *
 * @param <T> type of the held value
 * @author sunqian
 */
@Immutable
public interface Val<T> {

    /**
     * Returns a {@link Val} holding the {@code null}.
     *
     * @param <T> type of the held value
     * @return a {@link Val} holding the {@code null}
     */
    static <T> @Nonnull Val<T> ofNull() {
        return Kit.as(ValBack.OF_NULL);
    }

    /**
     * Returns a {@link Val} holding the specified value. If the specified value is {@code null}, returns
     * {@link #ofNull()}.
     *
     * @param value the specified value
     * @param <T>   type of the held value
     * @return a {@link Val} holding the specified value
     */
    static <T> @Nonnull Val<T> of(T value) {
        return value == null ? ofNull() : ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    T get();
}
