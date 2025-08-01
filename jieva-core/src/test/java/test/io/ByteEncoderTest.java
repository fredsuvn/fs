package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.ByteEncoder;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
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

public class ByteEncoderTest implements DataTest, AssertTest {

    @Test
    public void testEncode() throws Exception {
        testEncode(0, 123, 0);
        testEncode(0, 123, 77);
        testEncode(0, 123, 123);
        testEncode(0, 123, 1333);
        testEncode(1333, 123, 0);
        testEncode(1333, 123, 77);
        testEncode(1333, 123, 123);
        testEncode(1333, 123, 777);
        testEncode(123, 1333, 0);
        testEncode(123, 1333, 77);
        testEncode(123, 1333, 123);
        testEncode(123, 1333, 777);
        testEncode(123, 123, 1333);
        testEncode(123, 77, 1333);
        testEncode(77, 123, 1333);
        {
            // exceptions
            expectThrows(IllegalArgumentException.class, () -> ByteEncoder.from(new byte[0]).readBlockSize(0));
            expectThrows(IllegalArgumentException.class, () -> ByteEncoder.from(new byte[0]).readBlockSize(-1));
            expectThrows(IllegalArgumentException.class, () -> ByteEncoder.from(new byte[0]).readLimit(-1));
            expectThrows(IndexOutOfBoundsException.class, () -> ByteEncoder.from(new byte[0], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> ByteEncoder.from(new byte[0]).encodeTo(new byte[0], 1));
            TestInputStream err = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            err.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () ->
                ByteEncoder.from(err).encode());
            expectThrows(IORuntimeException.class, () ->
                ByteEncoder.from(new byte[10]).encodeTo(new ErrorOutputStream()));
            expectThrows(IORuntimeException.class, () ->
                ByteEncoder.from(new byte[10]).encodeTo(Channels.newChannel(new ErrorOutputStream())));
            expectThrows(IORuntimeException.class, () ->
                ByteEncoder.from(new byte[10]).encodeTo(new byte[1]));
            expectThrows(IORuntimeException.class, () ->
                ByteEncoder.from(new byte[10], 0, 5).encodeTo(new byte[1], 0));
            expectThrows(IORuntimeException.class, () ->
                ByteEncoder.from(new byte[10]).encodeTo(ByteBuffer.allocate(0)));
            Method writeTo = ByteEncoder.from(new byte[0]).getClass()
                .getDeclaredMethod("writeTo", ByteBuffer.class, Object.class);
            invokeThrows(UnsupportedOperationException.class, writeTo, ByteEncoder.from(new byte[0]),
                ByteBuffer.allocate(10), String.class);
        }
    }

    private void testEncode(int totalSize, int readBlockSize, int limit) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        byte[] limitedData = Arrays.copyOf(data, dstSize(totalSize, limit));
        byte[] timesData = timesData(data);
        byte[] limitedTimesData = timesData(limitedData);
        {
            // size effect
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).encode(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encode(),
                actualSize(totalSize, limit)
            );
            // with handlers
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encode(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encode(),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
        }
        {
            // to OutputStream
            BytesBuilder builder = new BytesBuilder();
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).encodeTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encodeTo(builder),
                actualSize(totalSize, limit)
            );
            assertEquals(builder.toByteArray(), limitedData);
            builder.reset();
            // with handlers
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toByteArray(), timesData);
            builder.reset();
            endCount.clear();
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(builder),
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
                ByteEncoder.from(data).readBlockSize(readBlockSize).encodeTo(channel),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encodeTo(channel),
                actualSize(totalSize, limit)
            );
            assertEquals(builder.toByteArray(), limitedData);
            builder.reset();
            // with handlers
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(channel),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toByteArray(), timesData);
            builder.reset();
            endCount.clear();
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(channel),
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
                ByteEncoder.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize).encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(dst, data);
            dst = new byte[limitedData.length];
            assertEquals(
                ByteEncoder.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit).encodeTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(dst, limitedData);
            // with handlers
            dst = new byte[timesData.length];
            assertEquals(
                ByteEncoder.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(dst, timesData);
            endCount.clear();
            dst = new byte[dstSize(totalSize, limit) * 4];
            assertEquals(
                ByteEncoder.from(ByteBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
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
                ByteEncoder.from(data).readBlockSize(readBlockSize).encodeTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), data);
            dst = new byte[limitedData.length + 5];
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encodeTo(dst, 5),
                actualSize(totalSize, limit)
            );
            assertEquals(
                Arrays.copyOfRange(dst, 5, dst.length),
                limitedData
            );
            // with handlers
            dst = new byte[timesData.length + 5];
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), timesData);
            endCount.clear();
            dst = new byte[limitedTimesData.length + 5];
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst, 5),
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
                ByteEncoder.from(data).readBlockSize(readBlockSize).encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(BufferKit.copyContent((ByteBuffer) dst.flip()), data);
            dst = ByteBuffer.allocate(limitedData.length);
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encodeTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(
                BufferKit.copyContent((ByteBuffer) dst.flip()),
                limitedData
            );
            // with handlers
            dst = ByteBuffer.allocate(timesData.length);
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(BufferKit.copyContent((ByteBuffer) dst.flip()), timesData);
            endCount.clear();
            dst = ByteBuffer.allocate(limitedTimesData.length);
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
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
                ByteEncoder.from(reader).handler(ByteEncoder.emptyHandler()).asInputStream().read());
            expectThrows(IOException.class, () ->
                ByteEncoder.from(reader).handler(ByteEncoder.emptyHandler()).asInputStream().close());
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
                IOKit.read(ByteEncoder.from(data).readBlockSize(readBlockSize).asInputStream()),
                data.length == 0 ? null : data
            );
            assertEquals(
                IOKit.read(ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).asInputStream()),
                limitedData.length == 0 ? null : limitedData
            );
            assertEquals(
                IOKit.read(ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asInputStream()),
                timesData.length == 0 ? null : timesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                IOKit.read(ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asInputStream()),
                limitedTimesData.length == 0 ? null : limitedTimesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testInputStream(
                ByteEncoder.from(data).readBlockSize(readBlockSize).asInputStream(),
                data,
                false, false, true
            );
            IOImplsTest.testInputStream(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).asInputStream(),
                limitedData,
                false, false, true
            );
            IOImplsTest.testInputStream(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asInputStream(),
                timesData,
                false, true, false
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testInputStream(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
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
                IOKit.read(ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.emptyHandler())
                    .asInputStream()),
                data.length == 0 ? null : data
            );
            IOImplsTest.testInputStream(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.emptyHandler())
                    .asInputStream(),
                data,
                false, true, false
            );
            IOImplsTest.testInputStream(
                ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(ByteEncoder.emptyHandler())
                    .asInputStream(),
                limitedData,
                false, true, false
            );
        }
        {
            // one by one
            InputStream in = ByteEncoder.from(data).readBlockSize(readBlockSize)
                .handler(ByteEncoder.emptyHandler())
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
            ByteSegment readData = ByteEncoder.from(data).readBlockSize(readBlockSize)
                .asByteReader().read(data.length + 1);
            assertEquals(readData.toByteArray(), data);
            assertTrue(readData.end());
            readData = ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .asByteReader().read(limitedData.length + 1);
            assertEquals(readData.toByteArray(), limitedData);
            assertTrue(readData.end());
            readData = ByteEncoder.from(data).readBlockSize(readBlockSize)
                .handler(timesHandler(readBlockSize, endCount))
                .handler(timesHandler(readBlockSize, endCount))
                .asByteReader().read(timesData.length + 1);
            assertEquals(readData.toByteArray(), timesData);
            assertTrue(readData.end());
            assertEquals(endCount.get(), 2);
            endCount.clear();
            readData = ByteEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .handler(timesHandler(readBlockSize, endCount))
                .handler(timesHandler(readBlockSize, endCount))
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

    private ByteEncoder.Handler timesHandler(int readBlockSize, IntVar endCount) {
        return ByteEncoder.newFixedSizeHandler((d, e) -> {
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
    public void testResidualSizeHandler() throws Exception {
        testResidualSizeHandler(0, 123, 37);
        testResidualSizeHandler(0, 123, 123);
        testResidualSizeHandler(0, 123, 1333);
        testResidualSizeHandler(1333, 123, 37);
        testResidualSizeHandler(1333, 123, 123);
        testResidualSizeHandler(1333, 123, 777);
        testResidualSizeHandler(123, 1333, 37);
        testResidualSizeHandler(123, 1333, 123);
        testResidualSizeHandler(123, 1333, 777);
        testResidualSizeHandler(123, 123, 1333);
        testResidualSizeHandler(123, 77, 1333);
        testResidualSizeHandler(77, 123, 1333);
        testResidualSizeHandler(256, 64, 32);
        testResidualSizeHandler(256, 32, 64);
        {
            // exception
            expectThrows(IllegalArgumentException.class, () ->
                ByteEncoder.from(new byte[0])
                    .handler(ByteEncoder.newFixedSizeHandler(ByteEncoder.emptyHandler(), -1)));
            expectThrows(IllegalArgumentException.class, () ->
                ByteEncoder.from(new byte[0])
                    .handler(ByteEncoder.newFixedSizeHandler(ByteEncoder.emptyHandler(), 0)));
            expectThrows(IllegalArgumentException.class, () ->
                ByteEncoder.from(new byte[0])
                    .handler(ByteEncoder.newMultipleSizeHandler(ByteEncoder.emptyHandler(), -1)));
            expectThrows(IllegalArgumentException.class, () ->
                ByteEncoder.from(new byte[0])
                    .handler(ByteEncoder.newMultipleSizeHandler(ByteEncoder.emptyHandler(), 0)));
        }
    }

    private void testResidualSizeHandler(int totalSize, int readBlockSize, int blockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        BytesBuilder builder = new BytesBuilder();
        {
            // FixedSizeHandler
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newFixedSizeHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                        } else {
                            assertEquals(d.remaining(), blockSize);
                        }
                        return d;
                    }, blockSize))
                    .encodeTo(builder),
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
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newMultipleSizeHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                        } else {
                            assertEquals(d.remaining() % blockSize, 0);
                        }
                        return d;
                    }, blockSize))
                    .encodeTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toByteArray(), data);
            assertEquals(endCount.get(), 1);
            builder.reset();
            endCount.clear();
        }
    }

    @Test
    public void testBufferedHandler() throws Exception {
        testBufferedHandler(0, 123);
        testBufferedHandler(123, 123);
        testBufferedHandler(123, 1234);
        testBufferedHandler(123, 1);
        testBufferedHandler(123, 2);
        testBufferedHandler(123, 3);
        testBufferedHandler(128, 16);
    }

    private void testBufferedHandler(int totalSize, int readBlockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        byte[] data = randomBytes(totalSize);
        BytesBuilder builder = new BytesBuilder();
        {
            // BufferedHandler
            assertEquals(
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newBufferedHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .encodeTo(builder),
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
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newBufferedHandler((d, e) -> {
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
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newBufferedHandler((d, e) -> {
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
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newBufferedHandler((d, e) -> {
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
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newBufferedHandler((d, e) -> {
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
                ByteEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(ByteEncoder.newBufferedHandler((d, e) -> {
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
