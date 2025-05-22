package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.JieArray;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.BiPredicate;
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
        if (JieArray.isEmpty(stackTraces)) {
            return null;
        }
        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement stackTraceElement = stackTraces[i];
            if (predicate.test(stackTraceElement)) {
                int targetIndex = i + 1 + offset;
                if (JieCheck.isInBounds(targetIndex, 0, stackTraces.length)) {
                    return stackTraces[targetIndex];
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Returns stack trace info of given throwable as string.
     *
     * @param throwable given throwable
     * @return stack trace info as string
     */
    public static String toString(Throwable throwable) {
        return toString(throwable, null);
    }

    /**
     * Returns stack trace info of given throwable as string, the line separator of return string will be replaced by
     * given line separator if given separator is not null.
     *
     * @param throwable     given throwable
     * @param lineSeparator given separator
     * @return stack trace info as string
     */
    public static String toString(Throwable throwable, @Nullable String lineSeparator) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        String stackTrace = sw.toString();
        if (lineSeparator == null) {
            return stackTrace;
        }
        String sysLineSeparator = System.lineSeparator();
        return stackTrace.replaceAll(sysLineSeparator, lineSeparator);
    }

    /**
     * Returns last stack trace info, of which class name has specified prefix, from given throwable. It is equivalent
     * to:
     * <pre>
     *     return getLastTrace(throwable, (e, t) -> t.getClassName().startsWith(classNamePrefix));
     * </pre>
     * This method is typically used in scenarios where it is necessary to obtain the
     * {@code effective root trace info}.
     *
     * @param throwable       given throwable
     * @param classNamePrefix specified prefix
     * @return last stack trace info
     */
    public static StackTraceElement getLastTrace(Throwable throwable, String classNamePrefix) {
        return getLastTrace(throwable, (e, t) -> t.getClassName().startsWith(classNamePrefix));
    }

    /**
     * Returns last stack trace info which passes specified predicate from given throwable.
     * <p>
     * This method will get all stack trace elements by {@link Throwable#getStackTrace()} to test with specified
     * predicate for each element in reverse order of elements array. If an element passes the predicate, it will be
     * assigned to the variable - {@code last}, and the test will be break to end. If a test is end, then this method
     * call {@link Throwable#getCause()} to get next cause throwable object, and a new test will start.
     * <p>
     * This test loop ends until a {@code null} was returned by {@link Throwable#getCause()} and the {@code last} will
     * be returned.
     * <p>
     * This method is typically used in scenarios where it is necessary to obtain the
     * {@code effective root trace info}.
     *
     * @param throwable given throwable
     * @param predicate specified predicate
     * @return last stack trace info which passes specified predicate
     */
    public static StackTraceElement getLastTrace(
        Throwable throwable, BiPredicate<Throwable, StackTraceElement> predicate) {
        Throwable cur = throwable;
        StackTraceElement last = null;
        do {
            StackTraceElement[] traceElements = cur.getStackTrace();
            if (JieArray.isNotEmpty(traceElements)) {
                for (int i = traceElements.length - 1; i >= 0; i--) {
                    if (predicate.test(cur, traceElements[i])) {
                        last = traceElements[i];
                        break;
                    }
                }
            }
        } while ((cur = cur.getCause()) != null);
        return last;
    }
}
