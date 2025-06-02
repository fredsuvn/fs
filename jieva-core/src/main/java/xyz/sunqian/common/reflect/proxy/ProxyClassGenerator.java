package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;

/**
 * This interface is the generator for generating proxy class.
 *
 * @author sunqian
 */
public interface ProxyClassGenerator {

    /**
     * Generates the proxy class of the specified proxied class and interfaces, with the specified proxy method
     * handler.
     * <p>
     * For the proxied class and interfaces, it needs to have at least one proxied class or interface, and at most one
     * class and multiple interfaces, and the class (if present) must at the first.
     *
     * @param proxied       the specified proxied class and interfaces
     * @param methodHandler the specified proxy method handler
     * @return the proxy class
     * @throws ProxyException if any problem occurs during the generating
     */
    @Nonnull
    ProxyClass generate(
        @Nonnull Class<?> @Nonnull [] proxied,
        @Nonnull ProxyMethodHandler methodHandler
    ) throws ProxyException;
}
