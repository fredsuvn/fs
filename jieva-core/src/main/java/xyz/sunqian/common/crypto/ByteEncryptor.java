package xyz.sunqian.common.crypto;

import xyz.sunqian.annotations.ThreadSafe;

import java.nio.ByteBuffer;

/**
 * Byte encryptor is used for encrypting in bytes, such as .
 *
 * @author sunqian
 */
@ThreadSafe
public interface ByteEncryptor extends ByteCrypto {

    /**
     * Encrypts given source into a new byte array.
     *
     * @param source given source
     * @return result of encrypting into a new byte array
     * @throws CryptoException for encrypting error
     */
    byte[] encrypt(byte[] source) throws CryptoException;

    /**
     * Encrypts given source into a new byte buffer. The source buffer's position will be incremented by the read
     * length, the returned buffer's position will be zero and its limit will be the number of encrypting bytes.
     *
     * @param source given source
     * @return result of encrypting into a new byte buffer
     * @throws CryptoException for encrypting error
     */
    ByteBuffer encrypt(ByteBuffer source) throws CryptoException;

    /**
     * Encrypts given source into specified dest, returns the number of bytes written.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link CryptoException} will
     * be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws CryptoException for encrypting error
     */
    int encrypt(byte[] source, byte[] dest) throws CryptoException;

    /**
     * Encrypts given source into specified dest, returns the number of bytes written. The buffer's positions will be
     * incremented by their affected length.
     * <p>
     * Ensure that the remaining of dest is enough, otherwise no byte will be written and a {@link CryptoException} will
     * be thrown.
     *
     * @param source given source
     * @param dest   specified dest
     * @return the number of bytes written
     * @throws CryptoException for encrypting error
     */
    int encrypt(ByteBuffer source, ByteBuffer dest) throws CryptoException;
}
