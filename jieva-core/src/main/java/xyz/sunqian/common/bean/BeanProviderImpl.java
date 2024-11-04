package xyz.sunqian.common.bean;

import xyz.sunqian.common.cache.Cache;

import java.lang.reflect.Type;

final class BeanProviderImpl implements BeanProvider {

    static BeanProviderImpl DEFAULT_PROVIDER = new BeanProviderImpl(BeanResolver.defaultResolver());

    private final Cache<Type, BeanInfo> cache = Cache.softCache();
    private final BeanResolver resolver;

    BeanProviderImpl(BeanResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public BeanInfo getBeanInfo(Type type) {
        return cache.compute(type, resolver::resolve);
    }
}
