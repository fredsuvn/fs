package tests.cache;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.BooleanVar;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.cache.AbstractSimpleCache;
import xyz.sunqian.common.cache.SimpleCache;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.PrintTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

public class CacheTest implements PrintTest, DataTest {

    private final char[] chars = randomChars(8 * 1024, 'a', 'z');

    @Test
    public void testNoGc() throws Exception {
        testCache(SimpleCache.ofStrong());
    }

    private void testCache(SimpleCache<Integer, Integer> cache) throws Exception {
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
        assertEquals(cache.size(), 1);

        // remove
        cache.remove(0);
        assertNull(cache.get(0));
        assertNull(cache.getVal(0));
        cache.remove(1);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));
        assertEquals(cache.size(), 0);

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
        assertEquals(cache.size(), 2);

        // clear
        assertNotNull(cache.getVal(2));
        cache.clear();
        assertNull(cache.getVal(1));
        assertNull(cache.getVal(2));
        assertNull(cache.getVal(3));
        assertNull(cache.getVal(4));
        assertEquals(cache.size(), 0);
    }

    @Test
    public void testAbsImpl() throws Exception {
        BooleanVar bv = BooleanVar.of(false);
        String hello = "hello";
        class XCache extends AbstractSimpleCache<Object, Object> {

            @Override
            public void clean() {
            }

            @Override
            protected @Nonnull Value<Object> generate(@Nonnull Object key, @Nonnull Object value) {
                return new Value<Object>() {
                    @Override
                    public @Nonnull Object key() {
                        return key;
                    }

                    @Override
                    public @Nullable Object refValue() {
                        if (bv.getAndToggle()) {
                            return hello;
                        }
                        return null;
                    }

                    @Override
                    public void invalid() {
                    }
                };
            }
        }
        XCache xc = new XCache();
        xc.put(1, 1);
        assertSame(xc.get(1, k -> 2), hello);
        assertSame(xc.getVal(1, k -> Val.of(2)).get(), hello);
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
        testWeakAndSoft(SimpleCache.ofWeak());
        testWeakAndSoft(SimpleCache.ofSoft());
    }

    private void testWeakAndSoft(SimpleCache<Integer, Integer> cache) {
        cache.put(1, 1);
        cache.get(1);
        cache.remove(1);
        cache.clean();
        assertEquals(cache.get(6, k -> 6), 6);
        assertNull(cache.get(7, k -> null));
        assertEquals(cache.getVal(8, k -> Val.of(8)).get(), 8);
        assertNull(cache.getVal(9, k -> null));
    }

    // Test for out of memory.
    //@Test
    public void testMemory() throws Exception {
        testMemory(SimpleCache.ofWeak(), 1000000);
        testMemory(SimpleCache.ofSoft(), 1000000);
        testMemory(SimpleCache.ofPhantom(), 1000000);
        // testMemory(SimpleCache.ofStrong(), 1000000);
    }

    private void testMemory(SimpleCache<Long, String> cache, long valueCount) throws Exception {
        for (long i = 0; i < valueCount; i++) {
            cache.put(i, new String(chars));
        }
    }

    @Test
    public void testThreads() throws Exception {
        testThreads(SimpleCache.ofWeak(), 10);
        testThreads(SimpleCache.ofSoft(), 10);
        testThreads(SimpleCache.ofPhantom(), 10);
        testThreads(SimpleCache.ofStrong(), 10);
        testThreads(SimpleCache.ofWeak(), 20);
        testThreads(SimpleCache.ofSoft(), 20);
        testThreads(SimpleCache.ofPhantom(), 20);
        testThreads(SimpleCache.ofStrong(), 20);
    }

    private void testThreads(SimpleCache<Integer, Integer> cache, int threadNum) throws Exception {
        CountDownLatch latch = new CountDownLatch(threadNum);
        List<Thread> threads = new ArrayList<>(threadNum);
        AtomicInteger[] counters = new AtomicInteger[threadNum];
        for (int i = 0; i < counters.length; i++) {
            counters[i] = new AtomicInteger();
        }
        for (int i = 0; i < threadNum; i++) {
            int value = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    int actualValue = cache.get(j, k -> value);
                    counters[actualValue].incrementAndGet();
                }
                latch.countDown();
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        latch.await();
        printFor("Cache threads[" + threadNum + "] counters",
            Arrays.stream(counters).map(a -> String.valueOf(a.get())).collect(Collectors.joining(", ")));
    }
}
