package xyz.sunqian.common.base.logging;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.time.TimeKit;

import java.util.Date;

final class LoggingBack {

    static @Nonnull SimpleLogger SYSTEM = newLogger(SimpleLogger.Level.INFO, System.out);

    static @Nonnull SimpleLogger newLogger(@Nonnull SimpleLogger.Level level, @Nonnull Appendable appendable) {
        return new SimpleLoggerImpl(level, appendable);
    }

    private static final class SimpleLoggerImpl implements SimpleLogger {

        private static final @Nonnull StackTraceElement NULL_TRACE = new StackTraceElement(
            "null", "null", "null", -1);

        private final @Nonnull Level level;
        private final @Nonnull Appendable appendable;

        private SimpleLoggerImpl(@Nonnull Level level, @Nonnull Appendable appendable) {
            this.level = level;
            this.appendable = appendable;
        }

        @Override
        public void fatal(Object... message) {
            log(Level.FATAL, "fatal", message);
        }

        @Override
        public void error(Object... message) {
            log(Level.ERROR, "error", message);
        }

        @Override
        public void warn(Object... message) {
            log(Level.WARN, "warn", message);
        }

        @Override
        public void info(Object... message) {
            log(Level.INFO, "info", message);
        }

        @Override
        public void debug(Object... message) {
            log(Level.DEBUG, "debug", message);
        }

        @Override
        public void trace(Object... message) {
            log(Level.TRACE, "trace", message);
        }

        @Override
        public @Nonnull Level level() {
            return level;
        }

        private void log(Level level, @Nonnull String methodName, Object... message) {
            if (level.levelValue() < this.level.levelValue()) {
                return;
            }
            try {
                StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
                StackTraceElement caller = Jie.nonnull(getCallerTrace(methodName, stackElements), NULL_TRACE);
                appendable.append(TimeKit.format(new Date()))
                    .append("[")
                    .append(level.levelName())
                    .append("]");
                appendable.append("@")
                    .append(caller.getClassName())
                    .append(".")
                    .append(caller.getMethodName())
                    .append("(")
                    .append(String.valueOf(caller.getLineNumber()))
                    .append(")");
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

        private @Nullable StackTraceElement getCallerTrace(
            @Nonnull String methodName,
            @Nonnull StackTraceElement[] stackElements
        ) {
            int i = -1;
            for (StackTraceElement element : stackElements) {
                i++;
                if (getClass().getName().equals(element.getClassName())
                    && methodName.equals(element.getMethodName())) {
                    break;
                }
            }
            if (i < 0 || i + 1 >= stackElements.length) {
                return null;
            }
            return stackElements[i + 1];
        }
    }
}
