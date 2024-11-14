package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;

/**
 * Super interface of {@link ByteEncoder} and {@link ByteDecoder}, provides methods for both {@code en/de}coding.
 *
 * @author sunqian
 */
public interface ByteCoder {

    /**
     * Returns output size in bytes for en/de-coding the specified input size. If the exact output size cannot be
     * determined, provide an estimated maximum value that can accommodate the output.
     *
     * @param inputSize specified input size
     * @return output size in bytes after en/de-coding specified input size
     * @throws CodingException if the input size is illegal
     */
    int getOutputSize(int inputSize) throws CodingException;

    /**
     * Returns the block size for encoding. When en/de-coding, data is sometimes processed in blocks, this method
     * returns the recommended or minimal size of those blocks (determined by implementations). If the block size cannot
     * be determined, it returns {@code 0}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link ByteStream.Encoder} with current coding algorithm. When using this method, the
     * {@link ByteStream#blockSize(int)} needs to be set to a correct value. The correct value usually comes from
     * {@link #getBlockSize()} or multiples of it, determined by the implementation. See
     * {@link ByteStream#encoder(ByteStream.Encoder)} and {@link ByteStream#encoders(Iterable)}.
     * <p>
     * {@link ByteStream} provides some wrapper encoders to accommodate encoders that do not applicable to the block
     * size, such as {@link ByteStream#roundEncoder(ByteStream.Encoder, int)},
     * {@link ByteStream#bufferedEncoder(ByteStream.Encoder)}.
     *
     * @return a {@link ByteStream.Encoder} with current en/de-coding
     * @see ByteStream#encoder(ByteStream.Encoder)
     * @see ByteStream#encoders(Iterable)
     * @see ByteStream#roundEncoder(ByteStream.Encoder, int)
     * @see ByteStream#bufferedEncoder(ByteStream.Encoder)
     */
    ByteStream.Encoder streamEncoder();
}
