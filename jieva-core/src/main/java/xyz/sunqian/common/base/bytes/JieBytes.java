package xyz.sunqian.common.base.bytes;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Static utility class for {@code bytes}.
 *
 * @author sunqian
 */
public class JieBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_BYTES);

    /**
     * Returns whether the given buffer is null or empty.
     *
     * @param buffer the given buffer
     * @return whether the given buffer is null or empty
     */
    public static boolean isEmpty(@Nullable ByteBuffer buffer) {
        return buffer == null || !buffer.hasRemaining();
    }

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
     * Converts the given bytes array to a int value in big endian, and the least significant byte is the last byte of
     * the array. Let {@code n} be {@code bytes.length - 1}, the int will be:
     * {@code byte[n-7], byte[n-6], byte[n-5], byte[n-4], byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not
     * enough, padding 0 into the missing bytes.
     *
     * @param bytes the given bytes array
     * @return the converted int value
     */
    public static int bytesToInt(byte @Nonnull [] bytes) {
        return bytesToInt(bytes, bytes.length - 1);
    }

    /**
     * Converts the given bytes array to a int value in big endian, and the least significant byte is specified by the
     * {@code leastIndex}. Let {@code n} be the index of the least significant byte, the int will be:
     * {@code byte[n-7], byte[n-6], byte[n-5], byte[n-4], byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not
     * enough, padding 0 into the missing bytes.
     *
     * @param bytes      the given bytes array
     * @param leastIndex the index of the least significant byte
     * @return the converted int value
     * @throws IndexOutOfBoundsException if the {@code leastIndex} is out of bounds
     */
    public static int bytesToInt(byte @Nonnull [] bytes, int leastIndex) throws IndexOutOfBoundsException {
        int ret = 0;
        int off = Math.max(leastIndex - Integer.BYTES + 1, 0);
        int shift = 0;
        for (int i = leastIndex; i >= off; i--) {
            int b = (bytes[i] & 0xFF) << shift;
            ret |= b;
            shift += 8;
        }
        return ret;
    }

    /**
     * Converts the given bytes array to a long value in big endian, and the least significant byte is the last byte of
     * the array. Let {@code n} be {@code bytes.length - 1}, the long will be:
     * {@code byte[n-7], byte[n-6], byte[n-5], byte[n-4], byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not
     * enough, padding 0 into the missing bytes.
     *
     * @param bytes the given bytes array
     * @return the converted long value
     */
    public static long bytesToLong(byte @Nonnull [] bytes) {
        return bytesToLong(bytes, bytes.length - 1);
    }

    /**
     * Converts the given bytes array to a long value in big endian, and the least significant byte is specified by the
     * {@code leastIndex}. Let {@code n} be the index of the least significant byte, the long will be:
     * {@code byte[n-7], byte[n-6], byte[n-5], byte[n-4], byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not
     * enough, padding 0 into the missing bytes.
     *
     * @param bytes      the given bytes array
     * @param leastIndex the index of the least significant byte
     * @return the converted long value
     * @throws IndexOutOfBoundsException if the {@code leastIndex} is out of bounds
     */
    public static long bytesToLong(byte @Nonnull [] bytes, int leastIndex) throws IndexOutOfBoundsException {
        long ret = 0;
        int off = Math.max(leastIndex - Long.BYTES + 1, 0);
        int shift = 0;
        for (int i = leastIndex; i >= off; i--) {
            int b = (bytes[i] & 0xFF) << shift;
            ret |= b;
            shift += 8;
        }
        return ret;
    }
}
