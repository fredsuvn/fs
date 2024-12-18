package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;

import java.nio.ByteBuffer;

/**
 * This interface extends {@link ByteEncoder} and adds methods for encoding binary to char, such as {@link JieBase64}
 * and {@link JieHex}.
 *
 * @author sunqian
 * @see JieBase64
 * @see JieHex
 */
public interface ToCharEncoder extends ByteEncoder {

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
