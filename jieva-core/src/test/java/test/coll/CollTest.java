package test.coll;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collection.JieCollection;

import java.util.*;
import java.util.stream.Stream;

import static org.testng.Assert.*;

public class CollTest {

    @Test
    public void testEmpty() {
        // Testing with null
        assertTrue(JieCollection.isEmpty((Iterable<?>) null));
        assertFalse(JieCollection.isNotEmpty((Iterable<?>) null));
        // Testing with empty collection
        assertTrue(JieCollection.isEmpty(Collections.emptyList()));
        assertFalse(JieCollection.isNotEmpty(Collections.emptyList()));
        // Testing with non-empty collection
        assertFalse(JieCollection.isEmpty(Jie.list(1, 2, 3)));
        assertTrue(JieCollection.isNotEmpty(Jie.list(1, 2, 3)));
        // Testing with empty iterable
        assertTrue(JieCollection.isEmpty(iterable()));
        assertFalse(JieCollection.isNotEmpty(iterable()));
        assertTrue(JieCollection.isNotEmpty(iterable(new Object[10])));
        // Testing with non-empty iterable
        assertFalse(JieCollection.isEmpty(Collections.singletonList(1)));
        assertTrue(JieCollection.isNotEmpty(Collections.singletonList(1)));
        // Testing with empty map
        assertTrue(JieCollection.isEmpty(Collections.emptyMap()));
        assertFalse(JieCollection.isNotEmpty(Collections.emptyMap()));
        // Testing with empty map
        assertTrue(JieCollection.isEmpty((Map<?, ?>) null));
        assertFalse(JieCollection.isNotEmpty((Map<?, ?>) null));
        // Testing with non-empty map
        assertFalse(JieCollection.isEmpty(Collections.singletonMap("key", "value")));
        assertTrue(JieCollection.isNotEmpty(Collections.singletonMap("key", "value")));
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
        assertEquals(JieCollection.toObjectArray(iterable()), new Object[0]);
        assertEquals(JieCollection.toObjectArray(iterable(1, 2, 3)), Jie.array(1, 2, 3));
        assertEquals(JieCollection.toObjectArray(Jie.list(1, 2, 3)), new Object[]{1, 2, 3});

        // Testing toArray method for Iterable
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toArray(Collections.emptyList()));
        assertEquals(JieCollection.toArray(Jie.list(1, 2, 3)), Jie.array(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toArray(Jie.list(null, null, null)));
        assertEquals(JieCollection.toArray(Jie.list(null, 2, null)), Jie.array(null, 2, null));
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toArray(iterable()));
        assertEquals(JieCollection.toArray(iterable(1, 2, 3)), Jie.array(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toArray(iterable(null, null, null)));
        assertEquals(JieCollection.toArray(iterable(null, 2, null)), Jie.array(null, 2, null));
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toArray((Iterable<?>) Collections.emptyList()));
        assertEquals(JieCollection.toArray((Iterable<?>) Jie.list(1, 2, 3)), Jie.array(1, 2, 3));
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toArray((Iterable<?>) Jie.list(null, null, null)));
        assertEquals(JieCollection.toArray((Iterable<?>) Jie.list(null, 2, null)), Jie.array(null, 2, null));

        assertEquals(JieCollection.toArray(Jie.list(1, 2, 3), Integer.class), Jie.array(1, 2, 3));
        assertEquals(JieCollection.toArray(Jie.list(), Integer.class), new Integer[0]);
        assertEquals(JieCollection.toArray(Jie.list(1, 2, 3), Long.class, i -> (long) i), Jie.array(1L, 2L, 3L));
        assertEquals(JieCollection.toArray(Jie.list(), Long.class, i -> (long) i), new Long[0]);
    }

    @Test
    public void testToCollection() {
        // list
        assertEquals(JieCollection.toList(Jie.array()), Collections.emptyList());
        assertEquals(JieCollection.toList(Jie.array(1, 2, 3)), Jie.list(1, 2, 3));
        assertEquals(JieCollection.toList(Jie.list(1, 2, 3)), Jie.list(1, 2, 3));
        assertEquals(JieCollection.toList(Collections.emptyList()), Collections.emptyList());
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.asImmutableList(Jie.array(1, 2, 3)).set(0, 2));
        assertEquals(JieCollection.toList(Jie.array(1, 2, 3)).get(1), 2);
        assertEquals(JieCollection.toStringList(Jie.list(1, 2, 3)), Jie.list("1", "2", "3"));
        assertEquals(JieCollection.toStringList(Jie.array(1, 2, 3)), Jie.list("1", "2", "3"));
        assertEquals(JieCollection.toStringList(Collections.emptyList()), Collections.emptyList());
        assertEquals(JieCollection.toList(Jie.list(), Object::toString), Collections.emptyList());
        assertEquals(JieCollection.toList(Jie.array(), Object::toString), Collections.emptyList());
        // set
        assertEquals(JieCollection.toSet(Jie.array()), Collections.emptySet());
        assertEquals(JieCollection.toSet(Jie.array(1, 2, 3)), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        assertEquals(JieCollection.toSet(Jie.list(1, 2, 3)), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        assertEquals(JieCollection.toSet(Collections.emptySet()), Collections.emptySet());
        expectThrows(UnsupportedOperationException.class, () -> JieCollection.toSet(Jie.array(1, 2, 3)).add(1));
        assertEquals(JieCollection.toSet(Jie.array(1, 2, 3)).iterator().next(), 1);
        assertEquals(JieCollection.toStringSet(Jie.list(1, 2, 3)), new LinkedHashSet<>(Jie.list("1", "2", "3")));
        assertEquals(JieCollection.toStringSet(Jie.array(1, 2, 3)), new LinkedHashSet<>(Jie.list("1", "2", "3")));
        assertEquals(JieCollection.toStringSet(Collections.emptySet()), Collections.emptySet());
        assertEquals(JieCollection.toSet(Jie.list(), Object::toString), Collections.emptyList());
        assertEquals(JieCollection.toSet(Jie.array(), Object::toString), Collections.emptyList());
        // map
        assertEquals(JieCollection.toMap(Jie.array()), Collections.emptyMap());
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Map<Integer, Integer> map2 = new LinkedHashMap<>(map);
        map2.put(3, 4);
        assertEquals(JieCollection.toMap(Jie.array(1, 2, 3)), map);
        assertEquals(JieCollection.toMap(Jie.array(1, 2, 3, 4)), map2);
        assertEquals(JieCollection.toMap(Collections.emptyList()), Collections.emptyMap());
        assertEquals(JieCollection.toMap(Jie.list(1, 2, 3)), map);
        assertEquals(JieCollection.toMap(Jie.list(1, 2, 3, 4)), map2);
        Properties props = new Properties();
        props.put(1, 2);
        props.put(3, 4);
        Map<String, String> strMap = new LinkedHashMap<>();
        strMap.put("1", "2");
        Map<String, String> strMap2 = new LinkedHashMap<>(strMap);
        strMap2.put("3", "4");
        assertEquals(JieCollection.toStringMap(props), strMap2);
        assertEquals(JieCollection.toMap(map2, String::valueOf, String::valueOf), strMap2);
        assertEquals(JieCollection.toMap(Collections.emptyMap(), String::valueOf, String::valueOf), Collections.emptyMap());
        // pairToMap
        Map<Long, Long> lmap = new LinkedHashMap<>();
        lmap.put(1L * 2, 1L * 3);
        lmap.put(2L * 2, 2L * 3);
        assertEquals(JieCollection.toPairs(Jie.array(1, 2), i -> (long) i * 2, i -> (long) i * 3), lmap);
        assertEquals(JieCollection.toPairs(Jie.array(), i -> (long) i * 2, i -> (long) i * 3), Collections.emptyMap());
        assertEquals(JieCollection.toPairs(Jie.list(1, 2), i -> (long) i * 2, i -> (long) i * 3), lmap);
        assertEquals(JieCollection.toPairs(Collections.emptyList(), i -> (long) i, i -> (long) i), Collections.emptyMap());
        // addAll
        assertEquals(JieCollection.addAll(new ArrayList<>(), Jie.array()), Collections.emptyList());
        assertEquals(JieCollection.addAll(new ArrayList<>(), Jie.list()), Collections.emptyList());
        assertEquals(JieCollection.addAll(new ArrayList<>(), Jie.array(), Object::toString), Collections.emptyList());
        assertEquals(JieCollection.addAll(new ArrayList<>(), Jie.list(), Object::toString), Collections.emptyList());
        // putAll
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.array()), Collections.emptyMap());
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.list()), Collections.emptyMap());
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.array(1, 2, 3), String::valueOf), strMap);
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.array(1, 2, 3, 4), String::valueOf), strMap2);
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.list(1, 2, 3), String::valueOf), strMap);
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.list(1, 2, 3, 4), String::valueOf), strMap2);
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.array(), String::valueOf), Collections.emptyMap());
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.list(), String::valueOf), Collections.emptyMap());
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Collections.emptyMap(), String::valueOf, String::valueOf), Collections.emptyMap());
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.array(), String::valueOf, String::valueOf), Collections.emptyMap());
        assertEquals(JieCollection.putAll(new LinkedHashMap<>(), Jie.list(), String::valueOf, String::valueOf), Collections.emptyMap());
    }

    @Test
    public void testOr() {
        List<Integer> list = Jie.list(1, 2, 3);
        assertSame(JieCollection.orList(list), list);
        assertNotSame(JieCollection.orList(iterable(1, 2, 3)), list);
        assertEquals(JieCollection.orList(iterable(1, 2, 3)), list);
        Set<Integer> set = Jie.set(1, 2, 3);
        assertSame(JieCollection.orSet(set), set);
        assertNotSame(JieCollection.orSet(iterable(1, 2, 3)), set);
        assertEquals(JieCollection.orSet(iterable(1, 2, 3)), set);
        Collection<Integer> collection = Jie.set(1, 2, 3);
        assertSame(JieCollection.orCollection(collection), collection);
        assertNotSame(JieCollection.orCollection(iterable(1, 2, 3)), collection);
        assertEquals(JieCollection.orCollection(iterable(1, 2, 3)), collection);
    }

    @Test
    public void testEnumeration() {
        assertEquals(JieCollection.asIterable(null), Collections.emptyList());
        Enumeration<Integer> enumeration = JieCollection.asEnumeration(Jie.list(1, 2, 3));
        assertEquals(enumeration.nextElement(), 1);
        assertEquals(enumeration.nextElement(), 2);
        assertEquals(enumeration.nextElement(), 3);
        assertFalse(enumeration.hasMoreElements());
        Iterable<Integer> iterable = JieCollection.asIterable(JieCollection.asEnumeration(Jie.list(1, 2, 3)));
        Iterator<Integer> iterator = iterable.iterator();
        assertEquals(iterator.next(), 1);
        assertEquals(iterator.next(), 2);
        assertEquals(iterator.next(), 3);
        assertFalse(iterator.hasNext());
        assertEquals(JieCollection.asIterable(JieCollection.asEnumeration(null)), Collections.emptyList());
        expectThrows(NoSuchElementException.class, () -> JieCollection.asEnumeration(null).nextElement());
    }

    @Test
    public void testStream() {
        assertEquals(JieCollection.stream(Jie.array()).toArray(), Stream.empty().toArray());
        assertEquals(JieCollection.stream(Jie.list()).toArray(), Stream.empty().toArray());
    }

    @Test
    public void testGet() {
        assertEquals(JieCollection.get(Jie.list(1, 2, null), 1, 6), 2);
        assertEquals(JieCollection.get(Jie.list(1, 2, null), -1, 6), 6);
        assertEquals(JieCollection.get(Jie.list(1, 2, null), 100, 6), 6);
        assertEquals(JieCollection.get(Jie.list(1, 2, null), 2, 6), 6);
        assertEquals(JieCollection.get(iterable(1, 2, null), 1, 6), 2);
        assertEquals(JieCollection.get(iterable(1, 2, null), -1, 6), 6);
        assertEquals(JieCollection.get(iterable(1, 2, null), 100, 6), 6);
        assertEquals(JieCollection.get(iterable(1, 2, null), 2, 6), 6);
        assertEquals(JieCollection.get((List<Integer>) null, 1, 6), 6);

        assertEquals(JieCollection.get(Jie.map(1, 2, 3), 1, 6), 2);
        assertEquals(JieCollection.get(Jie.map(1, 2, null), -1, 6), 6);
        assertEquals(JieCollection.get((Map<Integer, Integer>) null, 1, 6), 6);
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
        assertEquals(JieCollection.getRecursive(map, 1, new HashSet<>()), 3);
        assertEquals(JieCollection.getRecursive(map, 2, new HashSet<>()), 3);
        assertEquals(JieCollection.getRecursive(map, 3, new HashSet<>()), null);
        assertEquals(JieCollection.getRecursive(map, 10, new HashSet<>()), 11);
        assertEquals(JieCollection.getRecursive(map, 20, new HashSet<>()), 20);
        assertEquals(JieCollection.getRecursive(map, 6, new HashSet<>()), 9);
        expectThrows(IllegalStateException.class, () -> JieCollection.getRecursive(map, 6, new HashSet<>(), true));
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
