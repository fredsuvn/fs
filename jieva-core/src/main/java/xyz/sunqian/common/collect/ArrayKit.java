package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
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
public class ArrayKit {

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @param <T>   the component type of the given array
     * @return whether the given array is null or empty
     */
    public static <T> boolean isEmpty(T @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(boolean @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(byte @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(short @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(char @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(int @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(long @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(float @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is null or empty.
     *
     * @param array the given array
     * @return whether the given array is null or empty
     */
    public static boolean isEmpty(double @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @param <T>   the component type of the given array
     * @return whether the given array is not null and empty
     */
    public static <T> boolean isNotEmpty(T @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(boolean @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(byte @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(short @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(char @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(int @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(long @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(float @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether the given array is not null and empty.
     *
     * @param array the given array
     * @return whether the given array is not null and empty
     */
    public static boolean isNotEmpty(double @Nullable [] array) {
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
    public static <T> T @Nonnull [] fill(T @Nonnull @OutParam [] array, T value) {
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
    public static boolean @Nonnull [] fill(boolean @Nonnull @OutParam [] array, boolean value) {
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
    public static byte @Nonnull [] fill(byte @Nonnull @OutParam [] array, byte value) {
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
    public static short @Nonnull [] fill(short @Nonnull @OutParam [] array, short value) {
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
    public static char @Nonnull [] fill(char @Nonnull @OutParam [] array, char value) {
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
    public static int @Nonnull [] fill(int @Nonnull @OutParam [] array, int value) {
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
    public static long @Nonnull [] fill(long @Nonnull @OutParam [] array, long value) {
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
    public static float @Nonnull [] fill(float @Nonnull @OutParam [] array, float value) {
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
    public static double @Nonnull [] fill(double @Nonnull @OutParam [] array, double value) {
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
    public static <T, R> R @Nonnull [] map(
        T @Nonnull [] source,
        R @Nonnull @OutParam [] dest,
        @Nonnull Function<? super T, ? extends R> mapper
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
    public static <T, R> R @Nonnull [] map(
        T @Nonnull [] source,
        @Nonnull Function<? super T, ? extends R> mapper
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
        T @Nonnull [] source,
        R @Nonnull @OutParam [] dest,
        int start,
        @Nonnull Function<? super T, ? extends R> mapper
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
    public static <A> @Nonnull A newArray(@Nonnull Class<?> componentType, int length) {
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
    public static <T> T get(T @Nullable [] array, int index, T defaultValue) {
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
    public static boolean get(boolean @Nullable [] array, int index, boolean defaultValue) {
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
    public static byte get(byte @Nullable [] array, int index, byte defaultValue) {
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
    public static short get(short @Nullable [] array, int index, short defaultValue) {
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
    public static char get(char @Nullable [] array, int index, char defaultValue) {
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
    public static int get(int @Nullable [] array, int index, int defaultValue) {
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
    public static long get(long @Nullable [] array, int index, long defaultValue) {
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
    public static float get(float @Nullable [] array, int index, float defaultValue) {
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
    public static double get(double @Nullable [] array, int index, double defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns the first index of the element which equals the specified value via {@link Jie#equals(Object, Object)} at
     * the given array. If none of the elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @param <T>   the component type
     * @return the first index of the element which equals the specified value via {@link Jie#equals(Object, Object)} at
     * the given array
     */
    public static <T> int indexOf(T @Nonnull [] array, T value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. If the element is
     * {@code true}, the passed argument is 1, otherwise 0. If none of the elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(boolean @Nonnull [] array, boolean value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code int}. If none of the elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(byte @Nonnull [] array, byte value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code int}. If none of the elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(short @Nonnull [] array, short value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code int}. If none of the elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(char @Nonnull [] array, char value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. If none of the
     * elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(int @Nonnull [] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. If none of the
     * elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(long @Nonnull [] array, long value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code double}. If none of the elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(float @Nonnull [] array, float value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element which equals the specified value at the given array. If none of the
     * elements found, returns -1.
     *
     * @param array the given array
     * @param value the specified value
     * @return the first index of the element which equals the specified value at the given array
     */
    public static int indexOf(double @Nonnull [] array, double value) {
        for (int i = 0; i < array.length; i++) {
            if (Jie.equals(array[i], value)) {
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
     * @param <T>       the component type
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static <T> int indexOf(T @Nonnull [] array, @Nonnull IndexedPredicate<T> predicate) {
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
    public static int indexOf(boolean @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int indexOf(byte @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int indexOf(short @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int indexOf(char @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int indexOf(int @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int indexOf(long @Nonnull [] array, @Nonnull IndexedLongPredicate predicate) {
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
    public static int indexOf(float @Nonnull [] array, @Nonnull IndexedDoublePredicate predicate) {
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
    public static int indexOf(double @Nonnull [] array, @Nonnull IndexedDoublePredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            double v = array[i];
            if (predicate.test(i, v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value via {@link Jie#equals(Object, Object)} at
     * the given array. If none of the elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(Object[], Object)}.
     *
     * @param array the given array
     * @param value the specified value
     * @param <T>   the component type
     * @return the last index of the element which equals the specified value via {@link Jie#equals(Object, Object)} at
     * the given array
     */
    public static <T> int lastIndexOf(T @Nonnull [] array, T value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (Jie.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. If the element is
     * {@code true}, the passed argument is 1, otherwise 0. If none of the elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(boolean[], boolean)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(boolean @Nonnull [] array, boolean value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code int}. If none of the elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(byte[], byte)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(byte @Nonnull [] array, byte value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code int}. If none of the elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(short[], short)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(short @Nonnull [] array, short value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code int}. If none of the elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(char[], char)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(char @Nonnull [] array, char value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. If none of the
     * elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(int[], int)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(int @Nonnull [] array, int value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. If none of the
     * elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(long[], long)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(long @Nonnull [] array, long value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. Each element will be
     * passed as {@code double}. If none of the elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(float[], float)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(float @Nonnull [] array, float value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element which equals the specified value at the given array. If none of the
     * elements found, returns -1.
     * <p>
     * It is the reverse order method of the {@link #indexOf(double[], double)}.
     *
     * @param array the given array
     * @param value the specified value
     * @return the last index of the element which equals the specified value at the given array
     */
    public static int lastIndexOf(double @Nonnull [] array, double value) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
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
    public static <T> int lastIndexOf(T @Nonnull [] array, @Nonnull IndexedPredicate<T> predicate) {
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
    public static int lastIndexOf(boolean @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int lastIndexOf(byte @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int lastIndexOf(short @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int lastIndexOf(char @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int lastIndexOf(int @Nonnull [] array, @Nonnull IndexedIntPredicate predicate) {
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
    public static int lastIndexOf(long @Nonnull [] array, @Nonnull IndexedLongPredicate predicate) {
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
    public static int lastIndexOf(float @Nonnull [] array, @Nonnull IndexedDoublePredicate predicate) {
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
    public static int lastIndexOf(double @Nonnull [] array, @Nonnull IndexedDoublePredicate predicate) {
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
    public static <T> T @Nonnull [] array(T @Nonnull @RetainedParam ... elements) {
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
    public static <T> @Nonnull List<T> asList(T @Nonnull @RetainedParam ... array) {
        return Arrays.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Boolean> asList(boolean @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Byte> asList(byte @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Short> asList(short @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Character> asList(char @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Integer> asList(int @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Long> asList(long @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Float> asList(float @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }

    /**
     * Returns a fixed-size list backed by the given array. Changes to the returned list "write through" to the array,
     * and vice versa. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static @Nonnull List<@Nonnull Double> asList(double @Nonnull @RetainedParam ... array) {
        return ListBack.asList(array);
    }
}
