package tests.base.string;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.exception.UnknownArrayTypeException;
import xyz.sunqian.common.base.string.StringKit;
import xyz.sunqian.common.base.string.StringView;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Objects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class StringTest implements DataTest, PrintTest {

    @Test
    public void testIndexOf() {
        testIndexOf("123", "123");
        testIndexOf(StringView.of("123"), "123");
        testIndexOf(StringView.of("123"), "1234");
        assertEquals(StringKit.indexOf(StringView.of("123"), "", 100), 3);
        assertEquals(StringKit.indexOf("123", StringView.of("2")), "123".indexOf("2"));
        assertEquals(StringKit.lastIndexOf("123", StringView.of("2")), "123".lastIndexOf("2"));
    }

    private void testIndexOf(CharSequence chars, CharSequence subChars) {
        for (int i = 0; i < subChars.length(); i++) {
            for (int j = i; j < subChars.length() + 1; j++) {
                CharSequence sub = subChars.subSequence(i, j);
                // println("chars=" + chars + ", sub=" + sub);
                assertEquals(
                    StringKit.indexOf(chars, sub),
                    chars.toString().indexOf(sub.toString()),
                    "chars=" + chars + ", sub=" + sub
                );
                assertEquals(
                    StringKit.lastIndexOf(chars, sub),
                    chars.toString().lastIndexOf(sub.toString()),
                    "chars=" + chars + ", sub=" + sub
                );
                if (sub.length() == 1) {
                    assertEquals(
                        StringKit.indexOf(chars, sub.charAt(0)),
                        chars.toString().indexOf(sub.charAt(0)),
                        "chars=" + chars + ", sub=" + sub
                    );
                    assertEquals(
                        StringKit.lastIndexOf(chars, sub.charAt(0)),
                        chars.toString().lastIndexOf(sub.charAt(0)),
                        "chars=" + chars + ", sub=" + sub
                    );
                }
                for (int k = -1; k < chars.length() + 1; k++) {
                    assertEquals(
                        StringKit.indexOf(chars, sub, k),
                        chars.toString().indexOf(sub.toString(), k),
                        "chars=" + chars + ", sub=" + sub
                    );
                    assertEquals(
                        StringKit.lastIndexOf(chars, sub, k),
                        chars.toString().lastIndexOf(sub.toString(), k),
                        "chars=" + chars + ", sub=" + sub
                    );
                    if (sub.length() == 1) {
                        assertEquals(
                            StringKit.indexOf(chars, sub.charAt(0), k),
                            chars.toString().indexOf(sub.charAt(0), k),
                            "chars=" + chars + ", sub=" + sub
                        );
                        assertEquals(
                            StringKit.lastIndexOf(chars, sub.charAt(0), k),
                            chars.toString().lastIndexOf(sub.charAt(0), k),
                            "chars=" + chars + ", sub=" + sub
                        );
                    }
                }
            }
        }
    }

    @Test
    public void testStartsEndsWith() {
        testStartsEndsWith("123", "123");
    }

    private void testStartsEndsWith(CharSequence chars, CharSequence subChars) {
        for (int i = 0; i < subChars.length(); i++) {
            for (int j = i; j < subChars.length() + 1; j++) {
                CharSequence sub = subChars.subSequence(i, j);
                // println("chars=" + chars + ", sub=" + sub);
                assertEquals(
                    StringKit.startsWith(chars, sub),
                    chars.toString().startsWith(sub.toString()),
                    "chars=" + chars + ", sub=" + sub
                );
                assertEquals(
                    StringKit.endsWith(chars, sub),
                    chars.toString().endsWith(sub.toString()),
                    "chars=" + chars + ", sub=" + sub
                );
                for (int k = -1; k < chars.length() + 1; k++) {
                    assertEquals(
                        StringKit.startsWith(chars, sub, k),
                        chars.toString().startsWith(sub.toString(), k),
                        "chars=" + chars + ", sub=" + sub + ", k=" + k
                    );
                }
            }
        }
    }

    @Test
    public void testToString() throws Exception {
        String str = "str";
        assertEquals(StringKit.toString(str), Objects.toString(str));
        Object[] strs = {"str1", "str2"};
        assertEquals(StringKit.toStringAll(strs), Arrays.toString(strs));
        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(StringKit.toString(strss), Arrays.deepToString(strss));
        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(StringKit.toStringWith(strss2, true, false), Arrays.toString(strss2));
        assertEquals(StringKit.toStringWith(strss2, false, false), Objects.toString(strss2));
        assertEquals(StringKit.toString(new boolean[]{true, false}), Arrays.toString(new boolean[]{true, false}));
        assertEquals(StringKit.toString(new byte[]{6, 66}), Arrays.toString(new byte[]{6, 66}));
        assertEquals(StringKit.toString(new short[]{6, 66}), Arrays.toString(new short[]{6, 66}));
        assertEquals(StringKit.toString(new char[]{6, 66}), Arrays.toString(new char[]{6, 66}));
        assertEquals(StringKit.toString(new int[]{6, 66}), Arrays.toString(new int[]{6, 66}));
        assertEquals(StringKit.toString(new long[]{6, 66}), Arrays.toString(new long[]{6, 66}));
        assertEquals(StringKit.toString(new float[]{6, 66}), Arrays.toString(new float[]{6, 66}));
        assertEquals(StringKit.toString(new double[]{6, 66}), Arrays.toString(new double[]{6, 66}));

        // null:
        assertEquals(StringKit.toString(null), Objects.toString(null));
        assertEquals(StringKit.toStringWith(null, false, false), Objects.toString(null));

        // unknown:
        Method toStringArray = StringKit.class.getDeclaredMethod("toStringArray", Object.class, boolean.class);
        toStringArray.setAccessible(true);
        InvocationTargetException e = expectThrows(InvocationTargetException.class, () ->
            toStringArray.invoke(null, "str", true));
        assertTrue(e.getCause() instanceof UnknownArrayTypeException);
        // invokeThrows(UnknownArrayTypeException.class, toStringArray, null, "str", true);
    }

    @Test
    public void testCharEquals() {
        assertTrue(StringKit.charEquals("", ""));
        assertTrue(StringKit.charEquals("123", "123"));
        assertFalse(StringKit.charEquals("123", "124"));
        assertFalse(StringKit.charEquals("123", "12"));
        assertFalse(StringKit.charEquals("1", ""));
        assertFalse(StringKit.charEquals("", "1"));
    }

    @Test
    public void testCharsCopy() {
        testCharsCopy("12345");
        testCharsCopy(StringView.of("12345"));
        // error
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 0, 6, new char[5], 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 0, 5, new char[5], 1));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 5, 0, new char[5], 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 0, new char[1], 0, 5));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 8, new char[5], 0, 5));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 0, new char[5], 0, 6));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 0, new char[5], 6, 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 6, new char[5], 0, 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy("12345", 0, new char[5], 0, 6));

        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 0, 6, new char[5], 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 0, 5, new char[5], 1));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 5, 0, new char[5], 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 0, new char[1], 0, 5));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 8, new char[5], 0, 5));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 0, new char[5], 0, 6));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 0, new char[5], 6, 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 6, new char[5], 0, 0));
        expectThrows(IndexOutOfBoundsException.class, () ->
            StringKit.charsCopy(StringView.of("12345"), 0, new char[5], 0, 6));
    }

    private void testCharsCopy(CharSequence chars) {
        for (int i = 0; i < chars.length(); i++) {
            for (int j = i; j <= chars.length(); j++) {
                for (int k = 0; k < 3; k++) {
                    char[] dst = new char[j - i + k];
                    StringKit.charsCopy(chars, i, j, dst, k);
                    char[] dstCopy = new char[j - i + k];
                    StringKit.charsCopy(chars, i, dstCopy, k, j - i);
                    assertEquals(dst, dstCopy);
                    char[] dst1 = new char[j - i + k];
                    chars.toString().getChars(i, j, dst1, k);
                    assertEquals(dst, dst1);
                    char[] dst2 = new char[j - i + k];
                    System.arraycopy(chars.toString().toCharArray(), i, dst2, k, j - i);
                    assertEquals(dst, dst2);
                }
            }
        }
    }

    @Test
    public void testEmptyAndBlank() {
        assertTrue(StringKit.isBlank(""));
        assertTrue(StringKit.isBlank(" "));
        assertTrue(StringKit.isBlank(null));
        assertFalse(StringKit.isBlank(" a "));
        assertFalse(StringKit.isNonBlank(""));
        assertFalse(StringKit.isNonBlank(" "));
        assertFalse(StringKit.isNonBlank(null));
        assertTrue(StringKit.isNonBlank(" a "));
        assertTrue(StringKit.isNonEmpty(" a "));
        assertTrue(StringKit.anyEmpty(" ", ""));
        assertFalse(StringKit.anyEmpty(" ", " "));
        assertTrue(StringKit.allEmpty("", ""));
        assertFalse(StringKit.allEmpty("", " "));
        assertTrue(StringKit.anyBlank("a", ""));
        assertFalse(StringKit.anyBlank("a", "a"));
        assertTrue(StringKit.allBlank(" ", ""));
        assertFalse(StringKit.allBlank("", "a"));
    }

    @Test
    public void testEncode() {
        char[] chars = randomChars(20, 'a', 'z');
        byte[] en = new String(chars).getBytes(CharsKit.defaultCharset());
        assertEquals(StringKit.getBytes(chars), en);
        assertEquals(StringKit.getBytes(CharBuffer.wrap(chars)), en);
    }

    @Test
    public void testCase() {
        // case
        assertTrue(StringKit.allUpperCase("ABC"));
        assertFalse(StringKit.allUpperCase("ABc"));
        assertFalse(StringKit.allUpperCase("AB中"));
        assertTrue(StringKit.allUpperCase(""));
        assertTrue(StringKit.allLowerCase("abc"));
        assertFalse(StringKit.allLowerCase("ABc"));
        assertFalse(StringKit.allLowerCase("ab中"));
        assertTrue(StringKit.allLowerCase(""));
        assertEquals(StringKit.upperCase("abc"), "ABC");
        assertEquals(StringKit.upperCase("aBc"), "ABC");
        assertEquals(StringKit.upperCase("abc中"), "ABC中");
        assertEquals(StringKit.upperCase(""), "");
        assertEquals(StringKit.lowerCase("ABC"), "abc");
        assertEquals(StringKit.lowerCase("aBc"), "abc");
        assertEquals(StringKit.lowerCase("aBc中"), "abc中");
        assertEquals(StringKit.lowerCase(""), "");
        // capitalize
        assertEquals(StringKit.capitalize("abc"), "Abc");
        assertEquals(StringKit.capitalize("a"), "A");
        assertEquals(StringKit.capitalize("A"), "A");
        assertEquals(StringKit.capitalize(""), "");
        assertEquals(StringKit.capitalize("ABc"), "ABc");
        assertEquals(StringKit.uncapitalize("Abc"), "abc");
        assertEquals(StringKit.uncapitalize("A"), "a");
        assertEquals(StringKit.uncapitalize("a"), "a");
        assertEquals(StringKit.uncapitalize(""), "");
        assertEquals(StringKit.uncapitalize("ABc"), "aBc");
        assertEquals(StringKit.uncapitalize("ABC"), "ABC");
    }
}
