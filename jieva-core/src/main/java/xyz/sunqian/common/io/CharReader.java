package xyz.sunqian.common.io;

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
     * Returns a new {@link CharReader} with the given data source.
     * <p>
     * For the returned {@link CharReader}, the mark/reset operations are depends on and shared with the source.
     *
     * @param source the given data source
     * @return a new {@link CharReader} with the given data source
     */
    static CharReader from(Reader source) {
        return ReaderBack.of(source);
    }

    /**
     * Returns a new {@link CharReader} with the given data source.
     * <p>
     * The content of the buffer returned from the {@link CharReader} is shared with the content of the given data
     * source. Changes to the buffer's content will be visible in the given data source, and vice versa.
     * <p>
     * For the returned {@link CharReader}, the mark/reset operations are supported, and the close method has no
     * effect.
     *
     * @param source the given data source
     * @return a new {@link CharReader} with the given data source
     */
    static CharReader from(char[] source) {
        return from(source, 0, source.length);
    }

    /**
     * Returns a new {@link CharReader} with the given data source, starting at the specified offset and up to the
     * specified length.
     * <p>
     * The content of the buffer returned from the {@link CharReader} is shared with the content of the given data
     * source. Changes to the buffer's content will be visible in the given data source, and vice versa.
     * <p>
     * For the returned {@link CharReader}, the mark/reset operations are supported, and the close method has no
     * effect.
     *
     * @param source the given data source
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link CharReader} with the given data source
     * @throws IndexOutOfBoundsException if the specified offset or length is out of bounds
     */
    static CharReader from(char[] source, int offset, int length) throws IndexOutOfBoundsException {
        return ReaderBack.of(source, offset, length);
    }

    /**
     * Returns a new {@link CharReader} with the given data source.
     * <p>
     * The content of the buffer returned from the {@link CharReader} is shared with the content of the given data
     * source. Changes to the buffer's content will be visible in the given data source, and vice versa.
     * <p>
     * For the returned {@link CharReader}, the mark/reset operations are supported, and the close method has no
     * effect.
     *
     * @param source the given data source
     * @return a new {@link CharReader} with the given data source
     */
    static CharReader from(CharSequence source) {
        return from(source, 0, source.length());
    }

    /**
     * Returns a new {@link CharReader} with the given data source, starting at the specified start position inclusive
     * and end at the specified end position exclusive.
     * <p>
     * The content of the buffer returned from the {@link CharReader} is shared with the content of the given data
     * source. Changes to the buffer's content will be visible in the given data source, and vice versa.
     * <p>
     * For the returned {@link CharReader}, the mark/reset operations are supported, and the close method has no
     * effect.
     *
     * @param source the given data source
     * @param start  the specified start position inclusive
     * @param end    the specified end position exclusive
     * @return a new {@link CharReader} with the given data source
     * @throws IndexOutOfBoundsException if the specified start or end position is out of bounds
     */
    static CharReader from(CharSequence source, int start, int end) throws IndexOutOfBoundsException {
        return ReaderBack.of(source, start, end);
    }

    /**
     * Returns a new {@link CharReader} with the given data source.
     * <p>
     * The content of the buffer returned from the {@link CharReader} is shared with the content of the given data
     * source. Changes to the buffer's content will be visible in the given data source, and vice versa.
     * <p>
     * For the returned {@link CharReader}, the mark/reset operations are supported by and shared with the source, and
     * the close method has no effect.
     *
     * @param source the given data source
     * @return a new {@link CharReader} with the given data source
     */
    static CharReader from(CharBuffer source) {
        return ReaderBack.of(source);
    }

    /**
     * Reads and returns the next data segment with the specified size from the data source. This method reads
     * continuously until the specified number of chars is read or the end of the data source is reached. And it never
     * returns null, but can return an empty segment. It is equivalent to:
     * <pre>{@code
     *     return read(size, false);
     * }</pre>
     *
     * @param size the specified size
     * @return the next data segment
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     * @see #read(int, boolean)
     */
    default CharSegment read(int size) throws IllegalArgumentException, IORuntimeException {
        return read(size, false);
    }

    /**
     * Reads and returns the next data segment with the specified size from the data source. This method reads
     * continuously until the specified number of chars is read or the end of the data source is reached. And it never
     * returns null, but can return an empty segment.
     * <p>
     * The {@code endOnZeroRead} specifies whether a zero-char read, which could happen in NIO, should be treated as a
     * signal indicates the end of the data source has been reached.
     *
     * @param size          the specified size
     * @param endOnZeroRead specifies whether a zero-char read should be treated as a signal indicates the end of the
     *                      data source has been reached
     * @return the next data segment
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException;

    /**
     * Skips the data of the specified size and returns the actual skipped size. This method skips continuously until
     * the specified number of chars is skipped or the end of the data source is reached. It is equivalent to:
     * <pre>{@code
     *     return skip(size, false);
     * }</pre>
     *
     * @param size the specified size
     * @return the actual skipped size
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     * @see #skip(long, boolean)
     */
    default long skip(long size) throws IllegalArgumentException, IORuntimeException {
        return skip(size, false);
    }

    /**
     * Skips the data of the specified size and returns the actual skipped size. This method skips continuously until
     * the specified number of chars is skipped or the end of the data source is reached.
     * <p>
     * The {@code endOnZeroRead} specifies whether a zero-char read, which could happen in NIO, should be treated as a
     * signal indicates the end of the data source has been reached.
     *
     * @param size          the specified size
     * @param endOnZeroRead specifies whether a zero-char read should be treated as a signal indicates the end of the
     *                      data source has been reached
     * @return the actual skipped size
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException;

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
     * Returns a new {@link CharReader} backed by this instance, with read operations limited to the specified maximum
     * number of chars. The mark/reset operations are supported by and shared with this instance.
     *
     * @param readLimit the specified maximum number of chars to read
     * @return a new {@link CharReader} backed by this, with read operations limited to the specified maximum number of
     * chars
     * @throws IllegalArgumentException if the specified read limit is negative
     */
    default CharReader withReadLimit(long readLimit) throws IllegalArgumentException {
        return ReaderBack.of(this, readLimit);
    }
}
