package xyz.fslabo.common.mapper.handlers;

import lombok.EqualsAndHashCode;
import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.io.JieIO;
import xyz.fslabo.common.mapper.MapperException;
import xyz.fslabo.common.mapper.MappingOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementations of {@link TypedMapperHandler.Converter}.
 *
 * @author fredsuvn
 */
public class TypedConverters {

    /**
     * Default converter map, immutable. It supports:
     * <ul>
     *     <li>
     *         Based on {@link StringConverter}: {@link String}, {@link StringBuilder}, {@link StringBuffer},
     *         {@link CharSequence}, {@code char[]};
     *     </li>
     *     <li>
     *         Based on {@link BytesConverter}: {@code byte[]}, {@link ByteBuffer}, {@link InputStream},
     *         {@link ByteArrayInputStream};
     *     </li>
     *     <li>
     *         Based on {@link NumberConverter}: {@link Number}, {@code byte}, {@link Byte}, {@code short},
     *         {@link Short}, {@code int}, {@link Integer}, {@code long}, {@link Long}, {@code float}, {@link Float},
     *         {@code double}, {@link Double}, {@link BigDecimal}, {@link BigInteger};
     *     </li>
     *     <li>
     *         Based on {@link CharConverter}: {@code char}, {@link Character};
     *     </li>
     *     <li>
     *         Based on {@link DateConverter}: {@link Date};
     *     </li>
     *     <li>
     *         Based on {@link TemporalConverter}: {@link TemporalAccessor}, {@link Instant}, {@link LocalDateTime},
     *         {@link OffsetDateTime}, {@link ZonedDateTime}, {@link LocalDate}, {@link LocalTime};
     *     </li>
     *     <li>
     *         Based on {@link DurationConverter}: {@link Duration};
     *     </li>
     *     <li>
     *         Based on {@link BooleanConverter}: {@code boolean}, {@link Boolean};
     *     </li>
     * </ul>
     */
    @Immutable
    public static final Map<Type, TypedMapperHandler.Converter<?>> DEFAULT_CONVERTERS;

    static {
        Map<Type, TypedMapperHandler.Converter<?>> converters = new HashMap<>();
        StringConverter stringConverter = new StringConverter();
        converters.put(String.class, stringConverter);
        converters.put(StringBuilder.class, (s, t, p, o) -> new StringBuilder(stringConverter.convert(s, t, p, o)));
        converters.put(StringBuffer.class, (s, t, p, o) -> new StringBuffer(stringConverter.convert(s, t, p, o)));
        converters.put(CharSequence.class, (s, t, p, o) -> new StringBuffer(stringConverter.convert(s, t, p, o)));
        converters.put(char[].class, (s, t, p, o) -> stringConverter.convert(s, t, p, o).toCharArray());
        BytesConverter bytesConverter = new BytesConverter();
        converters.put(byte[].class, bytesConverter);
        converters.put(ByteBuffer.class, (s, t, p, o) -> ByteBuffer.wrap(bytesConverter.convert(s, t, p, o)));
        converters.put(InputStream.class, (s, t, p, o) -> new ByteArrayInputStream(bytesConverter.convert(s, t, p, o)));
        converters.put(ByteArrayInputStream.class, (s, t, p, o) -> new ByteArrayInputStream(bytesConverter.convert(s, t, p, o)));
        NumberConverter numberConverter = new NumberConverter();
        converters.put(Number.class, numberConverter);
        converters.put(byte.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).byteValue());
        converters.put(Byte.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).byteValue());
        converters.put(short.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).shortValue());
        converters.put(Short.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).shortValue());
        converters.put(int.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).intValue());
        converters.put(Integer.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).intValue());
        converters.put(long.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).longValue());
        converters.put(Long.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).longValue());
        converters.put(float.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).floatValue());
        converters.put(Float.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).floatValue());
        converters.put(double.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).doubleValue());
        converters.put(Double.class, (s, t, p, o) -> numberConverter.convert(s, t, p, o).doubleValue());
        converters.put(BigDecimal.class, (s, t, p, o) -> {
            Number number = numberConverter.convert(s, t, p, o);
            if (number instanceof BigDecimal) {
                return number;
            }
            if (number instanceof BigInteger) {
                return new BigDecimal((BigInteger) number);
            }
            if (number instanceof Integer) {
                return new BigDecimal((Integer) number);
            }
            if (number instanceof Long) {
                return new BigDecimal((Long) number);
            }
            if (number instanceof Double) {
                return BigDecimal.valueOf((Double) number);
            }
            return new BigDecimal(number.toString());
        });
        converters.put(BigInteger.class, (s, t, p, o) -> {
            Number number = numberConverter.convert(s, t, p, o);
            if (number instanceof BigInteger) {
                return number;
            }
            if (number instanceof BigDecimal) {
                return ((BigDecimal) number).toBigInteger();
            }
            if (number instanceof Long) {
                return BigInteger.valueOf((Long) number);
            }
            return new BigInteger(number.toString());
        });
        CharConverter charConverter = new CharConverter();
        converters.put(char.class, charConverter);
        converters.put(Character.class, charConverter);
        DEFAULT_CONVERTERS = Collections.unmodifiableMap(converters);
        DateConverter dateConverter = new DateConverter();
        converters.put(Date.class, dateConverter);
        TemporalConverter temporalConverter = new TemporalConverter();
        converters.put(TemporalAccessor.class, temporalConverter);
        converters.put(Instant.class, (s, t, p, o) -> TemporalConverter.toInstant(temporalConverter.convert(s, t, p, o)));
        converters.put(LocalDateTime.class, (s, t, p, o) -> TemporalConverter.toLocalDateTime(temporalConverter.convert(s, t, p, o)));
        converters.put(OffsetDateTime.class, (s, t, p, o) -> TemporalConverter.toOffsetDateTime(temporalConverter.convert(s, t, p, o)));
        converters.put(ZonedDateTime.class, (s, t, p, o) -> TemporalConverter.toZonedDateTime(temporalConverter.convert(s, t, p, o)));
        converters.put(LocalDate.class, (s, t, p, o) -> TemporalConverter.toLocalDate(temporalConverter.convert(s, t, p, o)));
        converters.put(LocalTime.class, (s, t, p, o) -> TemporalConverter.toLocalTime(temporalConverter.convert(s, t, p, o)));
        DurationConverter durationConverter = new DurationConverter();
        converters.put(Duration.class, durationConverter);
        BooleanConverter booleanConverter = new BooleanConverter();
        converters.put(boolean.class, booleanConverter);
        converters.put(Boolean.class, booleanConverter);
    }

    @Nullable
    private static DateTimeFormatter getDateTimeFormatter(@Nullable PropertyInfo targetProperty, MappingOptions options) {
        if (targetProperty == null) {
            return options.getDateFormat();
        }
        Function<PropertyInfo, DateTimeFormatter> func = options.getPropertyDateFormat();
        if (func == null) {
            return options.getDateFormat();
        }
        DateTimeFormatter formatter = func.apply(targetProperty);
        if (formatter != null) {
            return formatter;
        }
        return options.getDateFormat();
    }

    @Nullable
    private static NumberFormat getNumberFormatter(@Nullable PropertyInfo targetProperty, MappingOptions options) {
        if (targetProperty == null) {
            return options.getNumberFormat();
        }
        Function<PropertyInfo, NumberFormat> func = options.getPropertyNumberFormat();
        if (func == null) {
            return options.getNumberFormat();
        }
        NumberFormat formatter = func.apply(targetProperty);
        if (formatter != null) {
            return formatter;
        }
        return options.getNumberFormat();
    }

    @Nullable
    private static Charset getCharset(@Nullable PropertyInfo targetProperty, MappingOptions options) {
        if (targetProperty == null) {
            return options.getCharset();
        }
        Function<PropertyInfo, Charset> func = options.getPropertyCharset();
        if (func == null) {
            return options.getCharset();
        }
        Charset formatter = func.apply(targetProperty);
        if (formatter != null) {
            return formatter;
        }
        return options.getCharset();
    }

    private static long getMillis(TemporalAccessor temporal) {
        return temporal.getLong(ChronoField.EPOCH_DAY) * 24L * 60L * 60L * 1000L
            + temporal.getLong(ChronoField.SECOND_OF_DAY) * 1000L
            + temporal.getLong(ChronoField.NANO_OF_SECOND) / 1000000L;
    }

    /**
     * String converter.
     * <p>
     * It supports map subtypes of {@link Date}, {@link Number}, {@code byte[]} and
     * {@link ByteBuffer} to {@link String}, or call {@link Object#toString()} for other source types.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         For {@link ByteBuffer}, this converter use {@link JieIO#readReset(ByteBuffer)} to read and reset;
     *     </li>
     *     <li>
     *         If charset option is not found, use {@link JieChars#UTF_8};
     *     </li>
     * </ul>
     */
    public static class StringConverter implements TypedMapperHandler.Converter<String> {

        @Override
        public @Nullable String convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Date) {
                DateTimeFormatter dateTimeFormatter = getDateTimeFormatter(targetProperty, options);
                if (dateTimeFormatter != null) {
                    return dateTimeFormatter.format(((Date) source).toInstant());
                }
                return source.toString();
            }
            if (source instanceof TemporalAccessor) {
                DateTimeFormatter dateTimeFormatter = getDateTimeFormatter(targetProperty, options);
                if (dateTimeFormatter != null) {
                    return dateTimeFormatter.format((TemporalAccessor) source);
                }
                return source.toString();
            }
            if (source instanceof Number) {
                NumberFormat numberFormat = getNumberFormatter(targetProperty, options);
                if (numberFormat != null) {
                    return numberFormat.format(source);
                }
                return source.toString();
            }
            if (source instanceof byte[]) {
                Charset charset = getCharset(targetProperty, options);
                if (charset != null) {
                    return new String((byte[]) source, charset);
                }
                return new String((byte[]) source, JieChars.UTF_8);
            }
            if (source instanceof ByteBuffer) {
                Charset charset = getCharset(targetProperty, options);
                byte[] bytes = JieIO.readReset((ByteBuffer) source);
                if (charset != null) {
                    return new String(bytes, charset);
                }
                return new String(bytes, JieChars.UTF_8);
            }
            return source.toString();
        }
    }

    /**
     * Bytes converter.
     * <p>
     * It supports map subtypes of {@link CharSequence}, {@code byte[]}, {@link ByteBuffer} and
     * {@link InputStream} to {@link String}, or return {@code null} for other source types.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         For {@link ByteBuffer}, this converter use {@link JieIO#readReset(ByteBuffer)} to read and reset, but no
     *         reset for {@link InputStream};
     *     </li>
     *     <li>
     *         If charset option is not found, use {@link JieChars#UTF_8};
     *     </li>
     * </ul>
     */
    public static class BytesConverter implements TypedMapperHandler.Converter<byte[]> {

        @Override
        public @Nullable byte[] convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof CharSequence) {
                Charset charset = getCharset(targetProperty, options);
                if (charset != null) {
                    return source.toString().getBytes(charset);
                }
                return source.toString().getBytes(JieChars.UTF_8);
            }
            if (source instanceof byte[]) {
                return ((byte[]) source).clone();
            }
            if (source instanceof ByteBuffer) {
                return JieIO.readReset((ByteBuffer) source);
            }
            if (source instanceof InputStream) {
                return JieIO.read((InputStream) source);
            }
            return null;
        }
    }

    /**
     * Number converter.
     * <p>
     * It supports map subtypes of {@link Number}, {@link CharSequence}, {@code byte[]}, {@link ByteBuffer},
     * {@link Character}, {@link Date}, {@link TemporalAccessor} to {@link Number}, or return {@code null} for other
     * source types.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         For {@link ByteBuffer}, this converter use {@link JieIO#readReset(ByteBuffer)} to read and reset;
     *     </li>
     *     <li>
     *         For {@link Date} and {@link TemporalAccessor}, this converter map them as milliseconds;
     *     </li>
     * </ul>
     */
    public static class NumberConverter implements TypedMapperHandler.Converter<Number> {

        @Override
        public @Nullable Number convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Number) {
                return (Number) source;
            }
            if (source instanceof CharSequence) {
                return new StringNumber((CharSequence) source);
            }
            if (source instanceof byte[]) {
                return new BigInteger((byte[]) source);
            }
            if (source instanceof ByteBuffer) {
                return new BigInteger(JieIO.readReset((ByteBuffer) source));
            }
            if (source instanceof Character) {
                char c = (Character) source;
                return (int) c;
            }
            if (source instanceof Date) {
                return ((Date) source).getTime();
            }
            if (source instanceof TemporalAccessor) {
                TemporalAccessor temporal = (TemporalAccessor) source;
                return temporal.getLong(ChronoField.EPOCH_DAY) * 24L * 60L * 60L * 1000L
                    + temporal.getLong(ChronoField.SECOND_OF_DAY) * 1000L
                    + temporal.getLong(ChronoField.NANO_OF_SECOND) / 1000000L;
            }
            return null;
        }

        @EqualsAndHashCode(callSuper = false)
        private static final class StringNumber extends Number {

            private final CharSequence string;

            private StringNumber(CharSequence string) {
                this.string = string;
            }

            @Override
            public int intValue() {
                return Integer.parseInt(string.toString());
            }

            @Override
            public long longValue() {
                return Long.parseLong(string.toString());
            }

            @Override
            public float floatValue() {
                return Float.parseFloat(string.toString());
            }

            @Override
            public double doubleValue() {
                return Double.parseDouble(string.toString());
            }

            @Override
            public String toString() {
                return string.toString();
            }
        }
    }

    /**
     * Number converter.
     * <p>
     * It supports map subtypes of {@link Character}, {@link Number} to {@link Character}, or return {@code null} for
     * other source types.
     */
    public static class CharConverter implements TypedMapperHandler.Converter<Character> {

        @Override
        public @Nullable Character convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Character) {
                return (Character) source;
            }
            if (source instanceof Number) {
                return (char) ((Number) source).intValue();
            }
            return null;
        }
    }

    /**
     * Date converter.
     * <p>
     * It supports map subtypes of {@link Date}, {@link TemporalAccessor}, {@code long}, {@link Long},
     * {@link CharSequence} to {@link Date}, or return {@code null} for other source types.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         For mapping from {@code long}/{@link Long} to {@link Date}, the long value will be considered as
     *         milliseconds;
     *     </li>
     * </ul>
     */
    public static class DateConverter implements TypedMapperHandler.Converter<Date> {

        @Override
        public @Nullable Date convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Date) {
                return new Date(((Date) source).getTime());
            }
            if (source instanceof TemporalAccessor) {
                return new Date(getMillis((TemporalAccessor) source));
            }
            if (source instanceof Long) {
                return new Date((Long) source);
            }
            if (source instanceof CharSequence) {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    try {
                        return DateFormat.getInstance().parse(source.toString());
                    } catch (ParseException e) {
                        throw new MapperException(e);
                    }
                }
                return Date.from(Instant.from(formatter.parse(source.toString())));
            }
            return null;
        }
    }

    /**
     * Temporal object converter.
     * <p>
     * It supports map subtypes of {@link Date}, {@link TemporalAccessor}, {@code long}, {@link Long},
     * {@link CharSequence} to {@link TemporalAccessor}, or return {@code null} for other source types.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         For mapping from {@code long}/{@link Long} to {@link TemporalAccessor}, the long value will be
     *         considered as milliseconds;
     *     </li>
     * </ul>
     */
    public static class TemporalConverter implements TypedMapperHandler.Converter<TemporalAccessor> {

        /**
         * Maps given temporal object to {@link Instant}.
         *
         * @param temporal given temporal object
         * @return mapped {@link Instant}
         */
        public static Instant toInstant(TemporalAccessor temporal) {
            if (temporal instanceof StringTemporal) {
                return ((StringTemporal) temporal).toInstant();
            }
            return Instant.from(temporal);
        }

        /**
         * Maps given temporal object to {@link LocalDateTime}.
         *
         * @param temporal given temporal object
         * @return mapped {@link LocalDateTime}
         */
        public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
            if (temporal instanceof StringTemporal) {
                return ((StringTemporal) temporal).toLocalDateTime();
            }
            return LocalDateTime.from(temporal);
        }

        /**
         * Maps given temporal object to {@link OffsetDateTime}.
         *
         * @param temporal given temporal object
         * @return mapped {@link OffsetDateTime}
         */
        public static OffsetDateTime toOffsetDateTime(TemporalAccessor temporal) {
            if (temporal instanceof StringTemporal) {
                return ((StringTemporal) temporal).toOffsetDateTime();
            }
            return OffsetDateTime.from(temporal);
        }

        /**
         * Maps given temporal object to {@link ZonedDateTime}.
         *
         * @param temporal given temporal object
         * @return mapped {@link ZonedDateTime}
         */
        public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal) {
            if (temporal instanceof StringTemporal) {
                return ((StringTemporal) temporal).toZonedDateTime();
            }
            return ZonedDateTime.from(temporal);
        }

        /**
         * Maps given temporal object to {@link LocalDate}.
         *
         * @param temporal given temporal object
         * @return mapped {@link LocalDate}
         */
        public static LocalDate toLocalDate(TemporalAccessor temporal) {
            if (temporal instanceof StringTemporal) {
                return ((StringTemporal) temporal).toLocalDate();
            }
            return LocalDate.from(temporal);
        }

        /**
         * Maps given temporal object to {@link LocalTime}.
         *
         * @param temporal given temporal object
         * @return mapped {@link LocalTime}
         */
        public static LocalTime toLocalTime(TemporalAccessor temporal) {
            if (temporal instanceof StringTemporal) {
                return ((StringTemporal) temporal).toLocalTime();
            }
            return LocalTime.from(temporal);
        }

        @Override
        public @Nullable TemporalAccessor convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Date) {
                return ((Date) source).toInstant();
            }
            if (source instanceof TemporalAccessor) {
                return (TemporalAccessor) source;
            }
            if (source instanceof Long) {
                return Instant.ofEpochMilli((Long) source);
            }
            if (source instanceof CharSequence) {
                return new StringTemporal((CharSequence) source, targetProperty, options);
            }
            return null;
        }

        @EqualsAndHashCode(callSuper = false)
        private static final class StringTemporal implements TemporalAccessor {

            private final CharSequence string;
            private final @Nullable PropertyInfo targetProperty;
            private final MappingOptions options;

            private StringTemporal(CharSequence string, @Nullable PropertyInfo targetProperty, MappingOptions options) {
                this.string = string;
                this.targetProperty = targetProperty;
                this.options = options;
            }

            @Override
            public boolean isSupported(TemporalField field) {
                return false;
            }

            @Override
            public long getLong(TemporalField field) {
                return 0;
            }

            @Override
            public String toString() {
                return string.toString();
            }

            public Instant toInstant() {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    return Instant.parse(string);
                }
                return Instant.from(formatter.parse(string));
            }

            public LocalDateTime toLocalDateTime() {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    return LocalDateTime.parse(string);
                }
                return LocalDateTime.parse(string, formatter);
            }

            public OffsetDateTime toOffsetDateTime() {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    return OffsetDateTime.parse(string);
                }
                return OffsetDateTime.parse(string, formatter);
            }

            public ZonedDateTime toZonedDateTime() {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    return ZonedDateTime.parse(string);
                }
                return ZonedDateTime.parse(string, formatter);
            }

            public LocalDate toLocalDate() {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    return LocalDate.parse(string);
                }
                return LocalDate.parse(string, formatter);
            }

            public LocalTime toLocalTime() {
                DateTimeFormatter formatter = getDateTimeFormatter(targetProperty, options);
                if (formatter == null) {
                    return LocalTime.parse(string);
                }
                return LocalTime.parse(string, formatter);
            }
        }
    }

    /**
     * Duration converter.
     * <p>
     * It supports map subtypes of {@link Duration}, {@link CharSequence} to {@link Duration}, or return {@code null}
     * for other source types.
     */
    public static class DurationConverter implements TypedMapperHandler.Converter<Duration> {

        @Override
        public @Nullable Duration convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Duration) {
                return (Duration) source;
            }
            if (source instanceof CharSequence) {
                return Duration.parse((CharSequence) source);
            }
            return null;
        }
    }

    /**
     * Duration converter.
     * <p>
     * It supports map subtypes of {@code boolean}, {@link Boolean}, {@link CharSequence} to {@link Number}, or return
     * {@code null} for other source types.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         Using {@link Boolean#valueOf(String)} to map from {@link CharSequence} to bool,
     *     </li>
     *     <li>
     *         If source object is subtypes of {@link Number}, {@code 0} is false and others true.
     *     </li>
     * </ul>
     */
    public static class BooleanConverter implements TypedMapperHandler.Converter<Boolean> {

        @Override
        public @Nullable Boolean convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {
            if (source instanceof Boolean) {
                return (Boolean) source;
            }
            if (source instanceof CharSequence) {
                return Boolean.valueOf(source.toString());
            }
            if (source instanceof Number) {
                return ((Number) source).intValue() != 0;
            }
            return null;
        }
    }
}