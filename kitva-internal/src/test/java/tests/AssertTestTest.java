package tests;

import org.junit.jupiter.api.Test;
import internal.test.AssertTest;
import internal.test.KitvaTestException;
import internal.test.MaterialBox;
import internal.test.TestIOException;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static internal.test.MaterialBox.copyBuffer;
import static internal.test.MaterialBox.copyBytes;
import static internal.test.MaterialBox.copyChars;
import static internal.test.MaterialBox.copyDirect;
import static internal.test.MaterialBox.newFile;

public class AssertTestTest implements AssertTest {

    @Test
    public void testThrows() throws Exception {
        Method throwError = Tt.class.getDeclaredMethod("throwError");
        assertEquals(invokeThrows(KitvaTestException.class, throwError, null).getClass(), KitvaTestException.class);
        Method string = Tt.class.getDeclaredMethod("string");
        assertEquals(invokeThrows(AssertTest.NoThrows.class, string, null).getClass(), AssertTest.NoThrows.class);
    }

    @Test
    public void testEquals() throws Exception {
        Method string = Tt.class.getDeclaredMethod("string");
        invokeEquals("123", string, null);
        assertThrows(AssertionError.class, () ->
            invokeEquals("123", string, null, "123")
        );
    }

    @Test
    public void testFile() throws Exception {
        Path path = Paths.get("src", "test", "resources", "test.test");
        path.toFile().delete();
        byte[] data = {'1', '2', '3'};
        newFile(path, data);
        assertThrows(IllegalStateException.class, () -> newFile(path, data));
        FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 1);
        for (int i = 0; i >= 0; ) {
            i = fc.read(buffer);
        }
        buffer.flip();
        assertArrayEquals(Arrays.copyOf(buffer.array(), data.length), data);
        path.toFile().delete();
    }

    @Test
    public void testMaterialByteBuffer() {
        {
            // padding
            byte[] bytes = new byte[1024];
            Arrays.fill(bytes, (byte) 66);
            ByteBuffer bb = MaterialBox.copyPadding(bytes);
            assertEquals(bb.arrayOffset(), 10);
            assertEquals(bb.position(), 0);
            assertEquals(bb.limit(), bytes.length);
            assertEquals(bb.capacity(), bytes.length);
        }
        {
            // bytes copy
            byte[] bytes = new byte[1024];
            Arrays.fill(bytes, (byte) 66);
            ByteBuffer directBuffer = MaterialBox.copyDirect(bytes);
            assertEquals(directBuffer, ByteBuffer.wrap(bytes));
            assertTrue(directBuffer.isDirect());
            ByteBuffer heapBuffer = MaterialBox.copyHeap(bytes);
            assertEquals(heapBuffer, ByteBuffer.wrap(bytes));
            assertFalse(heapBuffer.isDirect());
            assertArrayEquals(bytes, copyBytes(ByteBuffer.wrap(bytes)));
        }
        {
            // buffer copy
            byte[] bytes = new byte[1024];
            Arrays.fill(bytes, (byte) 66);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            ByteBuffer directBuffer = MaterialBox.copyDirect(buffer);
            assertEquals(directBuffer, ByteBuffer.wrap(bytes));
            assertTrue(directBuffer.isDirect());
            ByteBuffer heapBuffer = MaterialBox.copyHeap(buffer);
            assertEquals(heapBuffer, ByteBuffer.wrap(bytes));
            assertFalse(heapBuffer.isDirect());
            assertArrayEquals(bytes, copyBytes(ByteBuffer.wrap(bytes)));
        }
        {
            // copy buffer
            byte[] bytes = new byte[1024];
            Arrays.fill(bytes, (byte) 66);
            ByteBuffer copyHeap = copyBuffer(ByteBuffer.wrap(bytes));
            assertFalse(copyHeap.isDirect());
            assertEquals(copyHeap, ByteBuffer.wrap(bytes));
            ByteBuffer copyDirect = copyBuffer(copyDirect(bytes));
            assertTrue(copyDirect.isDirect());
            assertEquals(copyDirect, copyDirect(bytes));
        }
    }

    @Test
    public void testMaterialCharBuffer() {
        {
            // padding
            char[] chars = new char[1024];
            Arrays.fill(chars, (char) 66);
            CharBuffer bb = MaterialBox.copyPadding(chars);
            assertEquals(bb.arrayOffset(), 10);
            assertEquals(bb.position(), 0);
            assertEquals(bb.limit(), chars.length);
            assertEquals(bb.capacity(), chars.length);
        }
        {
            // chars copy
            char[] chars = new char[1024];
            Arrays.fill(chars, (char) 66);
            CharBuffer directBuffer = MaterialBox.copyDirect(chars);
            assertEquals(directBuffer, CharBuffer.wrap(chars));
            assertTrue(directBuffer.isDirect());
            CharBuffer heapBuffer = MaterialBox.copyHeap(chars);
            assertEquals(heapBuffer, CharBuffer.wrap(chars));
            assertFalse(heapBuffer.isDirect());
            assertArrayEquals(chars, copyChars(CharBuffer.wrap(chars)));
        }
        {
            // buffer copy
            char[] chars = new char[1024];
            Arrays.fill(chars, (char) 66);
            CharBuffer buffer = CharBuffer.wrap(chars);
            CharBuffer directBuffer = MaterialBox.copyDirect(buffer);
            assertEquals(directBuffer, CharBuffer.wrap(chars));
            assertTrue(directBuffer.isDirect());
            CharBuffer heapBuffer = MaterialBox.copyHeap(buffer);
            assertEquals(heapBuffer, CharBuffer.wrap(chars));
            assertFalse(heapBuffer.isDirect());
            assertArrayEquals(chars, copyChars(CharBuffer.wrap(chars)));
        }
        {
            // copy buffer
            char[] chars = new char[1024];
            Arrays.fill(chars, (char) 66);
            CharBuffer copyHeap = copyBuffer(CharBuffer.wrap(chars));
            assertFalse(copyHeap.isDirect());
            assertEquals(copyHeap, CharBuffer.wrap(chars));
            CharBuffer copyDirect = copyBuffer(copyDirect(chars));
            assertTrue(copyDirect.isDirect());
            assertEquals(copyDirect, copyDirect(chars));
        }
    }

    @Test
    public void testException() {
        assertThrows(KitvaTestException.class, () -> {
            throw new KitvaTestException();
        });
        assertThrows(KitvaTestException.class, () -> {
            throw new KitvaTestException("");
        });
        assertThrows(KitvaTestException.class, () -> {
            throw new KitvaTestException("", new RuntimeException());
        });
        assertThrows(KitvaTestException.class, () -> {
            throw new KitvaTestException(new RuntimeException());
        });
        assertThrows(TestIOException.class, () -> {
            throw new TestIOException();
        });
        assertThrows(TestIOException.class, () -> {
            throw new TestIOException("");
        });
        assertThrows(TestIOException.class, () -> {
            throw new TestIOException("", new RuntimeException());
        });
        assertThrows(TestIOException.class, () -> {
            throw new TestIOException(new RuntimeException());
        });
    }

    private static final class Tt {

        private static void throwError() {
            throw new KitvaTestException();
        }

        private static String string() {
            return "123";
        }
    }
}
