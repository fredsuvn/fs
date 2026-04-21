package internal.samples;

import space.sunqian.fs.data.DataList;
import space.sunqian.fs.data.DataMap;
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
 *   <li>
 *     DataMap and DataList usage
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
 *   <li>
 *     {@link DataMap}: Data map for structured data
 *   </li>
 *   <li>
 *     {@link DataList}: Data list for ordered data
 *   </li>
 * </ul>
 */
public class DataSample {

    public static void main(String[] args) {
        demonstrateJsonProcessing();
        demonstratePropertiesProcessing();
        demonstrateDataMapAndList();
    }

    /**
     * Demonstrates JSON parsing and formatting.
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

        // Get property value
        String name = properties.getProperty("name");
        System.out.println("Name property: " + name);

        // Get property with default value
        String country = properties.getProperty("country", "USA");
        System.out.println("Country property (with default): " + country);
    }

    /**
     * Demonstrates DataMap and DataList usage.
     */
    public static void demonstrateDataMapAndList() {
        System.out.println("\n=== DataMap and DataList Processing ===");

        // Create DataMap
        DataMap dataMap = DataMap.newMap();
        dataMap.put("name", "John");
        dataMap.put("age", 30);
        dataMap.put("city", "New York");
        System.out.println("DataMap: " + dataMap);

        // Get value from DataMap
        try {
            String name = dataMap.getString("name");
            int age = dataMap.getInt("age");
            System.out.println("Name from DataMap: " + name);
            System.out.println("Age from DataMap: " + age);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create DataList
        DataList dataList = DataList.newList();
        dataList.add("apple");
        dataList.add("banana");
        dataList.add("orange");
        System.out.println("DataList: " + dataList);

        // Get value from DataList
        try {
            String firstFruit = dataList.getString(0);
            System.out.println("First fruit from DataList: " + firstFruit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nested DataMap
        DataMap nestedMap = DataMap.newMap();
        nestedMap.put("street", "123 Main St");
        nestedMap.put("zip", "10001");
        dataMap.put("address", nestedMap);
        System.out.println("DataMap with nested address: " + dataMap);

        // DataList within DataMap
        DataList hobbiesList = DataList.newList();
        hobbiesList.add("reading");
        hobbiesList.add("hiking");
        dataMap.put("hobbies", hobbiesList);
        System.out.println("DataMap with hobbies list: " + dataMap);
    }
}