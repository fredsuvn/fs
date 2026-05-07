package internal.samples;

import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaIntrospector;

/**
 * Sample: Object Meta Usage
 * <p>
 * Purpose: Demonstrate how to use the object meta utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Introspect the object meta from a class
 *   </li>
 *   <li>
 *     Access object properties through meta
 *   </li>
 *   <li>
 *     Work with nested object meta
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ObjectMeta}: Represents the meta of an object
 *   </li>
 *   <li>
 *     {@link ObjectMetaIntrospector}: Introspects the object meta from classes
 *   </li>
 * </ul>
 */
public class MetaSample {

    public static void main(String[] args) {
        demonstrateObjectMeta();
    }

    /**
     * Demonstrates object meta introspection and usage.
     */
    public static void demonstrateObjectMeta() {
        System.out.println("=== Object Meta Processing ===");

        try {
            // Introspect the object's meta from a class
            ObjectMeta meta = ObjectMetaIntrospector.defaultIntrospector().introspect(Person.class);
            System.out.println("Introspected meta for Person class: " + meta);

            // Access meta properties
            System.out.println("Number of properties: " + meta.properties().size());
            meta.properties().forEach((name, property) -> {
                System.out.println("Property: " + name + ", Type: " + property.type());
            });

            // Create an instance and set properties
            Person person = new Person();
            person.setName("John");
            person.setAge(30);
            person.setAddress(new Address("123 Main St", "New York"));

            // Access properties through meta
            System.out.println("\nPerson instance:");
            System.out.println("Name: " + meta.getProperty("name").getValue(person));
            System.out.println("Age: " + meta.getProperty("age").getValue(person));

            // Access nested properties
            Object address = meta.getProperty("address").getValue(person);
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
     * Sample class for meta demonstration.
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
     * Nested class for meta demonstration.
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