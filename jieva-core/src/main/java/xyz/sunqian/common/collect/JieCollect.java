package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Static utility class for {@link Collection}.
 *
 * @author sunqian
 */
public class JieCollect {

    /**
     * Returns whether the given iterable is null or empty.
     *
     * @param iterable the given iterable
     * @return whether the given iterable is null or empty
     */
    public static boolean isEmpty(@Nullable Iterable<?> iterable) {
        if (iterable == null) {
            return true;
        }
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return !iterable.iterator().hasNext();
    }

    /**
     * Returns whether the given iterable is not null and empty.
     *
     * @param iterable the given iterable
     * @return whether the given iterable is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    /**
     * Collects the given iterable to an array.
     *
     * @param it the given iterable
     * @return the array
     */
    public static Object @Nonnull [] toArray(@Nonnull Iterable<?> it) {
        if (it instanceof Collection) {
            return ((Collection<?>) it).toArray();
        }
        List<?> list = addAll(new ArrayList<>(), it);
        return list.toArray();
    }

    /**
     * Collects the given iterable to an array of the component type.
     *
     * @param it  the given iterable
     * @param <T> the component type
     * @return the array
     */
    public static <T> T @Nonnull [] toArray(@Nonnull Iterable<?> it, @Nonnull Class<?> componentType) {
        if (it instanceof Collection) {
            Collection<?> collection = (Collection<?>) it;
            T[] array = JieArray.newArray(componentType, collection.size());
            collection.toArray(array);
            return array;
        }
        List<?> list = addAll(new ArrayList<>(), it);
        return toArray(list, componentType);
    }

    /**
     * Puts all elements from the given array into the given collection and returns the given collection.
     *
     * @param collection the given collection
     * @param array      the given array
     * @param <T>        the component type
     * @param <C>        the type of the given collection
     * @return the given collection
     */
    @SafeVarargs
    public static <T, C extends Collection<? super T>> @Nonnull C addAll(
        @Nonnull @OutParam C collection,
        T @Nonnull ... array
    ) {
        collection.addAll(Arrays.asList(array));
        return collection;
    }

    /**
     * Puts all elements from the given iterable into the given collection and returns the given collection.
     *
     * @param collection the given collection
     * @param it         the given iterable
     * @param <T>        the component type
     * @param <C>        the type of the given collection
     * @return the given collection
     */
    public static <T, C extends Collection<? super T>> @Nonnull C addAll(
        @Nonnull @OutParam C collection,
        @Nonnull Iterable<? extends T> it
    ) {
        if (it instanceof Collection) {
            collection.addAll((Collection<? extends T>) it);
        } else {
            for (T e : it) {
                collection.add(e);
            }
        }
        return collection;
    }

    /**
     * Returns the given enumeration as an {@link Iterator}.
     *
     * @param enumeration the given enumeration
     * @param <T>         the component type
     * @return the given enumeration as an {@link Iterator}
     */
    public static <T> @Nonnull Iterator<T> asIterator(@Nonnull Enumeration<? extends T> enumeration) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }

    /**
     * Returns the given iterator as an {@link Enumeration}.
     *
     * @param iterator the given iterator
     * @param <T>      the component type
     * @return the given iterator as an {@link Enumeration}
     */
    public static <T> @Nonnull Enumeration<T> asEnumeration(@Nonnull Iterator<? extends T> iterator) {
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();

            }
        };
    }
}
