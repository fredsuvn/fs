package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.Kit;
import space.sunqian.common.collect.MapKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapTest {

    @Test
    public void testEmpty() {
        // Testing with empty map
        assertTrue(MapKit.isEmpty(Collections.emptyMap()));
        assertFalse(MapKit.isNotEmpty(Collections.emptyMap()));
        // Testing with empty map
        assertTrue(MapKit.isEmpty((Map<?, ?>) null));
        assertFalse(MapKit.isNotEmpty((Map<?, ?>) null));
        // Testing with non-empty map
        assertFalse(MapKit.isEmpty(Collections.singletonMap("key", "value")));
        assertTrue(MapKit.isNotEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    public void testMap() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        map.put(3, 4);
        map.put(5, 6);
        assertEquals(MapKit.map(1, 2, 3, 4, 5, 6), map);
        assertThrows(UnsupportedOperationException.class, () -> MapKit.map(1, 2, 3, 4, 5, 6).put(7, 8));
    }

    @Test
    public void testNewMap() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        map.put(3, 4);
        map.put(5, 6);
        assertEquals(MapKit.hashMap(1, 2, 3, 4, 5, 6), new HashMap<>(map));
        assertEquals(MapKit.hashMap(1, 2, 3, 4, 5, 6).getClass(), HashMap.class);
        assertEquals(MapKit.linkedHashMap(1, 2, 3, 4, 5, 6), new LinkedHashMap<>(map));
        assertEquals(MapKit.linkedHashMap(1, 2, 3, 4, 5, 6).getClass(), LinkedHashMap.class);
    }

    @Test
    public void testToMap() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        map.put(3, 4);
        map.put(5, 6);
        List<Object> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        assertEquals(MapKit.toMap(list), new LinkedHashMap<>(map));
        assertThrows(UnsupportedOperationException.class, () -> MapKit.toMap(list).put(7, 8));

        assertEquals(MapKit.toHashMap(list), new HashMap<>(map));
        assertNotSame(MapKit.toHashMap(list), new HashMap<>(map));
        assertEquals(MapKit.toHashMap(list).getClass(), HashMap.class);
        assertEquals(MapKit.toHashMap(list::iterator), new HashMap<>(map));
        assertNotSame(MapKit.toHashMap(list::iterator), new HashMap<>(map));
        assertEquals(MapKit.toHashMap(list::iterator).getClass(), HashMap.class);

        assertEquals(MapKit.toLinkedHashMap(list), new LinkedHashMap<>(map));
        assertNotSame(MapKit.toLinkedHashMap(list), new LinkedHashMap<>(map));
        assertEquals(MapKit.toLinkedHashMap(list).getClass(), LinkedHashMap.class);
        assertEquals(MapKit.toLinkedHashMap(list::iterator), new LinkedHashMap<>(map));
        assertNotSame(MapKit.toLinkedHashMap(list::iterator), new LinkedHashMap<>(map));
        assertEquals(MapKit.toLinkedHashMap(list::iterator).getClass(), LinkedHashMap.class);


        Map<Integer, Integer> intMap = new LinkedHashMap<>();
        intMap.put(1, 2);
        intMap.put(3, 4);
        intMap.put(5, 6);
        Map<String, String> strMap1 = MapKit.toMap(
            intMap,
            Object::toString,
            Object::toString
        );
        assertEquals(strMap1, MapKit.toMap(Arrays.asList("1", "2", "3", "4", "5", "6")));
        Map<String, String> strMap2 = MapKit.toMap(
            intMap,
            k -> Kit.equals(k, 5) ? "3" : k.toString(),
            Object::toString
        );
        assertEquals(strMap2, MapKit.toMap(Arrays.asList("1", "2", "3", "6")));
        assertThrows(UnsupportedOperationException.class, () -> strMap2.put("", ""));
    }

    @Test
    public void testPutAll() {
        testPutAll(6);
        testPutAll(7);
    }

    private void testPutAll(int size) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        int i = 1;
        while (true) {
            int k = i++;
            int v = i++;
            if (v < size) {
                map.put(k, v);
            } else {
                map.put(k, v == size ? v : null);
                break;
            }
        }
        List<Object> list = new ArrayList<>();
        i = 1;
        for (int j = 0; j < size; j++) {
            list.add(i++);
        }
        assertEquals(MapKit.putAll(new LinkedHashMap<>(), list), new LinkedHashMap<>(map));
        assertEquals(MapKit.putAll(new LinkedHashMap<>(), list.toArray()), new LinkedHashMap<>(map));
        assertEquals(MapKit.putAll(new LinkedHashMap<>(), list::iterator), new LinkedHashMap<>(map));
    }

    @Test
    public void testResolveChain() {
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
        assertEquals(MapKit.resolveChain(map, 1, new HashSet<>()), 3);
        assertEquals(MapKit.resolveChain(map, 2, new HashSet<>()), 3);
        assertNull(MapKit.resolveChain(map, 3, new HashSet<>()));
        assertNull(MapKit.resolveChain(map, 10, new HashSet<>()));
        assertNull(MapKit.resolveChain(map, 11, new HashSet<>()));
        assertEquals(null, MapKit.resolveChain(map, 20, new HashSet<>()));
        assertEquals(null, MapKit.resolveChain(map, 6, new HashSet<>()));
    }

    @Test
    public void testEntry() {
        Map.Entry<String, String> entry = MapKit.entry("1", "2");
        assertEquals(entry.getKey(), "1");
        assertEquals(entry.getValue(), "2");
        assertThrows(UnsupportedOperationException.class, () -> entry.setValue("3"));
    }
}
