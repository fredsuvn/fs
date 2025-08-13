package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.ByteEncoder;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Utilities kit for crypto.
 *
 * @author sunqian
 */
public class CryptoKit {

    /**
     * Returns a new {@link ByteEncoder.Handler} instance for the specified {@link Cipher}. The handler uses
     * {@link Cipher#doFinal(ByteBuffer, ByteBuffer)} to process the input data, and the input data is considered as a
     * block that needs to be encoded/decoded.
     * <p>
     * If the cipher requires a specified input length, try using {@link ByteEncoder#readBlockSize(int)} or
     * {@link ByteEncoder#newFixedSizeHandler(ByteEncoder.Handler, int)} to configure it.
     *
     * @param cipher the specified {@link Cipher}, should be initialized
     * @return a new {@link ByteEncoder.Handler} instance for the specified {@link Cipher}
     */
    public static @Nonnull ByteEncoder.Handler cipherHandler(@Nonnull Cipher cipher) {
        return new ByteEncoder.Handler() {
            @Override
            public @Nonnull ByteBuffer handle(@Nonnull ByteBuffer data, boolean end) throws Exception {
                int outputSize = cipher.getOutputSize(data.remaining());
                ByteBuffer out = ByteBuffer.allocate(outputSize);
                cipher.doFinal(data, out);
                out.flip();
                return out;
            }
        };
    }

    /**
     * Returns a new {@link ByteEncoder.Handler} instance for the specified {@link MessageDigest}. The handler uses
     * {@link MessageDigest#update(ByteBuffer)} and {@link MessageDigest#digest()} to process the input data, and before
     * the {@code end} is {@code true}, the handler returns {@code null}.
     * <p>
     * If the digest requires a specified input length, try using {@link ByteEncoder#readBlockSize(int)} or
     * {@link ByteEncoder#newFixedSizeHandler(ByteEncoder.Handler, int)} to configure it.
     *
     * @param digest the specified {@link MessageDigest}, should be initialized
     * @return a new {@link ByteEncoder.Handler} instance for the specified {@link MessageDigest}
     */
    public static @Nonnull ByteEncoder.Handler digestHandler(@Nonnull MessageDigest digest) {
        return (data, end) -> {
            digest.update(data);
            if (end) {
                return ByteBuffer.wrap(digest.digest());
            } else {
                return null;
            }
        };
    }

    /**
     * Returns a new {@link ByteEncoder.Handler} instance for the specified {@link Mac}. The handler uses
     * {@link Mac#update(ByteBuffer)} and {@link Mac#doFinal()} to process the input data, and before the {@code end} is
     * {@code true}, the handler returns {@code null}.
     * <p>
     * If the digest requires a specified input length, try using {@link ByteEncoder#readBlockSize(int)} or
     * {@link ByteEncoder#newFixedSizeHandler(ByteEncoder.Handler, int)} to configure it.
     *
     * @param mac the specified {@link Mac}, should be initialized
     * @return a new {@link ByteEncoder.Handler} instance for the specified {@link Mac}
     */
    public static @Nonnull ByteEncoder.Handler macHandler(@Nonnull Mac mac) {
        return (data, end) -> {
            mac.update(data);
            if (end) {
                return ByteBuffer.wrap(mac.doFinal());
            } else {
                return null;
            }
        };
    }
}
