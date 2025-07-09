package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface Rog {

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
     * for each {@link Probability} is {@code score / sum(score)}.
     * <p>
     * The {@code rd} is a {@link LongSupplier} which produces a random long value, the long value is used to calculate
     * the hit probability.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         the score of a {@link Probability} can be negative, but it will be converted to positive value before
     *         calculating;
     *     </li>
     *     <li>
     *         the total score of all provided {@link Probability}s can not overflow the maximum value of {@code long};
     *     </li>
     * </ul>
     *
     * @param probabilities the provided {@link Probability}s
     * @param <T>           the type of the random objects
     * @return a {@link Supplier} which produces the random objects
     */
    default <T> @Nonnull Supplier<T> supplier(
        @Nonnull Iterable<? extends @Nonnull Probability<? extends T>> probabilities
    ) {
        return supplier(Rng.getDefault(), probabilities);
    }

    /**
     * Returns a {@link Supplier} which produces the random objects, the usage example:
     * <pre>{@code
     * Supplier<String> strSupplier = RandomKit.supplier(
     *     rd,
     *     RandomKit.probability(20, () -> "a"), // 20% hit probability
     *     RandomKit.probability(80, () -> "b"), // 80% hit probability
     * );
     * }</pre>
     * <p>
     * Each provided {@link Probability}, which has a score and a supplier, represents the hit probability for its
     * supplier. Let the {@code sum(score)} be the total score of all provided {@link Probability}s, the hit probability
     * for each {@link Probability} is {@code score / sum(score)}.
     * <p>
     * The {@code rd} is a {@link LongSupplier} which produces a random long value, the long value is used to calculate
     * the hit probability.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         the score of a {@link Probability} can be negative, but it will be converted to positive value before
     *         calculating;
     *     </li>
     *     <li>
     *         the total score of all provided {@link Probability}s can not overflow the maximum value of {@code long};
     *     </li>
     * </ul>
     *
     * @param scoreGenerator a {@link LongSupplier} which produces a random long value
     * @param probabilities  the provided {@link Probability}s
     * @param <T>            the type of the random objects
     * @return a {@link Supplier} which produces the random objects
     */
    default <T> @Nonnull Supplier<T> supplier(
        @Nonnull ScoreGenerator scoreGenerator,
        @Nonnull Iterable<? extends @Nonnull Probability<? extends T>> probabilities
    ) {
        return new RngImpl.RandomSupplier<>(scoreGenerator, probabilities);
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
    default <T> @Nonnull Probability<T> probability(long score, @Nonnull T obj) throws IllegalArgumentException {
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
    <T> @Nonnull Probability<T> probability(
        long score, @Nonnull Supplier<? extends T> supplier
    ) throws IllegalArgumentException;

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

    /**
     * Represents the score generator, to produce the scores between the specified start value inclusive and the
     * specified end value exclusive.
     *
     * @author sunqian
     */
    interface ScoreGenerator {

        /**
         * Generates and returns a score between the specified start value inclusive and the specified end value
         * exclusive.
         *
         * @param startInclusive the specified start value inclusive
         * @param endExclusive   the specified end value exclusive
         * @return a score between the specified start value inclusive and the specified end value exclusive
         */
        long generateScore(long startInclusive, long endExclusive);
    }
}
