package xyz.sunqian.common.base.value;

/**
 * Primitive {@code double} version of {@link Val}.
 *
 * @author sunqian
 */
public interface DoubleVal extends PrimitiveToVal<Double> {

    /**
     * Returns a {@link DoubleVal} holding the {@code 0}.
     *
     * @return a {@link DoubleVal} holding the {@code 0}
     */
    static DoubleVal ofZero() {
        return ValBack.OF_ZERO_DOUBLE;
    }

    /**
     * Returns a {@link DoubleVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link DoubleVal} holding the specified value
     */
    static DoubleVal of(double value) {
        return ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    double get();

    @Override
    default Val<Double> toVal() {
        return Val.of(get());
    }
}
