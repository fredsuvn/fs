package test.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.thread.InterruptedRuntimeException;
import xyz.sunqian.common.thread.ThreadLatch;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LatchTest {

    @Test
    public void testLatch() throws Exception {
        testLatch(10, ThreadLatch.newLatch());
        AtomicInteger counter = new AtomicInteger(0);
        testLatch(11, ThreadLatch.newLatch(o -> {
            assertTrue(o instanceof Integer);
            counter.addAndGet((Integer) o);
        }));
        assertEquals(counter.get(), 11 * 3);

        {
            // interrupted
            ThreadLatch latch = ThreadLatch.newLatch();
            Thread thread1 = new Thread(() -> {
                try {
                    latch.waiter().await();
                } catch (InterruptedRuntimeException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread1.start();
            thread1.interrupt();
            Thread thread2 = new Thread(() -> {
                try {
                    latch.waiter().await(Duration.ofDays(1));
                } catch (InterruptedRuntimeException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread2.start();
            thread2.interrupt();
        }
    }

    private void testLatch(int threadNum, ThreadLatch latch) throws Exception {
        ThreadLatch.Waiter waiter = latch.waiter();
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch cd1 = new CountDownLatch(threadNum);
        CountDownLatch cd2 = new CountDownLatch(threadNum);
        CountDownLatch cd3 = new CountDownLatch(threadNum);
        CountDownLatch cd4 = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    assertEquals(waiter.state(), ThreadLatch.State.LOCKED);
                    cd1.countDown();
                    waiter.await();
                    waiter.signal(1);
                    assertEquals(waiter.state(), ThreadLatch.State.UNLOCKED);
                    count.incrementAndGet();
                    cd2.countDown();
                    cd2.await();
                    latch.lock();
                    cd3.countDown();
                    waiter.await();
                    count.incrementAndGet();
                    waiter.signal(2);
                    cd4.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        cd1.await();
        assertEquals(count.get(), 0);
        latch.unlock();
        cd2.await();
        assertEquals(count.get(), threadNum);
        cd3.await();
        latch.unlock();
        cd4.await();
        assertEquals(count.get(), threadNum * 2);
    }
}
