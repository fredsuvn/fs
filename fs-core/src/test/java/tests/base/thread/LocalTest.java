package tests.base.thread;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.thread.LocalKit;
import space.sunqian.common.collect.MapKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LocalTest implements PrintTest {

    @Test
    public void testLocal() {
        LocalKit.set("1", "1");
        assertEquals("1", LocalKit.get("1"));
        assertEquals("1", LocalKit.set("1", "2"));
        assertEquals("2", LocalKit.get("1"));
        assertNull(LocalKit.get("2"));
        assertEquals("2", LocalKit.get("2", k -> "2"));
        assertEquals("2", LocalKit.get("2", k -> "3"));
        assertEquals(LocalKit.contextMap(), MapKit.map("1", "2", "2", "2"));
        assertNull(LocalKit.get("3", k -> null));
        assertNull(LocalKit.get("3"));
        assertEquals(LocalKit.contextMap(), MapKit.map("1", "2", "2", "2"));
        assertEquals("2", LocalKit.remove("2"));
        assertEquals(LocalKit.contextMap(), MapKit.map("1", "2"));
        LocalKit.clear();
        assertEquals(LocalKit.contextMap(), MapKit.map());
    }
}
