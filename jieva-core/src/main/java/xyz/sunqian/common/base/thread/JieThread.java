package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.function.BooleanRunnable;

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
     * Executes the given action until it returns {@code true}. Its logic is as follows:
     * <pre>{@code
     * while (true) {
     *     if (action.run()) {
     *         return;
     *     }
     * }
     * }</pre>
     * <p>
     * Note this method may cause high CPU usage. When the action determines to return {@code false}, consider adding
     * some measures (such as sleep the current thread in a very short time) to avoid it.
     *
     * @param action the given action to be executed
     */
    public static void until(@Nonnull BooleanRunnable action) {
        while (true) {
            if (action.run()) {
                return;
            }
        }
    }

    private static final class Sleeper implements AwaitingAdaptor {

        private final static Sleeper INSTANCE = new Sleeper();

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
