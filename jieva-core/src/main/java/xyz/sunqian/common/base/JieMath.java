package xyz.sunqian.common.base;

/**
 * Math utilities.
 *
 * @author sunqian
 */
public class JieMath {

    /**
     * Returns the integer value of given long value. If the long value is greater than {@link Integer#MAX_VALUE},
     * returns {@link Integer#MAX_VALUE}.
     *
     * @param value given long value
     * @return the integer value of given long value
     */
    public static int intValue(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    /**
     * Returns the least portion number that {@code total} can be divided into specified {@code size}. It is equivalent
     * to:
     * <pre>
     *     return total % size == 0 ? total / size : total / size + 1;
     * </pre>
     *
     * @param total the total
     * @param size  specified size
     * @return the least portion number that {@code total} can be divided into specified {@code size}
     */
    public static int leastPortion(int total, int size) {
        return total % size == 0 ? total / size : total / size + 1;
    }

    /**
     * Returns the least portion number that {@code total} can be divided into specified {@code size}. It is equivalent
     * to:
     * <pre>
     *     return total % size == 0 ? total / size : total / size + 1;
     * </pre>
     *
     * @param total the total
     * @param size  specified size
     * @return the least portion number that {@code total} can be divided into specified {@code size}
     */
    public static long leastPortion(long total, long size) {
        return total % size == 0 ? total / size : total / size + 1;
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
