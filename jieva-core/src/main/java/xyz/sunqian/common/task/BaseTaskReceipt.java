package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;

import java.time.Duration;

/**
 * Base task receipt interface of {@link TaskReceipt} and {@link VoidReceipt}.
 *
 * @author sunqian
 */
interface BaseTaskReceipt {

    /**
     * Returns the state of the task.
     *
     * @return the state of the task
     */
    @Nonnull
    TaskState getState();

    /**
     * Cancels the task, may fail if the task has already been completed or canceled. It the task is executing, the
     * executing thread may be interrupted to cancel. This method is equivalent to {@code cancel(true)}.
     *
     * @return {@code true} to cancel successfully, otherwise {@code false}
     */
    default boolean cancel() {
        return cancel(true);
    }

    /**
     * Cancels the task, may fail if the task has already been completed or canceled. The {@code interrupt} parameter
     * specifies whether the executing thread can be interrupted if the task is executing.
     *
     * @param interrupt to specifies whether the executing thread can be interrupted if the task is executing
     * @return {@code true} to cancel successfully, otherwise {@code false}
     */
    boolean cancel(boolean interrupt);

    /**
     * Returns {@code true} if the task's state is {@link TaskState#CANCELED} or {@link TaskState#CANCELED_EXECUTING}.
     *
     * @return {@code true} if the task's state is {@link TaskState#CANCELED} or {@link TaskState#CANCELED_EXECUTING},
     * else {@code false}
     */
    default boolean isCancelled() {
        TaskState state = getState();
        return Jie.equals(state, TaskState.CANCELED)
            || Jie.equals(state, TaskState.CANCELED_EXECUTING);
    }

    /**
     * Returns {@code true} if the task's state is terminal ({@link TaskState#SUCCEEDED}, {@link TaskState#FAILED},
     * {@link TaskState#CANCELED}, {@link TaskState#CANCELED_EXECUTING}).
     *
     * @return {@code true} if the task's state is terminal, else {@code false}
     */
    default boolean isDone() {
        return getState().isTerminal();
    }

    /**
     * Returns the exception thrown by the task, if any.
     *
     * @return the exception thrown by the task, if any
     */
    @Nullable
    Throwable getException();

    /**
     * Returns the remaining delay of the task execution, or null if the task is not delayed.
     *
     * @return the remaining delay of the task execution, or null if the task is not delayed
     */
    @Nullable
    Duration getDelay();
}
