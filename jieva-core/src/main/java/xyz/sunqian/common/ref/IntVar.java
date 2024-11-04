package xyz.sunqian.common.ref;

/**
 * Int version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface IntVar extends IntVal {

    /**
     * Returns an instance of {@link IntVar} of 0 initialized value.
     *
     * @return an instance of {@link IntVar} of 0 initialized value
     */
    static IntVar ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link IntVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link IntVar} of initialized value
     */
    static IntVar of(int value) {
        return VarImpls.ofInt(value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    IntVar set(int value);

    /**
     * Adds specified value on current value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    IntVar add(int value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    int incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    int getAndIncrement();

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Integer> toWrapper() {
        return Var.of(get());
    }
}
