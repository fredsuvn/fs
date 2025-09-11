package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.collect.ArrayKit;
import xyz.sunqian.common.object.data.DataObjectException;
import xyz.sunqian.common.object.data.DataSchema;
import xyz.sunqian.common.object.data.MapSchema;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.object.data.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static xyz.sunqian.common.object.convert.MappingOptions.Key.EXCEPTION_HANDLER;
import static xyz.sunqian.common.object.convert.MappingOptions.Key.PROPERTY_MAPPER;

final class DataMapperImpl implements DataMapper {

    static final @Nonnull DataMapper SINGLETON = new DataMapperImpl(new SchemaCacheImpl(new ConcurrentHashMap<>()));

    private final @Nonnull SchemaCache schemaCache;

    DataMapperImpl(@Nonnull SchemaCache schemaCache) {
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
            DataMapper.PropertyMapper propertyMapper = Option.findValue(PROPERTY_MAPPER, options);
            DataMapper.ExceptionHandler exceptionHandler = Option.findValue(EXCEPTION_HANDLER, options);
            if (src instanceof Map) {
                MapSchema srcSchema = schemaCache.get(srcType, MapSchema::parse).asMapSchema();
                if (dst instanceof Map) {
                    MapSchema dstSchema = schemaCache.get(dstType, MapSchema::parse).asMapSchema();
                    mapToMap(
                        Jie.as(src), srcSchema, Jie.as(dst), dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                } else {
                    ObjectSchema dstSchema = schemaCache.get(dstType, ObjectSchema::parse).asObjectSchema();
                    mapToObject(
                        Jie.as(src), srcSchema, dst, dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                }
            } else {
                ObjectSchema srcSchema = schemaCache.get(srcType, ObjectSchema::parse).asObjectSchema();
                if (dst instanceof Map) {
                    MapSchema dstSchema = schemaCache.get(dstType, MapSchema::parse).asMapSchema();
                    objectToMap(
                        src, srcSchema, Jie.as(dst), dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                } else {
                    ObjectSchema dstSchema = schemaCache.get(dstType, ObjectSchema::parse).asObjectSchema();
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
                if (ignored(srcKey, options)) {
                    return;
                }
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
                    if (srcValue == null && ignoredNull(options)) {
                        return;
                    }
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
                } else {
                    throw e;
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
                if (ignored(srcKey, options)) {
                    return;
                }
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
                    if (srcValue == null && ignoredNull(options)) {
                        return;
                    }
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
                } else {
                    throw e;
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
                if (ignored(srcPropertyName, options)) {
                    return;
                }
                if (!srcProperty.isReadable()) {
                    return;
                }
                // do not map "class"
                if ("class".equals(srcPropertyName)) {
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
                    Object srcPropertyValue = srcProperty.getValue(src);
                    if (srcPropertyValue == null && ignoredNull(options)) {
                        return;
                    }
                    dstPropertyName = converter.convert(srcPropertyName, String.class, dstSchema.keyType(), options);
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
                } else {
                    throw e;
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
                if (ignored(srcPropertyName, options)) {
                    return;
                }
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
                    Object srcPropertyValue = srcProperty.getValue(src);
                    if (srcPropertyValue == null && ignoredNull(options)) {
                        return;
                    }
                    dstPropertyName = srcPropertyName;
                    dstProperty = dstSchema.getProperty((String) dstPropertyName);
                    if (dstProperty == null || !dstProperty.isWritable()) {
                        return;
                    }
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
                } else {
                    throw e;
                }
            }
        });
    }

    private boolean ignored(@Nonnull Object propertyName, @Nonnull Option<?, ?> @Nonnull ... options) {
        Object[] ignoredProperties = Option.findValue(MappingOptions.Key.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
    }

    private boolean ignoredNull(@Nonnull Option<?, ?> @Nonnull ... options) {
        Object op = Option.findOption(MappingOptions.Key.IGNORE_NULL_PROPERTIES, options);
        return op != null;
    }

    static final class SchemaCacheImpl implements SchemaCache {

        private final @Nonnull Map<@Nonnull Type, @Nonnull DataSchema> map;

        SchemaCacheImpl(@Nonnull Map<@Nonnull Type, @Nonnull DataSchema> map) {
            this.map = map;
        }

        @Override
        public @Nonnull DataSchema get(
            @Nonnull Type type, @Nonnull
            Function<@Nonnull Type, @Nonnull DataSchema> loader
        ) throws DataObjectException {
            return map.computeIfAbsent(type, loader);
        }
    }
}
