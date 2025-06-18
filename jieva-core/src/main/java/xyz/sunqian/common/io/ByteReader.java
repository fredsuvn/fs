package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * This interface represents the data segment reader to read byte data as {@link ByteSegment} from the data source,
 * which may be a byte sequence or a stream.
 *
 * @author sunqian
 */
public interface ByteReader {

    /**
     * Returns a new {@link ByteReader} with the given input stream. The mark/reset operations are depends on and shared
     * with the input stream.
     *
     * @param source the given input stream
     * @return a new {@link ByteReader} with the given input stream
     */
    static @Nonnull ByteReader from(@Nonnull InputStream source) {
        return ByteReaderImpl.of(source);
    }

    /**
     * Returns a new {@link ByteReader} with the given byte array.
     * <p>
     * The content of the segment returned from the {@link ByteReader#read(int)} is shared with the content of the given
     * byte array. Any changes to the segment's content will be reflected in the given byte array, and vice versa. The
     * mark/reset operations are supported, and the close method has no effect.
     *
     * @param source the given byte array
     * @return a new {@link ByteReader} with the given byte array
     */
    static @Nonnull ByteReader from(byte @Nonnull [] source) {
        return from(source, 0, source.length);
    }

    /**
     * Returns a new {@link ByteReader} with the given byte array, starting at the specified offset and up to the
     * specified length.
     * <p>
     * The content of the segment returned from the {@link ByteReader#read(int)} is shared with the content of the given
     * byte array. Any changes to the segment's content will be reflected in the given byte array, and vice versa. The
     * mark/reset operations are supported, and the close method has no effect.
     *
     * @param source the given byte array
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link ByteReader} with the given byte array
     * @throws IndexOutOfBoundsException if the specified offset or length is out of bounds
     */
    static @Nonnull ByteReader from(byte @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
        return ByteReaderImpl.of(source, offset, length);
    }

    /**
     * Returns a new {@link ByteReader} with the given byte buffer.
     * <p>
     * The content of the segment returned from the {@link ByteReader#read(int)} is shared with the content of the given
     * byte buffer. Any changes to the given byte buffer will be reflected in the returned segment, and vice versa if
     * the byte buffer is writeable. The mark/reset operations are supported, and the close method has no effect.
     *
     * @param source the given byte buffer
     * @return a new {@link ByteReader} with the given byte buffer
     */
    static @Nonnull ByteReader from(@Nonnull ByteBuffer source) {
        return ByteReaderImpl.of(source);
    }

    /**
     * Reads and returns the next data segment of the specified size from the data source. This method reads
     * continuously until the specified number of bytes is read or the end of the data source is reached. It never
     * returns {@code null}, but can return an empty segment. If the specified size is {@code 0}, returns an empty
     * segment immediately without reading.
     * <p>
     * The content of the returned segment may be shared with the content of the data source if, and only if, the data
     * source is a byte array or byte buffer. Any changes to those data source will be reflected in the returned
     * segment, and vice versa if the data source is the byte array or writeable byte buffer.
     *
     * @param size the specified size
     * @return the next data segment
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    @Nonnull
    ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException;

    // /**
    //  * Reads and returns the next data segment with the specified size from the data source. This method reads
    //  * continuously until the specified number of bytes is read or the end of the data source is reached. And it never
    //  * returns null, but can return an empty segment.
    //  * <p>
    //  * The {@code endOnZeroRead} specifies whether a zero-byte read, which could happen in NIO, should be treated as a
    //  * signal indicates the end of the data source has been reached.
    //  *
    //  * @param size          the specified size
    //  * @param endOnZeroRead specifies whether a zero-byte read should be treated as a signal indicates the end of the
    //  *                      data source has been reached
    //  * @return the next data segment
    //  * @throws IllegalArgumentException if the specified size is negative
    //  * @throws IORuntimeException       if an I/O error occurs
    //  */
    // @Nonnull
    // ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException;

    /**
     * Skips the data of the specified size and returns the actual skipped size. This method skips continuously until
     * the specified number of bytes is skipped or the end of the data source is reached. If the specified size is
     * {@code 0}, this method returns {@code 0} immediately without skipping.
     *
     * @param size the specified size
     * @return the actual skipped size
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    long skip(long size) throws IllegalArgumentException, IORuntimeException;

    // /**
    //  * Skips the data of the specified size and returns the actual skipped size. This method skips continuously until
    //  * the specified number of bytes is skipped or the end of the data source is reached.
    //  * <p>
    //  * The {@code endOnZeroRead} specifies whether a zero-byte read, which could happen in NIO, should be treated as a
    //  * signal indicates the end of the data source has been reached.
    //  *
    //  * @param size          the specified size
    //  * @param endOnZeroRead specifies whether a zero-byte read should be treated as a signal indicates the end of the
    //  *                      data source has been reached
    //  * @return the actual skipped size
    //  * @throws IllegalArgumentException if the specified size is negative
    //  * @throws IORuntimeException       if an I/O error occurs
    //  */
    // long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads from the data source into the specified destination until the destination is filled, returns the number of
     * actual data read or {@code -1} if the end of the source has already been reached. If the remaining space of the
     * destination is {@code 0}, this method returns {@code 0} immediately without reading.
     *
     * @param dest the specified destination
     * @return the number of actual data read or {@code -1} if the end of the source has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(byte @Nonnull [] dest) throws IORuntimeException {
        return readTo(dest, 0, dest.length);
    }

    /**
     * Reads from the data source into the specified destination (starting from the specified start index up to the
     * specified length) until the destination is filled, returns the number of actual data read or {@code -1} if the
     * end of the source has already been reached. If the specified length is {@code 0}, this method returns {@code 0}
     * immediately without reading.
     *
     * @param dest   the specified destination
     * @param offset the specified start index
     * @param length the specified length
     * @return the number of actual data read or {@code -1} if the end of the source has already been reached
     * @throws IndexOutOfBoundsException if the specified offset or length is out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    int readTo(byte @Nonnull [] dest, int offset, int length) throws IndexOutOfBoundsException, IORuntimeException;

    /**
     * Reads from the data source into the specified destination until the destination is filled, returns the number of
     * actual data read or {@code -1} if the end of the source has already been reached. The position of the destination
     * will be incremented by the actual read number. If the remaining space of the destination is {@code 0}, this
     * method returns {@code 0} immediately without reading.
     *
     * @param dest the specified destination
     * @return the number of actual data read or {@code -1} if the end of the source has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    int readTo(@Nonnull ByteBuffer dest) throws IORuntimeException;

    /**
     * Reads all data from the data source into the specified destination, returns the number of actual data read or
     * {@code -1} if the end of the source has already been reached.
     *
     * @param dest the specified destination
     * @return the number of actual data read or {@code -1} if the end of the source has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(@Nonnull OutputStream dest) throws IORuntimeException {
        return readTo(dest, -1);
    }

    /**
     * Reads the data of the specified length from the data source into the specified destination, returns the number of
     * actual data read or {@code -1} if the end of the source has already been reached. The length may less than 0,
     * means all data will be transferred. If the specified length is {@code 0}, this method returns {@code 0}
     * immediately without reading.
     *
     * @param dest   the specified destination
     * @param length the specified length, may {@code < 0}, means all data will be transferred
     * @return the number of actual data read or {@code -1} if the end of the source has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    long readTo(@Nonnull OutputStream dest, long length) throws IORuntimeException;

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

    // /**
    //  * Returns a new {@link ByteReader} backed by this instance, with read operations limited to the specified maximum
    //  * number of bytes. The mark/reset operations are supported by and shared with this instance.
    //  *
    //  * @param readLimit the specified maximum number of bytes to read
    //  * @return a new {@link ByteReader} backed by this, with read operations limited to the specified maximum number of
    //  * bytes
    //  * @throws IllegalArgumentException if the specified read limit is negative
    //  */
    // default @Nonnull ByteReader withReadLimit(long readLimit) throws IllegalArgumentException {
    //     return ByteReaderImpl.of(this, readLimit);
    // }
}
