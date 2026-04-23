package internal.samples;

import space.sunqian.fs.object.convert.ObjectConverter;

/**
 * Sample: Object Conversion Usage
 * <p>
 * Purpose: Demonstrate how to use the object conversion utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Convert objects between different types
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
 * </ul>
 */
public class ConvertSample {

    public static void main(String[] args) {
        demonstrateBasicObjectConversion();
    }

    /**
     * Demonstrates basic object conversion between different types.
     */
    public static void demonstrateBasicObjectConversion() {
        System.out.println("=== Basic Object Conversion Demonstration ===");

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
                "id=" + new String(id) +
                ", code=" + code +
                '}';
        }
    }
}