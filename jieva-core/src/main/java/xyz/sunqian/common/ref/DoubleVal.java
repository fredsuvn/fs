package xyz.sunqian.common.ref;

/**
 * Double version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface DoubleVal {

    /**
     * Returns an instance of {@link DoubleVal} of 0 initialized value.
     *
     * @return an instance of {@link DoubleVal} of 0 initialized value
     */
    static DoubleVal ofZero() {
        return ValImpls.OF_ZERO_DOUBLE;
    }

    /**
     * Returns an instance of {@link DoubleVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link DoubleVal} of initialized value
     */
    static DoubleVal of(double value) {
        return ValImpls.ofDouble(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    double get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Double> toWrapper() {
        return Val.of(get());
    }
}
