package space.sunqian.fs.base.function;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts an index and a single input argument and returns no result.
 * <p>
 * This is an indexed version of {@link Consumer} whose functional method is {@link #accept(int, Object)}.
 *
 * @param <T> the type of the input to the operation
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param index the index
     * @param t     the input argument
     */
    void accept(int index, T t);
}
