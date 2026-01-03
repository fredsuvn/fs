package tests.base.thread;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.thread.ThreadKit;

import java.time.Duration;

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
    }
}
