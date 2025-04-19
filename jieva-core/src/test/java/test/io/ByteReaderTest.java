package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ByteReaderTest {

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
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteReader reader = ByteReader.from(new ByteArrayInputStream(data));
            testRead0(reader, dataSize, false);
            testReader(reader, data, 0, dataSize, bufferSize, false);
            byte[] limitedData = JieRandom.fill(new byte[dataSize + 5]);
            ByteReader limitedReader = ByteReader.from(new ByteArrayInputStream(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
        }
        {
            // byte array
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteReader reader = ByteReader.from(data);
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            byte[] limitedData = JieRandom.fill(new byte[dataSize + 5]);
            ByteReader limitedReader = ByteReader.from(limitedData).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
        }
        {
            // padded byte array
            if (dataSize >= 3) {
                byte[] data = JieRandom.fill(new byte[dataSize]);
                ByteReader reader = ByteReader.from(data, 1, dataSize - 2);
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, bufferSize, true);
                byte[] limitedData = JieRandom.fill(new byte[dataSize + 5]);
                ByteReader limitedReader = ByteReader.from(limitedData, 1, dataSize - 2)
                    .withReadLimit(dataSize);
                testRead0(limitedReader, dataSize, true);
                testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, true);
            }
        }
        {
            // byte buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteReader reader = ByteReader.from(ByteBuffer.wrap(data));
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            byte[] limitedData = JieRandom.fill(new byte[dataSize + 5]);
            ByteReader limitedReader = ByteReader.from(ByteBuffer.wrap(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
        }
    }

    private void testReader(ByteReader reader, byte[] bytes, int offset, int length, int size, boolean shared) {
        byte[] bytesCopy = Arrays.copyOfRange(bytes, offset, offset + length);
        BytesBuilder builder = new BytesBuilder();
        while (true) {
            ByteSegment segment = reader.read(size);
            ByteBuffer buffer = segment.data();
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
        assertEquals(builder.toByteArray(), bytesCopy);
        if (shared) {
            byte[] bytesShared = new byte[length];
            Arrays.fill(bytesShared, (byte) 6);
            assertEquals(bytesShared, Arrays.copyOfRange(bytes, offset, offset + length));
        }
    }

    private void testRead0(ByteReader reader, int dataSize, boolean preKnown) {
        ByteSegment s0 = reader.read(0);
        assertEquals(s0.data(), JieBytes.emptyBuffer());
        if (preKnown) {
            assertEquals(s0.end(), dataSize == 0);
        } else {
            assertFalse(s0.end());
        }
    }

    @Test
    public void testExpReader() {
        byte[] bytes = JieRandom.fill(new byte[64]);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream testIn = new TestInputStream(in);
        {
            // NIO tests
            ByteReader reader = ByteReader.from(testIn);
            testIn.setNextReadOption(ReadOps.READ_ZERO);
            ByteSegment s0 = reader.read(bytes.length, true);
            assertEquals(s0.data(), JieBytes.emptyBuffer());
            assertTrue(s0.end());
            in.reset();
            ByteReader reader2 = ByteReader.from(testIn);
            testIn.setNextReadOption(ReadOps.READ_ZERO);
            ByteSegment s2 = reader2.read(bytes.length);
            assertEquals(s2.data(), ByteBuffer.wrap(bytes));
            assertFalse(s2.end());
        }
        {
            // Exception tests
            ByteReader reader = ByteReader.from(testIn);
            in.reset();
            testIn.setNextReadOption(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.read(66));
            expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
            expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
        }
        {
            // for segment
            byte[] bytesCopy = Arrays.copyOf(bytes, bytes.length);
            ByteReader reader = ByteReader.from(bytesCopy);
            ByteSegment segment = reader.read(bytesCopy.length * 2);
            assertEquals(segment.data(), ByteBuffer.wrap(bytesCopy));
            assertTrue(segment.end());
            ByteSegment segmentCopy = segment.clone();
            assertEquals(segmentCopy.data(), ByteBuffer.wrap(bytesCopy));
            assertTrue(segmentCopy.end());
            // fill the copy
            fillBuffer(segmentCopy.data());
            assertNotEquals(segmentCopy.data(), ByteBuffer.wrap(bytesCopy));
        }
    }

    private void fillBuffer(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put((byte) 6);
        }
    }
}
