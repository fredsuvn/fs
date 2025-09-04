package tests.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.thread.ThreadContext;
import xyz.sunqian.common.collect.MapKit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class ContextTest {

    @Test
    public void testContext() throws Exception {
        ThreadContext.set("1", "1");
        assertEquals(ThreadContext.get("1"), "1");
        assertEquals(ThreadContext.set("1", "2"), "1");
        assertEquals(ThreadContext.get("1"), "2");
        assertEquals(ThreadContext.asMap(), MapKit.map("1", "2"));
        assertNull(ThreadContext.get("2"));
    }
}
