package xyz.sunqian.common.base.res;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Kit;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.collect.SetKit;
import xyz.sunqian.common.io.IORuntimeException;

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
        Enumeration<URL> urls = Kit.uncheck(() -> classLoader.getResources(path), IORuntimeException::new);
        return SetKit.toSet(() -> CollectKit.asIterator(urls));
    }
}
