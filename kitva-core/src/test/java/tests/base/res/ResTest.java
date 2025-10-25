package tests.base.res;

import org.testng.annotations.Test;
import space.sunqian.common.base.res.ResKit;
import space.sunqian.common.io.IOKit;

import java.net.URL;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class ResTest {

    @Test
    public void testFindResource() throws Exception {
        URL res1 = ResKit.findResource("res/res1.txt");
        assertEquals(IOKit.string(res1.openStream()), "res1");
        URL res2 = ResKit.findResource("res/res2.txt");
        assertEquals(IOKit.string(res2.openStream()), "res2");
        Set<URL> resSet = ResKit.findResources("res/res1.txt");
        assertEquals(resSet.size(), 1);
        URL res = resSet.iterator().next();
        assertEquals(IOKit.string(res.openStream()), "res1");
        Set<URL> resSet2 = ResKit.findResources("res/res11.txt");
        assertEquals(resSet2.size(), 0);
        assertNull(ResKit.findResource("res/res11.txt"));
        assertEquals(ResKit.findResources("res/res11.txt").size(), 0);
    }
}
