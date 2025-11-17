package internal.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This interface provides methods for asserting.
 *
 * @author sunqian
 */
public interface AssertTest {

    /**
     * Tests the specified method via reflection and {@link Assertions#assertThrows(Class, Executable)}. This method is
     * equivalent to:
     * <pre>{@code
     * method.setAccessible(true);
     * return assertThrows(exception, () -> {
     *     try {
     *         method.invoke(inst, args);
     *          throw new NoThrows("No throws for: " + method + ".");
     *     } catch (InvocationTargetException e) {
     *         throw e.getCause();
     *     }
     * });
     * }</pre>
     *
     * @param exception the exception to be expected
     * @param method    the method to be tested
     * @param inst      the instance for the method
     * @param args      the arguments for the method
     * @param <T>       the type of the expected exception
     * @return the exception thrown by the method
     */
    default <T extends Throwable> @Nonnull T invokeThrows(
        @Nonnull Class<T> exception,
        @Nonnull Method method,
        @Nullable Object inst,
        @Nullable Object @Nonnull ... args
    ) {
        method.setAccessible(true);
        return assertThrows(exception, () -> {
            try {
                method.invoke(inst, args);
                throw new NoThrows("No throws for: " + method + ".");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    /**
     * Tests the specified method via reflection and {@link Assertions#assertEquals(Object, Object)}. This method is
     * equivalent to:
     * <pre>{@code
     * method.setAccessible(true);
     * Object actual;
     * try {
     *     actual = method.invoke(inst, args);
     * } catch (Exception e) {
     *     throw new AssertionError(e);
     * }
     * assertEquals(expected, actual);
     * }</pre>
     *
     * @param expected the expected result
     * @param method   the method to be tested
     * @param inst     the instance for the method
     * @param args     the arguments for the method
     */
    default void invokeEquals(
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
        assertEquals(expected, actual);
    }

    /**
     * This exception is thrown when the specified run-body does not throw an exception.
     */
    final class NoThrows extends RuntimeException {

        private NoThrows(@Nonnull String message) {
            super(message);
        }
    }
}
