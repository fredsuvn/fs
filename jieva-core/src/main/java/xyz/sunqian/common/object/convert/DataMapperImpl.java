package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.data.DataSchema;
import xyz.sunqian.common.object.data.MapSchema;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.object.data.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class DataMapperImpl implements DataMapper {

    static final @Nonnull DataMapper SINGLETON = new DataMapperImpl(new ConcurrentHashMap<>());

    private final @Nonnull Map<Type, DataSchema> schemaCache;

    DataMapperImpl(@Nonnull Map<@Nonnull Type, @Nonnull DataSchema> schemaCache) {
        this.schemaCache = schemaCache;
    }

    @Override
    public void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConversionException {
        try {
            DataMapper.PropertyMapper propertyMapper = Option.findValue(OptionKey.PROPERTY_MAPPER, options);
            DataMapper.ExceptionHandler exceptionHandler = Option.findValue(OptionKey.EXCEPTION_HANDLER, options);
            if (src instanceof Map) {
                MapSchema srcSchema = schemaCache.computeIfAbsent(srcType, MapSchema::parse).asMapSchema();
                if (dst instanceof Map) {
                    MapSchema dstSchema = schemaCache.computeIfAbsent(dstType, MapSchema::parse).asMapSchema();
                    mapToMap(
                        Jie.as(src), srcSchema, Jie.as(dst), dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                } else {
                    ObjectSchema dstSchema = schemaCache.computeIfAbsent(dstType, ObjectSchema::parse).asObjectSchema();
                    mapToObject(
                        Jie.as(src), srcSchema, dst, dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                }
            } else {
                ObjectSchema srcSchema = schemaCache.computeIfAbsent(srcType, ObjectSchema::parse).asObjectSchema();
                if (dst instanceof Map) {
                    MapSchema dstSchema = schemaCache.computeIfAbsent(dstType, MapSchema::parse).asMapSchema();
                    objectToMap(
                        src, srcSchema, Jie.as(dst), dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                } else {
                    ObjectSchema dstSchema = schemaCache.computeIfAbsent(dstType, ObjectSchema::parse).asObjectSchema();
                    objectToObject(
                        src, srcSchema, dst, dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                }
            }
        } catch (Exception e) {
            throw new ObjectConversionException(e);
        }
    }

    void mapToMap(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapSchema srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nullable DataMapper.PropertyMapper propertyMapper,
        @Nullable DataMapper.ExceptionHandler exceptionHandler,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                Object dstPropertyName;
                Object dstPropertyValue;
                if (propertyMapper != null) {
                    Map.Entry<Object, Object> entry = propertyMapper.map(
                        srcKey, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (entry == null) {
                        return;
                    }
                    dstPropertyName = entry.getKey();
                    dstPropertyValue = entry.getValue();
                } else {
                    dstPropertyName = converter.convert(
                        srcKey, srcSchema.keyType(), dstSchema.keyType(), options);
                    dstPropertyValue = converter.convert(
                        srcValue, srcSchema.valueType(), dstSchema.valueType(), options);
                }
                dst.put(dstPropertyName, dstPropertyValue);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcKey, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectConversionException(ex);
                    }
                }
            }
        });
    }

    void mapToObject(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapSchema srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nullable DataMapper.PropertyMapper propertyMapper,
        @Nullable DataMapper.ExceptionHandler exceptionHandler,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                Object dstPropertyName;
                Object dstPropertyValue;
                ObjectProperty dstProperty;
                if (propertyMapper != null) {
                    Map.Entry<Object, Object> entry = propertyMapper.map(
                        srcKey, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (entry == null) {
                        return;
                    }
                    dstPropertyName = entry.getKey();
                    dstPropertyValue = entry.getValue();
                    dstProperty = dstSchema.getProperty((String) dstPropertyName);
                    if (dstProperty == null || !dstProperty.isWritable()) {
                        return;
                    }
                } else {
                    dstPropertyName = converter.convert(srcKey, srcSchema.keyType(), String.class, options);
                    dstProperty = dstSchema.getProperty((String) dstPropertyName);
                    if (dstProperty == null || !dstProperty.isWritable()) {
                        return;
                    }
                    dstPropertyValue = converter.convert(srcValue, srcSchema.valueType(), dstProperty.type(), options);
                }
                dstProperty.setValue(dst, dstPropertyValue);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcKey, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectConversionException(ex);
                    }
                }
            }
        });
    }

    void objectToMap(
        @Nonnull Object src,
        @Nonnull ObjectSchema srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nullable DataMapper.PropertyMapper propertyMapper,
        @Nullable DataMapper.ExceptionHandler exceptionHandler,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        srcSchema.properties().forEach((srcPropertyName, srcProperty) -> {
            try {
                if (!srcProperty.isReadable()) {
                    return;
                }
                Object dstPropertyName;
                Object dstPropertyValue;
                if (propertyMapper != null) {
                    Map.Entry<Object, Object> entry = propertyMapper.map(
                        srcPropertyName, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (entry == null) {
                        return;
                    }
                    dstPropertyName = entry.getKey();
                    dstPropertyValue = entry.getValue();
                } else {
                    dstPropertyName = converter.convert(srcPropertyName, String.class, dstSchema.keyType(), options);
                    Object srcPropertyValue = srcProperty.getValue(src);
                    dstPropertyValue = converter.convert(
                        srcPropertyValue, srcProperty.type(), dstSchema.valueType(), options);
                }
                dst.put(dstPropertyName, dstPropertyValue);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcPropertyName, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectConversionException(ex);
                    }
                }
            }
        });
    }

    void objectToObject(
        @Nonnull Object src,
        @Nonnull ObjectSchema srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nullable DataMapper.PropertyMapper propertyMapper,
        @Nullable DataMapper.ExceptionHandler exceptionHandler,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        srcSchema.properties().forEach((srcPropertyName, srcProperty) -> {
            try {
                if (!srcProperty.isReadable()) {
                    return;
                }
                Object dstPropertyName;
                Object dstPropertyValue;
                ObjectProperty dstProperty;
                if (propertyMapper != null) {
                    Map.Entry<Object, Object> entry = propertyMapper.map(
                        srcPropertyName, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (entry == null) {
                        return;
                    }
                    dstPropertyName = entry.getKey();
                    dstPropertyValue = entry.getValue();
                    dstProperty = dstSchema.getProperty((String) dstPropertyName);
                    if (dstProperty == null || !dstProperty.isWritable()) {
                        return;
                    }
                } else {
                    dstPropertyName = srcPropertyName;
                    dstProperty = dstSchema.getProperty((String) dstPropertyName);
                    if (dstProperty == null || !dstProperty.isWritable()) {
                        return;
                    }
                    Object srcPropertyValue = srcProperty.getValue(src);
                    dstPropertyValue = converter.convert(
                        srcPropertyValue, srcProperty.type(), dstProperty.type(), options);
                }
                dstProperty.setValue(dst, dstPropertyValue);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcPropertyName, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectConversionException(ex);
                    }
                }
            }
        });
    }
}
