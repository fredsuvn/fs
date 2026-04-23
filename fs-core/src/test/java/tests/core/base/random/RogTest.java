package tests.core.base.random;

import internal.utils.Asserter;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.exception.UnreachablePointException;
import space.sunqian.fs.base.random.Rog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RogTest implements Asserter, TestPrint {

    @Test
    public void testObjectSupplier() throws Exception {
        // Test with different sizes
        testObjectSupplier(10);
        testObjectSupplier(100);
        testObjectSupplier(1000);
        testObjectSupplier(10000);

        // Test always returning the same value
        testAlwaysReturnSameValue();

        // Test exception case
        testExceptionCase();
    }

    private void testAlwaysReturnSameValue() {
        Rog<CharSequence> rog = Rog.newBuilder()
            .rng(() -> 5)
            .weight(10, "a")
            .weight(10, "b")
            .build();
        for (int i = 0; i < 100; i++) {
            assertEquals("a", rog.next());
        }
    }

    private void testExceptionCase() throws Exception {
        Rog<CharSequence> rog = Rog.newBuilder()
            .weight(10, "a")
            .build();
        Method getNode = rog.getClass().getDeclaredMethod("getWeight", long.class);
        invokeThrows(UnreachablePointException.class, getNode, rog, -1L);
    }

    private void testObjectSupplier(int size) {
        List<CharSequence> list = new ArrayList<>(size);
        Rog<CharSequence> rog = Rog.newBuilder()
            .weight(10, "a")
            .weight(30, () -> "b")
            .weight(60, "c")
            .build();

        // Generate random values
        for (int i = 0; i < size; i++) {
            list.add(rog.next());
        }

        // Count occurrences
        int ac = 0;
        int bc = 0;
        int cc = 0;
        int x = 0;
        for (CharSequence c : list) {
            if (c.equals("a")) {
                ac++;
            } else if (c.equals("b")) {
                bc++;
            } else if (c.equals("c")) {
                cc++;
            } else {
                x++;
            }
        }

        // Verify no unexpected values
        assertEquals(0, x);

        // Show probability distribution
        showProbability(size, ac, bc, cc);

        // Test exception case for negative weight
        assertThrows(IllegalArgumentException.class, () -> Rog.newBuilder().weight(-1, "a"));
    }

    private void showProbability(int size, int ac, int bc, int cc) {
        double total = ac + bc + cc;
        printFor(
            "size " + size,
            "[a: " + toPercent(ac / total) +
                ", b: " + toPercent(bc / total) +
                ", c: " + toPercent(cc / total) + "]"
        );
    }

    private String toPercent(double d) {
        return d * 100 + "%";
    }
}
