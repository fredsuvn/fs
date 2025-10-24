package space.sunqian.common.function.consumer;

import java.util.function.DoubleConsumer;

/**
 * Represents an operation that accepts an index and a single input argument and returns no result.
 * <p>
 * This is an indexed version of {@link DoubleConsumer} whose functional method is {@link #accept(int, double)}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedDoubleConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param index the index
     * @param t     the input argument
     */
    void accept(int index, double t);
}
