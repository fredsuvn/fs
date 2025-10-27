package tests.base.thread;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.thread.ThreadGate;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GateTest {

    @Test
    public void testGate() throws Exception {
        testGate(10);
        testGate(20);
    }

    private void testGate(int threadNum) throws Exception {
        ThreadGate gate = ThreadGate.newThreadGate();
        assertFalse(gate.isOpened());
        assertTrue(gate.isClosed());
        CountDownLatch latch1 = new CountDownLatch(threadNum);
        CountDownLatch latch2 = new CountDownLatch(threadNum);
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                latch1.countDown();
                gate.await();
                count.incrementAndGet();
                latch2.countDown();
            }).start();
        }
        latch1.await();
        assertEquals(count.get(), 0);
        gate.open();
        assertTrue(gate.isOpened());
        assertFalse(gate.isClosed());
        latch2.await();
        assertEquals(count.get(), threadNum);
        gate.close();
        assertFalse(gate.isOpened());
        assertTrue(gate.isClosed());
        assertFalse(gate.await(1));
        gate.open();
        assertTrue(gate.await(Duration.ofMillis(1)));
    }
}
