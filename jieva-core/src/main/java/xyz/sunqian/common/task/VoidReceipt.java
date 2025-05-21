package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This interface represents a receipt for a submitted task that has no return value. It can be used to track the
 * progress and status of the task. That is, this a no return version of {@link TaskReceipt}.
 *
 * @author sunqian
 */
public interface VoidReceipt extends BaseTaskReceipt {

    /**
     * Blocks the current thread until the task is completed or canceled.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks the current thread until the task is completed or canceled, or the specified waiting time elapses.
     *
     * @param duration the maximum time to wait
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await(@Nonnull Duration duration) throws AwaitingException;
}
