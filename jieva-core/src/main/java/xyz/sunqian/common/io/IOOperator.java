package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

/**
 * This interface provides I/O operations.
 *
 * @author sunqian
 */
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
        return bufSize == IOHelper.DEFAULT_BUFFER_SIZE ? IOOperations.DEFAULT_OPERATOR : newOperator(bufSize);
    }

    /**
     * Returns a new {@link IOOperator} instance with the given buffer size.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a new {@link IOOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static IOOperator newOperator(int bufSize) throws IllegalArgumentException {
        IOHelper.checkBufSize(bufSize);
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
    @SuppressWarnings("resource")
    default byte @Nullable [] read(@Nonnull InputStream src) throws IORuntimeException {
        try {
            int available = src.available();
            byte[] buf = new byte[available > 0 ? available : bufferSize()];
            BytesBuilder builder = null;
            int off = 0;
            while (true) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    if (builder != null) {
                        builder.append(buf, 0, off);
                        return builder.toByteArray();
                    }
                    return off == 0 ? null : Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
                if (off == buf.length) {
                    if (builder == null) {
                        int r = src.read();
                        if (r == -1) {
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
        }
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
    default byte @Nullable [] read(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOHelper.checkLen(len);
        if (len == 0) {
            return new byte[0];
        }
        try {
            byte[] buf = new byte[len];
            int off = 0;
            while (off < len) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    return off == 0 ? null : Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
            }
            return buf;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
    @SuppressWarnings("resource")
    default @Nullable ByteBuffer read(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        try {
            BytesBuilder builder = null;
            ByteBuffer dst = ByteBuffer.allocate(bufferSize());
            while (true) {
                int readSize = src.read(dst);
                if (readSize < 0) {
                    break;
                }
                if (dst.remaining() == 0) {
                    if (builder == null) {
                        int lastIndex = dst.capacity() - 1;
                        byte b = dst.get(lastIndex);
                        dst.position(lastIndex);
                        int r = src.read(dst);
                        dst.position(0);
                        if (r < 0) {
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
                return dst.position() == 0 ? null :
                    ByteBuffer.wrap(Arrays.copyOfRange(dst.array(), 0, dst.position()));
            } else {
                if (dst.position() > 0) {
                    dst.flip();
                    builder.append(dst);
                }
                return builder.toByteBuffer();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
    default @Nullable ByteBuffer read(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOHelper.checkLen(len);
        if (len == 0) {
            return ByteBuffer.allocate(0);
        }
        try {
            ByteBuffer dst = ByteBuffer.allocate(len);
            while (dst.remaining() > 0) {
                int readSize = src.read(dst);
                if (readSize < 0) {
                    return dst.position() == 0 ? null :
                        ByteBuffer.wrap(Arrays.copyOfRange(dst.array(), 0, dst.position()));
                }
            }
            dst.flip();
            return dst;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
        return IOOperations.readTo0(src, dst, bufferSize());
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
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len, bufferSize());
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
        return IOOperations.readTo0(src, dst, bufferSize());
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
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len, bufferSize());
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
        return IOOperations.readTo0(src, dst, 0, dst.length);
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
        IOHelper.checkOffLen(dst.length, off, len);
        return IOOperations.readTo0(src, dst, off, len);
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
    default int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return IOOperations.readTo0(src, dst, dst.remaining());
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
    default int readTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len);
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
        return IOOperations.readTo0(src, dst, bufferSize());
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
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len, bufferSize());
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
        return IOOperations.readTo0(src, dst, bufferSize());
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
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len, bufferSize());
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
    default int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return IOOperations.readTo0(src, dst);
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
    default int readTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len);
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
    @SuppressWarnings("resource")
    default char @Nullable [] read(@Nonnull Reader src) throws IORuntimeException {
        try {
            char[] buf = new char[bufferSize()];
            CharsBuilder builder = null;
            int off = 0;
            while (true) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    if (builder != null) {
                        builder.append(buf, 0, off);
                        return builder.toCharArray();
                    }
                    return off == 0 ? null : Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
                if (off == buf.length) {
                    if (builder == null) {
                        int r = src.read();
                        if (r == -1) {
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
        }
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
    default char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOHelper.checkLen(len);
        if (len == 0) {
            return new char[0];
        }
        try {
            char[] buf = new char[len];
            int off = 0;
            while (off < len) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    return off == 0 ? null : Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
            }
            return buf;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
        return IOOperations.readTo0(src, dst, bufferSize());
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
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len, bufferSize());
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
        return IOOperations.readTo0(src, dst, 0, dst.length);
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
        IOHelper.checkOffLen(dst.length, off, len);
        return IOOperations.readTo0(src, dst, off, len);
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
    default int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        return IOOperations.readTo0WithActualLen(src, dst, dst.remaining());
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
    default int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOHelper.checkLen(len);
        return IOOperations.readTo0(src, dst, len);
    }
}
