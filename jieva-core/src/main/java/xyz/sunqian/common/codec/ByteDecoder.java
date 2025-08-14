package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.ByteTransformer;

import java.nio.ByteBuffer;

/**
 * This interface represents the decoder for decoding bytes.
 *
 * @author sunqian
 */
public interface ByteDecoder {

    /**
     * Decodes the given bytes.
     *
     * @param bytes the given bytes to be decoded
     * @return the result
     * @throws ByteDecodingException if any error occurs
     */
    byte @Nonnull [] decode(byte @Nonnull [] bytes) throws ByteDecodingException;

    /**
     * Decodes all bytes from the given buffer. The buffer's position will increment to its limit, and the return
     * buffer's position will be {@code 0}.
     *
     * @param bytes the given buffer to be decoded
     * @return the result
     * @throws ByteDecodingException if any error occurs
     */
    @Nonnull
    ByteBuffer decode(@Nonnull ByteBuffer bytes) throws ByteDecodingException;

    /**
     * Decodes all bytes from the specified input buffer into the specified output buffer, returns the number of bytes
     * written to the output buffer. The input buffer's position will increment to its limit, and the output buffer's
     * position will increment by the write number. Make sure the output buffer has enough space to write.
     *
     * @param in  the specified input buffer
     * @param out the specified output buffer
     * @return the number of bytes written to the output buffer
     * @throws ByteDecodingException if any error occurs
     */
    int decode(@Nonnull ByteBuffer in, @Nonnull ByteBuffer out) throws ByteDecodingException;

    /**
     * Returns a {@link ByteTransformer} that equivalents to this decoder. Note that the {@link ByteTransformer} may be
     * stateful and not thread safe.
     *
     * @return a {@link ByteTransformer} that equivalents to this decoder
     */
    @Nonnull
    ByteTransformer asTransformer();
}
