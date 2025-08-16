package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;

import java.util.List;

/**
 * This interface is used to create proxy class for the specified proxied class and interfaces. The implementations
 * should be thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ProxyMaker {

    /**
     * Returns an instance of {@link AsmProxyMaker} which implements the {@link ProxyMaker}.
     *
     * @return an instance of {@link AsmProxyMaker} which implements the {@link ProxyMaker}
     */
    static @Nonnull AsmProxyMaker byAsm() {
        return new AsmProxyMaker();
    }

    /**
     * Returns an instance of {@link JdkProxyMaker} which implements the {@link ProxyMaker}.
     *
     * @return an instance of {@link JdkProxyMaker} which implements the {@link ProxyMaker}
     */
    static @Nonnull JdkProxyMaker byJdk() {
        return new JdkProxyMaker();
    }

    /**
     * Creates a new {@link ProxyFactory} instance for the specified proxied class and interfaces, with the specified
     * proxy handler.
     *
     * @param proxiedClass the specified class to be proxied, may be {@code null} if it is {@link Object}
     * @param interfaces   the interfaces to be proxied, may be empty
     * @param proxyHandler the specified proxy handler
     * @return a new {@link ProxyFactory} instance to create proxy instances
     * @throws ProxyException if any problem occurs during creation
     */
    @Nonnull
    ProxyFactory make(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<@Nonnull Class<?>> interfaces,
        @Nonnull ProxyHandler proxyHandler
    ) throws ProxyException;
}
