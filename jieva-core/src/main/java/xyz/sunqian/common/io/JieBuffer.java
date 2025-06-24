package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

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
     * @throws ReadOnlyBufferException       If the buffer is backed by an array but is read-only
     * @throws UnsupportedOperationException If the buffer is not backed by an accessible array
     */
    public static int arrayEndIndex(
        @Nonnull Buffer buffer
    ) throws ReadOnlyBufferException, UnsupportedOperationException {
        return buffer.arrayOffset() + buffer.position() + buffer.remaining();
    }

    /**
     * Reads all data from the source buffer into a new array, continuing until reaches the end of the buffer, and
     * returns the array. If the end of the source buffer has already been reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return the array containing the data
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
     * Reads the data of the specified length from the source buffer into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the buffer. If the end of the source buffer
     * has already been reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param len the specified read length, must {@code >= 0}
     * @return the array containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static byte @Nullable [] read(@Nonnull ByteBuffer src, int len) throws IllegalArgumentException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
     * Reads the data from the source buffer into the specified array, until the read number reaches the array's length
     * or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source buffer has already
     * been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified array
     * @return the actual number of bytes read
     */
    public static int readTo(@Nonnull ByteBuffer src, byte @Nonnull [] dst) {
        return readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads the data from the source buffer into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source buffer,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static int readTo(
        @Nonnull ByteBuffer src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(dst.length, off, len);
        return readTo0(src, dst, off, len);
    }

    private static int readTo0(@Nonnull ByteBuffer src, byte @Nonnull [] dst, int off, int len) {
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

    /**
     * Reads the data from the source buffer into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified buffer
     * @return the actual number of bytes read
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
     * Reads the data of the specified length from the source buffer into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
            int actualLen = Math.min(src.remaining(), dst.remaining());
            actualLen = Math.min(len, actualLen);
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
     * Reads the data from the source buffer into the specified channel, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified channel
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return readTo0(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source buffer into the specified channel, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len);
    }

    private static int readTo0(
        @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        try {
            int actualLen = len < 0 ? src.remaining() : Math.min(src.remaining(), len);
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

    /**
     * Reads the data from the source buffer into the specified stream, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified stream
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull OutputStream dst) throws IORuntimeException {
        return readTo0(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source buffer into the specified stream, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull OutputStream dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len);
    }

    private static int readTo0(
        @Nonnull ByteBuffer src, @Nonnull OutputStream dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        try {
            int actualLen = len < 0 ? src.remaining() : Math.min(src.remaining(), len);
            if (src.hasArray()) {
                dst.write(src.array(), JieBuffer.arrayStartIndex(src), actualLen);
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
     * Reads all data from the source buffer into a new array, continuing until reaches the end of the buffer, and
     * returns the array. If the end of the source buffer has already been reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return the array containing the data
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
     * Reads the data of the specified length from the source buffer into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the buffer. If the end of the source buffer
     * has already been reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param len the specified read length, must {@code >= 0}
     * @return the array containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static char @Nullable [] read(@Nonnull CharBuffer src, int len) throws IllegalArgumentException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
     * Reads the data from the source buffer into the specified array, until the read number reaches the array's length
     * or reaches the end of the source buffer, returns the actual number of chars read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source buffer has already
     * been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified array
     * @return the actual number of chars read
     */
    public static int readTo(@Nonnull CharBuffer src, char @Nonnull [] dst) {
        return readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads the data from the source buffer into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source buffer,
     * returns the actual number of chars read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static int readTo(
        @Nonnull CharBuffer src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(dst.length, off, len);
        return readTo0(src, dst, off, len);
    }

    private static int readTo0(
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

    /**
     * Reads the data from the source buffer into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of chars read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified buffer
     * @return the actual number of chars read
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
     * Reads the data of the specified length from the source buffer into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of chars read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's positions increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull CharBuffer src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
            int actualLen = Math.min(src.remaining(), dst.remaining());
            actualLen = Math.min(len, actualLen);
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
     * Reads the data from the source buffer into the specified appender, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of chars read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified appender
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull CharBuffer src, @Nonnull Appendable dst) throws IORuntimeException {
        return readTo0(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source buffer into the specified appender, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of chars read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len);
    }

    private static int readTo0(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        try {
            int actualLen = len < 0 ? src.remaining() : Math.min(src.remaining(), len);
            dst.append(src, 0, actualLen);
            src.position(src.position() + actualLen);
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source buffer into a new string, continuing until reaches the end of the buffer, and
     * returns the string. If the end of the source buffer has already been reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return the string containing the data
     */
    public static @Nullable String string(@Nonnull CharBuffer src) {
        char[] chars = read(src);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads the data of the specified length from the source buffer into a new string, and returns the string. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the buffer. If the end of the source buffer
     * has already been reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param len the specified read length, must {@code >= 0}
     * @return the string containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     */
    public static @Nullable String string(@Nonnull CharBuffer src, int len) {
        char[] chars = read(src, len);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads all data from the source buffer into a new string with the {@link JieChars#defaultCharset()}, continuing
     * until reaches the end of the buffer, and returns the string. If the end of the source buffer has already been
     * reached, returns {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @return the string containing the data
     */
    public static @Nullable String string(@Nonnull ByteBuffer src) {
        return string(src, JieChars.defaultCharset());
    }

    /**
     * Reads all data from the source buffer into a new string with the specified charset, continuing until reaches the
     * end of the buffer, and returns the string. If the end of the source buffer has already been reached, returns
     * {@code null}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param cs  the specified charset
     * @return the string containing the data
     */
    public static @Nullable String string(@Nonnull ByteBuffer src, @Nonnull Charset cs) {
        byte[] bytes = read(src);
        return bytes == null ? null : new String(bytes, cs);
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the given buffer's current position, and up to the specified length.
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
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return slice0(src, 0, len);
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the specified offset from the given buffer's current position, and up to the specified
     * length.
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
        JieCheck.checkOffsetLength(src.remaining(), off, len);
        return slice0(src, off, len);
    }

    private static @Nonnull ByteBuffer slice0(@Nonnull ByteBuffer src, int off, int len) {
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
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the given buffer's current position, and up to the specified length.
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
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return slice0(src, 0, len);
    }

    /**
     * Creates a new buffer whose content is a shared subsequence of the given buffer's content. The content of the new
     * buffer will start at the specified offset from the given buffer's current position, and up to the specified
     * length.
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
        JieCheck.checkOffsetLength(src.remaining(), off, len);
        return slice0(src, off, len);
    }

    private static @Nonnull CharBuffer slice0(@Nonnull CharBuffer src, int off, int len) {
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
     * The new buffer's position, limit and capacity are same with the source buffer's, and the new buffer is direct if,
     * and only if, the source buffer is direct.
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
     * The new buffer's position, limit and capacity are same with the source buffer's, and the new buffer is direct if,
     * and only if, the source buffer is direct.
     *
     * @param src the given source buffer
     * @return a new buffer copied from the given source buffer
     */
    public static @Nonnull CharBuffer copy(@Nonnull CharBuffer src) {
        CharBuffer dst = src.isDirect() ?
            directBuffer(src.capacity())
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
     * Returns a new direct buffer of which content is copied from the given array.
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity are same with the array's length.
     *
     * @param src the given array
     * @return a new direct buffer of which content is copied from the given array
     */
    public static @Nonnull ByteBuffer directBuffer(byte @Nonnull [] src) {
        ByteBuffer buf = ByteBuffer.allocateDirect(src.length);
        buf.put(src);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer of which content is copied from the given array (starting at the specified offset and
     * up to the specified length).
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity are same with the specified length.
     *
     * @param src the given array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return a new direct buffer of which content is copied from the given array
     */
    public static @Nonnull ByteBuffer directBuffer(
        byte @Nonnull [] src, int off, int len
    ) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(src.length, off, len);
        ByteBuffer buf = ByteBuffer.allocateDirect(len);
        buf.put(src, off, len);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer of which content is copied from the given array.
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity are same with the array's length.
     *
     * @param src the given array
     * @return a new direct buffer of which content is copied from the given array
     */
    public static @Nonnull CharBuffer directBuffer(char @Nonnull [] src) {
        CharBuffer buf = directBuffer(src.length);
        buf.put(src);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer of which content is copied from the given array (starting at the specified offset and
     * up to the specified length).
     * <p>
     * Returned buffer's position is {@code 0}, limit and capacity are same with the specified length.
     *
     * @param src the given array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return a new direct buffer of which content is copied from the given array
     */
    public static @Nonnull CharBuffer directBuffer(
        char @Nonnull [] src, int off, int len
    ) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(src.length, off, len);
        CharBuffer buf = directBuffer(len);
        buf.put(src, off, len);
        buf.flip();
        return buf;
    }

    /**
     * Returns a new direct buffer with the specified capacity.
     * <p>
     * Returned buffer's position is {@code 0}, limit equals to the capacity.
     *
     * @param capacity the specified capacity
     * @return a new direct buffer with the specified capacity
     * @throws IllegalArgumentException if the specified capacity is negative
     */
    public static @Nonnull CharBuffer directBuffer(int capacity) throws IllegalArgumentException {
        JieCheck.checkArgument(capacity >= 0, "capacity must >= 0");
        return ByteBuffer.allocateDirect(capacity * 2).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
    }
}
