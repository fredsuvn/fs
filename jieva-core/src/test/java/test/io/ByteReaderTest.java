package test.io;

import org.testng.annotations.Test;
import test.Constants;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
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
            byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
            ByteReader reader = ByteReader.from(new ByteArrayInputStream(data));
            testRead0(reader, dataSize, false);
            testReader(reader, data, 0, dataSize, bufferSize, false);
            testSkip(
                ByteReader.from(new ByteArrayInputStream(data)),
                data, 0, dataSize, bufferSize, false
            );
            byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
            ByteReader limitedReader = ByteReader.from(new ByteArrayInputStream(limitedData)).withReadLimit(dataSize);
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
            testSkip(
                ByteReader.from(new ByteArrayInputStream(limitedData)).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, false
            );
        }
        {
            // byte array
            byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
            ByteReader reader = ByteReader.from(data);
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            testSkip(
                ByteReader.from(data),
                data, 0, dataSize, bufferSize, true
            );
            byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
            ByteReader limitedReader = ByteReader.from(limitedData).withReadLimit(dataSize);
            limitedReader.close();
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
            testSkip(
                ByteReader.from(limitedData).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, true
            );
        }
        {
            // padded byte array
            if (dataSize >= 3) {
                byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
                ByteReader reader = ByteReader.from(data, 1, dataSize - 2);
                reader.close();
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, bufferSize, true);
                testSkip(
                    ByteReader.from(data, 1, dataSize - 2),
                    data, 1, dataSize - 2, bufferSize, true
                );
                byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
                ByteReader limitedReader = ByteReader.from(limitedData, 1, dataSize - 2)
                    .withReadLimit(dataSize);
                limitedReader.close();
                testRead0(limitedReader, dataSize, true);
                testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, true);
                testSkip(
                    ByteReader.from(limitedData, 1, dataSize - 2),
                    limitedData, 1, dataSize - 2, bufferSize, true
                );
            }
        }
        {
            // byte buffer
            byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
            ByteReader reader = ByteReader.from(ByteBuffer.wrap(data));
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, bufferSize, true);
            testSkip(
                ByteReader.from(ByteBuffer.wrap(data)),
                data, 0, dataSize, bufferSize, true
            );
            byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
            ByteReader limitedReader = ByteReader.from(ByteBuffer.wrap(limitedData)).withReadLimit(dataSize);
            limitedReader.close();
            testRead0(limitedReader, dataSize, true);
            testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
            testSkip(
                ByteReader.from(ByteBuffer.wrap(limitedData)).withReadLimit(dataSize),
                limitedData, 0, dataSize, bufferSize, true
            );
        }
    }

    private void testReader(ByteReader reader, byte[] bytes, int offset, int length, int readSize, boolean shared) {
        if (reader.markSupported()) {
            reader.mark();
            assertEquals(reader.skip(length + 100), length);
            reader.reset();
            reader.mark();
        }
        testReader0(reader, bytes, offset, length, readSize, shared);
        if (reader.markSupported()) {
            reader.reset();
            testReader0(reader, bytes, offset, length, readSize, shared);
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
        assertEquals(reader.skip(0), 0);
    }

    private void testSkip(ByteReader reader, byte[] bytes, int offset, int length, int readSize, boolean shared) {
        int skipNum = length / 2;
        assertEquals(reader.skip(skipNum), skipNum);
        int remaining = length - skipNum;
        testReader0(reader, bytes, offset + skipNum, remaining, readSize, shared);
    }

    private void testReader0(ByteReader reader, byte[] bytes, int offset, int length, int readSize, boolean shared) {
        byte[] bytesCopy = Arrays.copyOfRange(bytes, offset, offset + length);
        BytesBuilder builder = new BytesBuilder();
        while (true) {
            ByteSegment segment = reader.read(readSize);
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
            Arrays.fill(bytesShared, Constants.SHARE_BYTE);
            assertEquals(bytesShared, Arrays.copyOfRange(bytes, offset, offset + length));
        }
    }

    @Test
    public void testSpecial() throws Exception {
        byte[] bytes = JieArray.fill(new byte[64], Constants.FILL_BYTE);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream testIn = new TestInputStream(in);
        {
            // NIO tests
            ByteReader reader = ByteReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            ByteSegment s0 = reader.read(bytes.length, true);
            assertEquals(s0.data(), JieBytes.emptyBuffer());
            assertTrue(s0.end());
            in.reset();
            ByteReader reader2 = ByteReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            ByteSegment s2 = reader2.read(bytes.length);
            assertEquals(s2.data(), ByteBuffer.wrap(bytes));
            assertFalse(s2.end());
            testIn.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(reader.skip(66, true), 0);
            TestInputStream testIn2 = new TestInputStream(new ByteArrayInputStream(new byte[2]));
            testIn2.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(ByteReader.from(testIn2).skip(66), 2);
        }
        {
            // exception tests
            ByteReader reader = ByteReader.from(testIn);
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
        {
            // test seg impl
            ByteReader reader = ByteReader.from(bytes).withReadLimit(5);
            Method makeTure = reader.getClass().getDeclaredMethod("makeTrue", ByteSegment.class);
            makeTure.setAccessible(true);
            TestSeg ts = new TestSeg();
            assertFalse(ts.end());
            ByteSegment bs = (ByteSegment) makeTure.invoke(reader, new TestSeg());
            assertNotSame(ts, bs);
            assertTrue(bs.end());
        }
        {
            // special mark/reset
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[2]));
            ByteReader reader = ByteReader.from(tin).withReadLimit(1);
            assertTrue(reader.markSupported());
            tin.markSupported(false);
            assertFalse(reader.markSupported());
            reader.mark();
            reader.reset();
        }
    }

    private void fillBuffer(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(Constants.SHARE_BYTE);
        }
    }

    private static final class TestSeg implements ByteSegment {

        @Override
        public ByteBuffer data() {
            return JieBytes.emptyBuffer();
        }

        @Override
        public boolean end() {
            return false;
        }

        @Override
        public ByteSegment clone() {
            return null;
        }
    }
}
