package tests.annotations.nonnullpkg;

import org.junit.jupiter.api.Test;
import space.sunqian.annotation.DefaultNonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class to verify package-level @DefaultNonNull annotation behavior.
 */
public class NonnullTest {

    // This field should be non-null by default due to package-level @DefaultNonNull
    private final String nonNullStr = "";

    @Test
    public void testPackageLevelDefaultNonNull() {
        // Verify that the class is in the package with @DefaultNonNull
        Package pkg = NonnullTest.class.getPackage();
        assertTrue(pkg.isAnnotationPresent(DefaultNonNull.class),
            "Package should have @DefaultNonNull annotation");
    }

    @Test
    public void testNonNullFieldInitialization() {
        // Verify that the field is properly initialized
        assertNotNull(nonNullStr, "nonNullStr should not be null");
        assertEquals("", nonNullStr, "nonNullStr should be an empty string");
    }
}
