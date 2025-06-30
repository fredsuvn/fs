package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.Reader;
import java.nio.CharBuffer;

/**
 * This interface represents the data segment reader to read char data as {@link CharSegment} from the data source,
 * which may be a char sequence or a stream.
 *
 * @author sunqian
 */
public interface CharReader {

    /**
     * Wraps the given reader as a new {@link CharReader}.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the reader;</li>
     *     <li>close: closes the reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given reader
     * @return the given reader as a new {@link CharReader}
     */
    static @Nonnull CharReader from(@Nonnull Reader src) {
        return from(src, IOKit.bufferSize());
    }

    /**
     * Wraps the given reader as a new {@link CharReader} with the given buffer size.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the reader;</li>
     *     <li>close: closes the reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src        the given reader
     * @param bufferSize the given buffer size, must {@code > 0}
     * @return the given reader as a new {@link CharReader}
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static @Nonnull CharReader from(@Nonnull Reader src, int bufferSize) throws IllegalArgumentException {
        return CharReaderImpl.of(src, bufferSize);
    }

    /**
     * Wraps the given array as a new {@link CharReader}.
     * <p>
     * The content of the segment returned from the {@link CharReader#read(int)} is shared with the array. Any changes
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
     * @return the given array as a new {@link CharReader}
     */
    static @Nonnull CharReader from(char @Nonnull [] src) {
        return from(src, 0, src.length);
    }

    /**
     * Wraps the given array, starting at the specified offset and up to the specified length, as a new
     * {@link CharReader}.
     * <p>
     * The content of the segment returned from the {@link CharReader#read(int)} is shared with the array. Any changes
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
     * @return the given array as a new {@link CharReader}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull CharReader from(char @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        return CharReaderImpl.of(src, off, len);
    }

    /**
     * Wraps the given char sequence as a new {@link CharReader}.
     * <p>
     * The content of the segment returned from the {@link CharReader#read(int)} is shared with the array. Any changes
     * in the array will be reflected to the segment's content, but not vice versa.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the char sequence
     * @return the char sequence as a new {@link CharReader}
     */
    static @Nonnull CharReader from(@Nonnull CharSequence src) {
        return from(src, 0, src.length());
    }

    /**
     * Wraps the given char sequence, starting at the specified start index inclusive and end at the specified end index
     * exclusive, as a new {@link CharReader}.
     * <p>
     * The content of the segment returned from the {@link CharReader#read(int)} is shared with the array. Any changes
     * in the array will be reflected to the segment's content, but not vice versa.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src   the given char sequence
     * @param start the specified start index inclusive
     * @param end   the specified end index exclusive
     * @return the given char sequence as a new {@link CharReader}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull CharReader from(@Nonnull CharSequence src, int start, int end) throws IndexOutOfBoundsException {
        return CharReaderImpl.of(src, start, end);
    }

    /**
     * Wraps the given buffer as a new {@link CharReader}.
     * <p>
     * The content of the segment returned from the {@link CharReader#read(int)} is shared with the array. Any changes
     * to the segment's content will be reflected in the array, and vice versa.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: supported;</li>
     *     <li>close: invoking has no effect;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @param src the given buffer
     * @return the given buffer as a new {@link CharReader}
     */
    static @Nonnull CharReader from(@Nonnull CharBuffer src) {
        return CharReaderImpl.of(src);
    }

    /**
     * Reads and returns the next data segment of the specified size from the data source. This method reads
     * continuously until reaches the specified read size or reaches the end of the data source. It never returns
     * {@code null}, but can return an empty segment. If the specified size is {@code 0}, returns an empty segment
     * immediately without reading.
     * <p>
     * The content of the returned segment may be shared with the data source, depends on the implementation, such as
     * the instances obtained from the {@link #from(char[])}, {@link #from(char[], int, int)},
     * {@link #from(CharSequence)}, {@link #from(CharSequence, int, int)} and {@link #from(CharBuffer)}.
     *
     * @param len the specified read size, must {@code >= 0}
     * @return the next data segment
     * @throws IllegalArgumentException if the specified read size is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    @Nonnull
    CharSegment read(int len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Skips the data of the specified size and returns the actual skipped size. This method skips continuously until
     * reaches the specified size to skip or reaches the end of the data source. If the specified size is {@code 0},
     * this method returns {@code 0} immediately without skipping.
     *
     * @param len the specified size to skip, must {@code >= 0}
     * @return the actual skipped size
     * @throws IllegalArgumentException if the specified size to skip is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    long skip(long len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads all data into the specified output appender, until the read number reaches the specified length or reaches
     * the end of this source, returns the actual number of chars read to. If the end of this source has already been
     * reached, returns {@code -1}.
     *
     * @param dst the specified output appender
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    long readTo(@Nonnull Appendable dst) throws IORuntimeException;

    /**
     * Reads the data of the specified length into the specified output appender, until the read number reaches the
     * specified length or reaches the end of this source, returns the actual number of chars read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of this source has already
     * been reached, returns {@code -1}.
     *
     * @param dst the specified output appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads the data into the specified array, until the read number reaches the array's length or reaches the end of
     * this source, returns the actual number of chars read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of this source has already been
     * reached, returns {@code -1}.
     *
     * @param dst the specified array
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    int readTo(char @Nonnull [] dst) throws IORuntimeException;

    /**
     * Reads the data into the specified array (starting at the specified offset and up to the specified length), until
     * the read number reaches the specified length or reaches the end of this source, returns the actual number of
     * chars read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of this source has already
     * been reached, returns {@code -1}.
     *
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException;

    /**
     * Reads the data into the specified buffer, until the read number reaches the buffer's remaining or reaches the end
     * of this source, returns the actual number of chars read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of this source has already
     * been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the specified buffer
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    int readTo(@Nonnull CharBuffer dst) throws IORuntimeException;

    /**
     * Reads the data of the specified length into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of this source, returns the actual number of chars read to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of this
     * source has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException;

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
     * Wraps this reader as a new {@link CharReader} of which readable number is limited to the specified limit. The
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
     * @return this reader as a new {@link CharReader} of which readable number is limited to the specified limit
     * @throws IllegalArgumentException if the limit argument is negative
     */
    default CharReader limit(long limit) throws IllegalArgumentException {
        return CharReaderImpl.limit(this, limit);
    }

    /**
     * Wraps this reader as a new {@link Reader} of which content and status are shared with each other.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on this reader;</li>
     *     <li>close: closes this reader;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     *
     * @return a new {@link Reader} of which content and status are shared with each other
     */
    Reader asReader();
}
