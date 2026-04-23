package tests.core.base.logging;

import internal.utils.ErrorAppender;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.logging.LogKit;
import space.sunqian.fs.base.logging.SimpleLog;
import space.sunqian.fs.base.logging.SimpleLogger;
import space.sunqian.fs.base.thread.TraceKit;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogTest {

    @Test
    public void testSystemLogger() throws Exception {
        SimpleLogger sysLogger = SimpleLogger.system();
        assertEquals(SimpleLogger.Level.INFO, sysLogger.level());
        testLoggerMethods(sysLogger);
    }

    @Test
    public void testCustomLogger() {
        SimpleLogger cusLogger = SimpleLogger.newLogger(SimpleLogger.Level.TRACE, System.out);
        testLoggerMethods(cusLogger);
    }

    @Test
    public void testLoggerErrorHandling() throws Exception {
        // Test logger with error appender
        SimpleLogger errLogger = SimpleLogger.newLogger(SimpleLogger.Level.TRACE, new ErrorAppender());
        assertThrows(IllegalStateException.class, () -> errLogger.info("This", " is ", "a fatal message!"));

        // Test getCallerTrace method
        testCallerTraceMethod();
    }

    private void testCallerTraceMethod() throws Exception {
        SimpleLogger sysLogger = SimpleLogger.system();
        Method getCallerTrace = sysLogger.getClass()
            .getDeclaredMethod("getCallerTrace", Method.class, StackTraceElement[].class);
        getCallerTrace.setAccessible(true);

        Method infoMethod = SimpleLogger.class.getMethod("info", Object[].class);

        // Test with empty stack trace
        StackTraceElement caller = (StackTraceElement) getCallerTrace.invoke(
            sysLogger, infoMethod, new StackTraceElement[0]);
        assertEquals(TraceKit.EMPTY_FRAME, caller);

        // Test with stack trace containing SimpleLoggerImpl
        caller = (StackTraceElement) getCallerTrace.invoke(
            sysLogger, infoMethod, new StackTraceElement[]{
                new StackTraceElement(SimpleLog.class.getPackage().getName() + ".SimpleLoggerImpl",
                    "info",
                    "ErrorAppender.java", 1)});
        assertEquals(TraceKit.EMPTY_FRAME, caller);
    }

    private void testLoggerMethods(SimpleLogger logger) {
        // Test all logger methods
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
