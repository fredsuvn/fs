package test.cache;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.cache.SimpleCache;

import java.time.Duration;
import java.util.Objects;

import static org.testng.Assert.*;

public class Cache2Test {

    @Test
    public void baseTest() throws Exception {
        // base
        baseTest(SimpleCache.weak());
        baseTest(SimpleCache.soft());
        // duration and listener
        IntVar expiredCount = IntVar.of(0);
        SimpleCache<Integer, Integer> cache = SimpleCache.soft(
            Duration.ofSeconds(1),
            (key, value, cause) -> {
                if (Objects.equals(cause, SimpleCache.RemovalCause.EXPIRED)) {
                    expiredCount.getAndIncrement();
                }
            });
        cache.put(1, 1);
        Thread.sleep(1100);
        assertNull(cache.get(1));
        cache.clean();
        assertEquals(expiredCount.get(), 1);
        cache.put(1, 1);
        cache.expire(1, Duration.ofMillis(100));
        Thread.sleep(110);
        assertNull(cache.get(1));
        cache.clean();
        assertEquals(expiredCount.get(), 2);
    }

    private void baseTest(SimpleCache<Integer, Integer> cache) throws Exception {
        // get/put
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));
        cache.put(1, 1);
        assertEquals(cache.get(1), 1);
        assertEquals(cache.getVal(1).get(), 1);
        cache.put(1, 2);
        assertEquals(cache.get(1), 2);
        assertEquals(cache.getVal(1).get(), 2);
        assertNull(cache.getVal(2));
        cache.put(1, (Integer) null);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1).get());
        cache.put(1, SimpleCache.valueInfo(1, null));
        assertEquals(cache.get(1), 1);
        cache.remove(1);
        cache.put(1, SimpleCache.valueInfo(1, Duration.ofMillis(100)));
        assertEquals(cache.get(1), 1);
        Thread.sleep(110);
        assertNull(cache.get(1));
        // producer
        assertEquals(cache.get(2, k -> 2), 2);
        assertEquals(cache.get(2), 2);
        assertEquals(cache.get(2, k -> 3), 2);
        assertEquals(cache.getVal(3, k -> SimpleCache.valueInfo(3, null)).get(), 3);
        assertEquals(cache.getVal(3, k -> SimpleCache.valueInfo(4, null)).get(), 3);
        assertNull(cache.getVal(4, k -> (SimpleCache.ValueInfo<Integer>) null));
        assertNotNull(cache.getVal(4, k -> SimpleCache.valueInfo(null, null)));
        assertNull(cache.getVal(4).get());
        // contains
        cache.put(5, 5);
        assertTrue(cache.contains(5));
        assertFalse(cache.contains(6));
        // removes
        cache.remove(5);
        assertFalse(cache.contains(5));
        cache.remove(5);
        assertFalse(cache.contains(5));
        // expire
        cache.put(5, 5);
        cache.expire(5, Duration.ofSeconds(1));
        assertEquals(cache.get(5), 5);
        Thread.sleep(1100);
        assertFalse(cache.contains(5));
        assertNull(cache.get(5));
        cache.remove(5);
        cache.expire(5, Duration.ofSeconds(1));
        // clear and size
        cache.clear();
        assertEquals(cache.size(), 0);
    }
}
