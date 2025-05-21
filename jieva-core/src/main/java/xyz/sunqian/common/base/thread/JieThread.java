package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
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
    public static void sleep() throws AwaitingException {
        Sleeper.INSTANCE.await();
    }

    /**
     * Sleeps the current thread for the specified milliseconds.
     *
     * @param millis the specified milliseconds
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(long millis) throws AwaitingException {
        Sleeper.INSTANCE.await(millis);
    }

    /**
     * Sleeps the current thread for the specified duration.
     *
     * @param duration the specified duration
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(@Nonnull Duration duration) throws AwaitingException {
        Sleeper.INSTANCE.await(duration);
    }

    /**
     * Executes the given task until it returns {@code true}, and may throw an {@link AwaitingException} if an error
     * occurs while awaiting. Its logic is as follows:
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

    private static final class Sleeper implements AwaitingAdaptor {

        private final static Sleeper INSTANCE = new Sleeper();

        @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
        @Override
        public void awaitInterruptibly() throws Exception {
            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        }

        @Override
        public boolean awaitInterruptibly(long millis) throws Exception {
            Thread.sleep(millis);
            return true;
        }

        @Override
        public boolean awaitInterruptibly(@Nonnull Duration duration) throws Exception {
            Thread.sleep(duration.toMillis(), duration.getNano() / 1000);
            return true;
        }
    }
}
