package xyz.sunqian.common.base.value;

/**
 * Primitive {@code boolean} version of {@link Var}.
 *
 * @author sunqian
 */
public interface BooleanVar extends BooleanVal, PrimitiveToVar<Boolean> {

    /**
     * Returns a {@link BooleanVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link BooleanVar} initialized with the specified value
     */
    static BooleanVar of(boolean value) {
        return VarBack.of(value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    BooleanVar set(boolean value);

    /**
     * Toggles current value, returns this itself.
     *
     * @return this itself
     */
    BooleanVar toggle();

    @Override
    default Var<Boolean> toVar() {
        return Var.of(get());
    }

    /**
     * Toggles current value, and returns the result.
     *
     * @return toggle result
     */
    boolean toggleAndGet();

    /**
     * Toggles current value, and returns the old value before toggling.
     *
     * @return old value before toggling
     */
    boolean getAndToggle();
}
