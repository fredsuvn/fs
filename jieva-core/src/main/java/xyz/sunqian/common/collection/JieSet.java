package xyz.sunqian.common.collection;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.common.base.Jie;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Static utility class for {@link Set}.
 *
 * @author sunqian
 */
public class JieSet {

    /**
     * Returns a new immutable set of which content is added from the given array. The content of the set is added in
     * array order, and the duplicate elements will be ignored. The behavior of this method is equivalent to:
     * <pre>{@code
     * return Collections.unmodifiableSet(linkedHashSet(array));
     * }</pre>
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new immutable set of which content is added from the given array
     */
    @SafeVarargs
    public static <T> @Immutable Set<T> set(T... array) {
        return Collections.unmodifiableSet(linkedHashSet(array));
    }

    /**
     * Returns a new {@link HashSet} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link HashSet} initialing with the given array
     */
    @SafeVarargs
    public static <T> HashSet<T> hashSet(T... array) {
        return new HashSet<>(Arrays.asList(array));
    }

    /**
     * Returns a new {@link LinkedHashSet} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedHashSet} initialing with the given array
     */
    @SafeVarargs
    public static <T> LinkedHashSet<T> linkedHashSet(T... array) {
        return new LinkedHashSet<>(Arrays.asList(array));
    }

    /**
     * Returns a new immutable set of which content is added from the given iterable. The content of the iterable is
     * added in its order, and the duplicate elements will be ignored. This is the iterable version of
     * {@link #set(Object[])}.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new immutable set of which content is added from the given iterable
     */
    public static <T> @Immutable List<T> toSet(Iterable<? extends T> it) {
        Object[] array = JieCollection.toArray(it);
        Set<Object> set = set(array);
        return Jie.as(set);
    }

    /**
     * Returns a new {@link HashSet} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link HashSet} initialing with the given iterable
     */
    public static <T> HashSet<T> toHashSet(Iterable<T> it) {
        if (it instanceof Collection) {
            return new HashSet<>((Collection<T>) it);
        }
        return JieCollection.addAll(new HashSet<>(), it);
    }

    /**
     * Returns a new {@link LinkedHashSet} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link LinkedHashSet} initialing with the given iterable
     */
    public static <T> LinkedHashSet<T> toLinkedHashSet(Iterable<T> it) {
        if (it instanceof Collection) {
            return new LinkedHashSet<>((Collection<T>) it);
        }
        return JieCollection.addAll(new LinkedHashSet<>(), it);
    }
}
