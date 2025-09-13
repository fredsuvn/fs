package xyz.sunqian.common.base.lang;

import xyz.sunqian.annotations.Nonnull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for tracing.
 *
 * @author sunqian
 */
public class TraceKit {

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
}
