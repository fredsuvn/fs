package space.sunqian.fs.collect;

import space.sunqian.annotation.Nonnull;

/**
 * This interface represents a slice of an array, defined by a start index inclusive, and an end index exclusive. It is
 * a view of the original array, and any modification to the slice will be reflected in the original array, and vice
 * versa.
 * <p>
 * Subtypes:
 * <ul>
 *   <li>{@link BooleanArraySlice} - for {@code boolean[]}</li>
 *   <li>{@link ByteArraySlice} - for {@code byte[]}</li>
 *   <li>{@link CharArraySlice} - for {@code char[]}</li>
 *   <li>{@link ShortArraySlice} - for {@code short[]}</li>
 *   <li>{@link IntArraySlice} - for {@code int[]}</li>
 *   <li>{@link LongArraySlice} - for {@code long[]}</li>
 *   <li>{@link FloatArraySlice} - for {@code float[]}</li>
 *   <li>{@link DoubleArraySlice} - for {@code double[]}</li>
 *   <li>{@link ObjectArraySlice} - for {@code T[]}</li>
 * </ul>
 *
 * @param <A> the type of the array to slice
 * @author sunqian
 */
public interface ArraySlice<A> {

    /**
     * Returns the array to slice.
     *
     * @return the array to slice
     */
    @Nonnull
    A array();

    /**
     * Returns the start index of the slice.
     *
     * @return the start index of the slice
     */
    int startIndex();

    /**
     * Returns the end index of the slice.
     *
     * @return the end index of the slice
     */
    int endIndex();

    /**
     * Returns the length of the slice.
     *
     * @return the length of the slice
     */
    default int length() {
        return endIndex() - startIndex();
    }
}