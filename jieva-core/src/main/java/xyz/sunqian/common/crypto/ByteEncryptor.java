package xyz.sunqian.common.crypto;

import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.encode.ByteCoder;
import xyz.sunqian.common.encode.EncodingException;
import xyz.sunqian.common.encode.JieBase64;
import xyz.sunqian.common.encode.JieHex;

import java.nio.ByteBuffer;

/**
 * Byte encoder is used for encoding in bytes, such as {@link JieHex.Encoder} and {@link JieBase64.Encoder}. The
 * implementations should be thread-safe.
 *
 * @author sunqian
 * @see JieHex.Encoder
 * @see JieBase64.Encoder
 */
@ThreadSafe
public interface ByteEncryptor extends ByteCoder {

    /**
     * Encodes given source into a new byte array.
     *
     * @param source given source
     * @return result of encoding into a new byte array
     * @throws EncodingException for encoding error
     */
    byte[] encode(byte[] source) throws EncodingException;

    /**
     * Encodes given source into a new byte buffer. The source buffer's position will be incremented by the read length,
     * the returned buffer's position will be zero and its limit will be the number of encoding bytes.
     *
     * @param source given source
     * @return result of encoding into a new byte buffer
     * @throws EncodingException for encoding error
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
     * @throws EncodingException for encoding error
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
     * @throws EncodingException for encoding error
     */
    int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException;

    /**
     * Expansion of {@link ByteEncryptor}, provides methods to encode to string with {@link JieChars#latinCharset()}.
     */
    interface ToLatin extends ByteEncryptor {

        /**
         * Encodes given source to string with {@link JieChars#latinCharset()}.
         *
         * @param source given source
         * @return result of encoding to string with {@link JieChars#latinCharset()}
         * @throws EncodingException for encoding error
         */
        default String toString(byte[] source) throws EncodingException {
            return new String(encode(source), JieChars.latinCharset());
        }

        /**
         * Encodes given source to string with {@link JieChars#latinCharset()}.
         *
         * @param source given source
         * @return result of encoding to string with {@link JieChars#latinCharset()}
         * @throws EncodingException for encoding error
         */
        default String toString(ByteBuffer source) throws EncodingException {
            ByteBuffer encoded = encode(source);
            byte[] bytes = JieBytes.getBytes(encoded);
            return new String(bytes, JieChars.latinCharset());
        }
    }
}
