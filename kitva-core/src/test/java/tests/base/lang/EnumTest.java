package tests.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.lang.EnumKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnumTest {

    @Test
    public void testFindEnum() {
        assertEquals(X.A, EnumKit.findEnum(X.class, "A"));
        assertEquals(X.B, EnumKit.findEnum(X.class, "B"));
        assertEquals(X.C, EnumKit.findEnum(X.class, "C"));
        assertEquals(X.A, EnumKit.findEnum(X.class, 0));
        assertEquals(X.B, EnumKit.findEnum(X.class, 1));
        assertEquals(X.C, EnumKit.findEnum(X.class, 2));
        assertNull(EnumKit.findEnum(X.class, "D"));
        assertNull(EnumKit.findEnum(Kit.as(String.class), "D"));
        assertNull(EnumKit.findEnum(X.class, 3));
        assertNull(EnumKit.findEnum(X.class, -1));
        assertNull(EnumKit.findEnum(Kit.as(String.class), 3));
    }

    enum X {
        A, B, C;
    }
}
