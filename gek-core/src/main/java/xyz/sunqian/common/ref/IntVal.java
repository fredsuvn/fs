package xyz.sunqian.common.ref;

/**
 * Int version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface IntVal {

    /**
     * Returns an instance of {@link IntVal} of 0 initialized value.
     *
     * @return an instance of {@link IntVal} of 0 initialized value
     */
    static IntVal ofZero() {
        return ValImpls.OF_ZERO_INT;
    }

    /**
     * Returns an instance of {@link IntVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link IntVal} of initialized value
     */
    static IntVal of(int value) {
        return ValImpls.ofInt(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    int get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Integer> toWrapper() {
        return Val.of(get());
    }
}
