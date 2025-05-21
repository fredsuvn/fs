package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.WrappedException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Static utility class for executing tasks.
 *
 * @author sunqian
 */
public class JieTask {

    /**
     * Returns a {@link Runnable} wraps the given callable.
     *
     * @param callable the given callable
     * @return a {@link Runnable} wraps the given callable
     */
    public static @Nonnull Runnable toRunnable(@Nonnull Callable<?> callable) {
        if (callable instanceof Runnable) {
            return (Runnable) callable;
        }
        return () -> {
            try {
                callable.call();
            } catch (Exception e) {
                throw new WrappedException(e);
            }
        };
    }

    /**
     * Runs the given task asynchronously.
     *
     * @param task the task to run
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static void run(@Nonnull Runnable task) throws SubmissionException {
        ExecutorHolder.executor.run(task);
    }

    /**
     * Runs the given task asynchronously, and returns the receipt of the task.
     *
     * @param task the task to run
     * @param <T>  the type of the task result
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull TaskReceipt<T> run(@Nonnull Callable<? extends T> task) throws SubmissionException {
        return ExecutorHolder.executor.submit(task);
    }

    /**
     * Schedules the given task with a specified delay time from now, returns a {@link VoidReceipt} for the task. The
     * task becomes enabled after the given delay.
     *
     * @param task  the given task
     * @param delay the specified delay time
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt schedule(@Nonnull Runnable task, @Nonnull Duration delay) throws SubmissionException {
        return ScheduleHolder.executor.schedule(task, delay);
    }

    /**
     * Schedules the given task with a specified delay time from now, returns a {@link TaskReceipt} for the task. The
     * task becomes enabled after the given delay.
     *
     * @param task  the given task
     * @param delay the specified delay time
     * @param <T>   the type of the task result
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull TaskReceipt<T> schedule(
        @Nonnull Callable<? extends T> task,
        @Nonnull Duration delay
    ) throws SubmissionException {
        return ScheduleHolder.executor.schedule(task, delay);
    }

    /**
     * Schedules the given task to be executed at the specified time, returns a {@link VoidReceipt} for the task. The
     * task becomes enabled after the given time.
     *
     * @param task the given task
     * @param time the specified time to execute the task
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt scheduleAt(
        @Nonnull Runnable task, @Nonnull Instant time
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleAt(task, time);
    }

    /**
     * Schedules the given task to be executed at the specified time, returns a {@link VoidReceipt} for the task. The
     * task becomes enabled after the given time.
     *
     * @param task the given task
     * @param time the specified time to execute the task
     * @param <T>  the type of the task result
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull TaskReceipt<T> scheduleAt(
        @Nonnull Callable<? extends T> task,
        @Nonnull Instant time
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleAt(task, time);
    }

    /**
     * Schedules the given periodic task that becomes enabled first after the given initial delay, and subsequently with
     * the given period. That is, the executions will commence after {@code initialDelay} then
     * {@code initialDelay + period}, then {@code initialDelay + 2 * period}, and so on.
     * <p>
     * If any execution of the task fails, subsequent executions are suppressed. Otherwise, the task will only terminate
     * via cancellation or termination of the executor. If any execution of this task takes longer than its period, then
     * subsequent executions may start late, but will not concurrently execute.
     *
     * @param task         the given periodic task
     * @param initialDelay the given initial delay for first execution
     * @param period       the given period between successive executions
     * @return the receipt representing pending completion of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt scheduleWithRate(
        @Nonnull Runnable task,
        @Nonnull Duration initialDelay,
        @Nonnull Duration period
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleWithRate(task, initialDelay, period);
    }

    /**
     * Schedules the given periodic task that becomes enabled first after the given initial delay, and subsequently with
     * the given delay between the termination of one execution and the commencement of the next.
     * <p>
     * If any execution of the task fails, subsequent executions are suppressed. Otherwise, the task will only terminate
     * via cancellation or termination of the executor.
     *
     * @param task         the given periodic task
     * @param initialDelay the given initial delay for first execution
     * @param delay        the given delay between the termination of one execution and the commencement of the next
     * @return the receipt representing pending completion of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt scheduleWithDelay(
        @Nonnull Runnable task,
        @Nonnull Duration initialDelay,
        @Nonnull Duration delay
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleWithDelay(task, initialDelay, delay);
    }

    private static final class ExecutorHolder {
        private static final TaskExecutor executor = TaskExecutor.newExecutor();
    }

    private static final class ScheduleHolder {
        private static final TaskExecutor executor = TaskExecutor.newScheduler();
    }
}
