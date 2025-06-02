package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;

/**
 * This interface represents the generated proxy class.
 *
 * @author sunqian
 */
public interface ProxyClass {

    /**
     * Creates and returns a new proxy instance.
     *
     * @param <T> the type of the instance
     * @return a new proxy instance
     * @throws ProxyException if any problem occurs
     */
    <T> @Nonnull T newInstance() throws ProxyException;

    /**
     * Returns the generated proxy class.
     *
     * @return actual generated proxy class
     */
    @Nonnull
    Class<?> getProxyClass();
}
