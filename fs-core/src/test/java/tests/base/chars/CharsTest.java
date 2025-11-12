package tests.base.chars;

import internal.test.AssertTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.system.SystemKeys;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharsTest implements AssertTest {

    @Test
    public void testChars() {
        assertTrue(CharsKit.isEmpty(null));
        assertTrue(CharsKit.isEmpty(CharBuffer.allocate(0)));
        assertFalse(CharsKit.isEmpty(CharBuffer.allocate(1)));
        assertTrue(CharsKit.isEmpty(CharBuffer.allocate(0)));
        assertTrue(CharsKit.isEmpty(CharBuffer.wrap(new char[10], 5, 0)));
        assertTrue(CharsKit.isEmpty(CharsKit.emptyBuffer()));
        assertTrue(CharsKit.isEmpty(CharBuffer.wrap(CharsKit.empty())));
    }

    @Test
    public void testCharset() throws Exception {
        assertEquals(StandardCharsets.UTF_8, CharsKit.defaultCharset());
        assertEquals(StandardCharsets.ISO_8859_1, CharsKit.latinCharset());
        assertEquals(CharsKit.jvmCharset(), CharsKit.defaultCharset());
        assertEquals(StandardCharsets.UTF_8, CharsKit.charset(StandardCharsets.UTF_8.name()));
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
