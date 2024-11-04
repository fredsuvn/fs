package xyz.sunqian.common.ref;

/**
 * Float version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface FloatVal {

    /**
     * Returns an instance of {@link FloatVal} of 0 initialized value.
     *
     * @return an instance of {@link FloatVal} of 0 initialized value
     */
    static FloatVal ofZero() {
        return ValImpls.OF_ZERO_FLOAT;
    }

    /**
     * Returns an instance of {@link FloatVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link FloatVal} of initialized value
     */
    static FloatVal of(float value) {
        return ValImpls.ofFloat(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    float get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Float> toWrapper() {
        return Val.of(get());
    }
}
