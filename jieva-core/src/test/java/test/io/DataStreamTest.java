package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.*;
import xyz.sunqian.common.io.*;
import xyz.sunqian.test.JieTest;
import xyz.sunqian.test.JieTestException;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.*;

public class DataStreamTest {

    @Test
    public void testBytesStream() throws Exception {
        // readTo()
        testBytesStream(666, JieIO.BUFFER_SIZE, -1);
        testBytesStream(666, 67, -1);
        testBytesStream(666, 1, -1);
        testBytesStream(100, 10, -1);
        testBytesStream(666, JieIO.BUFFER_SIZE, -1);
        testBytesStream(666, 67, 667);
        testBytesStream(666, 1, 667);
        testBytesStream(100, 10, 101);
        testBytesStream(222, 33, 55);
        testBytesStream(100, 10, 0);
        testBytesStream(100, 10, 100);
        testBytesStream(6666, 99, 77777);

        int size = 10;
        int offset = 6;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        in.mark(0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // readTo methods
        byte[] outBytes = new byte[bytes.length];
        JieIO.readTo(in, outBytes);
        assertEquals(bytes, outBytes);
        byte[] outBytes2 = new byte[bytes.length * 2];
        // in.reset();
        // JieIO.transfer(in, outBytes2, offset, bytes.length);
        // assertEquals(bytes, Arrays.copyOfRange(outBytes2, offset, offset + bytes.length));
        ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
        in.reset();
        JieIO.readTo(in, outBuffer);
        outBuffer.flip();
        byte[] outBufferContent = JieBytes.getBytes(outBuffer);
        assertEquals(bytes, outBufferContent);
        in.reset();
        JieIO.readTo(in, out);
        assertEquals(bytes, out.toByteArray());
        // in.reset();
        // out.reset();
        // JieIO.transfer(in, out, 2);
        // assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
        // in.reset();
        // out.reset();
        // JieIO.transfer(in, out, 2, 1);
        // assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());
        // in.reset();
        // out.reset();
        // JieIO.transfer(in, out, 2, 100);
        // assertEquals(Arrays.copyOfRange(bytes, 0, 2), out.toByteArray());

        // read empty
        assertEquals(JieIO.readTo(new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream()), -1);

        // read limit
        in.reset();
        out.reset();
        long readNum = ByteStream.from(in).readLimit(0).writeTo(out);
        assertEquals(readNum, 0);
        readNum = ByteStream.from(in).readLimit(1).writeTo(out);
        assertEquals(readNum, 1);
        assertEquals(str.substring(0, 1), new String(Arrays.copyOfRange(out.toByteArray(), 0, 1), JieChars.UTF_8));
        in.reset();
        out.reset();
        readNum = ByteStream.from(in).encoder((b, e) -> {
            int len = b.remaining();
            byte[] bs = new byte[len * 2];
            b.get(bs, 0, len);
            b.flip();
            b.get(bs, len, len);
            return ByteBuffer.wrap(bs);
        }).writeTo(out);
        assertEquals(readNum, size);
        assertEquals(str + str, new String(out.toByteArray(), JieChars.UTF_8));

        // nio
        NioIn nioIn = new NioIn();
        byte[] nioBytes = new byte[size];
        readNum = ByteStream.from(nioIn).readLimit(nioBytes.length).writeTo(nioBytes);
        assertEquals(readNum, size);
        byte[] compareBytes = Arrays.copyOf(nioBytes, nioBytes.length);
        Arrays.fill(compareBytes, (byte) 1);
        assertEquals(nioBytes, compareBytes);
        nioIn.reset();
        Arrays.fill(nioBytes, (byte) 2);
        Arrays.fill(compareBytes, (byte) 2);
        readNum = ByteStream.from(nioIn).endOnZeroRead(true).writeTo(nioBytes);
        assertEquals(readNum, 0);
        assertEquals(nioBytes, compareBytes);

        // error
        expectThrows(IORuntimeException.class, () -> testBytesStream(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> ByteStream.from((InputStream) null).writeTo((OutputStream) null));
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new byte[0]).writeTo(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new byte[0]).writeTo((OutputStream) null));
        expectThrows(IORuntimeException.class, () -> ByteStream.from((InputStream) null).writeTo(new byte[0]));
        Method method = ByteStream.from(new byte[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, ByteStream.from(new byte[0]), "");
        method = ByteStream.from(new byte[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, ByteStream.from(new byte[0]), "");
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new ThrowIn(0)).writeTo(new byte[0]));
        expectThrows(IORuntimeException.class, () -> ByteStream.from(new ThrowIn(1)).writeTo(new byte[0]));
    }

    private void testBytesStream(int size, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);

        {
            // stream -> stream
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            in.mark(0);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteStream.from(in).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
        }

        {
            // stream -> byte[]
            byte[] outBytes = new byte[bytes.length];
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            long readNum = ByteStream.from(in).blockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, 0, bytes.length, JieChars.UTF_8));
            outBytes = new byte[bytes.length * 2];
            in.reset();
            readNum = ByteStream.from(in).blockSize(blockSize).writeTo(outBytes, offset, bytes.length);
            assertEquals(readNum, bytes.length);
            assertEquals(
                str,
                new String(Arrays.copyOfRange(outBytes, offset, offset + bytes.length), JieChars.UTF_8)
            );
        }
        {
            // stream -> buffer
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            long readNum = ByteStream.from(in).blockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBuffer = TU.bufferDangling(bytes);
            in.reset();
            readNum = ByteStream.from(in).blockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // byte[] -> stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteStream.from(bytes).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> byte[]
            byte[] outBytes = new byte[bytes.length];
            long readNum = ByteStream.from(bytes).blockSize(blockSize).readLimit(readLimit).writeTo(outBytes);
            assertEquals(readNum, getLength(size, readLimit));
            assertEquals(
                Arrays.copyOfRange(bytes, 0, getLength(size, readLimit)),
                Arrays.copyOfRange(outBytes, 0, getLength(size, readLimit))
            );
            outBytes = new byte[bytes.length];
            readNum = ByteStream.from(bytes).blockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            byte[] inBytes = new byte[bytes.length * 2];
            outBytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, inBytes, offset, bytes.length);
            readNum = ByteStream.from(inBytes, offset, bytes.length).blockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = ByteStream.from(bytes, 0, bytes.length)
                .blockSize(blockSize).writeTo(outBytes, 0, outBytes.length);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = ByteStream
                .from(bytes, 0, bytes.length - 1)
                .blockSize(blockSize)
                .writeTo(outBytes, 0, outBytes.length - 1);
            assertEquals(readNum, bytes.length - 1);
            assertEquals(
                str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outBytes, 0, outBytes.length - 1), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> buffer
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteStream.from(bytes).blockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(size, readLimit));
            outBuffer.flip();
            assertEquals(Arrays.copyOfRange(bytes, 0, getLength(size, readLimit)), JieBytes.getBytes(outBuffer));
            outBuffer = ByteBuffer.allocateDirect(bytes.length);
            readNum = ByteStream.from(bytes).blockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> stream
            ByteBuffer inBuffer = JieBytes.copyBuffer(bytes, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
            ByteBuffer inArray = TU.bufferDangling(bytes);
            out.reset();
            readNum = ByteStream.from(inArray).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
        }

        {
            // buffer -> byte[]
            ByteBuffer inBuffer = JieBytes.copyBuffer(bytes, true);
            byte[] outBytes = new byte[bytes.length];
            long readNum = ByteStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(outBytes);
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), Arrays.copyOfRange(outBytes, 0, getLength(size, readLimit)));
            inBuffer = JieBytes.copyBuffer(bytes, true);
            outBytes = new byte[bytes.length];
            readNum = ByteStream.from(inBuffer).blockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> buffer
            ByteBuffer inBuffer = TU.bufferDangling(bytes);
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), JieBytes.getBytes(outBuffer));
            inBuffer = TU.bufferDangling(bytes);
            outBuffer = TU.bufferDangling(bytes);
            readNum = ByteStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(JieBytes.getBytes(inBuffer), outBytes);
        }
    }

    @Test
    public void testCharsStream() throws Exception {
        // readTo()
        testCharsStream(666, JieIO.BUFFER_SIZE, -1);
        testCharsStream(666, 67, -1);
        testCharsStream(666, 1, -1);
        testCharsStream(100, 10, -1);
        testCharsStream(666, JieIO.BUFFER_SIZE, -1);
        testCharsStream(666, 67, 667);
        testCharsStream(666, 1, 667);
        testCharsStream(100, 10, 101);
        testCharsStream(222, 33, 55);
        testCharsStream(100, 10, 0);
        testCharsStream(100, 10, 100);
        testCharsStream(6666, 99, 77777);

        int size = 10;
        int offset = 6;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        char[] chars = str.toCharArray();
        CharArrayReader in = new CharArrayReader(chars);
        in.mark(0);
        CharArrayWriter out = new CharArrayWriter();

        CharBuffer dirBuffer = JieChars.copyBuffer(chars, true);

        // readTo methods
        char[] outChars = new char[chars.length];
        JieIO.readTo(in, outChars);
        assertEquals(chars, outChars);
        char[] outChars2 = new char[chars.length * 2];
        // in.reset();
        // JieIO.transfer(in, outChars2, offset, chars.length);
        // assertEquals(chars, Arrays.copyOfRange(outChars2, offset, offset + chars.length));
        CharBuffer outBuffer = dirBuffer;
        in.reset();
        JieIO.readTo(in, outBuffer);
        outBuffer.flip();
        char[] outBufferContent = JieChars.getChars(outBuffer);
        assertEquals(chars, outBufferContent);
        in.reset();
        JieIO.readTo(in, out);
        assertEquals(chars, out.toCharArray());
        // in.reset();
        // out.reset();
        // JieIO.transfer(in, out, 2);
        // assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
        // in.reset();
        // out.reset();
        // JieIO.transfer(in, out, 2, 1);
        // assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());
        // in.reset();
        // out.reset();
        // JieIO.transfer(in, out, 2, 100);
        // assertEquals(Arrays.copyOfRange(chars, 0, 2), out.toCharArray());

        // read empty
        assertEquals(JieIO.readTo(new CharArrayReader(new char[0]), new CharArrayWriter()), -1);

        // read limit
        in.reset();
        out.reset();
        long readNum = CharStream.from(in).readLimit(0).writeTo(out);
        assertEquals(readNum, 0);
        readNum = CharStream.from(in).readLimit(1).writeTo(out);
        assertEquals(readNum, 1);
        assertEquals(str.substring(0, 1), new String(Arrays.copyOfRange(out.toCharArray(), 0, 1)));
        in.reset();
        out.reset();
        readNum = CharStream.from(in).encoder((b, e) -> {
            int len = b.remaining();
            char[] bs = new char[len * 2];
            b.get(bs, 0, len);
            b.flip();
            b.get(bs, len, len);
            return CharBuffer.wrap(bs);
        }).writeTo(out);
        assertEquals(readNum, size);
        assertEquals(str + str, new String(out.toCharArray()));

        // nio
        NioReader nioReader = new NioReader();
        char[] nioChars = new char[size];
        readNum = CharStream.from(nioReader).readLimit(nioChars.length).writeTo(nioChars);
        assertEquals(readNum, size);
        char[] compareChars = Arrays.copyOf(nioChars, nioChars.length);
        Arrays.fill(compareChars, (char) 1);
        assertEquals(nioChars, compareChars);
        nioReader.reset();
        Arrays.fill(nioChars, (char) 2);
        Arrays.fill(compareChars, (char) 2);
        readNum = CharStream.from(nioReader).endOnZeroRead(true).writeTo(nioChars);
        assertEquals(readNum, 0);
        assertEquals(nioChars, compareChars);

        // error
        expectThrows(IORuntimeException.class, () -> testCharsStream(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> CharStream.from((Reader) null).writeTo((Appendable) null));
        expectThrows(IORuntimeException.class, () -> CharStream.from(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharStream.from(new char[0]).writeTo(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharStream.from(new char[0]).writeTo((Appendable) null));
        expectThrows(IORuntimeException.class, () -> CharStream.from((Reader) null).writeTo(new char[0]));
        Method method = CharStream.from(new char[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, CharStream.from(new char[0]), 1);
        method = CharStream.from(new char[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, CharStream.from(new char[0]), "");
        expectThrows(IORuntimeException.class, () -> CharStream.from(new ThrowReader(0)).writeTo(new char[0]));
        expectThrows(IORuntimeException.class, () -> CharStream.from(new ThrowReader(1)).writeTo(new char[0]));
    }

    private void testCharsStream(int size, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[size], 'a', 'z'));
        char[] chars = str.toCharArray();

        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = 0;
            bytes[i * 2 + 1] = (byte) chars[i];
        }
        CharBuffer dirBuffer = JieChars.copyBuffer(chars, true);

        {
            // stream -> stream
            CharArrayReader in = new CharArrayReader(chars);
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(in).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // string -> stream
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(str).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // direct -> stream
            CharBuffer dirInBuffer = JieChars.copyBuffer(dirBuffer);
            StringBuilder outBuilder = new StringBuilder();
            long readNum = CharStream.from(dirInBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            outBuilder.setLength(0);
            readNum = CharStream.from(dirInBuffer).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).writeTo(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            StringWriter sw = new StringWriter();
            readNum = CharStream.from(dirInBuffer).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).writeTo(sw);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), sw.toString());
        }

        {
            // stream -> char[]
            char[] outChars = new char[chars.length];
            CharArrayReader in = new CharArrayReader(chars);
            in.mark(0);
            long readNum = CharStream.from(in).blockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length * 2];
            in.reset();
            readNum = CharStream.from(in).blockSize(blockSize).writeTo(outChars, offset, chars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(Arrays.copyOfRange(outChars, offset, offset + chars.length)));
        }

        {
            // stream -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(dirBuffer);
            CharArrayReader in = new CharArrayReader(chars);
            long readNum = CharStream.from(in).blockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outChars = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outChars));
        }

        // char[] -> stream
        {
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(chars).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            out.reset();
            readNum = CharStream.from(chars).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharStream.from(chars).blockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            readNum = CharStream.from(chars).blockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            char[] inChars = new char[chars.length * 2];
            outChars = new char[chars.length];
            System.arraycopy(chars, 0, inChars, offset, chars.length);
            readNum = CharStream.from(inChars, offset, chars.length).blockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharStream.from(chars, 0, chars.length)
                .blockSize(blockSize).writeTo(outChars, 0, outChars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharStream.from(chars, 0, chars.length - 1)
                .blockSize(blockSize).writeTo(outChars, 0, outChars.length - 1);
            assertEquals(readNum, chars.length - 1);
            assertEquals(str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outChars, 0, outChars.length - 1)));
        }

        {
            // char[] -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(chars, true);
            long readNum = CharStream.from(chars).blockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            assertEquals(str, new String(JieChars.getChars(outBuffer)));
            outBuffer = JieChars.copyBuffer(chars, true);
            readNum = CharStream.from(chars).blockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(chars.length, readLimit));
            outBuffer.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(outBuffer), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> appender
            StringBuilder appender = new StringBuilder();
            long readNum = CharStream.from(chars).blockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.setLength(0);
            readNum = CharStream.from(chars).blockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // buffer -> stream
            CharBuffer inBuffer = TU.buffer(chars);
            inBuffer.mark();
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            inBuffer.reset();
            out.reset();
            readNum = CharStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            CharBuffer arrayIn = TU.bufferDangling(chars);
            CharBuffer arrayOut = TU.bufferDangling(new char[chars.length]);
            readNum = CharStream.from(arrayIn).blockSize(blockSize).readLimit(readLimit).writeTo(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(arrayOut), 0, getLength(chars.length, readLimit))
            );
            arrayIn.flip();
            arrayOut.flip();
            readNum = CharStream.from(arrayIn).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(arrayOut), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // buffer -> char[]
            CharBuffer inBuffer = TU.buffer(chars);
            inBuffer.mark();
            char[] outChars = new char[chars.length];
            long readNum = CharStream.from(inBuffer).blockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            inBuffer.reset();
            outChars = new char[chars.length];
            readNum = CharStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // buffer -> appender
            CharBuffer inBuffer = TU.bufferDangling(chars);
            inBuffer.mark();
            StringBuilder appender = new StringBuilder();
            long readNum = CharStream.from(inBuffer).blockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            inBuffer.reset();
            appender.setLength(0);
            readNum = CharStream.from(inBuffer).blockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // buffer -> buffer
            CharBuffer inBuffer = TU.bufferDangling(chars);
            inBuffer.mark();
            CharBuffer outBuffer = JieChars.copyBuffer(dirBuffer);
            long readNum = CharStream.from(inBuffer).blockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outBytes = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outBytes));
        }

        {
            // charSeq -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharStream.from(str).blockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharStream.from(str).blockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            outChars = new char[chars.length];
            readNum = CharStream.from(JieString.asChars(str.toCharArray())).blockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharStream.from(JieString.asChars(str.toCharArray()))
                .blockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // charSeq -> appender
            StringBuilder appender = new StringBuilder();
            long readNum = CharStream.from(str).blockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.setLength(0);
            readNum = CharStream.from(str).blockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
            appender.setLength(0);
            readNum = CharStream.from(str).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }
    }

    private int getLength(int length, int readLimit) {
        if (readLimit < 0) {
            return length;
        }
        return Math.min(length, readLimit);
    }

    @Test
    public void testEncoder() {
        testEncoder(1, 1);
        testEncoder(1, 10);
        testEncoder(99, 9);
        testEncoder(99, 990);
        testEncoder(1024, 77);
        testEncoder(1024 * 1024, 777);
        testEncoder(1024 * 1024, 1024);

        // error
        {
            Throwable[] ts = new Throwable[1];
            try {
                ByteStream.from(new byte[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).writeTo(new byte[100]);
            } catch (IOEncodingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
        {
            Throwable[] ts = new Throwable[1];
            try {
                CharStream.from(new char[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).writeTo(new char[100]);
            } catch (IOEncodingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
    }

    private void testEncoder(int totalSize, int blockSize) {
        {
            // byte encoder
            byte[] endBytes = "end".getBytes(JieChars.defaultCharset());
            ByteStream.Encoder bytesEn = (data, end) -> {
                if (!end) {
                    assertEquals(data.remaining(), blockSize);
                } else {
                    if (data.hasRemaining()) {
                        assertEquals(data.remaining(), totalSize % blockSize);
                    }
                }
                ByteBuffer bb = ByteBuffer.allocate(end ? data.remaining() + endBytes.length : data.remaining());
                bb.put(data);
                if (end) {
                    bb.put(endBytes);
                }
                bb.flip();
                return bb;
            };
            byte[] bSrc = JieRandom.fill(new byte[totalSize], 0, 9);
            byte[] bDst = new byte[bSrc.length + endBytes.length];
            long c = ByteStream.from(bSrc).blockSize(blockSize).encoder(bytesEn).writeTo(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, totalSize), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            bSrc = JieRandom.fill(new byte[totalSize], 0, 9);
            c = ByteStream.from(new ByteArrayInputStream(bSrc)).blockSize(blockSize).encoder(bytesEn).writeTo(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, totalSize), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            bSrc = JieRandom.fill(new byte[totalSize], 0, 9);
            c = ByteStream.from(new NioIn(new ByteArrayInputStream(bSrc))).blockSize(blockSize).encoder(bytesEn).writeTo(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, totalSize), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            bSrc = JieRandom.fill(new byte[totalSize], 0, 9);
            c = ByteStream.from(ByteBuffer.wrap(bSrc)).blockSize(blockSize).encoder(bytesEn).writeTo(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, totalSize), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            c = ByteStream.from(new NioIn()).blockSize(blockSize).encoder(bytesEn).endOnZeroRead(true).writeTo(bOut);
            assertEquals(c, 0);
            assertEquals(bOut.toByteArray(), endBytes);
        }

        {
            // byte encoders
            byte[] src = JieRandom.fill(new byte[totalSize]);
            int portion = JieMath.leastPortion(totalSize, blockSize);
            BytesBuilder bb = new BytesBuilder();
            int start = 0;
            for (int i = 0; i < portion; i++) {
                int end = Math.min(start + blockSize, totalSize);
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                start += blockSize;
            }
            byte[] expectDst = bb.toByteArray();
            bb.reset();
            ByteStream.Encoder encoder = (data, end) -> {
                byte[] bytes = JieBytes.getBytes(data);
                byte[] ret = new byte[bytes.length * 2];
                System.arraycopy(bytes, 0, ret, 0, bytes.length);
                System.arraycopy(bytes, 0, ret, bytes.length, bytes.length);
                return ByteBuffer.wrap(ret);
            };
            long count = ByteStream.from(src).blockSize(blockSize).encoders(Jie.list(
                encoder, encoder
            )).writeTo(bb);
            assertEquals(count, totalSize);
            assertEquals(bb.toByteArray(), expectDst);
        }

        {
            // char encoder
            char[] endChars = "end".toCharArray();
            CharStream.Encoder charsEn = (data, end) -> {
                if (!end) {
                    assertEquals(data.remaining(), blockSize);
                } else {
                    if (data.hasRemaining()) {
                        assertEquals(data.remaining(), totalSize % blockSize);
                    }
                }
                CharBuffer cb = CharBuffer.allocate(end ? data.remaining() + endChars.length : data.remaining());
                cb.put(data);
                if (end) {
                    cb.put(endChars);
                }
                cb.flip();
                return cb;
            };
            char[] cSrc = JieRandom.fill(new char[totalSize], '0', '9');
            char[] cDst = new char[cSrc.length + endChars.length];
            long c = CharStream.from(new CharArrayReader(cSrc)).blockSize(blockSize).encoder(charsEn).writeTo(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, totalSize), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[totalSize], '0', '9');
            c = CharStream.from(new String(cSrc)).blockSize(blockSize).encoder(charsEn).writeTo(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, totalSize), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[totalSize], '0', '9');
            c = CharStream.from(new NioReader(new CharArrayReader(cSrc))).blockSize(blockSize).encoder(charsEn).writeTo(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, totalSize), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[totalSize], '0', '9');
            c = CharStream.from(CharBuffer.wrap(cSrc)).blockSize(blockSize).encoder(charsEn).writeTo(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, totalSize), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[totalSize], '0', '9');
            c = CharStream.from(cSrc).blockSize(blockSize).encoder(charsEn).writeTo(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, totalSize), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            CharArrayWriter cOut = new CharArrayWriter();
            c = CharStream.from(new NioReader()).blockSize(blockSize).encoder(charsEn).endOnZeroRead(true).writeTo(cOut);
            assertEquals(c, 0);
            assertEquals(cOut.toCharArray(), endChars);
        }

        {
            // char encoders
            char[] src = JieRandom.fill(new char[totalSize]);
            int portion = JieMath.leastPortion(totalSize, blockSize);
            StringBuilder bb = new StringBuilder();
            int start = 0;
            for (int i = 0; i < portion; i++) {
                int end = Math.min(start + blockSize, totalSize);
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                start += blockSize;
            }
            char[] expectDst = bb.toString().toCharArray();
            bb.setLength(0);
            CharStream.Encoder encoder = (data, end) -> {
                char[] chars = JieChars.getChars(data);
                char[] ret = new char[chars.length * 2];
                System.arraycopy(chars, 0, ret, 0, chars.length);
                System.arraycopy(chars, 0, ret, chars.length, chars.length);
                return CharBuffer.wrap(ret);
            };
            long count = CharStream.from(src).blockSize(blockSize).encoders(Jie.list(
                encoder, encoder
            )).writeTo(bb);
            assertEquals(count, totalSize);
            assertEquals(bb.toString().toCharArray(), expectDst);
        }
    }

    @Test
    public void testRoundEncoder() {
        testRoundEncoder(100, 5, 6);
        testRoundEncoder(10086, 11, 333);
        testRoundEncoder(10086, 333, 11);
        testRoundEncoder(10086, 22, 22);
        testRoundEncoder(10086, 222, 1);
    }

    private void testRoundEncoder(int size, int blockSize, int expectedBlockSize) {
        {
            // bytes
            byte[] src = JieRandom.fill(new byte[size]);
            byte[] dst = new byte[src.length * 2];
            for (int i = 0; i < src.length; i++) {
                dst[i * 2] = src[i];
                dst[i * 2 + 1] = (byte) expectedBlockSize;
            }
            byte[] dst2 = new byte[src.length * 2];
            long len = ByteStream.from(src).blockSize(blockSize).encoder(ByteStream.roundEncoder(
                (data, end) -> {
                    if (!end) {
                        assertTrue(data.remaining() >= expectedBlockSize);
                        if (blockSize < expectedBlockSize) {
                            assertEquals(data.remaining(), expectedBlockSize);
                        } else {
                            assertTrue(data.remaining() <= (blockSize / expectedBlockSize + 1) * expectedBlockSize);
                            assertTrue(data.remaining() >= (blockSize / expectedBlockSize) * expectedBlockSize);
                        }
                    }
                    ByteBuffer bb = ByteBuffer.allocate(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((byte) expectedBlockSize);
                    }
                    bb.flip();
                    return bb;
                },
                expectedBlockSize
            )).writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = ByteStream.from(src).blockSize(blockSize).encoder(ByteStream.roundEncoder(
                (data, end) -> {
                    if (!end) {
                        assertTrue(data.remaining() >= expectedBlockSize);
                        if (blockSize < expectedBlockSize) {
                            assertEquals(data.remaining(), expectedBlockSize);
                        } else {
                            assertTrue(data.remaining() <= (blockSize / expectedBlockSize + 1) * expectedBlockSize);
                            assertTrue(data.remaining() >= (blockSize / expectedBlockSize) * expectedBlockSize);
                        }
                    }
                    ByteBuffer bb = ByteBuffer.allocateDirect(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((byte) expectedBlockSize);
                    }
                    bb.flip();
                    return bb;
                },
                expectedBlockSize
            )).writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
        }
        {
            // chars
            char[] src = JieRandom.fill(new char[size]);
            char[] dst = new char[src.length * 2];
            for (int i = 0; i < src.length; i++) {
                dst[i * 2] = src[i];
                dst[i * 2 + 1] = (char) expectedBlockSize;
            }
            char[] dst2 = new char[src.length * 2];
            long len = CharStream.from(src).blockSize(blockSize).encoder(CharStream.roundEncoder(
                (data, end) -> {
                    if (!end) {
                        assertTrue(data.remaining() >= expectedBlockSize);
                        if (blockSize < expectedBlockSize) {
                            assertEquals(data.remaining(), expectedBlockSize);
                        } else {
                            assertTrue(data.remaining() <= (blockSize / expectedBlockSize + 1) * expectedBlockSize);
                            assertTrue(data.remaining() >= (blockSize / expectedBlockSize) * expectedBlockSize);
                        }
                    }
                    CharBuffer bb = CharBuffer.allocate(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((char) expectedBlockSize);
                    }
                    bb.flip();
                    return bb;
                },
                expectedBlockSize
            )).writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = CharStream.from(src).blockSize(blockSize).encoder(CharStream.roundEncoder(
                (data, end) -> {
                    if (!end) {
                        assertTrue(data.remaining() >= expectedBlockSize);
                        if (blockSize < expectedBlockSize) {
                            assertEquals(data.remaining(), expectedBlockSize);
                        } else {
                            assertTrue(data.remaining() <= (blockSize / expectedBlockSize + 1) * expectedBlockSize);
                            assertTrue(data.remaining() >= (blockSize / expectedBlockSize) * expectedBlockSize);
                        }
                    }
                    CharBuffer bb = CharBuffer.allocate(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((char) expectedBlockSize);
                    }
                    bb.flip();
                    return bb;
                },
                expectedBlockSize
            )).writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
        }
    }

    @Test
    public void testBufferedEncoder() {
        testBufferedEncoder(100, 5, 6);
        testBufferedEncoder(10086, 11, 333);
        testBufferedEncoder(10086, 333, 11);
        testBufferedEncoder(10086, 22, 22);
        testBufferedEncoder(10086, 333, 1);
    }

    private void testBufferedEncoder(int size, int blockSize, int eatNum) {
        {
            // bytes
            byte[] src = JieRandom.fill(new byte[size]);
            byte[] dst = new byte[src.length];
            long len = ByteStream.from(src).blockSize(blockSize).encoder(ByteStream.bufferedEncoder(
                (data, end) -> {
                    if (end) {
                        return data;
                    }
                    byte[] bb = new byte[Math.min(data.remaining(), eatNum)];
                    data.get(bb);
                    return ByteBuffer.wrap(bb);
                }
            )).writeTo(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
        }
        {
            // chars
            char[] src = JieRandom.fill(new char[size]);
            char[] dst = new char[src.length];
            long len = CharStream.from(src).blockSize(blockSize).encoder(CharStream.bufferedEncoder(
                (data, end) -> {
                    if (end) {
                        return data;
                    }
                    char[] bb = new char[Math.min(data.remaining(), eatNum)];
                    data.get(bb);
                    return CharBuffer.wrap(bb);
                }
            )).writeTo(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
        }
    }

    private static final class NioIn extends InputStream {

        private int i = 0;
        private final InputStream in;

        public NioIn() {
            this(null);
        }

        public NioIn(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull byte[] b, int off, int len) throws IOException {
            if (i++ < 3) {
                return 0;
            }
            int actualLen = len <= 1 ? len : len / 2;
            if (in != null) {
                return in.read(b, off, actualLen);
            } else {
                Arrays.fill(b, off, off + actualLen, (byte) 1);
                return actualLen;
            }
        }

        public void reset() {
            i = 0;
        }
    }

    private static final class NioReader extends Reader {

        private int i = 0;
        private final Reader in;

        public NioReader() {
            this(null);
        }

        public NioReader(Reader in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull char[] b, int off, int len) throws IOException {
            if (i++ < 3) {
                return 0;
            }
            int actualLen = len <= 1 ? len : len / 2;
            if (in != null) {
                return in.read(b, off, actualLen);
            } else {
                Arrays.fill(b, off, off + actualLen, (char) 1);
                return actualLen;
            }
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
