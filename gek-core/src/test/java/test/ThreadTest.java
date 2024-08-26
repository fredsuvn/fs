package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {

    @Test
    public void testThread() throws InterruptedException {
        Thread thread = Jie.thread().name("hahaha").runnable(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Assert.assertEquals(thread.getName(), "hahaha");
        Assert.assertFalse(thread.isDaemon());
        thread.join();
        Assert.assertFalse(thread.isAlive());
    }

    @Test
    public void testThreadPool() {
        AtomicInteger ai = new AtomicInteger();
        ExecutorService executorService = Jie.threadPool().corePoolSize(10)
            .threadFactory(r -> {
                ai.incrementAndGet();
                return Jie.thread().runnable(r).build();
            })
            .keepAliveTime(Duration.ofMillis(10000)).allowCoreThreadTimeOut(false).build();
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> System.out.println(Thread.currentThread().getName()));
        }
        Assert.assertEquals(ai.get(), 10);
    }

    @Test
    public void testScheduledPool() {
        AtomicInteger ai = new AtomicInteger();
        ScheduledExecutorService executorService = Jie.scheduledPool().corePoolSize(10)
            .threadFactory(r -> {
                ai.incrementAndGet();
                return Jie.thread().runnable(r).build();
            })
            .keepAliveTime(Duration.ofMillis(10000)).allowCoreThreadTimeOut(true).build();
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> System.out.println(Thread.currentThread().getName()));
        }
        Assert.assertEquals(ai.get(), 10);
    }
}
