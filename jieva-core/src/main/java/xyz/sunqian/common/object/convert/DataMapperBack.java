package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.data.DataSchema;
import xyz.sunqian.common.runtime.reflect.TypeKit;
import xyz.sunqian.common.runtime.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class DataMapperBack {

    private final @Nonnull Map<Type, DataSchema> schemaCache = new ConcurrentHashMap<>();

    static void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConversionException {
        // try {
        //     if (source instanceof Map) {
        //         if (dest instanceof Map) {
        //             mapToMap(source, sourceType, dest, destType, options);
        //         } else {
        //             mapToBean(source, sourceType, dest, destType, options);
        //         }
        //     } else {
        //         if (dest instanceof Map) {
        //             beanToMap(source, sourceType, dest, destType, options);
        //         } else {
        //             beanToBean(source, sourceType, dest, destType, options);
        //         }
        //     }
        // } catch (Exception e) {
        //     throw new ObjectConversionException(e);
        // }
        // return dest;
    }

    void mapToMap(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapType srcType,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapType dstType,
        @Nonnull ObjectConverter converter,
        @Nullable MappingOptions.PropertyMapper propertyMapper,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        src.forEach((srcKey, srcValue) -> {
            String srcPropertyName = Jie.as(converter.convert(srcKey, srcType.keyType, String.class, options));
            String dstPropertyName;
            Object dstPropertyValue;
            if (propertyMapper != null) {
                Map.Entry<String, Object> entry = propertyMapper.mapName(srcPropertyName, src, null);
                if (entry == null) {
                    return;
                }
                dstPropertyName = entry.getKey();
                dstPropertyValue = entry.getValue();
            } else {
                dstPropertyName = srcPropertyName;
                dstPropertyValue = converter.convert(srcValue, srcType.valueType, dstType.valueType, options);
            }
        });
    }

    private @Nonnull MapType getMapType(Type mapType) {
        ParameterizedType pType = (ParameterizedType) mapType;
        return new MapType(pType.getActualTypeArguments()[0], pType.getActualTypeArguments()[1]);
    }

    private static final class MapType {

        private final @Nonnull Type keyType;
        private final @Nonnull Type valueType;

        private MapType(@Nonnull Type keyType, @Nonnull Type valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }
    }

    private static final class RawMapTypeRef extends TypeRef<Map<Object, Object>> {
        private static final RawMapTypeRef SINGLETON = new RawMapTypeRef();
    }
}
