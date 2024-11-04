package xyz.sunqian.common.base;

/**
 * Functional interface used to generate sub-object such as sub-string, sub-list.
 *
 * @param <T> type of source object to be sub-ed.
 * @param <R> type of result object after sub-ing.
 * @author fredsuvn
 */
@FunctionalInterface
public interface SubFunction<T, R> {

    /**
     * Returns sub-object of source object from start index inclusive to end index exclusive.
     *
     * @param source source object
     * @param start  start index inclusive
     * @param end    end index exclusive
     * @return sub-object of source object from start index inclusive to end index exclusive
     */
    R apply(T source, int start, int end);

    /**
     * Returns sub-object of source object from start index inclusive to end index exclusive. This is {@code long}
     * version of {@link #apply(Object, int, int)}.
     *
     * @param source source object
     * @param start  start index inclusive
     * @param end    end index exclusive
     * @return sub-object of source object from start index inclusive to end index exclusive
     */
    default R applyLong(T source, long start, long end) {
        return apply(source, (int) start, (int) end);
    }
}
