package tests.data.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import internal.test.ErrorAppender;
import internal.test.PrintTest;
import internal.test.ReadOps;
import internal.test.TestReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.string.StringView;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.data.DataFormattingException;
import space.sunqian.fs.data.json.JsonData;
import space.sunqian.fs.data.json.JsonDataException;
import space.sunqian.fs.data.json.JsonDataParsingException;
import space.sunqian.fs.data.json.JsonFormatter;
import space.sunqian.fs.data.json.JsonKit;
import space.sunqian.fs.data.json.JsonParser;
import space.sunqian.fs.data.json.JsonType;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
import space.sunqian.fs.reflect.TypeRef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonTest implements PrintTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testFormattingAndParsing() throws Exception {
        testFormattingAndParsing(
            new JsonFormatter() {

                @Override
                public void formatTo(@Nullable Object data, @Nonnull Appendable appender) throws DataFormattingException {
                    JsonKit.toJsonString(data, appender);
                }

                @Override
                public @Nonnull String format(@Nullable Object data) throws DataFormattingException {
                    return JsonKit.toJsonString(data);
                }

                @Override
                public byte @Nonnull [] formatBytes(@Nullable Object data) throws DataFormattingException {
                    return JsonKit.toJsonBytes(data);
                }
            },
            false,
            new JsonParser() {
                @Override
                public @Nonnull JsonData parse(@Nonnull InputStream input) throws JsonDataParsingException {
                    return JsonKit.parse(input);
                }

                @Override
                public @Nonnull JsonData parse(@Nonnull ReadableByteChannel channel) throws JsonDataParsingException {
                    return JsonKit.parse(channel);
                }

                @Override
                public @Nonnull JsonData parse(@Nonnull Reader reader) throws JsonDataParsingException {
                    return JsonKit.parse(reader);
                }

                @Override
                public @Nonnull JsonData parse(@Nonnull String string) throws JsonDataParsingException {
                    return JsonKit.parse(string);
                }
            }
        );
        testFormattingAndParsing(
            JsonFormatter.newFormatter(true),
            true,
            JsonParser.defaultParser()
        );
        testFormattingAndParsing(
            JsonFormatter.newFormatter(
                ObjectSchemaParser.defaultCachedParser(),
                ObjectConverter.defaultConverter(),
                false
            ),
            false,
            JsonParser.defaultParser()
        );
    }

    private void testFormattingAndParsing(
        JsonFormatter formatter,
        boolean ignoreNull,
        JsonParser parser
    ) throws Exception {
        {
            // object
            Date date = new Date();
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            Date dateFromStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            DataSrc dataSrc = new DataSrc();
            dataSrc.setS1("s1");
            dataSrc.setD1(dateFromStr);
            dataSrc.setD2(localDateTime);
            dataSrc.setN1(123L);
            dataSrc.setN2(new BigDecimal("123.456"));
            dataSrc.setE1(DataEnum.A);
            dataSrc.setB1(true);
            dataSrc.setB2(false);
            dataSrc.setFmt1(dateFromStr);
            dataSrc.setFmt2(localDateTime);
            dataSrc.setFmt3(123L);
            dataSrc.setFmt4(new BigDecimal("123.456"));
            String jsonString = formatter.format(dataSrc);
            // printFor("jsonString", jsonString);
            // printFor("jsonString by Jackson", jsonMapper.writeValueAsString(dataSrc));
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
            DataTarget targetByJackson = jsonMapper.readValue(jsonString, DataTarget.class);
            checkTarget(dataSrc, targetByJackson, dateString);
            DataTarget target = parser.parse(jsonString).toObject(DataTarget.class);
            checkTarget(dataSrc, target, dateString);
            assertEquals(targetByJackson, target);
            DataPack obj = new DataPack();
            obj.setS1("s1");
            obj.setS2("s2");
            Map<String, Object> map = new HashMap<>();
            map.put("m1", "m1");
            map.put("m2", "m2");
            map.put("m3", null);
            dataSrc.setO1(obj);
            dataSrc.setO2(map);
            String jsonString2 = formatter.format(dataSrc);
            // printFor("jsonString2", jsonString2);
            DataTarget targetByJackson2 = jsonMapper.readValue(jsonString2, DataTarget.class);
            checkTarget(dataSrc, targetByJackson2, dateString);
            DataTarget target2 = parser.parse(jsonString2).toObject(DataTarget.class);
            checkTarget(dataSrc, target2, dateString);
            assertEquals(targetByJackson2, target2);
            assertTrue(jsonString2.contains("\"n1\":123"));
            assertTrue(jsonString2.contains("\"n2\":123.456"));
            assertTrue(jsonString2.contains("\"fmt3\":123.000"));
            assertTrue(jsonString2.contains("\"fmt4\":123.4560"));
            if (!ignoreNull) {
                assertTrue(jsonString2.contains("\"m3\":null"));
                assertTrue(jsonString2.contains("\"nullStr\":null"));
                assertEquals(map, targetByJackson2.getO2());
            } else {
                assertFalse(jsonString2.contains("\"m3\":null"));
                assertFalse(jsonString2.contains("\"nullStr\":null"));
                assertEquals(MapKit.map("m1", "m1", "m2", "m2"), targetByJackson2.getO2());
            }
            assertEquals(obj, targetByJackson2.getO1());
            // empty object
            assertEquals(Collections.emptyMap(), parser.parse("{}").asMap());
            assertEquals(Collections.emptyMap(), parser.parse("{  }  ").asMap());
            assertEquals(Collections.emptyMap(), parser.parse("  {  }  ").asMap());
            // errors:
            JsonDataParsingException e1 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a:123,\"b\":243}").toObject(DataTarget.class);
            });
            assertEquals(9, e1.getOccurIndex());
            assertEquals(":", e1.getExpectedChars());
            assertEquals("b", e1.getUnexpectedChars());
            JsonDataParsingException e2 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\":123,\"b\":243").toObject(DataTarget.class);
            });
            assertEquals(16, e2.getOccurIndex());
            assertEquals("}", e2.getExpectedChars());
            assertNull(e2.getUnexpectedChars());
            JsonDataParsingException e3 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\":+123,\"b\":243}").toObject(DataTarget.class);
            });
            assertEquals(5, e3.getOccurIndex());
            assertNull(e3.getExpectedChars());
            assertEquals("+", e3.getUnexpectedChars());
            JsonDataParsingException e4 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\":123XXX\"\",\"b\":243}").toObject(DataTarget.class);
            });
            assertEquals(8, e4.getOccurIndex());
            assertNull(e4.getExpectedChars());
            assertEquals("X", e4.getUnexpectedChars());
            JsonDataParsingException e5 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\":123e11.11\"\",\"b\":243}").toObject(DataTarget.class);
            });
            assertEquals(5, e5.getOccurIndex());
            assertNull(e5.getExpectedChars());
            assertEquals("123e11.11", e5.getUnexpectedChars());
            JsonDataParsingException e6 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\":\"123,\"b\":243}").toObject(DataTarget.class);
            });
            assertEquals(11, e6.getOccurIndex());
            assertNull(e6.getExpectedChars());
            assertEquals("b", e6.getUnexpectedChars());
            JsonDataParsingException e7 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{,\"a\":\"123,\"b\":243}").toObject(DataTarget.class);
            });
            assertEquals(1, e7.getOccurIndex());
            assertNull(e7.getExpectedChars());
            assertEquals(",", e7.getUnexpectedChars());
            JsonDataParsingException e8 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\"").toObject(DataTarget.class);
            });
            assertEquals(4, e8.getOccurIndex());
            assertEquals(":", e8.getExpectedChars());
            assertNull(e8.getUnexpectedChars());
            JsonDataParsingException e9 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("{\"a\"  ").toObject(DataTarget.class);
            });
            assertEquals(6, e9.getOccurIndex());
            assertEquals(":", e9.getExpectedChars());
            assertNull(e9.getUnexpectedChars());
            JsonDataParsingException e10 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("").toObject(DataTarget.class);
            });
            assertEquals(0, e10.getOccurIndex());
            assertNull(e10.getExpectedChars());
            assertNull(e10.getUnexpectedChars());
            JsonDataParsingException e11 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("   ").toObject(DataTarget.class);
            });
            assertEquals(3, e11.getOccurIndex());
            assertNull(e11.getExpectedChars());
            assertNull(e11.getUnexpectedChars());
        }
        {
            // string
            StringView a = StringView.of("123");
            String jsonA = jsonMapper.writeValueAsString(a.toString());
            assertEquals(jsonA, formatter.format(a));
            assertEquals(a.toString(), parser.parse(jsonA).asString());
            String b = "\"123\"";
            String jsonB = jsonMapper.writeValueAsString(b);
            assertEquals(jsonB, formatter.format(b));
            assertEquals(b, parser.parse(jsonB).asString());
            String c = "fas,f{a\"fa{sf}s\"fas[fs}a\\fas[fsa\\\\fa]sfs\"fs:a,sd";
            String jsonC = jsonMapper.writeValueAsString(c);
            assertEquals(jsonC, formatter.format(c));
            assertEquals(c, parser.parse(jsonC).asString());
            String d = "abc\n\r\t\f\b1234\u0000中文";
            String jsonD = jsonMapper.writeValueAsString(d);
            assertEquals(jsonD, formatter.format(d));
            assertEquals(d, parser.parse(jsonD).asString());
            StringBuilder escapes = new StringBuilder();
            for (int i = 0; i < 38; i++) {
                escapes.append(String.format("\\u%04X", i));
            }
            for (int i = 0; i < 38; i++) {
                escapes.append((char) i);
            }
            String jsonEscapes = jsonMapper.writeValueAsString(escapes.toString());
            assertEquals(jsonEscapes, formatter.format(escapes.toString()));
            assertEquals(escapes.toString(), parser.parse(jsonEscapes).asString());
            // errors:
            JsonDataParsingException e1 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("\"b").toObject(DataTarget.class);
            });
            assertEquals(2, e1.getOccurIndex());
            assertEquals("\"", e1.getExpectedChars());
            assertNull(e1.getUnexpectedChars());
            JsonDataParsingException e2 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("\"b\\x\"").toObject(DataTarget.class);
            });
            assertEquals(4, e2.getOccurIndex());
            assertNull(e2.getExpectedChars());
            assertEquals("x", e2.getUnexpectedChars());
            JsonDataParsingException e3 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("\"b\\").toObject(DataTarget.class);
            });
            assertEquals(3, e3.getOccurIndex());
            assertNull(e3.getExpectedChars());
            assertNull(e3.getUnexpectedChars());
            JsonDataParsingException e4 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("\"b\\u123").toObject(DataTarget.class);
            });
            assertEquals(7, e4.getOccurIndex());
            assertNull(e4.getExpectedChars());
            assertNull(e4.getUnexpectedChars());
        }
        {
            // number
            Integer a = 12345;
            String jsonA = jsonMapper.writeValueAsString(a);
            assertEquals(jsonA, formatter.format(a));
            assertEquals(a, parser.parse(jsonA).asInt());
            Long b = 123456789123456789L;
            String jsonB = jsonMapper.writeValueAsString(b);
            assertEquals(jsonB, formatter.format(b));
            assertEquals(b, parser.parse(jsonB).asLong());
            BigDecimal c = new BigDecimal("123.456");
            String jsonC = jsonMapper.writeValueAsString(c);
            assertEquals(jsonC, formatter.format(c));
            assertEquals(c, parser.parse(jsonC).asBigDecimal());
            BigDecimal d = new BigDecimal("123.456e12");
            String jsonD = jsonMapper.writeValueAsString(d);
            assertEquals(jsonD, formatter.format(d));
            assertEquals(d, parser.parse(jsonD).asBigDecimal());
            BigDecimal e = new BigDecimal("123.456E12");
            String jsonE = jsonMapper.writeValueAsString(e);
            assertEquals(jsonE, formatter.format(e));
            assertEquals(e, parser.parse(jsonE).asBigDecimal());
            BigDecimal f = new BigDecimal("-123.456e12");
            String jsonF = jsonMapper.writeValueAsString(f);
            assertEquals(jsonF, formatter.format(f));
            assertEquals(f, parser.parse(jsonF).asBigDecimal());
            BigDecimal g = new BigDecimal("-123.456e+12");
            String jsonG = jsonMapper.writeValueAsString(g);
            assertEquals(jsonG, formatter.format(g));
            assertEquals(g, parser.parse(jsonG).asBigDecimal());
            for (int i = 0; i < 10; i++) {
                assertEquals(i, parser.parse(String.valueOf(i)).asInt());
            }
            // errors:
            JsonDataParsingException e1 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("123x").toObject(DataTarget.class);
            });
            assertEquals(3, e1.getOccurIndex());
            assertNull(e1.getExpectedChars());
            assertEquals("x", e1.getUnexpectedChars());
            JsonDataParsingException e2 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("y123x").toObject(DataTarget.class);
            });
            assertEquals(0, e2.getOccurIndex());
            assertNull(e2.getExpectedChars());
            assertEquals("y", e2.getUnexpectedChars());
        }
        {
            // boolean
            Boolean a = true;
            String jsonA = jsonMapper.writeValueAsString(a);
            assertEquals(jsonA, formatter.format(a));
            assertEquals(a, parser.parse(jsonA).asBoolean());
            Boolean b = false;
            String jsonB = jsonMapper.writeValueAsString(b);
            assertEquals(jsonB, formatter.format(b));
            assertEquals(b, parser.parse(jsonB).asBoolean());
            // errors:
            JsonDataParsingException e1 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("tru").toObject(DataTarget.class);
            });
            assertEquals(3, e1.getOccurIndex());
            assertEquals("e", e1.getExpectedChars());
            assertNull(e1.getUnexpectedChars());
            JsonDataParsingException e2 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("truq").toObject(DataTarget.class);
            });
            assertEquals(3, e2.getOccurIndex());
            assertEquals("e", e2.getExpectedChars());
            assertEquals("q", e2.getUnexpectedChars());
            JsonDataParsingException e3 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("fa").toObject(DataTarget.class);
            });
            assertEquals(2, e3.getOccurIndex());
            assertEquals("l", e3.getExpectedChars());
            assertNull(e3.getUnexpectedChars());
            JsonDataParsingException e4 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("faq").toObject(DataTarget.class);
            });
            assertEquals(2, e4.getOccurIndex());
            assertEquals("l", e4.getExpectedChars());
            assertEquals("q", e4.getUnexpectedChars());
        }
        {
            // null
            assertEquals("null", formatter.format(null));
            assertTrue(parser.parse("null").isNull());
            // errors:
            JsonDataParsingException e1 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("nul").toObject(DataTarget.class);
            });
            assertEquals(3, e1.getOccurIndex());
            assertEquals("l", e1.getExpectedChars());
            assertNull(e1.getUnexpectedChars());
        }
        {
            // array
            boolean[] booleans = new boolean[]{true, false, true};
            String jsonBooleans = jsonMapper.writeValueAsString(booleans);
            assertEquals(jsonBooleans, formatter.format(booleans));
            assertArrayEquals(booleans, parser.parse(jsonBooleans).toObject(boolean[].class));
            byte[] bytes = new byte[]{1, 2, 3};
            // Base64: "AQID"
            String jsonBytes = jsonMapper.writeValueAsString(bytes);
            assertEquals(jsonBytes, formatter.format(bytes));
            // assertArrayEquals(bytes, (byte[]) parser.parse(jsonBytes, byte[].class));
            char[] chars = new char[]{'a', 'b', 'c'};
            // String: "abc"
            String jsonChars = jsonMapper.writeValueAsString(chars);
            assertEquals(jsonChars, formatter.format(chars));
            // assertArrayEquals(chars, (char[]) parser.parse(jsonChars, char[].class));
            short[] shorts = new short[]{1, 2, 3};
            String jsonShorts = jsonMapper.writeValueAsString(shorts);
            assertEquals(jsonShorts, formatter.format(shorts));
            assertArrayEquals(shorts, parser.parse(jsonShorts).toObject(short[].class));
            int[] ints = new int[]{1, 2, 3};
            String jsonInts = jsonMapper.writeValueAsString(ints);
            assertEquals(jsonInts, formatter.format(ints));
            assertArrayEquals(ints, parser.parse(jsonInts).toObject(int[].class));
            long[] longs = new long[]{1, 2, 3};
            String jsonLongs = jsonMapper.writeValueAsString(longs);
            assertEquals(jsonLongs, formatter.format(longs));
            assertArrayEquals(longs, parser.parse(jsonLongs).toObject(long[].class));
            float[] floats = new float[]{1.0f, 2.0f, 3.0f};
            String jsonFloats = jsonMapper.writeValueAsString(floats);
            assertEquals(jsonFloats, formatter.format(floats));
            assertArrayEquals(floats, parser.parse(jsonFloats).toObject(float[].class));
            double[] doubles = new double[]{1.0, 2.0, 3.0};
            String jsonDoubles = jsonMapper.writeValueAsString(doubles);
            assertEquals(jsonDoubles, formatter.format(doubles));
            assertArrayEquals(doubles, parser.parse(jsonDoubles).toObject(double[].class));
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
            String jsonCollection = jsonMapper.writeValueAsString(collection);
            assertEquals(jsonCollection, formatter.format(collection));
            List<Object> expList = new ArrayList<>(collection);
            expList.set(6, 1);
            expList.set(7, 1);
            expList.set(8, String.valueOf((char) 1));
            expList.set(9, String.valueOf((char) 33));
            expList.set(11, 1);
            expList.set(12, new BigDecimal("0.1"));
            expList.set(13, new BigDecimal("0.1"));
            expList.set(14, 1);
            expList.set(15, 1);
            assertEquals(expList, parser.parse(jsonCollection).asList());
            // empty array
            assertEquals(Collections.emptyList(), parser.parse("[]").asList());
            assertEquals(Collections.emptyList(), parser.parse("[  ]").asList());
            // errors:
            JsonDataParsingException e1 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("[1, 2").asList();
            });
            assertEquals(5, e1.getOccurIndex());
            assertEquals("]", e1.getExpectedChars());
            assertNull(e1.getUnexpectedChars());
            JsonDataParsingException e2 = assertThrows(JsonDataParsingException.class, () -> {
                parser.parse("[ , ]").asList();
            });
            assertEquals(2, e2.getOccurIndex());
            assertNull(e2.getExpectedChars());
            assertEquals(",", e2.getUnexpectedChars());
        }
        {
            // complex data
            ComplexData data = new ComplexData();
            data.setI1(1);
            data.setL1(2L);
            data.setStr1("hello");
            data.setIi1(3);
            data.setLl1(4L);
            data.setBb1(new BigDecimal("5.0"));
            data.setLa1(new long[]{1L, 2L});
            data.setBa1(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
            data.setSa1(ListKit.list("a", "b"));
            data.setI2(1);
            data.setL2(2L);
            data.setStr2("hello");
            data.setIi2(3);
            data.setLl2(4L);
            data.setBb2(new BigDecimal("5.0"));
            data.setLa2(new long[]{1L, 2L});
            data.setBa2(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
            data.setSa2(ListKit.list("a", "b"));
            data.setI3(1);
            data.setL3(2L);
            data.setStr3("hello");
            data.setIi3(3);
            data.setLl3(4L);
            data.setBb3(new BigDecimal("5.0"));
            data.setLa3(new long[]{1L, 2L});
            data.setBa3(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
            data.setSa3(ListKit.list("a", "b"));
            String jsonData = formatter.format(data);
            assertEquals(data, jsonMapper.readValue(jsonData, ComplexData.class));
            assertEquals(data, parser.parse(jsonData).toObject(new TypeRef<ComplexData>() {}));
        }
        {
            // error
            assertThrows(DataFormattingException.class, () -> JsonKit.toJsonString(new Object(), new ErrorAppender()));
            Map<String, Object> map = MapKit.map("aaa", 1, "bbb", 2);
            assertThrows(DataFormattingException.class, () ->
                JsonKit.toJsonString(map, IOKit.limitedWriter(new StringWriter(), 5)));
            assertThrows(DataFormattingException.class, () ->
                JsonKit.toJsonString(new DataPack("1111", "2222"), IOKit.limitedWriter(new StringWriter(), 5)));
            TestReader tr = new TestReader(IOKit.emptyReader());
            tr.setNextOperation(ReadOps.THROW, 999);
            assertThrows(JsonDataParsingException.class, () -> JsonKit.parse(tr));
            assertThrows(JsonDataParsingException.class, () -> JsonKit.parse(IOKit.newInputStream(tr)));
            assertThrows(JsonDataParsingException.class, () -> JsonKit.parse(Channels.newChannel(IOKit.newInputStream(tr))));
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
        assertEquals(dataSrc.isB1(), target.isB1());
        assertEquals(dataSrc.isB2(), target.isB2());
        assertNull(target.getNullStr());
    }

    @Test
    public void testFormatBytes() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        byte[] json = jsonMapper.writeValueAsBytes(map);
        assertArrayEquals(json, JsonKit.toJsonBytes(map));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonKit.toJsonBytes(map, out);
        assertArrayEquals(json, out.toByteArray());
        out.reset();
        WritableByteChannel channel = Channels.newChannel(out);
        JsonKit.toJsonBytes(map, channel);
        assertArrayEquals(json, out.toByteArray());
    }

    @Test
    public void testFormatBase64() throws Exception {
        String content = "hello world";
        byte[] bytes = content.getBytes(CharsKit.defaultCharset());
        String base64 = Base64.getEncoder().encodeToString(bytes);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        // byte[]
        map.put("b", bytes);
        String json1 = jsonMapper.writeValueAsString(map);
        assertEquals(json1, JsonKit.toJsonString(map));
        assertEquals(base64, JsonKit.parse(json1).asMap().get("b"));
        map.put("b", new byte[0]);
        String json2 = jsonMapper.writeValueAsString(map);
        assertEquals(json2, JsonKit.toJsonString(map));
        assertEquals("", JsonKit.parse(json2).asMap().get("b"));
        {
            // ByteBuffer
            map.put("b", ByteBuffer.wrap(bytes));
            assertEquals(json1, JsonKit.toJsonString(map));
            map.put("b", ByteBuffer.wrap(new byte[0]));
            assertEquals(json2, JsonKit.toJsonString(map));
        }
        {
            // InputStream
            map.put("b", new ByteArrayInputStream(bytes));
            assertEquals(json1, JsonKit.toJsonString(map));
            map.put("b", new ByteArrayInputStream(new byte[0]));
            assertEquals(json2, JsonKit.toJsonString(map));
        }
        {
            // Channel
            map.put("b", Channels.newChannel(new ByteArrayInputStream(bytes)));
            assertEquals(json1, JsonKit.toJsonString(map));
            map.put("b", Channels.newChannel(new ByteArrayInputStream(new byte[0])));
            assertEquals(json2, JsonKit.toJsonString(map));
        }
    }

    @Test
    public void testJsonKit() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        String json = jsonMapper.writeValueAsString(map);
        assertEquals(
            map,
            JsonKit.parse(json).asMap()
        );
        assertEquals(
            map,
            JsonKit.parse(new StringReader(json)).asMap()
        );
        assertEquals(
            map,
            JsonKit.parse(IOKit.newInputStream(new StringReader(json))).asMap()
        );
        assertEquals(
            map,
            JsonKit.parse(Channels.newChannel(IOKit.newInputStream(new StringReader(json)))).asMap()
        );
    }

    @Test
    public void testJsonData() throws Exception {
        {
            // null
            JsonData data = JsonData.ofNull();
            assertSame(data, JsonData.ofNull());
            assertSame(JsonType.NULL, data.type());
            assertEquals("null", data.toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            WritableByteChannel channel = Channels.newChannel(out);
            data.writeTo(channel);
            assertEquals("null", out.toString(CharsKit.defaultCharset().name()));
            assertTrue(data.isNull());
            assertThrows(JsonDataException.class, data::asString);
            assertThrows(JsonDataException.class, data::asInt);
            assertThrows(JsonDataException.class, data::asLong);
            assertThrows(JsonDataException.class, data::asFloat);
            assertThrows(JsonDataException.class, data::asDouble);
            assertThrows(JsonDataException.class, data::asBigDecimal);
            assertThrows(JsonDataException.class, data::asBoolean);
            assertThrows(JsonDataException.class, data::asDataMap);
            assertThrows(JsonDataException.class, data::asDataList);
            assertThrows(JsonDataException.class, () -> data.toObject(String.class));
        }
        {
            // boolean
            JsonData tData = JsonData.ofBoolean(true);
            assertSame(tData, JsonData.ofBoolean(true));
            assertSame(JsonType.BOOLEAN, tData.type());
            assertEquals("true", tData.toString());
            assertEquals(true, tData.toObject(boolean.class));
            assertEquals(true, tData.toObject(Boolean.class));
            assertEquals("true", tData.toObject(String.class));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            WritableByteChannel channel = Channels.newChannel(out);
            tData.writeTo(channel);
            assertEquals("true", out.toString(CharsKit.defaultCharset().name()));
            assertFalse(tData.isNull());
            assertThrows(JsonDataException.class, tData::asString);
            assertThrows(JsonDataException.class, tData::asInt);
            assertThrows(JsonDataException.class, tData::asLong);
            assertThrows(JsonDataException.class, tData::asFloat);
            assertThrows(JsonDataException.class, tData::asDouble);
            assertThrows(JsonDataException.class, tData::asBigDecimal);
            assertTrue(tData.asBoolean());
            assertThrows(JsonDataException.class, tData::asDataMap);
            assertThrows(JsonDataException.class, tData::asDataList);

            JsonData fData = JsonData.ofBoolean(false);
            assertSame(fData, JsonData.ofBoolean(false));
            assertSame(JsonType.BOOLEAN, fData.type());
            assertEquals("false", fData.toString());
            assertEquals(false, fData.toObject(boolean.class));
            assertEquals(false, fData.toObject(Boolean.class));
            assertEquals("false", fData.toObject(String.class));
            out.reset();
            fData.writeTo(channel);
            assertEquals("false", out.toString(CharsKit.defaultCharset().name()));
            assertFalse(fData.isNull());
            assertThrows(JsonDataException.class, fData::asString);
            assertThrows(JsonDataException.class, fData::asInt);
            assertThrows(JsonDataException.class, fData::asLong);
            assertThrows(JsonDataException.class, fData::asFloat);
            assertThrows(JsonDataException.class, fData::asDouble);
            assertThrows(JsonDataException.class, fData::asBigDecimal);
            assertFalse(fData.asBoolean());
            assertThrows(JsonDataException.class, fData::asDataMap);
            assertThrows(JsonDataException.class, fData::asDataList);
        }
        {
            // string
            String value = "123456";
            JsonData data = JsonData.ofString(value);
            assertNotSame(data, JsonData.ofString(value));
            assertSame(JsonType.STRING, data.type());
            assertEquals(jsonMapper.writeValueAsString(value), data.toString());
            assertEquals(123456, data.toObject(int.class));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            WritableByteChannel channel = Channels.newChannel(out);
            data.writeTo(channel);
            assertEquals(jsonMapper.writeValueAsString(value), out.toString(CharsKit.defaultCharset().name()));
            assertFalse(data.isNull());
            assertSame(value, data.asString());
            assertThrows(JsonDataException.class, data::asInt);
            assertThrows(JsonDataException.class, data::asLong);
            assertThrows(JsonDataException.class, data::asFloat);
            assertThrows(JsonDataException.class, data::asDouble);
            assertThrows(JsonDataException.class, data::asBigDecimal);
            assertThrows(JsonDataException.class, data::asBoolean);
            assertThrows(JsonDataException.class, data::asDataMap);
            assertThrows(JsonDataException.class, data::asDataList);
        }
        {
            // number
            int value = 66;
            JsonData data = JsonData.ofNumber(value);
            assertNotSame(data, JsonData.ofNumber(value));
            assertSame(JsonType.NUMBER, data.type());
            assertEquals(jsonMapper.writeValueAsString(value), data.toString());
            assertEquals("66", data.toObject(String.class));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            WritableByteChannel channel = Channels.newChannel(out);
            data.writeTo(channel);
            assertEquals(jsonMapper.writeValueAsString(value), out.toString(CharsKit.defaultCharset().name()));
            assertFalse(data.isNull());
            assertThrows(JsonDataException.class, data::asString);
            assertEquals(value, data.asNumber());
            assertEquals(value, data.asInt());
            assertEquals(value, data.asLong());
            assertEquals(value, data.asFloat());
            assertEquals(value, data.asDouble());
            assertEquals(value, data.asBigDecimal().intValue());
            assertThrows(JsonDataException.class, data::asBoolean);
            assertThrows(JsonDataException.class, data::asDataMap);
            assertThrows(JsonDataException.class, data::asDataList);
            // BigDecimal
            BigDecimal decimal = new BigDecimal("123.456");
            assertSame(decimal, JsonData.ofNumber(decimal).asBigDecimal());
        }
        {
            // object
            Map<String, Object> value = MapKit.map("a", 1, "b", 2);
            JsonData data = JsonData.ofMap(value);
            assertNotSame(data, JsonData.ofMap(value));
            assertSame(JsonType.OBJECT, data.type());
            assertEquals(jsonMapper.writeValueAsString(value), data.toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            WritableByteChannel channel = Channels.newChannel(out);
            data.writeTo(channel);
            assertEquals(jsonMapper.writeValueAsString(value), out.toString(CharsKit.defaultCharset().name()));
            assertFalse(data.isNull());
            assertThrows(JsonDataException.class, data::asString);
            assertThrows(JsonDataException.class, data::asInt);
            assertThrows(JsonDataException.class, data::asLong);
            assertThrows(JsonDataException.class, data::asFloat);
            assertThrows(JsonDataException.class, data::asDouble);
            assertThrows(JsonDataException.class, data::asBigDecimal);
            assertThrows(JsonDataException.class, data::asBoolean);
            assertSame(value, data.asMap());
            assertEquals(value, data.asDataMap());
            assertThrows(JsonDataException.class, data::asDataList);
        }
        {
            // array
            List<Object> list = ListKit.list(1, 2, 3);
            JsonData lData = JsonData.ofList(list);
            assertNotSame(lData, JsonData.ofList(list));
            assertSame(JsonType.ARRAY, lData.type());
            assertEquals(jsonMapper.writeValueAsString(list), lData.toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            WritableByteChannel channel = Channels.newChannel(out);
            lData.writeTo(channel);
            assertEquals(jsonMapper.writeValueAsString(list), out.toString(CharsKit.defaultCharset().name()));
            assertFalse(lData.isNull());
            assertThrows(JsonDataException.class, lData::asString);
            assertThrows(JsonDataException.class, lData::asInt);
            assertThrows(JsonDataException.class, lData::asLong);
            assertThrows(JsonDataException.class, lData::asFloat);
            assertThrows(JsonDataException.class, lData::asDouble);
            assertThrows(JsonDataException.class, lData::asBigDecimal);
            assertThrows(JsonDataException.class, lData::asBoolean);
            assertThrows(JsonDataException.class, lData::asDataMap);
            assertSame(list, lData.asList());
            assertEquals(list, lData.asDataList());
            // object
            Object[] array = new Object[]{1, 2, 3};
            JsonData aData = JsonData.ofArray(array);
            assertNotSame(aData, JsonData.ofArray(array));
            assertSame(JsonType.ARRAY, aData.type());
            assertEquals(jsonMapper.writeValueAsString(array), aData.toString());
            out.reset();
            aData.writeTo(channel);
            assertEquals(jsonMapper.writeValueAsString(array), out.toString(CharsKit.defaultCharset().name()));
            assertFalse(aData.isNull());
            assertThrows(JsonDataException.class, aData::asString);
            assertThrows(JsonDataException.class, aData::asInt);
            assertThrows(JsonDataException.class, aData::asLong);
            assertThrows(JsonDataException.class, aData::asFloat);
            assertThrows(JsonDataException.class, aData::asDouble);
            assertThrows(JsonDataException.class, aData::asBigDecimal);
            assertThrows(JsonDataException.class, aData::asBoolean);
            assertThrows(JsonDataException.class, aData::asDataMap);
            assertArrayEquals(array, aData.asList().toArray());
            assertArrayEquals(array, aData.asDataList().toArray());
        }
        {
            // error
            assertThrows(IORuntimeException.class, () -> JsonData.ofString("123456").writeTo(new ErrorAppender()));
        }
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
        private String nullStr;
        private boolean b1;
        private boolean b2;

        private DataPack o1;
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
    public static class DataTarget {
        private String s1;
        private String d1;
        private String d2;
        private BigDecimal n1;
        private BigDecimal n2;
        private String e1;
        private Integer nullStr;
        private boolean b1;
        private boolean b2;

        private DataPack o1;
        private Map<String, Object> o2;

        private String fmt1;
        private String fmt2;
        private BigDecimal fmt3;
        private BigDecimal fmt4;
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPack {
        private String s1;
        private String s2;
    }

    @Data
    @EqualsAndHashCode
    public static class ComplexData {
        private int i1;
        private long l1;
        private String str1;
        private Integer ii1;
        private Long ll1;
        private BigDecimal bb1;
        private long[] la1;
        private BigDecimal[] ba1;
        private List<String> sa1;
        private int i2;
        private long l2;
        private String str2;
        private Integer ii2;
        private Long ll2;
        private BigDecimal bb2;
        private long[] la2;
        private BigDecimal[] ba2;
        private List<String> sa2;
        private int i3;
        private long l3;
        private String str3;
        private Integer ii3;
        private Long ll3;
        private BigDecimal bb3;
        private long[] la3;
        private BigDecimal[] ba3;
        private List<String> sa3;
    }

    public enum DataEnum {
        A, B, C
    }
}
