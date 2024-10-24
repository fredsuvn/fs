package xyz.fslabo.common.base;

/**
 * This is a static utilities class provides utilities for {@code bytes}.
 *
 * @author fredsuvn
 */
public class JieBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * Returns an empty byte array.
     *
     * @return an empty byte array
     */
    public static byte[] emptyBytes() {
        return EMPTY_BYTES;
    }
}
