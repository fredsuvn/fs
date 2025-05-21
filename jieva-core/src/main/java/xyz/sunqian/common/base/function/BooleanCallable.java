package xyz.sunqian.common.base.function;

import java.util.concurrent.Callable;

/**
 * A task that returns a result and may throw an exception. This is a variant of {@link Callable} that returns a
 * {@code boolean} result, and it's a functional interface whose functional method is {@link #call()}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface BooleanCallable {

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    boolean call() throws Exception;
}
