package tests.cache;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.cache.CacheFunction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheFunctionTest {

    @Test
    public void testCacheFunction() throws Exception {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        CacheFunction<Integer, Integer> cacheFunction = CacheFunction.ofMap(map);
        assertEquals(1, cacheFunction.get(1, k -> k));
        assertEquals(3, cacheFunction.get(3, k -> k));
        assertEquals(2, map.size());
        assertEquals(3, map.get(3));
    }
}
