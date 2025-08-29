package xyz.sunqian.common.base.time;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Specification for time and date, used to format and parse time and date.
 *
 * @author sunqian
 */
@Immutable
public interface TimeSpec {

    /**
     * Returns a time specification based on the given formatter.
     * <p>
     * The returned instance supports {@link Date}, {@link Instant}, {@link LocalDateTime}, {@link ZonedDateTime},
     * {@link OffsetDateTime}, {@link LocalDate} and {@link LocalTime}. But doesn't support pattern methods.
     *
     * @param formatter the given formatter
     * @return a time specification based on the given formatter
     */
    static @Nonnull TimeSpec ofFormatter(@Nonnull DateTimeFormatter formatter) {
        return TimeBack.ofFormatter(formatter);
    }

    /**
     * Returns a time specification based on the given pattern.
     * <p>
     * The returned instance supports {@link Date}, {@link Instant}, {@link LocalDateTime}, {@link ZonedDateTime},
     * {@link OffsetDateTime}, {@link LocalDate} and {@link LocalTime}. And its underlying formatter is from
     * {@link DateTimeFormatter#ofPattern(String)}.
     *
     * @param pattern the given pattern
     * @return a time specification based on the given pattern
     */
    static @Nonnull TimeSpec ofPattern(@Nonnull String pattern) {
        return TimeBack.ofPattern(pattern);
    }

    /**
     * Returns the pattern of this time specification.
     *
     * @return the pattern
     * @throws DateTimeException if the pattern is invalid
     */
    @Nonnull
    String pattern() throws DateTimeException;

    /**
     * Returns whether this time specification has a pattern.
     *
     * @return whether this time specification has a pattern
     */
    boolean hasPattern();

    /**
     * Formats the given date.
     *
     * @param date the given date to format
     * @return the formatted string
     * @throws DateTimeException if any error occurs
     */
    @Nonnull
    String format(@Nonnull Date date) throws DateTimeException;

    /**
     * Formats the given date. If the given date is {@code null}, or an exception thrown during formating, returns
     * {@code null}.
     *
     * @param date the given date to format, can be {@code null}
     * @return the formatted string, or {@code null} if the given date is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default @Nullable String formatSafe(@Nullable Date date) throws DateTimeException {
        if (date == null) {
            return null;
        }
        try {
            return format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Formats the given date with the specified zone.
     *
     * @param date   the given date to format
     * @param zoneId the specified zone
     * @return the formatted string
     * @throws DateTimeException if any error occurs
     */
    @Nonnull
    String format(@Nonnull Date date, @Nonnull ZoneId zoneId) throws DateTimeException;

    /**
     * Formats the given date with the specified zone. If the given date is {@code null}, or an exception thrown during
     * formating, returns {@code null}.
     *
     * @param date   the given date to format, can be {@code null}
     * @param zoneId the specified zone
     * @return the formatted string, or {@code null} if the given date is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default @Nullable String formatSafe(@Nullable Date date, @Nonnull ZoneId zoneId) throws DateTimeException {
        if (date == null) {
            return null;
        }
        try {
            return format(date, zoneId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Formats the given time object.
     *
     * @param time the given time object to format
     * @return the formatted string
     * @throws DateTimeException if any error occurs
     */
    @Nonnull
    String format(@Nonnull TemporalAccessor time) throws DateTimeException;

    /**
     * Formats the given time object. If the given time object is {@code null}, or an exception thrown during formating,
     * returns {@code null}.
     *
     * @param time the given time object to format, can be {@code null}
     * @return the formatted string, or {@code null} if the given time object is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default @Nullable String formatSafe(@Nullable TemporalAccessor time) throws DateTimeException {
        if (time == null) {
            return null;
        }
        try {
            return format(time);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Formats the given time object. If the given arguments cannot determine zone info, use the specified zone.
     *
     * @param time   the given time object to format
     * @param zoneId the specified zone
     * @return the formatted string
     * @throws DateTimeException if any error occurs
     */
    @Nonnull
    String format(@Nonnull TemporalAccessor time, @Nonnull ZoneId zoneId) throws DateTimeException;

    /**
     * Formats the given time object. If the given time object is {@code null}, or an exception thrown during formating,
     * returns {@code null}. If the given arguments cannot determine zone info, use the specified zone.
     *
     * @param time   the given time object to format, can be {@code null}
     * @param zoneId the specified zone
     * @return the formatted string, or {@code null} if the given time object is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default @Nullable String formatSafe(
        @Nullable TemporalAccessor time, @Nonnull ZoneId zoneId
    ) throws DateTimeException {
        if (time == null) {
            return null;
        }
        try {
            return format(time, zoneId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses the given date string to an instance of the specified time type.
     *
     * @param date     the given date string to parse
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the parsed time instance
     * @throws DateTimeException if any error occurs
     */
    <T> @Nonnull T parse(@Nonnull CharSequence date, @Nonnull Class<T> timeType) throws DateTimeException;

    /**
     * Parses the given date string to an instance of the specified time type. If the given date string is {@code null},
     * or an exception thrown during parsing, returns {@code null}.
     *
     * @param date     the given date string to parse, can be {@code null}
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the parsed time instance, or {@code null} if the given date string is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default <T> @Nullable T parseSafe(
        @Nullable CharSequence date, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        if (date == null) {
            return null;
        }
        try {
            return parse(date, timeType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts the given date to an instance of the specified time type.
     *
     * @param date     the given date to parse
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the converted time instance
     * @throws DateTimeException if any error occurs
     */
    <T> @Nonnull T convert(@Nonnull Date date, @Nonnull Class<T> timeType) throws DateTimeException;

    /**
     * Converts the given date to an instance of the specified time type. If the given date is {@code null}, or an
     * exception thrown during parsing, returns {@code null}.
     *
     * @param date     the given date to parse, can be {@code null}
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the converted time instance, or {@code null} if the given date is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default <T> @Nullable T convertSafe(@Nullable Date date, @Nonnull Class<T> timeType) throws DateTimeException {
        if (date == null) {
            return null;
        }
        try {
            return convert(date, timeType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts the given time object to an instance of the specified time type.
     *
     * @param time     the given time object to parse
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the converted time instance
     * @throws DateTimeException if any error occurs
     */
    <T> @Nonnull T convert(@Nonnull TemporalAccessor time, @Nonnull Class<T> timeType) throws DateTimeException;

    /**
     * Converts the given time object to an instance of the specified time type. If the given time object is
     * {@code null}, or an exception thrown during parsing, returns {@code null}.
     *
     * @param time     the given time object to parse, can be {@code null}
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the converted time instance, or {@code null} if the given time object is {@code null} or an exception
     * thrown
     * @throws DateTimeException if any error occurs
     */
    default <T> @Nullable T convertSafe(
        @Nullable TemporalAccessor time, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        if (time == null) {
            return null;
        }
        try {
            return convert(time, timeType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts the given date to an instance of the specified time type. If the given arguments cannot determine zone
     * info, use the specified zone.
     *
     * @param date     the given date to parse
     * @param timeType the specified time type
     * @param zoneId   the specified zone
     * @param <T>      the time type
     * @return the converted time instance
     * @throws DateTimeException if any error occurs
     */
    <T> @Nonnull T convert(
        @Nonnull Date date, @Nonnull Class<T> timeType, @Nonnull ZoneId zoneId
    ) throws DateTimeException;

    /**
     * Converts the given date to an instance of the specified time type. If the given date is {@code null}, or an
     * exception thrown during parsing, returns {@code null}. If the given arguments cannot determine zone info, use the
     * specified zone.
     *
     * @param date     the given date to parse, can be {@code null}
     * @param timeType the specified time type
     * @param zoneId   the specified zone
     * @param <T>      the time type
     * @return the converted time instance, or {@code null} if the given date is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    default <T> @Nullable T convertSafe(
        @Nullable Date date, @Nonnull Class<T> timeType, @Nonnull ZoneId zoneId
    ) throws DateTimeException {
        if (date == null) {
            return null;
        }
        try {
            return convert(date, timeType, zoneId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts the given time object to an instance of the specified time type. If the given arguments cannot determine
     * zone info, use the specified zone.
     *
     * @param time     the given time object to parse
     * @param timeType the specified time type
     * @param zoneId   the specified zone
     * @param <T>      the time type
     * @return the converted time instance
     * @throws DateTimeException if any error occurs
     */
    <T> @Nonnull T convert(
        @Nonnull TemporalAccessor time, @Nonnull Class<T> timeType, @Nonnull ZoneId zoneId
    ) throws DateTimeException;

    /**
     * Converts the given time object to an instance of the specified time type. If the given time object is
     * {@code null}, or an exception thrown during parsing, returns {@code null}. If the given arguments cannot
     * determine zone info, use the specified zone.
     *
     * @param time     the given time object to parse, can be {@code null}
     * @param timeType the specified time type
     * @param zoneId   the specified zone
     * @param <T>      the time type
     * @return the converted time instance, or {@code null} if the given time object is {@code null} or an exception
     * thrown
     * @throws DateTimeException if any error occurs
     */
    default <T> @Nullable T convertSafe(
        @Nullable TemporalAccessor time, @Nonnull Class<T> timeType, @Nonnull ZoneId zoneId
    ) throws DateTimeException {
        if (time == null) {
            return null;
        }
        try {
            return convert(time, timeType, zoneId);
        } catch (Exception e) {
            return null;
        }
    }
}
