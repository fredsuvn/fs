package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ByteReaderTest implements DataTest {

    private static final int DST_SIZE = 256;

    @Test
    public void testReadBytes() throws Exception {
        testReadBytes(0, 1);
        testReadBytes(1, 1);
        testReadBytes(32, 1);
        testReadBytes(32, 16);
        testReadBytes(32, 32);
        testReadBytes(32, 64);
        testReadBytes(128, 16);
        testReadBytes(128, 33);
        testReadBytes(128, 111);
        testReadBytes(128, 128);
        testReadBytes(128, 129);
        testReadBytes(128, 1024);

        // error
        {
            TestInputStream tr = new TestInputStream(new ByteArrayInputStream(new byte[10]));
            tr.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> ByteReader.from(tr).skip(100));
            expectThrows(IORuntimeException.class, () -> ByteReader.from(Channels.newChannel(tr)).skip(100));
        }
    }

    private void testReadBytes(int dataSize, int readSize) throws Exception {
        {
            // input stream
            byte[] data = randomBytes(dataSize);
            testReadBytes(ByteReader.from(new ByteArrayInputStream(data)), data, readSize, false);
            testSkipBytes(ByteReader.from(new ByteArrayInputStream(data)), data, readSize);
            testReadBytes(ByteReader.from(new OneByteInputStream(data)), data, readSize, false);
            testSkipBytes(ByteReader.from(new OneByteInputStream(data)), data, readSize);
            TestInputStream tr = new TestInputStream(new ByteArrayInputStream(data));
            tr.setNextOperation(ReadOps.READ_ZERO);
            testSkipBytes(ByteReader.from(tr), data, readSize);
        }
        {
            // channel
            byte[] data = randomBytes(dataSize);
            testReadBytes(
                ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))),
                data, readSize, false
            );
            testSkipBytes(ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))), data, readSize);
            testReadBytes(
                ByteReader.from(Channels.newChannel(new OneByteInputStream(data))),
                data, readSize, false
            );
            testSkipBytes(ByteReader.from(Channels.newChannel(new OneByteInputStream(data))), data, readSize);
        }
        {
            // byte array
            byte[] data = randomBytes(dataSize);
            testReadBytes(ByteReader.from(data), data, readSize, true);
            testSkipBytes(ByteReader.from(data), data, readSize);
            byte[] dataPadding = new byte[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testReadBytes(
                ByteReader.from(dataPadding, 33, data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkipBytes(ByteReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // buffer
            byte[] data = randomBytes(dataSize);
            testReadBytes(ByteReader.from(ByteBuffer.wrap(data)), data, readSize, true);
            testSkipBytes(ByteReader.from(ByteBuffer.wrap(data)), data, readSize);
        }
        {
            // limited
            byte[] data = randomBytes(dataSize);
            testReadBytes(
                ByteReader.from(data).limit(data.length),
                data,
                readSize, true
            );
            testSkipBytes(
                ByteReader.from(data),
                data,
                readSize
            );
            testReadBytes(
                ByteReader.from(data).limit(data.length + 5),
                data,
                readSize, true
            );
            testSkipBytes(
                ByteReader.from(data).limit(data.length + 5),
                data,
                readSize
            );
            testReadBytes(
                ByteReader.from(new ByteArrayInputStream(data)).limit(data.length + 5),
                data,
                readSize, false
            );
            testSkipBytes(
                ByteReader.from(new ByteArrayInputStream(data)).limit(data.length + 5),
                data,
                readSize
            );
            if (data.length > 5) {
                testReadBytes(
                    ByteReader.from(data).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize, true
                );
                testSkipBytes(
                    ByteReader.from(data).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize
                );
            }
        }
    }

    private void testReadBytes(ByteReader reader, byte[] data, int readSize, boolean preKnown) {
        assertFalse(reader.read(0).end());
        assertFalse(reader.read(0).data().hasRemaining());
        int hasRead = 0;
        while (hasRead < data.length) {
            ByteSegment segment = reader.read(readSize);
            int actualLen = Math.min(readSize, data.length - hasRead);
            assertEquals(
                BufferKit.copyContent(segment.data()),
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

    private void testSkipBytes(ByteReader reader, byte[] data, int readSize) {
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
    public void testReadBytesTo() {
        testReadBytesTo(0, 0);
        testReadBytesTo(0, 1);
        testReadBytesTo(1, 1);
        testReadBytesTo(2, 2);
        testReadBytesTo(64, 1);
        testReadBytesTo(64, 33);
        testReadBytesTo(64, 64);
        testReadBytesTo(64, 111);
        testReadBytesTo(111, 77);
        testReadBytesTo(111, 111);
        testReadBytesTo(111, 333);
        testReadBytesTo(DST_SIZE, DST_SIZE);
        testReadBytesTo(DST_SIZE, DST_SIZE + 6);
        testReadBytesTo(DST_SIZE, DST_SIZE - 6);

        {
            // read to channel error
            ByteReader reader = ByteReader.from(new byte[128]);
            WritableByteChannel errCh = Channels.newChannel(new ErrorOutputStream());
            expectThrows(IORuntimeException.class, () -> reader.readTo(errCh));
            expectThrows(IORuntimeException.class, () -> reader.readTo(errCh, 1));
        }
    }

    private void testReadBytesTo(int dataSize, int readSize) {
        byte[] data = randomBytes(dataSize);
        byte[] dataPadding = new byte[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadBytesTo(() -> ByteReader.from(new ByteArrayInputStream(data)), data, readSize);
            testReadBytesTo(() -> ByteReader.from(new OneByteInputStream(data)), data, readSize);
        }
        {
            // channel
            testReadBytesTo(() -> ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))), data, readSize);
            testReadBytesTo(() -> ByteReader.from(Channels.newChannel(new OneByteInputStream(data))), data, readSize);
        }
        {
            // byte array
            testReadBytesTo(() -> ByteReader.from(data), data, readSize);
            testReadBytesTo(() -> ByteReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // byte buffer
            testReadBytesTo(() -> ByteReader.from(ByteBuffer.wrap(data)), data, readSize);
            testReadBytesTo(() -> ByteReader.from(BufferKit.copyDirect(data)), data, readSize);
        }
        {
            // limited
            testReadBytesTo(() ->
                ByteReader.from(new ByteArrayInputStream(data)).limit(data.length), data, readSize);
            testReadBytesTo(() ->
                ByteReader.from(new ByteArrayInputStream(data)).limit(data.length + 5), data, readSize);
            if (data.length > 5) {
                testReadBytesTo(() ->
                        ByteReader.from(new ByteArrayInputStream(data)).limit(data.length - 5),
                    Arrays.copyOf(data, data.length - 5),
                    readSize
                );
            }
        }
    }

    private void testReadBytesTo(Supplier<ByteReader> supplier, byte[] data, int readSize) {
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
            // to one byte channel
            BytesBuilder builder = new BytesBuilder();
            WritableByteChannel channel = new OneByteWriteableChannel(builder);
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
                assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = ByteBuffer.allocateDirect(DST_SIZE);
                    actualLen = reader.readTo(dst);
                    assertEquals(actualLen, Math.min(data.length, DST_SIZE));
                    dst.flip();
                    assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = ByteBuffer.allocate(data.length);
                assertEquals(reader.readTo(dst), data.length);
                dst.flip();
                assertEquals(BufferKit.copyContent(dst), data);
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
                assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                if (reader.markSupported()) {
                    reader.reset();
                    dst = ByteBuffer.allocateDirect(DST_SIZE);
                    actualLen = reader.readTo(dst, readSize);
                    assertEquals(actualLen, minSize(data.length, readSize, DST_SIZE));
                    dst.flip();
                    assertEquals(BufferKit.copyContent(dst), Arrays.copyOf(data, actualLen));
                }
                // read all
                reader = supplier.get();
                dst = ByteBuffer.allocate(data.length);
                assertEquals(reader.readTo(dst, data.length), data.length);
                dst.flip();
                assertEquals(BufferKit.copyContent(dst), data);
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
    public void testAvailable() throws Exception {
        testAvailable(16);
        testAvailable(32);
        testAvailable(IOKit.bufferSize());
        testAvailable(IOKit.bufferSize() + 1);

        class ZeroIn extends InputStream {

            @Override
            public int available() {
                return 0;
            }

            @Override
            public int read() {
                return -1;
            }

            @Override
            public int read(@NotNull byte[] b, int off, int len) {
                return 0;
            }
        }

        {
            // limited
            ByteReader reader1 = ByteReader.from(new ZeroIn()).limit(11);
            assertEquals(reader1.availableTo(IOKit.nullOutputStream()), 0);
            assertEquals(reader1.availableTo(IOKit.nullOutputStream(), 100), 0);
            assertEquals(reader1.availableTo(Channels.newChannel(IOKit.nullOutputStream())), 0);
            assertEquals(reader1.availableTo(Channels.newChannel(IOKit.nullOutputStream()), 100), 0);
            assertEquals(reader1.availableTo(new byte[1]), 0);
            assertEquals(reader1.availableTo(new byte[1], 0, 1), 0);
            assertEquals(reader1.availableTo(ByteBuffer.allocate(1)), 0);
            assertEquals(reader1.availableTo(ByteBuffer.allocate(1), 1), 0);
            // -1
            ByteReader reader2 = ByteReader.from(new ByteArrayInputStream(new byte[10])).limit(1);
            assertEquals(reader2.availableTo(IOKit.nullOutputStream()), 1);
            assertEquals(reader2.availableTo(IOKit.nullOutputStream()), -1);
            assertEquals(reader2.availableTo(IOKit.nullOutputStream(), 1), -1);
            assertEquals(reader2.availableTo(Channels.newChannel(IOKit.nullOutputStream())), -1);
            assertEquals(reader2.availableTo(Channels.newChannel(IOKit.nullOutputStream()), 100), -1);
            assertEquals(reader2.availableTo(new byte[1]), -1);
            assertEquals(reader2.availableTo(new byte[1], 0, 1), -1);
            assertEquals(reader2.availableTo(ByteBuffer.allocate(1)), -1);
            assertEquals(reader2.availableTo(ByteBuffer.allocate(1), 1), -1);
            // 0
            assertEquals(reader2.availableTo(IOKit.nullOutputStream(), 0), 0);
            assertEquals(reader2.availableTo(Channels.newChannel(IOKit.nullOutputStream()), 0), 0);
            assertEquals(reader2.availableTo(new byte[0]), 0);
            assertEquals(reader2.availableTo(new byte[1], 0, 0), 0);
            assertEquals(reader2.availableTo(ByteBuffer.allocate(0)), 0);
            assertEquals(reader2.availableTo(ByteBuffer.allocate(0), 1), 0);
            assertEquals(reader2.availableTo(ByteBuffer.allocate(1), 0), 0);
        }
    }

    private void testAvailable(int size) throws Exception {
        byte[] src = randomBytes(size);

        class In extends InputStream {

            private final byte[] data = src;
            private int pos = 0;
            private boolean zero = true;

            @Override
            public int available() {
                if (pos >= data.length) {
                    return 0;
                }
                if (zero) {
                    return 0;
                }
                return 1;
            }

            @Override
            public int read() {
                return pos < data.length ? data[pos++] & 0xFF : -1;
            }

            @Override
            public int read(@NotNull byte[] b, int off, int len) {
                if (pos >= data.length) {
                    return -1;
                }
                int readSize = available();
                if (readSize == 0) {
                    zero = false;
                    return 0;
                } else {
                    zero = true;
                    b[off] = data[pos++];
                    return 1;
                }
            }
        }

        class Cin implements ReadableByteChannel {

            private final byte[] data = src;
            private int pos = 0;
            private boolean zero = true;

            @Override
            public int read(ByteBuffer dst) throws IOException {
                if (pos >= data.length) {
                    return -1;
                }
                if (zero) {
                    zero = false;
                    return 0;
                } else {
                    zero = true;
                    dst.put(data[pos++]);
                    return 1;
                }
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close() {
            }
        }

        // input stream
        testAvailable(size, src, () -> ByteReader.from(new In()), false);
        // byte channel
        testAvailable(size, src, () -> ByteReader.from(new Cin()), false);
        // byte array
        testAvailable(size, src, () -> ByteReader.from(src), true);
        // byte buffer
        testAvailable(size, src, () -> ByteReader.from(ByteBuffer.wrap(src)), true);
        // limited
        testAvailable(size, src, () -> ByteReader.from(new In()).limit(size), false);
        testAvailable(size, src, () -> ByteReader.from(new In()).limit(size + 1), false);
        testAvailable(
            size - 1,
            Arrays.copyOf(src, size - 1),
            () -> ByteReader.from(new In()).limit(size - 1),
            false
        );
        testAvailable(size, src, () -> ByteReader.from(src).limit(size), true);
        testAvailable(size, src, () -> ByteReader.from(src).limit(size + 1), true);
        testAvailable(
            size - 1,
            Arrays.copyOf(src, size - 1),
            () -> ByteReader.from(src).limit(size - 1),
            true
        );
    }

    private void testAvailable(
        int size, byte[] src, Supplier<ByteReader> supplier, boolean preKnown
    ) throws Exception {
        {
            // available
            ByteReader reader = supplier.get();
            assertFalse(reader.available(0).end());
            assertFalse(reader.available(0).data().hasRemaining());
            if (preKnown) {
                ByteSegment s = reader.available();
                assertTrue(s.end());
                assertEquals(BufferKit.copyContent(s.data()), src);
                assertTrue(reader.available(1).end());
            } else {
                ByteSegment s0 = reader.available();
                assertFalse(s0.end());
                assertEquals(BufferKit.copyContent(s0.data()).length, 0);
                BytesBuilder builder = new BytesBuilder();
                while (true) {
                    ByteSegment s1 = reader.available();
                    builder.append(s1.data());
                    ByteSegment s2 = reader.available(size);
                    builder.append(s2.data());
                    if (s2.end()) {
                        break;
                    }
                }
                assertEquals(builder.toByteArray(), src);
                ByteSegment se = reader.available(size);
                assertTrue(se.end());
                assertFalse(se.data().hasRemaining());
            }
        }
        {
            // to output stream
            BytesBuilder builder = new BytesBuilder();
            ByteReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(builder), preKnown ? size : 0);
            while (true) {
                long readSize = reader1.availableTo(builder);
                if (readSize < 0) {
                    break;
                }
            }
            assertEquals(builder.toByteArray(), src);
            builder.reset();
            ByteReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(builder, size * 2L), preKnown ? size : 0);
            while (true) {
                long readSize = reader2.availableTo(builder, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertEquals(builder.toByteArray(), src);
            builder.reset();
        }
        {
            // to out channel
            BytesBuilder builder = new BytesBuilder();
            WritableByteChannel outChannel = Channels.newChannel(builder);
            ByteReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(builder), preKnown ? size : 0);
            while (true) {
                long readSize = reader1.availableTo(outChannel);
                if (readSize < 0) {
                    break;
                }
            }
            assertEquals(builder.toByteArray(), src);
            builder.reset();
            ByteReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(builder, size * 2L), preKnown ? size : 0);
            while (true) {
                long readSize = reader2.availableTo(outChannel, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertEquals(builder.toByteArray(), src);
            builder.reset();
        }
        {
            // to array
            byte[] dst = new byte[size * 2];
            int c = 0;
            ByteReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader1.availableTo(dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertEquals(Arrays.copyOf(dst, size), src);
            dst = new byte[size * 2];
            c = 0;
            ByteReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader2.availableTo(dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertEquals(Arrays.copyOf(dst, size), src);
        }
        {
            // to buffer
            ByteBuffer dst = ByteBuffer.allocate(size * 2);
            int c = 0;
            ByteReader reader1 = supplier.get();
            assertEquals(reader1.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader1.availableTo(dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertEquals(BufferKit.read(dst), src);
            dst = ByteBuffer.allocate(size * 2);
            c = 0;
            ByteReader reader2 = supplier.get();
            assertEquals(reader2.availableTo(dst), preKnown ? size : 0);
            while (c < size) {
                long readSize = reader2.availableTo(dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertEquals(BufferKit.read(dst), src);
        }
    }

    @Test
    public void testReadAll() throws Exception {
        testReadAll(0);
        testReadAll(16);
        testReadAll(32);
        testReadAll(IOKit.bufferSize());
        testReadAll(IOKit.bufferSize() + 1);
    }

    private void testReadAll(int size) throws Exception {
        byte[] data = randomBytes(size);
        {
            // input stream
            ByteReader reader = ByteReader.from(new ByteArrayInputStream(data));
            ByteBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // readable channel
            ByteReader reader = ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data)));
            ByteBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // array
            ByteReader reader = ByteReader.from(data);
            ByteBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // buffer
            ByteReader reader = ByteReader.from(ByteBuffer.wrap(data));
            ByteBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
        {
            // limited
            ByteReader reader = ByteReader.from(data).limit(size);
            ByteBuffer buffer = reader.read();
            assertEquals(buffer == null, size == 0);
            if (buffer != null) {
                assertEquals(buffer.array(), data);
            }
            assertNull(reader.read());
        }
    }

    @Test
    public void testShareBytes() {
        int dataSize = 1024;
        int limitSize = dataSize / 2;
        {
            // input stream
            byte[] data = randomBytes(dataSize);
            testShareBytes(
                ByteReader.from(new ByteArrayInputStream(data)), ByteBuffer.wrap(data),
                false, false
            );
            testShareBytes(
                ByteReader.from(new ByteArrayInputStream(data)).limit(limitSize), ByteBuffer.wrap(data, 0, limitSize),
                false, false
            );
        }
        {
            // byte array
            byte[] data = randomBytes(dataSize);
            testShareBytes(ByteReader.from(data), ByteBuffer.wrap(data), true, true);
            testShareBytes(
                ByteReader.from(data).limit(limitSize),
                ByteBuffer.wrap(data, 0, limitSize),
                true, true
            );
            byte[] dataPadding = new byte[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShareBytes(
                ByteReader.from(dataPadding, 33, data.length),
                ByteBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
            testShareBytes(
                ByteReader.from(dataPadding, 33, data.length).limit(limitSize),
                ByteBuffer.wrap(dataPadding, 33, limitSize),
                true, true
            );
        }
        {
            // byte buffer
            byte[] data = randomBytes(dataSize);
            testShareBytes(
                ByteReader.from(ByteBuffer.wrap(data)),
                ByteBuffer.wrap(data),
                true, true
            );
            testShareBytes(
                ByteReader.from(ByteBuffer.wrap(data)).limit(limitSize),
                ByteBuffer.wrap(data, 0, limitSize),
                true, true
            );
            ByteBuffer direct = BufferKit.copyDirect(data);
            testShareBytes(ByteReader.from(direct), direct.slice(), true, true);
            direct.clear();
            testShareBytes(
                ByteReader.from(direct).limit(limitSize),
                BufferKit.slice(direct, limitSize),
                true, true
            );
        }
    }

    private void testShareBytes(
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
    public void testBytesSegment() throws Exception {
        byte[] bytes = randomBytes(64);
        ByteReader reader = ByteReader.from(bytes);
        ByteSegment segment = reader.read(bytes.length * 2);
        assertSame(segment.data().array(), bytes);
        assertTrue(segment.end());
        ByteSegment segmentCopy = segment.clone();
        assertEquals(segmentCopy.data(), ByteBuffer.wrap(bytes));
        assertTrue(segmentCopy.end());
        assertNotSame(segmentCopy.data().array(), bytes);
        assertEquals(segment.copyArray(), bytes);
        assertNotSame(segment.copyArray(), bytes);
        assertEquals(segment.array(), bytes);
        assertEquals(segment.array(), new byte[0]);
    }

    @Test
    public void testAsInputStream() throws Exception {
        testAsInputStream(128);
        testAsInputStream(IOKit.bufferSize());
        testAsInputStream(IOKit.bufferSize() + 1);
    }

    private void testAsInputStream(int size) throws Exception {
        byte[] data = randomBytes(size);
        BytesBuilder builder = new BytesBuilder(size);
        {
            // input stream
            IOImplsTest.testInputStream(
                ByteReader.from(IOKit.newInputStream(new FakeFile(data), 0)).asInputStream(),
                data,
                true,
                true,
                false
            );
            testReadToBuilder(ByteReader.from(new ByteArrayInputStream(data)), data, builder);
        }
        {
            // channel
            IOImplsTest.testInputStream(
                ByteReader.from(Channels.newChannel(IOKit.newInputStream(new FakeFile(data), 0))).asInputStream(),
                data,
                false,
                false,
                false
            );
            testReadToBuilder(
                ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))), data, builder
            );
        }
        {
            // array
            IOImplsTest.testInputStream(
                ByteReader.from(data).asInputStream(),
                data,
                true,
                false,
                true
            );
            testReadToBuilder(ByteReader.from(data), data, builder);
        }
        {
            // buffer
            IOImplsTest.testInputStream(
                ByteReader.from(ByteBuffer.wrap(data)).asInputStream(),
                data,
                true,
                false,
                false
            );
            testReadToBuilder(ByteReader.from(ByteBuffer.wrap(data)), data, builder);
        }
        {
            // limited
            IOImplsTest.testInputStream(
                ByteReader.from(data).limit(size).asInputStream(),
                data,
                false,
                false,
                true
            );
            testReadToBuilder(ByteReader.from(data).limit(size), data, builder);
            IOImplsTest.testInputStream(
                ByteReader.from(data).limit(size - 5).asInputStream(),
                Arrays.copyOf(data, size - 5),
                false,
                false,
                true
            );
            testReadToBuilder(
                ByteReader.from(data).limit(size - 5), Arrays.copyOf(data, size - 5), builder
            );
            IOImplsTest.testInputStream(
                ByteReader.from(data).limit(size + 5).asInputStream(),
                data,
                false,
                false,
                true
            );
            testReadToBuilder(ByteReader.from(data).limit(size + 5), data, builder);
        }
    }

    public static void testReadToBuilder(ByteReader reader, byte[] data, BytesBuilder builder) {
        builder.reset();
        InputStream asIn = reader.asInputStream();
        reader.readTo(builder, 1);
        IOKit.readTo(asIn, builder);
        assertEquals(builder.toByteArray(), data);
    }

    @Test
    public void testReady() throws Exception {
        testReady(128);
        testReady(IOKit.bufferSize());
        testReady(IOKit.bufferSize() + 1);
    }

    private void testReady(int size) throws Exception {
        byte[] data = randomBytes(size);
        {
            // reader
            ByteReader reader = ByteReader.from(new ByteArrayInputStream(data));
            assertEquals(reader.ready(), size);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // channel
            ByteReader reader = ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data)));
            assertEquals(reader.ready(), 0);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // array
            ByteReader reader = ByteReader.from(data);
            assertEquals(reader.ready(), size);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // buffer
            ByteReader reader = ByteReader.from(ByteBuffer.wrap(data));
            assertEquals(reader.ready(), size);
            reader.read();
            assertEquals(reader.ready(), 0);
        }
        {
            // limited
            ByteReader reader1 = ByteReader.from(data).limit(size);
            assertEquals(reader1.ready(), size);
            reader1.read();
            assertEquals(reader1.ready(), 0);
            ByteReader reader2 = ByteReader.from(data).limit(size - 5);
            assertEquals(reader2.ready(), size - 5);
            reader2.read();
            assertEquals(reader2.ready(), 0);
            ByteReader reader3 = ByteReader.from(data).limit(size + 5);
            assertEquals(reader3.ready(), size);
            reader3.read();
            assertEquals(reader3.ready(), 0);
            ByteReader reader4 = ByteReader.from(Channels.newChannel(new ByteArrayInputStream(data))).limit(size);
            assertEquals(reader4.ready(), 0);
            reader4.read();
            assertEquals(reader4.ready(), 0);
        }
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
            reader = ByteReader.from(ByteBuffer.allocate(1));
            expectThrows(IORuntimeException.class, reader::reset);
        }
        {
            // ready
            TestInputStream errIn = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            errIn.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> ByteReader.from(errIn).ready());
        }
        {
            // asInputStream
            InputStream in1 = ByteReader.from(ByteBuffer.allocate(1)).limit(1).asInputStream();
            expectThrows(IOException.class, in1::reset);
            InputStream in2 = ByteReader.from(tin).limit(1).asInputStream();
            expectThrows(IOException.class, () -> in2.skip(1));
            InputStream in3 = ByteReader.from(tin).limit(1).asInputStream();
            expectThrows(IOException.class, () -> in3.close());
            TestInputStream errIn = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            errIn.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IOException.class, () -> ByteReader.from(errIn).limit(1).asInputStream().read());
            expectThrows(IOException.class, () -> ByteReader.from(errIn).limit(1).asInputStream().read(new byte[2]));
        }
    }
}
