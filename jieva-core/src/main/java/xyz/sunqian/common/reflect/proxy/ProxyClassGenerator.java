package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;

import java.util.List;

/**
 * This interface is the generator for generating proxy class, all builtin implementations can be found in
 * {@link ProxyMode}. The implementations should be thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ProxyClassGenerator {

    /**
     * Generates the proxy class for the specified proxied class and interfaces, with the specified proxy method
     * handler. Note that if there are methods with the same name and parameter types, it may cause generation errors.
     *
     * @param proxiedClass  the specified class to be proxied, may be {@code null} if it is {@link Object}
     * @param interfaces    the interfaces to be proxied, may be empty
     * @param methodHandler the specified proxy method handler
     * @return the proxy class
     * @throws ProxyException if any problem occurs during the generating
     */
    @Nonnull
    ProxyClass generate(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<@Nonnull Class<?>> interfaces,
        @Nonnull ProxyMethodHandler methodHandler
    ) throws ProxyException;
}
