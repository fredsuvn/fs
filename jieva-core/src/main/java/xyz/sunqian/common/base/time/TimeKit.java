package xyz.sunqian.common.base.time;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.function.Supplier;

/**
 * Utilities for time and date.
 *
 * @author sunqian
 */
public class TimeKit {

    /**
     * Default format pattern: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Default date time formatter of {@link #DEFAULT_PATTERN} with the zone at {@link ZoneId#systemDefault()}.
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter
        .ofPattern(DEFAULT_PATTERN).withZone(ZoneId.systemDefault());

    /**
     * Returns a {@link DateFormat} of the specified pattern.
     *
     * @param pattern the specified pattern
     * @return a {@link DateFormat} of the specified pattern
     */
    public static @Nonnull DateFormat dateFormat(@Nonnull String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * Formats the given date with {@link #DEFAULT_PATTERN}.
     *
     * @param date the given date
     * @return the formatted string
     */
    public static @Nonnull String format(@Nonnull Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    /**
     * Formats the given date with the specified pattern.
     *
     * @param date    the given date
     * @param pattern the specified pattern
     * @return the formatted string
     */
    public static @Nonnull String format(@Nonnull Date date, @Nonnull String pattern) {
        return dateFormat(pattern).format(date);
    }

    /**
     * Parses the given date string with {@link #DEFAULT_PATTERN}.
     *
     * @param date the given date string
     * @return the parsed date
     */
    public static @Nonnull Date parse(@Nonnull String date) {
        return parse(date, DEFAULT_PATTERN);
    }

    /**
     * Parses the given date string with the specified pattern.
     *
     * @param date    the given date string
     * @param pattern the specified pattern
     * @return the parsed date
     */
    public static @Nonnull Date parse(@Nonnull String date, @Nonnull String pattern) {
        try {
            return dateFormat(pattern).parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Formats the given date with {@link #DEFAULT_PATTERN}. If the date is {@code null}, returns {@code null}.
     *
     * @param date the given date
     * @return the formatted string
     */
    public static @Nullable String toString(@Nullable Date date) {
        if (date == null) {
            return null;
        }
        return format(date);
    }

    /**
     * Formats the given date with the specified pattern. If the date or pattern is {@code null}, returns {@code null}.
     *
     * @param date    the given date
     * @param pattern the specified pattern
     * @return the formatted string
     */
    public static @Nullable String toString(@Nullable Date date, @Nullable String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return format(date, pattern);
    }

    /**
     * Parses the given date string with {@link #DEFAULT_PATTERN}. If the date string is {@code null}, returns
     * {@code null}.
     *
     * @param date the given date string
     * @return the parsed date
     */
    public static @Nullable Date toDate(@Nullable String date) {
        if (date == null) {
            return null;
        }
        return parse(date);
    }

    /**
     * Parses the given date string with the specified pattern. If the date string or pattern is {@code null}, returns
     * {@code null}.
     *
     * @param date    the given date string
     * @param pattern the specified pattern
     * @return the parsed date
     */
    public static @Nullable Date toDate(@Nullable String date, @Nullable String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return parse(date, pattern);
    }

    /**
     * Returns the current zone offset.
     *
     * @return the current zone offset
     */
    public static @Nonnull ZoneOffset zoneOffset() {
        return OffsetDateTime.now().getOffset();
    }

    /**
     * Converts the given temporal to an {@link Instant}. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal the given temporal
     * @return the {@link Instant} converted from the given temporal
     * @throws DateTimeException if the conversion fails
     */
    public static @Nonnull Instant toInstant(@Nonnull TemporalAccessor temporal) throws DateTimeException {
        return toInstant(temporal, TimeKit::zoneOffset);
    }

    /**
     * Converts the given temporal to an {@link Instant} with the specified zone offset.
     *
     * @param temporal the given temporal
     * @param offset   specified zone offset
     * @return the {@link Instant} converted from the given temporal
     * @throws DateTimeException if the conversion fails
     */
    public static @Nonnull Instant toInstant(@Nonnull TemporalAccessor temporal, @Nonnull ZoneOffset offset) {
        return toInstant(temporal, () -> offset);
    }

    private static @Nonnull Instant toInstant(
        @Nonnull TemporalAccessor temporal,
        @Nonnull Supplier<@Nonnull ZoneOffset> offset
    ) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).toInstant(offset.get());
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toInstant();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toInstant();
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN).toInstant(offset.get());
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal).toInstant(offset.get());
        }
        return Instant.from(temporal);
    }

    /**
     * Returns {@link LocalDateTime} from given temporal. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal given temporal
     * @return {@link LocalDateTime} from given temporal
     */
    @Nullable
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
        if (temporal instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) temporal, zoneOffset());
        }
        if (temporal instanceof LocalDateTime) {
            return (LocalDateTime) temporal;
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toLocalDateTime();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toLocalDateTime();
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN);
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal);
        }
        return LocalDateTime.from(temporal);
    }

    /**
     * Returns {@link LocalDateTime} from given temporal. If the given temporal lacks a zone offset, use specified zone
     * offset.
     *
     * @param temporal given temporal
     * @param offset   specified zone offset
     * @return {@link LocalDateTime} from given temporal
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporal, ZoneOffset offset) {
        if (temporal instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) temporal, offset);
        }
        if (temporal instanceof LocalDateTime) {
            return (LocalDateTime) temporal;
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toLocalDateTime();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toLocalDateTime();
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN);
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal);
        }
        return LocalDateTime.from(temporal);
    }

    /**
     * Returns {@link ZonedDateTime} from given temporal. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal given temporal
     * @return {@link ZonedDateTime} from given temporal
     */
    @Nullable
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal) {
        return toZonedDateTime(temporal, zoneOffset());
    }

    /**
     * Returns {@link ZonedDateTime} from given temporal. If the given temporal lacks a zone offset, use specified zone
     * offset.
     *
     * @param temporal given temporal
     * @param offset   specified zone offset
     * @return {@link ZonedDateTime} from given temporal
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal, ZoneOffset offset) {
        if (temporal instanceof Instant) {
            return ZonedDateTime.ofInstant((Instant) temporal, offset);
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).atZone(offset);
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).withZoneSameInstant(offset);
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).withOffsetSameInstant(offset).toZonedDateTime();
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN).atZone(offset);
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal).atZone(offset);
        }
        return ZonedDateTime.from(temporal).withZoneSameInstant(offset);
    }

    /**
     * Returns {@link OffsetDateTime} from given temporal. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal given temporal
     * @return {@link OffsetDateTime} from given temporal
     */
    @Nullable
    public static OffsetDateTime toOffsetDateTime(TemporalAccessor temporal) {
        return toOffsetDateTime(temporal, zoneOffset());
    }

    /**
     * Returns {@link OffsetDateTime} from given temporal. If the given temporal lacks a zone offset, use specified zone
     * offset.
     *
     * @param temporal given temporal
     * @param offset   specified zone offset
     * @return {@link OffsetDateTime} from given temporal
     */
    public static OffsetDateTime toOffsetDateTime(TemporalAccessor temporal, ZoneOffset offset) {
        if (temporal instanceof Instant) {
            return OffsetDateTime.ofInstant((Instant) temporal, offset);
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).atOffset(offset);
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toOffsetDateTime().withOffsetSameInstant(offset);
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).withOffsetSameInstant(offset);
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN).atOffset(offset);
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal).atOffset(offset);
        }
        return OffsetDateTime.from(temporal).withOffsetSameInstant(offset);
    }

    /**
     * Returns {@link LocalDate} from given temporal. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal given temporal
     * @return {@link LocalDate} from given temporal
     */
    @Nullable
    public static LocalDate toLocalDate(TemporalAccessor temporal) {
        return toLocalDateTime(temporal).toLocalDate();
    }

    /**
     * Returns {@link LocalDate} from given temporal. If the given temporal lacks a zone offset, use specified zone
     * offset.
     *
     * @param temporal given temporal
     * @param offset   specified zone offset
     * @return {@link LocalDate} from given temporal
     */
    public static LocalDate toLocalDate(TemporalAccessor temporal, ZoneOffset offset) {
        return toLocalDateTime(temporal, offset).toLocalDate();
    }

    /**
     * Returns {@link LocalTime} from given temporal. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal given temporal
     * @return {@link LocalTime} from given temporal
     */
    @Nullable
    public static LocalTime toLocalTime(TemporalAccessor temporal) {
        return toLocalDateTime(temporal).toLocalTime();
    }

    /**
     * Returns {@link LocalTime} from given temporal. If the given temporal lacks a zone offset, use specified zone
     * offset.
     *
     * @param temporal given temporal
     * @param offset   specified zone offset
     * @return {@link LocalTime} from given temporal
     */
    public static LocalTime toLocalTime(TemporalAccessor temporal, ZoneOffset offset) {
        return toLocalDateTime(temporal, offset).toLocalTime();
    }
}
