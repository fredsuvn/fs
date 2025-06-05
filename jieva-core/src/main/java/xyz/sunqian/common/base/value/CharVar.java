package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Nonnull;

/**
 * Primitive {@code char} version of {@link Var}.
 *
 * @author sunqian
 */
public interface CharVar extends CharVal, PrimitiveToVar<Character> {

    /**
     * Returns a {@link CharVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link CharVar} initialized with the specified value
     */
    static @Nonnull CharVar of(char value) {
        return VarImpls.of(value);
    }

    /**
     * Returns a {@link CharVar} initialized with the specified value.
     *
     * @param value the specified value
     * @return a {@link CharVar} initialized with the specified value
     */
    static @Nonnull CharVar of(int value) {
        return of((char) value);
    }

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    @Nonnull
    CharVar set(char value);

    /**
     * Sets the held value to the specified value, and returns this.
     *
     * @param value the specified value
     * @return this
     */
    default @Nonnull CharVar set(int value) {
        return set((char) value);
    }

    /**
     * Adds the specified value on current value, returns this itself.
     *
     * @param value the specified value
     * @return this itself
     */
    @Nonnull
    CharVar add(int value);

    @Override
    default @Nonnull Var<Character> toVar() {
        return Var.of(get());
    }

    /**
     * Increments current value by one, and returns the result.
     *
     * @return increment result
     */
    char incrementAndGet();

    /**
     * Increments current value by one, and returns the old value before increment.
     *
     * @return old value before increment
     */
    char getAndIncrement();
}
