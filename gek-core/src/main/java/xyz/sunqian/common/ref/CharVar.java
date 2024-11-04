package xyz.sunqian.common.ref;

/**
 * Char version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface CharVar extends CharVal {

    /**
     * Returns an instance of {@link CharVar} of 0 initialized value.
     *
     * @return an instance of {@link CharVar} of 0 initialized value
     */
    static CharVar ofZero() {
        return of((char) 0);
    }

    /**
     * Returns an instance of {@link CharVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link CharVar} of initialized value
     */
    static CharVar of(char value) {
        return VarImpls.ofChar(value);
    }

    /**
     * Returns an instance of {@link CharVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link CharVar} of initialized value
     */
    static CharVar of(int value) {
        return of((char) value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    CharVar set(char value);

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    default CharVar set(int value) {
        return set((char) value);
    }

    /**
     * Adds specified value on current value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    CharVar add(int value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    char incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    char getAndIncrement();

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Character> toWrapper() {
        return Var.of(get());
    }
}
