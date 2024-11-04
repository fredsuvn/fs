package xyz.sunqian.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.testng.Assert.expectThrows;

/**
 * Test utilities.
 *
 * @author fredsuvn
 */
public class JieTest {

    /**
     * Tests specified method, expect to throw an exception.
     *
     * @param exception the exception to be expected
     * @param method    specified method
     * @param inst      instance for the method invoked
     * @param args      arguments of invoking
     * @param <T>       type of exception to be expected
     */
    public static <T extends Throwable> T testThrow(
        Class<T> exception, Method method, Object inst, Object... args) {
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
