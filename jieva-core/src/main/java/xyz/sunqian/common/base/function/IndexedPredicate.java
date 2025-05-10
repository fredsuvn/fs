package xyz.sunqian.common.base.function;

import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of an index and one argument.
 * <p>
 * This is an indexed version of {@link Predicate} whose functional method is {@link #test(int, Object)}.
 *
 * @param <T> the type of the input to the predicate
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedPredicate<T> {

    /**
     * Evaluates this predicate on the given index and argument.
     *
     * @param index the index
     * @param t     the input argument
     * @return {@code true} if the index and input argument matches the predicate, otherwise {@code false}
     */
    boolean test(int index, T t);
}
