package xyz.sunqian.common.collection;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    @SafeVarargs
    public static <T> @Immutable List<T> list(T @RetainedParam ... array) {
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
    public static @Immutable List<Boolean> list(boolean @RetainedParam ... array) {
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
    public static @Immutable List<Byte> list(byte @RetainedParam ... array) {
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
    public static @Immutable List<Short> list(short @RetainedParam ... array) {
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
    public static @Immutable List<Character> list(char @RetainedParam ... array) {
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
    public static @Immutable List<Integer> list(int @RetainedParam ... array) {
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
    public static @Immutable List<Long> list(long @RetainedParam ... array) {
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
    public static @Immutable List<Float> list(float @RetainedParam ... array) {
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
    public static @Immutable List<Double> list(double @RetainedParam ... array) {
        return ListBack.immutableList(array);
    }

    /**
     * Returns a new {@link ArrayList} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link ArrayList} initialing with the given array
     */
    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * Returns a new {@link LinkedList} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedList} initialing with the given array
     */
    @SafeVarargs
    public static <T> LinkedList<T> linkedList(T... array) {
        return new LinkedList<>(Arrays.asList(array));
    }

    /**
     * Returns a new immutable list of which content is added from the given iterable. The content of the iterable is
     * added in its order.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new immutable list of which content is added from the given iterable
     */
    public static <T> @Immutable List<T> toList(Iterable<? extends T> it) {
        Object[] array = JieCollection.toArray(it);
        List<Object> list = list(array);
        return Jie.as(list);
    }

    /**
     * Returns a new {@link ArrayList} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link ArrayList} initialing with the given iterable
     */
    public static <T> ArrayList<T> toArrayList(Iterable<T> it) {
        if (it instanceof Collection) {
            return new ArrayList<>((Collection<T>) it);
        }
        return JieCollection.addAll(new ArrayList<>(), it);
    }

    /**
     * Returns a new {@link LinkedList} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link LinkedList} initialing with the given iterable
     */
    public static <T> LinkedList<T> toLinkedList(Iterable<T> it) {
        if (it instanceof Collection) {
            return new LinkedList<>((Collection<T>) it);
        }
        return JieCollection.addAll(new LinkedList<>(), it);
    }
}
