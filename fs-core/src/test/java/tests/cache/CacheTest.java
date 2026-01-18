package tests.cache;

import internal.test.DataTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.value.BooleanVar;
import space.sunqian.fs.base.value.Val;
import space.sunqian.fs.cache.AbstractSimpleCache;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.MapKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        assertEquals(
            MapKit.map(1, 1),
            cache.copyEntries()
        );
        assertEquals(1, cache.get(1));
        assertEquals(1, cache.getVal(1).get());
        cache.put(1, 2);
        assertEquals(
            MapKit.map(1, 2),
            cache.copyEntries()
        );
        assertEquals(2, cache.get(1));
        assertEquals(2, cache.getVal(1).get());
        assertNull(cache.getVal(2));
        cache.put(1, null);
        assertNull(cache.get(1));
        assertNotNull(cache.getVal(1));
        assertNull(cache.getVal(1).get());
        assertEquals(1, cache.size());
        assertEquals(
            MapKit.map(1, null),
            cache.copyEntries()
        );

        // copyEntries
        assertNotSame(
            cache.copyEntries(),
            cache.copyEntries()
        );

        // remove
        cache.remove(0);
        assertNull(cache.get(0));
        assertNull(cache.getVal(0));
        cache.remove(1);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));
        assertEquals(0, cache.size());
        assertEquals(
            MapKit.map(),
            cache.copyEntries()
        );

        // producer
        assertNull(cache.getVal(2));
        assertEquals(2, cache.get(2, k -> 2));
        assertEquals(2, cache.get(2));
        assertEquals(2, cache.get(2, k -> 3));
        assertNull(cache.getVal(3));
        assertEquals(3, cache.getVal(3, k -> Val.of(3)).get());
        assertEquals(3, cache.get(3));
        assertEquals(3, cache.getVal(3, k -> Val.of(4)).get());
        assertNull(cache.getVal(4, k -> null));
        assertNull(cache.getVal(4));
        assertEquals(2, cache.size());
        assertEquals(
            MapKit.map(2, 2, 3, 3),
            cache.copyEntries()
        );

        // clear
        assertNotNull(cache.getVal(2));
        cache.clear();
        assertNull(cache.getVal(1));
        assertNull(cache.getVal(2));
        assertNull(cache.getVal(3));
        assertNull(cache.getVal(4));
        assertEquals(0, cache.size());
        assertEquals(
            MapKit.map(),
            cache.copyEntries()
        );
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
        assertSame(hello, xc.get(1, k -> 2));
        assertSame(hello, xc.getVal(1, k -> Val.of(2)).get());
    }

    @Test
    public void testPhantom() {
        // get/put
        SimpleCache<Integer, Integer> cache = SimpleCache.ofPhantom();
        cache.put(1, 1);
        assertNull(cache.get(1));
        assertNull(cache.getVal(1));
        assertEquals(
            MapKit.map(),
            cache.copyEntries()
        );

        // producer
        assertNull(cache.getVal(2));
        assertEquals(2, cache.get(2, k -> 2));
        assertEquals(3, cache.get(2, k -> 3));
        assertNull(cache.get(2));
        assertNull(cache.getVal(2));
        assertNull(cache.getVal(3));
        assertEquals(2, cache.getVal(3, k -> Val.of(2)).get());
        assertEquals(3, cache.getVal(3, k -> Val.of(3)).get());
        assertNull(cache.get(3));
        assertNull(cache.getVal(3));
        assertEquals(
            MapKit.map(),
            cache.copyEntries()
        );

        // clean
        cache.put(1, 1);
        cache.remove(1);
        cache.clean();
        assertEquals(
            MapKit.map(),
            cache.copyEntries()
        );
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
        assertEquals(6, cache.get(6, k -> 6));
        assertNull(cache.get(7, k -> null));
        assertEquals(8, cache.getVal(8, k -> Val.of(8)).get());
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
