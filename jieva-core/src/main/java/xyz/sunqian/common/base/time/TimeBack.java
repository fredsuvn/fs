package xyz.sunqian.common.base.time;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

final class TimeBack {

    static @Nonnull TimeSpec ofFormatter(@Nonnull DateTimeFormatter formatter) {
        return new OfFormatter(formatter);
    }

    static @Nonnull TimeSpec ofPattern(@Nonnull String pattern) {
        return new OfPattern(pattern);
    }

    private static class AbsTimeSpec implements TimeSpec {

        private static final @Nonnull String NO_PATTERN = "No pattern in this TimeSpec.";

        protected final @Nonnull DateTimeFormatter formatter;

        private AbsTimeSpec(@Nonnull DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public @Nonnull String pattern() throws DateTimeException {
            throw new TimeException(NO_PATTERN);
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
            return formatter.format(time);
        }

        @Override
        public <T> @Nonnull T parse(@Nonnull CharSequence date, @Nonnull Class<T> timeType) throws DateTimeException {
            return Jie.as(parse0(date, timeType));
        }

        private @Nonnull Object parse0(
            @Nonnull CharSequence date, @Nonnull Class<?> timeType
        ) throws DateTimeException {
            if (Objects.equals(timeType, Instant.class)) {
                return Instant.parse(date);
            }
            if (Objects.equals(timeType, Date.class)) {
                return Date.from(Instant.parse(date));
            }
            if (Objects.equals(timeType, LocalDateTime.class)) {
                return LocalDateTime.parse(date, formatter);
            }
            if (Objects.equals(timeType, ZonedDateTime.class)) {
                return ZonedDateTime.parse(date, formatter);
            }
            if (Objects.equals(timeType, OffsetDateTime.class)) {
                return OffsetDateTime.parse(date, formatter);
            }
            if (Objects.equals(timeType, LocalDate.class)) {
                return LocalDate.parse(date, formatter);
            }
            if (Objects.equals(timeType, LocalTime.class)) {
                return LocalTime.parse(date, formatter);
            }
            throw new TimeException("Unsupported time type: " + timeType);
        }

        @Override
        public <T> @Nonnull T convert(@Nonnull Object time, @Nonnull Class<T> timeType) throws DateTimeException {
            if (time instanceof CharSequence) {
                return parse((CharSequence) time, timeType);
            }
            TemporalAccessor src;
            if (time instanceof TemporalAccessor) {
                src = (TemporalAccessor) time;
            } else if (time instanceof Date) {
                src = ((Date) time).toInstant();
            } else {
                throw new TimeException("Unsupported time type: " + time.getClass());
            }
            return Jie.as(convert0(src, timeType));
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
            throw new TimeException("Unsupported conversion from " + time.getClass() + " to " + timeType + ".");
        }
    }

    private static final class OfFormatter extends AbsTimeSpec {

        private OfFormatter(@Nonnull DateTimeFormatter formatter) {
            super(formatter);
        }
    }

    private static final class OfPattern extends AbsTimeSpec {

        private final @Nonnull String pattern;

        private OfPattern(@Nonnull String pattern) {
            super(DateTimeFormatter.ofPattern(pattern));
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
}
