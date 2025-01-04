package test.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.crypto.JieCrypto;
import xyz.sunqian.common.io.BytesBuilder;
import xyz.sunqian.common.io.IOEncodingException;
import xyz.sunqian.common.io.JieIO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class CryptoTest {

    @Test
    public void testCipher() throws Exception {
        testCipher(10086, null);
        Provider bouncyCastleProvider = new BouncyCastleProvider();
        testCipher(10086, bouncyCastleProvider);

        // error
        Cipher cipher = JieCrypto.cipher("AES", null);
        expectThrows(IOEncodingException.class, () ->
            JieIO.processor(new byte[10086])
                .encoder(JieCrypto.encoder(cipher, 16, true))
                .toByteArray()
        );
    }

    private void testCipher(int totalSize, @Nullable Provider provider) throws Exception {
        {
            // AES
            KeyGenerator keyGenerator = JieCrypto.keyGenerator("AES", provider);
            keyGenerator.init(256);
            Key key = keyGenerator.generateKey();
            Cipher cipher = JieCrypto.cipher("AES", provider);
            testCipher(totalSize, 16, 16, cipher, key, key);
        }
        {
            // RSA
            KeyPairGenerator keyPairGenerator = JieCrypto.keyPairGenerator("RSA", provider);
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Cipher cipher = JieCrypto.cipher("RSA/ECB/PKCS1Padding", provider);
            testCipher(totalSize, -245, -256, cipher, keyPair.getPublic(), keyPair.getPrivate());
        }
    }

    private void testCipher(
        int totalSize, int enBlock, int deBlock, Cipher cipher, Key enKey, Key deKey
    ) throws Exception {
        byte[] src = JieRandom.fill(new byte[totalSize]);
        // en
        cipher.init(Cipher.ENCRYPT_MODE, enKey);
        byte[] javaEn = doCipher(src, enBlock, cipher);
        cipher.init(Cipher.ENCRYPT_MODE, enKey);
        byte[] jieEn = JieIO.processor(src)
            .encoder(JieCrypto.encoder(cipher, Math.abs(enBlock), enBlock <= 0)).toByteArray();
        assertEquals(jieEn.length, javaEn.length);

        // de
        cipher.init(Cipher.DECRYPT_MODE, deKey);
        byte[] jieDe = JieIO.processor(javaEn)
            .encoder(((data, end) -> {
                ByteBuffer ret = ByteBuffer.allocateDirect(data.remaining());
                ret.put(data);
                ret.flip();
                return ret;
            }))
            .encoder(JieCrypto.encoder(cipher, Math.abs(deBlock), deBlock <= 0)).toByteArray();
        assertEquals(jieDe, src);
        cipher.init(Cipher.DECRYPT_MODE, deKey);
        byte[] javaDe = doCipher(javaEn, deBlock, cipher);
        assertEquals(javaDe, src);
    }

    private byte[] doCipher(byte[] src, int blockSize, Cipher cipher) throws Exception {
        if (blockSize > 0) {
            return doCipher(src, cipher);
        }
        BytesBuilder bb = new BytesBuilder();
        for (int i = 0; i < src.length; ) {
            int end = Math.min(i + -blockSize, src.length);
            bb.append(cipher.doFinal(Arrays.copyOfRange(src, i, end)));
            i += -blockSize;
        }
        return bb.toByteArray();
    }

    private byte[] doCipher(byte[] src, Cipher cipher) throws Exception {
        BytesBuilder bb = new BytesBuilder();
        for (int i = 0; i < src.length; ) {
            int end = Math.min(i + cipher.getBlockSize(), src.length);
            byte[] en;
            if (end >= src.length) {
                en = cipher.doFinal(Arrays.copyOfRange(src, i, end));
            } else {
                en = cipher.update(Arrays.copyOfRange(src, i, end));
            }
            if (en != null) {
                bb.append(en);
            }
            i += cipher.getBlockSize();
        }
        return bb.toByteArray();
    }

    @Test
    public void testDigest() throws Exception {
        testDigest(10086, 111, null);
        Provider bouncyCastleProvider = new BouncyCastleProvider();
        testDigest(10086, 111, bouncyCastleProvider);

        // error
        // MessageDigest digest = JieCrypto.digest("MD5", null);
        // expectThrows(IOEncodingException.class, () ->
        //     JieIO.processor(new byte[10086])
        //         .encoder(JieCrypto.encoder(digest, 16))
        //         .toByteArray()
        // );
        Mac mac = JieCrypto.mac("HmacSHA256", null);
        expectThrows(IOEncodingException.class, () ->
            JieIO.processor(new byte[10086])
                .encoder(JieCrypto.encoder(mac, 16))
                .toByteArray()
        );
    }

    public void testDigest(int totalSize, int blockSize, @Nullable Provider provider) throws Exception {
        byte[] src = JieRandom.fill(new byte[totalSize]);
        {
            // Digest
            MessageDigest digest = JieCrypto.digest("MD5", provider);
            digest.reset();
            byte[] javaEn = digest.digest(src);
            digest.reset();
            byte[] jieEn = JieIO.processor(src).encoder(JieCrypto.encoder(digest, blockSize)).toByteArray();
            assertEquals(jieEn, javaEn);
        }
        {
            // Mac
            Mac mac = JieCrypto.mac("HmacSHA256", provider);
            KeyGenerator keyGenerator = JieCrypto.keyGenerator("HmacSHA256", provider);
            Key key = keyGenerator.generateKey();
            mac.init(key);
            byte[] javaEn = mac.doFinal(src);
            mac.init(key);
            byte[] jieEn = JieIO.processor(src).encoder(JieCrypto.encoder(mac, blockSize)).toByteArray();
            assertEquals(jieEn, javaEn);
        }
    }
}
