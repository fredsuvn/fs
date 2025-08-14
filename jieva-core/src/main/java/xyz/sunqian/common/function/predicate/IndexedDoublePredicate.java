package xyz.sunqian.common.function.predicate;

import java.util.function.DoublePredicate;

/**
 * Represents a predicate (boolean-valued function) of an index and one argument.
 * <p>
 * This is an indexed version of {@link DoublePredicate} whose functional method is {@link #test(int, double)}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedDoublePredicate {

    /**
     * Evaluates this predicate on the given index and argument.
     *
     * @param index the index
     * @param t     the input argument
     * @return {@code true} if the index and input argument matches the predicate, otherwise {@code false}
     */
    boolean test(int index, double t);
}
