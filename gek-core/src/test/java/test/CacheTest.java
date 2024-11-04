//package test;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.sunqian.common.base.JieLog;
//import xyz.sunqian.common.cache.Cache;
//import xyz.sunqian.common.ref.Var;
//import xyz.sunqian.common.ref.IntVar;
//
//import java.time.Duration;
//import java.util.HashSet;
//import java.util.Objects;
//import java.util.Set;
//
//public class CacheTest {
//
//    @Test
//    public void testCache() {
//        final int[] detected = {0};
//        final long[] collected = {0};
//        testCache(Cache.softCache((k, v, c) -> {
//            detected[0]++;
//            if (Objects.equals(c, Cache.RemovalListener.Cause.COLLECTED)) {
//                collected[0]++;
//            }
//        }), detected, "soft-cache");
//        testCache(Cache.weakCache((k, v, c) -> {
//            detected[0]++;
//            if (Objects.equals(c, Cache.RemovalListener.Cause.COLLECTED)) {
//                collected[0]++;
//            }
//        }), detected, "weak-cache");
//        JieLog.of().info("Collected: ", collected[0]);
//    }
//
//    private void testCache(Cache<Integer, String> cache, int[] detected, String name) {
//        detected[0] = 0;
//        //Map<Integer, String> map = new HashMap<>();
//        int times = 1000 * 3;
//        String value = TestUtil.buildRandomString(1024 * 100, 1024 * 100);
//        for (int i = 0; i < times; i++) {
//            cache.put(i, value + "i");
//            //map.put(i, String.valueOf(i * 10086));
//        }
//        int removed = 0;
//        for (int i = 0; i < times; i++) {
//            String fsValue = cache.get(i);
//            if (fsValue == null) {
//                removed++;
//            } else {
//                //Assert.assertEquals(map.get(i), fsValue);
//            }
//        }
//        JieLog.of().info(name + "---> total: " + times +
//            ", removed: " + removed +
//            ", detected: " + detected[0] +
//            ", cached: " + (times - removed) +
//            ", size: " + cache.size()
//        );
//        //Assert.assertEquals(removed, detected[0]);
//    }
//
//    @Test
//    public void testCacheLoader() {
//        testCacheLoader(Cache.softCache());
//    }
//
//    private void testCacheLoader(Cache<Integer, String> cache) {
//        Assert.assertNull(cache.get(1));
//        Assert.assertEquals(cache.compute(1, String::valueOf), "1");
//        Assert.assertEquals(cache.compute(1, String::valueOf), "1");
//        JieLog.of().info("cacheLoader: 1=", cache.get(1));
//        cache.compute(1, k -> null);
//        Assert.assertEquals(cache.get(1), "1");
//        cache.remove(1);
//        cache.compute(1, k -> null);
//        Assert.assertNull(cache.get(1));
////        Assert.assertEquals(cache.getWrapper(1).get(), GekObject.empty().get());
//        cache.remove(1);
////        cache.getWrapper(1, k -> null);
//        Assert.assertNull(cache.get(1));
////        Assert.assertNull(cache.getWrapper(1));
//        cache.put(2, "2");
//        Assert.assertEquals(cache.get(2), "2");
//        Assert.assertEquals(cache.compute(2, k -> "4"), "2");
////        Assert.assertEquals(cache.getWrapper(2, k -> Cache.Value.of("8", null, null)).get(), "2");
//    }
//
//    @Test
//    public void testCleanUp() {
//        Cache<Integer, Integer> cache = Cache.softCache((k, v, c) -> {
//        });
//        for (int i = 0; i < 10000; i++) {
//            cache.put(i, i);
//        }
//        cache.cleanUp();
//    }
//
//    @Test
//    public void testRemove() {
//        IntVar intVar = Var.ofInt(0);
//        Cache<Integer, Integer> cache = Cache.softCache((k, v, c) -> intVar.incrementAndGet());
//        cache.put(1, 1);
//        cache.remove(1);
//        Assert.assertEquals(intVar.get(), 1);
//    }
//
//    @Test
//    public void testClear() {
//        IntVar intVar = Var.ofInt(0);
//        Cache<Integer, Integer> cache = Cache.softCache((k, v, c) -> intVar.incrementAndGet());
//        for (int i = 0; i < 10000; i++) {
//            cache.put(i, i);
//        }
//        cache.clear();
//        Assert.assertEquals(intVar.get(), 10000);
//    }
//
//    @Test
//    public void testRemoveIf() {
//        IntVar intVar = Var.ofInt(0);
//        Cache<Integer, Integer> cache = Cache.softCache((k, v, c) -> intVar.incrementAndGet());
//        for (int i = 0; i < 10; i++) {
//            cache.put(i, i);
//        }
//        cache.removeIf((k, v) -> k > 5);
//        Assert.assertEquals(intVar.get(), 4);
//        Assert.assertEquals(cache.size(), 6);
//    }
//
//    @Test
//    public void testNull() {
//        IntVar intVar = Var.ofInt(0);
//        Set<Integer> set = new HashSet<>();
//        Cache<Integer, Integer> cache = Cache.softCache((k, v, c) -> {
//            intVar.incrementAndGet();
//            set.remove(k);
//        });
//        for (int i = 0; i < 10000; i++) {
//            cache.put(i, (Integer) null);
//        }
//        for (int i = 0; i < 10000; i++) {
////            GekObject<Integer> w = cache.getWrapper(i);
////            if (w != null) {
////                Assert.assertNull(w.get());
////                set.add(i);
////            }
//        }
//        Assert.assertEquals(set.size() + intVar.get(), 10000);
//        cache.clear();
//        Assert.assertEquals(intVar.get(), 10000);
//    }
//
//    @Test
//    public void testExpiration() throws InterruptedException {
//        int[] l = {0};
//        Cache.RemovalListener.Cause[] cause = {null};
//        Cache<Integer, Integer> cache = Cache.softCache((k, v, c) -> {
//            l[0]++;
//            cause[0] = c;
//        });
//        cache.put(1, 1, 1000);
//        Assert.assertEquals(cache.get(1), 1);
//        Assert.assertEquals(l[0], 0);
//        Thread.sleep(1001);
//        Assert.assertNull(cache.get(1));
//        Assert.assertEquals(l[0], 1);
//        Assert.assertEquals(cause[0], Cache.RemovalListener.Cause.EXPIRED);
//        cache.put(1, 1, 1000);
//        cache.put(1, 1, 1000);
//        Assert.assertEquals(cause[0], Cache.RemovalListener.Cause.REPLACED);
//        cache.remove(1);
//        Assert.assertEquals(cause[0], Cache.RemovalListener.Cause.EXPLICIT);
//
//        l[0] = 1;
//        cache.put(2, 2, 1000);
//        cache.expire(2, 1500);
//        Thread.sleep(1111);
//        Assert.assertEquals(cache.get(2), 2);
//        Assert.assertEquals(l[0], 1);
//        Thread.sleep(1000);
//        Assert.assertNull(cache.get(2));
//        Assert.assertEquals(l[0], 2);
//        cache.put(3, Cache.Value.of(3, 1000, 1000));
//        Thread.sleep(500);
//        Assert.assertEquals(cache.get(3), 3);
//        Thread.sleep(500);
//        Assert.assertEquals(cache.get(3), 3);
//        Thread.sleep(1001);
//        Assert.assertNull(cache.get(3));
//
//        Cache<Integer, Integer> cache2 = Cache.newBuilder().expireAfterWrite(Duration.ofMillis(1000)).build();
//        cache2.put(1, 1);
//        Assert.assertEquals(cache2.get(1), 1);
//        Thread.sleep(1001);
//        Assert.assertNull(cache2.get(1));
//        cache2.put(2, 2);
//        cache2.expire(2, 1500);
//        Assert.assertEquals(cache2.get(2), 2);
//        Thread.sleep(1001);
//        Assert.assertEquals(cache2.get(2), 2);
//        Thread.sleep(501);
//        Assert.assertNull(cache2.get(2));
//    }
//}
