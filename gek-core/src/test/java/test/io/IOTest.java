package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieRandom;
import xyz.fslabo.common.io.ByteStream;
import xyz.fslabo.common.io.CharStream;
import xyz.fslabo.common.io.IORuntimeException;
import xyz.fslabo.common.io.JieIO;
import xyz.fslabo.test.JieTest;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class IOTest {

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

    @Test
    public void testBytesTransfer() throws Exception {
        // readTo()
        testBytesTransfer(666, JieIO.BUFFER_SIZE, -1);
        testBytesTransfer(666, 67, -1);
        testBytesTransfer(666, 1, -1);
        testBytesTransfer(100, 10, -1);
        testBytesTransfer(666, JieIO.BUFFER_SIZE, -1);
        testBytesTransfer(666, 67, 667);
        testBytesTransfer(666, 1, 667);
        testBytesTransfer(100, 10, 101);
        testBytesTransfer(222, 33, 55);
        testBytesTransfer(100, 10, 0);
        testBytesTransfer(100, 10, 100);

        int size = 10;
        int offset = 6;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        in.mark(0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // readTo methods
        byte[] outBytes = new byte[bytes.length];
        JieIO.transfer(in, outBytes);
        assertEquals(bytes, outBytes);
        byte[] outBytes2 = new byte[bytes.length * 2];
        in.reset();
        JieIO.transfer(in, outBytes2, offset, bytes.length);
        assertEquals(bytes, Arrays.copyOfRange(outBytes2, offset, offset + bytes.length));
        ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
        in.reset();
        JieIO.transfer(in, outBuffer);
        byte[] outBufferContent = new byte[bytes.length];
        outBuffer.flip();
        outBuffer.get(outBufferContent);
        assertEquals(bytes, outBufferContent);
        in.reset();
        JieIO.transfer(in, out);
        assertEquals(bytes, out.toByteArray());
        in.reset();
        out.reset();
        JieIO.transfer(in, out, 2);
        assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
        in.reset();
        out.reset();
        JieIO.transfer(in, out, 2, 1);
        assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
        in.reset();
        out.reset();
        JieIO.transfer(in, out, 2, 100);
        assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());

        // read limit
        in.reset();
        out.reset();
        long readNum = ByteStream.from(in).to(out).readLimit(0).start();
        assertEquals(readNum, 0);
        readNum = ByteStream.from(in).to(out).readLimit(1).start();
        assertEquals(readNum, 1);
        assertEquals(str.substring(0, 1), new String(Arrays.copyOfRange(out.toByteArray(), 0, 1), JieChars.UTF_8));
        in.reset();
        out.reset();
        readNum = ByteStream.from(in).to(out).encoder(b -> {
            int len = b.remaining();
            byte[] bs = new byte[len * 2];
            b.get(bs, 0, len);
            b.flip();
            b.get(bs, len, len);
            return ByteBuffer.wrap(bs);
        }).start();
        assertEquals(readNum, size);
        assertEquals(str + str, new String(out.toByteArray(), JieChars.UTF_8));

        // nio
        NioIn nioIn = new NioIn();
        byte[] nioBytes = new byte[size];
        readNum = ByteStream.from(nioIn).to(nioBytes).readLimit(nioBytes.length).start();
        assertEquals(readNum, size);
        byte[] compareBytes = Arrays.copyOf(nioBytes, nioBytes.length);
        Arrays.fill(compareBytes, (byte) 1);
        assertEquals(nioBytes, compareBytes);
        nioIn.reset();
        Arrays.fill(nioBytes, (byte) 2);
        Arrays.fill(compareBytes, (byte) 2);
        readNum = ByteStream.from(nioIn).to(nioBytes).breakOnZeroRead(true).start();
        assertEquals(readNum, 0);
        assertEquals(nioBytes, compareBytes);

        // error
        expectThrows(IORuntimeException.class, () -> testBytesTransfer(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> ByteStream.from((InputStream) null).start());
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new byte[0]).to(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new byte[0]).start());
        expectThrows(IORuntimeException.class, () -> ByteStream.from((InputStream) null).to(new byte[0]).start());
        Method method = ByteStream.from(new byte[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, ByteStream.from(new byte[0]), "");
        method = ByteStream.from(new byte[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, ByteStream.from(new byte[0]), "");
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new ThrowIn(0)).to(new byte[0]).start());
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new ThrowIn(1)).to(new byte[0]).start());
    }

    private void testBytesTransfer(int size, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);

        // stream -> stream
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        in.mark(0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long readNum = ByteStream.from(in).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(bytes.length, readLimit));
        assertEquals(str.substring(0, getLength(bytes.length, readLimit)), new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8));

        // stream -> byte[]
        byte[] outBytes = new byte[bytes.length];
        in.reset();
        readNum = ByteStream.from(in).to(outBytes).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        assertEquals(str, new String(outBytes, 0, bytes.length, JieChars.UTF_8));
        outBytes = new byte[bytes.length * 2];
        in.reset();
        readNum = ByteStream.from(in).to(outBytes, offset, bytes.length).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        assertEquals(str, new String(Arrays.copyOfRange(outBytes, offset, offset + bytes.length), JieChars.UTF_8));

        // stream -> buffer
        ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
        in.reset();
        readNum = ByteStream.from(in).to(outBuffer).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        outBuffer.flip();
        byte[] outBufferContent = new byte[outBuffer.capacity()];
        outBuffer.get(outBufferContent);
        assertEquals(str, new String(outBufferContent, JieChars.UTF_8));

        // byte[] -> stream
        out.reset();
        readNum = ByteStream.from(bytes).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(bytes.length, readLimit));
        assertEquals(str.substring(0, getLength(bytes.length, readLimit)), new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8));

        // byte[] -> byte[]
        outBytes = new byte[bytes.length];
        readNum = ByteStream.from(bytes).to(outBytes).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        assertEquals(str, new String(outBytes, JieChars.UTF_8));
        byte[] inBytes = new byte[bytes.length * 2];
        outBytes = new byte[bytes.length];
        System.arraycopy(bytes, 0, inBytes, offset, bytes.length);
        readNum = ByteStream.from(inBytes, offset, bytes.length).to(outBytes).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        assertEquals(str, new String(outBytes, JieChars.UTF_8));
        outBytes = new byte[bytes.length];
        readNum = ByteStream.from(bytes, 0, bytes.length).to(outBytes, 0, outBytes.length).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        assertEquals(str, new String(outBytes, JieChars.UTF_8));
        outBytes = new byte[bytes.length];
        readNum = ByteStream.from(bytes, 0, bytes.length - 1).to(outBytes, 0, outBytes.length - 1).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length - 1);
        assertEquals(str.substring(0, str.length() - 1), new String(Arrays.copyOfRange(outBytes, 0, outBytes.length - 1), JieChars.UTF_8));

        // byte[] -> buffer
        outBuffer = ByteBuffer.allocateDirect(bytes.length);
        readNum = ByteStream.from(bytes).to(outBuffer).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        outBuffer.flip();
        outBufferContent = new byte[outBuffer.capacity()];
        outBuffer.get(outBufferContent);
        assertEquals(str, new String(outBufferContent, JieChars.UTF_8));

        // buffer -> stream
        ByteBuffer inBuffer = ByteBuffer.allocateDirect(bytes.length);
        inBuffer.mark();
        inBuffer.put(bytes);
        inBuffer.reset();
        out.reset();
        readNum = ByteStream.from(inBuffer).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(bytes.length, readLimit));
        assertEquals(str.substring(0, getLength(bytes.length, readLimit)), new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8));

        // buffer -> byte[]
        inBuffer.reset();
        outBytes = new byte[bytes.length];
        readNum = ByteStream.from(inBuffer).to(outBytes).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        assertEquals(str, new String(outBytes, JieChars.UTF_8));

        // buffer -> buffer
        inBuffer.reset();
        outBuffer = ByteBuffer.allocateDirect(bytes.length);
        readNum = ByteStream.from(inBuffer).to(outBuffer).blockSize(blockSize).start();
        assertEquals(readNum, bytes.length);
        outBuffer.flip();
        outBufferContent = new byte[outBuffer.capacity()];
        outBuffer.get(outBufferContent);
        assertEquals(str, new String(outBufferContent, JieChars.UTF_8));
    }

    @Test
    public void testCharsTransfer() throws Exception {
        // readTo()
        testCharsTransfer(666, JieIO.BUFFER_SIZE, -1);
        testCharsTransfer(666, 67, -1);
        testCharsTransfer(666, 1, -1);
        testCharsTransfer(100, 10, -1);
        testCharsTransfer(666, JieIO.BUFFER_SIZE, -1);
        testCharsTransfer(666, 67, 667);
        testCharsTransfer(666, 1, 667);
        testCharsTransfer(100, 10, 101);
        testCharsTransfer(222, 33, 55);
        testCharsTransfer(100, 10, 0);
        testCharsTransfer(100, 10, 100);

        int size = 10;
        int offset = 6;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        char[] chars = str.toCharArray();
        CharArrayReader in = new CharArrayReader(chars);
        in.mark(0);
        CharArrayWriter out = new CharArrayWriter();

        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = 0;
            bytes[i * 2 + 1] = (byte) chars[i];
        }
        ByteBuffer dirBuffer = ByteBuffer.allocateDirect(bytes.length);
        dirBuffer.put(bytes);
        dirBuffer.flip();

        // readTo methods
        char[] outChars = new char[chars.length];
        JieIO.transfer(in, outChars);
        assertEquals(chars, outChars);
        char[] outChars2 = new char[chars.length * 2];
        in.reset();
        JieIO.transfer(in, outChars2, offset, chars.length);
        assertEquals(chars, Arrays.copyOfRange(outChars2, offset, offset + chars.length));
        CharBuffer outBuffer = dirBuffer.asCharBuffer();
        in.reset();
        JieIO.transfer(in, outBuffer);
        char[] outBufferContent = new char[chars.length];
        outBuffer.flip();
        outBuffer.get(outBufferContent);
        assertEquals(chars, outBufferContent);
        in.reset();
        JieIO.transfer(in, out);
        assertEquals(chars, out.toCharArray());
        in.reset();
        out.reset();
        JieIO.transfer(in, out, 2);
        assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
        in.reset();
        out.reset();
        JieIO.transfer(in, out, 2, 1);
        assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
        in.reset();
        out.reset();
        JieIO.transfer(in, out, 2, 100);
        assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());

        // read limit
        in.reset();
        out.reset();
        long readNum = CharStream.from(in).to(out).readLimit(0).start();
        assertEquals(readNum, 0);
        readNum = CharStream.from(in).to(out).readLimit(1).start();
        assertEquals(readNum, 1);
        assertEquals(str.substring(0, 1), new String(Arrays.copyOfRange(out.toCharArray(), 0, 1)));
        in.reset();
        out.reset();
        readNum = CharStream.from(in).to(out).encoder(b -> {
            int len = b.remaining();
            char[] bs = new char[len * 2];
            b.get(bs, 0, len);
            b.flip();
            b.get(bs, len, len);
            return CharBuffer.wrap(bs);
        }).start();
        assertEquals(readNum, size);
        assertEquals(str + str, new String(out.toCharArray()));

        // nio
        NioReader nioReader = new NioReader();
        char[] nioChars = new char[size];
        readNum = CharStream.from(nioReader).to(nioChars).readLimit(nioChars.length).start();
        assertEquals(readNum, size);
        char[] compareChars = Arrays.copyOf(nioChars, nioChars.length);
        Arrays.fill(compareChars, (char) 1);
        assertEquals(nioChars, compareChars);
        nioReader.reset();
        Arrays.fill(nioChars, (char) 2);
        Arrays.fill(compareChars, (char) 2);
        readNum = CharStream.from(nioReader).to(nioChars).breakOnZeroRead(true).start();
        assertEquals(readNum, 0);
        assertEquals(nioChars, compareChars);

        // error
        expectThrows(IORuntimeException.class, () -> testCharsTransfer(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> CharStream.from((Reader) null).start());
        expectThrows(IORuntimeException.class, () -> CharStream.from(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharStream.from(new char[0]).to(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharStream.from(new char[0]).start());
        expectThrows(IORuntimeException.class, () -> CharStream.from((Reader) null).to(new char[0]).start());
        Method method = CharStream.from(new char[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, CharStream.from(new char[0]), 1);
        method = CharStream.from(new char[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, CharStream.from(new char[0]), "");
        expectThrows(IORuntimeException.class, () -> CharStream.from(new ThrowReader(0)).to(new char[0]).start());
        expectThrows(IORuntimeException.class, () -> CharStream.from(new ThrowReader(1)).to(new char[0]).start());
    }

    private void testCharsTransfer(int size, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        char[] chars = str.toCharArray();

        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = 0;
            bytes[i * 2 + 1] = (byte) chars[i];
        }
        ByteBuffer dirBuffer = ByteBuffer.allocateDirect(bytes.length);
        dirBuffer.put(bytes);
        dirBuffer.order(ByteOrder.BIG_ENDIAN);
        dirBuffer.flip();

        // stream -> stream
        CharArrayReader in = new CharArrayReader(chars);
        in.mark(0);
        CharArrayWriter out = new CharArrayWriter();
        long readNum = CharStream.from(in).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(chars.length, readLimit));
        assertEquals(str.substring(0, getLength(chars.length, readLimit)), new String(out.toCharArray(), 0, getLength(chars.length, readLimit)));
        // string -> stream
        out.reset();
        readNum = CharStream.from(str).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(chars.length, readLimit));
        assertEquals(str.substring(0, getLength(chars.length, readLimit)), new String(out.toCharArray(), 0, getLength(chars.length, readLimit)));
        // direct -> stream
        CharBuffer dirInBuffer = dirBuffer.asCharBuffer();
        StringBuilder outBuilder = new StringBuilder();
        readNum = CharStream.from(dirInBuffer).to(outBuilder).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(chars.length, readLimit));
        assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());

        // stream -> char[]
        char[] outChars = new char[chars.length];
        in.reset();
        readNum = CharStream.from(in).to(outChars).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        assertEquals(str, new String(outChars));
        outChars = new char[chars.length * 2];
        in.reset();
        readNum = CharStream.from(in).to(outChars, offset, chars.length).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        assertEquals(str, new String(Arrays.copyOfRange(outChars, offset, offset + chars.length)));

        // stream -> buffer
        CharBuffer outBuffer = dirBuffer.asCharBuffer();
        in.reset();
        readNum = CharStream.from(in).to(outBuffer).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        outBuffer.flip();
        char[] outBufferContent = new char[outBuffer.capacity()];
        outBuffer.get(outBufferContent);
        assertEquals(str, new String(outBufferContent));

        // char[] -> stream
        out.reset();
        readNum = CharStream.from(chars).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(chars.length, readLimit));
        assertEquals(str.substring(0, getLength(chars.length, readLimit)), new String(out.toCharArray(), 0, getLength(chars.length, readLimit)));

        // char[] -> char[]
        outChars = new char[chars.length];
        readNum = CharStream.from(chars).to(outChars).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        assertEquals(str, new String(outChars));
        char[] inChars = new char[chars.length * 2];
        outChars = new char[chars.length];
        System.arraycopy(chars, 0, inChars, offset, chars.length);
        readNum = CharStream.from(inChars, offset, chars.length).to(outChars).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        assertEquals(str, new String(outChars));
        outChars = new char[chars.length];
        readNum = CharStream.from(chars, 0, chars.length).to(outChars, 0, outChars.length).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        assertEquals(str, new String(outChars));
        outChars = new char[chars.length];
        readNum = CharStream.from(chars, 0, chars.length - 1).to(outChars, 0, outChars.length - 1).blockSize(blockSize).start();
        assertEquals(readNum, chars.length - 1);
        assertEquals(str.substring(0, str.length() - 1), new String(Arrays.copyOfRange(outChars, 0, outChars.length - 1)));

        // char[] -> buffer
        outBuffer = dirBuffer.asCharBuffer();
        readNum = CharStream.from(chars).to(outBuffer).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        outBuffer.flip();
        outBufferContent = new char[outBuffer.capacity()];
        outBuffer.get(outBufferContent);
        assertEquals(str, new String(outBufferContent));

        // buffer -> stream
        CharBuffer inBuffer = CharBuffer.allocate(chars.length);
        inBuffer.mark();
        inBuffer.put(chars);
        inBuffer.reset();
        out.reset();
        readNum = CharStream.from(inBuffer).to(out).blockSize(blockSize).readLimit(readLimit).start();
        assertEquals(readNum, getLength(chars.length, readLimit));
        assertEquals(str.substring(0, getLength(chars.length, readLimit)), new String(out.toCharArray(), 0, getLength(chars.length, readLimit)));

        // buffer -> char[]
        inBuffer.reset();
        outChars = new char[chars.length];
        readNum = CharStream.from(inBuffer).to(outChars).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        assertEquals(str, new String(outChars));

        // buffer -> buffer
        inBuffer.reset();
        outBuffer = dirBuffer.asCharBuffer();
        readNum = CharStream.from(inBuffer).to(outBuffer).blockSize(blockSize).start();
        assertEquals(readNum, chars.length);
        outBuffer.flip();
        outBufferContent = new char[outBuffer.capacity()];
        outBuffer.get(outBufferContent);
        assertEquals(str, new String(outBufferContent));
    }

    private int getLength(int length, int readLimit) {
        if (readLimit < 0) {
            return length;
        }
        return Math.min(length, readLimit);
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

    private static final class NioIn extends InputStream {

        private int i = 0;

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull byte[] b, int off, int len) throws IOException {
            if (i++ < 3) {
                return 0;
            }
            Arrays.fill(b, (byte) 1);
            return len;
        }

        public void reset() {
            i = 0;
        }
    }

    private static final class NioReader extends Reader {

        private int i = 0;

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull char[] b, int off, int len) throws IOException {
            if (i++ < 3) {
                return 0;
            }
            Arrays.fill(b, (char) 1);
            return len;
        }

        public void reset() {
            i = 0;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static final class ThrowIn extends InputStream {

        private final int e;

        private ThrowIn(int e) {
            this.e = e;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull byte[] b, int off, int len) throws IOException {
            if (e == 0) {
                throw new IOException("e == 0");
            }
            throw new IllegalArgumentException("e = " + e);
        }
    }

    private static final class ThrowReader extends Reader {

        private final int e;

        private ThrowReader(int e) {
            this.e = e;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
            if (e == 0) {
                throw new IOException("e == 0");
            }
            throw new IllegalArgumentException("e = " + e);
        }

        @Override
        public void close() throws IOException {

        }
    }
}
