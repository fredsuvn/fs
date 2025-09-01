package tests.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CharReaderTest implements DataTest {

    private static final int DST_SIZE = 256;

    @Test
    public void testReadChars() throws Exception {
        testReadChars(0, 1);
        testReadChars(1, 1);
        testReadChars(32, 1);
        testReadChars(32, 16);
        testReadChars(32, 32);
        testReadChars(32, 64);
        testReadChars(128, 16);
        testReadChars(128, 33);
        testReadChars(128, 111);
        testReadChars(128, 128);
        testReadChars(128, 129);
        testReadChars(128, 1024);

        // error
        {
            TestReader tr = new TestReader(new CharArrayReader(new char[10]));
            tr.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> CharReader.from(tr).skip(100));
        }
    }

    private void testReadChars(int dataSize, int readSize) throws Exception {
        {
            // reader
            char[] data = randomChars(dataSize);
            testReadChars(CharReader.from(new CharArrayReader(data)), data, readSize, false);
            testSkipChars(CharReader.from(new CharArrayReader(data)), data, readSize);
            testReadChars(CharReader.from(new OneCharReader(data)), data, readSize, false);
            testSkipChars(CharReader.from(new OneCharReader(data)), data, readSize);
            TestReader tr = new TestReader(new CharArrayReader(data));
            tr.setNextOperation(ReadOps.READ_ZERO);
            testSkipChars(CharReader.from(tr), data, readSize);
        }
        {
            // char array
            char[] data = randomChars(dataSize);
            testReadChars(CharReader.from(data), data, readSize, true);
            testSkipChars(CharReader.from(data), data, readSize);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testReadChars(
                CharReader.from(dataPadding, 33, data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkipChars(CharReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // char sequence
            char[] data = randomChars(dataSize);
            String dataStr = new String(data);
            testReadChars(CharReader.from(dataStr), data, readSize, true);
            testSkipChars(CharReader.from(data), data, readSize);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            String dataStrPadding = new String(dataPadding);
            testReadChars(
                CharReader.from(dataStrPadding, 33, 33 + data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkipChars(CharReader.from(dataStrPadding, 33, 33 + data.length), data, readSize);
        }
        {
            // buffer
            char[] data = randomChars(dataSize);
            testReadChars(CharReader.from(CharBuffer.wrap(data)), data, readSize, true);
            testSkipChars(CharReader.from(CharBuffer.wrap(data)), data, readSize);
        }
        {
            // limited
            char[] data = randomChars(dataSize);
            testReadChars(
                CharReader.from(data).limit(data.length),
                data,
                readSize, true
            );
            testSkipChars(
                CharReader.from(data),
                data,
                readSize
            );
            testReadChars(
                CharReader.from(data).limit(data.length + 5),
                data,
                readSize, true
            );
            testSkipChars(
                CharReader.from(data).limit(data.length + 5),
                data,
                readSize
            );
            testReadChars(
                CharReader.from(new CharArrayReader(data)).limit(data.length + 5),
                data,
                readSize, false
            );
            testSkipChars(
                CharReader.from(new CharArrayReader(data)).limit(data.length + 5),
                data,
                readSize
            );
            if (data.length > 5) {
                testReadChars(
                    CharReader.from(data).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize, true
                );
                testSkipChars(
                    CharReader.from(data).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize
                );
            }
        }
    }

    private void testReadChars(CharReader reader, char[] data, int readSize, boolean preKnown) {
        assertFalse(reader.read(0).end());
        assertFalse(reader.read(0).data().hasRemaining());
        int hasRead = 0;
        while (hasRead < data.length) {
            CharSegment segment = reader.read(readSize);
            int actualLen = Math.min(readSize, data.length - hasRead);
            assertEquals(
                BufferKit.copyContent(segment.data()),
                Arrays.copyOfRange(data, hasRead, hasRead + actualLen)
            );
            hasRead += actualLen;
            if (hasRead >= data.length) {
                if (actualLen < readSize) {
                    assertTrue(segment.end());
                } else {
                    assertEquals(segment.end(), preKnown);
                }
            }
        }
        assertFalse(reader.read(0).end());
        assertFalse(reader.read(0).data().hasRemaining());
        assertTrue(reader.read(1).end());
        assertFalse(reader.read(1).data().hasRemaining());
    }

    private void testSkipChars(CharReader reader, char[] data, int readSize) {
        assertEquals(reader.skip(0), 0);
        int hasRead = 0;
        while (hasRead < data.length) {
            long skipped = reader.skip(readSize);
            int actualLen = Math.min(readSize, data.length - hasRead);
            assertEquals(skipped, actualLen);
            hasRead += actualLen;
        }
        assertEquals(reader.skip(0), 0);
        assertEquals(reader.skip(1), 0);
    }

    @Test
    public void testReadCharsTo() {
        testReadCharsTo(0, 0);
        testReadCharsTo(0, 1);
        testReadCharsTo(1, 1);
        testReadCharsTo(2, 2);
        testReadCharsTo(64, 1);
        testReadCharsTo(64, 33);
        testReadCharsTo(64, 64);
        testReadCharsTo(64, 111);
        testReadCharsTo(111, 77);
        testReadCharsTo(111, 111);
        testReadCharsTo(111, 333);
        testReadCharsTo(DST_SIZE, DST_SIZE);
        testReadCharsTo(DST_SIZE, DST_SIZE + 6);
        testReadCharsTo(DST_SIZE, DST_SIZE - 6);
    }

    private void testReadCharsTo(int dataSize, int readSize) {
        char[] data = randomChars(dataSize);
        char[] dataPadding = new char[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // reader
            testReadCharsTo(() -> CharReader.from(new CharArrayReader(data)), data, readSize);
            testReadCharsTo(() -> CharReader.from(new OneCharReader(data)), data, readSize);
        }
        {
            // char array
            testReadCharsTo(() -> CharReader.from(data), data, readSize);
            testReadCharsTo(() -> CharReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // char sequence
            testReadCharsTo(() -> CharReader.from(new String(data)), data, readSize);
            testReadCharsTo(() -> CharReader.from(new String(dataPadding), 33, 33 + data.length), data, readSize);
        }
        {
            // char buffer
            testReadCharsTo(() -> CharReader.from(CharBuffer.wrap(data)), data, readSize);
            testReadCharsTo(() -> CharReader.from(BufferKit.copyDirect(data)), data, readSize);
        }
        {
            // limited
            testReadCharsTo(() ->
                CharReader.from(new CharArrayReader(data)).limit(data.length), data, readSize);
            testReadCharsTo(() ->
                CharReader.from(new CharArrayReader(data)).limit(data.length + 5), data, readSize);
            if (data.length > 5) {
                testReadCharsTo(() ->
                        CharReader.from(new CharArrayReader(data)).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize
                );
            }
        }
    }

    private void testReadCharsTo(Supplier<CharReader> supplier, char[] data, int readSize) {
        {
            // to writer
            CharsBuilder builder = new CharsBuilder();
            if (data.length == 0) {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(builder), -1);
                assertEquals(reader.readTo(builder, 0), 0);
                reader.close();
                assertEquals(reader.readTo(builder, 0), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(builder, 0), 0);
                if (reader.markSupported()) {
                    reader.mark();
                }
                long hasRead = reader.readTo(builder, readSize);
                assertEquals(hasRead, Math.min(readSize, data.length));
                long restLen = data.length - hasRead;
                assertEquals(reader.readTo(builder), restLen == 0 ? -1 : restLen);
                assertEquals(builder.toCharArray(), data);
                if (reader.markSupported()) {
                    reader.reset();
                    builder.reset();
                    hasRead = reader.readTo(builder, readSize);
                    assertEquals(hasRead, Math.min(readSize, data.length));
                    restLen = data.length - hasRead;
                    assertEquals(reader.readTo(builder), restLen == 0 ? -1 : restLen);
                    assertEquals(builder.toCharArray(), data);
                }
                assertEquals(reader.readTo(builder), -1);
                assertEquals(reader.readTo(builder, 66), -1);
                reader.close();
                assertEquals(reader.readTo(builder, 0), 0);
            }
            // error
            expectThrows(IllegalArgumentException.class, () -> supplier.get().readTo(builder, -1));
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorAppender()));
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorAppender(), 1));
            }
        }
        {
            // to char array full
            if (data.length == 0) {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(new char[0]), 0);
                assertEquals(reader.readTo(new char[1]), -1);
                reader.close();
                assertEquals(reader.readTo(new char[0]), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(new char[0]), 0);
                char[] dst = new char[DST_SIZE];
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst);
                assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = new char[DST_SIZE];
                    actualLen = reader.readTo(dst);
                    assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                    assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = new char[data.length];
                assertEquals(reader.readTo(dst), data.length);
                assertEquals(dst, data);
                assertEquals(reader.readTo(new char[1]), -1);
                reader.close();
                assertEquals(reader.readTo(new char[0]), 0);
            }
        }
        {
            // to char array offset
            if (data.length == 0) {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(new char[0], 0, 0), 0);
                assertEquals(reader.readTo(new char[1], 0, 1), -1);
                reader.close();
                assertEquals(reader.readTo(new char[0], 0, 0), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(new char[0], 0, 0), 0);
                char[] dst = new char[DST_SIZE + 6];
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst, 3, Math.min(readSize, DST_SIZE));
                assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                assertEquals(Arrays.copyOfRange(dst, 3, 3 + actualLen), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = new char[DST_SIZE + 6];
                    actualLen = reader.readTo(dst, 3, Math.min(readSize, DST_SIZE));
                    assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                    assertEquals(Arrays.copyOfRange(dst, 3, 3 + actualLen), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = new char[data.length + 2];
                assertEquals(reader.readTo(dst, 1, data.length), data.length);
                assertEquals(Arrays.copyOfRange(dst, 1, 1 + data.length), data);
                assertEquals(reader.readTo(new char[1], 0, 1), -1);
                reader.close();
                assertEquals(reader.readTo(new char[0], 0, 0), 0);
            }
            // error
            expectThrows(IndexOutOfBoundsException.class, () -> supplier.get().readTo(new char[0], 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> supplier.get().readTo(new char[0], 0, 1));
        }
        {
            // to buffer full
            if (data.length == 0) {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(1)), -1);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0)), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1)), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0)), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0)), 0);
                CharBuffer dst = CharBuffer.allocate(DST_SIZE);
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst);
                assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                dst.flip();
                assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = BufferKit.directCharBuffer(DST_SIZE);
                    actualLen = reader.readTo(dst);
                    assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                    dst.flip();
                    assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = CharBuffer.allocate(data.length);
                assertEquals(reader.readTo(dst), data.length);
                dst.flip();
                assertEquals(BufferKit.copyContent(dst), data);
                assertEquals(reader.readTo(CharBuffer.allocate(1)), -1);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1)), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0)), 0);
            }
            // error
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(CharBuffer.allocate(1).asReadOnlyBuffer()));
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(BufferKit.directCharBuffer(1).asReadOnlyBuffer()));
            }
        }
        {
            // to buffer offset
            if (data.length == 0) {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(1), 1), -1);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1), 1), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0), 1), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0), 1), 0);
                CharBuffer dst = CharBuffer.allocate(DST_SIZE);
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst, readSize);
                assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                dst.flip();
                assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = BufferKit.directCharBuffer(DST_SIZE);
                    actualLen = reader.readTo(dst, readSize);
                    assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                    dst.flip();
                    assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = CharBuffer.allocate(data.length);
                assertEquals(reader.readTo(dst, data.length), data.length);
                dst.flip();
                assertEquals(BufferKit.copyContent(dst), data);
                assertEquals(reader.readTo(CharBuffer.allocate(1), 1), -1);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1), 1), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directCharBuffer(0), 1), 0);
            }
            // error
            expectThrows(IllegalArgumentException.class, () ->
                supplier.get().readTo(CharBuffer.allocate(1), -1));
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(CharBuffer.allocate(1).asReadOnlyBuffer(), 1));
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(BufferKit.directCharBuffer(1).asReadOnlyBuffer(), 1));
            }
        }
    }

    private int minSize(int totalSize, int readSize, int remaining) {
        return Math.min(remaining, Math.min(totalSize, readSize));
    }

    @Test
    public void testAvailable() throws Exception {
        testAvailable(16);
        testAvailable(32);
        testAvailable(IOKit.bufferSize());
        testAvailable(IOKit.bufferSize() + 1);

        class ZeroIn extends Reader {

            @Override
            public int read() {
                return -1;
            }

            @Override
            public int read(@NotNull char[] b, int off, int len) {
                return 0;
            }

            @Override
            public void close() {
            }
        }

        {
            // limited
            CharReader reader1 = CharReader.from(new ZeroIn()).limit(11);
            assertEquals(reader1.availableTo(IOKit.nullWriter()), 0);
            assertEquals(reader1.availableTo(IOKit.nullWriter(), 100), 0);
            assertEquals(reader1.availableTo(new char[1]), 0);
            assertEquals(reader1.availableTo(new char[1], 0, 1), 0);
            assertEquals(reader1.availableTo(CharBuffer.allocate(1)), 0);
            assertEquals(reader1.availableTo(CharBuffer.allocate(1), 1), 0);
            // -1
            CharReader reader2 = CharReader.from(new CharArrayReader(new char[10])).limit(1);
            assertEquals(reader2.availableTo(IOKit.nullWriter()), 1);
            assertEquals(reader2.availableTo(IOKit.nullWriter()), -1);
            assertEquals(reader2.availableTo(IOKit.nullWriter(), 1), -1);
            assertEquals(reader2.availableTo(new char[1]), -1);
            assertEquals(reader2.availableTo(new char[1], 0, 1), -1);
            assertEquals(reader2.availableTo(CharBuffer.allocate(1)), -1);
            assertEquals(reader2.availableTo(CharBuffer.allocate(1), 1), -1);
            // 0
            assertEquals(reader2.availableTo(IOKit.nullWriter(), 0), 0);
            assertEquals(reader2.availableTo(new char[0]), 0);
            assertEquals(reader2.availableTo(new char[1], 0, 0), 0);
            assertEquals(reader2.availableTo(CharBuffer.allocate(0)), 0);
            assertEquals(reader2.availableTo(CharBuffer.allocate(0), 1), 0);
            assertEquals(reader2.availableTo(CharBuffer.allocate(1), 0), 0);
        }
    }

    private void testAvailable(int size) throws Exception {
        char[] src = randomChars(size);

        class In extends Reader {

            private final char[] data = src;
            private int pos = 0;
            private boolean zero = true;

            private int available() {
                if (pos >= data.length) {
                    return 0;
                }
                if (zero) {
                    return 0;
                }
                return 1;
            }

            @Override
            public int read() {
                return pos < data.length ? data[pos++] & 0xFF : -1;
            }

            @Override
            public int read(@NotNull char[] b, int off, int len) {
                if (pos >= data.length) {
                    return -1;
                }
                int readSize = available();
                if (readSize == 0) {
                    zero = false;
                    return 0;
                } else {
                    zero = true;
                    b[off] = data[pos++];
                    return 1;
                }
            }

            @Override
            public void close() {
            }
        }

        // input stream
        testAvailable(size, src, () -> CharReader.from(new In()), false);
        // char array
        testAvailable(size, src, () -> CharReader.from(src), true);
        // char sequence
        testAvailable(size, src, () -> CharReader.from(new String(src)), true);
        // char buffer
        testAvailable(size, src, () -> CharReader.from(CharBuffer.wrap(src)), true);
        // limited
        testAvailable(size, src, () -> CharReader.from(new In()).limit(size), false);
        testAvailable(size, src, () -> CharReader.from(new In()).limit(size + 1), false);
        testAvailable(
            size - 1,
            Arrays.copyOf(src, size - 1),
            () -> CharReader.from(new In()).limit(size - 1),
            false
        );
        testAvailable(size, src, () -> CharReader.from(src).limit(size), true);
        testAvailable(size, src, () -> CharReader.from(src).limit(size + 1), true);
        testAvailable(
            size - 1,
            Arrays.copyOf(src, size - 1),
            () -> CharReader.from(src).limit(size - 1),
            true
        );
    }

    private void testAvailable(
        int size, char[] src, Supplier<CharReader> supplier, boolean preKnown
    ) throws Exception {
        {
            // available
            CharReader reader = supplier.get();
            assertFalse(reader.available(0).end());
            assertFalse(reader.available(0).data().hasRemaining());
            if (preKnown) {
                CharSegment s = reader.available();
                assertTrue(s.end());
                assertEquals(BufferKit.copyContent(s.data()), src);
                assertTrue(reader.available(1).end());
            } else {
                CharSegment s0 = reader.available();
                assertFalse(s0.end());
                assertEquals(BufferKit.copyContent(s0.data()).length, 0);
                CharsBuilder builder = new CharsBuilder();
                while (true) {
                    CharSegment s1 = reader.available();
                    builder.append(s1.data());
                    CharSegment s2 = reader.available(size);
                    builder.append(s2.data());
                    if (s2.end()) {
                        break;
                    }
                }
                assertEquals(builder.toCharArray(), src);
                CharSegment se = reader.available(size);
                assertTrue(se.end());
                assertFalse(se.data().hasRemaining());
            }
        }
        {
            // to output stream
            CharsBuilder builder = new CharsBuilder();
            CharReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(builder), preKnown ? size : 0);
            while (true) {
                long readSize = reader1.availableTo(builder);
                if (readSize < 0) {
                    break;
                }
            }
            assertEquals(builder.toCharArray(), src);
            builder.reset();
            CharReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(builder, size * 2L), preKnown ? size : 0);
            while (true) {
                long readSize = reader2.availableTo(builder, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertEquals(builder.toCharArray(), src);
            builder.reset();
        }
        {
            // to array
            char[] dst = new char[size * 2];
            int c = 0;
            CharReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader1.availableTo(dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertEquals(Arrays.copyOf(dst, size), src);
            dst = new char[size * 2];
            c = 0;
            CharReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader2.availableTo(dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertEquals(Arrays.copyOf(dst, size), src);
        }
        {
            // to buffer
            CharBuffer dst = CharBuffer.allocate(size * 2);
            int c = 0;
            CharReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader1.availableTo(dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertEquals(BufferKit.read(dst), src);
            dst = CharBuffer.allocate(size * 2);
            c = 0;
            CharReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader2.availableTo(dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertEquals(BufferKit.read(dst), src);
        }
    }

    @Test
    public void testReadAll() throws Exception {
        testReadAll(0);
        testReadAll(16);
        testReadAll(32);
        testReadAll(IOKit.bufferSize());
        testReadAll(IOKit.bufferSize() + 1);
    }

    private void testReadAll(int size) throws Exception {
        char[] data = randomChars(size);
        {
            // input stream
            CharReader reader = CharReader.from(new CharArrayReader(data));
            CharBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // array
            CharReader reader = CharReader.from(data);
            CharBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // sequence
            CharReader reader = CharReader.from(new String(data));
            CharBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(BufferKit.copyContent(buffer), data);
            }
            assertNull(reader.read());
        }
        {
            // buffer
            CharReader reader = CharReader.from(CharBuffer.wrap(data));
            CharBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // limited
            CharReader reader = CharReader.from(data).limit(size);
            CharBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
    }

    @Test
    public void testShareChars() {
        int dataSize = 1024;
        int limitSize = dataSize / 2;
        {
            // reader
            char[] data = randomChars(dataSize);
            testShareChars(
                CharReader.from(new CharArrayReader(data)),
                CharBuffer.wrap(data),
                false, false
            );
            testShareChars(
                CharReader.from(new CharArrayReader(data)).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                false, false
            );
        }
        {
            // char array
            char[] data = randomChars(dataSize);
            testShareChars(CharReader.from(data), CharBuffer.wrap(data), true, true);
            testShareChars(
                CharReader.from(data).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                true, true
            );
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShareChars(
                CharReader.from(dataPadding, 33, data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
            testShareChars(
                CharReader.from(dataPadding, 33, data.length).limit(limitSize),
                CharBuffer.wrap(dataPadding, 33, limitSize),
                true, true
            );
        }
        {
            // char sequence
            char[] data = randomChars(dataSize);
            CharSequence dataStr = CharBuffer.wrap(data);
            testShareChars(CharReader.from(dataStr), CharBuffer.wrap(data), false, true);
            testShareChars(
                CharReader.from(dataStr).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                false, true
            );
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            CharSequence dataStrPadding = CharBuffer.wrap(dataPadding);
            testShareChars(
                CharReader.from(dataStrPadding, 33, 33 + data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                false, true
            );
            testShareChars(
                CharReader.from(dataStrPadding, 33, 33 + data.length).limit(limitSize),
                CharBuffer.wrap(dataPadding, 33, limitSize),
                false, true
            );
        }
        {
            // char buffer
            char[] data = randomChars(dataSize);
            testShareChars(
                CharReader.from(CharBuffer.wrap(data)),
                CharBuffer.wrap(data),
                true, true
            );
            testShareChars(
                CharReader.from(CharBuffer.wrap(data)).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                true, true
            );
            CharBuffer direct = BufferKit.copyDirect(data);
            testShareChars(CharReader.from(direct), direct.slice(), true, true);
            direct.clear();
            testShareChars(
                CharReader.from(direct).limit(limitSize),
                BufferKit.slice(direct, limitSize),
                true, true
            );
        }
    }

    private void testShareChars(
        CharReader reader, CharBuffer data, boolean sharedReaderToData, boolean sharedDataToReader
    ) {
        CharSegment segment = reader.read(data.remaining() * 2);
        CharBuffer readBuf = segment.data();
        assertEquals(readBuf, data);
        assertTrue(segment.end());
        if (sharedReaderToData) {
            for (int i = 0; i < readBuf.remaining(); i++) {
                readBuf.put(i, (char) (readBuf.get(i) + 1));
            }
            assertEquals(readBuf, data);
        }
        if (sharedDataToReader) {
            for (int i = 0; i < data.remaining(); i++) {
                data.put(i, (char) (data.get(i) + 100));
            }
            assertEquals(data, readBuf);
        }
    }

    @Test
    public void testCharsSegment() throws Exception {
        char[] chars = randomChars(64);
        CharReader reader = CharReader.from(chars);
        CharSegment segment = reader.read(chars.length * 2);
        assertSame(segment.data().array(), chars);
        assertTrue(segment.end());
        CharSegment segmentCopy = segment.clone();
        assertEquals(segmentCopy.data(), CharBuffer.wrap(chars));
        assertTrue(segmentCopy.end());
        assertNotSame(segmentCopy.data().array(), chars);
        assertEquals(segment.copyArray(), chars);
        assertNotSame(segment.copyArray(), chars);
        assertEquals(segment.array(), chars);
        assertEquals(segment.array(), new char[0]);
    }

    @Test
    public void testAsReader() throws Exception {
        testAsReader(128);
        testAsReader(IOKit.bufferSize());
        testAsReader(IOKit.bufferSize() + 1);
    }

    private void testAsReader(int size) throws Exception {
        char[] data = randomChars(size, 'a', 'z');
        byte[] bytes = new String(data).getBytes(CharsKit.defaultCharset());
        CharsBuilder builder = new CharsBuilder(size);
        {
            // reader
            FakeFile file = new FakeFile(bytes);
            InputStream in = IOKit.newInputStream(file, 0);
            IOImplsTest.testReader(
                CharReader.from(IOKit.newReader(in, CharsKit.defaultCharset())).asReader(),
                data,
                false,
                true,
                false
            );
            testReadToBuilder(CharReader.from(new CharArrayReader(data)), data, builder);
        }
        {
            // array
            IOImplsTest.testReader(
                CharReader.from(data).asReader(),
                data,
                false,
                false,
                true
            );
            testReadToBuilder(CharReader.from(data), data, builder);
        }
        {
            // string
            IOImplsTest.testReader(
                CharReader.from(new String(data)).asReader(),
                data,
                false,
                false,
                true
            );
            testReadToBuilder(CharReader.from(new String(data)), data, builder);
        }
        {
            // buffer
            IOImplsTest.testReader(
                CharReader.from(CharBuffer.wrap(data)).asReader(),
                data,
                false,
                false,
                false
            );
            testReadToBuilder(CharReader.from(CharBuffer.wrap(data)), data, builder);
        }
        {
            // limited
            IOImplsTest.testReader(
                CharReader.from(data).limit(size).asReader(),
                data,
                false,
                false,
                true
            );
            testReadToBuilder(CharReader.from(data).limit(size), data, builder);
            IOImplsTest.testReader(
                CharReader.from(data).limit(size - 5).asReader(),
                Arrays.copyOf(data, size - 5),
                false,
                false,
                true
            );
            testReadToBuilder(CharReader.from(data).limit(size - 5), Arrays.copyOf(data, size - 5), builder);
            IOImplsTest.testReader(
                CharReader.from(data).limit(size + 5).asReader(),
                data,
                false,
                false,
                true
            );
            testReadToBuilder(CharReader.from(data).limit(size + 5), data, builder);
        }
    }

    public static void testReadToBuilder(CharReader reader, char[] data, CharsBuilder builder) {
        builder.reset();
        Reader asIn = reader.asReader();
        reader.readTo(builder, 1);
        IOKit.readTo(asIn, builder);
        assertEquals(builder.toCharArray(), data);
    }

    @Test
    public void testReady() throws Exception {
        testReady(128);
        testReady(IOKit.bufferSize());
        testReady(IOKit.bufferSize() + 1);
    }

    private void testReady(int size) throws Exception {
        char[] data = randomChars(size);
        {
            // reader
            CharReader reader = CharReader.from(new CharArrayReader(data));
            assertEquals(reader.ready(), 0);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // array
            CharReader reader = CharReader.from(data);
            assertEquals(reader.ready(), size);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // string
            CharReader reader = CharReader.from(new String(data));
            assertEquals(reader.ready(), size);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // buffer
            CharReader reader = CharReader.from(CharBuffer.wrap(data));
            assertEquals(reader.ready(), size);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // limited
            CharReader reader1 = CharReader.from(data).limit(size);
            assertEquals(reader1.ready(), size);
            reader1.read();
            assertEquals(reader1.ready(), 0);
            CharReader reader2 = CharReader.from(data).limit(size - 5);
            assertEquals(reader2.ready(), size - 5);
            reader2.read();
            assertEquals(reader2.ready(), 0);
            CharReader reader3 = CharReader.from(data).limit(size + 5);
            assertEquals(reader3.ready(), size);
            reader3.read();
            assertEquals(reader3.ready(), 0);
            CharReader reader4 = CharReader.from(new CharArrayReader(data)).limit(size);
            assertEquals(reader4.ready(), 0);
            reader4.read();
            assertEquals(reader4.ready(), 0);
        }
    }

    @Test
    public void testOthers() throws Exception {
        TestReader tin = new TestReader(new CharArrayReader(new char[0]));
        tin.setNextOperation(ReadOps.THROW, 99);
        {
            // mark/reset error
            CharReader reader = CharReader.from(tin);
            expectThrows(IORuntimeException.class, reader::mark);
            expectThrows(IORuntimeException.class, reader::reset);
            expectThrows(IORuntimeException.class, reader::close);
            reader = CharReader.from(CharBuffer.allocate(1));
            expectThrows(IORuntimeException.class, reader::reset);
            Reader in1 = CharReader.from(CharBuffer.allocate(1)).limit(1).asReader();
            expectThrows(IOException.class, in1::reset);
        }
        {
            // asReader
            Reader in2 = CharReader.from(tin).limit(1).asReader();
            expectThrows(IOException.class, () -> in2.skip(1));
            Reader in3 = CharReader.from(tin).limit(1).asReader();
            expectThrows(IOException.class, () -> in3.close());
            Reader in4 = CharReader.from(tin).limit(1).asReader();
            expectThrows(IOException.class, () -> in4.mark(1));
            TestReader errIn = new TestReader(new CharArrayReader(new char[0]));
            errIn.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IOException.class, () -> CharReader.from(errIn).limit(1).asReader().read());
            expectThrows(IOException.class, () -> CharReader.from(errIn).limit(1).asReader().read(new char[2]));
        }
    }
}
