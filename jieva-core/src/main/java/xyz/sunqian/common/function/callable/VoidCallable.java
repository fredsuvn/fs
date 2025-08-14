package xyz.sunqian.common.function.callable;

import java.util.concurrent.Callable;

/**
 * This interface is a {@link Callable} like functional interface whose functional method is {@link #call()} -- without
 * return value.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface VoidCallable {

    /**
     * Computes without any result, or throws an exception if unable to do so.
     *
     * @throws Exception if unable to compute a result
     */
    void call() throws Exception;
}
