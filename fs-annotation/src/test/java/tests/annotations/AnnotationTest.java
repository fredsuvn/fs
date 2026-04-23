package tests.annotations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class AnnotationTest {

    /**
     * Sample class for testing annotations in fs-annotation module.
     */
    public static class Samples {

        // Nullable field demonstration
        private final @Nullable String nullable = null;

        // Non-null field demonstration
        private final @Nonnull String nonNull = "Nonnull";

        // Immutable list field demonstration with nested annotations
        private final @Nonnull
        @Immutable List<@Nonnull String> immutableList = Collections.emptyList();

        /**
         * Demonstrates @CachedResult annotation on a method. The result of this method is supposed to be cached.
         *
         * @return A cached string value
         */
        @CachedResult
        public static @Nonnull String cachedString() {
            return "123";
        }

        /**
         * Demonstrates @OutParam annotation on a method parameter. This method modifies the input list.
         *
         * @param dst The list to be modified
         */
        public static void withParam(@Nonnull @OutParam List<@Nonnull String> dst) {
            dst.add("hello");
        }

        /**
         * Demonstrates varargs parameter with @Nonnull annotation.
         *
         * @param args The varargs parameters
         * @return The same array that was passed in
         */
        public static @Nonnull Object @Nonnull [] withParam(@Nonnull Object @Nonnull ... args) {
            return args;
        }

        /**
         * Demonstrates @RetainedParam annotation on a method parameter. This annotation indicates that the parameter
         * should be retained.
         *
         * @param value The value to be retained
         * @return The same value
         */
        public static @Nonnull String withRetainedParam(@Nonnull @RetainedParam String value) {
            return value;
        }

        /**
         * Demonstrates @DefaultNullable annotation at class level. All fields in this class are nullable by default.
         */
        @DefaultNullable
        public static class NullableClass {
            private final String nullable1 = null;
            private final String nullable2 = null;
        }

        /**
         * Demonstrates @DefaultNonNull annotation at class level. All fields in this class are non-null by default.
         */
        @DefaultNonNull
        public static class NonNullClass {
            private final String nonNull1 = "nonNull1";
            private final String nonNull2 = "nonNull2";
        }

        /**
         * Demonstrates @ThreadSafe annotation on a class. This class is thread-safe.
         */
        @ThreadSafe
        public static class ThreadSafeClass {
            private int counter = 0;

            public synchronized void increment() {
                counter++;
            }

            public synchronized int getCounter() {
                return counter;
            }
        }

        /**
         * Demonstrates @NonExported annotation on a class. This class is public but not exported.
         */
        @NonExported
        public static class NonExportedClass {
        }

        /**
         * Demonstrates @ValueClass annotation on a class. This class is a value class.
         */
        @ValueClass
        public static class Person {
            private final @Nonnull String name;
            private final int age;

            public Person(@Nonnull String name, int age) {
                this.name = name;
                this.age = age;
            }

            public @Nonnull String getName() {
                return name;
            }

            public int getAge() {
                return age;
            }
        }

        /**
         * Demonstrates @SimpleClass annotation on a class. This class is a simple class.
         */
        @SimpleClass
        public static class SimpleExampleClass {
            private String value;

            public void setValue(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }
    }

    private Samples samples;

    @BeforeEach
    public void setUp() {
        samples = new Samples();
    }

    @Test
    public void testNullableAnnotation() {
        // Test that Nullable annotation is present on the field
        try {
            Field field = Samples.class.getDeclaredField("nullable");
            assertTrue(field.isAnnotationPresent(Nullable.class), "Nullable field should have @Nullable annotation");
        } catch (NoSuchFieldException e) {
            fail("Nullable field not found", e);
        }
    }

    @Test
    public void testNonnullAnnotation() {
        // Test that Nonnull annotation is present on the field
        try {
            Field field = Samples.class.getDeclaredField("nonNull");
            assertTrue(field.isAnnotationPresent(Nonnull.class), "NonNull field should have @Nonnull annotation");
        } catch (NoSuchFieldException e) {
            fail("NonNull field not found", e);
        }
    }

    @Test
    public void testImmutableAnnotation() {
        // Test that Immutable annotation is present on the field
        try {
            Field field = Samples.class.getDeclaredField("immutableList");
            assertTrue(field.isAnnotationPresent(Immutable.class), "Immutable list field should have @Immutable annotation");
        } catch (NoSuchFieldException e) {
            fail("Immutable list field not found", e);
        }
    }

    @Test
    public void testDefaultNullableAnnotation() {
        // Test that DefaultNullable annotation is present on the class
        assertTrue(Samples.NullableClass.class.isAnnotationPresent(DefaultNullable.class),
            "NullableClass should have @DefaultNullable annotation");
    }

    @Test
    public void testDefaultNonNullAnnotation() {
        // Test that DefaultNonNull annotation is present on the class
        assertTrue(Samples.NonNullClass.class.isAnnotationPresent(DefaultNonNull.class),
            "NonNullClass should have @DefaultNonNull annotation");
    }

    @Test
    public void testCachedResultAnnotation() {
        // Test that CachedResult annotation is present on the method
        try {
            Method method = Samples.class.getDeclaredMethod("cachedString");
            assertTrue(method.isAnnotationPresent(CachedResult.class), "cachedString method should have @CachedResult annotation");
        } catch (NoSuchMethodException e) {
            fail("cachedString method not found", e);
        }
    }

    @Test
    public void testOutParamAnnotation() {
        // Test that OutParam annotation is present on the method parameter
        try {
            Method method = Samples.class.getDeclaredMethod("withParam", List.class);
            Parameter[] parameters = method.getParameters();
            assertTrue(parameters[0].isAnnotationPresent(OutParam.class), "withParam method parameter should have @OutParam annotation");
        } catch (NoSuchMethodException e) {
            fail("withParam method not found", e);
        }
    }

    @Test
    public void testNonExportedAnnotation() {
        // Test that NonExported annotation is present on the classes
        assertTrue(Samples.NonExportedClass.class.isAnnotationPresent(NonExported.class),
            "NonExportedClass should have @NonExported annotation");
    }

    @Test
    public void testThreadSafeAnnotation() {
        // Test that ThreadSafe annotation is present on the class
        assertTrue(Samples.ThreadSafeClass.class.isAnnotationPresent(ThreadSafe.class),
            "ThreadSafeClass should have @ThreadSafe annotation");
    }

    @Test
    public void testWithParamMethod() {
        // Test the functionality of the withParam method with OutParam
        List<String> testList = new ArrayList<>();
        Samples.withParam(testList);
        assertEquals(1, testList.size(), "List should have one element after withParam call");
        assertEquals("hello", testList.get(0), "List should contain 'hello'");
    }

    @Test
    public void testWithParamVarargsMethod() {
        // Test the functionality of the withParam varargs method
        Object[] testArgs = {"test1", "test2"};
        Object[] result = Samples.withParam(testArgs);
        assertSame(testArgs, result, "Method should return the same array");
    }

    @Test
    public void testCachedStringMethod() {
        // Test the functionality of the cachedString method
        String result1 = Samples.cachedString();
        String result2 = Samples.cachedString();
        assertEquals("123", result1, "cachedString should return '123'");
        assertEquals(result1, result2, "cachedString should return the same value");
    }

    @Test
    public void testAnnotationPresence() {
        // Test that all annotation classes exist
        try {
            Class.forName("space.sunqian.annotation.Nullable");
            Class.forName("space.sunqian.annotation.Nonnull");
            Class.forName("space.sunqian.annotation.DefaultNullable");
            Class.forName("space.sunqian.annotation.DefaultNonNull");
            Class.forName("space.sunqian.annotation.CachedResult");
            Class.forName("space.sunqian.annotation.OutParam");
            Class.forName("space.sunqian.annotation.NonExported");
            Class.forName("space.sunqian.annotation.Immutable");
            Class.forName("space.sunqian.annotation.ThreadSafe");
            Class.forName("space.sunqian.annotation.ValueClass");
            Class.forName("space.sunqian.annotation.RetainedParam");
            Class.forName("space.sunqian.annotation.SimpleClass");
        } catch (ClassNotFoundException e) {
            fail("Annotation class not found: " + e.getMessage(), e);
        }
    }
}
