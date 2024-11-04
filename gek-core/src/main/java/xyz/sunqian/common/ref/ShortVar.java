package xyz.sunqian.common.ref;

/**
 * Short version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface ShortVar extends ShortVal {

    /**
     * Returns an instance of {@link ShortVar} of 0 initialized value.
     *
     * @return an instance of {@link ShortVar} of 0 initialized value
     */
    static ShortVar ofZero() {
        return of((short) 0);
    }

    /**
     * Returns an instance of {@link ShortVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ShortVar} of initialized value
     */
    static ShortVar of(short value) {
        return VarImpls.ofShort(value);
    }

    /**
     * Returns an instance of {@link ShortVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ShortVar} of initialized value
     */
    static ShortVar of(int value) {
        return of((short) value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    ShortVar set(short value);

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    default ShortVar set(int value) {
        return set((short) value);
    }

    /**
     * Adds specified value on current value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    ShortVar add(int value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    short incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    short getAndIncrement();

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Short> toWrapper() {
        return Var.of(get());
    }
}
