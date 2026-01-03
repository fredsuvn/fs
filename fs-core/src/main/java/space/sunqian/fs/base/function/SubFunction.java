package space.sunqian.fs.base.function;

/**
 * Represents an operation that accepts an object and the specified range, and returns the sub-portion of the specified
 * range of the object.
 * <p>
 * This is a functional interface whose functional method is {@link #apply(Object, int, int)}.
 *
 * @param <T> the type of the object to be portioned
 * @param <R> the type of the sub-portion result
 * @author sunqian
 */
@FunctionalInterface
public interface SubFunction<T, R> {

    /**
     * Applies this function, returns sub-portion of the object from the specified start index inclusive to the end
     * index exclusive.
     *
     * @param t     the object to be portioned
     * @param start the specified start index inclusive
     * @param end   the specified end index exclusive
     * @return the sub-portion result
     */
    R apply(T t, int start, int end);
}
