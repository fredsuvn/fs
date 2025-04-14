package test.base.chars;

import org.testng.annotations.Test;
import test.TU;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.chars.JieChars;

import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.testng.Assert.*;

public class CharsTest {

    @Test
    public void testChars() throws Exception {
        {
            // chars and buffer
            assertEquals(JieChars.emptyChars(), new char[0]);
            assertEquals(JieChars.emptyBuffer(), CharBuffer.allocate(0));
            char[] chars = JieRandom.fill(new char[10086]);
            CharBuffer buffer = JieChars.copyBuffer(chars);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), chars.length);
            CharBuffer bufferDir = JieChars.copyBuffer(chars, true);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), chars.length);
            assertEquals(buffer, bufferDir);
            CharBuffer buffer2 = JieChars.copyBuffer(buffer);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), chars.length);
            CharBuffer bufferDir2 = JieChars.copyBuffer(bufferDir);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), chars.length);
            assertEquals(buffer, buffer2);
            assertNotSame(buffer, buffer2);
            assertEquals(bufferDir, bufferDir2);
            assertNotSame(bufferDir, bufferDir2);
            assertEquals(buffer2, bufferDir2);
            assertNotSame(buffer2, bufferDir2);
            assertEquals(JieChars.copyChars(buffer), chars);
            assertNotSame(JieChars.copyChars(buffer), chars);
            assertEquals(JieChars.copyChars(buffer2), chars);
            assertNotSame(JieChars.copyChars(buffer2), chars);
            assertEquals(JieChars.copyChars(buffer), JieChars.copyChars(buffer2));
            assertNotSame(JieChars.copyChars(buffer), JieChars.copyChars(buffer2));
            assertEquals(JieChars.copyChars(bufferDir), chars);
            assertNotSame(JieChars.copyChars(bufferDir), chars);
            assertEquals(JieChars.copyChars(bufferDir2), chars);
            assertNotSame(JieChars.copyChars(bufferDir2), chars);
            assertEquals(JieChars.copyChars(bufferDir), JieChars.copyChars(bufferDir2));
            assertNotSame(JieChars.copyChars(bufferDir), JieChars.copyChars(bufferDir2));
            assertEquals(JieChars.copyChars(buffer), JieChars.copyChars(bufferDir));
            assertNotSame(JieChars.copyChars(buffer), JieChars.copyChars(bufferDir));
            assertEquals(buffer.remaining(), chars.length);
            assertEquals(JieChars.getChars(buffer), chars);
            assertEquals(buffer.remaining(), 0);
            assertEquals(bufferDir.remaining(), chars.length);
            assertEquals(JieChars.getChars(bufferDir), chars);
            assertEquals(bufferDir.remaining(), 0);
            CharBuffer src = TU.bufferDangling(chars);
            CharBuffer dst = CharBuffer.allocate(chars.length * 2);
            JieChars.putBuffer(src, dst, chars.length);
            dst.flip();
            assertEquals(dst.remaining(), chars.length);
            assertEquals(JieChars.getChars(dst), chars);
            assertEquals(dst.remaining(), 0);
            assertEquals(src.remaining(), 0);
            src = TU.bufferDangling(chars);
            CharBuffer slice = JieChars.slice(src, 2, 222);
            assertEquals(src.position(), 0);
            assertEquals(src.limit(), chars.length);
            assertEquals(src.capacity(), chars.length);
            assertEquals(slice.position(), 0);
            assertEquals(slice.limit(), 222);
            assertEquals(slice.capacity(), 222);
            assertEquals(JieChars.getChars(slice), Arrays.copyOfRange(chars, 2, 2 + 222));
            assertEquals(slice.position(), slice.limit());
            assertSame(JieChars.slice(CharBuffer.allocate(99), 0, 0), JieChars.emptyBuffer());
            assertTrue(JieChars.isEmpty(null));
            assertTrue(JieChars.isEmpty(JieChars.emptyBuffer()));
            assertFalse(JieChars.isEmpty(CharBuffer.wrap(new char[10])));
        }

        {
            // charset
            assertEquals(JieChars.defaultCharset(), StandardCharsets.UTF_8);
            assertEquals(JieChars.latinCharset(), StandardCharsets.ISO_8859_1);
            assertEquals(JieChars.jvmCharset(), JieChars.defaultCharset());
            assertEquals(JieChars.charset(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8);
            assertNull(JieChars.charset(null));
            JieChars.nativeCharset();
            String nativesClassName = JieChars.class.getName() + "$Natives";
            Class<?> nativesClass = Class.forName(nativesClassName);
            Method search = nativesClass.getDeclaredMethod("search", String[].class);
            search.setAccessible(true);
            String[] args = {new String(JieRandom.fill(new char[1024])), JieSystem.KEY_OF_FILE_ENCODING};
            search.invoke(null, (Object) args);
            String[] args2 = {new String(JieRandom.fill(new char[1024]))};
            assertNull(search.invoke(null, (Object) args2));
        }
    }

    @Test
    public void testCopy() {

    }
}
