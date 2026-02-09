package space.sunqian.fs.collect;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.lang.reflect.Type;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.IntFunction;

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

    /**
     * Returns a new set instance with the given set type and initial capacity. If the given set type is unsupported,
     * returns {@code null}. The supported set types follow the {@link #setFunction(Type)}.
     *
     * @param setType         the given set type
     * @param initialCapacity the initial capacity
     * @param <T>             the type of the set elements
     * @return a new set instance, or {@code null} if the given set type is unsupported
     */
    public static <T> @Nullable Set<T> newSet(@Nonnull Type setType, int initialCapacity) {
        IntFunction<@Nonnull Set<T>> setFunction = setFunction(setType);
        if (setFunction == null) {
            return null;
        }
        return setFunction.apply(initialCapacity);
    }

    /**
     * Returns an instance of {@link IntFunction} that creates a new set instance with the given set type. If the given
     * set type is unsupported, returns {@code null}. The supported set types are:
     * <ul>
     *     <li>{@link Iterable}</li>
     *     <li>{@link Collection}</li>
     *     <li>{@link Set}</li>
     *     <li>{@link AbstractSet}</li>
     *     <li>{@link HashSet}</li>
     *     <li>{@link LinkedHashSet}</li>
     *     <li>{@link TreeSet}</li>
     *     <li>{@link CopyOnWriteArraySet}</li>
     *     <li>{@link ConcurrentSkipListSet}</li>
     * </ul>
     *
     * @param setType the given set type
     * @param <T>     the type of the set elements
     * @return an instance of {@link IntFunction} that creates a new set instance with the given set type, or
     * {@code null} if the given set type is unsupported
     */
    public static <T> @Nullable IntFunction<@Nonnull Set<T>> setFunction(@Nonnull Type setType) {
        return Fs.as(CollectBack.setFunction(setType));
    }

    private SetKit() {
    }
}
