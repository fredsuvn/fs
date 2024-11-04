package xyz.sunqian.common.ref;

/**
 * Boolean version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface BooleanVar extends BooleanVal {

    /**
     * Returns an instance of {@link BooleanVar} of false initialized value.
     *
     * @return an instance of {@link BooleanVar} of false initialized value
     */
    static BooleanVar ofFalse() {
        return of(false);
    }

    /**
     * Returns an instance of {@link BooleanVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link BooleanVar} of initialized value
     */
    static BooleanVar of(boolean value) {
        return VarImpls.ofBoolean(value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    BooleanVar set(boolean value);

    /**
     * Toggles current value, and returns the result.
     *
     * @return toggle result
     */
    boolean toggleAndGet();

    /**
     * Toggles current value, and returns the old value before toggling.
     *
     * @return old value before toggling
     */
    boolean getAndToggle();

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Boolean> toWrapper() {
        return Var.of(get());
    }
}
