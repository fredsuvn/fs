package space.sunqian.fs.object.convert.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumberPattern;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.PropertyMeta;
import space.sunqian.fs.object.meta.ObjectMeta;

import java.util.Map;

/**
 * The common implementation of {@link ObjectCopier.Handler}, also be the default last handler of
 * {@link ObjectCopier#defaultCopier()}. Using {@link #getInstance()} can get a same one instance of this handler.
 * <p>
 * This handler uses the default implementations of {@link ObjectCopier.Handler}, which directly copies properties from
 * the source object to the target object, following the rules of the specified options defined in
 * {@link ConvertOption}.
 *
 * @author sunqian
 */
public class CommonCopierHandler implements ObjectCopier.Handler {

    private static final @Nonnull CommonCopierHandler INST = new CommonCopierHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull CommonCopierHandler getInstance() {
        return INST;
    }

    @Override
    public boolean copyProperty(
        @Nonnull Object srcKey,
        @Nullable Object srcValue,
        @Nonnull Map<Object, Object> src,
        @Nonnull MapMeta srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (ConvertOption.isIgnoreProperty(srcKey, options)) {
            return false;
        }
        if (srcValue == null && ConvertOption.isIgnoreNull(options)) {
            return false;
        }
        if (srcKey instanceof String) {
            srcKey = Fs.as(ConvertOption.getNameMapper(options).map((String) srcKey));
        }
        Object dstKey = converter.convert(srcKey, srcSchema.keyType(), dstSchema.keyType(), options);
        Object dstValue = converter.convert(srcValue, srcSchema.valueType(), dstSchema.valueType(), options);
        dst.put(dstKey, dstValue);
        return false;
    }

    @Override
    public boolean copyProperty(
        @Nonnull Object srcKey,
        @Nullable Object srcValue,
        @Nonnull Map<Object, Object> src,
        @Nonnull MapMeta srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (ConvertOption.isIgnoreProperty(srcKey, options)) {
            return false;
        }
        if (srcValue == null && ConvertOption.isIgnoreNull(options)) {
            return false;
        }
        if (srcKey instanceof String) {
            srcKey = ConvertOption.getNameMapper(options).map((String) srcKey);
        }
        String dstPropertyName = Fs.as(converter.convert(srcKey, srcSchema.keyType(), String.class, options));
        PropertyMeta dstProperty = dstSchema.getProperty(dstPropertyName);
        if (dstProperty == null || !dstProperty.isWritable()) {
            return false;
        }
        DatePattern datePattern = dstProperty.getAnnotation(DatePattern.class);
        NumberPattern numberPattern = dstProperty.getAnnotation(NumberPattern.class);
        Option<?, ?>[] actualOps = ConvertKit.mergeOptions(options, datePattern, numberPattern);
        Object dstPropertyValue = converter.convert(srcValue, srcSchema.valueType(), dstProperty.type(), actualOps);
        dstProperty.setValue(dst, dstPropertyValue);
        return false;
    }

    @Override
    public boolean copyProperty(
        @Nonnull String srcPropertyName,
        @Nonnull PropertyMeta srcProperty,
        @Nonnull Object src,
        @Nonnull ObjectMeta srcSchema,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (ConvertOption.isIgnoreProperty(srcProperty.name(), options)) {
            return false;
        }
        if (!srcProperty.isReadable()) {
            return false;
        }
        if ("class".equals(srcPropertyName) && !ConvertOption.isIncludeClass(options)) {
            return false;
        }
        String actualSrcPropertyName = ConvertOption.getNameMapper(options).map(srcPropertyName);
        Object srcPropertyValue = srcProperty.getValue(src);
        if (srcPropertyValue == null && ConvertOption.isIgnoreNull(options)) {
            return false;
        }
        Object dstKey = converter.convert(actualSrcPropertyName, String.class, dstSchema.keyType(), options);
        Object dstValue = converter.convert(srcPropertyValue, srcProperty.type(), dstSchema.valueType(), options);
        dst.put(dstKey, dstValue);
        return false;
    }

    @Override
    public boolean copyProperty(
        @Nonnull String srcPropertyName,
        @Nonnull PropertyMeta srcProperty,
        @Nonnull Object src,
        @Nonnull ObjectMeta srcSchema,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (ConvertOption.isIgnoreProperty(srcProperty.name(), options)) {
            return false;
        }
        if (!srcProperty.isReadable()) {
            return false;
        }
        if ("class".equals(srcPropertyName) && !ConvertOption.isIncludeClass(options)) {
            return false;
        }
        String actualSrcPropertyName = ConvertOption.getNameMapper(options).map(srcPropertyName);
        Object srcPropertyValue = srcProperty.getValue(src);
        if (srcPropertyValue == null && ConvertOption.isIgnoreNull(options)) {
            return false;
        }
        String dstPropertyName = Fs.as(converter.convert(actualSrcPropertyName, String.class, String.class, options));
        PropertyMeta dstProperty = dstSchema.getProperty(dstPropertyName);
        if (dstProperty == null || !dstProperty.isWritable()) {
            return false;
        }
        DatePattern datePattern = ConvertKit.getAnnotation(
            DatePattern.class, srcProperty, dstProperty
        );
        NumberPattern numberPattern = ConvertKit.getAnnotation(
            NumberPattern.class, srcProperty, dstProperty
        );
        Option<?, ?>[] actualOps = ConvertKit.mergeOptions(options, datePattern, numberPattern);
        Object dstPropertyValue = converter.convert(
            srcPropertyValue, srcProperty.type(), dstProperty.type(), actualOps
        );
        dstProperty.setValue(dst, dstPropertyValue);
        return false;
    }
}
