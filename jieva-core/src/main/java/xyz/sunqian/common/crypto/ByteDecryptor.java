package xyz.sunqian.common.crypto;

import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.encode.ByteCoder;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.JieBase64;
import xyz.sunqian.common.encode.JieHex;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Byte decoder is used for decoding in bytes, such as {@link JieHex.Decoder} and {@link JieBase64.Decoder}. The
 * implementations should be thread-safe.
 *
 * @author sunqian
 * @see JieHex.Decoder
 * @see JieBase64.Decoder
 */
@ThreadSafe
public interface ByteDecryptor extends ByteCoder {

    /**
     * Decodes given data into a new byte array.
     *
     * @param data given data
     * @return result of decoding into a new byte array
     * @throws DecodingException for decoding error
     */
    byte[] decode(byte[] data) throws DecodingException;

    /**
     * Decodes given data into a new byte buffer. The data buffer's position will be incremented by the read length, the
     * returned buffer's position will be zero and its limit will be the number of decoding bytes.
     *
     * @param data given data
     * @return result of decoding into a new byte buffer
     * @throws DecodingException for decoding error
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
     * @throws DecodingException for decoding error
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
     * @throws DecodingException for decoding error
     */
    int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException;

    /**
     * Expansion of {@link ByteDecryptor}, provides methods to decode string with {@link JieChars#latinCharset()}.
     */
    interface ToLatin extends ByteDecryptor {

        /**
         * Decodes given data with {@link JieChars#latinCharset()}.
         *
         * @param data given data
         * @return result of decoding with {@link JieChars#latinCharset()}
         * @throws DecodingException for decoding error
         */
        default byte[] decode(CharSequence data) throws DecodingException {
            byte[] bytes = JieString.getBytes(data, JieChars.latinCharset());
            return decode(bytes);
        }

        /**
         * Decodes given data with {@link JieChars#latinCharset()}.
         *
         * @param data given data
         * @return result of decoding with {@link JieChars#latinCharset()}
         * @throws DecodingException for decoding error
         */
        default byte[] decode(char[] data) throws DecodingException {
            byte[] bytes = JieString.getBytes(data, JieChars.latinCharset());
            return decode(bytes);
        }

        /**
         * Decodes given data with {@link JieChars#latinCharset()}.
         *
         * @param data given data
         * @return result of decoding with {@link JieChars#latinCharset()}
         * @throws DecodingException for decoding error
         */
        default ByteBuffer decode(CharBuffer data) throws DecodingException {
            char[] chars = JieChars.getChars(data);
            byte[] bytes = JieString.getBytes(chars);
            return decode(ByteBuffer.wrap(bytes));
        }
    }
}
