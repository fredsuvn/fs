package space.sunqian.common.base.logging;

import space.sunqian.annotations.Nonnull;

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
        return LogKit.SYSTEM;
    }

    /**
     * Returns a new logger with the given log level and appender.
     *
     * @param level      the given log level
     * @param appendable the given appender
     * @return a new logger with the given log level and appender
     */
    static @Nonnull SimpleLogger newLogger(@Nonnull SimpleLogger.Level level, @Nonnull Appendable appendable) {
        return LogKit.newLogger(level, appendable);
    }

    /**
     * Logs the given message with {@link Level#FATAL} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void fatal(Object... message);

    /**
     * Logs the given message with {@link Level#ERROR} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void error(Object... message);

    /**
     * Logs the given message with {@link Level#WARN} level. The message will be output on a single line in the order of
     * the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void warn(Object... message);

    /**
     * Logs the given message with {@link Level#INFO} level. The message will be output on a single line in the order of
     * the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void info(Object... message);

    /**
     * Logs the given message with {@link Level#DEBUG} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void debug(Object... message);

    /**
     * Logs the given message with {@link Level#TRACE} level. The message will be output on a single line in the order
     * of the array, just like they are joined into one string. Using {@link Object#toString()} to convert the message
     * object to string.
     *
     * @param message the given message
     */
    void trace(Object... message);

    /**
     * Returns the level of this logger.
     *
     * @return the level of this logger
     */
    @Nonnull
    Level level();

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
         * The level for error.
         */
        ERROR(4),

        /**
         * The level for warning.
         */
        WARN(3),

        /**
         * The middle level.
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

        final int value;

        Level(int value) {
            this.value = value;
        }
    }
}
