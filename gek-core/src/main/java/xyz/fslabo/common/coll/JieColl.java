package xyz.fslabo.common.coll;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Collection utilities.
 *
 * @author fresduvn
 */
public class JieColl {

    /**
     * Returns whether given iterable is null or empty.
     *
     * @param iterable given iterable
     * @return whether given iterable is null or empty
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
     * Returns whether given iterable is not null and empty.
     *
     * @param iterable given iterable
     * @return whether given iterable is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    /**
     * Returns whether given map is null or empty.
     *
     * @param map given map
     * @return whether given map is null or empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

    /**
     * Returns whether given map is not null and empty.
     *
     * @param map given map
     * @return whether given map is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Returns give elements as an immutable list.
     * <p>
     * Note that although the returned list is immutable, the list directly references the given element array, any
     * changing for the content of the array will correspondingly change the content of the list.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable list
     */
    @Immutable
    public static <T> List<T> asImmutableList(T[] elements) {
        return JieArray.isEmpty(elements) ? Collections.emptyList() : new ImmutableList<>(elements);
    }

    /**
     * Collects given elements into an {@link Object} array. If given elements is empty, return an empty array.
     *
     * @param elements given elements
     * @return an {@link Object} array
     */
    public static Object[] toObjectArray(@Nullable Iterable<?> elements) {
        if (isEmpty(elements)) {
            return new Object[0];
        }
        if (elements instanceof Collection) {
            return ((Collection<?>) elements).toArray();
        }
        return stream(elements).toArray();
    }

    /**
     * Collects given elements into an array.
     * <p>
     * This method will try to create a new array as result. The element type of the new array will be determined by the
     * type of the first non-null value of given elements. If given elements is null or all given elements are null, an
     * {@link UnsupportedOperationException} will be thrown.
     *
     * @param elements given elements
     * @return a new array as result
     */
    public static <T> T[] toArray(Iterable<T> elements) {
        if (isEmpty(elements)) {
            throw new UnsupportedOperationException("Given elements is empty.");
        }
        if (elements instanceof Collection) {
            return toArray((Collection<T>) elements);
        }
        List<T> list = addAll(new LinkedList<>(), elements);
        return toArray(list);
    }

    /**
     * Collects given elements into an array.
     * <p>
     * This method will try to create a new array as result. The element type of the new array will be determined by the
     * type of the first non-null value of given elements. If given elements is null or all given elements are null, an
     * {@link UnsupportedOperationException} will be thrown.
     *
     * @param elements given elements
     * @return a new array as result
     */
    public static <T> T[] toArray(Collection<T> elements) {
        if (isEmpty(elements)) {
            throw new UnsupportedOperationException("Given elements is empty.");
        }
        Iterator<T> it = elements.iterator();
        int i = 0;
        while (it.hasNext()) {
            T t = it.next();
            if (t != null) {
                T[] array = JieArray.newArray(t.getClass(), elements.size());
                array[i] = t;
                return toArray0(array, i + 1, it);
            }
            i++;
        }
        throw new UnsupportedOperationException(Constants.UNABLE_NULL_TYPE);
    }

    private static <T> T[] toArray0(T[] array, int start, Iterator<T> elements) {
        for (int i = start; i < array.length; i++) {
            array[i] = elements.next();
        }
        return array;
    }

    /**
     * Collects given elements into a new array which is created with specified component type. If given elements is
     * empty, return an empty array.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return a new array which is created with specified component type
     */
    public static <T> T[] toArray(@Nullable Iterable<? extends T> elements, Class<T> componentType) {
        if (isEmpty(elements)) {
            return JieArray.newArray(componentType, 0);
        }
        return stream(elements).toArray(size -> JieArray.newArray(componentType, size));
    }

    /**
     * Collects given elements into a new array which is created with specified component type.
     * <p>
     * This method maps each element from type {@code T} to type {@code R} by specified {@code mapper}. If given
     * elements is empty, return an empty array.
     *
     * @param elements given elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return array
     */
    public static <T, R> R[] toArray(
        @Nullable Iterable<? extends T> elements, Class<R> type, Function<? super T, ? extends R> mapper) {
        if (isEmpty(elements)) {
            return JieArray.newArray(type, 0);
        }
        return stream(elements).map(mapper).toArray(size -> JieArray.newArray(type, size));
    }

    /**
     * Collects given elements into a new immutable list. If given elements is empty, return an empty list.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable list
     */
    @Immutable
    public static <T> List<T> toList(T[] elements) {
        return JieArray.isEmpty(elements) ? Collections.emptyList() : new ImmutableList<>(elements.clone());
    }

    /**
     * Collects given elements into a new immutable list. If given elements is empty, return an empty list.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable list
     */
    @Immutable
    public static <T> List<T> toList(@Nullable Iterable<? extends T> elements) {
        if (isEmpty(elements)) {
            return Collections.emptyList();
        }
        return new ImmutableList<>(toObjectArray(elements));
    }

    /**
     * Collects given elements into a new immutable list, maps each element from type {@code T} to type {@code R} by
     * specified {@code mapper}. If given elements is empty, return an empty list.
     *
     * @param elements source elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return immutable list
     */
    @Immutable
    public static <T, R> List<R> toList(
        @Nullable T[] elements, Function<? super T, ? extends R> mapper) {
        if (JieArray.isEmpty(elements)) {
            return Collections.emptyList();
        }
        return new ImmutableList<>(stream(elements).map(mapper).toArray());
    }

    /**
     * Collects given elements into a new immutable list, maps each element from type {@code T} to type {@code R} by
     * specified {@code mapper}. If given elements is empty, return an empty list.
     *
     * @param elements source elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return immutable list
     */
    @Immutable
    public static <T, R> List<R> toList(
        @Nullable Iterable<? extends T> elements, Function<? super T, ? extends R> mapper) {
        if (isEmpty(elements)) {
            return Collections.emptyList();
        }
        return new ImmutableList<>(stream(elements).map(mapper).toArray());
    }

    /**
     * Collects given elements into a new immutable string list, maps each element to string. If given elements is
     * empty, return an empty list.
     *
     * @param elements given elements
     * @return immutable string list
     */
    @Immutable
    public static List<String> toStringList(@Nullable Object[] elements) {
        return toList(elements, String::valueOf);
    }

    /**
     * Collects given elements into a new immutable string list, maps each element to string. If given elements is
     * empty, return an empty list.
     *
     * @param elements given elements
     * @return immutable string list
     */
    @Immutable
    public static List<String> toStringList(@Nullable Iterable<?> elements) {
        return toList(elements, String::valueOf);
    }

    /**
     * Collects given elements into a new immutable set. If given elements is empty, return an empty set.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable set
     */
    @Immutable
    public static <T> Set<T> toSet(T[] elements) {
        if (JieArray.isEmpty(elements)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(addAll(new LinkedHashSet<>(), elements));
    }

    /**
     * Collects given elements into a new immutable set. If given elements is empty, return an empty set.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable set
     */
    @Immutable
    public static <T> Set<T> toSet(@Nullable Iterable<? extends T> elements) {
        if (isEmpty(elements)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(addAll(new LinkedHashSet<>(), elements));
    }

    /**
     * Collects given elements into a new immutable set, maps each element from type {@code T} to type {@code R} by
     * specified {@code mapper}. If given elements is empty, return an empty set.
     *
     * @param elements source elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return immutable set
     */
    @Immutable
    public static <T, R> Set<R> toSet(
        @Nullable T[] elements, Function<? super T, ? extends R> mapper) {
        if (JieArray.isEmpty(elements)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(addAll(new LinkedHashSet<>(), elements, mapper));
    }

    /**
     * Collects given elements into a new immutable set, maps each element from type {@code T} to type {@code R} by
     * specified {@code mapper}. If given elements is empty, return an empty set.
     *
     * @param elements source elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return immutable set
     */
    @Immutable
    public static <T, R> Set<R> toSet(
        @Nullable Iterable<? extends T> elements, Function<? super T, ? extends R> mapper) {
        if (isEmpty(elements)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(addAll(new LinkedHashSet<>(), elements, mapper));
    }

    /**
     * Collects given elements int a new immutable string set, maps each element to string. If given elements is empty,
     * return an empty set.
     *
     * @param elements given elements
     * @return immutable string set
     */
    @Immutable
    public static Set<String> toStringSet(@Nullable Object[] elements) {
        return toSet(elements, String::valueOf);
    }

    /**
     * Collects given elements int a new immutable string set, maps each element to string. If given elements is empty,
     * return an empty set.
     *
     * @param elements given elements
     * @return immutable string set
     */
    @Immutable
    public static Set<String> toStringSet(@Nullable Iterable<?> elements) {
        return toSet(elements, String::valueOf);
    }

    /**
     * Collects given elements into a new immutable map. If given elements is empty, return an empty map.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on. If last key-{@code n}
     * is not followed by a value-{@code n}, it will be ignored.
     *
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <T>      type of element
     * @return immutable map
     */
    @Immutable
    public static <K, V, T> Map<K, V> toMap(T[] elements) {
        return JieArray.isEmpty(elements) ?
            Collections.emptyMap() : Collections.unmodifiableMap(putAll(new LinkedHashMap<>(), elements));
    }

    /**
     * Collects given elements into a new immutable map. If given elements is empty, return an empty map.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on. If last key-{@code n}
     * is not followed by a value-{@code n}, it will be ignored.
     *
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <T>      type of element
     * @return immutable map
     */
    @Immutable
    public static <K, V, T> Map<K, V> toMap(@Nullable Iterable<T> elements) {
        if (isEmpty(elements)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(putAll(new LinkedHashMap<>(), elements));
    }

    /**
     * Maps source map to a new immutable map, each entry of source map will be mapped from {@code K1} to {@code K2} and
     * {@code V1} and {@code V2} by {@code keyMapper} and {@code valueMapper}. If given elements is empty, return an
     * empty map.
     *
     * @param source      source map
     * @param keyMapper   key conversion function
     * @param valueMapper value conversion function
     * @param <K1>        type of source key
     * @param <V1>        type of source value
     * @param <K2>        type of target key
     * @param <V2>        type of target value
     * @return immutable map
     */
    @Immutable
    public static <K1, V1, K2, V2> Map<K2, V2> toMap(
        @Nullable Map<K1, V1> source,
        Function<? super K1, ? extends K2> keyMapper,
        Function<? super V1, ? extends V2> valueMapper
    ) {
        if (isEmpty(source)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(putAll(new LinkedHashMap<>(), source, keyMapper, valueMapper));
    }

    /**
     * Collects given elements into a new immutable map of which keys and values are type of string, each entry of
     * source map will be mapped to string. If given properties is empty, return an empty map.
     *
     * @param properties given properties
     * @return immutable string map
     */
    @Immutable
    public static Map<String, String> toStringMap(Properties properties) {
        return toMap(properties, String::valueOf, String::valueOf);
    }

    /**
     * Splits each of given elements to key-value pair, then puts them into a new immutable map. Each element will be
     * mapped by {@code keyMapper} and {@code valueMapper} to product key and value. It is equivalent to:
     * <pre>
     *     for (T element : elements) {
     *         dest.put(keyMapper.apply(element), valueMapper.apply(element));
     *     }
     * </pre>
     * If given elements is empty, return an empty map.
     *
     * @param elements    given elements
     * @param keyMapper   key mapper
     * @param valueMapper value mapper
     * @param <T>         type of source element
     * @param <K>         type of keys of target map
     * @param <V>         type of values of target map
     * @return immutable map
     */
    @Immutable
    public static <T, K, V> Map<K, V> toPairs(
        @Nullable T[] elements,
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends V> valueMapper
    ) {
        if (JieArray.isEmpty(elements)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(putAll(new LinkedHashMap<>(), elements, keyMapper, valueMapper));
    }

    /**
     * Splits each of given elements to key-value pair, then puts them into a new immutable map. Each element will be
     * mapped by {@code keyMapper} and {@code valueMapper} to product key and value. It is equivalent to:
     * <pre>
     *     for (T element : elements) {
     *         dest.put(keyMapper.apply(element), valueMapper.apply(element));
     *     }
     * </pre>
     * If given elements is empty, return an empty map.
     *
     * @param elements    given elements
     * @param keyMapper   key mapper
     * @param valueMapper value mapper
     * @param <T>         type of source element
     * @param <K>         type of keys of target map
     * @param <V>         type of values of target map
     * @return immutable map
     */
    @Immutable
    public static <T, K, V> Map<K, V> toPairs(
        @Nullable Iterable<? extends T> elements,
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends V> valueMapper
    ) {
        if (isEmpty(elements)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(putAll(new LinkedHashMap<>(), elements, keyMapper, valueMapper));
    }

    /**
     * Adds all given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <T>      type of element
     * @param <C>      type of dest collection
     * @return dest collection
     */
    public static <T, C extends Collection<? super T>> C addAll(C dest, T[] elements) {
        if (JieArray.isEmpty(elements)) {
            return dest;
        }
        dest.addAll(JieArray.asList(elements));
        return dest;
    }

    /**
     * Adds all given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <T>      type of element
     * @param <C>      type of dest collection
     * @return dest collection
     */
    public static <T, C extends Collection<? super T>> C addAll(C dest, Iterable<T> elements) {
        if (isEmpty(elements)) {
            return dest;
        }
        if (elements instanceof Collection) {
            dest.addAll((Collection<T>) elements);
        } else {
            for (T element : elements) {
                dest.add(element);
            }
        }
        return dest;
    }

    /**
     * Adds all given elements into dest collection, maps each element from type {@code T} to type {@code R} by
     * specified {@code mapper}, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @param <C>      type of dest collection
     * @return dest collection
     */
    public static <T, R, C extends Collection<? super R>> C addAll(
        C dest, T[] elements, Function<? super T, ? extends R> mapper) {
        if (JieArray.isEmpty(elements)) {
            return dest;
        }
        for (T element : elements) {
            dest.add(mapper.apply(element));
        }
        return dest;
    }

    /**
     * Adds all given elements into dest collection, maps each element from type {@code T} to type {@code R} by
     * specified {@code mapper}, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param mapper   specified mapper
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @param <C>      type of dest collection
     * @return dest collection
     */
    public static <T, R, C extends Collection<? super R>> C addAll(
        C dest, Iterable<T> elements, Function<? super T, ? extends R> mapper) {
        if (isEmpty(elements)) {
            return dest;
        }
        for (T element : elements) {
            dest.add(mapper.apply(element));
        }
        return dest;
    }

    /**
     * Adds all given elements into dest map, and returns the dest map.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on. If last key-{@code n}
     * is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <M>      type of dest map
     * @param <T>      type of element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T> M putAll(M dest, T[] elements) {
        if (JieArray.isEmpty(elements)) {
            return dest;
        }
        for (int i = 0; i < elements.length; i += 2) {
            if (i + 1 >= elements.length) {
                break;
            }
            Object key = elements[i];
            Object value = elements[i + 1];
            dest.put(Jie.as(key), Jie.as(value));
        }
        return dest;
    }

    /**
     * Adds all given elements into dest map, and returns the dest map.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on. If last key-{@code n}
     * is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <M>      type of dest map
     * @param <T>      type of element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T> M putAll(M dest, Iterable<T> elements) {
        if (isEmpty(elements)) {
            return dest;
        }
        Iterator<? extends T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            K key = Jie.as(iterator.next());
            if (iterator.hasNext()) {
                V value = Jie.as(iterator.next());
                dest.put(key, value);
            } else {
                break;
            }
        }
        return dest;
    }

    /**
     * Adds all given elements into dest map, maps each element from type {@code T} to type {@code R} by specified
     * {@code mapper}, and returns the dest map.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on. If last key-{@code n}
     * is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param mapper   specified mapper
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <M>      type of dest map
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T, R> M putAll(
        M dest, T[] elements, Function<? super T, ? extends R> mapper) {
        if (JieArray.isEmpty(elements)) {
            return dest;
        }
        for (int i = 0; i < elements.length; i += 2) {
            if (i + 1 >= elements.length) {
                break;
            }
            Object key = mapper.apply(elements[i]);
            Object value = mapper.apply(elements[i + 1]);
            dest.put(Jie.as(key), Jie.as(value));
        }
        return dest;
    }

    /**
     * Adds all given elements into dest map, maps each element from type {@code T} to type {@code R} by specified
     * {@code mapper}, and returns the dest map.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on. If last key-{@code n}
     * is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param mapper   specified mapper
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <M>      type of dest map
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T, R> M putAll(
        M dest, Iterable<? extends T> elements, Function<? super T, ? extends R> mapper) {
        if (isEmpty(elements)) {
            return dest;
        }
        Iterator<? extends T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            K key = Jie.as(mapper.apply(iterator.next()));
            if (iterator.hasNext()) {
                V value = Jie.as(mapper.apply(iterator.next()));
                dest.put(key, value);
            } else {
                break;
            }
        }
        return dest;
    }

    /**
     * Adds all given elements into dest map, each entry of source map will be mapped from {@code K1} to {@code K2} and
     * {@code V1} and {@code V2} by {@code keyMapper} and {@code valueMapper}, and returns the dest map.
     *
     * @param dest        dest map
     * @param source      source map
     * @param keyMapper   key mapper
     * @param valueMapper value mapper
     * @param <K1>        type of source key
     * @param <V1>        type of source value
     * @param <K2>        type of target key
     * @param <V2>        type of target value
     * @return converted map
     */
    public static <K1, V1, K2, V2, M extends Map<K2, V2>> M putAll(
        M dest,
        Map<K1, V1> source,
        Function<? super K1, ? extends K2> keyMapper,
        Function<? super V1, ? extends V2> valueMapper
    ) {
        if (isEmpty(source)) {
            return dest;
        }
        source.forEach((k, v) -> {
            dest.put(keyMapper.apply(k), valueMapper.apply(v));
        });
        return dest;
    }

    /**
     * Adds all given elements into dest map, and returns the dest map. Each element will be mapped by {@code keyMapper}
     * and {@code valueMapper} to product key and value, then the key and value will be put into the dest map. It is
     * equivalent to:
     * <pre>
     *     for (T element : elements) {
     *         dest.put(keyMapper.apply(element), valueMapper.apply(element));
     *     }
     * </pre>
     *
     * @param dest        dest collection
     * @param elements    given elements
     * @param keyMapper   key mapper
     * @param valueMapper value mapper
     * @param <K>         type of keys
     * @param <V>         type of values
     * @param <M>         type of dest map
     * @param <T>         type of element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T> M putAll(
        M dest, T[] elements, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        if (JieArray.isEmpty(elements)) {
            return dest;
        }
        for (T element : elements) {
            dest.put(keyMapper.apply(element), valueMapper.apply(element));
        }
        return dest;
    }

    /**
     * Adds all given elements into dest map, and returns the dest map. Each element will be mapped by {@code keyMapper}
     * and {@code valueMapper} to product key and value, then the key and value will be put into the dest map. It is
     * equivalent to:
     * <pre>
     *     for (T element : elements) {
     *         dest.put(keyMapper.apply(element), valueMapper.apply(element));
     *     }
     * </pre>
     *
     * @param dest        dest collection
     * @param elements    given elements
     * @param keyMapper   key mapper
     * @param valueMapper value mapper
     * @param <K>         type of keys
     * @param <V>         type of values
     * @param <M>         type of dest map
     * @param <T>         type of element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T> M putAll(
        M dest, Iterable<T> elements, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        if (isEmpty(elements)) {
            return dest;
        }
        for (T element : elements) {
            dest.put(keyMapper.apply(element), valueMapper.apply(element));
        }
        return dest;
    }

    /**
     * If given iterable is a list, return itself, otherwise collects it into an immutable list and return.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return list
     */
    public static <T> List<T> orList(@Nullable Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }
        return toList(iterable);
    }

    /**
     * If given iterable is a set, return itself, otherwise collects it into an immutable set and return.
     *
     * @param iterable given elements
     * @param <T>      type of element
     * @return set
     */
    public static <T> Set<T> orSet(@Nullable Iterable<T> iterable) {
        if (iterable instanceof Set) {
            return (Set<T>) iterable;
        }
        return toSet(iterable);
    }

    /**
     * If given iterable is a collection, return itself, otherwise collects it into an immutable set and return.
     *
     * @param iterable given elements
     * @param <T>      type of element
     * @return collection
     */
    public static <T> Collection<T> orCollection(@Nullable Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            return (Collection<T>) iterable;
        }
        return toSet(iterable);
    }

    /**
     * Returns given enumeration as an {@link Iterable}.
     *
     * @param enumeration given enumeration
     * @param <T>         type of element
     * @return given enumeration as an {@link Iterable}
     */
    public static <T> Iterable<T> asIterable(@Nullable Enumeration<? extends T> enumeration) {
        if (enumeration == null) {
            return Collections.emptyList();
        }
        return () -> new Iterator<T>() {
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
     * Returns given iterable as an {@link Enumeration}.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return given iterable as an {@link Enumeration}
     */
    public static <T> Enumeration<T> asEnumeration(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Jie.as(EmptyEnumeration.INSTANCE);
        }
        return new Enumeration<T>() {

            private Iterator<? extends T> iterator = null;

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }

            private Iterator<? extends T> iterator() {
                if (iterator == null) {
                    iterator = iterable.iterator();
                }
                return iterator;
            }
        };
    }

    /**
     * Returns value from given iterable at specified index, if the value is null or failed to obtain, return default
     * value.
     *
     * @param iterable     given iterable
     * @param index        specified index
     * @param defaultValue default value
     * @param <T>          type of element
     * @return value or default value
     */
    public static <T> T get(@Nullable Iterable<? extends T> iterable, int index, @Nullable T defaultValue) {
        if (iterable == null || index < 0) {
            return defaultValue;
        }
        if (iterable instanceof List) {
            List<T> list = Jie.as(iterable);
            if (index < list.size()) {
                T result = list.get(index);
                if (result != null) {
                    return result;
                }
            }
            return defaultValue;
        }
        int i = 0;
        for (T t : iterable) {
            if (index == i) {
                if (t != null) {
                    return t;
                } else {
                    return defaultValue;
                }
            }
            i++;
        }
        return defaultValue;
    }

    /**
     * Returns value from given map at specified key, if the value is null or failed to obtain, return default value.
     *
     * @param map          given map
     * @param key          specified key
     * @param defaultValue default value
     * @param <K>          type of keys
     * @param <V>          type of values
     * @return value or default value
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key, V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        V v = map.get(key);
        return v == null ? defaultValue : v;
    }

    /**
     * Nested get value from given map with given key.
     * <p>
     * This method gets value of given key, then let the value as next key to find next value and keep looping. If last
     * value as key cannot find next value (return null by {@link Map#get(Object)}), the last value will be returned. If
     * given key cannot find at least one value, or a same value in the given stack (which will cause an infinite loop),
     * return null.
     *
     * @param map   given map
     * @param key   given key
     * @param stack stack to store the historical values
     * @param <T>   type of element
     * @return nested value from given map with given key
     */
    @Nullable
    public static <T> T getNested(Map<?, T> map, T key, Set<T> stack) {
        T cur = key;
        stack.add(cur);
        while (true) {
            T result = map.get(cur);
            if (result == null || stack.contains(result)) {
                break;
            }
            cur = result;
            stack.add(cur);
        }
        if (Objects.equals(key, cur)) {
            return null;
        }
        return cur;
    }

    /**
     * Returns a {@link Stream} from given elements, not parallel.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return a {@link Stream} from given elements, not parallel
     */
    public static <T> Stream<T> stream(T[] elements) {
        if (JieArray.isEmpty(elements)) {
            return Stream.empty();
        }
        return Arrays.stream(elements);
    }

    /**
     * Returns a {@link Stream} from given elements, not parallel.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return a {@link Stream} from given elements, not parallel
     */
    public static <T> Stream<T> stream(Iterable<T> elements) {
        if (isEmpty(elements)) {
            return Stream.empty();
        }
        if (elements instanceof Collection) {
            return ((Collection<T>) elements).stream();
        }
        return StreamSupport.stream(elements.spliterator(), false);
    }

    private static final class ImmutableList<T> extends AbstractList<T> implements RandomAccess, Serializable {

        private final Object[] array;

        private ImmutableList(Object[] array) {
            this.array = array;
        }

        @Override
        public T get(int index) {
            return Jie.as(array[index]);
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    // private static final class ImmutableSet<T> extends AbstractSet<T> implements Serializable {
    //
    //     private final Object[] array;
    //
    //     private ImmutableSet(Object[] array, boolean distinct) {
    //         if (distinct) {
    //             this.array = Arrays.stream(array).distinct().toArray();
    //         } else {
    //             this.array = array;
    //         }
    //     }
    //
    //     @Override
    //     public Iterator<T> iterator() {
    //         return new ImmutableIterator();
    //     }
    //
    //     @Override
    //     public int size() {
    //         return array.length;
    //     }
    //
    //     private final class ImmutableIterator implements Iterator<T> {
    //
    //         private int i = 0;
    //
    //         @Override
    //         public boolean hasNext() {
    //             return i < array.length;
    //         }
    //
    //         @Override
    //         public T next() {
    //             return Jie.as(array[i++]);
    //         }
    //     }
    // }

    // private static final class ImmutableMap<K, V> extends AbstractMap<K, V> implements Serializable {
    //
    //     private final Set<Entry<K, V>> entries;
    //
    //     private ImmutableMap(Object[] array) {
    //         if (array.length < 2) {
    //             entries = Collections.emptySet();
    //             return;
    //         }
    //         Map<K, V> map = addAll(new LinkedHashMap<>(), array);
    //         entries = new ImmutableSet<>(
    //             map.entrySet().stream().map(it -> new SimpleImmutableEntry<>(it.getKey(), it.getValue())).toArray(),
    //             false
    //         );
    //     }
    //
    //     @Override
    //     public Set<Entry<K, V>> entrySet() {
    //         return entries;
    //     }
    // }

    private static final class EmptyEnumeration implements Enumeration<Object> {

        private static final EmptyEnumeration INSTANCE = new EmptyEnumeration();

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            throw new NoSuchElementException();
        }
    }
}
