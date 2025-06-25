package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

/**
 * This interface provides {@code byte} related I/O operations.
 *
 * @author sunqian
 */
public interface ByteIO {

    /**
     * Returns the default {@link ByteIO} instance of which buffer size is {@link JieIO#bufferSize()}.
     *
     * @return the default {@link ByteIO} instance of which buffer size is {@link JieIO#bufferSize()}
     */
    static ByteIO defaultOperator() {
        return JieIO.defaultByteOperator();
    }

    /**
     * Returns a {@link ByteIO} instance with the given buffer size. If the buffer size equals to the default buffer
     * size, returns the default {@link ByteIO} instance, otherwise returns a new one.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a {@link ByteIO} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static ByteIO get(int bufSize) throws IllegalArgumentException {
        return bufSize == JieIO.bufferSize() ? defaultOperator() : newOperator(bufSize);
    }

    /**
     * Returns a new {@link ByteIO} instance with the given buffer size.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a new {@link ByteIO} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static ByteIO newOperator(int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return new ByteIOImpl(bufSize);
    }

    /**
     * Returns the buffer size for reading and writing.
     *
     * @return the buffer size for reading and writing
     */
    int bufferSize();

    /**
     * Reads all data from the source stream into a new array, continuing until reaches the end of the stream, and
     * returns the array. If the end of the source stream has already been reached, returns {@code null}.
     * <p>
     * Note the data in the stream cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
     * @return the array containing the data
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
     * Reads the data of the specified length from the source stream into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the stream. If the end of the source stream
     * has already been reached, returns {@code null}.
     * <p>
     * Note the length cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
     * @param len the specified read length, must {@code >= 0}
     * @return the array containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default byte @Nullable [] read(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
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
     * Reads all data from the source channel into a new buffer, continuing until reaches the end of the channel, and
     * returns the buffer. The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of
     * which offset is {@code 0}. If the end of the source channel has already been reached, returns {@code null}.
     * <p>
     * Note the data in the channel cannot exceed the maximum limit of the buffer.
     *
     * @param src the source channel
     * @return the buffer containing the data
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
     * Reads the data of the specified length from the source channel into a new buffer, and returns the buffer. If the
     * specified length {@code length = 0}, returns an empty buffer without reading. Otherwise, this method keeps
     * reading until the read number reaches the specified length or reaches the end of the channel. If the end of the
     * source channel has already been reached, returns {@code null}.
     * <p>
     * The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of which offset is
     * {@code 0}. And note the length cannot exceed the maximum limit of the buffer.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return the buffer containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default @Nullable ByteBuffer read(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
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
     * Reads all data from the source stream into the specified output stream, until the read number reaches the
     * specified length or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the end of the source stream has already been reached, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, -1, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source stream into the specified output stream, until the read
     * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return ByteIOImpl.readTo0(src, dst, len, bufferSize());
    }

    /**
     * Reads all data from the source stream into the specified output channel, until the read number reaches the
     * specified length or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the end of the source stream has already been reached, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output channel
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(@Nonnull InputStream src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, -1, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source stream into the specified output channel, until the read
     * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return ByteIOImpl.readTo0(src, dst, len, bufferSize());
    }

    /**
     * Reads the data from the source stream into the specified array, until the read number reaches the array's length
     * or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has already
     * been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull InputStream src, byte @Nonnull [] dst) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads the data from the source stream into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source stream,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has
     * already been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    default int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return ByteIOImpl.readTo0(src, dst, off, len);
    }

    /**
     * Reads the data from the source stream into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source stream into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source stream, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source stream has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int readTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return ByteIOImpl.readTo0(src, dst, len);
    }

    /**
     * Reads all data from the source channel into the specified output channel, until the read number reaches the
     * specified length or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output channel
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst
    ) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, -1, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source channel into the specified output channel, until the read
     * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
     * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return ByteIOImpl.readTo0(src, dst, len, bufferSize());
    }

    /**
     * Reads all data from the source channel into the specified output stream, until the read number reaches the
     * specified length or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output stream
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst
    ) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, -1, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source channel into the specified output stream, until the read
     * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
     * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return ByteIOImpl.readTo0(src, dst, len, bufferSize());
    }

    /**
     * Reads the data from the source channel into the specified array, until the read number reaches the array's length
     * or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
     * already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified array
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull ReadableByteChannel src, byte @Nonnull [] dst) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, ByteBuffer.wrap(dst), -1);
    }

    /**
     * Reads the data from the source channel into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source channel,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
     * already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    default int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return ByteIOImpl.readTo0(src, ByteBuffer.wrap(dst, off, len), -1);
    }

    /**
     * Reads the data from the source channel into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source channel has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return ByteIOImpl.readTo0(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source channel into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source channel, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source channel has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int readTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return ByteIOImpl.readTo0(src, dst, len);
    }
}
