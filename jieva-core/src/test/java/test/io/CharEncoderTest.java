package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.CharEncoder;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.AssertTest;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CharEncoderTest implements DataTest, AssertTest {

    @Test
    public void testEncode() throws Exception {
        testEncode(0, 123, 0);
        testEncode(0, 123, 37);
        testEncode(0, 123, 123);
        testEncode(0, 123, 1333);
        testEncode(1333, 123, 0);
        testEncode(1333, 123, 37);
        testEncode(1333, 123, 123);
        testEncode(1333, 123, 777);
        testEncode(123, 1333, 0);
        testEncode(123, 1333, 37);
        testEncode(123, 1333, 123);
        testEncode(123, 1333, 777);
        testEncode(123, 123, 1333);
        testEncode(123, 77, 1333);
        testEncode(77, 123, 1333);
        {
            // exceptions
            expectThrows(IllegalArgumentException.class, () -> CharEncoder.from(new char[0]).readBlockSize(0));
            expectThrows(IllegalArgumentException.class, () -> CharEncoder.from(new char[0]).readBlockSize(-1));
            expectThrows(IllegalArgumentException.class, () -> CharEncoder.from(new char[0]).readLimit(-1));
            expectThrows(IndexOutOfBoundsException.class, () -> CharEncoder.from(new char[0], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> CharEncoder.from(new char[0]).encodeTo(new char[0], 1));
            TestReader err = new TestReader(new CharArrayReader(new char[0]));
            Writer errWriter = IOKit.newWriter(new ErrorOutputStream());
            err.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () ->
                CharEncoder.from(err).encode());
            expectThrows(IORuntimeException.class, () ->
                CharEncoder.from(new char[10]).encodeTo(errWriter));
            expectThrows(IORuntimeException.class, () ->
                CharEncoder.from(new char[10]).encodeTo(new char[1]));
            expectThrows(IORuntimeException.class, () ->
                CharEncoder.from(new char[10], 0, 5).encodeTo(new char[1], 0));
            expectThrows(IORuntimeException.class, () ->
                CharEncoder.from(new char[10]).encodeTo(CharBuffer.allocate(0)));
            Method writeTo = CharEncoder.from(new char[0]).getClass()
                .getDeclaredMethod("writeTo", CharBuffer.class, Object.class);
            invokeThrows(UnsupportedOperationException.class, writeTo, CharEncoder.from(new char[0]),
                CharBuffer.allocate(10), String.class);
        }
    }

    private void testEncode(int totalSize, int readBlockSize, int limit) throws Exception {
        IntVar endCount = IntVar.of(0);
        char[] data = randomChars(totalSize);
        char[] limitedData = Arrays.copyOf(data, dstSize(totalSize, limit));
        char[] timesData = timesData(data);
        char[] limitedTimesData = timesData(limitedData);
        {
            // size effect
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).encode(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encode(),
                actualSize(totalSize, limit)
            );
            // with handlers
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encode(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
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
            CharsBuilder builder = new CharsBuilder();
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).encodeTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encodeTo(builder),
                actualSize(totalSize, limit)
            );
            assertEquals(builder.toCharArray(), limitedData);
            builder.reset();
            // with handlers
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toCharArray(), timesData);
            builder.reset();
            endCount.clear();
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(builder),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toCharArray(), limitedTimesData);
            builder.reset();
            endCount.clear();
        }
        {
            // to array
            char[] dst = new char[totalSize];
            assertEquals(
                CharEncoder.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize).encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(dst, data);
            dst = new char[limitedData.length];
            assertEquals(
                CharEncoder.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit).encodeTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(dst, limitedData);
            // with handlers
            dst = new char[timesData.length];
            assertEquals(
                CharEncoder.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(dst, timesData);
            endCount.clear();
            dst = new char[dstSize(totalSize, limit) * 4];
            assertEquals(
                CharEncoder.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit)
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
            char[] dst = new char[totalSize + 5];
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).encodeTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), data);
            dst = new char[limitedData.length + 5];
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).encodeTo(dst, 5),
                actualSize(totalSize, limit)
            );
            assertEquals(
                Arrays.copyOfRange(dst, 5, dst.length),
                limitedData
            );
            // with handlers
            dst = new char[timesData.length + 5];
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), timesData);
            endCount.clear();
            dst = new char[limitedTimesData.length + 5];
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
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
            CharBuffer dst = CharBuffer.allocate(totalSize);
            assertEquals(
                CharEncoder.from(new String(data)).readBlockSize(readBlockSize).encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(BufferKit.copyContent((CharBuffer) dst.flip()), data);
            dst = CharBuffer.allocate(limitedData.length);
            assertEquals(
                CharEncoder.from(new String(data)).readBlockSize(readBlockSize).readLimit(limit).encodeTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(
                BufferKit.copyContent((CharBuffer) dst.flip()),
                limitedData
            );
            // with handlers
            dst = CharBuffer.allocate(timesData.length);
            assertEquals(
                CharEncoder.from(new String(data), 0, data.length).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(BufferKit.copyContent((CharBuffer) dst.flip()), timesData);
            endCount.clear();
            dst = CharBuffer.allocate(limitedTimesData.length);
            assertEquals(
                CharEncoder.from(new String(data), 0, data.length).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .encodeTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(endCount.get(), 2);
            assertEquals(
                BufferKit.copyContent((CharBuffer) dst.flip()),
                limitedTimesData
            );
            endCount.clear();
        }
    }

    @Test
    public void testAsInputStreamAndReader() throws Exception {
        testAsInputStreamAndReader(0, 123, 0);
        testAsInputStreamAndReader(0, 123, 77);
        testAsInputStreamAndReader(0, 123, 123);
        testAsInputStreamAndReader(0, 123, 1333);
        testAsInputStreamAndReader(1333, 123, 0);
        testAsInputStreamAndReader(1333, 123, 77);
        testAsInputStreamAndReader(1333, 123, 123);
        testAsInputStreamAndReader(1333, 123, 777);
        testAsInputStreamAndReader(123, 1333, 0);
        testAsInputStreamAndReader(123, 1333, 77);
        testAsInputStreamAndReader(123, 1333, 123);
        testAsInputStreamAndReader(123, 1333, 777);
        testAsInputStreamAndReader(123, 123, 1333);
        testAsInputStreamAndReader(123, 77, 1333);
        testAsInputStreamAndReader(77, 123, 1333);
        {
            // exception
            TestReader err = new TestReader(new CharArrayReader(new char[0]));
            err.setNextOperation(ReadOps.THROW, 99);
            CharReader reader = CharReader.from(err);
            expectThrows(IOException.class, () ->
                CharEncoder.from(reader).handler(CharEncoder.emptyHandler()).asReader().read());
            expectThrows(IOException.class, () ->
                CharEncoder.from(reader).handler(CharEncoder.emptyHandler()).asReader().close());
        }
    }

    private void testAsInputStreamAndReader(int totalSize, int readBlockSize, int limit) throws Exception {
        IntVar endCount = IntVar.of(0);
        char[] data = randomChars(totalSize);
        char[] limitedData = Arrays.copyOf(data, dstSize(totalSize, limit));
        char[] timesData = timesData(data);
        char[] limitedTimesData = timesData(limitedData);
        {
            assertEquals(
                IOKit.read(CharEncoder.from(data).readBlockSize(readBlockSize).asReader()),
                data.length == 0 ? null : data
            );
            assertEquals(
                IOKit.read(CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).asReader()),
                limitedData.length == 0 ? null : limitedData
            );
            assertEquals(
                IOKit.read(CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asReader()),
                timesData.length == 0 ? null : timesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                IOKit.read(CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asReader()),
                limitedTimesData.length == 0 ? null : limitedTimesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testReader(
                CharEncoder.from(data).readBlockSize(readBlockSize).asReader(),
                data,
                false, false, true
            );
            IOImplsTest.testReader(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit).asReader(),
                limitedData,
                false, false, true
            );
            IOImplsTest.testReader(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asReader(),
                timesData,
                false, true, false
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testReader(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(timesHandler(readBlockSize, endCount))
                    .handler(timesHandler(readBlockSize, endCount))
                    .asReader(),
                limitedTimesData,
                false, true, false
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
        }
        {
            // for empty
            assertEquals(
                IOKit.read(CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.emptyHandler())
                    .asReader()),
                data.length == 0 ? null : data
            );
            IOImplsTest.testReader(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.emptyHandler())
                    .asReader(),
                data,
                false, true, false
            );
            IOImplsTest.testReader(
                CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .handler(CharEncoder.emptyHandler())
                    .asReader(),
                limitedData,
                false, true, false
            );
        }
        {
            // one by one
            Reader in = CharEncoder.from(data).readBlockSize(readBlockSize)
                .handler(CharEncoder.emptyHandler())
                .asReader();
            CharsBuilder builder = new CharsBuilder();
            while (true) {
                int next = in.read();
                if (next < 0) {
                    break;
                }
                builder.append(next);
            }
            assertEquals(builder.toCharArray(), data);
            assertEquals(in.read(), -1);
        }
        {
            // reader
            CharSegment readData = CharEncoder.from(data).readBlockSize(readBlockSize)
                .asCharReader().read(data.length + 1);
            assertEquals(readData.toCharArray(), data);
            assertTrue(readData.end());
            readData = CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .asCharReader().read(limitedData.length + 1);
            assertEquals(readData.toCharArray(), limitedData);
            assertTrue(readData.end());
            readData = CharEncoder.from(data).readBlockSize(readBlockSize)
                .handler(timesHandler(readBlockSize, endCount))
                .handler(timesHandler(readBlockSize, endCount))
                .asCharReader().read(timesData.length + 1);
            assertEquals(readData.toCharArray(), timesData);
            assertTrue(readData.end());
            assertEquals(endCount.get(), 2);
            endCount.clear();
            readData = CharEncoder.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .handler(timesHandler(readBlockSize, endCount))
                .handler(timesHandler(readBlockSize, endCount))
                .asCharReader().read(limitedTimesData.length + 1);
            assertEquals(readData.toCharArray(), limitedTimesData);
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

    private CharEncoder.Handler timesHandler(int readBlockSize, IntVar endCount) {
        return CharEncoder.newFixedSizeHandler((d, e) -> {
            if (e) {
                endCount.incrementAndGet();
            }
            CharBuffer ret = CharBuffer.allocate(d.remaining() * 2);
            while (d.hasRemaining()) {
                char next = d.get();
                ret.put(next);
                ret.put(next);
            }
            ret.flip();
            return ret;
        }, readBlockSize + 1);
    }

    private char[] timesData(char[] data) {
        char[] ret = new char[data.length * 4];
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
                CharEncoder.from(new char[0])
                    .handler(CharEncoder.newFixedSizeHandler(CharEncoder.emptyHandler(), -1)));
            expectThrows(IllegalArgumentException.class, () ->
                CharEncoder.from(new char[0])
                    .handler(CharEncoder.newFixedSizeHandler(CharEncoder.emptyHandler(), 0)));
            expectThrows(IllegalArgumentException.class, () ->
                CharEncoder.from(new char[0])
                    .handler(CharEncoder.newMultipleSizeHandler(CharEncoder.emptyHandler(), -1)));
            expectThrows(IllegalArgumentException.class, () ->
                CharEncoder.from(new char[0])
                    .handler(CharEncoder.newMultipleSizeHandler(CharEncoder.emptyHandler(), 0)));
        }
    }

    private void testResidualSizeHandler(int totalSize, int readBlockSize, int blockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        char[] data = randomChars(totalSize);
        CharsBuilder builder = new CharsBuilder();
        {
            // FixedSizeHandler
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newFixedSizeHandler((d, e) -> {
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
            assertEquals(builder.toCharArray(), data);
            assertEquals(endCount.get(), 1);
            builder.reset();
            endCount.clear();
        }
        {
            // MultipleSizeHandler
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newMultipleSizeHandler((d, e) -> {
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
            assertEquals(builder.toCharArray(), data);
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
        char[] data = randomChars(totalSize);
        CharsBuilder builder = new CharsBuilder();
        {
            // BufferedHandler
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newBufferedHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .encodeTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toCharArray(), data);
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
        char[] data = randomChars(totalSize, 'a', 'z');
        String str = new String(data);
        {
            // toArray
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newBufferedHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toCharArray(),
                data
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
        {
            // toArray
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newBufferedHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toCharBuffer(),
                CharBuffer.wrap(data)
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
        {
            // toArray
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newBufferedHandler((d, e) -> {
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
        }
        {
            // toEncoder
            assertEquals(
                CharEncoder.from(data).readBlockSize(readBlockSize)
                    .handler(CharEncoder.newBufferedHandler((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .toByteEncoder(CharsKit.defaultCharset())
                    .toString(CharsKit.defaultCharset()),
                str
            );
            assertEquals(endCount.get(), 1);
            endCount.clear();
        }
    }
}
