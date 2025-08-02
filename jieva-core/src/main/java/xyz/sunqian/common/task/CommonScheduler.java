package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The common task scheduler, which is used to schedule tasks. This interface can be used as
 * {@link ScheduledExecutorService} via {@link #asScheduledExecutorService()}.
 *
 * @author sunqian
 */
public interface CommonScheduler extends BaseExecutor {

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
    RunReceipt scheduleAtRate(
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
     * Returns a {@link ScheduledExecutorService} represents this scheduler. Their content and state are shared, and all
     * behaviors are equivalent.
     *
     * @return a {@link ScheduledExecutorService} represents this scheduler
     */
    @Nonnull
    ScheduledExecutorService asScheduledExecutorService();
}
