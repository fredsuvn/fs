package xyz.sunqian.test;

import org.testng.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

/**
 * Test utilities.
 *
 * @author sunqian
 */
public class JieTest {

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
     * @param method    the specified method
     * @param inst      the instance of the method
     * @param args      the invoking arguments
     * @param <T>       type of the expected exception
     */
    public static <T extends Throwable> T reflectThrows(Class<T> exception, Method method, Object inst, Object... args) {
        method.setAccessible(true);
        return expectThrows(exception, () -> {
            try {
                method.invoke(inst, args);
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
     *         throw new JieTestException(e);
     *     }
     *         assertEquals(expected, actual);
     * }</pre>
     *
     * @param method the specified method
     * @param inst   the instance of the method
     * @param args   the invoking arguments
     */
    public static void reflectEquals(Method method, Object expected, Object inst, Object... args) {
        method.setAccessible(true);
        Object actual;
        try {
            actual = method.invoke(inst, args);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        assertEquals(expected, actual);
    }
}
