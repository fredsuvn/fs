package tests.base.chars;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.system.SystemKeys;
import xyz.sunqian.test.AssertTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class CharsTest implements AssertTest {

    @Test
    public void testChars() {
        assertTrue(CharsKit.isEmpty(null));
        assertTrue(CharsKit.isEmpty(CharBuffer.allocate(0)));
        assertTrue(CharsKit.isEmpty(CharBuffer.wrap(new char[10], 5, 0)));
        assertTrue(CharsKit.isEmpty(CharsKit.emptyBuffer()));
        assertTrue(CharsKit.isEmpty(CharBuffer.wrap(CharsKit.empty())));
    }

    @Test
    public void testCharset() throws Exception {
        assertEquals(CharsKit.defaultCharset(), StandardCharsets.UTF_8);
        assertEquals(CharsKit.latinCharset(), StandardCharsets.ISO_8859_1);
        assertEquals(CharsKit.jvmCharset(), CharsKit.defaultCharset());
        assertEquals(CharsKit.charset(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8);
        assertNull(CharsKit.charset(null));
        assertNotNull(CharsKit.localCharset());
        {
            // native chars
            Charset nativeCharset = CharsKit.nativeCharset();
            String nativesClassName = CharsKit.class.getName() + "$Natives";
            Class<?> nativesClass = Class.forName(nativesClassName);
            Field nc = nativesClass.getDeclaredField("NATIVE_CHARSET");
            nc.setAccessible(true);
            assertEquals(nativeCharset, nc.get(null));
            Charset fileCharset = CharsKit.charset(System.getProperty(SystemKeys.FILE_ENCODING));
            Method search = nativesClass.getDeclaredMethod("search", String[].class);
            invokeEquals(fileCharset, search, null, (Object) new String[]{"UTF888", SystemKeys.FILE_ENCODING});
            invokeEquals(null, search, null, (Object) new String[]{"UTF888"});
        }
    }
}
