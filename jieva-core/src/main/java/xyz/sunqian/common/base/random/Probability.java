package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;

import java.util.function.Supplier;

/**
 * This interface represents the probability of generating an object.
 * <p>
 * It contains a score and a supplier, the score is used to calculate the probability for generating, and the supplier
 * is used to generate the object.
 *
 * @param <T> the type of the object to be generated
 * @author sunqian
 */
public interface Probability<T> {

    /**
     * Returns the score which is used to calculate the probability.
     *
     * @return the score which is used to calculate the probability
     */
    long score();

    /**
     * Returns the supplier which is used to generate the object.
     *
     * @return the supplier which is used to generate the object
     */
    @Nonnull
    Supplier<T> supplier();
}
