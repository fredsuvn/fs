package space.sunqian.fs.base.thread;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to hold and access thread context, implemented by {@link ThreadLocal}. It supports get/set/clear
 * operations by {@link #get(Object)}, {@link #get(Object, Function)}, {@link #set(Object, Object)} and
 * {@link #clear()}.
 *
 * @author sunqian
 */
public class ThreadContext {

    private static final ThreadLocal<Map<Object, Object>> CONTEXT = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Returns the value to which the specified key is mapped int the current thread context. or {@code null} if the
     * current thread context contains no mapping for the key.
     *
     * @param key the specified key
     * @param <K> the key type
     * @param <V> the value type
     * @return the value to which the specified key is mapped int the current thread context, or {@code null} if the
     * current thread context contains no mapping for the key
     */
    public static <K, V> V get(@Nonnull K key) {
        return Fs.as(CONTEXT.get().get(key));
    }

    /**
     * Returns the value to which the specified key is mapped int the current thread context. If the specified key is
     * not already associated with a value (or is mapped to {@code null}), attempts to compute its value using the given
     * mapping function and enters it into this map unless {@code null}. If the function returns {@code null} no mapping
     * is recorded. The behavior of this method is equivalent to: {@code asMap().computeIfAbsent(key, func)}.
     *
     * @param key  the specified key
     * @param func the given mapping function
     * @param <K>  the key type
     * @param <V>  the value type
     * @return the value to which the specified key is mapped int the current thread context, or a new value computed by
     * the given mapping function if the current thread context contains no mapping for the key
     * @see Map#computeIfAbsent(Object, Function)
     */
    public static <K, V> V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends V> func) {
        Map<K, V> map = Fs.as(CONTEXT.get());
        return map.computeIfAbsent(key, func);
    }

    /**
     * Sets the specified value with the specified key int the current thread context. If a mapping already exists for
     * the key, the old value is replaced by the specified value and the old value will be returned.
     *
     * @param key   the specified key
     * @param value the specified value
     * @param <K>   the key type
     * @param <V>   the value type
     * @return the old value or {@code null} if no old mapping
     */
    public static <K, V> V set(@Nonnull K key, V value) {
        return Fs.as(CONTEXT.get().put(key, value));
    }

    /**
     * Removes the mapping for the specified key from this current thread context if present, returns the old value or
     * {@code null} if no old mapping.
     *
     * @param key the specified key
     * @param <K> the key type
     * @param <V> the value type
     * @return the old value or {@code null} if no old mapping
     */
    public static <K, V> V remove(@Nonnull K key) {
        return Fs.as(CONTEXT.get().remove(key));
    }

    /**
     * Removes all entries from the current thread context.
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Returns a copy of all entries in the current thread context as a {@link Map}. Any changes to the returned
     * {@link Map} can not reflect to the entries of the current thread context.
     *
     * @return a copy of all entries in the current thread context as a {@link Map}
     */
    public static @Nonnull Map<Object, Object> contextMap() {
        return new LinkedHashMap<>(CONTEXT.get());
    }

    private ThreadContext() {
    }
}
