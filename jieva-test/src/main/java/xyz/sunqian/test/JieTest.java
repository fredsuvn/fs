package xyz.sunqian.test;

import org.testng.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

/**
 * Test utilities.
 *
 * @author fredsuvn
 */
public class JieTest {

    /**
     * Tests specified method via reflection and {@link Assert#expectThrows(Class, Assert.ThrowingRunnable)}. This
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
     * @param method    specified method
     * @param inst      instance for the method invoked
     * @param args      arguments of invoking
     * @param <T>       type of exception to be expected
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
     * Tests specified method via reflection and {@link Assert#assertEquals(Object, Object)}. This method is equivalent
     * to:
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
     * @param method specified method
     * @param inst   instance for the method invoked
     * @param args   arguments of invoking
     */
    public static void reflectEquals(Method method, Object expected, Object inst, Object... args) {
        method.setAccessible(true);
        Object actual;
        try {
            actual = method.invoke(inst, args);
        } catch (Exception e) {
            throw new JieTestException(e);
        }
        assertEquals(expected, actual);
    }

    /**
     * Creates a new file with specified path and data.
     *
     * @param path specified path
     * @param data specified data
     */
    public static void createFile(Path path, byte[] data) {
        try {
            File file = path.toFile();
            if (file.createNewFile()) {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data);
                outputStream.close();
            } else {
                throw new IOException("File is existed.");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
