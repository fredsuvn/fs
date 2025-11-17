package tests.annotations;

import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class Jsr305Test extends Samples {

    @Test
    public void testJsr305() throws Exception {
        assertNotNull(testNonnullAlways());
        assertNotNull(testNonnullUnknown());
        assertNull(testNonnullNever());
        assertNotNull(testNonnullMaybe(1));
        assertNull(testNonnullMaybe(-1));
        Nonnull.Checker checker = new Nonnull.Checker();
        Nonnull nonnull = Jsr305Test.class.getDeclaredMethod("testNonnullAlways").getAnnotation(Nonnull.class);
        assertEquals(When.NEVER, checker.forConstantValue(nonnull, null));
        assertEquals(When.ALWAYS, checker.forConstantValue(nonnull, "testNonnullAlways"));
    }

    @Nonnull(when = When.ALWAYS)
    private String testNonnullAlways() {
        return "testNonnullAlways";
    }

    @Nonnull(when = When.UNKNOWN)
    private String testNonnullUnknown() {
        return "testNonnullUnknown";
    }

    @Nonnull(when = When.MAYBE)
    private String testNonnullMaybe(int i) {
        return i > 0 ? "testNonnullMaybe" : null;
    }

    @Nonnull(when = When.NEVER)
    private String testNonnullNever() {
        return null;
    }
}
