package space.sunqian.common.collect;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.OutParam;
import space.sunqian.common.base.Kit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities for {@link Map}.
 *
 * @author sunqian
 */
public class MapKit {

    /**
     * Returns whether the given map is null or empty.
     *
     * @param map the given map
     * @return whether the given map is null or empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

    /**
     * Returns whether the given map is not null and empty.
     *
     * @param map the given map
     * @return whether the given map is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

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
    public static <K, V> @Nonnull @Immutable Map<K, V> map(Object @Nonnull ... array) {
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
    public static <K, V> @Nonnull HashMap<K, V> hashMap(Object @Nonnull ... array) {
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
    public static <K, V> @Nonnull LinkedHashMap<K, V> linkedHashMap(Object @Nonnull ... array) {
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
    public static <K, V> @Nonnull @Immutable Map<K, V> toMap(@Nonnull Iterable<?> it) {
        Object[] array = CollectKit.toArray(it);
        return map(array);
    }

    /**
     * Returns a new immutable map of which entries are put from the given map, and all key-value pairs are mapped from
     * the old type to the new type during the put operation. The behavior is equivalent to:
     * <pre>{@code
     * return Collections.unmodifiableMap(
     *     map.entrySet().stream().collect(Collectors.toMap(
     *         entry -> keyMapper.apply(entry.getKey()),
     *         entry -> valueMapper.apply(entry.getValue()),
     *         (v1, v2) -> v2,
     *         LinkedHashMap::new
     *     ))
     * );
     * }</pre>
     *
     * @param map   the given map
     * @param <KO>> the old key type
     * @param <VO>> the old value type
     * @param <KN>  the new key type
     * @param <VN>  the new value type
     * @return a new immutable map of which entries are put from the given map
     */
    public static <KO, VO, KN, VN> @Nonnull @Immutable Map<KN, VN> toMap(
        @Nonnull Map<KO, VO> map,
        @Nonnull Function<? super KO, ? extends KN> keyMapper,
        @Nonnull Function<? super VO, ? extends VN> valueMapper
    ) {
        return Collections.unmodifiableMap(
            map.entrySet().stream().collect(Collectors.toMap(
                entry -> keyMapper.apply(entry.getKey()),
                entry -> valueMapper.apply(entry.getValue()),
                (v1, v2) -> v2,
                LinkedHashMap::new
            ))
        );
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
    public static <K, V> @Nonnull HashMap<K, V> toHashMap(@Nonnull Iterable<?> it) {
        Object[] array = CollectKit.toArray(it);
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
    public static <K, V> @Nonnull LinkedHashMap<K, V> toLinkedHashMap(@Nonnull Iterable<?> it) {
        Object[] array = CollectKit.toArray(it);
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
    public static <K, V, M extends Map<K, V>> @Nonnull M putAll(
        @Nonnull @OutParam M map,
        Object @Nonnull ... array
    ) {
        int end = array.length / 2 * 2;
        int i = 0;
        while (i < end) {
            K key = Kit.as(array[i++]);
            V value = Kit.as(array[i++]);
            map.put(key, value);
        }
        if (end < array.length) {
            map.put(Kit.as(array[end]), null);
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
    public static <K, V, M extends Map<K, V>> @Nonnull M putAll(
        @Nonnull @OutParam M map,
        @Nonnull Iterable<?> it
    ) {
        Iterator<?> iterator = it.iterator();
        while (iterator.hasNext()) {
            K key = Kit.as(iterator.next());
            V value = iterator.hasNext() ? Kit.as(iterator.next()) : null;
            map.put(key, value);
        }
        return map;
    }

    /**
     * Resolves the final value in a chain. For example, a map contains: [{@code A -> B, B -> C, C -> D}],
     * {@code resolveChain(map, A, stack)} will return D.
     * <p>
     * This method gets the value with the given key by {@link Map#get(Object)}, and then uses that value as the next
     * key to get the next value. This loop continues until the get method returns null when a value is used as the key,
     * and that value will be returned. If the given key doesn't map a value, or the loop is infinite, it will return
     * null.
     *
     * @param map   the given map
     * @param key   the given key
     * @param stack the stack to check infinite loop
     * @param <T>   the value type
     * @return the final value in a chain
     */
    public static <T> @Nullable T resolveChain(@Nonnull Map<?, T> map, T key, @Nonnull Set<T> stack) {
        if (!map.containsKey(key)) {
            return null;
        }
        stack.add(key);
        T nextKey = map.get(key);
        while (true) {
            if (stack.contains(nextKey)) {
                return null;
            }
            if (!map.containsKey(nextKey)) {
                return nextKey;
            }
            stack.add(nextKey);
            nextKey = map.get(nextKey);
        }
    }

    /**
     * Returns a new immutable {@link Map.Entry} with the given key and value.
     *
     * @param key   the given key
     * @param value the given value
     * @param <K>   the key type
     * @param <V>   the value type
     * @return a new immutable {@link Map.Entry} with the given key and value
     */
    public static <K, V> Map.@Nonnull Entry<K, V> entry(K key, V value) {
        return new Map.Entry<K, V>() {
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
