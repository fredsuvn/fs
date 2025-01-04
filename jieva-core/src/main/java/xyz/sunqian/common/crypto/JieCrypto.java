package xyz.sunqian.common.crypto;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieIO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.nio.ByteBuffer;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;

/**
 * This is a static utilities class provides utilities for crypto operations.
 *
 * @author sunqian
 */
public class JieCrypto {

    /**
     * Returns a {@link KeyGenerator} instance for the specified algorithm and provider. If the provider is
     * {@code null}, the default provider is used.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider, may be {@code null}
     * @return a {@link KeyGenerator} instance for the specified algorithm and provider
     */
    public static KeyGenerator keyGenerator(String algorithm, @Nullable Provider provider) {
        try {
            return provider == null ?
                KeyGenerator.getInstance(algorithm)
                :
                KeyGenerator.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Returns a {@link KeyPairGenerator} instance for the specified algorithm and provider. If the provider is
     * {@code null}, the default provider is used.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider, may be {@code null}
     * @return a {@link KeyPairGenerator} instance for the specified algorithm and provider
     */
    public static KeyPairGenerator keyPairGenerator(String algorithm, @Nullable Provider provider) {
        try {
            return provider == null ?
                KeyPairGenerator.getInstance(algorithm)
                :
                KeyPairGenerator.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Returns a {@link Cipher} instance for the specified algorithm and provider. If the provider is {@code null}, the
     * default provider is used.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider, may be {@code null}
     * @return a {@link Cipher} instance for the specified algorithm and provider
     */
    public static Cipher cipher(String algorithm, @Nullable Provider provider) {
        try {
            return provider == null ?
                Cipher.getInstance(algorithm)
                :
                Cipher.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Returns a {@link MessageDigest} instance for the specified algorithm and provider. If the provider is
     * {@code null}, the default provider is used.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider, may be {@code null}
     * @return a {@link MessageDigest} instance for the specified algorithm and provider
     */
    public static MessageDigest digest(String algorithm, @Nullable Provider provider) {
        try {
            return provider == null ?
                MessageDigest.getInstance(algorithm)
                :
                MessageDigest.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Returns a {@link Mac} instance for the specified algorithm and provider. If the provider is {@code null}, the
     * default provider is used.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider, may be {@code null}
     * @return a {@link Mac} instance for the specified algorithm and provider
     */
    public static Mac mac(String algorithm, @Nullable Provider provider) {
        try {
            return provider == null ?
                Mac.getInstance(algorithm)
                :
                Mac.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} for the specified {@link Cipher} operations in blocks, using specified
     * block size. If {@code onlyFinal} is true, the encoder uses only the {@code doFinal} method. Otherwise, the
     * encoder invokes the {@code update} method to process data when {@code end} is false, and invokes {@code doFinal}
     * for final processing when {@code end} is true. Returned encoder is a fixed size encoder from
     * {@link JieIO#fixedSizeEncoder(int, BytesProcessor.Encoder)}, and its fixed size is the specified block size.
     *
     * @param cipher    specified {@link Cipher}, should be initialized
     * @param blockSize specified block size
     * @param onlyFinal whether the encoder uses only the {@code doFinal} method
     * @return a {@link BytesProcessor.Encoder} for the specified {@link Mac} operations in blocks
     */
    public static BytesProcessor.Encoder encoder(Cipher cipher, int blockSize, boolean onlyFinal) {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    if (onlyFinal) {
                        if (JieBytes.isEmpty(data)) {
                            return null;
                        }
                        return doFinal(cipher, data);
                    }
                    ByteBuffer ret;
                    if (end) {
                        ret = doFinal(cipher, data);
                    } else {
                        ret = doUpdate(cipher, data);
                    }
                    return ret;
                } catch (Exception e) {
                    throw new CryptoException(e);
                }
            }
        };
        return JieIO.fixedSizeEncoder(blockSize, encoder);
    }

    private static ByteBuffer doFinal(Cipher cipher, ByteBuffer input) throws Exception {
        byte[] ret;
        if (input.hasArray()) {
            ret = cipher.doFinal(
                input.array(), input.arrayOffset() + input.position(), input.remaining()
            );
            input.position(input.position() + input.remaining());
        } else {
            byte[] inBytes = JieBytes.getBytes(input);
            ret = cipher.doFinal(inBytes);
        }
        return toBuffer(ret);
    }

    private static ByteBuffer doUpdate(Cipher cipher, ByteBuffer input) throws Exception {
        byte[] ret;
        if (input.hasArray()) {
            ret = cipher.update(
                input.array(), input.arrayOffset() + input.position(), input.remaining()
            );
            input.position(input.position() + input.remaining());
        } else {
            byte[] inBytes = JieBytes.getBytes(input);
            ret = cipher.update(inBytes);
        }
        return toBuffer(ret);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} for the specified {@link MessageDigest} operations in blocks, using
     * specified block size. The encoder invokes the {@code update} method to process data when {@code end} is false,
     * and invokes {@code doFinal} for final processing when {@code end} is true. Returned encoder is a fixed size
     * encoder from {@link JieIO#fixedSizeEncoder(int, BytesProcessor.Encoder)}, and its fixed size is the specified
     * block size.
     *
     * @param digest    specified {@link MessageDigest}, should be initialized
     * @param blockSize specified block size
     * @return a {@link BytesProcessor.Encoder} for the specified {@link Mac} operations in blocks
     */
    public static BytesProcessor.Encoder encoder(MessageDigest digest, int blockSize) {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    ByteBuffer ret;
                    if (end) {
                        ret = ByteBuffer.wrap(doFinal(digest, data));
                    } else {
                        doUpdate(digest, data);
                        ret = null;
                    }
                    return ret;
                } catch (Exception e) {
                    throw new CryptoException(e);
                }
            }
        };
        return JieIO.fixedSizeEncoder(blockSize, encoder);
    }

    private static byte[] doFinal(MessageDigest digest, ByteBuffer input) throws Exception {
        doUpdate(digest, input);
        return digest.digest();
    }

    private static void doUpdate(MessageDigest digest, ByteBuffer input) {
        if (input.hasArray()) {
            digest.update(
                input.array(), input.arrayOffset() + input.position(), input.remaining()
            );
            input.position(input.position() + input.remaining());
        } else {
            byte[] inBytes = JieBytes.getBytes(input);
            digest.update(inBytes);
        }
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} for the specified {@link Mac} operations in blocks, using specified
     * block size. The encoder invokes the {@code update} method to process data when {@code end} is false, and invokes
     * {@code doFinal} for final processing when {@code end} is true. Returned encoder is a fixed size encoder from
     * {@link JieIO#fixedSizeEncoder(int, BytesProcessor.Encoder)}, and its fixed size is the specified block size.
     *
     * @param mac       specified {@link Mac}, should be initialized
     * @param blockSize specified block size
     * @return a {@link BytesProcessor.Encoder} for the specified {@link Mac} operations in blocks
     */
    public static BytesProcessor.Encoder encoder(Mac mac, int blockSize) {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    ByteBuffer ret;
                    if (end) {
                        ret = ByteBuffer.wrap(doFinal(mac, data));
                    } else {
                        doUpdate(mac, data);
                        ret = null;
                    }
                    return ret;
                } catch (Exception e) {
                    throw new CryptoException(e);
                }
            }
        };
        return JieIO.fixedSizeEncoder(blockSize, encoder);
    }

    private static byte[] doFinal(Mac mac, ByteBuffer input) throws Exception {
        doUpdate(mac, input);
        return mac.doFinal();
    }

    private static void doUpdate(Mac mac, ByteBuffer input) {
        if (input.hasArray()) {
            mac.update(
                input.array(), input.arrayOffset() + input.position(), input.remaining()
            );
            input.position(input.position() + input.remaining());
        } else {
            byte[] inBytes = JieBytes.getBytes(input);
            mac.update(inBytes);
        }
    }

    private static ByteBuffer toBuffer(@Nullable byte[] ret) {
        return JieArray.isEmpty(ret) ? null : ByteBuffer.wrap(ret);
    }
}
