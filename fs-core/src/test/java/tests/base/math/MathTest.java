package tests.base.math;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.math.MathKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathTest {

    @Test
    public void testMaxMin() {
        assertEquals(3, MathKit.max(1, 2, 3));
        assertEquals(2, MathKit.max(1, 2, 2));
        assertEquals(3L, MathKit.max(1L, 2L, 3L));
        assertEquals(2L, MathKit.max(1L, 2L, 2L));
        assertEquals(1, MathKit.min(1, 2, 3));
        assertEquals(2, MathKit.min(2, 2, 3));
        assertEquals(1L, MathKit.min(1L, 2L, 3L));
        assertEquals(2L, MathKit.min(2L, 2L, 3L));
    }

    @Test
    public void testIntValue() {
        assertEquals(0, MathKit.intValue(0));
        assertEquals(1, MathKit.intValue(1));
        assertEquals(-1, MathKit.intValue(-1));
        assertEquals(Integer.MAX_VALUE, MathKit.intValue(Integer.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, MathKit.intValue(Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, MathKit.intValue(Integer.MAX_VALUE + 1L));
        assertEquals(Integer.MIN_VALUE, MathKit.intValue(Integer.MIN_VALUE - 1L));
    }

    @Test
    public void testPortionNum() {
        // int
        assertEquals(2, MathKit.portionNum(4, 2));
        assertEquals(3, MathKit.portionNum(5, 2));
        assertEquals(3, MathKit.portionNum(6, 2));
        assertEquals(4, MathKit.portionNum(7, 2));
        assertEquals(3, MathKit.portionNum(7, 3));
        assertEquals(100, MathKit.portionNum(100, 1));
        assertEquals(15, MathKit.portionNum(100, 7));
        // long
        assertEquals(2L, MathKit.portionNum(4L, 2L));
        assertEquals(3L, MathKit.portionNum(5L, 2L));
        assertEquals(3L, MathKit.portionNum(6L, 2L));
        assertEquals(4L, MathKit.portionNum(7L, 2L));
        assertEquals(3L, MathKit.portionNum(7L, 3L));
        assertEquals(100L, MathKit.portionNum(100L, 1L));
        assertEquals(15L, MathKit.portionNum(100L, 7L));
    }

    @Test
    public void testMakeIn() {
        {
            // int
            assertEquals(1, MathKit.makeIn(1, 0, 2));
            assertEquals(1, MathKit.makeIn(1, 1, 2));
            assertEquals(1, MathKit.makeIn(2, 0, 2));
            assertEquals(1, MathKit.makeIn(3, 0, 2));
            assertEquals(0, MathKit.makeIn(-3, 0, 2));
        }
        {
            // long
            assertEquals(1L, MathKit.makeIn(1L, 0L, 2L));
            assertEquals(1L, MathKit.makeIn(1L, 1L, 2L));
            assertEquals(1L, MathKit.makeIn(2L, 0L, 2L));
            assertEquals(1L, MathKit.makeIn(3L, 0L, 2L));
            assertEquals(0L, MathKit.makeIn(-3L, 0L, 2L));
        }
        {
            // float
            assertEquals(6.6f, MathKit.makeIn(6.6f, 6.5f, 6.8f));
            assertEquals(6.6f, MathKit.makeIn(6.6f, 6.6f, 6.8f));
            assertEquals(6.7f, MathKit.makeIn(6.6f, 6.7f, 6.8f));
            assertEquals(MathKit.makeIn(6.8f, 6.7f, 6.8f), Math.nextDown(6.8f));
            float end = 6.8f;
            float start = Math.nextDown(end);
            assertEquals(MathKit.makeIn(end, start, end), start);
            assertEquals(MathKit.makeIn(end, start, start), Math.nextDown(start));
        }
        {
            // double
            assertEquals(6.6, MathKit.makeIn(6.6, 6.5, 6.8));
            assertEquals(6.6, MathKit.makeIn(6.6, 6.6, 6.8));
            assertEquals(6.7, MathKit.makeIn(6.6, 6.7, 6.8));
            assertEquals(MathKit.makeIn(6.8, 6.7, 6.8), Math.nextDown(6.8));
            float end = 6.8f;
            float start = Math.nextDown(end);
            assertEquals(MathKit.makeIn(end, start, end), start);
            assertEquals(MathKit.makeIn(end, start, start), Math.nextDown(start));
        }
    }
}
