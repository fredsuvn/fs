package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.thread.JieThread;
import xyz.sunqian.common.base.thread.ThreadGate;

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
        }
        {
            long t1 = System.currentTimeMillis();
            JieThread.sleep(Duration.ofMillis(10));
            long t2 = System.currentTimeMillis();
            assertTrue(t2 - t1 >= 10);
        }
        {
            ThreadGate gate = ThreadGate.newThreadGate();
            Thread thread = new Thread(() -> {
                gate.open();
                JieThread.sleep();
            });
            thread.start();
            gate.await();
            thread.interrupt();
        }
    }

    @Test
    public void testUntil() {
        int[] i = {0};
        JieThread.until(() -> i[0]++ >= 10);
        assertEquals(i[0], 11);
    }
}
