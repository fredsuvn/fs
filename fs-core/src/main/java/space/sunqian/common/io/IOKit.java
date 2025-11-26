package space.sunqian.common.io;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.bytes.BytesBuilder;
import space.sunqian.common.base.chars.CharsBuilder;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.io.IOChecker.ReadChecker;

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
 * Utilities for IO related, some methods are provided by a default {@link IOOperator}.
 *
 * @author sunqian
 */
public class IOKit {

    /**
     * The default IO buffer size to use in this class: {@code 1024 * 8 = 8192}. And it is also the recommended IO
     * buffer size.
     */
    public static final int BUFFER_SIZE = 1024 * 8;

    // The default io operator.
    static final @Nonnull IOOperator io = IOOperator.newOperator(BUFFER_SIZE);

    /**
     * Returns the recommended IO buffer size, typically is 1024 * 8 = 8192.
     *
     * @return the recommended IO buffer size
     */
    public static int bufferSize() {
        return BUFFER_SIZE;
    }

    /**
     * Returns the default io operator this class uses.
     *
     * @return the default io operator this class uses
     */
    public static @Nonnull IOOperator ioOperator() {
        return io;
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
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
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
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
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
     * Reads all data from the source channel into a new array, continuing until reaches the end of the source channel,
     * and returns the array.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     *
     * @param src the source channel
     * @return a new array containing the read data, or {@code null} if reaches the end of the source channel and no
     * data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nullable [] readBytes(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return io.readBytes(src);
    }

    /**
     * Reads a specified length of data from the source channel into a new array, and returns the array. If the
     * specified length is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the source channel.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, or {@code null} if reaches the end of the source channel and no
     * data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static byte @Nullable [] readBytes(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.readBytes(src, len);
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
     * Reads data from the input stream into the destination buffer, until reaches the end of the stream or buffer, and
     * returns the actual number of bytes read to.
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
     * the specified length or reaches the end of the stream or buffer, and returns the actual number of bytes read to.
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
     * Reads data from the source channel into the destination buffer, until reaches the end of the channel or buffer,
     * and returns the actual number of bytes read to.
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
     * reaches the specified length or reaches the end of the channel or buffer, and returns the actual number of bytes
     * read to.
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
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
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
     * Reads all data from the input stream as a string with {@link CharsKit#defaultCharset()}, continuing until reaches
     * the end of the stream, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a string with {@link CharsKit#defaultCharset()}, or {@code null} if reaches the end of the stream and no
     * data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(@Nonnull InputStream src) throws IORuntimeException {
        return io.string(src);
    }

    /**
     * Reads all data from the input stream as a string with the specified charset, continuing until reaches the end of
     * the stream, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src     the input stream
     * @param charset the specified charset
     * @return a string with the specified charset, or {@code null} if reaches the end of the stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(
        @Nonnull InputStream src, @Nonnull Charset charset
    ) throws IORuntimeException {
        return io.string(src, charset);
    }

    /**
     * Reads all data from the channel as a string with {@link CharsKit#defaultCharset()}, continuing until reaches the
     * end of the channel, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src the channel
     * @return a string with {@link CharsKit#defaultCharset()}, or {@code null} if reaches the end of the channel and no
     * data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return io.string(src);
    }

    /**
     * Reads all data from the channel as a string with the specified charset, continuing until reaches the end of the
     * channel, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src     the channel
     * @param charset the specified charset
     * @return a string with the specified charset, or {@code null} if reaches the end of the channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(
        @Nonnull ReadableByteChannel src, @Nonnull Charset charset
    ) throws IORuntimeException {
        return io.string(src, charset);
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
     * Reads data from the reader into the destination buffer, until reaches the end of the reader or buffer, and
     * returns the actual number of chars read to.
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
     * specified length or reaches the end of the reader or buffer, and returns the actual number of chars read to.
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
     * Reads available data from the input stream into a new array, continuing until no data is immediately available,
     * and returns the array.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the input
     * stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nullable [] available(@Nonnull InputStream src) throws IORuntimeException {
        return io.available(src);
    }

    /**
     * Reads a specified length of data from the input stream into a new array, and returns the array. If the specified
     * length is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read
     * number reaches the specified length or no data is immediately available.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code null}.
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
     *
     * @param src the input stream
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the input
     * stream and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static byte @Nullable [] available(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.available(src, len);
    }

    /**
     * Reads available data from the source channel into a new buffer, continuing until no data is immediately
     * available, and returns the buffer. The new buffer's position is {@code 0} and limit equals its capacity.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     *
     * @param src the source channel
     * @return a new buffer containing the read data, possibly empty, or {@code null} if reaches the end of the source
     * channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable ByteBuffer available(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return io.available(src);
    }

    /**
     * Reads a specified length of data from the source channel into a new buffer, and returns the buffer. If the
     * specified length is {@code 0}, returns an empty buffer without reading. Otherwise, this method keeps reading
     * until the read number reaches the specified length or no data is immediately available. The new buffer's position
     * is {@code 0} and limit equals its capacity.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     * <p>
     * Note this method will allocate a new buffer with the specified length, and the excessive length may cause out of
     * memory.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return a new buffer containing the read data, possibly empty, or {@code null} if reaches the end of the source
     * channel and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nullable ByteBuffer available(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.available(src, len);
    }

    /**
     * Reads available data from the source channel into a new array, continuing until no data is immediately available,
     * and returns the array.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     *
     * @param src the source channel
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the source
     * channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nullable [] availableBytes(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return io.availableBytes(src);
    }

    /**
     * Reads a specified length of data from the source channel into a new array, and returns the array. If the
     * specified length is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or no data is immediately available.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code null}.
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the source
     * channel and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static byte @Nullable [] availableBytes(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableBytes(src, len);
    }

    /**
     * Reads available data from the input stream into the output stream, until no data is immediately available, and
     * returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output stream
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long availableTo(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the output stream, until the read number reaches the
     * specified length or no data is immediately available, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the input stream
     * and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the input stream into the output channel, until no data is immediately available, and
     * returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the input stream and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output channel
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst
    ) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the output channel, until the read number reaches the
     * specified length or no data is immediately available, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the input stream
     * and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the input stream into the destination array, until the read number reaches the array's
     * length or no data is immediately available, and returns the actual number of bytes read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the input stream and
     * no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the destination array
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int availableTo(@Nonnull InputStream src, byte @Nonnull [] dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the destination array, starting at the specified
     * offset, until the read number reaches the specified length or no data is immediately available, and returns the
     * actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the input stream
     * and no data is read, returns {@code -1}.
     *
     * @param src the input stream
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return io.availableTo(src, dst, off, len);
    }

    /**
     * Reads available data from the input stream into the destination buffer, until reaches the end of the buffer or no
     * data is immediately available, and returns the actual number of bytes read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * input stream and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the input stream
     * @param dst the destination buffer
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int availableTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input stream into the destination buffer, until the read number reaches
     * the specified length or reaches the end of the buffer or no data is immediately available, and returns the actual
     * number of bytes read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the input stream and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the input stream
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * stream and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the input channel into the output stream, until no data is immediately available, and
     * returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the input channel and no data is read, returns {@code -1}.
     *
     * @param src the input channel
     * @param dst the output stream
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst
    ) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the input channel into the output stream, until the read number reaches the
     * specified length or no data is immediately available, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the input channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the input channel
     * @param dst the output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the input
     * channel and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the source channel into the destination channel, until no data is immediately
     * available, and returns the actual number of bytes read to.
     * <p>
     * If reaches the end of the source channel and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination channel
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the source
     * channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst
    ) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the source channel into the destination channel, until the read number
     * reaches the specified length or no data is immediately available, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the source channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the source
     * channel and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the source channel into the destination array, until the read number reaches the
     * array's length or no data is immediately available, and returns the actual number of bytes read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination array
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the source
     * channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int availableTo(@Nonnull ReadableByteChannel src, byte @Nonnull [] dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the source channel into the destination array, starting at the specified
     * offset, until the read number reaches the specified length or no data is immediately available, and returns the
     * actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the source channel
     * and no data is read, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the source
     * channel and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return io.availableTo(src, dst, off, len);
    }

    /**
     * Reads available data from the source channel into the destination buffer, until reaches the end of the buffer or
     * no data is immediately available, and returns the actual number of bytes read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * source channel and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the destination buffer
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the source
     * channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst
    ) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the source channel into the destination buffer, until the read number
     * reaches the specified length or reaches the end of the buffer or no data is immediately available, and returns
     * the actual number of bytes read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the source channel and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, possibly {@code 0}, or {@code -1} if reaches the end of the source
     * channel and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the reader, continuing until no data is immediately available, and returns the string.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char @Nullable [] available(@Nonnull Reader src) throws IORuntimeException {
        return io.available(src);
    }

    /**
     * Reads a specified length of data from the reader, and returns the string. If the specified length is {@code 0},
     * returns an empty string without reading. Otherwise, this method keeps reading until the read number reaches the
     * specified length or no data is immediately available.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the reader
     * and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static char @Nullable [] available(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.available(src, len);
    }

    /**
     * Reads available data from the reader as a string, continuing until no data is immediately available, and returns
     * the string.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a string represents the read data, possibly empty, or {@code null} if reaches the end of the reader and
     * no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String availableString(@Nonnull Reader src) throws IORuntimeException {
        return io.availableString(src);
    }

    /**
     * Reads a specified length of data from the reader as a string, and returns the string. If the specified length is
     * {@code 0}, returns an empty string without reading. Otherwise, this method keeps reading until the read number
     * reaches the specified length or no data is immediately available.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a string represents the read data, possibly empty, or {@code null} if reaches the end of the reader and
     * no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nullable String availableString(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableString(src, len);
    }

    /**
     * Reads available data from the input stream as a string with {@link CharsKit#defaultCharset()}, continuing until
     * no data is immediately available, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a string with {@link CharsKit#defaultCharset()}, or {@code null} if reaches the end of the stream and no
     * data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String availableString(@Nonnull InputStream src) throws IORuntimeException {
        return io.availableString(src);
    }

    /**
     * Reads available data from the input stream as a string with the specified charset, continuing until no data is
     * immediately available, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src     the input stream
     * @param charset the specified charset
     * @return a string with the specified charset, or {@code null} if reaches the end of the stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String availableString(
        @Nonnull InputStream src, @Nonnull Charset charset
    ) throws IORuntimeException {
        return io.availableString(src, charset);
    }

    /**
     * Reads available data from the channel as a string with {@link CharsKit#defaultCharset()}, continuing until no
     * data is immediately available, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src the channel
     * @return a string with {@link CharsKit#defaultCharset()}, or {@code null} if reaches the end of the channel and no
     * data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String availableString(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return io.availableString(src);
    }

    /**
     * Reads available data from the channel as a string with the specified charset, continuing until no data is
     * immediately available, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src     the channel
     * @param charset the specified charset
     * @return a string with the specified charset, or {@code null} if reaches the end of the channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String availableString(
        @Nonnull ReadableByteChannel src, @Nonnull Charset charset
    ) throws IORuntimeException {
        return io.availableString(src, charset);
    }

    /**
     * Reads available data from the reader into the appender, until no data is immediately available, and returns the
     * actual number of chars read to.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long availableTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the reader into the appender, until the read number reaches the specified
     * length or no data is immediately available, returns the actual number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long availableTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    /**
     * Reads available data from the reader into the destination array, until the read number reaches the array's length
     * or no data is immediately available, and returns the actual number of chars read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int availableTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the reader into the destination array, starting at the specified offset,
     * until the read number reaches the specified length or no data is immediately available, and returns the actual
     * number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return io.availableTo(src, dst, off, len);
    }

    /**
     * Reads available data from the reader into the destination buffer, until reaches the end of the buffer or no data
     * is immediately available, and returns the actual number of chars read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the destination buffer
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int availableTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        return io.availableTo(src, dst);
    }

    /**
     * Reads a specified length of data from the reader into the destination buffer, until the read number reaches the
     * specified length or reaches the end of the buffer or no data is immediately available, and returns the actual
     * number of chars read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int availableTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return io.availableTo(src, dst, len);
    }

    @SuppressWarnings("resource")
    static byte @Nullable [] read0(
        @Nonnull InputStream src, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        BytesBuilder builder = null;
        try {
            int available = src.available();
            byte[] buf = new byte[available > 0 ? available : bufSize];
            int off = 0;
            while (true) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readChecker.readEnd(readSize)) {
                    if (builder != null) {
                        builder.append(buf, 0, off);
                        return builder.toByteArray();
                    }
                    int actualCount = readChecker.actualCount(readSize, off);
                    if (actualCount < 0) {
                        return null;
                    }
                    return Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
                if (off == buf.length) {
                    if (builder == null) {
                        int r = src.read();
                        if (r < 0) {
                            return buf;
                        }
                        builder = new BytesBuilder(buf.length + 1);
                        builder.append(buf);
                        builder.append(r);
                    } else {
                        builder.append(buf);
                    }
                    off = 0;
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (builder != null) {
                builder.close();
            }
        }
    }

    static byte @Nullable [] read0(
        @Nonnull InputStream src, int len, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IllegalArgumentException, IORuntimeException {
        if (len == 0) {
            return new byte[0];
        }
        try {
            byte[] buf = new byte[len];
            int off = 0;
            while (off < len) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readChecker.readEnd(readSize)) {
                    int actualCount = readChecker.actualCount(readSize, off);
                    if (actualCount < 0) {
                        return null;
                    }
                    return Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
            }
            return buf;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    static @Nullable ByteBuffer read0(
        @Nonnull ReadableByteChannel src, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        BytesBuilder builder = null;
        try {
            ByteBuffer dst = ByteBuffer.allocate(bufSize);
            int readSize;
            while (true) {
                readSize = src.read(dst);
                if (readChecker.readEnd(readSize)) {
                    break;
                }
                if (dst.remaining() == 0) {
                    if (builder == null) {
                        int lastIndex = dst.capacity() - 1;
                        byte b = dst.get(lastIndex);
                        dst.position(lastIndex);
                        int r = src.read(dst);
                        dst.position(0);
                        if (readChecker.readEnd(r)) {
                            return dst;
                        }
                        builder = new BytesBuilder(dst.capacity() + 1);
                        dst.limit(lastIndex);
                        builder.append(dst);
                        builder.append(b);
                        dst.limit(dst.capacity());
                        builder.append(dst);
                    } else {
                        dst.flip();
                        builder.append(dst);
                    }
                    dst.flip();
                }
            }
            if (builder == null) {
                int actualCount = readChecker.actualCount(readSize, dst.position());
                if (actualCount < 0) {
                    return null;
                }
                return ByteBuffer.wrap(Arrays.copyOfRange(dst.array(), 0, dst.position()));
            } else {
                if (dst.position() > 0) {
                    dst.flip();
                    builder.append(dst);
                }
                return builder.toByteBuffer();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (builder != null) {
                builder.close();
            }
        }
    }

    static @Nullable ByteBuffer read0(
        @Nonnull ReadableByteChannel src, int len, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IllegalArgumentException, IORuntimeException {
        if (len == 0) {
            return ByteBuffer.allocate(0);
        }
        try {
            ByteBuffer dst = ByteBuffer.allocate(len);
            while (dst.remaining() > 0) {
                int readSize = src.read(dst);
                if (readChecker.readEnd(readSize)) {
                    int actualCount = readChecker.actualCount(readSize, dst.position());
                    if (actualCount < 0) {
                        return null;
                    }
                    return ByteBuffer.wrap(Arrays.copyOfRange(dst.array(), 0, dst.position()));
                }
            }
            dst.flip();
            return dst;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull OutputStream dst, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        byte[] buf = new byte[bufSize];
        return readTo0(src, dst, buf, readChecker);
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull OutputStream dst, byte @Nonnull [] buf, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            // byte[] buf = new byte[bufSize];
            long count = 0;
            while (true) {
                int readSize = src.read(buf);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                dst.write(buf, 0, readSize);
                count += readSize;
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        byte[] buf = new byte[(int) Math.min(len, bufSize)];
        return readTo0(src, dst, len, buf, readChecker);
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len, byte @Nonnull [] buf, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            // byte[] buf = new byte[(int) Math.min(len, bufSize)];
            long count = 0;
            while (count < len) {
                int readSize = src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                dst.write(buf, 0, readSize);
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        byte[] arr = new byte[bufSize];
        return readTo0(src, dst, arr, readChecker);
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, byte @Nonnull [] buf, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            // byte[] arr = new byte[bufSize];
            ByteBuffer bufWrapper = ByteBuffer.wrap(buf);
            long count = 0;
            while (true) {
                int readSize = src.read(buf);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                bufWrapper.position(0);
                bufWrapper.limit(readSize);
                BufferKit.readTo(bufWrapper, dst);
                count += readSize;
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull InputStream src,
        @Nonnull WritableByteChannel dst,
        long len,
        int bufSize,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        byte[] arr = new byte[(int) Math.min(len, bufSize)];
        return readTo0(src, dst, len, arr, readChecker);
    }

    static long readTo0(
        @Nonnull InputStream src,
        @Nonnull WritableByteChannel dst,
        long len,
        byte @Nonnull [] buf,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            // byte[] arr = new byte[(int) Math.min(len, bufSize)];
            ByteBuffer bufWrapper = ByteBuffer.wrap(buf);
            long count = 0;
            while (count < len) {
                int readSize = src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                bufWrapper.position(0);
                bufWrapper.limit(readSize);
                BufferKit.readTo(bufWrapper, dst);
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            int count = 0;
            while (count < len) {
                int readSize = src.read(dst, off + count, len - count);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        return readTo0WithActualLen(src, dst, Math.min(dst.remaining(), len), readChecker);
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull OutputStream dst,
        int bufSize,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        byte[] buf = new byte[bufSize];
        return readTo0(src, dst, buf, readChecker);
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull OutputStream dst,
        byte @Nonnull [] buf,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            ByteBuffer bufWrapper = ByteBuffer.wrap(buf);
            long count = 0;
            while (true) {
                int readSize = src.read(bufWrapper);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                bufWrapper.flip();
                BufferKit.readTo(bufWrapper, dst);
                count += readSize;
                bufWrapper.clear();
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull OutputStream dst,
        long len,
        int bufSize,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        int actualBufSize = (int) Math.min(len, bufSize);
        byte[] buf = new byte[actualBufSize];
        return readTo0(src, dst, len, buf, readChecker);
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull OutputStream dst,
        long len,
        byte @Nonnull [] buf,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            ByteBuffer bufWrapper = ByteBuffer.wrap(buf);
            long count = 0;
            while (count < len) {
                int actualSize = (int) Math.min(buf.length, len - count);
                bufWrapper.limit(actualSize);
                int readSize = src.read(bufWrapper);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                bufWrapper.flip();
                BufferKit.readTo(bufWrapper, dst);
                count += readSize;
                bufWrapper.clear();
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull WritableByteChannel dst,
        int bufSize,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        byte[] buf = new byte[bufSize];
        return readTo0(src, dst, buf, readChecker);
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull WritableByteChannel dst,
        byte @Nonnull [] buf,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            ByteBuffer bufWrapper = ByteBuffer.wrap(buf);
            long count = 0;
            while (true) {
                int readSize = src.read(bufWrapper);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                bufWrapper.flip();
                BufferKit.readTo(bufWrapper, dst);
                count += readSize;
                bufWrapper.clear();
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull WritableByteChannel dst,
        long len,
        int bufSize,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        int actualBufSize = (int) Math.min(len, bufSize);
        byte[] buf = new byte[actualBufSize];
        return readTo0(src, dst, len, buf, readChecker);
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull WritableByteChannel dst,
        long len,
        byte @Nonnull [] buf,
        @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            ByteBuffer bufWrapper = ByteBuffer.wrap(buf);
            long count = 0;
            while (count < len) {
                int actualSize = (int) Math.min(buf.length, len - count);
                bufWrapper.limit(actualSize);
                int readSize = src.read(bufWrapper);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                bufWrapper.flip();
                BufferKit.readTo(bufWrapper, dst);
                count += readSize;
                bufWrapper.clear();
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        try {
            int count = 0;
            while (dst.hasRemaining()) {
                int readSize = src.read(dst);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        int remaining = dst.remaining();
        if (remaining == 0) {
            return 0;
        }
        int pos = dst.position();
        int oldLimit = dst.limit();
        int actualLen = Math.min(remaining, len);
        dst.limit(pos + actualLen);
        int ret = readTo0(src, dst, readChecker);
        if (ret <= 0) {
            return ret;
        }
        dst.position(pos + ret);
        dst.limit(oldLimit);
        return ret;
    }

    static int readTo0WithActualLen(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int actualLen, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            if (dst.hasArray()) {
                int pos = dst.position();
                int ret = readTo0(src, dst.array(), BufferKit.arrayStartIndex(dst), actualLen, readChecker);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(pos + ret);
                return ret;
            } else {
                byte[] buf = new byte[actualLen];
                int ret = readTo0(src, buf, 0, buf.length, readChecker);
                if (ret <= 0) {
                    return ret;
                }
                dst.put(buf, 0, ret);
                return ret;
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    static char @Nullable [] read0(
        @Nonnull Reader src, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        CharsBuilder builder = null;
        try {
            char[] buf = new char[bufSize];
            int off = 0;
            while (true) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readChecker.readEnd(readSize)) {
                    if (builder != null) {
                        builder.append(buf, 0, off);
                        return builder.toCharArray();
                    }
                    int actualCount = readChecker.actualCount(readSize, off);
                    if (actualCount < 0) {
                        return null;
                    }
                    return Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
                if (off == buf.length) {
                    if (builder == null) {
                        int r = src.read();
                        if (r < 0) {
                            return buf;
                        }
                        builder = new CharsBuilder(buf.length + 1);
                        builder.append(buf);
                        builder.append(r);
                    } else {
                        builder.append(buf);
                    }
                    off = 0;
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (builder != null) {
                builder.close();
            }
        }
    }

    static char @Nullable [] read0(
        @Nonnull Reader src, int len, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IllegalArgumentException, IORuntimeException {
        if (len == 0) {
            return new char[0];
        }
        try {
            char[] buf = new char[len];
            int off = 0;
            while (off < len) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readChecker.readEnd(readSize)) {
                    int actualCount = readChecker.actualCount(readSize, off);
                    if (actualCount < 0) {
                        return null;
                    }
                    return Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
            }
            return buf;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull Reader src, @Nonnull Appendable dst, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            char[] buf = new char[bufSize];
            long count = 0;
            while (true) {
                int readSize = src.read(buf);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                IOKit.write(dst, buf, 0, readSize);
                count += readSize;
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull Reader src, @Nonnull Appendable dst, long len, int bufSize, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            char[] buf = new char[(int) Math.min(len, bufSize)];
            long count = 0;
            while (count < len) {
                int readSize = src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                IOKit.write(dst, buf, 0, readSize);
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            int count = 0;
            while (count < len) {
                int readSize = src.read(dst, off + count, len - count);
                if (readChecker.readEnd(readSize)) {
                    return readChecker.actualCount(readSize, count);
                }
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        return readTo0WithActualLen(src, dst, Math.min(dst.remaining(), len), readChecker);
    }

    static int readTo0WithActualLen(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int actualLen, @Nonnull ReadChecker readChecker
    ) throws IORuntimeException {
        try {
            if (dst.hasArray()) {
                int pos = dst.position();
                int ret = readTo0(src, dst.array(), BufferKit.arrayStartIndex(dst), actualLen, readChecker);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(pos + ret);
                return ret;
            } else {
                char[] buf = new char[actualLen];
                int ret = readTo0(src, buf, 0, buf.length, readChecker);
                if (ret <= 0) {
                    return ret;
                }
                dst.put(buf, 0, ret);
                return ret;
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
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
     * Closes the given closeable object, which is an instance of {@link Closeable} or {@link AutoCloseable}. If the
     * given object is not an instance of above interfaces, then invoking this method has no effect.
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
        return IOBack.inputStream(array);
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
        return IOBack.inputStream(array, off, len);
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
        return IOBack.inputStream(buffer);
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
        return IOBack.inputStream(raf, off);
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
        return IOBack.inputStream(reader, charset);
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
        return IOBack.inputStream(stream, limit);
    }

    /**
     * Returns a singleton empty {@link InputStream}.
     *
     * @return a singleton empty {@link InputStream}
     */
    public static @Nonnull InputStream emptyInputStream() {
        return IOBack.emptyInputStream();
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
        return IOBack.reader(array);
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
        return IOBack.reader(array, off, len);
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
        return IOBack.reader(chars);
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
        return IOBack.reader(chars, start, end);
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
        return IOBack.reader(buffer);
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
        return IOBack.reader(stream, charset);
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
        return IOBack.reader(reader, limit);
    }

    /**
     * Returns a singleton empty {@link Reader}.
     *
     * @return a singleton empty {@link Reader}
     */
    public static @Nonnull Reader emptyReader() {
        return IOBack.emptyReader();
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
        return IOBack.outputStream(array);
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
        return IOBack.outputStream(array, off, len);
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
        return IOBack.outputStream(buffer);
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
        return IOBack.outputStream(raf, off);
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
        return IOBack.outputStream(appender, charset);
    }

    /**
     * Wraps the given stream as a new {@link OutputStream} of which writable number is limited to the specified limit.
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
     * @return the given stream as a new {@link OutputStream} of which writable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public static @Nonnull OutputStream limitedOutputStream(
        @Nonnull OutputStream stream, long limit
    ) throws IllegalArgumentException {
        return IOBack.outputStream(stream, limit);
    }

    /**
     * Returns a singleton {@link OutputStream} which supports writing infinitely data but immediately discards them.
     *
     * @return a singleton {@link OutputStream} which supports writing infinitely data but immediately discards them.
     */
    public static @Nonnull OutputStream nullOutputStream() {
        return IOBack.nullOutputStream();
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
        return IOBack.writer(array);
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
        return IOBack.writer(array, off, len);
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
        return IOBack.writer(buffer);
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
        return IOBack.writer(stream, charset);
    }

    /**
     * Wraps the given writer as a new {@link Writer} of which writable number is limited to the specified limit.
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
     * @return the given writer as a new {@link Writer} of which writable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public static @Nonnull Writer limitedWriter(@Nonnull Writer writer, long limit) throws IllegalArgumentException {
        return IOBack.writer(writer, limit);
    }

    /**
     * Returns a singleton {@link Writer} which supports writing infinitely data but immediately discards them.
     *
     * @return a singleton {@link Writer} which supports writing infinitely data but immediately discards them.
     */
    public static @Nonnull Writer nullWriter() {
        return IOBack.nullWriter();
    }

    private IOKit() {
    }
}
