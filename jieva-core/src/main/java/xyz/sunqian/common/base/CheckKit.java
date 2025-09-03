package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.UnreachablePointException;

import java.util.NoSuchElementException;

/**
 * Utilities for checking and verifying.
 *
 * @author sunqian
 */
public class CheckKit {

    /**
     * Checks whether the sub-range defined by the given offset and length is within the specified range. Its logic is
     * the same as the following code:
     * <pre>{@code
     *     if (offset < 0 || length < 0 || length > range - offset) {
     *         throw new IndexOutOfBoundsException();
     *     }
     * }</pre>
     *
     * @param range  the specified range
     * @param offset the given offset
     * @param length the given length
     * @throws IndexOutOfBoundsException if the sub-range is out of the specified range
     */
    public static void checkOffsetLength(int range, int offset, int length) throws IndexOutOfBoundsException {
        if (offset < 0 || length < 0 || length > range - offset) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks whether the sub-range defined by the given offset and length is within the specified range. Its logic is
     * the same as the following code:
     * <pre>{@code
     *     if (offset < 0 || length < 0 || length > range - offset) {
     *         throw new IndexOutOfBoundsException();
     *     }
     * }</pre>
     *
     * @param range  the specified range
     * @param offset the given offset
     * @param length the given length
     * @throws IndexOutOfBoundsException if the sub-range is out of the specified range
     */
    public static void checkOffsetLength(long range, long offset, long length) throws IndexOutOfBoundsException {
        if (offset < 0 || length < 0 || length > range - offset) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks whether the sub-range defined by the given start position and end position is within the specified range.
     * Its logic is the same as the following code:
     * <pre>{@code
     *     if (start < 0 || end < start || end > range) {
     *         throw new IndexOutOfBoundsException();
     *     }
     * }</pre>
     *
     * @param range the specified range
     * @param start the given start position
     * @param end   the given end position
     * @throws IndexOutOfBoundsException if the sub-range is out of the specified range
     */
    public static void checkStartEnd(int range, int start, int end) throws IndexOutOfBoundsException {
        if (start < 0 || end < start || end > range) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks whether the sub-range defined by the given start position and end position is within the specified range.
     * Its logic is the same as the following code:
     * <pre>{@code
     *     if (start < 0 || end < start || end > range) {
     *         throw new IndexOutOfBoundsException();
     *     }
     * }</pre>
     *
     * @param range the specified range
     * @param start the given start position
     * @param end   the given end position
     * @throws IndexOutOfBoundsException if the sub-range is out of the specified range
     */
    public static void checkStartEnd(long range, long start, long end) throws IndexOutOfBoundsException {
        if (start < 0 || end < start || end > range) {
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
     * Checks whether the given expression is {@code true}. If it is, an {@link UnreachablePointException} will be
     * thrown.
     *
     * @param expr the given expression
     * @throws UnreachablePointException if the given expression is {@code true}
     */
    public static void unreachable(boolean expr) throws UnreachablePointException {
        if (expr) {
            throw new UnreachablePointException();
        }
    }
}
