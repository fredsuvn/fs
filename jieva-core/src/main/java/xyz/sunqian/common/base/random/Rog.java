package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnreachablePointException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * The Random Object Generator, to produce random objects. The usage is simple:
 * <pre>{@code
 * Rog<String> rog = Rog.newBuilder()
 *     .weight(10, "a")
 *     .weight(10, "b")
 *     .build();
 * String randomString = rog.next();
 * }</pre>
 *
 * @author sunqian
 */
public interface Rog<T> {

    /**
     * Returns a new builder for generating a new {@link Rog} instances.
     *
     * @param <T> the type of the generated object
     * @return a new builder for generating a new {@link Rog} instances
     */
    static <T> @Nonnull Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Returns next random object.
     *
     * @return next random object
     */
    T next();

    /**
     * Builder for generating a new {@link Rog} instance.
     * <p>
     * This Builder generates {@link Rog} by setting {@code rng} (random number generator) and weights. For example:
     * <pre>{@code
     * Rog<String> rog = Rog.newBuilder()
     *     .weight(20, "a")       // 20% to generate "a"
     *     .weight(80, () -> "b") // 80% to generate "b"
     *     .rng(Rng.newRng())
     *     .build();
     * }</pre>
     * <p>
     * The probability of hitting the object or supplier associated with each weight is given by:
     * {@code weight / sum(weights)}. The {@code rng} provides the random long value, which is used to calculate the
     * hitting probability. If the {@code rng} is not set, {@link Rng#newRng()} is used.
     * <p>
     * Note that the sum of weights can not overflow the {@link Long#MAX_VALUE}.
     *
     * @param <T> the type of the random objects
     * @author sunqian
     */
    class Builder<T> {

        private final @Nonnull List<@Nonnull Weight<T>> weights = new ArrayList<>();
        private @Nullable LongSupplier rng;

        /**
         * Add a weight and its corresponding object.
         *
         * @param weight the weight, cannot be negative
         * @param obj    the object corresponding to the weight
         * @param <T1>   the type of the object
         * @return this builder
         */
        public <T1> @Nonnull Builder<T1> weight(long weight, T obj) throws IllegalArgumentException {
            return weight(weight, () -> obj);
        }

        /**
         * Add a weight and its corresponding supplier.
         *
         * @param weight   the weight, cannot be negative
         * @param supplier the supplier corresponding to the weight
         * @param <T1>     the type of the generated object
         * @return this builder
         */
        public <T1> @Nonnull Builder<T1> weight(
            long weight, @Nonnull Supplier<T> supplier
        ) throws IllegalArgumentException {
            CheckKit.checkArgument(weight >= 0, "weight must be non-negative");
            weights.add(new Weight<>(weight, supplier));
            return Jie.as(this);
        }

        /**
         * Set the random number generator. If this is not set, {@link Rng#newRng()} will be used.
         *
         * @param rng  the random number generator
         * @param <T1> the type of the generated object
         * @return this builder
         */
        public <T1> @Nonnull Builder<T1> rng(@Nonnull LongSupplier rng) {
            this.rng = rng;
            return Jie.as(this);
        }

        /**
         * Builds and returns a new {@link Rog} instance with the added weights, objects and suppliers.
         *
         * @param <T1> the type of the generated object
         * @return a new {@link Rog} instance with the added weights, objects and suppliers
         */
        public <T1> @Nonnull Rog<T1> build() {
            return Jie.as(
                new RogImpl<>(rng == null ? Rng.newRng() : rng, weights)
            );
        }

        private static final class RogImpl<T> implements Rog<T> {

            private final @Nonnull LongSupplier rng;
            private final @Nonnull WeightNode<T>[] nodes;
            private final long totalWeight;

            @SuppressWarnings("unchecked")
            RogImpl(
                @Nonnull LongSupplier rng,
                @Nonnull List<@Nonnull Weight<T>> weights
            ) {
                this.rng = rng;
                List<WeightNode<T>> nodes = new ArrayList<>(weights.size());
                long totalScore = 0;
                for (Weight<T> weight : weights) {
                    long from = totalScore;
                    totalScore += weight.weight;
                    long to = totalScore;
                    nodes.add(new WeightNode<>(weight.supplier, from, to));
                }
                this.nodes = nodes.toArray(new WeightNode[0]);
                this.totalWeight = totalScore;
            }

            @Override
            public T next() {
                long score = Math.abs(rng.getAsLong()) % totalWeight;
                WeightNode<T> weight = getWeight(score);
                return weight.supplier.get();
            }

            private @Nonnull WeightNode<T> getWeight(long score) {
                int index = binarySearch(score);
                if (index < 0) {
                    throw new UnreachablePointException("Weight not found by score: " + score + ".");
                }
                return nodes[index];
            }

            private int binarySearch(long score) {
                int left = 0;
                int right = nodes.length - 1;
                while (left <= right) {
                    int mid = (left + right) / 2;
                    WeightNode<T> weight = nodes[mid];
                    long compare = compare(score, weight);
                    if (compare == 0) {
                        return mid;
                    }
                    if (compare > 0) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                }
                return -1;
            }

            private long compare(long next, WeightNode<T> node) {
                if (next < node.from) {
                    return -1;
                }
                if (next >= node.to) {
                    return 1;
                }
                return 0;
            }

        }

        private static final class Weight<T> {

            private final long weight;
            private final @Nonnull Supplier<T> supplier;

            private Weight(long weight, @Nonnull Supplier<T> supplier) {
                this.weight = weight;
                this.supplier = supplier;
            }
        }

        private static final class WeightNode<T> {

            private final @Nonnull Supplier<T> supplier;
            private final long from;
            private final long to;

            private WeightNode(@Nonnull Supplier<T> supplier, long from, long to) {
                this.supplier = supplier;
                this.from = from;
                this.to = to;
            }
        }
    }
}
