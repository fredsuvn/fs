package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.BytesProcessor;

/**
 * Super interface for byte encoding/decoding. It has two main sub-interfaces: {@link ByteEncoder} and
 * {@link ByteDecoder}, for encoding and decoding respectively.
 *
 * @author sunqian
 * @see ByteEncoder
 * @see ByteDecoder
 */
public interface ByteCoder {

    /**
     * This method is used to estimate the output size in bytes of input data of specified size after encoding/decoding.
     * If the exact output size cannot be determined, returns a maximum value that can accommodate all output, or
     * {@code -1} if the estimating fails.
     *
     * @param inputSize specified size of input data
     * @return output size in bytes of input data of specified size after encoding/decoding
     * @throws EncodingException for encoding error
     * @throws DecodingException for encoding error
     */
    int getOutputSize(int inputSize) throws EncodingException, DecodingException;

    /**
     * If current implementation of this method requires processing in blocks, it returns the block size. Otherwise, it
     * returns {@code -1}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link BytesProcessor.Encoder} which encapsulates current encoding/decoding logic for processing within
     * {@link BytesProcessor}.
     * <p>
     * Note that different {@link BytesProcessor.Encoder} implementations may have specific requirements for the data to
     * be encoded/decoded.
     *
     * @return a {@link BytesProcessor.Encoder} encapsulates current encoding/decoding logic
     * @see BytesProcessor
     * @see BytesProcessor.Encoder
     */
    BytesProcessor.Encoder streamEncoder();
}
