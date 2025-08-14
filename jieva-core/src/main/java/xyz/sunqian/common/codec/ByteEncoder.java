package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.ByteTransformer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * This interface represents the encoder for encoding bytes.
 *
 * @author sunqian
 */
public interface ByteEncoder {

    /**
     * Encodes the given bytes.
     *
     * @param bytes the given bytes to be encoded
     * @return the result
     * @throws ByteEncodingException if any error occurs
     */
    byte @Nonnull [] encode(byte @Nonnull [] bytes) throws ByteEncodingException;

    /**
     * Encodes all bytes from the given buffer. The buffer's position will increment to its limit, and the return
     * buffer's position will be {@code 0}.
     *
     * @param bytes the given buffer to be encoded
     * @return the result
     * @throws ByteEncodingException if any error occurs
     */
    @Nonnull
    ByteBuffer encode(@Nonnull ByteBuffer bytes) throws ByteEncodingException;

    /**
     * Encodes all bytes from the specified input buffer into the specified output buffer, returns the number of bytes
     * written to the output buffer. The input buffer's position will increment to its limit, and the output buffer's
     * position will increment by the write number. Make sure the output buffer has enough space to write.
     *
     * @param in  the specified input buffer
     * @param out the specified output buffer
     * @return the number of bytes written to the output buffer
     * @throws ByteEncodingException if any error occurs
     */
    int encode(@Nonnull ByteBuffer in, @Nonnull ByteBuffer out) throws ByteEncodingException;

    /**
     * Returns a {@link ByteTransformer} that equivalents to this encoder. Note that the {@link ByteTransformer} may be
     * stateful and not thread safe.
     *
     * @return a {@link ByteTransformer} that equivalents to this encoder
     */
    @Nonnull
    ByteTransformer asTransformer();

    /**
     * Encodes the given bytes. Returns the result as String with {@link StandardCharsets#ISO_8859_1}.
     *
     * @param bytes the given bytes to be encoded
     * @return the result as String with {@link StandardCharsets#ISO_8859_1}
     * @throws ByteEncodingException if any error occurs
     */
    default @Nonnull String encodeToLatin1(byte @Nonnull [] bytes) throws ByteEncodingException {
        return new String(encode(bytes), StandardCharsets.ISO_8859_1);
    }

    /**
     * Encodes all bytes from the given buffer. Returns the result as String with {@link StandardCharsets#ISO_8859_1}.
     * The buffer's position will increment to its limit, and the return buffer's position will be {@code 0}.
     *
     * @param bytes the given buffer to be encoded
     * @return the result as String with {@link StandardCharsets#ISO_8859_1}
     * @throws ByteEncodingException if any error occurs
     */
    default @Nonnull String encodeToLatin1(@Nonnull ByteBuffer bytes) throws ByteEncodingException {
        byte[] encoded = BufferKit.read(encode(bytes));
        if (encoded == null) {
            return "";
        }
        return new String(encoded, StandardCharsets.ISO_8859_1);
    }
}
