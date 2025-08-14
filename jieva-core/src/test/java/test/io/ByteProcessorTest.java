package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.ByteProcessor;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.ByteTransformer;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.AssertTest;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ByteProcessorTest implements DataTest, AssertTest {

    @Test
    public void testProcess() throws Exception {
        testProcess(0, 123, 0);
        testProcess(0, 123, 77);
        testProcess(0, 123, 123);
        testProcess(0, 123, 1333);
        testProcess(1333, 123, 0);
        testProcess(1333, 123, 77);
        testProcess(1333, 123, 123);
        testProcess(1333, 123, 777);
        testProcess(123, 1333, 0);
        testProcess(123, 1333, 77);
        testProcess(123, 1333, 123);
        testProcess(123, 1333, 777);
        testProcess(123, 123, 1333);
        testProcess(123, 77, 1333);
        testProcess(77, 123, 1333);
        {
            // exceptions
            expectThrows(IllegalArgumentException.class, () -> ByteProcessor.from(new byte[0]).readBlockSize(0));
            expectThrows(IllegalArgumentException.class, () -> ByteProcessor.from(new byte[0]).readBlockSize(-1));
            expectThrows(IllegalArgumentException.class, () -> ByteProcessor.from(new byte[0]).readLimit(-1));
            expectThrows(IndexOutOfBoundsException.class, () -> ByteProcessor.from(new byte[0], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> ByteProcessor.from(new byte[0]).processTo(new byte[0], 1));
            TestInputStream err = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            err.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () ->
                ByteProcessor.from(err).process());
            expectThrows(IORuntimeException.class, () ->
                ByteProcessor.from(new byte[10]).processTo(new ErrorOutputStream()));
            expectThrows(IORuntimeException.class, () ->
                ByteProcessor.from(new byte[10]).processTo(Channels.newChannel(new ErrorOutputStream())));
            expectThrows(IORuntimeException.class, () ->
                ByteProcessor.from(new byte[10]).processTo(new byte[1]));
            expectThrows(IORuntimeException.class, () ->
                ByteProcessor.from(new byte[10], 0, 5).processTo(new byte[1], 0));
            expectThrows(IORuntimeException.class, () ->
                ByteProcessor.from(new byte[10]).processTo(ByteBuffer.allocate(0)));
            Method writeTo = ByteProcessor.from(new byte[0]).getClass()
                .getDeclaredMethod("writeTo", ByteBuffer.class, Object.class);
            invokeThrows(UnsupportedOperationException.class, writeTo, ByteProcessor.from(new byte[0]),
                ByteBuffer.allocate(10), String.class);
        }
    }

    private void testProcess(int totalSize, int readBlockSize, int limit) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        byte[] limitedData = Arrays.copyOf(data, dstSize(totalSize, limit));
        byte[] timesData = timesData(data);
        byte[] limitedTimesData = timesData(limitedData);
        {
            // size effect
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).process(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).process(),
                actualSize(totalSize, limit)
            );
            // with transformers
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .process(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .process(),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
        }
        {
            // to OutputStream
            BytesBuilder builder = new BytesBuilder();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).processTo(builder),
                actualSize(totalSize, limit)
            );
            assertEquals(builder.toByteArray(), limitedData);
            builder.reset();
            // with transformers
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toByteArray(), timesData);
            builder.reset();
            endCount.clear();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(builder),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toByteArray(), limitedTimesData);
            builder.reset();
            endCount.clear();
        }
        {
            // to channel
            BytesBuilder builder = new BytesBuilder();
            WritableByteChannel channel = Channels.newChannel(builder);
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).processTo(channel),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).processTo(channel),
                actualSize(totalSize, limit)
            );
            assertEquals(builder.toByteArray(), limitedData);
            builder.reset();
            // with transformers
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(channel),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toByteArray(), timesData);
            builder.reset();
            endCount.clear();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(channel),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toByteArray(), limitedTimesData);
            builder.reset();
            endCount.clear();
        }
        {
            // to array
            byte[] dst = new byte[totalSize];
            assertEquals(
                ByteProcessor.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize).processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(dst, data);
            dst = new byte[limitedData.length];
            assertEquals(
                ByteProcessor.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit).processTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(dst, limitedData);
            // with transformers
            dst = new byte[timesData.length];
            assertEquals(
                ByteProcessor.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(dst, timesData);
            endCount.clear();
            dst = new byte[dstSize(totalSize, limit) * 4];
            assertEquals(
                ByteProcessor.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(dst, limitedTimesData);
            endCount.clear();
        }
        {
            // to array offset
            byte[] dst = new byte[totalSize + 5];
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).processTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), data);
            dst = new byte[limitedData.length + 5];
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).processTo(dst, 5),
                actualSize(totalSize, limit)
            );
            assertEquals(
                Arrays.copyOfRange(dst, 5, dst.length),
                limitedData
            );
            // with transformers
            dst = new byte[timesData.length + 5];
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), timesData);
            endCount.clear();
            dst = new byte[limitedTimesData.length + 5];
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst, 5),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(
                Arrays.copyOfRange(dst, 5, dst.length),
                limitedTimesData
            );
            endCount.clear();
        }
        {
            // to buffer
            ByteBuffer dst = ByteBuffer.allocate(totalSize);
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(BufferKit.copyContent((ByteBuffer) dst.flip()), data);
            dst = ByteBuffer.allocate(limitedData.length);
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).processTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(
                BufferKit.copyContent((ByteBuffer) dst.flip()),
                limitedData
            );
            // with transformers
            dst = ByteBuffer.allocate(timesData.length);
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(BufferKit.copyContent((ByteBuffer) dst.flip()), timesData);
            endCount.clear();
            dst = ByteBuffer.allocate(limitedTimesData.length);
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(
                BufferKit.copyContent((ByteBuffer) dst.flip()),
                limitedTimesData
            );
            endCount.clear();
        }
    }

    @Test
    public void testAsInputStreamAndReader() throws Exception {
        testAsInputStreamAndReader(0, 123, 0);
        testAsInputStreamAndReader(0, 123, 37);
        testAsInputStreamAndReader(0, 123, 123);
        testAsInputStreamAndReader(0, 123, 1333);
        testAsInputStreamAndReader(1333, 123, 0);
        testAsInputStreamAndReader(1333, 123, 37);
        testAsInputStreamAndReader(1333, 123, 123);
        testAsInputStreamAndReader(1333, 123, 777);
        testAsInputStreamAndReader(123, 1333, 0);
        testAsInputStreamAndReader(123, 1333, 37);
        testAsInputStreamAndReader(123, 1333, 123);
        testAsInputStreamAndReader(123, 1333, 777);
        testAsInputStreamAndReader(123, 123, 1333);
        testAsInputStreamAndReader(123, 77, 1333);
        testAsInputStreamAndReader(77, 123, 1333);
        {
            // exception
            TestInputStream err = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            err.setNextOperation(ReadOps.THROW, 99);
            ByteReader reader = ByteReader.from(err);
            expectThrows(IOException.class, () ->
                ByteProcessor.from(reader).transformer(ByteTransformer.empty()).asInputStream().read());
            expectThrows(IOException.class, () ->
                ByteProcessor.from(reader).transformer(ByteTransformer.empty()).asInputStream().close());
        }
    }

    private void testAsInputStreamAndReader(int totalSize, int readBlockSize, int limit) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        byte[] limitedData = Arrays.copyOf(data, dstSize(totalSize, limit));
        byte[] timesData = timesData(data);
        byte[] limitedTimesData = timesData(limitedData);
        {
            assertEquals(
                IOKit.read(ByteProcessor.from(data).readBlockSize(readBlockSize).asInputStream()),
                data.length == 0 ? null : data
            );
            assertEquals(
                IOKit.read(ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).asInputStream()),
                limitedData.length == 0 ? null : limitedData
            );
            assertEquals(
                IOKit.read(ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asInputStream()),
                timesData.length == 0 ? null : timesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                IOKit.read(ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asInputStream()),
                limitedTimesData.length == 0 ? null : limitedTimesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testInputStream(
                ByteProcessor.from(data).readBlockSize(readBlockSize).asInputStream(),
                data,
                false, false, true
            );
            IOImplsTest.testInputStream(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).asInputStream(),
                limitedData,
                false, false, true
            );
            IOImplsTest.testInputStream(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asInputStream(),
                timesData,
                false, true, false
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testInputStream(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asInputStream(),
                limitedTimesData,
                false, true, false
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
        }
        {
            // for empty
            assertEquals(
                IOKit.read(ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.empty())
                    .asInputStream()),
                data.length == 0 ? null : data
            );
            IOImplsTest.testInputStream(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.empty())
                    .asInputStream(),
                data,
                false, true, false
            );
            IOImplsTest.testInputStream(
                ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(ByteTransformer.empty())
                    .asInputStream(),
                limitedData,
                false, true, false
            );
        }
        {
            // one by one
            InputStream in = ByteProcessor.from(data).readBlockSize(readBlockSize)
                .transformer(ByteTransformer.empty())
                .asInputStream();
            BytesBuilder builder = new BytesBuilder();
            while (true) {
                int next = in.read();
                if (next < 0) {
                    break;
                }
                builder.append(next);
            }
            assertEquals(builder.toByteArray(), data);
            assertEquals(in.read(), -1);
        }
        {
            // reader
            ByteSegment readData = ByteProcessor.from(data).readBlockSize(readBlockSize)
                .asByteReader().read(data.length + 1);
            assertEquals(readData.toByteArray(), data);
            assertTrue(readData.end());
            readData = ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .asByteReader().read(limitedData.length + 1);
            assertEquals(readData.toByteArray(), limitedData);
            assertTrue(readData.end());
            readData = ByteProcessor.from(data).readBlockSize(readBlockSize)
                .transformer(timesTransformer(readBlockSize, endCount))
                .transformer(timesTransformer(readBlockSize, endCount))
                .asByteReader().read(timesData.length + 1);
            assertEquals(readData.toByteArray(), timesData);
            assertTrue(readData.end());
            assertEquals(endCount.get(), 2);
            endCount.clear();
            readData = ByteProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .transformer(timesTransformer(readBlockSize, endCount))
                .transformer(timesTransformer(readBlockSize, endCount))
                .asByteReader().read(limitedTimesData.length + 1);
            assertEquals(readData.toByteArray(), limitedTimesData);
            assertTrue(readData.end());
            assertEquals(endCount.get(), 2);
            endCount.clear();
        }
    }

    private int actualSize(int totalSize, int limit) {
        if (totalSize <= 0 || limit <= 0) {
            return -1;
        }
        return Math.min(totalSize, limit);
    }

    private int dstSize(int totalSize, int limit) {
        if (totalSize <= 0 || limit <= 0) {
            return 0;
        }
        return Math.min(totalSize, limit);
    }

    private ByteTransformer timesTransformer(int readBlockSize, IntVar endCount) {
        return ByteTransformer.withFixedSize((d, e) -> {
            if (e) {
                endCount.incrementAndGet();
            }
            ByteBuffer ret = ByteBuffer.allocate(d.remaining() * 2);
            while (d.hasRemaining()) {
                byte next = d.get();
                ret.put(next);
                ret.put(next);
            }
            ret.flip();
            return ret;
        }, readBlockSize + 1);
    }

    private byte[] timesData(byte[] data) {
        byte[] ret = new byte[data.length * 4];
        for (int i = 0; i < data.length; i++) {
            ret[i * 4] = data[i];
            ret[i * 4 + 1] = data[i];
            ret[i * 4 + 2] = data[i];
            ret[i * 4 + 3] = data[i];
        }
        return ret;
    }

    @Test
    public void testResidualSizeTransformer() throws Exception {
        testResidualSizeTransformer(0, 123, 37);
        testResidualSizeTransformer(0, 123, 123);
        testResidualSizeTransformer(0, 123, 1333);
        testResidualSizeTransformer(1333, 123, 37);
        testResidualSizeTransformer(1333, 123, 123);
        testResidualSizeTransformer(1333, 123, 777);
        testResidualSizeTransformer(123, 1333, 37);
        testResidualSizeTransformer(123, 1333, 123);
        testResidualSizeTransformer(123, 1333, 777);
        testResidualSizeTransformer(123, 123, 1333);
        testResidualSizeTransformer(123, 77, 1333);
        testResidualSizeTransformer(77, 123, 1333);
        testResidualSizeTransformer(256, 64, 32);
        testResidualSizeTransformer(256, 32, 64);
        {
            // exception
            expectThrows(IllegalArgumentException.class, () ->
                ByteProcessor.from(new byte[0])
                    .transformer(ByteTransformer.withFixedSize(ByteTransformer.empty(), -1)));
            expectThrows(IllegalArgumentException.class, () ->
                ByteProcessor.from(new byte[0])
                    .transformer(ByteTransformer.withFixedSize(ByteTransformer.empty(), 0)));
            expectThrows(IllegalArgumentException.class, () ->
                ByteProcessor.from(new byte[0])
                    .transformer(ByteTransformer.withMultipleSize(ByteTransformer.empty(), -1)));
            expectThrows(IllegalArgumentException.class, () ->
                ByteProcessor.from(new byte[0])
                    .transformer(ByteTransformer.withMultipleSize(ByteTransformer.empty(), 0)));
        }
    }

    private void testResidualSizeTransformer(int totalSize, int readBlockSize, int blockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        BytesBuilder builder = new BytesBuilder();
        {
            // FixedSizeHandler
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withFixedSize((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                        } else {
                            assertEquals(d.remaining(), blockSize);
                        }
                        return d;
                    }, blockSize))
                    .processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            assertEquals(endCount.get(), 1);
            builder.reset();
            endCount.clear();
        }
        {
            // MultipleSizeHandler
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withMultipleSize((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                        } else {
                            assertEquals(d.remaining() % blockSize, 0);
                        }
                        return d;
                    }, blockSize))
                    .processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            assertEquals(endCount.get(), 1);
            builder.reset();
            endCount.clear();
        }
    }

    @Test
    public void testBufferedTransformer() throws Exception {
        testBufferedTransformer(0, 123);
        testBufferedTransformer(123, 123);
        testBufferedTransformer(123, 1234);
        testBufferedTransformer(123, 1);
        testBufferedTransformer(123, 2);
        testBufferedTransformer(123, 3);
        testBufferedTransformer(128, 16);
    }

    private void testBufferedTransformer(int totalSize, int readBlockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        BytesBuilder builder = new BytesBuilder();
        {
            // BufferedHandler
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            assertEquals(endCount.get(), 1);
            builder.reset();
            endCount.clear();
        }
    }

    @Test
    public void testTo() throws Exception {
        testTo(0, 123);
        testTo(123, 123);
        testTo(123, 1234);
        testTo(123, 1);
        testTo(123, 2);
        testTo(123, 3);
        testTo(128, 16);
    }

    private void testTo(int totalSize, int readBlockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        char[] chars = randomChars(totalSize, 'a', 'z');
        String str = new String(chars);
        byte[] data = str.getBytes(CharsKit.defaultCharset());
        {
            // toArray
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toByteArray(),
                data
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
        {
            // toArray
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toByteBuffer(),
                ByteBuffer.wrap(data)
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
        {
            // toArray
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toString(),
                str
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toString(CharsKit.defaultCharset()),
                str
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
        {
            // toEncoder
            assertEquals(
                ByteProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(ByteTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toCharEncoder(CharsKit.defaultCharset())
                    .toString(),
                str
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
    }
}
