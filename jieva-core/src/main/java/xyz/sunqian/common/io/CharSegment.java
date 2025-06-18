package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

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
     * Returns a new {@link CharSegment} with the given data and end flag.
     *
     * @param data the given data
     * @param end  the given end flag
     * @return a new {@link CharSegment} with the given data and end flag
     */
    static @Nonnull CharSegment of(@Nonnull CharBuffer data, boolean end) {
        return new CharReaderImpl.CharSegmentImpl(data, end);
    }

    /**
     * Returns an empty {@link CharSegment} with the given end flag. This method returns the same instance for each
     * flag.
     *
     * @param end the given end flag
     * @return an empty {@link CharSegment} with the given end flag
     */
    static @Nonnull CharSegment empty(boolean end) {
        return CharReaderImpl.CharSegmentImpl.empty(end);
    }

    /**
     * Returns the data content of this segment. This method never return null, but can return an empty buffer.
     *
     * @return the data content of this segment
     */
    @Nonnull
    CharBuffer data();

    /**
     * Returns whether this segment is the end of the data source.
     *
     * @return whether this segment is the end of the data source
     */
    boolean end();

    /**
     * Returns the remaining data as a new char array from the {@link #data()}.
     *
     * @return the remaining data as a new char array from the {@link #data()}
     */
    default char @Nonnull [] toCharArray() {
        return JieBuffer.read(data());
    }

    /**
     * Returns a clone of this segment. The {@link #data()} of the returned clone is an independent copy, not be shared
     * with the {@link #data()} of this segment.
     *
     * @return a clone of this segment
     */
    @Nonnull
    CharSegment clone();
}
