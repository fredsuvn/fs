package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilities class to provide context info in current thread.
 *
 * @author fredsuvn
 */
public class JieContext {

    private static final ThreadLocal<Map<Object, Object>> localMap = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Returns the value which mapping the specified key in current thread context, or null if no mapping for the key.
     *
     * @param key specified key
     * @param <K> type of key
     * @param <V> type of value
     * @return the value which mapping the specified key in current thread context, or null if no mapping for the key
     */
    @Nullable
    public static <K, V> V get(K key) {
        return Jie.as(localMap.get().get(key));
    }

    /**
     * Sets the specified value with the specified key in current thread context. If the context previously contained a
     * mapping for the key, the old value is replaced by the specified value. The old value will be returned, or null if
     * no old mapping.
     *
     * @param key   specified key
     * @param value specified value
     * @param <K>   type of key
     * @param <V>   type of value
     * @return old value or null if no old mapping
     */
    public static <K, V> V set(K key, @Nullable V value) {
        return Jie.as(localMap.get().put(key, value));
    }

    /**
     * Returns content of current thread context as {@link Map}. The returned {@link Map} is mutable, any modifications
     * to this map directly affect the content.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return content of current thread context as {@link Map}
     */
    @Nullable
    public static <K, V> Map<K, V> get() {
        return Jie.as(localMap.get());
    }
}
