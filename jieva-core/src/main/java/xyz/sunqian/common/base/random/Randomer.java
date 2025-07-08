package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/**
 * This interface is used to generate random values.
 *
 * @author sunqian
 */
public interface Randomer extends IntSupplier, LongSupplier, DoubleSupplier {

    /**
     * Returns a new {@link Randomer} based on the {@link ThreadLocalRandom#current()}.
     *
     * @return a new {@link Randomer} based on the {@link ThreadLocalRandom#current()}
     */
    static Randomer newRandomer() {
        return new RandomerImpl();
    }

    /**
     * Returns the next random boolean value.
     *
     * @return the next random boolean value
     */
    boolean nextBoolean();

    /**
     * Returns the next random byte value.
     *
     * @return the next random byte value
     */
    byte nextByte();

    /**
     * Returns the next random byte {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random byte {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    byte nextByte(byte startInclusive, byte endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random short value.
     *
     * @return the next random short value
     */
    short nextShort();

    /**
     * Returns the next random short {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random short {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    short nextShort(short startInclusive, short endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random char value.
     *
     * @return the next random char value
     */
    char nextChar();

    /**
     * Returns the next random char {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random char {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    char nextChar(char startInclusive, char endExclusive) throws IllegalArgumentException;

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
     * Returns the next random float value.
     *
     * @return the next random float value
     */
    float nextFloat();

    /**
     * Returns the next random float {@code value} in the range: {@code startInclusive <= value < endExclusive}. If
     * {@code startInclusive == endExclusive}, then {@code startInclusive} is returned.
     *
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the next random float {@code value} in the range: {@code startInclusive <= value < endExclusive}
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException;

    /**
     * Returns the next random double value.
     *
     * @return the next random double value
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
     * Fills the given array with random boolean values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    boolean @Nonnull [] fill(boolean @Nonnull [] array);

    /**
     * Fills the given array with random byte values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    byte @Nonnull [] fill(byte @Nonnull [] array);

    /**
     * Fills the given array with random byte values (from {@link #nextByte(byte, byte)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    byte @Nonnull [] fill(
        byte @Nonnull [] array, byte startInclusive, byte endExclusive
    ) throws IllegalArgumentException;

    /**
     * Fills the given array with random short values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    short @Nonnull [] fill(short @Nonnull [] array);

    /**
     * Fills the given array with random short values (from {@link #nextShort(short, short)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    short @Nonnull [] fill(
        short @Nonnull [] array, short startInclusive, short endExclusive
    ) throws IllegalArgumentException;

    /**
     * Fills the given array with random char values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    char @Nonnull [] fill(char @Nonnull [] array);

    /**
     * Fills the given array with random char values (from {@link #nextChar(char, char)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    char @Nonnull [] fill(
        char @Nonnull [] array, char startInclusive, char endExclusive
    ) throws IllegalArgumentException;

    /**
     * Fills the given array with random int values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    int @Nonnull [] fill(int @Nonnull [] array);

    /**
     * Fills the given array with random int values (from {@link #nextInt(int, int)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    int @Nonnull [] fill(
        int @Nonnull [] array, int startInclusive, int endExclusive
    ) throws IllegalArgumentException;

    /**
     * Fills the given array with random long values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    long @Nonnull [] fill(long @Nonnull [] array);

    /**
     * Fills the given array with random long values (from {@link #nextLong(long, long)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    long @Nonnull [] fill(
        long @Nonnull [] array, long startInclusive, long endExclusive
    ) throws IllegalArgumentException;

    /**
     * Fills the given array with random float values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    float @Nonnull [] fill(float @Nonnull [] array);

    /**
     * Fills the given array with random float values (from {@link #nextFloat(float, float)}) and returns the array.
     *
     * @param array          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the given array
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive}
     */
    float @Nonnull [] fill(
        float @Nonnull [] array, float startInclusive, float endExclusive
    ) throws IllegalArgumentException;

    /**
     * Fills the given array with random double values and returns the array.
     *
     * @param array the given array
     * @return the given array
     */
    double @Nonnull [] fill(double @Nonnull [] array);

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
    double @Nonnull [] fill(
        double @Nonnull [] array, double startInclusive, double endExclusive
    ) throws IllegalArgumentException;
}
