package tests.base.logging;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.logging.LogString;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogStringTest {

    @Test
    public void testLogString() {
        LogString logString = LogString.of(() -> "hello world");
        assertEquals(logString.toString(), "hello world");
    }
}
