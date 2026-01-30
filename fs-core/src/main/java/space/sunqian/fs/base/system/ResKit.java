package space.sunqian.fs.base.system;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.CollectKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * Utilities for resource.
 *
 * @author sunqian
 */
public class ResKit {

    /**
     * Returns the resource url of the given resource path. This method searches the resource in the classpath, and
     * doesn't expect the given path to start with "/".
     *
     * @param path the given resource path
     * @return the resource url of the given resource path, or {@code null} if not found
     */
    public static @Nullable URL findResource(@Nonnull String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(path);
    }

    /**
     * Returns all resource urls of the given resource path in . This method searches the resource in all classpath and
     * libs, and doesn't expect the given path to start with "/".
     *
     * @param path the given resource path
     * @return all resource urls of the given resource path, may be empty if not found
     */
    public static @Nonnull Set<@Nonnull URL> findResources(@Nonnull String path) throws IORuntimeException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Enumeration<URL> urls = Fs.uncheck(() -> classLoader.getResources(path), IORuntimeException::new);
        return SetKit.toSet(() -> CollectKit.asIterator(urls));
    }

    /**
     * Returns the resource stream of the given resource path. This method searches the resource in the classpath, and
     * doesn't expect the given path to start with "/".
     *
     * @param path the given resource path
     * @return the resource stream of the given resource path, or {@code null} if not found
     */
    public static @Nullable InputStream findStream(@Nonnull String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    private ResKit() {
    }
}
