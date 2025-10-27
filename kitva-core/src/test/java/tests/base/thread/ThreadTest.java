package tests.base.thread;

import org.junit.jupiter.api.Test;
import tests.utils.Utils;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.base.thread.ThreadKit;

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
            Thread thread = new Thread(ThreadKit::sleep);
            thread.start();
            Utils.awaitUntilExecuteTo(thread, Thread.class.getName(), "sleep");
            thread.interrupt();
        }
    }

    @Test
    public void testUntil() {
        int[] i = {0};
        ThreadKit.until(() -> i[0]++ >= 10);
        assertEquals(i[0], 11);
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
