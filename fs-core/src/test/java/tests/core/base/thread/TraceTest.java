package tests.core.base.thread;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.thread.TraceKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraceTest implements TestPrint {

    @Test
    public void testTrace() throws Exception {
        testStackTrace();
        testParseStackTrace();
    }

    private void testStackTrace() {
        List<StackTraceElement> elements = TraceKit.stackTrace();
        printFor("Stack trace", elements);
        if (!elements.isEmpty()) {
            StackTraceElement first = elements.get(0);
            assertEquals(first.getClassName(), TraceTest.class.getName());
            assertEquals("testStackTrace", first.getMethodName());
        }
    }

    private void testParseStackTrace() throws Exception {
        Method method = TraceKit.class.getDeclaredMethod("parseStackTrace", StackTraceElement[].class);
        method.setAccessible(true);

        testParseEmptyStackTrace(method);
        testParseTraceKitStackTrace(method);
    }

    private void testParseEmptyStackTrace(Method method) throws Exception {
        assertEquals(
            method.invoke(null, (Object) new StackTraceElement[0]),
            Collections.emptyList()
        );
    }

    private void testParseTraceKitStackTrace(Method method) throws Exception {
        assertEquals(
            method.invoke(null, (Object) new StackTraceElement[]{
                new StackTraceElement(TraceKit.class.getName(), "b", "c", 1)}),
            Collections.emptyList()
        );
    }
}
