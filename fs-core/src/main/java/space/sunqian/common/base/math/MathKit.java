package space.sunqian.common.base.math;

/**
 * Utilities for math.
 *
 * @author sunqian
 */
public class MathKit {

    /**
     * Returns the maximum value of given three numbers.
     *
     * @param a the number a
     * @param b the number b
     * @param c the number c
     * @return the maximum value of given three numbers
     */
    public static int max(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }

    /**
     * Returns the maximum value of given three numbers.
     *
     * @param a the number a
     * @param b the number b
     * @param c the number c
     * @return the maximum value of given three numbers
     */
    public static long max(long a, long b, long c) {
        return Math.max(Math.max(a, b), c);
    }

    /**
     * Returns the minimum value of given three numbers.
     *
     * @param a the number a
     * @param b the number b
     * @param c the number c
     * @return the minimum value of given three numbers
     */
    public static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    /**
     * Returns the minimum value of given three numbers.
     *
     * @param a the number a
     * @param b the number b
     * @param c the number c
     * @return the minimum value of given three numbers
     */
    public static long min(long a, long b, long c) {
        return Math.min(Math.min(a, b), c);
    }

    /**
     * Returns the safe integer value of the long value. If the long value {@code >} {@link Integer#MAX_VALUE}, returns
     * {@link Integer#MAX_VALUE}; else if the long value {@code <} {@link Integer#MIN_VALUE}, returns
     * {@link Integer#MIN_VALUE}; otherwise, returns the long value itself.
     *
     * @param value the long value
     * @return the integer value of the long value
     */
    public static int safeInt(long value) {
        return value == 0 ? 0 : (
            value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (
                value < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) value
            ));
    }

    /**
     * Returns the portion number for {@code total / size}, it is equivalent to:
     * <pre>{@code
     * return total % size == 0 ? total / size : total / size + 1;
     * }</pre>
     *
     * @param total the total
     * @param size  the size
     * @return the portion number for {@code total / size}
     */
    public static int portion(int total, int size) {
        return (total + size - 1) / size;
    }

    /**
     * Returns the portion number for {@code total / size}, it is equivalent to:
     * <pre>{@code
     * return total % size == 0 ? total / size : total / size + 1;
     * }</pre>
     *
     * @param total the total
     * @param size  the size
     * @return the portion number for {@code total / size}
     */
    public static long portion(long total, long size) {
        return (total + size - 1) / size;
    }

    /**
     * If {@code value <= startInclusive}, returns the {@code startInclusive}; else if {@code value >= endExclusive},
     * returns {@code endExclusive - 1}. It is equivalent to:
     * <pre>{@code
     * if (value <= startInclusive) {
     *     return startInclusive;
     * }
     * if (value >= endExclusive) {
     *     return endExclusive - 1;
     * }
     * return value;
     * }</pre>
     *
     * @param value          the value
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a value in the range: {@code startInclusive <= value < endExclusive}
     */
    public static int makeIn(int value, int startInclusive, int endExclusive) {
        if (value <= startInclusive) {
            return startInclusive;
        }
        if (value >= endExclusive) {
            return endExclusive - 1;
        }
        return value;
    }

    /**
     * If {@code value <= startInclusive}, returns the {@code startInclusive}; else if {@code value >= endExclusive},
     * returns {@code endExclusive - 1}. It is equivalent to:
     * <pre>{@code
     * if (value <= startInclusive) {
     *     return startInclusive;
     * }
     * if (value >= endExclusive) {
     *     return endExclusive - 1;
     * }
     * return value;
     * }</pre>
     *
     * @param value          the value
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a value in the range: {@code startInclusive <= value < endExclusive}
     */
    public static long makeIn(long value, long startInclusive, long endExclusive) {
        if (value <= startInclusive) {
            return startInclusive;
        }
        if (value >= endExclusive) {
            return endExclusive - 1;
        }
        return value;
    }

    /**
     * If {@code value <= startInclusive}, returns the {@code startInclusive}; else if {@code value >= endExclusive},
     * returns {@link Math#nextDown(float)} for the {@code endExclusive}. It is equivalent to:
     * <pre>{@code
     * if (value <= startInclusive) {
     *     return startInclusive;
     * }
     * if (value >= endExclusive) {
     *     return Math.nextDown(endExclusive);
     * }
     * return value;
     * }</pre>
     *
     * @param value          the value
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a value in the range: {@code startInclusive <= value < endExclusive}
     */
    public static float makeIn(float value, float startInclusive, float endExclusive) {
        if (value <= startInclusive) {
            return startInclusive;
        }
        if (value >= endExclusive) {
            return Math.nextDown(endExclusive);
        }
        return value;
    }

    /**
     * If {@code value <= startInclusive}, returns the {@code startInclusive}; else if {@code value >= endExclusive},
     * returns {@link Math#nextDown(float)} for the {@code endExclusive}. It is equivalent to:
     * <pre>{@code
     * if (value <= startInclusive) {
     *     return startInclusive;
     * }
     * if (value >= endExclusive) {
     *     return Math.nextDown(endExclusive);
     * }
     * return value;
     * }</pre>
     *
     * @param value          the value
     * @param startInclusive the start value inclusive
     * @param endExclusive   the end value exclusive
     * @return a value in the range: {@code startInclusive <= value < endExclusive}
     */
    public static double makeIn(double value, double startInclusive, double endExclusive) {
        if (value <= startInclusive) {
            return startInclusive;
        }
        if (value >= endExclusive) {
            return Math.nextDown(endExclusive);
        }
        return value;
    }

    private MathKit() {
    }
}
