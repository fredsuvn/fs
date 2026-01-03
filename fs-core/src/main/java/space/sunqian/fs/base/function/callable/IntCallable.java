package space.sunqian.fs.base.function.callable;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} extension interface that returns {@code int} via {@link #callAsInt()}, and its default behavior of
 * {@link #call()} is to call {@link #callAsInt()} and return its result.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface IntCallable extends Callable<Integer> {

    /**
     * Computes a result as {@code int}, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    int callAsInt() throws Exception;

    @Override
    default Integer call() throws Exception {
        return callAsInt();
    }
}
