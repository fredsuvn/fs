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
     */
    int getOutputSize(int inputSize);

    /**
     * Returns the block size. When en/de-coding, data is sometimes processed in blocks, this method returns the
     * recommended size of those blocks. If the block size cannot be determined, it returns {@code 0}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link ByteStream.Encoder} with current en/de-coding. When using this method, the
     * {@link ByteStream#blockSize(int)} needs to be set to a correct value, such as {@link #getBlockSize()} (if it does
     * not return {@code 0}).
     *
     * @return a {@link ByteStream.Encoder} with current en/de-coding
     */
    ByteStream.Encoder toStreamEncoder();
}
