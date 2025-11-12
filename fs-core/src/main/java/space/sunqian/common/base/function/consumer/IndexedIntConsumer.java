package space.sunqian.common.base.function.consumer;

import java.util.function.IntConsumer;

/**
 * Represents an operation that accepts an index and a single input argument and returns no result.
 * <p>
 * This is an indexed version of {@link IntConsumer} whose functional method is {@link #accept(int, int)}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedIntConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param index the index
     * @param t     the input argument
     */
    void accept(int index, int t);
}
