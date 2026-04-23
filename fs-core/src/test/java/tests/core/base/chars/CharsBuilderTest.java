package tests.core.base.chars;

import internal.utils.Asserter;
import internal.utils.DataGen;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.BufferKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CharsBuilderTest implements DataGen, Asserter {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    @Test
    public void testCharsBuilderInitialization() {
        assertThrows(IllegalArgumentException.class, () -> new CharsBuilder(-1));
        assertThrows(IllegalArgumentException.class, () -> new CharsBuilder(10, -2));
        assertThrows(IllegalArgumentException.class, () -> new CharsBuilder(10, 2));
    }

    @Test
    public void testCharsBuilderAppendMethods() throws Exception {
        testCharsBuilderAppendMethods(512);
        testCharsBuilderAppendMethods(1024);
    }

    @Test
    public void testCharsBuilderWriteToExceptions() {
        CharsBuilder cb = new CharsBuilder();
        cb.append(1);
        assertThrows(IORuntimeException.class, () -> cb.writeTo(new Writer() {

            @Override
            public void write(char @Nonnull [] cbuf, int off, int len) throws IOException {
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
        assertThrows(IORuntimeException.class, () -> cb.writeTo(bufEmpty));
    }

    @Test
    public void testCharsBuilderMemoryLimits() throws Exception {
        // test big memory!
        Method grow = CharsBuilder.class.getDeclaredMethod("grow", int.class);
        CharsBuilder cbs = new CharsBuilder(1, 1);
        cbs.flush();
        cbs.write(1);
        assertEquals(1, cbs.length());
        assertEquals(1, cbs.charAt(0));
        assertThrows(IndexOutOfBoundsException.class, () -> cbs.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cbs.charAt(1));
        assertEquals(new String(new char[]{1}), cbs.subSequence(0, 1));
        assertThrows(IllegalStateException.class, () -> cbs.write(1));
        CharsBuilder cbs2 = new CharsBuilder(2, 3);
        cbs2.write(1);
        cbs2.write(1);
        cbs2.write(1);
        assertThrows(IllegalStateException.class, () -> cbs2.write(1));
        invokeThrows(IllegalStateException.class, grow, new CharsBuilder(), MAX_ARRAY_SIZE + 10);
        Method newCapacity = CharsBuilder.class.getDeclaredMethod("newCapacity", int.class, int.class);
        newCapacity.setAccessible(true);
        assertEquals(MAX_ARRAY_SIZE, newCapacity.invoke(new CharsBuilder(), -1, 1));
    }

    private void testCharsBuilderAppendMethods(int size) throws Exception {
        char[] cs = randomChars(size, '0', '9');
        char[] bs = new String(cs).toCharArray();
        CharsBuilder cb = new CharsBuilder();

        // Test basic append operations
        testBasicAppendOperations(cb, bs, cs);

        // Test reset and trim operations
        testResetAndTrimOperations(cb, bs);

        // Test writeTo operations
        testWriteToOperations(cb);
    }

    private void testBasicAppendOperations(CharsBuilder cb, char[] bs, char[] cs) throws Exception {
        cb.close();
        cb.trim();

        // Append single char
        cb.append(bs[0]);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 1));

        // Append char array range
        cb.append(Arrays.copyOfRange(bs, 1, 10));
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 10));

        // Append char array with offset and length
        cb.append(bs, 10, 10);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 20));

        // Append CharBuffer
        CharBuffer buffer = CharBuffer.wrap(Arrays.copyOfRange(bs, 20, 25));
        cb.append(buffer);
        assertEquals(5, buffer.position());
        assertFalse(buffer.hasRemaining());

        // Append CharBuffer with position
        CharBuffer arrayBuf = CharBuffer.wrap(bs, 20, 10);
        arrayBuf.get(new char[5]);
        cb.append(arrayBuf);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 30));

        // Append Reader
        cb.append(IOKit.newReader(Arrays.copyOfRange(bs, 30, 40)));
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 40));

        // Append another CharsBuilder
        CharsBuilder cb2 = new CharsBuilder();
        cb2.append(Arrays.copyOfRange(bs, 40, 50));
        cb.append(cb2);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 50));

        // Append Reader with length
        cb.append(IOKit.newReader(Arrays.copyOfRange(bs, 50, 60)), 1);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 60));

        // Append direct CharBuffer
        CharBuffer buffer2 = BufferKit.directCharBuffer(10);
        buffer2.put(CharBuffer.wrap(Arrays.copyOfRange(bs, 60, 70)));
        buffer2.flip();
        cb.append(buffer2);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 70));
        assertEquals(10, buffer2.position());
        assertFalse(buffer2.hasRemaining());

        // Append empty direct CharBuffer
        cb.append(BufferKit.directCharBuffer(0));
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 70));

        // Append empty buffer
        cb.append(CharsKit.emptyBuffer());

        // Test exception cases
        assertThrows(IORuntimeException.class, () -> cb.append(new Reader() {

            @Override
            public int read(char @Nonnull [] cbuf, int off, int len) throws IOException {
                throw new IOException();
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        }));
        assertThrows(IllegalArgumentException.class, () ->
            cb.append(new CharArrayReader(new char[0]), -1)
        );

        // Test size and conversion methods
        assertEquals(70, cb.size());
        assertEquals(cb.toCharBuffer(), CharBuffer.wrap(bs, 0, 70));
        assertArrayEquals(Arrays.copyOf(cs, 70), cb.toString().toCharArray());
    }

    private void testResetAndTrimOperations(CharsBuilder cb, char[] bs) {
        // Test reset operation
        cb.reset();
        cb.append(bs[0]);
        cb.append(bs[1]);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 2));

        cb.reset();
        cb.append(bs[0]);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 1));

        // Test reset and trim combination
        cb.reset();
        cb.trim();
        cb.append(bs[0]);
        cb.append(bs[1]);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 2));

        cb.trim();
        cb.reset();
        cb.trim();
        cb.append(bs[0]);
        assertArrayEquals(cb.toCharArray(), Arrays.copyOf(bs, 1));
    }

    private void testWriteToOperations(CharsBuilder cb) throws IOException {
        // Test writeTo Writer
        CharArrayWriter out = new CharArrayWriter();
        cb.writeTo(out);
        assertArrayEquals(cb.toCharArray(), out.toCharArray());

        // Test writeTo CharBuffer
        CharBuffer bufOut = CharBuffer.allocate(1);
        cb.writeTo(bufOut);
        assertArrayEquals(cb.toCharArray(), bufOut.array());
    }
}
