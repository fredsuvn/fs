package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.function.BooleanSupplier;

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
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(long millis) throws AwaitingException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Sleeps the current thread for the specified duration.
     *
     * @param duration the specified duration
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(@Nonnull Duration duration) throws AwaitingException {
        try {
            Thread.sleep(duration.toMillis(), duration.getNano() / 1000);
        } catch (InterruptedException e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Executes the given action until it returns {@code true}.
     *
     * @param action the given action to be executed
     */
    public static void until(@Nonnull BooleanSupplier action) {
        while (true) {
            if (action.getAsBoolean()) {
                return;
            }
        }
    }
}
