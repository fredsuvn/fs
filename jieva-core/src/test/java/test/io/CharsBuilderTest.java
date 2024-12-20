package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.io.CharsBuilder;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieTest;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.*;

public class CharsBuilderTest {

    @Test
    public void testCharsBuilder() throws Exception {
        testCharsBuilder(512);
        testCharsBuilder(1024);
        expectThrows(IllegalArgumentException.class, () -> new CharsBuilder(-1));
        expectThrows(IllegalArgumentException.class, () -> new CharsBuilder(10, -2));
        expectThrows(IllegalArgumentException.class, () -> new CharsBuilder(10, 2));
        CharsBuilder cb = new CharsBuilder();
        cb.append((char) 1);
        expectThrows(IORuntimeException.class, () -> cb.writeTo(new Writer() {

            @Override
            public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
                throw new IOException();
            }

            @Override
            public void flush() throws IOException {
                throw new IOException();
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        }));
        CharBuffer bufEmpty = CharBuffer.allocate(0);
        expectThrows(IORuntimeException.class, () -> cb.writeTo(bufEmpty));

        // test big memory!
        Method grow = CharsBuilder.class.getDeclaredMethod("grow", int.class);
        CharsBuilder cbs = new CharsBuilder(1, 1);
        cbs.flush();
        cbs.write(1);
        assertEquals(cbs.length(), 1);
        assertEquals(cbs.charAt(0), 1);
        expectThrows(IllegalArgumentException.class, () -> cbs.charAt(-1));
        expectThrows(IndexOutOfBoundsException.class, () -> cbs.charAt(1));
        assertEquals(cbs.subSequence(0, 1), new String(new char[]{1}));
        expectThrows(IllegalStateException.class, () -> cbs.write(1));
        CharsBuilder cbs2 = new CharsBuilder(2, 3);
        cbs2.write(1);
        cbs2.write(1);
        cbs2.write(1);
        expectThrows(IllegalStateException.class, () -> cbs2.write(1));
        JieTest.testThrow(IllegalStateException.class, grow, new CharsBuilder(), CharsBuilder.MAX_ARRAY_SIZE + 10);
        Method newCapacity = CharsBuilder.class.getDeclaredMethod("newCapacity", int.class, int.class);
        newCapacity.setAccessible(true);
        assertEquals(CharsBuilder.MAX_ARRAY_SIZE, newCapacity.invoke(new CharsBuilder(), -1, 1));
    }

    private void testCharsBuilder(int size) throws Exception {
        char[] cs = JieRandom.fill(new char[size], '0', '9');
        char[] bs = new String(cs).toCharArray();
        CharsBuilder bb = new CharsBuilder();
        bb.close();
        bb.trimBuffer();
        bb.append(bs[0]);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 1));
        bb.append(Arrays.copyOfRange(bs, 1, 10));
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 10));
        bb.append(bs, 10, 10);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 20));
        CharBuffer buffer = CharBuffer.wrap(Arrays.copyOfRange(bs, 20, 25));
        bb.append(buffer);
        CharBuffer arrayBuf = CharBuffer.wrap(bs, 20, 10);
        arrayBuf.get(new char[5]);
        bb.append(arrayBuf);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 30));
        assertEquals(buffer.position(), 5);
        assertFalse(buffer.hasRemaining());
        bb.append(JieIO.reader(Arrays.copyOfRange(bs, 30, 40)));
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 40));
        CharsBuilder bb2 = new CharsBuilder();
        bb2.append(Arrays.copyOfRange(bs, 40, 50));
        bb.append(bb2);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 50));
        bb.append(JieIO.reader(Arrays.copyOfRange(bs, 50, 60)), 1);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 60));
        CharBuffer buffer2 = ByteBuffer.allocateDirect(20).asCharBuffer();
        buffer2.put(CharBuffer.wrap(Arrays.copyOfRange(bs, 60, 70)));
        buffer2.flip();
        bb.append(buffer2);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 70));
        assertEquals(buffer2.position(), 10);
        assertFalse(buffer2.hasRemaining());
        bb.append(JieChars.emptyBuffer());
        expectThrows(IORuntimeException.class, () -> bb.append(new Reader() {

            @Override
            public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
                throw new IOException();
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        }));
        assertEquals(bb.size(), 70);
        assertEquals(bb.toCharBuffer(), CharBuffer.wrap(bs, 0, 70));
        assertEquals(Arrays.copyOf(cs, 70), bb.toString().toCharArray());
        bb.reset();
        bb.append(bs[0]);
        bb.append(bs[1]);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 2));
        bb.reset();
        bb.append(bs[0]);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 1));
        bb.reset();
        bb.trimBuffer();
        bb.append(bs[0]);
        bb.append(bs[1]);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 2));
        bb.trimBuffer();
        bb.reset();
        bb.trimBuffer();
        bb.append(bs[0]);
        assertEquals(bb.toCharArray(), Arrays.copyOf(bs, 1));
        CharArrayWriter out = new CharArrayWriter();
        bb.writeTo(out);
        assertEquals(bb.toCharArray(), out.toCharArray());
        CharBuffer bufOut = CharBuffer.allocate(1);
        bb.writeTo(bufOut);
        assertEquals(bb.toCharArray(), bufOut.array());
    }
}
