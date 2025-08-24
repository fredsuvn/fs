package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Utilities for {@link List}.
 *
 * @author sunqian
 */
public class ListKit {

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
    public static <T> @Nonnull @Immutable List<T> list(T @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Boolean> list(boolean @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Byte> list(byte @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Short> list(short @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Character> list(char @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Integer> list(int @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Long> list(long @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Float> list(float @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Double> list(double @Nonnull @RetainedParam ... array) {
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
    public static <T> @Nonnull ArrayList<T> arrayList(T @Nonnull ... array) {
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
    public static <T> @Nonnull LinkedList<T> linkedList(T @Nonnull ... array) {
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
    public static <T> @Nonnull @Immutable List<T> toList(@Nonnull Iterable<? extends T> it) {
        Object[] array = CollectKit.toArray(it);
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
    public static <T> @Nonnull ArrayList<T> toArrayList(@Nonnull Iterable<T> it) {
        if (it instanceof Collection) {
            return new ArrayList<>((Collection<T>) it);
        }
        return CollectKit.addAll(new ArrayList<>(), it);
    }

    /**
     * Returns a new {@link LinkedList} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link LinkedList} initialing with the given iterable
     */
    public static <T> @Nonnull LinkedList<T> toLinkedList(@Nonnull Iterable<T> it) {
        if (it instanceof Collection) {
            return new LinkedList<>((Collection<T>) it);
        }
        return CollectKit.addAll(new LinkedList<>(), it);
    }
}
