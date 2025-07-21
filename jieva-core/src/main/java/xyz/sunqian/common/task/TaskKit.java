package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.WrappedException;

import java.util.concurrent.Callable;

/**
 * Static utility class for task-related.
 *
 * @author sunqian
 */
public class TaskKit {

    /**
     * Returns a {@link Runnable} wraps the given callable.
     *
     * @param callable the given callable
     * @return a {@link Runnable} wraps the given callable
     */
    public static @Nonnull Runnable toRunnable(@Nonnull Callable<?> callable) {
        if (callable instanceof Runnable) {
            return (Runnable) callable;
        }
        return () -> {
            try {
                callable.call();
            } catch (Exception e) {
                throw new WrappedException(e);
            }
        };
    }
}
