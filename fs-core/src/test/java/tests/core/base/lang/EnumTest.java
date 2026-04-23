package tests.core.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.lang.EnumKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnumTest {

    @Test
    public void testFindEnumByName() {
        // Test finding enum by name
        assertEquals(X.A, EnumKit.findEnum(X.class, "A"));
        assertEquals(X.B, EnumKit.findEnum(X.class, "B"));
        assertEquals(X.C, EnumKit.findEnum(X.class, "C"));

        // Test with non-existent name
        assertNull(EnumKit.findEnum(X.class, "D"));

        // Test with non-enum class
        assertNull(EnumKit.findEnum(Fs.as(String.class), "D"));
    }

    @Test
    public void testFindEnumByOrdinal() {
        // Test finding enum by ordinal
        assertEquals(X.A, EnumKit.findEnum(X.class, 0));
        assertEquals(X.B, EnumKit.findEnum(X.class, 1));
        assertEquals(X.C, EnumKit.findEnum(X.class, 2));

        // Test with out-of-bounds ordinal
        assertNull(EnumKit.findEnum(X.class, 3));
        assertNull(EnumKit.findEnum(X.class, -1));

        // Test with non-enum class
        assertNull(EnumKit.findEnum(Fs.as(String.class), 3));
    }

    enum X {
        A, B, C;
    }
}
