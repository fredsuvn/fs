package xyz.fslabo.common.codec;

import xyz.fslabo.common.io.ByteStream;

import java.nio.ByteBuffer;

/**
 * This is a generic interface for encoding. It extends {@link xyz.fslabo.common.io.ByteStream.Encoder}, and can be used
 * in conjunction with {@link ByteStream}.
 *
 * @author sunqian
 */
public interface Encoder extends ByteStream.Encoder {

    /**
     * Encodes given source.
     *
     * @param source given source
     * @return result of encoding
     * @throws CodecException if any codec problem occurs
     */
    byte[] encode(byte[] source) throws CodecException;

    /**
     * Encodes given source. The returned buffer's position will be zero and its limit will be the number of encoding
     * bytes.
     *
     * @param source given source
     * @return result of encoding
     * @throws CodecException if any codec problem occurs
     */
    ByteBuffer encode(ByteBuffer source) throws CodecException;

    /**
     * Encodes given source into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and an {@link CodecException} will
     * be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws CodecException if any codec problem occurs
     */
    int encode(byte[] source, byte[] dest) throws CodecException;

    /**
     * Encodes given source into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and an {@link CodecException} will
     * be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws CodecException if any codec problem occurs
     */
    int encode(ByteBuffer source, ByteBuffer dest) throws CodecException;

    /**
     * Returns output size in bytes after encoding the specified input size. If the exact output size cannot be
     * determined, provide an estimated maximum value that can accommodate the output.
     *
     * @param inputSize specified input size
     * @return output size in bytes after encoding specified input size
     */
    int getOutputSize(int inputSize);
}
