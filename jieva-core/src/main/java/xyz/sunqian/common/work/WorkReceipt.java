package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This interface represents a receipt for a submitted {@link Work}. It can be used to track the progress and status of
 * the {@link Work}, and retrieve the result of the {@link Work}.
 *
 * @param <T> the type of the result of the work
 * @author sunqian
 */
public interface WorkReceipt<T> extends BaseWorkReceipt {

    /**
     * Blocks current thread until the work is completed or canceled, returns the result.
     *
     * @return the result of the work
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    T await() throws AwaitingException;

    /**
     * Blocks current thread until the work is completed or canceled, or the specified waiting time elapses. Returns the
     * result.
     *
     * @param duration the maximum time to wait
     * @return the result of the work
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    T await(@Nonnull Duration duration) throws AwaitingException;
}
