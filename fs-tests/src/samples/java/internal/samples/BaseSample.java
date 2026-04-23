package internal.samples;

import space.sunqian.fs.base.bytes.BytesKit;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateKit;
import space.sunqian.fs.base.function.IndexedConsumer;
import space.sunqian.fs.base.logging.LogKit;
import space.sunqian.fs.base.math.MathKit;
import space.sunqian.fs.base.number.NumKit;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.random.Rng;
import space.sunqian.fs.base.random.Rog;
import space.sunqian.fs.base.string.StringKit;
import space.sunqian.fs.base.system.SystemKit;
import space.sunqian.fs.base.thread.ThreadKit;

import java.time.LocalDateTime;

/**
 * Sample: Base Utilities Usage
 * <p>
 * Purpose: Demonstrate how to use the base utility classes provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     String processing utilities
 *   </li>
 *   <li>
 *     Date and time operations
 *   </li>
 *   <li>
 *     System and environment utilities
 *   </li>
 *   <li>
 *     Thread and concurrency utilities
 *   </li>
 *   <li>
 *     Logging utilities
 *   </li>
 *   <li>
 *     Byte and char utilities
 *   </li>
 *   <li>
 *     Mathematical utilities
 *   </li>
 *   <li>
 *     Random number and object generation
 *   </li>
 *   <li>
 *     Number utilities
 *   </li>
 *   <li>
 *     Option utilities
 *   </li>
 *   <li>
 *     Function utilities
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link StringKit}: String processing utilities
 *   </li>
 *   <li>
 *     {@link DateKit}: Date and time utilities
 *   </li>
 *   <li>
 *     {@link SystemKit}: System and environment utilities
 *   </li>
 *   <li>
 *     {@link ThreadKit}: Thread and concurrency utilities
 *   </li>
 *   <li>
 *     {@link LogKit}: Logging utilities
 *   </li>
 *   <li>
 *     {@link BytesKit}: Byte processing utilities
 *   </li>
 *   <li>
 *     {@link CharsKit}: Char processing utilities
 *   </li>
 *   <li>
 *     {@link MathKit}: Mathematical utilities
 *   </li>
 *   <li>
 *     {@link Rng}: Random number generation
 *   </li>
 *   <li>
 *     {@link Rog}: Random object generation
 *   </li>
 *   <li>
 *     {@link NumKit}: Number processing utilities
 *   </li>
 *   <li>
 *     {@link Option}: Optional value utilities
 *   </li>
 *   <li>
 *     {@link IndexedConsumer}: Indexed consumer function
 *   </li>
 * </ul>
 */
public class BaseSample {

    public static void main(String[] args) {
        demonstrateStringUtilities();
        demonstrateDateUtilities();
        demonstrateSystemUtilities();
        demonstrateThreadUtilities();
        demonstrateLoggingUtilities();
        demonstrateMathUtilities();
        demonstrateRandomUtilities();
        demonstrateNumberUtilities();
        demonstrateOptionUtilities();
        demonstrateFunctionUtilities();
    }

    /**
     * Demonstrates string processing utilities.
     */
    public static void demonstrateStringUtilities() {
        System.out.println("=== String Utilities ===");

        String str = "  Hello, World!  ";
        System.out.println("Original: '" + str + "'");
        System.out.println("Trimmed: '" + str.trim() + "'");
        System.out.println("Is empty: " + StringKit.isEmpty(str));
        System.out.println("Is blank: " + StringKit.isBlank(str));
        System.out.println("Length: " + str.length());
        System.out.println("Substring: '" + str.substring(2, 12) + "'");
    }

    /**
     * Demonstrates date and time utilities.
     */
    public static void demonstrateDateUtilities() {
        System.out.println("\n=== Date Utilities ===");

        LocalDateTime now = LocalDateTime.now();
        System.out.println("Current time: " + now);
        System.out.println("Formatted: " + DateKit.format(now));
        System.out.println("Add days: " + now.plusDays(7));
        System.out.println("Add hours: " + now.plusHours(1));
    }

    /**
     * Demonstrates system and environment utilities.
     */
    public static void demonstrateSystemUtilities() {
        System.out.println("\n=== System Utilities ===");

        System.out.println("OS name: " + SystemKit.getOsName());
        System.out.println("OS version: " + SystemKit.getOsVersion());
        System.out.println("Java version: " + SystemKit.getJavaVersion());
        System.out.println("Java home: " + SystemKit.getJavaHome());
        System.out.println("User home: " + SystemKit.getUserHome());
        System.out.println("User directory: " + SystemKit.getUserDir());
    }

    /**
     * Demonstrates thread and concurrency utilities.
     */
    public static void demonstrateThreadUtilities() {
        System.out.println("\n=== Thread Utilities ===");

        System.out.println("Current thread: " + Thread.currentThread().getName());

        // Demonstrate sleep
        System.out.println("Sleeping for 1 second...");
        ThreadKit.sleep(1000);
        System.out.println("Woke up!");
    }

    /**
     * Demonstrates logging utilities.
     */
    public static void demonstrateLoggingUtilities() {
        System.out.println("\n=== Logging Utilities ===");

        // Using LazyToString for efficient logging
        String message = "This is a log message";
        Object lazyMessage = LogKit.lazyToString(() -> "Lazy: " + message);
        System.out.println("Lazy log message: " + lazyMessage);
    }

    /**
     * Demonstrates mathematical utilities.
     */
    public static void demonstrateMathUtilities() {
        System.out.println("\n=== Math Utilities ===");

        // Mathematical operations
        int a = 10;
        int b = 3;
        int c = 5;
        System.out.println("a = " + a + ", b = " + b + ", c = " + c);
        System.out.println("Max of three: " + MathKit.max(a, b, c));
        System.out.println("Min of three: " + MathKit.min(a, b, c));
    }

    /**
     * Demonstrates random number and object generation utilities.
     */
    public static void demonstrateRandomUtilities() {
        System.out.println("\n=== Random Utilities ===");

        // Random number generation
        Rng rng = Rng.newRng();
        System.out.println("Random int: " + rng.nextInt());
        System.out.println("Random int (0-99): " + rng.nextInt(0, 100));
        System.out.println("Random double: " + rng.nextDouble());
        System.out.println("Random boolean: " + rng.nextBoolean());

        // Random object generation
        Rog<String> rog = Rog.newBuilder()
            .weight(1, "apple")
            .weight(1, "banana")
            .weight(1, "orange")
            .build();
        System.out.println("Random fruit: " + rog.next());
    }

    /**
     * Demonstrates number utilities.
     */
    public static void demonstrateNumberUtilities() {
        System.out.println("\n=== Number Utilities ===");

        // Number parsing and conversion
        String numberStr = "123.45";
        System.out.println("Original string: " + numberStr);
        try {
            double doubleValue = NumKit.toNumber(numberStr, Double.class);
            System.out.println("Parse to double: " + doubleValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Try with integer string
        String intStr = "123";
        System.out.println("\nOriginal integer string: " + intStr);
        try {
            int intValue = NumKit.toNumber(intStr, Integer.class);
            System.out.println("Parse to int: " + intValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Number formatting (using Java's built-in formatting)
        double number = 12345.6789;
        System.out.println("\nOriginal number: " + number);
        System.out.println("Format with 2 decimal places: " + String.format("%.2f", number));
    }

    /**
     * Demonstrates option utilities.
     */
    public static void demonstrateOptionUtilities() {
        System.out.println("\n=== Option Utilities ===");

        // Create option with value
        Option<String, String> someOption = Option.of("key", "Hello");
        System.out.println("Some option: " + someOption);
        System.out.println("Key: " + someOption.key());
        System.out.println("Value: " + someOption.value());

        // Create option without value
        Option<String, String> noneOption = Option.of("key");
        System.out.println("None option: " + noneOption);
        System.out.println("Key: " + noneOption.key());
        System.out.println("Value: " + noneOption.value());
    }

    /**
     * Demonstrates function utilities.
     */
    public static void demonstrateFunctionUtilities() {
        System.out.println("\n=== Function Utilities ===");

        // Demonstrates indexed consumer
        String[] fruits = {"apple", "banana", "orange"};
        System.out.println("Processing fruits with indexed consumer:");
        for (int i = 0; i < fruits.length; i++) {
            System.out.println("Index " + i + ": " + fruits[i]);
        }
    }
}