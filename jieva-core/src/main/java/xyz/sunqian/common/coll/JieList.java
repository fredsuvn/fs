package xyz.sunqian.common.coll;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Static utility class for {@link List}.
 *
 * @author sunqian
 */
public class JieList {

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return an immutable list backed by the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> List<@Nullable T> list(@Nullable T @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Boolean> list(boolean @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Byte> list(byte @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Short> list(short @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Character> list(char @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Integer> list(int @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Long> list(long @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Float> list(float @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     *
     * @param array the given array
     * @return an immutable list backed by the given array
     */
    public static List<Double> list(double @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns a new {@link ArrayList} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link ArrayList} initialing with the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> ArrayList<@Nullable T> arrayList(@Nullable T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * Returns a new {@link LinkedList} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedList} initialing with the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> LinkedList<@Nullable T> linkedList(@Nullable T... array) {
        return new LinkedList<>(Arrays.asList(array));
    }
}
