package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Kit;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilities for tracing in thread. This class supports get and/or set values in the context of the current thread by
 * {@link #get(Object)}, {@link #get(Object, Function)} and {@link #set(Object, Object)}.
 *
 * @author sunqian
 */
public class TraceKit {

    private static final ThreadLocal<Map<Object, Object>> CONTEXT = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Returns the stack trace list of the current thread, starting at the method that invokes this method
     * ({@code TraceKit.stackTrace()}).
     * <p>
     * The list is a snapshot of the stack trace at the time of invocation. The first element at index {@code 0}
     * represents the <i>caller</i> of this method (the most recent method invocation). The last element of the list
     * represents the bottom of the stack, which is the least recent method invocation, typically is the {@code main()}
     * or {@link Thread#run()}.
     * <p>
     * The original stack trace info is come from {@link Thread#getStackTrace()}.
     *
     * @return the stack trace list of the current thread
     */
    public static @Nonnull List<@Nonnull StackTraceElement> stackTrace() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        return parseStackTrace(elements);
    }

    private static @Nonnull List<@Nonnull StackTraceElement> parseStackTrace(
        @Nonnull StackTraceElement @Nonnull [] elements
    ) {
        int preIndex = -1;
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            if (TraceKit.class.getName().equals(element.getClassName())
                && "stackTrace".equals(element.getMethodName())) {
                preIndex = i;
            }
        }
        if (preIndex < 0) {
            return Collections.emptyList();
        }
        StackTraceElement[] actualElements = new StackTraceElement[elements.length - preIndex - 1];
        System.arraycopy(elements, preIndex + 1, actualElements, 0, actualElements.length);
        return Arrays.asList(actualElements);
    }

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
        return Kit.as(contextMap().get(key));
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
        Map<K, V> map = Kit.as(contextMap());
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
        return Kit.as(contextMap().put(key, value));
    }

    /**
     * Returns content of the context in the current thread as a {@link Map}. The returned {@link Map} is mutable, any
     * changes to the {@link Map} will reflect to the context of the current thread.
     *
     * @return content of the context in the current thread as a {@link Map}
     */
    public static @Nonnull Map<Object, Object> contextMap() {
        return CONTEXT.get();
    }
}
