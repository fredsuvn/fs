package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.math.MathKit;

import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * Utilities kit for {@link Buffer} related.
 *
 * @author sunqian
 */
public class BufferKit {

    /**
     * Returns the actual start index (inclusive) of the backing array in the given buffer.
     *
     * @param buffer the given buffer
     * @return the actual start index (inclusive) of the backing array in the given buffer
     * @throws ReadOnlyBufferException       if the buffer is backed by an array but is read-only
     * @throws UnsupportedOperationException if the buffer is not backed by an accessible array
     */
    public static int arrayStartIndex(
        @Nonnull Buffer buffer
    ) throws ReadOnlyBufferException, UnsupportedOperationException {
        return buffer.arrayOffset() + buffer.position();
    }

    /**
     * Returns the actual start index (exclusive) of the backing array in the given buffer.
     *
     * @param buffer the given buffer
     * @return the actual start index (exclusive) of the backing array in the given buffer
     * @throws ReadOnlyBufferException       if the buffer is backed by an array but is read-only
     * @throws UnsupportedOperationException if the buffer is not backed by an accessible array
     */
    public static int arrayEndIndex(
        @Nonnull Buffer buffer
    ) throws ReadOnlyBufferException, UnsupportedOperationException {
        return buffer.arrayOffset() + buffer.position() + buffer.remaining();
    }

    /**
     * Reads all data from the source buffer into a new array, continuing until reaches the end of the source buffer,
     * and returns the array.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return a new array containing the read data, or {@code null} if reaches the end of the source buffer and no data
     * is read
     */
    public static byte @Nullable [] read(@Nonnull ByteBuffer src) {
        int len = src.remaining();
        if (len == 0) {
            return null;
        }
        byte[] result = new byte[len];
        src.get(result);
        return result;
    }

    /**
     * Reads a specified length of data from the source buffer into a new array, and returns the array. If the specified
     * length is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read
     * number reaches the specified length or reaches the end of the source buffer.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, or {@code null} if reaches the end of the source buffer and no data
     * is read
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static byte @Nullable [] read(@Nonnull ByteBuffer src, int len) throws IllegalArgumentException {
        IOChecker.checkLen(len);
        if (len == 0) {
            return new byte[0];
        }
        if (!src.hasRemaining()) {
            return null;
        }
        int actualLen = Math.min(len, src.remaining());
        byte[] result = new byte[actualLen];
        src.get(result);
        return result;
    }

    /**
     * Reads data from the source buffer into the destination array, until the read number reaches the array's length or
     * reaches the end of the source buffer, and returns the actual number of bytes read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination array
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     */
    public static int readTo(@Nonnull ByteBuffer src, byte @Nonnull [] dst) {
        return readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads a specified length of data from the source buffer into the destination array, starting at the specified
     * offset, until the read number reaches the specified length or reaches the end of the source buffer, and returns
     * the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     */
    public static int readTo(
        @Nonnull ByteBuffer src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(dst.length, off, len);
        return readTo0(src, dst, off, len);
    }

    /**
     * Reads data from the source buffer into the destination buffer, until reaches the end of any buffer, and returns
     * the actual number of bytes read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * source buffer and no data is read, returns {@code -1}.
     * <p>
     * The both buffers' positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination buffer
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        if (!dst.hasRemaining()) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return -1;
        }
        try {
            int actualLen = Math.min(src.remaining(), dst.remaining());
            if (src.remaining() <= dst.remaining()) {
                dst.put(src);
            } else {
                ByteBuffer srcSlice = slice0(src, 0, dst.remaining());
                dst.put(srcSlice);
                src.position(src.position() + actualLen);
            }
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads a specified length of data from the source buffer into the destination buffer, until the read number
     * reaches the specified length or reaches the end of any buffer, and returns the actual number of bytes read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the source buffer and no data is read, returns {@code -1}.
     * <p>
     * The both buffers' positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        if (len == 0) {
            return 0;
        }
        if (!dst.hasRemaining()) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return -1;
        }
        try {
            int actualLen = MathKit.min(src.remaining(), dst.remaining(), len);
            ByteBuffer srcBuf;
            if (src.remaining() > actualLen) {
                srcBuf = slice0(src, 0, actualLen);
            } else {
                srcBuf = src;
            }
            dst.put(srcBuf);
            if (srcBuf != src) {
                src.position(src.position() + actualLen);
            }
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads data from the source buffer into the destination channel, until reaches the end of the source buffer, and
     * returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination channel
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        if (src.remaining() == 0) {
            return -1;
        }
        try {
            int actualLen = src.remaining();
            while (src.remaining() > 0) {
                dst.write(src);
            }
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads a specified length of data from the source buffer into the destination channel, until the read number
     * reaches the specified length or reaches the end of the source buffer, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return readTo0(src, dst, len);
    }

    /**
     * Reads data from the source buffer into the output stream, until reaches the end of the source buffer, and returns
     * the actual number of bytes read to.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the output stream
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull OutputStream dst) throws IORuntimeException {
        if (src.remaining() == 0) {
            return -1;
        }
        return readTo0WithActualLen(src, dst, src.remaining());
    }

    /**
     * Reads a specified length of data from the source buffer into the output stream, until the read number reaches the
     * specified length or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull OutputStream dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return readTo0(src, dst, len);
    }

    static int readTo0(@Nonnull ByteBuffer src, byte @Nonnull [] dst, int off, int len) {
        if (len == 0) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return -1;
        }
        int actualLen = Math.min(len, src.remaining());
        src.get(dst, off, actualLen);
        return actualLen;
    }

    static int readTo0(
        @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        try {
            int actualLen = Math.min(src.remaining(), len);
            int oldLimit = src.limit();
            src.limit(src.position() + actualLen);
            while (src.remaining() > 0) {
                dst.write(src);
            }
            src.limit(oldLimit);
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull ByteBuffer src, @Nonnull OutputStream dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        int actualLen = Math.min(src.remaining(), len);
        return readTo0WithActualLen(src, dst, actualLen);
    }

    static int readTo0WithActualLen(
        @Nonnull ByteBuffer src, @Nonnull OutputStream dst, int actualLen
    ) throws IORuntimeException {
        try {
            if (src.hasArray()) {
                dst.write(src.array(), BufferKit.arrayStartIndex(src), actualLen);
                src.position(src.position() + actualLen);
            } else {
                byte[] buf = new byte[actualLen];
                src.get(buf);
                dst.write(buf);
            }
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source buffer into a new array, continuing until reaches the end of the source buffer,
     * and returns the array.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return a new array containing the read data, or {@code null} if reaches the end of the source buffer and no data
     * is read
     */
    public static char @Nullable [] read(@Nonnull CharBuffer src) {
        int len = src.remaining();
        if (len == 0) {
            return null;
        }
        char[] result = new char[len];
        src.get(result);
        return result;
    }

    /**
     * Reads a specified length of data from the source buffer into a new array, and returns the array. If the specified
     * length is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read
     * number reaches the specified length or reaches the end of the source buffer.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, or {@code null} if reaches the end of the source buffer and no data
     * is read
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static char @Nullable [] read(@Nonnull CharBuffer src, int len) throws IllegalArgumentException {
        IOChecker.checkLen(len);
        if (len == 0) {
            return new char[0];
        }
        if (!src.hasRemaining()) {
            return null;
        }
        int actualLen = Math.min(len, src.remaining());
        char[] result = new char[actualLen];
        src.get(result);
        return result;
    }

    /**
     * Reads data from the source buffer into the destination array, until the read number reaches the array's length or
     * reaches the end of the source buffer, and returns the actual number of chars read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination array
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     */
    public static int readTo(@Nonnull CharBuffer src, char @Nonnull [] dst) {
        return readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads a specified length of data from the source buffer into the destination array, starting at the specified
     * offset, until the read number reaches the specified length or reaches the end of the source buffer, and returns
     * the actual number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     */
    public static int readTo(
        @Nonnull CharBuffer src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(dst.length, off, len);
        return readTo0(src, dst, off, len);
    }

    /**
     * Reads data from the source buffer into the destination buffer, until reaches the end of any buffer, and returns
     * the actual number of chars read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * source buffer and no data is read, returns {@code -1}.
     * <p>
     * The both buffers' positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the destination buffer
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull CharBuffer src, @Nonnull CharBuffer dst) throws IORuntimeException {
        if (!dst.hasRemaining()) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return -1;
        }
        try {
            int actualLen = Math.min(src.remaining(), dst.remaining());
            if (src.remaining() <= dst.remaining()) {
                dst.put(src);
            } else {
                CharBuffer srcSlice = slice0(src, 0, dst.remaining());
                dst.put(srcSlice);
                src.position(src.position() + actualLen);
            }
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads a specified length of data from the source buffer into the destination buffer, until the read number
     * reaches the specified length or reaches the end of any buffer, and returns the actual number of chars read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the source buffer and no data is read, returns {@code -1}.
     * <p>
     * The both buffers' positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull CharBuffer src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        if (len == 0) {
            return 0;
        }
        if (!dst.hasRemaining()) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return -1;
        }
        try {
            int actualLen = MathKit.min(src.remaining(), dst.remaining(), len);
            CharBuffer srcBuf;
            if (src.remaining() > actualLen) {
                srcBuf = slice0(src, 0, actualLen);
            } else {
                srcBuf = src;
            }
            dst.put(srcBuf);
            if (srcBuf != src) {
                src.position(src.position() + actualLen);
            }
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads data from the source buffer into the output appender, until reaches the end of the source buffer, and
     * returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the output appender
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull CharBuffer src, @Nonnull Appendable dst) throws IORuntimeException {
        if (src.remaining() == 0) {
            return -1;
        }
        return readTo0WithActualLen(src, dst, src.remaining());
    }

    /**
     * Reads a specified length of data from the source buffer into the output appender, until the read number reaches
     * the specified length or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the source buffer
     * and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the output appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source buffer and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return readTo0(src, dst, len);
    }

    static int readTo0(
        @Nonnull CharBuffer src, char @Nonnull [] dst, int off, int len
    ) {
        if (len == 0) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return -1;
        }
        int actualLen = Math.min(len, src.remaining());
        src.get(dst, off, actualLen);
        return actualLen;
    }

    static int readTo0(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        int actualLen = Math.min(src.remaining(), len);
        return readTo0WithActualLen(src, dst, actualLen);
    }

    static int readTo0WithActualLen(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, int actualLen
    ) throws IORuntimeException {
        try {
            dst.append(src, 0, actualLen);
            src.position(src.position() + actualLen);
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source buffer as a string, continuing until reaches the end of the source buffer, and
     * returns the string.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return a string represents the read data, or {@code null} if reaches the end of the source buffer and no data is
     * read
     */
    public static @Nullable String string(@Nonnull CharBuffer src) {
        char[] chars = read(src);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads a specified length of data from the source buffer as a string, and returns the string. If the specified
     * length is {@code 0}, returns an empty string without reading. Otherwise, this method keeps reading until the read
     * number reaches the specified length or reaches the end of the source buffer.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param len the specified read length, must {@code >= 0}
     * @return a string represents the read data, or {@code null} if reaches the end of the source buffer and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static @Nullable String string(@Nonnull CharBuffer src, int len) throws IllegalArgumentException {
        char[] chars = read(src, len);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads all data from the source buffer as a string with {@link CharsKit#defaultCharset()}, continuing until
     * reaches the end of the source buffer, and returns the string.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return a string represents the read data, with {@link CharsKit#defaultCharset()}, or {@code null} if reaches the
     * end of the source buffer and no data is read
     */
    public static @Nullable String string(@Nonnull ByteBuffer src) {
        return string(src, CharsKit.defaultCharset());
    }

    /**
     * Reads all data from the source buffer as a string with the specified charset, continuing until reaches the end of
     * the source buffer, and returns the string.
     * <p>
     * If reaches the end of the source buffer and no data is read, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param cs  the specified charset
     * @return a string represents the read data, with the specified charset, or {@code null} if reaches the end of the
     * source buffer and no data is read
     */
    public static @Nullable String string(@Nonnull ByteBuffer src, @Nonnull Charset cs) {
        byte[] bytes = read(src);
        return bytes == null ? null : new String(bytes, cs);
    }

    /**
     * Writes string to the specified buffer with {@link CharsKit#defaultCharset()}.
     *
     * @param dst the specified buffer
     * @param str the string to write to the buffer
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(
        @Nonnull ByteBuffer dst, @Nonnull String str
    ) throws IORuntimeException {
        write(dst, str, CharsKit.defaultCharset());
    }

    /**
     * Writes string to the specified buffer with the specified charset.
     *
     * @param dst     the specified buffer
     * @param str     the string to write to the buffer
     * @param charset the specified charset
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(
        @Nonnull ByteBuffer dst, @Nonnull String str, @Nonnull Charset charset
    ) throws IORuntimeException {
        try {
            byte[] bytes = str.getBytes(charset);
            dst.put(bytes);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Creates a new buffer with the specified length. Its content is a shared subsequence of the given buffer's
     * content, starting at the given buffer's current position.
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     * <p>
     * The position and limit of the given buffer will not be changed.
     *
     * @param src the given buffer
     * @param len the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static @Nonnull ByteBuffer slice(@Nonnull ByteBuffer src, int len) throws IllegalArgumentException {
        IOChecker.checkLen(len);
        return slice0(src, 0, len);
    }

    /**
     * Creates a new buffer with the specified length. Its content is a shared subsequence of the given buffer's
     * content, starting at the specified offset of given buffer's current position ({@code src.position() + off}).
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     * <p>
     * The position and limit of the given buffer will not be changed.
     *
     * @param src the given buffer
     * @param off the specified offset
     * @param len the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IndexOutOfBoundsException if the offset and length is out of bounds
     */
    public static @Nonnull ByteBuffer slice(
        @Nonnull ByteBuffer src, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.remaining(), off, len);
        return slice0(src, off, len);
    }

    static @Nonnull ByteBuffer slice0(@Nonnull ByteBuffer src, int off, int len) {
        int pos = src.position();
        int limit = src.limit();
        src.position(pos + off);
        src.limit(pos + off + len);
        ByteBuffer slice = src.slice();
        src.position(pos);
        src.limit(limit);
        return slice;
    }

    /**
     * Creates a new buffer with the specified length. Its content is a shared subsequence of the given buffer's
     * content, starting at the given buffer's current position.
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     * <p>
     * The position and limit of the given buffer will not be changed.
     *
     * @param src the given buffer
     * @param len the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static @Nonnull CharBuffer slice(@Nonnull CharBuffer src, int len) throws IllegalArgumentException {
        IOChecker.checkLen(len);
        return slice0(src, 0, len);
    }

    /**
     * Creates a new buffer with the specified length. Its content is a shared subsequence of the given buffer's
     * content, starting at the specified offset of given buffer's current position ({@code src.position() + off}).
     * <p>
     * Changes to the given buffer's content will be visible in the new buffer, and vice versa. The new buffer's
     * position will be zero, its capacity and limit will be the specified length. The new buffer will be direct if, and
     * only if, the given buffer is direct, and it will be read-only if, and only if, the given buffer is read-only.
     * <p>
     * The position and limit of the given buffer will not be changed.
     *
     * @param src the given buffer
     * @param off the specified offset
     * @param len the specified length
     * @return a new buffer whose content is a shared subsequence of the given buffer's content
     * @throws IndexOutOfBoundsException if the offset and length is out of bounds
     */
    public static @Nonnull CharBuffer slice(
        @Nonnull CharBuffer src, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.remaining(), off, len);
        return slice0(src, off, len);
    }

    static @Nonnull CharBuffer slice0(@Nonnull CharBuffer src, int off, int len) {
        int pos = src.position();
        int limit = src.limit();
        src.position(pos + off);
        src.limit(pos + off + len);
        CharBuffer slice = src.slice();
        src.position(pos);
        src.limit(limit);
        return slice;
    }

    /**
     * Returns a new buffer copied from the given source buffer. The content of the new buffer is independent and copied
     * from {@code 0} to {@code src.capacity()} (not {@code src.position()} to {@code src.limit()}).
     * <p>
     * Position of the source buffer will not be changed. The new buffer's position, limit and capacity are same with
     * the source buffer's, and the new buffer is direct if, and only if, the source buffer is direct.
     *
     * @param src the given source buffer
     * @return a new buffer copied from the given source buffer
     */
    public static @Nonnull ByteBuffer copy(@Nonnull ByteBuffer src) {
        ByteBuffer dst = src.isDirect() ?
            ByteBuffer.allocateDirect(src.capacity())
            :
            ByteBuffer.allocate(src.capacity());
        int pos = src.position();
        int limit = src.limit();
        src.position(0);
        src.limit(src.capacity());
        dst.put(src);
        src.position(pos);
        src.limit(limit);
        dst.position(pos);
        dst.limit(limit);
        return dst;
    }

    /**
     * Returns a new buffer copied from the given source buffer. The content of the new buffer is independent and copied
     * from {@code 0} to {@code src.capacity()} (not {@code src.position()} to {@code src.limit()}).
     * <p>
     * Position of the source buffer will not be changed. The new buffer's position, limit and capacity are same with
     * the source buffer's, and the new buffer is direct if, and only if, the source buffer is direct.
     *
     * @param src the given source buffer
     * @return a new buffer copied from the given source buffer
     */
    public static @Nonnull CharBuffer copy(@Nonnull CharBuffer src) {
        CharBuffer dst = src.isDirect() ?
            directCharBuffer(src.capacity())
            :
            CharBuffer.allocate(src.capacity());
        int pos = src.position();
        int limit = src.limit();
        src.position(0);
        src.limit(src.capacity());
        dst.put(src);
        src.position(pos);
        src.limit(limit);
        dst.position(pos);
        dst.limit(limit);
        return dst;
    }

    /**
     * Returns a new array copied from the remaining content of the given source buffer. Position of the source buffer
     * will not be changed.
     *
     * @param src the given source buffer
     * @return a new array copied from the remaining content of the given source buffer
     */
    public static byte @Nonnull [] copyContent(@Nonnull ByteBuffer src) {
        byte[] ret = new byte[src.remaining()];
        int pos = src.position();
        src.get(ret);
        src.position(pos);
        return ret;
    }

    /**
     * Returns a new array copied from the remaining content of the given source buffer. Position of the source buffer
     * will not be changed.
     *
     * @param src the given source buffer
     * @return a new array copied from the remaining content of the given source buffer
     */
    public static char @Nonnull [] copyContent(@Nonnull CharBuffer src) {
        char[] ret = new char[src.remaining()];
        int pos = src.position();
        src.get(ret);
        src.position(pos);
        return ret;
    }

    /**
     * Returns a new direct buffer of which content is copied from the given array.
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity is the array's length.
     *
     * @param src the given array
     * @return a new direct buffer of which content is copied from the given array
     */
    public static @Nonnull ByteBuffer copyDirect(byte @Nonnull [] src) {
        ByteBuffer buf = ByteBuffer.allocateDirect(src.length);
        buf.put(src);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer of which content is a copy of data from the given array. The copied data starts at
     * the specified offset and has the specified length.
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity is the specified length.
     *
     * @param src the given array
     * @param off the specified offset
     * @param len the specified length
     * @return a new direct buffer of which content is a copy of data from the given array
     */
    public static @Nonnull ByteBuffer copyDirect(
        byte @Nonnull [] src, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.length, off, len);
        ByteBuffer buf = ByteBuffer.allocateDirect(len);
        buf.put(src, off, len);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer of which content is copied from the given array.
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity is the array's length.
     *
     * @param src the given array
     * @return a new direct buffer of which content is copied from the given array
     */
    public static @Nonnull CharBuffer copyDirect(char @Nonnull [] src) {
        CharBuffer buf = directCharBuffer(src.length);
        buf.put(src);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer of which content is a copy of data from the given array. The copied data starts at
     * the specified offset and has the specified length.
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity is the specified length.
     *
     * @param src the given array
     * @param off the specified offset
     * @param len the specified length
     * @return a new direct buffer of which content is a copy of data from the given array
     */
    public static @Nonnull CharBuffer copyDirect(
        char @Nonnull [] src, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.length, off, len);
        CharBuffer buf = directCharBuffer(len);
        buf.put(src, off, len);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct {@link CharBuffer} with the specified capacity, and its endian is
     * {@link ByteOrder#BIG_ENDIAN}.
     * <p>
     * Returned buffer's position is {@code 0}, limit equals to the capacity.
     *
     * @param capacity the specified capacity
     * @return a new direct {@link CharBuffer} with the specified capacity
     * @throws IllegalArgumentException if the specified capacity is negative
     */
    public static @Nonnull CharBuffer directCharBuffer(int capacity) throws IllegalArgumentException {
        IOChecker.checkCapacity(capacity);
        return ByteBuffer.allocateDirect(capacity * 2).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
    }
}
