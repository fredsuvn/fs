package tests.object.convert;

import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.date.DateKit;
import space.sunqian.fs.base.exception.UnreachablePointException;
import space.sunqian.fs.base.number.NumFormatter;
import space.sunqian.fs.base.number.NumKit;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConvertException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.convert.UnsupportedObjectConvertException;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.reflect.TypeKit;
import space.sunqian.fs.reflect.TypeRef;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConvertTest implements PrintTest {

    @Test
    public void testObjectConverter() throws Exception {
        testObjectConverter(ObjectConverter.defaultConverter());
    }

    private void testObjectConverter(ObjectConverter converter) throws Exception {
        A a = new A("1", "2", "3");
        assertEquals(new B(1L, 2L, 3L), converter.convert(a, B.class));
        assertEquals(new B(1L, 2L, 3L), converter.convert(a, A.class, B.class));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(null, B.class));
        ObjectConverter converter2 = ObjectConverter.newConverter(converter.asHandler());
        assertEquals(new B(1L, 2L, 3L), converter2.convert(a, B.class));
        assertEquals(new B(1L, 2L, 3L), converter2.convert(a, A.class, B.class));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter2.convert(null, B.class));
        {
            // error during handler
            ObjectConverter.Handler handler =
                (src, srcType, target, converter1, options) -> {
                    throw new UnreachablePointException();
                };
            ObjectConverter cvt = converter.withFirstHandler(handler);
            ObjectConvertException e = assertThrows(ObjectConvertException.class, () ->
                cvt.convert(a, B.class));
            assertTrue(e.getCause() instanceof UnreachablePointException);
        }
        {
            // break during handler
            ObjectConverter.Handler handler =
                (src, srcType, target, converter1, options) ->
                    ObjectConverter.Status.HANDLER_BREAK;
            ObjectConverter cvt = converter.withFirstHandler(handler);
            UnsupportedObjectConvertException e = assertThrows(UnsupportedObjectConvertException.class, () ->
                cvt.convert(a, B.class, ConvertOption.ignoreNull(true)));
            assertEquals(e.sourceObject(), a);
            assertEquals(A.class, e.sourceObjectType());
            assertEquals(B.class, e.targetType());
            assertSame(e.converter(), cvt);
            assertArrayEquals(e.options(), ArrayKit.array(ConvertOption.ignoreNull(true)));
        }
        {
            // withLastHandler
            class X<T> {}
            ObjectConverter.Handler handler =
                (src, srcType, target, converter1, options) -> {
                    throw new UnreachablePointException();
                };
            ObjectConverter cvt = converter.withFirstHandler(handler);
            ObjectConvertException e = assertThrows(ObjectConvertException.class, () ->
                cvt.convert(a, X.class.getTypeParameters()[0], ConvertOption.strictTargetTypeMode(true)));
            assertTrue(e.getCause() instanceof UnreachablePointException);
        }
        {
            // assignable
            String hello = "hello";
            CharSequence cs = converter.convert(hello, CharSequence.class);
            assertSame(hello, cs);
        }
        {
            // strict target type
            Type wildLower = ((ParameterizedType) (F.class.getField("l1").getGenericType()))
                .getActualTypeArguments()[0];
            Object obj = new Object();
            assertEquals(
                obj.toString(),
                converter.convert(obj, wildLower, ConvertOption.strictTargetTypeMode(false))
            );
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, wildLower, ConvertOption.strictTargetTypeMode(true)));
            assertEquals(converter.convert(obj, wildLower), obj.toString());
            Type upperLower = ((ParameterizedType) (F.class.getField("l2").getGenericType()))
                .getActualTypeArguments()[0];
            assertEquals(
                obj.toString(),
                converter.convert(obj, upperLower, ConvertOption.strictTargetTypeMode(false))
            );
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, upperLower, ConvertOption.strictTargetTypeMode(true)));
            assertEquals(converter.convert(obj, upperLower), obj.toString());
            class X<T> {}
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, X.class.getTypeParameters()[0], ConvertOption.strictTargetTypeMode(true)));
            assertSame(converter.convert(obj, X.class.getTypeParameters()[0]), obj);
        }

    }

    @Test
    public void testCommonHandler() {
        A a = new A("1", "2", "3");
        ObjectConverter converter = ObjectConverter.defaultConverter();
        // to enum
        assertEquals(E.A, converter.convert("A", E.class));
        assertNull(converter.convert("B", E.class));
        // to map
        Map<String, String> map1 = converter.convert(a, new TypeRef<Map<String, String>>() {});
        assertEquals(map1, MapKit.map("first", "1", "second", "2", "third", "3"));
        Map<String, String> map2 = Fs.as(converter.convert(a, A.class, new TypeRef<Map<String, String>>() {}.type()));
        assertEquals(map2, MapKit.map("first", "1", "second", "2", "third", "3"));
        Map<String, String> map3 = converter.convert(a, new TypeRef<Map<String, String>>() {},
            ConvertOption.builderProvider(BuilderProvider.newProvider(
                BuilderProvider.defaultProvider().asHandler())
            )
        );
        assertEquals(map3, MapKit.map("first", "1", "second", "2", "third", "3"));
        class Err {}
        assertThrows(ObjectConvertException.class, () -> converter.convert(a, Err.class));
        class N {}
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(a, N.class));
        {
            // to String
            Date now = new Date();
            String nowDate = DateKit.format(now);
            Charset c8859 = StandardCharsets.ISO_8859_1;
            assertEquals("123", converter.convert("123".toCharArray(), String.class));
            assertEquals("123.456", converter.convert(new BigDecimal("123.456"), String.class));
            assertEquals(converter.convert(now, String.class), nowDate);
            assertEquals(converter.convert(now.toInstant(), String.class), nowDate);
            assertEquals("123", converter.convert("123".getBytes(c8859), String.class, ConvertOption.charset(c8859)));
            assertEquals("123", converter.convert(ByteBuffer.wrap("123".getBytes(c8859)), String.class));
            ByteArrayInputStream in = new ByteArrayInputStream("123".getBytes(c8859));
            assertEquals("123", converter.convert(in, String.class));
            in.reset();
            assertEquals("123", converter.convert(in, String.class));
            in.reset();
            assertEquals(
                "123",
                converter.convert(Channels.newChannel(in), String.class)
            );
            CharArrayReader reader = new CharArrayReader("123".toCharArray());
            assertEquals("123", converter.convert(reader, String.class));
            Object x = new Object();
            assertEquals(converter.convert(x, String.class), x.toString());
        }
        {
            // String to
            assertArrayEquals(
                converter.convert("123", byte[].class),
                "123".getBytes(CharsKit.defaultCharset())
            );
            assertEquals(
                converter.convert("123", ByteBuffer.class),
                ByteBuffer.wrap("123".getBytes(CharsKit.defaultCharset()))
            );
            assertArrayEquals(
                converter.convert("123", char[].class),
                "123".toCharArray()
            );
            assertEquals(
                converter.convert("123", CharBuffer.class),
                CharBuffer.wrap("123")
            );
        }
        class X<T> {}
        Type nonClass = X.class.getTypeParameters()[0];
        {
            // to Number
            assertEquals(123.0, converter.convert(123, double.class));
            assertEquals(123.0, converter.convert(123, int.class, Double.class));
            assertEquals(123, converter.convert("123", int.class));
            assertEquals(123L, converter.convert("123", long.class));
            assertEquals(123L, converter.convert("123", Long.class));
            assertEquals(123.123, converter.convert("123.123", double.class));
            assertEquals(
                "123.12",
                converter.convert(123.12345, String.class, ConvertOption.numFormatter(NumFormatter.ofPattern("#.00")))
            );
            Date now = new Date();
            assertEquals(converter.convert(now, long.class), now.getTime());
            assertEquals(converter.convert(now.toInstant(), Long.class), now.getTime());
            assertEquals(123L, converter.convert(123, long.class));
            assertEquals(123L, converter.convert(123, Long.class));
            assertEquals(new BigDecimal("123"), converter.convert(123, BigDecimal.class));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(a, long.class));
        }
        {
            // to boolean
            assertEquals(true, converter.convert(true, boolean.class));
            assertEquals(false, converter.convert(false, boolean.class));
            assertEquals(true, converter.convert("true", Boolean.class));
            assertEquals(false, converter.convert("true0", Boolean.class));
            assertEquals(false, converter.convert(0, boolean.class));
            assertEquals(true, converter.convert(1, boolean.class));
            assertEquals(true, converter.convert(-1, boolean.class));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(a, boolean.class));
        }
        {
            // to time
            Date now = new Date();
            String nowDate = DateKit.format(now);
            assertEquals(converter.convert(nowDate, Date.class), now);
            assertEquals(converter.convert(now, Date.class), now);
            assertEquals(converter.convert(now.toInstant(), Date.class), now);
            assertEquals(converter.convert(now, Instant.class), now.toInstant());
            assertEquals(converter.convert(now.toInstant(), Instant.class), now.toInstant());
            assertEquals(converter.convert(now.getTime(), Date.class), now);
            assertEquals(converter.convert(now.getTime(), long.class, Date.class), now);
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(new X<String>(), nonClass, Date.class));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(a, Date.class));
        }
        Date now = new Date();
        DateFormatter nowFormat = DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = nowFormat.format(now);
        {
            // to array
            String[] strArray = new String[]{"1", "2", "3"};
            assertArrayEquals(new int[]{1, 2, 3}, converter.convert(strArray, int[].class));
            List<String> strList = ListKit.list("1", "2", "3");
            assertArrayEquals(new int[]{1, 2, 3}, converter.convert(strList, int[].class));
            assertArrayEquals(
                new int[]{1, 2, 3},
                Fs.as(converter.convert(strList, new TypeRef<List<String>>() {}.type(), int[].class))
            );
            assertArrayEquals(new int[]{1, 2, 3}, converter.convert(new Iterable<String>() {
                @Override
                public @Nonnull Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, int[].class));
            // with options
            Date[] dateArray = new Date[]{now, now, now};
            assertArrayEquals(
                converter.convert(dateArray, String[].class, ConvertOption.dateFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            List<Date> dateList = ListKit.list(now, now, now);
            assertArrayEquals(Fs.as(converter.convert(
                    dateList,
                    new TypeRef<List<Date>>() {}.type(),
                    String[].class,
                    ConvertOption.dateFormatter(nowFormat))
                ),
                new String[]{nowStr, nowStr, nowStr}
            );
            assertArrayEquals(Fs.as(converter.convert(new Iterable<Date>() {

                        @Override
                        public @Nonnull Iterator<Date> iterator() {
                            return dateList.iterator();
                        }
                    }, new TypeRef<Iterable<Date>>() {}.type(), String[].class, ConvertOption.dateFormatter(nowFormat))
                ),
                new String[]{nowStr, nowStr, nowStr}
            );
            // errors
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", int[].class));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", new TypeRef<List<String>>() {}.type(), int[].class));
        }
        {
            // to generic array
            String[] strArray = new String[]{"1", "2", "3"};
            GenericArrayType intsType = TypeKit.arrayType(Integer.class);
            assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.convert(strArray, intsType));
            List<String> strList = ListKit.list("1", "2", "3");
            assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.convert(strList, intsType));
            assertArrayEquals(
                new Integer[]{1, 2, 3},
                (Integer[]) converter.convert(strList, new TypeRef<List<String>>() {}.type(), intsType)
            );
            assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.convert(new Iterable<String>() {

                @Override
                public @Nonnull Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, intsType));
            // with options
            GenericArrayType stringsType = TypeKit.arrayType(String.class);
            Date[] dateArray = new Date[]{now, now, now};
            assertArrayEquals(
                (String[]) converter.convert(dateArray, stringsType, ConvertOption.dateFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            List<Date> dateList = ListKit.list(now, now, now);
            assertArrayEquals((String[]) converter.convert(
                    dateList, new TypeRef<List<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            assertArrayEquals((String[]) converter.convert(new Iterable<Date>() {

                    @Override
                    public @Nonnull Iterator<Date> iterator() {
                        return dateList.iterator();
                    }
                }, new TypeRef<Iterable<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            // errors
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", intsType));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", new TypeRef<List<String>>() {}.type(), intsType));
        }
        {
            // to collection
            String[] strArray = new String[]{"1", "2", "3"};
            Type intsType = new TypeRef<List<Integer>>() {}.type();
            assertEquals(converter.convert(strArray, intsType), ListKit.list(1, 2, 3));
            List<String> strList = ListKit.list("1", "2", "3");
            assertEquals(converter.convert(strList, Set.class, List.class), ListKit.list("1", "2", "3"));
            assertEquals(
                converter.convert(strList, new TypeRef<List<String>>() {}.type(), intsType),
                ListKit.list(1, 2, 3)
            );
            assertEquals(converter.convert(new Iterable<String>() {

                @Override
                public @Nonnull Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, intsType), ListKit.list(1, 2, 3));
            // with options
            Type stringsType = new TypeRef<List<String>>() {}.type();
            Date[] dateArray = new Date[]{now, now, now};
            assertEquals(
                converter.convert(dateArray, stringsType, ConvertOption.dateFormatter(nowFormat)),
                ListKit.list(nowStr, nowStr, nowStr)
            );
            List<Date> dateList = ListKit.list(now, now, now);
            assertEquals(converter.convert(
                    dateList, new TypeRef<List<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
                ListKit.list(nowStr, nowStr, nowStr)
            );
            assertEquals(converter.convert(new Iterable<Date>() {

                    @Override
                    public @Nonnull Iterator<Date> iterator() {
                        return dateList.iterator();
                    }
                }, new TypeRef<Iterable<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
                ListKit.list(nowStr, nowStr, nowStr)
            );
            // errors
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", intsType));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", new TypeRef<List<String>>() {}.type(), intsType));
        }
    }

    @Test
    public void testComplexConvert() {
        Date now = new Date();
        CA ca = new CA("1", ListKit.list("1", "2", "3"), new A("1", "2", "3"), DateKit.format(now));
        assertEquals(
            ObjectConverter.defaultConverter().convert(ca, CB.class,
                ConvertOption.objectCopier(ObjectCopier.defaultCopier()),
                ConvertOption.dateFormatter(DateFormatter.defaultFormatter()),
                ConvertOption.ioOperator(IOOperator.defaultOperator())
            ),
            new CB(1, ListKit.list(1, 2, 3), new B(1L, 2L, 3L), DateKit.parse(ca.date, LocalDateTime.class))
        );
    }

    @Test
    public void testDefaultOptions() {
        ObjectConverter converter = ObjectConverter.defaultConverter();
        ObjectConverter converterOps = converter.withDefaultOptions(
            ConvertOption.ignoreNull(true)
        );
        assertEquals(
            Collections.emptyList(),
            converter.defaultOptions()
        );
        assertEquals(
            ListKit.list(ConvertOption.ignoreNull(true)),
            converterOps.defaultOptions()
        );
        DataObject source = new DataObject("code", null);
        Map<String, Object> dst1 = converter.convert(source, new TypeRef<Map<String, Object>>() {});
        assertTrue(dst1.containsKey("name"));
        Map<String, Object> dst2 = converterOps.convert(source, new TypeRef<Map<String, Object>>() {});
        assertFalse(dst2.containsKey("name"));
    }

    @Test
    public void testConvertToSpecific() throws Exception {
        DataObject dataObject = new DataObject("code", "name");
        Map<String, String> dataMap = MapKit.map("code", "code", "name", "name");
        {
            // to TreeMap
            Map<String, String> map = ObjectConverter.defaultConverter()
                .convert(dataObject, new TypeRef<TreeMap<String, String>>() {}
                );
            assertEquals(dataMap, map);
        }
        {
            // to ConcurrentSkipListMap
            Map<String, String> map = ObjectConverter.defaultConverter()
                .convert(dataObject, new TypeRef<ConcurrentSkipListMap<String, String>>() {}
                );
            assertEquals(dataMap, map);
        }
        {
            // to List
            assertEquals(
                ListKit.list("code", "name"),
                ObjectConverter.defaultConverter()
                    .convert(new String[]{"code", "name"}, new TypeRef<LinkedList<String>>() {}
                    )
            );
            assertEquals(
                ListKit.list("code", "name"),
                ObjectConverter.defaultConverter()
                    .convert(new String[]{"code", "name"}, new TypeRef<CopyOnWriteArrayList<String>>() {}
                    )
            );
            assertEquals(
                SetKit.set("code", "name"),
                ObjectConverter.defaultConverter()
                    .convert(new String[]{"code", "name"}, new TypeRef<TreeSet<String>>() {}
                    )
            );
            assertEquals(
                SetKit.set("code", "name"),
                ObjectConverter.defaultConverter()
                    .convert(new String[]{"code", "name"}, new TypeRef<ConcurrentSkipListSet<String>>() {}
                    )
            );
        }
        {
            // Map<K, V>
            Map<String, Object> map = new HashMap<>();
            map.put("longNum", 1);
            assertEquals(
                1L,
                ObjectConverter.defaultConverter().convertMap(map, MapObject.class).getLongNum()
            );
            assertEquals(
                1L,
                ObjectConverter.defaultConverter().convertMap(map, new TypeRef<MapObject>() {}).getLongNum()
            );
            assertEquals(
                1L,
                ((MapObject) ObjectConverter.defaultConverter().convertMap(map, (Type) MapObject.class)).getLongNum()
            );
        }
    }

    @Test
    public void testCollections() {
        assertEquals(
            ListKit.list(1, 2),
            ObjectConverter.defaultConverter().convert(
                ListKit.list("1", "2"),
                // new TypeRef<List<String>>() {}.type(),
                new TypeRef<List<Integer>>() {}.type()
            )
        );
        assertEquals(
            ListKit.list(new MapObject(1L), new MapObject(2L)),
            ObjectConverter.defaultConverter().convert(
                ListKit.list(MapKit.map("longNum", "1"), MapKit.map("longNum", "2")),
                // new TypeRef<List<Map<String, String>>>() {}.type(),
                new TypeRef<List<MapObject>>() {}.type()
            )
        );
    }

    @Test
    public void testNewInstance() {
        Object str1 = new Object();
        Object str2 = ObjectConverter.defaultConverter().convert(str1, Object.class, ConvertOption.newInstanceMode(true));
        assertNotSame(str1, str2);
        Object str3 = ObjectConverter.defaultConverter().convert(str1, Object.class);
        assertSame(str1, str3);
        Object str4 = ObjectConverter.defaultConverter().convert(str1, Object.class, ConvertOption.newInstanceMode(false));
        assertSame(str1, str4);
    }

    @Test
    public void testAnnotation() {
        {
            // test pattern cache
            // Note soft-cache does not guarantee the same instance, so "assertSame" may not always work as expected.
            Option<?, DateFormatter> df1 = ConvertKit.getDateFormatterOption(
                "yyyy-MM-dd", ZoneId.systemDefault()
            );
            Option<?, DateFormatter> df2 = ConvertKit.getDateFormatterOption(
                "yyyy-MM-dd", ZoneId.systemDefault()
            );
            Option<?, DateFormatter> df3 = ConvertKit.getDateFormatterOption(
                "yyyy-MM-dd HH:mm:ss", ZoneId.systemDefault()
            );
            assertSame(df1, df2);
            assertNotSame(df1, df3);
            assertNotSame(df2, df3);
            Option<?, NumFormatter> nf1 = ConvertKit.getNumFormatterOption("#.0000");
            Option<?, NumFormatter> nf2 = ConvertKit.getNumFormatterOption("#.0000");
            Option<?, NumFormatter> nf3 = ConvertKit.getNumFormatterOption("#.000000");
            assertSame(nf1, nf2);
            assertNotSame(nf1, nf3);
            assertNotSame(nf2, nf3);
        }
        {
            // test annotation support: object to object
            ZonedDateTime zdt = ZonedDateTime.now();
            Ann1 ann1 = new Ann1();
            ann1.setDate1(DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
            ann1.setDate2(DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zdt));
            ann1.setDate3(zdt.toLocalDateTime());
            ann1.setDate4(Date.from(zdt.toInstant()));
            ann1.setNum1(new BigDecimal("123.456789"));
            ann1.setNum2(123.456789);
            ann1.setNum3(new BigDecimal("123.456789"));
            ann1.setComplex1(DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
            Ann2 ann2 = ObjectConverter.defaultConverter().convert(ann1, Ann2.class);
            assertEquals(
                DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse(ann1.getDate1(), LocalDateTime.class),
                ann2.getDate1()
            );
            assertEquals(
                DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", ZoneId.of("UTC+2")).parse(ann1.getDate2(), LocalDateTime.class),
                ann2.getDate2()
            );
            assertEquals(
                DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ann1.getDate3()),
                ann2.getDate3()
            );
            assertEquals(
                DateFormatter.ofPattern("YYYY-MM-dd HH:mm:ss", ZoneId.of("Asia/Shanghai")).format(ann1.getDate4()),
                ann2.getDate4()
            );
            assertEquals(
                NumFormatter.ofPattern(NumKit.DEFAULT_PATTERN).format(ann1.getNum1()),
                ann2.getNum1()
            );
            assertEquals(
                NumFormatter.ofPattern("#.000").format(ann1.getNum2()),
                ann2.getNum2()
            );
            assertEquals(
                ann1.getNum3().toString(),
                ann2.getNum3()
            );
            assertEquals(
                DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse(ann1.getComplex1(), Date.class),
                ann2.getComplex1()
            );
        }
        {
            // test annotation support: map to object
            ZonedDateTime zdt = ZonedDateTime.now();
            Map<String, Object> ann1 = new HashMap<>();
            ann1.put("date1", DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
            ann1.put("date2", DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zdt));
            ann1.put("date3", zdt.toLocalDateTime());
            ann1.put("date4", Date.from(zdt.toInstant()));
            ann1.put("num1", new BigDecimal("123.456789"));
            ann1.put("num2", 123.456789);
            ann1.put("num3", new BigDecimal("123.456789"));
            ann1.put("complex1", DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
            Ann2 ann2 = ObjectConverter.defaultConverter().convert(ann1, Ann2.class);
            assertEquals(
                DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse((CharSequence) ann1.get("date1"), LocalDateTime.class),
                ann2.getDate1()
            );
            assertEquals(
                DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", ZoneId.of("UTC+2")).parse((CharSequence) ann1.get("date2"), LocalDateTime.class),
                ann2.getDate2()
            );
            assertEquals(
                DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((LocalDateTime) ann1.get("date3")),
                ann2.getDate3()
            );
            assertEquals(
                DateFormatter.ofPattern("YYYY-MM-dd HH:mm:ss", ZoneId.of("Asia/Shanghai")).format((Date) ann1.get("date4")),
                ann2.getDate4()
            );
            assertEquals(
                ((BigDecimal) ann1.get("num1")).toString(),
                ann2.getNum1()
            );
            assertEquals(
                NumFormatter.ofPattern("#.000").format((Double) ann1.get("num2")),
                ann2.getNum2()
            );
            assertEquals(
                ((BigDecimal) ann1.get("num3")).toString(),
                ann2.getNum3()
            );
            assertEquals(
                DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse((CharSequence) ann1.get("complex1"), Date.class),
                ann2.getComplex1()
            );
        }
    }

    @Test
    public void testConvertKit() throws Exception {
        {
            // map parser
            assertSame(ConvertKit.mapParser(), ConvertKit.mapParser());
            assertSame(ConvertKit.mapParser(), ConvertOption.getMapParser(ConvertOption.ignoreNull(true)));
            assertNotEquals(
                ConvertKit.mapParser(),
                ConvertOption.getMapParser(ConvertOption.schemaParser(MapParser.defaultParser()))
            );
            assertSame(
                MapParser.defaultParser(),
                ConvertOption.getMapParser(ConvertOption.schemaParser(MapParser.defaultParser()))
            );
        }
        {
            // object parser
            assertSame(ConvertKit.objectParser(), ConvertKit.objectParser());
            assertSame(ConvertKit.objectParser(), ConvertOption.getObjectParser(ConvertOption.ignoreNull(true)));
            assertNotEquals(
                ConvertKit.objectParser(),
                ConvertOption.getObjectParser(ConvertOption.schemaParser(ObjectParser.defaultParser()))
            );
            assertSame(
                ObjectParser.defaultParser(),
                ConvertOption.getObjectParser(ConvertOption.schemaParser(ObjectParser.defaultParser()))
            );
        }
        {
            // builder provider
            assertSame(ConvertKit.builderProvider(), ConvertKit.builderProvider());
            assertSame(ConvertKit.builderProvider(), ConvertOption.getBuilderProvider(ConvertOption.ignoreNull(true)));
            assertNotEquals(
                ConvertKit.builderProvider(),
                ConvertOption.getBuilderProvider(ConvertOption.builderProvider(BuilderProvider.defaultProvider()))
            );
            assertSame(
                BuilderProvider.defaultProvider(),
                ConvertOption.getBuilderProvider(ConvertOption.builderProvider(BuilderProvider.defaultProvider()))
            );
        }
    }

    @Test
    public void testException() {
        {
            // ObjectConversionException
            assertThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException();
            });
            assertThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException("");
            });
            assertThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException("", new RuntimeException());
            });
            assertThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException(new RuntimeException());
            });
            assertThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException(Object.class, String.class);
            });
            assertThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException(Object.class, String.class, new RuntimeException());
            });
        }
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class A {
        private String first;
        private String second;
        private String third;
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class B {
        private Long first;
        private Long second;
        private Long third;
    }

    public static class F {
        public List<? super String> l1;
        public List<? extends String> l2;
    }

    public enum E {
        A,
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CA {
        private String p1;
        private List<String> p2;
        private A p3;
        private String date;
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CB {
        private Integer p1;
        private List<Integer> p2;
        private B p3;
        private LocalDateTime date;
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataObject {
        private String code;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MapObject {
        private long longNum;
    }

    @Data
    public static class Ann1 {
        @DatePattern
        private String date1;
        @DatePattern(value = "yyyy-MM-dd HH:mm:ss", zoneId = "Asia/Shanghai")
        private String date2;
        @DatePattern("yyyy-MM-dd HH:mm:ss")
        private LocalDateTime date3;
        private Date date4;
        @NumPattern
        private BigDecimal num1;
        @NumPattern("#.0")
        private double num2;
        private BigDecimal num3;
        @DatePattern
        @NumPattern
        private String complex1;
    }

    @Data
    public static class Ann2 {
        @DatePattern
        private LocalDateTime date1;
        @DatePattern(value = "yyyy-MM-dd HH:mm:ss", zoneId = "UTC+2")
        private LocalDateTime date2;
        @DatePattern("yyyy-MM-dd HH:mm:ss")
        private String date3;
        @DatePattern(value = "yyyy-MM-dd HH:mm:ss", zoneId = "Asia/Shanghai")
        private String date4;
        private String num1;
        @NumPattern("#.000")
        private String num2;
        private String num3;
        @DatePattern
        @NumPattern
        private Date complex1;
    }
}
