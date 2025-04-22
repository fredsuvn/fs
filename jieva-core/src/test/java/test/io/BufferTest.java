package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.test.MaterialBox;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class BufferTest {

    @Test
    public void testIndex() {
        byte[] bytes = new byte[100];
        ByteBuffer buffer = MaterialBox.copyPadding(bytes);
        buffer.get();
        assertEquals(buffer.arrayOffset(), 10);
        assertEquals(buffer.position(), 1);
        assertEquals(buffer.remaining(), 99);
        assertEquals(JieBuffer.arrayStartIndex(buffer), 10 + 1);
        assertEquals(JieBuffer.arrayEndIndex(buffer), 10 + 1 + 99);
    }

    @Test
    public void testRead() {
        testRead(0, 0);
        testRead(66, 0);
        testRead(33, 4);
        testRead(33, 44);
        testRead(7777, 999);
        testRead(1024, -1);
    }

    private void testRead(int size, int number) {
        byte[] bytes = JieArray.fill(new byte[size], (byte) 'a');
        char[] chars = JieArray.fill(new char[size], 'a');
        {
            // read byte
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            assertEquals(JieBuffer.read(buffer), bytes);
            buffer.flip();
            assertEquals(JieBuffer.read(buffer, number), Arrays.copyOf(bytes, getMinLength(size, number)));
            buffer.rewind();
            assertEquals(JieBuffer.string(buffer), new String(chars));
        }
        {
            // read char
            CharBuffer buffer = CharBuffer.wrap(chars);
            assertEquals(JieBuffer.read(buffer), chars);
            buffer.flip();
            assertEquals(JieBuffer.read(buffer, number), Arrays.copyOf(chars, getMinLength(size, number)));
            buffer.rewind();
            assertEquals(JieBuffer.string(buffer), new String(chars));
            buffer.rewind();
            assertEquals(JieBuffer.string(buffer, number), new String(Arrays.copyOf(chars, getMinLength(size, number))));
        }
    }

    @Test
    public void testReadTo() {
        testReadTo(0, 0);
        testReadTo(66, 0);
        testReadTo(0, 66);
        testReadTo(33, 4);
        testReadTo(33, 44);
        testReadTo(7777, 999);
        testReadTo(999, 7777);
        testReadTo(1024, 1);
        testReadTo(1, 1024);
        testReadTo(1024, 1024);
    }

    private void testReadTo(int size, int dstSize) {
        byte[] bytes = JieArray.fill(new byte[size], (byte) 'a');
        char[] chars = JieArray.fill(new char[size], 'a');
        int minSize = Math.min(size, dstSize);
        {
            // byte array to byte array
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            byte[] dst = new byte[dstSize];
            assertEquals(JieBuffer.readTo(buffer, dst), minSize);
            assertEquals(Arrays.copyOf(bytes, minSize), Arrays.copyOf(dst, minSize));
        }
        {
            // byte array to byte buffer
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            ByteBuffer dst = ByteBuffer.allocate(dstSize);
            assertEquals(JieBuffer.readTo(buffer, dst), minSize);
            assertEquals(Arrays.copyOf(bytes, minSize), Arrays.copyOf(dst.array(), minSize));
            assertEquals(dst.position(), minSize);
        }
        {
            // byte array to output stream
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            ByteArrayOutputStream dst = new ByteArrayOutputStream();
            assertEquals(JieBuffer.readTo(buffer, dst), size);
            assertEquals(bytes, dst.toByteArray());
        }
        {
            // char array to char array
            CharBuffer buffer = CharBuffer.wrap(chars);
            char[] dst = new char[dstSize];
            assertEquals(JieBuffer.readTo(buffer, dst), minSize);
            assertEquals(Arrays.copyOf(chars, minSize), Arrays.copyOf(dst, minSize));
        }
        {
            // char array to char buffer
            CharBuffer buffer = CharBuffer.wrap(chars);
            CharBuffer dst = CharBuffer.allocate(dstSize);
            assertEquals(JieBuffer.readTo(buffer, dst), minSize);
            assertEquals(Arrays.copyOf(chars, minSize), Arrays.copyOf(dst.array(), minSize));
            assertEquals(dst.position(), minSize);
        }
        {
            // char array to appendable
            CharBuffer buffer = CharBuffer.wrap(chars);
            CharArrayWriter dst = new CharArrayWriter();
            assertEquals(JieBuffer.readTo(buffer, dst), size);
            assertEquals(chars, dst.toCharArray());
        }
    }

    @Test
    public void testSlice() {
        testSlice(0, 0, 0);
        testSlice(1024, 0, 0);
        testSlice(1024, 111, 0);
        testSlice(1024, 0, 111);
        testSlice(1024, 111, 111);

        {
            // exceptions
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), -1, 0));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), 50, 51));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(CharBuffer.allocate(100), 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(CharBuffer.allocate(100), -1, 0));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(CharBuffer.allocate(100), 50, 51));
        }
    }

    private void testSlice(int size, int offset, int length) {
        {
            // byte
            byte[] bytes = JieArray.fill(new byte[size], (byte) 'a');
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            ByteBuffer slice1 = JieBuffer.slice(buffer, length);
            assertEquals(slice1.position(), 0);
            assertEquals(slice1.limit(), length);
            assertEquals(slice1.capacity(), length);
            buffer.mark();
            assertEquals(JieBuffer.read(slice1), JieBuffer.read(buffer, length));
            buffer.reset();
            ByteBuffer slice2 = JieBuffer.slice(buffer, offset, length);
            assertEquals(slice2.limit(), length);
            assertEquals(slice2.capacity(), length);
            buffer.position(buffer.position() + offset);
            assertEquals(JieBuffer.read(slice2), JieBuffer.read(buffer, length));
        }
        {
            // char
            char[] chars = JieArray.fill(new char[size], (char) 'a');
            CharBuffer buffer = CharBuffer.wrap(chars);
            CharBuffer slice1 = JieBuffer.slice(buffer, length);
            assertEquals(slice1.position(), 0);
            assertEquals(slice1.limit(), length);
            assertEquals(slice1.capacity(), length);
            buffer.mark();
            assertEquals(JieBuffer.read(slice1), JieBuffer.read(buffer, length));
            buffer.reset();
            CharBuffer slice2 = JieBuffer.slice(buffer, offset, length);
            assertEquals(slice2.limit(), length);
            assertEquals(slice2.capacity(), length);
            buffer.position(buffer.position() + offset);
            assertEquals(JieBuffer.read(slice2), JieBuffer.read(buffer, length));
        }
    }

    private int getMinLength(int size, int number) {
        if (number < 0) {
            return size;
        }
        return Math.min(size, number);
    }
}
