package test.base.thread;

import org.testng.annotations.Test;
import test.utils.Utils;
import xyz.sunqian.common.base.exception.AwaitingException;
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
        }
        {
            long t1 = System.currentTimeMillis();
            JieThread.sleep(Duration.ofMillis(10));
            long t2 = System.currentTimeMillis();
            assertTrue(t2 - t1 >= 10);
        }
        {
            Thread thread = new Thread(JieThread::sleep);
            thread.start();
            Utils.awaitUntilExecuteTo(thread, Thread.class.getName(), "sleep");
            thread.interrupt();
        }
    }

    @Test
    public void testUntil() {
        int[] i = {0};
        JieThread.until(() -> i[0]++ >= 10);
        assertEquals(i[0], 11);
        expectThrows(AwaitingException.class, () -> JieThread.until(() -> {
            throw new RuntimeException();
        }));
    }
}
