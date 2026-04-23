package internal.samples;

import space.sunqian.fs.object.convert.ObjectCopier;

/**
 * Sample: Object Copier Usage
 * <p>
 * Purpose: Demonstrate how to use the object copier utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Copy properties between objects of the same type
 *   </li>
 *   <li>
 *     Copy properties between objects of different types
 *   </li>
 *   <li>
 *     Handle nested object copying
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ObjectCopier}: Copies properties between objects
 *   </li>
 * </ul>
 */
public class CopierSample {

    public static void main(String[] args) {
        demonstrateObjectCopy();
    }

    /**
     * Demonstrates object property copying.
     */
    public static void demonstrateObjectCopy() {
        System.out.println("=== Object Copier Processing ===");

        try {
            // Create source object
            SourceObject source = new SourceObject();
            source.setName("John");
            source.setAge(30);
            source.setAddress(new Address("123 Main St", "New York"));
            System.out.println("Source object: " + source);

            // Copy to same type
            SourceObject targetSameType = new SourceObject();
            ObjectCopier.defaultCopier().copyProperties(source, targetSameType);
            System.out.println("Copied to same type: " + targetSameType);

            // Copy to different type
            TargetObject targetDifferentType = new TargetObject();
            ObjectCopier.defaultCopier().copyProperties(source, targetDifferentType);
            System.out.println("Copied to different type: " + targetDifferentType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Source class for copying demonstration.
     */
    public static class SourceObject {
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

        @Override
        public String toString() {
            return "SourceObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address=" + address +
                '}';
        }
    }

    /**
     * Target class for copying demonstration.
     */
    public static class TargetObject {
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

        @Override
        public String toString() {
            return "TargetObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address=" + address +
                '}';
        }
    }

    /**
     * Address class for nested object copying demonstration.
     */
    public static class Address {
        private String street;
        private String city;

        public Address() {
        }

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

        @Override
        public String toString() {
            return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                '}';
        }
    }
}