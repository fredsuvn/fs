package tests.data.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import internal.test.ErrorAppender;
import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.string.StringView;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.data.json.JsonDataException;
import space.sunqian.fs.data.json.JsonFormatter;
import space.sunqian.fs.data.json.JsonKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.schema.ObjectSchemaParser;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonTest implements PrintTest {

    @Test
    public void testFormatter() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        {
            // string
            StringView a = StringView.of("123");
            assertEquals("\"" + a + "\"", JsonKit.toJsonString(a));
            String b = "\"123\"";
            assertEquals("\"\\\"123\\\"\"", JsonKit.toJsonString(b));
            assertEquals(jsonMapper.writeValueAsString(a), JsonKit.toJsonString(a));
            assertEquals(jsonMapper.writeValueAsString(b), JsonKit.toJsonString(b));
            String c = "fas,f{a\"fa{sf}s\"fas[fs}a\\fas[fsa\\\\fa]sfs\"fs:a,sd";
            assertEquals(jsonMapper.writeValueAsString(c), JsonKit.toJsonString(c));
            String d = "abc\n\r\t\f\b1234\u0000";
            System.out.println(jsonMapper.writeValueAsString(d));
            assertEquals(jsonMapper.writeValueAsString(d), JsonKit.toJsonString(d));
            StringBuilder escapes = new StringBuilder();
            for (int i = 0; i < 38; i++) {
                escapes.append(String.format("\\u%04X", i));
            }
            for (int i = 0; i < 38; i++) {
                escapes.append((char) i);
            }
            assertEquals(jsonMapper.writeValueAsString(escapes.toString()), JsonKit.toJsonString(escapes.toString()));
        }
        {
            // number
            assertEquals("12345", JsonKit.toJsonString(12345));
            assertEquals(jsonMapper.writeValueAsString(12345), JsonKit.toJsonString(12345));
        }
        {
            // boolean
            assertEquals("true", JsonKit.toJsonString(true));
            assertEquals(jsonMapper.writeValueAsString(true), JsonKit.toJsonString(true));
            assertEquals("false", JsonKit.toJsonString(false));
            assertEquals(jsonMapper.writeValueAsString(false), JsonKit.toJsonString(false));
        }
        {
            // null
            assertEquals("null", JsonKit.toJsonString(null));
            assertEquals(jsonMapper.writeValueAsString(null), JsonKit.toJsonString(null));
        }
        {
            // object
            testFormatter(JsonKit::toJsonString, false);
            JsonFormatter jsonFormatter = JsonFormatter.newFormatter(
                ObjectSchemaParser.defaultCachedParser(), ObjectConverter.defaultConverter(), true
            );
            testFormatter(jsonFormatter::format, true);
        }
        {
            // array
            boolean[] booleans = new boolean[]{true, false, true};
            assertEquals("[true,false,true]", JsonKit.toJsonString(booleans));
            assertEquals(jsonMapper.writeValueAsString(booleans), JsonKit.toJsonString(booleans));
            byte[] bytes = new byte[]{1, 2, 3};
            // assertEquals("[1,2,3]", JsonKit.toJsonString(bytes));
            assertEquals(jsonMapper.writeValueAsString(bytes), JsonKit.toJsonString(bytes));
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            assertEquals(jsonMapper.writeValueAsString(bytes), JsonKit.toJsonString(buffer));
            char[] chars = new char[]{'a', 'b', 'c'};
            // assertEquals("[\"a\",\"b\",\"c\"]", JsonKit.toJsonString(chars));
            assertEquals(jsonMapper.writeValueAsString(chars), JsonKit.toJsonString(chars));
            short[] shorts = new short[]{1, 2, 3};
            assertEquals("[1,2,3]", JsonKit.toJsonString(shorts));
            assertEquals(jsonMapper.writeValueAsString(shorts), JsonKit.toJsonString(shorts));
            int[] ints = new int[]{1, 2, 3};
            assertEquals("[1,2,3]", JsonKit.toJsonString(ints));
            assertEquals(jsonMapper.writeValueAsString(ints), JsonKit.toJsonString(ints));
            long[] longs = new long[]{1, 2, 3};
            assertEquals("[1,2,3]", JsonKit.toJsonString(longs));
            assertEquals(jsonMapper.writeValueAsString(longs), JsonKit.toJsonString(longs));
            float[] floats = new float[]{1.0f, 2.0f, 3.0f};
            assertEquals("[1.0,2.0,3.0]", JsonKit.toJsonString(floats));
            assertEquals(jsonMapper.writeValueAsString(floats), JsonKit.toJsonString(floats));
            double[] doubles = new double[]{1.0, 2.0, 3.0};
            assertEquals("[1.0,2.0,3.0]", JsonKit.toJsonString(doubles));
            assertEquals(jsonMapper.writeValueAsString(doubles), JsonKit.toJsonString(doubles));
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("a", 1);
            map.put("b", 2);
            Collection<Object> collection = new ArrayList<>();
            collection.add(null);
            collection.add(map);
            collection.add(null);
            collection.add(map);
            collection.add(null);
            collection.add(true);
            collection.add((byte) 1);
            collection.add((short) 1);
            collection.add((char) 1);
            collection.add((char) 33);
            collection.add(1);
            collection.add(1L);
            collection.add(0.1f);
            collection.add(0.1d);
            collection.add(new BigInteger("1"));
            collection.add(new BigDecimal("1"));
            assertEquals(jsonMapper.writeValueAsString(collection), JsonKit.toJsonString(collection));
            assertEquals(jsonMapper.writeValueAsString(collection), JsonKit.toJsonString(collection.toArray()));
        }
        {
            // error
            assertThrows(IORuntimeException.class, () -> JsonKit.toJsonString(new Object(), new ErrorAppender()));
            Map<String, Object> map = MapKit.map("aaa", 1, "bbb", 2);
            assertThrows(IORuntimeException.class, () ->
                JsonKit.toJsonString(map, IOKit.limitedWriter(new StringWriter(), 5)));
            assertThrows(IORuntimeException.class, () ->
                JsonKit.toJsonString(new DataObj("1111", "2222"), IOKit.limitedWriter(new StringWriter(), 5)));
        }
    }

    private void testFormatter(Function<Object, String> formatter, boolean ignoreNull) throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        {
            // object
            Date date = new Date();
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            Date dateFromStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // assertEquals("\"" + dateString + "\"", JsonKit.toJsonString(dateFromStr));
            // assertEquals("\"" + dateString + "\"", JsonKit.toJsonString(localDateTime));
            DataSrc dataSrc = new DataSrc();
            dataSrc.setS1("s1");
            dataSrc.setD1(dateFromStr);
            dataSrc.setD2(localDateTime);
            dataSrc.setN1(123L);
            dataSrc.setN2(new BigDecimal("123.456"));
            dataSrc.setE1(DataEnum.A);
            dataSrc.setFmt1(dateFromStr);
            dataSrc.setFmt2(localDateTime);
            dataSrc.setFmt3(123L);
            dataSrc.setFmt4(new BigDecimal("123.456"));
            String jsonString = formatter.apply(dataSrc);
            printFor("jsonString", jsonString);
            printFor("jsonString by Jackson", jsonMapper.writeValueAsString(dataSrc));
            DataTarget target = jsonMapper.readValue(jsonString, DataTarget.class);
            checkTarget(dataSrc, target, dateString);
            assertTrue(jsonString.contains("\"n1\":123"));
            assertTrue(jsonString.contains("\"n2\":123.456"));
            assertTrue(jsonString.contains("\"fmt3\":123.000"));
            assertTrue(jsonString.contains("\"fmt4\":123.4560"));
            if (!ignoreNull) {
                assertTrue(jsonString.contains("\"o1\":null"));
                assertTrue(jsonString.contains("\"o2\":null"));
            } else {
                assertFalse(jsonString.contains("\"o1\":null"));
                assertFalse(jsonString.contains("\"o2\":null"));
            }
            DataObj obj = new DataObj();
            obj.setS1("s1");
            obj.setS2("s2");
            Map<String, Object> map = new HashMap<>();
            map.put("m1", "m1");
            map.put("m2", "m2");
            map.put("m3", null);
            dataSrc.setO1(obj);
            dataSrc.setO2(map);
            String jsonString2 = formatter.apply(dataSrc);
            printFor("jsonString2", jsonString2);
            DataTarget target2 = jsonMapper.readValue(jsonString2, DataTarget.class);
            checkTarget(dataSrc, target2, dateString);
            assertTrue(jsonString2.contains("\"n1\":123"));
            assertTrue(jsonString2.contains("\"n2\":123.456"));
            assertTrue(jsonString2.contains("\"fmt3\":123.000"));
            assertTrue(jsonString2.contains("\"fmt4\":123.4560"));
            if (!ignoreNull) {
                assertTrue(jsonString2.contains("\"m3\":null"));
                assertEquals(map, target2.getO2());
            } else {
                assertFalse(jsonString2.contains("\"m3\":null"));
                assertEquals(MapKit.map("m1", "m1", "m2", "m2"), target2.getO2());
            }
            assertEquals(obj, target2.getO1());
        }
    }

    private void checkTarget(DataSrc dataSrc, DataTarget target, String dateString) {
        assertEquals(dataSrc.getS1(), target.getS1());
        assertEquals(dataSrc.getD1().toString(), target.getD1());
        assertEquals(dataSrc.getD2().toString(), target.getD2());
        assertEquals(String.valueOf(dataSrc.getN1()), target.getN1().toString());
        assertEquals(dataSrc.getN2().toString(), target.getN2().toString());
        assertEquals(dateString, target.getFmt1());
        assertEquals(dateString, target.getFmt2());
        assertEquals("123.000", target.getFmt3().toString());
        assertEquals("123.4560", target.getFmt4().toString());
        assertEquals(DataEnum.A.toString(), target.getE1());
    }

    @Test
    public void testException() throws Exception {
        {
            // JsonDataException
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException();
            });
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException("");
            });
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException("", new RuntimeException());
            });
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException(new RuntimeException());
            });
        }
    }

    @Data
    public static class DataSrc {
        private String s1;
        private Date d1;
        private LocalDateTime d2;
        private long n1;
        private BigDecimal n2;
        private DataEnum e1;

        private DataObj o1;
        private Map<String, Object> o2;

        @DatePattern("yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date fmt1;
        @DatePattern("yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fmt2;
        @NumPattern("#.000")
        @JsonFormat(pattern = "#.000")
        private long fmt3;
        @NumPattern("#.0000")
        @JsonFormat(pattern = "#.0000")
        private BigDecimal fmt4;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataObj {
        private String s1;
        private String s2;
    }

    @Data
    public static class DataTarget {
        private String s1;
        private String d1;
        private String d2;
        private BigDecimal n1;
        private BigDecimal n2;
        private String e1;

        private DataObj o1;
        private Map<String, Object> o2;

        private String fmt1;
        private String fmt2;
        private BigDecimal fmt3;
        private BigDecimal fmt4;
    }

    public enum DataEnum {
        A, B, C
    }
}
