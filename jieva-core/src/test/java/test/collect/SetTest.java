package test.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.SetKit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.expectThrows;

public class SetTest {

    @Test
    public void testSet() {
        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        Set<String> stringSet = SetKit.set(stringArray);
        assertEquals(stringSet.size(), 5);
        assertEquals(stringSet, new LinkedHashSet<>(Arrays.asList(stringArray)));
        expectThrows(UnsupportedOperationException.class, () -> stringSet.add("modified"));
    }

    @Test
    public void testNewSet() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(SetKit.hashSet(1, 2, 3), new HashSet<>(list));
        assertEquals(SetKit.hashSet(1, 2, 3).getClass(), HashSet.class);
        assertEquals(SetKit.linkedHashSet(1, 2, 3), new LinkedHashSet<>(list));
        assertEquals(SetKit.linkedHashSet(1, 2, 3).getClass(), LinkedHashSet.class);
    }

    @Test
    public void testToSet() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(SetKit.toSet(list), new LinkedHashSet<>(list));
        assertNotSame(SetKit.toSet(list), new LinkedHashSet<>(list));
        expectThrows(UnsupportedOperationException.class, () -> SetKit.toSet(list).add(10));

        assertEquals(SetKit.toHashSet(list), new HashSet<>(list));
        assertNotSame(SetKit.toHashSet(list), new HashSet<>(list));
        assertEquals(SetKit.toHashSet(list).getClass(), HashSet.class);
        assertEquals(SetKit.toHashSet(list::iterator), new HashSet<>(list));
        assertNotSame(SetKit.toHashSet(list::iterator), new HashSet<>(list));
        assertEquals(SetKit.toHashSet(list::iterator).getClass(), HashSet.class);

        assertEquals(SetKit.toLinkedHashSet(list), new LinkedHashSet<>(list));
        assertNotSame(SetKit.toLinkedHashSet(list), new LinkedHashSet<>(list));
        assertEquals(SetKit.toLinkedHashSet(list).getClass(), LinkedHashSet.class);
        assertEquals(SetKit.toLinkedHashSet(list::iterator), new LinkedHashSet<>(list));
        assertNotSame(SetKit.toLinkedHashSet(list::iterator), new LinkedHashSet<>(list));
        assertEquals(SetKit.toLinkedHashSet(list::iterator).getClass(), LinkedHashSet.class);
    }
}
