package tests.core.base.value;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.value.SimpleKey;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleKeyTest {

    @Test
    public void testSimpleKey() {
        testSimpleKeyToString();
        testSimpleKeyEquality();
        testSimpleKeyHashCode();
        testSimpleKeyGetAs();
    }

    private void testSimpleKeyToString() {
        assertEquals(
            "SimpleKey" + Arrays.toString(new Object[]{1, 2, 3}),
            SimpleKey.of(1, 2, 3).toString()
        );
    }

    private void testSimpleKeyEquality() {
        assertEquals(SimpleKey.of(1, 2, 3), SimpleKey.of(1, 2, 3));
        assertNotEquals(SimpleKey.of(1, 2, 3), SimpleKey.of(1, 2));
        assertFalse(SimpleKey.of(1, 2, 3).equals(1));
        SimpleKey sk = SimpleKey.of(1, 2, 3);
        assertTrue(sk.equals(sk));
    }

    private void testSimpleKeyHashCode() {
        assertEquals(
            Arrays.hashCode(new Object[]{1, 2, 3}),
            SimpleKey.of(1, 2, 3).hashCode()
        );
    }

    private void testSimpleKeyGetAs() {
        assertEquals(1, (Object) SimpleKey.of(1, 2, 3).getAs(0));
        assertEquals(2, (Object) SimpleKey.of(1, 2, 3).getAs(1));
        assertEquals(3, (Object) SimpleKey.of(1, 2, 3).getAs(2));
        assertThrows(IndexOutOfBoundsException.class, () -> SimpleKey.of(1, 2, 3).getAs(3));
        assertThrows(ClassCastException.class, () -> {
            long l = SimpleKey.of(1, 2, 3).getAs(0);
            System.out.println(l);
        });
    }
}
