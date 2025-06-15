package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.function.BooleanCallable;

import java.time.Duration;

/**
 * Static utility class for thread.
 *
 * @author sunqian
 */
public class JieThread {

    /**
     * Sleeps the current thread until it is interrupted.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void sleep() throws AwaitingException {
        Jie.uncheck(
            () -> {
                while (true) {
                    Thread.sleep(Integer.MAX_VALUE);
                }
            },
            AwaitingException::new
        );
    }

    /**
     * Sleeps the current thread for the specified milliseconds.
     *
     * @param millis the specified milliseconds
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(long millis) throws AwaitingException {
        Jie.uncheck(
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
        Jie.uncheck(
            () -> {
                Thread.sleep(duration.toMillis(), duration.getNano() / 1000);
                return null;
            },
            AwaitingException::new
        );
    }

    /**
     * Executes the given task until it returns {@code true} or throws an exception. The original exception will be
     * wrapped by {@link AwaitingException} then thrown, using {@link AwaitingException#getCause()} can get the original
     * exception. The logic of this method is as follows:
     * <pre>{@code
     * try {
     *     while (true) {
     *         if (task.call()) {
     *             return;
     *         }
     *     }
     * } catch (Exception e) {
     *     throw new AwaitingException(e);
     * }
     * }</pre>
     * <p>
     * Note this method may cause high CPU usage. When the task determines to return {@code false}, consider adding some
     * measures (such as sleep the current thread in a very short time) to avoid it.
     *
     * @param task the given task to be executed
     * @throws AwaitingException if an error occurs while awaiting
     */
    public static void until(@Nonnull BooleanCallable task) throws AwaitingException {
        try {
            while (true) {
                if (task.call()) {
                    return;
                }
            }
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }
}
