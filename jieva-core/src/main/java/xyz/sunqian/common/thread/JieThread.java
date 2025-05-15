package xyz.sunqian.common.thread;

import xyz.sunqian.annotations.Nonnull;

import java.time.Duration;

/**
 * Static utility class for thread.
 *
 * @author sunqian
 */
public class JieThread {

    /**
     * Sleeps the current thread for the specified milliseconds.
     *
     * @param millis the specified milliseconds
     * @throws IllegalArgumentException    if the value of {@code millis} is negative
     * @throws InterruptedRuntimeException if any thread has interrupted the current thread
     */
    public static void sleep(long millis) throws IllegalArgumentException, InterruptedRuntimeException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new InterruptedRuntimeException(e);
        }
    }

    /**
     * Sleeps the current thread for the specified duration.
     *
     * @param duration the specified duration
     * @throws InterruptedRuntimeException if any thread has interrupted the current thread
     */
    public static void sleep(@Nonnull Duration duration) throws InterruptedRuntimeException {
        try {
            Thread.sleep(duration.toMillis(), duration.getNano() / 1000);
        } catch (InterruptedException e) {
            throw new InterruptedRuntimeException(e);
        }
    }
}
