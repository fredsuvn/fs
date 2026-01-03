package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;

/**
 * Primitive {@code long} version of {@link Var}.
 *
 * @author sunqian
 */
public interface LongVar extends LongVal, PrimitiveToVar<Long, LongVar> {

    /**
     * Returns a {@link LongVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link LongVar} initialized with the specified value
     */
    static @Nonnull LongVar of(long value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    @Nonnull
    LongVar set(long value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    @Nonnull
    LongVar add(long value);

    @Override
    default @Nonnull Var<Long> toVar() {
        return Var.of(get());
    }

    /**
     * Increments current value by one, and returns the result.
     *
     * @return increment result
     */
    long incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    long getAndIncrement();

    @Override
    default LongVar clear() {
        return set(0);
    }
}
