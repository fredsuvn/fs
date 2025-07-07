package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;

import java.util.function.LongSupplier;

/**
 * This interface represents the probability of generating a {@code long} value.
 * <p>
 * It contains a score and a supplier, the score is used to calculate the probability for generating, and the supplier
 * is used to generate the value.
 *
 * @author sunqian
 */
public interface LongProbability {

    /**
     * Returns the score which is used to calculate the probability.
     *
     * @return the score which is used to calculate the probability
     */
    long score();

    /**
     * Returns the supplier which is used to generate the {@code long} value.
     *
     * @return the supplier which is used to generate the {@code long} value
     */
    @Nonnull
    LongSupplier supplier();
}
