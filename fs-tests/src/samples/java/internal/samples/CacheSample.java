package internal.samples;

import space.sunqian.fs.cache.SimpleCache;

public class CacheSample {

    public static void main(String[] args) {
        SimpleCache<String, String> cache = SimpleCache.ofWeak();
        System.out.println(
            cache.get("world", k -> "hello " + k + "!")
        );
    }
}
