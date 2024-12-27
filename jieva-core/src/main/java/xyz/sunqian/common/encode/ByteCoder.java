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
     * Returns output size in bytes after encoding/decoding for the specified input size. If the exact output size
     * cannot be determined, returns an estimated maximum value that can accommodate all output. Or {@code -1} if it
     * cannot be estimated.
     *
     * @param inputSize specified input size
     * @return output size in bytes for the specified input size
     * @throws EncodingException for encoding error
     * @throws DecodingException for encoding error
     */
    int getOutputSize(int inputSize) throws EncodingException, DecodingException;

    /**
     * Returns the block size in bytes for current encoding/decoding logic. In an encoding/decoding process, data is
     * sometimes processed in blocks, this method returns the block size. If the encoding/decoding doesn't require
     * processing in blocks, or if the block size cannot be estimated, return {@code -1}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a new {@link BytesProcessor.Encoder} which encapsulates current encoding/decoding logic. Note
     * {@link BytesProcessor.Encoder} may require specified block size.
     *
     * @return a {@link BytesProcessor.Encoder} encapsulates current encoding/decoding logic
     * @see BytesProcessor
     * @see BytesProcessor.Encoder
     */
    BytesProcessor.Encoder streamEncoder();
}
