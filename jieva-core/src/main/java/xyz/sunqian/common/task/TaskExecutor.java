package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This interface represents the executor for tasks, which can be represented by {@link Runnable} or {@link Callable}.
 * It provides methods to submit or schedule (if the current implementation supports) tasks, and executes those
 * submitted tasks. The execution can be immediate or delayed, synchronous or asynchronous, depending on the
 * implementation.
 *
 * @author sunqian
 */
public interface TaskExecutor {

    /**
     * Returns the default {@link TaskExecutor}, which creates a new thread for each task. The way of creating threads
     * depends on the current JVM. In JVMs that support virtual threads, it will create virtual threads.
     *
     * @return the default {@link TaskExecutor}, which creates a new thread for each task
     */
    static @Nonnull TaskExecutor defaultExecutor() {
        return TaskExecutorImpl.DEFAULT_EXECUTOR;
    }

    /**
     * Returns the default scheduled {@link TaskExecutor}, of which core pool size is {@code 0}. The way of creating
     * threads depends on the current JVM. In JVMs that support virtual threads, it will create virtual threads.
     *
     * @return the default scheduled {@link TaskExecutor}, of which core pool size is {@code 0}
     */
    static @Nonnull TaskExecutor defaultScheduler() {
        return TaskExecutorImpl.DEFAULT_SCHEDULER;
    }

    /**
     * Returns a new {@link TaskExecutor}, which creates a new thread for each task. The way of creating threads depends
     * on the current JVM. In JVMs that support virtual threads, it will create virtual threads.
     *
     * @return a new {@link TaskExecutor}, which creates a new thread for each task
     */
    static @Nonnull TaskExecutor newExecutor() {
        return TaskExecutorImpl.newExecutor(false);
    }

    /**
     * Returns a new scheduled {@link TaskExecutor}, of which core pool size is {@code 0}. The way of creating threads
     * depends on the current JVM. In JVMs that support virtual threads, it will create virtual threads.
     *
     * @return a new scheduled {@link TaskExecutor}, of which core pool size is {@code 0}
     */
    static @Nonnull TaskExecutor newScheduler() {
        return TaskExecutorImpl.newExecutor(true);
    }

    /**
     * Returns a new {@link TaskExecutor} based on a thread pool (typically it is {@link ThreadPoolExecutor}). The
     * pool's core pool size is specified by {@code coreThreadSize}, and the maximum pool size and maximum queue size
     * are unlimited. The returned {@link TaskExecutor} does not support scheduling.
     *
     * @param coreThreadSize the specified core pool size
     * @return a new {@link TaskExecutor} based on a thread pool
     * @throws IllegalArgumentException if {@code coreThreadSize < 0}
     */
    static @Nonnull TaskExecutor newExecutor(
        int coreThreadSize
    ) throws IllegalArgumentException {
        return TaskExecutorImpl.newExecutor(coreThreadSize, Integer.MAX_VALUE, -1);
    }

    /**
     * Returns a new {@link TaskExecutor} based on a thread pool (typically it is {@link ThreadPoolExecutor}). The
     * pool's core pool size and maximum pool size are specified by {@code coreThreadSize} and {@code maxThreadSize},
     * and the maximum queue size is unlimited. The returned {@link TaskExecutor} does not support scheduling.
     *
     * @param coreThreadSize the specified core pool size
     * @param maxThreadSize  the specified maximum pool size
     * @return a new {@link TaskExecutor} based on a thread pool
     * @throws IllegalArgumentException if {@code coreThreadSize < 0} or {@code maxThreadSize <= 0} or
     *                                  {@code maxThreadSize < coreThreadSize}
     */
    static @Nonnull TaskExecutor newExecutor(
        int coreThreadSize, int maxThreadSize
    ) throws IllegalArgumentException {
        return TaskExecutorImpl.newExecutor(coreThreadSize, maxThreadSize, -1);
    }

    /**
     * Returns a new {@link TaskExecutor} based on a thread pool (typically it is {@link ThreadPoolExecutor}). The
     * pool's core pool size, maximum pool size and maximum queue size are specified by {@code coreThreadSize},
     * {@code maxThreadSize} and {@code maxQueueSize}. The returned {@link TaskExecutor} does not support scheduling.
     *
     * @param coreThreadSize the specified core pool size
     * @param maxThreadSize  the specified maximum pool size
     * @param maxQueueSize   the specified maximum queue size
     * @return a new {@link TaskExecutor} based on a thread pool
     * @throws IllegalArgumentException if {@code coreThreadSize < 0} or {@code maxThreadSize <= 0} or
     *                                  {@code maxQueueSize < 0} or {@code maxThreadSize < coreThreadSize}
     */
    static @Nonnull TaskExecutor newExecutor(
        int coreThreadSize, int maxThreadSize, int maxQueueSize
    ) throws IllegalArgumentException {
        return TaskExecutorImpl.newExecutor(coreThreadSize, maxThreadSize, maxQueueSize);
    }

    /**
     * Returns a new {@link TaskExecutor} based on the given {@link ExecutorService}. If the given
     * {@link ExecutorService} is an instance of {@link ScheduledExecutorService}, the returned {@link TaskExecutor}
     * supports scheduling, otherwise not.
     *
     * @param service the given {@link ExecutorService}
     * @return a new {@link TaskExecutor} based on the given {@link ExecutorService}
     */
    static @Nonnull TaskExecutor newExecutor(@Nonnull ExecutorService service) {
        return TaskExecutorImpl.newExecutor(service);
    }

    /**
     * Submits the given task to this executor.
     *
     * @param task the given task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    void run(@Nonnull Runnable task) throws TaskSubmissionException;

    /**
     * Submits the given task to this executor.
     *
     * @param task the given task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    default void run(@Nonnull Callable<?> task) throws TaskSubmissionException {
        try {
            run(TaskKit.toRunnable(Objects.requireNonNull(task)));
        } catch (TaskSubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

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
     * Executes the given tasks, returning a list of {@link CallReceipt} holding their status and results when all tasks
     * are done ({@link CallReceipt#isDone()} is {@code true} for each element of the returned list). Note that
     * '{@code done}' does not necessarily mean {@link TaskState#SUCCEEDED}.
     *
     * @param tasks the given tasks
     * @param <T>   the type of the task result
     * @return a list of {@link CallReceipt} of the given tasks, in the same order
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> @Nonnull List<@Nonnull CallReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks
    ) throws AwaitingException;

    /**
     * Executes the given tasks, returning a list of {@link CallReceipt} holding their status and results when all tasks
     * are done ({@link CallReceipt#isDone()} is {@code true} for each element of the returned list) or the specified
     * waiting time elapses. Upon return, tasks that have not done are cancelled. Note that '{@code done}' does not
     * necessarily mean {@link TaskState#SUCCEEDED}.
     *
     * @param tasks    the given tasks
     * @param duration the maximum time to wait
     * @param <T>      the type of the task result
     * @return a list of {@link CallReceipt} of the given tasks, in the same order
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> @Nonnull List<@Nonnull CallReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks,
        @Nonnull Duration duration
    ) throws AwaitingException;

    /**
     * Executes the given tasks, returning the result of one that has completed successfully
     * ({@link CallReceipt#getState()} is {@link TaskState#SUCCEEDED}), if any do. Upon normal or exceptional return,
     * tasks that have not done are cancelled.
     *
     * @param tasks the given tasks
     * @param <T>   the type of the task result
     * @return the result returned by one of the tasks
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks
    ) throws AwaitingException;

    /**
     * Executes the given tasks, returning the result of one that has completed successfully
     * ({@link CallReceipt#getState()} is {@link TaskState#SUCCEEDED}), if any do before the given timeout elapses. Upon
     * normal or exceptional return, tasks that have not done are cancelled.
     *
     * @param tasks    the given tasks
     * @param duration the maximum time to wait
     * @param <T>      the type of the task result
     * @return the result returned by one of the tasks
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks,
        @Nonnull Duration duration
    ) throws AwaitingException;

    /**
     * Stops this executor and returns immediately. The previously submitted tasks are executed, but no new task will be
     * accepted. Invocation has no additional effect if already closed.
     * <p>
     * NOTE: This method does not wait for previously submitted tasks to complete execution. Use {@link #await()} or
     * {@link #await(Duration)} to do that.
     */
    void shutdown();

    /**
     * Stops this executor and attempts to stop all actively executing tasks, halts the processing of waiting tasks, and
     * returns a list of the tasks as {@link Runnable} that were awaiting execution. A closed executor no longer accepts
     * new tasks, and invocation has no additional effect if already closed.
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
     * Returns {@code true} if this executor has been shut down.
     *
     * @return {@code true} if this executor has been shut down
     */
    boolean isShutdown();

    /**
     * Blocks the current thread until all tasks have been done after a close operation.
     * <p>
     * NOTE: This method should be invoked after a {@link #shutdown()} or {@link #shutdownNow()} is invoked.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks the current thread until all tasks have been done after a close operation, or the timeout occurs, or the
     * current thread is interrupted.
     * <p>
     * NOTE: This method should be invoked after a {@link #shutdown()} or {@link #shutdownNow()} is invoked.
     *
     * @param millis the maximum milliseconds to wait
     * @return {@code true} if this executor terminated and {@code false} if the timeout elapsed before termination
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    boolean await(long millis) throws AwaitingException;

    /**
     * Blocks the current thread until all tasks have been done after a close operation, or the timeout occurs, or the
     * current thread is interrupted.
     * <p>
     * NOTE: This method should be invoked after a {@link #shutdown()} or {@link #shutdownNow()} is invoked.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if this executor terminated and {@code false} if the timeout elapsed before termination
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    boolean await(@Nonnull Duration duration) throws AwaitingException;

    /**
     * Returns {@code true} if all tasks have been done and the executor is closed. Note that this method never returns
     * {@code true} unless either {@link #shutdown()} or {@link #shutdownNow()} was called first.
     *
     * @return {@code true} if all tasks have been done and the executor is closed
     */
    boolean isTerminated();

    /**
     * Returns whether the current implementation supports scheduling.
     *
     * @return {@code true} if the current implementation supports scheduling, {@code false} otherwise
     */
    boolean isScheduled();

    /**
     * Schedules the given task with a specified delay time from now, returns a {@link RunReceipt} for the task. The
     * task becomes enabled after the given delay.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param task  the given task
     * @param delay the specified delay time
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    @Nonnull
    RunReceipt schedule(@Nonnull Runnable task, @Nonnull Duration delay) throws TaskSubmissionException;

    /**
     * Schedules the given task with a specified delay time from now, returns a {@link CallReceipt} for the task. The
     * task becomes enabled after the given delay.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param task  the given task
     * @param delay the specified delay time
     * @param <T>   the type of the task result
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    <T> @Nonnull CallReceipt<T> schedule(
        @Nonnull Callable<? extends T> task,
        @Nonnull Duration delay
    ) throws TaskSubmissionException;

    /**
     * Schedules the given task to be executed at the specified time, returns a {@link RunReceipt} for the task. The
     * task becomes enabled after the given time.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param task the given task
     * @param time the specified time to execute the task
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    default @Nonnull RunReceipt scheduleAt(@Nonnull Runnable task, @Nonnull Instant time) throws TaskSubmissionException {
        try {
            Duration diff = Duration.between(Instant.now(), time);
            return schedule(task, diff);
        } catch (TaskSubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    /**
     * Schedules the given task to be executed at the specified time, returns a {@link RunReceipt} for the task. The
     * task becomes enabled after the given time.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param task the given task
     * @param time the specified time to execute the task
     * @param <T>  the type of the task result
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    default <T> @Nonnull CallReceipt<T> scheduleAt(
        @Nonnull Callable<? extends T> task,
        @Nonnull Instant time
    ) throws TaskSubmissionException {
        try {
            Duration diff = Duration.between(Instant.now(), time);
            return schedule(task, diff);
        } catch (TaskSubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    /**
     * Schedules the given periodic task that executes first after the given initial delay, and subsequently with the
     * given period. That is, the executions will start after {@code initialDelay} then {@code initialDelay + period},
     * then {@code initialDelay + 2 * period}, and so on.
     * <p>
     * If any execution of the task fails, subsequent executions are suppressed. Otherwise, the task will only terminate
     * via cancellation or termination of the executor. If any execution of this task takes longer than its period, then
     * subsequent executions may start late, but will not concurrently execute.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param task         the given periodic task
     * @param initialDelay the given initial delay for first execution
     * @param period       the given period between successive executions
     * @return the receipt of the periodic task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    @Nonnull
    RunReceipt scheduleWithRate(
        @Nonnull Runnable task,
        @Nonnull Duration initialDelay,
        @Nonnull Duration period
    ) throws TaskSubmissionException;

    /**
     * Schedules the given periodic task that executes first after the given initial delay, and subsequently with the
     * given delay between the termination of one execution and the commencement of the next.
     * <p>
     * If any execution of the task fails, subsequent executions are suppressed. Otherwise, the task will only terminate
     * via cancellation or termination of the executor.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param task         the given periodic task
     * @param initialDelay the given initial delay for first execution
     * @param delay        the given delay between the termination of one execution and the commencement of the next
     * @return the receipt of the periodic task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    @Nonnull
    RunReceipt scheduleWithDelay(
        @Nonnull Runnable task,
        @Nonnull Duration initialDelay,
        @Nonnull Duration delay
    ) throws TaskSubmissionException;

    /**
     * Returns an {@link ExecutorService} represents this {@link TaskExecutor}. Their contents are shared, and all
     * behaviors are equivalent.
     *
     * @return an {@link ExecutorService} represents this {@link TaskExecutor}
     */
    @Nonnull
    ExecutorService asExecutorService();

    /**
     * Returns an {@link ScheduledExecutorService} represents this {@link TaskExecutor}. Their contents are shared, and
     * all behaviors are equivalent.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @return an {@link ScheduledExecutorService} represents this {@link TaskExecutor}
     * @throws UnsupportedOperationException if the current implementation does not support scheduling
     */
    @Nonnull
    ScheduledExecutorService asScheduledExecutorService() throws UnsupportedOperationException;
}
