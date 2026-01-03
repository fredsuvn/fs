package tests.io;

import internal.test.DataTest;
import internal.test.ErrorAppender;
import internal.test.ErrorOutputStream;
import internal.test.ReadOps;
import internal.test.TestInputStream;
import internal.test.TestReader;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.bytes.BytesBuilder;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.io.BufferKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.io.IORuntimeException;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
            assertThrows(IORuntimeException.class, () -> IOKit.read(tin));
            assertThrows(IORuntimeException.class, () -> IOKit.read(tin, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.read(tin, -1));
            assertThrows(IORuntimeException.class, () -> IOKit.read(Channels.newChannel(tin)));
            assertThrows(IORuntimeException.class, () -> IOKit.read(Channels.newChannel(tin), 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.read(Channels.newChannel(tin), -1));
        }
    }

    private void testReadBytes(int totalSize) throws Exception {
        testReadBytes(IOOperator.defaultOperator(), totalSize);
        // testReadBytes(IOOperator.get(IOKit.bufferSize()), totalSize);
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
            assertArrayEquals(reader.read(new ByteArrayInputStream(data)), data);
            assertArrayEquals(
                reader.read(new ByteArrayInputStream(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertArrayEquals(reader.read(new OneByteInputStream(data)), data);
            assertArrayEquals(
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
        testReadBytesTo(IOKit.bufferSize());
        testReadBytesTo(IOKit.bufferSize() - 1);
        testReadBytesTo(IOKit.bufferSize() + 1);
        testReadBytesTo(IOKit.bufferSize() - 5);
        testReadBytesTo(IOKit.bufferSize() + 5);
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
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), bb)
            );
            assertEquals(0, bb.size());
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), bb, 0)
            );
            assertEquals(0, bb.size());
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), bb, 11)
            );
            assertEquals(0, bb.size());
        }
        {
            // size 0: stream to channel
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            WritableByteChannel channel = Channels.newChannel(bb);
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), channel)
            );
            assertEquals(0, bb.size());
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), channel, 0)
            );
            assertEquals(0, bb.size());
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), channel, 11)
            );
            assertEquals(0, bb.size());
        }
        {
            // size 0: stream to array
            byte[] data = new byte[0];
            byte[] aar = new byte[64];
            Arrays.fill(aar, (byte) 7);
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), aar)
            );
            assertEquals((byte) 7, aar[0]);
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), new byte[0])
            );
            assertEquals((byte) 7, aar[0]);
        }
        {
            // size 0: stream to heap buffer
            byte[] data = new byte[0];
            ByteBuffer dst1 = ByteBuffer.allocate(1);
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), dst1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 0)
            );
            assertEquals(0, dst1.position());
            ByteBuffer dst2 = ByteBuffer.allocate(0);
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), dst2)
            );
            assertEquals(0, dst2.position());
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), dst2, 1)
            );
            assertEquals(0, dst2.position());
        }
        {
            // size 0: stream to direct buffer
            byte[] data = new byte[0];
            ByteBuffer dst1 = ByteBuffer.allocateDirect(1);
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), dst1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                -1,
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), dst1, 0)
            );
            assertEquals(0, dst1.position());
            ByteBuffer dst2 = ByteBuffer.allocateDirect(0);
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), dst2)
            );
            assertEquals(0, dst2.position());
            assertEquals(
                0,
                IOKit.readTo(new ByteArrayInputStream(data), dst2, 1)
            );
            assertEquals(0, dst2.position());
        }
        {
            // size 0: channel to channel
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb))
            );
            assertEquals(0, bb.size());
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb), 0)
            );
            assertEquals(0, bb.size());
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb), 11)
            );
            assertEquals(0, bb.size());
        }
        {
            // size 0: channel to stream
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(data));
            assertEquals(
                -1,
                IOKit.readTo(channel, bb)
            );
            assertEquals(0, bb.size());
            assertEquals(
                0,
                IOKit.readTo(channel, bb, 0)
            );
            assertEquals(0, bb.size());
            assertEquals(
                -1,
                IOKit.readTo(channel, bb, 11)
            );
            assertEquals(0, bb.size());
        }
        {
            // size 0: channel to array
            byte[] data = new byte[0];
            byte[] aar = new byte[64];
            Arrays.fill(aar, (byte) 7);
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), aar)
            );
            assertEquals((byte) 7, aar[0]);
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), new byte[0])
            );
            assertEquals((byte) 7, aar[0]);
        }
        {
            // size 0: channel to heap buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocate(1);
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf)
            );
            assertEquals(0, buf.position());
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 1)
            );
            assertEquals(0, buf.position());
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 0)
            );
            assertEquals(0, buf.position());
            ByteBuffer dst2 = ByteBuffer.allocate(0);
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2)
            );
            assertEquals(0, dst2.position());
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2, 1)
            );
            assertEquals(0, dst2.position());
        }
        {
            // size 0: channel to direct buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocateDirect(1);
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf)
            );
            assertEquals(0, buf.position());
            assertEquals(
                -1,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 1)
            );
            assertEquals(0, buf.position());
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 0)
            );
            assertEquals(0, buf.position());
            ByteBuffer dst2 = ByteBuffer.allocateDirect(0);
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2)
            );
            assertEquals(0, dst2.position());
            assertEquals(
                0,
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst2, 1)
            );
            assertEquals(0, dst2.position());
        }

        {
            // error
            ErrorOutputStream errOut = new ErrorOutputStream();
            WritableByteChannel errCh = Channels.newChannel(errOut);
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            // read stream
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errOut, -1));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, new byte[1], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new byte[0], 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new byte[0], 0, 1));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, ByteBuffer.allocate(1)));
            assertThrows(IllegalArgumentException.class, () ->
                IOKit.readTo(tin, ByteBuffer.allocate(1), -1));
            assertThrows(IORuntimeException.class, () ->
                IOKit.readTo(new ByteArrayInputStream(new byte[1]), ByteBuffer.allocate(1).asReadOnlyBuffer())
            );
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errCh));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errCh, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errCh, -1));
            // read channel
            ReadableByteChannel tch = Channels.newChannel(tin);
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errCh));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errCh, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tch, errCh, -1));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tch, new byte[1], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tch, new byte[0], 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tch, new byte[0], 0, 1));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tch, ByteBuffer.allocate(1)));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tch, ByteBuffer.allocate(1), -1));
            assertThrows(IORuntimeException.class, () ->
                IOKit.readTo(
                    Channels.newChannel(new ByteArrayInputStream(new byte[1])),
                    ByteBuffer.allocate(1).asReadOnlyBuffer()
                )
            );
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errOut));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tch, errOut, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tch, errOut, -1));
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
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), builder),
                totalSize
            );
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
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
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel),
                totalSize
            );
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            // write one byte channel
            channel = new OneByteWritableChannel(builder);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel),
                totalSize
            );
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new ByteArrayInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel),
                totalSize
            );
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneByteInputStream(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
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
            assertArrayEquals(dst, data);
            if (readSize >= 0 && readSize <= totalSize) {
                dst = new byte[data.length];
                assertEquals(
                    reader.readTo(new ByteArrayInputStream(data), dst, 0, readSize),
                    readSize
                );
                assertArrayEquals(
                    Arrays.copyOf(dst, readSize),
                    Arrays.copyOf(data, readSize)
                );
                if (readSize <= totalSize - 1) {
                    dst = new byte[data.length];
                    assertEquals(
                        reader.readTo(new ByteArrayInputStream(data), dst, 1, readSize),
                        readSize
                    );
                    assertArrayEquals(
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
                assertArrayEquals(
                    Arrays.copyOf(dst, totalSize),
                    data
                );
                dst = new byte[readSize + 1];
                assertEquals(
                    reader.readTo(new ByteArrayInputStream(data), dst, 1, readSize),
                    totalSize
                );
                assertArrayEquals(
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
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(
                    Channels.newChannel(new ByteArrayInputStream(data)),
                    Channels.newChannel(builder),
                    readSize < 0 ? totalSize : readSize
                ),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            // write one byte channel
            WritableByteChannel wch = new OneByteWritableChannel(builder);
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), wch),
                totalSize
            );
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), wch, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new OneByteInputStream(data)), wch),
                totalSize
            );
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(Channels.newChannel(new OneByteInputStream(data)), wch, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
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
            assertArrayEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(
                    Channels.newChannel(new ByteArrayInputStream(data)),
                    builder,
                    readSize < 0 ? totalSize : readSize
                ),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
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
            assertArrayEquals(dst, data);
            if (readSize >= 0 && readSize <= totalSize) {
                dst = new byte[data.length];
                assertEquals(
                    reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 0, readSize),
                    readSize
                );
                assertArrayEquals(
                    Arrays.copyOf(dst, readSize),
                    Arrays.copyOf(data, readSize)
                );
                if (readSize <= totalSize - 1) {
                    dst = new byte[data.length];
                    assertEquals(
                        reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 1, readSize),
                        readSize
                    );
                    assertArrayEquals(
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
                assertArrayEquals(
                    Arrays.copyOf(dst, totalSize),
                    data
                );
                dst = new byte[readSize + 1];
                assertEquals(
                    reader.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 1, readSize),
                    totalSize
                );
                assertArrayEquals(
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
    public void testAvailableBytes() throws Exception {
        testAvailableBytes(16);
        testAvailableBytes(32);
        {
            // read 0
            ByteArrayInputStream empty = new ByteArrayInputStream(new byte[0]);
            ReadableByteChannel emptyCh = Channels.newChannel(empty);
            assertNull(IOKit.available(empty));
            assertNull(IOKit.available(empty, 1));
            assertNull(IOKit.available(emptyCh));
            assertNull(IOKit.available(emptyCh, 1));
            BytesBuilder builder = new BytesBuilder();
            assertEquals(-1, IOKit.availableTo(empty, builder));
            assertEquals(-1, IOKit.availableTo(empty, builder, 1));
            assertEquals(-1, IOKit.availableTo(empty, Channels.newChannel(builder)));
            assertEquals(-1, IOKit.availableTo(empty, Channels.newChannel(builder), 1));
            assertEquals(-1, IOKit.availableTo(empty, new byte[1]));
            assertEquals(-1, IOKit.availableTo(empty, new byte[1], 0, 1));
            assertEquals(-1, IOKit.availableTo(empty, ByteBuffer.allocate(1)));
            assertEquals(-1, IOKit.availableTo(empty, ByteBuffer.allocate(1), 1));
            assertEquals(-1, IOKit.availableTo(emptyCh, builder));
            assertEquals(-1, IOKit.availableTo(emptyCh, builder, 1));
            assertEquals(-1, IOKit.availableTo(emptyCh, Channels.newChannel(builder)));
            assertEquals(-1, IOKit.availableTo(emptyCh, Channels.newChannel(builder), 1));
            assertEquals(-1, IOKit.availableTo(emptyCh, new byte[1]));
            assertEquals(-1, IOKit.availableTo(emptyCh, new byte[1], 0, 1));
            assertEquals(-1, IOKit.availableTo(emptyCh, ByteBuffer.allocate(1)));
            assertEquals(-1, IOKit.availableTo(emptyCh, ByteBuffer.allocate(1), 1));
            assertEquals(0, IOKit.availableTo(empty, ByteBuffer.allocate(0)));
            assertEquals(0, IOKit.availableTo(empty, ByteBuffer.allocate(0), 1));
            assertEquals(0, IOKit.availableTo(empty, ByteBuffer.allocate(1), 0));
            assertEquals(0, IOKit.availableTo(emptyCh, ByteBuffer.allocate(0)));
            assertEquals(0, IOKit.availableTo(emptyCh, ByteBuffer.allocate(0), 1));
            assertEquals(0, IOKit.availableTo(emptyCh, ByteBuffer.allocate(1), 0));
        }
    }

    public void testAvailableBytes(int size) throws Exception {
        byte[] src = randomBytes(size);
        byte[] empty = new byte[0];
        ByteBuffer emptyBuf = ByteBuffer.allocate(0);

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
            public int read(byte @Nonnull [] b, int off, int len) {
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

        {
            // available bytes
            In in1 = new In();
            BytesBuilder builder = new BytesBuilder();
            byte[] b1 = IOKit.available(in1);
            assertArrayEquals(b1, empty);
            while (true) {
                byte[] b = IOKit.available(in1);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            In in2 = new In();
            byte[] b2 = IOKit.available(in2, size * 2);
            assertArrayEquals(b2, empty);
            while (true) {
                byte[] b = IOKit.available(in2);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            ReadableByteChannel c3 = new Cin();
            ByteBuffer b3 = IOKit.available(c3);
            assertEquals(b3, emptyBuf);
            while (true) {
                ByteBuffer b = IOKit.available(c3);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            ReadableByteChannel c4 = new Cin();
            ByteBuffer b4 = IOKit.available(c4, size * 2);
            assertEquals(b4, emptyBuf);
            while (true) {
                ByteBuffer b = IOKit.available(c4);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
        }
        {
            // available to OutputStream
            In in1 = new In();
            BytesBuilder builder = new BytesBuilder();
            assertEquals(0, IOKit.availableTo(in1, builder));
            while (true) {
                long readSize = IOKit.availableTo(in1, builder);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            In in2 = new In();
            assertEquals(0, IOKit.availableTo(in2, builder, size * 2L));
            while (true) {
                long readSize = IOKit.availableTo(in2, builder, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            ReadableByteChannel c3 = new Cin();
            assertEquals(0, IOKit.availableTo(c3, builder));
            while (true) {
                long readSize = IOKit.availableTo(c3, builder);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            ReadableByteChannel c4 = new Cin();
            assertEquals(0, IOKit.availableTo(c4, builder, size * 2L));
            while (true) {
                long readSize = IOKit.availableTo(c4, builder, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
        }
        {
            // available to Channel
            In in1 = new In();
            BytesBuilder builder = new BytesBuilder();
            WritableByteChannel outChannel = Channels.newChannel(builder);
            assertEquals(0, IOKit.availableTo(in1, builder));
            while (true) {
                long readSize = IOKit.availableTo(in1, outChannel);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            In in2 = new In();
            assertEquals(0, IOKit.availableTo(in2, builder, size * 2L));
            while (true) {
                long readSize = IOKit.availableTo(in2, outChannel, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            ReadableByteChannel c3 = new Cin();
            assertEquals(0, IOKit.availableTo(c3, builder));
            while (true) {
                long readSize = IOKit.availableTo(c3, outChannel);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
            ReadableByteChannel c4 = new Cin();
            assertEquals(0, IOKit.availableTo(c4, builder, size * 2L));
            while (true) {
                long readSize = IOKit.availableTo(c4, outChannel, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toByteArray(), src);
            builder.reset();
        }
        {
            // available to array
            In in1 = new In();
            byte[] dst = new byte[size * 2];
            int c = 0;
            assertEquals(0, IOKit.availableTo(in1, dst));
            while (c < size) {
                long readSize = IOKit.availableTo(in1, dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertArrayEquals(Arrays.copyOf(dst, size), src);
            dst = new byte[size * 2];
            c = 0;
            Cin c1 = new Cin();
            assertEquals(0, IOKit.availableTo(c1, dst));
            while (c < size) {
                long readSize = IOKit.availableTo(c1, dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertArrayEquals(Arrays.copyOf(dst, size), src);
        }
        {
            // available to buffer
            In in1 = new In();
            ByteBuffer dst = ByteBuffer.allocate(size * 2);
            int c = 0;
            assertEquals(0, IOKit.availableTo(in1, dst));
            while (c < size) {
                long readSize = IOKit.availableTo(in1, dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertArrayEquals(BufferKit.read(dst), src);
            dst = ByteBuffer.allocate(size * 2);
            c = 0;
            Cin c1 = new Cin();
            assertEquals(0, IOKit.availableTo(c1, dst));
            while (c < size) {
                long readSize = IOKit.availableTo(c1, dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertArrayEquals(BufferKit.read(dst), src);
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
            assertThrows(IORuntimeException.class, () -> IOKit.read(tin));
            assertThrows(IORuntimeException.class, () -> IOKit.read(tin, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.read(tin, -1));
        }
    }

    private void testReadChars(int totalSize) throws Exception {
        testReadChars(IOOperator.defaultOperator(), totalSize);
        // testReadChars(IOOperator.get(IOKit.bufferSize()), totalSize);
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
            assertArrayEquals(reader.read(new CharArrayReader(data)), data);
            assertEquals(reader.string(new CharArrayReader(data)), new String(data));
            assertArrayEquals(
                reader.read(new CharArrayReader(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertEquals(
                reader.string(new CharArrayReader(data), readSize < 0 ? totalSize : readSize),
                new String((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
            );
            assertArrayEquals(reader.read(new OneCharReader(data)), data);
            assertEquals(reader.string(new OneCharReader(data)), new String(data));
            assertArrayEquals(
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
        testReadCharsTo(IOKit.bufferSize());
        testReadCharsTo(IOKit.bufferSize() - 1);
        testReadCharsTo(IOKit.bufferSize() + 1);
        testReadCharsTo(IOKit.bufferSize() - 5);
        testReadCharsTo(IOKit.bufferSize() + 5);
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
                -1,
                IOKit.readTo(new CharArrayReader(data), bb)
            );
            assertEquals(0, bb.size());
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), bb, 0)
            );
            assertEquals(0, bb.size());
            assertEquals(
                -1,
                IOKit.readTo(new CharArrayReader(data), bb, 11)
            );
            assertEquals(0, bb.size());
        }
        {
            // size 0: reader to array
            char[] data = new char[0];
            char[] aar = new char[64];
            Arrays.fill(aar, (char) 7);
            assertEquals(
                -1,
                IOKit.readTo(new CharArrayReader(data), aar)
            );
            assertEquals((char) 7, aar[0]);
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), new char[0])
            );
            assertEquals((char) 7, aar[0]);
        }
        {
            // size 0: reader to heap buffer
            char[] data = new char[0];
            CharBuffer dst1 = CharBuffer.allocate(1);
            assertEquals(
                -1,
                IOKit.readTo(new CharArrayReader(data), dst1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), CharBuffer.allocate(0))
            );
            assertEquals(0, dst1.position());
            assertEquals(
                -1,
                IOKit.readTo(new CharArrayReader(data), dst1, 1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), dst1, 0)
            );
            assertEquals(0, dst1.position());
            CharBuffer dst2 = CharBuffer.allocate(0);
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), dst2)
            );
            assertEquals(0, dst2.position());
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), dst2, 1)
            );
            assertEquals(0, dst2.position());
        }
        {
            // size 0: reader to direct buffer
            char[] data = new char[0];
            CharBuffer dst1 = BufferKit.directCharBuffer(1);
            assertEquals(
                -1,
                IOKit.readTo(new CharArrayReader(data), dst1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                -1,
                IOKit.readTo(new CharArrayReader(data), dst1, 1)
            );
            assertEquals(0, dst1.position());
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), dst1, 0)
            );
            assertEquals(0, dst1.position());
            CharBuffer dst2 = BufferKit.directCharBuffer(0);
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), dst2)
            );
            assertEquals(0, dst2.position());
            assertEquals(
                0,
                IOKit.readTo(new CharArrayReader(data), dst2, 1)
            );
            assertEquals(0, dst2.position());
        }

        {
            // error
            TestReader tin = new TestReader(new CharArrayReader(new char[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            ErrorAppender errOut = new ErrorAppender();
            // read stream
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut, 1));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errOut, -1));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, new char[1], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new char[0], 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new char[0], 0, 1));
            assertThrows(IORuntimeException.class, () -> IOKit.readTo(tin, CharBuffer.allocate(1)));
            assertThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, CharBuffer.allocate(1), -1));
            assertThrows(IORuntimeException.class, () ->
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
            assertArrayEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new CharArrayReader(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
                builder.toCharArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
            assertEquals(
                reader.readTo(new OneCharReader(data), builder),
                totalSize
            );
            assertArrayEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneCharReader(data), builder, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertArrayEquals(
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
            assertArrayEquals(dst, data);
            if (readSize >= 0 && readSize <= totalSize) {
                dst = new char[data.length];
                assertEquals(
                    reader.readTo(new CharArrayReader(data), dst, 0, readSize),
                    readSize
                );
                assertArrayEquals(
                    Arrays.copyOf(dst, readSize),
                    Arrays.copyOf(data, readSize)
                );
                if (readSize <= totalSize - 1) {
                    dst = new char[data.length];
                    assertEquals(
                        reader.readTo(new CharArrayReader(data), dst, 1, readSize),
                        readSize
                    );
                    assertArrayEquals(
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
                assertArrayEquals(
                    Arrays.copyOf(dst, totalSize),
                    data
                );
                dst = new char[readSize + 1];
                assertEquals(
                    reader.readTo(new CharArrayReader(data), dst, 1, readSize),
                    totalSize
                );
                assertArrayEquals(
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

    @Test
    public void testAvailableChars() throws Exception {
        testAvailableChars(16);
        testAvailableChars(32);
        {
            // read 0
            CharArrayReader empty = new CharArrayReader(new char[0]);
            assertNull(IOKit.available(empty));
            assertNull(IOKit.available(empty, 1));
            assertNull(IOKit.availableString(empty));
            assertNull(IOKit.availableString(empty, 1));
            CharsBuilder builder = new CharsBuilder();
            assertEquals(-1, IOKit.availableTo(empty, builder));
            assertEquals(-1, IOKit.availableTo(empty, builder, 1));
            assertEquals(-1, IOKit.availableTo(empty, new char[1]));
            assertEquals(-1, IOKit.availableTo(empty, new char[1], 0, 1));
            assertEquals(-1, IOKit.availableTo(empty, CharBuffer.allocate(1)));
            assertEquals(-1, IOKit.availableTo(empty, CharBuffer.allocate(1), 1));
            assertEquals(0, IOKit.availableTo(empty, CharBuffer.allocate(0)));
            assertEquals(0, IOKit.availableTo(empty, CharBuffer.allocate(0), 1));
            assertEquals(0, IOKit.availableTo(empty, CharBuffer.allocate(1), 0));
        }
    }

    public void testAvailableChars(int size) throws Exception {
        char[] src = randomChars(size);
        char[] empty = new char[0];
        String emptyStr = "";
        CharBuffer emptyBuf = CharBuffer.allocate(0);

        class In extends Reader {

            private final char[] data = src;
            private int pos = 0;
            private boolean zero = true;

            private int available() {
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
            public int read(char @Nonnull [] b, int off, int len) {
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

            @Override
            public void close() {
            }
        }

        {
            // available chars
            In in1 = new In();
            CharsBuilder builder = new CharsBuilder();
            char[] s1 = IOKit.available(in1);
            assertArrayEquals(s1, empty);
            while (true) {
                char[] b = IOKit.available(in1);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toCharArray(), src);
            builder.reset();
            In in2 = new In();
            String s2 = IOKit.availableString(in2);
            assertEquals(emptyStr, s2);
            while (true) {
                String b = IOKit.availableString(in2);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toCharArray(), src);
            builder.reset();
            In in3 = new In();
            char[] s3 = IOKit.available(in3, size * 2);
            assertArrayEquals(s3, empty);
            while (true) {
                char[] b = IOKit.available(in3);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toCharArray(), src);
            builder.reset();
            In in4 = new In();
            String s4 = IOKit.availableString(in4, size * 2);
            assertEquals(emptyStr, s4);
            while (true) {
                String b = IOKit.availableString(in4);
                if (b == null) {
                    break;
                }
                builder.append(b);
            }
            assertArrayEquals(builder.toCharArray(), src);
            builder.reset();
        }
        {
            // available to Appender
            In in1 = new In();
            CharsBuilder builder = new CharsBuilder();
            assertEquals(0, IOKit.availableTo(in1, builder));
            while (true) {
                long readSize = IOKit.availableTo(in1, builder);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toCharArray(), src);
            builder.reset();
            In in2 = new In();
            assertEquals(0, IOKit.availableTo(in2, builder, size * 2L));
            while (true) {
                long readSize = IOKit.availableTo(in2, builder, size * 2L);
                if (readSize < 0) {
                    break;
                }
            }
            assertArrayEquals(builder.toCharArray(), src);
            builder.reset();
        }
        {
            // available to array
            In in1 = new In();
            char[] dst = new char[size * 2];
            int c = 0;
            assertEquals(0, IOKit.availableTo(in1, dst));
            while (c < size) {
                long readSize = IOKit.availableTo(in1, dst, c, size - c);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            assertArrayEquals(Arrays.copyOf(dst, size), src);
        }
        {
            // available to buffer
            In in1 = new In();
            CharBuffer dst = CharBuffer.allocate(size * 2);
            int c = 0;
            assertEquals(0, IOKit.availableTo(in1, dst));
            while (c < size) {
                long readSize = IOKit.availableTo(in1, dst, size * 2);
                if (readSize < 0) {
                    break;
                }
                c += (int) readSize;
            }
            dst.flip();
            assertArrayEquals(BufferKit.read(dst), src);
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
            assertEquals(666, IOOperator.newOperator(666).bufferSize());
        }
        {
            // error
            assertThrows(IllegalArgumentException.class, () -> IOOperator.newOperator(0));
            assertThrows(IllegalArgumentException.class, () -> IOOperator.newOperator(-1));
        }
    }
}
