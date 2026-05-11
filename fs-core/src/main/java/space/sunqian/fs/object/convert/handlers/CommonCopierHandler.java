package space.sunqian.fs.object.convert.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.annotation.AnnotationSet;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.DatePatternDetail;
import space.sunqian.fs.object.annotation.NumberPattern;
import space.sunqian.fs.object.annotation.NumberPatternDetail;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.PropertyMeta;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * The common implementation of {@link ObjectCopier.Handler}, also be the default last handler of
 * {@link ObjectCopier#defaultCopier()}. Using {@link #getInstance()} can get a same one instance of this handler.
 * <p>
 * This handler is the default implementations of {@link ObjectCopier.Handler}, which directly copies properties from
 * the source object to the target object, following the rules of the specified options defined in
 * {@link ConvertOption}. By default, this handler converts values. For keys/property names, if their types are equal,
 * no conversion is performed; only when their types differ, they will be converted in the same way as values.
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
        @Nonnull MapMeta srcMeta,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstMeta,
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
        Type srcKeyType = srcMeta.keyType();
        Type dstKeyType = dstMeta.keyType();
        Object dstKey = ensureKey(srcKey, srcKeyType, dstKeyType, converter, options);
        Object dstValue = converter.convert(srcValue, srcMeta.valueType(), dstMeta.valueType(), options);
        dst.put(dstKey, dstValue);
        return false;
    }

    @Override
    public boolean copyProperty(
        @Nonnull Object srcKey,
        @Nullable Object srcValue,
        @Nonnull Map<Object, Object> src,
        @Nonnull MapMeta srcMeta,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstMeta,
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
        Type srcKeyType = srcMeta.keyType();
        // Type dstKeyType = String.class;
        String dstPropertyName = (String) ensureKey(srcKey, srcKeyType, String.class, converter, options);
        PropertyMeta dstProperty = dstMeta.getProperty(dstPropertyName);
        if (dstProperty == null || !dstProperty.isWritable()) {
            return false;
        }
        AnnotationSet dstAnnotations = dstProperty.annotations();
        DatePatternDetail datePattern = dstAnnotations.getDetailByAnnotationType(DatePattern.class);
        NumberPatternDetail numberPattern = dstAnnotations.getDetailByAnnotationType(NumberPattern.class);
        Option<?, ?>[] actualOps = ConvertKit.mergeOptions(options, datePattern, numberPattern);
        Object dstPropertyValue = converter.convert(srcValue, srcMeta.valueType(), dstProperty.type(), actualOps);
        dstProperty.setValue(dst, dstPropertyValue);
        return false;
    }

    @Override
    public boolean copyProperty(
        @Nonnull String srcPropertyName,
        @Nonnull PropertyMeta srcProperty,
        @Nonnull Object src,
        @Nonnull ObjectMeta srcMeta,
        @Nonnull Map<Object, Object> dst,
        @Nonnull MapMeta dstMeta,
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
        // Type srcKeyType = String.class;
        Type dstKeyType = dstMeta.keyType();
        Object dstKey = ensureKey(actualSrcPropertyName, String.class, dstKeyType, converter, options);
        AnnotationSet srcAnnotations = srcProperty.annotations();
        DatePatternDetail datePattern = srcAnnotations.getDetailByAnnotationType(DatePattern.class);
        NumberPatternDetail numberPattern = srcAnnotations.getDetailByAnnotationType(NumberPattern.class);
        Option<?, ?>[] actualOps = ConvertKit.mergeOptions(options, datePattern, numberPattern);
        Object dstValue = converter.convert(srcPropertyValue, srcProperty.type(), dstMeta.valueType(), actualOps);
        dst.put(dstKey, dstValue);
        return false;
    }

    @Override
    public boolean copyProperty(
        @Nonnull String srcPropertyName,
        @Nonnull PropertyMeta srcProperty,
        @Nonnull Object src,
        @Nonnull ObjectMeta srcMeta,
        @Nonnull Object dst,
        @Nonnull ObjectMeta dstMeta,
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
        @SuppressWarnings("UnnecessaryLocalVariable")
        String dstPropertyName = actualSrcPropertyName;// Fs.as(converter.convert(actualSrcPropertyName, String.class, String.class, options));
        PropertyMeta dstProperty = dstMeta.getProperty(dstPropertyName);
        if (dstProperty == null || !dstProperty.isWritable()) {
            return false;
        }
        DatePatternDetail datePattern = ConvertKit.getAnnotationDetail(
            DatePattern.class, srcProperty, dstProperty
        );
        NumberPatternDetail numberPattern = ConvertKit.getAnnotationDetail(
            NumberPattern.class, srcProperty, dstProperty
        );
        Option<?, ?>[] actualOps = ConvertKit.mergeOptions(options, datePattern, numberPattern);
        Object dstPropertyValue = converter.convert(
            srcPropertyValue, srcProperty.type(), dstProperty.type(), actualOps
        );
        dstProperty.setValue(dst, dstPropertyValue);
        return false;
    }

    private @Nonnull Object ensureKey(
        @Nonnull Object srcKey,
        Type srcKeyType,
        Type dstKeyType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options) {
        return Objects.equals(srcKeyType, dstKeyType) ?
            srcKey
            :
            converter.convert(srcKey, srcKeyType, dstKeyType, options);
    }
}
