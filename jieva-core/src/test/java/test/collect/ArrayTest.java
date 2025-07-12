package test.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ArrayKit;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ArrayTest {

    @Test
    public void testEmpty() {
        // Test with null arrays
        assertTrue(ArrayKit.isEmpty((Object[]) null));
        assertTrue(ArrayKit.isEmpty((boolean[]) null));
        assertTrue(ArrayKit.isEmpty((byte[]) null));
        assertTrue(ArrayKit.isEmpty((short[]) null));
        assertTrue(ArrayKit.isEmpty((char[]) null));
        assertTrue(ArrayKit.isEmpty((int[]) null));
        assertTrue(ArrayKit.isEmpty((long[]) null));
        assertTrue(ArrayKit.isEmpty((float[]) null));
        assertTrue(ArrayKit.isEmpty((double[]) null));

        // Test with empty arrays
        assertTrue(ArrayKit.isEmpty(new Object[0]));
        assertTrue(ArrayKit.isEmpty(new boolean[0]));
        assertTrue(ArrayKit.isEmpty(new byte[0]));
        assertTrue(ArrayKit.isEmpty(new short[0]));
        assertTrue(ArrayKit.isEmpty(new char[0]));
        assertTrue(ArrayKit.isEmpty(new int[0]));
        assertTrue(ArrayKit.isEmpty(new long[0]));
        assertTrue(ArrayKit.isEmpty(new float[0]));
        assertTrue(ArrayKit.isEmpty(new double[0]));

        // Test with non-empty arrays
        assertFalse(ArrayKit.isEmpty(new Object[]{1}));
        assertFalse(ArrayKit.isEmpty(new boolean[]{true}));
        assertFalse(ArrayKit.isEmpty(new byte[]{1}));
        assertFalse(ArrayKit.isEmpty(new short[]{1}));
        assertFalse(ArrayKit.isEmpty(new char[]{'a'}));
        assertFalse(ArrayKit.isEmpty(new int[]{1}));
        assertFalse(ArrayKit.isEmpty(new long[]{1L}));
        assertFalse(ArrayKit.isEmpty(new float[]{1.0f}));
        assertFalse(ArrayKit.isEmpty(new double[]{1.0}));

        // Test with null arrays
        assertFalse(ArrayKit.isNotEmpty((Object[]) null));
        assertFalse(ArrayKit.isNotEmpty((boolean[]) null));
        assertFalse(ArrayKit.isNotEmpty((byte[]) null));
        assertFalse(ArrayKit.isNotEmpty((short[]) null));
        assertFalse(ArrayKit.isNotEmpty((char[]) null));
        assertFalse(ArrayKit.isNotEmpty((int[]) null));
        assertFalse(ArrayKit.isNotEmpty((long[]) null));
        assertFalse(ArrayKit.isNotEmpty((float[]) null));
        assertFalse(ArrayKit.isNotEmpty((double[]) null));

        // Test with empty arrays
        assertFalse(ArrayKit.isNotEmpty(new Object[0]));
        assertFalse(ArrayKit.isNotEmpty(new boolean[0]));
        assertFalse(ArrayKit.isNotEmpty(new byte[0]));
        assertFalse(ArrayKit.isNotEmpty(new short[0]));
        assertFalse(ArrayKit.isNotEmpty(new char[0]));
        assertFalse(ArrayKit.isNotEmpty(new int[0]));
        assertFalse(ArrayKit.isNotEmpty(new long[0]));
        assertFalse(ArrayKit.isNotEmpty(new float[0]));
        assertFalse(ArrayKit.isNotEmpty(new double[0]));

        // Test with non-empty arrays
        assertTrue(ArrayKit.isNotEmpty(new Object[]{1}));
        assertTrue(ArrayKit.isNotEmpty(new boolean[]{true}));
        assertTrue(ArrayKit.isNotEmpty(new byte[]{1}));
        assertTrue(ArrayKit.isNotEmpty(new short[]{1}));
        assertTrue(ArrayKit.isNotEmpty(new char[]{'a'}));
        assertTrue(ArrayKit.isNotEmpty(new int[]{1}));
        assertTrue(ArrayKit.isNotEmpty(new long[]{1L}));
        assertTrue(ArrayKit.isNotEmpty(new float[]{1.0f}));
        assertTrue(ArrayKit.isNotEmpty(new double[]{1.0}));
    }

    @Test
    public void testGet() {
        Integer[] objArray = {1, 2, 3, 4, 5};
        assertEquals(ArrayKit.get(objArray, 2, 0), 3);
        assertEquals(ArrayKit.get(objArray, -1, 9), 9);
        assertEquals(ArrayKit.get(objArray, 5, 9), 9);
        assertEquals(ArrayKit.get(null, 0, (Integer) 9), 9);
        assertEquals(ArrayKit.get(new Integer[]{null}, 0, 9), 9);

        int[] intArray = {1, 2, 3, 4, 5};
        assertEquals(ArrayKit.get(intArray, 2, 0), 3);
        assertEquals(ArrayKit.get(intArray, -1, 0), 0);
        assertEquals(ArrayKit.get(intArray, 5, 0), 0);
        assertEquals(ArrayKit.get((int[]) null, 0, 9), 9);

        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        assertEquals(ArrayKit.get(longArray, 1, 0L), 20L);
        assertEquals(ArrayKit.get(longArray, -1, 0L), 0L);
        assertEquals(ArrayKit.get(longArray, 5, 0L), 0L);
        assertEquals(ArrayKit.get((long[]) null, 0, 9), 9);

        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        assertEquals(ArrayKit.get(floatArray, 3, 0.0f), 4.5f);
        assertEquals(ArrayKit.get(floatArray, -1, 0.0f), 0.0f);
        assertEquals(ArrayKit.get(floatArray, 5, 0.0f), 0.0f);
        assertEquals(ArrayKit.get((float[]) null, 0, 9), 9);

        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        assertEquals(ArrayKit.get(doubleArray, 2, 0.0), 3.5);
        assertEquals(ArrayKit.get(doubleArray, -1, 0.0), 0.0);
        assertEquals(ArrayKit.get(doubleArray, 5, 0.0), 0.0);
        assertEquals(ArrayKit.get((double[]) null, 0, 9), 9);

        boolean[] booleanArray = {true, false, true, false, true};
        assertEquals(ArrayKit.get(booleanArray, 1, false), false);
        assertEquals(ArrayKit.get(booleanArray, -1, false), false);
        assertEquals(ArrayKit.get(booleanArray, 5, false), false);
        assertEquals(ArrayKit.get((boolean[]) null, 0, false), false);

        byte[] byteArray = {1, 2, 3, 4, 5};
        assertEquals(ArrayKit.get(byteArray, 4, (byte) 0), (byte) 5);
        assertEquals(ArrayKit.get(byteArray, -1, (byte) 0), (byte) 0);
        assertEquals(ArrayKit.get(byteArray, 5, (byte) 0), (byte) 0);
        assertEquals(ArrayKit.get((byte[]) null, 0, (byte) 9), 9);

        short[] shortArray = {100, 200, 300, 400, 500};
        assertEquals(ArrayKit.get(shortArray, 0, (short) 0), (short) 100);
        assertEquals(ArrayKit.get(shortArray, -1, (short) 0), (short) 0);
        assertEquals(ArrayKit.get(shortArray, 5, (short) 0), (short) 0);
        assertEquals(ArrayKit.get((short[]) null, 0, (short) 9), 9);

        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        assertEquals(ArrayKit.get(charArray, 3, 'x'), 'd');
        assertEquals(ArrayKit.get(charArray, -1, 'x'), 'x');
        assertEquals(ArrayKit.get(charArray, 5, 'x'), 'x');
        assertEquals(ArrayKit.get((char[]) null, 0, '9'), '9');
    }

    @Test
    public void testIndexOf() {
        // Test object array
        Integer[] objArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(ArrayKit.indexOf(objArray, 3), 2);
        assertEquals(ArrayKit.indexOf(objArray, 6), -1);
        assertEquals(ArrayKit.indexOf(objArray, 1), 0);
        assertEquals(ArrayKit.lastIndexOf(objArray, 3), 5);
        assertEquals(ArrayKit.lastIndexOf(objArray, 6), -1);
        assertEquals(ArrayKit.lastIndexOf(objArray, 1), 7);
        assertEquals(ArrayKit.indexOf(objArray, (i, t) -> Jie.equals(t, 3)), 2);
        assertEquals(ArrayKit.indexOf(objArray, (i, t) -> Jie.equals(t, 6)), -1);
        assertEquals(ArrayKit.indexOf(objArray, (i, t) -> Jie.equals(t, 1)), 0);
        assertEquals(ArrayKit.lastIndexOf(objArray, (i, t) -> Jie.equals(t, 3)), 5);
        assertEquals(ArrayKit.lastIndexOf(objArray, (i, t) -> Jie.equals(t, 6)), -1);
        assertEquals(ArrayKit.lastIndexOf(objArray, (i, t) -> Jie.equals(t, 1)), 7);

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(ArrayKit.indexOf(intArray, 3), 2);
        assertEquals(ArrayKit.indexOf(intArray, 6), -1);
        assertEquals(ArrayKit.indexOf(intArray, 1), 0);
        assertEquals(ArrayKit.lastIndexOf(intArray, 3), 5);
        assertEquals(ArrayKit.lastIndexOf(intArray, 6), -1);
        assertEquals(ArrayKit.lastIndexOf(intArray, 1), 7);
        assertEquals(ArrayKit.indexOf(intArray, (i, t) -> t == 3), 2);
        assertEquals(ArrayKit.indexOf(intArray, (i, t) -> t == 6), -1);
        assertEquals(ArrayKit.indexOf(intArray, (i, t) -> t == 1), 0);
        assertEquals(ArrayKit.lastIndexOf(intArray, (i, t) -> t == 3), 5);
        assertEquals(ArrayKit.lastIndexOf(intArray, (i, t) -> t == 6), -1);
        assertEquals(ArrayKit.lastIndexOf(intArray, (i, t) -> t == 1), 7);

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L, 30L, 20L, 10L};
        assertEquals(ArrayKit.indexOf(longArray, 30L), 2);
        assertEquals(ArrayKit.indexOf(longArray, 60L), -1);
        assertEquals(ArrayKit.indexOf(longArray, 10L), 0);
        assertEquals(ArrayKit.lastIndexOf(longArray, 30L), 5);
        assertEquals(ArrayKit.lastIndexOf(longArray, 60L), -1);
        assertEquals(ArrayKit.lastIndexOf(longArray, 10L), 7);
        assertEquals(ArrayKit.indexOf(longArray, (i, t) -> t == 30L), 2);
        assertEquals(ArrayKit.indexOf(longArray, (i, t) -> t == 60L), -1);
        assertEquals(ArrayKit.indexOf(longArray, (i, t) -> t == 10L), 0);
        assertEquals(ArrayKit.lastIndexOf(longArray, (i, t) -> t == 30L), 5);
        assertEquals(ArrayKit.lastIndexOf(longArray, (i, t) -> t == 60L), -1);
        assertEquals(ArrayKit.lastIndexOf(longArray, (i, t) -> t == 10L), 7);

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f, 3.5f, 2.5f, 1.5f};
        assertEquals(ArrayKit.indexOf(floatArray, 3.5f), 2);
        assertEquals(ArrayKit.indexOf(floatArray, 6.5f), -1);
        assertEquals(ArrayKit.indexOf(floatArray, 1.5f), 0);
        assertEquals(ArrayKit.lastIndexOf(floatArray, 3.5f), 5);
        assertEquals(ArrayKit.lastIndexOf(floatArray, 6.5f), -1);
        assertEquals(ArrayKit.lastIndexOf(floatArray, 1.5f), 7);
        assertEquals(ArrayKit.indexOf(floatArray, (i, t) -> t == 3.5f), 2);
        assertEquals(ArrayKit.indexOf(floatArray, (i, t) -> t == 6.5f), -1);
        assertEquals(ArrayKit.indexOf(floatArray, (i, t) -> t == 1.5f), 0);
        assertEquals(ArrayKit.lastIndexOf(floatArray, (i, t) -> t == 3.5f), 5);
        assertEquals(ArrayKit.lastIndexOf(floatArray, (i, t) -> t == 6.5f), -1);
        assertEquals(ArrayKit.lastIndexOf(floatArray, (i, t) -> t == 1.5f), 7);

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5, 3.5, 2.5, 1.5};
        assertEquals(ArrayKit.indexOf(doubleArray, 3.5), 2);
        assertEquals(ArrayKit.indexOf(doubleArray, 6.5), -1);
        assertEquals(ArrayKit.indexOf(doubleArray, 1.5), 0);
        assertEquals(ArrayKit.lastIndexOf(doubleArray, 3.5), 5);
        assertEquals(ArrayKit.lastIndexOf(doubleArray, 6.5), -1);
        assertEquals(ArrayKit.lastIndexOf(doubleArray, 1.5), 7);
        assertEquals(ArrayKit.indexOf(doubleArray, (i, t) -> t == 3.5), 2);
        assertEquals(ArrayKit.indexOf(doubleArray, (i, t) -> t == 6.5), -1);
        assertEquals(ArrayKit.indexOf(doubleArray, (i, t) -> t == 1.5), 0);
        assertEquals(ArrayKit.lastIndexOf(doubleArray, (i, t) -> t == 3.5), 5);
        assertEquals(ArrayKit.lastIndexOf(doubleArray, (i, t) -> t == 6.5), -1);
        assertEquals(ArrayKit.lastIndexOf(doubleArray, (i, t) -> t == 1.5), 7);

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true, true, false, true};
        assertEquals(ArrayKit.indexOf(booleanArray, true), 0);
        assertEquals(ArrayKit.indexOf(booleanArray, false), 1);
        assertEquals(ArrayKit.lastIndexOf(booleanArray, true), 7);
        assertEquals(ArrayKit.lastIndexOf(booleanArray, false), 6);
        assertEquals(ArrayKit.indexOf(new boolean[]{false}, true), -1);
        assertEquals(ArrayKit.lastIndexOf(new boolean[]{false}, true), -1);
        assertEquals(ArrayKit.indexOf(booleanArray, (i, t) -> t == 1), 0);
        assertEquals(ArrayKit.indexOf(booleanArray, (i, t) -> t == 0), 1);
        assertEquals(ArrayKit.lastIndexOf(booleanArray, (i, t) -> t == 1), 7);
        assertEquals(ArrayKit.lastIndexOf(booleanArray, (i, t) -> t == 0), 6);
        assertEquals(ArrayKit.indexOf(new boolean[]{false}, (i, t) -> t == 1), -1);
        assertEquals(ArrayKit.lastIndexOf(new boolean[]{false}, (i, t) -> t == 1), -1);

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(ArrayKit.indexOf(byteArray, (byte) 3), 2);
        assertEquals(ArrayKit.indexOf(byteArray, (byte) 6), -1);
        assertEquals(ArrayKit.indexOf(byteArray, (byte) 1), 0);
        assertEquals(ArrayKit.lastIndexOf(byteArray, (byte) 3), 5);
        assertEquals(ArrayKit.lastIndexOf(byteArray, (byte) 6), -1);
        assertEquals(ArrayKit.lastIndexOf(byteArray, (byte) 1), 7);
        assertEquals(ArrayKit.indexOf(byteArray, (i, t) -> t == 3), 2);
        assertEquals(ArrayKit.indexOf(byteArray, (i, t) -> t == 6), -1);
        assertEquals(ArrayKit.indexOf(byteArray, (i, t) -> t == 1), 0);
        assertEquals(ArrayKit.lastIndexOf(byteArray, (i, t) -> t == 3), 5);
        assertEquals(ArrayKit.lastIndexOf(byteArray, (i, t) -> t == 6), -1);
        assertEquals(ArrayKit.lastIndexOf(byteArray, (i, t) -> t == 1), 7);

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500, 300, 200, 100};
        assertEquals(ArrayKit.indexOf(shortArray, (short) 300), 2);
        assertEquals(ArrayKit.indexOf(shortArray, (short) 600), -1);
        assertEquals(ArrayKit.indexOf(shortArray, (short) 100), 0);
        assertEquals(ArrayKit.lastIndexOf(shortArray, (short) 300), 5);
        assertEquals(ArrayKit.lastIndexOf(shortArray, (short) 600), -1);
        assertEquals(ArrayKit.lastIndexOf(shortArray, (short) 100), 7);
        assertEquals(ArrayKit.indexOf(shortArray, (i, t) -> t == 300), 2);
        assertEquals(ArrayKit.indexOf(shortArray, (i, t) -> t == 600), -1);
        assertEquals(ArrayKit.indexOf(shortArray, (i, t) -> t == 100), 0);
        assertEquals(ArrayKit.lastIndexOf(shortArray, (i, t) -> t == 300), 5);
        assertEquals(ArrayKit.lastIndexOf(shortArray, (i, t) -> t == 600), -1);
        assertEquals(ArrayKit.lastIndexOf(shortArray, (i, t) -> t == 100), 7);

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e', 'c', 'b', 'a'};
        assertEquals(ArrayKit.indexOf(charArray, 'c'), 2);
        assertEquals(ArrayKit.indexOf(charArray, 'f'), -1);
        assertEquals(ArrayKit.indexOf(charArray, 'a'), 0);
        assertEquals(ArrayKit.lastIndexOf(charArray, 'c'), 5);
        assertEquals(ArrayKit.lastIndexOf(charArray, 'f'), -1);
        assertEquals(ArrayKit.lastIndexOf(charArray, 'a'), 7);
        assertEquals(ArrayKit.indexOf(charArray, (i, t) -> t == 'c'), 2);
        assertEquals(ArrayKit.indexOf(charArray, (i, t) -> t == 'f'), -1);
        assertEquals(ArrayKit.indexOf(charArray, (i, t) -> t == 'a'), 0);
        assertEquals(ArrayKit.lastIndexOf(charArray, (i, t) -> t == 'c'), 5);
        assertEquals(ArrayKit.lastIndexOf(charArray, (i, t) -> t == 'f'), -1);
        assertEquals(ArrayKit.lastIndexOf(charArray, (i, t) -> t == 'a'), 7);
    }

    @Test
    public void testAsList() {
        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        List<String> stringList = ArrayKit.asList(stringArray);
        assertEquals(stringList.size(), 5);
        assertEquals(stringList.get(2), "java");
        String oldValueString = stringList.set(2, "modified");
        assertEquals(oldValueString, "java");
        assertEquals(stringList.get(2), "modified");

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = ArrayKit.asList(intArray);
        assertEquals(intList.size(), 5);
        assertEquals(intList.get(2), 3);
        Integer oldValueInt = intList.set(2, 10);
        assertEquals(oldValueInt, 3);
        assertEquals(intList.get(2), 10);

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = ArrayKit.asList(longArray);
        assertEquals(longList.size(), 5);
        assertEquals(longList.get(2), 30L);
        Long oldValueLong = longList.set(2, 100L);
        assertEquals(oldValueLong, 30L);
        assertEquals(longList.get(2), 100L);

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = ArrayKit.asList(floatArray);
        assertEquals(floatList.size(), 5);
        assertEquals(floatList.get(2), 3.5f);
        Float oldValueFloat = floatList.set(2, 10.5f);
        assertEquals(oldValueFloat, 3.5f);
        assertEquals(floatList.get(2), 10.5f);

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = ArrayKit.asList(doubleArray);
        assertEquals(doubleList.size(), 5);
        assertEquals(doubleList.get(2), 3.5);
        Double oldValueDouble = doubleList.set(2, 10.5);
        assertEquals(oldValueDouble, 3.5);
        assertEquals(doubleList.get(2), 10.5);

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = ArrayKit.asList(booleanArray);
        assertEquals(booleanList.size(), 5);
        assertEquals(booleanList.get(2), true);
        Boolean oldValueBoolean = booleanList.set(2, false);
        assertEquals(oldValueBoolean, true);
        assertEquals(booleanList.get(2), false);

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = ArrayKit.asList(byteArray);
        assertEquals(byteList.size(), 5);
        assertEquals(byteList.get(2), (byte) 3);
        Byte oldValueByte = byteList.set(2, (byte) 10);
        assertEquals(oldValueByte, (byte) 3);
        assertEquals(byteList.get(2), (byte) 10);

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = ArrayKit.asList(shortArray);
        assertEquals(shortList.size(), 5);
        assertEquals(shortList.get(2), (short) 300);
        Short oldValueShort = shortList.set(2, (short) 1000);
        assertEquals(oldValueShort, (short) 300);
        assertEquals(shortList.get(2), (short) 1000);

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = ArrayKit.asList(charArray);
        assertEquals(charList.size(), 5);
        assertEquals(charList.get(2), 'c');
        Character oldValueChar = charList.set(2, 'z');
        assertEquals(oldValueChar, 'c');
        assertEquals(charList.get(2), 'z');
    }

    @Test
    public void testMap() {
        Character[] chars = {'a', 'b', null, 'c'};
        Integer[] asciiValues = {97, 98, null, 99};
        assertEquals(ArrayKit.map(chars, new Integer[0], c -> c == null ? null : (int) c), asciiValues);
        assertEquals(ArrayKit.map(chars, new Integer[4], c -> c == null ? null : (int) c), asciiValues);
        assertEquals(ArrayKit.map(chars, c -> c == null ? null : (int) c), asciiValues);
        Integer[] asciiValues2 = {null, 98, null, 99};
        assertEquals(ArrayKit.map(chars, c -> c == null ? null : (c == 'a' ? null : (int) c)), asciiValues2);
        expectThrows(UnsupportedOperationException.class, () -> ArrayKit.map(chars, c -> null));
    }

    @Test
    public void testArray() {
        Integer[] array = new Integer[]{1, 2, 3};
        assertSame(ArrayKit.array(array), array);
    }

    @Test
    public void testFill() {
        assertEquals(ArrayKit.fill(new Integer[3], 6), new Integer[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new int[3], 6), new int[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new long[3], 6), new long[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new float[3], 6), new float[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new double[3], 6), new double[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new boolean[3], true), new boolean[]{true, true, true});
        assertEquals(ArrayKit.fill(new byte[3], (byte) 6), new byte[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new short[3], (short) 6), new short[]{6, 6, 6});
        assertEquals(ArrayKit.fill(new char[3], (char) 6), new char[]{6, 6, 6});
    }
}
