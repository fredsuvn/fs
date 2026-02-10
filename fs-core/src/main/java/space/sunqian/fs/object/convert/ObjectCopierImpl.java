package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class ObjectCopierImpl implements ObjectCopier {

    static final @Nonnull ObjectCopier DEFAULT =
        new ObjectCopierImpl(null, null, Collections.emptyList());

    private final @Nonnull PropertyMapper propertyMapper;
    private final @Nullable ExceptionHandler exceptionHandler;
    private final @Nonnull List<@Nonnull Option<?, ?>> defaultOptions;
    private final @Nonnull Option<?, ?> @Nonnull [] defaultOptionsArray;

    ObjectCopierImpl(
        @Nullable PropertyMapper propertyMapper,
        @Nullable ExceptionHandler exceptionHandler,
        @Nonnull @RetainedParam List<@Nonnull Option<?, ?>> defaultOptions
    ) {
        this.propertyMapper = Fs.nonnull(propertyMapper, ObjectCopier.defaultPropertyMapper());
        this.exceptionHandler = exceptionHandler;
        this.defaultOptions = Collections.unmodifiableList(defaultOptions);
        this.defaultOptionsArray = defaultOptions.toArray(new Option[0]);
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
        @Nonnull Option<?, ?> @Nonnull [] actualOptions = OptionKit.mergeOptions(defaultOptionsArray, options);
        try {
            if (src instanceof Map) {
                MapParser srcParser = ConvertKit.mapParser(actualOptions);
                MapSchema srcSchema = parseMapSchema((Map<?, ?>) src, srcParser, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapSchema dstParser = parseMapSchema((Map<?, ?>) dst, srcParser, dstType, actualOptions);
                    mapToMap(Fs.as(src), srcSchema, Fs.as(dst), dstParser, converter, actualOptions);
                } else {
                    ObjectParser objectParser = ConvertKit.objectParser(actualOptions);
                    ObjectSchema dstSchema = parseObjectSchema(dst, objectParser, dstType, actualOptions);
                    mapToObject(Fs.as(src), srcSchema, dst, dstSchema, converter, actualOptions);
                }
            } else {
                ObjectParser srcParser = ConvertKit.objectParser(actualOptions);
                ObjectSchema srcSchema = parseObjectSchema(src, srcParser, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapParser dstParser = ConvertKit.mapParser(actualOptions);
                    MapSchema dstSchema = parseMapSchema((Map<?, ?>) dst, dstParser, dstType, actualOptions);
                    objectToMap(src, srcSchema, Fs.as(dst), dstSchema, converter, actualOptions);
                } else {
                    ObjectSchema dstSchema = srcParser.parse(dstType);
                    objectToObject(src, srcSchema, dst, dstSchema, converter, actualOptions);
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
            if (OptionKit.isEnabled(ConvertOption.STRICT_SOURCE_TYPE_MODE, options)) {
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
            if (OptionKit.isEnabled(ConvertOption.STRICT_SOURCE_TYPE_MODE, options)) {
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

    @Override
    public @Nonnull List<@Nonnull Option<?, ?>> defaultOptions() {
        return defaultOptions;
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
                propertyMapper.map(
                    srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
                );
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcKey, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectCopyException(ex);
                    }
                } else {
                    throw new ObjectCopyException(e);
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
                propertyMapper.map(srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcKey, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectCopyException(ex);
                    }
                } else {
                    throw new ObjectCopyException(e);
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
                propertyMapper.map(srcProperty, src, srcSchema, dst, dstSchema, converter, options);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcPropertyName, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectCopyException(ex);
                    }
                } else {
                    throw new ObjectCopyException(e);
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
                propertyMapper.map(srcProperty, src, srcSchema, dst, dstSchema, converter, options);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    try {
                        exceptionHandler.handle(e, srcPropertyName, src, srcSchema, dst, dstSchema, converter, options);
                    } catch (Exception ex) {
                        throw new ObjectCopyException(ex);
                    }
                } else {
                    throw new ObjectCopyException(e);
                }
            }
        });
    }
}
