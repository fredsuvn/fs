package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.cache.Cache;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

final class Impls {

    private static final Cache<Type, Map<ObjectIntrospector, ObjectDef>> CACHE = Cache.softCache();
    private static final ObjectIntrospector DEFAULT_INTRO = ObjectIntrospector.defaultResolver();

    static ObjectDef getObjectDef(Type type, @Nullable ObjectIntrospector introspector) {
        ObjectIntrospector intro = Jie.nonNull(introspector, DEFAULT_INTRO);
        Map<ObjectIntrospector, ObjectDef> ret = CACHE.compute(type, t -> new HashMap<>(1));
        return ret.computeIfAbsent(intro, i -> i.introspect(type));
    }
}
