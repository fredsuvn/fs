package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

final class PropertyCopierImpl implements PropertyCopier {

    static final @Nonnull PropertyCopier DEFAULT =
        new PropertyCopierImpl(null, null);

    private final @Nullable PropertyMapper propertyMapper;
    private final @Nullable ExceptionHandler exceptionHandler;

    PropertyCopierImpl(
        @Nullable PropertyMapper propertyMapper,
        @Nullable ExceptionHandler exceptionHandler
    ) {
        this.propertyMapper = propertyMapper;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectCopyException {
        try {
            if (src instanceof Map) {
                MapParser srcParser = ConvertKit.mapParser(options);
                MapSchema srcSchema = parseMapSchema((Map<?, ?>) src, srcParser, srcType, options);
                if (dst instanceof Map) {
                    MapSchema dstParser = parseMapSchema((Map<?, ?>) dst, srcParser, dstType, options);
                    mapToMap(Fs.as(src), srcSchema, Fs.as(dst), dstParser, converter, options);
                } else {
                    ObjectParser objectParser = ConvertKit.objectParser(options);
                    ObjectSchema dstSchema = parseObjectSchema(dst, objectParser, dstType, options);
                    mapToObject(Fs.as(src), srcSchema, dst, dstSchema, converter, options);
                }
            } else {
                ObjectParser srcParser = ConvertKit.objectParser(options);
                ObjectSchema srcSchema = parseObjectSchema(src, srcParser, srcType, options);
                if (dst instanceof Map) {
                    MapParser dstParser = ConvertKit.mapParser(options);
                    MapSchema dstSchema = parseMapSchema((Map<?, ?>) dst, dstParser, dstType, options);
                    objectToMap(src, srcSchema, Fs.as(dst), dstSchema, converter, options);
                } else {
                    ObjectSchema dstSchema = srcParser.parse(dstType);
                    objectToObject(src, srcSchema, dst, dstSchema, converter, options);
                }
            }
        } catch (Exception e) {
            throw new ObjectCopyException(e);
        }
    }

    private @Nonnull ObjectSchema parseObjectSchema(
        @Nonnull Object object,
        @Nonnull ObjectParser parser,
        @Nonnull Type type,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        try {
            return parser.parse(type);
        } catch (DataSchemaException e) {
            if (OptionKit.containsKey(ConvertOption.STRICT_SOURCE_TYPE, options)) {
                throw e;
            }
            Type objType = object.getClass();
            if (Objects.equals(objType, type)) {
                throw e;
            }
            return parser.parse(objType);
        }
    }

    private @Nonnull MapSchema parseMapSchema(
        @Nonnull Map<?, ?> object,
        @Nonnull MapParser parser,
        @Nonnull Type type,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        try {
            return parser.parse(type);
        } catch (DataSchemaException e) {
            if (OptionKit.containsKey(ConvertOption.STRICT_SOURCE_TYPE, options)) {
                throw e;
            }
            /*
            this never happen:
              Type objType = object.getClass();
              if (Objects.equals(objType, type)) {
                  throw e;
              }
             */
            return parser.parse(object.getClass());
        }
    }

    @Override
    public @Nullable PropertyMapper propertyMapper() {
        return propertyMapper;
    }

    @Override
    public @Nullable ExceptionHandler exceptionHandler() {
        return exceptionHandler;
    }

    void mapToMap(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapSchema srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                if (ignored(srcKey, options)) {
                    return;
                }
                if (srcKey instanceof String) {
                    srcKey = getNameMapper(options).map((String) srcKey, srcSchema.type());
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
                    if (srcValue == null && ignoreNull(options)) {
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
                        throw new ObjectCopyException(ex);
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
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                if (ignored(srcKey, options)) {
                    return;
                }
                if (srcKey instanceof String) {
                    srcKey = getNameMapper(options).map((String) srcKey, srcSchema.type());
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
                    if (srcValue == null && ignoreNull(options)) {
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
                        throw new ObjectCopyException(ex);
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
                srcPropertyName = getNameMapper(options).map(srcPropertyName, srcSchema.type());
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
                    if (srcPropertyValue == null && ignoreNull(options)) {
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
                        throw new ObjectCopyException(ex);
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
                srcPropertyName = getNameMapper(options).map(srcPropertyName, srcSchema.type());
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
                    if (srcPropertyValue == null && ignoreNull(options)) {
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
                        throw new ObjectCopyException(ex);
                    }
                } else {
                    throw e;
                }
            }
        });
    }

    private boolean ignored(@Nonnull Object propertyName, @Nonnull Option<?, ?> @Nonnull ... options) {
        Object[] ignoredProperties = OptionKit.findValue(ConvertOption.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
    }

    private boolean ignoreNull(@Nonnull Option<?, ?> @Nonnull ... options) {
        return OptionKit.containsKey(ConvertOption.IGNORE_NULL, options);
    }

    private @Nonnull PropertyNameMapper getNameMapper(@Nonnull Option<?, ?> @Nonnull ... options) {
        PropertyNameMapper mapper = OptionKit.findValue(ConvertOption.PROPERTY_NAME_MAPPER, options);
        return mapper != null ? mapper : PropertyNameMapper.defaultMapper();
    }
}
