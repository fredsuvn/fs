package tests.io;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.chars.CharsBuilder;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.value.IntVar;
import space.sunqian.common.io.BufferKit;
import space.sunqian.common.io.CharProcessor;
import space.sunqian.common.io.CharReader;
import space.sunqian.common.io.CharSegment;
import space.sunqian.common.io.CharTransformer;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.io.IORuntimeException;
import internal.test.AssertTest;
import internal.test.DataTest;
import internal.test.ErrorOutputStream;
import internal.test.ReadOps;
import internal.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CharProcessorTest implements DataTest, AssertTest {

    @Test
    public void testProcess() throws Exception {
        testProcess(0, 123, 0);
        testProcess(0, 123, 37);
        testProcess(0, 123, 123);
        testProcess(0, 123, 1333);
        testProcess(1333, 123, 0);
        testProcess(1333, 123, 37);
        testProcess(1333, 123, 123);
        testProcess(1333, 123, 777);
        testProcess(123, 1333, 0);
        testProcess(123, 1333, 37);
        testProcess(123, 1333, 123);
        testProcess(123, 1333, 777);
        testProcess(123, 123, 1333);
        testProcess(123, 77, 1333);
        testProcess(77, 123, 1333);
        {
            // exceptions
            assertThrows(IllegalArgumentException.class, () -> CharProcessor.from(new char[0]).readBlockSize(0));
            assertThrows(IllegalArgumentException.class, () -> CharProcessor.from(new char[0]).readBlockSize(-1));
            assertThrows(IllegalArgumentException.class, () -> CharProcessor.from(new char[0]).readLimit(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> CharProcessor.from(new char[0], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CharProcessor.from(new char[0]).processTo(new char[0], 1));
            TestReader err = new TestReader(new CharArrayReader(new char[0]));
            Writer errWriter = IOKit.newWriter(new ErrorOutputStream());
            err.setNextOperation(ReadOps.THROW, 99);
            assertThrows(IORuntimeException.class, () ->
                CharProcessor.from(err).process());
            assertThrows(IORuntimeException.class, () ->
                CharProcessor.from(new char[10]).processTo(errWriter));
            assertThrows(IORuntimeException.class, () ->
                CharProcessor.from(new char[10]).processTo(new char[1]));
            assertThrows(IORuntimeException.class, () ->
                CharProcessor.from(new char[10], 0, 5).processTo(new char[1], 0));
            assertThrows(IORuntimeException.class, () ->
                CharProcessor.from(new char[10]).processTo(CharBuffer.allocate(0)));
            Method writeTo = CharProcessor.from(new char[0]).getClass()
                .getDeclaredMethod("writeTo", CharBuffer.class, Object.class);
            invokeThrows(UnsupportedOperationException.class, writeTo, CharProcessor.from(new char[0]),
                CharBuffer.allocate(10), String.class);
        }
    }

    private void testProcess(int totalSize, int readBlockSize, int limit) throws Exception {
        IntVar endCount = IntVar.of(0);
        char[] data = randomChars(totalSize);
        char[] limitedData = Arrays.copyOf(data, dstSize(totalSize, limit));
        char[] timesData = timesData(data);
        char[] limitedTimesData = timesData(limitedData);
        {
            // size effect
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).process(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).process(),
                actualSize(totalSize, limit)
            );
            // with transformers
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .process(),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
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
            CharsBuilder builder = new CharsBuilder();
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).processTo(builder),
                actualSize(totalSize, limit)
            );
            assertEquals(builder.toCharArray(), limitedData);
            builder.reset();
            // with transformers
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(builder),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(builder.toCharArray(), timesData);
            builder.reset();
            endCount.clear();
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(builder),
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
                CharProcessor.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize).processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(dst, data);
            dst = new char[limitedData.length];
            assertEquals(
                CharProcessor.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit).processTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(dst, limitedData);
            // with transformers
            dst = new char[timesData.length];
            assertEquals(
                CharProcessor.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(dst, timesData);
            endCount.clear();
            dst = new char[dstSize(totalSize, limit) * 4];
            assertEquals(
                CharProcessor.from(CharBuffer.wrap(data)).readBlockSize(readBlockSize).readLimit(limit)
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
            char[] dst = new char[totalSize + 5];
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).processTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), data);
            dst = new char[limitedData.length + 5];
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).processTo(dst, 5),
                actualSize(totalSize, limit)
            );
            assertEquals(
                Arrays.copyOfRange(dst, 5, dst.length),
                limitedData
            );
            // with transformers
            dst = new char[timesData.length + 5];
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst, 5),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length), timesData);
            endCount.clear();
            dst = new char[limitedTimesData.length + 5];
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
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
            CharBuffer dst = CharBuffer.allocate(totalSize);
            assertEquals(
                CharProcessor.from(new String(data)).readBlockSize(readBlockSize).processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(BufferKit.copyContent((CharBuffer) dst.flip()), data);
            dst = CharBuffer.allocate(limitedData.length);
            assertEquals(
                CharProcessor.from(new String(data)).readBlockSize(readBlockSize).readLimit(limit).processTo(dst),
                actualSize(totalSize, limit)
            );
            assertEquals(
                BufferKit.copyContent((CharBuffer) dst.flip()),
                limitedData
            );
            // with transformers
            dst = CharBuffer.allocate(timesData.length);
            assertEquals(
                CharProcessor.from(new String(data), 0, data.length).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
                totalSize == 0 ? -1 : totalSize
            );
            assertEquals(endCount.get(), 2);
            assertEquals(BufferKit.copyContent((CharBuffer) dst.flip()), timesData);
            endCount.clear();
            dst = CharBuffer.allocate(limitedTimesData.length);
            assertEquals(
                CharProcessor.from(new String(data), 0, data.length).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .processTo(dst),
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
            assertThrows(IOException.class, () ->
                CharProcessor.from(reader).transformer(CharTransformer.empty()).asReader().read());
            assertThrows(IOException.class, () ->
                CharProcessor.from(reader).transformer(CharTransformer.empty()).asReader().close());
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
                IOKit.read(CharProcessor.from(data).readBlockSize(readBlockSize).asReader()),
                data.length == 0 ? null : data
            );
            assertEquals(
                IOKit.read(CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).asReader()),
                limitedData.length == 0 ? null : limitedData
            );
            assertEquals(
                IOKit.read(CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asReader()),
                timesData.length == 0 ? null : timesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            assertEquals(
                IOKit.read(CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asReader()),
                limitedTimesData.length == 0 ? null : limitedTimesData
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testReader(
                CharProcessor.from(data).readBlockSize(readBlockSize).asReader(),
                data,
                false, false, true
            );
            IOImplsTest.testReader(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit).asReader(),
                limitedData,
                false, false, true
            );
            IOImplsTest.testReader(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .asReader(),
                timesData,
                false, true, false
            );
            assertEquals(endCount.get(), 2);
            endCount.clear();
            IOImplsTest.testReader(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(timesTransformer(readBlockSize, endCount))
                    .transformer(timesTransformer(readBlockSize, endCount))
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
                IOKit.read(CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.empty())
                    .asReader()),
                data.length == 0 ? null : data
            );
            IOImplsTest.testReader(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.empty())
                    .asReader(),
                data,
                false, true, false
            );
            IOImplsTest.testReader(
                CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                    .transformer(CharTransformer.empty())
                    .asReader(),
                limitedData,
                false, true, false
            );
        }
        {
            // one by one
            Reader in = CharProcessor.from(data).readBlockSize(readBlockSize)
                .transformer(CharTransformer.empty())
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
            CharSegment readData = CharProcessor.from(data).readBlockSize(readBlockSize)
                .asCharReader().read(data.length + 1);
            assertEquals(readData.array(), data);
            assertTrue(readData.end());
            readData = CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .asCharReader().read(limitedData.length + 1);
            assertEquals(readData.array(), limitedData);
            assertTrue(readData.end());
            readData = CharProcessor.from(data).readBlockSize(readBlockSize)
                .transformer(timesTransformer(readBlockSize, endCount))
                .transformer(timesTransformer(readBlockSize, endCount))
                .asCharReader().read(timesData.length + 1);
            assertEquals(readData.array(), timesData);
            assertTrue(readData.end());
            assertEquals(endCount.get(), 2);
            endCount.clear();
            readData = CharProcessor.from(data).readBlockSize(readBlockSize).readLimit(limit)
                .transformer(timesTransformer(readBlockSize, endCount))
                .transformer(timesTransformer(readBlockSize, endCount))
                .asCharReader().read(limitedTimesData.length + 1);
            assertEquals(readData.array(), limitedTimesData);
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

    private CharTransformer timesTransformer(int readBlockSize, IntVar endCount) {
        return CharTransformer.withFixedSize((d, e) -> {
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
            assertThrows(IllegalArgumentException.class, () ->
                CharProcessor.from(new char[0])
                    .transformer(CharTransformer.withFixedSize(CharTransformer.empty(), -1)));
            assertThrows(IllegalArgumentException.class, () ->
                CharProcessor.from(new char[0])
                    .transformer(CharTransformer.withFixedSize(CharTransformer.empty(), 0)));
            assertThrows(IllegalArgumentException.class, () ->
                CharProcessor.from(new char[0])
                    .transformer(CharTransformer.withMultipleSize(CharTransformer.empty(), -1)));
            assertThrows(IllegalArgumentException.class, () ->
                CharProcessor.from(new char[0])
                    .transformer(CharTransformer.withMultipleSize(CharTransformer.empty(), 0)));
        }
    }

    private void testResidualSizeTransformer(int totalSize, int readBlockSize, int blockSize) throws Exception {
        IntVar endCount = IntVar.of(0);
        char[] data = randomChars(totalSize);
        CharsBuilder builder = new CharsBuilder();
        {
            // FixedSizeHandler
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withFixedSize((d, e) -> {
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
            assertEquals(builder.toCharArray(), data);
            assertEquals(endCount.get(), 1);
            builder.reset();
            endCount.clear();
        }
        {
            // MultipleSizeHandler
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withMultipleSize((d, e) -> {
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
            assertEquals(builder.toCharArray(), data);
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
        char[] data = randomChars(totalSize);
        CharsBuilder builder = new CharsBuilder();
        {
            // BufferedHandler
            assertEquals(
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withBuffered((d, e) -> {
                        if (e) {
                            endCount.incrementAndGet();
                            return d;
                        }
                        return null;
                    }))
                    .processTo(builder),
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
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withBuffered((d, e) -> {
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
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withBuffered((d, e) -> {
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
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withBuffered((d, e) -> {
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
                CharProcessor.from(data).readBlockSize(readBlockSize)
                    .transformer(CharTransformer.withBuffered((d, e) -> {
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
