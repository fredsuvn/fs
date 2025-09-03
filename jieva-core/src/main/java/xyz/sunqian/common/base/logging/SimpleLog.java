package xyz.sunqian.common.base.logging;

/**
 * Simple logging interface for Jie.
 *
 * @author fredsuvn
 */
public interface SimpleLog {

    /**
     * Log level: TRACE = 1000.
     */
    int LEVEL_TRACE = 1000;

    /**
     * Log level: DEBUG = 2000.
     */
    int LEVEL_DEBUG = 2000;

    /**
     * Log level: INFO = 3000.
     */
    int LEVEL_INFO = 3000;

    /**
     * Log level: WARN = 4000.
     */
    int LEVEL_WARN = 4000;

    /**
     * Log level: TERROR = 5000.
     */
    int LEVEL_ERROR = 5000;

    /**
     * Returns system default log with {@link #LEVEL_INFO} and {@link System#out}.
     *
     * @return system default log with {@link #LEVEL_INFO} and {@link System#out}
     */
    static SimpleLog system() {
        return LoggingBack.DEFAULT;
    }

    /**
     * Returns a new {@link SimpleLog} with specified level and appendable object to be written.
     *
     * @param level      specified level
     * @param appendable appendable object to be written
     * @return a new {@link SimpleLog} with specified level and appendable object to be written
     */
    static SimpleLog of(int level, Appendable appendable) {
        return new LoggingBack.SimpleLogImpl(level, appendable);
    }

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    void trace(Object... message);

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    void debug(Object... message);

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    void info(Object... message);

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    void warn(Object... message);

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    void error(Object... message);

    /**
     * Logs given message with specified level. The message will be output in order of the array.
     *
     * @param level   specified level
     * @param message given message
     */
    void log(int level, Object... message);

    /**
     * Returns level of this log.
     *
     * @return level of this log
     */
    int getLevel();
}
