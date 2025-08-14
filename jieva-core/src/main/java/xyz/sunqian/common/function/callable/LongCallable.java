package xyz.sunqian.common.function.callable;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} extension interface that returns {@code long} via {@link #callAsLong()}, and its default behavior
 * of {@link #call()} is to call {@link #callAsLong()} and return its result.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface LongCallable extends Callable<Long> {

    /**
     * Computes a result as {@code long}, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    long callAsLong() throws Exception;

    @Override
    default Long call() throws Exception {
        return callAsLong();
    }
}
