package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This interface represents the executor for {@link Work}. It provides methods to submit or schedule (if the current
 * implementation supports) works, and executes submitted works. The execution can be immediate or delayed, synchronous
 * or asynchronous, depending on the implementation.
 *
 * @author sunqian
 */
public interface WorkExecutor {

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
    void run(@Nonnull Callable<?> work) throws SubmissionException;

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
     * Submits the given work to this executor, returns a {@link WorkReceipt} for the work.
     *
     * @param work the given work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    <T> @Nonnull WorkReceipt<T> submit(@Nonnull Work<? extends T> work) throws SubmissionException;

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
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<? extends T>> works,
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
    <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<? extends T>> works
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
        @RetainedParam @Nonnull Collection<? extends @Nonnull Work<? extends T>> works,
        @Nonnull Duration duration
    ) throws AwaitingException;

    /**
     * Schedules the given work with a specified delay time from now, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given delay.
     * <p>
     * Note this method requires that the current implementation supports scheduling.
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
     * Note this method requires that the current implementation supports scheduling.
     *
     * @param work  the given work
     * @param delay the specified delay time
     * @param <T>   the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    <T> @Nonnull WorkReceipt<T> schedule(
        @Nonnull Work<? extends T> work,
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
        return schedule(work, Duration.between(Instant.now(), time));
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
        @Nonnull Work<? extends T> work,
        @Nonnull Instant time
    ) throws SubmissionException {
        return schedule(work, Duration.between(Instant.now(), time));
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
        return schedule(work, Duration.between(Instant.now(), time.toInstant()));
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
        @Nonnull Work<? extends T> work,
        @Nonnull Date time
    ) throws SubmissionException {
        return schedule(work, Duration.between(Instant.now(), time.toInstant()));
    }

    /**
     * Schedules the given periodic work that becomes enabled first after the given initial delay, and subsequently with
     * the given period. That is, the executions will commence after {@code initialDelay} then
     * {@code initialDelay + period}, then {@code initialDelay + 2 * period}, and so on.
     * <p>
     * If any execution of the work fails, subsequent executions are suppressed. Otherwise, the work will only terminate
     * via cancellation or termination of the executor. If any execution of this work takes longer than its period, then
     * subsequent executions may start late, but will not concurrently execute.
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
