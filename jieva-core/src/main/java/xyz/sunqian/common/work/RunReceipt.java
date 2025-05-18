package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This interface represents a receipt for a submitted {@link Runnable}. It can be used to track the progress and status
 * of the {@link Runnable}. This interface is the no return version of {@link WorkReceipt}.
 *
 * @author sunqian
 */
public interface RunReceipt extends BaseWorkReceipt {

    /**
     * Blocks the current thread until the work is completed or canceled.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks the current thread until the work is completed or canceled, or the specified waiting time elapses.
     *
     * @param duration the maximum time to wait
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await(@Nonnull Duration duration) throws AwaitingException;
}
