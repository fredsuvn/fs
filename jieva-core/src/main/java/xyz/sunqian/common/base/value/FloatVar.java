package xyz.sunqian.common.base.value;

/**
 * Primitive {@code float} version of {@link Var}.
 *
 * @author sunqian
 */
public interface FloatVar extends FloatVal, PrimitiveToVar<Float> {

    /**
     * Returns a {@link FloatVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link FloatVar} initialized with the specified value
     */
    static FloatVar of(float value) {
        return VarBack.of(value);
    }

    /**
     * Returns a {@link FloatVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link FloatVar} initialized with the specified value
     */
    static FloatVar of(double value) {
        return of((float) value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    FloatVar set(float value);

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    default FloatVar set(double value) {
        return set((float) value);
    }

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    FloatVar add(float value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    default FloatVar add(double value) {
        return add((float) value);
    }

    @Override
    default Var<Float> toVar() {
        return Var.of(get());
    }
}
