package tests.object.convert;

import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.exception.UnreachablePointException;
import space.sunqian.common.base.time.TimeFormatter;
import space.sunqian.common.base.time.TimeKit;
import space.sunqian.common.collect.ArrayKit;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.collect.MapKit;
import space.sunqian.common.io.IOOperator;
import space.sunqian.common.object.convert.ConvertOption;
import space.sunqian.common.object.convert.DataMapper;
import space.sunqian.common.object.convert.ObjectConvertException;
import space.sunqian.common.object.convert.ObjectConverter;
import space.sunqian.common.object.convert.UnsupportedObjectConvertException;
import space.sunqian.common.object.data.ObjectBuilderProvider;
import space.sunqian.common.runtime.reflect.TypeKit;
import space.sunqian.common.runtime.reflect.TypeRef;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(converter.convert(a, B.class), new B(1L, 2L, 3L));
        assertEquals(converter.convert(a, A.class, B.class), new B(1L, 2L, 3L));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(null, B.class));
        ObjectConverter converter2 = ObjectConverter.newConverter(converter.asHandler());
        assertEquals(converter2.convert(a, B.class), new B(1L, 2L, 3L));
        assertEquals(converter2.convert(a, A.class, B.class), new B(1L, 2L, 3L));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter2.convert(null, B.class));
        {
            // error during handler
            ObjectConverter cvt = converter.withFirstHandler(
                (src, srcType, target, converter1, options) -> {
                    throw new UnreachablePointException();
                });
            ObjectConvertException e = assertThrows(ObjectConvertException.class, () ->
                cvt.convert(a, B.class));
            assertTrue(e.getCause() instanceof UnreachablePointException);
        }
        {
            // break during handler
            ObjectConverter cvt = converter.withFirstHandler(
                (src, srcType, target, converter1, options) ->
                    ObjectConverter.Status.HANDLER_BREAK
            );
            UnsupportedObjectConvertException e = assertThrows(UnsupportedObjectConvertException.class, () ->
                cvt.convert(a, B.class, ConvertOption.IGNORE_NULL));
            assertEquals(e.sourceObject(), a);
            assertEquals(e.sourceObjectType(), A.class);
            assertEquals(e.targetType(), B.class);
            assertSame(e.converter(), cvt);
            assertArrayEquals(e.options(), ArrayKit.array(ConvertOption.IGNORE_NULL));
        }
        {
            // withLastHandler
            class X<T> {}
            ObjectConverter cvt = converter.withLastHandler(
                (src, srcType, target, converter1, options) -> {
                    throw new UnreachablePointException();
                });
            ObjectConvertException e = assertThrows(ObjectConvertException.class, () ->
                cvt.convert(a, X.class.getTypeParameters()[0], ConvertOption.STRICT_TYPE_MODE));
            assertTrue(e.getCause() instanceof UnreachablePointException);
        }
        {
            // assignable
            String hello = "hello";
            CharSequence cs = converter.convert(hello, CharSequence.class);
            assertSame(cs, hello);
        }
        {
            // strict type
            Type wildLower = ((ParameterizedType) (F.class.getField("l1").getGenericType()))
                .getActualTypeArguments()[0];
            Object obj = new Object();
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, wildLower, ConvertOption.STRICT_TYPE_MODE));
            assertEquals(converter.convert(obj, wildLower), obj.toString());
            Type upperLower = ((ParameterizedType) (F.class.getField("l2").getGenericType()))
                .getActualTypeArguments()[0];
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, upperLower, ConvertOption.STRICT_TYPE_MODE));
            assertEquals(converter.convert(obj, upperLower), obj.toString());
            class X<T> {}
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, X.class.getTypeParameters()[0], ConvertOption.STRICT_TYPE_MODE));
            assertSame(converter.convert(obj, X.class.getTypeParameters()[0]), obj);
        }

    }

    @Test
    public void testCommonHandler() {
        A a = new A("1", "2", "3");
        ObjectConverter converter = ObjectConverter.defaultConverter();
        // to enum
        assertEquals(converter.convert("A", E.class), E.A);
        assertNull(converter.convert("B", E.class));
        // to map
        Map<String, String> map1 = converter.convert(a, new TypeRef<Map<String, String>>() {});
        assertEquals(map1, MapKit.map("first", "1", "second", "2", "third", "3"));
        Map<String, String> map2 = converter.convert(a, A.class, new TypeRef<Map<String, String>>() {});
        assertEquals(map2, MapKit.map("first", "1", "second", "2", "third", "3"));
        Map<String, String> map3 = converter.convert(a, new TypeRef<Map<String, String>>() {},
            ConvertOption.builderProvider(ObjectBuilderProvider.newProvider(
                ObjectBuilderProvider.newBuilderCache(new HashMap<>()),
                ObjectBuilderProvider.defaultProvider().asHandler())
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
            String nowDate = TimeKit.format(now);
            Charset c8859 = StandardCharsets.ISO_8859_1;
            assertEquals(converter.convert("123".toCharArray(), String.class), "123");
            assertEquals(converter.convert(new BigDecimal("123.456"), String.class), "123.456");
            assertEquals(converter.convert(now, String.class), nowDate);
            assertEquals(converter.convert(now.toInstant(), String.class), nowDate);
            assertEquals(converter.convert("123".getBytes(c8859), String.class, ConvertOption.charset(c8859)), "123");
            assertEquals(converter.convert(ByteBuffer.wrap("123".getBytes(c8859)), String.class), "123");
            ByteArrayInputStream in = new ByteArrayInputStream("123".getBytes(c8859));
            assertEquals(converter.convert(in, String.class), "123");
            in.reset();
            assertEquals(converter.convert(in, String.class), "123");
            in.reset();
            assertEquals(
                converter.convert(Channels.newChannel(in), String.class),
                "123"
            );
            CharArrayReader reader = new CharArrayReader("123".toCharArray());
            assertEquals(converter.convert(reader, String.class), "123");
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
            Date now = new Date();
            assertEquals(converter.convert("123", int.class), 123);
            assertEquals(converter.convert("123", long.class), 123L);
            assertEquals(converter.convert("123", Long.class), 123L);
            assertEquals(converter.convert(now, long.class), now.getTime());
            assertEquals(converter.convert(now.toInstant(), Long.class), now.getTime());
            assertEquals(converter.convert(123, long.class), 123L);
            assertEquals(converter.convert(123, Long.class), 123L);
            assertEquals(converter.convert(123, BigDecimal.class), new BigDecimal("123"));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(new X<String>(), nonClass, long.class));
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(a, long.class));
        }
        {
            // to boolean
            assertEquals(converter.convert(true, boolean.class), true);
            assertEquals(converter.convert(false, boolean.class), false);
            assertEquals(converter.convert("true", Boolean.class), true);
            assertEquals(converter.convert("true0", Boolean.class), false);
            assertEquals(converter.convert(0, boolean.class), false);
            assertEquals(converter.convert(1, boolean.class), true);
            assertEquals(converter.convert(-1, boolean.class), true);
            assertThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(a, boolean.class));
        }
        {
            // to time
            Date now = new Date();
            String nowDate = TimeKit.format(now);
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
        TimeFormatter nowFormat = TimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = nowFormat.format(now);
        {
            // to array
            String[] strArray = new String[]{"1", "2", "3"};
            assertEquals(converter.convert(strArray, int[].class), new int[]{1, 2, 3});
            List<String> strList = ListKit.list("1", "2", "3");
            assertEquals(converter.convert(strList, int[].class), new int[]{1, 2, 3});
            assertEquals(
                converter.convert(strList, new TypeRef<List<String>>() {}.type(), int[].class),
                new int[]{1, 2, 3}
            );
            assertEquals(converter.convert(new Iterable<String>() {
                @Override
                public @Nonnull Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, int[].class), new int[]{1, 2, 3});
            // with options
            Date[] dateArray = new Date[]{now, now, now};
            assertEquals(
                converter.convert(dateArray, String[].class, ConvertOption.timeFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            List<Date> dateList = ListKit.list(now, now, now);
            assertEquals(converter.convert(
                    dateList, new TypeRef<List<Date>>() {}.type(), String[].class, ConvertOption.timeFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            assertEquals(converter.convert(new Iterable<Date>() {

                    @Override
                    public @Nonnull Iterator<Date> iterator() {
                        return dateList.iterator();
                    }
                }, new TypeRef<Iterable<Date>>() {}.type(), String[].class, ConvertOption.timeFormatter(nowFormat)),
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
            assertEquals(converter.convert(strArray, intsType), new Integer[]{1, 2, 3});
            List<String> strList = ListKit.list("1", "2", "3");
            assertEquals(converter.convert(strList, intsType), new Integer[]{1, 2, 3});
            assertEquals(
                converter.convert(strList, new TypeRef<List<String>>() {}.type(), intsType),
                new Integer[]{1, 2, 3}
            );
            assertEquals(converter.convert(new Iterable<String>() {

                @Override
                public @Nonnull Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, intsType), new Integer[]{1, 2, 3});
            // with options
            GenericArrayType stringsType = TypeKit.arrayType(String.class);
            Date[] dateArray = new Date[]{now, now, now};
            assertEquals(
                converter.convert(dateArray, stringsType, ConvertOption.timeFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            List<Date> dateList = ListKit.list(now, now, now);
            assertEquals(converter.convert(
                    dateList, new TypeRef<List<Date>>() {}.type(), stringsType, ConvertOption.timeFormatter(nowFormat)),
                new String[]{nowStr, nowStr, nowStr}
            );
            assertEquals(converter.convert(new Iterable<Date>() {

                    @Override
                    public @Nonnull Iterator<Date> iterator() {
                        return dateList.iterator();
                    }
                }, new TypeRef<Iterable<Date>>() {}.type(), stringsType, ConvertOption.timeFormatter(nowFormat)),
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
                converter.convert(dateArray, stringsType, ConvertOption.timeFormatter(nowFormat)),
                ListKit.list(nowStr, nowStr, nowStr)
            );
            List<Date> dateList = ListKit.list(now, now, now);
            assertEquals(converter.convert(
                    dateList, new TypeRef<List<Date>>() {}.type(), stringsType, ConvertOption.timeFormatter(nowFormat)),
                ListKit.list(nowStr, nowStr, nowStr)
            );
            assertEquals(converter.convert(new Iterable<Date>() {

                    @Override
                    public @Nonnull Iterator<Date> iterator() {
                        return dateList.iterator();
                    }
                }, new TypeRef<Iterable<Date>>() {}.type(), stringsType, ConvertOption.timeFormatter(nowFormat)),
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
        CA ca = new CA("1", ListKit.list("1", "2", "3"), new A("1", "2", "3"), TimeKit.format(now));
        assertEquals(
            ObjectConverter.defaultConverter().convert(ca, CB.class,
                ConvertOption.dataMapper(DataMapper.defaultMapper()),
                ConvertOption.timeFormatter(TimeFormatter.defaultFormatter()),
                ConvertOption.ioOperator(IOOperator.defaultOperator())
            ),
            new CB(1, ListKit.list(1, 2, 3), new B(1L, 2L, 3L), TimeKit.parse(ca.date, LocalDateTime.class))
        );
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
}
