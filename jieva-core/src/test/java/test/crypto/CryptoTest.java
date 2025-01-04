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
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
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
        Cipher cipher = Cipher.getInstance("AES");
        expectThrows(IOEncodingException.class, () ->
            JieIO.processor(new byte[10086])
                .encoder(JieCrypto.cipherEncoder(cipher, 16, true))
                .toByteArray()
        );
    }

    public void testCipher(int totalSize, @Nullable Provider provider) throws Exception {
        {
            // AES
            KeyGenerator keyGenerator = provider == null ?
                KeyGenerator.getInstance("AES") : KeyGenerator.getInstance("AES", provider);
            keyGenerator.init(256);
            Key key = keyGenerator.generateKey();
            Cipher cipher = provider == null ?
                Cipher.getInstance("AES") : Cipher.getInstance("AES", provider);
            testCipher(totalSize, 16, 16, cipher, key, key);
        }
        {
            // RSA
            KeyPairGenerator keyPairGenerator = provider == null ?
                KeyPairGenerator.getInstance("RSA") : KeyPairGenerator.getInstance("RSA", provider);
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Cipher cipher = provider == null ?
                Cipher.getInstance("RSA/ECB/PKCS1Padding")
                :
                Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
            testCipher(totalSize, -245, -256, cipher, keyPair.getPublic(), keyPair.getPrivate());
        }
    }

    private void testCipher(
        int totalSize, int enBlock, int deBlock, Cipher cipher, Key enKey, Key deKey
    ) throws Exception {
        byte[] src = JieRandom.fill(new byte[totalSize]);
        // en
        cipher.init(Cipher.ENCRYPT_MODE, enKey);
        byte[] cipherEn = doCipher(src, enBlock, cipher);
        cipher.init(Cipher.ENCRYPT_MODE, enKey);
        byte[] jieEn = JieIO.processor(src)
            .encoder(JieCrypto.cipherEncoder(cipher, Math.abs(enBlock), enBlock <= 0)).toByteArray();
        assertEquals(jieEn.length, cipherEn.length);

        // de
        cipher.init(Cipher.DECRYPT_MODE, deKey);
        byte[] jieDe = JieIO.processor(cipherEn)
            .encoder(((data, end) -> {
                ByteBuffer ret = ByteBuffer.allocateDirect(data.remaining());
                ret.put(data);
                ret.flip();
                return ret;
            }))
            .encoder(JieCrypto.cipherEncoder(cipher, Math.abs(deBlock), deBlock <= 0)).toByteArray();
        assertEquals(jieDe, src);
        cipher.init(Cipher.DECRYPT_MODE, deKey);
        byte[] cipherDe = doCipher(cipherEn, deBlock, cipher);
        assertEquals(cipherDe, src);
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
}
