package tests.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.thread.ThreadContext;
import xyz.sunqian.common.collect.MapKit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class ContextTest {

    @Test
    public void testContext() {
        ThreadContext.set("1", "1");
        assertEquals(ThreadContext.get("1"), "1");
        assertEquals(ThreadContext.set("1", "2"), "1");
        assertEquals(ThreadContext.get("1"), "2");
        assertNull(ThreadContext.get("2"));
        assertEquals(ThreadContext.get("2", k -> "2"), "2");
        assertEquals(ThreadContext.get("2", k -> "3"), "2");
        assertEquals(ThreadContext.asMap(), MapKit.map("1", "2", "2", "2"));
        assertNull(ThreadContext.get("3", k -> null));
        assertNull(ThreadContext.get("3"));
        assertEquals(ThreadContext.asMap(), MapKit.map("1", "2", "2", "2"));
    }
}
