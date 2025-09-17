package tests.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.thread.TraceKit;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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

    @Test
    public void testContext() {
        TraceKit.set("1", "1");
        assertEquals(TraceKit.get("1"), "1");
        assertEquals(TraceKit.set("1", "2"), "1");
        assertEquals(TraceKit.get("1"), "2");
        assertNull(TraceKit.get("2"));
        assertEquals(TraceKit.get("2", k -> "2"), "2");
        assertEquals(TraceKit.get("2", k -> "3"), "2");
        assertEquals(TraceKit.contextMap(), MapKit.map("1", "2", "2", "2"));
        assertNull(TraceKit.get("3", k -> null));
        assertNull(TraceKit.get("3"));
        assertEquals(TraceKit.contextMap(), MapKit.map("1", "2", "2", "2"));
    }
}
