package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.encode.hex.JieHex;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * This interface extends {@link ByteDecoder} and adds methods for decoding char to binary, such as {@link JieBase64}
 * and {@link JieHex}.
 *
 * @author sunqian
 * @see JieBase64
 * @see JieHex
 */
public interface ToCharDecoder extends ByteDecoder {

    /**
     * Decodes given chars data into a new byte array (with {@link JieChars#latinCharset()}).
     *
     * @param data given chars data
     * @return result of decoding into a new byte array (with {@link JieChars#latinCharset()})
     * @throws DecodingException if any decoding problem occurs
     */
    default byte[] decode(CharSequence data) throws DecodingException {
        byte[] bytes = JieString.getBytes(data, JieChars.latinCharset());
        return decode(bytes);
    }

    /**
     * Decodes given chars data into a new byte array (with {@link JieChars#latinCharset()}).
     *
     * @param data given chars data
     * @return result of decoding into a new byte array (with {@link JieChars#latinCharset()})
     * @throws DecodingException if any decoding problem occurs
     */
    default byte[] decode(char[] data) throws DecodingException {
        byte[] bytes = JieString.getBytes(data, JieChars.latinCharset());
        return decode(bytes);
    }

    /**
     * Decodes given char buffer into a new byte buffer (with {@link JieChars#latinCharset()}). The data buffer's
     * position will be incremented by the read length.
     *
     * @param data given data
     * @return result of decoding into a new byte buffer (with {@link JieChars#latinCharset()})
     * @throws DecodingException if any decoding problem occurs
     */
    default ByteBuffer decode(CharBuffer data) throws DecodingException {
        char[] chars = JieChars.getChars(data);
        byte[] bytes = JieString.getBytes(chars);
        return decode(ByteBuffer.wrap(bytes));
    }
}
