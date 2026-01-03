package space.sunqian.fs.dynamic;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities for dynamic runtime metaprogramming.
 *
 * @author sunqian
 */
public class DynamicKit {

    /**
     * Returns the bytecode of the specified class.
     *
     * @param cls the class to get bytecode from
     * @return the bytecode of the specified class
     * @throws DynamicException if an I/O error occurs
     */
    public static byte @Nonnull [] bytecode(@Nonnull Class<?> cls) throws DynamicException {
        String classFilePath = cls.getName().replace('.', '/') + ".class";
        URL url = ClassLoader.getSystemResource(classFilePath);
        if (url == null) {
            throw new DynamicException("URL not found for class: " + cls.getName());
        }
        return Fs.uncheck(() -> {
            Path path = Paths.get(url.toURI());
            return Files.readAllBytes(path);
        }, DynamicException::new);
    }

    private DynamicKit() {
    }
}