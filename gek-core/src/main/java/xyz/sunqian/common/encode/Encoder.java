package xyz.sunqian.common.encode;

import xyz.sunqian.common.codec.CodecException;
import xyz.sunqian.common.io.ByteStream;

import java.nio.ByteBuffer;

/**
 * This interface represents an encoder for encoding source bytes.
 *
 * @author sunqian
 */
public interface Encoder {

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
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link CodecException} will
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
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link CodecException} will
     * be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws CodecException if any codec problem occurs
     */
    int encode(ByteBuffer source, ByteBuffer dest) throws CodecException;

    /**
     * Returns output size in bytes for encoding the specified input size. If the exact output size cannot be
     * determined, provide an estimated maximum value that can accommodate the output.
     *
     * @param inputSize specified input size
     * @return output size in bytes after encoding specified input size
     */
    int getOutputSize(int inputSize);

    /**
     * Returns the block size. When encoding, data is sometimes processed in blocks, this method returns the size of
     * those blocks. If the block size cannot be determined, it returns {@code 0}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link ByteStream.Encoder} with current encoding. When using this method, the
     * {@link ByteStream#blockSize(int)} needs to be set to a correct value, such as {@link #getBlockSize()} (if it does
     * not return {@code 0}).
     *
     * @return a {@link ByteStream.Encoder} with current encoding
     */
    ByteStream.Encoder toStreamEncoder();
}
