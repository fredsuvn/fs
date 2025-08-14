package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.nio.ByteBuffer;

/**
 * This interface defines a method for processing data from a source array and writing the result into a destination. It
 * is typically used to simplify the operation of processing between {@link ByteBuffer}s: there is no need to worry
 * about whether the {@link ByteBuffer} has an underlying array, some methods will automatically process it, such as
 * {@link BufferKit#process(ByteBuffer, ByteBuffer, ByteArrayOperator)}.
 *
 * @author sunqian
 */
public interface ByteArrayOperator {

    /**
     * Processes the specified length of data from the source array starting at the specified offset, and writes the
     * result into the destination array starting at the specified offset. Returns the number of bytes written.
     * <p>
     * Note make sure that the bounds arguments are valid and the destination has enough space to write.
     *
     * @param src    the source array
     * @param srcOff the specified offset of the source array
     * @param dst    the destination array
     * @param dstOff the specified offset of the destination array
     * @param len    the specified length
     * @return the actual number of bytes written
     * @throws IORuntimeException if any error occurs
     */
    int process(
        byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
    ) throws IORuntimeException;
}
