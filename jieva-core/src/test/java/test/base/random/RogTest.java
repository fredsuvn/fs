package test.base.random;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.random.Rog;
import xyz.sunqian.test.AssertTest;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class RogTest implements AssertTest, PrintTest {

    @Test
    public void testObjectSupplier() throws Exception {
        testObjectSupplier(10);
        testObjectSupplier(100);
        testObjectSupplier(1000);
        testObjectSupplier(10000);

        {
            // always
            Supplier<CharSequence> supplier = Rog.newRog(() -> 5).supplier(Arrays.asList(
                Rog.probability(10, "a"),
                Rog.probability(10, "b")
            ));
            for (int i = 0; i < 100; i++) {
                assertEquals(supplier.get(), "a");
            }
        }

        {
            // exception
            Supplier<CharSequence> supplier = Rog.supplier(
                Rog.probability(10, "a")
            );
            Method getNode = supplier.getClass().getDeclaredMethod("getNode", long.class);
            invokeThrows(UnreachablePointException.class, getNode, supplier, -1L);
        }
    }

    private void testObjectSupplier(int size) {
        List<CharSequence> list = new ArrayList<>(size);
        Supplier<CharSequence> supplier = Rog.supplier(
            Rog.probability(10, "a"),
            Rog.probability(30, () -> "b"),
            Rog.probability(60, "c")
        );
        for (int i = 0; i < size; i++) {
            list.add(supplier.get());
        }
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
        assertEquals(x, 0);
        showProbability(size, ac, bc, cc);

        // exception:
        assertThrows(IllegalArgumentException.class, () -> Rog.probability(-1, "a"));
    }

    private void showProbability(int size, int ac, int bc, int cc) {
        double total = ac + bc + cc;
        println(
            "size: " + size +
                "[a: " + toPercent(ac / total) +
                ", b: " + toPercent(bc / total) +
                ", c: " + toPercent(cc / total) + "]"
        );
    }

    private String toPercent(double d) {
        return d * 100 + "%";
    }
}
