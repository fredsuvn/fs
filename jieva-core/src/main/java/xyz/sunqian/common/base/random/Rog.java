package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;

import java.util.Collection;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * The Random Object Generator, base interface to produce random objects.
 *
 * @author sunqian
 */
public interface Rog {

    /**
     * Returns the default implementation of {@link Rog}.
     *
     * @return the default implementation of {@link Rog}
     */
    static Rog getDefault() {
        return RogImpl.INST;
    }

    /**
     * Returns a new {@link Probability} with the specified score and a supplier which always returns the specified
     * object.
     *
     * @param score the specified score, must {@code >= 0}
     * @param obj   the specified object
     * @param <T>   the type of the object
     * @return a new {@link Probability} with the specified score and a supplier which always returns the specified
     * object
     * @throws IllegalArgumentException if the score is negative
     */
    static <T> @Nonnull Probability<T> probability(long score, @Nonnull T obj) throws IllegalArgumentException {
        return probability(score, () -> obj);
    }

    /**
     * Returns a new {@link Probability} with the specified score and supplier.
     *
     * @param score    the specified score, must {@code >= 0}
     * @param supplier the specified supplier
     * @param <T>      the type of the object
     * @return a new {@link Probability} with the specified score and supplier
     * @throws IllegalArgumentException if the score is negative
     */
    static <T> @Nonnull Probability<T> probability(
        long score, @Nonnull Supplier<? extends T> supplier
    ) throws IllegalArgumentException {
        return new RogImpl.ProbabilityImpl<>(score, supplier);
    }

    /**
     * Returns a {@link Supplier} which produces the random objects, the usage example:
     * <pre>{@code
     * Supplier<String> strSupplier = Rog.supplier(asList(
     *     Rog.probability(20, () -> "a"), // 20% hit probability
     *     Rog.probability(80, () -> "b")  // 80% hit probability
     * ));
     * }</pre>
     * <p>
     * Each provided {@link Probability}, which has a score and a supplier, represents the hit probability for its
     * supplier. Let the {@code sum(score)} be the total score of all provided {@link Probability}s, the hit probability
     * for each {@link Probability} is {@code score / sum(score)}. Note the total score of all provided
     * {@link Probability}s can not overflow the maximum value of {@code long}.
     * <p>
     * This method uses {@link Rng#threadLocal()} to generate the random long value, which is used to calculate the hit
     * probability.
     *
     * @param probabilities the provided {@link Probability}s
     * @param <T>           the type of the random objects
     * @return a {@link Supplier} which produces the random objects
     */
    default <T> @Nonnull Supplier<T> supplier(
        @Nonnull Collection<? extends @Nonnull Probability<? extends T>> probabilities
    ) {
        return supplier(Rng.threadLocal(), probabilities);
    }

    /**
     * Returns a {@link Supplier} which produces the random objects, the usage example:
     * <pre>{@code
     * Supplier<String> strSupplier = Rog.supplier(scoreGenerator, asList(
     *     Rog.probability(20, () -> "a"), // 20% hit probability
     *     Rog.probability(80, () -> "b"), // 80% hit probability
     * ));
     * }</pre>
     * <p>
     * Each provided {@link Probability}, which has a score and a supplier, represents the hit probability for its
     * supplier. Let the {@code sum(score)} be the total score of all provided {@link Probability}s, the hit probability
     * for each {@link Probability} is {@code score / sum(score)}. Note the total score of all provided
     * {@link Probability}s can not overflow the maximum value of {@code long}.
     * <p>
     * The {@code scoreGenerator} is a {@link LongSupplier} which produces a random long value, the long value is used
     * to calculate the hit probability.
     *
     * @param scoreGenerator a {@link LongSupplier} which produces a random long value
     * @param probabilities  the provided {@link Probability}s
     * @param <T>            the type of the random objects
     * @return a {@link Supplier} which produces the random objects
     */
    <T> @Nonnull Supplier<T> supplier(
        @Nonnull LongSupplier scoreGenerator,
        @Nonnull Collection<? extends @Nonnull Probability<? extends T>> probabilities
    );

    /**
     * Represents the probability of generating an object.
     * <p>
     * It contains a score and a supplier, the score is used to calculate the probability of generating, and the
     * supplier is used to generate the object.
     *
     * @param <T> the type of the generated object
     * @author sunqian
     */
    interface Probability<T> {

        /**
         * Returns the score.
         *
         * @return the score
         */
        long score();

        /**
         * Returns the supplier.
         *
         * @return the supplier
         */
        @Nonnull
        Supplier<T> supplier();
    }
}
