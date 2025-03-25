package xyz.sunqian.common.base.value;

/**
 * Primitive {@code short} version of {@link Var}.
 *
 * @author sunqian
 */
public interface ShortVar extends ShortVal, PrimitiveToVar<Short> {

    /**
     * Returns a {@link ShortVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link ShortVar} initialized with the specified value
     */
    static ShortVar of(short value) {
        return VarBack.of(value);
    }

    /**
     * Returns a {@link ShortVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link ShortVar} initialized with the specified value
     */
    static ShortVar of(int value) {
        return of((short) value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    ShortVar set(short value);

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    default ShortVar set(int value) {
        return set((short) value);
    }

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    ShortVar add(int value);

    @Override
    default Var<Short> toVar() {
        return Var.of(get());
    }

    /**
     * Increments current value by one, and returns the result.
     *
     * @return increment result
     */
    short incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    short getAndIncrement();
}
