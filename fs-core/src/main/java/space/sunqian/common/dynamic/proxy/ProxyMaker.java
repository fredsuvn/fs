package space.sunqian.common.dynamic.proxy;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.annotations.ThreadSafe;
import space.sunqian.common.dynamic.proxy.asm.AsmProxyMaker;
import space.sunqian.common.dynamic.proxy.jdk.JdkProxyMaker;

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
     * Creates a new {@link ProxySpec} instance for the specified proxied class and interfaces, with the specified proxy
     * handler.
     *
     * @param proxiedClass the specified class to be proxied, may be {@code null} if it is {@link Object}
     * @param interfaces   the interfaces to be proxied, may be empty (note it is annotated by {@link RetainedParam})
     * @param proxyHandler the specified proxy handler
     * @return a new {@link ProxySpec} instance to create proxy instances
     * @throws ProxyException if any problem occurs during creation
     */
    @Nonnull
    ProxySpec make(
        @Nullable Class<?> proxiedClass,
        @Nonnull @RetainedParam List<@Nonnull Class<?>> interfaces,
        @Nonnull ProxyHandler proxyHandler
    ) throws ProxyException;
}
