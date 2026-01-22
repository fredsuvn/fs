package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;

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
                MapParser mapParser = ConvertKit.mapParser(options);
                MapSchema srcSchema = mapParser.parse(srcType);
                if (dst instanceof Map) {
                    MapSchema dstSchema = mapParser.parse(dstType);
                    mapToMap(Fs.as(src), srcSchema, Fs.as(dst), dstSchema, converter, options);
                } else {
                    ObjectParser objectParser = ConvertKit.objectParser(options);
                    ObjectSchema dstSchema = objectParser.parse(dstType);
                    mapToObject(Fs.as(src), srcSchema, dst, dstSchema, converter, options);
                }
            } else {
                ObjectParser objectParser = ConvertKit.objectParser(options);
                ObjectSchema srcSchema = objectParser.parse(srcType);
                if (dst instanceof Map) {
                    MapParser mapParser = ConvertKit.mapParser(options);
                    MapSchema dstSchema = mapParser.parse(dstType);
                    objectToMap(src, srcSchema, Fs.as(dst), dstSchema, converter, options);
                } else {
                    ObjectSchema dstSchema = objectParser.parse(dstType);
                    objectToObject(src, srcSchema, dst, dstSchema, converter, options);
                }
            }
        } catch (Exception e) {
            throw new ObjectCopyException(e);
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
        Object[] ignoredProperties = Option.findValue(ConvertOption.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
    }

    private boolean ignoreNull(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Option.containsKey(ConvertOption.IGNORE_NULL, options);
    }
}
