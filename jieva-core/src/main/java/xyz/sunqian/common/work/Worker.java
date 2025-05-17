package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;

import java.util.concurrent.ExecutorService;

/**
 * This interface is an executor for {@link Work}.
 *
 * @author sunqian
 */
public interface Worker {

    void work(@Nonnull Runnable work);

    void work(@Nonnull Work<?> work);

    default void ss() {
        work(()-> {});
    }
}
