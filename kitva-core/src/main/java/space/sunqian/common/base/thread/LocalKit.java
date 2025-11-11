package space.sunqian.common.base.thread;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.Kit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilities for thread-local. This class supports get and/or set values in the local thread by {@link #get(Object)},
 * {@link #get(Object, Function)} and {@link #set(Object, Object)}.
 *
 * @author sunqian
 */
public class LocalKit {

    private static final ThreadLocal<Map<Object, Object>> CONTEXT = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Returns the value to which the specified key is mapped int the local thread. or {@code null} if the local thread
     * contains no mapping for the key.
     *
     * @param key the specified key
     * @param <K> the key type
     * @param <V> the value type
     * @return the value to which the specified key is mapped int the local thread, or {@code null} if the local thread
     * contains no mapping for the key
     */
    public static <K, V> V get(@Nonnull K key) {
        return Kit.as(contextMap().get(key));
    }

    /**
     * Returns the value to which the specified key is mapped int the local thread. If the specified key is not already
     * associated with a value (or is mapped to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}. If the function returns {@code null} no mapping is
     * recorded. The behavior of this method is equivalent to: {@code asMap().computeIfAbsent(key, func)}.
     *
     * @param key  the specified key
     * @param func the given mapping function
     * @param <K>  the key type
     * @param <V>  the value type
     * @return the value to which the specified key is mapped int the local thread, or a new value computed by the given
     * mapping function if the local thread contains no mapping for the key
     * @see Map#computeIfAbsent(Object, Function)
     */
    public static <K, V> V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends V> func) {
        Map<K, V> map = Kit.as(contextMap());
        return map.computeIfAbsent(key, func);
    }

    /**
     * Sets the specified value with the specified key int the local thread. If a mapping already exists for the key,
     * the old value is replaced by the specified value and the old value will be returned.
     *
     * @param key   the specified key
     * @param value the specified value
     * @param <K>   the key type
     * @param <V>   the value type
     * @return the old value or {@code null} if no old mapping
     */
    public static <K, V> V set(@Nonnull K key, V value) {
        return Kit.as(contextMap().put(key, value));
    }

    /**
     * Removes the mapping for the specified key from this local thread if present, returns the old value or
     * {@code null} if no old mapping.
     *
     * @param key the specified key
     * @param <K> the key type
     * @param <V> the value type
     * @return the old value or {@code null} if no old mapping
     */
    public static <K, V> V remove(@Nonnull K key) {
        return Kit.as(contextMap().remove(key));
    }

    /**
     * Removes all entries from the local thread.
     */
    public static void clear() {
        contextMap().clear();
    }

    /**
     * Returns all entries in the local thread as a {@link Map}. The returned {@link Map} is mutable, any changes to the
     * {@link Map} will reflect to the entries of the local thread.
     *
     * @return all entries in the local thread as a {@link Map}
     */
    public static @Nonnull Map<Object, Object> contextMap() {
        return CONTEXT.get();
    }

    private LocalKit() {
    }
}
