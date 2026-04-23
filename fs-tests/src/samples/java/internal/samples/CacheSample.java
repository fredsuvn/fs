package internal.samples;

import space.sunqian.fs.cache.SimpleCache;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample: SimpleCache Usage
 * <p>
 * Purpose: Demonstrate how to quickly create a cache for frequently accessed data.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Cache computation results to avoid redundant calculations
 *   </li>
 *   <li>
 *     Store frequently accessed data to improve performance
 *   </li>
 *   <li>
 *     Reduce database or network calls by caching results
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link SimpleCache}: The main cache interface
 *   </li>
 * </ul>
 */
public class CacheSample {

    public static void main(String[] args) {
        demonstrateBasicUsage();
        demonstrateReferenceTypes();
        demonstrateCacheOperations();
    }

    /**
     * Demonstrates the simplest way to create a cache. Uses soft references for automatic memory management.
     */
    public static void demonstrateBasicUsage() {
        System.out.println("=== Basic Cache Usage ===");
        SimpleCache<String, String> cache = SimpleCache.ofSoft();
        String value = cache.get("key", k -> "computed " + k);
        System.out.println("Cached value: " + value);
    }

    /**
     * Demonstrates different reference types for cache entries.
     */
    public static void demonstrateReferenceTypes() {
        System.out.println("\n=== Different Reference Types ===");
        // Soft reference - garbage collected under memory pressure
        SimpleCache<String, byte[]> softCache = SimpleCache.ofSoft();
        softCache.put("soft-key", new byte[1024]);
        System.out.println("Soft cache size: " + softCache.size());

        // Weak reference - garbage collected when not strongly referenced elsewhere
        SimpleCache<Object, Object> weakCache = SimpleCache.ofWeak();
        Object weakKey = new Object();
        weakCache.put(weakKey, "weak-value");
        System.out.println("Weak cache size: " + weakCache.size());

        // Strong reference - never garbage collected unless explicitly removed
        SimpleCache<String, String> strongCache = SimpleCache.ofStrong();
        strongCache.put("strong-key", "strong-value");
        System.out.println("Strong cache size: " + strongCache.size());

        // Phantom reference - garbage collected when phantom reachable
        SimpleCache<String, String> phantomCache = SimpleCache.ofPhantom();
        phantomCache.put("phantom-key", "phantom-value");
        System.out.println("Phantom cache size: " + phantomCache.size());
    }

    /**
     * Demonstrates basic cache operations including put, get, remove, and clear.
     */
    public static void demonstrateCacheOperations() {
        System.out.println("\n=== Cache Operations ===");
        SimpleCache<String, String> cache = SimpleCache.ofStrong();
        AtomicInteger computeCount = new AtomicInteger(0);

        // Put operation
        cache.put("key1", "value1");
        System.out.println("After put - Size: " + cache.size());

        // Get operation with loader
        String value2 = cache.get("key2", k -> {
            computeCount.incrementAndGet();
            return "computed-" + k;
        });
        System.out.println("Get with loader - Value: " + value2 + ", Compute count: " + computeCount.get());

        // Get operation from cache
        String value2Again = cache.get("key2", k -> {
            computeCount.incrementAndGet();
            return "computed-" + k;
        });
        System.out.println("Get from cache - Value: " + value2Again + ", Compute count: " + computeCount.get());

        // Remove operation
        cache.remove("key1");
        System.out.println("After remove - Size: " + cache.size());

        // Clear operation
        cache.clear();
        System.out.println("After clear - Size: " + cache.size());
    }

    /**
     * Demonstrates cache with custom map implementation.
     */
    public static void demonstrateCustomMapCache() {
        System.out.println("\n=== Custom Map Cache ===");
        // Create cache with custom map
        java.util.LinkedHashMap<String, String> customMap = new java.util.LinkedHashMap<>();
        SimpleCache<String, String> mapCache = SimpleCache.ofMap(customMap);

        mapCache.put("key1", "value1");
        mapCache.put("key2", "value2");

        System.out.println("Map cache size: " + mapCache.size());
        System.out.println("Cache entries: " + mapCache.copyEntries());
    }
}
