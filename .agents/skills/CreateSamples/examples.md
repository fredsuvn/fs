# CreateSamples Skill - Examples

This document provides complete examples demonstrating the sample creation flow for the fs library.

## Example 1: README Documentation Format

When updating the README.md "Documents and Samples" section, add short snippets like the following:

```markdown
### SimpleCache

Quickly create a cache for frequently accessed data.

**Basic usage with soft reference (auto memory management):**
SimpleCache<String, String> cache = SimpleCache.ofSoft();
String value = cache.get("key", k -> "computed value");

**Using strong or weak references:**
SimpleCache<String, byte[]> strongCache = SimpleCache.ofStrong();
SimpleCache<Object, Object> weakCache = SimpleCache.ofWeak();
```

Then add links to the complete sample files:

```markdown
**More samples:**
[Complete CacheSample](./fs-tests/src/samples/java/internal/samples/CacheSample.java)
```

## Example 2: Complete Sample File - CacheSample.java

```java
package internal.samples;

import space.sunqian.fs.cache.SimpleCache;
import java.lang.ref.ReferenceType;
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

    /**
     * Demonstrates the simplest way to create a cache.
     * Uses soft references for automatic memory management.
     */
    public static void demonstrateBasicUsage() {
        SimpleCache<String, String> cache = SimpleCache.ofSoft();
        String value = cache.get("key", k -> "computed " + k);
        System.out.println("Cached value: " + value);
    }

    /**
     * Demonstrates different reference types for cache entries.
     */
    public static void demonstrateReferenceTypes() {
        // Soft reference - garbage collected under memory pressure
        SimpleCache<String, byte[]> softCache = SimpleCache.ofSoft();

        // Weak reference - garbage collected when not strongly referenced elsewhere
        SimpleCache<Object, Object> weakCache = SimpleCache.ofWeak();

        // Strong reference - never garbage collected unless explicitly removed
        SimpleCache<String, String> strongCache = SimpleCache.ofStrong();
    }

    /**
     * Demonstrates cache with complex settings including
     * custom size limits, expiration, and removal listener.
     */
    public static void demonstrateAdvancedSettings() {
        AtomicInteger removeCount = new AtomicInteger(0);

        SimpleCache<String, String> customCache = SimpleCache.<String, String>builder()
            .referenceType(ReferenceType.SOFT)
            .maximumSize(1000)
            .expirationTime(3600_000) // 1 hour
            .removalListener((key, value, cause) -> {
                removeCount.incrementAndGet();
                System.out.println("Removed: " + key + " due to " + cause);
            })
            .build();

        // Usage
        String result = customCache.get("data", k -> expensiveComputation(k));
        System.out.println("Result: " + result + ", Removed count: " + removeCount.get());
    }

    private static String expensiveComputation(String input) {
        // Simulate expensive operation
        return "computed-" + input;
    }
}
```

## Note: This is only an example about the common concept of cache

Note that this example is just by using the common concept of cache, it does not mean that the cache in the fs library is designed like this!