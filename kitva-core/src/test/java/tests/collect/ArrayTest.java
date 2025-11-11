package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.Kit;
import space.sunqian.common.collect.ArrayKit;
import space.sunqian.common.collect.ArrayOperator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals(3, ArrayKit.get(objArray, 2, 0));
        assertEquals(9, ArrayKit.get(objArray, -1, 9));
        assertEquals(9, ArrayKit.get(objArray, 5, 9));
        assertEquals(9, ArrayKit.get(null, 0, (Integer) 9));
        assertEquals(9, ArrayKit.get(new Integer[]{null}, 0, 9));

        int[] intArray = {1, 2, 3, 4, 5};
        assertEquals(3, ArrayKit.get(intArray, 2, 0));
        assertEquals(0, ArrayKit.get(intArray, -1, 0));
        assertEquals(0, ArrayKit.get(intArray, 5, 0));
        assertEquals(9, ArrayKit.get((int[]) null, 0, 9));

        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        assertEquals(20L, ArrayKit.get(longArray, 1, 0L));
        assertEquals(0L, ArrayKit.get(longArray, -1, 0L));
        assertEquals(0L, ArrayKit.get(longArray, 5, 0L));
        assertEquals(9, ArrayKit.get((long[]) null, 0, 9));

        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        assertEquals(4.5f, ArrayKit.get(floatArray, 3, 0.0f));
        assertEquals(0.0f, ArrayKit.get(floatArray, -1, 0.0f));
        assertEquals(0.0f, ArrayKit.get(floatArray, 5, 0.0f));
        assertEquals(9, ArrayKit.get((float[]) null, 0, 9));

        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        assertEquals(3.5, ArrayKit.get(doubleArray, 2, 0.0));
        assertEquals(0.0, ArrayKit.get(doubleArray, -1, 0.0));
        assertEquals(0.0, ArrayKit.get(doubleArray, 5, 0.0));
        assertEquals(9, ArrayKit.get((double[]) null, 0, 9));

        boolean[] booleanArray = {true, false, true, false, true};
        assertEquals(false, ArrayKit.get(booleanArray, 1, false));
        assertEquals(false, ArrayKit.get(booleanArray, -1, false));
        assertEquals(false, ArrayKit.get(booleanArray, 5, false));
        assertEquals(false, ArrayKit.get((boolean[]) null, 0, false));

        byte[] byteArray = {1, 2, 3, 4, 5};
        assertEquals((byte) 5, ArrayKit.get(byteArray, 4, (byte) 0));
        assertEquals((byte) 0, ArrayKit.get(byteArray, -1, (byte) 0));
        assertEquals((byte) 0, ArrayKit.get(byteArray, 5, (byte) 0));
        assertEquals(9, ArrayKit.get((byte[]) null, 0, (byte) 9));

        short[] shortArray = {100, 200, 300, 400, 500};
        assertEquals((short) 100, ArrayKit.get(shortArray, 0, (short) 0));
        assertEquals((short) 0, ArrayKit.get(shortArray, -1, (short) 0));
        assertEquals((short) 0, ArrayKit.get(shortArray, 5, (short) 0));
        assertEquals(9, ArrayKit.get((short[]) null, 0, (short) 9));

        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        assertEquals('d', ArrayKit.get(charArray, 3, 'x'));
        assertEquals('x', ArrayKit.get(charArray, -1, 'x'));
        assertEquals('x', ArrayKit.get(charArray, 5, 'x'));
        assertEquals('9', ArrayKit.get((char[]) null, 0, '9'));
    }

    @Test
    public void testIndexOf() {
        // Test object array
        Integer[] objArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(2, ArrayKit.indexOf(objArray, 3));
        assertEquals(-1, ArrayKit.indexOf(objArray, 6));
        assertEquals(0, ArrayKit.indexOf(objArray, 1));
        assertEquals(5, ArrayKit.lastIndexOf(objArray, 3));
        assertEquals(-1, ArrayKit.lastIndexOf(objArray, 6));
        assertEquals(7, ArrayKit.lastIndexOf(objArray, 1));
        assertEquals(2, ArrayKit.indexOf(objArray, (i, t) -> Kit.equals(t, 3)));
        assertEquals(-1, ArrayKit.indexOf(objArray, (i, t) -> Kit.equals(t, 6)));
        assertEquals(0, ArrayKit.indexOf(objArray, (i, t) -> Kit.equals(t, 1)));
        assertEquals(5, ArrayKit.lastIndexOf(objArray, (i, t) -> Kit.equals(t, 3)));
        assertEquals(-1, ArrayKit.lastIndexOf(objArray, (i, t) -> Kit.equals(t, 6)));
        assertEquals(7, ArrayKit.lastIndexOf(objArray, (i, t) -> Kit.equals(t, 1)));

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(2, ArrayKit.indexOf(intArray, 3));
        assertEquals(-1, ArrayKit.indexOf(intArray, 6));
        assertEquals(0, ArrayKit.indexOf(intArray, 1));
        assertEquals(5, ArrayKit.lastIndexOf(intArray, 3));
        assertEquals(-1, ArrayKit.lastIndexOf(intArray, 6));
        assertEquals(7, ArrayKit.lastIndexOf(intArray, 1));
        assertEquals(2, ArrayKit.indexOf(intArray, (i, t) -> t == 3));
        assertEquals(-1, ArrayKit.indexOf(intArray, (i, t) -> t == 6));
        assertEquals(0, ArrayKit.indexOf(intArray, (i, t) -> t == 1));
        assertEquals(5, ArrayKit.lastIndexOf(intArray, (i, t) -> t == 3));
        assertEquals(-1, ArrayKit.lastIndexOf(intArray, (i, t) -> t == 6));
        assertEquals(7, ArrayKit.lastIndexOf(intArray, (i, t) -> t == 1));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L, 30L, 20L, 10L};
        assertEquals(2, ArrayKit.indexOf(longArray, 30L));
        assertEquals(-1, ArrayKit.indexOf(longArray, 60L));
        assertEquals(0, ArrayKit.indexOf(longArray, 10L));
        assertEquals(5, ArrayKit.lastIndexOf(longArray, 30L));
        assertEquals(-1, ArrayKit.lastIndexOf(longArray, 60L));
        assertEquals(7, ArrayKit.lastIndexOf(longArray, 10L));
        assertEquals(2, ArrayKit.indexOf(longArray, (i, t) -> t == 30L));
        assertEquals(-1, ArrayKit.indexOf(longArray, (i, t) -> t == 60L));
        assertEquals(0, ArrayKit.indexOf(longArray, (i, t) -> t == 10L));
        assertEquals(5, ArrayKit.lastIndexOf(longArray, (i, t) -> t == 30L));
        assertEquals(-1, ArrayKit.lastIndexOf(longArray, (i, t) -> t == 60L));
        assertEquals(7, ArrayKit.lastIndexOf(longArray, (i, t) -> t == 10L));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f, 3.5f, 2.5f, 1.5f};
        assertEquals(2, ArrayKit.indexOf(floatArray, 3.5f));
        assertEquals(-1, ArrayKit.indexOf(floatArray, 6.5f));
        assertEquals(0, ArrayKit.indexOf(floatArray, 1.5f));
        assertEquals(5, ArrayKit.lastIndexOf(floatArray, 3.5f));
        assertEquals(-1, ArrayKit.lastIndexOf(floatArray, 6.5f));
        assertEquals(7, ArrayKit.lastIndexOf(floatArray, 1.5f));
        assertEquals(2, ArrayKit.indexOf(floatArray, (i, t) -> t == 3.5f));
        assertEquals(-1, ArrayKit.indexOf(floatArray, (i, t) -> t == 6.5f));
        assertEquals(0, ArrayKit.indexOf(floatArray, (i, t) -> t == 1.5f));
        assertEquals(5, ArrayKit.lastIndexOf(floatArray, (i, t) -> t == 3.5f));
        assertEquals(-1, ArrayKit.lastIndexOf(floatArray, (i, t) -> t == 6.5f));
        assertEquals(7, ArrayKit.lastIndexOf(floatArray, (i, t) -> t == 1.5f));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5, 3.5, 2.5, 1.5};
        assertEquals(2, ArrayKit.indexOf(doubleArray, 3.5));
        assertEquals(-1, ArrayKit.indexOf(doubleArray, 6.5));
        assertEquals(0, ArrayKit.indexOf(doubleArray, 1.5));
        assertEquals(5, ArrayKit.lastIndexOf(doubleArray, 3.5));
        assertEquals(-1, ArrayKit.lastIndexOf(doubleArray, 6.5));
        assertEquals(7, ArrayKit.lastIndexOf(doubleArray, 1.5));
        assertEquals(2, ArrayKit.indexOf(doubleArray, (i, t) -> t == 3.5));
        assertEquals(-1, ArrayKit.indexOf(doubleArray, (i, t) -> t == 6.5));
        assertEquals(0, ArrayKit.indexOf(doubleArray, (i, t) -> t == 1.5));
        assertEquals(5, ArrayKit.lastIndexOf(doubleArray, (i, t) -> t == 3.5));
        assertEquals(-1, ArrayKit.lastIndexOf(doubleArray, (i, t) -> t == 6.5));
        assertEquals(7, ArrayKit.lastIndexOf(doubleArray, (i, t) -> t == 1.5));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true, true, false, true};
        assertEquals(0, ArrayKit.indexOf(booleanArray, true));
        assertEquals(1, ArrayKit.indexOf(booleanArray, false));
        assertEquals(7, ArrayKit.lastIndexOf(booleanArray, true));
        assertEquals(6, ArrayKit.lastIndexOf(booleanArray, false));
        assertEquals(-1, ArrayKit.indexOf(new boolean[]{false}, true));
        assertEquals(-1, ArrayKit.lastIndexOf(new boolean[]{false}, true));
        assertEquals(0, ArrayKit.indexOf(booleanArray, (i, t) -> t == 1));
        assertEquals(1, ArrayKit.indexOf(booleanArray, (i, t) -> t == 0));
        assertEquals(7, ArrayKit.lastIndexOf(booleanArray, (i, t) -> t == 1));
        assertEquals(6, ArrayKit.lastIndexOf(booleanArray, (i, t) -> t == 0));
        assertEquals(-1, ArrayKit.indexOf(new boolean[]{false}, (i, t) -> t == 1));
        assertEquals(-1, ArrayKit.lastIndexOf(new boolean[]{false}, (i, t) -> t == 1));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5, 3, 2, 1};
        assertEquals(2, ArrayKit.indexOf(byteArray, (byte) 3));
        assertEquals(-1, ArrayKit.indexOf(byteArray, (byte) 6));
        assertEquals(0, ArrayKit.indexOf(byteArray, (byte) 1));
        assertEquals(5, ArrayKit.lastIndexOf(byteArray, (byte) 3));
        assertEquals(-1, ArrayKit.lastIndexOf(byteArray, (byte) 6));
        assertEquals(7, ArrayKit.lastIndexOf(byteArray, (byte) 1));
        assertEquals(2, ArrayKit.indexOf(byteArray, (i, t) -> t == 3));
        assertEquals(-1, ArrayKit.indexOf(byteArray, (i, t) -> t == 6));
        assertEquals(0, ArrayKit.indexOf(byteArray, (i, t) -> t == 1));
        assertEquals(5, ArrayKit.lastIndexOf(byteArray, (i, t) -> t == 3));
        assertEquals(-1, ArrayKit.lastIndexOf(byteArray, (i, t) -> t == 6));
        assertEquals(7, ArrayKit.lastIndexOf(byteArray, (i, t) -> t == 1));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500, 300, 200, 100};
        assertEquals(2, ArrayKit.indexOf(shortArray, (short) 300));
        assertEquals(-1, ArrayKit.indexOf(shortArray, (short) 600));
        assertEquals(0, ArrayKit.indexOf(shortArray, (short) 100));
        assertEquals(5, ArrayKit.lastIndexOf(shortArray, (short) 300));
        assertEquals(-1, ArrayKit.lastIndexOf(shortArray, (short) 600));
        assertEquals(7, ArrayKit.lastIndexOf(shortArray, (short) 100));
        assertEquals(2, ArrayKit.indexOf(shortArray, (i, t) -> t == 300));
        assertEquals(-1, ArrayKit.indexOf(shortArray, (i, t) -> t == 600));
        assertEquals(0, ArrayKit.indexOf(shortArray, (i, t) -> t == 100));
        assertEquals(5, ArrayKit.lastIndexOf(shortArray, (i, t) -> t == 300));
        assertEquals(-1, ArrayKit.lastIndexOf(shortArray, (i, t) -> t == 600));
        assertEquals(7, ArrayKit.lastIndexOf(shortArray, (i, t) -> t == 100));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e', 'c', 'b', 'a'};
        assertEquals(2, ArrayKit.indexOf(charArray, 'c'));
        assertEquals(-1, ArrayKit.indexOf(charArray, 'f'));
        assertEquals(0, ArrayKit.indexOf(charArray, 'a'));
        assertEquals(5, ArrayKit.lastIndexOf(charArray, 'c'));
        assertEquals(-1, ArrayKit.lastIndexOf(charArray, 'f'));
        assertEquals(7, ArrayKit.lastIndexOf(charArray, 'a'));
        assertEquals(2, ArrayKit.indexOf(charArray, (i, t) -> t == 'c'));
        assertEquals(-1, ArrayKit.indexOf(charArray, (i, t) -> t == 'f'));
        assertEquals(0, ArrayKit.indexOf(charArray, (i, t) -> t == 'a'));
        assertEquals(5, ArrayKit.lastIndexOf(charArray, (i, t) -> t == 'c'));
        assertEquals(-1, ArrayKit.lastIndexOf(charArray, (i, t) -> t == 'f'));
        assertEquals(7, ArrayKit.lastIndexOf(charArray, (i, t) -> t == 'a'));
    }

    @Test
    public void testAsList() {
        // Test string array
        String[] stringArray = {"hello", "world", "java", "tests", "array"};
        List<String> stringList = ArrayKit.asList(stringArray);
        assertEquals(5, stringList.size());
        assertEquals("java", stringList.get(2));
        String oldValueString = stringList.set(2, "modified");
        assertEquals("java", oldValueString);
        assertEquals("modified", stringList.get(2));

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = ArrayKit.asList(intArray);
        assertEquals(5, intList.size());
        assertEquals(3, intList.get(2));
        Integer oldValueInt = intList.set(2, 10);
        assertEquals(3, oldValueInt);
        assertEquals(10, intList.get(2));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = ArrayKit.asList(longArray);
        assertEquals(5, longList.size());
        assertEquals(30L, longList.get(2));
        Long oldValueLong = longList.set(2, 100L);
        assertEquals(30L, oldValueLong);
        assertEquals(100L, longList.get(2));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = ArrayKit.asList(floatArray);
        assertEquals(5, floatList.size());
        assertEquals(3.5f, floatList.get(2));
        Float oldValueFloat = floatList.set(2, 10.5f);
        assertEquals(3.5f, oldValueFloat);
        assertEquals(10.5f, floatList.get(2));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = ArrayKit.asList(doubleArray);
        assertEquals(5, doubleList.size());
        assertEquals(3.5, doubleList.get(2));
        Double oldValueDouble = doubleList.set(2, 10.5);
        assertEquals(3.5, oldValueDouble);
        assertEquals(10.5, doubleList.get(2));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = ArrayKit.asList(booleanArray);
        assertEquals(5, booleanList.size());
        assertEquals(true, booleanList.get(2));
        Boolean oldValueBoolean = booleanList.set(2, false);
        assertEquals(true, oldValueBoolean);
        assertEquals(false, booleanList.get(2));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = ArrayKit.asList(byteArray);
        assertEquals(5, byteList.size());
        assertEquals((byte) 3, byteList.get(2));
        Byte oldValueByte = byteList.set(2, (byte) 10);
        assertEquals((byte) 3, oldValueByte);
        assertEquals((byte) 10, byteList.get(2));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = ArrayKit.asList(shortArray);
        assertEquals(5, shortList.size());
        assertEquals((short) 300, shortList.get(2));
        Short oldValueShort = shortList.set(2, (short) 1000);
        assertEquals((short) 300, oldValueShort);
        assertEquals((short) 1000, shortList.get(2));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = ArrayKit.asList(charArray);
        assertEquals(5, charList.size());
        assertEquals('c', charList.get(2));
        Character oldValueChar = charList.set(2, 'z');
        assertEquals('c', oldValueChar);
        assertEquals('z', charList.get(2));
    }

    @Test
    public void testMap() {
        Character[] chars = {'a', 'b', null, 'c'};
        Integer[] asciiValues = {97, 98, null, 99};
        assertArrayEquals(ArrayKit.map(chars, new Integer[0], c -> c == null ? null : (int) c), asciiValues);
        assertArrayEquals(ArrayKit.map(chars, new Integer[4], c -> c == null ? null : (int) c), asciiValues);
        assertArrayEquals(ArrayKit.map(chars, c -> c == null ? null : (int) c), asciiValues);
        Integer[] asciiValues2 = {null, 98, null, 99};
        assertArrayEquals(ArrayKit.map(chars, c -> c == null ? null : (c == 'a' ? null : (int) c)), asciiValues2);
        assertThrows(UnsupportedOperationException.class, () -> ArrayKit.map(chars, c -> null));
    }

    @Test
    public void testArray() {
        Integer[] array = new Integer[]{1, 2, 3};
        assertSame(ArrayKit.array(array), array);
    }

    @Test
    public void testFill() {
        assertArrayEquals(new Integer[]{6, 6, 6}, ArrayKit.fill(new Integer[3], 6));
        assertArrayEquals(new int[]{6, 6, 6}, ArrayKit.fill(new int[3], 6));
        assertArrayEquals(new long[]{6, 6, 6}, ArrayKit.fill(new long[3], 6));
        assertArrayEquals(new float[]{6, 6, 6}, ArrayKit.fill(new float[3], 6));
        assertArrayEquals(new double[]{6, 6, 6}, ArrayKit.fill(new double[3], 6));
        assertArrayEquals(new boolean[]{true, true, true}, ArrayKit.fill(new boolean[3], true));
        assertArrayEquals(new byte[]{6, 6, 6}, ArrayKit.fill(new byte[3], (byte) 6));
        assertArrayEquals(new short[]{6, 6, 6}, ArrayKit.fill(new short[3], (short) 6));
        assertArrayEquals(new char[]{6, 6, 6}, ArrayKit.fill(new char[3], (char) 6));
    }

    @Test
    public void testArrayOperator() {
        {
            // boolean
            ArrayOperator operator = ArrayOperator.of(boolean[].class);
            boolean[] array = new boolean[]{true};
            assertEquals(true, operator.get(array, 0));
            operator.set(array, 0, false);
            assertEquals(false, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // byte
            ArrayOperator operator = ArrayOperator.of(byte[].class);
            byte[] array = new byte[]{6};
            assertEquals((byte) 6, operator.get(array, 0));
            operator.set(array, 0, (byte) 9);
            assertEquals((byte) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // short
            ArrayOperator operator = ArrayOperator.of(short[].class);
            short[] array = new short[]{6};
            assertEquals((short) 6, operator.get(array, 0));
            operator.set(array, 0, (short) 9);
            assertEquals((short) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // char
            ArrayOperator operator = ArrayOperator.of(char[].class);
            char[] array = new char[]{6};
            assertEquals((char) 6, operator.get(array, 0));
            operator.set(array, 0, (char) 9);
            assertEquals((char) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // int
            ArrayOperator operator = ArrayOperator.of(int[].class);
            int[] array = new int[]{6};
            assertEquals((int) 6, operator.get(array, 0));
            operator.set(array, 0, (int) 9);
            assertEquals((int) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // long
            ArrayOperator operator = ArrayOperator.of(long[].class);
            long[] array = new long[]{6};
            assertEquals((long) 6, operator.get(array, 0));
            operator.set(array, 0, (long) 9);
            assertEquals((long) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // float
            ArrayOperator operator = ArrayOperator.of(float[].class);
            float[] array = new float[]{6};
            assertEquals((float) 6, operator.get(array, 0));
            operator.set(array, 0, (float) 9);
            assertEquals((float) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // double
            ArrayOperator operator = ArrayOperator.of(double[].class);
            double[] array = new double[]{6};
            assertEquals((double) 6, operator.get(array, 0));
            operator.set(array, 0, (double) 9);
            assertEquals((double) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        {
            // Object
            ArrayOperator operator = ArrayOperator.of(Object[].class);
            Object[] array = new Object[]{6};
            assertEquals((Object) 6, operator.get(array, 0));
            operator.set(array, 0, (Object) 9);
            assertEquals((Object) 9, operator.get(array, 0));
            assertEquals(1, operator.size(array));
        }
        // error
        assertThrows(IllegalArgumentException.class, () -> ArrayOperator.of(String.class));
    }
}
