package tests.benchmarks;

import internal.api.CacheApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheTest {

    @Test
    public void testCacheWithDifferentImplementations() throws Exception {
        testCacheImplementation("fs-simpleWeak");
        testCacheImplementation("fs-simpleSoft");
        testCacheImplementation("caffeineWeak");
        testCacheImplementation("caffeineSoft");
        testCacheImplementation("caffeine");
        testCacheImplementation("guavaWeak");
        testCacheImplementation("guavaSoft");
        testCacheImplementation("guava");
        testCacheImplementation("concurrentHashMap");
    }

    private void testCacheImplementation(String cacheType) throws Exception {
        CacheApi<String, String> cache = CacheApi.createApi(cacheType);
        assertEquals("1", cache.get("1", k -> k));
    }
}
