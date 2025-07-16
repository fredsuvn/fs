package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.ArrayKit;

import java.util.function.Predicate;

/**
 * Utilities for trace and throwable info.
 *
 * @author fredsuvn
 */
public class JieTrace {

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
}
