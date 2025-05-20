package test.work;

import org.testng.annotations.Test;
import test.utils.FlagException;
import test.utils.RejectedExecutor;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.JieThread;
import xyz.sunqian.common.work.RunReceipt;
import xyz.sunqian.common.work.SubmissionException;
import xyz.sunqian.common.work.WorkExecutor;
import xyz.sunqian.common.work.WorkReceipt;
import xyz.sunqian.common.work.WorkState;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ExecutorTest {

    @Test
    public void testExecutor() {
        testExecutor(WorkExecutor.newExecutor(), false);
        testExecutor(WorkExecutor.newScheduler(), true);
        testExecutor(WorkExecutor.newExecutor(1), false);
        testExecutor(WorkExecutor.newExecutor(1, 2), false);
        testExecutor(WorkExecutor.newExecutor(1, 2, 1024), false);
    }

    private void testExecutor(WorkExecutor executor, boolean scheduled) {
        testBasicExecution(executor);
        assertEquals(executor.isScheduled(), scheduled);
        if (executor.isScheduled()) {
            testScheduledExecution(executor);
        } else {
            expectThrows(SubmissionException.class, () -> executor.scheduleAt(() -> {}, Instant.now()));
        }
    }

    private void testBasicExecution(WorkExecutor executor) {
        Latch latch = new Latch();
        AtomicInteger count = new AtomicInteger(0);
        {
            // executor.run
            latch.reset();
            count.set(0);
            executor.run(() -> {
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
        }
        {
            // executor.run callable
            latch.reset();
            count.set(0);
            executor.run(() -> {
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
                return null;
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
        }
        {
            // executor.submit
            latch.reset();
            count.set(0);
            RunReceipt receipt = executor.submit(() -> {
                latch.await1();
                count.incrementAndGet();
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            receipt.await();
            assertEquals(count.get(), 1);
        }
        {
            // executor.submit
            latch.reset();
            count.set(0);
            WorkReceipt<String> receipt = executor.submit(() -> {
                latch.await1();
                count.incrementAndGet();
                return "hello";
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            assertEquals(receipt.await(), "hello");
        }
        {
            // executor.execute all
            int threadNum = 10;
            List<Callable<String>> works = new ArrayList<>(10);
            for (int i = 0; i < threadNum; i++) {
                works.add(() -> "hello");
            }
            List<WorkReceipt<String>> receipts = executor.executeAll(works);
            for (WorkReceipt<String> receipt : receipts) {
                assertEquals(receipt.await(), "hello");
            }
            receipts = executor.executeAll(works, Duration.ofSeconds(1));
            for (WorkReceipt<String> receipt : receipts) {
                assertEquals(receipt.await(), "hello");
            }
        }
        {
            // executor.execute any
            int threadNum = 10;
            List<Callable<String>> works = new ArrayList<>(10);
            for (int i = 0; i < threadNum; i++) {
                works.add(() -> "hello");
            }
            assertEquals(executor.executeAny(works), "hello");
            assertEquals(executor.executeAny(works, Duration.ofSeconds(1)), "hello");
        }
        {
            // exceptions
            expectThrows(SubmissionException.class, () -> executor.run((Runnable) null));
            expectThrows(SubmissionException.class, () -> executor.run((Callable<?>) null));
            expectThrows(SubmissionException.class, () -> executor.submit((Runnable) null));
            expectThrows(SubmissionException.class, () -> executor.submit((Callable<?>) null));
            expectThrows(AwaitingException.class, () -> executor.executeAll(null));
            expectThrows(AwaitingException.class, () -> executor.executeAll(Arrays.asList(null, null)));
            expectThrows(AwaitingException.class, () -> executor.executeAll(null, null));
            expectThrows(AwaitingException.class, () -> executor.executeAny(null));
            expectThrows(AwaitingException.class, () -> executor.executeAny(Arrays.asList(null, null)));
            expectThrows(AwaitingException.class, () -> executor.executeAny(null, null));
            // rejected
            WorkExecutor rejectedExecutor = WorkExecutor.newExecutor(new RejectedExecutor());
            expectThrows(SubmissionException.class, () -> rejectedExecutor.run(() -> null));
        }
    }

    private void testScheduledExecution(WorkExecutor executor) {
        Latch latch = new Latch();
        AtomicInteger count = new AtomicInteger(0);
        {
            // executor.scheduleAt
            latch.reset();
            count.set(0);
            long now = System.currentTimeMillis();
            Date after = new Date(now + 500);
            executor.scheduleAt(() -> {
                long n = System.currentTimeMillis();
                assertTrue(n >= after.getTime());
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
            }, after);
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
        }
        {
            // executor.scheduleAt callable
            latch.reset();
            count.set(0);
            long now = System.currentTimeMillis();
            Date after = new Date(now + 500);
            executor.scheduleAt(() -> {
                long n = System.currentTimeMillis();
                assertTrue(n >= after.getTime());
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
                return null;
            }, after);
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
        }
        {
            // executor.schedule rate
            latch.reset();
            count.set(0);
            RunReceipt receipt = executor.scheduleWithRate(() -> {
                latch.await1();
                int c = count.get();
                if (c >= 2) {
                    latch.countDown2();
                } else {
                    count.incrementAndGet();
                }
            }, Duration.ofSeconds(0), Duration.ofMillis(10));
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 2);
            receipt.cancel();
        }
        {
            // executor.schedule delay
            latch.reset();
            count.set(0);
            RunReceipt receipt = executor.scheduleWithDelay(() -> {
                latch.await1();
                int c = count.get();
                if (c >= 2) {
                    latch.countDown2();
                } else {
                    count.incrementAndGet();
                }
            }, Duration.ofSeconds(0), Duration.ofMillis(10));
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 2);
            receipt.cancel();
        }
        {
            // exceptions
            expectThrows(SubmissionException.class, () -> executor.schedule((Runnable) null, null));
            expectThrows(SubmissionException.class, () -> executor.schedule((Callable<?>) null, null));
            expectThrows(SubmissionException.class, () -> executor.scheduleAt((Runnable) null, (Date) null));
            expectThrows(SubmissionException.class, () -> executor.scheduleAt((Callable<?>) null, (Date) null));
            expectThrows(SubmissionException.class, () -> executor.scheduleAt((Runnable) null, (Instant) null));
            expectThrows(SubmissionException.class, () -> executor.scheduleAt((Callable<?>) null, (Instant) null));
            expectThrows(SubmissionException.class, () -> executor.scheduleWithRate(null, null, null));
            expectThrows(SubmissionException.class, () -> executor.scheduleWithDelay(null, null, null));
        }
    }

    @Test
    public void testClose() {
        Latch latch = new Latch();
        int[] c = {0};
        WorkExecutor executor1 = WorkExecutor.newExecutor(1, 1);
        executor1.run(() -> {
            c[0]++;
            try {
                JieThread.sleep();
            } catch (AwaitingException e) {
                c[0]++;
                latch.countDown1();
            }
        });
        executor1.run(() -> {c[0]++;});
        assertFalse(executor1.isClosed());
        assertFalse(executor1.isTerminated());
        assertEquals(executor1.closeNow().size(), 1);
        latch.await1();
        assertEquals(c[0], 2);
        assertTrue(executor1.isClosed());
        WorkExecutor executor2 = WorkExecutor.newExecutor(1, 1);
        executor2.close();
        assertTrue(executor2.isClosed());
        expectThrows(SubmissionException.class, () -> executor2.run(() -> {}));
    }

    @Test
    public void testAwaiting() {
        Latch latch = new Latch();
        WorkExecutor executor1 = WorkExecutor.newExecutor(1, 1);
        executor1.run(latch::countDown1);
        latch.await1();
        assertFalse(executor1.await(Duration.ofMillis(1)));
        assertFalse(executor1.isTerminated());
        executor1.close();
        assertTrue(executor1.await(Duration.ofMillis(1)));
        assertTrue(executor1.isTerminated());
        // Forever sleep:
        WorkExecutor executor2 = WorkExecutor.newExecutor(1, 1);
        executor2.run(() -> {
            try {
                JieThread.sleep();
            } catch (AwaitingException e) {
                throw new RuntimeException(e);
            }
        });
        latch.reset();
        int[] c = {0};
        assertFalse(executor2.await(Duration.ofMillis(1)));
        Thread waitThread = new Thread(() -> {
            latch.countDown1();
            try {
                executor2.await();
            } catch (AwaitingException e) {
                c[0]++;
                latch.countDown2();
            }
        });
        waitThread.start();
        latch.await1();
        assertEquals(c[0], 0);
        waitThread.interrupt();
        latch.await2();
        assertEquals(c[0], 1);
    }

    @Test
    public void testReceipt() {
        Latch latch = new Latch();
        class LatchPool extends ThreadPoolExecutor {

            public LatchPool() {
                super(
                    0,
                    10,
                    0,
                    TimeUnit.NANOSECONDS,
                    new LinkedBlockingQueue<>()
                );
            }

            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                latch.await1();
            }
        }
        {
            // RunReceipt succeeded
            WorkExecutor executor = WorkExecutor.newExecutor(new LatchPool());
            latch.reset();
            int[] c = {0};
            RunReceipt receipt = executor.submit(() -> {
                c[0]++;
                latch.countDown2();
                latch.await3();
            });
            assertEquals(receipt.getState(), WorkState.WAITING);
            assertEquals(c[0], 0);
            latch.countDown1();
            latch.await2();
            assertEquals(c[0], 1);
            assertEquals(receipt.getState(), WorkState.EXECUTING);
            latch.countDown3();
            receipt.await();
            assertEquals(receipt.getState(), WorkState.SUCCEEDED);
            assertNull(receipt.getException());
        }
        {
            // RunReceipt failed
            WorkExecutor executor = WorkExecutor.newExecutor(new LatchPool());
            latch.reset();
            RuntimeException error = new RuntimeException();
            RunReceipt receipt = executor.submit((Runnable) () -> {
                latch.countDown2();
                latch.await3();
                throw error;
            });
            assertEquals(receipt.getState(), WorkState.WAITING);
            latch.countDown1();
            latch.await2();
            assertEquals(receipt.getState(), WorkState.EXECUTING);
            latch.countDown3();
            receipt.await();
            assertEquals(receipt.getState(), WorkState.FAILED);
            assertSame(receipt.getException(), error);
        }
        {
            // RunReceipt canceled-during
            WorkExecutor executor = WorkExecutor.newExecutor(new LatchPool());
            latch.reset();
            RunReceipt receipt = executor.submit(() -> {
                latch.countDown2();
                latch.await3();
            });
            assertEquals(receipt.getState(), WorkState.WAITING);
            latch.countDown1();
            latch.await2();
            assertEquals(receipt.getState(), WorkState.EXECUTING);
            assertTrue(receipt.cancel());
            assertEquals(receipt.getState(), WorkState.CANCELED_DURING);
            assertNull(receipt.getException());
        }
        {
            // RunReceipt canceled-during
            WorkExecutor executor = WorkExecutor.newExecutor(new LatchPool());
            latch.reset();
            RunReceipt receipt = executor.submit(() -> {});
            assertEquals(receipt.getState(), WorkState.WAITING);
            assertTrue(receipt.cancel());
            latch.countDown1();
            assertEquals(receipt.getState(), WorkState.CANCELED);
            assertNull(receipt.getException());
        }
        {
            // RunReceipt failed -- for await duration
            WorkExecutor executor = WorkExecutor.newExecutor(new LatchPool());
            latch.reset();
            int[] ef = {0};
            RunReceipt receipt = executor.submit((Runnable) () -> {
                latch.countDown2();
                latch.await3();
                throw new FlagException(ef);
            });
            assertEquals(receipt.getState(), WorkState.WAITING);
            latch.countDown1();
            latch.await2();
            assertEquals(receipt.getState(), WorkState.EXECUTING);
            latch.countDown3();
            JieThread.until(() -> {
                try {
                    receipt.await(Duration.ofDays(1));
                    return true;
                } catch (AwaitingException e) {
                    return false;
                }
            });
            assertEquals(receipt.getState(), WorkState.FAILED);
            assertTrue(receipt.getException() instanceof FlagException);
        }
        {
            // RunReceipt awaiting interrupted
            WorkExecutor executor = WorkExecutor.newExecutor(new LatchPool());
            latch.reset();
            RunReceipt receipt = executor.submit(() -> {
                latch.countDown2();
                JieThread.sleep();
            });
            latch.countDown1();
            latch.await2();
            assertEquals(receipt.getState(), WorkState.EXECUTING);
            Thread thread = new Thread(() -> {
                try {
                    receipt.await();
                } catch (AwaitingException e) {
                    assertEquals(receipt.getState(),  WorkState.FAILED);
                    assertEquals(receipt.getException().getClass(), AwaitingException.class);
                }
            });
            thread.start();
            latch.await3();
            thread.interrupt();
            assertEquals(receipt.getState(), WorkState.FAILED);
            //assertTrue(receipt.getException() instanceof FlagException);
        }
    }

    private static final class Latch {

        private @Nonnull CountDownLatch latch1;
        private @Nonnull CountDownLatch latch2;
        private @Nonnull CountDownLatch latch3;

        private Latch() {
            reset();
        }

        public synchronized void reset() {
            latch1 = new CountDownLatch(1);
            latch2 = new CountDownLatch(1);
            latch3 = new CountDownLatch(1);
        }

        public void await1() {
            try {
                latch1.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void await2() {
            try {
                latch2.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void await3() {
            try {
                latch3.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void countDown1() {
            latch1.countDown();
        }

        public void countDown2() {
            latch2.countDown();
        }

        public void countDown3() {
            latch3.countDown();
        }
    }
}
