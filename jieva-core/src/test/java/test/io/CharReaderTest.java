package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
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
    }

    private void testReader(int dataSize, int bufferSize) {
        {
            // input stream
            char[] data = JieRandom.fill(new char[dataSize]);
            CharReader reader = CharReader.from(new CharArrayReader(data));
            testRead0(reader, dataSize, false);
            testReader(reader, data, 0, dataSize, bufferSize, false);
            char[] limitedData = JieRandom.fill(new char[dataSize + 5]);
            CharReader limitedReader = CharReader.from(new CharArrayReader(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
        }
        {
            // char array
            char[] data = JieRandom.fill(new char[dataSize]);
            CharReader reader = CharReader.from(data);
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            char[] limitedData = JieRandom.fill(new char[dataSize + 5]);
            CharReader limitedReader = CharReader.from(limitedData).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
        }
        {
            // padded char array
            if (dataSize >= 3) {
                char[] data = JieRandom.fill(new char[dataSize]);
                CharReader reader = CharReader.from(data, 1, dataSize - 2);
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, bufferSize, true);
                char[] limitedData = JieRandom.fill(new char[dataSize + 5]);
                CharReader limitedReader = CharReader.from(limitedData, 1, dataSize - 2)
                    .withReadLimit(dataSize);
                testRead0(limitedReader, dataSize, true);
                testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, true);
            }
        }
        {
            // char buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            CharReader reader = CharReader.from(CharBuffer.wrap(data));
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            char[] limitedData = JieRandom.fill(new char[dataSize + 5]);
            CharReader limitedReader = CharReader.from(CharBuffer.wrap(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
        }
        {
            // char sequence
            char[] data = JieRandom.fill(new char[dataSize]);
            CharReader reader = CharReader.from(new String(data));
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, false);
            char[] limitedData = JieRandom.fill(new char[dataSize + 5]);
            CharReader limitedReader = CharReader.from(new String(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
        }
        {
            // padded char sequence
            if (dataSize >= 3) {
                char[] data = JieRandom.fill(new char[dataSize]);
                CharReader reader = CharReader.from(new String(data), 1, dataSize - 1);
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, bufferSize, false);
                char[] limitedData = JieRandom.fill(new char[dataSize + 5]);
                CharReader limitedReader = CharReader.from(new String(limitedData), 1, dataSize - 1)
                    .withReadLimit(dataSize);
                testRead0(limitedReader, dataSize, true);
                testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, false);
            }
        }
    }

    private void testReader(CharReader reader, char[] chars, int offset, int length, int size, boolean shared) {
        char[] charsCopy = Arrays.copyOfRange(chars, offset, offset + length);
        CharsBuilder builder = new CharsBuilder();
        while (true) {
            CharSegment segment = reader.read(size);
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
            Arrays.fill(charsShared, (char) 6);
            assertEquals(charsShared, Arrays.copyOfRange(chars, offset, offset + length));
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
    }

    @Test
    public void testExpReader() throws IOException {
        char[] chars = JieRandom.fill(new char[64]);
        CharArrayReader in = new CharArrayReader(chars);
        TestReader testIn = new TestReader(in);
        {
            // NIO tests
            CharReader reader = CharReader.from(testIn);
            testIn.setNextReadOption(ReadOps.READ_ZERO);
            CharSegment s0 = reader.read(chars.length, true);
            assertEquals(s0.data(), JieChars.emptyBuffer());
            assertTrue(s0.end());
            in.reset();
            CharReader reader2 = CharReader.from(testIn);
            testIn.setNextReadOption(ReadOps.READ_ZERO);
            CharSegment s2 = reader2.read(chars.length);
            assertEquals(s2.data(), CharBuffer.wrap(chars));
            assertFalse(s2.end());
        }
        {
            // Exception tests
            CharReader reader = CharReader.from(testIn);
            in.reset();
            testIn.setNextReadOption(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.read(66));
            expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
            expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
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
    }

    private void fillBuffer(CharBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put((char) 6);
        }
    }
}
