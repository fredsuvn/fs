package tests.annotations.nullablepkg;

import org.junit.jupiter.api.Test;
import space.sunqian.annotation.DefaultNullable;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class to verify package-level @DefaultNullable annotation behavior.
 */
public class NullableTest {

    // This field can be null by default due to package-level @DefaultNullable
    private final String nullableStr = null;

    @Test
    public void testPackageLevelDefaultNullable() {
        // Verify that the class is in the package with @DefaultNullable
        Package pkg = NullableTest.class.getPackage();
        assertTrue(pkg.isAnnotationPresent(DefaultNullable.class),
            "Package should have @DefaultNullable annotation");
    }

    @Test
    public void testNullableFieldInitialization() {
        // Verify that the field is properly initialized to null
        assertNull(nullableStr, "nullableStr should be null");
    }
}
