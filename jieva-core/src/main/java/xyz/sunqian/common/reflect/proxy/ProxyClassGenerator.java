package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;

/**
 * This interface is the generator for generating proxy class.
 *
 * @author sunqian
 */
public interface ProxyClassGenerator {

    /**
     * Generates the proxy class with the specified proxy method handler.
     *
     * @param proxied       The class and interface to be proxied. Supports at most one class and multiple interfaces,
     *                      and the class (if present) must be the first.
     * @param methodHandler the specified proxy method handler
     * @return the proxy class
     * @throws ProxyException if a problem occurs during the generating
     */
    @Nonnull
    ProxyClass generate(
        @Nonnull Class<?> @Nonnull [] proxied,
        @Nonnull ProxyMethodHandler methodHandler
    ) throws ProxyException;
}
