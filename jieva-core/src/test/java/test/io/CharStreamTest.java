package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.*;
import xyz.sunqian.common.io.CharStream;
import xyz.sunqian.common.io.IOEncodingException;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieTest;
import xyz.sunqian.test.JieTestException;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.*;

public class CharStreamTest {

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

        {
            // empty
            StringBuilder bb = new StringBuilder();
            long c;
            c = CharStream.from(new char[0]).writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(new char[0]).writeTo(new char[0]);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(new char[0]).writeTo(CharBuffer.allocate(0));
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(JieChars.emptyBuffer()).writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(JieChars.emptyBuffer()).writeTo(new char[0]);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(JieChars.emptyBuffer()).writeTo(CharBuffer.allocate(0));
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(new CharArrayReader(new char[0])).writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from("").writeTo(new char[0]);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from("").writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
        }

        {
            // endOnZeroRead
            StringBuilder bb = new StringBuilder();
            long c;
            c = CharStream.from(new NioReader()).endOnZeroRead(true)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toString().toCharArray(), new char[0]);
            c = CharStream.from(new NioReader(new CharArrayReader(new char[0]))).endOnZeroRead(false)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toString().toCharArray(), new char[0]);
        }

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

        {
            // error
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

    @Test
    public void testRoundEncoder() {
        testRoundEncoder(100, 5, 6);
        testRoundEncoder(10086, 11, 333);
        testRoundEncoder(10086, 333, 11);
        testRoundEncoder(10086, 22, 22);
        testRoundEncoder(10086, 222, 1);
        testRoundEncoder(223, 2233, 2);
    }

    private void testRoundEncoder(int size, int blockSize, int expectedBlockSize) {
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

    @Test
    public void testBufferedEncoder() {
        testBufferedEncoder(100, 5, 6);
        testBufferedEncoder(10086, 11, 333);
        testBufferedEncoder(10086, 333, 11);
        testBufferedEncoder(10086, 22, 22);
        testBufferedEncoder(10086, 333, 1);
        testBufferedEncoder(233, 2333, 2);
    }

    private void testBufferedEncoder(int size, int blockSize, int eatNum) {
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

    @Test
    public void testFixedSizeEncoder() {
        testFixedSizeEncoder(100, 5, 6);
        testFixedSizeEncoder(10086, 11, 333);
        testFixedSizeEncoder(10086, 333, 11);
        testFixedSizeEncoder(10086, 22, 22);
        testFixedSizeEncoder(10086, 333, 1);
        testFixedSizeEncoder(10086, 20, 19);
        testFixedSizeEncoder(20, 40, 19);
    }

    private void testFixedSizeEncoder(int size, int blockSize, int fixedSize) {
        char[] src = JieRandom.fill(new char[size]);
        int times = size / fixedSize;
        StringBuilder charsBuilder = new StringBuilder();
        int pos = 0;
        for (int i = 0; i < times; i++) {
            charsBuilder.append(Arrays.copyOfRange(src, pos, pos + fixedSize));
            charsBuilder.append('\r');
            charsBuilder.append('\n');
            pos += fixedSize;
        }
        if (src.length > pos) {
            charsBuilder.append(Arrays.copyOfRange(src, pos, src.length));
            charsBuilder.append('\r');
            charsBuilder.append('\n');
        }
        int portion = JieMath.leastPortion(size, fixedSize);
        char[] dst = new char[src.length + portion * 2];
        long len = CharStream.from(src).blockSize(blockSize).encoder(CharStream.fixedSizeEncoder(
            (data, end) -> {
                int remaining = data.remaining();
                if (remaining == 0) {
                    return JieChars.emptyBuffer();
                }
                char[] bb = new char[remaining + 2];
                data.get(bb, 0, remaining);
                bb[remaining] = '\r';
                bb[remaining + 1] = '\n';
                return CharBuffer.wrap(bb);
            },
            fixedSize
        )).writeTo(dst);
        assertEquals(dst, charsBuilder.toString().toCharArray());
        assertEquals(len, src.length);
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
