package xyz.sunqian.common.function.function;

import java.util.function.Function;

/**
 * Represents a function that accepts an index and one argument and produces a result.
 * <p>
 * This is an indexed version of {@link Function} whose functional method is {@link #apply(int, Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedFunction<T, R> {

    /**
     * Applies this function to the given index and argument.
     *
     * @param index the index
     * @param t     the function argument
     * @return the function result
     */
    R apply(int index, T t);
}
