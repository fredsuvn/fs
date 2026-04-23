package internal.samples;

import space.sunqian.fs.Fs;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample: Object Operations Usage
 * <p>
 * Purpose: Demonstrate how to use the object utilities provided by fs-core module, including conversion, copying, and
 * property access.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Convert objects between different types
 *   </li>
 *   <li>
 *     Copy properties between objects
 *   </li>
 *   <li>
 *     Access object properties dynamically
 *   </li>
 *   <li>
 *     Handle type conversions automatically
 *   </li>
 *   <li>
 *     Customize conversion behavior
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ObjectConverter}: Main class for object conversion
 *   </li>
 *   <li>
 *     {@link ObjectCopier}: Main class for object property copying
 *   </li>
 *   <li>
 *     {@link ObjectSchema}: Object schema for type information
 *   </li>
 *   <li>
 *     {@link Fs}: Core utility class with object-related methods
 *   </li>
 * </ul>
 */
public class ObjectSamples {

    public static void main(String[] args) {
        demonstrateBasicObjectConversion();
        demonstrateObjectCopy();
        demonstratePropertyAccess();
        demonstrateMapToObjectConversion();
    }

    /**
     * Demonstrates basic object conversion between different types.
     */
    public static void demonstrateBasicObjectConversion() {
        System.out.println("=== Basic Object Conversion ===");

        // Create source object
        DataFrom dataFrom = new DataFrom();
        dataFrom.setId("001");
        dataFrom.setCode(999);
        System.out.println("Source object: " + dataFrom);

        // Convert to target type
        DataTo dataTo = ObjectConverter.defaultConverter()
            .convert(dataFrom, DataTo.class);
        System.out.println("Converted object: " + dataTo);
    }

    /**
     * Demonstrates object property copying.
     */
    public static void demonstrateObjectCopy() {
        System.out.println("\n=== Object Property Copying ===");

        // Create source object
        DataFrom source = new DataFrom();
        source.setId("002");
        source.setCode(888);
        System.out.println("Source object: " + source);

        // Create target object
        DataTo target = new DataTo();
        System.out.println("Target object before copy: " + target);

        // Copy properties
        try {
            ObjectCopier.defaultCopier().copyProperties(source, target);
            System.out.println("Target object after copy: " + target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates dynamic property access.
     */
    public static void demonstratePropertyAccess() {
        System.out.println("\n=== Dynamic Property Access ===");

        // Create object
        DataFrom data = new DataFrom();
        data.setId("003");
        data.setCode(777);
        System.out.println("Object: " + data);

        // Access properties
        Object idValue = Fs.getValue(data, "id");
        Object codeValue = Fs.getValue(data, "code");
        System.out.println("Accessed id: " + idValue);
        System.out.println("Accessed code: " + codeValue);
    }

    /**
     * Demonstrates map to object conversion.
     */
    public static void demonstrateMapToObjectConversion() {
        System.out.println("\n=== Map to Object Conversion ===");

        // Create map
        Map<String, Object> map = new HashMap<>();
        map.put("id", "004");
        map.put("code", 666);
        System.out.println("Source map: " + map);

        // Convert to object
        DataFrom data = ObjectConverter.defaultConverter()
            .convert(map, DataFrom.class);
        System.out.println("Converted object: " + data);
    }

    /**
     * Source class for object conversion demonstration.
     */
    public static class DataFrom {
        private String id;
        private int code;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "DataFrom{" +
                "id='" + id + '\'' +
                ", code=" + code +
                '}';
        }
    }

    /**
     * Target class for object conversion demonstration.
     */
    public static class DataTo {
        private char[] id;
        private Double code;

        public char[] getId() {
            return id;
        }

        public void setId(char[] id) {
            this.id = id;
        }

        public Double getCode() {
            return code;
        }

        public void setCode(Double code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "DataTo{" +
                "id=" + (id != null ? new String(id) : "null") +
                ", code=" + code +
                '}';
        }
    }
}