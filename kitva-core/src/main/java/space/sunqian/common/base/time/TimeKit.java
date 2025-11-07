package space.sunqian.common.base.time;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Utilities for time and date. This class is backed by {@link TimeFormatter#defaultFormatter()}.
 *
 * @author sunqian
 */
public class TimeKit {

    /**
     * Default format pattern: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final @Nonnull String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    static final @Nonnull TimeFormatter formatter = TimeFormatter.ofPattern(DEFAULT_PATTERN);

    /**
     * Formats the given date by {@link #formatter}.
     *
     * @param date the given date to format
     * @return the formatted string
     * @throws DateTimeException if any error occurs
     */
    public static @Nonnull String format(@Nonnull Date date) throws DateTimeException {
        return formatter.format(date);
    }

    /**
     * Formats the given date by {@link #formatter}. If the given date is {@code null}, or an exception thrown during
     * formating, returns {@code null}.
     *
     * @param date the given date to format, can be {@code null}
     * @return the formatted string, or {@code null} if the given date is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    public static @Nullable String formatSafe(@Nullable Date date) throws DateTimeException {
        return formatter.formatSafe(date);
    }

    /**
     * Formats the given time object by {@link #formatter}.
     *
     * @param time the given time object to format
     * @return the formatted string
     * @throws DateTimeException if any error occurs
     */
    public static @Nonnull String format(@Nonnull TemporalAccessor time) throws DateTimeException {
        return formatter.format(time);
    }

    /**
     * Formats the given time object by {@link #formatter}. If the given time object is {@code null}, or an exception
     * thrown during formating, returns {@code null}.
     *
     * @param time the given time object to format, can be {@code null}
     * @return the formatted string, or {@code null} if the given time object is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    public static @Nullable String formatSafe(@Nullable TemporalAccessor time) throws DateTimeException {
        return formatter.formatSafe(time);
    }

    /**
     * Parses the given date string to an instance of the specified time type by {@link #formatter}.
     *
     * @param date     the given date string to parse
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the parsed time instance
     * @throws DateTimeException if any error occurs
     */
    public static <T> @Nonnull T parse(
        @Nonnull CharSequence date, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        return formatter.parse(date, timeType);
    }

    /**
     * Parses the given date string to an instance of the specified time type by {@link #formatter}. If the given date
     * string is {@code null}, or an exception thrown during parsing, returns {@code null}.
     *
     * @param date     the given date string to parse, can be {@code null}
     * @param timeType the specified time type
     * @param <T>      the time type
     * @return the parsed time instance, or {@code null} if the given date string is {@code null} or an exception thrown
     * @throws DateTimeException if any error occurs
     */
    public static <T> @Nullable T parseSafe(
        @Nullable CharSequence date, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        return formatter.parseSafe(date, timeType);
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
    public static <T> @Nonnull T convert(@Nonnull Date date, @Nonnull Class<T> timeType) throws DateTimeException {
        return formatter.convert(date, timeType);
    }

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
    public static <T> @Nullable T convertSafe(
        @Nullable Date date, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        return formatter.convertSafe(date, timeType);
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
    public static <T> @Nonnull T convert(
        @Nonnull TemporalAccessor time, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        return formatter.convert(time, timeType);
    }

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
    public static <T> @Nullable T convertSafe(
        @Nullable TemporalAccessor time, @Nonnull Class<T> timeType
    ) throws DateTimeException {
        return formatter.convertSafe(time, timeType);
    }

    /**
     * Returns the {@link ZoneOffset} at the current time, which is equivalent to:
     * {@code ZoneOffset.systemDefault().getRules().getOffset(Instant.now())}.
     *
     * @return the {@link ZoneOffset} at the current time
     */
    public static @Nonnull ZoneOffset nowOffset() {
        return ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
    }

    private TimeKit() {
    }
}
