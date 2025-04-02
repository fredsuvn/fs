package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * Utilities for checking and verifying.
 *
 * @author sunqian
 */
public class JieCheck {

    /**
     * Checks whether the range in the given array, starting at the given offset up to the given length, is valid.
     *
     * @param array the given array
     * @param off   the given offset
     * @param len   the given length
     * @throws NullPointerException      if the given array is null
     * @throws IndexOutOfBoundsException if the range is out of bounds
     */
    public static void checkOffsetLength(byte[] array, int off, int len) {
        if (array == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > array.length - off) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks whether the range in the given array, starting at the given offset up to the given length, is valid.
     *
     * @param array the given array
     * @param off   the given offset
     * @param len   the given length
     * @throws NullPointerException      if the given array is null
     * @throws IndexOutOfBoundsException if the range is out of bounds
     */
    public static void checkOffsetLength(char[] array, int off, int len) {
        if (array == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > array.length - off) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks whether the range in the given array, starting at the given offset up to the given length, is valid.
     *
     * @param array the given array
     * @param off   the given offset
     * @param len   the given length
     * @throws NullPointerException      if the given array is null
     * @throws IndexOutOfBoundsException if the range is out of bounds
     */
    public static void checkOffsetLength(CharSequence array, int off, int len) {
        if (array == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > array.length() - off) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks whether given object is null, if it is, throw a {@link NullPointerException}.
     *
     * @param obj given object
     */
    public static void checkNull(@Nullable Object obj) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Checks whether given obj is null, if it is, throw a {@link NullPointerException} with given message.
     *
     * @param obj     given object
     * @param message given message
     */
    public static void checkNull(@Nullable Object obj, CharSequence message) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException(message.toString());
        }
    }

    /**
     * Checks whether given obj is null, if it is, throw a {@link NullPointerException} with message concatenated from
     * given message arguments.
     *
     * @param obj         given object
     * @param messageArgs given message arguments
     */
    public static void checkNull(@Nullable Object obj, Object... messageArgs) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException(JieString.concat(messageArgs));
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NullPointerException}.
     *
     * @param expr given expression
     */
    public static void checkNull(boolean expr) throws NullPointerException {
        if (!expr) {
            throw new NullPointerException();
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NullPointerException} with given message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkNull(boolean expr, CharSequence message) throws NullPointerException {
        if (!expr) {
            throw new NullPointerException(message.toString());
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NullPointerException} with message
     * concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkNull(boolean expr, Object... messageArgs) throws NullPointerException {
        if (!expr) {
            throw new NullPointerException(JieString.concat(messageArgs));
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalArgumentException}.
     *
     * @param expr given expression
     */
    public static void checkArgument(boolean expr) throws IllegalArgumentException {
        if (!expr) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalArgumentException} with given
     * message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkArgument(boolean expr, CharSequence message) throws IllegalArgumentException {
        if (!expr) {
            throw new IllegalArgumentException(message.toString());
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalArgumentException} with given
     * message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkArgument(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        if (!expr) {
            throw new IllegalArgumentException(JieString.concat(messageArgs));
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalStateException}.
     *
     * @param expr given expression
     */
    public static void checkState(boolean expr) throws IllegalStateException {
        if (!expr) {
            throw new IllegalStateException();
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalStateException} with given
     * message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkState(boolean expr, CharSequence message) throws IllegalStateException {
        if (!expr) {
            throw new IllegalStateException(message.toString());
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalStateException} with given message
     * concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkState(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        if (!expr) {
            throw new IllegalStateException(JieString.concat(messageArgs));
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link UnsupportedOperationException}.
     *
     * @param expr given expression
     */
    public static void checkSupported(boolean expr) throws UnsupportedOperationException {
        if (!expr) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link UnsupportedOperationException} with given
     * message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkSupported(boolean expr, CharSequence message) throws UnsupportedOperationException {
        if (!expr) {
            throw new UnsupportedOperationException(message.toString());
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link UnsupportedOperationException} with given
     * message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkSupported(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        if (!expr) {
            throw new UnsupportedOperationException(JieString.concat(messageArgs));
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NoSuchElementException}.
     *
     * @param expr given expression
     */
    public static void checkElement(boolean expr) throws NoSuchElementException {
        if (!expr) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NoSuchElementException} with given
     * message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkElement(boolean expr, CharSequence message) throws NoSuchElementException {
        if (!expr) {
            throw new NoSuchElementException(message.toString());
        }
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link IllegalArgumentException} with given
     * message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkElement(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        if (!expr) {
            throw new NoSuchElementException(JieString.concat(messageArgs));
        }
    }

    /**
     * Returns whether given index is in bounds from start index (inclusive) to end index (exclusive).
     * <p>
     * Note all indexed must &gt;= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @return whether given index is in bounds from start index (inclusive) to end index (exclusive)
     */
    public static boolean isInBounds(int index, int startIndex, int endIndex) {
        return index >= startIndex && index < endIndex && index >= 0 && startIndex >= 0;
    }

    /**
     * Returns whether given index is in bounds from start index (inclusive) to end index (exclusive).
     * <p>
     * Note all indexed must &gt;= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @return whether given index is in bounds from start index (inclusive) to end index (exclusive)
     */
    public static boolean isInBounds(long index, long startIndex, long endIndex) {
        return index >= startIndex && index < endIndex && index >= 0 && startIndex >= 0;
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with message pattern: [startIndex, endIndex): index.
     * <p>
     * Note all indexed must &gt;= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkInBounds(int index, int startIndex, int endIndex) throws IndexOutOfBoundsException {
        if (!isInBounds(index, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException("[" + startIndex + ", " + endIndex + "): " + index);
        }
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with given message.
     * <p>
     * Note all indexed must &gt;= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkInBounds(
        int index, int startIndex, int endIndex, CharSequence message) throws IndexOutOfBoundsException {
        if (!isInBounds(index, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException(message.toString());
        }
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with message pattern: [startIndex, endIndex): index.
     * <p>
     * Note all indexed must &gt;= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkInBounds(long index, long startIndex, long endIndex) throws IndexOutOfBoundsException {
        if (!isInBounds(index, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException("[" + startIndex + ", " + endIndex + "): " + index);
        }
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with given message.
     * <p>
     * Note all indexed must &gt;= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkInBounds(
        long index, long startIndex, long endIndex, CharSequence message) throws IndexOutOfBoundsException {
        if (!isInBounds(index, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException(message.toString());
        }
    }

    /**
     * Returns whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive).
     * <p>
     * Note all ranges and indexed must &gt;= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @return whether given range (from start range index inclusive to end range index exclusive) is in bounds from *
     * start index (inclusive) to end index (exclusive)
     */
    public static boolean isRangeInBounds(int startRange, int endRange, int startIndex, int endIndex) {
        return startRange >= startIndex && endRange <= endIndex && startRange <= endRange && startRange >= 0 && startIndex >= 0;
    }

    /**
     * Returns whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive).
     * <p>
     * Note all ranges and indexed must &gt;= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @return whether given range (from start range index inclusive to end range index exclusive) is in bounds from *
     * start index (inclusive) to end index (exclusive)
     */
    public static boolean isRangeInBounds(long startRange, long endRange, long startIndex, long endIndex) {
        return startRange >= startIndex && endRange <= endIndex && startRange <= endRange && startRange >= 0 && startIndex >= 0;
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not, throw an {@link IndexOutOfBoundsException} with
     * message pattern: [startIndex, endIndex): [startRange, endRange).
     * <p>
     * Note all ranges and indexed must &gt;= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkRangeInBounds(
        int startRange, int endRange, int startIndex, int endIndex) throws IndexOutOfBoundsException {
        if (!isRangeInBounds(startRange, endRange, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException("[" + startIndex + ", " + endIndex + "): [" + startRange + ", " + endRange + ")");
        }
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not, throw an {@link IndexOutOfBoundsException} with
     * given message.
     * <p>
     * Note all ranges and indexed must &gt;= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkRangeInBounds(
        int startRange, int endRange, int startIndex, int endIndex, CharSequence message) throws IndexOutOfBoundsException {
        if (!isRangeInBounds(startRange, endRange, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException(message.toString());
        }
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not, throw an {@link IndexOutOfBoundsException} with
     * message pattern: [startIndex, endIndex): [startRange, endRange).
     * <p>
     * Note all ranges and indexed must &gt;= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkRangeInBounds(
        long startRange, long endRange, long startIndex, long endIndex) throws IndexOutOfBoundsException {
        if (!isRangeInBounds(startRange, endRange, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException("[" + startIndex + ", " + endIndex + "): [" + startRange + ", " + endRange + ")");
        }
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not, throw an {@link IndexOutOfBoundsException} with
     * given message.
     * <p>
     * Note all ranges and indexed must &gt;= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkRangeInBounds(
        long startRange, long endRange, long startIndex, long endIndex, CharSequence message) throws IndexOutOfBoundsException {
        if (!isRangeInBounds(startRange, endRange, startIndex, endIndex)) {
            throw new IndexOutOfBoundsException(message.toString());
        }
    }

    /**
     * If given float value in bounds of specified start value inclusive and end value exclusive, returns itself.
     * Otherwise, if the float value less than {@code startInclusive}, return {@code startInclusive}, else return
     * {@link Math#nextDown(float)} of {@code endExclusive}.
     *
     * @param value          given float value
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return the float value in bounds
     */
    public static float makeIn(float value, float startInclusive, float endExclusive) {
        if (value < startInclusive) {
            return startInclusive;
        }
        if (value >= endExclusive) {
            return Math.nextDown(endExclusive);
        }
        return value;
    }

    /**
     * If given double value in bounds of specified start value inclusive and end value exclusive, returns itself.
     * Otherwise, if the double value less than {@code startInclusive}, return {@code startInclusive}, else return
     * {@link Math#nextDown(float)} of {@code endExclusive}.
     *
     * @param value          given double value
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return the double value in bounds
     */
    public static double makeIn(double value, double startInclusive, double endExclusive) {
        if (value < startInclusive) {
            return startInclusive;
        }
        if (value >= endExclusive) {
            return Math.nextDown(endExclusive);
        }
        return value;
    }
}
