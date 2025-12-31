package tests.base.logging;

import internal.test.ErrorAppender;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.logging.LogKit;
import space.sunqian.common.base.logging.SimpleLogger;
import space.sunqian.common.base.thread.TraceKit;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogTest {

    @Test
    public void testLogger() throws Exception {
        SimpleLogger sysLogger = SimpleLogger.system();
        assertEquals(SimpleLogger.Level.INFO, sysLogger.level());
        testSimpleLogger(sysLogger);
        SimpleLogger cusLogger = SimpleLogger.newLogger(SimpleLogger.Level.TRACE, System.out);
        testSimpleLogger(cusLogger);
        {
            // test unreachable point
            SimpleLogger errLogger = SimpleLogger.newLogger(SimpleLogger.Level.TRACE, new ErrorAppender());
            assertThrows(IllegalStateException.class, () -> errLogger.info("This", " is ", "a fatal message!"));
            Method getCallerTrace = sysLogger.getClass()
                .getDeclaredMethod("getCallerTrace", Method.class, StackTraceElement[].class);
            getCallerTrace.setAccessible(true);
            Method infoMethod = SimpleLogger.class.getMethod("info", Object[].class);
            StackTraceElement caller = (StackTraceElement) getCallerTrace.invoke(
                sysLogger, infoMethod, new StackTraceElement[0]);
            assertEquals(TraceKit.EMPTY_FRAME, caller);
            caller = (StackTraceElement) getCallerTrace.invoke(
                sysLogger, infoMethod, new StackTraceElement[]{
                    new StackTraceElement("space.sunqian.common.base.logging.SimpleLoggerImpl",
                        "info",
                        "ErrorAppender.java", 1)});
            assertEquals(TraceKit.EMPTY_FRAME, caller);
        }
    }

    private void testSimpleLogger(SimpleLogger logger) {
        logger.fatal("This", " is ", "a fatal message!");
        logger.error("This", " is ", "a error message!");
        logger.warn("This", " is ", "a warn message!");
        logger.info("This", " is ", "a info message!");
        logger.debug("This", " is ", "a debug message!");
        logger.trace("This", " is ", "a trace message!");
    }

    @Test
    public void testLazyToString() {
        Object lazyString = LogKit.lazyToString(() -> "hello world");
        assertEquals("hello world", lazyString.toString());
    }
}
