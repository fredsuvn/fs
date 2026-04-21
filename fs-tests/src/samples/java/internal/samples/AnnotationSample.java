package internal.samples;

import space.sunqian.annotation.CachedResult;
import space.sunqian.annotation.DefaultNonNull;
import space.sunqian.annotation.DefaultNullable;
import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.NonExported;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.OutParam;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.SimpleClass;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.annotation.ValueClass;

import java.util.Collections;
import java.util.List;

/**
 * Sample: Annotation Usage
 * <p>
 * Purpose: Demonstrate how to use annotations provided by fs-annotation module for code analysis and validation.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Null safety: Mark fields, parameters, and return types as nullable or non-null
 *   </li>
 *   <li>
 *     Immutability: Mark objects as immutable for better thread safety
 *   </li>
 *   <li>
 *     Caching: Mark methods whose results should be cached
 *   </li>
 *   <li>
 *     Parameter direction: Mark parameters as output parameters
 *   </li>
 *   <li>
 *     Default nullability: Set default nullability for all fields in a class
 *   </li>
 *   <li>
 *     Export control: Mark classes as non-exported
 *   </li>
 * </ul>
 * <p>
 * Key Annotations:
 * <ul>
 *   <li>
 *     {@link Nullable}: Marks a field, parameter, or return type as potentially null
 *   </li>
 *   <li>
 *     {@link Nonnull}: Marks a field, parameter, or return type as non-null
 *   </li>
 *   <li>
 *     {@link Immutable}: Marks an object as immutable
 *   </li>
 *   <li>
 *     {@link CachedResult}: Marks a method whose result should be cached
 *   </li>
 *   <li>
 *     {@link OutParam}: Marks a parameter as an output parameter
 *   </li>
 *   <li>
 *     {@link DefaultNullable}: Sets default nullability to nullable for all fields in a class
 *   </li>
 *   <li>
 *     {@link DefaultNonNull}: Sets default nullability to non-null for all fields in a class
 *   </li>
 *   <li>
 *     {@link NonExported}: Marks a class as non-exported
 *   </li>
 *   <li>
 *     {@link ThreadSafe}: Marks a class as thread-safe
 *   </li>
 *   <li>
 *     {@link ValueClass}: Marks a class as a value class
 *   </li>
 *   <li>
 *     {@link RetainedParam}: Marks a parameter as retained
 *   </li>
 *   <li>
 *     {@link SimpleClass}: Marks a class as a simple class
 *   </li>
 * </ul>
 */
public class AnnotationSample {

    /**
     * Demonstrates null safety annotations for fields.
     */
    public static void demonstrateNullSafetyAnnotations() {
        // Nullable field - can be null
        @Nullable String nullable = null;
        System.out.println("Nullable value: " + nullable);

        // Non-null field - should never be null
        @Nonnull String nonNull = "Nonnull";
        System.out.println("Non-null value: " + nonNull);

        // Immutable list with non-null elements
        @Immutable List<@Nonnull String> immutableList = Collections.emptyList();
        System.out.println("Immutable list size: " + immutableList.size());
    }

    /**
     * Demonstrates cached result annotation for methods. The result of this method will be cached for subsequent
     * calls.
     */
    @CachedResult
    public static @Nonnull String cachedString() {
        System.out.println("Computing cached string...");
        return "123";
    }

    /**
     * Demonstrates out parameter annotation for method parameters. This annotation indicates that the parameter is
     * intended to be modified by the method.
     */
    public static void demonstrateOutParam(@Nonnull @OutParam List<@Nonnull String> dst) {
        dst.add("hello");
        System.out.println("Added 'hello' to list");
    }

    /**
     * Demonstrates array parameter with non-null annotations.
     */
    public static @Nonnull Object @Nonnull [] demonstrateArrayParam(@Nonnull Object @Nonnull ... args) {
        System.out.println("Array parameter length: " + args.length);
        return args;
    }

    /**
     * Demonstrates default nullable annotation for a class. All fields in this class are nullable by default.
     */
    @DefaultNullable
    public static class NullableClass {
        private final String nullable1 = null;
        private final String nullable2 = null;
    }

    /**
     * Demonstrates default non-null annotation for a class. All fields in this class are non-null by default.
     */
    @DefaultNonNull
    public static class NonNullClass {
        private final String nonNull1 = "nonNull1";
        private final String nonNull2 = "nonNull2";
    }

    /**
     * Demonstrates non-exported annotation for a class. This class is thread-safe but not exported.
     */
    @NonExported
    public static class ThreadSafeClass {}

    /**
     * Demonstrates non-exported annotation for a class. This class is public but not exported.
     */
    @NonExported
    public static class NonExportedClass {}

    /**
     * Demonstrates thread-safe annotation for a class. This class is designed to be thread-safe.
     */
    @ThreadSafe
    public static class ThreadSafeExample {
        private final Object lock = new Object();
        private int count = 0;

        public void increment() {
            synchronized (lock) {
                count++;
            }
        }

        public int getCount() {
            synchronized (lock) {
                return count;
            }
        }
    }

    /**
     * Demonstrates value class annotation for a class. This class represents a value object.
     */
    @ValueClass
    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    /**
     * Demonstrates simple class annotation for a class. This class is a simple data holder.
     */
    @SimpleClass
    public static class Person {
        private String name;
        private int age;

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
    }

    /**
     * Demonstrates retained parameter annotation for method parameters. This annotation indicates that the parameter
     * should be retained, for example, when passing arrays to methods that store them.
     */
    public static <T> T[] demonstrateRetainedParam(@Nonnull T @Nonnull @RetainedParam ... elements) {
        System.out.println("Retained array parameter length: " + elements.length);
        return elements;
    }

    public static void main(String[] args) {
        demonstrateNullSafetyAnnotations();

        // Test cached result
        System.out.println("First call to cachedString(): " + cachedString());
        System.out.println("Second call to cachedString(): " + cachedString());

        // Test out parameter
        List<String> list = new java.util.ArrayList<>();
        demonstrateOutParam(list);
        System.out.println("List after out param: " + list);

        // Test array parameter
        Object[] result = demonstrateArrayParam("a", "b", "c");
        System.out.println("Array result: " + java.util.Arrays.toString(result));

        // Test retained parameter
        String[] retainedResult = demonstrateRetainedParam("x", "y", "z");
        System.out.println("Retained array result: " + java.util.Arrays.toString(retainedResult));

        // Test thread-safe class
        ThreadSafeExample threadSafeExample = new ThreadSafeExample();
        threadSafeExample.increment();
        System.out.println("Thread-safe count: " + threadSafeExample.getCount());

        // Test value class
        Point point1 = new Point(1, 2);
        Point point2 = new Point(1, 2);
        System.out.println("Point1 equals Point2: " + point1.equals(point2));
        System.out.println("Point1 hash code: " + point1.hashCode());

        // Test simple class
        Person person = new Person();
        person.setName("John");
        person.setAge(30);
        System.out.println("Person: " + person.getName() + ", " + person.getAge());
    }
}
