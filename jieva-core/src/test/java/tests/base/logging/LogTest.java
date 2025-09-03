package tests.base.logging;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.logging.SimpleLog;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class LogTest {

    @Test
    public void testLogger() {
        // system
        SimpleLog sysLog = SimpleLog.system();
        doLog(sysLog, SimpleLog.LEVEL_INFO);
        // custom1
        StringBuilder sb = new StringBuilder();
        SimpleLog cusLog1 = SimpleLog.of(SimpleLog.LEVEL_DEBUG, sb);
        doLog(cusLog1, SimpleLog.LEVEL_DEBUG);
        System.out.println(sb);
        // custom2
        sb.delete(0, sb.length());
        SimpleLog cusLog2 = SimpleLog.of(SimpleLog.LEVEL_TRACE, sb);
        doLog(cusLog2, SimpleLog.LEVEL_TRACE);
        System.out.println(sb);
        // custom3
        sb.delete(0, sb.length());
        SimpleLog cusLog3 = SimpleLog.of(SimpleLog.LEVEL_TRACE + 1, sb);
        doLog(cusLog3, SimpleLog.LEVEL_TRACE + 1);
        System.out.println(sb);
        // custom4
        sb.delete(0, sb.length());
        SimpleLog cusLog4 = SimpleLog.of(SimpleLog.LEVEL_TRACE, new Appendable() {
            @Override
            public Appendable append(CharSequence csq) throws IOException {
                throw new IOException();
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                throw new IOException();
            }

            @Override
            public Appendable append(char c) throws IOException {
                throw new IOException();
            }
        });
        expectThrows(IllegalStateException.class, () -> cusLog4.debug("123"));
    }

    private void doLog(SimpleLog logger, int level) {
        assertEquals(level, logger.getLevel());
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        logger.log(SimpleLog.LEVEL_TRACE, "test trace-level");
        logger.log(SimpleLog.LEVEL_ERROR, "test error-level");
        logger.log(SimpleLog.LEVEL_ERROR + 1, "test error+1");
    }
}
