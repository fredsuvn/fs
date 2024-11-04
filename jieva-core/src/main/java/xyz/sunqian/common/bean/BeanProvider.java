package xyz.sunqian.common.bean;

import java.lang.reflect.Type;

/**
 * This interface provides a standard way to get {@link BeanInfo}.
 *
 * @author sunqian
 * @see BeanInfo
 * @see BeanResolver
 */
public interface BeanProvider {

    /**
     * Returns default bean provider. Note the default provider has a cache to cache resolved bean info.
     *
     * @return default bean provider
     */
    static BeanProvider defaultProvider() {
        return BeanProviderImpl.DEFAULT_PROVIDER;
    }

    /**
     * Returns a new bean provider with specified resolver.
     *
     * @return specified resolver
     */
    static BeanProvider withResolver(BeanResolver resolver) {
        return new BeanProviderImpl(resolver);
    }

    /**
     * Returns {@link BeanInfo} of given type.
     *
     * @param type given type
     * @return {@link BeanInfo} of given type
     */
    BeanInfo getBeanInfo(Type type);
}
