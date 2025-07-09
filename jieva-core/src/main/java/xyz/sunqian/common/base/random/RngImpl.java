package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.exception.UnreachablePointException;

import java.util.PrimitiveIterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@ThreadSafe
final class RngImpl implements Rng {

    static final @Nonnull RngImpl INST = new RngImpl();

    private @Nonnull ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    @Override
    public int nextInt() {
        return random().nextInt();
    }

    @Override
    public int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return random().nextInt(startInclusive, endExclusive);
    }

    @Override
    public long nextLong() {
        return random().nextLong();
    }

    @Override
    public long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return random().nextLong(startInclusive, endExclusive);
    }

    @Override
    public double nextDouble() {
        return random().nextDouble();
    }

    @Override
    public double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return random().nextDouble(startInclusive, endExclusive);
    }

    @Override
    public @Nonnull IntStream ints() {
        return random().ints();
    }

    @Override
    public @Nonnull IntStream ints(int startInclusive, int endExclusive) {
        if (startInclusive == endExclusive) {
            return IntStream.generate(() -> startInclusive);
        }
        return random().ints(startInclusive, endExclusive);
    }

    @Override
    public @Nonnull IntSupplier intSupplier(int startInclusive, int endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return () -> startInclusive;
        }
        return new IntSupplier() {

            private final @Nonnull PrimitiveIterator.OfInt iterator =
                random().ints(startInclusive, endExclusive).iterator();

            @Override
            public int getAsInt() {
                return iterator.nextInt();
            }
        };
    }

    @Override
    public @Nonnull LongStream longs() {
        return random().longs();
    }

    @Override
    public @Nonnull LongStream longs(long startInclusive, long endExclusive) {
        if (startInclusive == endExclusive) {
            return LongStream.generate(() -> startInclusive);
        }
        return random().longs(startInclusive, endExclusive);
    }

    @Override
    public @Nonnull LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return () -> startInclusive;
        }
        return new LongSupplier() {

            private final @Nonnull PrimitiveIterator.OfLong iterator =
                random().longs(startInclusive, endExclusive).iterator();

            @Override
            public long getAsLong() {
                return iterator.nextLong();
            }
        };
    }

    @Override
    public @Nonnull DoubleStream doubles() {
        return random().doubles();
    }

    @Override
    public @Nonnull DoubleStream doubles(double startInclusive, double endExclusive) {
        if (startInclusive == endExclusive) {
            return DoubleStream.generate(() -> startInclusive);
        }
        return random().doubles(startInclusive, endExclusive);
    }

    @Override
    public @Nonnull DoubleSupplier doubleSupplier(
        double startInclusive, double endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return () -> startInclusive;
        }
        return new DoubleSupplier() {

            private final @Nonnull PrimitiveIterator.OfDouble iterator =
                random().doubles(startInclusive, endExclusive).iterator();

            @Override
            public double getAsDouble() {
                return iterator.nextDouble();
            }
        };
    }

    static final class RandomSupplier<T> implements Supplier<T> {

        private final @Nonnull LongSupplier rd;
        private final Node<? extends T> @Nonnull [] nodes;
        private final long totalScore;

        @SuppressWarnings("unchecked")
        RandomSupplier(@Nonnull LongSupplier rd, Probability<? extends T> @Nonnull [] probabilities) {
            this.rd = rd;
            long totalScore = 0;
            this.nodes = new Node[probabilities.length];
            for (int i = 0; i < probabilities.length; i++) {
                Probability<? extends T> probability = probabilities[i];
                long score = probability.score();
                nodes[i] = new Node<>(probability.supplier(), totalScore, totalScore + score);
                totalScore += score;
            }
            this.totalScore = totalScore;
        }

        @Override
        public T get() {
            long next = Math.abs(rd.getAsLong());
            int index = binarySearch(next % totalScore);
            if (index < 0) {
                throw new UnreachablePointException("Score not found: " + next + ".");
            }
            return nodes[index].supplier.get();
        }

        private int binarySearch(long next) {
            int left = 0;
            int right = nodes.length - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                Node<? extends T> node = nodes[mid];
                long compare = compare(next, node);
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
}
