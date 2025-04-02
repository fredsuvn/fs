package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Static utility class for {@link Buffer}.
 *
 * @author sunqian
 */
public class JieBuffer {

    /**
     * Returns the actual start index (inclusive) of the backing array in the given buffer.
     *
     * @param buffer the given buffer
     * @return the actual start index (inclusive) of the backing array in the given buffer
     * @throws ReadOnlyBufferException       If the buffer is backed by an array but is read-only
     * @throws UnsupportedOperationException If the buffer is not backed by an accessible array
     */
    public static int getArrayStartIndex(Buffer buffer)
        throws ReadOnlyBufferException, UnsupportedOperationException {
        return buffer.arrayOffset() + buffer.position();
    }

    /**
     * Returns the actual start index (exclusive) of the backing array in the given buffer.
     *
     * @param buffer the given buffer
     * @return the actual start index (exclusive) of the backing array in the given buffer
     * @throws ReadOnlyBufferException       If the buffer is backed by an array but is read-only
     * @throws UnsupportedOperationException If the buffer is not backed by an accessible array
     */
    public static int getArrayEndIndex(Buffer buffer)
        throws ReadOnlyBufferException, UnsupportedOperationException {
        return buffer.arrayOffset() + buffer.position() + buffer.remaining();
    }

    /**
     * Reads all bytes from source buffer into an array. Returns the array, or null if no data read out and reaches to
     * the end of buffer.
     *
     * @param source source buffer
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    @Nullable
    public static byte[] read(ByteBuffer source) throws IORuntimeException {
        try {
            int length = source.remaining();
            // if (length <= 0) {
            //     return null;
            // }
            byte[] result = new byte[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads specified number of bytes from source buffer into an array. Returns the array, or null if no data read out
     * and reaches to the end of buffer.
     * <p>
     * If the number &lt; 0, read all as {@link #read(ByteBuffer)}; els if the number is 0, no read and return an empty
     * array; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the buffer.
     *
     * @param source source buffer
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    @Nullable
    public static byte[] read(ByteBuffer source, int number) throws IORuntimeException {
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new byte[0];
        }
        try {
            int length = Math.min(number, source.remaining());
            if (length <= 0) {
                return null;
            }
            byte[] result = new byte[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Marks and reads all bytes from source buffer into an array, resets the buffer after reading. Returns the array,
     * or null if no data read out and reaches to the end of buffer.
     *
     * @param source source buffer
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    @Nullable
    public static byte[] readReset(ByteBuffer source) throws IORuntimeException {
        try {
            source.mark();
            byte[] result = read(source);
            source.reset();
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Marks and reads specified number of bytes from source buffer into an array, resets the buffer after reading.
     * Returns the array, or null if no data read out and reaches to the end of buffer.
     * <p>
     * If the number &lt; 0, read all as {@link #read(ByteBuffer)}; els if the number is 0, no read and return an empty
     * array; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the buffer.
     *
     * @param source source buffer
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    @Nullable
    public static byte[] readReset(ByteBuffer source, int number) throws IORuntimeException {
        try {
            source.mark();
            byte[] result = read(source, number);
            source.reset();
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads bytes from source buffer into dest buffer, returns actual read number, or -1 if no data read out and
     * reaches to the end of buffer. This method will keep reading until the dest buffer is filled up, or the reading
     * reaches the end of the buffer.
     *
     * @param source source buffer
     * @param dest   dest buffer
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    public static int readTo(ByteBuffer source, ByteBuffer dest) throws IORuntimeException {
        try {
            int sr = source.remaining();
            int dr = dest.remaining();
            if (sr <= dr) {
                dest.put(source);
                return sr;
            }
            ByteBuffer slice = readSlice(source, dr);
            dest.put(slice);
            return dr;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads specified number of bytes from source buffer into dest buffer, returns actual read number, or -1 if no data
     * read out and reaches to the end of buffer.
     * <p>
     * If the number &lt; 0, read as {@link #readTo(ByteBuffer, ByteBuffer)}; els if the number is 0, no read and return
     * 0; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the buffer (of source or dest).
     *
     * @param source source buffer
     * @param dest   dest buffer
     * @param number specified number
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    public static int readTo(ByteBuffer source, ByteBuffer dest, int number) throws IORuntimeException {
        if (number < 0) {
            return readTo(source, dest);
        }
        if (number == 0) {
            return 0;
        }
        try {
            ByteBuffer src = source.remaining() == number ? source : slice(source, number);
            int readNum = readTo(src, dest);
            if (readNum > 0) {
                source.position(source.position() + readNum);
            }
            return readNum;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads specified number of bytes from source buffer into dest array, returns actual read number, or -1 if no data
     * read out and reaches to the end of buffer or array.
     *
     * @param source source buffer
     * @param dest   dest array
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer or array
     * @throws IORuntimeException IO exception
     */
    public static int readTo(ByteBuffer source, byte[] dest) throws IORuntimeException {
        return readTo(source, dest, 0);
    }

    /**
     * Reads specified number of bytes from source buffer into dest array starting from given offset, returns actual
     * read number, or -1 if no data read out and reaches to the end of buffer or array.
     *
     * @param source source buffer
     * @param dest   dest array
     * @param offset given offset
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer or array
     * @throws IORuntimeException IO exception
     */
    public static int readTo(ByteBuffer source, byte[] dest, int offset) throws IORuntimeException {
        try {
            JieCheck.checkInBounds(offset, 0, dest.length);
            int minLen = Math.min(source.remaining(), dest.length - offset);
            if (minLen <= 0) {
                return 0;
            }
            if (source.remaining() >= minLen) {
                source.get(dest, offset, minLen);
            } else {
                ByteBuffer slice = readSlice(source, minLen);
                slice.get(dest, offset, minLen);
            }
            return minLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads bytes from source buffer into dest stream, returns actual read number, or -1 if no data read out and
     * reaches to the end of buffer. This method will keep reading until the reading reaches the end of the buffer.
     *
     * @param source source buffer
     * @param dest   dest stream
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    public static int readTo(ByteBuffer source, OutputStream dest) throws IORuntimeException {
        try {
            if (source.hasArray()) {
                int remaining = source.remaining();
                dest.write(source.array(), source.arrayOffset() + source.position(), remaining);
                source.position(source.position() + remaining);
                return remaining;
            }
            byte[] bytes = read(source);
            dest.write(bytes);
            return bytes.length;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all bytes from source buffer into a string with {@link JieChars#defaultCharset()}. Returns the string, or
     * null if no data read out and reaches to the end of buffer.
     *
     * @param source source buffer
     * @return the string, or null if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    @Nullable
    public static String readString(ByteBuffer source) throws IORuntimeException {
        return readString(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from source buffer into a string with specified charset. Returns the string, or null if no data
     * read out and reaches to the end of buffer.
     *
     * @param source  source buffer
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of buffer
     * @throws IORuntimeException IO exception
     */
    @Nullable
    public static String readString(ByteBuffer source, Charset charset) throws IORuntimeException {
        try {
            byte[] bytes = read(source);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Returns slice of given buffer by {@link ByteBuffer#slice()}, and sets the slice's limit to specified number (or
     * remaining if remaining is less than specified number). Position of given buffer will not be changed.
     *
     * @param buffer given buffer
     * @param number specified number
     * @return the slice buffer
     * @throws IORuntimeException IO exception
     */
    public static ByteBuffer slice(ByteBuffer buffer, int number) throws IORuntimeException {
        try {
            ByteBuffer slice = buffer.slice();
            slice.limit(Math.min(number, buffer.remaining()));
            return slice;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

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
     * Returns a sub-range view of given buffer, starting from given offset to buffer's limit. The two buffers will
     * share the same data so any operation will reflect each other.
     * <p>
     * Note the offset is counted from 0, not {@code buffer.position()}.
     *
     * @param buffer given buffer
     * @param offset given offset
     * @return the sub-buffer
     * @throws IORuntimeException IO exception
     */
    public static ByteBuffer subBuffer(ByteBuffer buffer, int offset) throws IORuntimeException {
        try {
            JieCheck.checkInBounds(offset, 0, buffer.limit());
            int pos = buffer.position();
            buffer.position(offset);
            ByteBuffer slice = buffer.slice();
            buffer.position(pos);
            return slice;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Returns a sub-range view of given buffer, starting from given offset to specified length. The two buffers will
     * share the same data so any operation will reflect each other.
     * <p>
     * Note the offset is counted from 0, not {@code buffer.position()}.
     *
     * @param buffer given buffer
     * @param offset given offset
     * @param length specified length
     * @return the sub-buffer
     * @throws IORuntimeException IO exception
     */
    public static ByteBuffer subBuffer(ByteBuffer buffer, int offset, int length) throws IORuntimeException {
        try {
            JieCheck.checkRangeInBounds(offset, offset + length, 0, buffer.limit());
            int pos = buffer.position();
            buffer.position(offset);
            ByteBuffer slice = slice(buffer, length);
            buffer.position(pos);
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
                readTo(buffer, newBuffer);
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
                readTo(buffer, newBuffer);
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
                    readTo(buffer, newBuffer);
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
     * be set to {@code buffer.limit()}. Otherwise, return {@link #read(ByteBuffer)}.
     *
     * @param buffer given buffer
     * @return back array if {@link #isSimpleWrapper(ByteBuffer)} returns true for given buffer
     */
    public static byte[] readBack(ByteBuffer buffer) {
        if (isSimpleWrapper(buffer)) {
            buffer.position(buffer.limit());
            return buffer.array();
        }
        return read(buffer);
    }
}
