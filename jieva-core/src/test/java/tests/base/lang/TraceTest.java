package tests.base.lang;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.lang.TraceKit;
import xyz.sunqian.test.PrintTest;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class TraceTest implements PrintTest {

    @Test
    public void testTrace() {
        List<StackTraceElement> elements = TraceKit.stackTrace();
        printFor("Stack trace", elements);
        if (elements.size() > 0) {
            StackTraceElement first = elements.get(0);
            assertEquals(first.getClassName(), TraceTest.class.getName());
            assertEquals(first.getMethodName(), "testTrace");
        }
    }
}
