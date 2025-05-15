package test.cache;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.cache.SimpleCache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class CacheTest {

    @Test
    public void testNoGc() throws Exception {
        testCache(SimpleCache.ofStrong());
    }

    private void testCache(SimpleCache<Integer, Integer> cache) {
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
        cache.put(1, null);
        assertNull(cache.get(1));
        assertNotNull(cache.getVal(1));
        assertNull(cache.getVal(1).get());

        // remove
        cache.remove(0);
        assertNull(cache.get(0));
        assertNull(cache.getVal(0));
        cache.remove(1);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));

        // producer
        assertNull(cache.getVal(2));
        assertEquals(cache.get(2, k -> 2), 2);
        assertEquals(cache.get(2), 2);
        assertEquals(cache.get(2, k -> 3), 2);
        assertNull(cache.getVal(3));
        assertEquals(cache.getVal(3, k -> Val.of(3)).get(), 3);
        assertEquals(cache.get(3), 3);
        assertEquals(cache.getVal(3, k -> Val.of(4)).get(), 3);
        assertNull(cache.getVal(4, k -> null));
        assertNull(cache.getVal(4));

        // clear
        assertNotNull(cache.getVal(2));
        cache.clear();
        assertNull(cache.getVal(1));
        assertNull(cache.getVal(2));
        assertNull(cache.getVal(3));
        assertNull(cache.getVal(4));
    }

    @Test
    public void testPhantom() {
        // get/put
        SimpleCache<Integer, Integer> cache = SimpleCache.ofPhantom();
        cache.put(1, 1);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));

        // producer
        assertNull(cache.getVal(2));
        assertEquals(cache.get(2, k -> 2), 2);
        assertEquals(cache.get(2, k -> 3), 3);
        assertNull(cache.get(2));
        assertNull(cache.getVal(2));
        assertNull(cache.getVal(3));
        assertEquals(cache.getVal(3, k -> Val.of(2)).get(), 2);
        assertEquals(cache.getVal(3, k -> Val.of(3)).get(), 3);
        assertNull(cache.get(3));
        assertNull(cache.getVal(3));

        // clean
        cache.put(1, 1);
        cache.remove(1);
        cache.clean();
    }

    @Test
    public void testWeakAndSoft() {
        // Most methods are tested by testNoGc and testPhantom.
        testSimpleOps(SimpleCache.ofWeak());
        testSimpleOps(SimpleCache.ofSoft());
    }

    private void testSimpleOps(SimpleCache<Integer, Integer> cache) {
        cache.put(1, 1);
        cache.get(1);
        cache.remove(1);
        cache.clean();
    }

    // Test for out of memory.
    //@Test
    public void testMemory() throws Exception {
        testMemory(SimpleCache.ofWeak());
        testMemory(SimpleCache.ofSoft());
    }

    private void testMemory(SimpleCache<Integer, Integer> cache) throws Exception {
        CountDownLatch latch = new CountDownLatch(10);
        List<Thread> threads = new ArrayList<>();
        AtomicBoolean flag = new AtomicBoolean(true);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                while (flag.get()) {
                    cache.put(JieRandom.nextInt(), JieRandom.nextInt());
                }
                latch.countDown();
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        Jie.sleep(Duration.ofSeconds(10));
        flag.set(false);
        latch.await();
    }
}
