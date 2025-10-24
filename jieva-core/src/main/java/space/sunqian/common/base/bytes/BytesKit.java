package space.sunqian.common.base.bytes;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Utilities for {@code byte} and {@code byte array}.
 *
 * @author sunqian
 */
public class BytesKit {

    private static final byte @Nonnull [] EMPTY = new byte[0];
    private static final @Nonnull ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY);

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
    public static byte @Nonnull [] empty() {
        return EMPTY;
    }

    /**
     * Returns an empty byte buffer.
     *
     * @return an empty byte buffer
     */
    public static @Nonnull ByteBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Converts the given byte array to an int value in big endian, and the least significant byte is the last byte of
     * the array. Let {@code n} be {@code bytes.length - 1}, the int will be:
     * {@code byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not enough, padding 0 into the missing bytes.
     *
     * @param bytes the given byte array
     * @return the converted int value
     */
    public static int bytesToInt(byte @Nonnull [] bytes) {
        return bytesToInt(bytes, bytes.length - 1);
    }

    /**
     * Converts the given byte array to an int value in big endian, and the least significant byte is specified by the
     * {@code leastIndex}. Let {@code n} be the index of the least significant byte, the int will be:
     * {@code byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not enough, padding 0 into the missing bytes.
     *
     * @param bytes      the given byte array
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
     * Converts the given byte array to a long value in big endian, and the least significant byte is the last byte of
     * the array. Let {@code n} be {@code bytes.length - 1}, the long will be:
     * {@code byte[n-7], byte[n-6], byte[n-5], byte[n-4], byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not
     * enough, padding 0 into the missing bytes.
     *
     * @param bytes the given byte array
     * @return the converted long value
     */
    public static long bytesToLong(byte @Nonnull [] bytes) {
        return bytesToLong(bytes, bytes.length - 1);
    }

    /**
     * Converts the given byte array to a long value in big endian, and the least significant byte is specified by the
     * {@code leastIndex}. Let {@code n} be the index of the least significant byte, the long will be:
     * {@code byte[n-7], byte[n-6], byte[n-5], byte[n-4], byte[n-3], byte[n-2], byte[n-1], byte[n]}. If the bytes is not
     * enough, padding 0 into the missing bytes.
     *
     * @param bytes      the given byte array
     * @param leastIndex the index of the least significant byte
     * @return the converted long value
     * @throws IndexOutOfBoundsException if the {@code leastIndex} is out of bounds
     */
    public static long bytesToLong(byte @Nonnull [] bytes, int leastIndex) throws IndexOutOfBoundsException {
        long ret = 0;
        int off = Math.max(leastIndex - Long.BYTES + 1, 0);
        int shift = 0;
        for (int i = leastIndex; i >= off; i--) {
            long b = (bytes[i] & 0xFFL) << shift;
            ret |= b;
            shift += 8;
        }
        return ret;
    }

    /**
     * Converts the given int value to a byte array in big endian like:
     * <pre>{@code
     * return new byte[]{
     *     (byte) (value >>> 24),
     *     (byte) (value >>> 16),
     *     (byte) (value >>> 8),
     *     (byte) value
     * };
     * }</pre>
     *
     * @param value the given int value
     * @return the converted byte array
     */
    public static byte @Nonnull [] intToBytes(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }

    /**
     * Converts the given long value to a byte array in big endian like:
     * <pre>{@code
     * return new byte[]{
     *     (byte) (value >>> 56),
     *     (byte) (value >>> 48),
     *     (byte) (value >>> 40),
     *     (byte) (value >>> 32),
     *     (byte) (value >>> 24),
     *     (byte) (value >>> 16),
     *     (byte) (value >>> 8),
     *     (byte) value
     * };
     * }</pre>
     *
     * @param value the given long value
     * @return the converted byte array
     */
    public static byte @Nonnull [] longToBytes(long value) {
        return new byte[]{
            (byte) (value >>> 56),
            (byte) (value >>> 48),
            (byte) (value >>> 40),
            (byte) (value >>> 32),
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }
}
