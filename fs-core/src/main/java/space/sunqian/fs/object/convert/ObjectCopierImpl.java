package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.convert.handlers.CommonCopierHandler;
import space.sunqian.fs.object.meta.DataMetaException;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.MapMetaManager;
import space.sunqian.fs.object.meta.PropertyMetaMeta;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaManager;

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
                MapMetaManager srcParser = ConvertOption.getMapSchemaParser(actualOptions);
                MapMeta srcSchema = parseMapSchema((Map<?, ?>) src, srcParser, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapMeta dstParser = parseMapSchema((Map<?, ?>) dst, srcParser, dstType, actualOptions);
                    mapToMap(Fs.as(src), srcSchema, Fs.as(dst), dstParser, converter, actualOptions);
                } else {
                    ObjectMetaManager objectParser = ConvertOption.getObjectSchemaParser(actualOptions);
                    ObjectMeta dstSchema = parseObjectSchema(dst, objectParser, dstType, actualOptions);
                    mapToObject(Fs.as(src), srcSchema, dst, dstSchema, converter, actualOptions);
                }
            } else {
                ObjectMetaManager srcParser = ConvertOption.getObjectSchemaParser(actualOptions);
                ObjectMeta srcSchema = parseObjectSchema(src, srcParser, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapMetaManager dstParser = ConvertOption.getMapSchemaParser(actualOptions);
                    MapMeta dstSchema = parseMapSchema((Map<?, ?>) dst, dstParser, dstType, actualOptions);
                    objectToMap(src, srcSchema, Fs.as(dst), dstSchema, converter, actualOptions);
                } else {
                    ObjectMeta dstSchema = srcParser.parse(dstType);
                    objectToObject(src, srcSchema, dst, dstSchema, converter, actualOptions);
                }
            }
        } catch (ObjectCopyException e) {
            throw e;
        } catch (Exception e) {
            throw new ObjectCopyException(e);
        }
    }

    private @Nonnull ObjectMeta parseObjectSchema(
        @Nonnull Object object,
        @Nonnull ObjectMetaManager parser,
        @Nonnull Type type,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        try {
            return parser.parse(type);
        } catch (DataMetaException e) {
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

    private @Nonnull MapMeta parseMapSchema(
        @Nonnull Map<?, ?> object,
        @Nonnull MapMetaManager parser,
        @Nonnull Type type,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        try {
            return parser.introspect(type);
        } catch (DataMetaException e) {
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
            return parser.introspect(object.getClass());
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
        @Nonnull MapMeta srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (!goon) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new ObjectCopyException(e);
            }
        });
    }

    void mapToObject(
        @Nonnull Map<Object, Object> src,
        @Nonnull MapMeta srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (!goon) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new ObjectCopyException(e);
            }
        });
    }

    void objectToMap(
        @Nonnull Object src,
        @Nonnull ObjectMeta srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        srcSchema.properties().forEach((srcPropertyName, srcProperty) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcProperty.name(), srcProperty, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (!goon) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new ObjectCopyException(e);
            }
        });
    }

    void objectToObject(
        @Nonnull Object src,
        @Nonnull ObjectMeta srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        srcSchema.properties().forEach((srcPropertyName, srcProperty) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcProperty.name(), srcProperty, src, srcSchema, dst, dstSchema, converter, options
                    );
                    if (!goon) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new ObjectCopyException(e);
            }
        });
    }

    @Override
    public boolean copyProperty(@Nonnull Object srcKey, @Nullable Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }

    @Override
    public boolean copyProperty(@Nonnull Object srcKey, @Nullable Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcKey, srcValue, src, srcSchema, dst, dstSchema, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }

    @Override
    public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcPropertyName, srcProperty, src, srcSchema, dst, dstSchema, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }

    @Override
    public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcPropertyName, srcProperty, src, srcSchema, dst, dstSchema, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }
}
