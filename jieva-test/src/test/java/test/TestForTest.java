package test;

import org.testng.annotations.Test;
import xyz.sunqian.test.JieAssert;
import xyz.sunqian.test.JieTestException;
import xyz.sunqian.test.MaterialBox;
import xyz.sunqian.test.TestIOException;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;
import static xyz.sunqian.test.JieAssert.invokeEquals;
import static xyz.sunqian.test.JieAssert.invokeThrows;
import static xyz.sunqian.test.MaterialBox.copyBuffer;
import static xyz.sunqian.test.MaterialBox.copyBytes;
import static xyz.sunqian.test.MaterialBox.copyChars;
import static xyz.sunqian.test.MaterialBox.copyDirect;
import static xyz.sunqian.test.MaterialBox.newFile;

public class TestForTest {

    @Test
    public void testThrows() throws Exception {
        Method throwError = Tt.class.getDeclaredMethod("throwError");
        assertEquals(invokeThrows(JieTestException.class, throwError, null).getClass(), JieTestException.class);
        Method string = Tt.class.getDeclaredMethod("string");
        assertEquals(invokeThrows(JieAssert.NoThrows.class, string, null).getClass(), JieAssert.NoThrows.class);
    }

    @Test
    public void testEquals() throws Exception {
        Method string = Tt.class.getDeclaredMethod("string");
        invokeEquals("123", string, null);
        expectThrows(AssertionError.class, () ->
            invokeEquals("123", string, null, "123")
        );
    }

    @Test
    public void testFile() throws Exception {
        Path path = Paths.get("src", "test", "resources", "test.test");
        path.toFile().delete();
        byte[] data = {'1', '2', '3'};
        newFile(path, data);
        expectThrows(IllegalStateException.class, () -> newFile(path, data));
        FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 1);
        for (int i = 0; i >= 0; ) {
            i = fc.read(buffer);
        }
        buffer.flip();
        assertEquals(Arrays.copyOf(buffer.array(), data.length), data);
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
            assertEquals(bytes, copyBytes(ByteBuffer.wrap(bytes)));
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
            assertEquals(bytes, copyBytes(ByteBuffer.wrap(bytes)));
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
            assertEquals(chars, copyChars(CharBuffer.wrap(chars)));
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
            assertEquals(chars, copyChars(CharBuffer.wrap(chars)));
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
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException();
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException("");
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException("", new RuntimeException());
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException(new RuntimeException());
        });
        expectThrows(TestIOException.class, () -> {
            throw new TestIOException();
        });
        expectThrows(TestIOException.class, () -> {
            throw new TestIOException("");
        });
        expectThrows(TestIOException.class, () -> {
            throw new TestIOException("", new RuntimeException());
        });
        expectThrows(TestIOException.class, () -> {
            throw new TestIOException(new RuntimeException());
        });
    }

    private static final class Tt {

        private static void throwError() {
            throw new JieTestException();
        }

        private static String string() {
            return "123";
        }
    }
}
