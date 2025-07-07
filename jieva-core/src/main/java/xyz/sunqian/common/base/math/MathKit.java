package xyz.sunqian.common.base.math;

/**
 * Math utilities.
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
     * Returns the integer value of the long value. If the long value {@code >} {@link Integer#MAX_VALUE}, returns
     * {@link Integer#MAX_VALUE}; else if the long value {@code <} {@link Integer#MIN_VALUE}, returns
     * {@link Integer#MIN_VALUE}; otherwise, returns the long value itself.
     *
     * @param value the long value
     * @return the integer value of the long value
     */
    public static int intValue(long value) {
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
    public static int portionNum(int total, int size) {
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
    public static long portionNum(long total, long size) {
        return (total + size - 1) / size;
    }

    /**
     * If the {@code value} is {@code startInclusive <= value < endExclusive}, returns the {@code value} itself.
     * Otherwise, if {@code value < startInclusive}, then returns {@code startInclusive}; else returns
     * {@link Math#nextDown(float)} of the {@code endExclusive}. It is equivalent to:
     * <pre>{@code
     * if (value < startInclusive) {
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
     * @return a value which is {@code startInclusive <= value < endExclusive}
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
     * If the {@code value} is {@code startInclusive <= value < endExclusive}, returns the {@code value} itself.
     * Otherwise, if {@code value < startInclusive}, then returns {@code startInclusive}; else returns
     * {@link Math#nextDown(float)} of the {@code endExclusive}. It is equivalent to:
     * <pre>{@code
     * if (value < startInclusive) {
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
     * @return a value which is {@code startInclusive <= value < endExclusive}
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
