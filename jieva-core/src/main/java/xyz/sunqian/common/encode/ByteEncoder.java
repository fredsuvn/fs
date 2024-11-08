package xyz.sunqian.common.encode;

import java.nio.ByteBuffer;

/**
 * This interface represents an encoder for {@code byte} encoding.
 *
 * @author sunqian
 */
public interface ByteEncoder extends ByteCoder {

    /**
     * Encodes given source into a new byte array.
     *
     * @param source given source
     * @return result of encoding into a new byte array
     * @throws EncodingException if any encoding problem occurs
     */
    byte[] encode(byte[] source) throws EncodingException;

    /**
     * Encodes given source into a new byte buffer. The source buffer's position will be incremented by the read length,
     * the returned buffer's position will be zero and its limit will be the number of encoding bytes.
     *
     * @param source given source
     * @return result of encoding into a new byte buffer
     * @throws EncodingException if any encoding problem occurs
     */
    ByteBuffer encode(ByteBuffer source) throws EncodingException;

    /**
     * Encodes given source into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link EncodingException}
     * will be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws EncodingException if any encoding problem occurs
     */
    int encode(byte[] source, byte[] dest) throws EncodingException;

    /**
     * Encodes given source into specified dest, returns the number of bytes written. The buffer's positions will be
     * incremented by their affected length.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link EncodingException}
     * will be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws EncodingException if any encoding problem occurs
     */
    int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException;
}
