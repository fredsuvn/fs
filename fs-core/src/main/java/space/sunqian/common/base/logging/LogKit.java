package space.sunqian.common.base.logging;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Fs;
import space.sunqian.common.base.date.DateKit;

import java.util.Date;
import java.util.function.Supplier;

/**
 * Utilities for logging.
 *
 * @author sunqian
 */
public class LogKit {

    // Implementation of SimpleLogger

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
            if (level.value < this.level.value) {
                return;
            }
            try {
                StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
                StackTraceElement caller = Fs.nonnull(getCallerTrace(methodName, stackElements), NULL_TRACE);
                appendable.append(DateKit.format(new Date()))
                    .append("[")
                    .append(level.name())
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
                if (
                    getClass().getName().equals(element.getClassName())
                        && methodName.equals(element.getMethodName())
                ) {
                    break;
                }
            }
            if (i < 0 || i + 1 >= stackElements.length) {
                return null;
            }
            return stackElements[i + 1];
        }
    }

    // For lazy toString for logging

    /**
     * Returns an object that encapsulates a specified {@link Supplier}, and {@link Supplier#get()} will only be called
     * when {@link #toString()} of the returned object is called. This class is typically used to reduce the high
     * overhead concatenation operations for log printing.
     *
     * @param supplier the specified {@link Supplier}
     * @return an object that encapsulates the specified {@link Supplier}
     */
    public static @Nonnull Object lazyToString(@Nonnull Supplier<@Nonnull String> supplier) {
        return new LazyToString(supplier);
    }

    private static final class LazyToString {

        private final @Nonnull Supplier<@Nonnull String> supplier;

        private LazyToString(@Nonnull Supplier<@Nonnull String> supplier) {
            this.supplier = supplier;
        }

        @Override
        public @Nonnull String toString() {
            return supplier.get();
        }
    }

    private LogKit() {
    }
}
