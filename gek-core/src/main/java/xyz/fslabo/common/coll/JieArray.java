package xyz.fslabo.common.coll;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * This is a static utilities class provides utilities for {@code array}.
 *
 * @author fredsuvn
 */
public class JieArray {

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static <T> boolean isEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
     */
    public static boolean isEmpty(@Nullable double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static <T> boolean isNotEmpty(@Nullable T[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable boolean[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable byte[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable short[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable char[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable int[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable long[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable float[] array) {
        return !isEmpty(array);
    }

    /**
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable double[] array) {
        return !isEmpty(array);
    }

    /**
     * Maps source array (component type {@code T}) to dest array (component type {@code R}) by specified mapper.
     * <p>
     * If given dest array's length equals to source array, the given dest array will be returned, otherwise, a new
     * array with same length of source array will be created and returned.
     * <p>
     * Each element of source array will be mapped to a new element by specified mapper, then set into the result array
     * at corresponding index.
     *
     * @param source the source array
     * @param dest   the dest array
     * @param mapper specified mapper
     * @param <T>    component type of source array
     * @param <R>    component type of dest array
     * @return the given dest or a new result array
     */
    public static <T, R> R[] map(T[] source, R[] dest, Function<? super T, ? extends R> mapper) {
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
     * Maps source array (component type {@code T}) to dest array (component type {@code R}) by specified mapper.
     * <p>
     * This method will try to create a new array as mapping result. The element type of the new array will be
     * determined by the type of the first non-null value returned by the specified mapper. If returned value of mapper
     * are null, an {@link UnsupportedOperationException} will be thrown.
     * <p>
     * Each element of source array will be mapped to a new element by specified mapper, then set into the result array
     * at corresponding index.
     *
     * @param source the source array
     * @param mapper specified mapper
     * @param <T>    component type of source array
     * @param <R>    component type of dest array
     * @return a new result array
     */
    public static <T, R> R[] map(T[] source, Function<? super T, ? extends R> mapper) {
        for (int i = 0; i < source.length; i++) {
            R r = mapper.apply(source[i]);
            if (r != null) {
                R[] dest = newArray(r.getClass(), source.length);
                map0(source, dest, i, mapper);
                return dest;
            }
        }
        throw new UnsupportedOperationException(Impls.UNABLE_NULL_TYPE);
    }

    private static <T, R> void map0(T[] source, R[] dest, int start, Function<? super T, ? extends R> mapper) {
        for (int i = start; i < source.length; i++) {
            dest[i] = mapper.apply(source[i]);
        }
    }

    /**
     * Creates a new array of given component type and length, all elements will be initialized to {@code null}.
     *
     * @param componentType given component type
     * @param length        given length
     * @param <A>           type of array, including primitive array
     * @return created array
     */
    public static <A> A newArray(Class<?> componentType, int length) {
        return Jie.as(Array.newInstance(componentType, length));
    }

    /**
     * Returns value from given array at specified index, if the value is null or failed to obtain, return default
     * value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @param <T>          component type
     * @return value from given array at specified index, if the value is null or failed to obtain, return default value
     */
    public static <T> T get(@Nullable T[] array, int index, @Nullable T defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        T value = array[index];
        return value == null ? defaultValue : value;
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static boolean get(@Nullable boolean[] array, int index, boolean defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static byte get(@Nullable byte[] array, int index, byte defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static short get(@Nullable short[] array, int index, short defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static char get(@Nullable char[] array, int index, char defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static int get(@Nullable int[] array, int index, int defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static long get(@Nullable long[] array, int index, long defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static float get(@Nullable float[] array, int index, float defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     * @return value from given array at specified index, if failed to obtain, return default value
     */
    public static double get(@Nullable double[] array, int index, double defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @param <T>     component type
     * @return first index of given element at given array, or -1 if not found
     */
    public static <T> int indexOf(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(element, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
     */
    public static int indexOf(boolean[] array, boolean element) {
        for (int i = 0; i < array.length; i++) {
            if (element == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns first index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return first index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @param <T>     component type
     * @return last index of given element at given array, or -1 if not found
     */
    public static <T> int lastIndexOf(T[] array, T element) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (Objects.equals(element, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * Returns last index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @return last index of given element at given array, or -1 if not found
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
     * RReturns given elements directly. This method is used to cast variable arguments to array.
     *
     * @param elements given elements
     * @param <T>      component type
     * @return given elements itself as an array
     */
    @SafeVarargs
    public static <T> T[] array(T... elements) {
        return elements;
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @param <T>   component type
     * @return given array as a mutable and size-fixed list
     */
    @SafeVarargs
    public static <T> List<T> asList(T... array) {
        return Arrays.asList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Boolean> asList(boolean[] array) {
        return new BooleanArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Byte> asList(byte[] array) {
        return new ByteArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Short> asList(short[] array) {
        return new ShortArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Character> asList(char[] array) {
        return new CharacterArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Integer> asList(int[] array) {
        return new IntegerArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Long> asList(long[] array) {
        return new LongArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Float> asList(float[] array) {
        return new FloatArrayList(array);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     *
     * @param array given array
     * @return given array as a mutable and size-fixed list
     */
    public static List<Double> asList(double[] array) {
        return new DoubleArrayList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @param <T>   component type
     * @return given array as an immutable list
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... array) {
        return new ImmutableList<>(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Boolean> listOf(boolean[] array) {
        return new BooleanImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Byte> listOf(byte[] array) {
        return new ByteImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Short> listOf(short[] array) {
        return new ShortImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Character> listOf(char[] array) {
        return new CharImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Integer> listOf(int[] array) {
        return new IntImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Long> listOf(long[] array) {
        return new LongImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Float> listOf(float[] array) {
        return new FloatImmutableList(array);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     *
     * @param array given array
     * @return given array as an immutable list
     */
    public static List<Double> listOf(double[] array) {
        return new DoubleImmutableList(array);
    }

    private static final class BooleanArrayList extends AbstractList<Boolean> {

        private final boolean[] array;

        private BooleanArrayList(boolean[] array) {
            this.array = array;
        }

        @Override
        public Boolean get(int index) {
            return array[index];
        }

        @Override
        public Boolean set(int index, Boolean element) {
            Boolean old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ByteArrayList extends AbstractList<Byte> {

        private final byte[] array;

        private ByteArrayList(byte[] array) {
            this.array = array;
        }

        @Override
        public Byte get(int index) {
            return array[index];
        }

        @Override
        public Byte set(int index, Byte element) {
            Byte old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ShortArrayList extends AbstractList<Short> {

        private final short[] array;

        private ShortArrayList(short[] array) {
            this.array = array;
        }

        @Override
        public Short get(int index) {
            return array[index];
        }

        @Override
        public Short set(int index, Short element) {
            short old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class CharacterArrayList extends AbstractList<Character> {

        private final char[] array;

        private CharacterArrayList(char[] array) {
            this.array = array;
        }

        @Override
        public Character get(int index) {
            return array[index];
        }

        @Override
        public Character set(int index, Character element) {
            Character old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class IntegerArrayList extends AbstractList<Integer> {

        private final int[] array;

        private IntegerArrayList(int[] array) {
            this.array = array;
        }

        @Override
        public Integer get(int index) {
            return array[index];
        }

        @Override
        public Integer set(int index, Integer element) {
            Integer old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class LongArrayList extends AbstractList<Long> {

        private final long[] array;

        private LongArrayList(long[] array) {
            this.array = array;
        }

        @Override
        public Long get(int index) {
            return array[index];
        }

        @Override
        public Long set(int index, Long element) {
            Long old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class FloatArrayList extends AbstractList<Float> {

        private final float[] array;

        private FloatArrayList(float[] array) {
            this.array = array;
        }

        @Override
        public Float get(int index) {
            return array[index];
        }

        @Override
        public Float set(int index, Float element) {
            Float old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class DoubleArrayList extends AbstractList<Double> {

        private final double[] array;

        private DoubleArrayList(double[] array) {
            this.array = array;
        }

        @Override
        public Double get(int index) {
            return array[index];
        }

        @Override
        public Double set(int index, Double element) {
            Double old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ImmutableList<T> extends AbstractList<T> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final T[] array;

        private ImmutableList(T[] array) {
            this.array = array;
        }

        @Override
        public T get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class BooleanImmutableList
        extends AbstractList<Boolean> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final boolean[] array;

        private BooleanImmutableList(boolean[] array) {
            this.array = array;
        }

        @Override
        public Boolean get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ByteImmutableList
        extends AbstractList<Byte> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final byte[] array;

        private ByteImmutableList(byte[] array) {
            this.array = array;
        }

        @Override
        public Byte get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ShortImmutableList
        extends AbstractList<Short> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final short[] array;

        private ShortImmutableList(short[] array) {
            this.array = array;
        }

        @Override
        public Short get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class CharImmutableList
        extends AbstractList<Character> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final char[] array;

        private CharImmutableList(char[] array) {
            this.array = array;
        }

        @Override
        public Character get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class IntImmutableList
        extends AbstractList<Integer> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final int[] array;

        private IntImmutableList(int[] array) {
            this.array = array;
        }

        @Override
        public Integer get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class LongImmutableList
        extends AbstractList<Long> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final long[] array;

        private LongImmutableList(long[] array) {
            this.array = array;
        }

        @Override
        public Long get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class FloatImmutableList
        extends AbstractList<Float> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final float[] array;

        private FloatImmutableList(float[] array) {
            this.array = array;
        }

        @Override
        public Float get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class DoubleImmutableList
        extends AbstractList<Double> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final double[] array;

        private DoubleImmutableList(double[] array) {
            this.array = array;
        }

        @Override
        public Double get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }
}
