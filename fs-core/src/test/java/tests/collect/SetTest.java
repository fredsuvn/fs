package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.collect.SetKit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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
