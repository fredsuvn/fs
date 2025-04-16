package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.coll.JieColl;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Random utilities.
 *
 * @author fredsuvn
 */
public class JieRandom {

    /**
     * Returns next random boolean value.
     *
     * @return next random boolean value
     */
    public static boolean nextBoolean() {
        return random().nextBoolean();
    }

    /**
     * Fills given array with random boolean value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static boolean[] fill(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextBoolean();
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static byte nextByte() {
        return (byte) nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static byte nextByte(byte startInclusive, byte endExclusive) {
        return nextByte(startInclusive, (int) endExclusive);
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static byte nextByte(int startInclusive, int endExclusive) {
        return (byte) nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static byte[] fill(byte[] array) {
        random().nextBytes(array);
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static byte[] fill(byte[] array, byte startInclusive, byte endExclusive) {
        return fill(array, startInclusive, (int) endExclusive);
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static byte[] fill(byte[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextByte(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static short nextShort() {
        return (short) nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static short nextShort(short startInclusive, short endExclusive) {
        return nextShort(startInclusive, (int) endExclusive);
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static short nextShort(int startInclusive, int endExclusive) {
        return (short) nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static short[] fill(short[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextShort();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static short[] fill(short[] array, short startInclusive, short endExclusive) {
        return fill(array, startInclusive, (int) endExclusive);
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static short[] fill(short[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextShort(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static char nextChar() {
        return (char) nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static char nextChar(char startInclusive, char endExclusive) {
        return nextChar(startInclusive, (int) endExclusive);
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static char nextChar(int startInclusive, int endExclusive) {
        return (char) nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static char[] fill(char[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextChar();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static char[] fill(char[] array, char startInclusive, char endExclusive) {
        return fill(array, startInclusive, (int) endExclusive);
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static char[] fill(char[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextChar(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static int nextInt() {
        return random().nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static int nextInt(int startInclusive, int endExclusive) {
        return random().nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static int[] fill(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextInt();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static int[] fill(int[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextInt(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static long nextLong() {
        return random().nextLong();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static long nextLong(long startInclusive, long endExclusive) {
        return random().nextLong(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static long[] fill(long[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextLong();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static long[] fill(long[] array, long startInclusive, long endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextLong(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static float nextFloat() {
        return random().nextFloat();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static float nextFloat(float startInclusive, float endExclusive) {
        return nextFloatJdkVer8(startInclusive, endExclusive);
    }

    private static float nextFloatJdkVer8(float startInclusive, float endExclusive) {
        float f = (float) nextDouble(startInclusive, endExclusive);
        return JieMath.makeIn(f, startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static float[] fill(float[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextFloat();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static float[] fill(float[] array, float startInclusive, float endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextFloat(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static double nextDouble() {
        return random().nextDouble();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static double nextDouble(double startInclusive, double endExclusive) {
        return random().nextDouble(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static double[] fill(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextDouble();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static double[] fill(double[] array, double startInclusive, double endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextDouble(startInclusive, endExclusive);
        }
        return array;
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    /**
     * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
     * <p>
     * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
     * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
     * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
     * chance of returning B:
     * <pre>
     *     JieRandom.supplier(
     *         JieRandom.score(80, "A"),
     *         JieRandom.score(20, () -> "B")
     *     );
     * </pre>
     *
     * @param scores specified scores info
     * @param <T>    type of random object
     * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
     */
    @SafeVarargs
    public static <T> Supplier<T> supplier(Score<T>... scores) {
        if (JieArray.isEmpty(scores)) {
            throw new IllegalArgumentException("Empty scores!");
        }
        return new RandomSupplier<>(JieRandom::random, Jie.list(scores));
    }

    /**
     * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
     * <p>
     * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
     * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
     * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
     * chance of returning B:
     * <pre>
     *     JieRandom.supplier(Jie.list(
     *         JieRandom.score(80, "A"),
     *         JieRandom.score(20, () -> "B")
     *     ));
     * </pre>
     *
     * @param scores specified scores info
     * @param <T>    type of random object
     * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
     */
    public static <T> Supplier<T> supplier(Iterable<Score<T>> scores) {
        if (JieColl.isEmpty(scores)) {
            throw new IllegalArgumentException("Empty scores!");
        }
        return new RandomSupplier<>(JieRandom::random, scores);
    }

    /**
     * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
     * <p>
     * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
     * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
     * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
     * chance of returning B:
     * <pre>
     *     JieRandom.supplier(
     *         JieRandom.score(80, "A"),
     *         JieRandom.score(20, () -> "B")
     *     );
     * </pre>
     *
     * @param random base random to select the {@code score}
     * @param scores specified scores info
     * @param <T>    type of random object
     * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
     */
    @SafeVarargs
    public static <T> Supplier<T> supplier(Random random, Score<T>... scores) {
        if (JieArray.isEmpty(scores)) {
            throw new IllegalArgumentException("Empty scores!");
        }
        return new RandomSupplier<>(() -> random, Jie.list(scores));
    }

    /**
     * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
     * <p>
     * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
     * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
     * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
     * chance of returning B:
     * <pre>
     *     JieRandom.supplier(Jie.list(
     *         JieRandom.score(80, "A"),
     *         JieRandom.score(20, () -> "B")
     *     ));
     * </pre>
     *
     * @param random base random to select the {@code score}
     * @param scores specified scores info
     * @param <T>    type of random object
     * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
     */
    public static <T> Supplier<T> supplier(Random random, Iterable<Score<T>> scores) {
        if (JieColl.isEmpty(scores)) {
            throw new IllegalArgumentException("Empty scores!");
        }
        return new RandomSupplier<>(() -> random, scores);
    }

    /**
     * Returns a random {@code score} consists of a score and a value. See {@link #supplier(Score[])},
     * {@link #supplier(Iterable)}, {@link #supplier(Random, Score[])} and {@link #supplier(Random, Iterable)}.
     *
     * @param score the score
     * @param value value of the score
     * @param <T>   type of random object
     * @return a random {@code score}
     * @see #supplier(Score[])
     * @see #supplier(Iterable)
     * @see #supplier(Random, Iterable)
     * @see #supplier(Random, Score[])
     */
    public static <T> Score<T> score(int score, T value) {
        return score(score, () -> value);
    }

    /**
     * Returns a random {@code score} consists of a score and a supplier. See {@link #supplier(Score[])},
     * {@link #supplier(Iterable)}, {@link #supplier(Random, Score[])} and {@link #supplier(Random, Iterable)}.
     *
     * @param score    the score
     * @param supplier supplier of the score
     * @param <T>      type of random object
     * @return a random {@code score}
     * @see #supplier(Score[])
     * @see #supplier(Iterable)
     * @see #supplier(Random, Iterable)
     * @see #supplier(Random, Score[])
     */
    public static <T> Score<T> score(int score, Supplier<T> supplier) {
        return new Score<>(score, supplier);
    }

    /**
     * This class represents a score and an object supplier for a random supplier. See {@link #supplier(Score[])},
     * {@link #supplier(Iterable)}, * {@link #supplier(Random, Score[])} and {@link #supplier(Random, Iterable)}.
     *
     * @param <T> type of random object
     * @see #supplier(Score[])
     * @see #supplier(Iterable)
     * @see #supplier(Random, Iterable)
     * @see #supplier(Random, Score[])
     */
    @Immutable
    public static class Score<T> {

        private final int score;
        private final Supplier<T> supplier;

        private Score(int score, Supplier<T> supplier) {
            this.score = score;
            this.supplier = supplier;
        }
    }

    private static final class RandomSupplier<T> implements Supplier<T> {

        private final Supplier<Random> random;
        private final Node<T>[] nodes;
        private final int totalScore;

        RandomSupplier(Supplier<Random> random, Iterable<Score<T>> scores) {
            this.random = random;
            int totalScore = 0;
            List<Node<T>> nodeList = new LinkedList<>();
            for (Score<T> score : scores) {
                nodeList.add(new Node<>(totalScore, totalScore + score.score, score.supplier));
                totalScore += score.score;
            }
            this.nodes = JieColl.toArray(nodeList);
            this.totalScore = totalScore;
        }

        @Override
        public T get() {
            int next = random.get().nextInt(totalScore);
            return supply(next);
        }

        private T supply(int next) {
            Node<T> node = binarySearch(next);
            if (node == null) {
                throw new IllegalStateException("Score not found!");
            }
            return node.supplier.get();
        }

        @Nullable
        private Node<T> binarySearch(int next) {
            int left = 0;
            int right = nodes.length - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                Node<T> node = nodes[mid];
                int compare = compare(next, node);
                if (compare == 0) {
                    return node;
                }
                if (compare > 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            return null;
        }

        private int compare(int next, Node<T> node) {
            if (next < node.from) {
                return -1;
            }
            if (next >= node.to) {
                return 1;
            }
            return 0;
        }

        private static final class Node<T> {

            private final int from;
            private final int to;
            private final Supplier<T> supplier;

            private Node(int from, int to, Supplier<T> supplier) {
                this.from = from;
                this.to = to;
                this.supplier = supplier;
            }
        }
    }
}
