package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This interface represents the byte data reader to read byte data as {@link ByteSegment} from the data source, which
 * may be a byte sequence or a stream.
 *
 * @author sunqian
 */
public interface ByteReader extends RuntimeCloseable {

    /**
     * Wraps the given stream as a new {@link ByteReader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the stream;</li>
     *     <li>close: closes the stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given stream
     * @return the given stream as a new {@link ByteReader}
     */
    static @Nonnull ByteReader from(@Nonnull InputStream src) {
        return from(src, IOKit.bufferSize());
    }

    /**
     * Wraps the given stream as a new {@link ByteReader} with the given buffer size.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the stream;</li>
     *     <li>close: closes the stream;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src     the given stream
     * @param bufSize the given buffer size, must {@code > 0}
     * @return the given stream as a new {@link ByteReader}
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static @Nonnull ByteReader from(@Nonnull InputStream src, int bufSize) throws IllegalArgumentException {
        return ByteReaderImpl.of(src, bufSize);
    }

    /**
     * Wraps the given channel as a new {@link ByteReader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: unsupported;</li>
     *     <li>close: closes the channel;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given channel
     * @return the given channel as a new {@link ByteReader}
     */
    static @Nonnull ByteReader from(@Nonnull ReadableByteChannel src) {
        return from(src, IOKit.bufferSize());
    }

    /**
     * Wraps the given channel as a new {@link ByteReader} with the given buffer size.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: unsupported;</li>
     *     <li>close: closes the channel;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src     the given channel
     * @param bufSize the given buffer size, must {@code > 0}
     * @return the given channel as a new {@link ByteReader}
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static @Nonnull ByteReader from(@Nonnull ReadableByteChannel src, int bufSize) throws IllegalArgumentException {
        return ByteReaderImpl.of(src, bufSize);
    }

    /**
     * Wraps the given array as a new {@link ByteReader}.
     * <p>
     * The content of the segment returned from the {@link ByteReader#read(int)} is shared with the array. Any changes
     * to the segment's content will be reflected in the array, and vice versa.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given array
     * @return the given array as a new {@link ByteReader}
     */
    static @Nonnull ByteReader from(byte @Nonnull [] src) {
        return from(src, 0, src.length);
    }

    /**
     * Wraps a specified length of data from the given array, starting at the specified offset, as a new
     * {@link ByteReader}.
     * <p>
     * The content of the segment returned from the {@link ByteReader#read(int)} is shared with the array. Any changes
     * to the segment's content will be reflected in the array, and vice versa.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given array
     * @param off the specified offset
     * @param len the specified length
     * @return the given array as a new {@link ByteReader}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull ByteReader from(byte @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        return ByteReaderImpl.of(src, off, len);
    }

    /**
     * Wraps the given buffer as a new {@link ByteReader}.
     * <p>
     * The content of the segment returned from the {@link ByteReader#read(int)} is shared with the array. Any changes
     * to the segment's content will be reflected in the array (if it is not readonly), and vice versa.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given buffer
     * @return the given buffer as a new {@link ByteReader}
     */
    static @Nonnull ByteReader from(@Nonnull ByteBuffer src) {
        return ByteReaderImpl.of(src);
    }

    /**
     * Reads and returns the next specified length of data segment. This method reads continuously until the read number
     * reaches the specified length or reaches the end of this reader. It never returns {@code null}, but can return an
     * empty segment.
     * <p>
     * If the specified length is {@code 0}, returns an empty segment immediately without reading.
     * <p>
     * The content of the returned segment may be shared with the data source, depends on the implementation, such as
     * the instances obtained from the {@link #from(byte[])}, {@link #from(byte[], int, int)} and
     * {@link #from(ByteBuffer)}.
     *
     * @param len the specified length to read, must {@code >= 0}
     * @return the next data segment
     * @throws IllegalArgumentException if the specified length {@code < 0}
     * @throws IORuntimeException       if an I/O error occurs
     */
    @Nonnull
    ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Skips the next specified length of data segment. This method skips continuously until the skipped number reaches
     * the specified length or reaches the end of this reader.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} immediately without skipping.
     *
     * @param len the specified length to skip, must {@code >= 0}
     * @return the actual skipped length
     * @throws IllegalArgumentException if the specified length {@code < 0}
     * @throws IORuntimeException       if an I/O error occurs
     */
    long skip(long len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads all data into the specified output stream, until reaches the end of this reader, returns the actual number
     * of bytes read to.
     * <p>
     * If reaches the end of this reader and no data is read, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param dst the specified output stream
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    long readTo(@Nonnull OutputStream dst) throws IORuntimeException;

    /**
     * Reads a specified length of data into the specified output stream, until the read number reaches the specified
     * length or reaches the end of this reader, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of this reader and no
     * data is read, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param dst the specified output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads all data into the specified output channel, until reaches the end of this reader, returns the actual number
     * of bytes read to.
     * <p>
     * If reaches the end of this reader and no data is read, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param dst the specified output channel
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException;

    /**
     * Reads a specified length of data into the specified output channel, until the read number reaches the specified
     * length or reaches the end of this reader, returns the actual number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of this reader and no
     * data is read, returns {@code -1}.
     * <p>
     * This method never invokes the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param dst the specified output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    long readTo(@Nonnull WritableByteChannel dst, long len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads data from this reader into the destination array, until the read number reaches the array's length or
     * reaches the end of this reader, and returns the actual number of bytes read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of this reader and no
     * data is read, returns {@code -1}.
     *
     * @param dst the destination array
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    int readTo(byte @Nonnull [] dst) throws IORuntimeException;

    /**
     * Reads a specified length of data from this reader into the destination array, starting at the specified offset,
     * until the read number reaches the specified length or reaches the end of this reader, and returns the actual
     * number of bytes read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of this reader and no
     * data is read, returns {@code -1}.
     *
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException;

    /**
     * Reads data from this reader into the destination buffer, until reaches the end of any buffer, and returns the
     * actual number of bytes read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of this
     * reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the destination buffer
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException;

    /**
     * Reads a specified length of data from this reader into the destination buffer, until the read number reaches the
     * specified length or reaches the end of any buffer, and returns the actual number of bytes read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of this reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read to, or {@code -1} if reaches the end of this reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Returns whether this reader supports the {@link #mark()} and {@link #reset()} methods.
     *
     * @return whether this reader supports the {@link #mark()} and {@link #reset()} methods
     */
    boolean markSupported();

    /**
     * Marks the current position in this reader. This method can be used to mark a position for later
     * {@link #reset()}.
     *
     * @throws IORuntimeException if an I/O error occurs
     */
    void mark() throws IORuntimeException;

    /**
     * Resets this reader to the last marked position by {@link #mark()}. This method can be used to re-read the data
     * from last marked position.
     *
     * @throws IORuntimeException if an I/O error occurs
     */
    void reset() throws IORuntimeException;

    /**
     * Closes this reader and the data source if the data source is closable. If this reader is already closed, this
     * method has no effect.
     *
     * @throws IORuntimeException if an I/O error occurs
     */
    void close() throws IORuntimeException;

    /**
     * Wraps this reader as a new {@link ByteReader} of which readable number is limited to the specified limit. The
     * shareability of the content of the returned segment inherits this reader.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on this reader;</li>
     *     <li>close: closes this reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param limit the specified limit, must {@code >= 0}
     * @return this reader as a new {@link ByteReader} of which readable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    default @Nonnull ByteReader limit(long limit) throws IllegalArgumentException {
        return ByteReaderImpl.limit(this, limit);
    }

    /**
     * Returns an {@link InputStream} represents this reader. Its content and status are shared with this reader.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on this reader;</li>
     *     <li>close: closes this reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @return an {@link InputStream} represents this reader. Its content and status are shared with this reader
     */
    default @Nonnull InputStream asInputStream() {
        return IOImpls.inputStream(this);
    }
}
