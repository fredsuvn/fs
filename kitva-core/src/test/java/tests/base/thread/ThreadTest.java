package tests.base.thread;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.base.thread.ThreadKit;
import tests.utils.Utils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadTest {

    @Test
    public void testSleep() throws Exception {
        {
            long t1 = System.currentTimeMillis();
            ThreadKit.sleep(10);
            long t2 = System.currentTimeMillis();
            assertTrue(t2 - t1 >= 10);
        }
        {
            long t1 = System.currentTimeMillis();
            ThreadKit.sleep(Duration.ofMillis(10));
            long t2 = System.currentTimeMillis();
            assertTrue(t2 - t1 >= 10);
        }
        {
            Thread sleep = new Thread(() -> {
                try {
                    ThreadKit.sleep();
                } catch (AwaitingException e) {
                    assertTrue(e.isCausedByInterruption());
                }
            });
            Thread awake = new Thread(() -> {
                Utils.awaitUntilExecuteTo(sleep, Thread.class.getName(), "sleep");
                sleep.interrupt();
            });
            sleep.start();
            awake.start();
            sleep.join();
            awake.join();
        }
        {
            System.setProperty(ThreadKit.class.getName() + ".sleep.millis", "10");
            Thread sleep = new Thread(() -> {
                try {
                    ThreadKit.sleep();
                } catch (AwaitingException e) {
                    assertTrue(e.isCausedByInterruption());
                }
            });
            Thread awake = new Thread(() -> {
                Kit.until(() -> {
                    boolean wake = System.getProperty(ThreadKit.class.getName() + ".sleep.wake") != null;
                    ThreadKit.sleep(10);
                    return wake;
                });
                sleep.interrupt();
            });
            sleep.start();
            awake.start();
            sleep.join();
            awake.join();
        }
    }

    @Test
    public void testUntil() {
        int[] i = {0};
        ThreadKit.until(() -> i[0]++ >= 10);
        assertEquals(11, i[0]);
        RuntimeException cause = new RuntimeException();
        try {
            ThreadKit.until(() -> {
                throw cause;
            });
        } catch (AwaitingException e) {
            assertSame(e.getCause(), cause);
        }
    }
}
