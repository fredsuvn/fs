package tests.base.time;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.time.TimeException;
import xyz.sunqian.common.base.time.TimeFormatter;
import xyz.sunqian.common.base.time.TimeKit;
import xyz.sunqian.test.PrintTest;

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
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeKit.DEFAULT_PATTERN);
            assertEquals(TimeKit.formatSafe(nowLocalDateTime), formatter.format(nowLocalDateTime));
            assertEquals(TimeKit.formatSafe(nowZonedDateTime), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowOffsetDateTime), formatter.format(nowOffsetDateTime));
            assertEquals(TimeKit.format(nowDate, zoneId), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowDate, zoneId), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.format(nowInstant, zoneId), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowInstant, zoneId), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowLocalDateTime, zoneId), formatter.format(nowZonedDateTime));
            assertEquals(TimeKit.formatSafe(nowZonedDateTime, zoneId), formatter.format(nowZonedDateTime));
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
            assertNull(TimeKit.formatSafe(nowDate));
            expectThrows(DateTimeException.class, () -> TimeKit.format(nowDate));
            assertNull(TimeKit.formatSafe(nowInstant));
            expectThrows(DateTimeException.class, () -> TimeKit.format(nowInstant));
            assertNull(TimeKit.formatSafe((Date) null));
            assertNull(TimeKit.formatSafe((Instant) null));
            assertNull(TimeKit.formatSafe((Date) null, zoneId));
            assertNull(TimeKit.formatSafe((Instant) null, zoneId));
            assertNull(TimeKit.formatSafe(nowLocalDate, zoneId));
            assertNull(TimeKit.formatSafe(new ErrDate(), zoneId));
        }
        {
            // parse
            String dateStr = TimeKit.format(nowLocalDateTime);
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
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, ZonedDateTime.class), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, OffsetDateTime.class), nowOffsetDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, LocalDateTime.class), nowLocalDateTime);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, Instant.class), nowInstant);
            assertEquals(TimeKit.convertSafe(nowZonedDateTime, Date.class), nowDate);
            assertEquals(TimeKit.convert(nowDate, Instant.class), nowInstant);
            assertEquals(TimeKit.convert(nowInstant, Date.class), nowDate);
            assertEquals(TimeKit.convert(nowDate, ZonedDateTime.class, zoneId), nowZonedDateTime);
            assertEquals(TimeKit.convert(nowInstant, ZonedDateTime.class, zoneId), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowDate, ZonedDateTime.class, zoneId), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowInstant, ZonedDateTime.class, zoneId), nowZonedDateTime);
            assertEquals(TimeKit.convertSafe(nowLocalDateTime, ZonedDateTime.class, zoneId), nowZonedDateTime);
            assertNull(TimeKit.convertSafe(nowLocalDateTime, ZonedDateTime.class));
            expectThrows(DateTimeException.class, () -> TimeKit.convert(nowLocalDateTime, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe((Date) null, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe((Instant) null, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe(nowDate, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe(nowInstant, ZonedDateTime.class));
            assertNull(TimeKit.convertSafe((Date) null, ZonedDateTime.class, zoneId));
            assertNull(TimeKit.convertSafe((Instant) null, ZonedDateTime.class, zoneId));
            assertNull(TimeKit.convertSafe(nowLocalDate, ZonedDateTime.class, zoneId));
            assertNull(TimeKit.convertSafe(nowDate, String.class, zoneId));
        }
        {
            // pattern
            TimeFormatter p = TimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            assertEquals(p.pattern(), "yyyy-MM-dd HH:mm:ss");
            assertTrue(p.hasPattern());
            TimeFormatter f = TimeFormatter.ofFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            expectThrows(DateTimeException.class, f::pattern);
            assertFalse(f.hasPattern());
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
}
