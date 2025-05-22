package test.io;

import org.testng.annotations.Test;
import test.Constants;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
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
    }

    private void testReader(int dataSize, int bufferSize) {
        {
            // reader
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(new CharArrayReader(data));
            testRead0(reader, dataSize, false);
            testReader(reader, data, 0, dataSize, bufferSize, false);
            testSkip(
                CharReader.from(new CharArrayReader(data)),
                data, 0, dataSize, bufferSize, false
            );
            char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            CharReader limitedReader = CharReader.from(new CharArrayReader(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
            testSkip(
                CharReader.from(new CharArrayReader(limitedData)).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, false
            );
        }
        {
            // char array
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(data);
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            testSkip(
                CharReader.from(data),
                data, 0, dataSize, bufferSize, true
            );
            char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            CharReader limitedReader = CharReader.from(limitedData).withReadLimit(dataSize);
            limitedReader.close();
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
            testSkip(
                CharReader.from(limitedData).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, true
            );
        }
        {
            // padded char array
            if (dataSize >= 3) {
                char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
                CharReader reader = CharReader.from(data, 1, dataSize - 2);
                reader.close();
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, bufferSize, true);
                testSkip(
                    CharReader.from(data, 1, dataSize - 2),
                    data, 1, dataSize - 2, bufferSize, true
                );
                char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
                CharReader limitedReader = CharReader.from(limitedData, 1, dataSize - 2)
                    .withReadLimit(dataSize);
                limitedReader.close();
                testRead0(limitedReader, dataSize, true);
                testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, true);
                testSkip(
                    CharReader.from(limitedData, 1, dataSize - 2),
                    limitedData, 1, dataSize - 2, bufferSize, true
                );
            }
        }
        {
            // char buffer
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(CharBuffer.wrap(data));
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            testSkip(
                CharReader.from(CharBuffer.wrap(data)),
                data, 0, dataSize, bufferSize, true
            );
            char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            CharReader limitedReader = CharReader.from(CharBuffer.wrap(limitedData)).withReadLimit(dataSize);
            limitedReader.close();
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
            testSkip(
                CharReader.from(CharBuffer.wrap(limitedData)).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, true
            );
        }
        {
            // char sequence
            char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
            CharReader reader = CharReader.from(new String(data));
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, false);
            testSkip(
                CharReader.from(new String(data)),
                data, 0, dataSize, bufferSize, false
            );
            char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
            CharReader limitedReader = CharReader.from(new String(limitedData)).withReadLimit(dataSize);
            limitedReader.close();
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
            testSkip(
                CharReader.from(new String(limitedData)).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, false
            );
        }
        {
            // padded char sequence
            if (dataSize >= 3) {
                char[] data = JieArray.fill(new char[dataSize], Constants.FILL_CHAR);
                CharReader reader = CharReader.from(new String(data), 1, dataSize - 1);
                reader.close();
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, bufferSize, false);
                testSkip(
                    CharReader.from(new String(data), 1, dataSize - 1),
                    data, 1, dataSize - 2, bufferSize, false
                );
                char[] limitedData = JieArray.fill(new char[dataSize + 5], Constants.FILL_CHAR);
                CharReader limitedReader = CharReader.from(new String(limitedData), 1, dataSize - 1)
                    .withReadLimit(dataSize);
                limitedReader.close();
                testRead0(limitedReader, dataSize, true);
                testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, false);
                testSkip(
                    CharReader.from(new String(limitedData), 1, dataSize - 1),
                    limitedData, 1, dataSize - 2, bufferSize, false
                );
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
                fillBuffer(buffer);
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
    public void testSpecial() throws Exception {
        char[] chars = JieArray.fill(new char[64], Constants.FILL_CHAR);
        CharArrayReader in = new CharArrayReader(chars);
        TestReader testIn = new TestReader(in);
        {
            // NIO tests
            CharReader reader = CharReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            CharSegment s0 = reader.read(chars.length, true);
            assertEquals(s0.data(), JieChars.emptyBuffer());
            assertTrue(s0.end());
            in.reset();
            CharReader reader2 = CharReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            CharSegment s2 = reader2.read(chars.length);
            assertEquals(s2.data(), CharBuffer.wrap(chars));
            assertFalse(s2.end());
            testIn.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(reader.skip(66, true), 0);
            TestReader testIn2 = new TestReader(new CharArrayReader(new char[2]));
            testIn2.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(CharReader.from(testIn2).skip(66), 2);
        }
        {
            // Exception tests
            CharReader reader = CharReader.from(testIn);
            in.reset();
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.read(66));
            expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
            expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
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
            fillBuffer(segmentCopy.data());
            assertNotEquals(segmentCopy.data(), CharBuffer.wrap(charsCopy));
        }
        {
            // test seg impl
            CharReader reader = CharReader.from(chars).withReadLimit(5);
            Method makeTure = reader.getClass().getDeclaredMethod("makeTrue", CharSegment.class);
            makeTure.setAccessible(true);
            TestSeg ts = new TestSeg();
            assertFalse(ts.end());
            CharSegment bs = (CharSegment) makeTure.invoke(reader, new TestSeg());
            assertNotSame(ts, bs);
            assertTrue(bs.end());
        }
        {
            // special mark/reset
            TestReader tin = new TestReader(new CharArrayReader(new char[2]));
            CharReader reader = CharReader.from(tin).withReadLimit(1);
            assertTrue(reader.markSupported());
            tin.markSupported(false);
            assertFalse(reader.markSupported());
            reader.mark();
            reader.reset();
        }
    }

    private void fillBuffer(CharBuffer buffer) {
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
