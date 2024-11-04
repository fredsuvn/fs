package xyz.sunqian.common.ref;

/**
 * Char version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface CharVal {

    /**
     * Returns an instance of {@link CharVal} of 0 initialized value.
     *
     * @return an instance of {@link CharVal} of 0 initialized value
     */
    static CharVal ofZero() {
        return ValImpls.OF_ZERO_CHAR;
    }

    /**
     * Returns an instance of {@link CharVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link CharVal} of initialized value
     */
    static CharVal of(char value) {
        return ValImpls.ofChar(value);
    }

    /**
     * Returns an instance of {@link CharVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link CharVal} of initialized value
     */
    static CharVal of(int value) {
        return of((char) value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    char get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Character> toWrapper() {
        return Val.of(get());
    }
}
