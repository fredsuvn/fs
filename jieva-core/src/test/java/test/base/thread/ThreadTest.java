package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.thread.InterruptedRuntimeException;
import xyz.sunqian.common.base.thread.JieThread;

import java.time.Duration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

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
                } catch (InterruptedRuntimeException e) {
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
                } catch (InterruptedRuntimeException e) {
                    assertEquals(e.getCause().getClass(), InterruptedException.class);
                }
            });
            thread.start();
            thread.interrupt();
        }
    }

    @Test
    public void testInterruptedRuntimeException() {
        expectThrows(InterruptedRuntimeException.class, () -> {
            throw new InterruptedRuntimeException("msg");
        });
        expectThrows(InterruptedRuntimeException.class, () -> {
            throw new InterruptedRuntimeException(new InterruptedException());
        });
    }
}
