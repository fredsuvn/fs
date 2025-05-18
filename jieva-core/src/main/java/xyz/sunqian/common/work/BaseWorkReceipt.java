package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Base work receipt interface of {@link WorkReceipt} and {@link RunReceipt}.
 *
 * @author sunqian
 */
interface BaseWorkReceipt {

    /**
     * Returns the state of the work.
     *
     * @return the state of the work
     */
    @Nonnull
    WorkState getState();

    /**
     * Cancels the work, may fail if the work has already been completed or canceled. It the work is executing, the
     * executing thread may be interrupted to cancel. This method is equivalent to {@code cancel(true)}.
     *
     * @return {@code true} to cancel successfully, otherwise {@code false}
     */
    default boolean cancel() {
        return cancel(true);
    }

    /**
     * Cancels the work, may fail if the work has already been completed or canceled. The {@code interrupt} parameter
     * specifies whether the executing thread can be interrupted if the work is executing.
     *
     * @param interrupt to specifies whether the executing thread can be interrupted if the work is executing
     * @return {@code true} to cancel successfully, otherwise {@code false}
     */
    boolean cancel(boolean interrupt);

    /**
     * Returns {@code true} if the work's state is {@link WorkState#CANCELED} or {@link WorkState#CANCELED_DURING}.
     * normally.
     *
     * @return {@code true} if the work's state is {@link WorkState#CANCELED} or {@link WorkState#CANCELED_DURING}, else
     * {@code false}
     */
    default boolean isCancelled() {
        WorkState state = getState();
        return Objects.equals(state, WorkState.CANCELED)
            || Objects.equals(state, WorkState.CANCELED_DURING);
    }

    /**
     * Returns {@code true} if the work's state is terminal ({@link WorkState#SUCCEEDED}, {@link WorkState#FAILED},
     * {@link WorkState#CANCELED}, {@link WorkState#CANCELED_DURING}).
     *
     * @return {@code true} if the work's state is terminal, else {@code false}
     */
    default boolean isDone() {
        return getState().isTerminal();
    }

    /**
     * Returns the exception thrown by the work, if any.
     *
     * @return the exception thrown by the work, if any
     */
    @Nullable
    Throwable getException();

    /**
     * Returns the remaining delay of the work execution, or null if the work is not delayed.
     *
     * @return the remaining delay of the work execution, or null if the work is not delayed
     */
    @Nullable
    Duration getDelay();
}
