package space.sunqian.common.collect;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.Fs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utilities for {@link Set}.
 *
 * @author sunqian
 */
public class SetKit {

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
    public static <T> @Nonnull @Immutable Set<T> set(T @Nonnull ... array) {
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
    public static <T> @Nonnull HashSet<T> hashSet(T @Nonnull ... array) {
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
    public static <T> @Nonnull LinkedHashSet<T> linkedHashSet(T @Nonnull ... array) {
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
    public static <T> @Nonnull @Immutable Set<T> toSet(@Nonnull Iterable<? extends T> it) {
        Object[] array = CollectKit.toArray(it);
        Set<Object> set = set(array);
        return Fs.as(set);
    }

    /**
     * Returns a new {@link HashSet} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link HashSet} initialing with the given iterable
     */
    public static <T> @Nonnull HashSet<T> toHashSet(@Nonnull Iterable<T> it) {
        if (it instanceof Collection) {
            return new HashSet<>((Collection<T>) it);
        }
        return CollectKit.addAll(new HashSet<>(), it);
    }

    /**
     * Returns a new {@link LinkedHashSet} initialing with the given iterable.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return a new {@link LinkedHashSet} initialing with the given iterable
     */
    public static <T> @Nonnull LinkedHashSet<T> toLinkedHashSet(@Nonnull Iterable<T> it) {
        if (it instanceof Collection) {
            return new LinkedHashSet<>((Collection<T>) it);
        }
        return CollectKit.addAll(new LinkedHashSet<>(), it);
    }

    private SetKit() {
    }
}
