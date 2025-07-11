package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.io.ByteIO;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ByteIOTest implements DataTest {

    @Test
    public void testRead() throws Exception {
        testRead(64);
        testRead(128);
        testRead(256);
        testRead(1024);
        testRead(IOKit.bufferSize());
        testRead(IOKit.bufferSize() - 1);
        testRead(IOKit.bufferSize() + 1);
        testRead(IOKit.bufferSize() - 5);
        testRead(IOKit.bufferSize() + 5);
        testRead(IOKit.bufferSize() * 2);
        testRead(IOKit.bufferSize() * 2 - 1);
        testRead(IOKit.bufferSize() * 2 + 1);
        testRead(IOKit.bufferSize() * 2 - 5);
        testRead(IOKit.bufferSize() * 2 + 5);
        testRead(IOKit.bufferSize() * 3);
        testRead(IOKit.bufferSize() * 3 - 1);
        testRead(IOKit.bufferSize() * 3 + 1);
        testRead(IOKit.bufferSize() * 3 - 5);
        testRead(IOKit.bufferSize() * 3 + 5);

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

    private void testRead(int totalSize) throws Exception {
        testRead(ByteIO.get(IOKit.bufferSize()), totalSize);
        testRead(ByteIO.get(1), totalSize);
        testRead(ByteIO.get(2), totalSize);
        testRead(ByteIO.get(IOKit.bufferSize() - 1), totalSize);
        testRead(ByteIO.get(IOKit.bufferSize() + 1), totalSize);
        testRead(ByteIO.get(IOKit.bufferSize() * 2), totalSize);
    }

    private void testRead(ByteIO reader, int totalSize) throws Exception {
        testRead(reader, totalSize, totalSize);
        testRead(reader, totalSize, 0);
        testRead(reader, totalSize, 1);
        testRead(reader, totalSize, totalSize / 2);
        testRead(reader, totalSize, totalSize - 1);
        testRead(reader, totalSize, totalSize + 1);
        testRead(reader, totalSize, totalSize * 2);
    }

    private void testRead(ByteIO reader, int totalSize, int readSize) throws Exception {
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
    public void testReadTo() throws Exception {
        testReadTo(64);
        testReadTo(128);
        testReadTo(256);
        testReadTo(1024);
        testReadTo(IOKit.bufferSize());
        testReadTo(IOKit.bufferSize() - 1);
        testReadTo(IOKit.bufferSize() + 1);
        testReadTo(IOKit.bufferSize() - 5);
        testReadTo(IOKit.bufferSize() + 5);
        testReadTo(IOKit.bufferSize() * 2);
        testReadTo(IOKit.bufferSize() * 2 - 1);
        testReadTo(IOKit.bufferSize() * 2 + 1);
        testReadTo(IOKit.bufferSize() * 2 - 5);
        testReadTo(IOKit.bufferSize() * 2 + 5);
        testReadTo(IOKit.bufferSize() * 3);
        testReadTo(IOKit.bufferSize() * 3 - 1);
        testReadTo(IOKit.bufferSize() * 3 + 1);
        testReadTo(IOKit.bufferSize() * 3 - 5);
        testReadTo(IOKit.bufferSize() * 3 + 5);

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
            ByteBuffer buf = ByteBuffer.allocate(1);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), ByteBuffer.allocate(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
        }
        {
            // size 0: stream to direct buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocateDirect(1);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), ByteBuffer.allocateDirect(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
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
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), ByteBuffer.allocate(0)),
                0
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
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), ByteBuffer.allocateDirect(0)),
                0
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
            expectThrows(IORuntimeException.class, () ->
                IOKit.readTo(tin, errCh)
            );
            expectThrows(IllegalArgumentException.class, () ->
                IOKit.readTo(tin, errCh, -1)
            );
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
            expectThrows(IORuntimeException.class, () ->
                IOKit.readTo(tch, errOut)
            );
            expectThrows(IllegalArgumentException.class, () ->
                IOKit.readTo(tch, errOut, -1)
            );
        }
    }

    private void testReadTo(int totalSize) throws Exception {
        testReadTo(ByteIO.get(IOKit.bufferSize()), totalSize);
        testReadTo(ByteIO.get(1), totalSize);
        testReadTo(ByteIO.get(2), totalSize);
        testReadTo(ByteIO.get(IOKit.bufferSize() - 1), totalSize);
        testReadTo(ByteIO.get(IOKit.bufferSize() + 1), totalSize);
        testReadTo(ByteIO.get(IOKit.bufferSize() * 2), totalSize);
    }

    private void testReadTo(ByteIO reader, int totalSize) throws Exception {
        testReadTo(reader, totalSize, totalSize);
        testReadTo(reader, totalSize, 0);
        testReadTo(reader, totalSize, 1);
        testReadTo(reader, totalSize, totalSize / 2);
        testReadTo(reader, totalSize, totalSize - 1);
        testReadTo(reader, totalSize, totalSize + 1);
        testReadTo(reader, totalSize, totalSize * 2);
    }

    private void testReadTo(ByteIO reader, int totalSize, int readSize) throws Exception {
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
            assertSame(ByteIO.defaultOperator(), ByteIO.get(IOKit.bufferSize()));
            assertEquals(ByteIO.newOperator(666).bufferSize(), 666);
        }
        {
            // error
            expectThrows(IllegalArgumentException.class, () -> ByteIO.newOperator(0));
            expectThrows(IllegalArgumentException.class, () -> ByteIO.newOperator(-1));
        }
    }
}
