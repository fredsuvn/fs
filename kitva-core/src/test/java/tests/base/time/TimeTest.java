package tests.base.time;

import org.testng.annotations.Test;
import space.sunqian.common.base.time.TimeException;
import space.sunqian.common.base.time.TimeFormatter;
import space.sunqian.common.base.time.TimeKit;
import space.sunqian.test.PrintTest;

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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class TimeTest implements PrintTest {

    @Test
    public void testTime() {
        long now = System.currentTimeMillis();
        ZoneId zoneId = ZoneId.systemDefault();
        Date nowDate = new Date(now);
        Instant nowInstant = Instant.ofEpochMilli(now);
        ZoneOffset nowOffset = ZoneOffset.systemDefault().getRules().getOffset(nowInstant);
        assertEquals(TimeKit.nowOffset(), nowOffset);
        LocalDateTime nowLocalDateTime = LocalDateTime.ofInstant(nowInstant, zoneId);
        OffsetDateTime nowOffsetDateTime = OffsetDateTime.ofInstant(nowInstant, zoneId);
        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(nowInstant, zoneId);
        LocalDate nowLocalDate = nowLocalDateTime.toLocalDate();
        LocalTime nowLocalTime = nowLocalDateTime.toLocalTime();
        {
            // format
            String nowFormatStr = new SimpleDateFormat(TimeKit.DEFAULT_PATTERN).format(nowDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeKit.DEFAULT_PATTERN);
            assertEquals(TimeKit.format(nowDate), nowFormatStr);
            assertEquals(TimeKit.format(nowDate.toInstant()), nowFormatStr);
            assertEquals(TimeKit.format(nowLocalDateTime), nowFormatStr);
            assertEquals(TimeKit.format(nowZonedDateTime), nowFormatStr);
            assertEquals(TimeKit.format(nowOffsetDateTime), nowFormatStr);
            assertEquals(TimeKit.formatSafe(nowDate), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.format(nowInstant), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowInstant), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowLocalDateTime), formatter.format(nowLocalDateTime));
            assertEquals(TimeKit.formatSafe(nowZonedDateTime), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowOffsetDateTime), formatter.format(nowOffsetDateTime));
            TimeFormatter instantSpec = TimeFormatter.ofFormatter(DateTimeFormatter.ISO_INSTANT);
            assertEquals(instantSpec.formatSafe(nowDate), DateTimeFormatter.ISO_INSTANT.format(nowInstant));
            assertEquals(instantSpec.formatSafe(nowInstant), DateTimeFormatter.ISO_INSTANT.format(nowInstant));
            assertEquals(
                TimeFormatter.ofFormatter(DateTimeFormatter.ISO_DATE).formatSafe(nowLocalDate),
                DateTimeFormatter.ISO_DATE.format(nowLocalDate)
            );
            assertEquals(
                TimeFormatter.ofFormatter(DateTimeFormatter.ISO_TIME).formatSafe(nowLocalTime),
                DateTimeFormatter.ISO_TIME.format(nowLocalTime)
            );
            assertNull(TimeKit.formatSafe((Date) null));
            assertNull(TimeKit.formatSafe((Instant) null));
            assertNull(TimeKit.formatSafe(new ErrDate()));
            assertNull(TimeKit.formatSafe(new ErrTime()));
        }
        {
            // parse
            String dateStr = TimeKit.format(nowLocalDateTime);
            assertEquals(TimeKit.parseSafe(dateStr, Date.class), nowDate);
            assertEquals(TimeKit.parseSafe(dateStr, Instant.class), nowInstant);
            assertEquals(TimeKit.parseSafe(dateStr, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(TimeKit.parseSafe(dateStr, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(TimeKit.parseSafe(dateStr, LocalDateTime.class), nowLocalDateTime);
            assertEquals(TimeKit.parseSafe(dateStr, LocalDate.class), nowLocalDate);
            assertEquals(TimeKit.parseSafe(dateStr, LocalTime.class), nowLocalTime);
            String fullStr = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(nowZonedDateTime);
            TimeFormatter instantSpec = TimeFormatter.ofFormatter(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            assertEquals(instantSpec.parseSafe(fullStr, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(instantSpec.parseSafe(fullStr, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(
                TimeFormatter.ofFormatter(DateTimeFormatter.ISO_INSTANT)
                    .parseSafe(DateTimeFormatter.ISO_INSTANT.format(nowInstant), Instant.class),
                nowInstant
            );
            assertEquals(
                TimeFormatter.ofFormatter(DateTimeFormatter.ISO_INSTANT)
                    .parseSafe(DateTimeFormatter.ISO_INSTANT.format(nowInstant), Date.class),
                nowDate
            );
            assertNull(TimeKit.parseSafe("hahaha", Date.class));
            expectThrows(DateTimeException.class, () -> TimeKit.parse("hahaha", Date.class));
            assertNull(TimeKit.parseSafe(null, Date.class));
            assertNull(TimeKit.parseSafe(dateStr, String.class));
        }
        {
            // convert
            assertEquals(TimeKit.convertSafe(nowLocalDateTime, LocalDateTime.class), nowLocalDateTime);
            assertEquals(TimeKit.convertSafe(nowLocalDateTime, LocalDate.class), nowLocalDate);
            assertEquals(TimeKit.convertSafe(nowLocalDateTime, LocalTime.class), nowLocalTime);
            assertEquals(TimeKit.convertSafe(nowDate, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowInstant, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowLocalDateTime, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowDate, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(TimeKit.convertSafe(nowInstant, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(TimeKit.convertSafe(nowLocalDateTime, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, LocalDateTime.class), nowLocalDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, Instant.class), nowInstant);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, Date.class), nowDate);
            assertSame(TimeKit.convert(nowDate, Date.class), nowDate);
            assertEquals(TimeKit.convert(nowDate, Instant.class), nowInstant);
            assertEquals(TimeKit.convert(nowInstant, Date.class), nowDate);
            assertNull(TimeKit.convertSafe((Date) null, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe((Instant) null, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe(nowDate, String.class));
            assertNull(TimeKit.convertSafe(nowInstant, String.class));
        }
        {
            // pattern
            TimeFormatter p = TimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            assertEquals(p.pattern(), "yyyy-MM-dd HH:mm:ss");
            assertTrue(p.hasPattern());
            TimeFormatter f = TimeFormatter.ofFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            expectThrows(DateTimeException.class, f::pattern);
            assertFalse(f.hasPattern());
            expectThrows(DateTimeException.class, () -> TimeFormatter.ofPattern(null));
        }
        {
            // zone
            assertEquals(TimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").zoneId(), ZoneId.systemDefault());
        }
        {
            // default formatter
            // assertEquals(TimeKit.formatter().pattern(), TimeKit.DEFAULT_PATTERN);
            assertEquals(TimeFormatter.defaultFormatter().pattern(), TimeKit.DEFAULT_PATTERN);
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // TimeException
            expectThrows(TimeException.class, () -> {
                throw new TimeException();
            });
            expectThrows(TimeException.class, () -> {
                throw new TimeException("");
            });
            expectThrows(TimeException.class, () -> {
                throw new TimeException("", new RuntimeException());
            });
            expectThrows(TimeException.class, () -> {
                throw new TimeException(new RuntimeException());
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
