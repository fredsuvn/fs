package xyz.fslabo.common.mapping;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.bean.BeanProvider;
import xyz.fslabo.common.bean.PropertyInfo;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Mapping options for {@link Mapper} and {@link BeanMapper}.
 *
 * @author sunqian
 */
@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class MappingOptions {

    private static final MappingOptions DEFAULT_OPTIONS = MappingOptions.builder().build();

    /**
     * Returns default options.
     *
     * @return default options
     */
    public static MappingOptions defaultOptions() {
        return DEFAULT_OPTIONS;
    }

    /**
     * Copy level: {@code ASSIGNABLE}.
     * <p>
     * In this level, if target type is assignable from type of source object, the source object will be returned as
     * mapping result. This is similar to shadow copy.
     */
    public static final int COPY_LEVEL_ASSIGNABLE = 1;

    /**
     * Copy level: {@code EQUAL}.
     * <p>
     * In this level, if target type is equal to type of source object, the source object will be returned as
     * mapping result. This is similar to strict shadow copy.
     */
    public static final int COPY_LEVEL_EQUAL = 2;

    /**
     * Copy level: {@code DEEP}.
     * <p>
     * In this level, the mapper should always create a new instance to return as mapping result. This is similar to
     * deep copy.
     */
    public static final int COPY_LEVEL_DEEP = 3;

    /**
     * Option for {@link Mapper}, to map objects in types if needed.
     * For {@link BeanMapper}, names of properties and keys of entries will be mapped by this mapper before finding
     * dest properties or entries.
     * If this option is null, the mapper will use {@link Mapper#defaultMapper()}.
     */
    private @Nullable Mapper mapper;

    /**
     * Option for {@link BeanProvider}, to resolve bean infos if needed.
     * If this option is null, the mapper will use {@link BeanProvider#defaultProvider()}.
     */
    private @Nullable BeanProvider beanProvider;

    /**
     * Option for {@link BeanMapper}, to map bean infos if needed.
     * If this option is null, the mapper will use {@link BeanMapper#defaultMapper()}.
     */
    private @Nullable BeanMapper beanMapper;

    /**
     * Ignored names or keys when mapping properties.
     */
    private @Nullable Collection<?> ignored;

    /**
     * Mapper function for property names of bean and keys of map.
     * <p>
     * The function applies 2 arguments, first is object of name/key, second is specified type of first object, and
     * returns mapped name/key. The mapped object may be a {@link Collection}, which means a name/key maps more than one
     * bean property or map entry. If the name/key itself is a {@link Collection}, a {@link Collection} which has
     * singleton element should be returned. If the function returns null, the name/key will be ignored.
     * <p>
     * Note the {@link #getMapper()} option will still valid for the names and keys after mapping by this name mapper.
     */
    private @Nullable BiFunction<Object, Type, @Nullable Object> nameMapper;

    /**
     * Whether the null value should be ignored when mapping.
     * <p>
     * Default is false.
     */
    @Builder.Default
    private boolean ignoreNull = false;

    /**
     * Whether ignore error when mapping.
     * <p>
     * Default is false.
     */
    @Builder.Default
    private boolean ignoreError = false;

    /**
     * Whether put the value into dest map if the dest map doesn't contain the value.
     * <p>
     * Default is true.
     */
    @Builder.Default
    private boolean putNew = true;

    /**
     * Copy level option. This option determines whether a new instance must be created during the mapping process,
     * similar to shallow copy and deep copy. Here are levels:
     * <ul>
     *     <li>{@link #COPY_LEVEL_ASSIGNABLE};</li>
     *     <li>{@link #COPY_LEVEL_EQUAL};</li>
     *     <li>{@link #COPY_LEVEL_DEEP};</li>
     * </ul>
     * <p>
     * Default is {@link #COPY_LEVEL_ASSIGNABLE}.
     */
    @Builder.Default
    private int copyLevel = COPY_LEVEL_ASSIGNABLE;

    /**
     * Option to determine which charset to use for character conversion. If it is {@code null}, the mapper should use
     * {@link JieChars#defaultCharset()}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Charset charset;

    /**
     * Option to determine which format to use for number conversion.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable NumberFormat numberFormat;

    /**
     * Option to determine which format to use for date conversion.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable DateTimeFormatter dateFormat;

    /**
     * Option to determine which zone offset to use for date conversion.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable ZoneOffset zoneOffset;

    /**
     * Function to determine which charset to use for character conversion. This option is typically used in
     * {@link BeanMapper}, and if this option is {@code null}, the mapper should try to use {@link #getCharset()}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, Charset> propertyCharset;

    /**
     * Function to determine which format to use for number conversion. This option is typically used in
     * {@link BeanMapper}, and if this option is {@code null}, the mapper should try to use {@link #getNumberFormat()}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, NumberFormat> propertyNumberFormat;

    /**
     * Function to determine which format to use for date conversion. This option is typically used in
     * {@link BeanMapper}, and if this option is {@code null}, the mapper should try to use {@link #getDateFormat()}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, DateTimeFormatter> propertyDateFormat;

    /**
     * Function to determine which zone offset to use for date conversion. This option is typically used in
     * {@link BeanMapper}, and if this option is {@code null}, the mapper should try to use {@link #getDateFormat()}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, ZoneOffset> propertyZoneOffset;

    /**
     * Returns {@link Charset} option from given property info and this options. If property info is not null and
     * {@link #getPropertyCharset()} is not null, obtains result of {@link #getPropertyCharset()}. Otherwise, obtains
     * result of {@link #getCharset()}. If the result is not null, returns the result, else returns
     * {@link JieChars#UTF_8}.
     *
     * @param targetProperty given property info
     * @return {@link Charset} option
     */
    public Charset getCharset(@Nullable PropertyInfo targetProperty) {
        if (targetProperty != null) {
            Function<PropertyInfo, Charset> func = getPropertyCharset();
            if (func != null) {
                return Jie.orDefault(func.apply(targetProperty), JieChars.defaultCharset());
            }
        }
        return Jie.orDefault(getCharset(), JieChars.defaultCharset());
    }

    /**
     * Returns {@link DateTimeFormatter} option from given property info and this options, may be {@code null}. If
     * property info is not null and {@link #getPropertyDateFormat()} is not null, returns result of
     * {@link #getPropertyDateFormat()}. Otherwise, it returns {@link #getDateFormat()}.
     * <p>
     * Note the returned formatter does not include zone offset from {@link #getZoneOffset(PropertyInfo)} or
     * {@link #getZoneOffset()}. Using {@link #getDateTimeFormatterWithZone(PropertyInfo)} to get that.
     *
     * @param targetProperty given property info
     * @return {@link DateTimeFormatter} option, may be {@code null}
     * @see #getDateTimeFormatterWithZone(PropertyInfo)
     */
    @Nullable
    public DateTimeFormatter getDateTimeFormatter(@Nullable PropertyInfo targetProperty) {
        if (targetProperty != null) {
            Function<PropertyInfo, DateTimeFormatter> func = getPropertyDateFormat();
            if (func != null) {
                return func.apply(targetProperty);
            }
        }
        return getDateFormat();
    }

    /**
     * Returns {@link NumberFormat} option from given property info and this options, may be {@code null}. If property
     * info is not null and {@link #getPropertyNumberFormat()} is not null, returns result of
     * {@link #getPropertyNumberFormat()}. Otherwise, it returns {@link #getNumberFormat()}.
     *
     * @param targetProperty given property info
     * @return {@link NumberFormat} option, may be {@code null}
     */
    @Nullable
    public NumberFormat getNumberFormatter(@Nullable PropertyInfo targetProperty) {
        if (targetProperty != null) {
            Function<PropertyInfo, NumberFormat> func = getPropertyNumberFormat();
            if (func != null) {
                return func.apply(targetProperty);
            }
        }
        return getNumberFormat();
    }

    /**
     * Returns {@link ZoneOffset} option from given property info and this options. If property info is not null and
     * {@link #getPropertyZoneOffset()} is not null, obtains result of {@link #getPropertyZoneOffset()}. Otherwise, it
     * returns {@link #getZoneOffset()}.
     *
     * @param targetProperty given property info
     * @return {@link ZoneOffset} option
     */
    @Nullable
    public ZoneOffset getZoneOffset(@Nullable PropertyInfo targetProperty) {
        if (targetProperty != null) {
            Function<PropertyInfo, ZoneOffset> func = getPropertyZoneOffset();
            if (func != null) {
                return func.apply(targetProperty);
            }
        }
        return getZoneOffset();
    }

    /**
     * Returns a combined {@link DateTimeFormatter} with {@link #getDateTimeFormatter(PropertyInfo)} and
     * {@link #getZoneOffset()}, may be {@code null} if result of {@link #getDateTimeFormatter(PropertyInfo)} is
     * {@code null}. It is equivalent to:
     * <pre>
     *     DateTimeFormatter formatter = getDateTimeFormatter(targetProperty);
     *     if (formatter == null) {
     *         return null;
     *     }
     *     ZoneOffset offset = getZoneOffset(targetProperty);
     *     if (offset == null) {
     *         return formatter;
     *     }
     *     return formatter.withZone(offset);
     * </pre>
     *
     * @param targetProperty given property info
     * @return {@link DateTimeFormatter} option, may be {@code null}
     */
    @Nullable
    public DateTimeFormatter getDateTimeFormatterWithZone(@Nullable PropertyInfo targetProperty) {
        DateTimeFormatter formatter = getDateTimeFormatter(targetProperty);
        if (formatter == null) {
            return null;
        }
        ZoneOffset offset = getZoneOffset(targetProperty);
        if (offset == null) {
            return formatter;
        }
        return formatter.withZone(offset);
    }
}
