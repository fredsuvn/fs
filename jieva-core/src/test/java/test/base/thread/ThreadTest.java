package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.JieThread;

import java.time.Duration;
import java.util.Objects;

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
            JieThread.until(() -> {
                StackTraceElement[] traceElements = thread.getStackTrace();
                for (StackTraceElement traceElement : traceElements) {
                    if (Objects.equals("sleep", traceElement.getMethodName())
                        && Objects.equals(Thread.class.getName(), traceElement.getClassName())) {
                        return true;
                    }
                }
                return false;
            });
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
