package test.task;

import org.testng.annotations.Test;
import test.utils.RejectedExecutor;
import test.utils.Utils;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.task.SubmissionException;
import xyz.sunqian.common.task.TaskExecutor;
import xyz.sunqian.common.task.TaskReceipt;
import xyz.sunqian.common.task.TaskState;
import xyz.sunqian.common.task.VoidReceipt;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ExecutorTest {

    @Test
    public void testExecutor() {
        testExecutor(TaskExecutor.newExecutor(), false);
        testExecutor(TaskExecutor.newScheduler(), true);
        testExecutor(TaskExecutor.newExecutor(1), false);
        testExecutor(TaskExecutor.newExecutor(1, 2), false);
        testExecutor(TaskExecutor.newExecutor(1, 2, 1024), false);
    }

    private void testExecutor(TaskExecutor executor, boolean scheduled) {
        testBasicExecution(executor);
        assertEquals(executor.isScheduled(), scheduled);
        if (executor.isScheduled()) {
            testScheduledExecution(executor);
        } else {
            expectThrows(SubmissionException.class, () -> executor.scheduleAt(() -> {
            }, Instant.now()));
        }
    }

    private void testBasicExecution(TaskExecutor executor) {
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
            VoidReceipt receipt = executor.submit(() -> {
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
            TaskReceipt<String> receipt = executor.submit(() -> {
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
            List<TaskReceipt<String>> receipts = executor.executeAll(works);
            for (TaskReceipt<String> receipt : receipts) {
                assertEquals(receipt.await(), "hello");
            }
            receipts = executor.executeAll(works, Duration.ofSeconds(1));
            for (TaskReceipt<String> receipt : receipts) {
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
            TaskExecutor rejectedExecutor = TaskExecutor.newExecutor(new RejectedExecutor());
            expectThrows(SubmissionException.class, () -> rejectedExecutor.run(() -> null));
        }
        {
            // no delay
            if (executor.isScheduled()) {
                assertNotNull(executor.submit(() -> {
                }).getDelay());
            } else {
                assertNull(executor.submit(() -> {
                }).getDelay());
            }
        }
    }

    private void testScheduledExecution(TaskExecutor executor) {
        Latch latch = new Latch();
        AtomicInteger count = new AtomicInteger(0);
        {
            // executor.scheduleAt
            latch.reset();
            count.set(0);
            Instant now = Instant.now();
            Instant time = now.plusMillis(TaskUtil.DELAY_MILLIS);
            executor.scheduleAt(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
            }, time);
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
        }
        {
            // executor.scheduleAt callable
            latch.reset();
            count.set(0);
            Instant now = Instant.now();
            Instant time = now.plusMillis(TaskUtil.DELAY_MILLIS);
            executor.scheduleAt(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
                return null;
            }, time);
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
        }
        {
            // executor.schedule rate
            latch.reset();
            Instant now = Instant.now();
            executor.scheduleWithRate(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown2();
                throw new RuntimeException();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS), Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await2();
        }
        {
            // executor.schedule delay
            latch.reset();
            Instant now = Instant.now();
            executor.scheduleWithDelay(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown2();
                throw new RuntimeException();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS), Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await2();
        }
        {
            // exceptions
            expectThrows(SubmissionException.class, () -> executor.schedule((Runnable) null, null));
            expectThrows(SubmissionException.class, () -> executor.schedule((Callable<?>) null, null));
            expectThrows(SubmissionException.class, () -> executor.scheduleAt((Runnable) null, null));
            expectThrows(SubmissionException.class, () -> executor.scheduleAt((Callable<?>) null, (Instant) null));
            expectThrows(SubmissionException.class, () -> executor.scheduleWithRate(null, null, null));
            expectThrows(SubmissionException.class, () -> executor.scheduleWithDelay(null, null, null));
        }
    }

    @Test
    public void testClose() {
        {
            Latch latch = new Latch();
            int[] c = {0};
            TaskExecutor executor = TaskExecutor.newExecutor(1, 1);
            executor.run(() -> {
                c[0]++;
                latch.countDown1();
                try {
                    Jie.sleep();
                } catch (AwaitingException e) {
                    c[0]++;
                    latch.countDown2();
                }
            });
            executor.run(() -> {
                c[0]++;
            });
            latch.await1();
            assertEquals(c[0], 1);
            assertFalse(executor.isClosed());
            assertFalse(executor.isTerminated());
            assertEquals(executor.closeNow().size(), 1);
            latch.await2();
            assertEquals(c[0], 2);
            assertTrue(executor.isClosed());
        }
        {
            TaskExecutor executor = TaskExecutor.newExecutor(1, 1);
            executor.close();
            assertTrue(executor.isClosed());
            expectThrows(SubmissionException.class, () -> executor.run(() -> {
            }));
        }
    }

    @Test
    public void testAwaiting() {
        {
            // await a while
            Latch latch = new Latch();
            TaskExecutor executor = TaskExecutor.newExecutor(1, 1);
            executor.run(latch::countDown1);
            latch.await1();
            assertFalse(executor.await(1));
            assertFalse(executor.isTerminated());
            executor.close();
            assertTrue(executor.await(Duration.ofMillis(1)));
            assertTrue(executor.isTerminated());
        }
        {
            // await forever
            TaskExecutor executor = TaskExecutor.newExecutor(1, 1);
            executor.run(() -> Jie.sleep());
            assertFalse(executor.await(Duration.ofMillis(1)));
            Thread waitThread = new Thread(executor::await);
            waitThread.start();
            Utils.awaitUntilExecuteTo(waitThread, executor.getClass().getName(), "awaitTermination");
            waitThread.interrupt();
        }
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
            // VoidReceipt succeeded
            TaskExecutor executor = TaskExecutor.newExecutor(new LatchPool());
            latch.reset();
            int[] c = {0};
            VoidReceipt receipt = executor.submit(() -> {
                c[0]++;
                latch.countDown2();
                latch.await3();
            });
            assertEquals(receipt.getState(), TaskState.WAITING);
            assertEquals(c[0], 0);
            latch.countDown1();
            latch.await2();
            assertEquals(c[0], 1);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            latch.countDown3();
            receipt.await();
            assertTrue(receipt.isDone());
            assertFalse(receipt.isCancelled());
            assertEquals(receipt.getState(), TaskState.SUCCEEDED);
            assertNull(receipt.getException());
        }
        {
            // VoidReceipt failed
            TaskExecutor executor = TaskExecutor.newExecutor(new LatchPool());
            latch.reset();
            RuntimeException error = new RuntimeException();
            VoidReceipt receipt = executor.submit((Runnable) () -> {
                latch.countDown2();
                latch.await3();
                throw error;
            });
            assertEquals(receipt.getState(), TaskState.WAITING);
            latch.countDown1();
            latch.await2();
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            latch.countDown3();
            receipt.await();
            assertTrue(receipt.isDone());
            assertFalse(receipt.isCancelled());
            assertEquals(receipt.getState(), TaskState.FAILED);
            assertSame(receipt.getException(), error);
        }
        {
            // VoidReceipt canceled-executing
            TaskExecutor executor = TaskExecutor.newExecutor(new LatchPool());
            latch.reset();
            VoidReceipt receipt = executor.submit(() -> {
                latch.countDown2();
                latch.await3();
            });
            assertEquals(receipt.getState(), TaskState.WAITING);
            latch.countDown1();
            latch.await2();
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            assertTrue(receipt.cancel());
            assertTrue(receipt.isDone());
            assertTrue(receipt.isCancelled());
            assertEquals(receipt.getState(), TaskState.CANCELED_EXECUTING);
            assertNull(receipt.getException());
        }
        {
            // VoidReceipt canceled
            TaskExecutor executor = TaskExecutor.newExecutor(new LatchPool());
            latch.reset();
            VoidReceipt receipt = executor.submit(() -> {
            });
            assertEquals(receipt.getState(), TaskState.WAITING);
            assertTrue(receipt.cancel());
            latch.countDown1();
            receipt.await();
            assertTrue(receipt.isDone());
            assertTrue(receipt.isCancelled());
            assertEquals(receipt.getState(), TaskState.CANCELED);
            assertNull(receipt.getException());
        }
        {
            // VoidReceipt
            TaskExecutor executor = TaskExecutor.newExecutor();
            VoidReceipt receipt1 = executor.submit(() -> {
            });
            receipt1.await();
            RuntimeException err = new RuntimeException();
            VoidReceipt receipt2 = executor.submit((Runnable) () -> {
                throw err;
            });
            receipt2.await();
            assertSame(receipt2.getException(), err);
            VoidReceipt receipt3 = executor.submit(() -> {
                Jie.sleep();
            });
            try {
                receipt3.await(Duration.ofMillis(1));
            } catch (AwaitingException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
        }
        {
            // TaskReceipt
            TaskExecutor executor = TaskExecutor.newExecutor();
            TaskReceipt<Integer> receipt1 = executor.submit(() -> 111);
            assertEquals(receipt1.await(), 111);
            Exception err = new Exception();
            TaskReceipt<?> receipt2 = executor.submit(() -> {
                throw err;
            });
            assertNull(receipt2.await());
            assertSame(receipt2.getException(), err);
            TaskReceipt<?> receipt3 = executor.submit(() -> {
                Jie.sleep();
                return 1;
            });
            try {
                receipt3.await(Duration.ofMillis(1));
            } catch (AwaitingException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
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
