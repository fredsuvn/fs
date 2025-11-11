package space.sunqian.common.base.thread;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.Kit;
import space.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * Utilities for thread.
 *
 * @author sunqian
 */
public class ThreadKit {

    /**
     * Sleeps the current thread for the specified milliseconds.
     *
     * @param millis the specified milliseconds
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(long millis) throws AwaitingException {
        Kit.uncheck(
            () -> {
                Thread.sleep(millis);
                return null;
            },
            AwaitingException::new
        );
    }

    /**
     * Sleeps the current thread for the specified duration.
     *
     * @param duration the specified duration
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(@Nonnull Duration duration) throws AwaitingException {
        Kit.uncheck(
            () -> {
                Thread.sleep(duration.toMillis(), duration.getNano() / 1000);
                return null;
            },
            AwaitingException::new
        );
    }

    private ThreadKit() {
    }
}
