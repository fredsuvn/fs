package test.io;

import org.testng.annotations.Test;
import test.Constants;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

        // test toByteArray()
        byte[] data = JieRandom.fill(new byte[1024]);
        assertEquals(ByteReader.from(data).read(99999).toByteArray(), data);
    }

    private void testReader(int dataSize, int readSize) {
        {
            // input stream
            byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
            ByteReader reader = ByteReader.from(new ByteArrayInputStream(data));
            testRead0(reader, dataSize, false);
            testReader(reader, data, 0, dataSize, readSize, false);
            testSkip(
                ByteReader.from(new ByteArrayInputStream(data)),
                data, 0, dataSize, readSize, false
            );
            // byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
            // ByteReader limitedReader = ByteReader.from(new ByteArrayInputStream(limitedData)).withReadLimit(dataSize);
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, bufferSize, false);
            // testSkip(
            //     ByteReader.from(new ByteArrayInputStream(limitedData)).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, bufferSize, false
            // );
        }
        {
            // byte array
            byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
            ByteReader reader = ByteReader.from(data);
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, readSize, true);
            testSkip(
                ByteReader.from(data),
                data, 0, dataSize, readSize, true
            );
            // byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
            // ByteReader limitedReader = ByteReader.from(limitedData).withReadLimit(dataSize);
            // limitedReader.close();
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
            // testSkip(
            //     ByteReader.from(limitedData).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, bufferSize, true
            // );
        }
        {
            // padded byte array
            if (dataSize >= 3) {
                byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
                ByteReader reader = ByteReader.from(data, 1, dataSize - 2);
                reader.close();
                testRead0(reader, dataSize - 2, true);
                testReader(reader, data, 1, dataSize - 2, readSize, true);
                testSkip(
                    ByteReader.from(data, 1, dataSize - 2),
                    data, 1, dataSize - 2, readSize, true
                );
                // byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
                // ByteReader limitedReader = ByteReader.from(limitedData, 1, dataSize - 2)
                //     .withReadLimit(dataSize);
                // limitedReader.close();
                // testRead0(limitedReader, dataSize, true);
                // testReader(limitedReader, limitedData, 1, dataSize - 2, bufferSize, true);
                // testSkip(
                //     ByteReader.from(limitedData, 1, dataSize - 2),
                //     limitedData, 1, dataSize - 2, bufferSize, true
                // );
            }
        }
        {
            // byte buffer
            byte[] data = JieArray.fill(new byte[dataSize], Constants.FILL_BYTE);
            ByteReader reader = ByteReader.from(ByteBuffer.wrap(data));
            reader.close();
            testRead0(reader, dataSize, true);
            testReader(reader, data, 0, dataSize, readSize, true);
            testSkip(
                ByteReader.from(ByteBuffer.wrap(data)),
                data, 0, dataSize, readSize, true
            );
            // byte[] limitedData = JieArray.fill(new byte[dataSize + 5], Constants.FILL_BYTE);
            // ByteReader limitedReader = ByteReader.from(ByteBuffer.wrap(limitedData)).withReadLimit(dataSize);
            // limitedReader.close();
            // testRead0(limitedReader, dataSize, true);
            // testReader(limitedReader, limitedData, 0, dataSize, bufferSize, true);
            // testSkip(
            //     ByteReader.from(ByteBuffer.wrap(limitedData)).withReadLimit(dataSize),
            //     limitedData, 0, dataSize, bufferSize, true
            // );
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
                fillSharedData(buffer);
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

    private void testRead0(int dataSize) {
        byte[] data = JieRandom.fill(new byte[dataSize]);
        byte[] dataPadding = new byte[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testRead0(ByteReader.from(new ByteArrayInputStream(data)), data, false, true);
            testRead0(ByteReader.from(new ByteArrayInputStream(data)), data, false, false);
        }
        {
            // byte array
            testRead0(ByteReader.from(data), data, true, true);
            testRead0(ByteReader.from(dataPadding, 33, data.length), data, true, true);
        }
        {
            // byte buffer
            testRead0(ByteReader.from(ByteBuffer.wrap(data)), data, true, true);
            ByteBuffer direct = ByteBuffer.allocateDirect(data.length);
            direct.put(data);
            direct.flip();
            testRead0(ByteReader.from(direct), data, true, true);
        }
    }

    private void testRead0(ByteReader reader, byte[] data, boolean preKnown, boolean shared) {
        if (reader.markSupported()) {
            reader.mark();
        }
        assertFalse(reader.read(0).end());
        assertEquals(reader.read(0).data().remaining(), 0);
        // to output stream
        int length = 1;
        int startIndex = 0;
        int count = 0;
        if (data.length == 0) {
            assertFalse(reader.read(0).end());
            assertTrue(reader.read(1).end());
        }
        if (data.length > 0) {
            while (true) {
                int endIndex = Math.min(data.length, startIndex + length);
                int actualLen = Math.min(length, endIndex - startIndex);
                ByteSegment segment = reader.read(length);
                ByteBuffer buffer = segment.data();
                assertEquals(JieBuffer.read(buffer), Arrays.copyOfRange(data, startIndex, endIndex));
                buffer.flip();
                fillSharedData(buffer);
                if (length > actualLen) {
                    assertTrue(segment.end());
                }
                if (length < actualLen) {
                    assertFalse(segment.end());
                }
                if (length == actualLen) {
                    assertEquals(segment.end(), preKnown);
                }
                count += actualLen;
                if (segment.end()) {
                    break;
                }
                length *= 2;
                startIndex = endIndex;
            }
            assertEquals(count, data.length);
            assertTrue(reader.read(1).end());
        }
        if (reader.markSupported()) {
            reader.reset();
            ByteSegment segment = reader.read(Integer.MAX_VALUE);
            assertTrue(segment.end());
            assertEquals(JieBuffer.read(segment.data()), data);
            if (shared) {
                assertEquals(data, fillSharedData(data.length));
            }
            assertTrue(reader.read(1).end());
        }
    }

    private void testSkip0(ByteReader reader, byte[] data, boolean preKnown, boolean shared) {
        if (reader.markSupported()) {
            reader.mark();
        }
        assertEquals(reader.skip(0), 0);
        // to output stream
        int length = 1;
        int startIndex = 0;
        int count = 0;
        if (data.length == 0) {
            assertEquals(reader.skip(0), 0);
            assertEquals(reader.skip(1), 0);
        }
        if (data.length > 0) {
            while (true) {
                int endIndex = Math.min(data.length, startIndex + length);
                int actualLen = Math.min(length, endIndex - startIndex);
                long actualSkipped = reader.skip(length);
                assertEquals(actualSkipped, actualLen);
                count += actualLen;
                if (length > actualLen) {
                    break;
                }
                length *= 2;
                startIndex = endIndex;
            }
            assertEquals(count, data.length);
        }
        if (reader.markSupported()) {
            reader.reset();
            ByteSegment segment = reader.read(Integer.MAX_VALUE);
            assertTrue(segment.end());
            assertEquals(JieBuffer.read(segment.data()), data);
            if (shared) {
                assertEquals(data, fillSharedData(data.length));
            }
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
        testReadTo0(4);

        {
            // special: nio
            class NioInput extends InputStream {

                private int count = 1;

                @Override
                public int read() throws IOException {
                    if (count > 0) {
                        count--;
                        return 66;
                    }
                    return -1;
                }
            }
            byte[] dst = new byte[2];
            assertEquals(ByteReader.from(new NioInput()).readTo(dst), 1);
            assertEquals(dst[0], 66);
        }
    }

    private void testReadTo0(int dataSize) {
        byte[] data = JieRandom.fill(new byte[dataSize]);
        byte[] dataPadding = new byte[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(ByteReader.from(new ByteArrayInputStream(data)), data);
        }
        {
            // byte array
            testReadTo0(ByteReader.from(data), data);
            testReadTo0(ByteReader.from(dataPadding, 33, data.length), data);
        }
        {
            // byte buffer
            testReadTo0(ByteReader.from(ByteBuffer.wrap(data)), data);
            ByteBuffer direct = ByteBuffer.allocateDirect(data.length);
            direct.put(data);
            direct.flip();
            testReadTo0(ByteReader.from(direct), data);
        }
    }

    private void testReadTo0(ByteReader reader, byte[] data) {
        reader.mark();
        {
            // to output stream
            BytesBuilder out = new BytesBuilder();
            assertEquals(reader.readTo(out), data.length == 0 ? -1 : data.length);
            assertEquals(out.toByteArray(), data);
            assertEquals(reader.readTo(out), -1);
            assertEquals(out.toByteArray(), data);
            assertEquals(reader.readTo(out, 0), 0);
            assertEquals(out.toByteArray(), data);
            reader.reset();
            out.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    assertEquals(reader.readTo(out, length), Math.min(length, endIndex - startIndex));
                    assertEquals(out.toByteArray(), Arrays.copyOfRange(data, startIndex, endIndex));
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
            // to byte array
            byte[] dst = new byte[data.length];
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(dst, data);
            assertEquals(reader.readTo(dst), dst.length == 0 ? 0 : -1);
            assertEquals(dst, data);
            reader.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = new byte[data.length];
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
            ByteBuffer dst = ByteBuffer.allocate(data.length);
            assertEquals(reader.readTo(dst), data.length);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            dst = ByteBuffer.allocate(data.length);
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(reader.readTo(dst), dst.remaining() == 0 ? 0 : -1);
            assertEquals(reader.readTo(ByteBuffer.allocate(1)), -1);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = ByteBuffer.allocate(data.length);
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    int actualLen = Math.min(length, endIndex - startIndex);
                    ByteBuffer slice = JieBuffer.slice(dst, actualLen);
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
            ByteBuffer dst = ByteBuffer.allocateDirect(data.length);
            assertEquals(reader.readTo(dst), data.length);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            dst = ByteBuffer.allocateDirect(data.length);
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(reader.readTo(dst), dst.remaining() == 0 ? 0 : -1);
            assertEquals(reader.readTo(ByteBuffer.allocateDirect(1)), -1);
            dst.flip();
            assertEquals(JieBuffer.read(dst), data);
            reader.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = ByteBuffer.allocateDirect(data.length);
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    int actualLen = Math.min(length, endIndex - startIndex);
                    ByteBuffer slice = JieBuffer.slice(dst, actualLen);
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
        byte[] bytes = JieArray.fill(new byte[64], Constants.FILL_BYTE);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream testIn = new TestInputStream(in);
        {
            // NIO tests
            ByteReader reader = ByteReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO, 10);
            ByteSegment s0 = reader.read(bytes.length);
            assertEquals(s0.data(), ByteBuffer.wrap(bytes));
            assertFalse(s0.end());
            s0 = reader.read(1);
            assertTrue(s0.end());
            assertEquals(reader.skip(66), 0);
            in.reset();
            ByteReader reader2 = ByteReader.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(reader2.skip(66), bytes.length);
            // TestInputStream testIn2 = new TestInputStream(new ByteArrayInputStream(new byte[2]));
            // testIn2.setNextOperation(ReadOps.READ_ZERO);
            // assertEquals(ByteReader.from(testIn2).skip(66), 2);
        }
        {
            // exception tests
            ByteReader reader = ByteReader.from(testIn);
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
            byte[] bytesCopy = Arrays.copyOf(bytes, bytes.length);
            ByteReader reader = ByteReader.from(bytesCopy);
            ByteSegment segment = reader.read(bytesCopy.length * 2);
            assertEquals(segment.data(), ByteBuffer.wrap(bytesCopy));
            assertTrue(segment.end());
            ByteSegment segmentCopy = segment.clone();
            assertEquals(segmentCopy.data(), ByteBuffer.wrap(bytesCopy));
            assertTrue(segmentCopy.end());
            // fill the copy
            fillSharedData(segmentCopy.data());
            assertNotEquals(segmentCopy.data(), ByteBuffer.wrap(bytesCopy));
        }
        // {
        //     // test seg impl
        //     ByteReader reader = ByteReader.from(bytes);//.withReadLimit(5);
        //     Method makeTure = reader.getClass().getDeclaredMethod("makeTrue", ByteSegment.class);
        //     makeTure.setAccessible(true);
        //     TestSeg ts = new TestSeg();
        //     assertFalse(ts.end());
        //     ByteSegment bs = (ByteSegment) makeTure.invoke(reader, new TestSeg());
        //     assertNotSame(ts, bs);
        //     assertTrue(bs.end());
        // }
        {
            // special mark/reset
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[2]));
            ByteReader reader = ByteReader.from(tin);//.withReadLimit(1);
            assertTrue(reader.markSupported());
            tin.markSupported(false);
            assertFalse(reader.markSupported());
            reader.mark();
            reader.reset();
        }
    }

    private void fillSharedData(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(Constants.SHARE_BYTE);
        }
    }

    private byte[] fillSharedData(int size) {
        byte[] bytes = new byte[size];
        Arrays.fill(bytes, Constants.SHARE_BYTE);
        return bytes;
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
