package test.base;

import org.testng.annotations.Test;
import test.Log;
import xyz.sunqian.common.base.JieMath;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class MathTest {

    @Test
    public void testMakeIn() {
        float f = 6.6f;
        assertEquals(JieMath.makeIn(f, 6.5f, 6.8f), 6.6f);
        float fd = JieMath.makeIn(f, 6.5f, 6.6f);
        assertNotEquals(fd, f);
        assertTrue(fd < f);
        Log.log(fd);
        fd = JieMath.makeIn(f, 6.6f, 6.7f);
        assertEquals(fd, f);
        fd = JieMath.makeIn(f, 6.7f, 6.8f);
        assertEquals(fd, 6.7f);

        double d = 6.6;
        assertEquals(JieMath.makeIn(d, 6.5, 6.8), 6.6);
        double dd = JieMath.makeIn(d, 6.5, 6.6);
        assertNotEquals(dd, d);
        assertTrue(dd < d);
        Log.log(dd);
        dd = JieMath.makeIn(d, 6.6, 6.7);
        assertEquals(dd, d);
        dd = JieMath.makeIn(d, 6.7, 6.8);
        assertEquals(dd, 6.7);
    }
}
