package xyz.sunqian.common.base.value;

/**
 * Primitive {@code char} version of {@link Val}.
 *
 * @author sunqian
 */
public interface CharVal extends PrimitiveToVal<Character> {

    /**
     * Returns a {@link CharVal} holding the {@code 0}.
     *
     * @return a {@link CharVal} holding the {@code 0}
     */
    static CharVal ofZero() {
        return ValBack.OF_ZERO_CHAR;
    }

    /**
     * Returns a {@link CharVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link CharVal} holding the specified value
     */
    static CharVal of(char value) {
        return ValBack.of(value);
    }

    /**
     * Returns a {@link CharVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link CharVal} holding the specified value
     */
    static CharVal of(int value) {
        return of((char) value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    char get();

    @Override
    default Val<Character> toVal() {
        return Val.of(get());
    }
}
