package xyz.sunqian.common.io;

import java.nio.CharBuffer;

/**
 * This interface represents a segment of data in the data source, which may be a char sequence or a stream. It includes
 * the {@link #data()} which is the content of this segment, and the {@link #end()} which indicates whether this segment
 * is the end of the data source.
 *
 * @author sunqian
 */
public interface CharSegment {

    /**
     * Returns the data content of this segment. This method never return null, but can return an empty buffer.
     *
     * @return the data content of this segment
     */
    CharBuffer data();

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
    CharSegment clone();
}
