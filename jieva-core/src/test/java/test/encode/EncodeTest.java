package test.encode;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.encode.*;
import xyz.sunqian.common.io.ByteStream;
import xyz.sunqian.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Base64;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class EncodeTest {

    @Test
    public void testBase64() throws Exception {
        for (int i = 0; i < 10; i++) {
            testBase64(i);
        }
        testBase64(47);
        testBase64(48);
        testBase64(49);
        testBase64(1139);
        testBase64(1140);
        testBase64(1141);
        testBase64(384 * 3 - 1);
        testBase64(384 * 3);
        testBase64(384 * 3 + 1);
        testBase64(10086);
        testBase64(99);
        testBase64(JieIO.BUFFER_SIZE);
        testBase64(JieIO.BUFFER_SIZE + 10086);
        testBase64(57);
        testBase64(1024);
        testBase64(1024 * 1024);

        // error
        expectThrows(EncodingException.class, () -> JieBase64.encoder().getOutputSize(-1));
        expectThrows(DecodingException.class, () -> JieBase64.decoder().getOutputSize(-1));
        expectThrows(EncodingException.class, () -> JieBase64.separationEncoder(-1, new byte[1], true, true));
        expectThrows(EncodingException.class, () -> JieBase64.separationEncoder(5, new byte[1], true, true));

        // pem without padding
        testPemBase64();
    }

    private void testBase64(int size) throws Exception {
        byte[] source = JieRandom.fill(new byte[size]);
        testBase64Jdk(source, JieBase64.encoder(true), Base64.getEncoder());
        testBase64Jdk(source, JieBase64.urlEncoder(true), Base64.getUrlEncoder());
        testBase64Jdk(source, JieBase64.mimeEncoder(true), Base64.getMimeEncoder());
        testBase64Apache(
            source,
            JieBase64.pemEncoder(true),
            new org.apache.commons.codec.binary.Base64(64)
        );
        testBase64Jdk(source, JieBase64.encoder(false), Base64.getEncoder().withoutPadding());
        testBase64Jdk(source, JieBase64.urlEncoder(false), Base64.getUrlEncoder().withoutPadding());
        testBase64Jdk(source, JieBase64.mimeEncoder(false), Base64.getMimeEncoder().withoutPadding());
        testBase64Jdk(
            source,
            JieBase64.separationEncoder(16, new byte[]{'\t'}, true, false),
            Base64.getMimeEncoder(16, new byte[]{'\t'})
        );
        testBase64Jdk(
            source,
            JieBase64.separationEncoder(16, new byte[]{'\t'}, false, false),
            Base64.getMimeEncoder(16, new byte[]{'\t'}).withoutPadding()
        );
        testBase64Jdk(
            source,
            JieBase64.separationEncoder(4, new byte[]{'\t'}, true, false),
            Base64.getMimeEncoder(4, new byte[]{'\t'})
        );
        testBase64Jdk(
            source,
            JieBase64.separationEncoder(4, new byte[]{'\t'}, false, false),
            Base64.getMimeEncoder(4, new byte[]{'\t'}).withoutPadding()
        );
        testBase64Jdk(
            source,
            JieBase64.separationEncoder(400, new byte[]{'\t', '\r'}, true, false),
            Base64.getMimeEncoder(400, new byte[]{'\t', '\r'})
        );
        testBase64Jdk(
            source,
            JieBase64.separationEncoder(400, new byte[]{'\t', '\r'}, false, false),
            Base64.getMimeEncoder(400, new byte[]{'\t', '\r'}).withoutPadding()
        );
        testBase64Apache(
            source,
            JieBase64.separationEncoder(16, new byte[]{'\t'}, true, true),
            new org.apache.commons.codec.binary.Base64(16, new byte[]{'\t'})
        );
        testBase64Apache(
            source,
            JieBase64.separationEncoder(4, new byte[]{'\t'}, true, true),
            new org.apache.commons.codec.binary.Base64(4, new byte[]{'\t'})
        );
        testBase64Apache(
            source,
            JieBase64.separationEncoder(400, new byte[]{'\t'}, true, true),
            new org.apache.commons.codec.binary.Base64(400, new byte[]{'\t'})
        );
        testBase64Apache(
            source,
            JieBase64.separationEncoder(16, new byte[0], true, false),
            new org.apache.commons.codec.binary.Base64(16, new byte[0])
        );
        testBase64Apache(
            source,
            JieBase64.separationEncoder(4, new byte[0], true, false),
            new org.apache.commons.codec.binary.Base64(4, new byte[0])
        );
        testBase64Apache(
            source,
            JieBase64.separationEncoder(400, new byte[0], true, false),
            new org.apache.commons.codec.binary.Base64(400, new byte[0])
        );
    }

    private void testBase64Jdk(
        byte[] data,
        JieBase64.Encoder encoder,
        Base64.Encoder jdkEncoder
    ) throws Exception {

        assertEquals(encoder.encode(data), jdkEncoder.encode(data));
        assertEquals(encoder.toString(data), jdkEncoder.encodeToString(data));
        assertEquals(encoder.toString(ByteBuffer.wrap(data)), jdkEncoder.encodeToString(data));

        {
            // wrap
            ByteBuffer b1 = encoder.encode(ByteBuffer.wrap(data));
            ByteBuffer b2 = jdkEncoder.encode(ByteBuffer.wrap(data));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }

        {
            // array offset
            ByteBuffer b1 = encoder.encode(TU.bufferDangling(data));
            ByteBuffer b2 = jdkEncoder.encode(TU.bufferDangling(data));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }


        {
            // direct
            ByteBuffer b1 = encoder.encode(JieBytes.copyBuffer(data, true));
            ByteBuffer b2 = jdkEncoder.encode(JieBytes.copyBuffer(data, true));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }

        {
            // byte[] -> byte[]
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(data, dest);
            assertEquals(dest, jdkEncoder.encode(data));
        }

        {
            // buffer -> buffer
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(dest));
            assertEquals(dest, jdkEncoder.encode(data));
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(JieBytes.copyBuffer(data, true), ByteBuffer.wrap(dest));
            assertEquals(dest, jdkEncoder.encode(data));
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(TU.bufferDangling(data), ByteBuffer.wrap(dest));
            assertEquals(dest, jdkEncoder.encode(data));
            ByteBuffer destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(JieBytes.copyBuffer(data, true), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), jdkEncoder.encode(data));
            destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(TU.bufferDangling(data), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), jdkEncoder.encode(data));
        }

        {
            // stream
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            OutputStream out = jdkEncoder.wrap(bytesOut);
            out.write(data);
            out.close();
            ByteArrayOutputStream bytesOut2 = new ByteArrayOutputStream();
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2).encoder(encoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2).blockSize(57).encoder(encoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2).blockSize(570).encoder(encoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            for (int i = 1; i < 10; i++) {
                {
                    bytesOut2.reset();
                    ByteStream byteStream = ByteStream.from(data).to(bytesOut2).blockSize(i).encoder(encoder.streamEncoder());
                    int readNum = (int) byteStream.start();
                    assertEquals(readNum, data.length == 0 ? -1 : data.length);
                    assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
                }
            }
        }

        {
            // error
            if (data.length > 0) {
                expectThrows(EncodingException.class, () -> encoder.encode(data, new byte[0]));
                expectThrows(EncodingException.class, () -> encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(new byte[0])));
            }
        }
    }

    private void testBase64Apache(
        byte[] data,
        JieBase64.Encoder encoder,
        org.apache.commons.codec.binary.Base64 apacheEncoder
    ) throws Exception {

        assertEquals(encoder.encode(data), apacheEncoder.encode(data));
        assertEquals(encoder.toString(data), apacheEncoder.encodeToString(data));
        assertEquals(encoder.toString(ByteBuffer.wrap(data)), apacheEncoder.encodeToString(data));

        {
            // wrap
            ByteBuffer b1 = encoder.encode(ByteBuffer.wrap(data));
            ByteBuffer b2 = ByteBuffer.wrap(apacheEncoder.encode(data));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }

        {
            // array offset
            ByteBuffer b1 = encoder.encode(TU.bufferDangling(data));
            ByteBuffer b2 = ByteBuffer.wrap(apacheEncoder.encode(JieBytes.copyBytes(TU.bufferDangling(data))));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }


        {
            // direct
            ByteBuffer b1 = encoder.encode(JieBytes.copyBuffer(data, true));
            ByteBuffer b2 = ByteBuffer.wrap(apacheEncoder.encode(JieBytes.copyBytes(JieBytes.copyBuffer(data, true))));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }

        {
            // byte[] -> byte[]
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(data, dest);
            assertEquals(dest, apacheEncoder.encode(data));
        }

        {
            // buffer -> buffer
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(dest));
            assertEquals(dest, apacheEncoder.encode(data));
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(JieBytes.copyBuffer(data, true), ByteBuffer.wrap(dest));
            assertEquals(dest, apacheEncoder.encode(data));
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(TU.bufferDangling(data), ByteBuffer.wrap(dest));
            assertEquals(dest, apacheEncoder.encode(data));
            ByteBuffer destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(JieBytes.copyBuffer(data, true), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), apacheEncoder.encode(data));
            destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(TU.bufferDangling(data), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), apacheEncoder.encode(data));
        }

        {
            // error
            if (data.length > 0) {
                expectThrows(EncodingException.class, () -> encoder.encode(data, new byte[0]));
                expectThrows(EncodingException.class, () -> encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(new byte[0])));
            }
        }
    }

    private void testPemBase64() {
        // pem without padding
        {
            // 47
            byte[] pemSrc = new byte[47];
            byte[] pemEn = JieBase64.pemEncoder(false).encode(pemSrc);
            assertEquals(pemEn.length, 65);
            byte[] pemApacheEn = new org.apache.commons.codec.binary.Base64(64).encode(pemSrc);
            assertEquals(pemApacheEn.length, 66);
            assertEquals(Arrays.copyOf(pemEn, 63), Arrays.copyOf(pemApacheEn, 63));
        }
        {
            // 48
            byte[] pemSrc = new byte[48];
            byte[] pemEn = JieBase64.pemEncoder(false).encode(pemSrc);
            assertEquals(pemEn.length, 66);
            byte[] pemApacheEn = new org.apache.commons.codec.binary.Base64(64).encode(pemSrc);
            assertEquals(pemApacheEn.length, 66);
            assertEquals(Arrays.copyOf(pemEn, 66), Arrays.copyOf(pemApacheEn, 66));
        }
        {
            // 49
            byte[] pemSrc = new byte[49];
            byte[] pemEn = JieBase64.pemEncoder(false).encode(pemSrc);
            assertEquals(pemEn.length, 70);
            byte[] pemApacheEn = new org.apache.commons.codec.binary.Base64(64).encode(pemSrc);
            assertEquals(pemApacheEn.length, 72);
            assertEquals(Arrays.copyOf(pemEn, 68), Arrays.copyOf(pemApacheEn, 68));
        }
    }

    @Test
    public void testHex() throws Exception {
        for (int i = 0; i < 10; i++) {
            testHex(i);
        }
        testHex(1139);
        testHex(1140);
        testHex(1141);
        testHex(384 * 3 - 1);
        testHex(384 * 3);
        testHex(384 * 3 + 1);
        testHex(10086);
        testHex(99);
        testHex(JieIO.BUFFER_SIZE);
        testHex(JieIO.BUFFER_SIZE + 10086);
        testHex(57);
        testHex(1024);
        testHex(1024 * 1024);

        // error
        expectThrows(EncodingException.class, () -> JieHex.encoder().getOutputSize(-1));
        expectThrows(DecodingException.class, () -> JieHex.decoder().getOutputSize(-1));
        byte[] encoded = new byte[3];
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded));
        byte[] encoded2 = new byte[2];
        encoded2[0] = '0' - 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = '9' + 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'A' - 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'F' + 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'a' - 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'f' + 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
    }

    private void testHex(int size) throws Exception {
        byte[] source = JieRandom.fill(new byte[size]);
        testHex(source, JieHex.encoder(), JieHex.decoder());
    }

    private void testHex(byte[] data, JieHex.Encoder encoder, JieHex.Decoder decoder) throws Exception {

        String apache = Hex.encodeHexString(data, false);
        byte[] aBytes = apache.getBytes(JieChars.latinCharset());
        assertEquals(encoder.encode(data), aBytes);
        assertEquals(encoder.toString(data), apache);
        assertEquals(encoder.toString(ByteBuffer.wrap(data)), apache);
        assertEquals(decoder.decode(aBytes), data);

        {
            // wrap
            ByteBuffer b1 = encoder.encode(ByteBuffer.wrap(data));
            ByteBuffer b2 = ByteBuffer.wrap(aBytes);
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
            ByteBuffer d1 = decoder.decode(ByteBuffer.wrap(aBytes));
            ByteBuffer d2 = ByteBuffer.wrap(data);
            assertEquals(d1, d2);
            assertEquals(JieBytes.copyBytes(d1), JieBytes.copyBytes(d2));
        }

        {
            // array offset
            ByteBuffer b1 = encoder.encode(TU.bufferDangling(data));
            ByteBuffer b2 = ByteBuffer.wrap(aBytes);
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
            ByteBuffer d1 = decoder.decode(TU.bufferDangling(aBytes));
            ByteBuffer d2 = ByteBuffer.wrap(data);
            assertEquals(d1, d2);
            assertEquals(JieBytes.copyBytes(d1), JieBytes.copyBytes(d2));
        }


        {
            // direct
            ByteBuffer b1 = encoder.encode(JieBytes.copyBuffer(data, true));
            ByteBuffer b2 = ByteBuffer.wrap(aBytes);
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
            ByteBuffer d1 = decoder.decode(JieBytes.copyBuffer(aBytes, true));
            ByteBuffer d2 = ByteBuffer.wrap(data);
            assertEquals(d1, d2);
            assertEquals(JieBytes.copyBytes(d1), JieBytes.copyBytes(d2));
        }

        {
            // byte[] -> byte[]
            byte[] eDest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(data, eDest);
            assertEquals(eDest, aBytes);
            byte[] dDest = new byte[decoder.getOutputSize(aBytes.length)];
            decoder.decode(aBytes, dDest);
            assertEquals(dDest, data);
        }

        {
            // buffer -> buffer
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(dest));
            assertEquals(dest, aBytes);
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(JieBytes.copyBuffer(data, true), ByteBuffer.wrap(dest));
            assertEquals(dest, aBytes);
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(TU.bufferDangling(data), ByteBuffer.wrap(dest));
            assertEquals(dest, aBytes);
            ByteBuffer destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(JieBytes.copyBuffer(data, true), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), aBytes);
            destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(TU.bufferDangling(data), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), aBytes);

            dest = new byte[decoder.getOutputSize(aBytes.length)];
            decoder.decode(ByteBuffer.wrap(aBytes), ByteBuffer.wrap(dest));
            assertEquals(dest, data);
            dest = new byte[decoder.getOutputSize(aBytes.length)];
            decoder.decode(JieBytes.copyBuffer(aBytes, true), ByteBuffer.wrap(dest));
            assertEquals(dest, data);
            dest = new byte[decoder.getOutputSize(aBytes.length)];
            decoder.decode(TU.bufferDangling(aBytes), ByteBuffer.wrap(dest));
            assertEquals(dest, data);
            destBuffer = ByteBuffer.allocateDirect(decoder.getOutputSize(aBytes.length));
            decoder.decode(JieBytes.copyBuffer(aBytes, true), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), data);
            destBuffer = ByteBuffer.allocateDirect(decoder.getOutputSize(aBytes.length));
            decoder.decode(TU.bufferDangling(aBytes), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), data);
        }

        {
            // stream
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            bytesOut.write(aBytes);
            ByteArrayOutputStream bytesOut2 = new ByteArrayOutputStream();
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2).blockSize(encoder.getBlockSize()).encoder(encoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2).blockSize(encoder.getBlockSize() * 2).encoder(encoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2).blockSize(encoder.getBlockSize() * 20).encoder(encoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }

            bytesOut = new ByteArrayOutputStream();
            bytesOut.write(data);
            bytesOut2 = new ByteArrayOutputStream();
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(aBytes).to(bytesOut2).blockSize(decoder.getBlockSize()).encoder(decoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, aBytes.length == 0 ? -1 : aBytes.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(aBytes).to(bytesOut2).blockSize(decoder.getBlockSize() * 2).encoder(decoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, aBytes.length == 0 ? -1 : aBytes.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(aBytes).to(bytesOut2).blockSize(decoder.getBlockSize() * 20).encoder(decoder.streamEncoder());
                int readNum = (int) byteStream.start();
                assertEquals(readNum, aBytes.length == 0 ? -1 : aBytes.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
        }

        {
            // error
            if (data.length > 0) {
                expectThrows(EncodingException.class, () -> encoder.encode(data, new byte[0]));
                expectThrows(EncodingException.class, () -> encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(new byte[0])));
            }
            if (aBytes.length > 0) {
                expectThrows(DecodingException.class, () -> decoder.decode(aBytes, new byte[0]));
                expectThrows(DecodingException.class, () -> decoder.decode(ByteBuffer.wrap(aBytes), ByteBuffer.wrap(new byte[0])));
            }
        }
    }

    @Test
    public void testToChars() throws Exception {
        String s = "0123456789ABCDEFabcdef";
        assertEquals(JieHex.decoder().decode(s), Hex.decodeHex(s));
        assertEquals(JieHex.decoder().decode(s.toCharArray()), Hex.decodeHex(s));
        CharBuffer cb = CharBuffer.wrap(s);
        assertEquals(JieBytes.copyBytes(JieHex.decoder().decode(cb)), Hex.decodeHex(s));
        assertEquals(cb.position(), s.length());
    }

    // @Test
    // public void test0() throws Exception {
    //     byte[] src = JieRandom.fill(new byte[58]);
    //     byte[] enSrc = org.apache.commons.codec.binary.Base64.encodeBase64(src, true);
    //     System.out.println(new String(enSrc));
    //     byte[] xenSrc = new byte[enSrc.length + 2];
    //     System.arraycopy(enSrc, 0, xenSrc, 0, enSrc.length);
    //     xenSrc[enSrc.length] = '(';
    //     xenSrc[enSrc.length + 1] = ')';
    //     System.out.println(new String(xenSrc));
    //     byte[] deSrc = org.apache.commons.codec.binary.Base64.decodeBase64(xenSrc);
    //     assertEquals(deSrc, src);
    //     deSrc = Base64.getMimeDecoder().decode(xenSrc);
    //     assertEquals(deSrc, src);
    // }

    // jdk encode: 876
    // jie encode: 1001
    // jdk decode: 1486
    // jie decode: 1610
    // java direct: 975
    // jie direct: 1138
    // jie encode direct buffer: 1130
    // jie encode heap buffer: 974
    // java encode stream: 4148
    // jie encode stream (570): 1376
    // jie encode stream (30): 2603
    // jie encode stream (577): 1464
    // jie encode stream (567): 1250
    // jie encode stream (570): 1241
    //@Test
    public void testBase64Performance() throws Exception {
        int times = 10000;
        byte[] source = JieRandom.fill(new byte[99999]);
        ByteBuffer sb = ByteBuffer.wrap(source);
        ByteEncoder encoder = JieBase64.encoder();
        ByteDecoder decoder = JieBase64.decoder();
        Base64.Encoder be = Base64.getEncoder();
        Base64.Decoder de = Base64.getDecoder();
        byte[] deBytes = be.encode(source);
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            be.encode(sb);
            sb.flip();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("jdk encode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            encoder.encode(sb);
            sb.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            de.decode(deBytes);
        }
        t2 = System.currentTimeMillis();
        System.out.println("jdk decode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            decoder.decode(deBytes);
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie decode: " + (t2 - t1));
        sb = ByteBuffer.allocateDirect(source.length);
        sb.put(source);
        sb.flip();
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            be.encode(sb);
            sb.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("java direct: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            encoder.encode(sb);
            sb.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie direct: " + (t2 - t1));

        ByteBuffer dest = ByteBuffer.allocate(133332);
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            encoder.encode(sb, dest);
            sb.flip();
            dest.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode direct buffer: " + (t2 - t1));
        sb = ByteBuffer.wrap(source);
        dest = ByteBuffer.allocate(133332);
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            encoder.encode(sb, dest);
            sb.flip();
            dest.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode heap buffer: " + (t2 - t1));

        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            OutputStream out = Base64.getEncoder().wrap(new ByteArrayOutputStream());
            out.write(source);
        }
        t2 = System.currentTimeMillis();
        System.out.println("java encode stream: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream()).blockSize(570).encoder(encoder.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode stream (570): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream()).blockSize(30).encoder(encoder.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode stream (30): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream()).blockSize(577).encoder(encoder.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode stream (577): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream()).blockSize(567).encoder(encoder.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode stream (567): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream()).blockSize(570).encoder(encoder.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie encode stream (570): " + (t2 - t1));
    }

    // jie hex encode: 839
    // apache hex encode: 2745
    // jie hex decode: 6009
    // apache hex decode: 8705
    // jie hex decode stream: 6050
    // jie hex decode stream(99999): 6138
    // jie hex decode stream(2): 37468
    //@Test
    public void testHexPerformance() throws Exception {
        int times = 10000;
        byte[] source = JieRandom.fill(new byte[99999]);
        ByteEncoder hexEn = JieHex.encoder();
        ByteDecoder hexDe = JieHex.decoder();
        Hex hex = new Hex();
        byte[] hexEncoded = hex.encode(source);
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            hexEn.encode(source);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("jie hex encode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            hex.encode(source);
        }
        t2 = System.currentTimeMillis();
        System.out.println("apache hex encode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            hexDe.decode(hexEncoded);
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie hex decode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            hex.decode(hexEncoded);
        }
        t2 = System.currentTimeMillis();
        System.out.println("apache hex decode: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(hexEncoded).to(new ByteArrayOutputStream()).encoder(hexDe.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie hex decode stream: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(hexEncoded).to(new ByteArrayOutputStream()).blockSize(99999).encoder(hexDe.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie hex decode stream(99999): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(hexEncoded).to(new ByteArrayOutputStream()).blockSize(hexDe.getBlockSize()).encoder(hexDe.streamEncoder());
            bs.start();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie hex decode stream(2): " + (t2 - t1));
    }
}
