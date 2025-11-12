package tests.base.logging;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.logging.SimpleLogger;
import internal.test.ErrorAppender;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoggerTest {

    @Test
    public void testLogger() throws Exception {
        SimpleLogger sysLogger = SimpleLogger.system();
        assertEquals(SimpleLogger.Level.INFO, sysLogger.level());
        sysLogger.fatal("This", " is ", "a fatal message!");
        sysLogger.error("This", " is ", "a error message!");
        sysLogger.warn("This", " is ", "a warn message!");
        sysLogger.info("This", " is ", "a info message!");
        sysLogger.debug("This", " is ", "a debug message!");
        sysLogger.trace("This", " is ", "a trace message!");
        SimpleLogger cusLogger = SimpleLogger.newLogger(SimpleLogger.Level.TRACE, System.out);
        cusLogger.fatal("This", " is ", "a fatal message!");
        cusLogger.error("This", " is ", "a error message!");
        cusLogger.warn("This", " is ", "a warn message!");
        cusLogger.info("This", " is ", "a info message!");
        cusLogger.debug("This", " is ", "a debug message!");
        cusLogger.trace("This", " is ", "a trace message!");
        SimpleLogger errLogger = SimpleLogger.newLogger(SimpleLogger.Level.TRACE, new ErrorAppender());
        assertThrows(IllegalStateException.class, () -> errLogger.info("This", " is ", "a fatal message!"));
        Method getCallerTrace = sysLogger.getClass()
            .getDeclaredMethod("getCallerTrace", String.class, StackTraceElement[].class);
        getCallerTrace.setAccessible(true);
        assertNull(getCallerTrace.invoke(sysLogger, "info", new StackTraceElement[0]));
        assertNull(getCallerTrace.invoke(sysLogger, "info", new StackTraceElement[]{
            new StackTraceElement("space.sunqian.common.base.logging.SimpleLoggerImpl",
                "info",
                "ErrorAppender.java", 1)}
        ));
    }
}
