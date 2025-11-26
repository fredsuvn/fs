package tests.benchmarks;

import internal.tests.api.CacheApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheTest {

    @Test
    public void testCache() throws Exception {
        testCache("simpleWeak");
        testCache("simpleSoft");
        testCache("caffeineWeak");
        testCache("caffeineSoft");
        testCache("caffeine");
        testCache("guavaWeak");
        testCache("guavaSoft");
        testCache("guava");
        testCache("concurrentHashMap");
    }

    private void testCache(String cacheType) throws Exception {
        assertEquals("1", CacheApi.createCache(cacheType).get("1", k -> k));
    }
}
