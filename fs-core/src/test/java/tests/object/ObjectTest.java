package tests.object;

import internal.test.AssertTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.exception.UnknownArrayTypeException;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.ObjectKit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectTest implements AssertTest, PrintTest {

    @Test
    public void testEquals() throws Exception {
        assertTrue(ObjectKit.equals("", ""));
        assertFalse(ObjectKit.equals("", null));
        assertFalse(ObjectKit.equals(null, ""));
        assertFalse(ObjectKit.equals("1", "2"));
        assertFalse(ObjectKit.equals(new boolean[]{true}, ""));
        assertFalse(ObjectKit.equalsWith(new boolean[]{true}, new boolean[]{true}, false, false));
        assertTrue(ObjectKit.equalsAll("", "", ""));
        assertFalse(ObjectKit.equalsAll("1", "2", "3"));
        assertTrue(ObjectKit.equalsAll());
        assertTrue(ObjectKit.equalsAll(new Object[]{""}));
        assertTrue(ObjectKit.equalsAll(new Object[]{"", ""}));
        assertFalse(ObjectKit.equalsAll(new Object[]{"1", "2"}));

        // boolean
        assertTrue(ObjectKit.equals(new boolean[]{true}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new short[]{1}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new char[]{1}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new int[]{1}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new long[]{1}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new float[]{1}));
        assertFalse(ObjectKit.equals(new boolean[]{true}, new double[]{1}));

        // byte
        assertFalse(ObjectKit.equals(new byte[]{1}, new boolean[]{true}));
        assertTrue(ObjectKit.equals(new byte[]{1}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new byte[]{1}, new short[]{1}));
        assertFalse(ObjectKit.equals(new byte[]{1}, new char[]{1}));
        assertFalse(ObjectKit.equals(new byte[]{1}, new int[]{1}));
        assertFalse(ObjectKit.equals(new byte[]{1}, new long[]{1}));
        assertFalse(ObjectKit.equals(new byte[]{1}, new float[]{1}));
        assertFalse(ObjectKit.equals(new byte[]{1}, new double[]{1}));

        // short
        assertFalse(ObjectKit.equals(new short[]{1}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new short[]{1}, new byte[]{1}));
        assertTrue(ObjectKit.equals(new short[]{1}, new short[]{1}));
        assertFalse(ObjectKit.equals(new short[]{1}, new char[]{1}));
        assertFalse(ObjectKit.equals(new short[]{1}, new int[]{1}));
        assertFalse(ObjectKit.equals(new short[]{1}, new long[]{1}));
        assertFalse(ObjectKit.equals(new short[]{1}, new float[]{1}));
        assertFalse(ObjectKit.equals(new short[]{1}, new double[]{1}));

        // char
        assertFalse(ObjectKit.equals(new char[]{1}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new char[]{1}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new char[]{1}, new short[]{1}));
        assertTrue(ObjectKit.equals(new char[]{1}, new char[]{1}));
        assertFalse(ObjectKit.equals(new char[]{1}, new int[]{1}));
        assertFalse(ObjectKit.equals(new char[]{1}, new long[]{1}));
        assertFalse(ObjectKit.equals(new char[]{1}, new float[]{1}));
        assertFalse(ObjectKit.equals(new char[]{1}, new double[]{1}));

        // int
        assertFalse(ObjectKit.equals(new int[]{1}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new int[]{1}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new int[]{1}, new short[]{1}));
        assertFalse(ObjectKit.equals(new int[]{1}, new char[]{1}));
        assertTrue(ObjectKit.equals(new int[]{1}, new int[]{1}));
        assertFalse(ObjectKit.equals(new int[]{1}, new long[]{1}));
        assertFalse(ObjectKit.equals(new int[]{1}, new float[]{1}));
        assertFalse(ObjectKit.equals(new int[]{1}, new double[]{1}));

        // long
        assertFalse(ObjectKit.equals(new long[]{1}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new long[]{1}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new long[]{1}, new short[]{1}));
        assertFalse(ObjectKit.equals(new long[]{1}, new char[]{1}));
        assertFalse(ObjectKit.equals(new long[]{1}, new int[]{1}));
        assertTrue(ObjectKit.equals(new long[]{1}, new long[]{1}));
        assertFalse(ObjectKit.equals(new long[]{1}, new float[]{1}));
        assertFalse(ObjectKit.equals(new long[]{1}, new double[]{1}));

        // float
        assertFalse(ObjectKit.equals(new float[]{1}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new float[]{1}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new float[]{1}, new short[]{1}));
        assertFalse(ObjectKit.equals(new float[]{1}, new char[]{1}));
        assertFalse(ObjectKit.equals(new float[]{1}, new int[]{1}));
        assertFalse(ObjectKit.equals(new float[]{1}, new long[]{1}));
        assertTrue(ObjectKit.equals(new float[]{1}, new float[]{1}));
        assertFalse(ObjectKit.equals(new float[]{1}, new double[]{1}));

        // double
        assertFalse(ObjectKit.equals(new double[]{1}, new boolean[]{true}));
        assertFalse(ObjectKit.equals(new double[]{1}, new byte[]{1}));
        assertFalse(ObjectKit.equals(new double[]{1}, new short[]{1}));
        assertFalse(ObjectKit.equals(new double[]{1}, new char[]{1}));
        assertFalse(ObjectKit.equals(new double[]{1}, new int[]{1}));
        assertFalse(ObjectKit.equals(new double[]{1}, new long[]{1}));
        assertFalse(ObjectKit.equals(new double[]{1}, new float[]{1}));
        assertTrue(ObjectKit.equals(new double[]{1}, new double[]{1}));

        // object
        assertFalse(ObjectKit.equals(new Object[]{1}, new boolean[]{true}));
        assertTrue(ObjectKit.equalsWith(new Object[]{1}, new Object[]{1}, true, true));
        assertFalse(ObjectKit.equalsWith(new Object[]{new Object[]{1}}, new Object[]{new Object[]{1}}, true, false));

        // unknown:
        Method equalsArray = ObjectKit.class.getDeclaredMethod("equalsArray", Object.class, Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, equalsArray, null, "str", "str", true);
    }

    @Test
    public void testHashcode() throws Exception {
        String str = "str";
        assertEquals(ObjectKit.hashCode(str), Objects.hashCode(str));
        Object[] strs = {"str1", "str2"};
        assertEquals(ObjectKit.hashAll(strs), Arrays.hashCode(strs));
        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(ObjectKit.hashCode(strss), Arrays.deepHashCode(strss));
        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(ObjectKit.hashWith(strss2, true, false), Arrays.hashCode(strss2));
        assertEquals(ObjectKit.hashWith(strss2, false, false), Objects.hashCode(strss2));
        assertEquals(ObjectKit.hashCode(new boolean[]{true, false}), Arrays.hashCode(new boolean[]{true, false}));
        assertEquals(ObjectKit.hashCode(new byte[]{6, 66}), Arrays.hashCode(new byte[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new short[]{6, 66}), Arrays.hashCode(new short[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new char[]{6, 66}), Arrays.hashCode(new char[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new int[]{6, 66}), Arrays.hashCode(new int[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new long[]{6, 66}), Arrays.hashCode(new long[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new float[]{6, 66}), Arrays.hashCode(new float[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new double[]{6, 66}), Arrays.hashCode(new double[]{6, 66}));
        assertEquals(ObjectKit.id(str), System.identityHashCode(str));

        // null:
        assertEquals(ObjectKit.hashCode(null), Objects.hashCode(null));
        assertEquals(ObjectKit.hashWith(null, false, false), Objects.hashCode(null));

        // unknown:
        Method hashArray = ObjectKit.class.getDeclaredMethod("hashArray", Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, hashArray, null, "str", true);
    }

    @Test
    public void testException() {
        {
            // ObjectException
            assertThrows(ObjectException.class, () -> {
                throw new ObjectException();
            });
            assertThrows(ObjectException.class, () -> {
                throw new ObjectException("");
            });
            assertThrows(ObjectException.class, () -> {
                throw new ObjectException("", new RuntimeException());
            });
            assertThrows(ObjectException.class, () -> {
                throw new ObjectException(new RuntimeException());
            });
        }
    }
}
