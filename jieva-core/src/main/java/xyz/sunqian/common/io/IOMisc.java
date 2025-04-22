package xyz.sunqian.common.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;

public class IOMisc {

    /**
     * Returns slice of given buffer by {@link ByteBuffer#slice()}, and sets the slice's limit to specified number (or
     * remaining if remaining is less than specified number). Position of given buffer will be set to
     * {@code (buffer.position + slice.remaining())}.
     *
     * @param buffer given buffer
     * @param number specified number
     * @return the slice buffer
     * @throws IORuntimeException IO exception
     */
    public static ByteBuffer readSlice(ByteBuffer buffer, int number) throws IORuntimeException {
        try {
            ByteBuffer slice = buffer.slice();
            slice.limit(Math.min(number, buffer.remaining()));
            buffer.position(buffer.position() + slice.remaining());
            return slice;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Splits given buffer in specified length, returns split buffer list. This method starts the loop:
     * <ul>
     *     <li>
     *         If remaining length is &gt;= specified length, this method will use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified length. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than specified length, loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer given buffer
     * @param length specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, int length) {
        return split(buffer, length, len -> null);
    }

    /**
     * Splits given buffer in specified length, returns split buffer list. This method starts the loop:
     * <ul>
     *     <li>
     *         If remaining length is &gt;= specified length, this method will call {@code generator} with specified
     *         length to generate new buffer. If {@code generator} return a new buffer, this method will fill data from
     *         given buffer into new buffer. If {@code generator} return null, use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified buffer. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than specified length, loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer    given buffer
     * @param length    specified length
     * @param generator given buffer generator, the argument is specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, int length, IntFunction<ByteBuffer> generator) {
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            return Collections.emptyList();
        }
        if (remaining < length) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = new ArrayList<>(remaining / length);
        while (buffer.remaining() >= length) {
            ByteBuffer newBuffer = generator.apply(length);
            if (newBuffer == null) {
                result.add(readSlice(buffer, length));
            } else {
                JieBuffer.readTo(buffer, newBuffer);
                newBuffer.flip();
                result.add(newBuffer);
            }
        }
        return result;
    }

    /**
     * Splits given buffer in specified length, returns split buffer list. This method assumes the length is specified
     * at specified length offset, and starts the loop:
     * <ul>
     *     <li>
     *         Marks current position.
     *         If remaining length is &gt;= {@code lengthOffset + lengthSize}, this method will skip and read number of
     *         {@code lengthSize} bytes at {@code lengthOffset} as specified length.
     *     </li>
     *     <li>
     *         Resets current position.
     *         If remaining length is &gt;= specified length, this method will use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified length. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than {@code lengthOffset + lengthSize} or specified length,
     *         loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   byte number of length, must in 1, 2, 4
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, int lengthOffset, int lengthSize) {
        return split(buffer, lengthOffset, lengthSize, len -> null);
    }

    /**
     * Splits given buffer in specified length, returns split buffer list. This method assumes the length is specified
     * at specified length offset, and starts the loop:
     * <ul>
     *     <li>
     *         Marks current position.
     *         If remaining length is &gt;= {@code lengthOffset + lengthSize}, this method will skip and read number of
     *         {@code lengthSize} bytes at {@code lengthOffset} as specified length.
     *     </li>
     *     <li>
     *         Resets current position.
     *         If remaining length is &gt;= specified length, this method will call {@code generator} with specified
     *         length to generate new buffer. If {@code generator} return a new buffer, this method will fill data from
     *         given buffer into new buffer. If {@code generator} return null, use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified buffer. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than {@code lengthOffset + lengthSize} or specified length,
     *         loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   byte number of length, must in 1, 2, 4
     * @param generator    given buffer generator, the argument is specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(
        ByteBuffer buffer, int lengthOffset, int lengthSize, IntFunction<ByteBuffer> generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        int minSize = lengthOffset + lengthSize;
        if (buffer.remaining() < minSize) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (true) {
            buffer.mark();
            buffer.position(buffer.position() + lengthOffset);
            int length = readLength(buffer, lengthSize);
            buffer.reset();
            if (buffer.remaining() < length) {
                break;
            }
            ByteBuffer newBuffer = generator.apply(length);
            if (newBuffer == null) {
                result.add(readSlice(buffer, length));
            } else {
                JieBuffer.readTo(buffer, newBuffer);
                newBuffer.flip();
                result.add(newBuffer);
            }
            if (buffer.remaining() < minSize) {
                break;
            }
        }
        return result;
    }

    private static int readLength(ByteBuffer buffer, int lengthSize) {
        switch (lengthSize) {
            case 1:
                return buffer.get() & 0x000000ff;
            case 2:
                return buffer.getShort() & 0x0000ffff;
            case 4:
                return buffer.getInt();
        }
        throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
    }

    /**
     * Splits given buffer in specified delimiter, returns split buffer list. This method starts the loop:
     * <ul>
     *     <li>
     *         Marks current position, reads until meets specified delimiter.
     *     </li>
     *     <li>
     *         If specified delimiter is met, this method will use {@link ByteBuffer#slice()} to generate a slice
     *         buffer of read data (delimiter exclusive). Then moves the position to next of delimiter.
     *     </li>
     *     <li>
     *         If no specified delimiter is met, reset position and loop will be broken.
     *     </li>
     * </ul>
     * For example:
     * <pre>
     *     split("123|456|789|") = ["123", "456", "789"]
     *     split("|123|456|789|") = ["", "123", "456", "789"]
     *     split("|123|456|78") = ["", "123", "456"] and reset to position 9
     * </pre>
     *
     * @param buffer    given buffer
     * @param delimiter specified delimiter
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, byte delimiter) {
        return split(buffer, delimiter, len -> null);
    }

    /**
     * Splits given buffer in specified delimiter, returns split buffer list. This method starts the loop:
     * <ul>
     *     <li>
     *         Marks current position, reads until meets specified delimiter.
     *     </li>
     *     <li>
     *         If specified delimiter is met, this method will call {@code generator} with length of read data to
     *         generate new buffer. If {@code generator} return a new buffer, this method will fill data from given
     *         buffer into new buffer. If {@code generator} return null, use {@link ByteBuffer#slice()} to generate a
     *         slice buffer of read data (delimiter exclusive).
     *         Then moves the position to next of delimiter.
     *     </li>
     *     <li>
     *         If no specified delimiter is met, reset position and loop will be broken.
     *     </li>
     * </ul>
     * For example:
     * <pre>
     *     split("123|456|789|") = ["123", "456", "789"]
     *     split("|123|456|789|") = ["", "123", "456", "789"]
     *     split("|123|456|78") = ["", "123", "456"] and reset to position 9
     * </pre>
     *
     * @param buffer    given buffer
     * @param delimiter specified delimiter
     * @param generator given buffer generator, the argument is specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, byte delimiter, IntFunction<ByteBuffer> generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        buffer.mark();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            int pos = buffer.position();
            if (b == delimiter) {
                int delimiterPos = pos - 1;
                buffer.reset();
                int length = delimiterPos - buffer.position();
                ByteBuffer newBuffer = generator.apply(length);
                if (result == null) {
                    result = new LinkedList<>();
                }
                if (newBuffer == null) {
                    result.add(readSlice(buffer, length));
                } else {
                    JieBuffer.readTo(buffer, newBuffer);
                    newBuffer.flip();
                    result.add(newBuffer);
                }
                buffer.position(pos);
                buffer.mark();
            }
        }
        buffer.reset();
        return result == null ? Collections.emptyList() : result;
    }

    /**
     * Returns whether given buffer is a simple wrapper of back array:
     * <pre>
     *     return buffer.hasArray()
     *             && buffer.position() == 0
     *             && buffer.arrayOffset() == 0
     *             && buffer.limit() == buffer.array().length;
     * </pre>
     *
     * @param buffer given buffer
     * @return whether given buffer is a simple wrapper of back array
     */
    public static boolean isSimpleWrapper(ByteBuffer buffer) {
        return buffer.hasArray()
            && buffer.position() == 0
            && buffer.arrayOffset() == 0
            && buffer.limit() == buffer.array().length;
    }

    /**
     * Returns back array if {@link #isSimpleWrapper(ByteBuffer)} returns true for given buffer, and the position will
     * be set to {@code buffer.limit()}. Otherwise, return {@link JieBuffer#read(ByteBuffer)}.
     *
     * @param buffer given buffer
     * @return back array if {@link #isSimpleWrapper(ByteBuffer)} returns true for given buffer
     */
    public static byte[] readBack(ByteBuffer buffer) {
        if (isSimpleWrapper(buffer)) {
            buffer.position(buffer.limit());
            return buffer.array();
        }
        return JieBuffer.read(buffer);
    }
}
