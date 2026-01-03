package space.sunqian.fs.base.date;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

final class DateBack {

    static @Nonnull DateFormatter ofFormatter(
        @Nonnull DateTimeFormatter formatter, @Nonnull ZoneId zoneId
    ) {
        return new OfFormatter(formatter, zoneId);
    }

    static @Nonnull DateFormatter ofPattern(
        @Nonnull String pattern, @Nonnull ZoneId zoneId
    ) throws DateTimeException {
        try {
            return new OfPattern(pattern, zoneId);
        } catch (Exception e) {
            throw new DateException(e);
        }
    }

    private static class AbsDateFormatter implements DateFormatter {

        private static final @Nonnull String NO_PATTERN = "No pattern in this TimeSpec.";

        protected final @Nonnull DateTimeFormatter formatter;
        private final @Nonnull ZoneId zoneId;

        private AbsDateFormatter(@Nonnull DateTimeFormatter formatter, @Nonnull ZoneId zoneId) {
            this.formatter = formatter;
            this.zoneId = zoneId;
        }

        @Override
        public @Nonnull ZoneId zoneId() {
            return zoneId;
        }

        @Override
        public @Nonnull String pattern() throws DateTimeException {
            throw new DateException(NO_PATTERN);
        }

        @Override
        public boolean hasPattern() {
            return false;
        }

        @Override
        public @Nonnull String format(@Nonnull Date date) throws DateTimeException {
            Instant instant = date.toInstant();
            return format(instant);
        }

        @Override
        public @Nonnull String format(@Nonnull TemporalAccessor time) throws DateTimeException {
            TemporalAccessor withZone = withZoneId(time, zoneId);
            return format0(withZone);
        }

        private @Nonnull String format0(@Nonnull TemporalAccessor time) throws DateTimeException {
            return formatter.format(time);
        }

        @Override
        public <T> @Nonnull T parse(@Nonnull CharSequence date, @Nonnull Class<T> timeType) throws DateTimeException {
            return Fs.as(parse0(date, timeType));
        }

        private @Nonnull Object parse0(
            @Nonnull CharSequence date, @Nonnull Class<?> timeType
        ) throws DateTimeException {
            if (Objects.equals(timeType, Instant.class)) {
                try {
                    return Instant.parse(date);
                } catch (DateTimeParseException e) {
                    LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
                    return ZonedDateTime.of(localDateTime, zoneId).toInstant();
                }
            }
            if (Objects.equals(timeType, Date.class)) {
                return Date.from(parse(date, Instant.class));
            }
            if (Objects.equals(timeType, LocalDateTime.class)) {
                return LocalDateTime.parse(date, formatter);
            }
            if (Objects.equals(timeType, ZonedDateTime.class)) {
                try {
                    return ZonedDateTime.parse(date, formatter);
                } catch (DateTimeParseException e) {
                    LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
                    return ZonedDateTime.of(localDateTime, zoneId);
                }
            }
            if (Objects.equals(timeType, OffsetDateTime.class)) {
                try {
                    return OffsetDateTime.parse(date, formatter);
                } catch (DateTimeParseException e) {
                    LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
                    return OffsetDateTime.of(localDateTime, DateKit.nowOffset());
                }
            }
            if (Objects.equals(timeType, LocalDate.class)) {
                return LocalDate.parse(date, formatter);
            }
            if (Objects.equals(timeType, LocalTime.class)) {
                return LocalTime.parse(date, formatter);
            }
            throw new DateException("Unsupported time type: " + timeType);
        }

        @Override
        public <T> @Nonnull T convert(@Nonnull Date date, @Nonnull Class<T> timeType) throws DateTimeException {
            if (timeType.equals(Date.class)) {
                return Fs.as(date);
            }
            return convert(date.toInstant(), timeType);
        }

        @Override
        public <T> @Nonnull T convert(
            @Nonnull TemporalAccessor time, @Nonnull Class<T> timeType
        ) throws DateTimeException {
            if (timeType.equals(time.getClass())) {
                return Fs.as(time);
            }
            TemporalAccessor withZone = withZoneId(time, zoneId);
            return Fs.as(convert0(withZone, timeType));
        }

        private @Nonnull Object convert0(
            @Nonnull TemporalAccessor time, @Nonnull Class<?> timeType
        ) throws DateTimeException {
            if (Objects.equals(timeType, Instant.class)) {
                return Instant.from(time);
            }
            if (Objects.equals(timeType, Date.class)) {
                return Date.from(Instant.from(time));
            }
            if (Objects.equals(timeType, LocalDateTime.class)) {
                return LocalDateTime.from(time);
            }
            if (Objects.equals(timeType, ZonedDateTime.class)) {
                return ZonedDateTime.from(time);
            }
            if (Objects.equals(timeType, OffsetDateTime.class)) {
                return OffsetDateTime.from(time);
            }
            if (Objects.equals(timeType, LocalDate.class)) {
                return LocalDate.from(time);
            }
            if (Objects.equals(timeType, LocalTime.class)) {
                return LocalTime.from(time);
            }
            throw new DateException("Unsupported conversion from " + time.getClass() + " to " + timeType + ".");
        }

        private @Nonnull TemporalAccessor withZoneId(@Nonnull TemporalAccessor time, @Nonnull ZoneId zoneId) {
            if (time instanceof Instant) {
                return ZonedDateTime.ofInstant((Instant) time, zoneId);
            }
            if (time instanceof LocalDateTime) {
                return ZonedDateTime.of((LocalDateTime) time, zoneId);
            }
            // if (time instanceof LocalDate) {
            //     return ZonedDateTime.of((LocalDate) time, LocalTime.MIN, zoneId);
            // }
            // if (time instanceof LocalTime) {
            //     return ZonedDateTime.of(LocalDate.MIN, (LocalTime) time, zoneId);
            // }
            return time;
        }
    }

    private static final class OfFormatter extends AbsDateFormatter {
        private OfFormatter(@Nonnull DateTimeFormatter formatter, @Nonnull ZoneId zoneId) {
            super(formatter, zoneId);
        }
    }

    private static final class OfPattern extends AbsDateFormatter {

        private final @Nonnull String pattern;

        private OfPattern(@Nonnull String pattern, @Nonnull ZoneId zoneId) throws DateTimeException {
            super(DateTimeFormatter.ofPattern(pattern), zoneId);
            this.pattern = pattern;
        }

        @Override
        public @Nonnull String pattern() {
            return pattern;
        }

        @Override
        public boolean hasPattern() {
            return true;
        }
    }

    private DateBack() {
    }
}
