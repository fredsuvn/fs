package test.coll;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.coll.JieColl;

import java.util.*;
import java.util.stream.Stream;

import static org.testng.Assert.*;

public class CollTest {

    @Test
    public void testEmpty() {
        // Testing with null
        assertTrue(JieColl.isEmpty((Iterable<?>) null));
        assertFalse(JieColl.isNotEmpty((Iterable<?>) null));
        // Testing with empty collection
        assertTrue(JieColl.isEmpty(Collections.emptyList()));
        assertFalse(JieColl.isNotEmpty(Collections.emptyList()));
        // Testing with non-empty collection
        assertFalse(JieColl.isEmpty(Jie.list(1, 2, 3)));
        assertTrue(JieColl.isNotEmpty(Jie.list(1, 2, 3)));
        // Testing with empty iterable
        assertTrue(JieColl.isEmpty(iterable()));
        assertFalse(JieColl.isNotEmpty(iterable()));
        assertTrue(JieColl.isNotEmpty(iterable(new Object[10])));
        // Testing with non-empty iterable
        assertFalse(JieColl.isEmpty(Collections.singletonList(1)));
        assertTrue(JieColl.isNotEmpty(Collections.singletonList(1)));
        // Testing with empty map
        assertTrue(JieColl.isEmpty(Collections.emptyMap()));
        assertFalse(JieColl.isNotEmpty(Collections.emptyMap()));
        // Testing with empty map
        assertTrue(JieColl.isEmpty((Map<?, ?>) null));
        assertFalse(JieColl.isNotEmpty((Map<?, ?>) null));
        // Testing with non-empty map
        assertFalse(JieColl.isEmpty(Collections.singletonMap("key", "value")));
        assertTrue(JieColl.isNotEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    public void testAsList() {
        assertEquals(Jie.list(), Collections.emptyList());
        assertEquals(Jie.list(1, 2, 3), Arrays.asList(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> Jie.list(1, 2, 3).set(0, 2));
        Integer[] is = {2, 3, 4};
        assertEquals(Jie.list(is), Arrays.asList(2, 3, 4));
        is[0] = 666;
        assertEquals(Jie.list(is), Arrays.asList(666, 3, 4));
    }

    @Test
    public void testToArray() {
        // Testing toObjectArray method
        assertEquals(JieColl.toObjectArray(iterable()), new Object[0]);
        assertEquals(JieColl.toObjectArray(iterable(1, 2, 3)), Jie.array(1, 2, 3));
        assertEquals(JieColl.toObjectArray(Jie.list(1, 2, 3)), new Object[]{1, 2, 3});

        // Testing toArray method for Iterable
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(Collections.emptyList()));
        assertEquals(JieColl.toArray(Jie.list(1, 2, 3)), Jie.array(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(Jie.list(null, null, null)));
        assertEquals(JieColl.toArray(Jie.list(null, 2, null)), Jie.array(null, 2, null));
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(iterable()));
        assertEquals(JieColl.toArray(iterable(1, 2, 3)), Jie.array(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(iterable(null, null, null)));
        assertEquals(JieColl.toArray(iterable(null, 2, null)), Jie.array(null, 2, null));
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray((Iterable<?>) Collections.emptyList()));
        assertEquals(JieColl.toArray((Iterable<?>) Jie.list(1, 2, 3)), Jie.array(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray((Iterable<?>) Jie.list(null, null, null)));
        assertEquals(JieColl.toArray((Iterable<?>) Jie.list(null, 2, null)), Jie.array(null, 2, null));

        assertEquals(JieColl.toArray(Jie.list(1, 2, 3), Integer.class), Jie.array(1, 2, 3));
        assertEquals(JieColl.toArray(Jie.list(), Integer.class), new Integer[0]);
        assertEquals(JieColl.toArray(Jie.list(1, 2, 3), Long.class, i -> (long) i), Jie.array(1L, 2L, 3L));
        assertEquals(JieColl.toArray(Jie.list(), Long.class, i -> (long) i), new Long[0]);
    }

    @Test
    public void testToCollection() {
        // list
        assertEquals(JieColl.toList(Jie.array()), Collections.emptyList());
        assertEquals(JieColl.toList(Jie.array(1, 2, 3)), Jie.list(1, 2, 3));
        assertEquals(JieColl.toList(Jie.list(1, 2, 3)), Jie.list(1, 2, 3));
        assertEquals(JieColl.toList(Collections.emptyList()), Collections.emptyList());
        expectThrows(UnsupportedOperationException.class, () -> JieColl.asImmutableList(Jie.array(1, 2, 3)).set(0, 2));
        assertEquals(JieColl.toList(Jie.array(1, 2, 3)).get(1), 2);
        assertEquals(JieColl.toStringList(Jie.list(1, 2, 3)), Jie.list("1", "2", "3"));
        assertEquals(JieColl.toStringList(Jie.array(1, 2, 3)), Jie.list("1", "2", "3"));
        assertEquals(JieColl.toStringList(Collections.emptyList()), Collections.emptyList());
        assertEquals(JieColl.toList(Jie.list(), Object::toString), Collections.emptyList());
        assertEquals(JieColl.toList(Jie.array(), Object::toString), Collections.emptyList());
        // set
        assertEquals(JieColl.toSet(Jie.array()), Collections.emptySet());
        assertEquals(JieColl.toSet(Jie.array(1, 2, 3)), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        assertEquals(JieColl.toSet(Jie.list(1, 2, 3)), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        assertEquals(JieColl.toSet(Collections.emptySet()), Collections.emptySet());
        expectThrows(UnsupportedOperationException.class, () -> JieColl.toSet(Jie.array(1, 2, 3)).add(1));
        assertEquals(JieColl.toSet(Jie.array(1, 2, 3)).iterator().next(), 1);
        assertEquals(JieColl.toStringSet(Jie.list(1, 2, 3)), new LinkedHashSet<>(Jie.list("1", "2", "3")));
        assertEquals(JieColl.toStringSet(Jie.array(1, 2, 3)), new LinkedHashSet<>(Jie.list("1", "2", "3")));
        assertEquals(JieColl.toStringSet(Collections.emptySet()), Collections.emptySet());
        assertEquals(JieColl.toSet(Jie.list(), Object::toString), Collections.emptyList());
        assertEquals(JieColl.toSet(Jie.array(), Object::toString), Collections.emptyList());
        // map
        assertEquals(JieColl.toMap(Jie.array()), Collections.emptyMap());
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Map<Integer, Integer> map2 = new LinkedHashMap<>(map);
        map2.put(3, 4);
        assertEquals(JieColl.toMap(Jie.array(1, 2, 3)), map);
        assertEquals(JieColl.toMap(Jie.array(1, 2, 3, 4)), map2);
        assertEquals(JieColl.toMap(Collections.emptyList()), Collections.emptyMap());
        assertEquals(JieColl.toMap(Jie.list(1, 2, 3)), map);
        assertEquals(JieColl.toMap(Jie.list(1, 2, 3, 4)), map2);
        Properties props = new Properties();
        props.put(1, 2);
        props.put(3, 4);
        Map<String, String> strMap = new LinkedHashMap<>();
        strMap.put("1", "2");
        Map<String, String> strMap2 = new LinkedHashMap<>(strMap);
        strMap2.put("3", "4");
        assertEquals(JieColl.toStringMap(props), strMap2);
        assertEquals(JieColl.toMap(map2, String::valueOf, String::valueOf), strMap2);
        assertEquals(JieColl.toMap(Collections.emptyMap(), String::valueOf, String::valueOf), Collections.emptyMap());
        // pairToMap
        Map<Long, Long> lmap = new LinkedHashMap<>();
        lmap.put(1L * 2, 1L * 3);
        lmap.put(2L * 2, 2L * 3);
        assertEquals(JieColl.toPairs(Jie.array(1, 2), i -> (long) i * 2, i -> (long) i * 3), lmap);
        assertEquals(JieColl.toPairs(Jie.array(), i -> (long) i * 2, i -> (long) i * 3), Collections.emptyMap());
        assertEquals(JieColl.toPairs(Jie.list(1, 2), i -> (long) i * 2, i -> (long) i * 3), lmap);
        assertEquals(JieColl.toPairs(Collections.emptyList(), i -> (long) i, i -> (long) i), Collections.emptyMap());
        // addAll
        assertEquals(JieColl.addAll(new ArrayList<>(), Jie.array()), Collections.emptyList());
        assertEquals(JieColl.addAll(new ArrayList<>(), Jie.list()), Collections.emptyList());
        assertEquals(JieColl.addAll(new ArrayList<>(), Jie.array(), Object::toString), Collections.emptyList());
        assertEquals(JieColl.addAll(new ArrayList<>(), Jie.list(), Object::toString), Collections.emptyList());
        // putAll
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array()), Collections.emptyMap());
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list()), Collections.emptyMap());
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(1, 2, 3), String::valueOf), strMap);
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(1, 2, 3, 4), String::valueOf), strMap2);
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(1, 2, 3), String::valueOf), strMap);
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(1, 2, 3, 4), String::valueOf), strMap2);
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(), String::valueOf), Collections.emptyMap());
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(), String::valueOf), Collections.emptyMap());
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Collections.emptyMap(), String::valueOf, String::valueOf), Collections.emptyMap());
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(), String::valueOf, String::valueOf), Collections.emptyMap());
        assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(), String::valueOf, String::valueOf), Collections.emptyMap());
    }

    @Test
    public void testOr() {
        List<Integer> list = Jie.list(1, 2, 3);
        assertSame(JieColl.orList(list), list);
        assertNotSame(JieColl.orList(iterable(1, 2, 3)), list);
        assertEquals(JieColl.orList(iterable(1, 2, 3)), list);
        Set<Integer> set = Jie.set(1, 2, 3);
        assertSame(JieColl.orSet(set), set);
        assertNotSame(JieColl.orSet(iterable(1, 2, 3)), set);
        assertEquals(JieColl.orSet(iterable(1, 2, 3)), set);
        Collection<Integer> collection = Jie.set(1, 2, 3);
        assertSame(JieColl.orCollection(collection), collection);
        assertNotSame(JieColl.orCollection(iterable(1, 2, 3)), collection);
        assertEquals(JieColl.orCollection(iterable(1, 2, 3)), collection);
    }

    @Test
    public void testEnumeration() {
        assertEquals(JieColl.asIterable(null), Collections.emptyList());
        Enumeration<Integer> enumeration = JieColl.asEnumeration(Jie.list(1, 2, 3));
        assertEquals(enumeration.nextElement(), 1);
        assertEquals(enumeration.nextElement(), 2);
        assertEquals(enumeration.nextElement(), 3);
        assertFalse(enumeration.hasMoreElements());
        Iterable<Integer> iterable = JieColl.asIterable(JieColl.asEnumeration(Jie.list(1, 2, 3)));
        Iterator<Integer> iterator = iterable.iterator();
        assertEquals(iterator.next(), 1);
        assertEquals(iterator.next(), 2);
        assertEquals(iterator.next(), 3);
        assertFalse(iterator.hasNext());
        assertEquals(JieColl.asIterable(JieColl.asEnumeration(null)), Collections.emptyList());
        expectThrows(NoSuchElementException.class, () -> JieColl.asEnumeration(null).nextElement());
    }

    @Test
    public void testStream() {
        assertEquals(JieColl.stream(Jie.array()).toArray(), Stream.empty().toArray());
        assertEquals(JieColl.stream(Jie.list()).toArray(), Stream.empty().toArray());
    }

    @Test
    public void testGet() {
        assertEquals(JieColl.get(Jie.list(1, 2, null), 1, 6), 2);
        assertEquals(JieColl.get(Jie.list(1, 2, null), -1, 6), 6);
        assertEquals(JieColl.get(Jie.list(1, 2, null), 100, 6), 6);
        assertEquals(JieColl.get(Jie.list(1, 2, null), 2, 6), 6);
        assertEquals(JieColl.get(iterable(1, 2, null), 1, 6), 2);
        assertEquals(JieColl.get(iterable(1, 2, null), -1, 6), 6);
        assertEquals(JieColl.get(iterable(1, 2, null), 100, 6), 6);
        assertEquals(JieColl.get(iterable(1, 2, null), 2, 6), 6);
        assertEquals(JieColl.get((List<Integer>) null, 1, 6), 6);

        assertEquals(JieColl.get(Jie.map(1, 2, 3), 1, 6), 2);
        assertEquals(JieColl.get(Jie.map(1, 2, null), -1, 6), 6);
        assertEquals(JieColl.get((Map<Integer, Integer>) null, 1, 6), 6);
    }

    @Test
    public void testNestedGet() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 2);
        map.put(2, 3);
        map.put(10, 11);
        map.put(11, 10);
        map.put(20, 20);
        map.put(6, 7);
        map.put(7, 8);
        map.put(8, 9);
        map.put(9, 8);
        assertEquals(JieColl.getRecursive(map, 1, new HashSet<>()), 3);
        assertEquals(JieColl.getRecursive(map, 2, new HashSet<>()), 3);
        assertEquals(JieColl.getRecursive(map, 3, new HashSet<>()), null);
        assertEquals(JieColl.getRecursive(map, 10, new HashSet<>()), 11);
        assertEquals(JieColl.getRecursive(map, 20, new HashSet<>()), 20);
        assertEquals(JieColl.getRecursive(map, 6, new HashSet<>()), 9);
        expectThrows(IllegalStateException.class, () -> JieColl.getRecursive(map, 6, new HashSet<>(), true));
    }

    private <T> Iterable<T> iterable(T... array) {
        return () -> new Iterator<T>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public T next() {
                return array[i++];
            }
        };
    }
}
