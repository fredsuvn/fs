package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * The common task executor, which is used to submit and execute tasks. The execution can be immediate or delayed,
 * synchronous or asynchronous, depending on the implementation. This interface can be used as {@link ExecutorService}
 * via {@link #asExecutorService()}.
 *
 * @author sunqian
 */
public interface CommonExecutor extends BaseExecutor {

    /**
     * Submits the given task to this executor, returns a {@link RunReceipt} for the task.
     *
     * @param task the given task
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    @Nonnull
    RunReceipt submit(@Nonnull Runnable task) throws TaskSubmissionException;

    /**
     * Submits the given task to this executor, returns a {@link CallReceipt} for the task.
     *
     * @param task the given task
     * @param <T>  the type of the task result
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    <T> @Nonnull CallReceipt<T> submit(@Nonnull Callable<? extends T> task) throws TaskSubmissionException;

    /**
     * Submits the given task and awaits it until it is completed.
     *
     * @param task the given task
     * @throws AwaitingException if an error occurs during invocation or invoking
     */
    void execute(@Nonnull Runnable task) throws AwaitingException;

    /**
     * Submits the given task and awaits it until it is completed., returns the result of the task.
     *
     * @param task the given task
     * @param <T>  the type of the task result
     * @return the result of the task
     * @throws AwaitingException if an error occurs during invocation or invoking
     */
    <T> T execute(@Nonnull Callable<? extends T> task) throws AwaitingException;

    /**
     * Returns an {@link ExecutorService} represents this executor. Their content and state are shared, and all
     * behaviors are equivalent.
     *
     * @return an {@link ExecutorService} represents this executor
     */
    @Nonnull
    ExecutorService asExecutorService();
}
