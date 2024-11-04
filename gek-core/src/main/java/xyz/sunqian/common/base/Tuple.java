package xyz.sunqian.common.base;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class represents a tuple which stores elements of different types, each element at its index is mutable.
 * <p>
 * Note {@link #equals(Object)}, {@link #hashCode()} and {@link #toString()} are overridden by {@code deep} method.
 *
 * @author sunq62
 */
public class Tuple {

    /**
     * Returns a {@link Tuple} of given elements.
     *
     * @param elements given elements
     * @return a {@link Tuple} of given elements
     */
    public static Tuple of(Object... elements) {
        return new Tuple(elements);
    }

    private final Object[] data;

    private Tuple(Object[] data) {
        this.data = data;
    }

    /**
     * Returns element as specified type at specified index.
     *
     * @param index specified index
     * @param <T>   specified type
     * @return element as specified type at specified index
     */
    public <T> T get(int index) {
        return Jie.as(data[index]);
    }

    /**
     * Sets given element at specified index.
     *
     * @param index   specified index
     * @param element given element
     * @return this tuple
     */
    public Tuple set(int index, Object element) {
        data[index] = element;
        return this;
    }

    /**
     * Returns size of this tuple.
     *
     * @return size of this tuple
     */
    public int size() {
        return data.length;
    }

    /**
     * Returns result of {@link Objects#deepEquals(Object, Object)} for comparing.
     *
     * @return result of {@link Objects#deepEquals(Object, Object)} for comparing
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return Objects.deepEquals(data, tuple.data);
    }

    /**
     * Returns result of {@link Arrays#deepHashCode(Object[])} for current elements.
     *
     * @return result of {@link Arrays#deepHashCode(Object[])} for current elements
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(data);
    }

    /**
     * Returns result of {@link Arrays#deepToString(Object[])} for current elements.
     *
     * @return result of {@link Arrays#deepToString(Object[])} for current elements
     */
    @Override
    public String toString() {
        return Arrays.deepToString(data);
    }
}
