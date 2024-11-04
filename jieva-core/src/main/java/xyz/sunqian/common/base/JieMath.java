package xyz.sunqian.common.base;

/**
 * Math utilities.
 *
 * @author sunq62
 */
public class JieMath {

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
}
