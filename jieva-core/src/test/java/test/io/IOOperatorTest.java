package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;
import xyz.sunqian.test.TestReader;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class IOOperatorTest implements DataTest {

    @Test
    public void testReadBytes() throws Exception {
        testReadBytes(64);
        testReadBytes(128);
        testReadBytes(256);
        testReadBytes(1024);
        testReadBytes(IOKit.bufferSize());
        testReadBytes(IOKit.bufferSize() - 1);
        testReadBytes(IOKit.bufferSize() + 1);
        testReadBytes(IOKit.bufferSize() - 5);
        testReadBytes(IOKit.bufferSize() + 5);
        testReadBytes(IOKit.bufferSize() * 2);
        testReadBytes(IOKit.bufferSize() * 2 - 1);
        testReadBytes(IOKit.bufferSize() * 2 + 1);
        testReadBytes(IOKit.bufferSize() * 2 - 5);
        testReadBytes(IOKit.bufferSize() * 2 + 5);
        testReadBytes(IOKit.bufferSize() * 3);
        testReadBytes(IOKit.bufferSize() * 3 - 1);
        testReadBytes(IOKit.bufferSize() * 3 + 1);
        testReadBytes(IOKit.bufferSize() * 3 - 5);
        testReadBytes(IOKit.bufferSize() * 3 + 5);

        {
            // read stream
            assertNull(IOKit.read(new ByteArrayInputStream(new byte[0])));
            assertNull(IOKit.read(new ByteArrayInputStream(new byte[0]), 66));
            assertNull(IOKit.read(Channels.newChannel(new ByteArrayInputStream(new byte[0]))));
            assertNull(IOKit.read(Channels.newChannel(new ByteArrayInputStream(new byte[0])), 66));
        }

        {
            // error
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> IOKit.read(tin));
            expectThrows(IORuntimeException.class, () -> IOKit.read(tin, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.read(tin, -1));
            expectThrows(IORuntimeException.class, () -> IOKit.read(Channels.newChannel(tin)));
            expectThrows(IORuntimeException.class, () -> IOKit.read(Channels.newChannel(tin), 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.read(Channels.newChannel(tin), -1));
        }
    }

    private void testReadBytes(int totalSize) throws Exception {
        testReadBytes(IOOperator.get(IOKit.bufferSize()), totalSize);
        testReadBytes(IOOperator.get(1), totalSize);
        testReadBytes(IOOperator.get(2), totalSize);
        testReadBytes(IOOperator.get(IOKit.bufferSize() - 1), totalSize);
        testReadBytes(IOOperator.get(IOKit.bufferSize() + 1), totalSize);
        testReadBytes(IOOperator.get(IOKit.bufferSize() * 2), totalSize);
    }

    private void testReadBytes(IOOperator reader, int totalSize) throws Exception {
        testReadBytes(reader, totalSize, totalSize);
        testReadBytes(reader, totalSize, 0);
        testReadBytes(reader, totalSize, 1);
        testReadBytes(reader, totalSize, totalSize / 2);
        testReadBytes(reader, totalSize, totalSize - 1);
        testReadBytes(reader, totalSize, totalSize + 1);
        testReadBytes(reader, totalSize, totalSize * 2);
    }

    private void testReadBytes(IOOperator reader, int totalSize, int readSize) throws Exception {
        {
            // stream
            byte[] data = randomBytes(totalSize);
            assertEquals(reader.read(new ByteArrayInputStream(data)), data);
            assertEquals(
                reader.read(new ByteArrayInputStream(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertEquals(reader.read(new OneByteInputStream(data)), data);
            assertEquals(
                reader.read(new OneByteInputStream(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
        }
        {
            // channel
            byte[] data = randomBytes(totalSize);
            ByteBuffer dataBuf = ByteBuffer.wrap(data);
            assertEquals(reader.read(Channels.newChannel(new ByteArrayInputStream(data))), dataBuf);
            assertEquals(
                reader.read(Channels.newChannel(new ByteArrayInputStream(data)), readSize < 0 ? totalSize : readSize),
                ByteBuffer.wrap((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
            );
            assertEquals(
                reader.read(Channels.newChannel(new OneByteInputStream(data))),
                ByteBuffer.wrap(data)
            );
            assertEquals(
                reader.read(Channels.newChannel(new OneByteInputStream(data)), readSize < 0 ? totalSize : readSize),
                ByteBuffer.wrap((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
            );
        }
    }

    @Test
    public void testReadBytesTo() throws Exception {
        testReadBytesTo(64);
        testReadBytesTo(128);
        testReadBytesTo(256);
        testReadBytesTo(1024);
        testReadBytesTo(IOKit.bufferSize());
        testReadBytesTo(IOKit.bufferSize() - 1);
        testReadBytesTo(IOKit.bufferSize() + 1);
        testReadBytesTo(IOKit.bufferSize() - 5);
        testReadBytesTo(IOKit.bufferSize() + 5);
        testReadBytesTo(IOKit.bufferSize() * 2);
        testReadBytesTo(IOKit.bufferSize() * 2 - 1);
        testReadBytesTo(IOKit.bufferSize() * 2 + 1);
        testReadBytesTo(IOKit.bufferSize() * 2 - 5);
        testReadBytesTo(IOKit.bufferSize() * 2 + 5);
        testReadBytesTo(IOKit.bufferSize() * 3);
        testReadBytesTo(IOKit.bufferSize() * 3 - 1);
        testReadBytesTo(IOKit.bufferSize() * 3 + 1);
        testReadBytesTo(IOKit.bufferSize() * 3 - 5);
        testReadBytesTo(IOKit.bufferSize() * 3 + 5);

        {
            // size 0: stream to stream
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), bb),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), bb, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), bb, 11),
                -1
            );
            assertEquals(bb.size(), 0);
        }
        {
            // size 0: stream to channel
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            WritableByteChannel channel = Channels.newChannel(bb);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), channel),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), channel, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), channel, 11),
                -1
            );
            assertEquals(bb.size(), 0);
        }
        {
            // size 0: stream to array
            byte[] data = new byte[0];
            byte[] aar = new byte[64];
            Arrays.fill(aar, (byte) 7);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), aar),
                -1
            );
            assertEquals(aar[0], (byte) 7);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), new byte[0]),
                0
            );
            assertEquals(aar[0], (byte) 7);
        }
        {
            // size 0: stream to heap buffer
            byte[] data = new byte[0];
            ByteBuffer dst1 = ByteBuffer.allocate(1);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 0),
                0
            );
            assertEquals(dst1.position(), 0);
            ByteBuffer dst2 = ByteBuffer.allocate(0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst2),
                0
            );
            assertEquals(dst2.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst2, 1),
                0
            );
            assertEquals(dst2.position(), 0);
        }
        {
            // size 0: stream to direct buffer
            byte[] data = new byte[0];
            ByteBuffer dst1 = ByteBuffer.allocateDirect(1);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 0),
                0
            );
            assertEquals(dst1.position(), 0);
            ByteBuffer dst2 = ByteBuffer.allocateDirect(0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst2),
                0
            );
            assertEquals(dst2.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst2, 1),
                0
            );
            assertEquals(dst2.position(), 0);
        }
        {
            // size 0: channel to channel
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb)),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb), 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb), 11),
                -1
            );
            assertEquals(bb.size(), 0);
        }
        {
            // size 0: channel to stream
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(data));
            assertEquals(
                IOKit.readTo(channel, bb),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(channel, bb, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(channel, bb, 11),
                -1
            );
            assertEquals(bb.size(), 0);
        }
        {
            // size 0: channel to array
            byte[] data = new byte[0];
            byte[] aar = new byte[64];
            Arrays.fill(aar, (byte) 7);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), aar),
                -1
            );
            assertEquals(aar[0], (byte) 7);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), new byte[0]),
                0
            );
            assertEquals(aar[0], (byte) 7);
        }
        {
            // size 0: channel to heap buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocate(1);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
            ByteBuffer dst2 = ByteBuffer.allocate(0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2),
                0
            );
            assertEquals(dst2.position(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2, 1),
                0
            );
            assertEquals(dst2.position(), 0);
        }
        {
            // size 0: channel to direct buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocateDirect(1);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
            ByteBuffer dst2 = ByteBuffer.allocateDirect(0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2),
                0
            );
            assertEquals(dst2.position(), 0);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2, 1),
                0
            );
            assertEquals(dst2.position(), 0);
        }

        {
            // error
            ErrorOutputStream errOut = new ErrorOutputStream();
            WritableByteChannel errCh = Channels.newChannel(errOut);
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            // read stream
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errOut, -1));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, new byte[1], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new byte[0], 1, 0));
            expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new byte[0], 0, 1));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, ByteBuffer.allocate(1)));
            expectThrows(IllegalArgumentException.class, () ->
                IOKit.readTo(tin, ByteBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                IOKit.readTo(new ByteArrayInputStream(new byte[1]), ByteBuffer.allocate(1).asReadOnlyBuffer())
            );
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errCh));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errCh, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errCh, -1));
            // read channel
            ReadableByteChannel tch = Channels.newChannel(tin);
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errCh));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errCh, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tch, errCh, -1));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tch, new byte[1], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tch, new byte[0], 1, 0));
            expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tch, new byte[0], 0, 1));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tch, ByteBuffer.allocate(1)));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tch, ByteBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                IOKit.readTo(
                    Channels.newChannel(new ByteArrayInputStream(new byte[1])),
                    ByteBuffer.allocate(1).asReadOnlyBuffer()
                )
            );
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errOut));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errOut, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tch, errOut, -1));
        }
    }

    private void testReadBytesTo(int totalSize) throws Exception {
        testReadBytesTo(IOOperator.get(IOKit.bufferSize()), totalSize);
        testReadBytesTo(IOOperator.get(1), totalSize);
        testReadBytesTo(IOOperator.get(2), totalSize);
        testReadBytesTo(IOOperator.get(IOKit.bufferSize() - 1), totalSize);
        testReadBytesTo(IOOperator.get(IOKit.bufferSize() + 1), totalSize);
        testReadBytesTo(IOOperator.get(IOKit.bufferSize() * 2), totalSize);
    }

    private void testReadBytesTo(IOOperator reader, int totalSize) throws Exception {
        testReadBytesTo(reader, totalSize, totalSize);
        testReadBytesTo(reader, totalSize, 0);
        testReadBytesTo(reader, totalSize, 1);
        testReadBytesTo(reader, totalSize, totalSize / 2);
        testReadBytesTo(reader, totalSize, totalSize - 1);
        testReadBytesTo(reader, totalSize, totalSize + 1);
        testReadBytesTo(reader, totalSize, totalSize * 2);
    }

    private void testReadBytesTo(IOOperator reader, int totalSize, int readSize) throws Exception {
        {
            // stream to stream
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), builder),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), builder),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
        }
        {
            // stream to channel
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            WritableByteChannel channel = Channels.newChannel(builder);
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            // write one byte channel
            channel = new OneByteWriteableChannel(builder);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
        }
        {
            // stream to array
            byte[] data = randomBytes(totalSize);
            byte[] dst = new byte[data.length];
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), dst),
                totalSize
            );
            assertEquals(dst, data);
            if (readSize >= 0 && readSize <= totalSize) {
                dst = new byte[data.length];
                assertEquals(
                    reader.readTo(new ByteArrayInputStream(data), dst, 0, readSize),
                    readSize
                );
                assertEquals(
                    Arrays.copyOf(dst, readSize),
                    Arrays.copyOf(data, readSize)
                );
                if (readSize <= totalSize - 1) {
                    dst = new byte[data.length];
                    assertEquals(
                        reader.readTo(new ByteArrayInputStream(data), dst, 1, readSize),
                        readSize
                    );
                    assertEquals(
                        Arrays.copyOfRange(dst, 1, 1 + readSize),
                        Arrays.copyOf(data, readSize)
                    );
                }
            }
            if (readSize > totalSize) {
                dst = new byte[readSize];
                assertEquals(
                    reader.readTo(new ByteArrayInputStream(data), dst, 0, readSize),
                    totalSize
                );
                assertEquals(
                    Arrays.copyOf(dst, totalSize),
                    data
                );
                dst = new byte[readSize + 1];
                assertEquals(
                    reader.readTo(new ByteArrayInputStream(data), dst, 1, readSize),
                    totalSize
                );
                assertEquals(
                    Arrays.copyOfRange(dst, 1, 1 + totalSize),
                    data
                );
            }
        }
        {
            // stream to heap buffer
            byte[] data = randomBytes(totalSize);
            ByteBuffer dst = ByteBuffer.allocate(data.length);
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), dst),
                totalSize
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(data));
            dst = ByteBuffer.allocate(data.length);
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), dst, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            ));
        }
        {
            // stream to direct buffer
            byte[] data = randomBytes(totalSize);
            ByteBuffer dst = ByteBuffer.allocateDirect(data.length);
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), dst),
                totalSize
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(data));
            dst = ByteBuffer.allocateDirect(data.length);
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), dst, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            ));
        }
        {
            // channel to channel
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder)),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(
                    Channels.newChannel(new ByteArrayInputStream(data)),
                    Channels.newChannel(builder),
                    readSize < 0 ? totalSize : readSize
                ),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            // write one byte channel
            WritableByteChannel wch = new OneByteWriteableChannel(builder);
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), wch),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), wch, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new OneByteInputStream(data)), wch),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new OneByteInputStream(data)), wch, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
        }
        {
            // channel to stream
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), builder),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(
                    Channels.newChannel(new ByteArrayInputStream(data)),
                    builder,
                    readSize < 0 ? totalSize : readSize
                ),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
        }
        {
            // channel to array
            byte[] data = randomBytes(totalSize);
            byte[] dst = new byte[data.length];
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst),
                totalSize
            );
            assertEquals(dst, data);
            if (readSize >= 0 && readSize <= totalSize) {
                dst = new byte[data.length];
                assertEquals(
                    reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 0, readSize),
                    readSize
                );
                assertEquals(
                    Arrays.copyOf(dst, readSize),
                    Arrays.copyOf(data, readSize)
                );
                if (readSize <= totalSize - 1) {
                    dst = new byte[data.length];
                    assertEquals(
                        reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 1, readSize),
                        readSize
                    );
                    assertEquals(
                        Arrays.copyOfRange(dst, 1, 1 + readSize),
                        Arrays.copyOf(data, readSize)
                    );
                }
            }
            if (readSize > totalSize) {
                dst = new byte[readSize];
                assertEquals(
                    reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 0, readSize),
                    totalSize
                );
                assertEquals(
                    Arrays.copyOf(dst, totalSize),
                    data
                );
                dst = new byte[readSize + 1];
                assertEquals(
                    reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 1, readSize),
                    totalSize
                );
                assertEquals(
                    Arrays.copyOfRange(dst, 1, 1 + totalSize),
                    data
                );
            }
        }
        {
            // channel to heap buffer
            byte[] data = randomBytes(totalSize);
            ByteBuffer dst = ByteBuffer.allocate(data.length);
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst),
                totalSize
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(data));
            dst = ByteBuffer.allocate(data.length);
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            ));
        }
        {
            // channel to direct buffer
            byte[] data = randomBytes(totalSize);
            ByteBuffer dst = ByteBuffer.allocateDirect(data.length);
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst),
                totalSize
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(data));
            dst = ByteBuffer.allocateDirect(data.length);
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(dst.flip(), ByteBuffer.wrap(
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            ));
        }
    }

    @Test
    public void testReadChars() throws Exception {
        testReadChars(64);
        testReadChars(128);
        testReadChars(256);
        testReadChars(1024);
        testReadChars(IOKit.bufferSize());
        testReadChars(IOKit.bufferSize() - 1);
        testReadChars(IOKit.bufferSize() + 1);
        testReadChars(IOKit.bufferSize() - 5);
        testReadChars(IOKit.bufferSize() + 5);
        testReadChars(IOKit.bufferSize() * 2);
        testReadChars(IOKit.bufferSize() * 2 - 1);
        testReadChars(IOKit.bufferSize() * 2 + 1);
        testReadChars(IOKit.bufferSize() * 2 - 5);
        testReadChars(IOKit.bufferSize() * 2 + 5);
        testReadChars(IOKit.bufferSize() * 3);
        testReadChars(IOKit.bufferSize() * 3 - 1);
        testReadChars(IOKit.bufferSize() * 3 + 1);
        testReadChars(IOKit.bufferSize() * 3 - 5);
        testReadChars(IOKit.bufferSize() * 3 + 5);

        {
            // read stream
            assertNull(IOKit.read(new CharArrayReader(new char[0])));
            assertNull(IOKit.read(new CharArrayReader(new char[0]), 66));
            assertNull(IOKit.string(new CharArrayReader(new char[0])));
            assertNull(IOKit.string(new CharArrayReader(new char[0]), 66));
        }

        {
            // error
            TestReader tin = new TestReader(new CharArrayReader(new char[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> IOKit.read(tin));
            expectThrows(IORuntimeException.class, () -> IOKit.read(tin, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.read(tin, -1));
        }
    }

    private void testReadChars(int totalSize) throws Exception {
        testReadChars(IOOperator.get(IOKit.bufferSize()), totalSize);
        testReadChars(IOOperator.get(1), totalSize);
        testReadChars(IOOperator.get(2), totalSize);
        testReadChars(IOOperator.get(IOKit.bufferSize() - 1), totalSize);
        testReadChars(IOOperator.get(IOKit.bufferSize() + 1), totalSize);
        testReadChars(IOOperator.get(IOKit.bufferSize() * 2), totalSize);
    }

    private void testReadChars(IOOperator reader, int totalSize) throws Exception {
        testReadChars(reader, totalSize, totalSize);
        testReadChars(reader, totalSize, 0);
        testReadChars(reader, totalSize, 1);
        testReadChars(reader, totalSize, totalSize / 2);
        testReadChars(reader, totalSize, totalSize - 1);
        testReadChars(reader, totalSize, totalSize + 1);
        testReadChars(reader, totalSize, totalSize * 2);
    }

    private void testReadChars(IOOperator reader, int totalSize, int readSize) throws Exception {
        {
            // reader
            char[] data = randomChars(totalSize);
            assertEquals(reader.read(new CharArrayReader(data)), data);
            assertEquals(reader.string(new CharArrayReader(data)), new String(data));
            assertEquals(
                reader.read(new CharArrayReader(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertEquals(
                reader.string(new CharArrayReader(data), readSize < 0 ? totalSize : readSize),
                new String((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
            );
            assertEquals(reader.read(new OneCharReader(data)), data);
            assertEquals(reader.string(new OneCharReader(data)), new String(data));
            assertEquals(
                reader.read(new OneCharReader(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertEquals(
                reader.string(new OneCharReader(data), readSize < 0 ? totalSize : readSize),
                new String((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
            );
        }
    }

    @Test
    public void testReadCharsTo() throws Exception {
        testReadCharsTo(64);
        testReadCharsTo(128);
        testReadCharsTo(256);
        testReadCharsTo(1024);
        testReadCharsTo(IOKit.bufferSize());
        testReadCharsTo(IOKit.bufferSize() - 1);
        testReadCharsTo(IOKit.bufferSize() + 1);
        testReadCharsTo(IOKit.bufferSize() - 5);
        testReadCharsTo(IOKit.bufferSize() + 5);
        testReadCharsTo(IOKit.bufferSize() * 2);
        testReadCharsTo(IOKit.bufferSize() * 2 - 1);
        testReadCharsTo(IOKit.bufferSize() * 2 + 1);
        testReadCharsTo(IOKit.bufferSize() * 2 - 5);
        testReadCharsTo(IOKit.bufferSize() * 2 + 5);
        testReadCharsTo(IOKit.bufferSize() * 3);
        testReadCharsTo(IOKit.bufferSize() * 3 - 1);
        testReadCharsTo(IOKit.bufferSize() * 3 + 1);
        testReadCharsTo(IOKit.bufferSize() * 3 - 5);
        testReadCharsTo(IOKit.bufferSize() * 3 + 5);

        {
            // size 0: reader to appender
            char[] data = new char[0];
            CharsBuilder bb = new CharsBuilder();
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), bb),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), bb, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), bb, 11),
                -1
            );
            assertEquals(bb.size(), 0);
        }
        {
            // size 0: reader to array
            char[] data = new char[0];
            char[] aar = new char[64];
            Arrays.fill(aar, (char) 7);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), aar),
                -1
            );
            assertEquals(aar[0], (char) 7);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), new char[0]),
                0
            );
            assertEquals(aar[0], (char) 7);
        }
        {
            // size 0: reader to heap buffer
            char[] data = new char[0];
            CharBuffer dst1 = CharBuffer.allocate(1);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), CharBuffer.allocate(0)),
                0
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst1, 1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst1, 0),
                0
            );
            assertEquals(dst1.position(), 0);
            CharBuffer dst2 = CharBuffer.allocate(0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst2),
                0
            );
            assertEquals(dst2.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst2, 1),
                0
            );
            assertEquals(dst2.position(), 0);
        }
        {
            // size 0: reader to direct buffer
            char[] data = new char[0];
            CharBuffer dst1 = BufferKit.directCharBuffer(1);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst1, 1),
                -1
            );
            assertEquals(dst1.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst1, 0),
                0
            );
            assertEquals(dst1.position(), 0);
            CharBuffer dst2 = BufferKit.directCharBuffer(0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst2),
                0
            );
            assertEquals(dst2.position(), 0);
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst2, 1),
                0
            );
            assertEquals(dst2.position(), 0);
        }

        {
            // error
            TestReader tin = new TestReader(new CharArrayReader(new char[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            ErrorAppender errOut = new ErrorAppender();
            // read stream
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut, 1));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errOut, -1));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, new char[1], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new char[0], 1, 0));
            expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new char[0], 0, 1));
            expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, CharBuffer.allocate(1)));
            expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, CharBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                IOKit.readTo(new CharArrayReader(new char[1]), CharBuffer.allocate(1).asReadOnlyBuffer())
            );
        }
    }

    private void testReadCharsTo(int totalSize) throws Exception {
        testReadCharsTo(IOOperator.get(IOKit.bufferSize()), totalSize);
        testReadCharsTo(IOOperator.get(1), totalSize);
        testReadCharsTo(IOOperator.get(2), totalSize);
        testReadCharsTo(IOOperator.get(IOKit.bufferSize() - 1), totalSize);
        testReadCharsTo(IOOperator.get(IOKit.bufferSize() + 1), totalSize);
        testReadCharsTo(IOOperator.get(IOKit.bufferSize() * 2), totalSize);
    }

    private void testReadCharsTo(IOOperator reader, int totalSize) throws Exception {
        testReadCharsTo(reader, totalSize, totalSize);
        testReadCharsTo(reader, totalSize, 0);
        testReadCharsTo(reader, totalSize, 1);
        testReadCharsTo(reader, totalSize, totalSize / 2);
        testReadCharsTo(reader, totalSize, totalSize - 1);
        testReadCharsTo(reader, totalSize, totalSize + 1);
        testReadCharsTo(reader, totalSize, totalSize * 2);
    }

    private void testReadCharsTo(IOOperator reader, int totalSize, int readSize) throws Exception {
        {
            // reader to appender
            char[] data = randomChars(totalSize);
            CharsBuilder builder = new CharsBuilder();
            assertEquals(
                reader.readTo(new CharArrayReader(data), builder),
                totalSize
            );
            assertEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new CharArrayReader(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toCharArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneCharReader(data), builder),
                totalSize
            );
            assertEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneCharReader(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toCharArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
        }
        {
            // reader to array
            char[] data = randomChars(totalSize);
            char[] dst = new char[data.length];
            assertEquals(
                reader.readTo(new CharArrayReader(data), dst),
                totalSize
            );
            assertEquals(dst, data);
            if (readSize >= 0 && readSize <= totalSize) {
                dst = new char[data.length];
                assertEquals(
                    reader.readTo(new CharArrayReader(data), dst, 0, readSize),
                    readSize
                );
                assertEquals(
                    Arrays.copyOf(dst, readSize),
                    Arrays.copyOf(data, readSize)
                );
                if (readSize <= totalSize - 1) {
                    dst = new char[data.length];
                    assertEquals(
                        reader.readTo(new CharArrayReader(data), dst, 1, readSize),
                        readSize
                    );
                    assertEquals(
                        Arrays.copyOfRange(dst, 1, 1 + readSize),
                        Arrays.copyOf(data, readSize)
                    );
                }
            }
            if (readSize > totalSize) {
                dst = new char[readSize];
                assertEquals(
                    reader.readTo(new CharArrayReader(data), dst, 0, readSize),
                    totalSize
                );
                assertEquals(
                    Arrays.copyOf(dst, totalSize),
                    data
                );
                dst = new char[readSize + 1];
                assertEquals(
                    reader.readTo(new CharArrayReader(data), dst, 1, readSize),
                    totalSize
                );
                assertEquals(
                    Arrays.copyOfRange(dst, 1, 1 + totalSize),
                    data
                );
            }
        }
        {
            // reader to heap buffer
            char[] data = randomChars(totalSize);
            CharBuffer dst = CharBuffer.allocate(data.length);
            assertEquals(
                reader.readTo(new CharArrayReader(data), dst),
                totalSize
            );
            assertEquals(dst.flip(), CharBuffer.wrap(data));
            dst = CharBuffer.allocate(data.length);
            assertEquals(
                reader.readTo(new CharArrayReader(data), dst, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(dst.flip(), CharBuffer.wrap(
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            ));
        }
        {
            // reader to direct buffer
            char[] data = randomChars(totalSize);
            CharBuffer dst = BufferKit.directCharBuffer(data.length * 2);
            assertEquals(
                reader.readTo(new CharArrayReader(data), dst),
                totalSize
            );
            assertEquals(dst.flip(), CharBuffer.wrap(data));
            dst = BufferKit.directCharBuffer(data.length * 2);
            assertEquals(
                reader.readTo(new CharArrayReader(data), dst, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(dst.flip(), CharBuffer.wrap(
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            ));
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

    @Test
    public void testOther() {
        {
            // get operator
            assertSame(IOOperator.get(IOKit.bufferSize()), IOOperator.get(IOKit.bufferSize()));
            assertEquals(IOOperator.newOperator(666).bufferSize(), 666);
        }
        {
            // error
            expectThrows(IllegalArgumentException.class, () -> IOOperator.newOperator(0));
            expectThrows(IllegalArgumentException.class, () -> IOOperator.newOperator(-1));
        }
    }
}
