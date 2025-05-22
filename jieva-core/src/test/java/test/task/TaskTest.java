package test.task;

import org.testng.annotations.Test;
import test.utils.FlagException;
import xyz.sunqian.common.base.exception.WrappedException;
import xyz.sunqian.common.task.JieTask;
import xyz.sunqian.common.task.SubmissionException;
import xyz.sunqian.common.task.TaskReceipt;
import xyz.sunqian.common.task.VoidReceipt;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class TaskTest {

    @Test
    public void testToRunnable() {
        int[] i = {0};
        Runnable runnable = JieTask.toRunnable(() -> {
            i[0]++;
            return null;
        });
        assertEquals(i[0], 0);
        runnable.run();
        assertEquals(i[0], 1);
        RunnableCall<?> work1 = new RunnableCall<Object>() {
            @Override
            public void run() {
                i[0]++;
            }

            @Override
            public Object call() {
                i[0]++;
                return null;
            }
        };
        Runnable workRunnable1 = JieTask.toRunnable(work1);
        workRunnable1.run();
        assertEquals(i[0], 2);
        Callable<?> work2 = (Callable<Object>) () -> {
            throw new FlagException(i);
        };
        Runnable workRunnable2 = JieTask.toRunnable(work2);
        expectThrows(WrappedException.class, workRunnable2::run);
        assertEquals(i[0], 3);
    }

    @Test
    public void testRun() throws Exception {
        {
            int[] i = {0};
            CountDownLatch latch = new CountDownLatch(1);
            JieTask.run(() -> {
                i[0]++;
                latch.countDown();
            });
            latch.await();
            assertEquals(i[0], 1);
            TaskReceipt<Integer> receipt = JieTask.run(() -> 666);
            assertEquals(receipt.await(), 666);
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            VoidReceipt receipt = JieTask.schedule(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            receipt.await();
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            TaskReceipt<Integer> receipt = JieTask.schedule(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                return 66;
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            assertEquals(receipt.await(), 66);
        }
        {
            Instant now = Instant.now();
            Instant time = now.plusMillis(TaskUtil.DELAY_MILLIS);
            CountDownLatch latch = new CountDownLatch(1);
            VoidReceipt receipt = JieTask.scheduleAt(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
            }, time);
            latch.await();
            receipt.await();
        }
        {
            Instant now = Instant.now();
            Instant time = now.plusMillis(TaskUtil.DELAY_MILLIS);
            CountDownLatch latch = new CountDownLatch(1);
            TaskReceipt<Integer> receipt = JieTask.scheduleAt(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                return 66;
            }, time);
            latch.await();
            assertEquals(receipt.await(), 66);
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            VoidReceipt receipt = JieTask.scheduleWithRate(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                throw new RuntimeException();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS), Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            receipt.await();
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            VoidReceipt receipt = JieTask.scheduleWithDelay(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                throw new RuntimeException();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS), Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            receipt.await();
        }
    }

    @Test
    public void testExceptionConstructors() {
        // SubmissionException
        expectThrows(SubmissionException.class, () -> {
            throw new SubmissionException();
        });
        expectThrows(SubmissionException.class, () -> {
            throw new SubmissionException("");
        });
        expectThrows(SubmissionException.class, () -> {
            throw new SubmissionException("", new RuntimeException());
        });
        expectThrows(SubmissionException.class, () -> {
            throw new SubmissionException(new RuntimeException());
        });
    }

    private interface RunnableCall<T> extends Runnable, Callable<T> {
    }
}
