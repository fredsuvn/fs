package xyz.sunqian.common.base.logging;

import xyz.sunqian.common.base.lang.TraceKit;
import xyz.sunqian.common.base.time.TimeKit;

import java.util.Date;
import java.util.Objects;

final class LoggingBack {

    static final SimpleLog DEFAULT = new SimpleLogImpl(SimpleLog.LEVEL_INFO, System.out);

    static final class SimpleLogImpl implements SimpleLog {

        private final int level;
        private final Appendable appendable;

        SimpleLogImpl(int level, Appendable appendable) {
            this.level = level;
            this.appendable = appendable;
        }

        @Override
        public void trace(Object... message) {
            log0(LEVEL_TRACE, message);
        }

        @Override
        public void debug(Object... message) {
            log0(LEVEL_DEBUG, message);
        }

        @Override
        public void info(Object... message) {
            log0(LEVEL_INFO, message);
        }

        @Override
        public void warn(Object... message) {
            log0(LEVEL_WARN, message);
        }

        @Override
        public void error(Object... message) {
            log0(LEVEL_ERROR, message);
        }

        @Override
        public void log(int level, Object... message) {
            log0(level, message);
        }

        private void log0(int level, Object... message) {
            if (level < this.level) {
                return;
            }
            StackTraceElement trace = TraceKit.findCallerTrace(0, t -> {
                if (!Objects.equals(t.getClassName(), SimpleLogImpl.class.getName())) {
                    return false;
                }
                return Objects.equals(t.getMethodName(), "log")
                    || Objects.equals(t.getMethodName(), "trace")
                    || Objects.equals(t.getMethodName(), "debug")
                    || Objects.equals(t.getMethodName(), "info")
                    || Objects.equals(t.getMethodName(), "warn")
                    || Objects.equals(t.getMethodName(), "error");
            });
            try {
                appendable.append(TimeKit.format(new Date()))
                    .append("[")
                    .append(levelToString(level))
                    .append("]");
                if (trace != null) {
                    appendable.append("@")
                        .append(trace.getClassName())
                        .append(".")
                        .append(trace.getMethodName())
                        .append("(")
                        .append(String.valueOf(trace.getLineNumber()))
                        .append(")");
                }
                Thread thread = Thread.currentThread();
                appendable.append("-")
                    .append("[")
                    .append(thread.getName())
                    .append("]: ");
                for (Object o : message) {
                    appendable.append(String.valueOf(o));
                }
                appendable.append(System.lineSeparator());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        private String levelToString(int level) {
            switch (level) {
                case LEVEL_TRACE:
                    return "TRACE";
                case LEVEL_DEBUG:
                    return "DEBUG";
                case LEVEL_INFO:
                    return "INFO";
                case LEVEL_WARN:
                    return "WARN";
                case LEVEL_ERROR:
                    return "ERROR";
            }
            return String.valueOf(level);
        }
    }
}
