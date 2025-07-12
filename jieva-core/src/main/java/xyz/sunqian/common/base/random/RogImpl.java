package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnreachablePointException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

final class RogImpl implements Rog {

    static @Nonnull RogImpl rng(@Nonnull LongSupplier rng) {
        return new RogImpl(rng);
    }

    private final @Nonnull LongSupplier rng;

    private RogImpl(@Nonnull LongSupplier rng) {
        this.rng = rng;
    }

    @Override
    public @Nonnull <T> Supplier<T> supplier(
        @Nonnull Iterable<? extends @Nonnull Probability<? extends T>> probabilities
    ) {
        return new RandomSupplier<>(rng, probabilities);
    }

    static final class RandomSupplier<T> implements Supplier<T> {

        private final @Nonnull LongSupplier rd;
        private final Node<? extends T> @Nonnull [] nodes;
        private final long totalScore;

        @SuppressWarnings("unchecked")
        RandomSupplier(
            @Nonnull LongSupplier rd,
            @Nonnull Iterable<? extends @Nonnull Probability<? extends T>> probabilities
        ) {
            this.rd = rd;
            long totalScore = 0;
            List<Node<? extends T>> nodeList = new ArrayList<>();
            int i = 0;
            for (Probability<? extends T> probability : probabilities) {
                long score = probability.score();
                nodeList.add(new Node<>(probability.supplier(), totalScore, totalScore + score));
                totalScore += score;
            }
            this.nodes = nodeList.toArray(new Node[0]);
            this.totalScore = totalScore;
        }

        @Override
        public T get() {
            long score = Math.abs(rd.getAsLong()) % totalScore;
            Node<? extends T> node = getNode(score);
            return node.supplier.get();
        }

        private @Nonnull Node<? extends T> getNode(long score) {
            int index = binarySearch(score);
            if (index < 0) {
                throw new UnreachablePointException("Score not found: " + score + ".");
            }
            return nodes[index];
        }

        private int binarySearch(long score) {
            int left = 0;
            int right = nodes.length - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                Node<? extends T> node = nodes[mid];
                long compare = compare(score, node);
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

        private static long compare(long next, Node<?> node) {
            if (next < node.from) {
                return -1;
            }
            if (next >= node.to) {
                return 1;
            }
            return 0;
        }

        private static final class Node<T> {

            private final @Nonnull Supplier<T> supplier;
            private final long from;
            private final long to;

            private Node(@Nonnull Supplier<T> supplier, long from, long to) {
                this.supplier = supplier;
                this.from = from;
                this.to = to;
            }
        }
    }

    static final class ProbabilityImpl<T> implements Rog.Probability<T> {

        private final long score;
        private final @Nonnull Supplier<T> supplier;

        ProbabilityImpl(long score, @Nonnull Supplier<? extends T> supplier) throws IllegalArgumentException {
            if (score < 0) {
                throw new IllegalArgumentException("Score must >= 0!");
            }
            this.score = score;
            this.supplier = Jie.as(supplier);
        }

        public long score() {
            return Math.abs(score);
        }

        public @Nonnull Supplier<T> supplier() {
            return supplier;
        }
    }
}
