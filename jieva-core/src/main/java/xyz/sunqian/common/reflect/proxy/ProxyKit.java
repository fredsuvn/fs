package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.util.List;

/**
 * Static utility class for proxy.
 *
 * @author sunqian
 */
public class ProxyKit {

    /**
     * Generates the proxy class for the specified proxied class and interfaces, with the specified proxy method
     * handler. Note that if there are methods with the same name and parameter types, it may cause generation errors.
     * <p>
     * This method invokes the actual underlying implementation of
     * {@link ProxyClassGenerator#generate(Class, List, ProxyMethodHandler)} via the parameter {@code mode}, it is
     * equivalent to:
     * <pre>{@code
     * return mode.newGenerator().generate(proxiedClass, interfaces, methodHandler);
     * }</pre>
     *
     * @param proxiedClass  the specified class to be proxied, may be {@code null} if it is {@link Object}
     * @param interfaces    the interfaces to be proxied, may be empty
     * @param methodHandler the specified proxy method handler
     * @param mode          to specifies the actual underlying implementation of the generation
     * @return the proxy class
     * @throws ProxyException if any problem occurs during the generating
     */
    public static @Nonnull ProxyClass proxy(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<Class<?>> interfaces,
        @Nonnull ProxyMethodHandler methodHandler,
        @Nonnull ProxyMode mode
    ) throws ProxyException {
        return mode.newGenerator().generate(proxiedClass, interfaces, methodHandler);
    }
}
