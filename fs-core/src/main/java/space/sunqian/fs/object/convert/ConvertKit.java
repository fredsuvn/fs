package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.number.NumFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.base.value.SimpleKey;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.schema.ObjectProperty;

import java.lang.annotation.Annotation;
import java.time.ZoneId;

/**
 * Utilities for object conversion.
 *
 * @author sunqian
 */
public class ConvertKit {

    /**
     * Returns a {@link Option} of {@link DateFormatter} for the given {@link DatePattern}. This method is based on a
     * soft-reference cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned
     * for the same pattern and zone id.
     *
     * @param datePattern the pattern of the date formatter
     * @return the {@link Option} of {@link DateFormatter} for the given {@link DatePattern}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter> getDateFormatterOption(
        @Nonnull DatePattern datePattern) {
        ZoneId zoneId;
        if ("".equals(datePattern.zoneId())) {
            zoneId = ZoneId.systemDefault();
        } else {
            zoneId = ZoneId.of(datePattern.zoneId());
        }
        return getDateFormatterOption(datePattern.value(), zoneId);
    }

    /**
     * Returns a {@link Option} of {@link DateFormatter} for the given pattern and zone id. This method is based on a
     * soft-reference cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned
     * for the same pattern and zone id.
     *
     * @param pattern the pattern of the date formatter
     * @param zoneId  the zone id of the date formatter
     * @return the {@link Option} of {@link DateFormatter} for the given pattern and zone id
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter> getDateFormatterOption(
        @Nonnull String pattern, @Nonnull ZoneId zoneId) {
        return DateFormatterCache.INST.get(pattern, zoneId);
    }

    /**
     * Returns a {@link Option} of {@link NumFormatter} for the given {@link NumPattern}. This method is based on a
     * soft-reference cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned
     * for the same pattern.
     *
     * @param numPattern the pattern of the number formatter
     * @return the {@link Option} of {@link NumFormatter} for the given {@link NumPattern}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter> getNumFormatterOption(
        @Nonnull NumPattern numPattern) {
        return getNumFormatterOption(numPattern.value());
    }

    /**
     * Returns a {@link Option} of {@link NumFormatter} for the given pattern. This method is based on a soft-reference
     * cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned for the same
     * pattern.
     *
     * @param pattern the pattern of the number formatter
     * @return the {@link Option} of {@link NumFormatter} for the given pattern
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter> getNumFormatterOption(
        @Nonnull String pattern) {
        return NumFormatterCache.INST.get(pattern);
    }

    /**
     * Merges the default options with the date formatter and number formatter if they are not null.
     * <p>
     * If both date pattern and number pattern are {@code null}, the default options are returned. If the date pattern
     * is {@code null}, the number formatter is merged with the default options in a new array. If the number pattern is
     * {@code null}, the date formatter is merged with the default options in a new array. If both date pattern and
     * number pattern are not {@code null}, the date formatter and number formatter are merged with the default options
     * in a new array.
     *
     * @param defaultOptions the default options
     * @param datePattern    the date pattern
     * @param numPattern     the number pattern
     * @return the merged options
     */
    public static @Nonnull Option<?, ?> @Nonnull [] mergeOptions(
        @Nonnull Option<?, ?> @Nonnull @RetainedParam [] defaultOptions,
        @Nullable DatePattern datePattern,
        @Nullable NumPattern numPattern
    ) {
        if (datePattern == null) {
            if (numPattern == null) {
                return defaultOptions;
            } else {
                Option<ConvertOption, NumFormatter> numFormatter = ConvertKit.getNumFormatterOption(numPattern);
                return OptionKit.mergeOption(defaultOptions, numFormatter);
            }
        } else {
            Option<ConvertOption, DateFormatter> dateFormatter = ConvertKit.getDateFormatterOption(datePattern);
            if (numPattern == null) {
                return OptionKit.mergeOption(defaultOptions, dateFormatter);
            } else {
                Option<ConvertOption, NumFormatter> numFormatter = ConvertKit.getNumFormatterOption(numPattern);
                return OptionKit.mergeOptions(defaultOptions, dateFormatter, numFormatter);
            }
        }
    }

    /**
     * Returns the annotation for the given type from the source property if it exists, otherwise from the destination
     * property.
     *
     * @param annotationType the type of the annotation
     * @param srcProperty    the source property
     * @param dstProperty    the destination property
     * @param <A>            the type of the annotation
     * @return the annotation for the given type from the source property if it exists, otherwise from the destination
     * property
     */
    public static <A extends Annotation> @Nullable A getAnnotation(
        @Nonnull Class<A> annotationType,
        @Nonnull ObjectProperty srcProperty,
        @Nonnull ObjectProperty dstProperty
    ) {
        A srcAnnotation = srcProperty.getAnnotation(annotationType);
        A dstAnnotation = dstProperty.getAnnotation(annotationType);
        if (dstAnnotation == null) {
            return srcAnnotation;
        } else {
            return dstAnnotation;
        }
    }

    private enum DateFormatterCache {
        INST;

        private final @Nonnull SimpleCache<
            @Nonnull SimpleKey,
            @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter>
            > cache = SimpleCache.ofSoft();

        public @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter> get(
            @Nonnull String pattern, @Nonnull ZoneId zoneId
        ) {
            return cache.get(
                SimpleKey.of(pattern, zoneId),
                k -> ConvertOption.dateFormatter(DateFormatter.ofPattern(k.getAs(0), k.getAs(1))
                )
            );
        }
    }

    private enum NumFormatterCache {
        INST;

        private final @Nonnull SimpleCache<
            @Nonnull String,
            @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter>
            > cache = SimpleCache.ofSoft();

        public @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter> get(@Nonnull String pattern) {
            return cache.get(
                pattern,
                p -> ConvertOption.numFormatter(NumFormatter.ofPattern(p))
            );
        }
    }

    private ConvertKit() {
    }
}
