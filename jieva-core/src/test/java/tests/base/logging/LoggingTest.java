package tests.base.logging;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.logging.LogString;

import static org.testng.Assert.assertEquals;

public class LoggingTest {

    @Test
    public void testLogString() {
        LogString logString = LogString.of(() -> "hello world");
        assertEquals(logString.toString(), "hello world");
    }
}
