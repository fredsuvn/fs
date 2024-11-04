package xyz.sunqian.common.ref;

/**
 * Byte version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface ByteVar extends ByteVal {

    /**
     * Returns an instance of {@link ByteVar} of 0 initialized value.
     *
     * @return an instance of {@link ByteVar} of 0 initialized value
     */
    static ByteVar ofZero() {
        return of((byte) 0);
    }

    /**
     * Returns an instance of {@link ByteVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ByteVar} of initialized value
     */
    static ByteVar of(byte value) {
        return VarImpls.ofByte(value);
    }

    /**
     * Returns an instance of {@link ByteVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ByteVar} of initialized value
     */
    static ByteVar of(int value) {
        return of((byte) value);
    }

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    ByteVar set(byte value);

    /**
     * Sets specified value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    default ByteVar set(int value) {
        return set((byte) value);
    }

    /**
     * Adds specified value on current value for this ref and returns this ref itself.
     *
     * @param value specified value
     * @return this ref itself
     */
    ByteVar add(int value);

    /**
     * Increments current value by one, and returns the result.
     *
     * @return toggle result
     */
    byte incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    byte getAndIncrement();

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Byte> toWrapper() {
        return Var.of(get());
    }
}
