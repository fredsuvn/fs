package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This interface represents a receipt for a submitted task. It can be used to track the progress and status of the
 * task, and retrieve the task result.
 *
 * @param <T> the type of the task result
 * @author sunqian
 */
public interface TaskReceipt<T> extends BaseTaskReceipt {

    /**
     * Blocks the current thread until the task is completed or canceled, returns the result.
     *
     * @return the result of the task
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    T await() throws AwaitingException;

    /**
     * Blocks the current thread until the task is completed or canceled, or the specified waiting time elapses. Returns
     * the result.
     *
     * @param duration the maximum time to wait
     * @return the result of the task
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    T await(@Nonnull Duration duration) throws AwaitingException;
}
