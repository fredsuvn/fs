package xyz.sunqian.common.crypto;

import xyz.sunqian.common.io.BytesProcessor;

/**
 * This interface is the super interface for {@link ByteEncryptor} and {@link ByteDecryptor}, for encrypting and
 * decrypting in bytes respectively.
 *
 * @author sunqian
 * @see ByteEncryptor
 * @see ByteDecryptor
 */
public interface ByteCrypto {

    /**
     * This method is used to estimate the output size in bytes of input data of specified size after
     * encrypting/decrypting. If the exact output size cannot be determined, returns a maximum value that can
     * accommodate all output, or {@code -1} if the estimating fails.
     *
     * @param inputSize specified size of input data
     * @return output size in bytes of input data of specified size after encrypting/decrypting
     * @throws CryptoException for crypto error
     */
    int getOutputSize(int inputSize) throws CryptoException;

    /**
     * If current implementation of this method requires processing in blocks, it returns the block size. Otherwise, it
     * returns a value {@code <= 0}.
     *
     * @return the block size
     */
    int getBlockSize();

    /**
     * Returns a {@link BytesProcessor.Encoder} which encapsulates current encrypting/decrypting algorithm for
     * processing within {@link BytesProcessor}.
     * <p>
     * Note that different {@link BytesProcessor.Encoder} implementations may have specific requirements, such as
     * specified block size, for the data to be encrypted/decrypted.
     *
     * @return a {@link BytesProcessor.Encoder} encapsulates current encrypting/decrypting algorithm
     * @see BytesProcessor
     * @see BytesProcessor.Encoder
     */
    BytesProcessor.Encoder streamEncoder();
}
