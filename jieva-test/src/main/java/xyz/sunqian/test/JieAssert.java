package xyz.sunqian.test;

import org.testng.Assert;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

/**
 * Assert utilities for tests.
 *
 * @author sunqian
 */
public class JieAssert {

    /**
     * Tests the specified method via reflection and {@link Assert#expectThrows(Class, Assert.ThrowingRunnable)}. This
     * method is equivalent to:
     * <pre>{@code
     *     method.setAccessible(true);
     *     return expectThrows(exception, () -> {
     *         try {
     *             method.invoke(inst, args);
     *         } catch (InvocationTargetException e) {
     *             throw e.getCause();
     *         }
     *     });
     * }</pre>
     *
     * @param exception the exception to be expected
     * @param method    the method to be tested
     * @param inst      the instance for the method
     * @param args      the arguments for the method
     * @param <T>       the type of the expected exception
     */
    public static <T extends Throwable> @Nonnull T invokeThrows(
        @Nonnull Class<T> exception,
        @Nonnull Method method,
        @Nonnull Object inst,
        @Nullable Object @Nonnull ... args
    ) {
        method.setAccessible(true);
        return expectThrows(exception, () -> {
            try {
                method.invoke(inst, args);
                throw new NoThrows("No throws for: " + method + ".");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    /**
     * Tests the specified method via reflection and {@link Assert#assertEquals(Object, Object)}. This method is
     * equivalent to:
     * <pre>{@code
     *     method.setAccessible(true);
     *     Object actual;
     *     try {
     *         actual = method.invoke(inst, args);
     *     } catch (Exception e) {
     *         throw new AssertionError(e);
     *     }
     *     assertEquals(expected, actual);
     * }</pre>
     *
     * @param expected the expected result
     * @param method   the method to be tested
     * @param inst     the instance for the method
     * @param args     the arguments for the method
     */
    public static void invokeEquals(
        @Nonnull Object expected,
        @Nonnull Method method,
        @Nonnull Object inst,
        @Nullable Object @Nonnull ... args
    ) {
        method.setAccessible(true);
        Object actual;
        try {
            actual = method.invoke(inst, args);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        assertEquals(actual, expected);
    }

    public static final class NoThrows extends RuntimeException {

        private NoThrows(@Nonnull String message) {
            super(message);
        }
    }
}
