package xyz.sunqian.common.function.callable;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} extension interface that returns {@code boolean} via {@link #callAsBoolean()}, and its default
 * behavior of {@link #call()} is to call {@link #callAsBoolean()} and return its result.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface BooleanCallable extends Callable<Boolean> {

    /**
     * Computes a result as {@code boolean}, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    boolean callAsBoolean() throws Exception;

    @Override
    default Boolean call() throws Exception {
        return callAsBoolean();
    }
}
