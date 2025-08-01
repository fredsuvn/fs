package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.system.JvmException;

import java.nio.ByteBuffer;

/**
 * A class loader provides methods for loading classes from the specified byte data:
 * <ul>
 *     <li>{@link #loadClass(String, byte[])}</li>
 *     <li>{@link #loadClass(String, ByteBuffer)}</li>
 * </ul>
 *
 * @author sunqian
 */
public class BytesClassLoader extends ClassLoader {

    /**
     * Loads and returns a class from the specified byte data.
     * <p>
     * A class should only be loaded once in a loader, so if the class has been loaded before, it will be returned
     * directly, or an error thrown if the class name is not specified.
     *
     * @param name  the expected name of the class to load, or null if not known
     * @param bytes the specified byte data
     * @return the {@link Class} instance loaded from the specified byte data
     * @throws JvmException if an exception occurs
     */
    public @Nonnull Class<?> loadClass(@Nullable String name, @Nonnull byte @Nonnull [] bytes) throws JvmException {
        return Jie.uncheck(() -> {
            if (name != null) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
            }
            return defineClass(null, bytes, 0, bytes.length);
        }, JvmException::new);
    }

    /**
     * Loads and returns a class from the specified byte data.
     * <p>
     * A class should only be loaded once in a loader, so if the class has been loaded before, it will be returned
     * directly, or an error thrown if the class name is not specified.
     *
     * @param name  the expected name of the class to load, or null if not known
     * @param bytes the specified byte data
     * @return the {@link Class} instance loaded from the specified byte data
     * @throws JvmException if an exception occurs
     */
    public @Nonnull Class<?> loadClass(@Nullable String name, @Nonnull ByteBuffer bytes) throws JvmException {
        return Jie.uncheck(() -> {
            if (name != null) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
            }
            return defineClass(null, bytes, null);
        }, JvmException::new);
    }
}
