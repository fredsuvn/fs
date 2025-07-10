package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.math.MathKit;

import java.nio.ByteBuffer;
import java.util.Arrays;
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
     * Returns the default implementation of {@link Rng} based on the {@link ThreadLocalRandom} (so it is thread-safe).
     *
     * @return the default implementation of {@link Rng} based on the {@link ThreadLocalRandom} (so it is thread-safe)
     */
    static @ThreadSafe Rng getDefault() {
        return RngImpl.INST;
    }

    /**
     * Returns the next random boolean value.
     *
     * @return the next random boolean value
     */
    default boolean nextBoolean() {
        return nextInt() < 0;
    }

    /**
     * Returns the next random byte value.
     *
     * @return the next random byte value
     */
    default byte nextByte() {
        return (byte) nextInt();
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
    default byte nextByte(byte startInclusive, byte endExclusive) throws IllegalArgumentException {
        return (byte) nextInt(startInclusive, endExclusive);
    }

    /**
     * Returns the next random short value.
     *
     * @return the next random short value
     */
    default short nextShort() {
        return (short) nextInt();
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
    default short nextShort(short startInclusive, short endExclusive) throws IllegalArgumentException {
        return (short) nextInt(startInclusive, endExclusive);
    }

    /**
     * Returns the next random char value.
     *
     * @return the next random char value
     */
    default char nextChar() {
        return (char) nextInt();
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
    default char nextChar(char startInclusive, char endExclusive) throws IllegalArgumentException {
        return (char) nextInt(startInclusive & 0x0000ffff, endExclusive & 0x0000ffff);
    }

    /**
     * Returns the next random int value.
     *
     * @return the next random int value
     */
    int nextInt();

    /**
     * Returns the next random int {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random int {@code value} in the range: {@code startInclusive <= value < endExclusive}
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
     * Returns the next random long {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random long {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random float value between {@code 0} inclusive and {@code 1} exclusive.
     *
     * @return the next random float value between {@code 0} inclusive and {@code 1} exclusive
     */
    default float nextFloat() {
        return MathKit.makeIn((float) nextDouble(), 0, 1);
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
    default float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return MathKit.makeIn((float) nextDouble(startInclusive, endExclusive), startInclusive, endExclusive);
    }

    /**
     * Returns the next random double value between {@code 0} inclusive and {@code 1} exclusive.
     *
     * @return the next random double value between {@code 0} inclusive and {@code 1} exclusive
     */
    double nextDouble();

    /**
     * Returns the next random double {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random double {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException;

    /**
     * Fills the given array (from the specified offset up to the specified length) with the random value.
     *
     * @param array the given array
     * @param off   the specified offset
     * @param len   the specified length
     * @throws IndexOutOfBoundsException if {@code off < 0 || len < 0 || off + len > array.length}
     */
    default void nextBytes(byte @Nonnull [] array, int off, int len) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(array.length, off, len);
        for (int i = off; i < off + len; i++) {
            array[i] = nextByte();
        }
    }

    /**
     * Fills the given buffer with the random value. The buffer's position increments by the filled count.
     *
     * @param buffer the given buffer
     */
    default void nextBytes(@Nonnull ByteBuffer buffer) {
        if (buffer.hasArray()) {
            nextBytes(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
            buffer.position(buffer.limit());
        } else {
            buffer.put(fill(new byte[buffer.remaining()]));
        }
    }

    /**
     * Returns a new unlimited {@link IntStream} that produces random {@code int} values.
     *
     * @return a new unlimited {@link IntStream} that produces random {@code int} values
     */
    @Nonnull
    default IntStream ints() {
        return IntStream.generate(this::nextInt);
    }

    /**
     * Returns a new unlimited {@link IntStream} that produces random {@code int} value in the range:
     * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
     * {@code startInclusive} is always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new unlimited {@link IntStream} that produces random {@code int} value in the range:
     * {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    @Nonnull
    default IntStream ints(int startInclusive, int endExclusive) {
        IntSupplier supplier = intSupplier(startInclusive, endExclusive);
        return IntStream.generate(supplier);
    }

    /**
     * Returns a new {@link IntSupplier} that produces random {@code int} values.
     *
     * @return a new {@link IntSupplier} that produces random {@code int} values
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
     * Returns a new unlimited {@link LongStream} that produces random {@code long} values.
     *
     * @return a new unlimited {@link LongStream} that produces random {@code long} values
     */
    @Nonnull
    default LongStream longs() {
        return LongStream.generate(this::nextLong);
    }

    /**
     * Returns a new unlimited {@link LongStream} that produces random {@code long} value in the range:
     * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
     * {@code startInclusive} is always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new unlimited {@link LongStream} that produces random {@code long} value in the range:
     * {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    @Nonnull
    default LongStream longs(long startInclusive, long endExclusive) {
        LongSupplier supplier = longSupplier(startInclusive, endExclusive);
        return LongStream.generate(supplier);
    }

    /**
     * Returns a new {@link LongSupplier} that produces random {@code long} values.
     *
     * @return a new {@link LongSupplier} that produces random {@code long} values
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
    @Nonnull
    LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException;

    /**
     * Returns a new unlimited {@link DoubleStream} that produces random {@code double} values between {@code 0}
     * inclusive and {@code 1} exclusive.
     *
     * @return a new unlimited {@link DoubleStream} that produces random {@code double} values between {@code 0}
     * inclusive and {@code 1} exclusive
     */
    @Nonnull
    default DoubleStream doubles() {
        return DoubleStream.generate(this::nextDouble);
    }

    /**
     * Returns a new unlimited {@link DoubleStream} that produces random {@code double} value in the range:
     * {@code startInclusive <= value < endExclusive}. If {@code startInclusive == endExclusive}, then
     * {@code startInclusive} is always produced.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new unlimited {@link DoubleStream} that produces random {@code double} value in the range:
     * {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    @Nonnull
    default DoubleStream doubles(double startInclusive, double endExclusive) {
        DoubleSupplier supplier = doubleSupplier(startInclusive, endExclusive);
        return DoubleStream.generate(supplier);
    }

    /**
     * Returns a new {@link DoubleSupplier} that produces random {@code double} values.
     *
     * @return a new {@link DoubleSupplier} that produces random {@code double} values
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
     * Fills the given array with random boolean values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    default boolean @Nonnull [] fill(boolean @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextBoolean();
        }
        return array;
    }

    /**
     * Fills the given array with random byte values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    default byte @Nonnull [] fill(byte @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextByte();
        }
        return array;
    }

    /**
     * Fills the given array with random byte values (as if by {@link #nextByte(byte, byte)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default byte @Nonnull [] fill(
        byte @Nonnull [] array, byte startInclusive, byte endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        IntSupplier supplier = intSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) supplier.getAsInt();
        }
        return array;
    }

    /**
     * Fills the given array with random short values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    default short @Nonnull [] fill(short @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextShort();
        }
        return array;
    }

    /**
     * Fills the given array with random short values (as if by {@link #nextShort(short, short)}) and returns the
     * array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default short @Nonnull [] fill(
        short @Nonnull [] array, short startInclusive, short endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        IntSupplier supplier = intSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = (short) supplier.getAsInt();
        }
        return array;
    }

    /**
     * Fills the given array with random char values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    default char @Nonnull [] fill(char @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextChar();
        }
        return array;
    }

    /**
     * Fills the given array with random char values (as if by {@link #nextChar(char, char)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default char @Nonnull [] fill(
        char @Nonnull [] array, char startInclusive, char endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        IntSupplier supplier = intSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) supplier.getAsInt();
        }
        return array;
    }

    /**
     * Fills the given array with random int values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    default int @Nonnull [] fill(int @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextInt();
        }
        return array;
    }

    /**
     * Fills the given array with random int values (as if by {@link #nextInt(int, int)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default int @Nonnull [] fill(
        int @Nonnull [] array, int startInclusive, int endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        IntSupplier supplier = intSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.getAsInt();
        }
        return array;
    }

    /**
     * Fills the given array with random long values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    default long @Nonnull [] fill(long @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextLong();
        }
        return array;
    }

    /**
     * Fills the given array with random long values (as if by {@link #nextLong(long, long)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default long @Nonnull [] fill(
        long @Nonnull [] array, long startInclusive, long endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        LongSupplier supplier = longSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.getAsLong();
        }
        return array;
    }

    /**
     * Fills the given array with random float values (between {@code 0} inclusive and {@code 1} exclusive) and returns
     * the array.
     *
     * @param array the given array
     * @return the given array
     */
    default float @Nonnull [] fill(float @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextFloat();
        }
        return array;
    }

    /**
     * Fills the given array with random float values (as if by {@link #nextFloat(float, float)}) and returns the
     * array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default float @Nonnull [] fill(
        float @Nonnull [] array, float startInclusive, float endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        DoubleSupplier supplier = doubleSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = MathKit.makeIn((float) supplier.getAsDouble(), startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Fills the given array with random double values (between {@code 0} inclusive and {@code 1} exclusive) and returns
     * the array.
     *
     * @param array the given array
     * @return the given array
     */
    default double @Nonnull [] fill(double @Nonnull [] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextDouble();
        }
        return array;
    }

    /**
     * Fills the given array with random double values (as if by {@link #nextDouble(double, double)}) and returns the
     * array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    default double @Nonnull [] fill(
        double @Nonnull [] array, double startInclusive, double endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        DoubleSupplier supplier = doubleSupplier(startInclusive, endExclusive);
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.getAsDouble();
        }
        return array;
    }

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
     * Returns the next random double value between {@code 0} inclusive and {@code 1} exclusive.
     *
     * @return the next random double value between {@code 0} inclusive and {@code 1} exclusive
     */
    @Override
    default double getAsDouble() {
        return nextDouble();
    }
}
