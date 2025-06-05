package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;

/**
 * Primitive {@code double} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface DoubleVal extends PrimitiveToVal<Double> {

    /**
     * Returns a {@link DoubleVal} holding the {@code 0}.
     *
     * @return a {@link DoubleVal} holding the {@code 0}
     */
    static @Nonnull DoubleVal ofZero() {
        return ValImpls.OF_ZERO_DOUBLE;
    }

    /**
     * Returns a {@link DoubleVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link DoubleVal} holding the specified value
     */
    static @Nonnull DoubleVal of(double value) {
        return ValImpls.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    double get();

    @Override
    default @Nonnull Val<Double> toVal() {
        return Val.of(get());
    }
}
