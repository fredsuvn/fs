package tests.core.base.chars;

import internal.utils.Asserter;
import internal.utils.ErrorAppender;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.system.SystemKeys;
import space.sunqian.fs.io.IORuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharsTest implements Asserter, TestPrint {

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

    @Test
    public void testUnicode() throws Exception {
        {
            StringBuilder appender = new StringBuilder();

            // to Unicode
            assertEquals("\\u0061", CharsKit.toUnicode('a', false));
            assertEquals("\\u0068", CharsKit.toUnicode('h', false));
            assertEquals("\\u4E2D", CharsKit.toUnicode('中'));
            assertEquals("\\u4e2d", CharsKit.toUnicode('中', false));
            CharsKit.toUnicode('a', false, appender);
            assertEquals("\\u0061", appender.toString());
            appender.setLength(0);
            CharsKit.toUnicode('h', false, appender);
            assertEquals("\\u0068", appender.toString());
            appender.setLength(0);
            CharsKit.toUnicode('中', true, appender);
            assertEquals("\\u4E2D", appender.toString());
            appender.setLength(0);
            CharsKit.toUnicode('中', false, appender);
            assertEquals("\\u4e2d", appender.toString());
            appender.setLength(0);
            assertThrows(IORuntimeException.class, () -> CharsKit.toUnicode('0', true, new ErrorAppender()));

            // control
            for (int i = 0; i < 256; i++) {
                String lower = String.format("\\u%04x", i);
                String higher = String.format("\\u%04X", i);
                assertEquals(
                    lower,
                    CharsKit.toUnicode((char) i, false)
                );
                assertEquals(
                    higher,
                    CharsKit.toUnicode((char) i)
                );
                CharsKit.toUnicode((char) i, false, appender);
                assertEquals(lower, appender.toString());
                appender.setLength(0);
                CharsKit.toUnicode((char) i, true, appender);
                assertEquals(higher, appender.toString());
                appender.setLength(0);
            }

            // emoji
            String emoji = "\uD83D\uDE00";
            String emoji0 = "\\uD83D";
            String emoji1 = "\\uDE00";
            assertEquals(
                emoji0,
                CharsKit.toUnicode(emoji.charAt(0), true)
            );
            assertEquals(
                emoji1,
                CharsKit.toUnicode(emoji.charAt(1), true)
            );
            CharsKit.toUnicode(emoji.charAt(0), true, appender);
            assertEquals(emoji0, appender.toString());
            appender.setLength(0);
            CharsKit.toUnicode(emoji.charAt(1), true, appender);
            assertEquals(emoji1, appender.toString());
            appender.setLength(0);
        }
        {
            // Unicode to char
            assertEquals('a', CharsKit.unicodeToChar('0', '0', '6', '1'));
            assertEquals('h', CharsKit.unicodeToChar('0', '0', '6', '8'));
            assertEquals('中', CharsKit.unicodeToChar('4', 'e', '2', 'd'));
            assertEquals('中', CharsKit.unicodeToChar('4', 'E', '2', 'D'));
            assertThrows(IllegalArgumentException.class, () -> CharsKit.unicodeToChar('0', '0', '6', 'g'));
            assertThrows(IllegalArgumentException.class, () -> CharsKit.unicodeToChar((char) ('0' - 1), '0', '6', '1'));
            assertThrows(IllegalArgumentException.class, () -> CharsKit.unicodeToChar((char) ('a' - 1), '0', '6', '1'));
            assertThrows(IllegalArgumentException.class, () -> CharsKit.unicodeToChar((char) ('A' - 1), '0', '6', '1'));

            // control
            for (int i = 0; i < 256; i++) {
                String lower = String.format("\\u%04x", i);
                String higher = String.format("\\u%04X", i);
                assertEquals(
                    (char) i,
                    CharsKit.unicodeToChar(lower.charAt(2), lower.charAt(3), lower.charAt(4), lower.charAt(5))
                );
                assertEquals(
                    (char) i,
                    CharsKit.unicodeToChar(higher.charAt(2), higher.charAt(3), higher.charAt(4), higher.charAt(5))
                );
            }

            // emoji
            String emoji = "\uD83D\uDE00";
            assertEquals(
                emoji.charAt(0),
                CharsKit.unicodeToChar('D', '8', '3', 'D')
            );
            assertEquals(
                emoji.charAt(1),
                CharsKit.unicodeToChar('D', 'E', '0', '0')
            );
        }
    }
}
