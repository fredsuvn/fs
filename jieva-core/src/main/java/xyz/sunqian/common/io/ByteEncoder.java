package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * This interface represents an encoder, which is a type of intermediate operation for {@link ByteProcessor}.
 *
 * @author sunqian
 */
public interface ByteEncoder {

    /**
     * Encodes the specified input data and return the result. The specified input data will not be null (but may be
     * empty), and the return value can be null.
     * <p>
     * If it returns null, the next encoder will not be invoked and the encoding chain will be interrupted; If it
     * returns an empty buffer, the encoding chain will continue.
     *
     * @param data the specified input data
     * @param end  whether the current encoding is the last invocation
     * @return the result of encoding
     * @throws Exception thrown for any problems
     */
    @Nullable
    ByteBuffer encode(ByteBuffer data, boolean end) throws Exception;

    /**
     * Returns a wrapper {@link ByteEncoder} that wraps the given encoder to encode data in fixed-size blocks.
     * <p>
     * The wrapper splits the original data into blocks of the specified fixed size by {@link ByteBuffer#slice()}, and
     * each block will be passed to the given encoder sequentially. The remainder data, which is insufficient to form a
     * full block, will be buffered until enough data is received. The content of the block is shared with the
     * sub-content of the original data if, and only if, it is sliced by {@link ByteBuffer#slice()}. If a block is
     * formed by concatenating multiple original data pieces, its content is not shared.
     * <p>
     * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may be
     * less than the specified fixed size.
     *
     * @param size    the specified fixed size
     * @param encoder the given encoder
     * @return a wrapper {@link ByteEncoder} that wraps the given encoder to encode data in fixed-size blocks
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    static ByteEncoder withFixedSize(int size, ByteEncoder encoder) throws IllegalArgumentException {
        return new ByteProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a wrapper {@link ByteEncoder} that wraps the given encoder to encode data in rounding down blocks.
     * <p>
     * The wrapper rounds down the size of the original data to the largest multiple ({@code >= 1}) of the specified
     * size that does not exceed it, and splits the original data into the block of the rounded size by
     * {@link ByteBuffer#slice()}. The block will be passed to the given encoder. The remainder data, of which size is
     * less than one multiple of the specified size, will be buffered until enough data is received. The content of the
     * block is shared with the sub-content of the original data if, and only if, it is sliced by
     * {@link ByteBuffer#slice()}. If a block is formed by concatenating multiple original data pieces, its content is
     * not shared.
     * <p>
     * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may be
     * less than one multiple of the specified size.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a wrapper {@link ByteEncoder} that wraps the given encoder to encode data in rounding down blocks
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    static ByteEncoder withRounding(int size, ByteEncoder encoder) throws IllegalArgumentException {
        return new ByteProcessorImpl.RoundingEncoder(encoder, size);
    }

    /**
     * Returns a wrapper {@link ByteEncoder} that wraps the given encoder to support buffering unconsumed data.
     * <p>
     * When the wrapper is invoked, if no buffered data exists, the original data is directly passed to the given
     * encoder; if buffered data exists, a new buffer concatenating the buffered data followed by the original data is
     * passed to the given. After the execution of the given encoder, any unconsumed data remaining in passed buffer
     * will be buffered.
     * <p>
     * Specially, in the last invocation (when {@code end == true}) of the wrapper, no data buffered.
     *
     * @param encoder the given encoder
     * @return a wrapper {@link ByteEncoder} that wraps the given encoder to support buffering unconsumed data
     */
    static ByteEncoder withBuffering(ByteEncoder encoder) {
        return new ByteProcessorImpl.BufferingEncoder(encoder);
    }

    /**
     * Returns an empty {@link ByteEncoder} which does nothing but only returns the input data directly.
     *
     * @return an empty {@link ByteEncoder} which does nothing but only returns the input data directly
     */
    static ByteEncoder emptyEncoder() {
        return ByteProcessorImpl.EmptyEncoder.SINGLETON;
    }
}
