package tests.base.lang;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.lang.EnumKit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class EnumTest {

    @Test
    public void testFindEnum() {
        assertEquals(EnumKit.findEnum(X.class, "A"), X.A);
        assertEquals(EnumKit.findEnum(X.class, "B"), X.B);
        assertEquals(EnumKit.findEnum(X.class, "C"), X.C);
        assertEquals(EnumKit.findEnum(X.class, 0), X.A);
        assertEquals(EnumKit.findEnum(X.class, 1), X.B);
        assertEquals(EnumKit.findEnum(X.class, 2), X.C);
        assertNull(EnumKit.findEnum(X.class, "D"));
        assertNull(EnumKit.findEnum(Jie.as(String.class), "D"));
        assertNull(EnumKit.findEnum(X.class, 3));
        assertNull(EnumKit.findEnum(X.class, -1));
        assertNull(EnumKit.findEnum(Jie.as(String.class), 3));
    }

    enum X {
        A, B, C;
    }
}
