package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.StackCounter;

import static org.testng.Assert.assertEquals;

public class StackCounterTest {

    @Test
    public void testStackCounter() throws Exception {
        StackCounter counter = new StackCounter();
        testStackCounterValue(counter);
        counter.reset();
        testStackCounterValue(counter);
    }

    private void testStackCounterValue(StackCounter counter) {
        assertEquals(counter.getDepth(), 0);
        assertEquals(counter.getMaxDepth(), 0);
        counter.increment();
        assertEquals(counter.getDepth(), 1);
        assertEquals(counter.getMaxDepth(), 1);
        counter.increment();
        assertEquals(counter.getDepth(), 2);
        assertEquals(counter.getMaxDepth(), 2);
        counter.increment(2);
        assertEquals(counter.getDepth(), 4);
        assertEquals(counter.getMaxDepth(), 4);
        counter.decrement();
        assertEquals(counter.getDepth(), 3);
        assertEquals(counter.getMaxDepth(), 4);
        counter.decrement(2);
        assertEquals(counter.getDepth(), 1);
        assertEquals(counter.getMaxDepth(), 4);
        counter.increment(1);
        assertEquals(counter.getDepth(), 2);
        assertEquals(counter.getMaxDepth(), 4);
        counter.increment();
        assertEquals(counter.getDepth(), 3);
        assertEquals(counter.getMaxDepth(), 4);
        counter.resetDepth();
        assertEquals(counter.getDepth(), 0);
        assertEquals(counter.getMaxDepth(), 4);
    }
}
