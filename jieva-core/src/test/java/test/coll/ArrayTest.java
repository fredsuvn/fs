package test.coll;

import org.testng.annotations.Test;
import xyz.sunqian.common.collection.JieArray;

import java.util.List;

import static org.testng.Assert.*;

public class ArrayTest {

    @Test
    public void testEmpty() {
        // Test with null arrays
        assertTrue(JieArray.isEmpty((Object[]) null));
        assertTrue(JieArray.isEmpty((boolean[]) null));
        assertTrue(JieArray.isEmpty((byte[]) null));
        assertTrue(JieArray.isEmpty((short[]) null));
        assertTrue(JieArray.isEmpty((char[]) null));
        assertTrue(JieArray.isEmpty((int[]) null));
        assertTrue(JieArray.isEmpty((long[]) null));
        assertTrue(JieArray.isEmpty((float[]) null));
        assertTrue(JieArray.isEmpty((double[]) null));

        // Test with empty arrays
        assertTrue(JieArray.isEmpty(new Object[0]));
        assertTrue(JieArray.isEmpty(new boolean[0]));
        assertTrue(JieArray.isEmpty(new byte[0]));
        assertTrue(JieArray.isEmpty(new short[0]));
        assertTrue(JieArray.isEmpty(new char[0]));
        assertTrue(JieArray.isEmpty(new int[0]));
        assertTrue(JieArray.isEmpty(new long[0]));
        assertTrue(JieArray.isEmpty(new float[0]));
        assertTrue(JieArray.isEmpty(new double[0]));

        // Test with non-empty arrays
        assertFalse(JieArray.isEmpty(new Object[]{1}));
        assertFalse(JieArray.isEmpty(new boolean[]{true}));
        assertFalse(JieArray.isEmpty(new byte[]{1}));
        assertFalse(JieArray.isEmpty(new short[]{1}));
        assertFalse(JieArray.isEmpty(new char[]{'a'}));
        assertFalse(JieArray.isEmpty(new int[]{1}));
        assertFalse(JieArray.isEmpty(new long[]{1L}));
        assertFalse(JieArray.isEmpty(new float[]{1.0f}));
        assertFalse(JieArray.isEmpty(new double[]{1.0}));

        // Test with null arrays
        assertFalse(JieArray.isNotEmpty((Object[]) null));
        assertFalse(JieArray.isNotEmpty((boolean[]) null));
        assertFalse(JieArray.isNotEmpty((byte[]) null));
        assertFalse(JieArray.isNotEmpty((short[]) null));
        assertFalse(JieArray.isNotEmpty((char[]) null));
        assertFalse(JieArray.isNotEmpty((int[]) null));
        assertFalse(JieArray.isNotEmpty((long[]) null));
        assertFalse(JieArray.isNotEmpty((float[]) null));
        assertFalse(JieArray.isNotEmpty((double[]) null));

        // Test with empty arrays
        assertFalse(JieArray.isNotEmpty(new Object[0]));
        assertFalse(JieArray.isNotEmpty(new boolean[0]));
        assertFalse(JieArray.isNotEmpty(new byte[0]));
        assertFalse(JieArray.isNotEmpty(new short[0]));
        assertFalse(JieArray.isNotEmpty(new char[0]));
        assertFalse(JieArray.isNotEmpty(new int[0]));
        assertFalse(JieArray.isNotEmpty(new long[0]));
        assertFalse(JieArray.isNotEmpty(new float[0]));
        assertFalse(JieArray.isNotEmpty(new double[0]));

        // Test with non-empty arrays
        assertTrue(JieArray.isNotEmpty(new Object[]{1}));
        assertTrue(JieArray.isNotEmpty(new boolean[]{true}));
        assertTrue(JieArray.isNotEmpty(new byte[]{1}));
        assertTrue(JieArray.isNotEmpty(new short[]{1}));
        assertTrue(JieArray.isNotEmpty(new char[]{'a'}));
        assertTrue(JieArray.isNotEmpty(new int[]{1}));
        assertTrue(JieArray.isNotEmpty(new long[]{1L}));
        assertTrue(JieArray.isNotEmpty(new float[]{1.0f}));
        assertTrue(JieArray.isNotEmpty(new double[]{1.0}));
    }

    @Test
    public void testGet() {
        Integer[] objArray = {1, 2, 3, 4, 5};
        assertEquals(JieArray.get(objArray, 2, 0), 3);
        assertEquals(JieArray.get(objArray, -1, 9), 9);
        assertEquals(JieArray.get(objArray, 5, 9), 9);
        assertEquals(JieArray.get(null, 0, (Integer) 9), 9);
        assertEquals(JieArray.get(new Integer[]{null}, 0, 9), 9);

        int[] intArray = {1, 2, 3, 4, 5};
        assertEquals(JieArray.get(intArray, 2, 0), 3);
        assertEquals(JieArray.get(intArray, -1, 0), 0);
        assertEquals(JieArray.get(intArray, 5, 0), 0);
        assertEquals(JieArray.get((int[]) null, 0, 9), 9);

        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        assertEquals(JieArray.get(longArray, 1, 0L), 20L);
        assertEquals(JieArray.get(longArray, -1, 0L), 0L);
        assertEquals(JieArray.get(longArray, 5, 0L), 0L);
        assertEquals(JieArray.get((long[]) null, 0, 9), 9);

        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        assertEquals(JieArray.get(floatArray, 3, 0.0f), 4.5f);
        assertEquals(JieArray.get(floatArray, -1, 0.0f), 0.0f);
        assertEquals(JieArray.get(floatArray, 5, 0.0f), 0.0f);
        assertEquals(JieArray.get((float[]) null, 0, 9), 9);

        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        assertEquals(JieArray.get(doubleArray, 2, 0.0), 3.5);
        assertEquals(JieArray.get(doubleArray, -1, 0.0), 0.0);
        assertEquals(JieArray.get(doubleArray, 5, 0.0), 0.0);
        assertEquals(JieArray.get((double[]) null, 0, 9), 9);

        boolean[] booleanArray = {true, false, true, false, true};
        assertEquals(JieArray.get(booleanArray, 1, false), false);
        assertEquals(JieArray.get(booleanArray, -1, false), false);
        assertEquals(JieArray.get(booleanArray, 5, false), false);
        assertEquals(JieArray.get((boolean[]) null, 0, false), false);

        byte[] byteArray = {1, 2, 3, 4, 5};
        assertEquals(JieArray.get(byteArray, 4, (byte) 0), (byte) 5);
        assertEquals(JieArray.get(byteArray, -1, (byte) 0), (byte) 0);
        assertEquals(JieArray.get(byteArray, 5, (byte) 0), (byte) 0);
        assertEquals(JieArray.get((byte[]) null, 0, (byte) 9), 9);

        short[] shortArray = {100, 200, 300, 400, 500};
        assertEquals(JieArray.get(shortArray, 0, (short) 0), (short) 100);
        assertEquals(JieArray.get(shortArray, -1, (short) 0), (short) 0);
        assertEquals(JieArray.get(shortArray, 5, (short) 0), (short) 0);
        assertEquals(JieArray.get((short[]) null, 0, (short) 9), 9);

        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        assertEquals(JieArray.get(charArray, 3, 'x'), 'd');
        assertEquals(JieArray.get(charArray, -1, 'x'), 'x');
        assertEquals(JieArray.get(charArray, 5, 'x'), 'x');
        assertEquals(JieArray.get((char[]) null, 0, '9'), '9');
    }

    @Test
    public void testIndexOf() {
        // Test object array
        Integer[] objArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(JieArray.indexOf(objArray, 3), 2);
        assertEquals(JieArray.indexOf(objArray, 6), -1);
        assertEquals(JieArray.indexOf(objArray, 1), 0);
        assertEquals(JieArray.lastIndexOf(objArray, 3), 5);
        assertEquals(JieArray.lastIndexOf(objArray, 6), -1);
        assertEquals(JieArray.lastIndexOf(objArray, 1), 7);

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(JieArray.indexOf(intArray, 3), 2);
        assertEquals(JieArray.indexOf(intArray, 6), -1);
        assertEquals(JieArray.indexOf(intArray, 1), 0);
        assertEquals(JieArray.lastIndexOf(intArray, 3), 5);
        assertEquals(JieArray.lastIndexOf(intArray, 6), -1);
        assertEquals(JieArray.lastIndexOf(intArray, 1), 7);

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L, 30L, 20L, 10L};
        assertEquals(JieArray.indexOf(longArray, 30L), 2);
        assertEquals(JieArray.indexOf(longArray, 60L), -1);
        assertEquals(JieArray.indexOf(longArray, 10L), 0);
        assertEquals(JieArray.lastIndexOf(longArray, 30L), 5);
        assertEquals(JieArray.lastIndexOf(longArray, 60L), -1);
        assertEquals(JieArray.lastIndexOf(longArray, 10L), 7);

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f, 3.5f, 2.5f, 1.5f};
        assertEquals(JieArray.indexOf(floatArray, 3.5f), 2);
        assertEquals(JieArray.indexOf(floatArray, 6.5f), -1);
        assertEquals(JieArray.indexOf(floatArray, 1.5f), 0);
        assertEquals(JieArray.lastIndexOf(floatArray, 3.5f), 5);
        assertEquals(JieArray.lastIndexOf(floatArray, 6.5f), -1);
        assertEquals(JieArray.lastIndexOf(floatArray, 1.5f), 7);

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5, 3.5, 2.5, 1.5};
        assertEquals(JieArray.indexOf(doubleArray, 3.5), 2);
        assertEquals(JieArray.indexOf(doubleArray, 6.5), -1);
        assertEquals(JieArray.indexOf(doubleArray, 1.5), 0);
        assertEquals(JieArray.lastIndexOf(doubleArray, 3.5), 5);
        assertEquals(JieArray.lastIndexOf(doubleArray, 6.5), -1);
        assertEquals(JieArray.lastIndexOf(doubleArray, 1.5), 7);

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true, true, false, true};
        assertEquals(JieArray.indexOf(booleanArray, true), 0);
        assertEquals(JieArray.indexOf(booleanArray, false), 1);
        assertEquals(JieArray.lastIndexOf(booleanArray, true), 7);
        assertEquals(JieArray.lastIndexOf(booleanArray, false), 6);
        assertEquals(JieArray.indexOf(new boolean[]{false}, true), -1);
        assertEquals(JieArray.lastIndexOf(new boolean[]{false}, true), -1);

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(JieArray.indexOf(byteArray, (byte) 3), 2);
        assertEquals(JieArray.indexOf(byteArray, (byte) 6), -1);
        assertEquals(JieArray.indexOf(byteArray, (byte) 1), 0);
        assertEquals(JieArray.lastIndexOf(byteArray, (byte) 3), 5);
        assertEquals(JieArray.lastIndexOf(byteArray, (byte) 6), -1);
        assertEquals(JieArray.lastIndexOf(byteArray, (byte) 1), 7);

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500, 300, 200, 100};
        assertEquals(JieArray.indexOf(shortArray, (short) 300), 2);
        assertEquals(JieArray.indexOf(shortArray, (short) 600), -1);
        assertEquals(JieArray.indexOf(shortArray, (short) 100), 0);
        assertEquals(JieArray.lastIndexOf(shortArray, (short) 300), 5);
        assertEquals(JieArray.lastIndexOf(shortArray, (short) 600), -1);
        assertEquals(JieArray.lastIndexOf(shortArray, (short) 100), 7);

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e', 'c', 'b', 'a'};
        assertEquals(JieArray.indexOf(charArray, 'c'), 2);
        assertEquals(JieArray.indexOf(charArray, 'f'), -1);
        assertEquals(JieArray.indexOf(charArray, 'a'), 0);
        assertEquals(JieArray.lastIndexOf(charArray, 'c'), 5);
        assertEquals(JieArray.lastIndexOf(charArray, 'f'), -1);
        assertEquals(JieArray.lastIndexOf(charArray, 'a'), 7);
    }

    @Test
    public void testAsList() {
        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = JieArray.asList(intArray);
        assertEquals(intList.size(), 5);
        assertEquals(intList.get(2), 3);
        Integer oldValueInt = intList.set(2, 10); // 修改索引 2 的元素为 10
        assertEquals(oldValueInt, 3);
        assertEquals(intList.get(2), 10);

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = JieArray.asList(longArray);
        assertEquals(longList.size(), 5);
        assertEquals(longList.get(2), 30L);
        Long oldValueLong = longList.set(2, 100L); // 修改索引 2 的元素为 100
        assertEquals(oldValueLong, 30L);
        assertEquals(longList.get(2), 100L);

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = JieArray.asList(floatArray);
        assertEquals(floatList.size(), 5);
        assertEquals(floatList.get(2), 3.5f);
        Float oldValueFloat = floatList.set(2, 10.5f); // 修改索引 2 的元素为 10.5
        assertEquals(oldValueFloat, 3.5f);
        assertEquals(floatList.get(2), 10.5f);

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = JieArray.asList(doubleArray);
        assertEquals(doubleList.size(), 5);
        assertEquals(doubleList.get(2), 3.5);
        Double oldValueDouble = doubleList.set(2, 10.5); // 修改索引 2 的元素为 10.5
        assertEquals(oldValueDouble, 3.5);
        assertEquals(doubleList.get(2), 10.5);

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = JieArray.asList(booleanArray);
        assertEquals(booleanList.size(), 5);
        assertEquals(booleanList.get(2), true);
        Boolean oldValueBoolean = booleanList.set(2, false); // 修改索引 2 的元素为 false
        assertEquals(oldValueBoolean, true);
        assertEquals(booleanList.get(2), false);

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = JieArray.asList(byteArray);
        assertEquals(byteList.size(), 5);
        assertEquals(byteList.get(2), (byte) 3);
        Byte oldValueByte = byteList.set(2, (byte) 10); // 修改索引 2 的元素为 10
        assertEquals(oldValueByte, (byte) 3);
        assertEquals(byteList.get(2), (byte) 10);

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = JieArray.asList(shortArray);
        assertEquals(shortList.size(), 5);
        assertEquals(shortList.get(2), (short) 300);
        Short oldValueShort = shortList.set(2, (short) 1000); // 修改索引 2 的元素为 1000
        assertEquals(oldValueShort, (short) 300);
        assertEquals(shortList.get(2), (short) 1000);

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = JieArray.asList(charArray);
        assertEquals(charList.size(), 5);
        assertEquals(charList.get(2), 'c');
        Character oldValueChar = charList.set(2, 'z'); // 修改索引 2 的元素为 'z'
        assertEquals(oldValueChar, 'c');
        assertEquals(charList.get(2), 'z');

        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        List<String> stringList = JieArray.asList(stringArray);
        assertEquals(stringList.size(), 5);
        assertEquals(stringList.get(2), "java");
        String oldValueString = stringList.set(2, "modified"); // 修改索引 2 的元素为 "modified"
        assertEquals(oldValueString, "java");
        assertEquals(stringList.get(2), "modified");
    }

    @Test
    public void testListOf() {
        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = JieArray.immutableList(intArray);
        assertEquals(intList.size(), 5);
        assertEquals(intList.get(2), 3);
        expectThrows(UnsupportedOperationException.class, () -> intList.set(2, 10));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = JieArray.immutableList(longArray);
        assertEquals(longList.size(), 5);
        assertEquals(longList.get(2), 30L);
        expectThrows(UnsupportedOperationException.class, () -> longList.set(2, 100L));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = JieArray.immutableList(floatArray);
        assertEquals(floatList.size(), 5);
        assertEquals(floatList.get(2), 3.5f);
        expectThrows(UnsupportedOperationException.class, () -> floatList.set(2, 10.5f));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = JieArray.immutableList(doubleArray);
        assertEquals(doubleList.size(), 5);
        assertEquals(doubleList.get(2), 3.5);
        expectThrows(UnsupportedOperationException.class, () -> doubleList.set(2, 10.5));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = JieArray.immutableList(booleanArray);
        assertEquals(booleanList.size(), 5);
        assertEquals(booleanList.get(2), true);
        expectThrows(UnsupportedOperationException.class, () -> booleanList.set(2, false));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = JieArray.immutableList(byteArray);
        assertEquals(byteList.size(), 5);
        assertEquals(byteList.get(2), (byte) 3);
        expectThrows(UnsupportedOperationException.class, () -> byteList.set(2, (byte) 10));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = JieArray.immutableList(shortArray);
        assertEquals(shortList.size(), 5);
        assertEquals(shortList.get(2), (short) 300);
        expectThrows(UnsupportedOperationException.class, () -> shortList.set(2, (short) 1000));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = JieArray.immutableList(charArray);
        assertEquals(charList.size(), 5);
        assertEquals(charList.get(2), 'c');
        expectThrows(UnsupportedOperationException.class, () -> charList.set(2, 'z'));

        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        List<String> stringList = JieArray.immutableList(stringArray);
        assertEquals(stringList.size(), 5);
        assertEquals(stringList.get(2), "java");
        expectThrows(UnsupportedOperationException.class, () -> stringList.set(2, "modified"));
    }

    @Test
    public void testMap() {
        Character[] chars = {'a', 'b', 'c'};
        Integer[] asciiValues = {97, 98, 99};
        assertEquals(JieArray.map(chars, new Integer[0], c -> (int) c), asciiValues);
        assertEquals(JieArray.map(chars, new Integer[3], c -> (int) c), asciiValues);
        assertEquals(JieArray.map(chars, c -> (int) c), asciiValues);
        expectThrows(UnsupportedOperationException.class, () -> JieArray.map(chars, c -> null));
        Integer[] asciiValues2 = {null, 98, 99};
        assertEquals(JieArray.map(chars, c -> c == 'a' ? null : (int) c), asciiValues2);
    }
}
