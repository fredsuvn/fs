package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.JieThread;

import java.time.Duration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ThreadTest {

    @Test
    public void testSleep() {
        {
            long t1 = System.currentTimeMillis();
            JieThread.sleep(10);
            long t2 = System.currentTimeMillis();
            assertTrue(t2 - t1 >= 10);
            Thread thread = new Thread(() -> {
                try {
                    JieThread.sleep(999999999999999999L);
                } catch (AwaitingException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread.start();
            thread.interrupt();
        }
        {
            long t1 = System.currentTimeMillis();
            JieThread.sleep(Duration.ofMillis(10));
            long t2 = System.currentTimeMillis();
            assertTrue(t2 - t1 >= 10);
            Thread thread = new Thread(() -> {
                try {
                    JieThread.sleep(Duration.ofMillis(999999999999999999L));
                } catch (AwaitingException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread.start();
            thread.interrupt();
        }
    }
}
