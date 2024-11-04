package xyz.sunqian.common.ref;

/**
 * Short version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface ShortVal {

    /**
     * Returns an instance of {@link ShortVal} of 0 initialized value.
     *
     * @return an instance of {@link ShortVal} of 0 initialized value
     */
    static ShortVal ofZero() {
        return ValImpls.OF_ZERO_SHORT;
    }

    /**
     * Returns an instance of {@link ShortVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ShortVal} of initialized value
     */
    static ShortVal of(short value) {
        return ValImpls.ofShort(value);
    }

    /**
     * Returns an instance of {@link ShortVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ShortVal} of initialized value
     */
    static ShortVal of(int value) {
        return of((short) value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    short get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Short> toWrapper() {
        return Val.of(get());
    }
}
