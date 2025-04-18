package xyz.sunqian.common.io;

/**
 * This interface represents the data segment reader to read byte data as {@link ByteSegment} from the data source,
 * which may be a byte sequence or a stream.
 *
 * @author sunqian
 */
public interface ByteReader {

    /**
     * Reads and returns the next data segment with the specified size from the data source. This method never return
     * null, but can return an empty segment. This method is equivalent to:
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
    default ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException {
        return read(size, false);
    }

    /**
     * Reads and returns the next data segment with the specified size from the data source. This method never return
     * null, but can return an empty segment.
     * <p>
     * The {@code endOnZeroRead} specifies whether a zero-byte read, which could happen in NIO, should be treated as a
     * signal indicates the end of the data source has been reached.
     *
     * @param size          the specified size
     * @param endOnZeroRead specifies whether a zero-byte read should be treated as a signal indicates the end of the
     *                      data source has been reached
     * @return the next data segment
     * @throws IllegalArgumentException if the specified size is negative
     * @throws IORuntimeException       if an I/O error occurs
     */
    ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException;

    /**
     * Returns a new {@link ByteReader} backed by this instance, with read operations limited to the specified maximum
     * number of bytes.
     *
     * @param readLimit the specified maximum number of bytes to read
     * @return a new {@link ByteReader} backed by this, with read operations limited to the specified maximum number of
     * bytes
     * @throws IllegalArgumentException if the specified read limit is negative
     */
    default ByteReader withReadLimit(long readLimit) throws IllegalArgumentException {
        return ReaderBack.of(this, readLimit);
    }
}
