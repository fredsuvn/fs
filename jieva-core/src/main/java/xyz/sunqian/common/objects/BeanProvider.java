package xyz.sunqian.common.objects;

import java.lang.reflect.Type;

/**
 * This interface provides a standard way to get {@link ObjectDef}.
 *
 * @author sunqian
 * @see ObjectDef
 * @see ObjectIntrospector
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
    static BeanProvider withResolver(ObjectIntrospector resolver) {
        return new BeanProviderImpl(resolver);
    }

    /**
     * Returns {@link ObjectDef} of given type.
     *
     * @param type given type
     * @return {@link ObjectDef} of given type
     */
    ObjectDef getBeanInfo(Type type);
}
