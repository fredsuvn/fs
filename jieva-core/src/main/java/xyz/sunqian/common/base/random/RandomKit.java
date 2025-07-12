// package xyz.sunqian.common.base.random;
//
// import xyz.sunqian.annotations.Nonnull;
//
// import java.nio.ByteBuffer;
// import java.util.Arrays;
// import java.util.function.DoubleSupplier;
// import java.util.function.IntSupplier;
// import java.util.function.LongSupplier;
// import java.util.function.Supplier;
// import java.util.stream.DoubleStream;
// import java.util.stream.IntStream;
// import java.util.stream.LongStream;
//
// /**
//  * Random utilities.
//  *
//  * @author fredsuvn
//  */
// public class RandomKit {
//
//     private static final @Nonnull Rng rng = Rng.threadLocal();
//     private static final @Nonnull Rog rog = Rog.getDefault();
//
//     /**
//      * Returns the next random boolean value.
//      *
//      * @return the next random boolean value
//      */
//     public static boolean nextBoolean() {
//         return rng.nextBoolean();
//     }
//
//     /**
//      * Returns the next random byte value.
//      *
//      * @return the next random byte value
//      */
//     public static byte nextByte() {
//         return rng.nextByte();
//     }
//
//     /**
//      * Returns the next random byte {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random byte {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static byte nextByte(byte startInclusive, byte endExclusive) throws IllegalArgumentException {
//         return rng.nextByte(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns the next random short value.
//      *
//      * @return the next random short value
//      */
//     public static short nextShort() {
//         return rng.nextShort();
//     }
//
//     /**
//      * Returns the next random short {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random short {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static short nextShort(short startInclusive, short endExclusive) throws IllegalArgumentException {
//         return rng.nextShort(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns the next random char value.
//      *
//      * @return the next random char value
//      */
//     public static char nextChar() {
//         return rng.nextChar();
//     }
//
//     /**
//      * Returns the next random char {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random char {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static char nextChar(char startInclusive, char endExclusive) throws IllegalArgumentException {
//         return rng.nextChar(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns the next random int value.
//      *
//      * @return the next random int value
//      */
//     public static int nextInt() {
//         return rng.nextInt();
//     }
//
//     /**
//      * Returns the next random int {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random int {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
//         return rng.nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns the next random long value.
//      *
//      * @return the next random long value
//      */
//     public static long nextLong() {
//         return rng.nextLong();
//     }
//
//     /**
//      * Returns the next random long {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random long {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
//         return rng.nextLong(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns the next random float value between {@code 0} inclusive and {@code 1} exclusive.
//      *
//      * @return the next random float value between {@code 0} inclusive and {@code 1} exclusive
//      */
//     public static float nextFloat() {
//         return rng.nextFloat();
//     }
//
//     /**
//      * Returns the next random float {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random float {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException {
//         return rng.nextFloat(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns the next random double value between {@code 0} inclusive and {@code 1} exclusive.
//      *
//      * @return the next random double value between {@code 0} inclusive and {@code 1} exclusive
//      */
//     public static double nextDouble() {
//         return rng.nextDouble();
//     }
//
//     /**
//      * Returns the next random double {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
//      * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return the next random double {@code value} in the range: {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
//         return rng.nextDouble(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills the given array (from the specified offset up to the specified length) with the random value.
//      *
//      * @param array the given array
//      * @param off   the specified offset
//      * @param len   the specified length
//      * @throws IndexOutOfBoundsException if {@code off < 0 || len < 0 || off + len > array.length}
//      */
//     public static void nextBytes(byte @Nonnull [] array, int off, int len) throws IndexOutOfBoundsException {
//         rng.nextBytes(array, off, len);
//     }
//
//     /**
//      * Fills the given buffer with the random value. The buffer's position increments by the filled count.
//      *
//      * @param buffer the given buffer
//      */
//     public static void nextBytes(@Nonnull ByteBuffer buffer) {
//         rng.nextBytes(buffer);
//     }
//
//     /**
//      * Returns a new unlimited {@link IntStream} that produces random {@code int} values.
//      *
//      * @return a new unlimited {@link IntStream} that produces random {@code int} values
//      */
//     public static @Nonnull IntStream ints() {
//         return rng.ints();
//     }
//
//     /**
//      * Returns a new unlimited {@link IntStream} that produces random {@code int} value in the range:
//      * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
//      * {@code startInclusive} is always produced.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return a new unlimited {@link IntStream} that produces random {@code int} value in the range:
//      * {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static @Nonnull IntStream ints(int startInclusive, int endExclusive) {
//         return rng.ints(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns a new {@link IntSupplier} that produces random {@code int} values.
//      *
//      * @return a new {@link IntSupplier} that produces random {@code int} values
//      */
//     public static @Nonnull IntSupplier intSupplier() {
//         return rng.intSupplier();
//     }
//
//     /**
//      * Returns a new {@link IntSupplier} that produces random {@code int} value in the range:
//      * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
//      * {@code startInclusive} is always produced.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return a new {@link IntSupplier} that produces random {@code int} value in the range:
//      * {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static @Nonnull IntSupplier intSupplier(
//         int startInclusive, int endExclusive
//     ) throws IllegalArgumentException {
//         return rng.intSupplier(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns a new unlimited {@link LongStream} that produces random {@code long} values.
//      *
//      * @return a new unlimited {@link LongStream} that produces random {@code long} values
//      */
//     public static @Nonnull LongStream longs() {
//         return rng.longs();
//     }
//
//     /**
//      * Returns a new unlimited {@link LongStream} that produces random {@code long} value in the range:
//      * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
//      * {@code startInclusive} is always produced.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return a new unlimited {@link LongStream} that produces random {@code long} value in the range:
//      * {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static @Nonnull LongStream longs(long startInclusive, long endExclusive) {
//         return rng.longs(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns a new {@link LongSupplier} that produces random {@code long} values.
//      *
//      * @return a new {@link LongSupplier} that produces random {@code long} values
//      */
//     public static @Nonnull LongSupplier longSupplier() {
//         return rng.longSupplier();
//     }
//
//     /**
//      * Returns a new {@link LongSupplier} that produces random {@code long} value in the range:
//      * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
//      * {@code startInclusive} is always produced.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return a new {@link LongSupplier} that produces random {@code long} value in the range:
//      * {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static @Nonnull LongSupplier longSupplier(
//         long startInclusive, long endExclusive
//     ) throws IllegalArgumentException {
//         return rng.longSupplier(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns a new unlimited {@link DoubleStream} that produces random {@code double} values between {@code 0}
//      * inclusive and {@code 1} exclusive.
//      *
//      * @return a new unlimited {@link DoubleStream} that produces random {@code double} values between {@code 0}
//      * inclusive and {@code 1} exclusive
//      */
//     public static @Nonnull DoubleStream doubles() {
//         return rng.doubles();
//     }
//
//     /**
//      * Returns a new unlimited {@link DoubleStream} that produces random {@code double} value in the range:
//      * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
//      * {@code startInclusive} is always produced.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return a new unlimited {@link DoubleStream} that produces random {@code double} value in the range:
//      * {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static @Nonnull DoubleStream doubles(double startInclusive, double endExclusive) {
//         return rng.doubles(startInclusive, endExclusive);
//     }
//
//     /**
//      * Returns a new {@link DoubleSupplier} that produces random {@code double} values.
//      *
//      * @return a new {@link DoubleSupplier} that produces random {@code double} values
//      */
//     public static @Nonnull DoubleSupplier doubleSupplier() {
//         return rng.doubleSupplier();
//     }
//
//     /**
//      * Returns a new {@link DoubleSupplier} that produces random {@code double} value in the range:
//      * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
//      * {@code startInclusive} is always produced.
//      *
//      * @param startInclusive the start value inclusive
//      * @param endExclusive   the end value exclusive
//      * @return a new {@link DoubleSupplier} that produces random {@code double} value in the range:
//      * {@code startInclusive <= value < endExclusive}
//      * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//      */
//     public static @Nonnull DoubleSupplier doubleSupplier(
//         double startInclusive, double endExclusive
//     ) throws IllegalArgumentException {
//         return rng.doubleSupplier(startInclusive, endExclusive);
//     }
//
//     // /**
//     //  * Fills the given array with random boolean values and returns the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static boolean @Nonnull [] fill(boolean @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random byte values and returns the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static byte @Nonnull [] fill(byte @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random byte values (as if by {@link #nextByte(byte, byte)}) and returns the array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static byte @Nonnull [] fill(
//     //     byte @Nonnull [] array, byte startInclusive, byte endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random short values and returns the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static short @Nonnull [] fill(short @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random short values (as if by {@link #nextShort(short, short)}) and returns the
//     //  * array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static short @Nonnull [] fill(
//     //     short @Nonnull [] array, short startInclusive, short endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random char values and returns the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static char @Nonnull [] fill(char @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random char values (as if by {@link #nextChar(char, char)}) and returns the array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static char @Nonnull [] fill(
//     //     char @Nonnull [] array, char startInclusive, char endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random int values and returns the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static int @Nonnull [] fill(int @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random int values (as if by {@link #nextInt(int, int)}) and returns the array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static int @Nonnull [] fill(
//     //     int @Nonnull [] array, int startInclusive, int endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random long values and returns the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static long @Nonnull [] fill(long @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random long values (as if by {@link #nextLong(long, long)}) and returns the array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static long @Nonnull [] fill(
//     //     long @Nonnull [] array, long startInclusive, long endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random float values (between {@code 0} inclusive and {@code 1} exclusive) and returns
//     //  * the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static float @Nonnull [] fill(float @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random float values (as if by {@link #nextFloat(float, float)}) and returns the
//     //  * array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static float @Nonnull [] fill(
//     //     float @Nonnull [] array, float startInclusive, float endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random double values (between {@code 0} inclusive and {@code 1} exclusive) and returns
//     //  * the array.
//     //  *
//     //  * @param array the given array
//     //  * @return the given array
//     //  */
//     // public static double @Nonnull [] fill(double @Nonnull [] array) {
//     //     return rng.fill(array);
//     // }
//     //
//     // /**
//     //  * Fills the given array with random double values (as if by {@link #nextDouble(double, double)}) and returns the
//     //  * array.
//     //  *
//     //  * @param array          the given array
//     //  * @param startInclusive the start value inclusive
//     //  * @param endExclusive   the end value exclusive
//     //  * @return the given array
//     //  * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
//     //  */
//     // public static double @Nonnull [] fill(
//     //     double @Nonnull [] array, double startInclusive, double endExclusive
//     // ) throws IllegalArgumentException {
//     //     return rng.fill(array, startInclusive, endExclusive);
//     // }
//
//     /**
//      * Returns a {@link Supplier} which produces the random objects, the usage example:
//      * <pre>{@code
//      * Supplier<String> strSupplier = RandomKit.supplier(
//      *     Rog.probability(20, () -> "a"), // 20% hit probability
//      *     Rog.probability(80, () -> "b")  // 80% hit probability
//      * );
//      * }</pre>
//      * <p>
//      * Each provided {@link Rog.Probability}, which has a score and a supplier, represents the hit probability for its
//      * supplier. Let the {@code sum(score)} be the total score of all provided {@link Rog.Probability}s, the hit
//      * probability for each {@link Rog.Probability} is {@code score / sum(score)}. Note the total score of all provided
//      * {@link Rog.Probability}s can not overflow the maximum value of {@code long}.
//      * <p>
//      * This method uses {@link Rng#threadLocal()} to generate the random long value, which is used to calculate the hit
//      * probability.
//      *
//      * @param probabilities the provided {@link Rog.Probability}s
//      * @param <T>           the type of the random objects
//      * @return a {@link Supplier} which produces the random objects
//      */
//     @SafeVarargs
//     public static <T> @Nonnull Supplier<T> supplier(
//         @Nonnull Rog.Probability<? extends T> @Nonnull ... probabilities
//     ) {
//         return rog.supplier(Arrays.asList(probabilities));
//     }
//
//     /**
//      * Returns a {@link Supplier} which produces the random objects, the usage example:
//      * <pre>{@code
//      * Supplier<String> strSupplier = RandomKit.supplier(
//      *     scoreGenerator,
//      *     Rog.probability(20, () -> "a"), // 20% hit probability
//      *     Rog.probability(80, () -> "b"), // 80% hit probability
//      * );
//      * }</pre>
//      * <p>
//      * Each provided {@link Rog.Probability}, which has a score and a supplier, represents the hit probability for its
//      * supplier. Let the {@code sum(score)} be the total score of all provided {@link Rog.Probability}s, the hit
//      * probability for each {@link Rog.Probability} is {@code score / sum(score)}. Note the total score of all provided
//      * {@link Rog.Probability}s can not overflow the maximum value of {@code long}.
//      * <p>
//      * The {@code scoreGenerator} is a {@link LongSupplier} which produces a random long value, the long value is used
//      * to calculate the hit probability.
//      *
//      * @param scoreGenerator a {@link LongSupplier} which produces a random long value
//      * @param probabilities  the provided {@link Rog.Probability}s
//      * @param <T>            the type of the random objects
//      * @return a {@link Supplier} which produces the random objects
//      */
//     @SafeVarargs
//     public static <T> @Nonnull Supplier<T> supplier(
//         @Nonnull LongSupplier scoreGenerator,
//         @Nonnull Rog.Probability<? extends T> @Nonnull ... probabilities
//     ) {
//         return rog.supplier(scoreGenerator, Arrays.asList(probabilities));
//     }
// }
