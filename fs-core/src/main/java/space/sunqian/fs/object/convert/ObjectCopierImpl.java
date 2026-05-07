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
import space.sunqian.fs.object.meta.MapMetaIntrospector;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaIntrospector;
import space.sunqian.fs.object.meta.PropertyMeta;

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
                MapMetaIntrospector srcIntrospector = ConvertOption.getMapMetaIntrospector(actualOptions);
                MapMeta srcMeta = introspectMap((Map<?, ?>) src, srcIntrospector, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapMeta dstMeta = introspectMap((Map<?, ?>) dst, srcIntrospector, dstType, actualOptions);
                    mapToMap(Fs.as(src), srcMeta, Fs.as(dst), dstMeta, converter, actualOptions);
                } else {
                    ObjectMetaIntrospector dstIntrospector = ConvertOption.getObjectMetaIntrospector(actualOptions);
                    ObjectMeta dstMeta = introspectObject(dst, dstIntrospector, dstType, actualOptions);
                    mapToObject(Fs.as(src), srcMeta, dst, dstMeta, converter, actualOptions);
                }
            } else {
                ObjectMetaIntrospector srcIntrospector = ConvertOption.getObjectMetaIntrospector(actualOptions);
                ObjectMeta srcMeta = introspectObject(src, srcIntrospector, srcType, actualOptions);
                if (dst instanceof Map) {
                    MapMetaIntrospector dstIntrospector = ConvertOption.getMapMetaIntrospector(actualOptions);
                    MapMeta dstMeta = introspectMap((Map<?, ?>) dst, dstIntrospector, dstType, actualOptions);
                    objectToMap(src, srcMeta, Fs.as(dst), dstMeta, converter, actualOptions);
                } else {
                    ObjectMeta dstMeta = introspectObject(dst, srcIntrospector, dstType, actualOptions);
                    objectToObject(src, srcMeta, dst, dstMeta, converter, actualOptions);
                }
            }
        } catch (ObjectCopyException e) {
            throw e;
        } catch (Exception e) {
            throw new ObjectCopyException(e);
        }
    }

    private @Nonnull ObjectMeta introspectObject(
        @Nonnull Object object,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull Type type,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        try {
            return introspector.introspect(type);
        } catch (DataMetaException e) {
            if (ConvertOption.isStrictSourceTypeMode(options)) {
                throw e;
            }
            Type objType = object.getClass();
            if (Objects.equals(objType, type)) {
                throw e;
            }
            return introspector.introspect(objType);
        }
    }

    private @Nonnull MapMeta introspectMap(
        @Nonnull Map<?, ?> object,
        @Nonnull MapMetaIntrospector introspector,
        @Nonnull Type type,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        try {
            return introspector.introspect(type);
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
            return introspector.introspect(object.getClass());
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
        @Nonnull MapMeta srcMeta,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstMeta,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcKey, srcValue, src, srcMeta, dst, dstMeta, converter, options
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
        @Nonnull MapMeta srcMeta,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstMeta,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        src.forEach((srcKey, srcValue) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcKey, srcValue, src, srcMeta, dst, dstMeta, converter, options
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
        @Nonnull ObjectMeta srcMeta,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstMeta,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        srcMeta.properties().forEach((srcPropertyName, srcProperty) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcProperty.name(), srcProperty, src, srcMeta, dst, dstMeta, converter, options
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
        @Nonnull ObjectMeta srcMeta,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstMeta,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        srcMeta.properties().forEach((srcPropertyName, srcProperty) -> {
            try {
                for (Handler handler : handlers) {
                    boolean goon = handler.copyProperty(
                        srcProperty.name(), srcProperty, src, srcMeta, dst, dstMeta, converter, options
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
    public boolean copyProperty(@Nonnull Object srcKey, @Nullable Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcMeta, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstMeta, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcKey, srcValue, src, srcMeta, dst, dstMeta, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }

    @Override
    public boolean copyProperty(@Nonnull Object srcKey, @Nullable Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcMeta, @Nonnull Object dst, @Nonnull ObjectMeta dstMeta, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcKey, srcValue, src, srcMeta, dst, dstMeta, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }

    @Override
    public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcMeta, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstMeta, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcPropertyName, srcProperty, src, srcMeta, dst, dstMeta, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }

    @Override
    public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcMeta, @Nonnull Object dst, @Nonnull ObjectMeta dstMeta, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        boolean goon = true;
        for (Handler handler : handlers) {
            goon = handler.copyProperty(
                srcPropertyName, srcProperty, src, srcMeta, dst, dstMeta, converter, options
            );
            if (!goon) {
                break;
            }
        }
        return goon;
    }
}
