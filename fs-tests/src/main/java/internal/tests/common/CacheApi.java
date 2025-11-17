package internal.tests.common;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import space.sunqian.common.cache.SimpleCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface CacheApi<K, V> {

    int CAPACITY = 1000;

    static <K, V> CacheApi<K, V> createCache(String cacheType) {
        return switch (cacheType) {
            case "simpleWeak" -> {
                SimpleCache<K, V> cache = SimpleCache.ofWeak();
                yield cache::get;
            }
            case "simpleSoft" -> {
                SimpleCache<K, V> cache = SimpleCache.ofSoft();
                yield cache::get;
            }
            case "caffeineWeak" -> {
                Cache<K, V> cache = Caffeine.newBuilder().weakValues().build();
                yield cache::get;
            }
            case "caffeineSoft" -> {
                Cache<K, V> cache = Caffeine.newBuilder().softValues().build();
                yield cache::get;
            }
            case "caffeine" -> {
                Cache<K, V> cache = Caffeine.newBuilder().maximumSize(CAPACITY).build();
                yield cache::get;
            }
            case "guavaWeak" -> {
                com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder().weakValues().build();
                yield (key, function) -> cache.get(key, () -> function.apply(key));
            }
            case "guavaSoft" -> {
                com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder().softValues().build();
                yield (key, function) -> cache.get(key, () -> function.apply(key));
            }
            case "guava" -> {
                com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder().maximumSize(CAPACITY).build();
                yield (key, function) -> cache.get(key, () -> function.apply(key));
            }
            case "concurrentHashMap" -> {
                ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
                yield cache::computeIfAbsent;
            }
            default -> throw new IllegalArgumentException();
        };
    }

    V get(K key, Function<? super K, ? extends V> function) throws Exception;
}
