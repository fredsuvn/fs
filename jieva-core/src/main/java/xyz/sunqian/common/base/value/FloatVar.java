package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Nonnull;

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
    static @Nonnull FloatVar of(float value) {
        return VarImpls.of(value);
    }

    /**
     * Returns a {@link FloatVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link FloatVar} initialized with the specified value
     */
    static @Nonnull FloatVar of(double value) {
        return of((float) value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    @Nonnull
    FloatVar set(float value);

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    default @Nonnull FloatVar set(double value) {
        return set((float) value);
    }

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    @Nonnull
    FloatVar add(float value);

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    default @Nonnull FloatVar add(double value) {
        return add((float) value);
    }

    @Override
    default @Nonnull Var<Float> toVar() {
        return Var.of(get());
    }
}
