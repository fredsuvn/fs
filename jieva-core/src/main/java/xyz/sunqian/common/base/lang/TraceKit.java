package xyz.sunqian.common.base.lang;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ArrayKit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Utilities for trace and throwable info.
 *
 * @author fredsuvn
 */
public class TraceKit {

    private static final ThreadLocal<Map<Object, Object>> localMap = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Returns caller stack trace of given class name and method name, or null if failed.
     * <p>
     * This method searches the result of {@link Thread#getStackTrace()} of current thread, to find first
     * {@link StackTraceElement} which can pass the given predicate. Let the next found element be the {@code caller},
     * if given {@code offset} is 0, the {@code caller} will be returned. Otherwise, the element at index of
     * {@code (caller's index + offset)} will be returned.
     * <p>
     * If stack trace element is null or empty, or the final index is out of bound, return null.
     *
     * @param offset    given offset
     * @param predicate given predicate
     * @return caller stack trace
     */
    @Nullable
    public static StackTraceElement findCallerTrace(int offset, Predicate<StackTraceElement> predicate) {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (ArrayKit.isEmpty(stackTraces)) {
            return null;
        }
        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement stackTraceElement = stackTraces[i];
            if (predicate.test(stackTraceElement)) {
                int targetIndex = i + 1 + offset;
                if (CheckKit.isInBounds(targetIndex, 0, stackTraces.length)) {
                    return stackTraces[targetIndex];
                }
                return null;
            }
        }
        return null;
    }

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
