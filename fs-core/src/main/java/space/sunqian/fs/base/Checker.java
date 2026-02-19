package space.sunqian.fs.base;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.exception.UnreachablePointException;

import java.util.NoSuchElementException;

/**
 * This class is the global checking utilities for checking and verifying parameters.
 *
 * @author sunqian
 */
public class Checker {

    /**
     * Checks whether the given {@code off} and {@code len} are out of the range: {@code [0, capacity)}. Its logic is
     * the same as the following code:
     * <pre>{@code
     * if (off < 0 || len < 0 || off + len > capacity) {
     *     throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param off      the given offset
     * @param len      the given length
     * @param capacity the given capacity which is typically represents the length of the range
     * @throws IndexOutOfBoundsException if the given {@code off} and {@code len} are out of the range:
     *                                   {@code [0, capacity)}
     */
    public static void checkOffLen(int off, int len, int capacity) throws IndexOutOfBoundsException {
        if (off < 0 || len < 0 || off + len > capacity) {
            throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given {@code off} and {@code len} are out of the range: {@code [0, capacity)}. Its logic is
     * the same as the following code:
     * <pre>{@code
     * if (off < 0 || len < 0 || off + len > capacity) {
     *     throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param off      the given offset
     * @param len      the given length
     * @param capacity the given capacity which is typically represents the length of the range
     * @throws IndexOutOfBoundsException if the given {@code off} and {@code len} are out of the range:
     *                                   {@code [0, capacity)}
     */
    public static void checkOffLen(long off, long len, long capacity) throws IndexOutOfBoundsException {
        if (off < 0 || len < 0 || len > capacity - off) {
            throw new IndexOutOfBoundsException("off=" + off + ", len=" + len + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given {@code start} and {@code end} are out of the range: {@code [0, capacity)}. Its logic is
     * the same as the following code:
     * <pre>{@code
     * if (start < 0 || end < start || end > capacity) {
     *     throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param start    the given start, typically inclusive
     * @param end      the given end, typically exclusive
     * @param capacity the given capacity which is typically represents the length of the range
     * @throws IndexOutOfBoundsException if the given {@code start} and {@code end} are out of the range:
     *                                   {@code [0, capacity)}
     */
    public static void checkStartEnd(int start, int end, int capacity) throws IndexOutOfBoundsException {
        if (start < 0 || end < start || end > capacity) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given {@code start} and {@code end} are out of the range: {@code [0, capacity)}. Its logic is
     * the same as the following code:
     * <pre>{@code
     * if (start < 0 || end < start || end > capacity) {
     *     throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
     * }
     * }</pre>
     *
     * @param start    the given start, typically inclusive
     * @param end      the given end, typically exclusive
     * @param capacity the given capacity which is typically represents the length of the range
     * @throws IndexOutOfBoundsException if the given {@code start} and {@code end} are out of the range:
     *                                   {@code [0, capacity)}
     */
    public static void checkStartEnd(long start, long end, long capacity) throws IndexOutOfBoundsException {
        if (start < 0 || end < start || end > capacity) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", capacity=" + capacity);
        }
    }

    /**
     * Checks whether the given expression is {@code true}, if it is not, throws a new {@link IllegalArgumentException}
     * with the given message.
     *
     * @param expr    the given expression
     * @param message the given message
     * @throws IllegalArgumentException if the given expression is {@code false}
     */
    public static void checkArgument(
        boolean expr, @Nonnull CharSequence message
    ) throws IllegalArgumentException {
        if (!expr) {
            throw new IllegalArgumentException(message.toString());
        }
    }

    /**
     * Checks whether the given expression is {@code true}, if it is not, throws a new {@link IllegalStateException}
     * with the given message.
     *
     * @param expr    the given expression
     * @param message the given message
     * @throws IllegalStateException if the given expression is {@code false}
     */
    public static void checkState(
        boolean expr, @Nonnull CharSequence message
    ) throws IllegalStateException {
        if (!expr) {
            throw new IllegalStateException(message.toString());
        }
    }

    /**
     * Checks whether the given expression is {@code true}, if it is not, throws a new
     * {@link UnsupportedOperationException} with the given message.
     *
     * @param expr    the given expression
     * @param message the given message
     * @throws UnsupportedOperationException if the given expression is {@code false}
     */
    public static void checkSupported(
        boolean expr, @Nonnull CharSequence message
    ) throws UnsupportedOperationException {
        if (!expr) {
            throw new UnsupportedOperationException(message.toString());
        }
    }

    /**
     * Checks whether the given expression is {@code true}, if it is not, throws a new {@link NoSuchElementException}
     * with the given message.
     *
     * @param expr    the given expression
     * @param message the given message
     * @throws NoSuchElementException if the given expression is {@code false}
     */
    public static void checkElement(
        boolean expr, @Nonnull CharSequence message
    ) throws NoSuchElementException {
        if (!expr) {
            throw new NoSuchElementException(message.toString());
        }
    }

    /**
     * Checks whether the given expression is {@code true}, if it is not, throws a new {@link UnreachablePointException}
     * with the given message.
     *
     * @param expr    the given expression
     * @param message the given message
     * @throws UnreachablePointException if the given expression is {@code false}
     */
    public static void checkReachable(
        boolean expr, @Nonnull CharSequence message
    ) throws UnreachablePointException {
        if (!expr) {
            throw new UnreachablePointException(message.toString());
        }
    }

    /**
     * Returns whether the given position is between the start boundary (inclusive) and the end boundary (exclusive).
     * Its logic is the same as the following code:
     * <pre>{@code
     * return pos >= startBound && pos < endBound;
     * }</pre>
     *
     * @param pos        the given position to be compared
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @return whether the given position is between the start boundary (inclusive) and the end boundary (exclusive)
     */
    public static boolean isInBounds(int pos, int startBound, int endBound) {
        return pos >= startBound && pos < endBound;
    }

    /**
     * Returns whether the given position is between the start boundary (inclusive) and the end boundary (exclusive).
     * Its logic is the same as the following code:
     * <pre>{@code
     * return pos >= startBound && pos < endBound;
     * }</pre>
     *
     * @param pos        the given position to be compared
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @return whether the given position is between the start boundary (inclusive) and the end boundary (exclusive)
     */
    public static boolean isInBounds(long pos, long startBound, long endBound) {
        return pos >= startBound && pos < endBound;
    }

    /**
     * Checks whether the given position is between the start boundary (inclusive) and the end boundary (exclusive). Its
     * logic is the same as the following code:
     * <pre>{@code
     * if (!(pos >= startBound && pos < endBound)) {
     *     throw new IndexOutOfBoundsException(
     *         "Out of bounds for " + pos + " in [" + startPos + ", " + endPos + ")."
     *     );
     * }
     * }</pre>
     *
     * @param pos        the given position to be compared
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @throws IndexOutOfBoundsException if the given position is out of the specified bounds
     */
    public static void checkInBounds(
        int pos, int startBound, int endBound
    ) throws IndexOutOfBoundsException {
        if (!isInBounds(pos, startBound, endBound)) {
            throw new IndexOutOfBoundsException(
                "Out of bounds for " + pos + " in [" + startBound + ", " + endBound + ")."
            );
        }
    }

    /**
     * Checks whether the given position is between the start boundary (inclusive) and the end boundary (exclusive). Its
     * logic is the same as the following code:
     * <pre>{@code
     * if (!(pos >= startBound && pos < endBound)) {
     *     throw new IndexOutOfBoundsException(
     *         "Out of bounds for " + pos + " in [" + startPos + ", " + endPos + ")."
     *     );
     * }
     * }</pre>
     *
     * @param pos        the given position to be compared
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @throws IndexOutOfBoundsException if the given position is out of the specified bounds
     */
    public static void checkInBounds(
        long pos, long startBound, long endBound
    ) throws IndexOutOfBoundsException {
        if (!isInBounds(pos, startBound, endBound)) {
            throw new IndexOutOfBoundsException(
                "Out of bounds for " + pos + " in [" + startBound + ", " + endBound + ")."
            );
        }
    }

    /**
     * Returns whether the given range, starting at the given start position (inclusive) and ending at the given end
     * position (exclusive), is between the start boundary (inclusive) and the end boundary (exclusive). Its logic is
     * the same as the following code:
     * <pre>{@code
     * return start >= startBound && end <= endBound && start <= end;
     * }</pre>
     *
     * @param start      the given start position of the range, inclusive
     * @param end        the given end position of the range, exclusive
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @return whether the given range is between the start boundary (inclusive) and the end boundary (exclusive)
     */
    public static boolean isInBounds(int start, int end, int startBound, int endBound) {
        return start >= startBound && end <= endBound && start <= end;
    }

    /**
     * Returns whether the given range, starting at the given start position (inclusive) and ending at the given end
     * position (exclusive), is between the start boundary (inclusive) and the end boundary (exclusive). Its logic is
     * the same as the following code:
     * <pre>{@code
     * return start >= startBound && end <= endBound && start <= end;
     * }</pre>
     *
     * @param start      the given start position of the range, inclusive
     * @param end        the given end position of the range, exclusive
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @return whether the given range is between the start boundary (inclusive) and the end boundary (exclusive)
     */
    public static boolean isInBounds(long start, long end, long startBound, long endBound) {
        return start >= startBound && end <= endBound && start <= end;
    }

    /**
     * Checks whether the given range, starting at the given start position (inclusive) and ending at the given end
     * position (exclusive), is between the start boundary (inclusive) and the end boundary (exclusive). Its logic is
     * the same as the following code:
     * <pre>{@code
     * if (!(start >= startBound && end <= endBound && start <= end)) {
     *     throw new IndexOutOfBoundsException(
     *         "Out of bounds for [" + start + ", " + end + ") in [" + startBound + ", " + endBound + ")."
     *     );
     * }
     * }</pre>
     *
     * @param start      the given start position of the range, inclusive
     * @param end        the given end position of the range, exclusive
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @throws IndexOutOfBoundsException if the given range is out of the specified bounds
     */
    public static void checkInBounds(
        int start, int end, int startBound, int endBound) throws IndexOutOfBoundsException {
        if (!isInBounds(start, end, startBound, endBound)) {
            throw new IndexOutOfBoundsException(
                "Out of bounds for [" + start + ", " + end + ") in [" + startBound + ", " + endBound + ")."
            );
        }
    }

    /**
     * Checks whether the given range, starting at the given start position (inclusive) and ending at the given end
     * position (exclusive), is between the start boundary (inclusive) and the end boundary (exclusive). Its logic is
     * the same as the following code:
     * <pre>{@code
     * if (!(start >= startBound && end <= endBound && start <= end)) {
     *     throw new IndexOutOfBoundsException(
     *         "Out of bounds for [" + start + ", " + end + ") in [" + startBound + ", " + endBound + ")."
     *     );
     * }
     * }</pre>
     *
     * @param start      the given start position of the range, inclusive
     * @param end        the given end position of the range, exclusive
     * @param startBound the start boundary, inclusive
     * @param endBound   the end boundary, exclusive
     * @throws IndexOutOfBoundsException if the given range is out of the specified bounds
     */
    public static void checkInBounds(
        long start, long end, long startBound, long endBound) throws IndexOutOfBoundsException {
        if (!isInBounds(start, end, startBound, endBound)) {
            throw new IndexOutOfBoundsException(
                "Out of bounds for [" + start + ", " + end + ") in [" + startBound + ", " + endBound + ")."
            );
        }
    }

    private Checker() {
    }
}
