package xyz.sunqian.common.io;

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
     */
    public static byte[] read(ByteBuffer source) {
        int length = source.remaining();
        if (length <= 0) {
            return new byte[0];
        }
        byte[] result = new byte[length];
        source.get(result);
        return result;
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
     */
    public static byte[] read(ByteBuffer source, int number) {
        if (!source.hasRemaining()) {
            return new byte[0];
        }
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new byte[0];
        }
        int length = Math.min(number, source.remaining());
        byte[] result = new byte[length];
        source.get(result);
        return result;
    }

    /**
     * Reads all data from the source buffer into a new array, continuing until the end of the buffer, and returns the
     * array.
     *
     * @param source the source buffer
     * @return the array containing the data
     */
    public static char[] read(CharBuffer source) {
        int length = source.remaining();
        if (length <= 0) {
            return new char[0];
        }
        char[] result = new char[length];
        source.get(result);
        return result;
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
     */
    public static char[] read(CharBuffer source, int number) {
        if (!source.hasRemaining()) {
            return new char[0];
        }
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new char[0];
        }
        int length = Math.min(number, source.remaining());
        char[] result = new char[length];
        source.get(result);
        return result;
    }

    /**
     * Reads all data from the source buffer into a string, continuing until the end of the buffer, and returns the
     * string.
     *
     * @param source the source buffer
     * @return the string containing the data
     */
    public static String string(CharBuffer source) {
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
     */
    public static String string(CharBuffer source, int number) {
        StringBuilder builder = new StringBuilder();
        CharProcessor.from(source).readLimit(number).writeTo(builder);
        return builder.toString();
    }

    /**
     * Reads all bytes from the source buffer and returns them as a string with {@link JieChars#defaultCharset()}.
     *
     * @param source the source buffer
     * @return the string
     */
    public static String string(ByteBuffer source) {
        return string(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from the source buffer and returns them as a string with the specified charset.
     *
     * @param source  the source buffer
     * @param charset the specified charset
     * @return the string
     */
    public static String string(ByteBuffer source, Charset charset) {
        byte[] bytes = read(source);
        if (JieArray.isEmpty(bytes)) {
            return "";
        }
        return new String(bytes, charset);
    }

    /**
     * Reads data from the source buffer into the specified array until the array is completely filled or the end of the
     * buffer is reached. Returns the actual number of bytes read.
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
     * Reads data from the source buffer into the dest buffer until the dest buffer is completely filled or the end of
     * the source buffer is reached. Returns the actual number of bytes read.
     *
     * @param source the source buffer
     * @param dest   the dest buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(ByteBuffer source, ByteBuffer dest) throws IORuntimeException {
        int limit = Math.min(source.remaining(), dest.remaining());
        if (limit <= 0) {
            return 0;
        }
        int srcLimit = source.limit();
        source.limit(source.position() + limit);
        dest.put(source);
        source.limit(srcLimit);
        return limit;
    }

    /**
     * Reads data from the source buffer into the specified output buffer until the end of the source buffer is reached.
     * Returns the actual number of bytes read.
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
     * buffer is reached. Returns the actual number of chars read.
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
     * Reads data from the source buffer into the dest buffer until the dest buffer is completely filled or the end of
     * the source buffer is reached. Returns the actual number of chars read.
     *
     * @param source the source buffer
     * @param dest   the dest buffer
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(CharBuffer source, CharBuffer dest) throws IORuntimeException {
        int limit = Math.min(source.remaining(), dest.remaining());
        if (limit <= 0) {
            return 0;
        }
        int srcLimit = source.limit();
        source.limit(source.position() + limit);
        dest.put(source);
        source.limit(srcLimit);
        return limit;
    }

    /**
     * Reads data from the source buffer into the specified appender until the end of the buffer is reached. Returns the
     * actual number of chars read.
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
}
