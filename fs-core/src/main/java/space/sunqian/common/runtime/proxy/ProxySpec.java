package space.sunqian.common.runtime.proxy;

import space.sunqian.annotations.Nonnull;

import java.util.List;

/**
 * The specification of a proxy class, provides definition info of the proxy class and methods to create its instances.
 *
 * @author sunqian
 */
public interface ProxySpec {

    /**
     * Creates and returns a new instance of the proxy class.
     *
     * @param <T> the type of the instance
     * @return a new instance of the proxy class
     * @throws ProxyException if any problem occurs
     */
    <T> @Nonnull T newInstance() throws ProxyException;

    /**
     * Returns the proxy class defined by this specification.
     *
     * @return the proxy class defined by this specification
     */
    @Nonnull
    Class<?> proxyClass();

    /**
     * Returns the proxied class, which is typically the superclass of the {@link #proxyClass()}.
     *
     * @return the proxied class, which is typically the superclass of the {@link #proxyClass()}
     */
    @Nonnull
    Class<?> proxiedClass();

    /**
     * Returns the proxied interfaces, which are typically the interfaces implemented by {@link #proxyClass()}.
     *
     * @return the proxied interfaces, which are typically the interfaces implemented by {@link #proxyClass()}
     */
    @Nonnull
    List<@Nonnull Class<?>> proxiedInterfaces();

    /**
     * Returns the proxy handler, which is used to handle the proxy behavior. The handler typically is the handler
     * argument of {@link ProxyMaker#make(Class, List, ProxyHandler)}.
     *
     * @return the proxy handler, which is used to handle the proxy behavior
     */
    @Nonnull
    ProxyHandler proxyHandler();
}
