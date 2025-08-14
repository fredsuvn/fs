package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.nio.CharBuffer;

/**
 * This interface defines a method for processing data from a source array and writing the result into a destination. It
 * is typically used to simplify the operation of processing between {@link CharBuffer}s: there is no need to worry
 * about whether the {@link CharBuffer} has an underlying array, some methods will automatically process it, such as
 * {@link BufferKit#process(CharBuffer, CharBuffer, CharArrayOperator)}.
 *
 * @author sunqian
 */
public interface CharArrayOperator {

    /**
     * Processes the specified length of data from the source array starting at the specified offset, and writes the
     * result into the destination array starting at the specified offset. Returns the number of chars written.
     * <p>
     * Note make sure that the bounds arguments are valid and the destination has enough space to write.
     *
     * @param src    the source array
     * @param srcOff the specified offset of the source array
     * @param dst    the destination array
     * @param dstOff the specified offset of the destination array
     * @param len    the specified length
     * @return the actual number of chars written
     * @throws IORuntimeException if any error occurs
     */
    int process(
        char @Nonnull [] src, int srcOff, char @Nonnull [] dst, int dstOff, int len
    ) throws IORuntimeException;
}
