package xyz.sunqian.common.encode;

import java.nio.ByteBuffer;

/**
 * This interface represents a decoder for {@code byte} decoding.
 *
 * @author sunqian
 */
public interface ByteDecoder extends ByteCoder {

    /**
     * Decodes given data into a new byte array.
     *
     * @param data given data
     * @return result of decoding into a new byte array
     * @throws DecodingException if any decoding problem occurs
     */
    byte[] decode(byte[] data) throws DecodingException;

    /**
     * Decodes given data into a new byte buffer. The data buffer's position will be incremented by the read length, the
     * returned buffer's position will be zero and its limit will be the number of decoding bytes.
     *
     * @param data given data
     * @return result of decoding into a new byte buffer
     * @throws DecodingException if any decoding problem occurs
     */
    ByteBuffer decode(ByteBuffer data) throws DecodingException;

    /**
     * Decodes given data into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link DecodingException}
     * will be thrown.
     *
     * @param data given data
     * @param dest specified dest
     * @return the number of bytes written
     * @throws DecodingException if any decoding problem occurs
     */
    int decode(byte[] data, byte[] dest) throws DecodingException;

    /**
     * Decodes given data into specified dest, returns the number of bytes written. The buffer's positions will be
     * incremented by their affected length.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link DecodingException}
     * will be thrown.
     *
     * @param data given data
     * @param dest specified dest
     * @return the number of bytes written
     * @throws DecodingException if any decoding problem occurs
     */
    int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException;
}
