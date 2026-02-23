package tests.base.date;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.date.DateException;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.date.DateKit;

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
import java.time.temporal.TemporalField;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateTest implements PrintTest {

    @Test
    public void testTime() {
        long now = System.currentTimeMillis();
        ZoneId zoneId = ZoneId.systemDefault();
        Date nowDate = new Date(now);
        Instant nowInstant = Instant.ofEpochMilli(now);
        ZoneOffset nowOffset = ZoneOffset.systemDefault().getRules().getOffset(nowInstant);
        assertEquals(DateKit.nowOffset(), nowOffset);
        LocalDateTime nowLocalDateTime = LocalDateTime.ofInstant(nowInstant, zoneId);
        OffsetDateTime nowOffsetDateTime = OffsetDateTime.ofInstant(nowInstant, zoneId);
        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(nowInstant, zoneId);
        LocalDate nowLocalDate = nowLocalDateTime.toLocalDate();
        LocalTime nowLocalTime = nowLocalDateTime.toLocalTime();
        {
            // format
            String nowFormatStr = new SimpleDateFormat(DateKit.DEFAULT_PATTERN).format(nowDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateKit.DEFAULT_PATTERN);
            assertEquals(DateKit.format(nowDate), nowFormatStr);
            assertEquals(DateKit.format(nowDate.toInstant()), nowFormatStr);
            assertEquals(DateKit.format(nowLocalDateTime), nowFormatStr);
            assertEquals(DateKit.format(nowZonedDateTime), nowFormatStr);
            assertEquals(DateKit.format(nowOffsetDateTime), nowFormatStr);
            assertEquals(DateKit.formatSafe(nowDate), formatter.format(nowZonedDateTime));
            assertEquals(DateKit.format(nowInstant), formatter.format(nowZonedDateTime));
            assertEquals(DateKit.formatSafe(nowInstant), formatter.format(nowZonedDateTime));
            assertEquals(DateKit.formatSafe(nowLocalDateTime), formatter.format(nowLocalDateTime));
            assertEquals(DateKit.formatSafe(nowZonedDateTime), formatter.format(nowZonedDateTime));
            assertEquals(DateKit.formatSafe(nowOffsetDateTime), formatter.format(nowOffsetDateTime));
            DateFormatter instantSpec = DateFormatter.ofFormatter(DateTimeFormatter.ISO_INSTANT);
            assertEquals(instantSpec.formatSafe(nowDate), DateTimeFormatter.ISO_INSTANT.format(nowInstant));
            assertEquals(instantSpec.formatSafe(nowInstant), DateTimeFormatter.ISO_INSTANT.format(nowInstant));
            assertEquals(
                DateFormatter.ofFormatter(DateTimeFormatter.ISO_DATE).formatSafe(nowLocalDate),
                DateTimeFormatter.ISO_DATE.format(nowLocalDate)
            );
            assertEquals(
                DateFormatter.ofFormatter(DateTimeFormatter.ISO_TIME).formatSafe(nowLocalTime),
                DateTimeFormatter.ISO_TIME.format(nowLocalTime)
            );
            assertNull(DateKit.formatSafe((Date) null));
            assertNull(DateKit.formatSafe((Instant) null));
            assertNull(DateKit.formatSafe(new ErrDate()));
            assertNull(DateKit.formatSafe(new ErrTime()));
        }
        {
            // parse
            String dateStr = DateKit.format(nowLocalDateTime);
            assertEquals(DateKit.parseSafe(dateStr, Date.class), nowDate);
            assertEquals(DateKit.parseSafe(dateStr, Instant.class), nowInstant);
            assertEquals(DateKit.parseSafe(dateStr, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(DateKit.parseSafe(dateStr, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(DateKit.parseSafe(dateStr, LocalDateTime.class), nowLocalDateTime);
            assertEquals(DateKit.parseSafe(dateStr, LocalDate.class), nowLocalDate);
            assertEquals(DateKit.parseSafe(dateStr, LocalTime.class), nowLocalTime);
            String fullStr = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(nowZonedDateTime);
            DateFormatter instantSpec = DateFormatter.ofFormatter(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            assertEquals(instantSpec.parseSafe(fullStr, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(instantSpec.parseSafe(fullStr, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(
                DateFormatter.ofFormatter(DateTimeFormatter.ISO_INSTANT)
                    .parseSafe(DateTimeFormatter.ISO_INSTANT.format(nowInstant), Instant.class),
                nowInstant
            );
            assertEquals(
                DateFormatter.ofFormatter(DateTimeFormatter.ISO_INSTANT)
                    .parseSafe(DateTimeFormatter.ISO_INSTANT.format(nowInstant), Date.class),
                nowDate
            );
            assertNull(DateKit.parseSafe("hahaha", Date.class));
            assertThrows(DateTimeException.class, () -> DateKit.parse("hahaha", Date.class));
            assertNull(DateKit.parseSafe(null, Date.class));
            assertNull(DateKit.parseSafe(dateStr, String.class));
        }
        {
            // convert
            assertEquals(DateKit.convertSafe(nowLocalDateTime, LocalDateTime.class), nowLocalDateTime);
            assertEquals(DateKit.convertSafe(nowLocalDateTime, LocalDate.class), nowLocalDate);
            assertEquals(DateKit.convertSafe(nowLocalDateTime, LocalTime.class), nowLocalTime);
            assertEquals(DateKit.convertSafe(nowDate, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(DateKit.convertSafe(nowInstant, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(DateKit.convertSafe(nowLocalDateTime, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(DateKit.convertSafe(nowDate, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(DateKit.convertSafe(nowInstant, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(DateKit.convertSafe(nowLocalDateTime, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(DateKit.convertSafe(nowZonedDateTime, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(DateKit.convertSafe(nowZonedDateTime, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(DateKit.convertSafe(nowZonedDateTime, LocalDateTime.class), nowLocalDateTime);
            assertEquals(DateKit.convertSafe(nowZonedDateTime, Instant.class), nowInstant);
            assertEquals(DateKit.convertSafe(nowZonedDateTime, Date.class), nowDate);
            assertSame(DateKit.convert(nowDate, Date.class), nowDate);
            assertEquals(DateKit.convert(nowDate, Instant.class), nowInstant);
            assertEquals(DateKit.convert(nowInstant, Date.class), nowDate);
            assertNull(DateKit.convertSafe((Date) null, ZonedDateTime.class));
            assertNull(DateKit.convertSafe((Instant) null, ZonedDateTime.class));
            assertNull(DateKit.convertSafe(nowDate, String.class));
            assertNull(DateKit.convertSafe(nowInstant, String.class));
        }
        {
            // pattern
            DateFormatter p = DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            assertEquals("yyyy-MM-dd HH:mm:ss", p.pattern());
            assertTrue(p.hasPattern());
            DateFormatter f = DateFormatter.ofFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            assertThrows(DateTimeException.class, f::pattern);
            assertFalse(f.hasPattern());
            assertThrows(DateTimeException.class, () -> DateFormatter.ofPattern(null));
        }
        {
            // zone
            assertEquals(DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").zoneId(), ZoneId.systemDefault());
        }
        {
            // default formatter
            // assertEquals(TimeKit.formatter().pattern(), TimeKit.DEFAULT_PATTERN);
            assertEquals(DateKit.DEFAULT_PATTERN, DateFormatter.defaultFormatter().pattern());
        }
        {
            // special case:
            // DateFormatter ymd = DateFormatter.ofPattern("yyyy-MM-dd");
            // assertEquals(
            //     LocalDateTime.of(2026, 2, 24, 0, 0, 0),
            //     ymd.parse("2026-02-24", LocalDateTime.class)
            // );
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // TimeException
            assertThrows(DateException.class, () -> {
                throw new DateException();
            });
            assertThrows(DateException.class, () -> {
                throw new DateException("");
            });
            assertThrows(DateException.class, () -> {
                throw new DateException("", new RuntimeException());
            });
            assertThrows(DateException.class, () -> {
                throw new DateException(new RuntimeException());
            });
        }
    }

    private static final class ErrDate extends Date {
        @Override
        public Instant toInstant() {
            throw new RuntimeException();
        }
    }

    private static final class ErrTime implements TemporalAccessor {

        @Override
        public boolean isSupported(TemporalField field) {
            throw new RuntimeException();
        }

        @Override
        public long getLong(TemporalField field) {
            throw new RuntimeException();
        }
    }
}
