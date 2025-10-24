package space.sunqian.common.collect;

import space.sunqian.annotations.Nonnull;

/**
 * Represents an operator for array types.
 *
 * @author sunqian
 */
public interface ArrayOperator {

    /**
     * Returns an {@code ArrayOperator} for the given array type.
     *
     * @param arrayType the array type
     * @return an {@code ArrayOperator} for the given array type
     * @throws IllegalArgumentException if the given array type is not an array type
     */
    static @Nonnull ArrayOperator of(@Nonnull Class<?> arrayType) throws IllegalArgumentException {
        return ArrayBack.operator(arrayType);
    }

    /**
     * Returns the element at the specified index in the given array.
     *
     * @param array the given array
     * @param index the specified index of the element
     * @return the element at the specified index in the given array
     */
    Object get(@Nonnull Object array, int index);

    /**
     * Sets the element at the specified index in the given array.
     *
     * @param array the given array
     * @param index the specified index of the element
     * @param value the value to be set
     */
    void set(@Nonnull Object array, int index, Object value);

    /**
     * Returns the size of the given array.
     *
     * @param array the given array
     * @return the size of the given array
     */
    int size(@Nonnull Object array);
}
