package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
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
    public void testEmpty() throws Exception {
        assertEquals(JieIO.emptyInputStream().read(), -1);
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
    // public void testReadTo() throws Exception {
    //     {
    //         // bytes
    //         // readTo methods
    //         byte[] outBytes = new byte[bytes.length];
    //         JieIO.readTo(in, outBytes);
    //         assertEquals(bytes, outBytes);
    //         byte[] outBytes2 = new byte[bytes.length * 2];
    //         // in.reset();
    //         // JieIO.transfer(in, outBytes2, offset, bytes.length);
    //         // assertEquals(bytes, Arrays.copyOfRange(outBytes2, offset, offset + bytes.length));
    //         ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
    //         in.reset();
    //         JieIO.readTo(in, outBuffer);
    //         outBuffer.flip();
    //         byte[] outBufferContent = JieBytes.getBytes(outBuffer);
    //         assertEquals(bytes, outBufferContent);
    //         in.reset();
    //         JieIO.readTo(in, out);
    //         assertEquals(bytes, out.toByteArray());
    //         // in.reset();
    //         // out.reset();
    //         // JieIO.transfer(in, out, 2);
    //         // assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
    //         // in.reset();
    //         // out.reset();
    //         // JieIO.transfer(in, out, 2, 1);
    //         // assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
    //         // in.reset();
    //         // out.reset();
    //         // JieIO.transfer(in, out, 2, 100);
    //         // assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
    //
    //         // read empty
    //         assertEquals(JieIO.readTo(new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream()), -1);
    //     }
    //
    //     {
    //         // chars
    //         // readTo methods
    //         char[] outChars = new char[chars.length];
    //         JieIO.readTo(in, outChars);
    //         assertEquals(chars, outChars);
    //         char[] outChars2 = new char[chars.length * 2];
    //         // in.reset();
    //         // JieIO.transfer(in, outChars2, offset, chars.length);
    //         // assertEquals(chars, Arrays.copyOfRange(outChars2, offset, offset + chars.length));
    //         CharBuffer outBuffer = dirBuffer;
    //         in.reset();
    //         JieIO.readTo(in, outBuffer);
    //         outBuffer.flip();
    //         char[] outBufferContent = JieChars.getChars(outBuffer);
    //         assertEquals(chars, outBufferContent);
    //         in.reset();
    //         JieIO.readTo(in, out);
    //         assertEquals(chars, out.toCharArray());
    //         // in.reset();
    //         // out.reset();
    //         // JieIO.transfer(in, out, 2);
    //         // assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
    //         // in.reset();
    //         // out.reset();
    //         // JieIO.transfer(in, out, 2, 1);
    //         // assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
    //         // in.reset();
    //         // out.reset();
    //         // JieIO.transfer(in, out, 2, 100);
    //         // assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
    //
    //         // read empty
    //         assertEquals(JieIO.readTo(new CharArrayReader(new char[0]), new CharArrayWriter()), -1);
    //     }
    // }

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
