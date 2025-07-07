package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.random.RandomKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CharReaderTest {

    private static final int DST_SIZE = 256;

    @Test
    public void testRead() throws Exception {
        testRead0(0, 1);
        testRead0(1, 1);
        testRead0(32, 1);
        testRead0(32, 16);
        testRead0(32, 32);
        testRead0(32, 64);
        testRead0(128, 16);
        testRead0(128, 33);
        testRead0(128, 111);
        testRead0(128, 128);
        testRead0(128, 129);
        testRead0(128, 1024);

        // error
        {
            TestReader tr = new TestReader(new CharArrayReader(new char[10]));
            tr.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> CharReader.from(tr).skip(100));
        }
    }

    private void testRead0(int dataSize, int readSize) throws Exception {
        {
            // reader
            char[] data = RandomKit.fill(new char[dataSize]);
            testRead0(CharReader.from(new CharArrayReader(data)), data, readSize, false);
            testSkip0(CharReader.from(new CharArrayReader(data)), data, readSize);
            testRead0(CharReader.from(new OneCharReader(data)), data, readSize, false);
            testSkip0(CharReader.from(new OneCharReader(data)), data, readSize);
            TestReader tr = new TestReader(new CharArrayReader(data));
            tr.setNextOperation(ReadOps.READ_ZERO);
            testSkip0(CharReader.from(tr), data, readSize);
            if (dataSize >= 128) {
                IOImplsTest.testReader(CharReader.from(IOKit.newReader(data)).asReader(),
                    data, true, false, false
                );
            }
        }
        {
            // char array
            char[] data = RandomKit.fill(new char[dataSize]);
            testRead0(CharReader.from(data), data, readSize, true);
            testSkip0(CharReader.from(data), data, readSize);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testRead0(
                CharReader.from(dataPadding, 33, data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkip0(CharReader.from(dataPadding, 33, data.length), data, readSize);
            if (dataSize >= 128) {
                IOImplsTest.testReader(CharReader.from(data).asReader(),
                    data, true, false, true
                );
                IOImplsTest.testReader(CharReader.from(dataPadding, 33, data.length).asReader(),
                    Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                    true, false, true
                );
            }
        }
        {
            // char sequence
            char[] data = RandomKit.fill(new char[dataSize]);
            String dataStr = new String(data);
            testRead0(CharReader.from(dataStr), data, readSize, true);
            testSkip0(CharReader.from(data), data, readSize);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            String dataStrPadding = new String(dataPadding);
            testRead0(
                CharReader.from(dataStrPadding, 33, 33 + data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkip0(CharReader.from(dataStrPadding, 33, 33 + data.length), data, readSize);
            if (dataSize >= 128) {
                IOImplsTest.testReader(CharReader.from(dataStr).asReader(),
                    data, true, false, true
                );
                IOImplsTest.testReader(CharReader.from(dataStrPadding, 33, 33 + data.length).asReader(),
                    Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                    true, false, true
                );
            }
        }
        {
            // buffer
            char[] data = RandomKit.fill(new char[dataSize]);
            testRead0(CharReader.from(CharBuffer.wrap(data)), data, readSize, true);
            testSkip0(CharReader.from(CharBuffer.wrap(data)), data, readSize);
            if (dataSize >= 128) {
                IOImplsTest.testReader(CharReader.from(CharBuffer.wrap(data)).asReader(),
                    data, true, false, false
                );
            }
        }
        {
            // limited
            char[] data = RandomKit.fill(new char[dataSize]);
            testRead0(
                CharReader.from(data).limit(data.length),
                data,
                readSize, true
            );
            testSkip0(
                CharReader.from(data),
                data,
                readSize
            );
            testRead0(
                CharReader.from(data).limit(data.length + 5),
                data,
                readSize, true
            );
            testSkip0(
                CharReader.from(data).limit(data.length + 5),
                data,
                readSize
            );
            if (data.length > 5) {
                testRead0(
                    CharReader.from(data).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize, false
                );
                testSkip0(
                    CharReader.from(data).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize
                );
            }
            if (dataSize >= 128) {
                IOImplsTest.testReader(
                    CharReader.from(data).limit(data.length).asReader(),
                    data, false, false, true
                );
                IOImplsTest.testReader(
                    CharReader.from(data).limit(data.length + 5).asReader(),
                    data, false, false, true
                );
                IOImplsTest.testReader(
                    CharReader.from(data).limit(data.length - 5).asReader(),
                    Arrays.copyOf(data, data.length - 5), false, false, true
                );
            }
        }
    }

    private void testRead0(CharReader reader, char[] data, int readSize, boolean preKnown) {
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

    private void testSkip0(CharReader reader, char[] data, int readSize) {
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
    public void testReadTo() {
        testReadTo0(0, 0);
        testReadTo0(0, 1);
        testReadTo0(1, 1);
        testReadTo0(2, 2);
        testReadTo0(64, 1);
        testReadTo0(64, 33);
        testReadTo0(64, 64);
        testReadTo0(64, 111);
        testReadTo0(111, 77);
        testReadTo0(111, 111);
        testReadTo0(111, 333);
        testReadTo0(DST_SIZE, DST_SIZE);
        testReadTo0(DST_SIZE, DST_SIZE + 6);
        testReadTo0(DST_SIZE, DST_SIZE - 6);
    }

    private void testReadTo0(int dataSize, int readSize) {
        char[] data = RandomKit.fill(new char[dataSize]);
        char[] dataPadding = new char[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // reader
            testReadTo0(() -> CharReader.from(new CharArrayReader(data)), data, readSize);
            testReadTo0(() -> CharReader.from(new OneCharReader(data)), data, readSize);
        }
        {
            // char array
            testReadTo0(() -> CharReader.from(data), data, readSize);
            testReadTo0(() -> CharReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // char sequence
            testReadTo0(() -> CharReader.from(new String(data)), data, readSize);
            testReadTo0(() -> CharReader.from(new String(dataPadding), 33, 33 + data.length), data, readSize);
        }
        {
            // char buffer
            testReadTo0(() -> CharReader.from(CharBuffer.wrap(data)), data, readSize);
            testReadTo0(() -> CharReader.from(BufferKit.directBuffer(data)), data, readSize);
        }
        {
            // limited
            testReadTo0(() ->
                CharReader.from(new CharArrayReader(data)).limit(data.length), data, readSize);
            testReadTo0(() ->
                CharReader.from(new CharArrayReader(data)).limit(data.length + 5), data, readSize);
            if (data.length > 5) {
                testReadTo0(() ->
                        CharReader.from(new CharArrayReader(data)).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize
                );
            }
        }
    }

    private void testReadTo0(Supplier<CharReader> supplier, char[] data, int readSize) {
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
                assertEquals(reader.readTo(BufferKit.directBuffer(0)), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(1)), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0)), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0)), 0);
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
                    dst = BufferKit.directBuffer(DST_SIZE);
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
                assertEquals(reader.readTo(BufferKit.directBuffer(1)), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0)), 0);
            }
            // error
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(CharBuffer.allocate(1).asReadOnlyBuffer()));
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(BufferKit.directBuffer(1).asReadOnlyBuffer()));
            }
        }
        {
            // to buffer offset
            if (data.length == 0) {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(1), 1), -1);
                assertEquals(reader.readTo(BufferKit.directBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(1), 1), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0), 1), 0);
            } else {
                CharReader reader = supplier.get();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0), 1), 0);
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
                    dst = BufferKit.directBuffer(DST_SIZE);
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
                assertEquals(reader.readTo(BufferKit.directBuffer(1), 1), -1);
                reader.close();
                assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(CharBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(1), 0), 0);
                assertEquals(reader.readTo(BufferKit.directBuffer(0), 1), 0);
            }
            // error
            expectThrows(IllegalArgumentException.class, () ->
                supplier.get().readTo(CharBuffer.allocate(1), -1));
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(CharBuffer.allocate(1).asReadOnlyBuffer(), 1));
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(BufferKit.directBuffer(1).asReadOnlyBuffer(), 1));
            }
        }
    }

    private int minSize(int totalSize, int readSize, int remaining) {
        return Math.min(remaining, Math.min(totalSize, readSize));
    }

    @Test
    public void testShare() {
        int dataSize = 1024;
        int limitSize = dataSize / 2;
        {
            // reader
            char[] data = RandomKit.fill(new char[dataSize]);
            testShare(
                CharReader.from(new CharArrayReader(data)),
                CharBuffer.wrap(data),
                false, false
            );
            testShare(
                CharReader.from(new CharArrayReader(data)).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                false, false
            );
        }
        {
            // char array
            char[] data = RandomKit.fill(new char[dataSize]);
            testShare(CharReader.from(data), CharBuffer.wrap(data), true, true);
            testShare(
                CharReader.from(data).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                true, true
            );
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShare(
                CharReader.from(dataPadding, 33, data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
            testShare(
                CharReader.from(dataPadding, 33, data.length).limit(limitSize),
                CharBuffer.wrap(dataPadding, 33, limitSize),
                true, true
            );
        }
        {
            // char sequence
            char[] data = RandomKit.fill(new char[dataSize]);
            CharSequence dataStr = JieString.asChars(data);
            testShare(CharReader.from(dataStr), CharBuffer.wrap(data), false, true);
            testShare(
                CharReader.from(dataStr).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                false, true
            );
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            CharSequence dataStrPadding = JieString.asChars(dataPadding);
            testShare(
                CharReader.from(dataStrPadding, 33, 33 + data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                false, true
            );
            testShare(
                CharReader.from(dataStrPadding, 33, 33 + data.length).limit(limitSize),
                CharBuffer.wrap(dataPadding, 33, limitSize),
                false, true
            );
        }
        {
            // char buffer
            char[] data = RandomKit.fill(new char[dataSize]);
            testShare(
                CharReader.from(CharBuffer.wrap(data)),
                CharBuffer.wrap(data),
                true, true
            );
            testShare(
                CharReader.from(CharBuffer.wrap(data)).limit(limitSize),
                CharBuffer.wrap(data, 0, limitSize),
                true, true
            );
            CharBuffer direct = BufferKit.directBuffer(data);
            testShare(CharReader.from(direct), direct.slice(), true, true);
            direct.clear();
            testShare(
                CharReader.from(direct).limit(limitSize),
                BufferKit.slice(direct, limitSize),
                true, true
            );
        }
    }

    private void testShare(
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
    public void testSegment() throws Exception {
        char[] chars = RandomKit.fill(new char[64]);
        CharReader reader = CharReader.from(chars);
        CharSegment segment = reader.read(chars.length * 2);
        assertSame(segment.data().array(), chars);
        assertTrue(segment.end());
        CharSegment segmentCopy = segment.clone();
        assertEquals(segmentCopy.data(), CharBuffer.wrap(chars));
        assertTrue(segmentCopy.end());
        assertNotSame(segmentCopy.data().array(), chars);
        assertEquals(segment.copyCharArray(), chars);
        assertNotSame(segment.copyCharArray(), chars);
        assertEquals(segment.toCharArray(), chars);
        assertEquals(segment.toCharArray(), new char[0]);
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
            Reader in2 = CharReader.from(tin).limit(1).asReader();
            expectThrows(IOException.class, () -> in2.skip(1));
            Reader in3 = CharReader.from(tin).limit(1).asReader();
            expectThrows(IOException.class, () -> in3.close());
            Reader in4 = CharReader.from(tin).limit(1).asReader();
            expectThrows(IOException.class, () -> in4.mark(1));
        }
    }
}
