package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.UnreachablePointException;

import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

final class RandomSupplier {

    static <T> Supplier<T> supplier(@Nonnull Random random, Probability<T> @Nonnull [] probs) {
        return new OfObject<>(random, probs);
    }

    static IntSupplier supplier(@Nonnull Random random, IntProbability @Nonnull [] probs) {
        return new OfInt(random, probs);
    }

    static LongSupplier supplier(@Nonnull Random random, LongProbability @Nonnull [] probs) {
        return new OfLong(random, probs);
    }

    static DoubleSupplier supplier(@Nonnull Random random, DoubleProbability @Nonnull [] probs) {
        return new OfDouble(random, probs);
    }

    private static final class OfObject<T> implements Supplier<T> {

        private final @Nonnull Random random;
        private final ObjNode<T> @Nonnull [] nodes;
        private final long totalScore;

        @SuppressWarnings("unchecked")
        private OfObject(@Nonnull Random random, Probability<T> @Nonnull [] probs) {
            this.random = random;
            long totalScore = 0;
            this.nodes = new ObjNode[probs.length];
            for (int i = 0; i < probs.length; i++) {
                Probability<T> prob = probs[i];
                long score = Math.abs(prob.score());
                nodes[i] = new ObjNode<>(totalScore, totalScore + score, prob.supplier());
                totalScore += score;
            }
            this.totalScore = totalScore;
        }

        @Override
        public T get() {
            long next = Math.abs(random.nextLong());
            return generate(next % totalScore);
        }

        private T generate(long next) {
            int index = binarySearch(nodes, next);
            checkBinarySearch(index, next);
            ObjNode<T> node = nodes[index];
            return node.supplier.get();
        }

        private static final class ObjNode<T> extends Node {

            private final @Nonnull Supplier<T> supplier;

            private ObjNode(long from, long to, @Nonnull Supplier<T> supplier) {
                super(from, to);
                this.supplier = supplier;
            }
        }
    }

    private static final class OfInt implements IntSupplier {

        private final @Nonnull Random random;
        private final IntNode @Nonnull [] nodes;
        private final long totalScore;

        private OfInt(@Nonnull Random random, IntProbability @Nonnull [] probs) {
            this.random = random;
            long totalScore = 0;
            this.nodes = new IntNode[probs.length];
            for (int i = 0; i < probs.length; i++) {
                IntProbability prob = probs[i];
                long score = Math.abs(prob.score());
                nodes[i] = new IntNode(totalScore, totalScore + score, prob.supplier());
                totalScore += score;
            }
            this.totalScore = totalScore;
        }

        @Override
        public int getAsInt() {
            long next = Math.abs(random.nextLong());
            return generate(next % totalScore);
        }

        private int generate(long next) {
            int index = binarySearch(nodes, next);
            checkBinarySearch(index, next);
            IntNode node = nodes[index];
            return node.supplier.getAsInt();
        }

        private static final class IntNode extends Node {

            private final @Nonnull IntSupplier supplier;

            private IntNode(long from, long to, @Nonnull IntSupplier supplier) {
                super(from, to);
                this.supplier = supplier;
            }
        }
    }

    private static final class OfLong implements LongSupplier {

        private final @Nonnull Random random;
        private final LongNode @Nonnull [] nodes;
        private final long totalScore;

        private OfLong(@Nonnull Random random, LongProbability @Nonnull [] probs) {
            this.random = random;
            long totalScore = 0;
            this.nodes = new LongNode[probs.length];
            for (int i = 0; i < probs.length; i++) {
                LongProbability prob = probs[i];
                long score = Math.abs(prob.score());
                nodes[i] = new LongNode(totalScore, totalScore + score, prob.supplier());
                totalScore += score;
            }
            this.totalScore = totalScore;
        }

        @Override
        public long getAsLong() {
            long next = Math.abs(random.nextLong());
            return generate(next % totalScore);
        }

        private long generate(long next) {
            int index = binarySearch(nodes, next);
            checkBinarySearch(index, next);
            LongNode node = nodes[index];
            return node.supplier.getAsLong();
        }

        private static final class LongNode extends Node {

            private final @Nonnull LongSupplier supplier;

            private LongNode(long from, long to, @Nonnull LongSupplier supplier) {
                super(from, to);
                this.supplier = supplier;
            }
        }
    }

    private static final class OfDouble implements DoubleSupplier {

        private final @Nonnull Random random;
        private final DoubleNode @Nonnull [] nodes;
        private final long totalScore;

        private OfDouble(@Nonnull Random random, DoubleProbability @Nonnull [] probs) {
            this.random = random;
            long totalScore = 0;
            this.nodes = new DoubleNode[probs.length];
            for (int i = 0; i < probs.length; i++) {
                DoubleProbability prob = probs[i];
                long score = Math.abs(prob.score());
                nodes[i] = new DoubleNode(totalScore, totalScore + score, prob.supplier());
                totalScore += score;
            }
            this.totalScore = totalScore;
        }

        @Override
        public double getAsDouble() {
            long next = Math.abs(random.nextLong());
            return generate(next % totalScore);
        }

        private double generate(long next) {
            int index = binarySearch(nodes, next);
            checkBinarySearch(index, next);
            DoubleNode node = nodes[index];
            return node.supplier.getAsDouble();
        }

        private static final class DoubleNode extends Node {

            private final @Nonnull DoubleSupplier supplier;

            private DoubleNode(long from, long to, @Nonnull DoubleSupplier supplier) {
                super(from, to);
                this.supplier = supplier;
            }
        }
    }

    private static int binarySearch(Node @Nonnull [] nodes, long next) {
        int left = 0;
        int right = nodes.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            Node node = nodes[mid];
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

    private static long compare(long next, Node node) {
        if (next < node.from) {
            return -1;
        }
        if (next >= node.to) {
            return 1;
        }
        return 0;
    }

    private static void checkBinarySearch(int index, long next) {
        if (index < 0) {
            throw new UnreachablePointException("Score of probability not found: " + next + "!");
        }
    }

    private static class Node {

        protected final long from;
        protected final long to;

        private Node(long from, long to) {
            this.from = from;
            this.to = to;
        }
    }
}
