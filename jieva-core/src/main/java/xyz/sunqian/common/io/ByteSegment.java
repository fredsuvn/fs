package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.nio.ByteBuffer;

/**
 * This interface represents a segment of byte data in the data source. It includes the {@link #data()}, which is the
 * content of this segment, and the {@link #end()}, which indicates whether this segment is the end of the data.
 *
 * @author sunqian
 */
public interface ByteSegment {

    /**
     * Returns a new {@link ByteSegment} with the given data and end flag.
     *
     * @param data the given data
     * @param end  the end flag
     * @return a new {@link ByteSegment} with the given data and end flag
     */
    static @Nonnull ByteSegment of(@Nonnull ByteBuffer data, boolean end) {
        return ByteReaderImpl.newSeg(data, end);
    }

    /**
     * Returns an empty singleton {@link ByteSegment} with the end flag.
     *
     * @param end the end flag
     * @return an empty singleton {@link ByteSegment} with the end flag
     */
    static @Nonnull ByteSegment empty(boolean end) {
        return ByteReaderImpl.emptySeg(end);
    }

    /**
     * Returns the data content of this segment. This method never returns {@code null}, but can return an empty
     * buffer.
     *
     * @return the data content of this segment
     */
    @Nonnull
    ByteBuffer data();

    /**
     * Returns whether this segment is the end of the data source.
     *
     * @return whether this segment is the end of the data source
     */
    boolean end();

    /**
     * Returns the remaining data as a new byte array from the {@link #data()}. This method never returns {@code null},
     * but can return an empty array.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @return the remaining data as a new byte array from the {@link #data()}
     */
    default byte @Nonnull [] toByteArray() {
        byte[] ret = JieBuffer.read(data());
        return ret == null ? new byte[0] : ret;
    }

    /**
     * Returns a new array copied from the remaining content of the {@link #data()}. Position of the {@link #data()}
     * will not be changed.
     *
     * @return a new array copied from the remaining content of the {@link #data()}
     */
    default byte @Nonnull [] copyByteArray() {
        return JieBuffer.copyContent(data());
    }

    /**
     * Returns a clone of this segment. The {@link #data()} of the returned clone is an independent copy, not be shared
     * with the {@link #data()} of this segment.
     *
     * @return a clone of this segment
     */
    @Nonnull
    ByteSegment clone();
}
