package test.encode;

import org.testng.annotations.Test;
import test.TU;
import xyz.fslabo.common.base.JieBytes;
import xyz.fslabo.common.base.JieRandom;
import xyz.fslabo.common.codec.CodecException;
import xyz.fslabo.common.encode.Base64Encoder;
import xyz.fslabo.common.encode.Encoder;
import xyz.fslabo.common.encode.JieBase64;
import xyz.fslabo.common.io.ByteStream;
import xyz.fslabo.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
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

    private void testBase64(byte[] data, Base64Encoder encoder, Base64.Encoder be) throws Exception {

        assertEquals(encoder.encode(data), be.encode(data));
        assertEquals(encoder.encodeToString(data), be.encodeToString(data));
        assertEquals(encoder.encodeToString(ByteBuffer.wrap(data)), be.encodeToString(data));

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
                    .blockSize(encoder.getBlockSize() - 7).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() + 7).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() * 10).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                bytesOut2.reset();
                ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                    .blockSize(encoder.getBlockSize() * 10 + 7).encoder(encoder.toStreamEncoder());
                int readNum = (int) byteStream.transfer();
                assertEquals(readNum, data.length == 0 ? -1 : data.length);
                assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
            }
            {
                for (int i = 1; i < 100; i++) {
                    bytesOut2.reset();
                    ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
                        .blockSize(i).encoder(encoder.toStreamEncoder());
                    int readNum = (int) byteStream.transfer();
                    assertEquals(readNum, data.length == 0 ? -1 : data.length);
                    assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
                }
            }
        }

        {
            // error
            if (data.length > 0) {
                expectThrows(CodecException.class, () -> encoder.encode(data, new byte[0]));
                expectThrows(CodecException.class, () -> encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(new byte[0])));
            }
        }
    }

    //@Test
    public void testBuffer() throws Exception {
        int times = 10000;
        byte[] source = JieRandom.fill(new byte[99999]);
        ByteBuffer sb = ByteBuffer.wrap(source);
        Encoder encoder = JieBase64.encoder();
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
        // t1 = System.currentTimeMillis();
        // for (int i = 0; i < times; i++) {
        //     ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream())
        //         .blockSize(1).encoder(encoder.toStreamEncoder());
        //     bs.transfer();
        // }
        // t2 = System.currentTimeMillis();
        // System.out.println("jie out (block:1): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream())
                .blockSize(1024).encoder(encoder.toStreamEncoder());
            bs.transfer();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie out (block:1024): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream())
                .blockSize(10240).encoder(encoder.toStreamEncoder());
            bs.transfer();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie out (block:10240): " + (t2 - t1));
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream())
                .blockSize(1024 * 3).encoder(encoder.toStreamEncoder());
            bs.transfer();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie out (block:1024 * 3): " + (t2 - t1));
    }
}
