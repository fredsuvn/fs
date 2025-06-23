package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.MaterialBox;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
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
    public void testDirect() {
        {
            // direct
            char[] chars = JieRandom.fill(new char[128]);
            CharBuffer heapBuffer = CharBuffer.wrap(chars);
            CharBuffer directBuffer = JieBuffer.directBuffer(chars.length);
            directBuffer.put(chars);
            directBuffer.flip();
            assertEquals(directBuffer, heapBuffer);
            assertTrue(directBuffer.isDirect());
        }
        {
            // byte
            byte[] data = JieRandom.fill(new byte[128]);
            ByteBuffer buffer = JieBuffer.directBuffer(data);
            assertEquals(buffer, ByteBuffer.wrap(data));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), data.length);
            assertEquals(buffer.capacity(), data.length);
            assertTrue(buffer.isDirect());
            buffer = JieBuffer.directBuffer(data, 6, 66);
            assertEquals(buffer, ByteBuffer.wrap(data, 6, 66));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), 66);
            assertEquals(buffer.capacity(), 66);
            assertTrue(buffer.isDirect());
        }
        {
            // char
            char[] data = JieRandom.fill(new char[128]);
            CharBuffer buffer = JieBuffer.directBuffer(data);
            assertEquals(buffer, CharBuffer.wrap(data));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), data.length);
            assertEquals(buffer.capacity(), data.length);
            assertTrue(buffer.isDirect());
            buffer = JieBuffer.directBuffer(data, 6, 66);
            assertEquals(buffer, CharBuffer.wrap(data, 6, 66));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), 66);
            assertEquals(buffer.capacity(), 66);
            assertTrue(buffer.isDirect());
        }
    }

    @Test
    public void testCopy() {
        {
            // byte
            byte[] data = JieRandom.fill(new byte[1024]);
            ByteBuffer b1 = ByteBuffer.wrap(data, 6, 66);
            b1.get(new byte[5]);
            ByteBuffer b2 = JieBuffer.copy(b1);
            assertEquals(b2, b1);
            assertFalse(b2.isDirect());
            ByteBuffer b3 = JieBuffer.directBuffer(data);
            b3.get(new byte[5]);
            ByteBuffer b4 = JieBuffer.copy(b3);
            assertEquals(b4, b3);
            assertTrue(b4.isDirect());
        }
        {
            // char
            char[] data = JieRandom.fill(new char[1024]);
            CharBuffer b1 = CharBuffer.wrap(data, 6, 66);
            b1.get(new char[5]);
            CharBuffer b2 = JieBuffer.copy(b1);
            assertEquals(b2, b1);
            assertFalse(b2.isDirect());
            CharBuffer b3 = JieBuffer.directBuffer(data.length);
            b3.put(data);
            b3.flip();
            b3.get(new char[5]);
            CharBuffer b4 = JieBuffer.copy(b3);
            assertEquals(b4, b3);
            assertTrue(b4.isDirect());
        }
    }

    // @Test
    // public void testRead() {
    //     testRead(0, 0);
    //     testRead(66, 0);
    //     testRead(33, 4);
    //     testRead(33, 44);
    //     testRead(7777, 999);
    //     testRead(1024, -1);
    // }
    //
    // private void testRead(int size, int number) {
    //     byte[] bytes = JieArray.fill(new byte[size], (byte) 'a');
    //     char[] chars = JieArray.fill(new char[size], 'a');
    //     {
    //         // read byte
    //         ByteBuffer buffer = ByteBuffer.wrap(bytes);
    //         assertEquals(JieBuffer.read(buffer), bytes);
    //         buffer.flip();
    //         assertEquals(JieBuffer.read(buffer, number), Arrays.copyOf(bytes, getMinLength(size, number)));
    //         buffer.rewind();
    //         assertEquals(JieBuffer.string(buffer), new String(chars));
    //     }
    //     {
    //         // read char
    //         CharBuffer buffer = CharBuffer.wrap(chars);
    //         assertEquals(JieBuffer.read(buffer), chars);
    //         buffer.flip();
    //         assertEquals(JieBuffer.read(buffer, number), Arrays.copyOf(chars, getMinLength(size, number)));
    //         buffer.rewind();
    //         assertEquals(JieBuffer.string(buffer), new String(chars));
    //         buffer.rewind();
    //         assertEquals(JieBuffer.string(buffer, number), new String(Arrays.copyOf(chars, getMinLength(size, number))));
    //     }
    // }
    //
    // @Test
    // public void testReadTo() {
    //     testReadTo(0, 0);
    //     testReadTo(66, 0);
    //     testReadTo(0, 66);
    //     testReadTo(33, 4);
    //     testReadTo(33, 44);
    //     testReadTo(7777, 999);
    //     testReadTo(999, 7777);
    //     testReadTo(1024, 1);
    //     testReadTo(1, 1024);
    //     testReadTo(1024, 1024);
    // }
    //
    // private void testReadTo(int size, int dstSize) {
    //     byte[] bytes = JieArray.fill(new byte[size], (byte) 'a');
    //     char[] chars = JieArray.fill(new char[size], 'a');
    //     int minSize = Math.min(size, dstSize);
    //     {
    //         // byte array to byte array
    //         ByteBuffer buffer = ByteBuffer.wrap(bytes);
    //         byte[] dst = new byte[dstSize];
    //         assertEquals(JieBuffer.readTo(buffer, dst), minSize);
    //         assertEquals(Arrays.copyOf(bytes, minSize), Arrays.copyOf(dst, minSize));
    //     }
    //     {
    //         // byte array to byte buffer
    //         ByteBuffer buffer = ByteBuffer.wrap(bytes);
    //         ByteBuffer dst = ByteBuffer.allocate(dstSize);
    //         assertEquals(JieBuffer.readTo(buffer, dst), minSize);
    //         assertEquals(Arrays.copyOf(bytes, minSize), Arrays.copyOf(dst.array(), minSize));
    //         assertEquals(dst.position(), minSize);
    //     }
    //     {
    //         // byte array to output stream
    //         ByteBuffer buffer = ByteBuffer.wrap(bytes);
    //         ByteArrayOutputStream dst = new ByteArrayOutputStream();
    //         assertEquals(JieBuffer.readTo(buffer, dst), size);
    //         assertEquals(bytes, dst.toByteArray());
    //     }
    //     {
    //         // char array to char array
    //         CharBuffer buffer = CharBuffer.wrap(chars);
    //         char[] dst = new char[dstSize];
    //         assertEquals(JieBuffer.readTo(buffer, dst), minSize);
    //         assertEquals(Arrays.copyOf(chars, minSize), Arrays.copyOf(dst, minSize));
    //     }
    //     {
    //         // char array to char buffer
    //         CharBuffer buffer = CharBuffer.wrap(chars);
    //         CharBuffer dst = CharBuffer.allocate(dstSize);
    //         assertEquals(JieBuffer.readTo(buffer, dst), minSize);
    //         assertEquals(Arrays.copyOf(chars, minSize), Arrays.copyOf(dst.array(), minSize));
    //         assertEquals(dst.position(), minSize);
    //     }
    //     {
    //         // char array to appendable
    //         CharBuffer buffer = CharBuffer.wrap(chars);
    //         CharArrayWriter dst = new CharArrayWriter();
    //         assertEquals(JieBuffer.readTo(buffer, dst), size);
    //         assertEquals(chars, dst.toCharArray());
    //     }
    // }

    @Test
    public void testSlice() {
        testSlice(0, 0, 0);
        testSlice(1024, 0, 0);
        testSlice(1024, 111, 0);
        testSlice(1024, 0, 111);
        testSlice(1024, 111, 111);
        testSlice(1024, 111, 222);

        {
            // exceptions
            // byte
            expectThrows(IllegalArgumentException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), -1, 0));
            expectThrows(IndexOutOfBoundsException.class, () ->
                JieBuffer.slice(ByteBuffer.allocate(100), 50, 51));
            // char
            expectThrows(IllegalArgumentException.class, () ->
                JieBuffer.slice(CharBuffer.allocate(100), -1));
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
            // byte: slice(src, len)
            byte[] data = JieArray.fill(new byte[size], (byte) 6);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            ByteBuffer slice = JieBuffer.slice(buffer, length);
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), size);
            assertEquals(buffer.capacity(), size);
            assertEquals(slice.position(), 0);
            assertEquals(slice.limit(), length);
            assertEquals(slice.capacity(), length);
            slice.put(JieArray.fill(new byte[length], (byte) 8));
            assertEquals(
                Arrays.copyOf(data, length),
                JieArray.fill(new byte[length], (byte) 8)
            );
            assertEquals(
                Arrays.copyOfRange(data, length, data.length),
                JieArray.fill(new byte[size - length], (byte) 6)
            );
        }
        {
            // byte: slice(src, off, len)
            byte[] data = JieArray.fill(new byte[size], (byte) 6);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            ByteBuffer slice = JieBuffer.slice(buffer, offset, length);
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), size);
            assertEquals(buffer.capacity(), size);
            assertEquals(slice.position(), 0);
            assertEquals(slice.limit(), length);
            assertEquals(slice.capacity(), length);
            slice.put(JieArray.fill(new byte[length], (byte) 8));
            assertEquals(
                Arrays.copyOfRange(data, offset, offset + length),
                JieArray.fill(new byte[length], (byte) 8)
            );
            assertEquals(
                Arrays.copyOfRange(data, 0, offset),
                JieArray.fill(new byte[offset], (byte) 6)
            );
            assertEquals(
                Arrays.copyOfRange(data, offset + length, data.length),
                JieArray.fill(new byte[data.length - offset - length], (byte) 6)
            );
        }
        {
            // char: slice(src, len)
            char[] data = JieArray.fill(new char[size], (char) 6);
            CharBuffer buffer = CharBuffer.wrap(data);
            CharBuffer slice = JieBuffer.slice(buffer, length);
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), size);
            assertEquals(buffer.capacity(), size);
            assertEquals(slice.position(), 0);
            assertEquals(slice.limit(), length);
            assertEquals(slice.capacity(), length);
            slice.put(JieArray.fill(new char[length], (char) 8));
            assertEquals(
                Arrays.copyOf(data, length),
                JieArray.fill(new char[length], (char) 8)
            );
            assertEquals(
                Arrays.copyOfRange(data, length, data.length),
                JieArray.fill(new char[size - length], (char) 6)
            );
        }
        {
            // char: slice(src, off, len)
            char[] data = JieArray.fill(new char[size], (char) 6);
            CharBuffer buffer = CharBuffer.wrap(data);
            CharBuffer slice = JieBuffer.slice(buffer, offset, length);
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), size);
            assertEquals(buffer.capacity(), size);
            assertEquals(slice.position(), 0);
            assertEquals(slice.limit(), length);
            assertEquals(slice.capacity(), length);
            slice.put(JieArray.fill(new char[length], (char) 8));
            assertEquals(
                Arrays.copyOfRange(data, offset, offset + length),
                JieArray.fill(new char[length], (char) 8)
            );
            assertEquals(
                Arrays.copyOfRange(data, 0, offset),
                JieArray.fill(new char[offset], (char) 6)
            );
            assertEquals(
                Arrays.copyOfRange(data, offset + length, data.length),
                JieArray.fill(new char[data.length - offset - length], (char) 6)
            );
        }
    }

    @Test
    public void testRead() {
        testRead(0, 0);
        testRead(64, 0);
        testRead(0, 64);
        testRead(64, 64);
        testRead(128, 64);
        testRead(64, 128);

        {
            // error
            expectThrows(IllegalArgumentException.class, () ->
                JieBuffer.read(ByteBuffer.allocate(1), -1));
            expectThrows(IllegalArgumentException.class, () ->
                JieBuffer.readTo(ByteBuffer.allocate(1), ByteBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                JieBuffer.readTo(ByteBuffer.allocate(1), Channels.newChannel(new ErrorOutputStream())));
            expectThrows(IllegalArgumentException.class, () ->
                JieBuffer.readTo(ByteBuffer.allocate(1), Channels.newChannel(new ByteArrayOutputStream()), -1));
            expectThrows(IORuntimeException.class, () ->
                JieBuffer.readTo(ByteBuffer.allocate(1), new ErrorOutputStream()));
            expectThrows(IllegalArgumentException.class, () ->
                JieBuffer.readTo(ByteBuffer.allocate(1), new ByteArrayOutputStream(), -1));
        }
    }

    private void testRead(int totalSize, int readSize) {
        int actualLen = Math.min(totalSize, readSize);
        {
            // read all
            byte[] data = JieRandom.fill(new byte[totalSize]);
            ByteBuffer src = ByteBuffer.wrap(data);
            byte[] ret = JieBuffer.read(src);
            assertEquals(ret, data);
            assertEquals(src.position(), src.limit());
            src.clear();
            ret = JieBuffer.read(src, readSize);
            assertEquals(ret, Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // buffer to array
            byte[] data = JieRandom.fill(new byte[totalSize]);
            ByteBuffer src = ByteBuffer.wrap(data);
            byte[] dst = new byte[readSize];
            assertEquals(JieBuffer.readTo(src, dst), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            src.clear();
            dst = new byte[readSize];
            assertEquals(JieBuffer.readTo(src, dst, 0, readSize), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // buffer to buffer
            byte[] data = JieRandom.fill(new byte[totalSize]);
            ByteBuffer src = ByteBuffer.wrap(data);
            byte[] dstData = new byte[readSize];
            ByteBuffer dst = ByteBuffer.wrap(dstData);
            assertEquals(JieBuffer.readTo(src, dst), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dstData, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            assertEquals(dst.position(), actualLen);
            src.clear();
            dstData = new byte[readSize];
            dst = ByteBuffer.wrap(dstData);
            assertEquals(JieBuffer.readTo(src, dst, readSize), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dstData, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            assertEquals(dst.position(), actualLen);
            src.clear();
            dst = ByteBuffer.allocate(0);
            assertEquals(JieBuffer.readTo(src, dst, readSize), 0);
            assertEquals(src.position(), 0);
        }
        {
            // buffer to channel
            byte[] data = JieRandom.fill(new byte[totalSize]);
            BytesBuilder builder = new BytesBuilder();
            ByteBuffer src = ByteBuffer.wrap(data);
            WritableByteChannel dst = Channels.newChannel(builder);
            assertEquals(JieBuffer.readTo(src, dst), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(JieBuffer.readTo(src, dst, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // heap buffer to stream
            byte[] data = JieRandom.fill(new byte[totalSize]);
            BytesBuilder builder = new BytesBuilder();
            ByteBuffer src = ByteBuffer.wrap(data);
            assertEquals(JieBuffer.readTo(src, builder), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(JieBuffer.readTo(src, builder, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // direct buffer to stream
            byte[] data = JieRandom.fill(new byte[totalSize]);
            BytesBuilder builder = new BytesBuilder();
            ByteBuffer src = JieBuffer.directBuffer(data);
            assertEquals(JieBuffer.readTo(src, builder), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(JieBuffer.readTo(src, builder, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
    }

    private int actualReadSize(int totalSize, int readSize) {
        if (totalSize == 0) {
            return readSize == 0 ? 0 : -1;
        }
        return Math.min(totalSize, readSize);
    }
}
