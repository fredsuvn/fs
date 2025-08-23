package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.chars.CharsKit;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * This interface provides I/O operations, and it is thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
public interface IOOperator {

    /**
     * Returns a {@link IOOperator} instance with the given buffer size. If the buffer size equals to the
     * {@link IOKit#bufferSize()}, returns the default {@link IOOperator} instance, otherwise returns a new one by
     * {@link #newOperator(int)}.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a {@link IOOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static IOOperator get(int bufSize) throws IllegalArgumentException {
        IOOperator io = IOKit.io;
        return bufSize == io.bufferSize() ? io : newOperator(bufSize);
    }

    /**
     * Returns a new {@link IOOperator} instance with the given buffer size.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a new {@link IOOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static IOOperator newOperator(int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return () -> bufSize;
    }

    /**
     * Returns the buffer size for I/O operations.
     *
     * @return the buffer size for I/O operations
     */
    int bufferSize();

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
    default byte @Nullable [] read(@Nonnull InputStream src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.endChecker());
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
    default byte @Nullable [] read(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.endChecker());
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
    default @Nullable ByteBuffer read(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.endChecker());
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
    default @Nullable ByteBuffer read(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.endChecker());
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
    default byte @Nullable [] readBytes(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        ByteBuffer buf = read(src);
        return buf == null ? null : BufferKit.read(buf);
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
    default byte @Nullable [] readBytes(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        ByteBuffer buf = read(src, len);
        return buf == null ? null : BufferKit.read(buf);
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
    default long readTo(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.endChecker());
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
    default long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.endChecker());
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
    default long readTo(@Nonnull InputStream src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.endChecker());
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
    default long readTo(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.endChecker());
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
    default int readTo(@Nonnull InputStream src, byte @Nonnull [] dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.endChecker());
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
    default int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return IOKit.readTo0(src, dst, off, len, IOChecker.endChecker());
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
    default int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, dst.remaining(), IOChecker.endChecker());
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
    default int readTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.endChecker());
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
    default long readTo(@Nonnull ReadableByteChannel src, @Nonnull OutputStream dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.endChecker());
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
    default long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.endChecker());
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
    default long readTo(@Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.endChecker());
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
    default long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.endChecker());
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
    default int readTo(@Nonnull ReadableByteChannel src, byte @Nonnull [] dst) throws IORuntimeException {
        return readTo(src, ByteBuffer.wrap(dst));
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
    default int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        ByteBuffer buf = ByteBuffer.wrap(dst, off, len);
        return readTo(src, buf);
    }

    /**
     * Reads data from the source channel into the destination buffer, until reaches the end of the stream or buffer,
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
    default int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, IOChecker.endChecker());
    }

    /**
     * Reads a specified length of data from the source channel into the destination buffer, until the read number
     * reaches the specified length or reaches the end of the stream or buffer, and returns the actual number of bytes
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
    default int readTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.endChecker());
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
    default char @Nullable [] read(@Nonnull Reader src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.endChecker());
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
    default char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.endChecker());
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
    default @Nullable String string(@Nonnull Reader src) throws IORuntimeException {
        char[] chars = read(src);
        return chars == null ? null : new String(chars);
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
    default @Nullable String string(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        char[] chars = read(src, len);
        return chars == null ? null : new String(chars);
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
    default @Nullable String string(@Nonnull InputStream src) throws IORuntimeException {
        return string(src, CharsKit.defaultCharset());
    }

    /**
     * Reads all data from the input stream as a string with the specified charset, continuing until reaches the end of
     * the stream, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a string with the specified charset, or {@code null} if reaches the end of the stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String string(
        @Nonnull InputStream src, @Nonnull Charset charset
    ) throws IORuntimeException {
        byte[] bytes = read(src);
        return bytes == null ? null : new String(bytes, charset);
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
    default @Nullable String string(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return string(src, CharsKit.defaultCharset());
    }

    /**
     * Reads all data from the channel as a string with the specified charset, continuing until reaches the end of the
     * channel, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src the channel
     * @return a string with the specified charset, or {@code null} if reaches the end of the channel and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String string(
        @Nonnull ReadableByteChannel src, @Nonnull Charset charset
    ) throws IORuntimeException {
        ByteBuffer bytes = read(src);
        return bytes == null ? null : BufferKit.string(bytes, charset);
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
    default long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.endChecker());
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
    default long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.endChecker());
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
    default int readTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.endChecker());
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
    default int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return IOKit.readTo0(src, dst, off, len, IOChecker.endChecker());
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
    default int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        return IOKit.readTo0WithActualLen(src, dst, dst.remaining(), IOChecker.endChecker());
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
    default int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.endChecker());
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
    default byte @Nullable [] available(@Nonnull InputStream src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.availableChecker());
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
    default byte @Nullable [] available(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.availableChecker());
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
    default @Nullable ByteBuffer available(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.availableChecker());
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
    default @Nullable ByteBuffer available(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.availableChecker());
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
    default byte @Nullable [] availableBytes(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        ByteBuffer buf = available(src);
        return buf == null ? null : BufferKit.read(buf);
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
    default byte @Nullable [] availableBytes(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        ByteBuffer buf = available(src, len);
        return buf == null ? null : BufferKit.read(buf);
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
    default long availableTo(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(@Nonnull InputStream src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.availableChecker());
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
    default int availableTo(@Nonnull InputStream src, byte @Nonnull [] dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.availableChecker());
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
    default int availableTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return IOKit.readTo0(src, dst, off, len, IOChecker.availableChecker());
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
    default int availableTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, dst.remaining(), IOChecker.availableChecker());
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
    default int availableTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.availableChecker());
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
    default long availableTo(@Nonnull ReadableByteChannel src, @Nonnull OutputStream dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(@Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.availableChecker());
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
    default int availableTo(@Nonnull ReadableByteChannel src, byte @Nonnull [] dst) throws IORuntimeException {
        return availableTo(src, ByteBuffer.wrap(dst));
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
    default int availableTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        ByteBuffer buf = ByteBuffer.wrap(dst, off, len);
        return availableTo(src, buf);
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
    default int availableTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, IOChecker.availableChecker());
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
    default int availableTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.availableChecker());
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
    default char @Nullable [] available(@Nonnull Reader src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.availableChecker());
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
    default char @Nullable [] available(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.availableChecker());
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
    default @Nullable String availableString(@Nonnull Reader src) throws IORuntimeException {
        char[] chars = available(src);
        return chars == null ? null : new String(chars);
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
    default @Nullable String availableString(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        char[] chars = available(src, len);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads available data from the input stream as a string with {@link CharsKit#defaultCharset()}, continuing until
     * no data is immediately available, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a string with {@link CharsKit#defaultCharset()}, possibly empty, or {@code null} if reaches the end of
     * the stream and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String availableString(@Nonnull InputStream src) throws IORuntimeException {
        return availableString(src, CharsKit.defaultCharset());
    }

    /**
     * Reads available data from the input stream as a string with the specified charset, continuing until no data is
     * immediately available, and returns the string.
     * <p>
     * If reaches the end of the stream and no data is read, returns {@code null}.
     *
     * @param src the input stream
     * @return a string with the specified charset, possibly empty, or {@code null} if reaches the end of the stream and
     * no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String availableString(
        @Nonnull InputStream src, @Nonnull Charset charset
    ) throws IORuntimeException {
        byte[] bytes = available(src);
        return bytes == null ? null : new String(bytes, charset);
    }

    /**
     * Reads available data from the channel as a string with {@link CharsKit#defaultCharset()}, continuing until no
     * data is immediately available, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src the channel
     * @return a string with {@link CharsKit#defaultCharset()}, possibly empty, or {@code null} if reaches the end of
     * the channel and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String availableString(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return availableString(src, CharsKit.defaultCharset());
    }

    /**
     * Reads available data from the channel as a string with the specified charset, continuing until no data is
     * immediately available, and returns the string.
     * <p>
     * If reaches the end of the channel and no data is read, returns {@code null}.
     *
     * @param src the channel
     * @return a string with the specified charset, possibly empty, or {@code null} if reaches the end of the channel
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String availableString(
        @Nonnull ReadableByteChannel src, @Nonnull Charset charset
    ) throws IORuntimeException {
        ByteBuffer bytes = available(src);
        if (bytes == null) {
            return null;
        }
        if (!bytes.hasRemaining()) {
            return "";
        }
        return BufferKit.string(bytes, charset);
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
    default long availableTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.availableChecker());
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
    default long availableTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.availableChecker());
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
    default int availableTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.availableChecker());
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
    default int availableTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return IOKit.readTo0(src, dst, off, len, IOChecker.availableChecker());
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
    default int availableTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        return IOKit.readTo0WithActualLen(src, dst, dst.remaining(), IOChecker.availableChecker());
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
    default int availableTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.availableChecker());
    }
}
