package xyz.sunqian.common.base.res;

import xyz.sunqian.common.base.Jie;
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
     * @return the resource url of the given resource path
     */
    public static URL findResource(String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(path);
    }

    /**
     * Returns all resource urls of the given resource path in . This method searches the resource in all classpath and
     * libs, and doesn't expect the given path to start with "/".
     *
     * @param path the given resource path
     * @return all resource urls of the given resource path
     */
    public static Set<URL> findAllRes(String path) throws IORuntimeException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Enumeration<URL> urls = Jie.uncheck(() -> classLoader.getResources(path), IORuntimeException::new);
        return SetKit.toSet(() -> CollectKit.asIterator(urls));
    }
}
