package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;

/**
 * Primitive {@code short} version of {@link Var}.
 *
 * @author sunqian
 */
public interface ShortVar extends ShortVal, PrimitiveToVar<Short, ShortVar> {

    /**
     * Returns a {@link ShortVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link ShortVar} initialized with the specified value
     */
    static @Nonnull ShortVar of(short value) {
        return VarBack.of(value);
    }

    /**
     * Returns a {@link ShortVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link ShortVar} initialized with the specified value
     */
    static @Nonnull ShortVar of(int value) {
        return of((short) value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    @Nonnull
    ShortVar set(short value);

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    default @Nonnull ShortVar set(int value) {
        return set((short) value);
    }

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    @Nonnull
    ShortVar add(int value);

    @Override
    default @Nonnull Var<Short> toVar() {
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

    @Override
    default ShortVar clear() {
        return set(0);
    }
}
