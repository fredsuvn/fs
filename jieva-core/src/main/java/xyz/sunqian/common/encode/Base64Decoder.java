package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.codec.CodecException;

/**
 * This interface represents a type of {@link Decoder} for {@code Base64}, see {@link JieBase64}.
 *
 * @author sunqian
 * @see JieBase64
 */
public interface Base64Decoder extends Decoder {

    /**
     * Decodes given data to bytes (with {@link JieChars#latinCharset()}).
     *
     * @param data given data
     * @return result of decoding to bytes (with {@link JieChars#latinCharset()})
     * @throws CodecException if any codec problem occurs
     */
    default byte[] decode(String data) throws CodecException {
        byte[] bytes = data.getBytes(JieChars.latinCharset());
        return decode(bytes);
    }
}
