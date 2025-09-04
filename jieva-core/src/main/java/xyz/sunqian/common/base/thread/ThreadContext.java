package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents the context of the current thread, based on {@link ThreadLocal}.
 *
 * @author sunqian
 */
public class ThreadContext {

    private static final ThreadLocal<Map<Object, Object>> CONTEXT = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Returns the value to which the specified key is mapped in the context of the current thread. or {@code null} if
     * the context contains no mapping for the key.
     *
     * @param key the specified key
     * @param <K> the key type
     * @param <V> the value type
     * @return the value to which the specified key is mapped in the context of the current thread, or {@code null} if
     * the context contains no mapping for the key
     */
    public static <K, V> V get(@Nonnull K key) {
        return Jie.as(asMap().get(key));
    }

    /**
     * Returns the value to which the specified key is mapped in the context of the current thread. If the specified key
     * is not already associated with a value (or is mapped to {@code null}), attempts to compute its value using the
     * given mapping function and enters it into this map unless {@code null}. If the function returns {@code null} no
     * mapping is recorded. The behavior of this method is equivalent to: {@code asMap().computeIfAbsent(key, func)}.
     *
     * @param key  the specified key
     * @param func the given mapping function
     * @param <K>  the key type
     * @param <V>  the value type
     * @return the value to which the specified key is mapped in the context of the current thread, or a new value
     * computed by the given mapping function if the context contains no mapping for the key
     * @see Map#computeIfAbsent(Object, Function)
     */
    public static <K, V> V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends V> func) {
        Map<K, V> map = Jie.as(asMap());
        return map.computeIfAbsent(key, func);
    }

    /**
     * Sets the specified value with the specified key in the context of the current thread. If a mapping already exists
     * for the key, the old value is replaced by the specified value and the old value will be returned.
     *
     * @param key   the specified key
     * @param value the specified value
     * @param <K>   the key type
     * @param <V>   the value type
     * @return the old value or {@code null} if no old mapping
     */
    public static <K, V> V set(@Nonnull K key, V value) {
        return Jie.as(asMap().put(key, value));
    }

    /**
     * Returns the content of the current thread as a {@link Map}. The returned {@link Map} is mutable, any changes to
     * the {@link Map} will reflect to the context of the current thread.
     *
     * @return the content of the current thread as a {@link Map}
     */
    public static @Nonnull Map<Object, Object> asMap() {
        return CONTEXT.get();
    }
}
