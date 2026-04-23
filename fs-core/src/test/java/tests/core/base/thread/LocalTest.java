package tests.core.base.thread;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.thread.LocalKit;
import space.sunqian.fs.collect.MapKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LocalTest implements TestPrint {

    @Test
    public void testLocal() {
        testLocalSetAndGet();
        testLocalGetWithDefault();
        testLocalContextMap();
        testLocalRemove();
        testLocalClear();
    }

    private void testLocalSetAndGet() {
        LocalKit.set("1", "1");
        assertEquals("1", LocalKit.get("1"));
        assertEquals("1", LocalKit.set("1", "2"));
        assertEquals("2", LocalKit.get("1"));
        assertNull(LocalKit.get("2"));
    }

    private void testLocalGetWithDefault() {
        assertEquals("2", LocalKit.get("2", k -> "2"));
        assertEquals("2", LocalKit.get("2", k -> "3"));
        assertNull(LocalKit.get("3", k -> null));
        assertNull(LocalKit.get("3"));
    }

    private void testLocalContextMap() {
        assertEquals(LocalKit.contextMap(), MapKit.map("1", "2", "2", "2"));
    }

    private void testLocalRemove() {
        assertEquals("2", LocalKit.remove("2"));
        assertEquals(LocalKit.contextMap(), MapKit.map("1", "2"));
    }

    private void testLocalClear() {
        LocalKit.clear();
        assertEquals(LocalKit.contextMap(), MapKit.map());
    }
}
