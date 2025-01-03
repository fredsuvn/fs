package test.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import java.security.Provider;

import static org.testng.Assert.assertEquals;

public class CryptoTest {

    @Test
    public void testRsa() throws Exception {
        //testRsa(null);
        Provider bouncyCastleProvider = new BouncyCastleProvider();
        testRsa(bouncyCastleProvider);
    }

    public void testRsa(Provider provider) throws Exception {
        KeyPairGenerator keyPairGenerator = provider == null ?
            KeyPairGenerator.getInstance("RSA") : KeyPairGenerator.getInstance("RSA", provider);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        byte[] data = JieRandom.fill(new byte[222]);

        // encrypt
        Cipher cipher = provider == null ?
            Cipher.getInstance("RSA") : Cipher.getInstance("RSA", provider);
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

        // stream
        data = JieRandom.fill(new byte[1222]);
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] en = JieIO.processor(data).readBlockSize(3).encoder(cipher.getBlockSize(), new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    byte[] result = cipher.doFinal(JieBytes.getBytes(data));
                    System.out.println("result length: " + (result == null ? 0 : result.length));
                    return result == null ? null : ByteBuffer.wrap(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).writeToByteArray();
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] de = JieIO.processor(en).readBlockSize(3).encoder(cipher.getBlockSize(), new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    byte[] result = cipher.doFinal(JieBytes.getBytes(data));
                    System.out.println("result length: " + (result == null ? 0 : result.length));
                    return result == null ? null : ByteBuffer.wrap(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).writeToByteArray();
        assertEquals(data, de);
    }

    @Test
    public void testAes() throws Exception {
        testAes(null);
        Provider bouncyCastleProvider = new BouncyCastleProvider();
        testAes(bouncyCastleProvider);
    }

    private void testAes(@Nullable Provider provider) throws Exception {
        KeyGenerator keyGenerator = provider == null ?
            KeyGenerator.getInstance("AES") : KeyGenerator.getInstance("AES", provider);
        keyGenerator.init(256);
        Key key = keyGenerator.generateKey();

        byte[] data = JieRandom.fill(new byte[17]);

        // encrypt
        Cipher cipher = provider == null ?
            Cipher.getInstance("AES") : Cipher.getInstance("AES", provider);
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
