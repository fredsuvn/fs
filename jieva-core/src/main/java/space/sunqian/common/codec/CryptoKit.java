package space.sunqian.common.codec;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.io.ByteProcessor;
import space.sunqian.common.io.ByteTransformer;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.nio.ByteBuffer;

/**
 * Utilities for crypto.
 *
 * @author sunqian
 */
public class CryptoKit {

    /**
     * Returns a new {@link ByteTransformer} instance for the specified {@link Cipher}. The transformer uses
     * {@link Cipher#doFinal(ByteBuffer, ByteBuffer)} to process the input data, and the input data is considered as a
     * block that needs to be encoded/decoded.
     * <p>
     * If the cipher requires a specified input length, try using {@link ByteProcessor#readBlockSize(int)} or
     * {@link ByteTransformer#withFixedSize(ByteTransformer, int)} to configure it.
     *
     * @param cipher the specified {@link Cipher}, should be initialized
     * @return a new {@link ByteTransformer} instance for the specified {@link Cipher}
     */
    public static @Nonnull ByteTransformer cipherTransformer(@Nonnull Cipher cipher) {
        return new ByteTransformer() {
            @Override
            public @Nonnull ByteBuffer transform(@Nonnull ByteBuffer data, boolean end) throws Exception {
                int outputSize = cipher.getOutputSize(data.remaining());
                ByteBuffer out = ByteBuffer.allocate(outputSize);
                cipher.doFinal(data, out);
                out.flip();
                return out;
            }
        };
    }

    /**
     * Returns a new {@link ByteTransformer} instance for the specified {@link Mac}. The transformer uses
     * {@link Mac#update(ByteBuffer)} and {@link Mac#doFinal()} to process the input data, and before the {@code end} is
     * {@code true}, the transformer returns {@code null}.
     * <p>
     * If the digest requires a specified input length, try using {@link ByteProcessor#readBlockSize(int)} or
     * {@link ByteTransformer#withFixedSize(ByteTransformer, int)} to configure it.
     *
     * @param mac the specified {@link Mac}, should be initialized
     * @return a new {@link ByteTransformer} instance for the specified {@link Mac}
     */
    public static @Nonnull ByteTransformer macTransformer(@Nonnull Mac mac) {
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
