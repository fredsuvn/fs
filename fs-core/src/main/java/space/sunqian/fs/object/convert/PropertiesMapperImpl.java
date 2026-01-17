package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.object.schema.DataSchema;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.MapSchemaParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.object.schema.ObjectSchemaParser;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class PropertiesMapperImpl implements PropertiesMapper {

    static final @Nonnull PropertiesMapper DEFAULT =
        new PropertiesMapperImpl(CacheFunction.ofMap(new ConcurrentHashMap<>()));

    private final @Nonnull CacheFunction<@Nonnull Type, @Nonnull DataSchema> cache;

    PropertiesMapperImpl(@Nonnull CacheFunction<@Nonnull Type, @Nonnull DataSchema> cache) {
        this.cache = cache;
    }

    @Override
    public void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        try {
            PropertyMapper propertyMapper = Option.findValue(ConvertOption.PROPERTY_MAPPER, options);
            PropertiesMapper.ExceptionHandler exceptionHandler = Option.findValue(ConvertOption.EXCEPTION_HANDLER, options);
            if (src instanceof Map) {
                MapSchemaParser mapSchemaParser = Fs.nonnull(
                    Option.findValue(ConvertOption.MAP_SCHEMA_PARSER),
                    MapSchemaParser.defaultParser()
                );
                MapSchema srcSchema = cache.get(srcType, mapSchemaParser::parse).asMapSchema();
                if (dst instanceof Map) {
                    MapSchema dstSchema = cache.get(dstType, mapSchemaParser::parse).asMapSchema();
                    mapToMap(
                        Fs.as(src), srcSchema, Fs.as(dst), dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                } else {
                    ObjectSchemaParser objectSchemaParser = Fs.nonnull(
                        Option.findValue(ConvertOption.OBJECT_SCHEMA_PARSER),
                        ObjectSchemaParser.defaultParser()
                    );
                    ObjectSchema dstSchema = cache.get(dstType, objectSchemaParser::parse).asObjectSchema();
                    mapToObject(
                        Fs.as(src), srcSchema, dst, dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                }
            } else {
                ObjectSchemaParser objectSchemaParser = Fs.nonnull(
                    Option.findValue(ConvertOption.OBJECT_SCHEMA_PARSER),
                    ObjectSchemaParser.defaultParser()
                );
                ObjectSchema srcSchema = cache.get(srcType, objectSchemaParser::parse).asObjectSchema();
                if (dst instanceof Map) {
                    MapSchemaParser mapSchemaParser = Fs.nonnull(
                        Option.findValue(ConvertOption.MAP_SCHEMA_PARSER),
                        MapSchemaParser.defaultParser()
                    );
                    MapSchema dstSchema = cache.get(dstType, mapSchemaParser::parse).asMapSchema();
                    objectToMap(
                        src, srcSchema, Fs.as(dst), dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                } else {
                    ObjectSchema dstSchema = cache.get(dstType, objectSchemaParser::parse).asObjectSchema();
                    objectToObject(
                        src, srcSchema, dst, dstSchema, converter, propertyMapper, exceptionHandler, options
                    );
                }
            }
        } catch (Exception e) {
            throw new ObjectConvertException(e);
        }
    }

    void mapToMap(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapSchema srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nullable PropertyMapper propertyMapper,
        @Nullable PropertiesMapper.ExceptionHandler exceptionHandler,
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
                        throw new ObjectConvertException(ex);
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
        @Nullable PropertyMapper propertyMapper,
        @Nullable PropertiesMapper.ExceptionHandler exceptionHandler,
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
                        throw new ObjectConvertException(ex);
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
        @Nullable PropertyMapper propertyMapper,
        @Nullable PropertiesMapper.ExceptionHandler exceptionHandler,
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
                        throw new ObjectConvertException(ex);
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
        @Nullable PropertyMapper propertyMapper,
        @Nullable PropertiesMapper.ExceptionHandler exceptionHandler,
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
                        throw new ObjectConvertException(ex);
                    }
                } else {
                    throw e;
                }
            }
        });
    }

    private boolean ignored(@Nonnull Object propertyName, @Nonnull Option<?, ?> @Nonnull ... options) {
        Object[] ignoredProperties = Option.findValue(ConvertOption.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
    }

    private boolean ignoredNull(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Option.containsKey(ConvertOption.IGNORE_NULL, options);
    }
}
