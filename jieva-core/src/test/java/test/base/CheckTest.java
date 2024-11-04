package test.base;

import org.testng.annotations.Test;
import test.Log;
import xyz.sunqian.common.base.JieCheck;

import static org.testng.Assert.*;

public class CheckTest {

    @Test
    public void testMakeIn() {
        float f = 6.6f;
        assertEquals(JieCheck.makeIn(f, 6.5f, 6.8f), 6.6f);
        float fd = JieCheck.makeIn(f, 6.5f, 6.6f);
        assertNotEquals(fd, f);
        assertTrue(fd < f);
        Log.log(fd);
        fd = JieCheck.makeIn(f, 6.6f, 6.7f);
        assertEquals(fd, f);
        fd = JieCheck.makeIn(f, 6.7f, 6.8f);
        assertEquals(fd, 6.7f);

        double d = 6.6;
        assertEquals(JieCheck.makeIn(d, 6.5, 6.8), 6.6);
        double dd = JieCheck.makeIn(d, 6.5, 6.6);
        assertNotEquals(dd, d);
        assertTrue(dd < d);
        Log.log(dd);
        dd = JieCheck.makeIn(d, 6.6, 6.7);
        assertEquals(dd, d);
        dd = JieCheck.makeIn(d, 6.7, 6.8);
        assertEquals(dd, 6.7);
    }
}
