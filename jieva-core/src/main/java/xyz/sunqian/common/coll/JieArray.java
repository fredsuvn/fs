package xyz.sunqian.common.coll;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

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
    public static <T> boolean isEmpty(@Nullable T @Nullable [] array) {
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
    public static <T> boolean isNotEmpty(@Nullable T @Nullable [] array) {
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
    public static <T> @Nullable T @Nullable [] fill(@Nullable T[] array, @Nullable T value) {
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
    public static boolean[] fill(boolean[] array, boolean value) {
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
    public static byte[] fill(byte[] array, byte value) {
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
    public static short[] fill(short[] array, short value) {
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
    public static char[] fill(char[] array, char value) {
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
    public static int[] fill(int[] array, int value) {
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
    public static long[] fill(long[] array, long value) {
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
    public static float[] fill(float[] array, float value) {
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
    public static double[] fill(double[] array, double value) {
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
    public static <T, R> @Nullable R[] map(
        @Nullable T[] source, @Nullable R[] dest, Function<? super @Nullable T, ? extends @Nullable R> mapper
    ) {
        @Nullable R[] result;
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
     * corresponding index. The component type of the new array is the type of the first non-null new element. If all
     * new elements are null, an {@link UnsupportedOperationException} will be thrown.
     *
     * @param source the source array
     * @param mapper the specified mapper
     * @param <T>    the component type of the source array
     * @param <R>    the component type of the dest array
     * @return a new result array
     * @throws UnsupportedOperationException if all new elements are null
     */
    public static <T, R> @Nullable R[] map(
        @Nullable T[] source, Function<? super @Nullable T, ? extends @Nullable R> mapper
    ) throws UnsupportedOperationException {
        for (int i = 0; i < source.length; i++) {
            @Nullable R r = mapper.apply(source[i]);
            if (r != null) {
                @Nullable R[] dest = newArray(r.getClass(), source.length);
                map0(source, dest, i, mapper);
                return dest;
            }
        }
        throw new UnsupportedOperationException("Can not resolve the component type.");
    }

    private static <T, R> void map0(
        @Nullable T[] source, @Nullable R[] dest, int start, Function<? super @Nullable T, ? extends @Nullable R> mapper
    ) {
        for (int i = start; i < source.length; i++) {
            @Nullable T t = source[i];
            @Nullable R r = mapper.apply(t);
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
    public static <T> T get(@Nullable T @Nullable [] array, int index, T defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        @Nullable T value = array[index];
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
     * Returns the first index of the element which can pass the specified predication (return true) at the given array.
     * If the element is not found, returns -1.
     *
     * @param array the given array
     * @param predicate the specified predication
     * @param <T>   the component type
     * @return the first index of the element which can pass the specified predication (return true) at the given array
     */
    public static <T> int indexOf(@Nullable T[] array, Predicate<@Nullable T> predicate) {
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(boolean[] array, Pre) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(byte[] array, byte element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(short[] array, short element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(char[] array, char element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(int[] array, int element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(long[] array, long element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(float[] array, float element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the first index of the element same with the specified element at the given array
     */
    public static int indexOf(double[] array, double element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @param <T>     the component type
     * @return the last index of the element same with the specified element at the given array
     */
    public static <T> int lastIndexOf(T[] array, @Nullable T element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (Objects.equals(element, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(boolean[] array, boolean element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(byte[] array, byte element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(short[] array, short element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(char[] array, char element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(int[] array, int element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(long[] array, long element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(float[] array, float element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the element same with the specified element at the given array. If the element is not
     * found, returns -1.
     *
     * @param array   the given array
     * @param element the specified element
     * @return the last index of the element same with the specified element at the given array
     */
    public static int lastIndexOf(double[] array, double element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (element == array[i]) {
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
    public static <T> T[] array(T... elements) {
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

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a fixed-size list backed by the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> List<T> immutableList(T... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Boolean> immutableList(boolean[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Byte> immutableList(byte[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Short> immutableList(short[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Character> immutableList(char[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Integer> immutableList(int[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Long> immutableList(long[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Float> immutableList(float[] array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. Changes to the backing array "write through" to the returned
     * list, but the list is immutable. The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return a fixed-size list backed by the given array
     */
    public static List<Double> immutableList(double[] array) {
        return ListBack.immutableList(array);
    }
}
