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
import java.util.Arrays;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ByteReaderTest {

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

        // {
        //     // special: nio
        //     class NioInput extends InputStream {
        //
        //         private int count = 1;
        //
        //         @Override
        //         public int read() throws IOException {
        //             if (count > 0) {
        //                 count--;
        //                 return 66;
        //             }
        //             return -1;
        //         }
        //     }
        //     byte[] dst = new byte[2];
        //     assertEquals(ByteReader.from(new NioInput()).readTo(dst), 1);
        //     assertEquals(dst[0], 66);
        // }
    }

    private void testReadTo0(int dataSize, int readSize) {
        byte[] data = JieRandom.fill(new byte[dataSize]);
        byte[] dataPadding = new byte[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(() -> ByteReader.from(new ByteArrayInputStream(data)), data, readSize);
        }
        // {
        //     // channel
        //     testReadTo0(ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))), data);
        // }
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
            ByteReader reader = supplier.get();
            BytesBuilder builder = new BytesBuilder();
            if (reader.markSupported()) {
                reader.mark();
            }
            long actualLen = reader.readTo(builder, readSize);
            assertEquals(actualLen, actualReadSize(data.length, readSize));
            long restLen = data.length - (actualLen < 0 ? 0 : actualLen);
            assertEquals(reader.readTo(builder), restLen == 0 ? -1 : restLen);
            assertEquals(builder.toByteArray(), data);
            if (reader.markSupported()) {
                reader.reset();
                builder.reset();
                actualLen = reader.readTo(builder, readSize);
                assertEquals(actualLen, actualReadSize(data.length, readSize));
                restLen = data.length - (actualLen < 0 ? 0 : actualLen);
                assertEquals(reader.readTo(builder), restLen == 0 ? -1 : restLen);
                assertEquals(builder.toByteArray(), data);
            }
            assertEquals(reader.readTo(builder), -1);
            assertEquals(reader.readTo(builder, 66), -1);
            reader.close();
            assertEquals(reader.readTo(builder, 0), 0);
            // error
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorOutputStream()));
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(new ErrorOutputStream(), 1));
                expectThrows(IllegalArgumentException.class, () -> supplier.get().readTo(builder, -1));
            }
        }
        {
            // to byte array full
            ByteReader reader = supplier.get();
            byte[] dst = new byte[data.length];
            if (reader.markSupported()) {
                reader.mark();
            }
            int actualLen = reader.readTo(dst);
            assertEquals(actualLen, actualReadSize(data.length, dst.length));
            assertEquals(data, dst);
            if (reader.markSupported()) {
                reader.reset();
                dst = new byte[data.length];
                actualLen = reader.readTo(dst);
                assertEquals(actualLen, actualReadSize(data.length, dst.length));
                assertEquals(data, dst);
            }
            assertEquals(reader.readTo(new byte[1]), -1);
            reader.close();
            assertEquals(reader.readTo(new byte[0]), 0);
        }
        {
            // to byte array offset
            ByteReader reader = supplier.get();
            byte[] dst = new byte[data.length + 6];
            if (reader.markSupported()) {
                reader.mark();
            }
            int len = Math.min(data.length, readSize);
            int actualLen = reader.readTo(dst, 3, len);
            assertEquals(actualLen, actualReadSize(data.length, readSize, len));
            assertEquals(data, Arrays.copyOfRange(dst, 3, 3 + Math.max(actualLen, 0)));
            if (reader.markSupported()) {
                reader.reset();
                dst = new byte[data.length + 6];
                actualLen = reader.readTo(dst, 3, len);
                assertEquals(actualLen, actualReadSize(data.length, readSize, len));
                assertEquals(data, Arrays.copyOfRange(dst, 3, 3 + Math.max(actualLen, 0)));
            }
            assertEquals(reader.readTo(new byte[1]), -1);
            reader.close();
            assertEquals(reader.readTo(new byte[0]), 0);
            // error
            if (data.length > 0) {
                expectThrows(IndexOutOfBoundsException.class, () -> reader.readTo(new byte[0], 0, -1));
                expectThrows(IndexOutOfBoundsException.class, () -> reader.readTo(new byte[0], 0, 1));
            }
        }
        {
            // to heap buffer
            ByteReader reader = supplier.get();
            ByteBuffer dst = ByteBuffer.allocate(data.length);
            if (reader.markSupported()) {
                reader.mark();
            }
            int actualLen = reader.readTo(dst, readSize);
            assertEquals(actualLen, actualReadSize(data.length, readSize));
            int restLen = data.length - (Math.max(actualLen, 0));
            assertEquals(reader.readTo(dst), actualReadSize(restLen, dst.remaining()));
            dst.flip();
            assertEquals(JieBuffer.copyContent(dst), data);
            if (reader.markSupported()) {
                reader.reset();
                dst = ByteBuffer.allocate(data.length);
                actualLen = reader.readTo(dst, readSize);
                assertEquals(actualLen, actualReadSize(data.length, readSize));
                restLen = data.length - (Math.max(actualLen, 0));
                assertEquals(reader.readTo(dst), actualReadSize(restLen, dst.remaining()));
                dst.flip();
                assertEquals(JieBuffer.copyContent(dst), data);
            }
            assertEquals(reader.readTo(dst), 0);
            assertEquals(reader.readTo(dst, 66), 0);
            assertEquals(reader.readTo(ByteBuffer.allocate(1)), -1);
            reader.close();
            assertEquals(reader.readTo(dst), 0);
            assertEquals(reader.readTo(dst, 0), 0);
            assertEquals(reader.readTo(dst, 66), 0);
            // error
            if (data.length > 0) {
                ByteBuffer buf = ByteBuffer.allocate(6);
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(buf.asReadOnlyBuffer()));
                expectThrows(IllegalArgumentException.class, () -> supplier.get().readTo(buf, -1));
            }
        }
        {
            // to heap buffer
            ByteReader reader = supplier.get();
            ByteBuffer dst = ByteBuffer.allocateDirect(data.length);
            if (reader.markSupported()) {
                reader.mark();
            }
            int actualLen = reader.readTo(dst, readSize);
            assertEquals(actualLen, actualReadSize(data.length, readSize));
            int restLen = data.length - (Math.max(actualLen, 0));
            assertEquals(reader.readTo(dst), actualReadSize(restLen, dst.remaining()));
            dst.flip();
            assertEquals(JieBuffer.copyContent(dst), data);
            if (reader.markSupported()) {
                reader.reset();
                dst = ByteBuffer.allocateDirect(data.length);
                actualLen = reader.readTo(dst, readSize);
                assertEquals(actualLen, actualReadSize(data.length, readSize));
                restLen = data.length - (Math.max(actualLen, 0));
                assertEquals(reader.readTo(dst), actualReadSize(restLen, dst.remaining()));
                dst.flip();
                assertEquals(JieBuffer.copyContent(dst), data);
            }
            assertEquals(reader.readTo(dst), 0);
            assertEquals(reader.readTo(dst, 66), 0);
            assertEquals(reader.readTo(ByteBuffer.allocateDirect(1)), -1);
            reader.close();
            assertEquals(reader.readTo(dst), 0);
            assertEquals(reader.readTo(dst, 0), 0);
            assertEquals(reader.readTo(dst, 66), 0);
            // error
            if (data.length > 0) {
                ByteBuffer buf = ByteBuffer.allocateDirect(6);
                expectThrows(IORuntimeException.class, () -> supplier.get().readTo(buf.asReadOnlyBuffer()));
                expectThrows(IllegalArgumentException.class, () -> supplier.get().readTo(buf, -1));
            }
        }
    }

    private int actualReadSize(int totalSize, int readSize) {
        if (readSize == 0) {
            return 0;
        }
        if (totalSize == 0) {
            return -1;
        }
        return Math.min(readSize, totalSize);
    }

    private int actualReadSize(int totalSize, int readSize, int range) {
        if (readSize == 0 || range == 0) {
            return 0;
        }
        if (totalSize == 0) {
            return -1;
        }
        return Math.min(readSize, totalSize);
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

    // @Test
    // public void testSpecial() throws Exception {
    //     byte[] bytes = JieArray.fill(new byte[64], Constants.FILL_BYTE);
    //     ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    //     TestInputStream testIn = new TestInputStream(in);
    //     {
    //         // NIO tests
    //         ByteReader reader = ByteReader.from(testIn);
    //         testIn.setNextOperation(ReadOps.READ_ZERO, 10);
    //         ByteSegment s0 = reader.read(bytes.length);
    //         assertEquals(s0.data(), ByteBuffer.wrap(bytes));
    //         assertFalse(s0.end());
    //         s0 = reader.read(1);
    //         assertTrue(s0.end());
    //         assertEquals(reader.skip(66), 0);
    //         in.reset();
    //         ByteReader reader2 = ByteReader.from(testIn);
    //         testIn.setNextOperation(ReadOps.READ_ZERO);
    //         assertEquals(reader2.skip(66), bytes.length);
    //         // TestInputStream testIn2 = new TestInputStream(new ByteArrayInputStream(new byte[2]));
    //         // testIn2.setNextOperation(ReadOps.READ_ZERO);
    //         // assertEquals(ByteReader.from(testIn2).skip(66), 2);
    //     }
    //     {
    //         // exception tests
    //         ByteReader reader = ByteReader.from(testIn);
    //         in.reset();
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.read(66));
    //         expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
    //         // expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
    //         expectThrows(IllegalArgumentException.class, () -> reader.skip(-66));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.skip(66));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.readTo(ByteBuffer.allocate(1)));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.readTo(new byte[1]));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, reader::mark);
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, reader::reset);
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, reader::close);
    //     }
    //     {
    //         // for segment
    //         byte[] bytesCopy = Arrays.copyOf(bytes, bytes.length);
    //         ByteReader reader = ByteReader.from(bytesCopy);
    //         ByteSegment segment = reader.read(bytesCopy.length * 2);
    //         assertSame(segment.data().array(), bytesCopy);
    //         assertTrue(segment.end());
    //         ByteSegment segmentCopy = segment.clone();
    //         assertEquals(segmentCopy.data(), ByteBuffer.wrap(bytesCopy));
    //         assertTrue(segmentCopy.end());
    //         assertNotSame(segmentCopy.data().array(), bytesCopy);
    //     }
    //     {
    //         // special mark/reset
    //         TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[2]));
    //         ByteReader reader = ByteReader.from(tin);//.withReadLimit(1);
    //         assertTrue(reader.markSupported());
    //         tin.markSupported(false);
    //         assertFalse(reader.markSupported());
    //         reader.mark();
    //         reader.reset();
    //     }
    //     {
    //         // close
    //         ByteReader.from(JieIO.newInputStream(new byte[0])).close();
    //         ByteReader.from(new byte[0]).close();
    //         ByteReader.from(ByteBuffer.allocate(0)).close();
    //     }
    // }
}
