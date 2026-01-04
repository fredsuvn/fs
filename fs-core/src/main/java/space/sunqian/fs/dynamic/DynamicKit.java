package space.sunqian.fs.dynamic;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.system.ResKit;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Utilities for dynamic runtime metaprogramming.
 *
 * @author sunqian
 */
public class DynamicKit {

    /**
     * Returns the bytecode of the specified class. The class file needs to be in the classpath.
     *
     * @param cls the class to get bytecode from
     * @return the bytecode of the specified class
     * @throws DynamicException if an I/O error occurs
     */
    public static byte @Nonnull [] bytecode(@Nonnull Class<?> cls) throws DynamicException {
        String classFilePath = cls.getName().replace('.', '/') + ".class";
        URL url = ResKit.findResource(classFilePath);
        if (url == null) {
            throw new DynamicException("Class file not found: " + classFilePath + ".");
        }
        return Fs.uncheck(() -> {
            InputStream stream = url.openStream();
            byte[] bytes = Objects.requireNonNull(IOKit.read(stream));
            stream.close();
            return bytes;
        }, DynamicException::new);
    }

    private DynamicKit() {
    }
}