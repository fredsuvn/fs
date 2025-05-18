package xyz.sunqian.common.work;

import xyz.sunqian.common.base.exception.JieException;

import java.util.concurrent.Callable;

/**
 * This interface represents an executable body, which is also a functional interface whose functional method is
 * {@link #doWork()}. And it inherits {@link Runnable} and {@link Callable}.
 *
 * @param <T> the result type of the work
 * @author sunqian
 */
@FunctionalInterface
public interface Work<T> extends Runnable, Callable<T> {

    /**
     * Does the task work and returns a result, or throws an exception if unable to do so.
     *
     * @return the work result
     * @throws Exception if an exception occurs
     */
    T doWork() throws Exception;

    @Override
    default void run() {
        try {
            doWork();
        } catch (Exception e) {
            throw new RuntimeException(JieException.getMessage(e), e);
        }
    }

    @Override
    default T call() throws Exception {
        return doWork();
    }
}
