package xyz.sunqian.common.base;

import xyz.sunqian.common.base.exception.UnreachablePointException;

import java.util.NoSuchElementException;

/**
 * Utilities for checking and verifying.
 *
 * @author sunqian
 */
public class CheckKit {

    /**
     * Checks whether the given {@code off} and {@code len} are out of the given {@code capacity}. Its logic is the same
     * as the following code:
     * <pre>{@code
     * if (off < 0 || len < 0 || off + len > capacity) {
     *     throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param off      the given offset
     * @param len      the given length
     * @param capacity the given capacity
     * @throws IndexOutOfBoundsException if the given {@code off} and {@code len} are out of the given {@code capacity}
     */
    public static void checkOffLen(int off, int len, int capacity) throws IndexOutOfBoundsException {
        if (off < 0 || len < 0 || off + len > capacity) {
            throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given {@code off} and {@code len} are out of the given {@code capacity}. Its logic is the same
     * as the following code:
     * <pre>{@code
     * if (off < 0 || len < 0 || off + len > capacity) {
     *     throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param off      the given offset
     * @param len      the given length
     * @param capacity the given capacity
     * @throws IndexOutOfBoundsException if the given {@code off} and {@code len} are out of the given {@code capacity}
     */
    public static void checkOffLen(long off, long len, long capacity) throws IndexOutOfBoundsException {
        if (off < 0 || len < 0 || len > capacity - off) {
            throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given {@code start} and {@code end} are out of the given {@code capacity}. Its logic is the
     * same as the following code:
     * <pre>{@code
     * if (start < 0 || end < start || end > capacity) {
     *     throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param start    the given start, typically inclusive
     * @param end      the given end, typically exclusive
     * @param capacity the given capacity
     * @throws IndexOutOfBoundsException if the given {@code start} and {@code end} are out of the given
     *                                   {@code capacity}
     */
    public static void checkStartEnd(int start, int end, int capacity) throws IndexOutOfBoundsException {
        if (start < 0 || end < start || end > capacity) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given {@code start} and {@code end} are out of the given {@code capacity}. Its logic is the
     * same as the following code:
     * <pre>{@code
     * if (start < 0 || end < start || end > capacity) {
     *     throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param start    the given start, typically inclusive
     * @param end      the given end, typically exclusive
     * @param capacity the given capacity
     * @throws IndexOutOfBoundsException if the given {@code start} and {@code end} are out of the given
     *                                   {@code capacity}
     */
    public static void checkStartEnd(long start, long end, long capacity) throws IndexOutOfBoundsException {
        if (start < 0 || end < start || end > capacity) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
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
