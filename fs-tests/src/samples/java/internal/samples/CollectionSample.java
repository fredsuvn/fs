package internal.samples;

import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.collect.SetKit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sample: Collection Utilities Usage
 * <p>
 * Purpose: Demonstrate how to use the collection utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Array operations
 *   </li>
 *   <li>
 *     List operations
 *   </li>
 *   <li>
 *     Map operations
 *   </li>
 *   <li>
 *     Set operations
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ArrayKit}: Array operation utilities
 *   </li>
 *   <li>
 *     {@link ListKit}: List operation utilities
 *   </li>
 *   <li>
 *     {@link MapKit}: Map operation utilities
 *   </li>
 *   <li>
 *     {@link SetKit}: Set operation utilities
 *   </li>
 * </ul>
 */
public class CollectionSample {

    public static void main(String[] args) {
        demonstrateArrayOperations();
        demonstrateListOperations();
        demonstrateMapOperations();
        demonstrateSetOperations();
    }

    /**
     * Demonstrates array operations.
     */
    public static void demonstrateArrayOperations() {
        System.out.println("=== Array Operations ===");

        // Create array
        String[] array = {"a", "b", "c", "d"};
        System.out.println("Original array: " + Arrays.toString(array));

        // Check if array is empty
        System.out.println("Is empty: " + ArrayKit.isEmpty(array));

        // Get array length
        System.out.println("Length: " + array.length);

        // Check if array contains element
        boolean containsB = false;
        for (String element : array) {
            if ("b".equals(element)) {
                containsB = true;
                break;
            }
        }
        System.out.println("Contains 'b': " + containsB);

        // Find index of element
        System.out.println("Index of 'c': " + ArrayKit.indexOf(array, "c"));
    }

    /**
     * Demonstrates list operations.
     */
    public static void demonstrateListOperations() {
        System.out.println("\n=== List Operations ===");

        // Create list
        List<String> list = ListKit.list("a", "b", "c", "d");
        System.out.println("Original list: " + list);

        // Check if list is empty
        System.out.println("Is empty: " + list.isEmpty());

        // Get list size
        System.out.println("Size: " + list.size());

        // Check if list contains element
        System.out.println("Contains 'b': " + list.contains("b"));

        // Find index of element
        System.out.println("Index of 'c': " + list.indexOf("c"));

        // Create immutable list
        List<String> immutableList = ListKit.list("a", "b", "c", "d");
        System.out.println("Immutable list: " + immutableList);
    }

    /**
     * Demonstrates map operations.
     */
    public static void demonstrateMapOperations() {
        System.out.println("\n=== Map Operations ===");

        // Create map
        Map<String, Integer> map = MapKit.map(
            "a", 1,
            "b", 2,
            "c", 3
        );
        System.out.println("Original map: " + map);

        // Check if map is empty
        System.out.println("Is empty: " + MapKit.isEmpty(map));

        // Get map size
        System.out.println("Size: " + map.size());

        // Check if map contains key
        System.out.println("Contains key 'b': " + map.containsKey("b"));

        // Check if map contains value
        System.out.println("Contains value 3: " + map.containsValue(3));

        // Get value by key
        System.out.println("Value for 'a': " + map.get("a"));
    }

    /**
     * Demonstrates set operations.
     */
    public static void demonstrateSetOperations() {
        System.out.println("\n=== Set Operations ===");

        // Create set
        Set<String> set = SetKit.set("a", "b", "c", "d");
        System.out.println("Original set: " + set);

        // Check if set is empty
        System.out.println("Is empty: " + set.isEmpty());

        // Get set size
        System.out.println("Size: " + set.size());

        // Check if set contains element
        System.out.println("Contains 'b': " + set.contains("b"));
    }
}