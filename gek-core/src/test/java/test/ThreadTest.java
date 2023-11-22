package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.Gek;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {

    @Test
    public void testThread() throws InterruptedException {
        Thread thread = Gek.thread().name("hahaha").task(() -> {
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
        ExecutorService executorService = Gek.threadPool().corePoolSize(10)
            .threadFactory(r -> {
                ai.incrementAndGet();
                return Gek.thread().task(r).build();
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
        ScheduledExecutorService executorService = Gek.scheduledPool().corePoolSize(10)
            .threadFactory(r -> {
                ai.incrementAndGet();
                return Gek.thread().task(r).build();
            })
            .keepAliveTime(Duration.ofMillis(10000)).allowCoreThreadTimeOut(true).build();
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> System.out.println(Thread.currentThread().getName()));
        }
        Assert.assertEquals(ai.get(), 10);
    }
}
