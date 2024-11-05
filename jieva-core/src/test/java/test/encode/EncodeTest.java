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
import java.util.Base64;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class EncodeTest {

    @Test
    public void testBase64() throws Exception {
        testBase64(1024 * 1024);
        testBase64(1139);
        testBase64(1140);
        testBase64(1141);
        testBase64(384 * 3 - 1);
        testBase64(384 * 3);
        testBase64(384 * 3 + 1);
        testBase64(10086);
        testBase64(99);
        for (int i = 0; i < 10; i++) {
            testBase64(i);
        }
        testBase64(JieIO.BUFFER_SIZE);
        testBase64(JieIO.BUFFER_SIZE + 10086);
        testBase64(57);
        testBase64(1024);
    }

    private void testBase64(int size) throws Exception {
        byte[] source = JieRandom.fill(new byte[size]);
        testBase64(source, JieBase64.encoder(true), Base64.getEncoder());
        testBase64(source, JieBase64.urlEncoder(true), Base64.getUrlEncoder());
        testBase64(source, JieBase64.mimeEncoder(true), Base64.getMimeEncoder());
        testBase64(source, JieBase64.encoder(false), Base64.getEncoder().withoutPadding());
        testBase64(source, JieBase64.urlEncoder(false), Base64.getUrlEncoder().withoutPadding());
        testBase64(source, JieBase64.mimeEncoder(false), Base64.getMimeEncoder().withoutPadding());
        testBase64(
            source,
            JieBase64.mimeEncoder(16, new byte[]{'\t'}, true),
            Base64.getMimeEncoder(16, new byte[]{'\t'})
        );
        testBase64(
            source,
            JieBase64.mimeEncoder(16, new byte[]{'\t'}, false),
            Base64.getMimeEncoder(16, new byte[]{'\t'}).withoutPadding()
        );
        testBase64(
            source,
            JieBase64.mimeEncoder(4, new byte[]{'\t'}, true),
            Base64.getMimeEncoder(4, new byte[]{'\t'})
        );
        testBase64(
            source,
            JieBase64.mimeEncoder(4, new byte[]{'\t'}, false),
            Base64.getMimeEncoder(4, new byte[]{'\t'}).withoutPadding()
        );
        testBase64(
            source,
            JieBase64.mimeEncoder(400, new byte[]{'\t', '\r'}, true),
            Base64.getMimeEncoder(400, new byte[]{'\t', '\r'})
        );
        testBase64(
            source,
            JieBase64.mimeEncoder(400, new byte[]{'\t', '\r'}, false),
            Base64.getMimeEncoder(400, new byte[]{'\t', '\r'}).withoutPadding()
        );
    }

    private void testBase64(byte[] data, ToCharEncoder encoder, Base64.Encoder be) throws Exception {

        assertEquals(encoder.encode(data), be.encode(data));
        assertEquals(encoder.toString(data), be.encodeToString(data));
        assertEquals(encoder.toString(ByteBuffer.wrap(data)), be.encodeToString(data));

        {
            // wrap
            ByteBuffer b1 = encoder.encode(ByteBuffer.wrap(data));
            ByteBuffer b2 = be.encode(ByteBuffer.wrap(data));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }

        {
            // array offset
            ByteBuffer b1 = encoder.encode(TU.bufferDangling(data));
            ByteBuffer b2 = be.encode(TU.bufferDangling(data));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }


        {
            // direct
            ByteBuffer b1 = encoder.encode(JieBytes.copyBuffer(data, true));
            ByteBuffer b2 = be.encode(JieBytes.copyBuffer(data, true));
            assertEquals(b1, b2);
            assertEquals(JieBytes.copyBytes(b1), JieBytes.copyBytes(b2));
        }

        {
            // byte[] -> byte[]
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(data, dest);
            assertEquals(dest, be.encode(data));
        }

        {
            // buffer -> buffer
            byte[] dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(dest));
            assertEquals(dest, be.encode(data));
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(JieBytes.copyBuffer(data, true), ByteBuffer.wrap(dest));
            assertEquals(dest, be.encode(data));
            dest = new byte[encoder.getOutputSize(data.length)];
            encoder.encode(TU.bufferDangling(data), ByteBuffer.wrap(dest));
            assertEquals(dest, be.encode(data));
            ByteBuffer destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(JieBytes.copyBuffer(data, true), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), be.encode(data));
            destBuffer = ByteBuffer.allocateDirect(encoder.getOutputSize(data.length));
            encoder.encode(TU.bufferDangling(data), destBuffer);
            destBuffer.flip();
            assertEquals(JieBytes.copyBytes(destBuffer), be.encode(data));
        }

        {
            // stream
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            OutputStream out = be.wrap(bytesOut);
            out.write(data);
            out.close();
            ByteArrayOutputStream bytesOut2 = new ByteArrayOutputStream();
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize()).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() * 2).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() * 20).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                int blockSize;
                if (encoder.getClass().getName().contains("Mime")) {
                    blockSize = encoder.getBlockSize() / 20;
                } else {
                    blockSize = 3;
                }
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(blockSize).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
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

    @Test
    public void testHex() throws Exception {
        testHex(1024 * 1024);
        testHex(1139);
        testHex(1140);
        testHex(1141);
        testHex(384 * 3 - 1);
        testHex(384 * 3);
        testHex(384 * 3 + 1);
        testHex(10086);
        testHex(99);
        for (int i = 0; i < 10; i++) {
            testHex(i);
        }
        testHex(JieIO.BUFFER_SIZE);
        testHex(JieIO.BUFFER_SIZE + 10086);
        testHex(57);
        testHex(1024);

        // error
        byte[] encoded = new byte[3];
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded));
        byte[] encoded2 = new byte[2];
        encoded2[0] = 'G';
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));

        // coverage
        String s = "0123456789ABCDEF";
        assertEquals(JieHex.decoder().decode(s), Hex.decodeHex(s));
        assertEquals(JieHex.decoder().decode(s.toCharArray()), Hex.decodeHex(s));
        CharBuffer cb = CharBuffer.wrap(s);
        assertEquals(JieBytes.copyBytes(JieHex.decoder().decode(cb)), Hex.decodeHex(s));
        assertEquals(cb.position(), s.length());
    }

    private void testHex(int size) throws Exception {
        byte[] source = JieRandom.fill(new byte[size]);
        testHex(source, JieHex.encoder(), JieHex.decoder());
    }

    private void testHex(byte[] data, ToCharEncoder encoder, ToCharDecoder decoder) throws Exception {

        String apache = Hex.encodeHexString(data, false);
        byte[] aBytes = apache.getBytes(JieChars.latinCharset());
        assertEquals(encoder.encode(data), aBytes);
        assertEquals(encoder.toString(data), apache);
        assertEquals(encoder.toString(ByteBuffer.wrap(data)), apache);
        assertEquals(decoder.decode(apache), data);

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
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize()).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() * 2).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() * 20).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }

            bytesOut = new ByteArrayOutputStream();
            bytesOut.write(data);
            bytesOut2 = new ByteArrayOutputStream();
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(aBytes).to(bytesOut2)
                    .blockSize(decoder.getBlockSize()).encoder(decoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, aBytes.length == 0 ? -1 : aBytes.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(aBytes).to(bytesOut2)
                    .blockSize(decoder.getBlockSize() * 2).encoder(decoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, aBytes.length == 0 ? -1 : aBytes.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(aBytes).to(bytesOut2)
                    .blockSize(decoder.getBlockSize() * 20).encoder(decoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
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

    //@Test
    public void testBase64Performance() throws Exception {
        int times = 10000;
        byte[] source = JieRandom.fill(new byte[99999]);
        ByteBuffer sb = ByteBuffer.wrap(source);
        ByteEncoder encoder = JieBase64.encoder();
        Base64.Encoder be = Base64.getEncoder();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            be.encode(sb);
            sb.flip();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("java array: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            encoder.encode(sb);
            sb.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie array: " + (t2 - t1));
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
        System.out.println("jie buffer dest dir: " + (t2 - t1));
        sb = ByteBuffer.wrap(source);
        dest = ByteBuffer.allocate(133332);
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            encoder.encode(sb, dest);
            sb.flip();
            dest.flip();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie buffer dest: " + (t2 - t1));

        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            OutputStream out = Base64.getEncoder().wrap(new ByteArrayOutputStream());
            out.write(source);
        }
        t2 = System.currentTimeMillis();
        System.out.println("java out: " + (t2 - t1));
        // jie out: 1422
        // jie out (block:1): 20735
        // jie out (block:1024): 1516
        // jie out (block:10240): 1465
        // jie out (block:1024 * 3): 1519

        // jie out: 1441
        // jie out (block:1): 36657
        // jie out (block:1024): 1422
        // jie out (block:10240): 1454
        // jie out (block:1024 * 3): 1530
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream())
                .blockSize(encoder.getBlockSize()).encoder(encoder.toStreamEncoder());
            bs.transfer();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie out: " + (t2 - t1));
    }

    @Test
    public void testHexPerformance() throws Exception {
        int times = 10000;
        byte[] source = JieRandom.fill(new byte[99999]);

        // jie hex encode: 1016
        // apache hex encode: 3554
        // jie hex decode: 15883
        // apache hex decode: 10844
        // jie hex encode: 1008
        // apache hex encode: 3461
        // jie hex decode: 8977
        // apache hex decode: 10813
        // jie hex encode: 1047
        // apache hex encode: 3375
        // jie hex decode: 8954
        // apache hex decode: 10219
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
    }
}
