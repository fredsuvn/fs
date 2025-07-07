package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieCollect;

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
public class RandomKit {

    private static final @Nonnull Randomer rd = Randomer.newRandomer();

    /**
     * Returns the next random boolean value.
     *
     * @return the next random boolean value
     */
    public static boolean nextBoolean() {
        return rd.nextBoolean();
    }

    /**
     * Fills the given array with random boolean values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static boolean @Nonnull [] fill(boolean @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Returns the next random byte value.
     *
     * @return the next random byte value
     */
    public static byte nextByte() {
        return rd.nextByte();
    }

    /**
     * Returns the next random byte {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random byte {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static byte nextByte(byte startInclusive, byte endExclusive) throws IllegalArgumentException {
        return rd.nextByte(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random byte values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static byte @Nonnull [] fill(byte @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random byte values (from {@link #nextByte(byte, byte)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static byte @Nonnull [] fill(
        byte @Nonnull [] array, byte startInclusive, byte endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    /**
     * Returns the next random short value.
     *
     * @return the next random short value
     */
    public static short nextShort() {
        return rd.nextShort();
    }

    /**
     * Returns the next random short {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random short {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static short nextShort(short startInclusive, short endExclusive) throws IllegalArgumentException {
        return rd.nextShort(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random short values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static short @Nonnull [] fill(short @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random short values (from {@link #nextShort(short, short)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static short @Nonnull [] fill(
        short @Nonnull [] array, short startInclusive, short endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    /**
     * Returns the next random char value.
     *
     * @return the next random char value
     */
    public static char nextChar() {
        return rd.nextChar();
    }

    /**
     * Returns the next random char {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random char {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static char nextChar(char startInclusive, char endExclusive) throws IllegalArgumentException {
        return rd.nextChar(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random char values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static char @Nonnull [] fill(char @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random char values (from {@link #nextChar(char, char)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static char @Nonnull [] fill(
        char @Nonnull [] array, char startInclusive, char endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    /**
     * Returns the next random int value.
     *
     * @return the next random int value
     */
    public static int nextInt() {
        return rd.nextInt();
    }

    /**
     * Returns the next random int {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random int {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
        return rd.nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random int values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static int @Nonnull [] fill(int @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random int values (from {@link #nextInt(int, int)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static int @Nonnull [] fill(
        int @Nonnull [] array, int startInclusive, int endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    /**
     * Returns the next random long value.
     *
     * @return the next random long value
     */
    public static long nextLong() {
        return rd.nextLong();
    }

    /**
     * Returns the next random long {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random long {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
        return rd.nextLong(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random long values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static long @Nonnull [] fill(long @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random long values (from {@link #nextLong(long, long)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static long @Nonnull [] fill(
        long @Nonnull [] array, long startInclusive, long endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    /**
     * Returns the next random float value.
     *
     * @return the next random float value
     */
    public static float nextFloat() {
        return rd.nextFloat();
    }

    /**
     * Returns the next random float {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random float {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException {
        return rd.nextFloat(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random float values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static float @Nonnull [] fill(float @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random float values (from {@link #nextFloat(float, float)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static float @Nonnull [] fill(
        float @Nonnull [] array, float startInclusive, float endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    /**
     * Returns the next random double value.
     *
     * @return the next random double value
     */
    public static double nextDouble() {
        return rd.nextDouble();
    }

    /**
     * Returns the next random double {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random double {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
        return rd.nextDouble(startInclusive, endExclusive);
    }

    /**
     * Fills the given array with random double values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    public static double @Nonnull [] fill(double @Nonnull [] array) {
        return rd.fill(array);
    }

    /**
     * Fills the given array with random double values (from {@link #nextDouble(double, double)}) and returns the
     * array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    public static double @Nonnull [] fill(
        double @Nonnull [] array, double startInclusive, double endExclusive
    ) throws IllegalArgumentException {
        return rd.fill(array, startInclusive, endExclusive);
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    // /**
    //  * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
    //  * <p>
    //  * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
    //  * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
    //  * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
    //  * chance of returning B:
    //  * <pre>
    //  *     JieRandom.supplier(
    //  *         JieRandom.score(80, "A"),
    //  *         JieRandom.score(20, () -> "B")
    //  *     );
    //  * </pre>
    //  *
    //  * @param scores specified scores info
    //  * @param <T>    type of random object
    //  * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
    //  */
    // @SafeVarargs
    // public static <T> Supplier<T> supplier(Score<T>... scores) {
    //     if (JieArray.isEmpty(scores)) {
    //         throw new IllegalArgumentException("Empty scores!");
    //     }
    //     return new RandomSupplier<>(RandomKit::random, Jie.list(scores));
    // }
    //
    // /**
    //  * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
    //  * <p>
    //  * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
    //  * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
    //  * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
    //  * chance of returning B:
    //  * <pre>
    //  *     JieRandom.supplier(Jie.list(
    //  *         JieRandom.score(80, "A"),
    //  *         JieRandom.score(20, () -> "B")
    //  *     ));
    //  * </pre>
    //  *
    //  * @param scores specified scores info
    //  * @param <T>    type of random object
    //  * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
    //  */
    // public static <T> Supplier<T> supplier(Iterable<Score<T>> scores) {
    //     if (JieCollect.isEmpty(scores)) {
    //         throw new IllegalArgumentException("Empty scores!");
    //     }
    //     return new RandomSupplier<>(RandomKit::random, scores);
    // }
    //
    // /**
    //  * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
    //  * <p>
    //  * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
    //  * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
    //  * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
    //  * chance of returning B:
    //  * <pre>
    //  *     JieRandom.supplier(
    //  *         JieRandom.score(80, "A"),
    //  *         JieRandom.score(20, () -> "B")
    //  *     );
    //  * </pre>
    //  *
    //  * @param random base random to select the {@code score}
    //  * @param scores specified scores info
    //  * @param <T>    type of random object
    //  * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
    //  */
    // @SafeVarargs
    // public static <T> Supplier<T> supplier(Random random, Score<T>... scores) {
    //     if (JieArray.isEmpty(scores)) {
    //         throw new IllegalArgumentException("Empty scores!");
    //     }
    //     return new RandomSupplier<>(() -> random, Jie.list(scores));
    // }
    //
    // /**
    //  * Returns a random {@link Supplier} which products a random object for each {@link Supplier#get()}.
    //  * <p>
    //  * When the {@link Supplier#get()} of returned supplier is invoked, the supplier first randomly selects a
    //  * {@code score} based on their proportion, and then returns the object generated from the supplier or value of
    //  * selected {@code score}. For example, to get a random supplier which has an 80% chance of returning A and a 20%
    //  * chance of returning B:
    //  * <pre>
    //  *     JieRandom.supplier(Jie.list(
    //  *         JieRandom.score(80, "A"),
    //  *         JieRandom.score(20, () -> "B")
    //  *     ));
    //  * </pre>
    //  *
    //  * @param random base random to select the {@code score}
    //  * @param scores specified scores info
    //  * @param <T>    type of random object
    //  * @return a {@link Supplier} which products a random object for each {@link Supplier#get()}
    //  */
    // public static <T> Supplier<T> supplier(Random random, Iterable<Score<T>> scores) {
    //     if (JieCollect.isEmpty(scores)) {
    //         throw new IllegalArgumentException("Empty scores!");
    //     }
    //     return new RandomSupplier<>(() -> random, scores);
    // }
    //
    // /**
    //  * Returns a random {@code score} consists of a score and a value. See {@link #supplier(Score[])},
    //  * {@link #supplier(Iterable)}, {@link #supplier(Random, Score[])} and {@link #supplier(Random, Iterable)}.
    //  *
    //  * @param score the score
    //  * @param value value of the score
    //  * @param <T>   type of random object
    //  * @return a random {@code score}
    //  * @see #supplier(Score[])
    //  * @see #supplier(Iterable)
    //  * @see #supplier(Random, Iterable)
    //  * @see #supplier(Random, Score[])
    //  */
    // public static <T> Probability<T> probability(int score, T value) {
    //     return score(score, () -> value);
    // }
    //
    // /**
    //  * Returns a random {@code score} consists of a score and a supplier. See {@link #supplier(Score[])},
    //  * {@link #supplier(Iterable)}, {@link #supplier(Random, Score[])} and {@link #supplier(Random, Iterable)}.
    //  *
    //  * @param score    the score
    //  * @param supplier supplier of the score
    //  * @param <T>      type of random object
    //  * @return a random {@code score}
    //  * @see #supplier(Score[])
    //  * @see #supplier(Iterable)
    //  * @see #supplier(Random, Iterable)
    //  * @see #supplier(Random, Score[])
    //  */
    // public static <T> Score<T> score(int score, Supplier<T> supplier) {
    //     return new Score<>(score, supplier);
    // }


}
