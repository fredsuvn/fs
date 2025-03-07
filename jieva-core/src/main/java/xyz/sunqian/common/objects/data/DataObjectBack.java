package xyz.sunqian.common.objects.data;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.cache.Cache;

import java.lang.reflect.Type;
import java.util.Map;

final class DataObjectBack {

    private static final Cache<Type, Object> CACHE = Cache.softCache();

    static DataSchema getDataSchema(Type type, @Nullable DataSchemaParser parser) {
        DataSchemaParser p = parser == null ? DataSchemaParser.defaultParser() : parser;
        Object ret = CACHE.compute(type, p::parse);
        if (ret instanceof DataSchema) {
            return (DataSchema) ret;
        }
        Map<DataSchemaParser, DataSchema> map = Jie.as(ret);
        return map.computeIfAbsent(p, i -> i.parse(type));
    }
}
