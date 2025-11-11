package space.sunqian.common.collect;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.common.Kit;

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
    public static @Nonnull @Immutable List<@Nonnull Boolean> booleanList(boolean @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Byte> byteList(byte @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Short> shortList(short @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Character> charList(char @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Integer> intList(int @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Long> longList(long @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Float> floatList(float @Nonnull @RetainedParam ... array) {
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
    public static @Nonnull @Immutable List<@Nonnull Double> doubleList(double @Nonnull @RetainedParam ... array) {
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
        return Kit.as(list);
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

    /**
     * Returns a composite view of multiple lists as a single list. The returned list provides a unified view of all
     * elements from the provided lists, in the order they are specified. Changes to the underlying lists will be
     * reflected in the composite view.
     * <p>
     * The returned list is un-variable-size, serializable and implements {@link RandomAccess}, and is mutable if the
     * corresponding underlying list is mutable.
     *
     * @param lists the lists to be composited as a view
     * @param <T>   the component type
     * @return a composite view of multiple lists
     */
    @SafeVarargs
    public static <T> @Nonnull List<T> compositeView(@Nonnull List<T> @Nonnull ... lists) {
        return ListBack.compositeList(lists);
    }

    private ListKit() {
    }
}
