package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ByteReaderTest {

    private static final int DST_SIZE = 256;

    @Test
    public void testRead() {
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
            TestInputStream tr = new TestInputStream(new ByteArrayInputStream(new byte[10]));
            tr.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> ByteReader.from(tr).skip(100));
            expectThrows(IORuntimeException.class, () -> ByteReader.from(Channels.newChannel(tr)).skip(100));
        }
    }

    private void testRead0(int dataSize, int readSize) {
        {
            // input stream
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(ByteReader.from(new ByteArrayInputStream(data)), data, readSize, false);
            testSkip0(ByteReader.from(new ByteArrayInputStream(data)), data, readSize);
            TestInputStream tr = new TestInputStream(new ByteArrayInputStream(data));
            tr.setNextOperation(ReadOps.READ_ZERO);
            testSkip0(ByteReader.from(tr), data, readSize);
        }
        {
            // channel
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(
                ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))),
                data, readSize, false
            );
            testSkip0(ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))), data, readSize);
        }
        {
            // byte array
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(ByteReader.from(data), data, readSize, true);
            testSkip0(ByteReader.from(data), data, readSize);
            byte[] dataPadding = new byte[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testRead0(
                ByteReader.from(dataPadding, 33, data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkip0(ByteReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testRead0(ByteReader.from(ByteBuffer.wrap(data)), data, readSize, true);
            testSkip0(ByteReader.from(ByteBuffer.wrap(data)), data, readSize);
        }
    }

    private void testRead0(ByteReader reader, byte[] data, int readSize, boolean preKnown) {
        assertFalse(reader.read(0).end());
        assertFalse(reader.read(0).data().hasRemaining());
        int hasRead = 0;
        while (hasRead < data.length) {
            ByteSegment segment = reader.read(readSize);
            int actualLen = Math.min(readSize, data.length - hasRead);
            assertEquals(
                JieBuffer.copyContent(segment.data()),
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

    private void testSkip0(ByteReader reader, byte[] data, int readSize) {
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

        {
            // read to channel error
            ByteReader reader = ByteReader.from(new byte[128]);
            WritableByteChannel errCh = Channels.newChannel(new ErrorOutputStream());
            expectThrows(IORuntimeException.class, () -> reader.readTo(errCh));
            expectThrows(IORuntimeException.class, () -> reader.readTo(errCh, 1));
        }
    }

    private void testReadTo0(int dataSize, int readSize) {
        byte[] data = JieRandom.fill(new byte[dataSize]);
        byte[] dataPadding = new byte[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(() -> ByteReader.from(new ByteArrayInputStream(data)), data, readSize);
        }
        {
            // channel
            testReadTo0(() -> ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))), data, readSize);
        }
        {
            // byte array
            testReadTo0(() -> ByteReader.from(data), data, readSize);
            testReadTo0(() -> ByteReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // byte buffer
            testReadTo0(() -> ByteReader.from(ByteBuffer.wrap(data)), data, readSize);
            testReadTo0(() -> ByteReader.from(JieBuffer.directBuffer(data)), data, readSize);
        }
    }

    private void testReadTo0(Supplier<ByteReader> supplier, byte[] data, int readSize) {
        {
            // to output stream
            BytesBuilder builder = new BytesBuilder();
            if (data.length == 0) {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(builder), -1);
                assertEquals(reader.readTo(builder, 0), 0);
                reader.close();
                assertEquals(reader.readTo(builder, 0), 0);
            } else {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(builder, 0), 0);
                if (reader.markSupported()) {
                    reader.mark();
                }
                long hasRead = reader.readTo(builder, readSize);
                assertEquals(hasRead, Math.min(readSize, data.length));
                long restLen = data.length - hasRead;
                assertEquals(reader.readTo(builder), restLen == 0 ? -1 : restLen);
                assertEquals(builder.toByteArray(), data);
                if (reader.markSupported()) {
                    reader.reset();
                    builder.reset();
                    hasRead = reader.readTo(builder, readSize);
                    assertEquals(hasRead, Math.min(readSize, data.length));
                    restLen = data.length - hasRead;
                    assertEquals(reader.readTo(builder), restLen == 0 ? -1 : restLen);
                    assertEquals(builder.toByteArray(), data);
                }
                assertEquals(reader.readTo(builder), -1);
                assertEquals(reader.readTo(builder, 66), -1);
                reader.close();
                assertEquals(reader.readTo(builder, 0), 0);
            }
            // error
            expectThrows(IllegalArgumentException.class, () -> supplier.get().readTo(builder, -1));
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorOutputStream()));
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorOutputStream(), 1));
            }
        }
        {
            // to channel
            BytesBuilder builder = new BytesBuilder();
            WritableByteChannel channel = Channels.newChannel(builder);
            if (data.length == 0) {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(channel), -1);
                assertEquals(reader.readTo(channel, 0), 0);
                reader.close();
                assertEquals(reader.readTo(channel, 0), 0);
            } else {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(channel, 0), 0);
                if (reader.markSupported()) {
                    reader.mark();
                }
                long hasRead = reader.readTo(channel, readSize);
                assertEquals(hasRead, Math.min(readSize, data.length));
                long restLen = data.length - hasRead;
                assertEquals(reader.readTo(channel), restLen == 0 ? -1 : restLen);
                assertEquals(builder.toByteArray(), data);
                if (reader.markSupported()) {
                    reader.reset();
                    builder.reset();
                    hasRead = reader.readTo(channel, readSize);
                    assertEquals(hasRead, Math.min(readSize, data.length));
                    restLen = data.length - hasRead;
                    assertEquals(reader.readTo(channel), restLen == 0 ? -1 : restLen);
                    assertEquals(builder.toByteArray(), data);
                }
                assertEquals(reader.readTo(channel), -1);
                assertEquals(reader.readTo(channel, 66), -1);
                reader.close();
                assertEquals(reader.readTo(channel, 0), 0);
            }
            // error
            expectThrows(IllegalArgumentException.class, () -> supplier.get().readTo(builder, -1));
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorOutputStream()));
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorOutputStream(), 1));
            }
        }
        {
            // to byte array full
            if (data.length == 0) {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(new byte[0]), 0);
                assertEquals(reader.readTo(new byte[1]), -1);
                reader.close();
                assertEquals(reader.readTo(new byte[0]), 0);
            } else {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(new byte[0]), 0);
                byte[] dst = new byte[DST_SIZE];
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst);
                assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = new byte[DST_SIZE];
                    actualLen = reader.readTo(dst);
                    assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                    assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = new byte[data.length];
                assertEquals(reader.readTo(dst), data.length);
                assertEquals(dst, data);
                assertEquals(reader.readTo(new byte[1]), -1);
                reader.close();
                assertEquals(reader.readTo(new byte[0]), 0);
            }
        }
        {
            // to byte array offset
            if (data.length == 0) {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(new byte[0], 0, 0), 0);
                assertEquals(reader.readTo(new byte[1], 0, 1), -1);
                reader.close();
                assertEquals(reader.readTo(new byte[0], 0, 0), 0);
            } else {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(new byte[0], 0, 0), 0);
                byte[] dst = new byte[DST_SIZE + 6];
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst, 3, Math.min(readSize, DST_SIZE));
                assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                assertEquals(Arrays.copyOfRange(dst, 3, 3 + actualLen), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = new byte[DST_SIZE + 6];
                    actualLen = reader.readTo(dst, 3, Math.min(readSize, DST_SIZE));
                    assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                    assertEquals(Arrays.copyOfRange(dst, 3, 3 + actualLen), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = new byte[data.length + 2];
                assertEquals(reader.readTo(dst, 1, data.length), data.length);
                assertEquals(Arrays.copyOfRange(dst, 1, 1 + data.length), data);
                assertEquals(reader.readTo(new byte[1], 0, 1), -1);
                reader.close();
                assertEquals(reader.readTo(new byte[0], 0, 0), 0);
            }
            // error
            expectThrows(IndexOutOfBoundsException.class, () -> supplier.get().readTo(new byte[0], 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> supplier.get().readTo(new byte[0], 0, 1));
        }
        {
            // to buffer full
            if (data.length == 0) {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(ByteBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(ByteBuffer.allocate(1)), -1);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0)), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1)), -1);
                reader.close();
                assertEquals(reader.readTo(ByteBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0)), 0);
            } else {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(ByteBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0)), 0);
                ByteBuffer dst = ByteBuffer.allocate(DST_SIZE);
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst);
                assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                dst.flip();
                assertEquals(JieBuffer.copyContent(dst), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = ByteBuffer.allocateDirect(DST_SIZE);
                    actualLen = reader.readTo(dst);
                    assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                    dst.flip();
                    assertEquals(JieBuffer.copyContent(dst), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = ByteBuffer.allocate(data.length);
                assertEquals(reader.readTo(dst), data.length);
                dst.flip();
                assertEquals(JieBuffer.copyContent(dst), data);
                assertEquals(reader.readTo(ByteBuffer.allocate(1)), -1);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1)), -1);
                reader.close();
                assertEquals(reader.readTo(ByteBuffer.allocate(0)), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0)), 0);
            }
            // error
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(ByteBuffer.allocate(1).asReadOnlyBuffer()));
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(ByteBuffer.allocateDirect(1).asReadOnlyBuffer()));
            }
        }
        {
            // to buffer offset
            if (data.length == 0) {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(ByteBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(ByteBuffer.allocate(1), 1), -1);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0), 1), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1), 1), -1);
                reader.close();
                assertEquals(reader.readTo(ByteBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0), 1), 0);
            } else {
                ByteReader reader = supplier.get();
                assertEquals(reader.readTo(ByteBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0), 1), 0);
                ByteBuffer dst = ByteBuffer.allocate(DST_SIZE);
                if (reader.markSupported()) {
                    reader.mark();
                }
                int actualLen = reader.readTo(dst, readSize);
                assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                dst.flip();
                assertEquals(JieBuffer.copyContent(dst), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = ByteBuffer.allocateDirect(DST_SIZE);
                    actualLen = reader.readTo(dst, readSize);
                    assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                    dst.flip();
                    assertEquals(JieBuffer.copyContent(dst), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = ByteBuffer.allocate(data.length);
                assertEquals(reader.readTo(dst, data.length), data.length);
                dst.flip();
                assertEquals(JieBuffer.copyContent(dst), data);
                assertEquals(reader.readTo(ByteBuffer.allocate(1), 1), -1);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1), 1), -1);
                reader.close();
                assertEquals(reader.readTo(ByteBuffer.allocate(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocate(0), 1), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(1), 0), 0);
                assertEquals(reader.readTo(ByteBuffer.allocateDirect(0), 1), 0);
            }
            // error
            expectThrows(IllegalArgumentException.class, () ->
                supplier.get().readTo(ByteBuffer.allocate(1), -1));
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(ByteBuffer.allocate(1).asReadOnlyBuffer(), 1));
                expectThrows(IORuntimeException.class, () ->
                    supplier.get().readTo(ByteBuffer.allocateDirect(1).asReadOnlyBuffer(), 1));
            }
        }
    }

    private int minSize(int totalSize, int readSize, int remaining) {
        return Math.min(remaining, Math.min(totalSize, readSize));
    }

    @Test
    public void testShare() {
        int dataSize = 1024;
        {
            // input stream
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testShare(ByteReader.from(new ByteArrayInputStream(data)), ByteBuffer.wrap(data), false, false);
        }
        {
            // byte array
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testShare(ByteReader.from(data), ByteBuffer.wrap(data), true, true);
            byte[] dataPadding = new byte[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShare(
                ByteReader.from(dataPadding, 33, data.length),
                ByteBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
        }
        {
            // byte buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testShare(ByteReader.from(ByteBuffer.wrap(data)), ByteBuffer.wrap(data), true, true);
            ByteBuffer direct = JieBuffer.directBuffer(data);
            testShare(ByteReader.from(direct), direct.slice(), true, true);
        }
    }

    private void testShare(
        ByteReader reader, ByteBuffer data, boolean sharedReaderToData, boolean sharedDataToReader
    ) {
        ByteSegment segment = reader.read(data.remaining() * 2);
        ByteBuffer readBuf = segment.data();
        assertEquals(readBuf, data);
        assertTrue(segment.end());
        if (sharedReaderToData) {
            for (int i = 0; i < readBuf.remaining(); i++) {
                readBuf.put(i, (byte) (readBuf.get(i) + 1));
            }
            assertEquals(readBuf, data);
        }
        if (sharedDataToReader) {
            for (int i = 0; i < data.remaining(); i++) {
                data.put(i, (byte) (data.get(i) + 100));
            }
            assertEquals(data, readBuf);
        }
    }

    @Test
    public void testSegment() throws Exception {
        byte[] bytes = JieRandom.fill(new byte[64]);
        ByteReader reader = ByteReader.from(bytes);
        ByteSegment segment = reader.read(bytes.length * 2);
        assertSame(segment.data().array(), bytes);
        assertTrue(segment.end());
        ByteSegment segmentCopy = segment.clone();
        assertEquals(segmentCopy.data(), ByteBuffer.wrap(bytes));
        assertTrue(segmentCopy.end());
        assertNotSame(segmentCopy.data().array(), bytes);
        assertEquals(segment.copyByteArray(), bytes);
        assertNotSame(segment.copyByteArray(), bytes);
        assertEquals(segment.toByteArray(), bytes);
        assertEquals(segment.toByteArray(), new byte[0]);
    }

    @Test
    public void testOthers() throws Exception {
        TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[0]));
        tin.setNextOperation(ReadOps.THROW, 99);
        {
            // mark/reset error
            ByteReader reader = ByteReader.from(tin);
            expectThrows(IORuntimeException.class, reader::mark);
            expectThrows(IORuntimeException.class, reader::reset);
            expectThrows(IORuntimeException.class, reader::close);
            reader = ByteReader.from(Channels.newChannel(tin));
            expectThrows(IORuntimeException.class, reader::mark);
            expectThrows(IORuntimeException.class, reader::reset);
            expectThrows(IORuntimeException.class, reader::close);
        }
    }
}
