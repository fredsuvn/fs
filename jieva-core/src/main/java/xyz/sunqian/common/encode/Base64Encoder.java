package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.codec.CodecException;
import xyz.sunqian.common.io.JieBuffer;

import java.nio.ByteBuffer;

/**
 * This interface represents a type of {@link Encoder} for {@code Base64}, see {@link JieBase64}.
 *
 * @author sunqian
 * @see JieBase64
 */
public interface Base64Encoder extends Encoder {

    /**
     * Encodes given source to string (with {@link JieChars#latinCharset()}).
     *
     * @param source given source
     * @return result of encoding to string (with {@link JieChars#latinCharset()})
     * @throws CodecException if any codec problem occurs
     */
    default String encodeToString(byte[] source) throws CodecException {
        return new String(encode(source), JieChars.latinCharset());
    }

    /**
     * Encodes given source to string (with {@link JieChars#latinCharset()}). The returned buffer's position will be
     * zero and its limit will be the number of encoding bytes.
     *
     * @param source given source
     * @return result of encoding to string (with {@link JieChars#latinCharset()})
     * @throws CodecException if any codec problem occurs
     */
    default String encodeToString(ByteBuffer source) throws CodecException {
        ByteBuffer encoded = encode(source);
        byte[] bytes = JieBuffer.read(encoded);
        return new String(bytes, JieChars.latinCharset());
    }
}
