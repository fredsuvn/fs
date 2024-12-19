package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.io.BytesBuilder;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class JieIOTest {

    @Test
    public void testProcessor() throws Exception {
        JieIO.processor(new byte[0]);
        JieIO.processor(JieIO.emptyInputStream());
        JieIO.processor(ByteBuffer.allocate(1));
        JieIO.processor(new byte[1], 0, 1);
        JieIO.processor(new char[0]);
        JieIO.processor("");
        JieIO.processor(JieIO.emptyReader());
        JieIO.processor(CharBuffer.allocate(1));
        JieIO.processor(new char[1], 0, 1);
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals(JieIO.emptyInputStream().read(), -1);
        assertEquals(JieIO.emptyReader().read(), -1);
        JieIO.emptyReader().close();
    }

    @Test
    public void testRead() throws Exception {
        testRead(50, -1);
        testRead(JieIO.BUFFER_SIZE * 2, -1);
        testRead(50, 5);
        testRead(JieIO.BUFFER_SIZE * 2, 5);
        testRead(50, 0);
        testRead(50, 55);
    }

    @Test
    public void testReadTo() throws Exception {
        {
            // bytes
            int size = 1024;
            byte[] src = JieRandom.fill(new byte[size]);
            InputStream in = JieIO.inputStream(src);
            in.mark(0);
            byte[] dst = new byte[size];
            long c = JieIO.readTo(in, dst);
            assertEquals(c, size);
            assertEquals(dst, src);
            ByteBuffer dstBuffer = ByteBuffer.allocate(size);
            in.reset();
            c = JieIO.readTo(in, dstBuffer);
            assertEquals(c, size);
            dstBuffer.flip();
            assertEquals(JieBytes.copyBytes(dstBuffer), src);
            BytesBuilder dstOut = new BytesBuilder();
            in.reset();
            c = JieIO.readTo(in, dstOut);
            assertEquals(c, size);
            assertEquals(dstOut.toByteArray(), src);
        }

        {
            // chars
            int size = 1024;
            char[] src = JieRandom.fill(new char[size]);
            Reader in = JieIO.reader(src);
            in.mark(0);
            char[] dst = new char[size];
            long c = JieIO.readTo(in, dst);
            assertEquals(c, size);
            assertEquals(dst, src);
            CharBuffer dstBuffer = CharBuffer.allocate(size);
            in.reset();
            c = JieIO.readTo(in, dstBuffer);
            assertEquals(c, size);
            dstBuffer.flip();
            assertEquals(JieChars.copyChars(dstBuffer), src);
            StringBuilder dstOut = new StringBuilder();
            in.reset();
            c = JieIO.readTo(in, dstOut);
            assertEquals(c, size);
            assertEquals(dstOut.toString().toCharArray(), src);
        }
    }

    private void testRead(int size, int available) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);

        // bytes
        assertEquals(JieIO.read(bytesIn(bytes, available)), bytes);
        assertEquals(JieIO.read(JieIO.emptyInputStream()), null);
        assertEquals(JieIO.read(empty(available)), null);
        assertEquals(JieIO.read(empty(available), 1), null);
        expectThrows(IORuntimeException.class, () -> JieIO.read(errorIn()));
        assertEquals(JieIO.read(bytesIn(bytes, available), -1), bytes);
        assertEquals(JieIO.read(bytesIn(bytes, available), 0), new byte[0]);
        expectThrows(IORuntimeException.class, () -> JieIO.read(errorIn(), 1));
        assertEquals(JieIO.read(bytesIn(bytes, available), offset), Arrays.copyOf(bytes, offset));
        assertEquals(JieIO.read(bytesIn(bytes, available), size + 1), bytes);
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.read(bytesIn(bytes, available), JieIO.BUFFER_SIZE + offset),
                Arrays.copyOf(bytes, JieIO.BUFFER_SIZE + offset));
        }
        assertEquals(JieIO.available(bytesIn(bytes, bytes.length)), bytes);
        assertEquals(JieIO.available(bytesIn(bytes, offset)), Arrays.copyOf(bytes, offset));
        assertEquals(JieIO.available(empty(bytes.length)), null);
        if (available > 0) {
            assertEquals(JieIO.available(bytesIn(bytes, available)), Arrays.copyOf(bytes, Math.min(size, available)));
        }
        if (available == 0) {
            assertEquals(JieIO.available(bytesIn(bytes, available)), Arrays.copyOf(bytes, 1));
            assertEquals(JieIO.available(empty(available, 0)), new byte[0]);
        }
        if (available < 0) {
            assertEquals(JieIO.available(empty(available, 0)), null);
        }

        // string
        assertEquals(JieIO.read(new StringReader(str)), str);
        assertEquals(JieIO.read(new InputStreamReader(JieIO.emptyInputStream())), null);
        assertEquals(JieIO.read(new InputStreamReader(JieIO.emptyInputStream()), 1), null);
        expectThrows(IORuntimeException.class, () -> JieIO.read(new InputStreamReader(errorIn())));
        assertEquals(JieIO.read(new StringReader(str), offset), str.substring(0, offset));
        assertEquals(JieIO.read(new StringReader(str), -1), str);
        assertEquals(JieIO.read(new StringReader(str), 0), "");
        assertEquals(JieIO.read(new StringReader(str), size + 1), str);
        expectThrows(IORuntimeException.class, () -> JieIO.read(new InputStreamReader(errorIn()), 1));
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.read(new StringReader(str), JieIO.BUFFER_SIZE + offset),
                str.substring(0, JieIO.BUFFER_SIZE + offset));
        }
        assertEquals(JieIO.readString(new ByteArrayInputStream(bytes)), str);
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.readString(new ByteArrayInputStream(bytes)), str);
        }
        assertEquals(JieIO.readString(JieIO.emptyInputStream()), null);
        assertEquals(JieIO.avalaibleString(bytesIn(bytes, bytes.length)), str);
        assertEquals(JieIO.avalaibleString(JieIO.emptyInputStream()), null);
        expectThrows(IORuntimeException.class, () -> JieIO.avalaibleString(errorIn()));
    }

    private InputStream bytesIn(byte[] array, int available) {
        return new BytesIn(array, available);
    }

    private InputStream empty(int available) {
        return empty(available, -1);
    }

    private InputStream empty(int available, int readSize) {
        return new EmptyIn(available, readSize);
    }

    private InputStream errorIn() {
        return new ErrorIn();
    }

    private static final class BytesIn extends ByteArrayInputStream {

        private final int available;

        public BytesIn(byte[] buf, int available) {
            super(buf);
            this.available = available;
        }

        @Override
        public synchronized int available() {
            return available;
        }
    }

    private static final class EmptyIn extends InputStream {

        private final int available;
        private final int readSize;

        private EmptyIn(int available, int readSize) {
            this.available = available;
            this.readSize = readSize;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull byte[] b) throws IOException {
            return readSize;
        }

        @Override
        public int available() throws IOException {
            return available;
        }
    }

    private static final class ErrorIn extends InputStream {

        @Override
        public int read() throws IOException {
            throw new IOException();
        }

        @Override
        public synchronized int available() {
            return 100;
        }
    }
}
