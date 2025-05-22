package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;

/**
 * Primitive {@code char} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface CharVal extends PrimitiveToVal<Character> {

    /**
     * Returns a {@link CharVal} holding the {@code 0}.
     *
     * @return a {@link CharVal} holding the {@code 0}
     */
    static @Nonnull CharVal ofZero() {
        return ValBack.OF_ZERO_CHAR;
    }

    /**
     * Returns a {@link CharVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link CharVal} holding the specified value
     */
    static @Nonnull CharVal of(char value) {
        return ValBack.of(value);
    }

    /**
     * Returns a {@link CharVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link CharVal} holding the specified value
     */
    static @Nonnull CharVal of(int value) {
        return of((char) value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    char get();

    @Override
    default @Nonnull Val<Character> toVal() {
        return Val.of(get());
    }
}
