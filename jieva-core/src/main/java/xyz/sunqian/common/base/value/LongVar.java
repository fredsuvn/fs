package xyz.sunqian.common.base.value;

/**
 * Primitive {@code long} version of {@link Var}.
 *
 * @author sunqian
 */
public interface LongVar extends LongVal, PrimitiveToVar<Long> {

    /**
     * Returns a {@link LongVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link LongVar} initialized with the specified value
     */
    static LongVar of(long value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    LongVar set(long value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    LongVar add(long value);

    @Override
    default Var<Long> toVar() {
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
}
