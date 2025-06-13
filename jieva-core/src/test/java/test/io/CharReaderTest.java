package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.Constants;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
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
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CharReaderTest {

    @Test
    public void testReader() {
        testReader(10240, 1024);
        testReader(10240, 10240);
        testReader(1024, 10240);
        testReader(333, 77);
        testReader(77, 333);
        testReader(0, 1);
        testReader(1, 1);
        testReader(1, 2);
        testReader(2, 1);

        // test toCharArray()
        char[] data = JieRandom.fill(new char[1024]);
        assertEquals(CharReader.from(data).read(99999).toCharArray(), data);
    }

    private void testReader(int dataSize, int readSize) {
        {
            // reader
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(new CharArrayReader(data));
            testRead0(reader, dataSize, false);
            testReader(reader, data, 0, dataSize, readSize, false);
            testSkip(
                CharReader.from(new CharArrayReader(data)),
                data, 0, dataSize, readSize, false
            );
            // char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            // CharReader limitedReader = CharReader.from(new CharArrayReader(limitedData)).withReadLimit(dataSize);
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, readSize, false);
            // testSkip(
            //     CharReader.from(new CharArrayReader(limitedData)).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, readSize, false
            // );
        }
        {
            // char array
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(data);
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, readSize, true);
            testSkip(
                CharReader.from(data),
                data, 0, dataSize, readSize, true
            );
            // char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            // CharReader limitedReader = CharReader.from(limitedData).withReadLimit(dataSize);
            // limitedReader.close();
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, readSize, true);
            // testSkip(
            //     CharReader.from(limitedData).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, readSize, true
            // );
        }
        {
            // padded char array
            if (dataSize >= 3) {
                char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
                CharReader reader = CharReader.from(data, 1, dataSize - 2);
                reader.close();
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, readSize, true);
                testSkip(
                    CharReader.from(data, 1, dataSize - 2),
                    data, 1, dataSize - 2, readSize, true
                );
                // char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
                // CharReader limitedReader = CharReader.from(limitedData, 1, dataSize - 2)
                //     .withReadLimit(dataSize);
                // limitedReader.close();
                // testRead0(limitedReader, dataSize, true);
                // testReader(limitedReader, limitedData, 1, dataSize - 2, readSize, true);
                // testSkip(
                //     CharReader.from(limitedData, 1, dataSize - 2),
                //     limitedData, 1, dataSize - 2, readSize, true
                // );
            }
        }
        {
            // char buffer
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(CharBuffer.wrap(data));
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, readSize, true);
            testSkip(
                CharReader.from(CharBuffer.wrap(data)),
                data, 0, dataSize, readSize, true
            );
            // char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            // CharReader limitedReader = CharReader.from(CharBuffer.wrap(limitedData)).withReadLimit(dataSize);
            // limitedReader.close();
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, readSize, true);
            // testSkip(
            //     CharReader.from(CharBuffer.wrap(limitedData)).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, readSize, true
            // );
        }
        {
            // char sequence
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(new String(data));
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, readSize, false);
            testSkip(
                CharReader.from(new String(data)),
                data, 0, dataSize, readSize, false
            );
            // char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            // CharReader limitedReader = CharReader.from(new String(limitedData)).withReadLimit(dataSize);
            // limitedReader.close();
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, readSize, false);
            // testSkip(
            //     CharReader.from(new String(limitedData)).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, readSize, false
            // );
        }
        {
            // padded char sequence
            if (dataSize >= 3) {
                char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
                CharReader reader = CharReader.from(new String(data), 1, dataSize - 1);
                reader.close();
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, readSize, false);
                testSkip(
                    CharReader.from(new String(data), 1, dataSize - 1),
                    data, 1, dataSize - 2, readSize, false
                );
                // char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
                // CharReader limitedReader = CharReader.from(new String(limitedData), 1, dataSize - 1)
                //     .withReadLimit(dataSize);
                // limitedReader.close();
                // testRead0(limitedReader, dataSize, true);
                // testReader(limitedReader, limitedData, 1, dataSize - 2, readSize, false);
                // testSkip(
                //     CharReader.from(new String(limitedData), 1, dataSize - 1),
                //     limitedData, 1, dataSize - 2, readSize, false
                // );
            }
        }
    }

    private void testReader(CharReader reader, char[] chars, int offset, int length, int readSize, boolean shared) {
        if (reader.markSupported()) {
            reader.mark();
            assertEquals(reader.skip(length + 100), length);
            reader.reset();
            reader.mark();
        }
        testReader0(reader, chars, offset, length, readSize, shared);
        if (reader.markSupported()) {
            reader.reset();
            testReader0(reader, chars, offset, length, readSize, shared);
        }
    }

    private void testRead0(CharReader reader, int dataSize, boolean preKnown) {
        CharSegment s0 = reader.read(0);
        assertEquals(s0.data(), JieChars.emptyBuffer());
        if (preKnown) {
            assertEquals(s0.end(), dataSize == 0);
        } else {
            assertFalse(s0.end());
        }
        assertEquals(reader.skip(0), 0);
    }

    private void testSkip(CharReader reader, char[] chars, int offset, int length, int readSize, boolean shared) {
        int skipNum = length / 2;
        assertEquals(reader.skip(skipNum), skipNum);
        int remaining = length - skipNum;
        testReader0(reader, chars, offset + skipNum, remaining, readSize, shared);
    }

    private void testReader0(CharReader reader, char[] chars, int offset, int length, int readSize, boolean shared) {
        char[] charsCopy = Arrays.copyOfRange(chars, offset, offset + length);
        CharsBuilder builder = new CharsBuilder();
        while (true) {
            CharSegment segment = reader.read(readSize);
            CharBuffer buffer = segment.data();
            buffer.mark();
            builder.append(buffer);
            assertFalse(buffer.hasRemaining());
            if (shared) {
                buffer.reset();
                fillSharedData(buffer);
            }
            if (segment.end()) {
                break;
            }
        }
        assertEquals(builder.toCharArray(), charsCopy);
        if (shared) {
            char[] charsShared = new char[length];
            Arrays.fill(charsShared, Constants.SHARE_CHAR);
            assertEquals(charsShared, Arrays.copyOfRange(chars, offset, offset + length));
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
            assertEquals(CharReader.from(new NioInput()).readTo(dst), 1);
            assertEquals(dst[0], 66);
        }
    }

    private void testReadTo0(int dataSize) {
        char[] data = JieRandom.fill(new char[dataSize], 'A', 'Z');
        char[] dataPadding = new char[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(CharReader.from(new CharArrayReader(data)), data);
        }
        {
            // char array
            testReadTo0(CharReader.from(data), data);
            testReadTo0(CharReader.from(dataPadding, 33, data.length), data);
        }
        {
            // char sequence
            testReadTo0(CharReader.from(new String(data)), data);
            testReadTo0(CharReader.from(new String(dataPadding), 33, 33 + data.length), data);
        }
        {
            // char buffer
            testReadTo0(CharReader.from(CharBuffer.wrap(data)), data);
            CharBuffer direct = MaterialBox.copyDirect(data);
            direct.put(data);
            direct.flip();
            testReadTo0(CharReader.from(direct), data);
        }
    }

    private void testReadTo0(CharReader reader, char[] data) {
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
    public void testSpecial() throws Exception {
        char[] chars = JieArray.fill(new char[64], Constants.FILL_CHAR);
        CharArrayReader in = new CharArrayReader(chars);
        TestReader testIn = new TestReader(in);
        {
            // NIO tests
            CharReader reader = CharReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO, 10);
            CharSegment s0 = reader.read(chars.length);
            assertEquals(s0.data(), CharBuffer.wrap(chars));
            assertFalse(s0.end());
            s0 = reader.read(1);
            assertTrue(s0.end());
            assertEquals(reader.skip(66), 0);
            in.reset();
            CharReader reader2 = CharReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(reader2.skip(66), chars.length);
            // TestInputStream testIn2 = new TestInputStream(new CharArrayInputStream(new char[2]));
            // testIn2.setNextOperation(ReadOps.READ_ZERO);
            // assertEquals(CharReader.from(testIn2).skip(66), 2);
        }
        {
            // Exception tests
            CharReader reader = CharReader.from(testIn);
            in.reset();
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.read(66));
            expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
            // expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
            expectThrows(IllegalArgumentException.class, () -> reader.skip(-66));
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
            CharReader reader = CharReader.from(charsCopy);
            CharSegment segment = reader.read(charsCopy.length * 2);
            assertEquals(segment.data(), CharBuffer.wrap(charsCopy));
            assertTrue(segment.end());
            CharSegment segmentCopy = segment.clone();
            assertEquals(segmentCopy.data(), CharBuffer.wrap(charsCopy));
            assertTrue(segmentCopy.end());
            // fill the copy
            fillSharedData(segmentCopy.data());
            assertNotEquals(segmentCopy.data(), CharBuffer.wrap(charsCopy));
        }
        // {
        //     // test seg impl
        //     CharReader reader = CharReader.from(chars).withReadLimit(5);
        //     Method makeTure = reader.getClass().getDeclaredMethod("makeTrue", CharSegment.class);
        //     makeTure.setAccessible(true);
        //     TestSeg ts = new TestSeg();
        //     assertFalse(ts.end());
        //     CharSegment bs = (CharSegment) makeTure.invoke(reader, new TestSeg());
        //     assertNotSame(ts, bs);
        //     assertTrue(bs.end());
        // }
        {
            // special mark/reset
            TestReader tin = new TestReader(new CharArrayReader(new char[2]));
            CharReader reader = CharReader.from(tin);//.withReadLimit(1);
            assertTrue(reader.markSupported());
            tin.markSupported(false);
            assertFalse(reader.markSupported());
            reader.mark();
            reader.reset();
        }
    }

    private void fillSharedData(CharBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(Constants.SHARE_CHAR);
        }
    }

    private static final class TestSeg implements CharSegment {

        @Override
        public CharBuffer data() {
            return JieChars.emptyBuffer();
        }

        @Override
        public boolean end() {
            return false;
        }

        @Override
        public CharSegment clone() {
            return null;
        }
    }
}
