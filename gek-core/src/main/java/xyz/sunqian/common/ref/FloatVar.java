package xyz.sunqian.common.ref;

/**
 * Float version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface FloatVar extends FloatVal {

    /**
     * Returns an instance of {@link FloatVar} of 0 initialized value.
     *
     * @return an instance of {@link FloatVar} of 0 initialized value
     */
    static FloatVar ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link FloatVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link FloatVar} of initialized value
     */
    static FloatVar of(float value) {
        return VarImpls.ofFloat(value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    FloatVar set(float value);

    /**
     * Adds specified value on current value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    FloatVar add(float value);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Float> toWrapper() {
        return Var.of(get());
    }
}
