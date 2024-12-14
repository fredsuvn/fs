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
import java.util.Arrays;

import static org.testng.Assert.*;

public class ByteStreamTest {

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

        {
            // empty
            BytesBuilder bb = new BytesBuilder();
            long c;
            c = ByteStream.from(new byte[0]).writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(new byte[0]).writeTo(new byte[0]);
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(new byte[0]).writeTo(ByteBuffer.allocate(0));
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(JieBytes.emptyBuffer()).writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(JieBytes.emptyBuffer()).writeTo(new byte[0]);
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(JieBytes.emptyBuffer()).writeTo(ByteBuffer.allocate(0));
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(new ByteArrayInputStream(new byte[0])).writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
        }

        {
            // endOnZeroRead
            BytesBuilder bb = new BytesBuilder();
            long c;
            c = ByteStream.from(new NioIn()).endOnZeroRead(true)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteStream.from(new NioIn(new ByteArrayInputStream(new byte[0]))).endOnZeroRead(false)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, -1);
            assertEquals(bb.toByteArray(), new byte[0]);
        }

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
                ByteStream.from(new byte[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).writeTo(new byte[100]);
            } catch (IOEncodingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
    }

    private void testEncoder(int totalSize, int blockSize) {
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

    @Test
    public void testRoundEncoder() {
        testRoundEncoder(100, 5, 6);
        testRoundEncoder(10086, 11, 333);
        testRoundEncoder(10086, 333, 11);
        testRoundEncoder(10086, 22, 22);
        testRoundEncoder(10086, 222, 1);
    }

    private void testRoundEncoder(int size, int blockSize, int expectedBlockSize) {
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

    @Test
    public void testBufferedEncoder() {
        testBufferedEncoder(100, 5, 6);
        testBufferedEncoder(10086, 11, 333);
        testBufferedEncoder(10086, 333, 11);
        testBufferedEncoder(10086, 22, 22);
        testBufferedEncoder(10086, 333, 1);
    }

    private void testBufferedEncoder(int size, int blockSize, int eatNum) {
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
}
