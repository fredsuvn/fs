//package test;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.fslabo.annotations.Nullable;
//import xyz.fslabo.common.base.JieString;
//import xyz.fslabo.common.codec.CipherCodec;
//import xyz.fslabo.common.codec.CodecConfigurator;
//import xyz.fslabo.common.codec.JieCodec;
//import xyz.fslabo.common.io.JieIO;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.Mac;
//import javax.crypto.SecretKey;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.security.*;
//import java.util.Arrays;
//
//public class CodecTest {
//
//    private static final Provider BC = new BouncyCastleProvider();
//
//    @Test
//    public void testCodec() throws Exception {
//        testCodecCipher(1111, "RSA", "RSA",
//            245, 256, 1280, true);
//        testCodecCipher(3, "RSA", "RSA/ECB/PKCS1Padding",
//            245, 256, 256, true);
//        testCodecCipher(33333, "RSA", "RSA/ECB/PKCS1Padding",
//            245, 256, 35072, true);
//        testCodecCipher(88888, "AES", "AES",
//            16, 32, 177776, false);
//        testCodecCipher(1, "AES", "AES",
//            16, 32, 16, false);
//        testCodecCipher(88888, "AES", "AES",
//            0, 0, 88896, false);
//        testCodecCipher(1, "AES", "AES",
//            0, 0, 16, false);
//        testCodecCipher(15, "AES", "AES",
//            0, 0, 16, false);
//        testCodecCipher(17, "AES", "AES",
//            0, 0, 32, false);
//    }
//
//    private void testCodecCipher(
//        int dataSize, String keyAlgorithm, String dataAlgorithm,
//        int enBlockSize, int deBlockSize, long enOutSize, boolean isAsymmetric
//    ) throws Exception {
//        byte[] data = JieString.encode(TestUtil.buildRandomString(dataSize, 0));
//        Cipher cipher = Cipher.getInstance(dataAlgorithm);
//        Key encryptKey;
//        Key decryptKey;
//        if (isAsymmetric) {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
//            KeyPair keyPair = keyPairGenerator.generateKeyPair();
//            encryptKey = keyPair.getPublic();
//            decryptKey = keyPair.getPrivate();
//        } else {
//            KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
//            SecretKey key = keyGenerator.generateKey();
//            encryptKey = key;
//            decryptKey = key;
//        }
//
//        //buffer -> buffer
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        System.out.println(cipher.getOutputSize(enBlockSize) + ", " + cipher.getBlockSize());
//        ByteBuffer inBuffer = ByteBuffer.wrap(data);
//        ByteBuffer outBuffer = ByteBuffer.allocate(decodeSize(cipher, data.length, deBlockSize));
//        long enResultSize = JieCodec.doCipher(cipher, inBuffer, outBuffer, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        outBuffer.flip();
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        System.out.println(cipher.getOutputSize(deBlockSize) + ", " + cipher.getBlockSize());
//        ByteBuffer comBuffer = ByteBuffer.allocate(decodeSize(cipher, data.length, deBlockSize));
//        long deResultSize = JieCodec.doCipher(cipher, outBuffer, comBuffer, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        comBuffer.flip();
//        Assert.assertEquals(JieIO.read(comBuffer), data);
//
//        //buffer -> stream
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        inBuffer = ByteBuffer.wrap(data);
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        enResultSize = JieCodec.doCipher(cipher, inBuffer, outStream, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        ByteArrayOutputStream comStream = new ByteArrayOutputStream();
//        deResultSize = JieCodec.doCipher(cipher, ByteBuffer.wrap(outStream.toByteArray()), comStream, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        Assert.assertEquals(comStream.toByteArray(), data);
//
//        //stream -> buffer
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
//        outBuffer.clear();
//        enResultSize = JieCodec.doCipher(cipher, inStream, outBuffer, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        outBuffer.flip();
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        comBuffer.clear();
//        deResultSize = JieCodec.doCipher(cipher, new ByteArrayInputStream(JieIO.read(outBuffer)), comBuffer, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        comBuffer.flip();
//        Assert.assertEquals(JieIO.read(comBuffer), data);
//
//        //stream -> stream
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        inStream = new ByteArrayInputStream(data);
//        outStream.reset();
//        enResultSize = JieCodec.doCipher(cipher, inStream, outStream, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        comStream.reset();
//        deResultSize = JieCodec.doCipher(cipher, ByteBuffer.wrap(outStream.toByteArray()), comStream, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        Assert.assertEquals(comStream.toByteArray(), data);
//    }
//
//    private int decodeSize(Cipher cipher, int dataSize, int deBlockSize) {
//        int max = Math.max(dataSize * 10, deBlockSize);
//        return Math.max(max, cipher.getBlockSize());
//    }
//
//    @Test
//    public void testCipher() throws Exception {
//        testCipherAsymmetric(150, 88, 256, "RSA", "RSA/ECB/PKCS1Padding", null);
//        testCipherAsymmetric(1500, 188, 256, "RSA", "RSA", null);
//        testCipherAsymmetric(1025, 222, 256, "RSA", "RSA", null);
//        testCipherSymmetric(150, 999, 9999, "AES", "AES", null);
//        testCipherSymmetric(1500, 16, 32, "AES", "AES", null);
//
//        testCipherAsymmetric(150, 88, 256, "RSA", "RSA/ECB/PKCS1Padding", BC);
//        testCipherAsymmetric(1500, 188, 256, "RSA", "RSA", BC);
//        testCipherAsymmetric(1025, 222, 256, "RSA", "RSA", BC);
//        testCipherSymmetric(150, 999, 9999, "AES", "AES", BC);
//        testCipherSymmetric(1500, 16, 32, "AES", "AES", BC);
//    }
//
//    private void testCipherAsymmetric(
//        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm, @Nullable Provider provider) throws Exception {
//        byte[] data = TestUtil.buildRandomBytes(dataSize);
//        KeyPairGenerator keyPairGenerator = JieCodec.keyPairGenerator(keyAlgorithm, provider);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//        CipherCodec cipher = JieCodec.cipher().algorithm(cryptoAlgorithm, provider);
//        byte[] enBytes = cipher.input(data).blockSize(enBlockSize).key(publicKey).encrypt().finalBytes();
//        byte[] deBytes = cipher.input(enBytes).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
//        Assert.assertEquals(data, deBytes);
//        enBytes = cipher.input(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(publicKey).encrypt().finalBytes();
//        deBytes = cipher.input(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
//        Assert.assertEquals(data, deBytes);
//        enBytes = cipher.input(JieIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().finalBytes();
//        deBytes = cipher.input(JieIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
//        Assert.assertEquals(data, deBytes);
//        enBytes = JieIO.read(
//            cipher.input(JieIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().finalStream());
//        deBytes = JieIO.read(
//            cipher.input(JieIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalStream());
//        Assert.assertEquals(data, deBytes);
//        byte[] enDest = new byte[dataSize * 10];
//        int destSize = (int) cipher.input(data, 2, data.length - 2).blockSize(enBlockSize).key(publicKey).encrypt().output(enDest, 1).doFinal();
//        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
//        deBytes = cipher.input(JieIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
//        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
//    }
//
//    private void testCipherSymmetric(
//        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm, @Nullable Provider provider) throws Exception {
//        byte[] data = TestUtil.buildRandomBytes(dataSize);
//        KeyGenerator keyGenerator = JieCodec.keyGenerator(keyAlgorithm, provider);
//        SecretKey key = keyGenerator.generateKey();
//        CipherCodec cipher = JieCodec.cipher().algorithm(cryptoAlgorithm, provider);
//        byte[] enBytes = cipher.input(data).blockSize(enBlockSize).key(key).encrypt().finalBytes();
//        byte[] deBytes = cipher.input(enBytes).blockSize(deBlockSize).key(key).decrypt().finalBytes();
//        Assert.assertEquals(data, deBytes);
//        enBytes = cipher.input(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(key).encrypt().finalBytes();
//        deBytes = cipher.input(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalBytes();
//        Assert.assertEquals(data, deBytes);
//        enBytes = cipher.input(JieIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().finalBytes();
//        deBytes = cipher.input(JieIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalBytes();
//        Assert.assertEquals(data, deBytes);
//        enBytes = JieIO.read(
//            cipher.input(JieIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().finalStream());
//        deBytes = JieIO.read(
//            cipher.input(JieIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalStream());
//        Assert.assertEquals(data, deBytes);
//        byte[] enDest = new byte[dataSize * 10];
//        int destSize = (int) cipher.input(data, 2, data.length - 2).blockSize(enBlockSize).key(key).encrypt().output(enDest, 1).doFinal();
//        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
//        deBytes = cipher.input(JieIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalBytes();
//        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
//    }
//
//    @Test
//    public void testDigest() throws Exception {
//        testDigest("MD5", 1111, null);
//        testDigest("MD5", 1, null);
//        testDigest("MD5", 256, null);
//        testDigest("MD5", 1111, BC);
//        testDigest("MD5", 1, BC);
//        testDigest("MD5", 256, BC);
//    }
//
//    private void testDigest(String algorithm, int size, @Nullable Provider provider) {
//        byte[] data = TestUtil.buildRandomBytes(size);
//        MessageDigest md = JieCodec.messageDigest(algorithm, provider);
//        byte[] mdBytes = md.digest(data);
//        System.out.println(mdBytes.length + ", " + md.getDigestLength());
//        byte[] bfBytes = JieCodec.doDigest(md, ByteBuffer.wrap(data));
//        Assert.assertEquals(mdBytes, bfBytes);
//        byte[] inBytes = JieCodec.doDigest(md, new ByteArrayInputStream(data));
//        Assert.assertEquals(mdBytes, inBytes);
//    }
//
//    @Test
//    public void testMac() throws InvalidKeyException {
//        testMac("HmacSHA256", 1111, null);
//        testMac("HmacSHA256", 1, null);
//        testMac("HmacSHA256", 256, null);
//        testMac("HmacSHA256", 1111, BC);
//        testMac("HmacSHA256", 1, BC);
//        testMac("HmacSHA256", 256, BC);
//    }
//
//    private void testMac(String algorithm, int size, @Nullable Provider provider) throws InvalidKeyException {
//        byte[] data = TestUtil.buildRandomBytes(size);
//        KeyGenerator keyGenerator = JieCodec.keyGenerator(algorithm, provider);
//        SecretKey secretKey = keyGenerator.generateKey();
//        Mac md = JieCodec.mac(algorithm, provider);
//        md.init(secretKey);
//        byte[] mdBytes = md.doFinal(data);
//        System.out.println(mdBytes.length + ", " + md.getMacLength());
//        byte[] bfBytes = JieCodec.doMac(md, ByteBuffer.wrap(data));
//        Assert.assertEquals(mdBytes, bfBytes);
//        byte[] inBytes = JieCodec.doMac(md, new ByteArrayInputStream(data));
//        Assert.assertEquals(mdBytes, inBytes);
//    }
//
//    @Test
//    public void testSign() throws Exception {
//        testSign("RSA", "SHA256withRSA", 1111, null);
//        testSign("RSA", "SHA256withRSA", 1, null);
//        testSign("RSA", "SHA256withRSA", 256, null);
//        testSign("RSA", "SHA256withRSA", 1111, BC);
//        testSign("RSA", "SHA256withRSA", 1, BC);
//        testSign("RSA", "SHA256withRSA", 256, BC);
//    }
//
//    private void testSign(String keyAlgorithm, String signAlgorithm, int size, @Nullable Provider provider) throws Exception {
//        byte[] data = TestUtil.buildRandomBytes(size);
//        KeyPairGenerator keyPairGenerator = JieCodec.keyPairGenerator(keyAlgorithm, provider);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//
//        //sign
//        Signature signature = JieCodec.signature(signAlgorithm, provider);
//        signature.initSign(privateKey);
//        signature.update(data);
//        byte[] signBytes = signature.sign();
//        System.out.println(signBytes.length);
//        signature.initSign(privateKey);
//        byte[] bfBytes = JieCodec.doSign(signature, ByteBuffer.wrap(data));
//        Assert.assertEquals(signBytes, bfBytes);
//        signature.initSign(privateKey);
//        byte[] inBytes = JieCodec.doSign(signature, new ByteArrayInputStream(data));
//        Assert.assertEquals(signBytes, inBytes);
//
//        //verify
//        signature.initVerify(publicKey);
//        signature.update(data);
//        Assert.assertTrue(signature.verify(signBytes));
//        signature.initVerify(publicKey);
//        Assert.assertTrue(JieCodec.doVerify(signature, ByteBuffer.wrap(data), signBytes));
//        signature.initVerify(publicKey);
//        Assert.assertTrue(JieCodec.doVerify(signature, new ByteArrayInputStream(data), signBytes));
//    }
//
//    @Test
//    public void testEncodeCodec() {
//        testEncodeCodec(JieCodec.base64(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH", true);
//        testEncodeCodec(JieCodec.base64().decode(), "MTIzNDU25Lit5paH5Lit5paH", "123456中文中文", false);
//        testEncodeCodec(JieCodec.hex(), "123456中文中文", "313233343536E4B8ADE69687E4B8ADE69687", true);
//        testEncodeCodec(JieCodec.hex().decode(), "313233343536E4B8ADE69687E4B8ADE69687", "123456中文中文", false);
//    }
//
//    private void testEncodeCodec(
//        CodecConfigurator<?> codec, String source, String dest, boolean encode) {
//        byte[] srcBytes = encode ? JieString.encode(source) : JieString.encode(source, StandardCharsets.ISO_8859_1);
//        byte[] destBytes = encode ? JieString.encode(dest, StandardCharsets.ISO_8859_1) : JieString.encode(dest);
//        byte[] srcBytesPadding = padBytes(srcBytes, 10);
//        byte[] destBytesPadding = padBytes(destBytes, 10);
//
//        byte[] finalBytes = new byte[1024];
//
//        //simple byte[]
//        int size = codec.input(srcBytes).output(finalBytes).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(srcBytes).output(destBytesPadding, 10).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(srcBytes).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(srcBytes).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(srcBytes).output(JieIO.toOutputStream(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        //byte[]
//        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(finalBytes).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(destBytesPadding, 10).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(JieIO.toOutputStream(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        //simple buffer
//        size = codec.input(ByteBuffer.wrap(srcBytes)).output(finalBytes).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytes)).output(destBytesPadding, 10).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytes)).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytes)).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytes)).output(JieIO.toOutputStream(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        //buffer
//        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(finalBytes).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(destBytesPadding, 10).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(JieIO.toOutputStream(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        //stream
//        size = codec.input(JieIO.toInputStream(srcBytes)).output(finalBytes).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(JieIO.toInputStream(srcBytes)).output(destBytesPadding, 10).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(JieIO.toInputStream(srcBytes)).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        size = codec.input(JieIO.toInputStream(srcBytes)).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
//            destBytes
//        );
//        size = codec.input(JieIO.toInputStream(srcBytes)).output(JieIO.toOutputStream(finalBytes)).doFinalInt();
//        Assert.assertEquals(
//            Arrays.copyOfRange(finalBytes, 0, size),
//            destBytes
//        );
//        //final
//        Assert.assertEquals(
//            codec.input(srcBytes).finalBytes(),
//            destBytes
//        );
//        Assert.assertEquals(
//            JieIO.read(codec.input(srcBytes).finalStream()),
//            destBytes
//        );
//        Assert.assertEquals(
//            codec.input(srcBytes).finalString(),
//            dest
//        );
//        if (encode) {
//            Assert.assertEquals(
//                codec.input(srcBytes).finalLatin(),
//                dest
//            );
//        }
//    }
//
//    private byte[] padBytes(byte[] src, int paddingSize) {
//        byte[] result = new byte[src.length + paddingSize * 2];
//        System.arraycopy(src, 0, result, paddingSize, src.length);
//        return result;
//    }
//}
