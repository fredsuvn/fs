package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieLog;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class LogTest {

    @Test
    public void testLogger() {
        // system
        JieLog sysLog = JieLog.system();
        doLog(sysLog, JieLog.LEVEL_INFO);
        // custom1
        StringBuilder sb = new StringBuilder();
        JieLog cusLog1 = JieLog.of(JieLog.LEVEL_DEBUG, sb);
        doLog(cusLog1, JieLog.LEVEL_DEBUG);
        System.out.println(sb);
        // custom2
        sb.delete(0, sb.length());
        JieLog cusLog2 = JieLog.of(JieLog.LEVEL_TRACE, sb);
        doLog(cusLog2, JieLog.LEVEL_TRACE);
        System.out.println(sb);
        // custom3
        sb.delete(0, sb.length());
        JieLog cusLog3 = JieLog.of(JieLog.LEVEL_TRACE + 1, sb);
        doLog(cusLog3, JieLog.LEVEL_TRACE + 1);
        System.out.println(sb);
        // custom4
        sb.delete(0, sb.length());
        JieLog cusLog4 = JieLog.of(JieLog.LEVEL_TRACE, new Appendable() {
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

    private void doLog(JieLog logger, int level) {
        assertEquals(level, logger.getLevel());
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        logger.log(JieLog.LEVEL_TRACE, "test trace-level");
        logger.log(JieLog.LEVEL_ERROR, "test error-level");
        logger.log(JieLog.LEVEL_ERROR + 1, "test error+1");
    }
}
