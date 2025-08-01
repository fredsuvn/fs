package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.Tuple;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.expectThrows;

public class BaseTest {

    @Test
    public void testConvenient() {
        assertEquals(Jie.array(1, 2, 3), new Integer[]{1, 2, 3});
        assertEquals(Jie.list(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(Jie.list(1, 2, 3).get(1), 2);
        expectThrows(UnsupportedOperationException.class, () -> Jie.list(1, 2, 3).set(1, 2));
        Integer[] is = new Integer[]{1, 2, 3};
        List<Integer> list = Jie.list(is);
        assertEquals(list, Arrays.asList(1, 2, 3));
        is[1] = 888;
        assertEquals(list, Arrays.asList(1, 888, 3));
        assertEquals(Jie.set(1, 2, 3), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        assertEquals(Jie.set(1, 2, 3, 3, 2), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Map<Integer, Integer> map2 = new LinkedHashMap<>(map);
        map2.put(3, 4);
        assertEquals(Jie.map(1, 2), map);
        assertEquals(Jie.map(1, 2, 3, 4), map2);
        map.put(3, null);
        assertEquals(Jie.map(1, 2, 3), map);
    }

    @Test
    public void testTuple() {
        Tuple tuple = Tuple.of(0, "1", 2L);
        assertEquals(tuple.size(), 3);
        assertEquals(tuple.set(0, 0), tuple);
        assertEquals((Integer) tuple.get(0), 0);
        assertEquals(tuple.get(1), "1");
        assertEquals((Long) tuple.get(2), 2L);
        assertEquals(tuple, Tuple.of(0, "1", 2L));
        assertNotEquals(tuple, Tuple.of(0, "1", 22L));
        assertNotSame(tuple, Tuple.of(0, "1", 2L));
        tuple.set(1, "11");
        assertEquals(tuple.get(1), "11");
        assertEquals(tuple, Tuple.of(0, "11", 2L));
        tuple.set(1, new int[]{1, 2});
        tuple.set(2, Jie.arrayList("22", "33"));
        assertEquals(tuple, Tuple.of(0, new int[]{1, 2}, Jie.arrayList("22", "33")));
        assertEquals(tuple.hashCode(), Tuple.of(0, new int[]{1, 2}, Jie.arrayList("22", "33")).hashCode());
        assertEquals(Tuple.of(0, "1", 2L).toString(), Arrays.deepToString(new Object[]{0, "1", 2L}));
        assertFalse(tuple.equals(""));
        assertFalse(tuple.equals(null));
    }
}
