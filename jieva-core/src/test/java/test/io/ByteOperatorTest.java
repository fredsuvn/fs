package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.io.ByteOperator;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ByteOperatorTest {

    @Test
    public void testRead() throws Exception {
        testRead(64);
        testRead(128);
        testRead(256);
        testRead(1024);
        testRead(JieIO.bufferSize());
        testRead(JieIO.bufferSize() - 1);
        testRead(JieIO.bufferSize() + 1);
        testRead(JieIO.bufferSize() - 5);
        testRead(JieIO.bufferSize() + 5);
        testRead(JieIO.bufferSize() * 2);
        testRead(JieIO.bufferSize() * 2 - 1);
        testRead(JieIO.bufferSize() * 2 + 1);
        testRead(JieIO.bufferSize() * 2 - 5);
        testRead(JieIO.bufferSize() * 2 + 5);
        testRead(JieIO.bufferSize() * 3);
        testRead(JieIO.bufferSize() * 3 - 1);
        testRead(JieIO.bufferSize() * 3 + 1);
        testRead(JieIO.bufferSize() * 3 - 5);
        testRead(JieIO.bufferSize() * 3 + 5);

        {
            // read stream
            assertNull(JieIO.read(new ByteArrayInputStream(new byte[0])));
            assertNull(JieIO.read(new ByteArrayInputStream(new byte[0]), 66));
            assertNull(JieIO.read(Channels.newChannel(new ByteArrayInputStream(new byte[0]))));
            assertNull(JieIO.read(Channels.newChannel(new ByteArrayInputStream(new byte[0])), 66));
        }

        {
            // error
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(new byte[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> JieIO.read(tin));
            expectThrows(IORuntimeException.class, () -> JieIO.read(tin, 1));
            expectThrows(IllegalArgumentException.class, () -> JieIO.read(tin, -1));
            expectThrows(IORuntimeException.class, () -> JieIO.read(Channels.newChannel(tin)));
            expectThrows(IORuntimeException.class, () -> JieIO.read(Channels.newChannel(tin), 1));
            expectThrows(IllegalArgumentException.class, () -> JieIO.read(Channels.newChannel(tin), -1));
        }
    }

    private void testRead(int totalSize) throws Exception {
        testRead(ByteOperator.get(JieIO.bufferSize()), totalSize);
        testRead(ByteOperator.get(1), totalSize);
        testRead(ByteOperator.get(2), totalSize);
        testRead(ByteOperator.get(JieIO.bufferSize() - 1), totalSize);
        testRead(ByteOperator.get(JieIO.bufferSize() + 1), totalSize);
        testRead(ByteOperator.get(JieIO.bufferSize() * 2), totalSize);
    }

    private void testRead(ByteOperator reader, int totalSize) throws Exception {
        testRead(reader, totalSize, -1);
        testRead(reader, totalSize, 0);
        testRead(reader, totalSize, 1);
        testRead(reader, totalSize, totalSize / 2);
        testRead(reader, totalSize, totalSize - 1);
        testRead(reader, totalSize, totalSize);
        testRead(reader, totalSize, totalSize + 1);
        testRead(reader, totalSize, totalSize * 2);
    }

    private void testRead(ByteOperator reader, int totalSize, int readSize) throws Exception {
        {
            // stream
            byte[] data = JieRandom.fill(new byte[totalSize]);
            assertEquals(reader.read(new ByteArrayInputStream(data)), data);
            assertEquals(
                reader.read(new ByteArrayInputStream(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertEquals(reader.read(new OneBytePerRead(data)), data);
            assertEquals(
                reader.read(new OneBytePerRead(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
        }
        {
            // channel
            byte[] data = JieRandom.fill(new byte[totalSize]);
            ByteBuffer dataBuf = ByteBuffer.wrap(data);
            assertEquals(reader.read(Channels.newChannel(new ByteArrayInputStream(data))), dataBuf);
            assertEquals(
                reader.read(Channels.newChannel(new ByteArrayInputStream(data)), readSize < 0 ? totalSize : readSize),
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
        testReadTo(JieIO.bufferSize());
        testReadTo(JieIO.bufferSize() - 1);
        testReadTo(JieIO.bufferSize() + 1);
        testReadTo(JieIO.bufferSize() - 5);
        testReadTo(JieIO.bufferSize() + 5);
        testReadTo(JieIO.bufferSize() * 2);
        testReadTo(JieIO.bufferSize() * 2 - 1);
        testReadTo(JieIO.bufferSize() * 2 + 1);
        testReadTo(JieIO.bufferSize() * 2 - 5);
        testReadTo(JieIO.bufferSize() * 2 + 5);
        testReadTo(JieIO.bufferSize() * 3);
        testReadTo(JieIO.bufferSize() * 3 - 1);
        testReadTo(JieIO.bufferSize() * 3 + 1);
        testReadTo(JieIO.bufferSize() * 3 - 5);
        testReadTo(JieIO.bufferSize() * 3 + 5);

        {
            // size 0: stream to stream
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), bb),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), bb, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), bb, 11),
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
                JieIO.readTo(new ByteArrayInputStream(data), channel),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), channel, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), channel, 11),
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
                JieIO.readTo(new ByteArrayInputStream(data), aar),
                -1
            );
            assertEquals(aar[0], (byte) 7);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), new byte[0]),
                0
            );
            assertEquals(aar[0], (byte) 7);
        }
        {
            // size 0: stream to heap buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocate(1);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), ByteBuffer.allocate(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
        }
        {
            // size 0: stream to direct buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocateDirect(1);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), ByteBuffer.allocateDirect(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
        }
        {
            // size 0: channel to channel
            byte[] data = new byte[0];
            BytesBuilder bb = new BytesBuilder();
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb)),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb), 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(bb), 11),
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
                JieIO.readTo(channel, bb),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(channel, bb, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(channel, bb, 11),
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
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), aar),
                -1
            );
            assertEquals(aar[0], (byte) 7);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), new byte[0]),
                0
            );
            assertEquals(aar[0], (byte) 7);
        }
        {
            // size 0: channel to heap buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocate(1);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), ByteBuffer.allocate(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
        }
        {
            // size 0: channel to direct buffer
            byte[] data = new byte[0];
            ByteBuffer buf = ByteBuffer.allocateDirect(1);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), ByteBuffer.allocateDirect(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), buf, 0),
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
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, errOut));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, errOut, 1));
            expectThrows(IllegalArgumentException.class, () -> JieIO.readTo(tin, errOut, -1));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, new byte[1], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> JieIO.readTo(tin, new byte[0], 1, 0));
            expectThrows(IndexOutOfBoundsException.class, () -> JieIO.readTo(tin, new byte[0], 0, 1));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, ByteBuffer.allocate(1)));
            expectThrows(IllegalArgumentException.class, () ->
                JieIO.readTo(tin, ByteBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                JieIO.readTo(new ByteArrayInputStream(new byte[1]), ByteBuffer.allocate(1).asReadOnlyBuffer())
            );
            expectThrows(IORuntimeException.class, () ->
                JieIO.readTo(tin, errCh)
            );
            expectThrows(IllegalArgumentException.class, () ->
                JieIO.readTo(tin, errCh, -1)
            );
            // read channel
            ReadableByteChannel tch = Channels.newChannel(tin);
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tch, errCh));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tch, errCh, 1));
            expectThrows(IllegalArgumentException.class, () -> JieIO.readTo(tch, errCh, -1));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tch, new byte[1], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> JieIO.readTo(tch, new byte[0], 1, 0));
            expectThrows(IndexOutOfBoundsException.class, () -> JieIO.readTo(tch, new byte[0], 0, 1));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tch, ByteBuffer.allocate(1)));
            expectThrows(IllegalArgumentException.class, () -> JieIO.readTo(tch, ByteBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                JieIO.readTo(
                    Channels.newChannel(new ByteArrayInputStream(new byte[1])),
                    ByteBuffer.allocate(1).asReadOnlyBuffer()
                )
            );
            expectThrows(IORuntimeException.class, () ->
                JieIO.readTo(tch, errOut)
            );
            expectThrows(IllegalArgumentException.class, () ->
                JieIO.readTo(tch, errOut, -1)
            );
        }
    }

    private void testReadTo(int totalSize) throws Exception {
        testReadTo(ByteOperator.get(JieIO.bufferSize()), totalSize);
        testReadTo(ByteOperator.get(1), totalSize);
        testReadTo(ByteOperator.get(2), totalSize);
        testReadTo(ByteOperator.get(JieIO.bufferSize() - 1), totalSize);
        testReadTo(ByteOperator.get(JieIO.bufferSize() + 1), totalSize);
        testReadTo(ByteOperator.get(JieIO.bufferSize() * 2), totalSize);
    }

    private void testReadTo(ByteOperator reader, int totalSize) throws Exception {
        testReadTo(reader, totalSize, -1);
        testReadTo(reader, totalSize, 0);
        testReadTo(reader, totalSize, 1);
        testReadTo(reader, totalSize, totalSize / 2);
        testReadTo(reader, totalSize, totalSize - 1);
        testReadTo(reader, totalSize, totalSize);
        testReadTo(reader, totalSize, totalSize + 1);
        testReadTo(reader, totalSize, totalSize * 2);
    }

    private void testReadTo(ByteOperator reader, int totalSize, int readSize) throws Exception {
        {
            // stream to stream
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
                reader.readTo(new OneBytePerRead(data), builder),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneBytePerRead(data), builder, readSize < 0 ? totalSize : readSize),
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
                reader.readTo(new OneBytePerRead(data), channel),
                totalSize
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneBytePerRead(data), channel, readSize < 0 ? totalSize : readSize),
                actualReadSize(totalSize, readSize)
            );
            assertEquals(
                builder.toByteArray(),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            builder.reset();
        }
        {
            // stream to array
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
        }
        {
            // channel to stream
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
            byte[] data = JieRandom.fill(new byte[totalSize]);
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
        if (readSize < 0) {
            return totalSize;
        }
        return Math.min(readSize, totalSize);
    }

    @Test
    public void testOther() {
        {
            // get operator
            assertSame(ByteOperator.defaultOperator(), ByteOperator.get(JieIO.bufferSize()));
            assertEquals(ByteOperator.newOperator(666).bufferSize(), 666);
        }
        {
            // error
            expectThrows(IllegalArgumentException.class, () -> ByteOperator.newOperator(0));
            expectThrows(IllegalArgumentException.class, () -> ByteOperator.newOperator(-1));
        }
    }

    private static class OneBytePerRead extends InputStream {

        private final byte[] data;
        private int pos = 0;

        private OneBytePerRead(byte[] data) {
            this.data = data;
        }

        @Override
        public int read() throws IOException {
            if (pos >= data.length) {
                return -1;
            }
            return data[pos++] & 0x000000ff;
        }

        @Override
        public int read(@NotNull byte[] b, int off, int len) throws IOException {
            if (pos >= data.length) {
                return -1;
            }
            b[off] = data[pos++];
            return 1;
        }
    }
}
