package tests.base.res;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.system.ResKit;
import space.sunqian.fs.io.IOKit;

import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResTest {

    @Test
    public void testFindResource() throws Exception {
        URL res1 = ResKit.findResource("res/res1.txt");
        assertEquals("res1", IOKit.string(res1.openStream()));
        URL res2 = ResKit.findResource("res/res2.txt");
        assertEquals("res2", IOKit.string(res2.openStream()));
        Set<URL> resSet = ResKit.findResources("res/res1.txt");
        assertEquals(1, resSet.size());
        URL res = resSet.iterator().next();
        assertEquals("res1", IOKit.string(res.openStream()));
        Set<URL> resSet2 = ResKit.findResources("res/res11.txt");
        assertEquals(0, resSet2.size());
        assertNull(ResKit.findResource("res/res11.txt"));
        assertEquals(0, ResKit.findResources("res/res11.txt").size());
    }
}
