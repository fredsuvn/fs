package tests.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.ListKit;

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
        String[] stringArray = {"hello", "world", "java", "tests", "array"};
        List<String> stringList = ListKit.list(stringArray);
        assertEquals(stringList.size(), 5);
        assertEquals(stringList.get(2), "java");
        expectThrows(UnsupportedOperationException.class, () -> stringList.set(2, "modified"));

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = ListKit.list(intArray);
        assertEquals(intList.size(), 5);
        assertEquals(intList.get(2), 3);
        expectThrows(UnsupportedOperationException.class, () -> intList.set(2, 10));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = ListKit.list(longArray);
        assertEquals(longList.size(), 5);
        assertEquals(longList.get(2), 30L);
        expectThrows(UnsupportedOperationException.class, () -> longList.set(2, 100L));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = ListKit.list(floatArray);
        assertEquals(floatList.size(), 5);
        assertEquals(floatList.get(2), 3.5f);
        expectThrows(UnsupportedOperationException.class, () -> floatList.set(2, 10.5f));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = ListKit.list(doubleArray);
        assertEquals(doubleList.size(), 5);
        assertEquals(doubleList.get(2), 3.5);
        expectThrows(UnsupportedOperationException.class, () -> doubleList.set(2, 10.5));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = ListKit.list(booleanArray);
        assertEquals(booleanList.size(), 5);
        assertEquals(booleanList.get(2), true);
        expectThrows(UnsupportedOperationException.class, () -> booleanList.set(2, false));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = ListKit.list(byteArray);
        assertEquals(byteList.size(), 5);
        assertEquals(byteList.get(2), (byte) 3);
        expectThrows(UnsupportedOperationException.class, () -> byteList.set(2, (byte) 10));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = ListKit.list(shortArray);
        assertEquals(shortList.size(), 5);
        assertEquals(shortList.get(2), (short) 300);
        expectThrows(UnsupportedOperationException.class, () -> shortList.set(2, (short) 1000));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = ListKit.list(charArray);
        assertEquals(charList.size(), 5);
        assertEquals(charList.get(2), 'c');
        expectThrows(UnsupportedOperationException.class, () -> charList.set(2, 'z'));

        // empty
        assertEquals(ListKit.list(new Object[0]), Collections.emptyList());
        assertEquals(ListKit.list(new int[0]), Collections.emptyList());
        assertEquals(ListKit.list(new long[0]), Collections.emptyList());
        assertEquals(ListKit.list(new float[0]), Collections.emptyList());
        assertEquals(ListKit.list(new double[0]), Collections.emptyList());
        assertEquals(ListKit.list(new boolean[0]), Collections.emptyList());
        assertEquals(ListKit.list(new byte[0]), Collections.emptyList());
        assertEquals(ListKit.list(new short[0]), Collections.emptyList());
        assertEquals(ListKit.list(new char[0]), Collections.emptyList());
    }

    @Test
    public void testNewList() {
        assertEquals(ListKit.arrayList(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(ListKit.arrayList(1, 2, 3).getClass(), ArrayList.class);
        assertEquals(ListKit.linkedList(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(ListKit.linkedList(1, 2, 3).getClass(), LinkedList.class);
    }

    @Test
    public void testToList() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(ListKit.toList(list), list);
        assertNotSame(ListKit.toList(list), list);
        expectThrows(UnsupportedOperationException.class, () -> ListKit.toList(list).set(0, 10));

        assertEquals(ListKit.toArrayList(list), list);
        assertNotSame(ListKit.toArrayList(list), list);
        assertEquals(ListKit.toArrayList(list).getClass(), ArrayList.class);
        assertEquals(ListKit.toArrayList(list::iterator), list);
        assertNotSame(ListKit.toArrayList(list::iterator), list);
        assertEquals(ListKit.toArrayList(list::iterator).getClass(), ArrayList.class);

        assertEquals(ListKit.toLinkedList(list), list);
        assertNotSame(ListKit.toLinkedList(list), list);
        assertEquals(ListKit.toLinkedList(list).getClass(), LinkedList.class);
        assertEquals(ListKit.toLinkedList(list::iterator), list);
        assertNotSame(ListKit.toLinkedList(list::iterator), list);
        assertEquals(ListKit.toLinkedList(list::iterator).getClass(), LinkedList.class);
    }
}
