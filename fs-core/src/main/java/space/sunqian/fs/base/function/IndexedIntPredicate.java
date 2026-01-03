package space.sunqian.fs.base.function;

import java.util.function.IntPredicate;

/**
 * Represents a predicate (boolean-valued function) of an index and one argument.
 * <p>
 * This is an indexed version of {@link IntPredicate} whose functional method is {@link #test(int, int)}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface IndexedIntPredicate {

    /**
     * Evaluates this predicate on the given index and argument.
     *
     * @param index the index
     * @param t     the input argument
     * @return {@code true} if the index and input argument matches the predicate, otherwise {@code false}
     */
    boolean test(int index, int t);
}
