package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.WrappedException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Static utility class for executing works.
 *
 * @author sunqian
 */
public class JieWork {

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
     * Runs the given work asynchronously.
     *
     * @param work the work to run
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static void run(@Nonnull Runnable work) throws SubmissionException {
        ExecutorHolder.executor.run(work);
    }

    /**
     * Runs the given work asynchronously, and returns the receipt of the work.
     *
     * @param work the work to run
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull WorkReceipt<T> run(@Nonnull Callable<? extends T> work) throws SubmissionException {
        return ExecutorHolder.executor.submit(work);
    }

    /**
     * Schedules the given work with a specified delay time from now, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given delay.
     *
     * @param work  the given work
     * @param delay the specified delay time
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull RunReceipt schedule(@Nonnull Runnable work, @Nonnull Duration delay) throws SubmissionException {
        return ScheduleHolder.executor.schedule(work, delay);
    }

    /**
     * Schedules the given work with a specified delay time from now, returns a {@link WorkReceipt} for the work. The
     * work becomes enabled after the given delay.
     *
     * @param work  the given work
     * @param delay the specified delay time
     * @param <T>   the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull WorkReceipt<T> schedule(
        @Nonnull Callable<? extends T> work,
        @Nonnull Duration delay
    ) throws SubmissionException {
        return ScheduleHolder.executor.schedule(work, delay);
    }

    /**
     * Schedules the given work to be executed at the specified time, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given time.
     *
     * @param work the given work
     * @param time the specified time to execute the work
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull RunReceipt scheduleAt(
        @Nonnull Runnable work, @Nonnull Instant time
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleAt(work, time);
    }

    /**
     * Schedules the given work to be executed at the specified time, returns a {@link RunReceipt} for the work. The
     * work becomes enabled after the given time.
     *
     * @param work the given work
     * @param time the specified time to execute the work
     * @param <T>  the type of the work result
     * @return the receipt of the work
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull WorkReceipt<T> scheduleAt(
        @Nonnull Callable<? extends T> work,
        @Nonnull Instant time
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleAt(work, time);
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
    public static @Nonnull RunReceipt scheduleWithRate(
        @Nonnull Runnable work,
        @Nonnull Duration initialDelay,
        @Nonnull Duration period
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleWithRate(work, initialDelay, period);
    }

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
    public static @Nonnull RunReceipt scheduleWithDelay(
        @Nonnull Runnable work,
        @Nonnull Duration initialDelay,
        @Nonnull Duration delay
    ) throws SubmissionException {
        return ScheduleHolder.executor.scheduleWithDelay(work, initialDelay, delay);
    }

    private static final class ExecutorHolder {
        private static final WorkExecutor executor = WorkExecutor.newExecutor();
    }

    private static final class ScheduleHolder {
        private static final WorkExecutor executor = WorkExecutor.newScheduler();
    }
}
