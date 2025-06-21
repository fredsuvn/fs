package test.io;

import org.testng.annotations.Test;
import test.Constants;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.ByteReaderX;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.MaterialBox;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ByteReaderXTest {

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
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(ByteReaderX.from(new ByteArrayInputStream(data)), ByteBuffer.wrap(data), false, false);
            testSkip0(ByteReaderX.from(new ByteArrayInputStream(data)), data);
        }
        {
            // byte array
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(ByteReaderX.from(data), ByteBuffer.wrap(data), true, true);
            testSkip0(ByteReaderX.from(data), data);
            byte[] dataPadding = new byte[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testRead0(
                ByteReaderX.from(dataPadding, 33, data.length),
                ByteBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
            testSkip0(
                ByteReaderX.from(dataPadding, 33, data.length),
                data
            );
        }
        {
            // heap buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(ByteReaderX.from(ByteBuffer.wrap(data)), ByteBuffer.wrap(data), true, true);
            testSkip0(ByteReaderX.from(ByteBuffer.wrap(data)), data);
        }
        {
            // direct buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteBuffer direct = MaterialBox.copyDirect(data);
            direct.mark();
            testRead0(ByteReaderX.from(direct), direct.slice(), true, true);
            direct.reset();
            testSkip0(ByteReaderX.from(direct), data);
        }
    }

    private void testRead0(ByteReaderX reader, ByteBuffer data, boolean preKnown, boolean shared) {
        reader.mark();
        data.mark();
        assertFalse(reader.read(0).end());
        assertEquals(reader.read(0).data().remaining(), 0);
        int dataLength = data.remaining();
        if (dataLength == 0) {
            assertFalse(reader.read(0).end());
            assertTrue(reader.read(1).end());
        }
        BytesBuilder newData = new BytesBuilder();
        if (dataLength > 0) {
            int length = 1;
            int startIndex = 0;
            int count = 0;
            while (true) {
                int endIndex = Math.min(dataLength, startIndex + length);
                int actualLen = Math.min(length, endIndex - startIndex);
                ByteSegment segment = reader.read(length);
                ByteBuffer readBuf = segment.data();
                assertEquals(readBuf.remaining(), actualLen);
                byte[] dataBuf = new byte[actualLen];
                data.get(dataBuf);
                assertEquals(
                    JieBuffer.read(readBuf),
                    dataBuf
                );
                if (shared) {
                    byte[] newBytes = JieRandom.fill(new byte[actualLen]);
                    readBuf.flip();
                    readBuf.put(newBytes);
                    newData.append(newBytes);
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
        ByteSegment segment = reader.read(dataLength == 0 ? 1 : dataLength * 2);
        assertTrue(segment.end());
        byte[] readBuf = JieBuffer.read(segment.data());
        byte[] dataBuf = JieBuffer.read(data);
        assertEquals(readBuf, dataBuf);
        if (shared) {
            assertEquals(readBuf, newData.toByteArray());
        }
        assertTrue(reader.read(1).end());
    }

    private void testSkip0(ByteReaderX reader, byte[] data) {
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
            assertEquals(ByteReaderX.from(new NioInput()).readTo(dst), 1);
            assertEquals(dst[0], 66);
        }
    }

    private void testReadTo0(int dataSize) {
        byte[] data = JieRandom.fill(new byte[dataSize]);
        byte[] dataPadding = new byte[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(ByteReaderX.from(new ByteArrayInputStream(data)), data);
        }
        {
            // byte array
            testReadTo0(ByteReaderX.from(data), data);
            testReadTo0(ByteReaderX.from(dataPadding, 33, data.length), data);
        }
        {
            // byte buffer
            testReadTo0(ByteReaderX.from(ByteBuffer.wrap(data)), data);
            ByteBuffer direct = ByteBuffer.allocateDirect(data.length);
            direct.put(data);
            direct.flip();
            testReadTo0(ByteReaderX.from(direct), data);
        }
    }

    private void testReadTo0(ByteReaderX reader, byte[] data) {
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
            // error
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> reader.readTo(new ErrorOutputStream()));
                reader.reset();
                expectThrows(IORuntimeException.class, () -> reader.readTo(new ErrorOutputStream(), 5));
                reader.reset();
            }
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
    public void testShare() {
        int dataSize = 1024;
        {
            // input stream
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testShare(ByteReaderX.from(new ByteArrayInputStream(data)), ByteBuffer.wrap(data), false, false);
        }
        {
            // byte array
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testShare(ByteReaderX.from(data), ByteBuffer.wrap(data), true, true);
            byte[] dataPadding = new byte[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShare(
                ByteReaderX.from(dataPadding, 33, data.length),
                ByteBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
        }
        {
            // heap buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testShare(ByteReaderX.from(ByteBuffer.wrap(data)), ByteBuffer.wrap(data), true, true);
        }
        {
            // direct buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteBuffer direct = MaterialBox.copyDirect(data);
            testShare(ByteReaderX.from(direct), direct.slice(), true, true);
        }
    }

    private void testShare(
        ByteReaderX reader, ByteBuffer data, boolean sharedReaderToData, boolean sharedDataToReader
    ) {
        ByteSegment segment = reader.read(data.remaining() * 2);
        ByteBuffer readBuf = segment.data();
        assertEquals(readBuf, data);
        assertTrue(segment.end());
        if (!readBuf.isReadOnly()) {
            for (int i = 0; i < readBuf.remaining(); i++) {
                readBuf.put(i, (byte) (readBuf.get(i) + 1));
            }
            assertEquals(readBuf.equals(data), sharedReaderToData);
        }
        if (!data.isReadOnly()) {
            for (int i = 0; i < data.remaining(); i++) {
                data.put(i, (byte) (data.get(i) + 100));
            }
            assertEquals(data.equals(readBuf), sharedDataToReader);
        }
    }

    @Test
    public void testSpecial() throws Exception {
        byte[] bytes = JieArray.fill(new byte[64], Constants.FILL_BYTE);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream testIn = new TestInputStream(in);
        {
            // NIO tests
            ByteReaderX reader = ByteReaderX.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO, 10);
            ByteSegment s0 = reader.read(bytes.length);
            assertEquals(s0.data(), ByteBuffer.wrap(bytes));
            assertFalse(s0.end());
            s0 = reader.read(1);
            assertTrue(s0.end());
            assertEquals(reader.skip(66), 0);
            in.reset();
            ByteReaderX reader2 = ByteReaderX.from(testIn);
            testIn.setNextOperation(ReadOps.READ_ZERO);
            assertEquals(reader2.skip(66), bytes.length);
            // TestInputStream testIn2 = new TestInputStream(new ByteArrayInputStream(new byte[2]));
            // testIn2.setNextOperation(ReadOps.READ_ZERO);
            // assertEquals(ByteReader.from(testIn2).skip(66), 2);
        }
        {
            // exception tests
            ByteReaderX reader = ByteReaderX.from(testIn);
            in.reset();
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.read(66));
            expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
            // expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
            expectThrows(IllegalArgumentException.class, () -> reader.skip(-66));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.skip(66));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.readTo(ByteBuffer.allocate(1)));
            testIn.setNextOperation(ReadOps.THROW);
            expectThrows(IORuntimeException.class, () -> reader.readTo(new byte[1]));
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
            ByteReaderX reader = ByteReaderX.from(bytesCopy);
            ByteSegment segment = reader.read(bytesCopy.length * 2);
            assertSame(segment.data().array(), bytesCopy);
            assertTrue(segment.end());
            ByteSegment segmentCopy = segment.clone();
            assertEquals(segmentCopy.data(), ByteBuffer.wrap(bytesCopy));
            assertTrue(segmentCopy.end());
            assertNotSame(segmentCopy.data().array(), bytesCopy);
        }
        {
            // special mark/reset
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[2]));
            ByteReaderX reader = ByteReaderX.from(tin);//.withReadLimit(1);
            assertTrue(reader.markSupported());
            tin.markSupported(false);
            assertFalse(reader.markSupported());
            reader.mark();
            reader.reset();
        }
        {
            // close
            ByteReaderX.from(JieIO.newInputStream(new byte[0])).close();
            ByteReaderX.from(new byte[0]).close();
            ByteReaderX.from(ByteBuffer.allocate(0)).close();
        }
    }
}
