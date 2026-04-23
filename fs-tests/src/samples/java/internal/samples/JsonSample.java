package internal.samples;

import space.sunqian.fs.data.json.JsonKit;

/**
 * Sample: JSON Processing Usage
 * <p>
 * Purpose: Demonstrate how to use the JSON utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     JSON parsing from string
 *   </li>
 *   <li>
 *     JSON formatting to string
 *   </li>
 *   <li>
 *     JSON processing with nested structures
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link JsonKit}: JSON parsing and formatting utilities
 *   </li>
 * </ul>
 */
public class JsonSample {

    public static void main(String[] args) {
        demonstrateJsonProcessing();
    }

    /**
     * Demonstrates JSON parsing and formatting with various scenarios.
     */
    public static void demonstrateJsonProcessing() {
        System.out.println("=== JSON Processing ===");

        // Sample JSON string with nested structure
        String jsonString = "{\"name\": \"John\", \"age\": 30, \"city\": \"New York\", \"address\": {\"street\": \"123 Main St\", \"zip\": \"10001\"}, \"hobbies\": [\"reading\", \"hiking\", \"coding\"]}";
        System.out.println("Original JSON: " + jsonString);

        // Parse JSON to object
        try {
            Object jsonObject = JsonKit.parse(jsonString);
            System.out.println("Parsed JSON object: " + jsonObject);

            // Format object to JSON
            String formattedJson = JsonKit.toJsonString(jsonObject);
            System.out.println("Formatted JSON:\n" + formattedJson);

            // Demonstrate JSON with simple types
            String simpleJson = "{\"id\": 1, \"name\": \"Test\", \"active\": true, \"score\": 95.5}";
            System.out.println("\nSimple JSON: " + simpleJson);
            Object simpleJsonObject = JsonKit.parse(simpleJson);
            System.out.println("Parsed simple JSON object: " + simpleJsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}