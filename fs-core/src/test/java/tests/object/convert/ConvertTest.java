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
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConvertException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.PropertyCopier;
import space.sunqian.fs.object.convert.UnsupportedObjectConvertException;
import space.sunqian.fs.object.create.CreatorProvider;
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
                cvt.convert(a, B.class, ConvertOption.IGNORE_NULL));
            assertEquals(e.sourceObject(), a);
            assertEquals(A.class, e.sourceObjectType());
            assertEquals(B.class, e.targetType());
            assertSame(e.converter(), cvt);
            assertArrayEquals(e.options(), ArrayKit.array(ConvertOption.IGNORE_NULL));
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
                cvt.convert(a, X.class.getTypeParameters()[0], ConvertOption.STRICT_TARGET_TYPE));
            assertTrue(e.getCause() instanceof UnreachablePointException);
        }
        {
            // assignable
            String hello = "hello";
            CharSequence cs = converter.convert(hello, CharSequence.class);
            assertSame(hello, cs);
        }
        {
            // strict type
            Type wildLower = ((ParameterizedType) (F.class.getField("l1").getGenericType()))
                .getActualTypeArguments()[0];
            Object obj = new Object();
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, wildLower, ConvertOption.STRICT_TARGET_TYPE));
            assertEquals(converter.convert(obj, wildLower), obj.toString());
            Type upperLower = ((ParameterizedType) (F.class.getField("l2").getGenericType()))
                .getActualTypeArguments()[0];
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, upperLower, ConvertOption.STRICT_TARGET_TYPE));
            assertEquals(converter.convert(obj, upperLower), obj.toString());
            class X<T> {}
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, X.class.getTypeParameters()[0], ConvertOption.STRICT_TARGET_TYPE));
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
            ConvertOption.creatorProvider(CreatorProvider.newProvider(
                CreatorProvider.defaultProvider().asHandler())
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
                ConvertOption.propertyCopier(PropertyCopier.defaultCopier()),
                ConvertOption.dateFormatter(DateFormatter.defaultFormatter()),
                ConvertOption.ioOperator(IOOperator.defaultOperator())
            ),
            new CB(1, ListKit.list(1, 2, 3), new B(1L, 2L, 3L), DateKit.parse(ca.date, LocalDateTime.class))
        );
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
        Object str2 = ObjectConverter.defaultConverter().convert(str1, Object.class, ConvertOption.NEW_INSTANCE);
        assertNotSame(str1, str2);
        Object str3 = ObjectConverter.defaultConverter().convert(str1, Object.class);
        assertSame(str1, str3);
    }

    @Test
    public void testConvertKit() throws Exception {
        {
            // map parser
            assertSame(ConvertKit.mapParser(), ConvertKit.mapParser());
            assertSame(ConvertKit.mapParser(), ConvertKit.mapParser(ConvertOption.ignoreNull()));
            assertNotEquals(
                ConvertKit.mapParser(),
                ConvertKit.mapParser(ConvertOption.schemaParser(MapParser.defaultParser()))
            );
            assertSame(
                MapParser.defaultParser(),
                ConvertKit.mapParser(ConvertOption.schemaParser(MapParser.defaultParser()))
            );
        }
        {
            // object parser
            assertSame(ConvertKit.objectParser(), ConvertKit.objectParser());
            assertSame(ConvertKit.objectParser(), ConvertKit.objectParser(ConvertOption.ignoreNull()));
            assertNotEquals(
                ConvertKit.objectParser(),
                ConvertKit.objectParser(ConvertOption.schemaParser(ObjectParser.defaultParser()))
            );
            assertSame(
                ObjectParser.defaultParser(),
                ConvertKit.objectParser(ConvertOption.schemaParser(ObjectParser.defaultParser()))
            );
        }
        {
            // creator provider
            assertSame(ConvertKit.creatorProvider(), ConvertKit.creatorProvider());
            assertSame(ConvertKit.creatorProvider(), ConvertKit.creatorProvider(ConvertOption.ignoreNull()));
            assertNotEquals(
                ConvertKit.creatorProvider(),
                ConvertKit.creatorProvider(ConvertOption.creatorProvider(CreatorProvider.defaultProvider()))
            );
            assertSame(
                CreatorProvider.defaultProvider(),
                ConvertKit.creatorProvider(ConvertOption.creatorProvider(CreatorProvider.defaultProvider()))
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
}
