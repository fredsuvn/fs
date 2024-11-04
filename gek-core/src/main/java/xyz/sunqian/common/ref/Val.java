package xyz.sunqian.common.ref;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;

/**
 * This interface represents an immutable value reference. It also has versions for primitive types: {@link BooleanVal},
 * {@link ByteVal}, {@link CharVal}, {@link ShortVal}, {@link IntVal}, {@link LongVal}, {@link DoubleVal} and
 * {@link FloatVal}.
 *
 * @param <T> type of referenced value
 * @author fredsuvn
 */
public interface Val<T> {

    /**
     * Return an instance of {@link Val} of null initialized value.
     *
     * @param <T> type of initialized value
     * @return an instance of {@link Val} of null initialized value
     */
    static <T> Val<T> ofNull() {
        return Jie.as(ValImpls.OF_NULL);
    }

    /**
     * Returns an instance of {@link Val} of initialized value.
     *
     * @param value initialized value
     * @param <T>   type of initialized value
     * @return an instance of {@link Val} of initialized value
     */
    static <T> Val<T> of(@Nullable T value) {
        return ValImpls.of(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    @Nullable
    T get();
}
