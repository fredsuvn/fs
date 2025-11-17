package space.sunqian.common.base.function.consumer;

import java.util.function.LongConsumer;

/**
 * Represents an operation that accepts an index and a single input argument and returns no result.
 * <p>
 * This is an indexed version of {@link LongConsumer} whose functional method is {@link #accept(int, long)}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedLongConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param index the index
     * @param t     the input argument
     */
    void accept(int index, long t);
}
