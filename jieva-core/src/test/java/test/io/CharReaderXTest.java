package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.Constants;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.CharReaderX;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.MaterialBox;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CharReaderXTest {

    @Test
    public void testRead() {
        testRead0(10240);
        testRead0(10240);
        testRead0(1024);
        testRead0(333);
        testRead0(77);
        testRead0(0);
        testRead0(1);
        testRead0(1);
        testRead0(2);
        testRead0(4);
    }

    private void testRead0(int dataSize) {
        {
            // input stream
            char[] data = JieRandom.fill(new char[dataSize]);
            testRead0(CharReaderX.from(new CharArrayReader(data)), CharBuffer.wrap(data), false, false);
            testSkip0(CharReaderX.from(new CharArrayReader(data)), data);
        }
        {
            // char array
            char[] data = JieRandom.fill(new char[dataSize]);
            testRead0(CharReaderX.from(data), CharBuffer.wrap(data), true, true);
            testSkip0(CharReaderX.from(data), data);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testRead0(
                CharReaderX.from(dataPadding, 33, data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
            testSkip0(CharReaderX.from(dataPadding, 33, data.length), data);
        }
        {
            // char sequence
            char[] data = JieRandom.fill(new char[dataSize]);
            String dataStr = new String(data);
            testRead0(CharReaderX.from(dataStr), CharBuffer.wrap(data), true, false);
            testSkip0(CharReaderX.from(data), data);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            String dataStrPadding = new String(dataPadding);
            testRead0(
                CharReaderX.from(dataStrPadding, 33, 33 + data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                true, false
            );
            testSkip0(CharReaderX.from(dataStrPadding, 33, 33 + data.length), data);
        }
        {
            // heap buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            testRead0(CharReaderX.from(CharBuffer.wrap(data)), CharBuffer.wrap(data), true, true);
            testSkip0(CharReaderX.from(CharBuffer.wrap(data)), data);
        }
        {
            // direct buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            CharBuffer direct = MaterialBox.copyDirect(data);
            direct.mark();
            testRead0(CharReaderX.from(direct), direct.slice(), true, true);
            direct.reset();
            testSkip0(CharReaderX.from(direct), data);
        }
    }

    private void testRead0(CharReaderX reader, CharBuffer data, boolean preKnown, boolean shared) {
        reader.mark();
        data.mark();
        assertFalse(reader.read(0).end());
        assertEquals(reader.read(0).data().remaining(), 0);
        int dataLength = data.remaining();
        if (dataLength == 0) {
            assertFalse(reader.read(0).end());
            assertTrue(reader.read(1).end());
        }
        CharsBuilder newData = new CharsBuilder();
        if (dataLength > 0) {
            int length = 1;
            int startIndex = 0;
            int count = 0;
            while (true) {
                int endIndex = Math.min(dataLength, startIndex + length);
                int actualLen = Math.min(length, endIndex - startIndex);
                CharSegment segment = reader.read(length);
                CharBuffer readBuf = segment.data();
                assertEquals(readBuf.remaining(), actualLen);
                char[] dataBuf = new char[actualLen];
                data.get(dataBuf);
                assertEquals(
                    JieBuffer.read(readBuf),
                    dataBuf
                );
                if (shared) {
                    char[] newChars = JieRandom.fill(new char[actualLen]);
                    readBuf.flip();
                    readBuf.put(newChars);
                    newData.append(newChars);
                }
                if (length > actualLen) {
                    assertTrue(segment.end());
                }
                if (length < actualLen) {
                    assertFalse(segment.end());
                }
                if (length == actualLen && endIndex >= dataLength) {
                    assertEquals(segment.end(), preKnown);
                }
                count += actualLen;
                if (endIndex >= dataLength) {
                    break;
                }
                length *= 2;
                startIndex = endIndex;
            }
            assertEquals(count, dataLength);
            assertTrue(reader.read(1).end());
        }
        reader.reset();
        data.reset();
        CharSegment segment = reader.read(dataLength == 0 ? 1 : dataLength * 2);
        assertTrue(segment.end());
        char[] readBuf = JieBuffer.read(segment.data());
        char[] dataBuf = JieBuffer.read(data);
        assertEquals(readBuf, dataBuf);
        if (shared) {
            assertEquals(readBuf, newData.toCharArray());
        }
        assertTrue(reader.read(1).end());
    }

    private void testSkip0(CharReaderX reader, char[] data) {
        if (reader.markSupported()) {
            reader.mark();
        }
        assertEquals(reader.skip(0), 0);
        if (data.length == 0) {
            assertEquals(reader.skip(0), 0);
            assertEquals(reader.skip(1), 0);
        }
        int length = 1;
        int startIndex = 0;
        int count = 0;
        if (data.length > 0) {
            while (true) {
                int endIndex = Math.min(data.length, startIndex + length);
                int actualLen = Math.min(length, endIndex - startIndex);
                long actualSkipped = reader.skip(length);
                assertEquals(actualSkipped, actualLen);
                count += actualLen;
                if (endIndex >= data.length) {
                    break;
                }
                length *= 2;
                startIndex = endIndex;
            }
            assertEquals(count, data.length);
        }
        if (reader.markSupported()) {
            reader.reset();
            long actualSkipped = reader.skip(Integer.MAX_VALUE);
            assertEquals(actualSkipped, data.length);
        }
    }

    @Test
    public void testReadTo() {
        testReadTo0(10240);
        testReadTo0(10240);
        testReadTo0(1024);
        testReadTo0(333);
        testReadTo0(77);
        testReadTo0(0);
        testReadTo0(1);
        testReadTo0(1);
        testReadTo0(2);

        {
            // special: nio
            class NioInput extends Reader {

                private int count = 1;

                @Override
                public int read() throws IOException {
                    if (count > 0) {
                        count--;
                        return 66;
                    }
                    return -1;
                }

                @Override
                public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
                    int c = 0;
                    for (int i = 0; i < len; i++) {
                        int r = read();
                        if (r < 0) {
                            return c == 0 ? -1 : c;
                        }
                        cbuf[i + off] = (char) r;
                        c++;
                    }
                    return c;
                }

                @Override
                public void close() {
                }
            }
            char[] dst = new char[2];
            assertEquals(CharReaderX.from(new NioInput()).readTo(dst), 1);
            assertEquals(dst[0], 66);
        }
    }

    private void testReadTo0(int dataSize) {
        char[] data = JieRandom.fill(new char[dataSize]);
        char[] dataPadding = new char[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(CharReaderX.from(new CharArrayReader(data)), data);
        }
        {
            // char array
            testReadTo0(CharReaderX.from(data), data);
            testReadTo0(CharReaderX.from(dataPadding, 33, data.length), data);
        }
        {
            // char sequence
            testReadTo0(CharReaderX.from(new String(data)), data);
            testReadTo0(CharReaderX.from(new String(dataPadding), 33, 33 + data.length), data);
        }
        {
            // char buffer
            testReadTo0(CharReaderX.from(CharBuffer.wrap(data)), data);
            CharBuffer direct = MaterialBox.copyDirect(data);
            direct.put(data);
            direct.flip();
            testReadTo0(CharReaderX.from(direct), data);
        }
    }

    private void testReadTo0(CharReaderX reader, char[] data) {
        reader.mark();
        {
            // to output stream
            CharsBuilder out = new CharsBuilder();
            assertEquals(reader.readTo(out), data.length == 0 ? -1 : data.length);
            assertEquals(out.toCharArray(), data);
            assertEquals(reader.readTo(out), -1);
            assertEquals(out.toCharArray(), data);
            assertEquals(reader.readTo(out, 0), 0);
            assertEquals(out.toCharArray(), data);
            reader.reset();
            out.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    assertEquals(reader.readTo(out, length), Math.min(length, endIndex - startIndex));
                    assertEquals(out.toCharArray(), Arrays.copyOfRange(data, startIndex, endIndex));
                    out.reset();
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
            }
            reader.reset();
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> reader.readTo(JieIO.newWriter(new ErrorOutputStream())));
                reader.reset();
                expectThrows(IORuntimeException.class, () -> reader.readTo(JieIO.newWriter(new ErrorOutputStream()), 5));
                reader.reset();
            }
        }
        {
            // to char array
            char[] dst = new char[data.length];
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(dst, data);
            assertEquals(reader.readTo(dst), dst.length == 0 ? 0 : -1);
            assertEquals(dst, data);
            reader.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = new char[data.length];
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    assertEquals(
                        reader.readTo(dst, startIndex, Math.min(length, endIndex - startIndex)),
                        Math.min(length, endIndex - startIndex)
                    );
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
                assertEquals(dst, data);
            }
            reader.reset();
        }
        {
            // to heap buffer
            CharBuffer dst = CharBuffer.allocate(data.length);
            assertEquals(reader.readTo(dst), data.length);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            dst = CharBuffer.allocate(data.length);
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(reader.readTo(dst), dst.remaining() == 0 ? 0 : -1);
            assertEquals(reader.readTo(CharBuffer.allocate(1)), -1);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = CharBuffer.allocate(data.length);
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    int actualLen = Math.min(length, endIndex - startIndex);
                    CharBuffer slice = JieBuffer.slice(dst, actualLen);
                    assertEquals(
                        reader.readTo(slice),
                        Math.min(length, endIndex - startIndex)
                    );
                    dst.position(dst.position() + actualLen);
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
                dst.flip();
                assertEquals(JieBuffer.read(dst), data);
            }
            reader.reset();
        }
        {
            // to direct buffer
            CharBuffer dst = ByteBuffer.allocateDirect(data.length * 2).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
            assertEquals(reader.readTo(dst), data.length);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            dst = ByteBuffer.allocateDirect(data.length * 2).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(reader.readTo(dst), dst.remaining() == 0 ? 0 : -1);
            assertEquals(reader.readTo(ByteBuffer.allocateDirect(2).order(ByteOrder.BIG_ENDIAN).asCharBuffer()), -1);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = ByteBuffer.allocateDirect(data.length * 2).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    int actualLen = Math.min(length, endIndex - startIndex);
                    CharBuffer slice = JieBuffer.slice(dst, actualLen);
                    assertEquals(
                        reader.readTo(slice),
                        Math.min(length, endIndex - startIndex)
                    );
                    dst.position(dst.position() + actualLen);
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
                dst.flip();
                assertEquals(JieBuffer.read(dst), data);
            }
            reader.reset();
        }
    }

    @Test
    public void testShare() {
        int dataSize = 1024;
        {
            // input stream
            char[] data = JieRandom.fill(new char[dataSize]);
            testShare(CharReaderX.from(new CharArrayReader(data)), CharBuffer.wrap(data), false, false);
        }
        {
            // char array
            char[] data = JieRandom.fill(new char[dataSize]);
            testShare(CharReaderX.from(data), CharBuffer.wrap(data), true, true);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShare(
                CharReaderX.from(dataPadding, 33, data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
        }
        {
            // char sequence
            char[] data = JieRandom.fill(new char[dataSize]);
            CharSequence dataStr = JieString.asChars(data);
            testShare(CharReaderX.from(dataStr), CharBuffer.wrap(data), false, true);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            CharSequence dataStrPadding = JieString.asChars(dataPadding);
            testShare(
                CharReaderX.from(dataStrPadding, 33, 33 + data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                false, true
            );
        }
        {
            // heap buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            testShare(CharReaderX.from(CharBuffer.wrap(data)), CharBuffer.wrap(data), true, true);
        }
        {
            // direct buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            CharBuffer direct = MaterialBox.copyDirect(data);
            testShare(CharReaderX.from(direct), direct.slice(), true, true);
        }
    }

    private void testShare(
        CharReaderX reader, CharBuffer data, boolean sharedReaderToData, boolean sharedDataToReader
    ) {
        CharSegment segment = reader.read(data.remaining() * 2);
        CharBuffer readBuf = segment.data();
        assertEquals(readBuf, data);
        assertTrue(segment.end());
        if (!readBuf.isReadOnly()) {
            for (int i = 0; i < readBuf.remaining(); i++) {
                readBuf.put(i, (char) (readBuf.get(i) + 1));
            }
            assertEquals(readBuf.equals(data), sharedReaderToData);
        }
        if (!data.isReadOnly()) {
            for (int i = 0; i < data.remaining(); i++) {
                data.put(i, (char) (data.get(i) + 100));
            }
            assertEquals(data.equals(readBuf), sharedDataToReader);
        }
    }

    @Test
    public void testSpecial() throws Exception {
        char[] chars = JieArray.fill(new char[64], Constants.FILL_CHAR);
        CharArrayReader in = new CharArrayReader(chars);
        TestReader testIn = new TestReader(in);
        {
            // NIO tests
            CharReaderX reader = CharReaderX.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO, 10);
            CharSegment s0 = reader.read(chars.length);
            assertEquals(s0.data(), CharBuffer.wrap(chars));
            assertFalse(s0.end());
            s0 = reader.read(1);
            assertTrue(s0.end());
            assertEquals(reader.skip(66), 0);
            in.reset();
            CharReaderX reader2 = CharReaderX.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(reader2.skip(66), chars.length);
            // TestInputStream testIn2 = new TestInputStream(new CharArrayInputStream(new char[2]));
            // testIn2.setNextOperation(ReadOps.READ_ZERO);
            // assertEquals(CharReader.from(testIn2).skip(66), 2);
        }
        {
            // Exception tests
            CharReaderX reader = CharReaderX.from(testIn);
            in.reset();
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.read(66));
            expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
            // expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
            expectThrows(IllegalArgumentException.class, () -> reader.skip(-66));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.readTo(CharBuffer.allocate(1)));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.readTo(new char[1]));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.skip(66));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, reader::mark);
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, reader::reset);
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, reader::close);
        }
        {
            // for segment
            char[] charsCopy = Arrays.copyOf(chars, chars.length);
            CharReaderX reader = CharReaderX.from(charsCopy);
            CharSegment segment = reader.read(charsCopy.length * 2);
            assertSame(segment.data().array(), charsCopy);
            assertTrue(segment.end());
            CharSegment segmentCopy = segment.clone();
            assertEquals(segmentCopy.data(), CharBuffer.wrap(charsCopy));
            assertTrue(segmentCopy.end());
            assertNotSame(segmentCopy.data().array(), charsCopy);
        }
        {
            // special mark/reset
            TestReader tin = new TestReader(new CharArrayReader(new char[2]));
            CharReaderX reader = CharReaderX.from(tin);//.withReadLimit(1);
            assertTrue(reader.markSupported());
            tin.markSupported(false);
            assertFalse(reader.markSupported());
            reader.mark();
            reader.reset();
        }
        {
            // close
            CharReaderX.from(JieIO.newReader(new char[0])).close();
            CharReaderX.from(new char[0]).close();
            CharReaderX.from(CharBuffer.allocate(0)).close();
            CharReaderX.from("").close();
        }
    }
}
