package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;

/**
 * Super interface for encoding and decoding.
 *
 * @author sunqian
 * @see ByteEncoder
 * @see ByteDecoder
 */
public interface ByteCoder {

    /**
     * Returns output size in bytes for the specified input size. If the exact output size cannot be determined, returns
     * an estimated maximum value that can accommodate the output. Or {@code -1} if it cannot be estimated.
     *
     * @param inputSize specified input size
     * @return output size in bytes for the specified input size
     * @throws CodingException if the input size is illegal
     */
    int getOutputSize(int inputSize) throws CodingException;

    /**
     * Returns the block size in bytes for current coding logic. In a coding process, data is sometimes processed in
     * blocks, this method returns the recommended or minimal size of those blocks (determined by implementations). If
     * the block size cannot be determined, it returns {@code -1}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Encapsulates current coding logic into a {@link ByteStream.Encoder} and returns. Note {@link ByteStream.Encoder}
     * may require appropriate {@link ByteStream#blockSize(int)}.
     *
     * @return a {@link ByteStream.Encoder} with current coding logic
     * @see ByteStream
     * @see ByteStream.Encoder
     */
    ByteStream.Encoder streamEncoder();
}
