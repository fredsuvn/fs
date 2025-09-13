package tests.base.lang;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.lang.TraceKit;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TraceTest implements PrintTest {

    @Test
    public void testTrace() throws Exception {
        List<StackTraceElement> elements = TraceKit.stackTrace();
        printFor("Stack trace", elements);
        if (!elements.isEmpty()) {
            StackTraceElement first = elements.get(0);
            assertEquals(first.getClassName(), TraceTest.class.getName());
            assertEquals(first.getMethodName(), "testTrace");
        }
        Method method = TraceKit.class.getDeclaredMethod("parseStackTrace", StackTraceElement[].class);
        method.setAccessible(true);
        assertEquals(
            method.invoke(null, (Object) new StackTraceElement[0]),
            Collections.emptyList()
        );
        assertEquals(
            method.invoke(null, (Object) new StackTraceElement[]{
                new StackTraceElement(TraceKit.class.getName(), "b", "c", 1)}),
            Collections.emptyList()
        );
    }
}
