package internal.samples;

import space.sunqian.fs.data.json.JsonKit;
import space.sunqian.fs.data.properties.PropertiesKit;

import java.util.Properties;

/**
 * Sample: Data Processing Usage
 * <p>
 * Purpose: Demonstrate how to use the data processing utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     JSON parsing and formatting
 *   </li>
 *   <li>
 *     Properties file handling
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link JsonKit}: JSON parsing and formatting utilities
 *   </li>
 *   <li>
 *     {@link PropertiesKit}: Properties file handling utilities
 *   </li>
 * </ul>
 */
public class DataSample {

    public static void main(String[] args) {
        demonstrateJsonProcessing();
        demonstratePropertiesProcessing();
    }

    /**
     * Demonstrates JSON parsing and formatting.
     */
    public static void demonstrateJsonProcessing() {
        System.out.println("=== JSON Processing ===");

        // Sample JSON string
        String jsonString = "{\"name\": \"John\", \"age\": 30, \"city\": \"New York\"}";
        System.out.println("Original JSON: " + jsonString);

        // Parse JSON to object
        try {
            Object jsonObject = JsonKit.parse(jsonString);
            System.out.println("Parsed JSON object: " + jsonObject);

            // Format object to JSON
            String formattedJson = JsonKit.toJsonString(jsonObject);
            System.out.println("Formatted JSON: " + formattedJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates properties file handling.
     */
    public static void demonstratePropertiesProcessing() {
        System.out.println("\n=== Properties Processing ===");

        // Create properties object
        Properties properties = new Properties();
        properties.setProperty("name", "John");
        properties.setProperty("age", "30");
        properties.setProperty("city", "New York");

        // Convert properties to string
        String propertiesString = properties.toString();
        System.out.println("Properties string: " + propertiesString);

        // Load properties from string (using standard Properties API)
        Properties loadedProperties = new Properties();
        System.out.println("Loaded properties: " + loadedProperties);

        // Get property value
        String name = properties.getProperty("name");
        System.out.println("Name property: " + name);
    }
}