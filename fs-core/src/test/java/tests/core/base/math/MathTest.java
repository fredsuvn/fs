package tests.core.base.math;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.math.MathKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathTest {

    @Test
    public void testMax() {
        // Test int max
        assertEquals(3, MathKit.max(1, 2, 3));
        assertEquals(2, MathKit.max(1, 2, 2));

        // Test long max
        assertEquals(3L, MathKit.max(1L, 2L, 3L));
        assertEquals(2L, MathKit.max(1L, 2L, 2L));
    }

    @Test
    public void testMin() {
        // Test int min
        assertEquals(1, MathKit.min(1, 2, 3));
        assertEquals(2, MathKit.min(2, 2, 3));

        // Test long min
        assertEquals(1L, MathKit.min(1L, 2L, 3L));
        assertEquals(2L, MathKit.min(2L, 2L, 3L));
    }

    @Test
    public void testSafeInt() {
        // Test with normal values
        assertEquals(0, MathKit.safeInt(0));
        assertEquals(1, MathKit.safeInt(1));
        assertEquals(-1, MathKit.safeInt(-1));

        // Test with boundary values
        assertEquals(Integer.MAX_VALUE, MathKit.safeInt(Integer.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, MathKit.safeInt(Integer.MIN_VALUE));

        // Test with overflow values
        assertEquals(Integer.MAX_VALUE, MathKit.safeInt(Integer.MAX_VALUE + 1L));
        assertEquals(Integer.MIN_VALUE, MathKit.safeInt(Integer.MIN_VALUE - 1L));
    }

    @Test
    public void testPortionInt() {
        // Test int portion
        assertEquals(2, MathKit.portion(4, 2));   // Exact division
        assertEquals(3, MathKit.portion(5, 2));   // Rounding up
        assertEquals(3, MathKit.portion(6, 2));   // Exact division
        assertEquals(4, MathKit.portion(7, 2));   // Rounding up
        assertEquals(3, MathKit.portion(7, 3));   // Rounding up
        assertEquals(100, MathKit.portion(100, 1)); // Dividing by 1
        assertEquals(15, MathKit.portion(100, 7));  // Rounding up
    }

    @Test
    public void testPortionLong() {
        // Test long portion
        assertEquals(2L, MathKit.portion(4L, 2L));   // Exact division
        assertEquals(3L, MathKit.portion(5L, 2L));   // Rounding up
        assertEquals(3L, MathKit.portion(6L, 2L));   // Exact division
        assertEquals(4L, MathKit.portion(7L, 2L));   // Rounding up
        assertEquals(3L, MathKit.portion(7L, 3L));   // Rounding up
        assertEquals(100L, MathKit.portion(100L, 1L)); // Dividing by 1
        assertEquals(15L, MathKit.portion(100L, 7L));  // Rounding up
    }

    @Test
    public void testMakeInInt() {
        // Test int makeIn
        assertEquals(1, MathKit.makeIn(1, 0, 2));  // Value within range
        assertEquals(1, MathKit.makeIn(1, 1, 2));  // Value at lower bound
        assertEquals(1, MathKit.makeIn(2, 0, 2));  // Value at upper bound
        assertEquals(1, MathKit.makeIn(3, 0, 2));  // Value above upper bound
        assertEquals(0, MathKit.makeIn(-3, 0, 2)); // Value below lower bound
    }

    @Test
    public void testMakeInLong() {
        // Test long makeIn
        assertEquals(1L, MathKit.makeIn(1L, 0L, 2L));  // Value within range
        assertEquals(1L, MathKit.makeIn(1L, 1L, 2L));  // Value at lower bound
        assertEquals(1L, MathKit.makeIn(2L, 0L, 2L));  // Value at upper bound
        assertEquals(1L, MathKit.makeIn(3L, 0L, 2L));  // Value above upper bound
        assertEquals(0L, MathKit.makeIn(-3L, 0L, 2L)); // Value below lower bound
    }

    @Test
    public void testMakeInFloat() {
        // Test float makeIn
        assertEquals(6.6f, MathKit.makeIn(6.6f, 6.5f, 6.8f));  // Value within range
        assertEquals(6.6f, MathKit.makeIn(6.6f, 6.6f, 6.8f));  // Value at lower bound
        assertEquals(6.7f, MathKit.makeIn(6.6f, 6.7f, 6.8f));  // Value below lower bound
        assertEquals(Math.nextDown(6.8f), MathKit.makeIn(6.8f, 6.7f, 6.8f));  // Value at upper bound

        // Test with edge cases
        float end = 6.8f;
        float start = Math.nextDown(end);
        assertEquals(start, MathKit.makeIn(end, start, end));  // Value at upper bound
        assertEquals(Math.nextDown(start), MathKit.makeIn(end, start, start));  // Value above upper bound
    }

    @Test
    public void testMakeInDouble() {
        // Test double makeIn
        assertEquals(6.6, MathKit.makeIn(6.6, 6.5, 6.8));  // Value within range
        assertEquals(6.6, MathKit.makeIn(6.6, 6.6, 6.8));  // Value at lower bound
        assertEquals(6.7, MathKit.makeIn(6.6, 6.7, 6.8));  // Value below lower bound
        assertEquals(Math.nextDown(6.8), MathKit.makeIn(6.8, 6.7, 6.8));  // Value at upper bound

        // Test with edge cases
        float end = 6.8f;
        float start = Math.nextDown(end);
        assertEquals(start, MathKit.makeIn(end, start, end));  // Value at upper bound
        assertEquals(Math.nextDown(start), MathKit.makeIn(end, start, start));  // Value above upper bound
    }
}
