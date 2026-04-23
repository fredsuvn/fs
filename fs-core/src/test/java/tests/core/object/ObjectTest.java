package tests.core.object;

import internal.utils.Asserter;
import internal.utils.TestPrint;
import lombok.Data;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.exception.UnknownArrayTypeException;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.ObjectKit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectTest implements Asserter, TestPrint {

    @Test
    public void testEquals() throws Exception {
        testBasicEquals();
        testArrayEquals();
        testObjectArrayEquals();
        testUnknownArrayType();
    }

    @Test
    public void testHashcode() throws Exception {
        testBasicHashCode();
        testArrayHashCode();
        testNullHashCode();
        testUnknownArrayHash();
    }

    @Test
    public void testGetPropertyValue() {
        Z z = new Z();
        Map<?, ?> map = MapKit.hashMap("zzz", z);

        // Test normal case
        assertEquals("v", ObjectKit.getPropertyValue(map, "zzz.y.x.v"));

        // Test non-existent path
        assertNull(ObjectKit.getPropertyValue(map, "zzz.y.w.v"));

        // Test null map
        assertNull(ObjectKit.getPropertyValue(null, "zzz.y.x.v"));

        // Test null intermediate object
        z.y.x = null;
        assertNull(ObjectKit.getPropertyValue(map, "zzz.y.x.v"));

        // Test empty map
        map.clear();
        assertNull(ObjectKit.getPropertyValue(map, "zzz.y.x.v"));
    }

    @Test
    public void testException() {
        assertThrows(ObjectException.class, () -> {throw new ObjectException();});
        assertThrows(ObjectException.class, () -> {throw new ObjectException("");});
        assertThrows(ObjectException.class, () -> {throw new ObjectException("", new RuntimeException());});
        assertThrows(ObjectException.class, () -> {throw new ObjectException(new RuntimeException());});
    }

    private void testBasicEquals() {
        // Test basic equals
        assertTrue(ObjectKit.equals("", ""));
        assertFalse(ObjectKit.equals("", null));
        assertFalse(ObjectKit.equals(null, ""));
        assertFalse(ObjectKit.equals("1", "2"));
        assertFalse(ObjectKit.equals(new boolean[]{true}, ""));

        // Test equalsWith
        assertFalse(ObjectKit.equalsWith(new boolean[]{true}, new boolean[]{true}, false, false));

        // Test equalsAll
        assertTrue(ObjectKit.equalsAll("", "", ""));
        assertFalse(ObjectKit.equalsAll("1", "2", "3"));
        assertTrue(ObjectKit.equalsAll());
        assertTrue(ObjectKit.equalsAll(new Object[]{""}));
        assertTrue(ObjectKit.equalsAll(new Object[]{"", ""}));
        assertFalse(ObjectKit.equalsAll(new Object[]{"1", "2"}));
    }

    private void testArrayEquals() {
        // boolean array
        assertTrue(ObjectKit.equals(new boolean[]{true}, new boolean[]{true}));
        testArrayTypeMismatch(new boolean[]{true});

        // byte array
        assertTrue(ObjectKit.equals(new byte[]{1}, new byte[]{1}));
        testArrayTypeMismatch(new byte[]{1});

        // short array
        assertTrue(ObjectKit.equals(new short[]{1}, new short[]{1}));
        testArrayTypeMismatch(new short[]{1});

        // char array
        assertTrue(ObjectKit.equals(new char[]{1}, new char[]{1}));
        testArrayTypeMismatch(new char[]{1});

        // int array
        assertTrue(ObjectKit.equals(new int[]{1}, new int[]{1}));
        testArrayTypeMismatch(new int[]{1});

        // long array
        assertTrue(ObjectKit.equals(new long[]{1}, new long[]{1}));
        testArrayTypeMismatch(new long[]{1});

        // float array
        assertTrue(ObjectKit.equals(new float[]{1}, new float[]{1}));
        testArrayTypeMismatch(new float[]{1});

        // double array
        assertTrue(ObjectKit.equals(new double[]{1}, new double[]{1}));
        testArrayTypeMismatch(new double[]{1});
    }

    private void testArrayTypeMismatch(Object array) {
        Class<?> arrayClass = array.getClass();
        if (!arrayClass.equals(boolean[].class)) {
            assertFalse(ObjectKit.equals(array, new boolean[]{true}));
        }
        if (!arrayClass.equals(byte[].class)) {
            assertFalse(ObjectKit.equals(array, new byte[]{1}));
        }
        if (!arrayClass.equals(short[].class)) {
            assertFalse(ObjectKit.equals(array, new short[]{1}));
        }
        if (!arrayClass.equals(char[].class)) {
            assertFalse(ObjectKit.equals(array, new char[]{1}));
        }
        if (!arrayClass.equals(int[].class)) {
            assertFalse(ObjectKit.equals(array, new int[]{1}));
        }
        if (!arrayClass.equals(long[].class)) {
            assertFalse(ObjectKit.equals(array, new long[]{1}));
        }
        if (!arrayClass.equals(float[].class)) {
            assertFalse(ObjectKit.equals(array, new float[]{1}));
        }
        if (!arrayClass.equals(double[].class)) {
            assertFalse(ObjectKit.equals(array, new double[]{1}));
        }
    }

    private void testObjectArrayEquals() {
        assertFalse(ObjectKit.equals(new Object[]{1}, new boolean[]{true}));
        assertTrue(ObjectKit.equalsWith(new Object[]{1}, new Object[]{1}, true, true));
        assertFalse(ObjectKit.equalsWith(new Object[]{new Object[]{1}}, new Object[]{new Object[]{1}}, true, false));
    }

    private void testUnknownArrayType() throws Exception {
        Method equalsArray = ObjectKit.class.getDeclaredMethod("equalsArray", Object.class, Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, equalsArray, null, "str", "str", true);
    }

    private void testBasicHashCode() {
        String str = "str";
        assertEquals(ObjectKit.hashCode(str), Objects.hashCode(str));
        assertEquals(ObjectKit.id(str), System.identityHashCode(str));
    }

    private void testArrayHashCode() {
        Object[] strs = {"str1", "str2"};
        assertEquals(ObjectKit.hashAll(strs), Arrays.hashCode(strs));

        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(ObjectKit.hashCode(strss), Arrays.deepHashCode(strss));

        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(ObjectKit.hashWith(strss2, true, false), Arrays.hashCode(strss2));
        assertEquals(ObjectKit.hashWith(strss2, false, false), Objects.hashCode(strss2));

        // Test primitive arrays
        assertEquals(ObjectKit.hashCode(new boolean[]{true, false}), Arrays.hashCode(new boolean[]{true, false}));
        assertEquals(ObjectKit.hashCode(new byte[]{6, 66}), Arrays.hashCode(new byte[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new short[]{6, 66}), Arrays.hashCode(new short[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new char[]{6, 66}), Arrays.hashCode(new char[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new int[]{6, 66}), Arrays.hashCode(new int[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new long[]{6, 66}), Arrays.hashCode(new long[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new float[]{6, 66}), Arrays.hashCode(new float[]{6, 66}));
        assertEquals(ObjectKit.hashCode(new double[]{6, 66}), Arrays.hashCode(new double[]{6, 66}));
    }

    private void testNullHashCode() {
        assertEquals(ObjectKit.hashCode(null), Objects.hashCode(null));
        assertEquals(ObjectKit.hashWith(null, false, false), Objects.hashCode(null));
    }

    private void testUnknownArrayHash() throws Exception {
        Method hashArray = ObjectKit.class.getDeclaredMethod("hashArray", Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, hashArray, null, "str", true);
    }

    @Data
    public static class X {
        private String v = "v";
    }

    @Data
    public static class Y {
        private X x = new X();
    }

    @Data
    public static class Z {
        private Y y = new Y();
    }
}