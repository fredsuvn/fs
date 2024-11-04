package xyz.sunqian.common.ref;

/**
 * Long version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface LongVal {

    /**
     * Returns an instance of {@link LongVal} of 0 initialized value.
     *
     * @return an instance of {@link LongVal} of 0 initialized value
     */
    static LongVal ofZero() {
        return ValImpls.OF_ZERO_LONG;
    }

    /**
     * Returns an instance of {@link LongVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link LongVal} of initialized value
     */
    static LongVal of(long value) {
        return ValImpls.ofLong(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    long get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Long> toWrapper() {
        return Val.of(get());
    }
}
