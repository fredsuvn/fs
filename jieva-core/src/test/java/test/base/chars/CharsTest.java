package test.base.chars;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.chars.JieChars;

import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

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
