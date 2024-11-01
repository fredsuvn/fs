package xyz.fslabo.common.base;

import java.nio.ByteBuffer;

/**
 * This is a static utilities class provides utilities for {@code bytes}.
 *
 * @author fredsuvn
 */
public class JieBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_BYTES);

    /**
     * Returns an empty byte array.
     *
     * @return an empty byte array
     */
    public static byte[] emptyBytes() {
        return EMPTY_BYTES;
    }

    /**
     * Returns an empty byte buffer.
     *
     * @return an empty byte buffer
     */
    public static ByteBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Returns a new {@link ByteBuffer} (not direct) of which content copied from given data. This method is equivalent
     * to ({@link #copyBuffer(byte[], boolean)}):
     * <pre>
     *     return copy(data, false);
     * </pre>
     *
     * @param data given data
     * @return a new {@link ByteBuffer} (not direct) of which content copied from given data
     * @see #copyBuffer(byte[], boolean)
     */
    public static ByteBuffer copyBuffer(byte[] data) {
        return copyBuffer(data, false);
    }

    /**
     * Returns a new {@link ByteBuffer} of which content copied from given data. The buffer will be direct if specified
     * direct option is {@code true}, otherwise be not.
     *
     * @param data   given data
     * @param direct specified direct option
     * @return a new {@link ByteBuffer} of which content copied from given data
     */
    public static ByteBuffer copyBuffer(byte[] data, boolean direct) {
        ByteBuffer buffer = direct ? ByteBuffer.allocateDirect(data.length) : ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a new {@link ByteBuffer} of which content copied from given data. The buffer will be direct if given data
     * is direct, otherwise be not. The position of given data will not be changed, rather than incremented by its
     * remaining.
     *
     * @param data given data
     * @return a new {@link ByteBuffer} (not direct) of which content copied from given data
     */
    public static ByteBuffer copyBuffer(ByteBuffer data) {
        ByteBuffer buffer = data.isDirect() ?
            ByteBuffer.allocateDirect(data.remaining()) : ByteBuffer.allocate(data.remaining());
        int pos = data.position();
        buffer.put(data);
        data.position(pos);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a new {@code byte} array of which content copied from given data. The position of given data will not be
     * changed, rather than incremented by its remaining.
     *
     * @param data given data
     * @return a new {@code byte} array of which content copied from given data
     */
    public static byte[] copyBytes(ByteBuffer data) {
        int pos = data.position();
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        data.position(pos);
        return bytes;
    }
}
