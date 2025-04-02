package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.io.BytesBuilder;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.*;

public class JieIOTest {

    @Test
    public void testEmptyAndNull() throws Exception {
        assertEquals(JieIO.emptyInStream().read(), -1);
        assertEquals(JieIO.emptyReader().read(), -1);
        JieIO.emptyReader().close();
        assertSame(JieIO.emptyInStream(), JieIO.emptyInStream());
        assertSame(JieIO.emptyReader(), JieIO.emptyReader());
        JieIO.nullOutStream().write(JieRandom.fill(new byte[10086]));
        JieIO.nullOutStream().close();
        JieIO.nullWriter().write(JieRandom.fill(new char[10086]));
        JieIO.nullWriter().close();
        assertSame(JieIO.nullOutStream(), JieIO.nullOutStream());
        assertSame(JieIO.nullWriter(), JieIO.nullWriter());
        JieIO.nullWriter().flush();
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

    private void testRead(int size, int available) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);
        char[] chars = str.toCharArray();

        // bytes
        assertEquals(JieIO.read(bytesInput(bytes, available)), bytes);
        assertEquals(JieIO.read(JieIO.emptyInStream()), new byte[0]);
        assertEquals(JieIO.read(JieIO.emptyInStream(), size), new byte[0]);
        assertEquals(JieIO.read(emptyInput(available)), new byte[0]);
        assertEquals(JieIO.read(emptyInput(available), 1), new byte[0]);
        expectThrows(IORuntimeException.class, () -> JieIO.read(errorInput()));
        assertEquals(JieIO.read(bytesInput(bytes, available), -1), bytes);
        assertEquals(JieIO.read(bytesInput(bytes, available), 0), new byte[0]);
        expectThrows(IORuntimeException.class, () -> JieIO.read(errorInput(), 1));
        assertEquals(JieIO.read(bytesInput(bytes, available), offset), Arrays.copyOf(bytes, offset));
        assertEquals(JieIO.read(bytesInput(bytes, available), size + 1), bytes);
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.read(bytesInput(bytes, available), JieIO.BUFFER_SIZE + offset),
                Arrays.copyOf(bytes, JieIO.BUFFER_SIZE + offset));
        }
        assertEquals(JieIO.available(bytesInput(bytes, bytes.length)), bytes);
        assertEquals(JieIO.available(bytesInput(bytes, offset)), Arrays.copyOf(bytes, offset));
        assertEquals(JieIO.available(emptyInput(bytes.length)), new byte[0]);
        if (available > 0) {
            assertEquals(JieIO.available(bytesInput(bytes, available)), Arrays.copyOf(bytes, Math.min(size, available)));
        }
        if (available == 0) {
            assertEquals(JieIO.available(bytesInput(bytes, available)), Arrays.copyOf(bytes, 1));
            assertEquals(JieIO.available(emptyInput(available, 0)), new byte[0]);
        }
        if (available < 0) {
            assertEquals(JieIO.available(emptyInput(available, 0)), new byte[0]);
        }

        // chars
        assertEquals(JieIO.read(charsReader(chars)), chars);
        assertEquals(JieIO.read(JieIO.emptyReader()), new char[0]);
        assertEquals(JieIO.read(JieIO.emptyReader(), size), new char[0]);
        expectThrows(IORuntimeException.class, () -> JieIO.read(errorReader()));
        assertEquals(JieIO.read(charsReader(chars), -1), chars);
        assertEquals(JieIO.read(charsReader(chars), 0), new char[0]);
        expectThrows(IORuntimeException.class, () -> JieIO.read(errorReader(), 1));
        assertEquals(JieIO.read(charsReader(chars), offset), Arrays.copyOf(chars, offset));
        assertEquals(JieIO.read(charsReader(chars), size + 1), chars);
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.read(charsReader(chars), JieIO.BUFFER_SIZE + offset),
                Arrays.copyOf(chars, JieIO.BUFFER_SIZE + offset));
        }

        // string
        assertEquals(JieIO.string(new StringReader(str)), str);
        assertEquals(JieIO.string(new InputStreamReader(JieIO.emptyInStream())), "");
        assertEquals(JieIO.string(new InputStreamReader(JieIO.emptyInStream()), 1), "");
        expectThrows(IORuntimeException.class, () -> JieIO.string(new InputStreamReader(errorInput())));
        assertEquals(JieIO.string(new StringReader(str), offset), str.substring(0, offset));
        assertEquals(JieIO.string(new StringReader(str), -1), str);
        assertEquals(JieIO.string(new StringReader(str), 0), "");
        assertEquals(JieIO.string(new StringReader(str), size + 1), str);
        expectThrows(IORuntimeException.class, () -> JieIO.string(new InputStreamReader(errorInput()), 1));
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.string(new StringReader(str), JieIO.BUFFER_SIZE + offset),
                str.substring(0, JieIO.BUFFER_SIZE + offset));
        }
        assertEquals(JieIO.string(new ByteArrayInputStream(bytes)), str);
        if (size > JieIO.BUFFER_SIZE + offset) {
            assertEquals(JieIO.string(new ByteArrayInputStream(bytes)), str);
        }
        assertEquals(JieIO.string(JieIO.emptyInStream()), "");
        assertEquals(JieIO.avalaibleString(bytesInput(bytes, bytes.length)), str);
        assertEquals(JieIO.avalaibleString(JieIO.emptyInStream()), "");
        expectThrows(IORuntimeException.class, () -> JieIO.avalaibleString(errorInput()));
    }

    @Test
    public void testReadTo() throws Exception {
        {
            // bytes
            int size = 1024;
            byte[] src = JieRandom.fill(new byte[size]);
            InputStream in = JieIO.inStream(src);
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

    private InputStream bytesInput(byte[] array, int available) {
        return new BytesInput(array, available);
    }

    private Reader charsReader(char[] array) {
        return new CharsReader(array);
    }

    private InputStream emptyInput(int available) {
        return emptyInput(available, -1);
    }

    private InputStream emptyInput(int available, int readSize) {
        return new EmptyInput(available, readSize);
    }

    private InputStream errorInput() {
        return new ErrorInput();
    }

    private Reader errorReader() {
        return new ErrorReader();
    }

    private static final class BytesInput extends ByteArrayInputStream {

        private final int available;

        public BytesInput(byte[] buf, int available) {
            super(buf);
            this.available = available;
        }

        @Override
        public synchronized int available() {
            return available;
        }
    }

    private static final class CharsReader extends CharArrayReader {

        public CharsReader(char[] buf) {
            super(buf);
        }
    }

    private static final class EmptyInput extends InputStream {

        private final int available;
        private final int readSize;

        private EmptyInput(int available, int readSize) {
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

    private static final class ErrorInput extends InputStream {

        @Override
        public int read() throws IOException {
            throw new IOException();
        }

        @Override
        public synchronized int available() {
            return 100;
        }
    }

    private static final class ErrorReader extends Reader {

        @Override
        public int read() throws IOException {
            throw new IOException();
        }

        @Override
        public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
            throw new IOException();
        }

        @Override
        public void close() throws IOException {
        }
    }
}
