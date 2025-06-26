package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * Date utilities.
 *
 * @author fredsuvn
 */
public class JieDate {

    /**
     * Default date pattern: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Default date formatter of {@link #DEFAULT_PATTERN} with zone at {@link ZoneId#systemDefault()}.
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter
        .ofPattern(DEFAULT_PATTERN).withZone(ZoneId.systemDefault());

    /**
     * Returns {@link DateFormat} of given pattern.
     *
     * @param pattern given pattern
     * @return {@link DateFormat} of given pattern
     */
    public static DateFormat dateFormat(CharSequence pattern) {
        return new SimpleDateFormat(pattern.toString());
    }

    /**
     * Formats given date with {@link #DEFAULT_PATTERN}.
     *
     * @param date given date
     * @return formatted string
     */
    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    /**
     * Formats given date with given pattern.
     *
     * @param date    given date
     * @param pattern given pattern
     * @return formatted string
     */
    public static String format(Date date, CharSequence pattern) {
        return dateFormat(pattern).format(date);
    }

    /**
     * Parses given date string with {@link #DEFAULT_PATTERN}.
     *
     * @param date given date string
     * @return parsed date
     */
    public static Date parse(CharSequence date) {
        return parse(date, DEFAULT_PATTERN);
    }

    /**
     * Parses given date string with given pattern.
     *
     * @param date    given date string
     * @param pattern given pattern
     * @return parsed date
     */
    public static Date parse(CharSequence date, CharSequence pattern) {
        try {
            return dateFormat(pattern).parse(date.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Formats given date with {@link #DEFAULT_PATTERN}. If given date is null, return null.
     *
     * @param date given date
     * @return formatted string or null
     */
    @Nullable
    public static String toString(@Nullable Date date) {
        return toString(date, DEFAULT_PATTERN);
    }

    /**
     * Formats given date with given pattern. If given date or pattern is null or empty, return null.
     *
     * @param date    given date
     * @param pattern given pattern
     * @return formatted string or null
     */
    @Nullable
    public static String toString(@Nullable Date date, @Nullable CharSequence pattern) {
        if (date == null || JieString.isEmpty(pattern)) {
            return null;
        }
        return format(date, pattern);
    }

    /**
     * Parses given date string with {@link #DEFAULT_PATTERN}. If given date string is null, return null.
     *
     * @param date given date string
     * @return parsed date or null
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date) {
        return toDate(date, DEFAULT_PATTERN);
    }

    /**
     * Parses given date string with given pattern. If given date string or pattern is null or empty, return null.
     *
     * @param date    given date string
     * @param pattern given pattern
     * @return parsed date or null
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date, @Nullable CharSequence pattern) {
        if (JieString.isEmpty(date) || JieString.isEmpty(pattern)) {
            return null;
        }
        return parse(date, pattern);
    }

    /**
     * Returns current zone offset.
     *
     * @return current zone offset
     */
    public static ZoneOffset zoneOffset() {
        ZoneId.systemDefault().getRules().isFixedOffset();
        return OffsetDateTime.now().getOffset();
    }

    /**
     * Returns zone offset from given zone id.
     *
     * @param zoneId given zone id
     * @return zone offset from given zone id
     */
    public static ZoneOffset toZoneOffset(ZoneId zoneId) {
        return zoneId.getRules().getOffset(Instant.now());
    }

    /**
     * Returns {@link Instant} from given temporal. If the given temporal lacks a zone offset, use
     * {@link #zoneOffset()}.
     *
     * @param temporal given temporal
     * @return {@link Instant} from given temporal
     */
    @Nullable
    public static Instant toInstant(TemporalAccessor temporal) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).toInstant(zoneOffset());
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toInstant();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toInstant();
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN).toInstant(zoneOffset());
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal).toInstant(zoneOffset());
        }
        return Instant.from(temporal);
    }

    /**
     * Returns {@link Instant} from given temporal. If the given temporal lacks a zone offset, use specified zone
     * offset.
     *
     * @param temporal given temporal
     * @param offset   specified zone offset
     * @return {@link Instant} from given temporal
     */
    public static Instant toInstant(TemporalAccessor temporal, ZoneOffset offset) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).toInstant(offset);
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toInstant();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toInstant();
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN).toInstant(offset);
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal).toInstant(offset);
        }
        return LocalDateTime.from(temporal).toInstant(offset);
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
