package xyz.sunqian.common.work;

import java.util.concurrent.Callable;

/**
 * This interface represents an executable task, such as a {@link Runnable}, {@link Callable}, a third process, or other
 * executable body. This is a functional interface whose functional method is {@link #doWork()}.
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
            throw new WorkingException(e);
        }
    }

    @Override
    default T call() throws Exception {
        return doWork();
    }
}
