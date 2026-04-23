package internal.samples;

import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.object.schema.ObjectSchemaParser;

/**
 * Sample: Object Schema Usage
 * <p>
 * Purpose: Demonstrate how to use the object schema utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Parse object schema from a class
 *   </li>
 *   <li>
 *     Access object properties through schema
 *   </li>
 *   <li>
 *     Work with nested object schemas
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ObjectSchema}: Represents the schema of an object
 *   </li>
 *   <li>
 *     {@link ObjectSchemaParser}: Parses object schema from classes
 *   </li>
 * </ul>
 */
public class SchemaSample {

    public static void main(String[] args) {
        demonstrateObjectSchema();
    }

    /**
     * Demonstrates object schema parsing and usage.
     */
    public static void demonstrateObjectSchema() {
        System.out.println("=== Object Schema Processing ===");

        try {
            // Parse schema from a class
            ObjectSchema schema = ObjectSchemaParser.defaultParser().parse(Person.class);
            System.out.println("Parsed schema for Person class: " + schema);

            // Access schema properties
            System.out.println("Number of properties: " + schema.properties().size());
            schema.properties().forEach((name, property) -> {
                System.out.println("Property: " + name + ", Type: " + property.type());
            });

            // Create an instance and set properties
            Person person = new Person();
            person.setName("John");
            person.setAge(30);
            person.setAddress(new Address("123 Main St", "New York"));

            // Access properties through schema
            System.out.println("\nPerson instance:");
            System.out.println("Name: " + schema.getProperty("name").getValue(person));
            System.out.println("Age: " + schema.getProperty("age").getValue(person));

            // Access nested properties
            Object address = schema.getProperty("address").getValue(person);
            System.out.println("Address:");
            if (address instanceof Address) {
                Address addr = (Address) address;
                System.out.println("Street: " + addr.getStreet());
                System.out.println("City: " + addr.getCity());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sample class for schema demonstration.
     */
    public static class Person {
        private String name;
        private int age;
        private Address address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    /**
     * Nested class for schema demonstration.
     */
    public static class Address {
        private String street;
        private String city;

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}