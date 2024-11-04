package xyz.fslabo.common.encode;

import xyz.fslabo.common.codec.CodecException;
import xyz.fslabo.common.io.ByteStream;

import java.nio.ByteBuffer;

/**
 * This interface represents a decoder for decoding data bytes.
 *
 * @author sunqian
 */
public interface Decoder {

    /**
     * Decodes given data.
     *
     * @param data given data
     * @return result of decoding
     * @throws CodecException if any codec problem occurs
     */
    byte[] decode(byte[] data) throws CodecException;

    /**
     * Decodes given data. The returned buffer's position will be zero and its limit will be the number of decoding
     * bytes.
     *
     * @param data given data
     * @return result of decoding
     * @throws CodecException if any codec problem occurs
     */
    ByteBuffer decode(ByteBuffer data) throws CodecException;

    /**
     * Decodes given data into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link CodecException} will
     * be thrown.
     *
     * @param data given data
     * @param dest specified dest
     * @return the number of bytes written
     * @throws CodecException if any codec problem occurs
     */
    int decode(byte[] data, byte[] dest) throws CodecException;

    /**
     * Decodes given data into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link CodecException} will
     * be thrown.
     *
     * @param data given data
     * @param dest specified dest
     * @return the number of bytes written
     * @throws CodecException if any codec problem occurs
     */
    int decode(ByteBuffer data, ByteBuffer dest) throws CodecException;

    /**
     * Returns output size in bytes for decoding the specified input size. If the exact output size cannot be
     * determined, provide an estimated maximum value that can accommodate the output.
     *
     * @param inputSize specified input size
     * @return output size in bytes after decoding specified input size
     */
    int getOutputSize(int inputSize);

    /**
     * Returns the block size. When decoding, data is sometimes processed in blocks, this method returns the size of
     * those blocks. If the block size cannot be determined, it returns {@code 0}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link ByteStream.Encoder} with current decoding. When using this method, the
     * {@link ByteStream#blockSize(int)} needs to be set to a correct value, such as {@link #getBlockSize()} (if it does
     * not return {@code 0}).
     *
     * @return a {@link ByteStream.Encoder} with current decoding
     */
    ByteStream.Encoder toStreamEncoder();
}
