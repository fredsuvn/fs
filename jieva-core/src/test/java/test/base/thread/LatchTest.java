package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.CountLatch;
import xyz.sunqian.common.base.thread.ThreadLatch2;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LatchTest {

    @Test
    public void testThreadLatch() throws Exception {
        testThreadLatch(30, ThreadLatch2.newLatch());
        AtomicInteger counter = new AtomicInteger(0);
        testThreadLatch(11, ThreadLatch2.newLatch((l, o) -> {
            assertTrue(o instanceof Integer);
            counter.addAndGet((Integer) o);
        }));
        assertEquals(counter.get(), 11 * 3 + 3);

        {
            // interrupted
            ThreadLatch2<?> latch = ThreadLatch2.newLatch();
            Thread thread1 = new Thread(() -> {
                try {
                    latch.waiter().await();
                } catch (AwaitingException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread1.start();
            thread1.interrupt();
            Thread thread2 = new Thread(() -> {
                try {
                    latch.waiter().await(Duration.ofDays(1));
                } catch (AwaitingException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread2.start();
            thread2.interrupt();
        }

        {
            // unlatch self
            ThreadLatch2<?> latch = ThreadLatch2.newLatch((l, o) -> {
                l.unlatch();
            });
            CountDownLatch cd = new CountDownLatch(1);
            new Thread(() -> {
                try {
                    assertEquals(latch.state(), ThreadLatch2.State.LATCHED);
                    latch.waiter().signal(null);
                    assertEquals(latch.state(), ThreadLatch2.State.UNLATCHED);
                    cd.countDown();
                } catch (AwaitingException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            }).start();
            cd.await();
            assertEquals(latch.state(), ThreadLatch2.State.UNLATCHED);
        }
    }

    private void testThreadLatch(int threadNum, ThreadLatch2<Object> latch) throws Exception {
        ThreadLatch2.Waiter<Object> waiter = latch.waiter();
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch cd1 = new CountDownLatch(threadNum);
        CountDownLatch cd2 = new CountDownLatch(threadNum);
        CountDownLatch cd3 = new CountDownLatch(threadNum);
        CountDownLatch cd4 = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    assertEquals(waiter.state(), ThreadLatch2.State.LATCHED);
                    sleep();
                    cd1.countDown();
                    sleep();
                    waiter.await();
                    sleep();
                    waiter.signal(1);
                    sleep();
                    assertEquals(waiter.state(), ThreadLatch2.State.UNLATCHED);
                    sleep();
                    count.incrementAndGet();
                    sleep();
                    cd2.countDown();
                    sleep();
                    cd2.await();
                    sleep();
                    latch.latch();
                    sleep();
                    cd3.countDown();
                    sleep();
                    waiter.await();
                    sleep();
                    count.incrementAndGet();
                    sleep();
                    waiter.signal(2);
                    sleep();
                    cd4.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        cd1.await();
        sleep();
        assertEquals(count.get(), 0);
        sleep();
        latch.unlatch();
        sleep();
        cd2.await();
        sleep();
        assertEquals(count.get(), threadNum);
        sleep();
        cd3.await();
        sleep();
        latch.unlatch();
        sleep();
        cd4.await();
        sleep();
        latch.signal(3);
        sleep();
        assertEquals(count.get(), threadNum * 2);
    }

    @Test
    public void testCountLatch() throws Exception {
        testCountLatch(10);

        {
            CountLatch latch = CountLatch.newLatch();
            latch.latch();
            assertEquals(latch.count(), 1);
            latch.unlatch();
            assertEquals(latch.count(), 0);
        }
    }

    private void testCountLatch(int threadNum) throws Exception {
        CountLatch latch = CountLatch.newLatch(threadNum);
        CountLatch.Waiter waiter = latch.waiter();
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch cd1 = new CountDownLatch(threadNum);
        CountDownLatch cd2 = new CountDownLatch(threadNum);
        CountDownLatch cd3 = new CountDownLatch(threadNum);
        CountDownLatch cd4 = new CountDownLatch(threadNum);
        CountDownLatch cd5 = new CountDownLatch(1);
        CountDownLatch cd6 = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    assertEquals(waiter.state(), ThreadLatch2.State.LATCHED);
                    sleep();
                    cd1.countDown();
                    sleep();
                    waiter.await();
                    sleep();
                    assertEquals(waiter.state(), ThreadLatch2.State.UNLATCHED);
                    sleep();
                    count.incrementAndGet();
                    sleep();
                    cd2.countDown();
                    sleep();
                    cd2.await();
                    sleep();
                    waiter.countUp();
                    sleep();
                    cd3.countDown();
                    sleep();
                    waiter.await();
                    sleep();
                    count.incrementAndGet();
                    sleep();
                    cd4.countDown();
                    sleep();
                    cd5.await();
                    sleep();
                    waiter.signal(null);
                    sleep();
                    waiter.signal(Long.valueOf(1L));
                    sleep();
                    cd6.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        cd1.await();
        sleep();
        assertEquals(count.get(), 0);
        sleep();
        for (int i = 0; i < threadNum; i++) {
            latch.countDown();
            sleep();
        }
        cd2.await();
        sleep();
        assertEquals(count.get(), threadNum);
        sleep();
        cd3.await();
        sleep();
        assertEquals(latch.count(), threadNum);
        sleep();
        latch.unlatch();
        sleep();
        cd4.await();
        sleep();
        assertEquals(count.get(), threadNum * 2);
        sleep();
        latch.reset(0);
        sleep();
        cd5.countDown();
        sleep();
        cd6.await();
        sleep();
        assertEquals(latch.count(), threadNum);
    }

    private void sleep() {
        Jie.sleep(JieRandom.nextInt(1, 10));
    }
}
