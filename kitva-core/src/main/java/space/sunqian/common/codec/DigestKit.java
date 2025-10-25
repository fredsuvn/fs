package space.sunqian.common.codec;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.io.ByteProcessor;
import space.sunqian.common.io.ByteTransformer;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Utilities for {@link MessageDigest}.
 *
 * @author sunqian
 */
public class DigestKit {

    /**
     * Returns a new {@link ByteTransformer} instance for the specified {@link MessageDigest}. The transformer uses
     * {@link MessageDigest#update(ByteBuffer)} and {@link MessageDigest#digest()} to process the input data, and before
     * the {@code end} is {@code true}, the transformer returns {@code null}.
     * <p>
     * If the digest requires a specified input length, try using {@link ByteProcessor#readBlockSize(int)} or
     * {@link ByteTransformer#withFixedSize(ByteTransformer, int)} to configure it.
     *
     * @param digest the specified {@link MessageDigest}, should be initialized
     * @return a new {@link ByteTransformer} instance for the specified {@link MessageDigest}
     */
    public static @Nonnull ByteTransformer digestTransformer(@Nonnull MessageDigest digest) {
        return (data, end) -> {
            digest.update(data);
            if (end) {
                return ByteBuffer.wrap(digest.digest());
            } else {
                return null;
            }
        };
    }
}
