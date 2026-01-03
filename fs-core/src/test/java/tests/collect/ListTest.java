package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListTest {

    @Test
    public void testList() {
        // Test string array
        String[] stringArray = {"hello", "world", "java", "tests", "array"};
        List<String> stringList = ListKit.list(stringArray);
        assertEquals(5, stringList.size());
        assertEquals("java", stringList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> stringList.set(2, "modified"));

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = ListKit.intList(intArray);
        assertEquals(5, intList.size());
        assertEquals(3, intList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> intList.set(2, 10));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = ListKit.longList(longArray);
        assertEquals(5, longList.size());
        assertEquals(30L, longList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> longList.set(2, 100L));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = ListKit.floatList(floatArray);
        assertEquals(5, floatList.size());
        assertEquals(3.5f, floatList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> floatList.set(2, 10.5f));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = ListKit.doubleList(doubleArray);
        assertEquals(5, doubleList.size());
        assertEquals(3.5, doubleList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> doubleList.set(2, 10.5));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = ListKit.booleanList(booleanArray);
        assertEquals(5, booleanList.size());
        assertEquals(true, booleanList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> booleanList.set(2, false));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = ListKit.byteList(byteArray);
        assertEquals(5, byteList.size());
        assertEquals((byte) 3, byteList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> byteList.set(2, (byte) 10));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = ListKit.shortList(shortArray);
        assertEquals(5, shortList.size());
        assertEquals((short) 300, shortList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> shortList.set(2, (short) 1000));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = ListKit.charList(charArray);
        assertEquals(5, charList.size());
        assertEquals('c', charList.get(2));
        assertThrows(UnsupportedOperationException.class, () -> charList.set(2, 'z'));

        // empty
        assertEquals(ListKit.list(new Object[0]), Collections.emptyList());
        assertEquals(ListKit.intList(new int[0]), Collections.emptyList());
        assertEquals(ListKit.longList(new long[0]), Collections.emptyList());
        assertEquals(ListKit.floatList(new float[0]), Collections.emptyList());
        assertEquals(ListKit.doubleList(new double[0]), Collections.emptyList());
        assertEquals(ListKit.booleanList(new boolean[0]), Collections.emptyList());
        assertEquals(ListKit.byteList(new byte[0]), Collections.emptyList());
        assertEquals(ListKit.shortList(new short[0]), Collections.emptyList());
        assertEquals(ListKit.charList(new char[0]), Collections.emptyList());
    }

    @Test
    public void testNewList() {
        assertEquals(ListKit.arrayList(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(ArrayList.class, ListKit.arrayList(1, 2, 3).getClass());
        assertEquals(ListKit.linkedList(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(LinkedList.class, ListKit.linkedList(1, 2, 3).getClass());
    }

    @Test
    public void testToList() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(ListKit.toList(list), list);
        assertNotSame(ListKit.toList(list), list);
        assertThrows(UnsupportedOperationException.class, () -> ListKit.toList(list).set(0, 10));

        assertEquals(ListKit.toArrayList(list), list);
        assertNotSame(ListKit.toArrayList(list), list);
        assertEquals(ArrayList.class, ListKit.toArrayList(list).getClass());
        assertEquals(ListKit.toArrayList(list::iterator), list);
        assertNotSame(ListKit.toArrayList(list::iterator), list);
        assertEquals(ArrayList.class, ListKit.toArrayList(list::iterator).getClass());

        assertEquals(ListKit.toLinkedList(list), list);
        assertNotSame(ListKit.toLinkedList(list), list);
        assertEquals(LinkedList.class, ListKit.toLinkedList(list).getClass());
        assertEquals(ListKit.toLinkedList(list::iterator), list);
        assertNotSame(ListKit.toLinkedList(list::iterator), list);
        assertEquals(LinkedList.class, ListKit.toLinkedList(list::iterator).getClass());
    }

    @Test
    public void testCompositeView() {
        List<Integer> list123 = ListKit.arrayList(1, 2, 3);
        List<Integer> list456 = ListKit.arrayList(4, 5, 6);
        List<Integer> list789 = ListKit.arrayList(7, 8, 9);
        List<Integer> compositeView = ListKit.compositeView(list123, list456, list789);
        assertEquals(compositeView, ListKit.intList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        list456.set(1, 10);
        assertEquals(compositeView, ListKit.intList(1, 2, 3, 4, 10, 6, 7, 8, 9));
        for (int i = 0; i < compositeView.size(); i++) {
            compositeView.set(i, compositeView.get(i) + 1);
        }
        assertEquals(compositeView, ListKit.intList(2, 3, 4, 5, 11, 7, 8, 9, 10));
        assertEquals(list123, ListKit.intList(2, 3, 4));
        assertEquals(list456, ListKit.intList(5, 11, 7));
        assertEquals(list789, ListKit.intList(8, 9, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> compositeView.set(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> compositeView.set(100, 10));
    }
}
