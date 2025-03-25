package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;

/**
 * This interface represents an immutable container holding a value, similar to a {@code final} variable. There are also
 * primitive type versions: {@link BooleanVal}, {@link ByteVal}, {@link CharVal}, {@link ShortVal}, {@link IntVal},
 * {@link LongVal}, {@link DoubleVal} and {@link FloatVal}.
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
    static <T> Val<T> ofNull() {
        return Jie.as(ValBack.OF_NULL);
    }

    /**
     * Returns a {@link Val} holding the specified value.
     *
     * @param value the specified value
     * @param <T>   type of the held value
     * @return a {@link Val} holding the specified value
     */
    static <T> Val<T> of(@Nullable T value) {
        return ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    @Nullable
    T get();
}
