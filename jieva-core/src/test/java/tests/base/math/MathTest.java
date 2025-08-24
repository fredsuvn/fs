package tests.base.math;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.math.MathKit;

import static org.testng.Assert.assertEquals;

public class MathTest {

    @Test
    public void testMaxMin() {
        assertEquals(MathKit.max(1, 2, 3), 3);
        assertEquals(MathKit.max(1, 2, 2), 2);
        assertEquals(MathKit.max(1L, 2L, 3L), 3L);
        assertEquals(MathKit.max(1L, 2L, 2L), 2L);
        assertEquals(MathKit.min(1, 2, 3), 1);
        assertEquals(MathKit.min(2, 2, 3), 2);
        assertEquals(MathKit.min(1L, 2L, 3L), 1L);
        assertEquals(MathKit.min(2L, 2L, 3L), 2L);
    }

    @Test
    public void testIntValue() {
        assertEquals(MathKit.intValue(0), 0);
        assertEquals(MathKit.intValue(1), 1);
        assertEquals(MathKit.intValue(-1), -1);
        assertEquals(MathKit.intValue(Integer.MAX_VALUE), Integer.MAX_VALUE);
        assertEquals(MathKit.intValue(Integer.MIN_VALUE), Integer.MIN_VALUE);
        assertEquals(MathKit.intValue(Integer.MAX_VALUE + 1L), Integer.MAX_VALUE);
        assertEquals(MathKit.intValue(Integer.MIN_VALUE - 1L), Integer.MIN_VALUE);
    }

    @Test
    public void testPortionNum() {
        // int
        assertEquals(MathKit.portionNum(4, 2), 2);
        assertEquals(MathKit.portionNum(5, 2), 3);
        assertEquals(MathKit.portionNum(6, 2), 3);
        assertEquals(MathKit.portionNum(7, 2), 4);
        assertEquals(MathKit.portionNum(7, 3), 3);
        assertEquals(MathKit.portionNum(100, 1), 100);
        assertEquals(MathKit.portionNum(100, 7), 15);
        // long
        assertEquals(MathKit.portionNum(4L, 2L), 2L);
        assertEquals(MathKit.portionNum(5L, 2L), 3L);
        assertEquals(MathKit.portionNum(6L, 2L), 3L);
        assertEquals(MathKit.portionNum(7L, 2L), 4L);
        assertEquals(MathKit.portionNum(7L, 3L), 3L);
        assertEquals(MathKit.portionNum(100L, 1L), 100L);
        assertEquals(MathKit.portionNum(100L, 7L), 15L);
    }

    @Test
    public void testMakeIn() {
        {
            // int
            assertEquals(MathKit.makeIn(1, 0, 2), 1);
            assertEquals(MathKit.makeIn(1, 1, 2), 1);
            assertEquals(MathKit.makeIn(2, 0, 2), 1);
            assertEquals(MathKit.makeIn(3, 0, 2), 1);
            assertEquals(MathKit.makeIn(-3, 0, 2), 0);
        }
        {
            // long
            assertEquals(MathKit.makeIn(1L, 0L, 2L), 1L);
            assertEquals(MathKit.makeIn(1L, 1L, 2L), 1L);
            assertEquals(MathKit.makeIn(2L, 0L, 2L), 1L);
            assertEquals(MathKit.makeIn(3L, 0L, 2L), 1L);
            assertEquals(MathKit.makeIn(-3L, 0L, 2L), 0L);
        }
        {
            // float
            assertEquals(MathKit.makeIn(6.6f, 6.5f, 6.8f), 6.6f);
            assertEquals(MathKit.makeIn(6.6f, 6.6f, 6.8f), 6.6f);
            assertEquals(MathKit.makeIn(6.6f, 6.7f, 6.8f), 6.7f);
            assertEquals(MathKit.makeIn(6.8f, 6.7f, 6.8f), Math.nextDown(6.8f));
            float end = 6.8f;
            float start = Math.nextDown(end);
            assertEquals(MathKit.makeIn(end, start, end), start);
            assertEquals(MathKit.makeIn(end, start, start), Math.nextDown(start));
        }
        {
            // double
            assertEquals(MathKit.makeIn(6.6, 6.5, 6.8), 6.6);
            assertEquals(MathKit.makeIn(6.6, 6.6, 6.8), 6.6);
            assertEquals(MathKit.makeIn(6.6, 6.7, 6.8), 6.7);
            assertEquals(MathKit.makeIn(6.8, 6.7, 6.8), Math.nextDown(6.8));
            float end = 6.8f;
            float start = Math.nextDown(end);
            assertEquals(MathKit.makeIn(end, start, end), start);
            assertEquals(MathKit.makeIn(end, start, start), Math.nextDown(start));
        }
    }
}
