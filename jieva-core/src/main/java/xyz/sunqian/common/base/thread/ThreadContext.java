package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public static <K, V> V get(K key) {
        return Jie.as(asMap().get(key));
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
    public static <K, V> V set(K key, V value) {
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
