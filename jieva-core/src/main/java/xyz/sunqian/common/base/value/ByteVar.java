package xyz.sunqian.common.base.value;

/**
 * Primitive {@code byte} version of {@link Var}.
 *
 * @author sunqian
 */
public interface ByteVar extends ByteVal, PrimitiveToVar<Byte> {

    /**
     * Returns a {@link ByteVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link ByteVar} initialized with the specified value
     */
    static ByteVar of(byte value) {
        return VarBack.of(value);
    }

    /**
     * Returns a {@link ByteVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link ByteVar} initialized with the specified value
     */
    static ByteVar of(int value) {
        return of((byte) value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    ByteVar set(byte value);

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    default ByteVar set(int value) {
        return set((byte) value);
    }

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    ByteVar add(int value);

    @Override
    default Var<Byte> toVar() {
        return Var.of(get());
    }

    /**
     * Increments current value by one, and returns the result.
     *
     * @return increment result
     */
    byte incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    byte getAndIncrement();
}
