package xyz.sunqian.common.io;

import java.nio.ByteBuffer;

/**
 * This interface represents a segment of data in the data source, which may be a byte sequence or a stream. It includes
 * the {@link #data()} which is the content of this segment, and the {@link #end()} which indicates whether this segment
 * is the end of the data source.
 *
 * @author sunqian
 */
public interface ByteSegment {

    /**
     * Returns a new {@link ByteSegment} with the given data and end flag.
     *
     * @param data the given data
     * @param end  the given end flag
     * @return a new {@link ByteSegment} with the given data and end flag
     */
    static ByteSegment of(ByteBuffer data, boolean end) {
        return new ReaderImpl.ByteSegmentImpl(data, end);
    }

    /**
     * Returns an empty {@link ByteSegment} with the given end flag. This method returns the same instance for each
     * flag.
     *
     * @param end the given end flag
     * @return an empty {@link ByteSegment} with the given end flag
     */
    static ByteSegment empty(boolean end) {
        return ReaderImpl.ByteSegmentImpl.empty(end);
    }

    /**
     * Returns the data content of this segment. This method never return null, but can return an empty buffer.
     *
     * @return the data content of this segment
     */
    ByteBuffer data();

    /**
     * Returns whether this segment is the end of the data source.
     *
     * @return whether this segment is the end of the data source
     */
    boolean end();

    /**
     * Returns a clone of this segment. The {@link #data()} of the clone is an independent copy, not shared with the
     * {@link #data()} of this segment.
     *
     * @return a clone of this segment
     */
    ByteSegment clone();
}
