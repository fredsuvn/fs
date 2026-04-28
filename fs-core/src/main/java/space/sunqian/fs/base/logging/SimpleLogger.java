package space.sunqian.fs.base.logging;

import space.sunqian.annotation.Nonnull;

import java.time.ZonedDateTime;

/**
 * A very simple logger interface, using {@link Level} to determine which messages can be output: only messages with
 * higher or the same log level can be output.
 *
 * @author sunqian
 */
public interface SimpleLogger {

    /**
     * Returns a system default logger with level of {@link Level#INFO} and appender of {@link System#out}.
     *
     * @return a system default logger with level of {@link Level#INFO} and appender of {@link System#out}
     */
    static @Nonnull SimpleLogger system() {
        return SimpleLoggerImpl.SYSTEM;
    }

    /**
     * Returns a new logger with the given log level and appender.
     *
     * @param level      the given log level
     * @param appendable the given appender
     * @return a new logger with the given log level and appender
     */
    static @Nonnull SimpleLogger newLogger(@Nonnull SimpleLogger.Level level, @Nonnull Appendable appendable) {
        return new SimpleLoggerImpl(level, appendable);
    }

    /**
     * Creates a new {@link Log} instance with the given parameters.
     *
     * @param timestamp  the timestamp of the log message
     * @param level      the level of the log message
     * @param message    the message of the log message
     * @param stackTrace the stack trace of the log message
     * @param thread     the thread where the log message is logged
     * @return a new {@link Log} instance with the given parameters
     */
    static @Nonnull SimpleLogger.Log newLog(
        @Nonnull ZonedDateTime timestamp,
        @Nonnull SimpleLogger.Level level,
        @Nonnull Object[] message,
        @Nonnull StackTraceElement[] stackTrace,
        @Nonnull Thread thread
    ) {
        return new Log() {
            @Override
            public @Nonnull ZonedDateTime timestamp() {
                return timestamp;
            }

            @Override
            public @Nonnull SimpleLogger.Level level() {
                return level;
            }

            @Override
            public @Nonnull Object[] message() {
                return message;
            }

            @Override
            public @Nonnull StackTraceElement @Nonnull [] stackTrace() {
                return stackTrace;
            }

            @Override
            public @Nonnull Thread thread() {
                return thread;
            }
        };
    }

    /**
     * Logs the given message with {@link Level#FATAL} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void fatal(Object @Nonnull ... message);

    /**
     * Logs the given message with {@link Level#ERROR} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void error(Object @Nonnull ... message);

    /**
     * Logs the given message with {@link Level#WARN} level. The message will be output on a single line in the order of
     * the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void warn(Object @Nonnull ... message);

    /**
     * Logs the given message with {@link Level#INFO} level. The message will be output on a single line in the order of
     * the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void info(Object @Nonnull ... message);

    /**
     * Logs the given message with {@link Level#DEBUG} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void debug(Object @Nonnull ... message);

    /**
     * Logs the given message with {@link Level#TRACE} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void trace(Object @Nonnull ... message);

    /**
     * Returns the level of this logger.
     *
     * @return the level of this logger
     */
    @Nonnull
    Level level();

    /**
     * Represents the log content of the {@link SimpleLogger} interface, to provide the necessary information for
     * logging.
     */
    interface Log {

        /**
         * Returns the timestamp of this log message.
         *
         * @return the timestamp of this log message
         */
        @Nonnull
        ZonedDateTime timestamp();

        /**
         * Returns the level of this log message.
         *
         * @return the level of this log message
         */
        @Nonnull
        SimpleLogger.Level level();

        /**
         * Returns the message of this log message.
         *
         * @return the message of this log message
         */
        @Nonnull
        Object[] message();

        /**
         * Returns the stack trace of this log message.
         *
         * @return the stack trace of this log message
         */
        @Nonnull
        StackTraceElement @Nonnull [] stackTrace();

        /**
         * Returns the thread where this log message is logged.
         *
         * @return the thread where this log message is logged
         */
        @Nonnull
        Thread thread();
    }

    /**
     * Level of {@link SimpleLogger}, including: {@link #FATAL}, {@link #ERROR}, {@link #WARN}, {@link #INFO},
     * {@link #DEBUG}, and {@link #TRACE}, from highest to lowest level.
     */
    enum Level {

        /**
         * The level for fatal error, it is the highest level.
         */
        FATAL(5),

        /**
         * The error level.
         */
        ERROR(4),

        /**
         * The level for warning.
         */
        WARN(3),

        /**
         * The info level.
         */
        INFO(2),

        /**
         * The level for debug.
         */
        DEBUG(1),

        /**
         * The level for tracing, it is the lowest level.
         */
        TRACE(0);;

        private final int value;

        Level(int value) {
            this.value = value;
        }

        /**
         * Returns the value of this level. The more severe, the higher the value (e.g. {@link #FATAL} has the highest
         * value 5, while {@link #TRACE} has the lowest value 0).
         *
         * @return the value of this level
         */
        int value() {
            return value;
        }
    }
}
