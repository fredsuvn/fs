package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.List;

/**
 * Base executor interface for task.
 *
 * @author sunqian
 */
public interface BaseExecutor {

    /**
     * Stops this task executor and returns immediately. The previously submitted tasks are executed, but no new task
     * will be accepted. Invocation has no additional effect if the executor has already been shut down.
     * <p>
     * NOTE: This method does not wait for previously submitted tasks to complete execution. Use {@link #await()} or
     * {@link #await(Duration)} to do that.
     */
    void shutdown();

    /**
     * Stops this task executor and attempts to stop all actively executing tasks, halts the processing of waiting
     * tasks, and returns a list of the tasks as {@link Runnable} that were awaiting execution. No new task will be
     * accepted to this executor, and invocation has no additional effect if the executor has already been shut down.
     * <p>
     * NOTE1: This method does not wait for previously submitted tasks to complete execution. Use {@link #await()} or
     * {@link #await(Duration)} to do that.
     * <p>
     * NOTE2: There are no guarantees beyond best-effort attempts to stop processing actively executing tasks. For
     * example, typical implementations will cancel via {@link Thread#interrupt}, so any task that fails to respond to
     * interrupts may never terminate.
     *
     * @return a list of tasks as {@link Runnable} that never commenced execution
     */
    List<Runnable> shutdownNow();

    /**
     * Returns {@code true} if this task executor has been shut down.
     *
     * @return {@code true} if this task executor has been shut down
     */
    boolean isShutdown();

    /**
     * Blocks the current thread until all tasks have been done after a shutdown operation.
     * <p>
     * NOTE: This method should be invoked after a {@link #shutdown()} or {@link #shutdownNow()} is invoked.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks the current thread until all tasks have been done after a shutdown operation, or the timeout occurs, or
     * the current thread is interrupted.
     * <p>
     * NOTE: This method should be invoked after a {@link #shutdown()} or {@link #shutdownNow()} is invoked.
     *
     * @param millis the maximum milliseconds to wait
     * @return {@code true} if this task executor terminated and {@code false} if the timeout elapsed before termination
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    boolean await(long millis) throws AwaitingException;

    /**
     * Blocks the current thread until all tasks have been done after a shutdown operation, or the timeout occurs, or
     * the current thread is interrupted.
     * <p>
     * NOTE: This method should be invoked after a {@link #shutdown()} or {@link #shutdownNow()} is invoked.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if this task executor terminated and {@code false} if the timeout elapsed before termination
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    boolean await(@Nonnull Duration duration) throws AwaitingException;

    /**
     * Returns {@code true} if all tasks have been done and the task executor has been shut down. Note that this method
     * never returns {@code true} unless either {@link #shutdown()} or {@link #shutdownNow()} was called first.
     *
     * @return {@code true} if all tasks have been done and the task executor has been shut down
     */
    boolean isTerminated();
}
