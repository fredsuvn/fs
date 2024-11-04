package xyz.sunqian.common.base;

import java.nio.BufferOverflowException;
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
     * Returns a new buffer (not direct) of which content copied from given data. This method is equivalent to
     * ({@link #copyBuffer(byte[], boolean)}):
     * <pre>
     *     return copy(data, false);
     * </pre>
     * The new buffer's position will be 0, limit and capacity will be length of given data, and it is not read-only.
     *
     * @param data given data
     * @return a new buffer (not direct) of which content copied from given data
     * @see #copyBuffer(byte[], boolean)
     */
    public static ByteBuffer copyBuffer(byte[] data) {
        return copyBuffer(data, false);
    }

    /**
     * Returns a new buffer of which content copied from given data. The buffer will be direct if specified direct
     * option is {@code true}, otherwise be not. The new buffer's position will be 0, limit and capacity will be length
     * of given data, and it is not read-only.
     *
     * @param data   given data
     * @param direct specified direct option
     * @return a new buffer of which content copied from given data
     */
    public static ByteBuffer copyBuffer(byte[] data, boolean direct) {
        ByteBuffer buffer = direct ? ByteBuffer.allocateDirect(data.length) : ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a new buffer of which content copied from given data. The buffer will be direct if given data is direct,
     * otherwise be not. The position of given data will not be changed, rather than incremented by its remaining. The
     * new buffer's position will be 0, limit and capacity will be length of given data, and it is not read-only.
     *
     * @param data given data
     * @return a new buffer (not direct) of which content copied from given data
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
     * Returns a new array of which content copied from given data. The position of given data will not be changed,
     * rather than incremented by its remaining.
     *
     * @param data given data
     * @return a new array of which content copied from given data
     */
    public static byte[] copyBytes(ByteBuffer data) {
        int pos = data.position();
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        data.position(pos);
        return bytes;
    }

    /**
     * Reads given data into a new array then returns. The position of given data will be incremented by its remaining.
     *
     * @param data given data
     * @return a new array of which content read from given data
     */
    public static byte[] getBytes(ByteBuffer data) {
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        return bytes;
    }

    /**
     * Puts content of specified length from given source into destination. The positions of two buffers will be
     * incremented by specified length.
     *
     * @param source given source
     * @param dest   given destination
     * @param length specified length
     * @throws IllegalArgumentException if the preconditions on length do not hold
     * @throws IllegalArgumentException If there is insufficient space in the destination
     */
    public static void putBuffer(ByteBuffer source, ByteBuffer dest, int length)
        throws IllegalArgumentException, BufferOverflowException {
        ByteBuffer slice = slice(source, 0, length);
        dest.put(slice);
        source.position(source.position() + length);
    }

    /**
     * Returns a new buffer whose content is a shared subsequence of given buffer's content. The content of the new
     * buffer will start at specified offset to given buffer's current position, up to specified length. Changes to
     * given buffer's content will be visible in the new buffer, and vice versa.
     * <p>
     * The two buffers' position, limit, and mark values will be independent. The new buffer's position will be zero,
     * its capacity and its limit will be the specified length, and its mark will be undefined. The new buffer will be
     * direct if, and only if, given buffer is direct, and it will be read-only if, and only if, given buffer is
     * read-only. The position of given buffer will not be changed.
     * <p>
     * Specially if specified length is {@code 0}, returns {@link #emptyBuffer()}.
     *
     * @param buffer given buffer
     * @param offset specified offset to {@code position}
     * @param length specified length
     * @throws IllegalArgumentException if the preconditions on offset and length do not hold
     */
    public static ByteBuffer slice(ByteBuffer buffer, int offset, int length) throws IllegalArgumentException {
        if (length == 0) {
            return emptyBuffer();
        }
        int pos = buffer.position();
        int limit = buffer.limit();
        buffer.position(pos + offset);
        buffer.limit(pos + offset + length);
        ByteBuffer slice = buffer.slice();
        buffer.position(pos);
        buffer.limit(limit);
        return slice;
    }
}
