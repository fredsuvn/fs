package space.sunqian.common.base.thread;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * Utilities for thread.
 *
 * @author sunqian
 */
public class ThreadKit {

    /**
     * Sleeps the current thread until it is interrupted.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    @SuppressWarnings({
        "InfiniteLoopStatement",
        //"BusyWait"
    })
    public static void sleep() throws AwaitingException {
        String millisStr = System.getProperty(ThreadKit.class.getName() + ".sleep.millis");
        long millis = millisStr == null ? Integer.MAX_VALUE : Long.parseLong(millisStr);
        boolean test = millisStr != null;
        while (true) {
            Kit.uncheck(() -> sleep0(millis, test), AwaitingException::new);
        }
    }

    private static void sleep0(long millis, boolean test) throws Exception {
        if (test) {
            System.setProperty(ThreadKit.class.getName() + ".sleep.wake", "1");
        }
        Thread.sleep(millis);
    }

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
