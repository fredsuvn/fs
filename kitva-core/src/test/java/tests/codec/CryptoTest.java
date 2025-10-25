package tests.codec;

import org.testng.annotations.Test;
import space.sunqian.common.codec.CryptoKit;
import space.sunqian.common.io.ByteProcessor;
import internal.test.DataTest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.testng.Assert.assertEquals;

public class CryptoTest implements DataTest {

    @Test
    public void testCipher() throws Exception {
        {
            KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
            aesKeyGenerator.init(128);
            SecretKey aesKey = aesKeyGenerator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            testCipher(aesCipher, aesKey, aesKey, 1024, 16, 32);
            testCipher(aesCipher, aesKey, aesKey, 1111, 16, 32);
            testCipher(aesCipher, aesKey, aesKey, 3337, 16, 32);
        }
        {
            KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
            rsaKeyPairGenerator.initialize(1024);
            KeyPair rsaKeyPair = rsaKeyPairGenerator.generateKeyPair();
            Cipher rsaCipher = Cipher.getInstance("RSA");
            testCipher(rsaCipher, rsaKeyPair.getPublic(), rsaKeyPair.getPrivate(), 1024, 111, 128);
            testCipher(rsaCipher, rsaKeyPair.getPublic(), rsaKeyPair.getPrivate(), 1024, 22, 128);
            testCipher(rsaCipher, rsaKeyPair.getPublic(), rsaKeyPair.getPrivate(), 1111, 111, 128);
            testCipher(rsaCipher, rsaKeyPair.getPublic(), rsaKeyPair.getPrivate(), 3337, 22, 128);
        }
        {
            KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
            rsaKeyPairGenerator.initialize(2048);
            KeyPair rsaKeyPair = rsaKeyPairGenerator.generateKeyPair();
            Cipher rsaCipher = Cipher.getInstance("RSA");
            testCipher(rsaCipher, rsaKeyPair.getPublic(), rsaKeyPair.getPrivate(), 9999, 245, 256);
            testCipher(rsaCipher, rsaKeyPair.getPublic(), rsaKeyPair.getPrivate(), 9999, 99, 256);
        }
    }

    private void testCipher(
        Cipher cipher, Key enKey, Key deKey, int totalSize, int enBlock, int deBlock
    ) throws Exception {
        byte[] data = randomBytes(totalSize);
        cipher.init(Cipher.ENCRYPT_MODE, enKey);
        byte[] enBytes = ByteProcessor.from(data)
            .readBlockSize(enBlock)
            .transformer(CryptoKit.cipherTransformer(cipher))
            .toByteArray();
        cipher.init(Cipher.DECRYPT_MODE, deKey);
        byte[] ret = ByteProcessor.from(enBytes)
            .readBlockSize(deBlock)
            .transformer(CryptoKit.cipherTransformer(cipher))
            .toByteArray();
        assertEquals(ret, data);
    }

    @Test
    public void testMac() throws Exception {
        KeyGenerator hmacKeyGenerator = KeyGenerator.getInstance("HmacMD5");
        testMac(Mac.getInstance("HmacMD5"), hmacKeyGenerator.generateKey(), 1024, 64);
        testMac(Mac.getInstance("HmacMD5"), hmacKeyGenerator.generateKey(), 3337, 97);
    }

    private void testMac(Mac mac, Key key, int totalSize, int blockSize) throws Exception {
        byte[] data = randomBytes(totalSize);
        mac.init(key);
        byte[] enBytes = ByteProcessor.from(data)
            .readBlockSize(blockSize)
            .transformer(CryptoKit.macTransformer(mac))
            .toByteArray();
        assertEquals(enBytes, mac.doFinal(data));
    }
}
