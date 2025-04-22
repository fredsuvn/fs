package xyz.sunqian.common.base.chars;

import xyz.sunqian.annotations.Nullable;

import java.nio.CharBuffer;

/**
 * This interface represents an encoder, which is a type of intermediate operation for {@link CharProcessor}.
 *
 * @author sunqian
 */
public interface CharEncoder {

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
    CharBuffer encode(CharBuffer data, boolean end) throws Exception;

    /**
     * Returns a wrapper {@link CharEncoder} that wraps the given encoder to encode data in fixed-size blocks.
     * <p>
     * The wrapper splits the original data into blocks of the specified fixed size by {@link CharBuffer#slice()}, and
     * each block will be passed to the given encoder sequentially. The remainder data, which is insufficient to form a
     * full block, will be buffered until enough data is received. The content of the block is shared with the
     * sub-content of the original data if, and only if, it is sliced by {@link CharBuffer#slice()}. If a block is
     * formed by concatenating multiple original data pieces, its content is not shared.
     * <p>
     * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may be
     * less than the specified fixed size.
     *
     * @param size    the specified fixed size
     * @param encoder the given encoder
     * @return a wrapper {@link CharEncoder} that wraps the given encoder to encode data in fixed-size blocks
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    static CharEncoder withFixedSize(int size, CharEncoder encoder) throws IllegalArgumentException {
        return new CharProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a wrapper {@link CharEncoder} that wraps the given encoder to encode data in rounding down blocks.
     * <p>
     * The wrapper rounds down the size of the original data to the largest multiple ({@code >= 1}) of the specified
     * size that does not exceed it, and splits the original data into the block of the rounded size by
     * {@link CharBuffer#slice()}. The block will be passed to the given encoder. The remainder data, of which size is
     * less than one multiple of the specified size, will be buffered until enough data is received. The content of the
     * block is shared with the sub-content of the original data if, and only if, it is sliced by
     * {@link CharBuffer#slice()}. If a block is formed by concatenating multiple original data pieces, its content is
     * not shared.
     * <p>
     * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may be
     * less than one multiple of the specified size.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a wrapper {@link CharEncoder} that wraps the given encoder to encode data in rounding down blocks
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    static CharEncoder withRounding(int size, CharEncoder encoder) throws IllegalArgumentException {
        return new CharProcessorImpl.RoundingEncoder(encoder, size);
    }

    /**
     * Returns a wrapper {@link CharEncoder} that wraps the given encoder to support buffering unconsumed data.
     * <p>
     * When the wrapper is invoked, if no buffered data exists, the original data is directly passed to the given
     * encoder; if buffered data exists, a new buffer concatenating the buffered data followed by the original data is
     * passed to the given. After the execution of the given encoder, any unconsumed data remaining in passed buffer
     * will be buffered.
     * <p>
     * Specially, in the last invocation (when {@code end == true}) of the wrapper, no data buffered.
     *
     * @param encoder the given encoder
     * @return a wrapper {@link CharEncoder} that wraps the given encoder to support buffering unconsumed data
     */
    static CharEncoder withBuffering(CharEncoder encoder) {
        return new CharProcessorImpl.BufferingEncoder(encoder);
    }

    /**
     * Returns an empty {@link CharEncoder} which does nothing.
     *
     * @return an empty {@link CharEncoder} which does nothing
     */
    static CharEncoder emptyEncoder() {
        return CharProcessorImpl.EmptyEncoder.SINGLETON;
    }
}
