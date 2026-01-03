package space.sunqian.fs.dynamic;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.nio.ByteBuffer;

/**
 * Dynamic class loader provides methods for loading classes from the specified byte data:
 * <ul>
 *     <li>{@link #loadClass(String, byte[])}</li>
 *     <li>{@link #loadClass(String, ByteBuffer)}</li>
 * </ul>
 *
 * @author sunqian
 */
public class DynamicClassLoader extends ClassLoader {

    /**
     * Constructs a new {@link DynamicClassLoader} instance.
     */
    public DynamicClassLoader() {
        super();
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
     * @throws DynamicException if an exception occurs
     */
    public @Nonnull Class<?> loadClass(@Nullable String name, byte @Nonnull [] bytes) throws DynamicException {
        return Fs.uncheck(() -> {
            if (name != null) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
            }
            return defineClass(null, bytes, 0, bytes.length);
        }, DynamicException::new);
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
     * @throws DynamicException if an exception occurs
     */
    public @Nonnull Class<?> loadClass(@Nullable String name, @Nonnull ByteBuffer bytes) throws DynamicException {
        return Fs.uncheck(() -> {
            if (name != null) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
            }
            return defineClass(null, bytes, null);
        }, DynamicException::new);
    }
}
