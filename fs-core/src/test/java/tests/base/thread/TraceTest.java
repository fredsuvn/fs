package tests.base.thread;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.thread.TraceKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraceTest implements PrintTest {

    @Test
    public void testTrace() throws Exception {
        List<StackTraceElement> elements = TraceKit.stackTrace();
        printFor("Stack trace", elements);
        if (!elements.isEmpty()) {
            StackTraceElement first = elements.get(0);
            assertEquals(first.getClassName(), TraceTest.class.getName());
            assertEquals("testTrace", first.getMethodName());
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
