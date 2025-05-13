package xyz.sunqian.common.collection;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.common.base.Jie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static utility class for {@link Map}.
 *
 * @author sunqian
 */
public class JieMap {

    /**
     * Returns a new immutable map of which content is added from the given array.
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     * <p>
     * The behavior of this method is equivalent to:
     * <pre>{@code
     *  return Collections.unmodifiableMap(linkedHashMap(array));
     *  }</pre>
     *
     * @param array the given array
     * @param <K>>  the key type
     * @param <V>>  the value type
     * @return a new {@link HashMap} initialing with the given array
     */
    public static <K, V> @Immutable Map<K, V> map(Object... array) {
        return Collections.unmodifiableMap(linkedHashMap(array));
    }

    /**
     * Returns a new {@link HashMap} initialing with the given array.
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     *
     * @param array the given array
     * @param <K>>  the key type
     * @param <V>>  the value type
     * @return a new {@link HashMap} initialing with the given array
     */
    public static <K, V> HashMap<K, V> hashMap(Object... array) {
        return putAll(new HashMap<>(), array);
    }

    /**
     * Returns a new {@link LinkedHashMap} initialing with the given array
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     *
     * @param array the given array
     * @param <K>>  the key type
     * @param <V>>  the value type
     * @return a new {@link HashMap} initialing with the given array
     */
    public static <K, V> LinkedHashMap<K, V> linkedHashMap(Object... array) {
        return putAll(new LinkedHashMap<>(), array);
    }

    /**
     * Returns a new immutable map of which content is added from the given iterable.
     * <p>
     * Every two elements of the iterable form a key-value pair, that means, the {@code it[0]} and {@code it[1]} will be
     * the first key-value pair, the {@code it[2]} and {@code it[3]} will be the second key-value pair, and so on. If
     * the length of the iterable is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put. This is the iterable version of {@link #map(Object...)}.
     *
     * @param it   the given iterable
     * @param <K>> the key type
     * @param <V>> the value type
     * @return a new {@link HashMap} initialing with the given iterable
     */
    public static <K, V> @Immutable Map<K, V> toMap(Iterable<?> it) {
        Object[] array = JieCollection.toArray(it);
        return map(array);
    }

    /**
     * Returns a new {@link HashMap} initialing with the given iterable.
     * <p>
     * Every two elements of the iterable form a key-value pair, that means, the {@code it[0]} and {@code it[1]} will be
     * the first key-value pair, the {@code it[2]} and {@code it[3]} will be the second key-value pair, and so on. If
     * the length of the iterable is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     *
     * @param it   the given iterable
     * @param <K>> the key type
     * @param <V>> the value type
     * @return a new {@link HashMap} initialing with the given iterable
     */
    public static <K, V> HashMap<K, V> toHashMap(Iterable<?> it) {
        Object[] array = JieCollection.toArray(it);
        return hashMap(array);
    }

    /**
     * Returns a new {@link LinkedHashMap} initialing with the given iterable.
     * <p>
     * Every two elements of the iterable form a key-value pair, that means, the {@code it[0]} and {@code it[1]} will be
     * the first key-value pair, the {@code it[2]} and {@code it[3]} will be the second key-value pair, and so on. If
     * the length of the iterable is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     *
     * @param it   the given iterable
     * @param <K>> the key type
     * @param <V>> the value type
     * @return a new {@link HashMap} initialing with the given iterable
     */
    public static <K, V> LinkedHashMap<K, V> toLinkedHashMap(Iterable<?> it) {
        Object[] array = JieCollection.toArray(it);
        return linkedHashMap(array);
    }

    /**
     * Puts all elements from the given array into the given map and returns the given map.
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     *
     * @param map   the given map
     * @param array the given array
     * @param <K>   the key type
     * @param <V>   the value type
     * @param <M>   the type of the given map
     * @return the given map
     */
    public static <K, V, M extends Map<K, V>> M putAll(M map, Object... array) {
        int end = array.length / 2 * 2;
        int i = 0;
        while (i < end) {
            K key = Jie.as(array[i++]);
            V value = Jie.as(array[i++]);
            map.put(key, value);
        }
        if (end < array.length) {
            map.put(Jie.as(array[end]), null);
        }
        return map;
    }

    /**
     * Puts all elements from the given iterable into the given map and returns the given map.
     * <p>
     * Every two elements of the iterable form a key-value pair, that means, the {@code it[0]} and {@code it[1]} will be
     * the first key-value pair, the {@code it[2]} and {@code it[3]} will be the second key-value pair, and so on. If
     * the length of the iterable is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     *
     * @param map the given map
     * @param it  the given iterable
     * @param <K> the key type
     * @param <V> the value type
     * @param <M> the type of the given map
     * @return the given map
     */
    public static <K, V, M extends Map<K, V>> M putAll(M map, Iterable<?> it) {
        Iterator<?> iterator = it.iterator();
        while (iterator.hasNext()) {
            K key = Jie.as(iterator.next());
            V value = iterator.hasNext() ? Jie.as(iterator.next()) : null;
            map.put(key, value);
        }
        return map;
    }
}
