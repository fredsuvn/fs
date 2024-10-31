package test.codec;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.JieRandom;
import xyz.fslabo.common.codec.CodecException;
import xyz.fslabo.common.codec.Encoder;
import xyz.fslabo.common.codec.JieBase64;
import xyz.fslabo.common.io.ByteStream;
import xyz.fslabo.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class CodecTest {

    @Test
    public void testBase64() throws Exception {
        testBase64(99);
        testBase64(1);
        testBase64(2);
        testBase64(3);
        testBase64(4);
        testBase64(10086);
        testBase64(JieIO.BUFFER_SIZE);
        testBase64(JieIO.BUFFER_SIZE + 10086);
        testBase64(57);
        testBase64(1024 * 1024);
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
    }

    private void testBase64(byte[] data, Encoder encoder, Base64.Encoder be) throws Exception {

        assertEquals(encoder.encode(data), be.encode(data));

        ByteBuffer bb1 = encoder.encode(ByteBuffer.wrap(data));
        byte[] bs1 = new byte[bb1.remaining()];
        bb1.get(bs1);
        ByteBuffer bb2 = be.encode(ByteBuffer.wrap(data));
        byte[] bs2 = new byte[bb2.remaining()];
        bb2.get(bs2);
        assertEquals(bs1, bs2);

        ByteBuffer sb = ByteBuffer.allocateDirect(data.length);
        sb.put(data);
        sb.flip();
        ByteBuffer bbb1 = encoder.encode(sb);
        sb.flip();
        ByteBuffer bbb2 = be.encode(sb);
        bbb1.get(bs1);
        bbb2.get(bs2);
        assertEquals(bs1, bs2);

        byte[] dest1 = new byte[data.length * 10];
        byte[] dest2 = new byte[data.length * 10];
        encoder.encode(data, dest1);
        be.encode(data, dest2);
        assertEquals(dest1, dest2);

        ByteBuffer db1 = ByteBuffer.allocate(data.length * 10);
        encoder.encode(ByteBuffer.wrap(data), db1);
        db1.flip();
        byte[] dbb = new byte[db1.remaining()];
        db1.get(dbb);
        assertEquals(dbb, be.encode(data));

        db1 = ByteBuffer.allocateDirect(data.length * 10);
        encoder.encode(ByteBuffer.wrap(data), db1);
        db1.flip();
        dbb = new byte[db1.remaining()];
        db1.get(dbb);
        assertEquals(dbb, be.encode(data));

        ByteBuffer bs = ByteBuffer.allocateDirect(data.length);
        bs.put(data);
        bs.flip();
        db1 = ByteBuffer.allocateDirect(data.length * 10);
        encoder.encode(bs, db1);
        db1.flip();
        dbb = new byte[db1.remaining()];
        db1.get(dbb);
        assertEquals(dbb, be.encode(data));

        expectThrows(CodecException.class, () -> encoder.encode(data, new byte[0]));
        expectThrows(CodecException.class, () -> encoder.encode(ByteBuffer.wrap(data), ByteBuffer.wrap(new byte[0])));

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        OutputStream out = be.wrap(bytesOut);
        out.write(data);
        out.close();
        ByteArrayOutputStream bytesOut2 = new ByteArrayOutputStream();
        ByteStream byteStream = ByteStream.from(data).to(bytesOut2)
            .blockSize(encoder.getBlockSize()).encoder(encoder.toStreamEncoder());
        int readNum = (int) byteStream.transfer();
        assertEquals(readNum, data.length);
        assertEquals(bytesOut.toByteArray(), bytesOut2.toByteArray());
    }

    @Test
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
        t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteStream bs = ByteStream.from(source).to(new ByteArrayOutputStream())
                .blockSize(encoder.getBlockSize()).encoder(encoder.toStreamEncoder());
            bs.transfer();
        }
        t2 = System.currentTimeMillis();
        System.out.println("jie out: " + (t2 - t1));
    }
}
