package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;

import java.nio.ByteBuffer;

/**
 * A class loader provides methods for loading classes from the specified byte data.
 *
 * @author sunqian
 */
public class BytesClassLoader extends ClassLoader {

    /**
     * Loads and returns a class from the specified byte data.
     *
     * @param bytes the specified byte data
     * @return the {@link Class} instance loaded from the specified byte data
     * @throws JvmException if any problem occurs
     */
    public @Nonnull Class<?> load(@Nonnull byte @Nonnull [] bytes) throws JvmException {
        try {
            return defineClass(null, bytes, 0, bytes.length);
        } catch (ClassFormatError | Exception e) {
            throw new JvmException(e);
        }
    }

    /**
     * Loads and returns a class from the specified byte data.
     *
     * @param bytes the specified byte data
     * @return the {@link Class} instance loaded from the specified byte data
     * @throws JvmException if any problem occurs
     */
    public @Nonnull Class<?> load(@Nonnull ByteBuffer bytes) throws JvmException {
        try {
            return defineClass(null, bytes, null);
        } catch (ClassFormatError | Exception e) {
            throw new JvmException(e);
        }
    }
}
