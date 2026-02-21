package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.convert.handlers.CommonCopierHandler;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class ObjectCopierImpl implements ObjectCopier, ObjectCopier.Handler {

    static final @Nonnull ObjectCopier DEFAULT = new ObjectCopierImpl(
        ListKit.list(CommonCopierHandler.getInstance()),
        Collections.emptyList()
    );

    private final @Nonnull List<@Nonnull Handler> handlers;
    private final @Nonnull List<@Nonnull Option<?, ?>> defaultOptions;
    private final @Nonnull Option<?, ?> @Nonnull [] defaultOptionsArray;

    ObjectCopierImpl(
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers,
        @Nonnull @RetainedParam List<@Nonnull Option<?, ?>> defaultOptions
    ) {
        this.handlers = Collections.unmodifiableList(handlers);
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
        try {
            @Nonnull Option<?, ?> @Nonnull [] actualOptions = OptionKit.mergeOptions(defaultOptionsArray, options);
            if (src instanceof Map) {
                MapParser srcParser = ConvertOption.getMapParser(actualOptions);
                MapSchema srcSchema = parseMapSchema((Map<?, ?>) src, srcParser, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapSchema dstParser = parseMapSchema((Map<?, ?>) dst, srcParser, dstType, actualOptions);
                    mapToMap(Fs.as(src), srcSchema, Fs.as(dst), dstParser, converter, actualOptions);
                } else {
                    ObjectParser objectParser = ConvertOption.getObjectParser(actualOptions);
                    ObjectSchema dstSchema = parseObjectSchema(dst, objectParser, dstType, actualOptions);
                    mapToObject(Fs.as(src), srcSchema, dst, dstSchema, converter, actualOptions);
                }
            } else {
                ObjectParser srcParser = ConvertOption.getObjectParser(actualOptions);
                ObjectSchema srcSchema = parseObjectSchema(src, srcParser, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapParser dstParser = ConvertOption.getMapParser(actualOptions);
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
            if (ConvertOption.isStrictSourceTypeMode(options)) {
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
            if (ConvertOption.isStrictSourceTypeMode(options)) {
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
    public @Nonnull List<@Nonnull Handler> handlers() {
        return handlers;
    }

    @Override
    public @Nonnull List<@Nonnull Option<?, ?>> defaultOptions() {
        return defaultOptions;
    }

    @Override
    public @Nonnull Handler asHandler() {
        return this;
    }

    void mapToMap(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapSchema srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        for (Map.Entry<Object, Object> entry : src.entrySet()) {
            Object srcKey = entry.getKey();
            Object srcValue = entry.getValue();
            for (Handler handler : handlers) {
                boolean goon = handler.copyProperty(
                    srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
                );
                if (!goon) {
                    break;
                }
            }
        }
    }

    void mapToObject(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapSchema srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        for (Map.Entry<Object, Object> entry : src.entrySet()) {
            Object srcKey = entry.getKey();
            Object srcValue = entry.getValue();
            for (Handler handler : handlers) {
                boolean goon = handler.copyProperty(
                    srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
                );
                if (!goon) {
                    break;
                }
            }
        }
    }

    void objectToMap(
        @Nonnull Object src,
        @Nonnull ObjectSchema srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        for (Map.Entry<@Nonnull String, @Nonnull ObjectProperty> entry : srcSchema.properties().entrySet()) {
            // String srcPropertyName = entry.getKey();
            ObjectProperty srcProperty = entry.getValue();
            for (Handler handler : handlers) {
                boolean goon = handler.copyProperty(
                    srcProperty.name(), srcProperty, src, srcSchema, dst, dstSchema, converter, options
                );
                if (!goon) {
                    break;
                }
            }
        }
    }

    void objectToObject(
        @Nonnull Object src,
        @Nonnull ObjectSchema srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        for (Map.Entry<@Nonnull String, @Nonnull ObjectProperty> entry : srcSchema.properties().entrySet()) {
            // String srcPropertyName = entry.getKey();
            ObjectProperty srcProperty = entry.getValue();
            for (Handler handler : handlers) {
                boolean goon = handler.copyProperty(
                    srcProperty.name(), srcProperty, src, srcSchema, dst, dstSchema, converter, options
                );
                if (!goon) {
                    break;
                }
            }
        }
    }
}
