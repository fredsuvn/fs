package test.base.bytes;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieMath;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.ByteProcessor;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieTestException;
import xyz.sunqian.test.MaterialBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;
import static xyz.sunqian.common.base.bytes.ByteEncoder.withBuffering;
import static xyz.sunqian.common.base.bytes.ByteEncoder.withRounding;
import static xyz.sunqian.test.MaterialBox.copyDirect;

public class ByteProcessorTest {

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
    }

    private void testProcessing(int totalSize, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[totalSize], 'a', 'z'));
        byte[] bytes = str.getBytes(JieChars.defaultCharset());

        {
            // stream -> stream
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            in.mark(0);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteProcessor.from(in).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.defaultCharset())
            );
        }

        {
            // stream -> byte[]
            byte[] outBytes = new byte[bytes.length];
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            long readNum = ByteProcessor.from(in).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, 0, bytes.length, JieChars.defaultCharset()));
            outBytes = new byte[bytes.length * 2];
            in.reset();
            readNum = ByteProcessor.from(in).readBlockSize(blockSize).writeTo(outBytes, offset, bytes.length);
            assertEquals(readNum, bytes.length);
            assertEquals(
                str,
                new String(Arrays.copyOfRange(outBytes, offset, offset + bytes.length), JieChars.defaultCharset())
            );
        }
        {
            // stream -> buffer
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            long readNum = ByteProcessor.from(in).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBuffer.read(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
            outBuffer = MaterialBox.copyPadding(bytes);
            in.reset();
            readNum = ByteProcessor.from(in).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            outBytes = JieBuffer.read(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
        }

        {
            // byte[] -> stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteProcessor.from(bytes).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.defaultCharset())
            );
        }

        {
            // byte[] -> byte[]
            byte[] outBytes = new byte[bytes.length];
            long readNum = ByteProcessor.from(bytes).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBytes);
            assertEquals(readNum, getLength(totalSize, readLimit));
            assertEquals(
                Arrays.copyOfRange(bytes, 0, getLength(totalSize, readLimit)),
                Arrays.copyOfRange(outBytes, 0, getLength(totalSize, readLimit))
            );
            outBytes = new byte[bytes.length];
            readNum = ByteProcessor.from(bytes).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
            byte[] inBytes = new byte[bytes.length * 2];
            outBytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, inBytes, offset, bytes.length);
            readNum = ByteProcessor.from(inBytes, offset, bytes.length).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
            outBytes = new byte[bytes.length];
            readNum = ByteProcessor.from(bytes, 0, bytes.length)
                .readBlockSize(blockSize).writeTo(outBytes, 0, outBytes.length);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
            outBytes = new byte[bytes.length];
            readNum = ByteProcessor.from(bytes, 0, bytes.length - 1)
                .readBlockSize(blockSize)
                .writeTo(outBytes, 0, outBytes.length - 1);
            assertEquals(readNum, bytes.length - 1);
            assertEquals(
                str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outBytes, 0, outBytes.length - 1), JieChars.defaultCharset())
            );
        }

        {
            // byte[] -> buffer
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteProcessor.from(bytes).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(totalSize, readLimit));
            outBuffer.flip();
            assertEquals(Arrays.copyOfRange(bytes, 0, getLength(totalSize, readLimit)), JieBuffer.read(outBuffer));
            outBuffer = ByteBuffer.allocateDirect(bytes.length);
            readNum = ByteProcessor.from(bytes).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, bytes.length);
            outBuffer.flip();
            byte[] outBytes = JieBuffer.read(outBuffer);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
        }

        {
            // buffer -> stream
            ByteBuffer inBuffer = copyDirect(bytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long readNum = ByteProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.defaultCharset())
            );
            ByteBuffer inArray = MaterialBox.copyPadding(bytes);
            out.reset();
            readNum = ByteProcessor.from(inArray).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(bytes.length, readLimit));
            assertEquals(
                str.substring(0, getLength(bytes.length, readLimit)),
                new String(out.toByteArray(), 0, getLength(bytes.length, readLimit), JieChars.defaultCharset())
            );
        }

        {
            // buffer -> byte[]
            ByteBuffer inBuffer = copyDirect(bytes);
            byte[] outBytes = new byte[bytes.length];
            long readNum = ByteProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBytes);
            assertEquals(readNum, getLength(totalSize, readLimit));
            inBuffer.flip();
            assertEquals(JieBuffer.read(inBuffer), Arrays.copyOfRange(outBytes, 0, getLength(totalSize, readLimit)));
            inBuffer = copyDirect(bytes);
            outBytes = new byte[bytes.length];
            readNum = ByteProcessor.from(inBuffer).readBlockSize(blockSize).writeTo(outBytes);
            assertEquals(readNum, bytes.length);
            assertEquals(str, new String(outBytes, JieChars.defaultCharset()));
        }

        {
            // buffer -> buffer
            ByteBuffer inBuffer = MaterialBox.copyPadding(bytes);
            ByteBuffer outBuffer = ByteBuffer.allocateDirect(bytes.length);
            long readNum = ByteProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(totalSize, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            assertEquals(JieBuffer.read(inBuffer), JieBuffer.read(outBuffer));
            inBuffer = MaterialBox.copyPadding(bytes);
            outBuffer = MaterialBox.copyPadding(bytes);
            readNum = ByteProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(totalSize, readLimit));
            inBuffer.flip();
            outBuffer.flip();
            byte[] outBytes = JieBuffer.read(outBuffer);
            assertEquals(JieBuffer.read(inBuffer), outBytes);
        }

        {
            // any -> null
            long[] counter = {0};
            long readNum = ByteProcessor.from(new byte[totalSize])
                .readBlockSize(blockSize)
                .readLimit(readLimit)
                .encoder(((data, end) -> {
                    counter[0] += data.remaining();
                    return data;
                }))
                .process();
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
                ByteProcessor.from(new byte[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).writeTo(new byte[100]);
            } catch (ProcessingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
    }

    private void testEncoder(int totalSize, int blockSize) {
        {
            // from byte array
            byte[] src = JieArray.fill(new byte[totalSize], (byte) 6);
            BytesBuilder dst = new BytesBuilder();
            ByteProcessor.from(src).readBlockSize(blockSize).encoder((d, e) -> {
                BytesBuilder dst0 = new BytesBuilder();
                while (d.hasRemaining()) {
                    byte b = d.get();
                    dst0.append(b);
                    dst0.append(b);
                }
                d.flip();
                while (d.hasRemaining()) {
                    d.put((byte) 9);
                }
                return dst0.toByteBuffer();
            }).writeTo(dst);
            assertEquals(dst.toByteArray(), JieArray.fill(new byte[totalSize * 2], (byte) 6));
            assertEquals(src, JieArray.fill(new byte[totalSize], (byte) 9));
        }
        {
            // from byte buffer
            byte[] srcBytes = JieArray.fill(new byte[totalSize], (byte) 6);
            ByteBuffer src = ByteBuffer.allocateDirect(srcBytes.length);
            src.put(srcBytes);
            src.flip();
            BytesBuilder dst = new BytesBuilder();
            ByteProcessor.from(src).readBlockSize(blockSize).encoder((d, e) -> {
                BytesBuilder dst0 = new BytesBuilder();
                while (d.hasRemaining()) {
                    byte b = d.get();
                    dst0.append(b);
                    dst0.append(b);
                }
                d.flip();
                while (d.hasRemaining()) {
                    d.put((byte) 9);
                }
                return dst0.toByteBuffer();
            }).writeTo(dst);
            assertEquals(dst.toByteArray(), JieArray.fill(new byte[totalSize * 2], (byte) 6));
            src.flip();
            assertEquals(JieBuffer.read(src), JieArray.fill(new byte[totalSize], (byte) 9));
        }
        {
            // from stream
            byte[] srcBytes = JieArray.fill(new byte[totalSize], (byte) 6);
            ByteArrayInputStream src = new ByteArrayInputStream(srcBytes);
            BytesBuilder dst = new BytesBuilder();
            ByteProcessor.from(src).readBlockSize(blockSize).encoder((d, e) -> {
                BytesBuilder dst0 = new BytesBuilder();
                while (d.hasRemaining()) {
                    byte b = d.get();
                    dst0.append(b);
                    dst0.append(b);
                }
                d.flip();
                while (d.hasRemaining()) {
                    d.put((byte) 9);
                }
                return dst0.toByteBuffer();
            }).writeTo(dst);
            assertEquals(dst.toByteArray(), JieArray.fill(new byte[totalSize * 2], (byte) 6));
            src.reset();
            assertEquals(JieIO.read(src), JieArray.fill(new byte[totalSize], (byte) 6));
        }
    }

    @Test
    public void testRoundingEncoder() {
        testRoundingEncoder(100, 5, 6);
        testRoundingEncoder(100, 200, 60);
        testRoundingEncoder(10086, 11, 333);
        testRoundingEncoder(10086, 333, 11);
        testRoundingEncoder(10086, 22, 22);
        testRoundingEncoder(10086, 222, 1);
        testRoundingEncoder(222, 10086, 1);
        testRoundingEncoder(223, 2233, 2);
    }

    private void testRoundingEncoder(int totalSize, int blockSize, int expectedBlockSize) {
        {
            byte[] src = JieRandom.fill(new byte[totalSize]);
            byte[] dst = new byte[src.length * 2];
            for (int i = 0; i < src.length; i++) {
                dst[i * 2] = src[i];
                dst[i * 2 + 1] = (byte) expectedBlockSize;
            }
            byte[] dst2 = new byte[src.length * 2];
            long len = ByteProcessor.from(src).readBlockSize(blockSize)
                .encoder(withRounding(expectedBlockSize, (data, end) -> {
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
                }))
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = ByteProcessor.from(src).readBlockSize(blockSize)
                .encoder(withRounding(expectedBlockSize, (data, end) -> {
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
                }))
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
        }
        {
            // null
            byte[] src = JieRandom.fill(new byte[totalSize]);
            BytesBuilder builder = new BytesBuilder();
            ByteProcessor.from(src).readBlockSize(blockSize)
                .encoder(withRounding(expectedBlockSize, (data, end) -> null))
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testBufferingEncoder() {
        testBufferingEncoder(100, 5, 6);
        testBufferingEncoder(100, 200, 60);
        testBufferingEncoder(10086, 11, 333);
        testBufferingEncoder(10086, 333, 11);
        testBufferingEncoder(10086, 22, 22);
        testBufferingEncoder(10086, 333, 1);
        testBufferingEncoder(333, 10086, 1);
        testBufferingEncoder(233, 2333, 2);
    }

    private void testBufferingEncoder(int size, int blockSize, int eatNum) {
        {
            byte[] src = JieRandom.fill(new byte[size]);
            byte[] dst = new byte[src.length];
            boolean[] buffer = {true};
            long len = ByteProcessor.from(src).readBlockSize(blockSize)
                .encoder(withBuffering((data, end) -> {
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
                }))
                .writeTo(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
        }
        {
            // null
            byte[] src = JieRandom.fill(new byte[size]);
            BytesBuilder builder = new BytesBuilder();
            ByteProcessor.from(src).readBlockSize(blockSize)
                .encoder(withBuffering((data, end) -> null))
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
        testFixedSizeEncoder(10240, 1024, 512);
        testFixedSizeEncoder(1024, 1024, 1024);
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
            long len = ByteProcessor.from(src).readBlockSize(blockSize)
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
            ByteProcessor.from(src).readBlockSize(blockSize)
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
            InputStream in = ByteProcessor.from(new byte[0]).toInputStream();
            assertEquals(in.read(), -1);
            assertEquals(in.read(), -1);
            assertEquals(in.read(new byte[1], 0, 0), 0);
            assertEquals(in.skip(-1), 0);
            assertEquals(in.skip(0), 0);
            assertEquals(in.available(), 0);
            in.close();
            in.close();
            expectThrows(IOException.class, () -> in.read());
            InputStream nio = ByteProcessor.from(new NioIn()).endOnZeroRead(true).toInputStream();
            assertEquals(nio.read(), -1);
            InputStream empty = ByteProcessor.from(new byte[]{9}).encoder(((data, end) -> {
                BytesBuilder bb = new BytesBuilder();
                bb.append(data);
                if (end) {
                    bb.append(new byte[]{1, 2, 3});
                }
                return bb.toByteBuffer();
            })).toInputStream();
            assertEquals(JieIO.read(empty), new byte[]{9, 1, 2, 3});
            assertEquals(empty.read(), -1);
            InputStream err1 = ByteProcessor.from(new ThrowIn(0)).toInputStream();
            expectThrows(IOException.class, () -> err1.close());
            InputStream err2 = ByteProcessor.from(new ThrowIn(2)).toInputStream();
            expectThrows(IOException.class, () -> err2.close());
            InputStream err3 = ByteProcessor.from(new ThrowIn(3)).toInputStream();
            expectThrows(IOException.class, () -> err3.read());
        }
        {
            boolean[] flag = {true};
            InputStream in = ByteProcessor.from(new byte[1024]).readBlockSize(1).encoder(((data, end) -> {
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
            InputStream in = ByteProcessor.from(src).readBlockSize(blockSize).encoder(((data, end) -> {
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
            InputStream in = ByteProcessor.from(src).readBlockSize(blockSize).encoder(((data, end) -> {
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
            InputStream in = ByteProcessor.from(src).readBlockSize(blockSize).encoder(((data, end) -> {
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
            InputStream in = ByteProcessor.from(src).readBlockSize(blockSize).toInputStream();
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
            byte[] bytes = new String(str).getBytes(JieChars.defaultCharset());
            String converted = JieIO.string(
                ByteProcessor.from(bytes).readBlockSize(blockSize).toCharProcessor(JieChars.defaultCharset()).toReader()
            );
            assertEquals(converted.toCharArray(), str);
        }
        {
            char[] str = JieRandom.fill(new char[totalSize], '\u4e00', '\u9fff');
            byte[] bytes = new String(str).getBytes(JieChars.defaultCharset());
            String converted = JieIO.string(
                ByteProcessor.from(bytes).readBlockSize(blockSize).toCharProcessor(JieChars.defaultCharset()).toReader()
            );
            assertEquals(converted.toCharArray(), str);
        }
    }

    @Test
    public void testSpecial() throws Exception {
        {
            // empty
            BytesBuilder bb = new BytesBuilder();
            long c;
            c = ByteProcessor.from(new byte[0]).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(new byte[0]).writeTo(new byte[0]);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(new byte[0]).writeTo(ByteBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(JieBytes.emptyBuffer()).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(JieBytes.emptyBuffer()).writeTo(new byte[0]);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(JieBytes.emptyBuffer()).writeTo(ByteBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(new ByteArrayInputStream(new byte[0])).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
        }

        {
            // endOnZeroRead
            BytesBuilder bb = new BytesBuilder();
            long c;
            c = ByteProcessor.from(new NioIn()).endOnZeroRead(true)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toByteArray(), new byte[0]);
            c = ByteProcessor.from(new NioIn(new ByteArrayInputStream(new byte[0]))).endOnZeroRead(false)
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
            ByteProcessor.from(src).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((byte) 2);
                }
                return data;
            })).process();
            assertEquals(src, target);
            Arrays.fill(src, (byte) 1);
            assertNotEquals(src, target);
            ByteProcessor.from(ByteBuffer.wrap(src)).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((byte) 2);
                }
                return data;
            })).process();
            assertEquals(src, target);
        }

        {
            // writeTo
            String str = "1234567890qwertyuiop[]中文";
            byte[] strBytes = str.getBytes(JieChars.defaultCharset());
            assertEquals(ByteProcessor.from(strBytes).toByteArray(), strBytes);
            assertEquals(ByteProcessor.from(strBytes).toByteBuffer(), ByteBuffer.wrap(strBytes));
            assertEquals(ByteProcessor.from(strBytes).toString(), str);
        }

        // error
        expectThrows(IllegalArgumentException.class, () -> testProcessing(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> ByteProcessor.from((InputStream) null).writeTo((OutputStream) null));
        expectThrows(IndexOutOfBoundsException.class, () -> ByteProcessor.from(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteProcessor.from(new byte[0]).writeTo(new byte[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> ByteProcessor.from(new byte[0]).writeTo((OutputStream) null));
        expectThrows(IORuntimeException.class, () -> ByteProcessor.from((InputStream) null).writeTo(new byte[0]));
        expectThrows(IORuntimeException.class, () -> ByteProcessor.from(new ThrowIn(0)).writeTo(new byte[0]));
        expectThrows(IORuntimeException.class, () -> ByteProcessor.from(new ThrowIn(1)).writeTo(new byte[0]));
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
