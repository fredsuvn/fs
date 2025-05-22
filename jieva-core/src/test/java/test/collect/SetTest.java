package test.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.JieSet;

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
        Set<String> stringSet = JieSet.set(stringArray);
        assertEquals(stringSet.size(), 5);
        assertEquals(stringSet, new LinkedHashSet<>(Arrays.asList(stringArray)));
        expectThrows(UnsupportedOperationException.class, () -> stringSet.add("modified"));
    }

    @Test
    public void testNewSet() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(JieSet.hashSet(1, 2, 3), new HashSet<>(list));
        assertEquals(JieSet.hashSet(1, 2, 3).getClass(), HashSet.class);
        assertEquals(JieSet.linkedHashSet(1, 2, 3), new LinkedHashSet<>(list));
        assertEquals(JieSet.linkedHashSet(1, 2, 3).getClass(), LinkedHashSet.class);
    }

    @Test
    public void testToSet() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(JieSet.toSet(list), new LinkedHashSet<>(list));
        assertNotSame(JieSet.toSet(list), new LinkedHashSet<>(list));
        expectThrows(UnsupportedOperationException.class, () -> JieSet.toSet(list).add(10));

        assertEquals(JieSet.toHashSet(list), new HashSet<>(list));
        assertNotSame(JieSet.toHashSet(list), new HashSet<>(list));
        assertEquals(JieSet.toHashSet(list).getClass(), HashSet.class);
        assertEquals(JieSet.toHashSet(list::iterator), new HashSet<>(list));
        assertNotSame(JieSet.toHashSet(list::iterator), new HashSet<>(list));
        assertEquals(JieSet.toHashSet(list::iterator).getClass(), HashSet.class);

        assertEquals(JieSet.toLinkedHashSet(list), new LinkedHashSet<>(list));
        assertNotSame(JieSet.toLinkedHashSet(list), new LinkedHashSet<>(list));
        assertEquals(JieSet.toLinkedHashSet(list).getClass(), LinkedHashSet.class);
        assertEquals(JieSet.toLinkedHashSet(list::iterator), new LinkedHashSet<>(list));
        assertNotSame(JieSet.toLinkedHashSet(list::iterator), new LinkedHashSet<>(list));
        assertEquals(JieSet.toLinkedHashSet(list::iterator).getClass(), LinkedHashSet.class);
    }
}
