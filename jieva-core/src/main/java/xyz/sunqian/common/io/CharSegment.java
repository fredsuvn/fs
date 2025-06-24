package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.nio.CharBuffer;

/**
 * This interface represents a segment of char data in the data source. It includes the {@link #data()}, which is the
 * content of this segment, and the {@link #end()}, which indicates whether this segment is the end of the data.
 *
 * @author sunqian
 */
public interface CharSegment {

    /**
     * Returns a new {@link CharSegment} with the given data and end flag.
     *
     * @param data the given data
     * @param end  the end flag
     * @return a new {@link CharSegment} with the given data and end flag
     */
    static @Nonnull CharSegment of(@Nonnull CharBuffer data, boolean end) {
        return new CharReaderImpl.CharSegmentImpl(data, end);
    }

    /**
     * Returns an empty singleton {@link CharSegment} with the end flag.
     *
     * @param end the end flag
     * @return an empty singleton {@link CharSegment} with the end flag
     */
    static @Nonnull CharSegment empty(boolean end) {
        return CharReaderImpl.CharSegmentImpl.empty(end);
    }

    /**
     * Returns the data content of this segment. This method never returns {@code null}, but can return an empty
     * buffer.
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
     * Returns the remaining data as a new char array from the {@link #data()}. This method never returns {@code null},
     * but can return an empty array.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @return the remaining data as a new char array from the {@link #data()}
     */
    default char @Nonnull [] toCharArray() {
        char[] ret = JieBuffer.read(data());
        return ret == null ? new char[0] : ret;
    }

    /**
     * Returns a new array copied from the remaining content of the {@link #data()}. Position of the {@link #data()}
     * will not be changed.
     *
     * @return a new array copied from the remaining content of the {@link #data()}
     */
    default char @Nonnull [] copyCharArray() {
        return JieBuffer.copyContent(data());
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
