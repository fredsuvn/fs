package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.io.BytesBuilder;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.*;

public class BytesBuilderTest {

    @Test
    public void testBytesBuilder() throws Exception {
        testBytesBuilder(512);
        testBytesBuilder(1024);
        expectThrows(IllegalArgumentException.class, () -> new BytesBuilder(-1));
        expectThrows(IllegalArgumentException.class, () -> new BytesBuilder(10, -2));
        expectThrows(IllegalArgumentException.class, () -> new BytesBuilder(10, 2));
        BytesBuilder bb = new BytesBuilder();
        bb.append((byte) 1);
        expectThrows(IORuntimeException.class, () -> bb.writeTo(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        }));
        ByteBuffer bufEmpty = ByteBuffer.allocate(0);
        expectThrows(IORuntimeException.class, () -> bb.writeTo(bufEmpty));

        // test big memory!
        Method grow = BytesBuilder.class.getDeclaredMethod("grow", int.class);
        BytesBuilder bbs = new BytesBuilder(1, 1);
        bbs.write(1);
        expectThrows(IllegalStateException.class, () -> bbs.write(1));
        BytesBuilder bbs2 = new BytesBuilder(2, 3);
        bbs2.write(1);
        bbs2.write(1);
        bbs2.write(1);
        expectThrows(IllegalStateException.class, () -> bbs2.write(1));
        JieTest.testThrow(IllegalStateException.class, grow, new BytesBuilder(), BytesBuilder.MAX_ARRAY_SIZE + 10);
        Method newCapacity = BytesBuilder.class.getDeclaredMethod("newCapacity", int.class, int.class);
        newCapacity.setAccessible(true);
        assertEquals(BytesBuilder.MAX_ARRAY_SIZE, newCapacity.invoke(new BytesBuilder(), -1, 1));
    }

    private void testBytesBuilder(int size) throws Exception {
        char[] cs = JieRandom.fill(new char[size], '0', '9');
        byte[] bs = new String(cs).getBytes();
        BytesBuilder bb = new BytesBuilder();
        bb.close();
        bb.trimBuffer();
        bb.append(bs[0]);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 1));
        bb.append(Arrays.copyOfRange(bs, 1, 10));
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 10));
        bb.append(bs, 10, 10);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 20));
        ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(bs, 20, 25));
        bb.append(buffer);
        ByteBuffer arrayBuf = ByteBuffer.wrap(bs, 20, 10);
        arrayBuf.get(new byte[5]);
        bb.append(arrayBuf);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 30));
        assertEquals(buffer.position(), 5);
        assertFalse(buffer.hasRemaining());
        bb.append(JieIO.inputStream(Arrays.copyOfRange(bs, 30, 40)));
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 40));
        BytesBuilder bb2 = new BytesBuilder();
        bb2.append(Arrays.copyOfRange(bs, 40, 50));
        bb.append(bb2);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 50));
        bb.append(JieIO.inputStream(Arrays.copyOfRange(bs, 50, 60)), 1);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 60));
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(10);
        buffer2.put(ByteBuffer.wrap(Arrays.copyOfRange(bs, 60, 70)));
        buffer2.flip();
        bb.append(buffer2);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 70));
        assertEquals(buffer2.position(), 10);
        assertFalse(buffer2.hasRemaining());
        bb.append(JieBytes.emptyBuffer());
        expectThrows(IORuntimeException.class, () -> bb.append(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        }));
        assertEquals(bb.size(), 70);
        assertEquals(bb.toByteBuffer(), ByteBuffer.wrap(bs, 0, 70));
        assertEquals(Arrays.copyOf(cs, 70), bb.toString().toCharArray());
        assertEquals(Arrays.copyOf(cs, 70), bb.toString("utf-8").toCharArray());
        assertEquals(Arrays.copyOf(cs, 70), bb.toString(JieChars.UTF_8).toCharArray());
        bb.reset();
        bb.append(bs[0]);
        bb.append(bs[1]);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 2));
        bb.reset();
        bb.append(bs[0]);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 1));
        bb.reset();
        bb.trimBuffer();
        bb.append(bs[0]);
        bb.append(bs[1]);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 2));
        bb.trimBuffer();
        bb.reset();
        bb.trimBuffer();
        bb.append(bs[0]);
        assertEquals(bb.toByteArray(), Arrays.copyOf(bs, 1));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bb.writeTo(out);
        assertEquals(bb.toByteArray(), out.toByteArray());
        ByteBuffer bufOut = ByteBuffer.allocate(1);
        bb.writeTo(bufOut);
        assertEquals(bb.toByteArray(), bufOut.array());
    }
}
