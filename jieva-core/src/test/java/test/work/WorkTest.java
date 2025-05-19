package test.work;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.WrappedException;
import xyz.sunqian.common.work.JieWork;
import xyz.sunqian.common.work.RunReceipt;
import xyz.sunqian.common.work.Work;
import xyz.sunqian.common.work.WorkExecutor;
import xyz.sunqian.common.work.WorkReceipt;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class WorkTest {

    @Test
    public void testWorExecutor() {
        testWorExecutor(WorkExecutor.newExecutor());
    }

    private void testWorExecutor(WorkExecutor executor) {
        Latch latch = new Latch();
        AtomicInteger count = new AtomicInteger(0);
        {
            // executor.run
            latch.reset();
            executor.run(() -> {
                latch.await1();
                count.incrementAndGet();
                latch.countDown2();
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            latch.await2();
            assertEquals(count.get(), 1);
            count.set(0);
        }
        {
            // executor.run callable
            latch.reset();
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
            count.set(0);
        }
        {
            // executor.submit
            latch.reset();
            RunReceipt receipt = executor.submit(() -> {
                latch.await1();
                count.incrementAndGet();
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            receipt.await();
            assertEquals(count.get(), 1);
            count.set(0);
        }
        {
            // executor.submit
            latch.reset();
            WorkReceipt<String> receipt = executor.submit(() -> {
                latch.await1();
                count.incrementAndGet();
                return "hello";
            });
            assertEquals(count.get(), 0);
            latch.countDown1();
            assertEquals(receipt.await(), "hello");
            count.set(0);
        }
    }

    @Test
    public void testToRunnable() {
        int[] i = {0};
        Runnable runnable = JieWork.toRunnable(() -> {
            i[0]++;
            return null;
        });
        assertEquals(i[0], 0);
        runnable.run();
        assertEquals(i[0], 1);
        Work<?> work1 = (Work<Object>) () -> {
            i[0]++;
            return null;
        };
        Runnable workRunnable1 = JieWork.toRunnable(work1);
        workRunnable1.run();
        assertEquals(i[0], 2);
        Callable<?> work2 = (Callable<Object>) () -> {
            throw new InnerException(i);
        };
        Runnable workRunnable2 = JieWork.toRunnable(work2);
        expectThrows(WrappedException.class, workRunnable2::run);
        assertEquals(i[0], 3);
    }

    private static final class Latch {

        private @Nonnull CountDownLatch latch1;
        private @Nonnull CountDownLatch latch2;

        private Latch() {
            reset();
        }

        public synchronized void reset() {
            latch1 = new CountDownLatch(1);
            latch2 = new CountDownLatch(1);
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

        public void countDown1() {
            latch1.countDown();
        }

        public void countDown2() {
            latch2.countDown();
        }
    }

    private static final class InnerException extends Exception {

        private InnerException(int[] i) {
            super();
            i[0]++;
        }
    }
}
