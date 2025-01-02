package test.crypto;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieIO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.testng.Assert.assertEquals;

public class CryptoTest {

    @Test
    public void testRsa() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        byte[] data = JieRandom.fill(new byte[245]);

        // encrypt
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        System.out.println("encrypt block size: " + cipher.getBlockSize());
        System.out.println("encrypt output size: " + cipher.getOutputSize(data.length));
        byte[] encryptedBytes = cipher.doFinal(data);
        System.out.println("encryptedBytes.length: " + encryptedBytes.length);
        System.out.println("encrypt block size: " + cipher.getBlockSize());
        System.out.println("encrypt output size: " + cipher.getOutputSize(data.length));

        // decrypt
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        System.out.println("decrypt block size: " + cipher.getBlockSize());
        System.out.println("decrypt output size: " + cipher.getOutputSize(encryptedBytes.length));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        System.out.println("decrypt block size: " + cipher.getBlockSize());
        System.out.println("decrypt output size: " + cipher.getOutputSize(encryptedBytes.length));

        assertEquals(data, decryptedBytes);
    }

    @Test
    public void testAes() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        Key key = keyGenerator.generateKey();

        byte[] data = JieRandom.fill(new byte[17]);

        // encrypt
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        System.out.println("encrypt block size: " + cipher.getBlockSize());
        System.out.println("encrypt output size: " + cipher.getOutputSize(data.length));
        byte[] encryptedBytes = cipher.doFinal(data);
        System.out.println("encryptedBytes.length: " + encryptedBytes.length);
        System.out.println("encrypt block size: " + cipher.getBlockSize());
        System.out.println("encrypt output size: " + cipher.getOutputSize(data.length));

        // decrypt
        cipher.init(Cipher.DECRYPT_MODE, key);
        System.out.println("decrypt block size: " + cipher.getBlockSize());
        System.out.println("decrypt output size: " + cipher.getOutputSize(encryptedBytes.length));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        System.out.println("decrypt block size: " + cipher.getBlockSize());
        System.out.println("decrypt output size: " + cipher.getOutputSize(encryptedBytes.length));

        assertEquals(data, decryptedBytes);

        // stream
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] en = JieIO.processor(data).readBlockSize(3).encoder(cipher.getBlockSize(), new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                if (!end) {
                    byte[] result = cipher.update(JieBytes.getBytes(data));
                    System.out.println("result length: " + (result == null ? 0 : result.length));
                    return result == null ? null : ByteBuffer.wrap(result);
                }
                try {
                    byte[] result = cipher.doFinal(JieBytes.getBytes(data));
                    System.out.println("result length: " + (result == null ? 0 : result.length));
                    return result == null ? null : ByteBuffer.wrap(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).writeToByteArray();
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] de = cipher.doFinal(en);
        assertEquals(data, de);
    }
}
