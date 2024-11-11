package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.io.ByteStream;
import xyz.sunqian.common.io.CharStream;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieTest;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class StreamTest {

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
        testBytesTransfer(6666, 99, 77777);

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
        outBuffer.flip();
        byte[] outBufferContent = JieBytes.getBytes(outBuffer);
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

        // read empty
        assertEquals(JieIO.transfer(new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream()), -1);

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
        readNum = ByteStream.from(in).to(out).encoder((b, e) -> {
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
        readNum = ByteStream.from(nioIn).to(nioBytes).endOnZeroRead(true).start();
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

        {
            // stream -> stream
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            in.mark(0);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteStream.from(in).to(out).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = ByteStream.from(in).to(outBytes).blockSize(blockSize).start();
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, 0, bytes.length, JieChars.UTF_8));
            outBytes = new byte[bytes.length * 2];
            in.reset();
            readNum = ByteStream.from(in).to(outBytes, offset, bytes.length).blockSize(blockSize).start();
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
            long readNum = ByteStream.from(in).to(outBuffer).blockSize(blockSize).start();
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBuffer = TU.bufferDangling(bytes);
            in.reset();
            readNum = ByteStream.from(in).to(outBuffer).blockSize(blockSize).start();
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // byte[] -> stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteStream.from(bytes).to(out).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> byte[]
            byte[] outBytes = new byte[bytes.length];
            long readNum = ByteStream.from(bytes).to(outBytes).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(size, readLimit));
            assertEquals(
                Arrays.copyOfRange(bytes, 0, getLength(size, readLimit)),
                Arrays.copyOfRange(outBytes, 0, getLength(size, readLimit))
            );
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
            readNum = ByteStream.from(bytes, 0, bytes.length)
                .to(outBytes, 0, outBytes.length).blockSize(blockSize).start();
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = ByteStream
                .from(bytes, 0, bytes.length - 1)
                .to(outBytes, 0, outBytes.length - 1)
                .blockSize(blockSize).start();
            assertEquals(readNum, bytes.length - 1);
            assertEquals(
                str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outBytes, 0, outBytes.length - 1), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> buffer
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteStream.from(bytes).to(outBuffer).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(size, readLimit));
            outBuffer.flip();
            assertEquals(Arrays.copyOfRange(bytes, 0, getLength(size, readLimit)), JieBytes.getBytes(outBuffer));
            outBuffer = ByteBuffer.allocateDirect(bytes.length);
            readNum = ByteStream.from(bytes).to(outBuffer).blockSize(blockSize).start();
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> stream
            ByteBuffer inBuffer = JieBytes.copyBuffer(bytes, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteStream.from(inBuffer).to(out).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
            ByteBuffer inArray = TU.bufferDangling(bytes);
            out.reset();
            readNum = ByteStream.from(inArray).to(out).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = ByteStream.from(inBuffer).to(outBytes).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), Arrays.copyOfRange(outBytes, 0, getLength(size, readLimit)));
            inBuffer = JieBytes.copyBuffer(bytes, true);
            outBytes = new byte[bytes.length];
            readNum = ByteStream.from(inBuffer).to(outBytes).blockSize(blockSize).start();
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> buffer
            ByteBuffer inBuffer = TU.bufferDangling(bytes);
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteStream.from(inBuffer).to(outBuffer).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), JieBytes.getBytes(outBuffer));
            inBuffer = TU.bufferDangling(bytes);
            outBuffer = TU.bufferDangling(bytes);
            readNum = ByteStream.from(inBuffer).to(outBuffer).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(size, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(JieBytes.getBytes(inBuffer), outBytes);
        }
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
        testCharsTransfer(6666, 99, 77777);

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
        JieIO.transfer(in, outChars);
        assertEquals(chars, outChars);
        char[] outChars2 = new char[chars.length * 2];
        in.reset();
        JieIO.transfer(in, outChars2, offset, chars.length);
        assertEquals(chars, Arrays.copyOfRange(outChars2, offset, offset + chars.length));
        CharBuffer outBuffer = dirBuffer;
        in.reset();
        JieIO.transfer(in, outBuffer);
        outBuffer.flip();
        char[] outBufferContent = JieChars.getChars(outBuffer);
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

        // read empty
        assertEquals(JieIO.transfer(new CharArrayReader(new char[0]), new CharArrayWriter()), -1);

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
        readNum = CharStream.from(in).to(out).encoder((b, e) -> {
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
        readNum = CharStream.from(nioReader).to(nioChars).endOnZeroRead(true).start();
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
        CharBuffer dirBuffer = JieChars.copyBuffer(chars, true);

        {
            // stream -> stream
            CharArrayReader in = new CharArrayReader(chars);
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(in).to(out).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // string -> stream
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(str).to(out).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = CharStream.from(dirInBuffer).to(outBuilder).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            outBuilder.setLength(0);
            readNum = CharStream.from(dirInBuffer).to(outBuilder).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            StringWriter sw = new StringWriter();
            readNum = CharStream.from(dirInBuffer).to(sw).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), sw.toString());
        }

        {
            // stream -> char[]
            char[] outChars = new char[chars.length];
            CharArrayReader in = new CharArrayReader(chars);
            in.mark(0);
            long readNum = CharStream.from(in).to(outChars).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length * 2];
            in.reset();
            readNum = CharStream.from(in).to(outChars, offset, chars.length).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(Arrays.copyOfRange(outChars, offset, offset + chars.length)));
        }

        {
            // stream -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(dirBuffer);
            CharArrayReader in = new CharArrayReader(chars);
            long readNum = CharStream.from(in).to(outBuffer).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outChars = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outChars));
        }

        // char[] -> stream
        {
            CharArrayWriter out = new CharArrayWriter();
            long readNum = CharStream.from(chars).to(out).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            out.reset();
            readNum = CharStream.from(chars).to(out).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharStream.from(chars).to(outChars).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            readNum = CharStream.from(chars).to(outChars).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
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
        }

        {
            // char[] -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(chars, true);
            long readNum = CharStream.from(chars).to(outBuffer).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            assertEquals(str, new String(JieChars.getChars(outBuffer)));
            outBuffer = JieChars.copyBuffer(chars, true);
            readNum = CharStream.from(chars).to(outBuffer).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = CharStream.from(chars).to(appender).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.setLength(0);
            readNum = CharStream.from(chars).to(appender).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = CharStream.from(inBuffer).to(out).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            inBuffer.reset();
            out.reset();
            readNum = CharStream.from(inBuffer).to(out).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            CharBuffer arrayIn = TU.bufferDangling(chars);
            CharBuffer arrayOut = TU.bufferDangling(new char[chars.length]);
            readNum = CharStream.from(arrayIn).to(arrayOut).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(arrayOut), 0, getLength(chars.length, readLimit))
            );
            arrayIn.flip();
            arrayOut.flip();
            readNum = CharStream.from(arrayIn).to(arrayOut).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).start();
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
            long readNum = CharStream.from(inBuffer).to(outChars).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            inBuffer.reset();
            outChars = new char[chars.length];
            readNum = CharStream.from(inBuffer).to(outChars).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = CharStream.from(inBuffer).to(appender).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            inBuffer.reset();
            appender.setLength(0);
            readNum = CharStream.from(inBuffer).to(appender).blockSize(blockSize).readLimit(readLimit).start();
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
            long readNum = CharStream.from(inBuffer).to(outBuffer).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outBytes = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outBytes));
        }

        {
            // charSeq -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharStream.from(str).to(outChars).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharStream.from(str).to(outChars).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            outChars = new char[chars.length];
            readNum = CharStream.from(JieString.asChars(str.toCharArray())).to(outChars).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharStream.from(JieString.asChars(str.toCharArray())).to(outChars).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // charSeq -> appender
            StringBuilder appender = new StringBuilder();
            long readNum = CharStream.from(str).to(appender).blockSize(blockSize).start();
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.setLength(0);
            readNum = CharStream.from(str).to(appender).blockSize(blockSize).readLimit(readLimit).start();
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
            appender.setLength(0);
            readNum = CharStream.from(str).to(appender).blockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).start();
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
    }

    private void testEncoder(int size, int blockSize) {
        // bytes
        byte[] endBytes = "end".getBytes(JieChars.defaultCharset());
        ByteStream.Encoder bytesEn = (data, end) -> {
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
        long c = ByteStream.from(bSrc).to(bDst).blockSize(blockSize).encoder(bytesEn).start();
        assertEquals(c, bSrc.length);
        assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
        assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
        bSrc = JieRandom.fill(new byte[size], 0, 9);
        c = ByteStream.from(new ByteArrayInputStream(bSrc)).to(bDst).blockSize(blockSize).encoder(bytesEn).start();
        assertEquals(c, bSrc.length);
        assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
        assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
        bSrc = JieRandom.fill(new byte[size], 0, 9);
        c = ByteStream.from(new NioIn(new ByteArrayInputStream(bSrc))).to(bDst).blockSize(blockSize).encoder(bytesEn).start();
        assertEquals(c, bSrc.length);
        assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
        assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
        bSrc = JieRandom.fill(new byte[size], 0, 9);
        c = ByteStream.from(ByteBuffer.wrap(bSrc)).to(bDst).blockSize(blockSize).encoder(bytesEn).start();
        assertEquals(c, bSrc.length);
        assertEquals(Arrays.copyOfRange(bDst, 0, size), bSrc);
        assertEquals(Arrays.copyOfRange(bDst, bDst.length - 3, bDst.length), endBytes);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        c = ByteStream.from(new NioIn()).to(bOut).blockSize(blockSize).encoder(bytesEn).endOnZeroRead(true).start();
        assertEquals(c, 0);
        assertEquals(bOut.toByteArray(), new byte[0]);


        // chars
        char[] endChars = "end".toCharArray();
        CharStream.Encoder charsEn = (data, end) -> {
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
        c = CharStream.from(new CharArrayReader(cSrc)).to(cDst).blockSize(blockSize).encoder(charsEn).start();
        assertEquals(c, cSrc.length);
        assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
        assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
        cSrc = JieRandom.fill(new char[size], '0', '9');
        c = CharStream.from(new String(cSrc)).to(cDst).blockSize(blockSize).encoder(charsEn).start();
        assertEquals(c, cSrc.length);
        assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
        assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
        cSrc = JieRandom.fill(new char[size], '0', '9');
        c = CharStream.from(new NioReader(new CharArrayReader(cSrc))).to(cDst).blockSize(blockSize).encoder(charsEn).start();
        assertEquals(c, cSrc.length);
        assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
        assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
        cSrc = JieRandom.fill(new char[size], '0', '9');
        c = CharStream.from(CharBuffer.wrap(cSrc)).to(cDst).blockSize(blockSize).encoder(charsEn).start();
        assertEquals(c, cSrc.length);
        assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
        assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
        cSrc = JieRandom.fill(new char[size], '0', '9');
        c = CharStream.from(cSrc).to(cDst).blockSize(blockSize).encoder(charsEn).start();
        assertEquals(c, cSrc.length);
        assertEquals(Arrays.copyOfRange(cDst, 0, size), cSrc);
        assertEquals(Arrays.copyOfRange(cDst, cDst.length - 3, cDst.length), endChars);
        CharArrayWriter cOut = new CharArrayWriter();
        c = CharStream.from(new NioReader()).to(cOut).blockSize(blockSize).encoder(charsEn).endOnZeroRead(true).start();
        assertEquals(c, 0);
        assertEquals(cOut.toCharArray(), new char[0]);
    }

    @Test
    public void testBufferedEncoder() {
        testBufferedEncoder(100, 5, 6);
        testBufferedEncoder(10086, 11, 333);
        testBufferedEncoder(10086, 333, 11);
        testBufferedEncoder(10086, 22, 22);
    }

    private void testBufferedEncoder(int size, int blockSize, int expectedBlockSize) {
        {
            // bytes
            byte[] src = JieRandom.fill(new byte[size]);
            byte[] dst = new byte[src.length];
            ByteStream.from(src).to(dst).blockSize(blockSize).encoder(ByteStream.bufferedEncoder(
                (data, end) -> data,
                expectedBlockSize,
                null
            )).start();
            assertEquals(src, dst);
            dst = new byte[src.length];
            ByteStream.from(src).to(dst).blockSize(blockSize).encoder(ByteStream.bufferedEncoder(
                (data, end) -> data,
                expectedBlockSize,
                d -> d
            )).start();
            assertEquals(src, dst);
        }
        {
            // chars
            char[] src = JieRandom.fill(new char[size]);
            char[] dst = new char[src.length];
            CharStream.from(src).to(dst).blockSize(blockSize).encoder(CharStream.bufferedEncoder(
                (data, end) -> data,
                expectedBlockSize,
                null
            )).start();
            assertEquals(src, dst);
            dst = new char[src.length];
            CharStream.from(src).to(dst).blockSize(blockSize).encoder(CharStream.bufferedEncoder(
                (data, end) -> data,
                expectedBlockSize,
                d -> d
            )).start();
            assertEquals(src, dst);
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
