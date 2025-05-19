package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This interface represents the executor for works, which can be represented by {@link Work}, {@link Runnable} and
 * {@link Callable}. It provides methods to submit or schedule (if the current implementation supports) works, and
 * executes submitted works. The execution can be immediate or delayed, synchronous or asynchronous, depending on the
 * implementation.
 *
 * @author sunqian
 */
public interface WorkExecutor {

    /**
     * Returns a new {@link WorkExecutor} which starts a new thread for each new work, and destroys the thread after the
     * work has been done. The returned {@link WorkExecutor} does not support scheduling.
     *
     * @return a new {@link WorkExecutor} which starts a new thread for each new work
     */
    static @Nonnull WorkExecutor newExecutor() {
        return WorkBack.newExecutor(false);
    }

    /**
     * Returns a new {@link WorkExecutor} which starts a new thread for each new work, and destroys the thread after the
     * work has been done. The returned {@link WorkExecutor} supports scheduling.
     *
     * @return a new {@link WorkExecutor} which starts a new thread for each new work, supporting scheduling
     */
    static @Nonnull WorkExecutor newScheduler() {
        return WorkBack.newExecutor(true);
    }

    /**
     * Returns a new {@link WorkExecutor} based on a thread pool (which refers to {@link ThreadPoolExecutor}). The
     * pool's core pool size is specified by {@code coreThreadSize}. The returned {@link WorkExecutor} does not support
     * scheduling.
     *
     * @param coreThreadSize the specified core pool size
     * @return a new {@link WorkExecutor} based on a thread pool
     */
    static @Nonnull WorkExecutor newExecutor(int coreThreadSize) {
        return WorkBack.newExecutor(coreThreadSize, Integer.MAX_VALUE, -1);
    }

    /**
     * Returns a new {@link WorkExecutor} based on a thread pool (which refers to {@link ThreadPoolExecutor}). The
     * pool's core pool size and maximum pool size are specified by {@code coreThreadSize} and {@code maxThreadSize}.
     * The returned {@link WorkExecutor} does not support scheduling.
     *
     * @param coreThreadSize the specified core pool size
     * @param maxThreadSize  the specified maximum pool size
     * @return a new {@link WorkExecutor} based on a thread pool
     */
    static @Nonnull WorkExecutor newExecutor(int coreThreadSize, int maxThreadSize) {
        return WorkBack.newExecutor(coreThreadSize, maxThreadSize, -1);
    }

    /**
     * Returns a new {@link WorkExecutor} based on a thread pool (which refers to {@link ThreadPoolExecutor}). The
     * pool's core pool size, maximum pool size and maximum queue size are specified by {@code coreThreadSize},
     * {@code maxThreadSize} and {@code maxQueueSize}. The returned {@link WorkExecutor} does not support scheduling.
     *
     * @param coreThreadSize the specified core pool size
     * @param maxThreadSize  the specified maximum pool size
     * @param maxQueueSize   the specified maximum queue size
     * @return a new {@link WorkExecutor} based on a thread pool
     */
    static @Nonnull WorkExecutor newExecutor(int coreThreadSize, int maxThreadSize, int maxQueueSize) {
        return WorkBack.newExecutor(coreThreadSize, maxThreadSize, maxQueueSize);
    }

    /**
     * Returns a new {@link WorkExecutor} based on the given {@link ExecutorService}. If the given
     * {@link ExecutorService} is an instance of {@link ScheduledExecutorService}, the returned {@link WorkExecutor}
     * supports scheduling, otherwise not.
     *
     * @param service the given {@link ExecutorService}
     * @return a new {@link WorkExecutor} based on the given {@link ExecutorService}
     */
    static @Nonnull WorkExecutor newExecutor(@Nonnull ExecutorService service) {
        return WorkBack.newExecutor(service);
    }

    /**
     * Submits the given work to this executor. This method does not return a {@link WorkReceipt}.
     *
     * @param work the given work
     * @throws SubmissionException if an error occurs during the submitting
     */
    void run(@Nonnull Runnable work) throws SubmissionException;

    /**
     * Submits the given work to this executor. This method does not return a {@link WorkReceipt}.
     *
     * @param work the given work
     * @throws SubmissionException if an error occurs during the submitting
     */
    default void run(@Nonnull Callable<?> work) throws SubmissionException {
        try {
            run(JieWork.toRunnable(Objects.requireNonNull(work)));
        } catch (SubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new SubmissionException(e);
        }
    }

    /**
     * Submits the given work to this executor, returns a {@link RunReceipt} for the work.
     *
     * @param work the given work
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    @Nonnull
    RunReceipt submit(@Nonnull Runnable work) throws SubmissionException;

    /**
     * Submits the given work to this executor, returns a {@link WorkReceipt} for the work.
     *
     * @param work the given work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    <T> @Nonnull WorkReceipt<T> submit(@Nonnull Callable<? extends T> work) throws SubmissionException;

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
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works
    ) throws AwaitingException;

    /**
     * Executes the given works, returning a list of {@link WorkReceipt} holding their status and results when all works
     * are done ({@link WorkReceipt#isDone()} is {@code true} for each element of the returned list) or the specified
     * waiting time elapses. Upon return, works that have not done are cancelled. Note that '{@code done}' does not
     * necessarily mean {@link WorkState#SUCCEEDED}.
     *
     * @param works    the given works
     * @param duration the maximum time to wait
     * @param <T>      the type of the work result
     * @return a list of {@link WorkReceipt} of the given works, in the same order
     * @throws AwaitingException if an error occurs during the awaiting
     */
    <T> @Nonnull List<@Nonnull WorkReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works,
        @Nonnull Duration duration
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
    <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works
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
    <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works,
        @Nonnull Duration duration
    ) throws AwaitingException;

    /**
     * Closes this executor and returns immediately. The previously submitted works are executed, but no new work will
     * be accepted. Invocation has no additional effect if already closed.
     * <p>
     * NOTE: This method does not wait for previously submitted works to complete execution. Use {@link #await()} or
     * {@link #await(Duration)} to do that.
     */
    void close();

    /**
     * Closes this executor and attempts to stop all actively executing works, halts the processing of waiting works,
     * and returns a list of the works as {@link Runnable} that were awaiting execution. A closed executor no longer
     * accepts new works, and invocation has no additional effect if already closed.
     * <p>
     * NOTE1: This method does not wait for previously submitted works to complete execution. Use {@link #await()} or
     * {@link #await(Duration)} to do that.
     * <p>
     * NOTE2: There are no guarantees beyond best-effort attempts to stop processing actively executing works. For
     * example, typical implementations will cancel via {@link Thread#interrupt}, so any task that fails to respond to
     * interrupts may never terminate.
     *
     * @return a list of works as {@link Runnable} that never commenced execution
     */
    List<Runnable> closeNow();

    /**
     * Returns {@code true} if this executor has been closed.
     *
     * @return {@code true} if this executor has been closed
     */
    boolean isClosed();

    /**
     * Blocks the current thread until all works have been done after a close operation.
     * <p>
     * NOTE: This method should be invoked after a {@link #close()} or {@link #closeNow()} is invoked.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks the current thread until all works have been done after a close operation, or the timeout occurs, or the
     * current thread is interrupted.
     * <p>
     * NOTE: This method should be invoked after a {@link #close()} or {@link #closeNow()} is invoked.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if this executor terminated and {@code false} if the timeout elapsed before termination
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    boolean await(@Nonnull Duration duration) throws AwaitingException;

    /**
     * Returns {@code true} if all works have been done and the executor is closed. Note that this method never returns
     * {@code true} unless either {@link #close()} or {@link #closeNow()} was called first.
     *
     * @return {@code true} if all works have been done and the executor is closed
     */
    boolean isTerminated();

    /**
     * Returns whether the current implementation supports scheduling.
     *
     * @return {@code true} if the current implementation supports scheduling, {@code false} otherwise
     */
    boolean isScheduled();

    /**
     * Schedules the given work with a specified delay time from now, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given delay.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work  the given work
     * @param delay the specified delay time
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    @Nonnull
    RunReceipt schedule(@Nonnull Runnable work, @Nonnull Duration delay) throws SubmissionException;

    /**
     * Schedules the given work with a specified delay time from now, returns a {@link WorkReceipt} for the work. The
     * work becomes enabled after the given delay.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work  the given work
     * @param delay the specified delay time
     * @param <T>   the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    <T> @Nonnull WorkReceipt<T> schedule(
        @Nonnull Callable<? extends T> work,
        @Nonnull Duration delay
    ) throws SubmissionException;

    /**
     * Schedules the given work to be executed at the specified time, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given time.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work the given work
     * @param time the specified time to execute the work
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    default @Nonnull RunReceipt scheduleAt(@Nonnull Runnable work, @Nonnull Instant time) throws SubmissionException {
        try {
            return schedule(work, Duration.between(Instant.now(), time));
        } catch (SubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new SubmissionException(e);
        }
    }

    /**
     * Schedules the given work to be executed at the specified time, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given time.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work the given work
     * @param time the specified time to execute the work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    default <T> @Nonnull WorkReceipt<T> scheduleAt(
        @Nonnull Callable<? extends T> work,
        @Nonnull Instant time
    ) throws SubmissionException {
        try {
            return schedule(work, Duration.between(Instant.now(), time));
        } catch (SubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new SubmissionException(e);
        }
    }

    /**
     * Schedules the given work to be executed at the specified time, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given time.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work the given work
     * @param time the specified time to execute the work
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    default @Nonnull RunReceipt scheduleAt(@Nonnull Runnable work, @Nonnull Date time) throws SubmissionException {
        try {
            return scheduleAt(work, time.toInstant());
        } catch (SubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new SubmissionException(e);
        }
    }

    /**
     * Schedules the given work to be executed at the specified time, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given time.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work the given work
     * @param time the specified time to execute the work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    default <T> @Nonnull WorkReceipt<T> scheduleAt(
        @Nonnull Callable<? extends T> work,
        @Nonnull Date time
    ) throws SubmissionException {
        try {
            return scheduleAt(work, time.toInstant());
        } catch (SubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new SubmissionException(e);
        }
    }

    /**
     * Schedules the given periodic work that becomes enabled first after the given initial delay, and subsequently with
     * the given period. That is, the executions will commence after {@code initialDelay} then
     * {@code initialDelay + period}, then {@code initialDelay + 2 * period}, and so on.
     * <p>
     * If any execution of the work fails, subsequent executions are suppressed. Otherwise, the work will only terminate
     * via cancellation or termination of the executor. If any execution of this work takes longer than its period, then
     * subsequent executions may start late, but will not concurrently execute.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work         the given periodic work
     * @param initialDelay the given initial delay for first execution
     * @param period       the given period between successive executions
     * @return the receipt representing pending completion of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    @Nonnull
    RunReceipt scheduleWithRate(
        @Nonnull Runnable work,
        @Nonnull Duration initialDelay,
        @Nonnull Duration period
    ) throws SubmissionException;

    /**
     * Schedules the given periodic work that becomes enabled first after the given initial delay, and subsequently with
     * the given delay between the termination of one execution and the commencement of the next.
     * <p>
     * If any execution of the work fails, subsequent executions are suppressed. Otherwise, the work will only terminate
     * via cancellation or termination of the executor.
     * <p>
     * NOTE: This method requires that the current implementation supports scheduling.
     *
     * @param work         the given periodic work
     * @param initialDelay the given initial delay for first execution
     * @param delay        the given delay between the termination of one execution and the commencement of the next
     * @return the receipt representing pending completion of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    @Nonnull
    RunReceipt scheduleWithDelay(
        @Nonnull Runnable work,
        @Nonnull Duration initialDelay,
        @Nonnull Duration delay
    ) throws SubmissionException;
}
