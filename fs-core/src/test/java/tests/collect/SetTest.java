package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.SetKit;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetTest {

    @Test
    public void testSet() {
        // Test string array
        String[] stringArray = {"hello", "world", "java", "tests", "array"};
        Set<String> stringSet = SetKit.set(stringArray);
        assertEquals(5, stringSet.size());
        assertEquals(stringSet, new LinkedHashSet<>(Arrays.asList(stringArray)));
        assertThrows(UnsupportedOperationException.class, () -> stringSet.add("modified"));
    }

    @Test
    public void testNewSet() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(SetKit.hashSet(1, 2, 3), new HashSet<>(list));
        assertEquals(HashSet.class, SetKit.hashSet(1, 2, 3).getClass());
        assertEquals(SetKit.linkedHashSet(1, 2, 3), new LinkedHashSet<>(list));
        assertEquals(LinkedHashSet.class, SetKit.linkedHashSet(1, 2, 3).getClass());

        // newSet
        assertInstanceOf(HashSet.class, SetKit.newSet(Iterable.class, 10));
        assertInstanceOf(HashSet.class, SetKit.newSet(Collection.class, 10));
        assertInstanceOf(HashSet.class, SetKit.newSet(Set.class, 10));
        assertInstanceOf(HashSet.class, SetKit.newSet(AbstractSet.class, 10));
        assertInstanceOf(HashSet.class, SetKit.newSet(HashSet.class, 10));
        assertInstanceOf(LinkedHashSet.class, SetKit.newSet(LinkedHashSet.class, 10));
        assertInstanceOf(TreeSet.class, SetKit.newSet(TreeSet.class, 10));
        assertInstanceOf(CopyOnWriteArraySet.class, SetKit.newSet(CopyOnWriteArraySet.class, 10));
        assertInstanceOf(ConcurrentSkipListSet.class, SetKit.newSet(ConcurrentSkipListSet.class, 10));
        assertNull(SetKit.newSet(Object.class, 10));
    }

    @Test
    public void testToSet() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(SetKit.toSet(list), new LinkedHashSet<>(list));
        assertNotSame(SetKit.toSet(list), new LinkedHashSet<>(list));
        assertThrows(UnsupportedOperationException.class, () -> SetKit.toSet(list).add(10));

        assertEquals(SetKit.toHashSet(list), new HashSet<>(list));
        assertNotSame(SetKit.toHashSet(list), new HashSet<>(list));
        assertEquals(HashSet.class, SetKit.toHashSet(list).getClass());
        assertEquals(SetKit.toHashSet(list::iterator), new HashSet<>(list));
        assertNotSame(SetKit.toHashSet(list::iterator), new HashSet<>(list));
        assertEquals(HashSet.class, SetKit.toHashSet(list::iterator).getClass());

        assertEquals(SetKit.toLinkedHashSet(list), new LinkedHashSet<>(list));
        assertNotSame(SetKit.toLinkedHashSet(list), new LinkedHashSet<>(list));
        assertEquals(LinkedHashSet.class, SetKit.toLinkedHashSet(list).getClass());
        assertEquals(SetKit.toLinkedHashSet(list::iterator), new LinkedHashSet<>(list));
        assertNotSame(SetKit.toLinkedHashSet(list::iterator), new LinkedHashSet<>(list));
        assertEquals(LinkedHashSet.class, SetKit.toLinkedHashSet(list::iterator).getClass());
    }
}
