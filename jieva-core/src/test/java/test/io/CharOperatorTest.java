package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.io.CharOperator;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class CharOperatorTest {

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
            assertNull(JieIO.read(new CharArrayReader(new char[0])));
            assertNull(JieIO.read(new CharArrayReader(new char[0]), 66));
            assertNull(JieIO.string(new CharArrayReader(new char[0])));
            assertNull(JieIO.string(new CharArrayReader(new char[0]), 66));
        }

        {
            // error
            TestReader tin = new TestReader(new CharArrayReader(new char[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> JieIO.read(tin));
            expectThrows(IORuntimeException.class, () -> JieIO.read(tin, 1));
            expectThrows(IllegalArgumentException.class, () -> JieIO.read(tin, -1));
        }
    }

    private void testRead(int totalSize) throws Exception {
        testRead(CharOperator.get(JieIO.bufferSize()), totalSize);
        testRead(CharOperator.get(1), totalSize);
        testRead(CharOperator.get(2), totalSize);
        testRead(CharOperator.get(JieIO.bufferSize() - 1), totalSize);
        testRead(CharOperator.get(JieIO.bufferSize() + 1), totalSize);
        testRead(CharOperator.get(JieIO.bufferSize() * 2), totalSize);
    }

    private void testRead(CharOperator reader, int totalSize) throws Exception {
        testRead(reader, totalSize, totalSize);
        testRead(reader, totalSize, 0);
        testRead(reader, totalSize, 1);
        testRead(reader, totalSize, totalSize / 2);
        testRead(reader, totalSize, totalSize - 1);
        testRead(reader, totalSize, totalSize + 1);
        testRead(reader, totalSize, totalSize * 2);
    }

    private void testRead(CharOperator reader, int totalSize, int readSize) throws Exception {
        {
            // reader
            char[] data = JieRandom.fill(new char[totalSize]);
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
            assertEquals(reader.read(new OneCharPerRead(data)), data);
            assertEquals(reader.string(new OneCharPerRead(data)), new String(data));
            assertEquals(
                reader.read(new OneCharPerRead(data), readSize < 0 ? totalSize : readSize),
                (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
            );
            assertEquals(
                reader.string(new OneCharPerRead(data), readSize < 0 ? totalSize : readSize),
                new String((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
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
            // size 0: reader to appender
            char[] data = new char[0];
            CharsBuilder bb = new CharsBuilder();
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), bb),
                -1
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), bb, 0),
                0
            );
            assertEquals(bb.size(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), bb, 11),
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
                JieIO.readTo(new CharArrayReader(data), aar),
                -1
            );
            assertEquals(aar[0], (char) 7);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), new char[0]),
                0
            );
            assertEquals(aar[0], (char) 7);
        }
        {
            // size 0: reader to heap buffer
            char[] data = new char[0];
            CharBuffer buf = CharBuffer.allocate(1);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), CharBuffer.allocate(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
        }
        {
            // size 0: reader to direct buffer
            char[] data = new char[0];
            CharBuffer buf = JieBuffer.directBuffer(1);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), buf),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), JieBuffer.directBuffer(0)),
                0
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), buf, 1),
                -1
            );
            assertEquals(buf.position(), 0);
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), buf, 0),
                0
            );
            assertEquals(buf.position(), 0);
        }

        {
            // error
            TestReader tin = new TestReader(new CharArrayReader(new char[0]));
            tin.setNextOperation(ReadOps.THROW, 99);
            ErrorAppender errOut = new ErrorAppender();
            // read stream
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, errOut));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, errOut, 1));
            expectThrows(IllegalArgumentException.class, () -> JieIO.readTo(tin, errOut, -1));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, new char[1], 0, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> JieIO.readTo(tin, new char[0], 1, 0));
            expectThrows(IndexOutOfBoundsException.class, () -> JieIO.readTo(tin, new char[0], 0, 1));
            expectThrows(IORuntimeException.class, () -> JieIO.readTo(tin, CharBuffer.allocate(1)));
            expectThrows(IllegalArgumentException.class, () -> JieIO.readTo(tin, CharBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                JieIO.readTo(new CharArrayReader(new char[1]), CharBuffer.allocate(1).asReadOnlyBuffer())
            );
        }
    }

    private void testReadTo(int totalSize) throws Exception {
        testReadTo(CharOperator.get(JieIO.bufferSize()), totalSize);
        testReadTo(CharOperator.get(1), totalSize);
        testReadTo(CharOperator.get(2), totalSize);
        testReadTo(CharOperator.get(JieIO.bufferSize() - 1), totalSize);
        testReadTo(CharOperator.get(JieIO.bufferSize() + 1), totalSize);
        testReadTo(CharOperator.get(JieIO.bufferSize() * 2), totalSize);
    }

    private void testReadTo(CharOperator reader, int totalSize) throws Exception {
        testReadTo(reader, totalSize, totalSize);
        testReadTo(reader, totalSize, 0);
        testReadTo(reader, totalSize, 1);
        testReadTo(reader, totalSize, totalSize / 2);
        testReadTo(reader, totalSize, totalSize - 1);
        testReadTo(reader, totalSize, totalSize + 1);
        testReadTo(reader, totalSize, totalSize * 2);
    }

    private void testReadTo(CharOperator reader, int totalSize, int readSize) throws Exception {
        {
            // reader to appender
            char[] data = JieRandom.fill(new char[totalSize]);
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
                reader.readTo(new OneCharPerRead(data), builder),
                totalSize
            );
            assertEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                reader.readTo(new OneCharPerRead(data), builder, readSize < 0 ? totalSize : readSize),
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
            char[] data = JieRandom.fill(new char[totalSize]);
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
            char[] data = JieRandom.fill(new char[totalSize]);
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
            char[] data = JieRandom.fill(new char[totalSize]);
            CharBuffer dst = JieBuffer.directBuffer(data.length * 2);
            assertEquals(
                reader.readTo(new CharArrayReader(data), dst),
                totalSize
            );
            assertEquals(dst.flip(), CharBuffer.wrap(data));
            dst = JieBuffer.directBuffer(data.length * 2);
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
            assertSame(CharOperator.defaultOperator(), CharOperator.get(JieIO.bufferSize()));
            assertEquals(CharOperator.newOperator(666).bufferSize(), 666);
        }
        {
            // error
            expectThrows(IllegalArgumentException.class, () -> CharOperator.newOperator(0));
            expectThrows(IllegalArgumentException.class, () -> CharOperator.newOperator(-1));
        }
    }

    private static class OneCharPerRead extends Reader {

        private final char[] data;
        private int pos = 0;

        private OneCharPerRead(char[] data) {
            this.data = data;
        }

        @Override
        public int read() throws IOException {
            if (pos >= data.length) {
                return -1;
            }
            return data[pos++] & 0x0000ffff;
        }

        @Override
        public int read(@NotNull char[] b, int off, int len) throws IOException {
            if (pos >= data.length) {
                return -1;
            }
            b[off] = data[pos++];
            return 1;
        }

        @Override
        public void close() throws IOException {
        }
    }
}
