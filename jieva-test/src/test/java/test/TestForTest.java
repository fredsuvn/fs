package test;

import org.testng.annotations.Test;

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
import static xyz.sunqian.test.JieTest.reflectEquals;
import static xyz.sunqian.test.JieTest.reflectThrows;
import static xyz.sunqian.test.MaterialBox.createFile;
import static xyz.sunqian.test.MaterialBox.directBuffer;
import static xyz.sunqian.test.MaterialBox.heapBuffer;
import static xyz.sunqian.test.MaterialBox.paddedBuffer;

public class TestForTest {

    @Test
    public void testThrows() throws Exception {
        Method throwError = Tt.class.getDeclaredMethod("throwError");
        assertEquals(reflectThrows(TestException.class, throwError, null).getClass(), TestException.class);
    }

    @Test
    public void testEquals() throws Exception {
        Method string = Tt.class.getDeclaredMethod("string");
        reflectEquals(string, "123", null);
        expectThrows(AssertionError.class, () ->
            reflectEquals(string, "123", null, "123")
        );
    }

    @Test
    public void testFile() throws Exception {
        Path path = Paths.get("src", "test", "resources", "test.test");
        path.toFile().delete();
        byte[] data = {'1', '2', '3'};
        createFile(path, data);
        expectThrows(IllegalStateException.class, () -> createFile(path, data));
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
    public void testMaterialBuffer() {
        {
            // bytes
            byte[] bytes = new byte[1111];
            Arrays.fill(bytes, (byte) 66);
            ByteBuffer bb = paddedBuffer(bytes);
            assertEquals(bb.arrayOffset(), 10);
            assertEquals(bb.position(), 0);
            assertEquals(bb.limit(), bytes.length);
            assertEquals(bb.capacity(), bytes.length);
            ByteBuffer directBuffer = directBuffer(bytes);
            assertEquals(directBuffer, ByteBuffer.wrap(bytes));
            assertTrue(directBuffer.isDirect());
            ByteBuffer heapBuffer = heapBuffer(bytes);
            assertEquals(heapBuffer, ByteBuffer.wrap(bytes));
            assertFalse(heapBuffer.isDirect());
        }
        {
            // chars
            char[] chars = new char[1111];
            Arrays.fill(chars, (char) 66);
            CharBuffer cb = paddedBuffer(chars);
            assertEquals(cb.arrayOffset(), 10);
            assertEquals(cb.position(), 0);
            assertEquals(cb.limit(), chars.length);
            assertEquals(cb.capacity(), chars.length);
            CharBuffer directBuffer = directBuffer(chars);
            assertEquals(directBuffer, CharBuffer.wrap(chars));
            assertTrue(directBuffer.isDirect());
            CharBuffer heapBuffer = heapBuffer(chars);
            assertEquals(heapBuffer, CharBuffer.wrap(chars));
            assertFalse(heapBuffer.isDirect());
        }
    }

    private static final class Tt {

        private static void throwError() {
            throw new TestException();
        }

        private static String string() {
            return "123";
        }
    }

    public static class TestException extends RuntimeException {

        public TestException() {
            super();
        }

        public TestException(String message) {
            super(message);
        }

        public TestException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
