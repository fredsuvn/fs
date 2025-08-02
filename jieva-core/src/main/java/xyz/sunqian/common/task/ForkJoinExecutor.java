package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nonnull;

import java.util.concurrent.ForkJoinPool;

/**
 * The fork join task executor, which extends the {@link CommonExecutor} and supports forkable tasks. The forkable task
 * can be generated via {@link ForkableTask}. This interface can be used as {@link ForkJoinPool} via
 * {@link #asExecutorService()}.
 *
 * @author sunqian
 */
public interface ForkJoinExecutor extends CommonExecutor {

    /**
     * Returns a {@link ForkJoinPool} represents this executor. Their content and state are shared, and all behaviors
     * are equivalent.
     *
     * @return a {@link ForkJoinPool} represents this executor
     */
    @Override
    @Nonnull
    ForkJoinPool asExecutorService();
}
