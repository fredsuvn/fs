# CreateSamples Skill

## Overview

The CreateSamples skill is designed to create and maintain examples for using the fs library. This skill helps users understand how to effectively use the fs library by providing clear, concise, and well-structured sample code.

## Key Modules

The fs library consists of several core modules:

1. **fs-core**: The main library with comprehensive utilities and interfaces, including almost all tool classes and interfaces of fs.
2. **fs-annotation**: Contains almost all auxiliary annotations of fs for code analysis and validation.
3. **fs-asm**: Built-in ASM framework for bytecode manipulation, only used for bytecode programming, and already encapsulated by the ASM implementation in fs-core, so it is rarely used directly.

## Process

### 1. Study the Documentation

Before creating samples, carefully read the documentation and comments of the above modules to understand the basic usage of fs. This includes:

- Javadoc comments in the source code, especially the comments of package-infos;
- Interface comments and structures;
- Any other relevant documentation and codes in the project.

### 2. Sample Location

Sample code should be placed in the source set: `fs-tests/src/samples`. This source set already contains some existing samples that need to be refactored, supplemented, and improved.

### 3. Refactor and Enhance Existing Samples

Review the existing sample files and:

- Improve code readability and documentation
- Ensure samples cover the most common use cases
- Fix any outdated or incorrect code
- Add missing examples for important features

### 4. Add New Samples

Add new sample files as needed to demonstrate:

- Core functionality of fs-core
- Usage of fs-annotation
- Advanced features and best practices

### 5. Update Documentation Links

Ensure the samples are referenced in the "Documents and Samples" section of the project's README.md file. This section can show snippets of sample code, with links to more detailed code in the repository.

## Sample Categories

Samples should cover the following categories:

1. **Base Utilities** (bytes, chars, date, string, system, etc.)
2. **Collections & Data** (arrays, lists, maps, JSON, properties)
3. **I/O Operations** (file operations, byte/char processing)
4. **Networking** (HTTP, TCP, UDP)
5. **Object Manipulation** (conversion, copying, schema)
6. **Dynamic Programming** (proxy, aspect, reflection)
7. **Dependency Injection**
8. **Utilities** (codec, event bus, JDBC)
9. **Annotations** (null safety, immutability, etc.)
10. **Caching**

## Best Practices

- Keep samples simple and flexible
- Include clear comments explaining the code
- Provide both basic and advanced usage examples
- Ensure samples are well-tested and functional
- Each sample class only mainly demonstrates usages and features for one class/interface/package (or related classes/interfaces).

## Output Format

When creating or updating samples, ensure they follow this structure:

1. Package declaration
2. Imports
3. Class definition with descriptive name
4. Well-commented methods demonstrating specific features
5. Clear examples of usage patterns

## Linking with Documentation

The README.md's "Documents and Samples" section should include:

- Brief descriptions of each sample
- Links to the sample files
- Code snippets for key examples with explanations

## Sample Code Templates

When creating a new sample, follow this structure to ensure consistency and clarity.

### Step 1: Define the Sample Purpose

At the beginning of each sample file, include a comment block that clearly states:

- What this sample demonstrates
- Why this feature is useful
- Key use cases

```java
/**
 * Sample: SimpleCache Usage
 * <p>
 * Purpose: Demonstrate how to quickly create a cache for frequently accessed data.
 * <p>
 * Use Cases:
 * - Cache computation results to avoid redundant calculations
 * - Store frequently accessed data to improve performance
 * - Reduce database or network calls by caching results
 */
```

### Step 2: Provide Multiple Implementation Variations

Show different ways to create/use the feature, from simple to complex:

#### Variation 1: Quickest and Simplest

```java
// Create a simple soft-reference cache with default settings
// Best for: General-purpose caching with automatic memory management
SimpleCache<String, String> cache = SimpleCache.ofSoft();
String value = cache.get("key", k -> "computed value");
```

#### Variation 2: With Specific Reference Type

```java
// Use strong reference when you need guaranteed retention
// Best for: Small, important data that should not be garbage collected
SimpleCache<String, byte[]> strongCache = SimpleCache.ofStrong();

// Use weak reference for cache entries that should not prevent GC
// Best for: Caching that should not cause memory leaks
SimpleCache<Object, Object> weakCache = SimpleCache.ofWeak();
```

#### Variation 3: With Complex Settings

```java
// Create a cache with custom expiration and size limits
// Best for: Fine-tuned cache behavior for specific scenarios
SimpleCache<String, String> customCache = SimpleCache.<String, String>builder()
    .referenceType(ReferenceType.SOFT)
    .maximumSize(1000)
    .expirationTime(3600_000) // 1 hour in milliseconds
    .removalListener((key, value, cause) -> {
        System.out.println("Removed: " + key + " due to " + cause);
    })
    .build();
```

### Step 3: README Documentation Format

When updating the README.md "Documents and Samples" section, add short snippets of **Step 2** to the README.md section, and add links to the complete sample files like:

```markdown
[Complete CacheSample](./fs-tests/src/samples/java/internal/samples/CacheSample.java)
```

## Example: Complete Sample Creation Flow

### 1. Create the Sample File

**File:** `fs-tests/src/samples/java/internal/samples/CacheSample.java`

```java
package internal.samples;

import space.sunqian.fs.cache.SimpleCache;
import java.lang.ref.ReferenceType;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample: SimpleCache Usage
 *
 * Purpose: Demonstrate how to quickly create a cache for frequently accessed data.
 *
 * Use Cases:
 * - Cache computation results to avoid redundant calculations
 * - Store frequently accessed data to improve performance
 * - Reduce database or network calls by caching results
 *
 * Key Classes:
 * - SimpleCache: The main cache interface
 * - ReferenceType: Determines how cache entries are stored (soft, weak, strong)
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

### 2. Update README.md

Add to the "Documents and Samples" section:

```markdown
### SimpleCache

Quickly create a cache for frequently accessed data.

**Basic usage with soft reference (auto memory management):**
```java
SimpleCache<String, String> cache = SimpleCache.ofSoft();
String value = cache.get("key", k -> "computed value");
```

**Using strong or weak references:**
```java
SimpleCache<String, byte[]> strongCache = SimpleCache.ofStrong();
SimpleCache<Object, Object> weakCache = SimpleCache.ofWeak();
```

**With custom size limits and expiration:**
```java
SimpleCache<String, String> cache = SimpleCache.<String, String>builder()
    .maximumSize(1000)
    .expirationTime(3600_000)
    .build();
```

[Complete CacheSample](./fs-tests/src/samples/java/internal/samples/CacheSample.java)
```

## Conclusion

The CreateSamples skill aims to provide high-quality, well-documented examples that demonstrate the power and versatility of the fs library. By following this guide, you can create samples that help users quickly get started with fs and make the most of its features.