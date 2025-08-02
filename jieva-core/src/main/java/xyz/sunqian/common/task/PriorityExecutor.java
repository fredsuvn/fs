package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;

import java.util.concurrent.Callable;

/**
 * A prioritized task executor, where each task is assigned a priority, and tasks with higher priority have a higher
 * probability to execute first. But like the priority of threads, this is not absolute, it is only used to prompt the
 * executor and is not mandatory.
 *
 * @author sunqian
 */
public interface PriorityExecutor extends BaseExecutor {

    /**
     * Submits the given task to this executor, returns a {@link RunReceipt} for the task.
     *
     * @param task     the given task
     * @param priority the priority of the task
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    @Nonnull
    RunReceipt submit(@Nonnull Runnable task, @Nonnull TaskPriority priority) throws TaskSubmissionException;

    /**
     * Submits the given task to this executor, returns a {@link CallReceipt} for the task.
     *
     * @param task     the given task
     * @param priority the priority of the task
     * @param <T>      the type of the task result
     * @return the receipt of the task
     * @throws TaskSubmissionException if an error occurs during submitting
     */
    <T> @Nonnull CallReceipt<T> submit(
        @Nonnull Callable<? extends T> task, @Nonnull TaskPriority priority
    ) throws TaskSubmissionException;
}
