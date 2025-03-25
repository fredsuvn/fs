package xyz.sunqian.common.base.value;

/**
 * Primitive {@code int} version of {@link Var}.
 *
 * @author sunqian
 */
public interface IntVar extends IntVal, PrimitiveToVar<Integer> {

    /**
     * Returns a {@link IntVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link IntVar} initialized with the specified value
     */
    static IntVar of(int value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    IntVar set(int value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    IntVar add(int value);

    @Override
    default Var<Integer> toVar() {
        return Var.of(get());
    }

    /**
     * Increments current value by one, and returns the result.
     *
     * @return increment result
     */
    int incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    int getAndIncrement();
}
