package tests.core.base.date;

import internal.utils.TestPrint;
import org.junit.jupiter.api.BeforeEach;
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

public class DateTest implements TestPrint {

    private long now;
    private ZoneId zoneId;
    private Date nowDate;
    private Instant nowInstant;
    private ZoneOffset nowOffset;
    private LocalDateTime nowLocalDateTime;
    private OffsetDateTime nowOffsetDateTime;
    private ZonedDateTime nowZonedDateTime;
    private LocalDate nowLocalDate;
    private LocalTime nowLocalTime;

    @BeforeEach
    public void setUp() {
        now = System.currentTimeMillis();
        zoneId = ZoneId.systemDefault();
        nowDate = new Date(now);
        nowInstant = Instant.ofEpochMilli(now);
        nowOffset = ZoneOffset.systemDefault().getRules().getOffset(nowInstant);
        nowLocalDateTime = LocalDateTime.ofInstant(nowInstant, zoneId);
        nowOffsetDateTime = OffsetDateTime.ofInstant(nowInstant, zoneId);
        nowZonedDateTime = ZonedDateTime.ofInstant(nowInstant, zoneId);
        nowLocalDate = nowLocalDateTime.toLocalDate();
        nowLocalTime = nowLocalDateTime.toLocalTime();
    }

    @Test
    public void testNowOffset() {
        assertEquals(DateKit.nowOffset(), nowOffset);
    }

    @Test
    public void testFormatMethods() {
        String nowFormatStr = new SimpleDateFormat(DateKit.DEFAULT_PATTERN).format(nowDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateKit.DEFAULT_PATTERN);

        // Test format method
        assertEquals(DateKit.format(nowDate), nowFormatStr);
        assertEquals(DateKit.format(nowDate.toInstant()), nowFormatStr);
        assertEquals(DateKit.format(nowLocalDateTime), nowFormatStr);
        assertEquals(DateKit.format(nowZonedDateTime), nowFormatStr);
        assertEquals(DateKit.format(nowOffsetDateTime), nowFormatStr);

        // Test formatSafe method
        assertEquals(DateKit.formatSafe(nowDate), formatter.format(nowZonedDateTime));
        assertEquals(DateKit.format(nowInstant), formatter.format(nowZonedDateTime));
        assertEquals(DateKit.formatSafe(nowInstant), formatter.format(nowZonedDateTime));
        assertEquals(DateKit.formatSafe(nowLocalDateTime), formatter.format(nowLocalDateTime));
        assertEquals(DateKit.formatSafe(nowZonedDateTime), formatter.format(nowZonedDateTime));
        assertEquals(DateKit.formatSafe(nowOffsetDateTime), formatter.format(nowOffsetDateTime));

        // Test formatSafe with different formatters
        DateFormatter instantSpec = DateFormatter.from(DateTimeFormatter.ISO_INSTANT);
        assertEquals(instantSpec.formatSafe(nowDate), DateTimeFormatter.ISO_INSTANT.format(nowInstant));
        assertEquals(instantSpec.formatSafe(nowInstant), DateTimeFormatter.ISO_INSTANT.format(nowInstant));

        assertEquals(
            DateFormatter.from(DateTimeFormatter.ISO_DATE).formatSafe(nowLocalDate),
            DateTimeFormatter.ISO_DATE.format(nowLocalDate)
        );

        assertEquals(
            DateFormatter.from(DateTimeFormatter.ISO_TIME).formatSafe(nowLocalTime),
            DateTimeFormatter.ISO_TIME.format(nowLocalTime)
        );

        // Test null and error cases
        assertNull(DateKit.formatSafe((Date) null));
        assertNull(DateKit.formatSafe((Instant) null));
        assertNull(DateKit.formatSafe(new ErrDate()));
        assertNull(DateKit.formatSafe(new ErrTime()));
    }

    @Test
    public void testParseMethods() {
        String dateStr = DateKit.format(nowLocalDateTime);

        // Test parseSafe method with different types
        assertEquals(DateKit.parseSafe(dateStr, Date.class), nowDate);
        assertEquals(DateKit.parseSafe(dateStr, Instant.class), nowInstant);
        assertEquals(DateKit.parseSafe(dateStr, ZonedDateTime.class), nowZonedDateTime);
        assertEquals(DateKit.parseSafe(dateStr, OffsetDateTime.class), nowOffsetDateTime);
        assertEquals(DateKit.parseSafe(dateStr, LocalDateTime.class), nowLocalDateTime);
        assertEquals(DateKit.parseSafe(dateStr, LocalDate.class), nowLocalDate);
        assertEquals(DateKit.parseSafe(dateStr, LocalTime.class), nowLocalTime);

        // Test parseSafe with ISO_ZONED_DATE_TIME
        String fullStr = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(nowZonedDateTime);
        DateFormatter instantSpec = DateFormatter.from(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        assertEquals(instantSpec.parseSafe(fullStr, ZonedDateTime.class), nowZonedDateTime);
        assertEquals(instantSpec.parseSafe(fullStr, OffsetDateTime.class), nowOffsetDateTime);

        // Test parseSafe with ISO_INSTANT
        assertEquals(
            DateFormatter.from(DateTimeFormatter.ISO_INSTANT)
                .parseSafe(DateTimeFormatter.ISO_INSTANT.format(nowInstant), Instant.class),
            nowInstant
        );

        assertEquals(
            DateFormatter.from(DateTimeFormatter.ISO_INSTANT)
                .parseSafe(DateTimeFormatter.ISO_INSTANT.format(nowInstant), Date.class),
            nowDate
        );

        // Test error cases
        assertNull(DateKit.parseSafe("hahaha", Date.class));
        assertThrows(DateTimeException.class, () -> DateKit.parse("hahaha", Date.class));
        assertNull(DateKit.parseSafe(null, Date.class));
        assertNull(DateKit.parseSafe(dateStr, String.class));
    }

    @Test
    public void testConvertMethods() {
        // Test convertSafe method with different types
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

        // Test convert method
        assertSame(DateKit.convert(nowDate, Date.class), nowDate);
        assertEquals(DateKit.convert(nowDate, Instant.class), nowInstant);
        assertEquals(DateKit.convert(nowInstant, Date.class), nowDate);

        // Test error cases
        assertNull(DateKit.convertSafe((Date) null, ZonedDateTime.class));
        assertNull(DateKit.convertSafe((Instant) null, ZonedDateTime.class));
        assertNull(DateKit.convertSafe(nowDate, String.class));
        assertNull(DateKit.convertSafe(nowInstant, String.class));
    }

    @Test
    public void testDateFormatterPattern() {
        DateFormatter p = DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        assertEquals("yyyy-MM-dd HH:mm:ss", p.pattern());
        assertTrue(p.hasPattern());

        DateFormatter f = DateFormatter.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertThrows(DateTimeException.class, f::pattern);
        assertFalse(f.hasPattern());

        assertThrows(DateTimeException.class, () -> DateFormatter.ofPattern(null));
    }

    @Test
    public void testDateFormatterZone() {
        assertEquals(DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").zoneId(), ZoneId.systemDefault());
    }

    @Test
    public void testDefaultFormatter() {
        assertEquals(DateKit.DEFAULT_PATTERN, DateFormatter.defaultFormatter().pattern());
    }

    @Test
    public void testDateException() {
        assertThrows(DateException.class, () -> {throw new DateException();});
        assertThrows(DateException.class, () -> {throw new DateException("");});
        assertThrows(DateException.class, () -> {throw new DateException("", new RuntimeException());});
        assertThrows(DateException.class, () -> {throw new DateException(new RuntimeException());});
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
