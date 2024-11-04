package xyz.sunqian.common.ref;

/**
 * Double version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface DoubleVar extends DoubleVal {

    /**
     * Returns an instance of {@link DoubleVar} of 0 initialized value.
     *
     * @return an instance of {@link DoubleVar} of 0 initialized value
     */
    static DoubleVar ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link DoubleVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link DoubleVar} of initialized value
     */
    static DoubleVar of(double value) {
        return VarImpls.ofDouble(value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    DoubleVar set(double value);

    /**
     * Adds specified value on current value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    DoubleVar add(double value);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Double> toWrapper() {
        return Var.of(get());
    }
}
