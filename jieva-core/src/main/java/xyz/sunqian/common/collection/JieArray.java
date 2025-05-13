package xyz.sunqian.common.collection;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.function.IndexedDoublePredicate;
import xyz.sunqian.common.base.function.IndexedIntPredicate;
import xyz.sunqian.common.base.function.IndexedLongPredicate;
import xyz.sunqian.common.base.function.IndexedPredicate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;

/**
 * Static utility class for array.
 *
 * @author sunqian
 */
public class JieArray {

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @param <T>   the component type of the given array
     * @return whether the given array is null or empty
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @param <T>   the component type of the given array
     * @return whether the given array is not null and empty
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(byte[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(short[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(char[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(float[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @param <T>   the component type of the given array
     * @return the given array
     */
    public static <T> T[] fill(T @Nonnull [] array, T value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static boolean[] fill(boolean @Nonnull [] array, boolean value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static byte[] fill(byte @Nonnull [] array, byte value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static short[] fill(short @Nonnull [] array, short value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static char[] fill(char @Nonnull [] array, char value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static int[] fill(int @Nonnull [] array, int value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static long[] fill(long @Nonnull [] array, long value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static float[] fill(float @Nonnull [] array, float value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Fills the given array with the given value and returns the given array.
     *
     * @param array the given array
     * @param value the given value
     * @return the given array
     */
    public static double[] fill(double @Nonnull [] array, double value) {
        Arrays.fill(array, value);
        return array;
    }

    /**
     * Maps the source array to the dest array by specified mapper.
     * <p>
     * If the dest array's length equals to the source array's length, the dest array will be returned. Otherwise, a new
     * array with same length of the source array will be created and returned.
     * <p>
     * Each element of the source array will be mapped to a new element by the specified mapper, then be set into the
     * result array at corresponding index.
     *
     * @param source the source array
     * @param dest   the dest array
     * @param mapper the specified mapper
     * @param <T>    the component type of the source array
     * @param <R>    the component type of the dest array
     * @return the given dest or a new result array
     */
    public static <T, R> R[] map(
        T @Nonnull [] source, R @Nonnull [] dest, @Nonnull Function<? super T, ? extends R> mapper
    ) {
        R[] result;
        if (dest.length == source.length) {
            result = dest;
        } else {
            result = newArray(dest.getClass().getComponentType(), source.length);
        }
        map0(source, result, 0, mapper);
        return result;
    }

    /**
     * Maps the source array to the dest array by the specified mapper.
     * <p>
     * This method tries to create a new array to receive the mapping result and return. Each element of the source
     * array will be mapped to a new element by the specified mapper, then be set into the created new array at
     * corresponding index. The component type of the new array is the type of the first nonnull new element. If all new
     * elements are null, an {@link UnsupportedOperationException} will be thrown.
     *
     * @param source the source array
     * @param mapper the specified mapper
     * @param <T>    the component type of the source array
     * @param <R>    the component type of the dest array
     * @return a new result array
     * @throws UnsupportedOperationException if all new elements are null
     */
    public static <T, R> R[] map(
        T @Nonnull [] source, @Nonnull Function<? super T, ? extends R> mapper
    ) throws UnsupportedOperationException {
        for (int i = 0; i < source.length; i++) {
            R r = mapper.apply(source[i]);
            if (r != null) {
                R[] dest = newArray(r.getClass(), source.length);
                map0(source, dest, i, mapper);
                return dest;
            }
        }
        throw new UnsupportedOperationException("Can not resolve the component type.");
    }

    private static <T, R> void map0(
        T @Nonnull [] source, R @Nonnull [] dest, int start, @Nonnull Function<? super T, ? extends R> mapper
    ) {
        for (int i = start; i < source.length; i++) {
            T t = source[i];
            R r = mapper.apply(t);
            dest[i] = r;
        }
    }

    /**
     * Creates a new empty array with the specified component type and length.
     *
     * @param componentType the specified component type
     * @param length        the specified length
     * @param <A>           the type of created array, not the component type
     * @return the created array
     */
    public static <A> A newArray(Class<?> componentType, int length) {
        return Jie.as(Array.newInstance(componentType, length));
    }

    /**
     * Returns the value from the given array at the specified index. If the array or the value is null, or the index is
     * out of bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @param <T>          the component type
     * @return the value from the given array at the specified index
     */
    public static <T> T get(T[] array, int index, T defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        T value = array[index];
        return value == null ? defaultValue : value;
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static boolean get(boolean[] array, int index, boolean defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static byte get(byte[] array, int index, byte defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static short get(short[] array, int index, short defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static char get(char[] array, int index, char defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static int get(int[] array, int index, int defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static long get(long[] array, int index, long defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static float get(float[] array, int index, float defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the value from the given array at the specified index. If the array is null, or the index is out of
     * bounds, returns the default value.
     *
     * @param array        the given array
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the value from the given array at the specified index
     */
    public static double get(double[] array, int index, double defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @param <T>       the component type
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static <T> int indexOf(T[] array, IndexedPredicate<T> predicate) {
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(i, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * If the element is {@code true}, the passed argument is 1, otherwise 0. If none of the elements pass the
     * predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(boolean[] array, IndexedIntPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            int v = array[i] ? 1 : 0;
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code int}. If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(byte[] array, IndexedIntPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code int}. If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(short[] array, IndexedIntPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code int}. If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(char[] array, IndexedIntPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(int[] array, IndexedIntPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(long[] array, IndexedLongPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            long v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code double}. If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(float[] array, IndexedDoublePredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            double v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static int indexOf(double[] array, IndexedDoublePredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            double v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(Object[], IndexedPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @param <T>       the component type
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static <T> int lastIndexOf(T[] array, IndexedPredicate<T> predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (predicate.test(i, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * If the element is {@code true}, the passed argument is 1, otherwise 0. If none of the elements pass the
     * predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(boolean[], IndexedIntPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(boolean[] array, IndexedIntPredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            int v = array[i] ? 1 : 0;
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code int}. If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(byte[], IndexedIntPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(byte[] array, IndexedIntPredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code int}. If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(short[], IndexedIntPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(short[] array, IndexedIntPredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code int}. If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(char[], IndexedIntPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(char[] array, IndexedIntPredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(int[], IndexedIntPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(int[] array, IndexedIntPredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            int v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(long[], IndexedLongPredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(long[] array, IndexedLongPredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            long v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * Each element will be passed as {@code double}. If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(float[], IndexedDoublePredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(float[] array, IndexedDoublePredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            double v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which can pass the specified predication (return true) at the given array.
     * If none of the elements pass the predication, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(double[], IndexedDoublePredicate)}.
     *
     * @param array     the given array
     * @param predicate the specified predication
     * @return the last index of the element which can pass the specified predication (return true) at the given array
     */
    public static int lastIndexOf(double[] array, IndexedDoublePredicate predicate) {
        for (int i = array.length - 1; i >= 0; i--) {
            double v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Directly returns the given variable arguments as an array.
     *
     * @param elements the given variable arguments
     * @param <T>      the component type
     * @return the given variable arguments as an array
     */
    @SafeVarargs
    public static <T> T[] array(T @RetainedParam ... elements) {
        return elements;
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a fixed-size list backed by the given array
     */
    @SafeVarargs
    public static <T> List<T> asList(T @RetainedParam ... array) {
        return Arrays.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Boolean> asList(boolean @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Byte> asList(byte @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Short> asList(short @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Character> asList(char @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Integer> asList(int @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Long> asList(long @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Float> asList(float @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Double> asList(double @RetainedParam ... array) {
        return ListBack.asList(array);
    }
}
