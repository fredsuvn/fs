package space.sunqian.fs.base.function;

import java.util.function.LongFunction;

/**
 * Represents a function that accepts an index and one argument and produces a result.
 * <p>
 * This is an indexed version of {@link LongFunction} whose functional method is {@link #apply(int, long)}.
 *
 * @param <R> the type of the result of the function
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedLongFunction<R> {

    /**
     * Applies this function to the given index and argument.
     *
     * @param index the index
     * @param t     the function argument
     * @return the function result
     */
    R apply(int index, long t);
}
