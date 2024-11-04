package xyz.sunqian.common.ref;

/**
 * Boolean version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface BooleanVal {

    /**
     * Returns an instance of {@link BooleanVal} of false initialized value.
     *
     * @return an instance of {@link BooleanVal} of false initialized value
     */
    static BooleanVal ofFalse() {
        return ValImpls.OF_FALSE;
    }

    /**
     * Returns an instance of {@link BooleanVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link BooleanVal} of initialized value
     */
    static BooleanVal of(boolean value) {
        return ValImpls.ofBoolean(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    boolean get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Boolean> toWrapper() {
        return Val.of(get());
    }
}
