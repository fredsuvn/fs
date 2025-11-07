package tests.base.thread;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.base.thread.ThreadKit;
import tests.utils.Utils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadTest {

    @Test
    public void testSleep() {
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
            Thread _this = Thread.currentThread();
            Thread thread = new Thread(() -> {
                Utils.awaitUntilExecuteTo(_this, Thread.class.getName(), "sleep");
                _this.interrupt();
            });
            thread.start();
            try {
                ThreadKit.sleep();
            } catch (AwaitingException e) {
                assertTrue(e.isCausedByInterruption());
            }
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
