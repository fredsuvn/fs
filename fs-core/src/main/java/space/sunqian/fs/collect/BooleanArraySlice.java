package space.sunqian.fs.collect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.Checker;

/**
 * This interface represents a slice of a boolean array, boolean array implementation of {@link ArraySlice}.
 *
 * @author sunqian
 */
public class BooleanArraySlice implements ArraySlice<boolean[]> {

    /**
     * Returns a new {@link BooleanArraySlice} from the specified array, start index, and end index.
     *
     * @param array      the array to slice from
     * @param startIndex the start index of the slice, inclusive
     * @param endIndex   the end index of the slice, exclusive
     * @return the new {@link BooleanArraySlice} from the specified array, start index, and end index
     * @throws IndexOutOfBoundsException if the start index or end index is out of range
     */
    public static @Nonnull BooleanArraySlice of(
        boolean @Nonnull [] array,
        int startIndex,
        int endIndex
    ) throws IndexOutOfBoundsException {
        Checker.checkInBounds(startIndex, endIndex, 0, array.length);
        return new BooleanArraySlice(array, startIndex, endIndex);
    }

    private final boolean @Nonnull [] array;
    private final int startIndex;
    private final int endIndex;

    private BooleanArraySlice(boolean @Nonnull [] array, int startIndex, int endIndex) {
        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public boolean @Nonnull [] array() {
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
    public boolean get(int index) throws IndexOutOfBoundsException {
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
    public void set(int index, boolean value) throws IndexOutOfBoundsException {
        Checker.checkInBounds(index, 0, length());
        array[index + startIndex()] = value;
    }
}