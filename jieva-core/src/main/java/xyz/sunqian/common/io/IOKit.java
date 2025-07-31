package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Static utility class for I/O operations, some methods are provided by {@link IOOperator}.
 *
 * @author sunqian
 */
public class IOKit {

    private static final @Nonnull IOOperator io = IOOperator.get(bufferSize());

    /**
     * Returns the recommended IO buffer size, typically is 1024 * 8 = 8192.
     *
     * @return the recommended IO buffer size
     */
    public static int bufferSize() {
        return IOHelper.DEFAULT_BUFFER_SIZE;
    }

    /**
     * Reads all data from the input stream into a new array, continuing until reaches the end of the input stream, and
     * returns the array.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a new array containing the read data, or {@code null} if reaches the end of the input stream and no data
     * is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nullable [] read(@Nonnull InputStream src) throws IORuntimeException {
        return io.read(src);
    }

    /**
     * Reads a specified length of data from the input stream into a new array, and returns the array. If the specified
     * length is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read
     * number reaches the specified length or reaches the end of the input stream.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, or {@code null} if reaches the end of the input stream and no data
     * is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static byte @Nullable [] read(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.read(src, len);
    }

    /**
     * Reads all data from the source channel into a new buffer, continuing until reaches the end of the source channel,
     * and returns the buffer. The new buffer's position is {@code 0} and limit equals its capacity.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     *
     * @param src the source channel
     * @return a new buffer containing the read data, or {@code null} if reaches the end of the source channel and no
     * data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable ByteBuffer read(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return io.read(src);
    }

    /**
     * Reads a specified length of data from the source channel into a new buffer, and returns the buffer. If the
     * specified length is {@code 0}, returns an empty buffer without reading. Otherwise, this method keeps reading
     * until the read number reaches the specified length or reaches the end of the source channel. The new buffer's
     * position is {@code 0} and limit equals its capacity.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return a new buffer containing the read data, or {@code null} if reaches the end of the source channel and no
     * data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nullable ByteBuffer read(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.read(src, len);
    }

    /**
     * Reads data from the input stream into the output stream, until reaches the end of the input stream, and returns
     * the actual number of bytes read to.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output stream
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the output stream, until the read number reaches the
     * specified length or reaches the end of the input stream, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the input stream
     * and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads data from the input stream into the output channel, until reaches the end of the input stream, and returns
     * the actual number of bytes read to.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output channel
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull InputStream src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the output channel, until the read number reaches the
     * specified length or reaches the end of the input stream, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the input stream
     * and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads data from the input stream into the destination array, until the read number reaches the array's length or
     * reaches the end of the input stream, and returns the actual number of bytes read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the input stream and
     * no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the destination array
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull InputStream src, byte @Nonnull [] dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the destination array, starting at the specified
     * offset, until the read number reaches the specified length or reaches the end of the input stream, and returns
     * the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the input stream
     * and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return io.readTo(src, dst, off, len);
    }

    /**
     * Reads data from the input stream into the destination buffer, until reaches the end of any buffer, and returns
     * the actual number of bytes read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * input stream and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the input stream
     * @param dst the destination buffer
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the destination buffer, until the read number reaches
     * the specified length or reaches the end of any buffer, and returns the actual number of bytes read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the input stream and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the input stream
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input stream and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads data from the input channel into the output stream, until reaches the end of the input channel, and returns
     * the actual number of bytes read to.
     * <p>
     * If reaches the end of the input channel and no data is read, returns {@code -1}.
     *
     * @param src the input channel
     * @param dst the output stream
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull ReadableByteChannel src, @Nonnull OutputStream dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input channel into the output stream, until the read number reaches the
     * specified length or reaches the end of the input channel, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the input channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the input channel
     * @param dst the output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the input channel and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads data from the source channel into the destination channel, until reaches the end of the source channel, and
     * returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination channel
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the source channel into the destination channel, until the read number
     * reaches the specified length or reaches the end of the source channel, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the source channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source channel and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads data from the source channel into the destination array, until the read number reaches the array's length
     * or reaches the end of the source channel, and returns the actual number of bytes read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination array
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ReadableByteChannel src, byte @Nonnull [] dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the source channel into the destination array, starting at the specified
     * offset, until the read number reaches the specified length or reaches the end of the source channel, and returns
     * the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source channel and no data is
     * read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return io.readTo(src, dst, off, len);
    }

    /**
     * Reads data from the source channel into the destination buffer, until reaches the end of any buffer, and returns
     * the actual number of bytes read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * source channel and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the destination buffer
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the source channel into the destination buffer, until the read number
     * reaches the specified length or reaches the end of any buffer, and returns the actual number of bytes read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the source channel and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of the source channel and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads all data from the reader into a new array, continuing until reaches the end of the reader, and returns the
     * array.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a new array containing the read data, or {@code null} if reaches the end of the reader and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char @Nullable [] read(@Nonnull Reader src) throws IORuntimeException {
        return io.read(src);
    }

    /**
     * Reads a specified length of data from the reader into a new array, and returns the array. If the specified length
     * is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read number
     * reaches the specified length or reaches the end of the reader.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, or {@code null} if reaches the end of the reader and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.read(src, len);
    }

    /**
     * Reads all data from the reader as a string, continuing until reaches the end of the reader, and returns the
     * string.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a string represents the read data, or {@code null} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(@Nonnull Reader src) throws IORuntimeException {
        return io.string(src);
    }

    /**
     * Reads a specified length of data from the reader as a string, and returns the string. If the specified length is
     * {@code 0}, returns an empty string without reading. Otherwise, this method keeps reading until the read number
     * reaches the specified length or reaches the end of the reader.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a string represents the read data, or {@code null} if reaches the end of the reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nullable String string(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.string(src, len);
    }

    /**
     * Reads data from the reader into the appender, until reaches the end of the reader, and returns the actual number
     * of chars read to.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the reader into the appender, until the read number reaches the specified
     * length or reaches the end of the reader, returns the actual number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Reads data from the reader into the destination array, until the read number reaches the array's length or
     * reaches the end of the reader, and returns the actual number of chars read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the reader into the destination array, starting at the specified offset,
     * until the read number reaches the specified length or reaches the end of the reader, and returns the actual
     * number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return io.readTo(src, dst, off, len);
    }

    /**
     * Reads data from the reader into the destination buffer, until reaches the end of any buffer, and returns the
     * actual number of chars read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the destination buffer
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        return io.readTo(src, dst);
    }

    /**
     * Reads a specified length of data from the reader into the destination buffer, until the read number reaches the
     * specified length or reaches the end of any buffer, and returns the actual number of chars read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readTo(src, dst, len);
    }

    /**
     * Writes all data to the specified appender from the given array.
     *
     * @param dst the specified appender
     * @param src the given array
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(@Nonnull Appendable dst, char @Nonnull [] src) throws IORuntimeException {
        write(dst, src, 0, src.length);
    }

    /**
     * Writes a specified length of data to the specified appender from the given array, starting at the specified
     * offset.
     *
     * @param dst the specified appender
     * @param src the given array
     * @param off the specified offset
     * @param len the specified length
     * @throws IndexOutOfBoundsException if the specified offset or length is out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static void write(
        @Nonnull Appendable dst, char @Nonnull [] src, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        try {
            if (dst instanceof Writer) {
                ((Writer) dst).write(src, off, len);
                return;
            }
            dst.append(new String(src, off, len));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes string to the specified output stream with {@link CharsKit#defaultCharset()}.
     *
     * @param dst the specified output stream
     * @param str the string to write to the output stream
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(
        @Nonnull OutputStream dst, @Nonnull String str
    ) throws IORuntimeException {
        write(dst, str, CharsKit.defaultCharset());
    }

    /**
     * Writes string to the specified output stream with the specified charset.
     *
     * @param dst     the specified output stream
     * @param str     the string to write to the output stream
     * @param charset the specified charset
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(
        @Nonnull OutputStream dst, @Nonnull String str, @Nonnull Charset charset
    ) throws IORuntimeException {
        try {
            byte[] bytes = str.getBytes(charset);
            dst.write(bytes);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads available data from the input stream into a new array, and returns the array. The array's length is based
     * on the {@link InputStream#available()}.
     *
     * @param in the input stream
     * @return the array containing the available data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nonnull [] available(@Nonnull InputStream in) throws IORuntimeException {
        try {
            int available = in.available();
            if (available <= 0) {
                return new byte[0];
            }
            byte[] bytes = new byte[available];
            int c = in.read(bytes);
            if (c < 0) {
                return new byte[0];
            }
            if (c == available) {
                return bytes;
            }
            return Arrays.copyOf(bytes, c);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the input stream as a string with {@link CharsKit#defaultCharset()}, continuing until reaches
     * the end of the input stream, and returns the string.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code null}.
     *
     * @param in the input stream
     * @return a string represents the read data, or {@code null} if reaches the end of the input stream and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(@Nonnull InputStream in) throws IORuntimeException {
        return string(in, CharsKit.defaultCharset());
    }

    /**
     * Reads all data from the input stream as a string with the specified charset, continuing until reaches the end of
     * the input stream, and returns the string.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code null}.
     *
     * @param in      the input stream
     * @param charset the specified charset
     * @return a string represents the read data, or {@code null} if reaches the end of the input stream and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(
        @Nonnull InputStream in, @Nonnull Charset charset
    ) throws IORuntimeException {
        byte[] bytes = read(in);
        return bytes == null ? null : new String(bytes, charset);
    }

    /**
     * Closes the given closeable object, which is an instance of {@link Closeable} or {@link AutoCloseable} or
     * {@link IORuntimeCloseable}. If the given object is not an instance of above interfaces, then invoking this method
     * has no effect.
     *
     * @param closeable the given closeable object
     * @throws IOException if an I/O error occurs
     */
    public static void close(Object closeable) throws IOException {
        if (closeable instanceof Closeable) {
            ((Closeable) closeable).close();
        } else if (closeable instanceof AutoCloseable) {
            try {
                ((AutoCloseable) closeable).close();
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
        } else if (closeable instanceof IORuntimeCloseable) {
            try {
                ((IORuntimeCloseable) closeable).close();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Flushes the given flushable object, which is an instance of {@link Flushable}. If the given object is not an
     * instance of {@link Flushable}, then invoking this method has no effect.
     *
     * @param flushable the given flushable object
     * @throws IOException if an I/O error occurs
     */
    public static void flush(Object flushable) throws IOException {
        if (flushable instanceof Flushable) {
            ((Flushable) flushable).flush();
        }
    }

    /**
     * Wraps the given array as a new {@link InputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @return the given array as a new {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(byte @Nonnull [] array) {
        return IOImpls.inputStream(array);
    }

    /**
     * Wraps a specified length of data from the given array, starting at the specified offset, as a new
     * {@link InputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @param off   the specified offset
     * @param len   the specified length
     * @return the given array as a new {@link InputStream}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static @Nonnull InputStream newInputStream(
        byte @Nonnull [] array, int off, int len
    ) throws IndexOutOfBoundsException {
        return IOImpls.inputStream(array, off, len);
    }

    /**
     * Wraps the given buffer as a new {@link InputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param buffer the given buffer
     * @return the given buffer as a new {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull ByteBuffer buffer) {
        return IOImpls.inputStream(buffer);
    }

    /**
     * Wraps the given random access file, starting at the specified file pointer offset, as a new {@link InputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supports via {@link RandomAccessFile#seek(long)};</li>
     *     <li>close: closes the random access file;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param raf the given random access file
     * @param off the specified file pointer offset
     * @return the given random access file as a new {@link InputStream}
     * @throws IllegalArgumentException if the offset is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nonnull InputStream newInputStream(
        @Nonnull RandomAccessFile raf, long off
    ) throws IllegalArgumentException, IORuntimeException {
        return IOImpls.inputStream(raf, off);
    }

    /**
     * Wraps the given reader as a new {@link InputStream} with {@link CharsKit#defaultCharset()}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given reader;</li>
     *     <li>close: closes the reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param reader the given reader
     * @return the given reader as a new {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull Reader reader) {
        return newInputStream(reader, CharsKit.defaultCharset());
    }

    /**
     * Wraps the given reader as a new {@link InputStream} with the specified charset.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given reader;</li>
     *     <li>close: closes the reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param reader  the given reader
     * @param charset the specified charset
     * @return the given reader as a new {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull Reader reader, @Nonnull Charset charset) {
        return IOImpls.inputStream(reader, charset);
    }

    /**
     * Wraps the given reader as a new {@link InputStream}. Note this method returns a new wrapper object, it is not
     * equivalent to {@link ByteReader#asInputStream()}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given reader;</li>
     *     <li>close: closes the reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param reader the given reader
     * @return the given reader as a new {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull ByteReader reader) {
        return IOImpls.inputStream(reader);
    }

    /**
     * Wraps the given stream as a new {@link InputStream} of which readable number is limited to the specified limit.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given stream;</li>
     *     <li>close: closes the given stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param stream the given stream
     * @param limit  the specified limit, must {@code >= 0}
     * @return the given stream as a new {@link InputStream} of which readable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public static @Nonnull InputStream limitedInputStream(
        @Nonnull InputStream stream, long limit
    ) throws IllegalArgumentException {
        return IOImpls.inputStream(stream, limit);
    }

    /**
     * Returns a singleton empty {@link InputStream}.
     *
     * @return a singleton empty {@link InputStream}
     */
    public static @Nonnull InputStream emptyInputStream() {
        return IOImpls.emptyInputStream();
    }

    /**
     * Wraps the given array as a new {@link Reader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @return the given array as a new {@link Reader}
     */
    public static @Nonnull Reader newReader(char @Nonnull [] array) {
        return IOImpls.reader(array);
    }

    /**
     * Wraps a specified length of data from the given array, starting at the specified offset, as a new
     * {@link Reader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @param off   the specified offset
     * @param len   the specified length
     * @return the given array as a new {@link Reader}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static @Nonnull Reader newReader(
        char @Nonnull [] array, int off, int len
    ) throws IndexOutOfBoundsException {
        return IOImpls.reader(array, off, len);
    }

    /**
     * Wraps the given char sequence as a new {@link Reader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param chars the given char sequence
     * @return the given char sequence as a new {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull CharSequence chars) {
        return IOImpls.reader(chars);
    }

    /**
     * Wraps the given char sequence, starting at the specified start index inclusive and ending at the specified end
     * index exclusive, as a new {@link Reader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param chars the given char sequence
     * @param start the specified start index inclusive
     * @param end   the specified end index exclusive
     * @return the given char sequence as a new {@link Reader}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static @Nonnull Reader newReader(
        @Nonnull CharSequence chars, int start, int end
    ) throws IndexOutOfBoundsException {
        return IOImpls.reader(chars, start, end);
    }

    /**
     * Wraps the given buffer as a new {@link Reader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported and size-unlimited;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param buffer the given buffer
     * @return the given buffer as a new {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull CharBuffer buffer) {
        return IOImpls.reader(buffer);
    }

    /**
     * Wraps the given stream as a new {@link Reader} with {@link CharsKit#defaultCharset()}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given stream;</li>
     *     <li>close: closes the stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param stream the given stream
     * @return the given stream as a new {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull InputStream stream) {
        return newReader(stream, CharsKit.defaultCharset());
    }

    /**
     * Wraps the given stream as a new {@link Reader} with the specified charset.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given stream;</li>
     *     <li>close: closes the stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param stream  the given stream
     * @param charset the specified charset
     * @return the given stream as a new {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull InputStream stream, @Nonnull Charset charset) {
        return IOImpls.reader(stream, charset);
    }

    /**
     * Wraps the given reader as a new {@link Reader}. Note this method returns a new wrapper object, it is not
     * equivalent to {@link CharReader#asReader()}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given reader;</li>
     *     <li>close: closes the reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param reader the given reader
     * @return the given reader as a new {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull CharReader reader) {
        return IOImpls.reader(reader);
    }

    /**
     * Wraps the given reader as a new {@link Reader} of which readable number is limited to the specified limit.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the given reader;</li>
     *     <li>close: closes the given reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param reader the given reader
     * @param limit  the specified limit, must {@code >= 0}
     * @return the given reader as a new {@link Reader} of which readable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public static @Nonnull Reader limitedReader(@Nonnull Reader reader, long limit) throws IllegalArgumentException {
        return IOImpls.reader(reader, limit);
    }

    /**
     * Returns a singleton empty {@link Reader}.
     *
     * @return a singleton empty {@link Reader}
     */
    public static @Nonnull Reader emptyReader() {
        return IOImpls.emptyReader();
    }

    /**
     * Wraps the given array as a new {@link OutputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: in bounds of the array;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>flush: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @return the given array as a new {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(byte @Nonnull [] array) {
        return IOImpls.outputStream(array);
    }

    /**
     * Wraps a specified length of data from the given array, starting at the specified offset, as a new
     * {@link OutputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: in specified bounds of the array;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>flush: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @param off   the specified offset
     * @param len   the specified length
     * @return the given array as a new {@link OutputStream}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static @Nonnull OutputStream newOutputStream(
        byte @Nonnull [] array, int off, int len
    ) throws IndexOutOfBoundsException {
        return IOImpls.outputStream(array, off, len);
    }

    /**
     * Wraps the given buffer as a new {@link OutputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: in bounds of the buffer;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>flush: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param buffer the given buffer
     * @return the given buffer as a new {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(@Nonnull ByteBuffer buffer) {
        return IOImpls.outputStream(buffer);
    }

    /**
     * Wraps the given random access file, starting at the specified file pointer offset, as a new
     * {@link OutputStream}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity:determined by the random access file;</li>
     *     <li>close: closes the random access file;</li>
     *     <li>flush: flushes the random access file;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param raf the given random access file
     * @param off the specified file pointer offset
     * @return the given random access file as a new {@link OutputStream}
     * @throws IllegalArgumentException if the offset is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nonnull OutputStream newOutputStream(
        @Nonnull RandomAccessFile raf, long off
    ) throws IllegalArgumentException, IORuntimeException {
        return IOImpls.outputStream(raf, off);
    }

    /**
     * Wraps the given appender as a new {@link OutputStream} with {@link CharsKit#defaultCharset()}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity:determined by the appender;</li>
     *     <li>close: closes the appender;</li>
     *     <li>flush: flushes the appender;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param appender the given appender
     * @return the given appender as a new {@link InputStream}
     */
    public static @Nonnull OutputStream newOutputStream(@Nonnull Appendable appender) {
        return newOutputStream(appender, CharsKit.defaultCharset());
    }

    /**
     * Wraps the given appender as a new {@link OutputStream} with the specified charset.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity:determined by the appender;</li>
     *     <li>close: closes the appender;</li>
     *     <li>flush: flushes the appender;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param appender the given appender
     * @param charset  the specified charset
     * @return the given appender as a new {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(@Nonnull Appendable appender, @Nonnull Charset charset) {
        return IOImpls.outputStream(appender, charset);
    }

    /**
     * Wraps the given stream as a new {@link OutputStream} of which writeable number is limited to the specified
     * limit.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: based on the given stream and limited by the specified limit;</li>
     *     <li>close: closes the given stream;</li>
     *     <li>flush: flushes the given stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param stream the given stream
     * @param limit  the specified limit, must {@code >= 0}
     * @return the given stream as a new {@link OutputStream} of which writeable number is limited to the specified
     * limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public static @Nonnull OutputStream limitedOutputStream(
        @Nonnull OutputStream stream, long limit
    ) throws IllegalArgumentException {
        return IOImpls.outputStream(stream, limit);
    }

    /**
     * Returns a singleton {@link OutputStream} which supports writing infinitely data but immediately discards them.
     *
     * @return a singleton {@link OutputStream} which supports writing infinitely data but immediately discards them.
     */
    public static @Nonnull OutputStream nullOutputStream() {
        return IOImpls.nullOutputStream();
    }

    /**
     * Wraps the given array as a new {@link Writer}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: in bounds of the array;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>flush: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @return the given array as a new {@link Writer}
     */
    public static @Nonnull Writer newWriter(char @Nonnull [] array) {
        return IOImpls.writer(array);
    }

    /**
     * Wraps a specified length of data from the given array, starting at the specified offset, as a new
     * {@link Writer}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: in specified bounds of the array;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>flush: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param array the given array
     * @param off   the specified offset
     * @param len   the specified length
     * @return the given array as a new {@link Writer}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    public static @Nonnull Writer newWriter(
        char @Nonnull [] array, int off, int len
    ) throws IndexOutOfBoundsException {
        return IOImpls.writer(array, off, len);
    }

    /**
     * Wraps the given buffer as a new {@link Writer}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: in bounds of the buffer;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>flush: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param buffer the given buffer
     * @return the given buffer as a new {@link Writer}
     */
    public static @Nonnull Writer newWriter(@Nonnull CharBuffer buffer) {
        return IOImpls.writer(buffer);
    }

    /**
     * Wraps the given stream as a new {@link Writer} with {@link CharsKit#defaultCharset()}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity:determined by the stream;</li>
     *     <li>close: closes the stream;</li>
     *     <li>flush: flushes the stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param stream the given stream
     * @return the given stream as a new {@link InputStream}
     */
    public static @Nonnull Writer newWriter(@Nonnull OutputStream stream) {
        return newWriter(stream, CharsKit.defaultCharset());
    }

    /**
     * Wraps the given stream as a new {@link Writer} with the specified charset.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity:determined by the stream;</li>
     *     <li>close: closes the stream;</li>
     *     <li>flush: flushes the stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param stream  the given stream
     * @param charset the specified charset
     * @return the given stream as a new {@link Writer}
     */
    public static @Nonnull Writer newWriter(@Nonnull OutputStream stream, @Nonnull Charset charset) {
        return IOImpls.writer(stream, charset);
    }

    /**
     * Wraps the given writer as a new {@link Writer} of which writeable number is limited to the specified limit.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>capacity: based on the given writer and limited by the specified limit;</li>
     *     <li>close: closes the given writer;</li>
     *     <li>flush: flushes the given writer;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param writer the given writer
     * @param limit  the specified limit, must {@code >= 0}
     * @return the given writer as a new {@link Writer} of which writeable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public static @Nonnull Writer limitedWriter(@Nonnull Writer writer, long limit) throws IllegalArgumentException {
        return IOImpls.writer(writer, limit);
    }

    /**
     * Returns a singleton {@link Writer} which supports writing infinitely data but immediately discards them.
     *
     * @return a singleton {@link Writer} which supports writing infinitely data but immediately discards them.
     */
    public static @Nonnull Writer nullWriter() {
        return IOImpls.nullWriter();
    }
}
