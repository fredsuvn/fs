package xyz.sunqian.test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This interface provides methods for generating test data.
 *
 * @author sunqian
 */
public interface DataTest {

    /**
     * Returns a new random bytes array of the specified length.
     *
     * @param length the specified length
     * @return a new random bytes array of the specified length
     */
    default byte[] randomBytes(int length) throws IllegalArgumentException {
        return fillRandomBytes(new byte[length]);
    }

    /**
     * Returns a new random bytes array of the specified length. The random value will in
     * {@code [startInclusive, endExclusive)}.
     *
     * @param length         the specified length
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new random bytes array of the specified length
     */
    default byte[] randomBytes(int length, byte startInclusive, byte endExclusive) throws IllegalArgumentException {
        return fillRandomBytes(new byte[length], startInclusive, endExclusive);
    }

    /**
     * Fills random bytes to the given array, then returns the array.
     *
     * @param bytes the given array
     * @return the array
     */
    default byte[] fillRandomBytes(byte[] bytes) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) random.nextInt();
        }
        return bytes;
    }

    /**
     * Fills random bytes to the given array, then returns the array. The random value will in
     * {@code [startInclusive, endExclusive)}.
     *
     * @param bytes          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the array
     */
    default byte[] fillRandomBytes(byte[] bytes, byte startInclusive, byte endExclusive) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) random.nextInt(startInclusive, endExclusive);
        }
        return bytes;
    }

    /**
     * Returns a new random chars array of the specified length.
     *
     * @param length the specified length
     * @return a new random chars array of the specified length
     */
    default char[] randomChars(int length) throws IllegalArgumentException {
        return fillRandomChars(new char[length]);
    }

    /**
     * Returns a new random chars array of the specified length. The random value will in
     * {@code [startInclusive, endExclusive)}.
     *
     * @param length         the specified length
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a new random chars array of the specified length
     */
    default char[] randomChars(int length, char startInclusive, char endExclusive) throws IllegalArgumentException {
        return fillRandomChars(new char[length], startInclusive, endExclusive);
    }

    /**
     * Fills random chars to the given array, then returns the array.
     *
     * @param chars the given array
     * @return the array
     */
    default char[] fillRandomChars(char[] chars) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) random.nextInt();
        }
        return chars;
    }

    /**
     * Fills random chars to the given array, then returns the array. The random value will in
     * {@code [startInclusive, endExclusive)}.
     *
     * @param chars          the given array
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return the array
     */
    default char[] fillRandomChars(char[] chars, char startInclusive, char endExclusive) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) random.nextInt(startInclusive & 0xFFFF, endExclusive & 0xFFFF);
        }
        return chars;
    }
}
