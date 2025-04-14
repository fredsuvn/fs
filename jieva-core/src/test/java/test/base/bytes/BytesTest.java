package test.base.bytes;

import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.testng.Assert.*;

public class BytesTest {

    @Test
    public void testBytes() {
        {
            // bytes and buffer
            assertEquals(JieBytes.emptyBytes(), new byte[0]);
            assertEquals(JieBytes.emptyBuffer(), ByteBuffer.allocate(0));
            byte[] bytes = JieRandom.fill(new byte[10086]);
            ByteBuffer buffer = JieBytes.copyBuffer(bytes);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), bytes.length);
            ByteBuffer bufferDir = JieBytes.copyBuffer(bytes, true);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), bytes.length);
            assertEquals(buffer, bufferDir);
            ByteBuffer buffer2 = JieBytes.copyBuffer(buffer);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), bytes.length);
            ByteBuffer bufferDir2 = JieBytes.copyBuffer(bufferDir);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), bytes.length);
            assertEquals(buffer, buffer2);
            assertNotSame(buffer, buffer2);
            assertEquals(bufferDir, bufferDir2);
            assertNotSame(bufferDir, bufferDir2);
            assertEquals(buffer2, bufferDir2);
            assertNotSame(buffer2, bufferDir2);
            assertEquals(JieBytes.copyBytes(buffer), bytes);
            assertNotSame(JieBytes.copyBytes(buffer), bytes);
            assertEquals(JieBytes.copyBytes(buffer2), bytes);
            assertNotSame(JieBytes.copyBytes(buffer2), bytes);
            assertEquals(JieBytes.copyBytes(buffer), JieBytes.copyBytes(buffer2));
            assertNotSame(JieBytes.copyBytes(buffer), JieBytes.copyBytes(buffer2));
            assertEquals(JieBytes.copyBytes(bufferDir), bytes);
            assertNotSame(JieBytes.copyBytes(bufferDir), bytes);
            assertEquals(JieBytes.copyBytes(bufferDir2), bytes);
            assertNotSame(JieBytes.copyBytes(bufferDir2), bytes);
            assertEquals(JieBytes.copyBytes(bufferDir), JieBytes.copyBytes(bufferDir2));
            assertNotSame(JieBytes.copyBytes(bufferDir), JieBytes.copyBytes(bufferDir2));
            assertEquals(JieBytes.copyBytes(buffer), JieBytes.copyBytes(bufferDir));
            assertNotSame(JieBytes.copyBytes(buffer), JieBytes.copyBytes(bufferDir));
            assertEquals(buffer.remaining(), bytes.length);
            assertEquals(JieBytes.getBytes(buffer), bytes);
            assertEquals(buffer.remaining(), 0);
            assertEquals(bufferDir.remaining(), bytes.length);
            assertEquals(JieBytes.getBytes(bufferDir), bytes);
            assertEquals(bufferDir.remaining(), 0);
            ByteBuffer src = TU.bufferDangling(bytes);
            ByteBuffer dst = ByteBuffer.allocateDirect(bytes.length * 2);
            JieBytes.putBuffer(src, dst, bytes.length);
            dst.flip();
            assertEquals(dst.remaining(), bytes.length);
            assertEquals(JieBytes.getBytes(dst), bytes);
            assertEquals(dst.remaining(), 0);
            assertEquals(src.remaining(), 0);
            src = TU.bufferDangling(bytes);
            ByteBuffer slice = JieBytes.slice(src, 2, 222);
            assertEquals(src.position(), 0);
            assertEquals(src.limit(), bytes.length);
            assertEquals(src.capacity(), bytes.length);
            assertEquals(slice.position(), 0);
            assertEquals(slice.limit(), 222);
            assertEquals(slice.capacity(), 222);
            assertEquals(JieBytes.getBytes(slice), Arrays.copyOfRange(bytes, 2, 2 + 222));
            assertEquals(slice.position(), slice.limit());
            assertSame(JieBytes.slice(ByteBuffer.allocate(99), 0, 0), JieBytes.emptyBuffer());
            assertTrue(JieBytes.isEmpty(null));
            assertTrue(JieBytes.isEmpty(JieBytes.emptyBuffer()));
            assertFalse(JieBytes.isEmpty(ByteBuffer.wrap(new byte[10])));
        }
    }

    @Test
    public void testCopy() {
        byte[] bytes = JieRandom.fill(new byte[10086]);
        ByteBuffer buffer = JieBytes.copyBuffer(bytes);
    }
}
