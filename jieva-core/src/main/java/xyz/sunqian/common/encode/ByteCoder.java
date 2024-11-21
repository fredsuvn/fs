package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteSource;

/**
 * Super interface for coding process.
 *
 * @author sunqian
 * @see ByteEncoder
 * @see ByteDecoder
 */
public interface ByteCoder {

    /**
     * Returns output size in bytes for the specified input size. If the exact output size cannot be determined, provide
     * an estimated maximum value that can accommodate the output.
     *
     * @param inputSize specified input size
     * @return output size in bytes for the specified input size
     * @throws CodingException if the input size is illegal
     */
    int getOutputSize(int inputSize) throws CodingException;

    /**
     * Returns the block size in bytes for current coding algorithm. In a coding process, data is sometimes processed in
     * blocks, this method returns the recommended or minimal size of those blocks (determined by implementations). If
     * the block size cannot be determined, it returns {@code 0}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link ByteSource.Encoder} for {@link ByteSource#encoder(ByteSource.Encoder)} and
     * {@link ByteSource#encoders(Iterable)} with current coding algorithm. When using this method, the
     * {@link ByteSource#blockSize(int)} needs to be set to a reasonable value. The reasonable value usually comes from
     * {@link #getBlockSize()} or multiples of it or may be not, determined by the implementation.
     *
     * @return a {@link ByteSource.Encoder} with current coding algorithm
     * @see ByteSource#encoder(ByteSource.Encoder)
     * @see ByteSource#encoders(Iterable)
     */
    ByteSource.Encoder streamEncoder();
}
