package space.sunqian.fs.collect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.Checker;

public class ObjectArraySlice<T> implements ArraySlice<T[]> {

    /**
     * Returns a new {@link ObjectArraySlice} from the specified array, start index, and end index.
     *
     * @param array      the array to slice from
     * @param startIndex the start index of the slice, inclusive
     * @param endIndex   the end index of the slice, exclusive
     * @param <T>        the type of the elements in the array
     * @return the new {@link ObjectArraySlice} from the specified array, start index, and end index
     * @throws IndexOutOfBoundsException if the start index or end index is out of range
     */
    public static <T> @Nonnull ObjectArraySlice<T> of(
        T @Nonnull [] array,
        int startIndex,
        int endIndex
    ) throws IndexOutOfBoundsException {
        Checker.checkInBounds(startIndex, endIndex, 0, array.length);
        return new ObjectArraySlice<>(array, startIndex, endIndex);
    }

    private final T @Nonnull [] array;
    private final int startIndex;
    private final int endIndex;

    private ObjectArraySlice(T @Nonnull [] array, int startIndex, int endIndex) {
        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public T @Nonnull [] array() {
        return array;
    }

    @Override
    public int startIndex() {
        return startIndex;
    }

    @Override
    public int endIndex() {
        return endIndex;
    }

    /**
     * Returns the element at the specified index relative the start index of the slice. For example, the index
     * {@code 1} is the index {@code 1 + startIndex()} in the original array.
     *
     * @param index the index of the element relative the start index of the slice
     * @return the element at the specified index relative the start index of the slice
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public T get(int index) throws IndexOutOfBoundsException {
        Checker.checkInBounds(index, 0, length());
        return array[index + startIndex()];
    }

    /**
     * Sets the element at the specified index relative the start index of the slice to the specified value.
     *
     * @param index the index of the element to set relative the start index of the slice
     * @param value the value to set
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void set(int index, T value) throws IndexOutOfBoundsException {
        Checker.checkInBounds(index, 0, length());
        array[index + startIndex()] = value;
    }
}