package space.sunqian.common.base.logging;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Fs;
import space.sunqian.common.base.exception.UnreachablePointException;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;

/**
 * This is a skeletal implementation of {@link SimpleLogger} to minimize the effort required to implement the interface.
 * The subclasses of this class only need to implement the {@link #log(SimpleLog, Method)} method.
 *
 * @author sunqian
 */
public abstract class AbstractSimpleLogger implements SimpleLogger {

    private final @Nonnull Method FATAL_METHOD = Fs.uncheck(
        () -> AbstractSimpleLogger.class.getMethod("fatal", Object[].class),
        UnreachablePointException::new);
    private final @Nonnull Method ERROR_METHOD = Fs.uncheck(
        () -> AbstractSimpleLogger.class.getMethod("error", Object[].class),
        UnreachablePointException::new);
    private final @Nonnull Method WARN_METHOD = Fs.uncheck(
        () -> AbstractSimpleLogger.class.getMethod("warn", Object[].class),
        UnreachablePointException::new);
    private final @Nonnull Method INFO_METHOD = Fs.uncheck(
        () -> AbstractSimpleLogger.class.getMethod("info", Object[].class),
        UnreachablePointException::new);
    private final @Nonnull Method DEBUG_METHOD = Fs.uncheck(
        () -> AbstractSimpleLogger.class.getMethod("debug", Object[].class),
        UnreachablePointException::new);
    private final @Nonnull Method TRACE_METHOD = Fs.uncheck(
        () -> AbstractSimpleLogger.class.getMethod("trace", Object[].class),
        UnreachablePointException::new);

    private final @Nonnull Level level;

    /**
     * Constructs a new abstract simple logger with the given level. The level can be accessed by {@link #level()}.
     *
     * @param level the level of this logger
     */
    protected AbstractSimpleLogger(@Nonnull Level level) {
        this.level = level;
    }

    @Override
    public void fatal(Object @Nonnull ... message) {
        log(Level.FATAL, FATAL_METHOD, message);
    }

    @Override
    public void error(Object @Nonnull ... message) {
        log(Level.ERROR, ERROR_METHOD, message);
    }

    @Override
    public void warn(Object @Nonnull ... message) {
        log(Level.WARN, WARN_METHOD, message);
    }

    @Override
    public void info(Object @Nonnull ... message) {
        log(Level.INFO, INFO_METHOD, message);
    }

    @Override
    public void debug(Object @Nonnull ... message) {
        log(Level.DEBUG, DEBUG_METHOD, message);
    }

    @Override
    public void trace(Object @Nonnull ... message) {
        log(Level.TRACE, TRACE_METHOD, message);
    }

    @Override
    public @Nonnull Level level() {
        return level;
    }

    private void log(@Nonnull Level level, @Nonnull Method method, Object @Nonnull ... message) {
        SimpleLog log = newLog(level, message);
        if (log == null) {
            return;
        }
        log(log, method);
    }

    /**
     * Logs the given log message with the log method which is the method be called to log the message.
     *
     * @param log    the log message
     * @param method the log method, which is the method be called to log the message, one of
     *               {@link SimpleLogger#fatal(Object...)}, {@link SimpleLogger#error(Object...)},
     *               {@link SimpleLogger#warn(Object...)}, {@link SimpleLogger#info(Object...)},
     *               {@link SimpleLogger#debug(Object...)}, and {@link SimpleLogger#trace(Object...)}.
     */
    protected abstract void log(@Nonnull SimpleLog log, @Nonnull Method method);

    private @Nullable SimpleLog newLog(@Nonnull Level level, Object @Nonnull ... message) {
        if (level.value() < this.level.value()) {
            return null;
        }
        ZonedDateTime now = ZonedDateTime.now();
        Thread currentThread = Thread.currentThread();
        StackTraceElement[] stackElements = currentThread.getStackTrace();
        return SimpleLog.newLog(now, level, message, stackElements, currentThread);
    }
}
