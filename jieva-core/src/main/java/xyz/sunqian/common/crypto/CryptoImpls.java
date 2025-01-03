package xyz.sunqian.common.crypto;

import xyz.sunqian.common.io.BytesProcessor;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;

final class CryptoImpls {


    private static final class CipherEncryptor implements ByteEncryptor {

        private final Cipher cipher;

        private CipherEncryptor(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        public byte[] encrypt(byte[] source) throws CryptoException {
            try {
                return cipher.doFinal(source);
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        }

        @Override
        public ByteBuffer encrypt(ByteBuffer source) throws CryptoException {
            try {
                byte[] sourceArray = new byte[source.remaining()];
                return ByteBuffer.wrap(cipher.doFinal(sourceArray));
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        }

        @Override
        public int encrypt(byte[] source, byte[] dest) throws CryptoException {
            return 0;
        }

        @Override
        public int encrypt(ByteBuffer source, ByteBuffer dest) throws CryptoException {
            return 0;
        }

        @Override
        public int getOutputSize(int inputSize) throws CryptoException {
            return 0;
        }

        @Override
        public int getBlockSize() {
            return 0;
        }

        @Override
        public BytesProcessor.Encoder streamEncoder() {
            return null;
        }
    }
}
