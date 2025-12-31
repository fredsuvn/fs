package space.sunqian.common.base.logging;

import space.sunqian.annotations.Nonnull;

import java.time.ZonedDateTime;

/**
 * Represents the content of a simple log message, to provide the necessary information for logging. It is typically
 * used to help implement the {@link SimpleLogger} interface.
 */
public interface SimpleLog {

    /**
     * Creates a new {@link SimpleLog} instance with the given parameters.
     *
     * @param timestamp  the timestamp of the log message
     * @param level      the level of the log message
     * @param message    the message of the log message
     * @param stackTrace the stack trace of the log message
     * @param thread     the thread where the log message is logged
     * @return a new {@link SimpleLog} instance with the given parameters
     */
    static @Nonnull SimpleLog newLog(
        @Nonnull ZonedDateTime timestamp,
        @Nonnull SimpleLogger.Level level,
        @Nonnull Object[] message,
        @Nonnull StackTraceElement[] stackTrace,
        @Nonnull Thread thread
    ) {
        return new SimpleLog() {
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
