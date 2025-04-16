package test.base.chars;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.chars.JieChars;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class CharsTest {

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
