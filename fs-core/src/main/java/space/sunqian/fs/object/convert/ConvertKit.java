package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.number.NumberFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumberPattern;
import space.sunqian.fs.object.meta.PropertyMetaMeta;

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
        @Nonnull DatePattern datePattern
    ) {
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
        @Nonnull String pattern,
        @Nonnull ZoneId zoneId
    ) {
        return Option.of(ConvertOption.DATE_FORMATTER, DateFormatter.ofPattern(pattern, zoneId));
    }

    /**
     * Returns a {@link Option} of {@link NumberFormatter} for the given {@link NumberPattern}. This method is based on a
     * soft-reference cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned
     * for the same pattern.
     *
     * @param numberPattern the pattern of the number formatter
     * @return the {@link Option} of {@link NumberFormatter} for the given {@link NumberPattern}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumberFormatter> getNumFormatterOption(
        @Nonnull NumberPattern numberPattern
    ) {
        return getNumFormatterOption(numberPattern.value());
    }

    /**
     * Returns a {@link Option} of {@link NumberFormatter} for the given pattern. This method is based on a
     * soft-reference cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned
     * for the same pattern.
     *
     * @param pattern the pattern of the number formatter
     * @return the {@link Option} of {@link NumberFormatter} for the given pattern
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumberFormatter> getNumFormatterOption(
        @Nonnull String pattern
    ) {
        return Option.of(ConvertOption.NUMBER_FORMATTER, NumberFormatter.ofPattern(pattern));
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
     * @param numberPattern     the number pattern
     * @return the merged options
     */
    public static @Nonnull Option<?, ?> @Nonnull [] mergeOptions(
        @Nonnull Option<?, ?> @Nonnull @RetainedParam [] defaultOptions,
        @Nullable DatePattern datePattern,
        @Nullable NumberPattern numberPattern
    ) {
        if (datePattern == null) {
            if (numberPattern == null) {
                return defaultOptions;
            } else {
                Option<ConvertOption, NumberFormatter> numFormatter = ConvertKit.getNumFormatterOption(numberPattern);
                return OptionKit.mergeOption(defaultOptions, numFormatter);
            }
        } else {
            Option<ConvertOption, DateFormatter> dateFormatter = ConvertKit.getDateFormatterOption(datePattern);
            if (numberPattern == null) {
                return OptionKit.mergeOption(defaultOptions, dateFormatter);
            } else {
                Option<ConvertOption, NumberFormatter> numFormatter = ConvertKit.getNumFormatterOption(numberPattern);
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
        @Nonnull PropertyMetaMeta srcProperty,
        @Nonnull PropertyMetaMeta dstProperty
    ) {
        A srcAnnotation = srcProperty.getAnnotation(annotationType);
        A dstAnnotation = dstProperty.getAnnotation(annotationType);
        if (dstAnnotation == null) {
            return srcAnnotation;
        } else {
            return dstAnnotation;
        }
    }

    private ConvertKit() {
    }
}
