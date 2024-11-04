package xyz.sunqian.common.ref;

/**
 * Byte version of {@link Val}.
 *
 * @author fredsuvn
 */
public interface ByteVal {

    /**
     * Returns an instance of {@link ByteVal} of 0 initialized value.
     *
     * @return an instance of {@link ByteVal} of 0 initialized value
     */
    static ByteVal ofZero() {
        return ValImpls.OF_ZERO_BYTE;
    }

    /**
     * Returns an instance of {@link ByteVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ByteVal} of initialized value
     */
    static ByteVal of(byte value) {
        return ValImpls.ofByte(value);
    }

    /**
     * Returns an instance of {@link ByteVal} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link ByteVal} of initialized value
     */
    static ByteVal of(int value) {
        return of((byte) value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    byte get();

    /**
     * Returns {@link Val} version with current value.
     *
     * @return {@link Val} version with current value
     */
    default Val<Byte> toWrapper() {
        return Val.of(get());
    }
}
