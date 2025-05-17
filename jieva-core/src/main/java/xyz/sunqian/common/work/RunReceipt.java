package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.concurrent.Future;

/**
 * This interface represents a receipt for a submitted work which has no result. It can be used to track the progress
 * and status of the work, similar to a {@link Future} without any result.
 *
 * @author sunqian
 */
public interface RunReceipt extends BaseWorkReceipt {

    /**
     * Blocks current thread until the work is completed or canceled.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks current thread until the work is completed or canceled, or the specified waiting time elapses.
     *
     * @param duration the maximum time to wait
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await(@Nonnull Duration duration) throws AwaitingException;
}
