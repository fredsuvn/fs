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

public class StreamTest {

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
        long readNum = ByteSource.from(in).readLimit(0).to(out);
        assertEquals(readNum, 0);
        readNum = ByteSource.from(in).readLimit(1).to(out);
        assertEquals(readNum, 1);
        assertEquals(str.substring(0, 1), new String(Arrays.copyOfRange(out.toByteArray(), 0, 1), JieChars.UTF_8));
        in.reset();
        out.reset();
        readNum = ByteSource.from(in).encoder((b, e) -> {
            int len = b.remaining();
            byte[] bs = new byte[len * 2];
            b.get(bs, 0, len);
            b.flip();
            b.get(bs, len, len);
            return ByteBuffer.wrap(bs);
        }).to(out);
        assertEquals(readNum, size);
        assertEquals(str + str, new String(out.toByteArray(), JieChars.UTF_8));

        // nio
        NioIn nioIn = new NioIn();
        byte[] nioBytes = new byte[size];
        readNum = ByteSource.from(nioIn).readLimit(nioBytes.length).to(nioBytes);
        assertEquals(readNum, size);
        byte[] compareBytes = Arrays.copyOf(nioBytes, nioBytes.length);
        Arrays.fill(compareBytes, (byte) 1);
        assertEquals(nioBytes, compareBytes);
        nioIn.reset();
        Arrays.fill(nioBytes, (byte) 2);
        Arrays.fill(compareBytes, (byte) 2);
        readNum = ByteSource.from(nioIn).endOnZeroRead(true).to(nioBytes);
        assertEquals(readNum, 0);
        assertEquals(nioBytes, compareBytes);

        // error
        expectThrows(IORuntimeException.class, () -> testBytesStream(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> ByteSource.from((InputStream) null).to((OutputStream) null));
        expectThrows(IORuntimeException.class, () -> ByteSource.from(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteSource.from(new byte[0]).to(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteSource.from(new byte[0]).to((OutputStream) null));
        expectThrows(IORuntimeException.class, () -> ByteSource.from((InputStream) null).to(new byte[0]));
        Method method = ByteSource.from(new byte[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, ByteSource.from(new byte[0]), "");
        method = ByteSource.from(new byte[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, ByteSource.from(new byte[0]), "");
        expectThrows(IORuntimeException.class, () -> ByteSource.from(new ThrowIn(0)).to(new byte[0]));
        expectThrows(IORuntimeException.class, () -> ByteSource.from(new ThrowIn(1)).to(new byte[0]));
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
            long readNum = ByteSource.from(in).blockSize(blockSize).readLimit(readLimit).to(out);
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
            long readNum = ByteSource.from(in).blockSize(blockSize).to(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, 0, bytes.length, JieChars.UTF_8));
            outBytes = new byte[bytes.length * 2];
            in.reset();
            readNum = ByteSource.from(in).blockSize(blockSize).to(outBytes, offset, bytes.length);
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
            long readNum = ByteSource.from(in).blockSize(blockSize).to(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBuffer = TU.bufferDangling(bytes);
            in.reset();
            readNum = ByteSource.from(in).blockSize(blockSize).to(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // byte[] -> stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteSource.from(bytes).blockSize(blockSize).readLimit(readLimit).to(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> byte[]
            byte[] outBytes = new byte[bytes.length];
            long readNum = ByteSource.from(bytes).blockSize(blockSize).readLimit(readLimit).to(outBytes);
            assertEquals(readNum, getLength(size, readLimit));
            assertEquals(
                Arrays.copyOfRange(bytes, 0, getLength(size, readLimit)),
                Arrays.copyOfRange(outBytes, 0, getLength(size, readLimit))
            );
            outBytes = new byte[bytes.length];
            readNum = ByteSource.from(bytes).blockSize(blockSize).to(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            byte[] inBytes = new byte[bytes.length * 2];
            outBytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, inBytes, offset, bytes.length);
            readNum = ByteSource.from(inBytes, offset, bytes.length).blockSize(blockSize).to(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = ByteSource.from(bytes, 0, bytes.length)
                .blockSize(blockSize).to(outBytes, 0, outBytes.length);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = ByteSource
                .from(bytes, 0, bytes.length - 1)
                .blockSize(blockSize)
                .to(outBytes, 0, outBytes.length - 1);
            assertEquals(readNum, bytes.length - 1);
            assertEquals(
                str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outBytes, 0, outBytes.length - 1), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> buffer
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteSource.from(bytes).blockSize(blockSize).readLimit(readLimit).to(outBuffer);
            assertEquals(readNum, getLength(size, readLimit));
            outBuffer.flip();
            assertEquals(Arrays.copyOfRange(bytes, 0, getLength(size, readLimit)), JieBytes.getBytes(outBuffer));
            outBuffer = ByteBuffer.allocateDirect(bytes.length);
            readNum = ByteSource.from(bytes).blockSize(blockSize).to(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> stream
            ByteBuffer inBuffer = JieBytes.copyBuffer(bytes, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
            ByteBuffer inArray = TU.bufferDangling(bytes);
            out.reset();
            readNum = ByteSource.from(inArray).blockSize(blockSize).readLimit(readLimit).to(out);
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
            long readNum = ByteSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(outBytes);
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), Arrays.copyOfRange(outBytes, 0, getLength(size, readLimit)));
            inBuffer = JieBytes.copyBuffer(bytes, true);
            outBytes = new byte[bytes.length];
            readNum = ByteSource.from(inBuffer).blockSize(blockSize).to(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> buffer
            ByteBuffer inBuffer = TU.bufferDangling(bytes);
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(outBuffer);
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), JieBytes.getBytes(outBuffer));
            inBuffer = TU.bufferDangling(bytes);
            outBuffer = TU.bufferDangling(bytes);
            readNum = ByteSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(outBuffer);
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
        long readNum = CharSource.from(in).readLimit(0).to(out);
        assertEquals(readNum, 0);
        readNum = CharSource.from(in).readLimit(1).to(out);
        assertEquals(readNum, 1);
        assertEquals(str.substring(0, 1), new String(Arrays.copyOfRange(out.toCharArray(), 0, 1)));
        in.reset();
        out.reset();
        readNum = CharSource.from(in).encoder((b, e) -> {
            int len = b.remaining();
            char[] bs = new char[len * 2];
            b.get(bs, 0, len);
            b.flip();
            b.get(bs, len, len);
            return CharBuffer.wrap(bs);
        }).to(out);
        assertEquals(readNum, size);
        assertEquals(str + str, new String(out.toCharArray()));

        // nio
        NioReader nioReader = new NioReader();
        char[] nioChars = new char[size];
        readNum = CharSource.from(nioReader).readLimit(nioChars.length).to(nioChars);
        assertEquals(readNum, size);
        char[] compareChars = Arrays.copyOf(nioChars, nioChars.length);
        Arrays.fill(compareChars, (char) 1);
        assertEquals(nioChars, compareChars);
        nioReader.reset();
        Arrays.fill(nioChars, (char) 2);
        Arrays.fill(compareChars, (char) 2);
        readNum = CharSource.from(nioReader).endOnZeroRead(true).to(nioChars);
        assertEquals(readNum, 0);
        assertEquals(nioChars, compareChars);

        // error
        expectThrows(IORuntimeException.class, () -> testCharsStream(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> CharSource.from((Reader) null).to((Appendable) null));
        expectThrows(IORuntimeException.class, () -> CharSource.from(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharSource.from(new char[0]).to(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharSource.from(new char[0]).to((Appendable) null));
        expectThrows(IORuntimeException.class, () -> CharSource.from((Reader) null).to(new char[0]));
        Method method = CharSource.from(new char[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, CharSource.from(new char[0]), 1);
        method = CharSource.from(new char[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, CharSource.from(new char[0]), "");
        expectThrows(IORuntimeException.class, () -> CharSource.from(new ThrowReader(0)).to(new char[0]));
        expectThrows(IORuntimeException.class, () -> CharSource.from(new ThrowReader(1)).to(new char[0]));
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
            long readNum = CharSource.from(in).blockSize(blockSize).readLimit(readLimit).to(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // string -> stream
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharSource.from(str).blockSize(blockSize).readLimit(readLimit).to(out);
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
            long readNum = CharSource.from(dirInBuffer).blockSize(blockSize).readLimit(readLimit).to(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            outBuilder.setLength(0);
            readNum = CharSource.from(dirInBuffer).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).to(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            StringWriter sw = new StringWriter();
            readNum = CharSource.from(dirInBuffer).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).to(sw);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), sw.toString());
        }

        {
            // stream -> char[]
            char[] outChars = new char[chars.length];
            CharArrayReader in = new CharArrayReader(chars);
            in.mark(0);
            long readNum = CharSource.from(in).blockSize(blockSize).to(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length * 2];
            in.reset();
            readNum = CharSource.from(in).blockSize(blockSize).to(outChars, offset, chars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(Arrays.copyOfRange(outChars, offset, offset + chars.length)));
        }

        {
            // stream -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(dirBuffer);
            CharArrayReader in = new CharArrayReader(chars);
            long readNum = CharSource.from(in).blockSize(blockSize).to(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outChars = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outChars));
        }

        // char[] -> stream
        {
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharSource.from(chars).blockSize(blockSize).readLimit(readLimit).to(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            out.reset();
            readNum = CharSource.from(chars).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).to(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharSource.from(chars).blockSize(blockSize).to(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            readNum = CharSource.from(chars).blockSize(blockSize).readLimit(readLimit).to(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            char[] inChars = new char[chars.length * 2];
            outChars = new char[chars.length];
            System.arraycopy(chars, 0, inChars, offset, chars.length);
            readNum = CharSource.from(inChars, offset, chars.length).blockSize(blockSize).to(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharSource.from(chars, 0, chars.length)
                .blockSize(blockSize).to(outChars, 0, outChars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharSource.from(chars, 0, chars.length - 1)
                .blockSize(blockSize).to(outChars, 0, outChars.length - 1);
            assertEquals(readNum, chars.length - 1);
            assertEquals(str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outChars, 0, outChars.length - 1)));
        }

        {
            // char[] -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(chars, true);
            long readNum = CharSource.from(chars).blockSize(blockSize).to(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            assertEquals(str, new String(JieChars.getChars(outBuffer)));
            outBuffer = JieChars.copyBuffer(chars, true);
            readNum = CharSource.from(chars).blockSize(blockSize).readLimit(readLimit).to(outBuffer);
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
            long readNum = CharSource.from(chars).blockSize(blockSize).to(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.setLength(0);
            readNum = CharSource.from(chars).blockSize(blockSize).readLimit(readLimit).to(appender);
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
            long readNum = CharSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            inBuffer.reset();
            out.reset();
            readNum = CharSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).to(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            CharBuffer arrayIn = TU.bufferDangling(chars);
            CharBuffer arrayOut = TU.bufferDangling(new char[chars.length]);
            readNum = CharSource.from(arrayIn).blockSize(blockSize).readLimit(readLimit).to(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(arrayOut), 0, getLength(chars.length, readLimit))
            );
            arrayIn.flip();
            arrayOut.flip();
            readNum = CharSource.from(arrayIn).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).to(arrayOut);
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
            long readNum = CharSource.from(inBuffer).blockSize(blockSize).to(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            inBuffer.reset();
            outChars = new char[chars.length];
            readNum = CharSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(outChars);
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
            long readNum = CharSource.from(inBuffer).blockSize(blockSize).to(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            inBuffer.reset();
            appender.setLength(0);
            readNum = CharSource.from(inBuffer).blockSize(blockSize).readLimit(readLimit).to(appender);
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
            long readNum = CharSource.from(inBuffer).blockSize(blockSize).to(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outBytes = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outBytes));
        }

        {
            // charSeq -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharSource.from(str).blockSize(blockSize).to(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharSource.from(str).blockSize(blockSize).readLimit(readLimit).to(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            outChars = new char[chars.length];
            readNum = CharSource.from(JieString.asChars(str.toCharArray())).blockSize(blockSize).to(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharSource.from(JieString.asChars(str.toCharArray()))
                .blockSize(blockSize).readLimit(readLimit).to(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // charSeq -> appender
            StringBuilder appender = new StringBuilder();
            long readNum = CharSource.from(str).blockSize(blockSize).to(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.setLength(0);
            readNum = CharSource.from(str).blockSize(blockSize).readLimit(readLimit).to(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
            appender.setLength(0);
            readNum = CharSource.from(str).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).to(appender);
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
                ByteSource.from(new byte[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).to(new byte[100]);
            } catch (IOEncodingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
        {
            Throwable[] ts = new Throwable[1];
            try {
                CharSource.from(new char[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).to(new char[100]);
            } catch (IOEncodingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
    }

    private void testEncoder(int size, int blockSize) {
        {
            // bytes
            byte[] endBytes = "end".getBytes(JieChars.defaultCharset());
            ByteSource.Encoder bytesEn = (data, end) -> {
                if (!end) {
                    assertEquals(data.remaining(), blockSize);
                } else {
                    assertEquals(data.remaining(), size % blockSize);
                }
                ByteBuffer bb = ByteBuffer.allocate(end ? data.remaining() + endBytes.length : data.remaining());
                bb.put(data);
                if (end) {
                    bb.put(endBytes);
                }
                bb.flip();
                return bb;
            };
            byte[] bSrc = JieRandom.fill(new byte[size], 0, 9);
            byte[] bDst = new byte[bSrc.length + endBytes.length];
            long c = ByteSource.from(bSrc).blockSize(blockSize).encoder(bytesEn).to(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            bSrc = JieRandom.fill(new byte[size], 0, 9);
            c = ByteSource.from(new ByteArrayInputStream(bSrc)).blockSize(blockSize).encoder(bytesEn).to(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            bSrc = JieRandom.fill(new byte[size], 0, 9);
            c = ByteSource.from(new NioIn(new ByteArrayInputStream(bSrc))).blockSize(blockSize).encoder(bytesEn).to(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            bSrc = JieRandom.fill(new byte[size], 0, 9);
            c = ByteSource.from(ByteBuffer.wrap(bSrc)).blockSize(blockSize).encoder(bytesEn).to(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
            assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            c = ByteSource.from(new NioIn()).blockSize(blockSize).encoder(bytesEn).endOnZeroRead(true).to(bOut);
            assertEquals(c, 0);
            assertEquals(bOut.toByteArray(), new byte[0]);
            // encoders
            bSrc = JieRandom.fill(new byte[size], 0, 9);
            bDst = new byte[bSrc.length * 2];
            c = ByteSource.from(bSrc).encoders(Jie.list(
                (d, b) -> JieBytes.emptyBuffer(),
                (d, b) -> d
            )).to(bDst);
            assertEquals(c, bSrc.length);
            assertEquals(bDst, new byte[bSrc.length * 2]);
            bSrc = JieRandom.fill(new byte[size], 1, 2);
            bDst = new byte[bSrc.length * 2];
            c = ByteSource.from(bSrc).encoders(Jie.list(
                (d, b) -> d,
                (d, b) -> {
                    byte[] bs = new byte[d.remaining() * 2];
                    Arrays.fill(bs, (byte) 2);
                    return ByteBuffer.wrap(bs);
                }
            )).to(bDst);
            assertEquals(c, bSrc.length);
            byte[] bb = new byte[bSrc.length * 2];
            Arrays.fill(bb, (byte) 2);
            assertEquals(bDst, bb);
        }

        {
            // chars
            char[] endChars = "end".toCharArray();
            CharSource.Encoder charsEn = (data, end) -> {
                if (!end) {
                    assertEquals(data.remaining(), blockSize);
                } else {
                    assertEquals(data.remaining(), size % blockSize);
                }
                CharBuffer cb = CharBuffer.allocate(end ? data.remaining() + endChars.length : data.remaining());
                cb.put(data);
                if (end) {
                    cb.put(endChars);
                }
                cb.flip();
                return cb;
            };
            char[] cSrc = JieRandom.fill(new char[size], '0', '9');
            char[] cDst = new char[cSrc.length + endChars.length];
            long c = CharSource.from(new CharArrayReader(cSrc)).blockSize(blockSize).encoder(charsEn).to(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[size], '0', '9');
            c = CharSource.from(new String(cSrc)).blockSize(blockSize).encoder(charsEn).to(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[size], '0', '9');
            c = CharSource.from(new NioReader(new CharArrayReader(cSrc))).blockSize(blockSize).encoder(charsEn).to(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[size], '0', '9');
            c = CharSource.from(CharBuffer.wrap(cSrc)).blockSize(blockSize).encoder(charsEn).to(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            cSrc = JieRandom.fill(new char[size], '0', '9');
            c = CharSource.from(cSrc).blockSize(blockSize).encoder(charsEn).to(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
            assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
            CharArrayWriter cOut = new CharArrayWriter();
            c = CharSource.from(new NioReader()).blockSize(blockSize).encoder(charsEn).endOnZeroRead(true).to(cOut);
            assertEquals(c, 0);
            assertEquals(cOut.toCharArray(), new char[0]);
            // encoders
            cSrc = JieRandom.fill(new char[size], 0, 9);
            cDst = new char[cSrc.length * 2];
            c = CharSource.from(cSrc).encoders(Jie.list(
                (d, b) -> JieChars.emptyBuffer(),
                (d, b) -> d
            )).to(cDst);
            assertEquals(c, cSrc.length);
            assertEquals(cDst, new char[cSrc.length * 2]);
            cSrc = JieRandom.fill(new char[size], 1, 2);
            cDst = new char[cSrc.length * 2];
            c = CharSource.from(cSrc).encoders(Jie.list(
                (d, b) -> d,
                (d, b) -> {
                    char[] bs = new char[d.remaining() * 2];
                    Arrays.fill(bs, (char) 2);
                    return CharBuffer.wrap(bs);
                }
            )).to(cDst);
            assertEquals(c, cSrc.length);
            char[] bb = new char[cSrc.length * 2];
            Arrays.fill(bb, (char) 2);
            assertEquals(cDst, bb);
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
            long len = ByteSource.from(src).blockSize(blockSize).encoder(ByteSource.roundEncoder(
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
            )).to(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = ByteSource.from(src).blockSize(blockSize).encoder(ByteSource.roundEncoder(
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
            )).to(dst2);
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
            long len = CharSource.from(src).blockSize(blockSize).encoder(CharSource.roundEncoder(
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
            )).to(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = CharSource.from(src).blockSize(blockSize).encoder(CharSource.roundEncoder(
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
            )).to(dst2);
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
            long len = ByteSource.from(src).blockSize(blockSize).encoder(ByteSource.bufferedEncoder(
                (data, end) -> {
                    if (end) {
                        return data;
                    }
                    byte[] bb = new byte[Math.min(data.remaining(), eatNum)];
                    data.get(bb);
                    return ByteBuffer.wrap(bb);
                }
            )).to(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
        }
        {
            // chars
            char[] src = JieRandom.fill(new char[size]);
            char[] dst = new char[src.length];
            long len = CharSource.from(src).blockSize(blockSize).encoder(CharSource.bufferedEncoder(
                (data, end) -> {
                    if (end) {
                        return data;
                    }
                    char[] bb = new char[Math.min(data.remaining(), eatNum)];
                    data.get(bb);
                    return CharBuffer.wrap(bb);
                }
            )).to(dst);
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
