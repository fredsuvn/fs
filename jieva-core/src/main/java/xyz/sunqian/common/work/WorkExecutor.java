package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * This interface represents an executor which provides methods to submit executable works, similar to a
 * {@link ExecutorService}. It executes submitted works immediately or in the future, synchronously or asynchronously,
 * depending on the implementation.
 *
 * @author sunqian
 */
public interface WorkExecutor {

    /**
     * Submits the given work to this executor. This method does not return a {@link WorkReceipt}.
     *
     * @param work the given work
     * @throws WorkException if an error occurs during the submitting
     */
    void run(@Nonnull Runnable work) throws WorkException;

    /**
     * Submits the given work to this executor. This method does not return a {@link WorkReceipt}.
     *
     * @param work the given work
     * @throws WorkException if an error occurs during the submitting
     */
    void run(@Nonnull Callable<?> work) throws WorkException;

    /**
     * Submits the given work to this executor. This method does not return a {@link WorkReceipt}.
     *
     * @param work the given work
     * @throws WorkException if an error occurs during the submitting
     */
    void run(@Nonnull Work<?> work) throws WorkException;

    /**
     * Submits the given work to this executor, returns a {@link RunReceipt} for the work.
     *
     * @param work the given work
     * @return the receipt of the work
     * @throws WorkException if an error occurs during the submitting
     */
    RunReceipt submit(@Nonnull Runnable work) throws WorkException;

    /**
     * Submits the given work to this executor, returns a {@link WorkReceipt} for the work.
     *
     * @param work the given work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws WorkException if an error occurs during the submitting
     */
    <T> WorkReceipt<T> submit(@Nonnull Callable<? extends T> work) throws WorkException;

    /**
     * Submits the given work to this executor, returns a {@link WorkReceipt} for the work.
     *
     * @param work the given work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws WorkException if an error occurs during the submitting
     */
    <T> WorkReceipt<T> submit(@Nonnull Work<? extends T> work) throws WorkException;

    /**
     * Executes the given works, returning a list of {@link WorkReceipt} holding their status and results when all works
     * are done ({@link WorkReceipt#isDone()} is {@code true} for each element of the returned list). Note that
     * '{@code done}' does not necessarily mean {@link WorkState#SUCCEEDED}.
     *
     * @param works the given works
     * @param <T>   the type of the work result
     * @return a list of {@link WorkReceipt} of the given works, in the same order
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> @Nonnull List<@Nonnull WorkReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<? extends T>> works
    ) throws AwaitingException;

    /**
     * Executes the given works, returning a list of {@link WorkReceipt} holding their status and results when all works
     * are done ({@link WorkReceipt#isDone()} is {@code true} for each element of the returned list) or the specified
     * waiting time elapses. Upon return, works that have not done are cancelled. Note that '{@code done}' does not
     * necessarily mean {@link WorkState#SUCCEEDED}.
     *
     * @param works   the given works
     * @param timeout the maximum time to wait
     * @param <T>     the type of the work result
     * @return a list of {@link WorkReceipt} of the given works, in the same order
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> @Nonnull List<@Nonnull WorkReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<T>> works,
        @Nonnull Duration timeout
    ) throws AwaitingException;

    /**
     * Executes the given works, returning the result of one that has completed successfully
     * ({@link WorkReceipt#getState()} is {@link WorkState#SUCCEEDED}), if any do. Upon normal or exceptional return,
     * works that have not done are cancelled.
     *
     * @param works the given works
     * @param <T>   the type of the work result
     * @return the result returned by one of the works
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> T invokeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<T>> works
    ) throws AwaitingException;

    /**
     * Executes the given works, returning the result of one that has completed successfully
     * ({@link WorkReceipt#getState()} is {@link WorkState#SUCCEEDED}), if any do before the given timeout elapses. Upon
     * normal or exceptional return, works that have not done are cancelled.
     *
     * @param works    the given works
     * @param duration the maximum time to wait
     * @param <T>      the type of the work result
     * @return the result returned by one of the works
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> T invokeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<T>> works,
        @Nonnull Duration duration
    ) throws AwaitingException;
}
