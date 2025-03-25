package xyz.sunqian.common.base.value;

/**
 * Primitive {@code double} version of {@link Var}.
 *
 * @author sunqian
 */
public interface DoubleVar extends DoubleVal, PrimitiveToVar<Double> {

    /**
     * Returns a {@link DoubleVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link DoubleVar} initialized with the specified value
     */
    static DoubleVar of(double value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    DoubleVar set(double value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    DoubleVar add(double value);

    @Override
    default Var<Double> toVar() {
        return Var.of(get());
    }
}
