package test.base.chars;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.chars.JieChars;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static xyz.sunqian.test.JieTest.reflectEquals;

public class CharsTest {

    @Test
    public void testChars() {
        assertTrue(JieChars.isEmpty(null));
        assertTrue(JieChars.isEmpty(CharBuffer.allocate(0)));
        assertTrue(JieChars.isEmpty(CharBuffer.wrap(new char[10], 5, 0)));
        assertTrue(JieChars.isEmpty(JieChars.emptyBuffer()));
        assertTrue(JieChars.isEmpty(CharBuffer.wrap(JieChars.emptyChars())));
    }

    @Test
    public void testCharset() throws Exception {
        assertEquals(JieChars.defaultCharset(), StandardCharsets.UTF_8);
        assertEquals(JieChars.latinCharset(), StandardCharsets.ISO_8859_1);
        assertEquals(JieChars.jvmCharset(), JieChars.defaultCharset());
        assertEquals(JieChars.charset(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8);
        assertNull(JieChars.charset(null));

        {
            // native chars
            Charset nativeCharset = JieChars.nativeCharset();
            String nativesClassName = JieChars.class.getName() + "$Natives";
            Class<?> nativesClass = Class.forName(nativesClassName);
            Field nc = nativesClass.getDeclaredField("NATIVE_CHARSET");
            nc.setAccessible(true);
            assertEquals(nativeCharset, nc.get(null));
            Charset fileCharset = JieChars.charset(System.getProperty(JieSystem.KEY_OF_FILE_ENCODING));
            Method search = nativesClass.getDeclaredMethod("search", String[].class);
            reflectEquals(search, fileCharset, null, (Object) new String[]{"UTF888", JieSystem.KEY_OF_FILE_ENCODING});
            reflectEquals(search, null, null, (Object) new String[]{"UTF888"});
        }
    }
}
