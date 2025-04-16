package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.ByteProcessor;
import xyz.sunqian.common.base.chars.CharProcessor;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.coll.JieArray;

import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

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
    public static int arrayStartIndex(Buffer buffer) throws ReadOnlyBufferException, UnsupportedOperationException {
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
    public static int arrayEndIndex(Buffer buffer) throws ReadOnlyBufferException, UnsupportedOperationException {
        return buffer.arrayOffset() + buffer.position() + buffer.remaining();
    }

    /**
     * Reads all data from the source buffer into a new array, continuing until the end of the buffer, and returns the
     * array.
     *
     * @param source the source buffer
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte[] read(ByteBuffer source) throws IORuntimeException {
        try {
            int length = source.remaining();
            if (length <= 0) {
                return new byte[0];
            }
            byte[] result = new byte[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the specified number of data from the source buffer into a new array, and returns the array. If
     * {@code number < 0}, this method performs as {@link #read(ByteBuffer)}. If {@code number == 0}, returns an empty
     * array without reading. Otherwise, this method keeps reading until the read number reaches the specified number or
     * the end of the buffer has been reached.
     *
     * @param source the source buffer
     * @param number the specified number
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    @Nullable
    public static byte[] read(ByteBuffer source, int number) throws IORuntimeException {
        if (!source.hasRemaining()) {
            return new byte[0];
        }
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new byte[0];
        }
        try {
            int length = Math.min(number, source.remaining());
            byte[] result = new byte[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source buffer into a new array, continuing until the end of the buffer, and returns the
     * array.
     *
     * @param source the source buffer
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char[] read(CharBuffer source) throws IORuntimeException {
        try {
            int length = source.remaining();
            if (length <= 0) {
                return new char[0];
            }
            char[] result = new char[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the specified number of data from the source buffer into a new array, and returns the array. If
     * {@code number < 0}, this method performs as {@link #read(CharBuffer)}. If {@code number == 0}, returns an empty
     * array without reading. Otherwise, this method keeps reading until the read number reaches the specified number or
     * the end of the buffer has been reached.
     *
     * @param source the source buffer
     * @param number the specified number
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char[] read(CharBuffer source, int number) throws IORuntimeException {
        if (!source.hasRemaining()) {
            return new char[0];
        }
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new char[0];
        }
        try {
            int length = Math.min(number, source.remaining());
            char[] result = new char[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source buffer into a string, continuing until the end of the buffer, and returns the
     * string.
     *
     * @param source the source buffer
     * @return the string containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static String string(CharBuffer source) throws IORuntimeException {
        StringBuilder builder = new StringBuilder();
        readTo(source, builder);
        return builder.toString();
    }

    /**
     * Reads the specified number of data from the source buffer into a string, and returns the string. If
     * {@code number < 0}, this method performs as {@link #string(CharBuffer)}. If {@code number == 0}, returns an empty
     * array without reading. Otherwise, this method keeps reading until the read number reaches the specified number or
     * the end of the buffer has been reached.
     *
     * @param source the source buffer
     * @param number the specified number
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static String string(CharBuffer source, int number) throws IORuntimeException {
        StringBuilder builder = new StringBuilder();
        CharProcessor.from(source).readLimit(number).writeTo(builder);
        return builder.toString();
    }

    /**
     * Reads all bytes from the source buffer and returns them as a string with {@link JieChars#defaultCharset()}.
     *
     * @param source the source buffer
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static String string(ByteBuffer source) throws IORuntimeException {
        return string(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from the source buffer and returns them as a string with the specified charset.
     *
     * @param source  the source buffer
     * @param charset the specified charset
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static String string(ByteBuffer source, Charset charset) throws IORuntimeException {
        byte[] bytes = read(source);
        if (JieArray.isEmpty(bytes)) {
            return "";
        }
        return new String(bytes, charset);
    }

    /**
     * Reads data from the source buffer into the specified array until the array is completely filled or the end of the
     * buffer is reached. Returns the actual number of bytes read
     *
     * @param source the source buffer
     * @param dest   the specified array
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(ByteBuffer source, byte[] dest) throws IORuntimeException {
        return (int) ByteProcessor.from(source).readLimit(dest.length).writeTo(dest);
    }

    /**
     * Reads data from the source buffer into the specified buffer until the buffer is completely filled or the end of
     * the buffer is reached. Returns the actual number of bytes read
     *
     * @param source the source buffer
     * @param dest   the specified buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(ByteBuffer source, ByteBuffer dest) throws IORuntimeException {
        return (int) ByteProcessor.from(source).readLimit(dest.remaining()).writeTo(dest);
    }

    /**
     * Reads data from the source buffer into the specified output buffer until the end of the source buffer is reached.
     * Returns the actual number of bytes read
     *
     * @param source the source buffer
     * @param dest   the specified output buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(ByteBuffer source, OutputStream dest) throws IORuntimeException {
        return ByteProcessor.from(source).writeTo(dest);
    }

    /**
     * Reads data from the source buffer into the specified array until the array is completely filled or the end of the
     * buffer is reached. Returns the actual number of chars read
     *
     * @param source the source buffer
     * @param dest   the specified array
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(CharBuffer source, char[] dest) throws IORuntimeException {
        return (int) CharProcessor.from(source).readLimit(dest.length).writeTo(dest);
    }

    /**
     * Reads data from the source buffer into the specified buffer until the buffer is completely filled or the end of
     * the buffer is reached. Returns the actual number of chars read
     *
     * @param source the source buffer
     * @param dest   the specified buffer
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(CharBuffer source, CharBuffer dest) throws IORuntimeException {
        return (int) CharProcessor.from(source).readLimit(dest.remaining()).writeTo(dest);
    }

    /**
     * Reads data from the source buffer into the specified appender until the end of the buffer is reached. Returns the
     * actual number of chars read
     *
     * @param source the source buffer
     * @param dest   the specified appender
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(CharBuffer source, Appendable dest) throws IORuntimeException {
        return CharProcessor.from(source).writeTo(dest);
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the given buffer's current position, and extends for the specified length.
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     *
     * @param buffer the given buffer
     * @param length the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IndexOutOfBoundsException if the offset and length is out of bounds
     */
    public static ByteBuffer slice(ByteBuffer buffer, int length) throws IndexOutOfBoundsException {
        return slice(buffer, 0, length);
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the specified offset from the given buffer's current position, and extends for the specified
     * length.
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     *
     * @param buffer the given buffer
     * @param offset the specified offset
     * @param length the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IndexOutOfBoundsException if the offset and length is out of bounds
     */
    public static ByteBuffer slice(ByteBuffer buffer, int offset, int length) throws IndexOutOfBoundsException {
        checkOffsetLength(buffer.remaining(), offset, length);
        int pos = buffer.position();
        int limit = buffer.limit();
        buffer.position(pos + offset);
        buffer.limit(pos + offset + length);
        ByteBuffer slice = buffer.slice();
        buffer.position(pos);
        buffer.limit(limit);
        return slice;
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the given buffer's current position, and extends for the specified length.
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     *
     * @param buffer the given buffer
     * @param length the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IndexOutOfBoundsException if the offset and length is out of bounds
     */
    public static CharBuffer slice(CharBuffer buffer, int length) throws IndexOutOfBoundsException {
        return slice(buffer, 0, length);
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the specified offset from the given buffer's current position, and extends for the specified
     * length.
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     *
     * @param buffer the given buffer
     * @param offset the specified offset
     * @param length the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IndexOutOfBoundsException if the offset and length is out of bounds
     */
    public static CharBuffer slice(CharBuffer buffer, int offset, int length) throws IndexOutOfBoundsException {
        checkOffsetLength(buffer.remaining(), offset, length);
        int pos = buffer.position();
        int limit = buffer.limit();
        buffer.position(pos + offset);
        buffer.limit(pos + offset + length);
        CharBuffer slice = buffer.slice();
        buffer.position(pos);
        buffer.limit(limit);
        return slice;
    }

    //------------------------------------------------------------//
    //------------------------------------------------------------//

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
