package tests.core.base.res;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.system.ResKit;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResTest {

    @Test
    public void testFindResource() throws Exception {
        // Test findResource method
        testFindResourceMethod();

        // Test findStream method
        testFindStreamMethod();

        // Test findResources method
        testFindResourcesMethod();
    }

    private void testFindResourceMethod() throws Exception {
        // Test finding existing resources
        URL res1 = ResKit.findResource("res/res1.txt");
        assertEquals("res1", IOKit.string(res1.openStream()));

        URL res2 = ResKit.findResource("res/res2.txt");
        assertEquals("res2", IOKit.string(res2.openStream()));

        // Test finding non-existent resource
        assertNull(ResKit.findResource("res/res11.txt"));
    }

    private void testFindStreamMethod() throws Exception {
        // Test finding existing resource streams
        InputStream res1 = ResKit.findStream("res/res1.txt");
        assertEquals("res1", IOKit.string(res1));

        InputStream res2 = ResKit.findStream("res/res2.txt");
        assertEquals("res2", IOKit.string(res2));

        // Test finding non-existent resource stream
        assertNull(ResKit.findStream("res/res11.txt"));
    }

    private void testFindResourcesMethod() throws Exception {
        // Test finding existing resources
        Set<URL> resSet = ResKit.findResources("res/res1.txt");
        assertEquals(1, resSet.size());
        URL res = resSet.iterator().next();
        assertEquals("res1", IOKit.string(res.openStream()));

        // Test finding non-existent resources
        Set<URL> resSet2 = ResKit.findResources("res/res11.txt");
        assertEquals(0, resSet2.size());
        assertEquals(0, ResKit.findResources("res/res11.txt").size());
    }
}
