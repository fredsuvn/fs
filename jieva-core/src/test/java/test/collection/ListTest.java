package test.collection;

import org.testng.annotations.Test;
import xyz.sunqian.common.collection.JieList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.expectThrows;

public class ListTest {

    @Test
    public void testList() {
        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        List<String> stringList = JieList.list(stringArray);
        assertEquals(stringList.size(), 5);
        assertEquals(stringList.get(2), "java");
        expectThrows(UnsupportedOperationException.class, () -> stringList.set(2, "modified"));

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = JieList.list(intArray);
        assertEquals(intList.size(), 5);
        assertEquals(intList.get(2), 3);
        expectThrows(UnsupportedOperationException.class, () -> intList.set(2, 10));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = JieList.list(longArray);
        assertEquals(longList.size(), 5);
        assertEquals(longList.get(2), 30L);
        expectThrows(UnsupportedOperationException.class, () -> longList.set(2, 100L));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = JieList.list(floatArray);
        assertEquals(floatList.size(), 5);
        assertEquals(floatList.get(2), 3.5f);
        expectThrows(UnsupportedOperationException.class, () -> floatList.set(2, 10.5f));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = JieList.list(doubleArray);
        assertEquals(doubleList.size(), 5);
        assertEquals(doubleList.get(2), 3.5);
        expectThrows(UnsupportedOperationException.class, () -> doubleList.set(2, 10.5));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = JieList.list(booleanArray);
        assertEquals(booleanList.size(), 5);
        assertEquals(booleanList.get(2), true);
        expectThrows(UnsupportedOperationException.class, () -> booleanList.set(2, false));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = JieList.list(byteArray);
        assertEquals(byteList.size(), 5);
        assertEquals(byteList.get(2), (byte) 3);
        expectThrows(UnsupportedOperationException.class, () -> byteList.set(2, (byte) 10));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = JieList.list(shortArray);
        assertEquals(shortList.size(), 5);
        assertEquals(shortList.get(2), (short) 300);
        expectThrows(UnsupportedOperationException.class, () -> shortList.set(2, (short) 1000));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = JieList.list(charArray);
        assertEquals(charList.size(), 5);
        assertEquals(charList.get(2), 'c');
        expectThrows(UnsupportedOperationException.class, () -> charList.set(2, 'z'));

        // empty
        assertEquals(JieList.list(new Object[0]), Collections.emptyList());
        assertEquals(JieList.list(new int[0]), Collections.emptyList());
        assertEquals(JieList.list(new long[0]), Collections.emptyList());
        assertEquals(JieList.list(new float[0]), Collections.emptyList());
        assertEquals(JieList.list(new double[0]), Collections.emptyList());
        assertEquals(JieList.list(new boolean[0]), Collections.emptyList());
        assertEquals(JieList.list(new byte[0]), Collections.emptyList());
        assertEquals(JieList.list(new short[0]), Collections.emptyList());
        assertEquals(JieList.list(new char[0]), Collections.emptyList());
    }

    @Test
    public void testNewList() {
        assertEquals(JieList.arrayList(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(JieList.arrayList(1, 2, 3).getClass(), ArrayList.class);
        assertEquals(JieList.linkedList(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(JieList.linkedList(1, 2, 3).getClass(), LinkedList.class);
    }

    @Test
    public void testToList() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(JieList.toList(list), list);
        assertNotSame(JieList.toList(list), list);
        expectThrows(UnsupportedOperationException.class, () -> JieList.toList(list).set(0, 10));

        assertEquals(JieList.toArrayList(list), list);
        assertNotSame(JieList.toArrayList(list), list);
        assertEquals(JieList.toArrayList(list).getClass(), ArrayList.class);
        assertEquals(JieList.toArrayList(list::iterator), list);
        assertNotSame(JieList.toArrayList(list::iterator), list);
        assertEquals(JieList.toArrayList(list::iterator).getClass(), ArrayList.class);

        assertEquals(JieList.toLinkedList(list), list);
        assertNotSame(JieList.toLinkedList(list), list);
        assertEquals(JieList.toLinkedList(list).getClass(), LinkedList.class);
        assertEquals(JieList.toLinkedList(list::iterator), list);
        assertNotSame(JieList.toLinkedList(list::iterator), list);
        assertEquals(JieList.toLinkedList(list::iterator).getClass(), LinkedList.class);
    }
}
