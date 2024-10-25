package xyz.fslabo.common.codec;

import xyz.fslabo.common.io.ByteStream;

import java.nio.ByteBuffer;

/**
 * This is a generic interface for decoding. It extends {@link xyz.fslabo.common.io.ByteStream.Encoder}, and can be used
 * in conjunction with {@link ByteStream}.
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
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and an {@link CodecException} will
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
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and an {@link CodecException} will
     * be thrown.
     *
     * @param data given data
     * @param dest specified dest
     * @return the number of bytes written
     * @throws CodecException if any codec problem occurs
     */
    int decode(ByteBuffer data, ByteBuffer dest) throws CodecException;

    /**
     * Returns output size in bytes after decoding the specified input size. If the exact output size cannot be
     * determined, provide an estimated maximum value that can accommodate the output.
     *
     * @param inputSize specified input size
     * @return output size in bytes after decoding specified input size
     */
    int getOutputSize(int inputSize);
}
