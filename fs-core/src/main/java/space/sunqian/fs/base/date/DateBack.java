package space.sunqian.fs.base.date;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.SimpleKey2;
import space.sunqian.fs.cache.SimpleCache;

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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;

final class DateBack {

    static @Nonnull DateFormatter getFormatter(
        @Nonnull SimpleKey2 key,
        @Nonnull Function<@Nonnull SimpleKey2, @Nonnull DateFormatter> function
    ) {
        return Cache.get(key, function);
    }

    static @Nonnull DateFormatter newFormatter(
        @Nonnull DateTimeFormatter formatter
    ) {
        return new OfFormatter(formatter);
    }

    static @Nonnull DateFormatter newFormatter(
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

        // private static final @Nonnull Map<@Nonnull Class<?>, @Nonnull DateParser> PARSER_MAP;
        // private static final @Nonnull Map<@Nonnull Class<?>, @Nonnull DateConverter> CONVERTER_MAP;

        protected final @Nonnull DateTimeFormatter formatter;
        private final @Nonnull ZoneId zoneId;
        private final @Nonnull ThreadLocal<DateFormat> format =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern());
                sdf.setTimeZone(TimeZone.getTimeZone(zoneId()));
                return sdf;
            });

        private AbsDateFormatter(@Nonnull DateTimeFormatter formatter, @Nullable ZoneId zoneId) {
            this.formatter = formatter;
            this.zoneId = Fs.nonnull(zoneId, ZoneId.systemDefault());
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
            if (hasPattern()) {
                return format.get().format(date);
            }
            Instant instant = date.toInstant();
            return format0(instant);
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
        public <T> @Nonnull T parse(@Nonnull CharSequence dateString, @Nonnull Class<T> timeType) throws DateTimeException {
            return Fs.as(parse0(dateString, timeType));
        }

        private @Nonnull Object parse0(
            @Nonnull CharSequence cs, @Nonnull Class<?> timeType
        ) throws DateTimeException {
            // DateParser parser = PARSER_MAP.get(timeType);
            // if (parser != null) {
            //     return parser.parse(cs, timeType, this);
            // }
            if (Instant.class.equals(timeType)) {
                try {
                    return Instant.parse(cs);
                } catch (DateTimeParseException e) {
                    LocalDateTime localDateTime = LocalDateTime.parse(cs, formatter);
                    return ZonedDateTime.of(localDateTime, zoneId).toInstant();
                }
            }
            if (Date.class.equals(timeType)) {
                if (hasPattern()) {
                    try {
                        return format.get().parse(cs.toString());
                    } catch (ParseException e) {
                        throw new DateTimeException(e.getMessage(), e);
                    }
                }
                return Date.from(parse(cs, Instant.class));
            }
            if (LocalDateTime.class.equals(timeType)) {
                return LocalDateTime.parse(cs, formatter);
            }
            if (ZonedDateTime.class.equals(timeType)) {
                try {
                    return ZonedDateTime.parse(cs, formatter);
                } catch (DateTimeParseException e) {
                    LocalDateTime localDateTime = LocalDateTime.parse(cs, formatter);
                    return ZonedDateTime.of(localDateTime, zoneId);
                }
            }
            if (OffsetDateTime.class.equals(timeType)) {
                try {
                    return OffsetDateTime.parse(cs, formatter);
                } catch (DateTimeParseException e) {
                    LocalDateTime localDateTime = LocalDateTime.parse(cs, formatter);
                    return OffsetDateTime.of(localDateTime, DateKit.nowOffset());
                }
            }
            if (LocalDate.class.equals(timeType)) {
                return LocalDate.parse(cs, formatter);
            }
            if (LocalTime.class.equals(timeType)) {
                return LocalTime.parse(cs, formatter);
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
            @Nonnull TemporalAccessor ta, @Nonnull Class<?> timeType
        ) throws DateTimeException {
            // DateConverter converter = CONVERTER_MAP.get(timeType);
            // if (converter != null) {
            //     return converter.convert(time, timeType, this);
            // }
            if (Instant.class.equals(timeType)) {
                return Instant.from(ta);
            }
            if (Date.class.equals(timeType)) {
                return Date.from(Instant.from(ta));
            }
            if (LocalDateTime.class.equals(timeType)) {
                return LocalDateTime.from(ta);
            }
            if (ZonedDateTime.class.equals(timeType)) {
                return ZonedDateTime.from(ta);
            }
            if (OffsetDateTime.class.equals(timeType)) {
                return OffsetDateTime.from(ta);
            }
            if (LocalDate.class.equals(timeType)) {
                return LocalDate.from(ta);
            }
            if (LocalTime.class.equals(timeType)) {
                return LocalTime.from(ta);
            }
            throw new DateException("Unsupported conversion from " + ta + " to " + timeType + ".");
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

        // static {
        //     PARSER_MAP = new HashMap<>();
        //     PARSER_MAP.put(Instant.class, (cs, t, f) -> {
        //         try {
        //             return Instant.parse(cs);
        //         } catch (DateTimeParseException e) {
        //             LocalDateTime localDateTime = LocalDateTime.parse(cs, f.formatter);
        //             return ZonedDateTime.of(localDateTime, f.zoneId).toInstant();
        //         }
        //     });
        //     PARSER_MAP.put(Date.class, (cs, t, f) -> {
        //         return Date.from(f.parse(cs, Instant.class));
        //     });
        //     PARSER_MAP.put(LocalDateTime.class, (cs, t, f) -> {
        //         return LocalDateTime.parse(cs, f.formatter);
        //     });
        //     PARSER_MAP.put(ZonedDateTime.class, (cs, t, f) -> {
        //         try {
        //             return ZonedDateTime.parse(cs, f.formatter);
        //         } catch (DateTimeParseException e) {
        //             LocalDateTime localDateTime = LocalDateTime.parse(cs, f.formatter);
        //             return ZonedDateTime.of(localDateTime, f.zoneId);
        //         }
        //     });
        //     PARSER_MAP.put(OffsetDateTime.class, (cs, t, f) -> {
        //         try {
        //             return OffsetDateTime.parse(cs, f.formatter);
        //         } catch (DateTimeParseException e) {
        //             LocalDateTime localDateTime = LocalDateTime.parse(cs, f.formatter);
        //             return OffsetDateTime.of(localDateTime, DateKit.nowOffset());
        //         }
        //     });
        //     PARSER_MAP.put(LocalDate.class, (cs, t, f) -> {
        //         return LocalDate.parse(cs, f.formatter);
        //     });
        //     PARSER_MAP.put(LocalTime.class, (cs, t, f) -> {
        //         return LocalTime.parse(cs, f.formatter);
        //     });
        //
        //     CONVERTER_MAP = new HashMap<>();
        //     CONVERTER_MAP.put(Instant.class, (ta, t, f) -> {
        //         return Instant.from(ta);
        //     });
        //     CONVERTER_MAP.put(Date.class, (ta, t, f) -> {
        //         return Date.from(Instant.from(ta));
        //     });
        //     CONVERTER_MAP.put(LocalDateTime.class, (ta, t, f) -> {
        //         return LocalDateTime.from(ta);
        //     });
        //     CONVERTER_MAP.put(ZonedDateTime.class, (ta, t, f) -> {
        //         return ZonedDateTime.from(ta);
        //     });
        //     CONVERTER_MAP.put(OffsetDateTime.class, (ta, t, f) -> {
        //         return OffsetDateTime.from(ta);
        //     });
        //     CONVERTER_MAP.put(LocalDate.class, (ta, t, f) -> {
        //         return LocalDate.from(ta);
        //     });
        //     CONVERTER_MAP.put(LocalTime.class, (ta, t, f) -> {
        //         return LocalTime.from(ta);
        //     });
        // }

        // private interface DateParser {
        //
        //     @Nonnull
        //     Object parse(
        //         @Nonnull CharSequence cs, @Nonnull Class<?> t, @Nonnull AbsDateFormatter f
        //     ) throws DateTimeException;
        // }
        //
        // private interface DateConverter {
        //
        //     @Nonnull
        //     Object convert(
        //         @Nonnull TemporalAccessor ta, @Nonnull Class<?> t, @Nonnull AbsDateFormatter f
        //     ) throws DateTimeException;
        // }
    }

    private static final class OfFormatter extends AbsDateFormatter {
        private OfFormatter(@Nonnull DateTimeFormatter formatter) {
            super(formatter, formatter.getZone());
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

    private static final class Cache {

        private static final @Nonnull SimpleCache<
            @Nonnull SimpleKey2,
            @Nonnull DateFormatter
            > CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        private static @Nonnull DateFormatter get(
            @Nonnull SimpleKey2 key,
            @Nonnull Function<@Nonnull SimpleKey2, @Nonnull DateFormatter> function
        ) {
            return CACHE.get(key, function);
        }

        private Cache() {
        }
    }

    private DateBack() {
    }
}
