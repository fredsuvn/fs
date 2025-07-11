package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ErrorOutputStream;
import xyz.sunqian.test.MaterialBox;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class BufferTest implements DataTest {

    @Test
    public void testIndex() {
        byte[] bytes = new byte[100];
        ByteBuffer buffer = MaterialBox.copyPadding(bytes);
        buffer.get();
        assertEquals(buffer.arrayOffset(), 10);
        assertEquals(buffer.position(), 1);
        assertEquals(buffer.remaining(), 99);
        assertEquals(BufferKit.arrayStartIndex(buffer), 10 + 1);
        assertEquals(BufferKit.arrayEndIndex(buffer), 10 + 1 + 99);
    }

    @Test
    public void testDirect() {
        {
            // direct
            char[] chars = randomChars(128);
            CharBuffer heapBuffer = CharBuffer.wrap(chars);
            CharBuffer directBuffer = BufferKit.directBuffer(chars.length);
            directBuffer.put(chars);
            directBuffer.flip();
            assertEquals(directBuffer, heapBuffer);
            assertTrue(directBuffer.isDirect());
            expectThrows(IllegalArgumentException.class, () -> BufferKit.directBuffer(-1));
        }
        {
            // byte
            byte[] data = randomBytes(128);
            ByteBuffer buffer = BufferKit.directBuffer(data);
            assertEquals(buffer, ByteBuffer.wrap(data));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), data.length);
            assertEquals(buffer.capacity(), data.length);
            assertTrue(buffer.isDirect());
            buffer = BufferKit.directBuffer(data, 6, 66);
            assertEquals(buffer, ByteBuffer.wrap(data, 6, 66));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), 66);
            assertEquals(buffer.capacity(), 66);
            assertTrue(buffer.isDirect());
        }
        {
            // char
            char[] data = randomChars(128);
            CharBuffer buffer = BufferKit.directBuffer(data);
            assertEquals(buffer, CharBuffer.wrap(data));
            assertEquals(buffer.position(), 0);
            assertEquals(buffer.limit(), data.length);
            assertEquals(buffer.capacity(), data.length);
            assertTrue(buffer.isDirect());
            buffer = BufferKit.directBuffer(data, 6, 66);
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
            byte[] data = randomBytes(128);
            ByteBuffer b1 = ByteBuffer.wrap(data, 6, 66);
            b1.get(new byte[5]);
            ByteBuffer b2 = BufferKit.copy(b1);
            assertEquals(b2, b1);
            assertFalse(b2.isDirect());
            ByteBuffer b3 = BufferKit.directBuffer(data);
            b3.get(new byte[5]);
            ByteBuffer b4 = BufferKit.copy(b3);
            assertEquals(b4, b3);
            assertTrue(b4.isDirect());
            ByteBuffer b5 = ByteBuffer.wrap(data, 6, 66);
            assertEquals(BufferKit.copyContent(b5), Arrays.copyOfRange(data, 6, 6 + 66));
            assertEquals(b5.position(), 6);
            assertEquals(b5.remaining(), 66);
        }
        {
            // char
            char[] data = randomChars(128);
            CharBuffer b1 = CharBuffer.wrap(data, 6, 66);
            b1.get(new char[5]);
            CharBuffer b2 = BufferKit.copy(b1);
            assertEquals(b2, b1);
            assertFalse(b2.isDirect());
            CharBuffer b3 = BufferKit.directBuffer(data.length);
            b3.put(data);
            b3.flip();
            b3.get(new char[5]);
            CharBuffer b4 = BufferKit.copy(b3);
            assertEquals(b4, b3);
            assertTrue(b4.isDirect());
            CharBuffer b5 = CharBuffer.wrap(data, 6, 66);
            assertEquals(BufferKit.copyContent(b5), Arrays.copyOfRange(data, 6, 6 + 66));
            assertEquals(b5.position(), 6);
            assertEquals(b5.remaining(), 66);
        }
    }

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
                BufferKit.slice(ByteBuffer.allocate(100), -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                BufferKit.slice(ByteBuffer.allocate(100), 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                BufferKit.slice(ByteBuffer.allocate(100), -1, 0));
            expectThrows(IndexOutOfBoundsException.class, () ->
                BufferKit.slice(ByteBuffer.allocate(100), 50, 51));
            // char
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.slice(CharBuffer.allocate(100), -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                BufferKit.slice(CharBuffer.allocate(100), 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () ->
                BufferKit.slice(CharBuffer.allocate(100), -1, 0));
            expectThrows(IndexOutOfBoundsException.class, () ->
                BufferKit.slice(CharBuffer.allocate(100), 50, 51));
        }
    }

    private void testSlice(int size, int offset, int length) {
        {
            // byte: slice(src, len)
            byte[] data = JieArray.fill(new byte[size], (byte) 6);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            ByteBuffer slice = BufferKit.slice(buffer, length);
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
            ByteBuffer slice = BufferKit.slice(buffer, offset, length);
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
            CharBuffer slice = BufferKit.slice(buffer, length);
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
            CharBuffer slice = BufferKit.slice(buffer, offset, length);
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
    public void testByteRead() {
        testByteRead(0, 0);
        testByteRead(64, 0);
        testByteRead(0, 64);
        testByteRead(64, 64);
        testByteRead(128, 64);
        testByteRead(64, 128);

        {
            // byte to string
            String hello = "hello";
            byte[] bytes = hello.getBytes(JieChars.defaultCharset());
            assertEquals(BufferKit.string(ByteBuffer.wrap(bytes)), hello);
            assertNull(BufferKit.string(ByteBuffer.allocate(0)));
        }

        {
            // error
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.read(ByteBuffer.allocate(1), -1));
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), ByteBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), Channels.newChannel(new ErrorOutputStream())));
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), Channels.newChannel(new ByteArrayOutputStream()), -1));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), new ErrorOutputStream()));
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), new ByteArrayOutputStream(), -1));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), ByteBuffer.allocate(1).asReadOnlyBuffer()));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(ByteBuffer.allocate(1), ByteBuffer.allocate(1).asReadOnlyBuffer(), 1));
        }
    }

    private void testByteRead(int totalSize, int readSize) {
        int actualLen = Math.min(totalSize, readSize);
        {
            // read all
            byte[] data = randomBytes(totalSize);
            ByteBuffer src = ByteBuffer.wrap(data);
            byte[] ret = BufferKit.read(src);
            if (totalSize == 0) {
                assertNull(ret);
            } else {
                assertEquals(ret, data);
            }
            assertEquals(src.position(), src.limit());
            src.clear();
            ret = BufferKit.read(src, readSize);
            if (totalSize == 0 && readSize != 0) {
                assertNull(ret);
            } else {
                assertEquals(ret, Arrays.copyOf(data, actualLen));
            }
            assertEquals(src.position(), actualLen);
        }
        {
            // buffer to array
            byte[] data = randomBytes(totalSize);
            ByteBuffer src = ByteBuffer.wrap(data);
            byte[] dst = new byte[readSize];
            assertEquals(BufferKit.readTo(src, dst), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            src.clear();
            dst = new byte[readSize];
            assertEquals(BufferKit.readTo(src, dst, 0, readSize), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // buffer to buffer
            byte[] data = randomBytes(totalSize);
            ByteBuffer src = ByteBuffer.wrap(data);
            byte[] dstData = new byte[readSize];
            ByteBuffer dst = ByteBuffer.wrap(dstData);
            assertEquals(BufferKit.readTo(src, dst), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dstData, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            assertEquals(dst.position(), actualLen);
            src.clear();
            dstData = new byte[readSize];
            dst = ByteBuffer.wrap(dstData);
            assertEquals(BufferKit.readTo(src, dst, readSize), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dstData, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            assertEquals(dst.position(), actualLen);
            src.clear();
            dst = ByteBuffer.allocate(0);
            assertEquals(BufferKit.readTo(src, dst, readSize), 0);
            assertEquals(src.position(), 0);
        }
        {
            // buffer to channel
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            ByteBuffer src = ByteBuffer.wrap(data);
            WritableByteChannel dst = Channels.newChannel(builder);
            assertEquals(BufferKit.readTo(src, dst), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, dst, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            // write one byte channel
            dst = new OneByteWriteableChannel(builder);
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, dst), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, dst, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // heap buffer to stream
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            ByteBuffer src = ByteBuffer.wrap(data);
            assertEquals(BufferKit.readTo(src, builder), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, builder, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // direct buffer to stream
            byte[] data = randomBytes(totalSize);
            BytesBuilder builder = new BytesBuilder();
            ByteBuffer src = BufferKit.directBuffer(data);
            assertEquals(BufferKit.readTo(src, builder), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toByteArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, builder, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
    }

    @Test
    public void testCharRead() {
        testCharRead(0, 0);
        testCharRead(64, 0);
        testCharRead(0, 64);
        testCharRead(64, 64);
        testCharRead(128, 64);
        testCharRead(64, 128);

        {
            // error
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.read(CharBuffer.allocate(1), -1));
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.readTo(CharBuffer.allocate(1), CharBuffer.allocate(1), -1));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(CharBuffer.allocate(1), new ErrorAppender()));
            expectThrows(IllegalArgumentException.class, () ->
                BufferKit.readTo(CharBuffer.allocate(1), new CharArrayWriter(), -1));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(CharBuffer.allocate(1), CharBuffer.allocate(1).asReadOnlyBuffer()));
            expectThrows(IORuntimeException.class, () ->
                BufferKit.readTo(CharBuffer.allocate(1), CharBuffer.allocate(1).asReadOnlyBuffer(), 1));
        }
    }

    private void testCharRead(int totalSize, int readSize) {
        int actualLen = Math.min(totalSize, readSize);
        {
            // read all
            char[] data = randomChars(totalSize);
            CharBuffer src = CharBuffer.wrap(data);
            char[] ret = BufferKit.read(src);
            if (totalSize == 0) {
                assertNull(ret);
            } else {
                assertEquals(ret, data);
            }
            assertEquals(src.position(), src.limit());
            src.clear();
            ret = BufferKit.read(src, readSize);
            if (totalSize == 0 && readSize != 0) {
                assertNull(ret);
            } else {
                assertEquals(ret, Arrays.copyOf(data, actualLen));
            }
            assertEquals(src.position(), actualLen);
            // string
            src.clear();
            String str = BufferKit.string(src);
            if (totalSize == 0) {
                assertNull(str);
            } else {
                assertEquals(str, new String(data));
            }
            assertEquals(src.position(), src.limit());
            src.clear();
            str = BufferKit.string(src, readSize);
            if (totalSize == 0 && readSize != 0) {
                assertNull(str);
            } else {
                assertEquals(str, new String(Arrays.copyOf(data, actualLen)));
            }
            assertEquals(src.position(), actualLen);
        }
        {
            // buffer to array
            char[] data = randomChars(totalSize);
            CharBuffer src = CharBuffer.wrap(data);
            char[] dst = new char[readSize];
            assertEquals(BufferKit.readTo(src, dst), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            src.clear();
            dst = new char[readSize];
            assertEquals(BufferKit.readTo(src, dst, 0, readSize), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dst, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // buffer to buffer
            char[] data = randomChars(totalSize);
            CharBuffer src = CharBuffer.wrap(data);
            char[] dstData = new char[readSize];
            CharBuffer dst = CharBuffer.wrap(dstData);
            assertEquals(BufferKit.readTo(src, dst), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dstData, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            assertEquals(dst.position(), actualLen);
            src.clear();
            dstData = new char[readSize];
            dst = CharBuffer.wrap(dstData);
            assertEquals(BufferKit.readTo(src, dst, readSize), actualReadSize(totalSize, readSize));
            assertEquals(Arrays.copyOf(dstData, actualLen), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
            assertEquals(dst.position(), actualLen);
            src.clear();
            dst = CharBuffer.allocate(0);
            assertEquals(BufferKit.readTo(src, dst, readSize), 0);
            assertEquals(src.position(), 0);
        }
        {
            // heap buffer to appender
            char[] data = randomChars(totalSize);
            CharsBuilder builder = new CharsBuilder();
            CharBuffer src = CharBuffer.wrap(data);
            assertEquals(BufferKit.readTo(src, builder), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toCharArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, builder, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toCharArray(), Arrays.copyOf(data, actualLen));
            assertEquals(src.position(), actualLen);
        }
        {
            // direct buffer to appender
            char[] data = randomChars(totalSize);
            CharsBuilder builder = new CharsBuilder();
            CharBuffer src = BufferKit.directBuffer(data);
            assertEquals(BufferKit.readTo(src, builder), totalSize == 0 ? -1 : totalSize);
            assertEquals(builder.toCharArray(), data);
            assertEquals(src.position(), src.limit());
            src.clear();
            builder.reset();
            assertEquals(BufferKit.readTo(src, builder, readSize), actualReadSize(totalSize, readSize));
            assertEquals(builder.toCharArray(), Arrays.copyOf(data, actualLen));
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
