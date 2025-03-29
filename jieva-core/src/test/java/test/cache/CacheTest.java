package test.cache;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.cache.SimpleCache;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static org.testng.Assert.*;

public class CacheTest {

    @Test
    public void testNoGc() throws Exception {
        // base
        {
            baseTest(SimpleCache.weak());
            baseTest(SimpleCache.soft());
        }
        // duration and listener
        {
            IntVar expiredCount = IntVar.of(0);
            IntVar replaceCount = IntVar.of(0);
            IntVar explicitCount = IntVar.of(0);
            SimpleCache<Integer, Integer> cache = SimpleCache.soft(
                    Duration.ofMillis(50),
                    (key, value, cause) -> {
                        if (Objects.equals(cause, SimpleCache.RemovalCause.EXPIRED)) {
                            expiredCount.getAndIncrement();
                        } else if (Objects.equals(cause, SimpleCache.RemovalCause.REPLACED)) {
                            replaceCount.getAndIncrement();
                        } else if (Objects.equals(cause, SimpleCache.RemovalCause.EXPLICIT)) {
                            explicitCount.getAndIncrement();
                        }
                    });
            cache.put(1, 1);
            Thread.sleep(60);
            assertNull(cache.get(1));
            cache.put(1, 1);
            cache.expire(1, Duration.ofMillis(100));
            Thread.sleep(60);
            assertEquals(cache.get(1), 1);
            Thread.sleep(50);
            assertNull(cache.getVal(1));
            cache.put(1, 1);
            Thread.sleep(60);
            assertEquals(cache.get(1, k -> 2), 2);
            Thread.sleep(60);
            assertEquals(
                    cache.getVal(1, k -> SimpleCache.valueInfo(3, null)).get(),
                    3
            );
            Thread.sleep(60);
            assertNull(cache.getVal(1));
            assertEquals(
                    cache.getVal(1, k -> SimpleCache.valueInfo(3, Duration.ofMillis(100))).get(),
                    3
            );
            Thread.sleep(60);
            assertEquals(cache.get(1), 3);
            Thread.sleep(50);
            assertNull(cache.get(1));
            // count
            cache.put(2, 2);
            cache.put(2, 3);
            cache.put(2, SimpleCache.valueInfo(4, null));
            cache.remove(3);
            cache.put(3, 33);
            cache.clear();
            cache.clean();
            assertEquals(expiredCount.get(), 6);
            assertEquals(replaceCount.get(), 2);
            assertEquals(explicitCount.get(), 2);
        }
        // removal value
        {
            IntVar expiredCount = IntVar.of(0);
            IntVar replaceCount = IntVar.of(0);
            IntVar explicitCount = IntVar.of(0);
            SimpleCache.RemovalListener<Integer, Integer> listener =
                    (key, value, cause) -> {
                        if (Objects.equals(cause, SimpleCache.RemovalCause.EXPIRED)) {
                            expiredCount.getAndIncrement();
                            assertEquals(key, 1);
                            assertEquals(value.get(), 1);
                        } else if (Objects.equals(cause, SimpleCache.RemovalCause.REPLACED)) {
                            replaceCount.getAndIncrement();
                            assertEquals(key, 2);
                            assertEquals(value.get(), 2);
                        } else if (Objects.equals(cause, SimpleCache.RemovalCause.EXPLICIT)) {
                            explicitCount.getAndIncrement();
                            assertEquals(key, 3);
                            assertEquals(value.get(), 3);
                        }
                    };
            SimpleCache<Integer, Integer> cache = SimpleCache.soft(Duration.ofMillis(50), listener);
            cache.put(1, 1);
            Thread.sleep(60);
            cache.put(1, 1);
            cache.put(2, 2);
            cache.put(2, 3);
            cache.put(3, 3);
            cache.remove(3);
            cache.clean();
            assertEquals(expiredCount.get(), 1);
            assertEquals(replaceCount.get(), 1);
            assertEquals(explicitCount.get(), 1);
            replaceCount.set(0);
            cache = SimpleCache.soft(Duration.ofMillis(50), listener);
            cache.put(2, 2);
            cache.put(2, SimpleCache.valueInfo(3, null));
            cache.clean();
            assertEquals(replaceCount.get(), 1);
            explicitCount.set(0);
            cache = SimpleCache.soft(Duration.ofMillis(50), listener);
            cache.put(3, 3);
            cache.clear();
            cache.clean();
            assertEquals(explicitCount.get(), 1);
        }
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
        assertNull(cache.getVal(5));
        cache.put(5, 55);
        assertEquals(cache.get(5), 55);
        cache.remove(5);
        assertFalse(cache.contains(5));
        assertNull(cache.getVal(5));
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

    @Test
    public void testPhantom() throws Exception {
        testPhantom(SimpleCache.phantom(null));
        IntVar count = IntVar.of(0);
        SimpleCache<Integer, Integer> cache = SimpleCache.phantom(
                (key, value, cause) -> {
                    count.getAndIncrement();
                    assertEquals(cause, SimpleCache.RemovalCause.COLLECTED);
                });
        testPhantom(cache);
        cache.clear();
        cache.clean();
        assertEquals(count.get(), 5);
    }

    private void testPhantom(SimpleCache<Integer, Integer> cache) throws Exception {
        cache.put(1, 1);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));
        cache.put(1, SimpleCache.valueInfo(2, null));
        cache.expire(1, Duration.ofMillis(100));
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));
        cache.put(2, SimpleCache.valueInfo(2, null));
        assertNull(cache.get(2));
        cache.put(3, 3);
        cache.remove(3);
        assertNull(cache.get(3));
        assertNull(cache.getVal(3));
        cache.put(4, 4);
        cache.clear();
        assertNull(cache.get(4));
        assertNull(cache.getVal(4));
    }

    @Test
    public void testGc() throws Exception {
        int threadCount = 10;
        int loopCount = 100000;
        AtomicLong gcCounter = new AtomicLong(0);
        SimpleCache<Long, Long> weak = SimpleCache.weak(null, (k, v, c) -> {
            if (Objects.equals(c, SimpleCache.RemovalCause.COLLECTED)) {
                gcCounter.incrementAndGet();
            }
        });
        testGc(weak, gcCounter, threadCount, loopCount);
        gcCounter.set(0);
        SimpleCache<Long, Long> soft = SimpleCache.soft(null, (k, v, c) -> {
            if (Objects.equals(c, SimpleCache.RemovalCause.COLLECTED)) {
                gcCounter.incrementAndGet();
            }
        });
        testGc(soft, gcCounter, threadCount, loopCount);
    }

    private void testGc(
            SimpleCache<Long, Long> cache, AtomicLong gcCounter, int threadCount, int loopCount) throws Exception {
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int t = i;
            new Thread(() -> {
                for (int j = 0; j < loopCount; j++) {
                    long x = t * 100000000L + j;
                    cache.put(x, x);
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        cache.clean();
        System.out.println("gcCounter: " + gcCounter.get()
                + ", cache size: " + cache.size()
                + ", total: " + (cache.size() + gcCounter.get())
        );
        assertEquals(cache.size() + gcCounter.get(), (long) threadCount * loopCount);
    }

    @Test
    public void testMultiThread() throws Exception {
        int threadCount = 50;
        int loopCount = 100000;
        testMultiThread(SimpleCache.weak(), threadCount, loopCount);
        testMultiThread(SimpleCache.soft(), threadCount, loopCount);
        testMultiThread(SimpleCache.phantom(null), threadCount, loopCount);
    }

    private void testMultiThread(SimpleCache<Long, Long> cache, int threadCount, int loopCount) throws Exception {
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < loopCount; j++) {
                    long next = j;
                    long v = cache.get(next, k -> next);
                    assertEquals(v, next);
                }
                latch.countDown();
            }).start();
        }
        latch.await();
    }
}
