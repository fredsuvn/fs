package xyz.sunqian.common.objects;

import xyz.sunqian.common.cache.Cache;

import java.lang.reflect.Type;

final class BeanProviderImpl implements BeanProvider {

    static BeanProviderImpl DEFAULT_PROVIDER = new BeanProviderImpl(ObjectIntrospector.defaultResolver());

    private final Cache<Type, ObjectDef> cache = Cache.softCache();
    private final ObjectIntrospector resolver;

    BeanProviderImpl(ObjectIntrospector resolver) {
        this.resolver = resolver;
    }

    @Override
    public ObjectDef getBeanInfo(Type type) {
        return cache.compute(type, resolver::introspect);
    }
}
