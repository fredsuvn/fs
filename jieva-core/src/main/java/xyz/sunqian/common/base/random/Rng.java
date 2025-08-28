package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.CheckKit;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * The Random Number Generator, base interface to produce random numbers. It extends the {@link IntSupplier},
 * {@link LongSupplier} and {@link DoubleSupplier}, to supply random {@code int}, {@code long} and {@code double}
 * values.
 *
 * @author sunqian
 */
public interface Rng extends IntSupplier, LongSupplier, DoubleSupplier {

    /**
     * Returns a {@link Rng} instance.
     *
     * @return a {@link Rng} instance
     */
    static @Nonnull Rng newRng() {
        Random random = new Random();
        random.setSeed(System.nanoTime());
        return newRng(random);
    }

    /**
     * Returns a {@link Rng} instance with the specified seed.
     *
     * @param seed the specified seed
     * @return a {@link Rng} instance with the specified seed
     */
    static @Nonnull Rng newRng(long seed) {
        Random random = new Random();
        random.setSeed(seed);
        return newRng(random);
    }

    /**
     * Returns a {@link Rng} instance based on the specified {@link Random}.
     *
     * @param random the specified {@link Random}
     * @return a {@link Rng} instance based on the specified {@link Random}
     */
    static @Nonnull Rng newRng(@Nonnull Random random) {
        return RngBack.random(random);
    }

    /**
     * Returns a {@link Rng} instance based on the {@link ThreadLocalRandom}, and it is thread-safe and its
     * {@link #reset(long)} has no effect.
     *
     * @return a {@link Rng} instance based on the {@link ThreadLocalRandom}
     */
    static @Nonnull Rng threadLocal() {
        return RngBack.threadLocalRandom();
    }

    /**
     * Returns a {@link Rng} instance based on the {@link SecureRandom} with the specified random algorithm. This method
     * is equivalent to:
     * <pre>{@code
     * try {
     *     SecureRandom secureRandom = SecureRandom.getInstance(algorithm);
     *     return RngImpl.random(secureRandom);
     * } catch (NoSuchAlgorithmException e) {
     *     throw new UnsupportedOperationException(e);
     * }
     * }</pre>
     *
     * @param algorithm the specified random algorithm
     * @return a {@link Rng} instance based on the {@link SecureRandom} with the specified random algorithm
     * @throws UnsupportedOperationException if the specified algorithm is unsupported
     * @see SecureRandom#getInstance(String)
     */
    static @Nonnull Rng secure(@Nonnull String algorithm) throws UnsupportedOperationException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(algorithm);
            return RngBack.random(secureRandom);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Resets a random seed for this {@link Rng}.
     */
    default void reset() {
        reset(nextLong());
    }

    /**
     * Resets this {@link Rng} via the specified seed.
     *
     * @param seed the specified seed
     */
    void reset(long seed);

    /**
     * Resets this {@link Rng} via the specified seed.
     *
     * @param seed the specified seed
     */
    void reset(byte @Nonnull [] seed);

    /**
     * Returns the next random boolean value.
     *
     * @return the next random boolean value
     */
    default boolean nextBoolean() {
        return nextInt() < 0;
    }

    /**
     * Returns the next random int value.
     *
     * @return the next random int value
     */
    int nextInt();

    /**
     * Returns the next random int value in the range {@code [startInclusive, endExclusive)}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     * <p>
     * Note that if this method needs to be invoked multiple times, it is recommended to use {@code ints} or
     * {@code intSupplier} to reduce overhead.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random int value in the range {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random long value.
     *
     * @return the next random long value
     */
    long nextLong();

    /**
     * Returns the next random long value in the range {@code [startInclusive, endExclusive)}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     * <p>
     * Note that if this method needs to be invoked multiple times, it is recommended to use {@code longs} or
     * {@code longSupplier} to reduce overhead.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random long value in the range {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random float value in the range {@code [0.0, 1.0)}.
     *
     * @return the next random float value in the range {@code [0.0, 1.0)}
     */
    float nextFloat();

    /**
     * Returns the next random float value in the range {@code [startInclusive, endExclusive)}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     * <p>
     * Note that if this method needs to be invoked multiple times, it is recommended to use {@code doubles} or
     * {@code doubleSupplier} to reduce overhead.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random float value in the range {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random double value in the range {@code [0.0, 1.0)}.
     *
     * @return the next random double value in the range {@code [0.0, 1.0)}
     */
    double nextDouble();

    /**
     * Returns the next random double value in the range {@code [startInclusive, endExclusive)}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     * <p>
     * Note that if this method needs to be invoked multiple times, it is recommended to use {@code doubles} or
     * {@code doubleSupplier} to reduce overhead.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random double value in the range {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException;

    /**
     * Returns a new random byte array of the specified length.
     *
     * @param length the specified length of the array
     * @return a new random byte array of the specified length
     * @throws NegativeArraySizeException if {@code length < 0}
     */
    default byte @Nonnull [] nextBytes(int length) throws NegativeArraySizeException {
        byte[] bytes = new byte[length];
        nextBytes(bytes, 0, length);
        return bytes;
    }

    /**
     * Fills the specified byte array with random bytes.
     *
     * @param bytes the specified byte array
     */
    default void nextBytes(byte @Nonnull [] bytes) {
        nextBytes(bytes, 0, bytes.length);
    }

    /**
     * Fills the specified byte array with {@code len} random bytes, starting at the specified offset.
     *
     * @param bytes the specified byte array
     * @param off   the specified offset
     * @param len   the number of bytes to fill
     * @throws IndexOutOfBoundsException if {@code off < 0} or {@code len < 0} or {@code off + len > bytes.length}
     */
    default void nextBytes(byte @Nonnull [] bytes, int off, int len) throws IndexOutOfBoundsException {
        CheckKit.checkOffsetLength(bytes.length, off, len);
        int i = off;
        int end = off + len;
        for (int words = len >> 3; words-- > 0; ) {
            long rnd = nextLong();
            for (int n = 8; n-- > 0; rnd >>>= Byte.SIZE) {
                bytes[i++] = (byte) rnd;
            }
        }
        if (i < end) {
            for (long rnd = nextLong(); i < end; rnd >>>= Byte.SIZE) {
                bytes[i++] = (byte) rnd;
            }
        }
    }

    /**
     * Fills the specified byte buffer with random bytes. The buffer's position increments by the actual filled number.
     *
     * @param bytes the specified byte buffer
     */
    default void nextBytes(@Nonnull ByteBuffer bytes) {
        int remaining = bytes.remaining();
        if (bytes.hasArray()) {
            nextBytes(bytes.array(), bytes.arrayOffset() + bytes.position(), remaining);
            bytes.position(bytes.position() + remaining);
        } else {
            byte[] rd = new byte[remaining];
            nextBytes(rd);
            bytes.put(rd);
        }
    }

    /**
     * Returns a new unlimited {@link IntStream} that produces random int values.
     *
     * @return a new unlimited {@link IntStream} that produces random int values
     */
    default @Nonnull IntStream ints() {
        return IntStream.generate(this::nextInt);
    }

    /**
     * Returns a new unlimited {@link IntStream} that produces random int values in the range
     * {@code [startInclusive, endExclusive)}. If {@code startInclusive == endExclusive}, then {@code startInclusive} is
     * always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new unlimited {@link IntStream} that produces random int values in the range
     * {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default @Nonnull IntStream ints(int startInclusive, int endExclusive) throws IllegalArgumentException {
        IntSupplier supplier = intSupplier(startInclusive, endExclusive);
        return IntStream.generate(supplier);
    }

    /**
     * Returns a new {@link IntStream} that produces random int values, and the stream size is limited by the given
     * size.
     *
     * @param size the given stream size
     * @return a new {@link IntStream} that produces random int values, and the stream size is limited by the given size
     * @throws IllegalArgumentException if {@code size < 0}
     */
    default @Nonnull IntStream ints(long size) throws IllegalArgumentException {
        return ints().limit(size);
    }

    /**
     * Returns a new {@link IntStream} that produces random int values in the range
     * {@code [startInclusive, endExclusive)}, and the stream size is limited by the given size. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is always produced.
     *
     * @param size           the given stream size
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new {@link IntStream} that produces random int values in the range
     * {@code [startInclusive, endExclusive)}, and the stream size is limited by the given size
     * @throws IllegalArgumentException if {@code size < 0} or {@code startInclusive > endExclusive}
     */
    default @Nonnull IntStream ints(long size, int startInclusive, int endExclusive) throws IllegalArgumentException {
        return ints(startInclusive, endExclusive).limit(size);
    }

    /**
     * Returns a new {@link IntSupplier} that produces random int values.
     *
     * @return a new {@link IntSupplier} that produces random int values
     */
    default @Nonnull IntSupplier intSupplier() {
        return this::nextInt;
    }

    /**
     * Returns a new {@link IntSupplier} that produces random {@code int} value in the range:
     * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
     * {@code startInclusive} is always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new {@link IntSupplier} that produces random {@code int} value in the range:
     * {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    @Nonnull
    IntSupplier intSupplier(int startInclusive, int endExclusive) throws IllegalArgumentException;

    /**
     * Returns a new unlimited {@link LongStream} that produces random long values.
     *
     * @return a new unlimited {@link LongStream} that produces random long values
     */
    default @Nonnull LongStream longs() {
        return LongStream.generate(this::nextLong);
    }

    /**
     * Returns a new unlimited {@link LongStream} that produces random long values in the range
     * {@code [startInclusive, endExclusive)}. If {@code startInclusive == endExclusive}, then {@code startInclusive} is
     * always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new unlimited {@link LongStream} that produces random long values in the range
     * {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default @Nonnull LongStream longs(long startInclusive, long endExclusive) throws IllegalArgumentException {
        LongSupplier supplier = longSupplier(startInclusive, endExclusive);
        return LongStream.generate(supplier);
    }

    /**
     * Returns a new {@link LongStream} that produces random long values, and the stream size is limited by the given
     * size.
     *
     * @param size the given stream size
     * @return a new {@link LongStream} that produces random long values, and the stream size is limited by the given
     * size
     * @throws IllegalArgumentException if {@code size < 0}
     */
    default @Nonnull LongStream longs(long size) throws IllegalArgumentException {
        return longs().limit(size);
    }

    /**
     * Returns a new {@link LongStream} that produces random long values in the range
     * {@code [startInclusive, endExclusive)}, and the stream size is limited by the given size. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is always produced.
     *
     * @param size           the given stream size
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new {@link LongStream} that produces random long values in the range
     * {@code [startInclusive, endExclusive)}, and the stream size is limited by the given size
     * @throws IllegalArgumentException if {@code size < 0} or {@code startInclusive > endExclusive}
     */
    default @Nonnull LongStream longs(long size, long startInclusive, long endExclusive) throws IllegalArgumentException {
        return longs(startInclusive, endExclusive).limit(size);
    }

    /**
     * Returns a new {@link LongSupplier} that produces random long values.
     *
     * @return a new {@link LongSupplier} that produces random long values
     */
    default @Nonnull LongSupplier longSupplier() {
        return this::nextLong;
    }

    /**
     * Returns a new {@link LongSupplier} that produces random {@code long} value in the range:
     * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
     * {@code startInclusive} is always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new {@link LongSupplier} that produces random {@code long} value in the range:
     * {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException;

    /**
     * Returns a new unlimited {@link DoubleStream} that produces random double values in the range {@code [0.0, 1.0)}.
     *
     * @return a new unlimited {@link DoubleStream} that produces random double values in the range {@code [0.0, 1.0)}
     */
    default @Nonnull DoubleStream doubles() {
        return DoubleStream.generate(this::nextDouble);
    }

    /**
     * Returns a new unlimited {@link DoubleStream} that produces random double values in the range
     * {@code [startInclusive, endExclusive)}. If {@code startInclusive == endExclusive}, then {@code startInclusive} is
     * always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new unlimited {@link DoubleStream} that produces random double values in the range
     * {@code [startInclusive, endExclusive)}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default @Nonnull DoubleStream doubles(double startInclusive, double endExclusive) throws IllegalArgumentException {
        DoubleSupplier supplier = doubleSupplier(startInclusive, endExclusive);
        return DoubleStream.generate(supplier);
    }

    /**
     * Returns a new {@link DoubleStream} that produces random double values in the range {@code [0.0, 1.0)}, and the
     * stream size is limited by the given size.
     *
     * @param size the given stream size
     * @return a new {@link DoubleStream} that produces random double values in the range {@code [0.0, 1.0)}, and the
     * stream size is limited by the given size
     * @throws IllegalArgumentException if {@code size < 0}
     */
    default @Nonnull DoubleStream doubles(long size) throws IllegalArgumentException {
        return doubles().limit(size);
    }

    /**
     * Returns a new {@link DoubleStream} that produces random double values in the range
     * {@code [startInclusive, endExclusive)}, and the stream size is limited by the given size. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is always produced.
     *
     * @param size           the given stream size
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new {@link DoubleStream} that produces random double values in the range
     * {@code [startInclusive, endExclusive)}, and the stream size is limited by the given size
     * @throws IllegalArgumentException if {@code size < 0} or {@code startInclusive > endExclusive}
     */
    default @Nonnull DoubleStream doubles(long size, double startInclusive, double endExclusive) throws IllegalArgumentException {
        return doubles(startInclusive, endExclusive).limit(size);
    }

    /**
     * Returns a new {@link DoubleSupplier} that produces random double values in the range {@code [0.0, 1.0)}.
     *
     * @return a new {@link DoubleSupplier} that produces random double values in the range {@code [0.0, 1.0)}
     */
    default @Nonnull DoubleSupplier doubleSupplier() {
        return this::nextDouble;
    }

    /**
     * Returns a new {@link DoubleSupplier} that produces random {@code double} value in the range:
     * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
     * {@code startInclusive} is always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new {@link DoubleSupplier} that produces random {@code double} value in the range:
     * {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    @Nonnull
    DoubleSupplier doubleSupplier(double startInclusive, double endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random int value.
     *
     * @return the next random int value
     */
    @Override
    default int getAsInt() {
        return nextInt();
    }

    /**
     * Returns the next random long value.
     *
     * @return the next random long value
     */
    @Override
    default long getAsLong() {
        return nextLong();
    }

    /**
     * Returns the next random double value in the range {@code [0.0, 1.0)}.
     *
     * @return the next random double value in the range {@code [0.0, 1.0)}
     */
    @Override
    default double getAsDouble() {
        return nextDouble();
    }
}
