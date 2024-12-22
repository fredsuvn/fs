package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieMath;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.io.*;
import xyz.sunqian.test.JieTest;
import xyz.sunqian.test.JieTestException;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.*;

public class BytesProcessorTest {

    @Test
    public void testProcessing() throws Exception {
        // readTo()
        testProcessing(666, JieIO.BUFFER_SIZE, -1);
        testProcessing(666, 67, -1);
        testProcessing(666, 1, -1);
        testProcessing(100, 10, -1);
        testProcessing(666, JieIO.BUFFER_SIZE, -1);
        testProcessing(666, 67, 667);
        testProcessing(666, 1, 667);
        testProcessing(100, 10, 101);
        testProcessing(222, 33, 55);
        testProcessing(100, 10, 0);
        testProcessing(100, 10, 100);
        testProcessing(6666, 99, 77777);

        {
            // empty
            BytesBuilder bb = new BytesBuilder();
            long c;
            c = JieIO.processor(new byte[0]).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(new byte[0]).writeTo(new byte[0]);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(new byte[0]).writeTo(ByteBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(JieBytes.emptyBuffer()).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(JieBytes.emptyBuffer()).writeTo(new byte[0]);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(JieBytes.emptyBuffer()).writeTo(ByteBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(new ByteArrayInputStream(new byte[0])).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
        }

        {
            // endOnZeroRead
            BytesBuilder bb = new BytesBuilder();
            long c;
            c = JieIO.processor(new NioIn()).endOnZeroRead(true)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = JieIO.processor(new NioIn(new ByteArrayInputStream(new byte[0]))).endOnZeroRead(false)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
        }

        {
            // writeable
            byte[] src = new byte[1024];
            byte[] target = new byte[1024];
            Arrays.fill(src, (byte) 1);
            Arrays.fill(target, (byte) 2);
            assertNotEquals(src, target);
            JieIO.processor(src).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((byte) 2);
                }
                return data;
            })).writeTo();
            assertEquals(src, target);
            Arrays.fill(src, (byte) 1);
            assertNotEquals(src, target);
            JieIO.processor(ByteBuffer.wrap(src)).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((byte) 2);
                }
                return data;
            })).writeTo();
            assertEquals(src, target);
            JieIO.processor(new ByteArrayInputStream(src)).readBlockSize(3).encoder(((data, end) -> {
                assertTrue(data.isReadOnly());
                return data;
            })).writeTo();
        }

        {
            // writeTo
            String str = "1234567890qwertyuiop[]中文";
            byte[] strBytes = str.getBytes(JieChars.UTF_8);
            assertEquals(JieIO.processor(strBytes).writeToByteArray(), strBytes);
            assertEquals(JieIO.processor(strBytes).writeToByteBuffer(), ByteBuffer.wrap(strBytes));
            assertEquals(JieIO.processor(strBytes).writeToString(JieChars.UTF_8), str);
        }

        // error
        expectThrows(IORuntimeException.class, () -> testProcessing(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> JieIO.processor((InputStream) null).writeTo((OutputStream) null));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.processor(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> JieIO.processor(new byte[0]).writeTo(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> JieIO.processor(new byte[0]).writeTo((OutputStream) null));
        expectThrows(IORuntimeException.class, () -> JieIO.processor((InputStream) null).writeTo(new byte[0]));
        Method method = JieIO.processor(new byte[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, JieIO.processor(new byte[0]), "");
        method = JieIO.processor(new byte[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        JieTest.testThrow(IORuntimeException.class, method, JieIO.processor(new byte[0]), "");
        expectThrows(IORuntimeException.class, () -> JieIO.processor(new ThrowIn(0)).writeTo(new byte[0]));
        expectThrows(IORuntimeException.class, () -> JieIO.processor(new ThrowIn(1)).writeTo(new byte[0]));
    }

    private void testProcessing(int totalSize, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[totalSize], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.UTF_8);

        {
            // stream -> stream
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            in.mark(0);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = JieIO.processor(in).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
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
            long readNum = JieIO.processor(in).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, 0, bytes.length, JieChars.UTF_8));
            outBytes = new byte[bytes.length * 2];
            in.reset();
            readNum = JieIO.processor(in).readBlockSize(blockSize).writeTo(outBytes, offset, bytes.length);
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
            long readNum = JieIO.processor(in).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBuffer = TU.bufferDangling(bytes);
            in.reset();
            readNum = JieIO.processor(in).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // byte[] -> stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = JieIO.processor(bytes).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
        }

        {
            // byte[] -> byte[]
            byte[] outBytes = new byte[bytes.length];
            long readNum = JieIO.processor(bytes).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBytes);
            assertEquals(readNum, getLength(totalSize, readLimit));
            assertEquals(
                Arrays.copyOfRange(bytes, 0, getLength(totalSize, readLimit)),
                Arrays.copyOfRange(outBytes, 0, getLength(totalSize, readLimit))
            );
            outBytes = new byte[bytes.length];
            readNum = JieIO.processor(bytes).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            byte[] inBytes = new byte[bytes.length * 2];
            outBytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, inBytes, offset, bytes.length);
            readNum = JieIO.processor(inBytes, offset, bytes.length).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = JieIO.processor(bytes, 0, bytes.length)
                .readBlockSize(blockSize).writeTo(outBytes, 0, outBytes.length);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
            outBytes = new byte[bytes.length];
            readNum = BytesProcessor
                .from(bytes, 0, bytes.length - 1)
                .readBlockSize(blockSize)
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
            long readNum = JieIO.processor(bytes).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(totalSize, readLimit));
            outBuffer.flip();
            assertEquals(Arrays.copyOfRange(bytes, 0, getLength(totalSize, readLimit)), JieBytes.getBytes(outBuffer));
            outBuffer = ByteBuffer.allocateDirect(bytes.length);
            readNum = JieIO.processor(bytes).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> stream
            ByteBuffer inBuffer = JieBytes.copyBuffer(bytes, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = JieIO.processor(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.UTF_8)
            );
            ByteBuffer inArray = TU.bufferDangling(bytes);
            out.reset();
            readNum = JieIO.processor(inArray).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
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
            long readNum = JieIO.processor(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBytes);
            assertEquals(readNum, getLength(totalSize, readLimit));
            inBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), Arrays.copyOfRange(outBytes, 0, getLength(totalSize, readLimit)));
            inBuffer = JieBytes.copyBuffer(bytes, true);
            outBytes = new byte[bytes.length];
            readNum = JieIO.processor(inBuffer).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.UTF_8));
        }

        {
            // buffer -> buffer
            ByteBuffer inBuffer = TU.bufferDangling(bytes);
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = JieIO.processor(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(totalSize, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            assertEquals(JieBytes.getBytes(inBuffer), JieBytes.getBytes(outBuffer));
            inBuffer = TU.bufferDangling(bytes);
            outBuffer = TU.bufferDangling(bytes);
            readNum = JieIO.processor(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(totalSize, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            byte[] outBytes = JieBytes.getBytes(outBuffer);
            assertEquals(JieBytes.getBytes(inBuffer), outBytes);
        }

        {
            // any -> null
            long[] counter = {0};
            long readNum = JieIO.processor(new byte[totalSize])
                .readBlockSize(blockSize)
                .readLimit(readLimit)
                .encoder(((data, end) -> {
                    counter[0] += data.remaining();
                    return data;
                }))
                .writeTo();
            assertEquals(readNum, getLength(totalSize, readLimit));
            assertEquals(counter[0], getLength(totalSize, readLimit));
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
                JieIO.processor(new byte[100]).encoder((data, end) -> {
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
        {
            // simple
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
            BytesProcessor.Encoder encoder = (data, end) -> {
                byte[] bytes = JieBytes.getBytes(data);
                byte[] ret = new byte[bytes.length * 2];
                System.arraycopy(bytes, 0, ret, 0, bytes.length);
                System.arraycopy(bytes, 0, ret, bytes.length, bytes.length);
                return ByteBuffer.wrap(ret);
            };
            long count = JieIO.processor(src).readBlockSize(blockSize).encoder(encoder).encoder(encoder).writeTo(bb);
            assertEquals(count, totalSize);
            assertEquals(bb.toByteArray(), expectDst);
        }
        {
            // complex
            byte[] src = JieRandom.fill(new byte[totalSize]);
            BytesBuilder bb = new BytesBuilder();
            for (int i = 0, j = 0; i < src.length; i++) {
                if (j == 2) {
                    j = 0;
                    continue;
                }
                bb.append(src[i]);
                j++;
            }
            byte[] proc = bb.toByteArray();
            bb.reset();
            for (int i = 0, j = 0; i < proc.length; i++) {
                bb.append(proc[i]);
                if (j == 9) {
                    j = 0;
                    bb.append((byte) '\r');
                } else {
                    j++;
                }
            }
            proc = bb.toByteArray();
            bb.reset();
            boolean[] buffer = {true};
            long count = JieIO.processor(src).readBlockSize(blockSize)
                .roundEncoder(3, (data, end) -> {
                    BytesBuilder ret = new BytesBuilder();
                    int j = 0;
                    while (data.hasRemaining()) {
                        byte b = data.get();
                        if (j == 2) {
                            j = 0;
                            continue;
                        }
                        ret.append(b);
                        j++;
                    }
                    return ret.toByteBuffer();
                })
                .bufferedEncoder((data, end) -> {
                    if (end) {
                        return data;
                    }
                    ByteBuffer ret;
                    if (buffer[0]) {
                        ret = JieBytes.emptyBuffer();
                    } else {
                        ret = data;
                    }
                    buffer[0] = !buffer[0];
                    return ret;
                })
                .encoder(10, (data, end) -> {
                    if (data.remaining() == 10) {
                        byte[] ret = new byte[11];
                        data.get(ret, 0, 10);
                        ret[10] = '\r';
                        return ByteBuffer.wrap(ret);
                    } else {
                        return data;
                    }
                })
                .writeTo(bb);
            assertEquals(count, totalSize);
            assertEquals(bb.toByteArray(), proc);
        }
        {
            // null
            byte[] src = JieRandom.fill(new byte[totalSize]);
            byte[] dst = new byte[src.length];
            int[] pos = {0};
            BytesBuilder dst0 = new BytesBuilder();
            long c = JieIO.processor(src)
                .encoder((data, end) -> {
                    int len = data.remaining();
                    data.get(dst, pos[0], len);
                    pos[0] += len;
                    return null;
                }).encoder((data, end) -> data)
                .writeTo(dst0);
            assertEquals(c, totalSize);
            assertEquals(dst, src);
            assertEquals(dst0.size(), 0);
            byte[] dst1 = new byte[src.length];
            boolean[] buffer = {true};
            c = JieIO.processor(src)
                .bufferedEncoder((data, end) -> {
                    boolean b = buffer[0];
                    buffer[0] = !b;
                    return b ? data : null;
                }).encoder((data, end) -> data)
                .writeTo(dst1);
            assertEquals(c, totalSize);
            assertEquals(dst1, src);
            byte[] dst2 = new byte[src.length];
            boolean[] hit = {false};
            c = JieIO.processor(src)
                .encoder((data, end) -> null)
                .encoder((data, end) -> {
                    hit[0] = true;
                    return data;
                })
                .writeTo(dst0);
            assertEquals(c, totalSize);
            assertEquals(dst2, new byte[src.length]);
            assertEquals(dst0.size(), 0);
            assertFalse(hit[0]);
        }
    }

    @Test
    public void testRoundEncoder() {
        testRoundEncoder(100, 5, 6);
        testRoundEncoder(100, 200, 60);
        testRoundEncoder(10086, 11, 333);
        testRoundEncoder(10086, 333, 11);
        testRoundEncoder(10086, 22, 22);
        testRoundEncoder(10086, 222, 1);
        testRoundEncoder(222, 10086, 1);
        testRoundEncoder(223, 2233, 2);
    }

    private void testRoundEncoder(int totalSize, int blockSize, int expectedBlockSize) {
        {
            byte[] src = JieRandom.fill(new byte[totalSize]);
            byte[] dst = new byte[src.length * 2];
            for (int i = 0; i < src.length; i++) {
                dst[i * 2] = src[i];
                dst[i * 2 + 1] = (byte) expectedBlockSize;
            }
            byte[] dst2 = new byte[src.length * 2];
            long len = JieIO.processor(src).readBlockSize(blockSize)
                .roundEncoder(expectedBlockSize, (data, end) -> {
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
                })
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = JieIO.processor(src).readBlockSize(blockSize)
                .roundEncoder(expectedBlockSize, (data, end) -> {
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
                })
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
        }
        {
            // null
            byte[] src = JieRandom.fill(new byte[totalSize]);
            BytesBuilder builder = new BytesBuilder();
            JieIO.processor(src).readBlockSize(blockSize)
                .roundEncoder(expectedBlockSize, (data, end) -> null)
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testBufferedEncoder() {
        testBufferedEncoder(100, 5, 6);
        testBufferedEncoder(100, 200, 60);
        testBufferedEncoder(10086, 11, 333);
        testBufferedEncoder(10086, 333, 11);
        testBufferedEncoder(10086, 22, 22);
        testBufferedEncoder(10086, 333, 1);
        testBufferedEncoder(333, 10086, 1);
        testBufferedEncoder(233, 2333, 2);
    }

    private void testBufferedEncoder(int size, int blockSize, int eatNum) {
        {
            byte[] src = JieRandom.fill(new byte[size]);
            byte[] dst = new byte[src.length];
            boolean[] buffer = {true};
            long len = JieIO.processor(src).readBlockSize(blockSize)
                .bufferedEncoder((data, end) -> {
                    if (end) {
                        return data;
                    }
                    ByteBuffer ret;
                    if (buffer[0]) {
                        byte[] bb = new byte[Math.min(data.remaining(), eatNum)];
                        data.get(bb);
                        ret = ByteBuffer.wrap(bb);
                    } else {
                        ret = data;
                    }
                    buffer[0] = !buffer[0];
                    return ret;
                })
                .writeTo(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
        }
        {
            // null
            byte[] src = JieRandom.fill(new byte[size]);
            BytesBuilder builder = new BytesBuilder();
            JieIO.processor(src).readBlockSize(blockSize)
                .bufferedEncoder((data, end) -> null)
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testFixedSizeEncoder() {
        testFixedSizeEncoder(100, 5, 6);
        testFixedSizeEncoder(100, 200, 60);
        testFixedSizeEncoder(10086, 11, 333);
        testFixedSizeEncoder(10086, 333, 11);
        testFixedSizeEncoder(10086, 22, 22);
        testFixedSizeEncoder(10086, 333, 1);
        testFixedSizeEncoder(333, 10086, 1);
        testFixedSizeEncoder(10086, 20, 19);
        testFixedSizeEncoder(20, 40, 19);
    }

    private void testFixedSizeEncoder(int totalSize, int blockSize, int fixedSize) {
        {
            byte[] src = JieRandom.fill(new byte[totalSize]);
            int times = totalSize / fixedSize;
            BytesBuilder bytesBuilder = new BytesBuilder();
            int pos = 0;
            for (int i = 0; i < times; i++) {
                bytesBuilder.append(Arrays.copyOfRange(src, pos, pos + fixedSize));
                bytesBuilder.append((byte) '\r');
                bytesBuilder.append((byte) '\n');
                pos += fixedSize;
            }
            if (src.length > pos) {
                bytesBuilder.append(Arrays.copyOfRange(src, pos, src.length));
                bytesBuilder.append((byte) '\r');
                bytesBuilder.append((byte) '\n');
            }
            int portion = JieMath.leastPortion(totalSize, fixedSize);
            byte[] dst = new byte[src.length + portion * 2];
            long len = JieIO.processor(src).readBlockSize(blockSize)
                .encoder(fixedSize, (data, end) -> {
                    int remaining = data.remaining();
                    if (remaining == 0) {
                        return JieBytes.emptyBuffer();
                    }
                    byte[] bb = new byte[remaining + 2];
                    data.get(bb, 0, remaining);
                    bb[remaining] = '\r';
                    bb[remaining + 1] = '\n';
                    return ByteBuffer.wrap(bb);
                })
                .writeTo(dst);
            assertEquals(dst, bytesBuilder.toByteArray());
            assertEquals(len, src.length);
        }
        {
            // null
            byte[] src = JieRandom.fill(new byte[totalSize]);
            BytesBuilder builder = new BytesBuilder();
            JieIO.processor(src).readBlockSize(blockSize)
                .encoder(fixedSize, (data, end) -> null)
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testToInputStream() throws Exception {
        testToInputStream(10, 5);
        testToInputStream(10086, 11);
        testToInputStream(10086, 333);
        testToInputStream(10086, 22);
        testToInputStream(333, 10086);
        testToInputStream(20, 10086);
        testToInputStream(20, 40);
        {
            InputStream in = JieIO.processor(new byte[0]).toInputStream();
            assertEquals(in.read(), -1);
            assertEquals(in.read(), -1);
            assertEquals(in.read(new byte[1], 0, 0), 0);
            assertEquals(in.skip(-1), 0);
            assertEquals(in.skip(0), 0);
            assertEquals(in.available(), 0);
            in.close();
            in.close();
            expectThrows(IOException.class, () -> in.read());
            InputStream nio = JieIO.processor(new NioIn()).endOnZeroRead(true).toInputStream();
            assertEquals(nio.read(), -1);
            InputStream empty = JieIO.processor(new byte[]{9}).encoder(((data, end) -> {
                BytesBuilder bb = new BytesBuilder();
                bb.append(data);
                if (end) {
                    bb.append(new byte[]{1, 2, 3});
                }
                return bb.toByteBuffer();
            })).toInputStream();
            assertEquals(JieIO.read(empty), new byte[]{9, 1, 2, 3});
            assertEquals(empty.read(), -1);
            InputStream err1 = JieIO.processor(new ThrowIn(0)).toInputStream();
            expectThrows(IOException.class, () -> err1.close());
            InputStream err2 = JieIO.processor(new ThrowIn(2)).toInputStream();
            expectThrows(IOException.class, () -> err2.close());
            InputStream err3 = JieIO.processor(new ThrowIn(3)).toInputStream();
            expectThrows(IOException.class, () -> err3.read());
        }
        {
            boolean[] flag = {true};
            InputStream in = JieIO.processor(new byte[1024]).readBlockSize(1).encoder(((data, end) -> {
                ByteBuffer ret = flag[0] ? data : JieBytes.emptyBuffer();
                flag[0] = !flag[0];
                return ret;
            })).toInputStream();
            BytesBuilder builder = new BytesBuilder();
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                builder.append((byte) b);
            }
            assertEquals(builder.toByteArray().length, 1024 / 2);
        }
    }

    private void testToInputStream(int totalSize, int blockSize) throws Exception {
        byte[] src = JieRandom.fill(new byte[totalSize]);
        int times = totalSize / blockSize;
        BytesBuilder bb = new BytesBuilder();
        int pos = 0;
        for (int i = 0; i < times; i++) {
            bb.append(Arrays.copyOfRange(src, pos, pos + blockSize));
            bb.append((byte) '\r');
            pos += blockSize;
        }
        if (pos < totalSize) {
            bb.append(Arrays.copyOfRange(src, pos, totalSize));
            bb.append((byte) '\r');
        }
        byte[] encoded = bb.toByteArray();
        {
            InputStream in = JieIO.processor(src).readBlockSize(blockSize).encoder(((data, end) -> {
                if (!data.hasRemaining()) {
                    return data;
                }
                BytesBuilder b = new BytesBuilder();
                b.append(data);
                b.append((byte) '\r');
                return b.toByteBuffer();
            })).toInputStream();
            assertEquals(JieIO.read(in), encoded);
            assertEquals(in.read(), -1);
        }
        {
            InputStream in = JieIO.processor(src).readBlockSize(blockSize).encoder(((data, end) -> {
                if (!data.hasRemaining()) {
                    return data;
                }
                BytesBuilder b = new BytesBuilder();
                b.append(data);
                b.append((byte) '\r');
                return b.toByteBuffer();
            })).toInputStream();
            BytesBuilder builder = new BytesBuilder();
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                builder.append((byte) b);
            }
            assertEquals(builder.toByteArray(), encoded);
        }
        {
            InputStream in = JieIO.processor(src).readBlockSize(blockSize).encoder(((data, end) -> {
                if (!data.hasRemaining()) {
                    return data;
                }
                BytesBuilder b = new BytesBuilder();
                b.append(data);
                b.append((byte) '\r');
                return b.toByteBuffer();
            })).toInputStream();
            assertEquals(in.skip(666), Math.min(666, encoded.length));
            assertEquals(in.skip(1666), Math.min(1666, Math.max(encoded.length - 666, 0)));
        }
        {
            InputStream in = JieIO.processor(src).readBlockSize(blockSize).toInputStream();
            assertEquals(JieIO.read(in), src);
            assertEquals(in.read(), -1);
        }
    }

    @Test
    public void testToCharProcessor() {
        testToCharProcessor(100, 5);
        testToCharProcessor(10086, 11);
        testToCharProcessor(10086, 333);
        testToCharProcessor(10086, 22);
        testToCharProcessor(10086, 333);
        testToCharProcessor(10086, 20);
        testToCharProcessor(20, 40);
        testToCharProcessor(10086, 1);
    }

    private void testToCharProcessor(int totalSize, int blockSize) {
        {
            char[] str = JieRandom.fill(new char[totalSize], 'a', 'z');
            byte[] bytes = new String(str).getBytes(JieChars.UTF_8);
            String converted = JieIO.read(
                JieIO.processor(bytes).readBlockSize(blockSize).toCharProcessor(JieChars.UTF_8).toReader()
            );
            assertEquals(converted.toCharArray(), str);
        }
        {
            char[] str = JieRandom.fill(new char[totalSize], '\u4e00', '\u9fff');
            byte[] bytes = new String(str).getBytes(JieChars.UTF_8);
            String converted = JieIO.read(
                JieIO.processor(bytes).readBlockSize(blockSize).toCharProcessor(JieChars.UTF_8).toReader()
            );
            assertEquals(converted.toCharArray(), str);
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

        @Override
        public void close() throws IOException {
            if (e == 0) {
                throw new IOException("e == 0");
            }
            throw new IllegalArgumentException("e = " + e);
        }
    }
}
