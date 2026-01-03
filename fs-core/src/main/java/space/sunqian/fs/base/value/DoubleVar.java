package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;

/**
 * Primitive {@code double} version of {@link Var}.
 *
 * @author sunqian
 */
public interface DoubleVar extends DoubleVal, PrimitiveToVar<Double, DoubleVar> {

    /**
     * Returns a {@link DoubleVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link DoubleVar} initialized with the specified value
     */
    static @Nonnull DoubleVar of(double value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    @Nonnull
    DoubleVar set(double value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    @Nonnull
    DoubleVar add(double value);

    @Override
    default @Nonnull Var<Double> toVar() {
        return Var.of(get());
    }

    @Override
    default DoubleVar clear() {
        return set(0);
    }
}
