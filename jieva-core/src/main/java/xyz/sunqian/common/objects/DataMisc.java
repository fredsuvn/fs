package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.cache.Cache;

import java.lang.reflect.Type;
import java.util.Map;

final class DataMisc {

    private static final Cache<Type, Object> CACHE = Cache.softCache();
    private static final DataSchemaParser DEFAULT_INTRO = DataSchemaParser.defaultParser();

    static DataSchema getDataSchema(Type type, @Nullable DataSchemaParser inspector) {
        DataSchemaParser ins = Jie.nonNull(inspector, DEFAULT_INTRO);
        Object ret = CACHE.compute(type, ins::parse);
        if (ret instanceof DataSchema) {
            return (DataSchema) ret;
        }
        Map<DataSchemaParser, DataSchema> map = Jie.as(ret);
        return map.computeIfAbsent(ins, i -> i.parse(type));
    }
}
