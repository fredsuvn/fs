package xyz.sunqian.common.base.function;

import java.util.concurrent.Callable;

/**
 * A task without a return value, may throw an exception. This is a variant of {@link Callable} without a return value,
 * and it's a functional interface whose functional method is {@link #call()}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface VoidCallable {

    /**
     * Computes without a return value, or throws an exception if unable to do so.
     *
     * @throws Exception if unable to compute a result
     */
    void call() throws Exception;
}
