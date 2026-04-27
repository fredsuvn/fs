package tests.core.base.thread;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.thread.ThreadContext;
import space.sunqian.fs.collect.MapKit;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ContextTest implements TestPrint {

    @Test
    public void testContext() {
        testClear();
        testSetAndGet();
        testGetWithDefault();
        testRemove();
        testContextMap();
        ThreadContext.clear();
    }

    private void testClear() {
        ThreadContext.clear();
        assertNull(ThreadContext.get("1"));
        ThreadContext.set("1", "1");
        assertEquals("1", ThreadContext.get("1"));
        ThreadContext.clear();
        assertNull(ThreadContext.get("1"));
    }

    private void testSetAndGet() {
        ThreadContext.clear();
        ThreadContext.set("1", "1");
        assertEquals("1", ThreadContext.get("1"));
        assertEquals("1", ThreadContext.set("1", "2"));
        assertEquals("2", ThreadContext.get("1"));
        assertNull(ThreadContext.get("2"));
    }

    private void testGetWithDefault() {
        ThreadContext.clear();
        assertEquals("2", ThreadContext.get("2", k -> "2"));
        assertEquals("2", ThreadContext.get("2", k -> "3"));
        assertNull(ThreadContext.get("3", k -> null));
        assertNull(ThreadContext.get("3"));
    }

    private void testRemove() {
        ThreadContext.clear();
        ThreadContext.set("2", "2");
        assertEquals("2", ThreadContext.remove("2"));
    }

    private void testContextMap() {
        ThreadContext.clear();
        ThreadContext.set("1", "1");
        ThreadContext.set("2", "2");
        Map<Object, Object> map1 = ThreadContext.contextMap();
        assertEquals(MapKit.map("1", "1", "2", "2"), map1);
        ThreadContext.clear();
        Map<Object, Object> map2 = ThreadContext.contextMap();
        assertEquals(MapKit.map(), map2);
    }
}
