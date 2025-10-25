package space.sunqian.common.function.callable;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} extension interface that returns {@code double} via {@link #callAsDouble()}, and its default
 * behavior of {@link #call()} is to call {@link #callAsDouble()} and return its result.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface DoubleCallable extends Callable<Double> {

    /**
     * Computes a result as {@code double}, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    double callAsDouble() throws Exception;

    @Override
    default Double call() throws Exception {
        return callAsDouble();
    }
}
