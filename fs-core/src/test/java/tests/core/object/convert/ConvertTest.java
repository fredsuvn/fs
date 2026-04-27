package tests.core.object.convert;

import internal.utils.DataGen;
import internal.utils.TestPrint;
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
import space.sunqian.fs.base.number.NumberFormatter;
import space.sunqian.fs.base.number.NumberKit;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.builder.BuilderOperatorProvider;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConvertException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.convert.UnsupportedObjectConvertException;
import space.sunqian.fs.object.schema.MapSchemaParser;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
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

public class ConvertTest implements TestPrint, DataGen {

    @Test
    public void testObjectConverter() throws Exception {
        testObjectConverter(ObjectConverter.defaultConverter());
    }

    @Test
    public void testAsHandler() throws Exception {
        class X {}
        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(new X(), int.class));
        ObjectConverter cvt = ObjectConverter.newConverter(
            ObjectConverter.defaultConverter().asHandler(),
            (src, srcType, targetType, converter, options) -> {
                if (src instanceof X) {
                    return 666;
                }
                return null;
            }
        );
        assertEquals(666, cvt.convert(new X(), int.class));
    }

    @Test
    public void testAssignableConvertHandler() {
        ObjectConverter converter = ObjectConverter.defaultConverter();

        Map<String, ?> map = MapKit.map(
            "one", "1",
            "two", 2,
            "array", new long[]{1, 2, 3},
            "list", ListKit.list("1", "2", "3")
        );
        C expected = new C(1, "2", new int[]{1, 2, 3}, SetKit.set(1, 2, 3));

        C target = converter.convert(map, C.class);
        assertEquals(expected, target);

        C target2 = converter.convert(map, new TypeRef<Map<String, ?>>() {}.type(), C.class);
        assertEquals(expected, target2);

        List<? extends String> strList = ListKit.list("1", "2", "3");
        assertArrayEquals(
            new int[]{1, 2, 3},
            converter.convert(strList, new TypeRef<List<? extends String>>() {}.type(), int[].class)
        );

        List<? super String> superList = ListKit.list("1", "2", "3");
        assertArrayEquals(
            new int[]{1, 2, 3},
            converter.convert(superList, new TypeRef<List<? super String>>() {}.type(), new TypeRef<int[]>() {})
        );

        @Data
        @AllArgsConstructor
        class X<T extends String> {}
        assertEquals(111, converter.convert("111", X.class.getTypeParameters()[0], int.class));

        List<?> nullList = ListKit.list((Object) null);
        assertArrayEquals(
            new Object[]{null},
            converter.convert(nullList, new TypeRef<List<?>>() {}.type(), Object[].class)
        );
    }

    @Test
    public void testCommonHandler() {
        A a = createA("1", "2", "3");
        ObjectConverter converter = ObjectConverter.defaultConverter();

        testToEnum(converter);
        testToMap(converter, a);
        testConversionErrors(converter, a);
        testToString(converter);
        testStringToOtherTypes(converter);
        testToNumber(converter, a);
        testToBoolean(converter, a);
        testToTime(converter, a);
        testToArray(converter);
        testToGenericArray(converter);
        testToCollection(converter);
    }

    @Test
    public void testComplexConvert() {
        Date now = new Date();
        CA ca = new CA("1", ListKit.list("1", "2", "3"), createA("1", "2", "3"), DateKit.format(now));
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
        ObjectConverter converterOps = converter.withDefaultOptions(ConvertOption.ignoreNull(true));

        assertEquals(Collections.emptyList(), converter.defaultOptions());
        assertEquals(ListKit.list(ConvertOption.ignoreNull(true)), converterOps.defaultOptions());

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

        testToTreeMap(dataObject, dataMap);
        testToConcurrentSkipListMap(dataObject, dataMap);
        testToList();
        testMapKV();
    }

    @Test
    public void testCollections() {
        assertEquals(
            ListKit.list(1, 2),
            ObjectConverter.defaultConverter().convert(
                ListKit.list("1", "2"),
                new TypeRef<List<Integer>>() {}.type()
            )
        );
        assertEquals(
            ListKit.list(new MapObject(1L), new MapObject(2L)),
            ObjectConverter.defaultConverter().convert(
                ListKit.list(MapKit.map("longNum", "1"), MapKit.map("longNum", "2")),
                new TypeRef<List<MapObject>>() {}.type()
            )
        );
    }

    @Test
    public void testNewInstance() throws Exception {
        Object str1 = new Object();
        Object str2 = ObjectConverter.defaultConverter().convert(str1, Object.class, ConvertOption.newInstanceMode(true));
        assertNotSame(str1, str2);
        Object str3 = ObjectConverter.defaultConverter().convert(str1, Object.class);
        assertSame(str1, str3);
        Object str4 = ObjectConverter.defaultConverter().convert(str1, Object.class, ConvertOption.newInstanceMode(false));
        assertSame(str1, str4);

        // DataObject src = new DataObject(new String(randomChars(16)), new String(randomChars(16)));
        // DataObject dst = new DataObject();
        // BeanUtils.copyProperties(dst, src);
        // assertSame(src.getCode(), dst.getCode());
        // assertSame(src.getName(), dst.getName());
        //
        // dst.setCode(null);
        // dst.setName(null);
        // org.springframework.beans.BeanUtils.copyProperties(src, dst);
        // assertSame(src.getCode(), dst.getCode());
        // assertSame(src.getName(), dst.getName());
        //
        // dst.setCode(null);
        // dst.setName(null);
        // BeanUtil.copyProperties(src, dst);
        // assertSame(src.getCode(), dst.getCode());
        // assertSame(src.getName(), dst.getName());
        //
        // dst.setCode(null);
        // dst.setName(null);
        // Fs.copyProperties(src, dst);
        // assertSame(src.getCode(), dst.getCode());
        // assertSame(src.getName(), dst.getName());
    }

    @Test
    public void testAnnotation() {
        testPatternAnnotation();
        testAnnotationObjectToObject();
        testAnnotationMapToObject();
        testAnnotationNullSupport();
    }

    @Test
    public void testConvertKit() throws Exception {
        testMapParserOptions();
        testObjectParserOptions();
        testBuilderProviderOptions();
    }

    @Test
    public void testException() {
        assertThrows(ObjectConvertException.class, () -> {throw new ObjectConvertException();});
        assertThrows(ObjectConvertException.class, () -> {throw new ObjectConvertException("");});
        assertThrows(ObjectConvertException.class, () -> {
            throw new ObjectConvertException("", new RuntimeException());
        });
        assertThrows(ObjectConvertException.class, () -> {throw new ObjectConvertException(new RuntimeException());});
        assertThrows(ObjectConvertException.class, () -> {
            throw new ObjectConvertException(Object.class, String.class);
        });
        assertThrows(ObjectConvertException.class, () -> {
            throw new ObjectConvertException(Object.class, String.class, new RuntimeException());
        });
    }

    private void testObjectConverter(ObjectConverter converter) throws Exception {
        A a = createA("1", "2", "3");

        assertEquals(new B(1L, 2L, 3L), converter.convert(a, B.class));
        assertEquals(new B(1L, 2L, 3L), converter.convert(a, A.class, B.class));
        assertNull(converter.convert(null, B.class));

        ObjectConverter converter2 = ObjectConverter.newConverter(converter.asHandler());
        assertEquals(new B(1L, 2L, 3L), converter2.convert(a, B.class));
        assertEquals(new B(1L, 2L, 3L), converter2.convert(a, A.class, B.class));
        assertNull(converter2.convert(null, B.class));

        testHandlerError(converter, a);
        testHandlerBreak(converter, a);
        testWithLastHandler(converter, a);
        testAssignable(converter);
        testStrictTargetType(converter);
    }

    private void testHandlerError(ObjectConverter converter, A a) {
        ObjectConverter.Handler handler = (src, srcType, target, converter1, options) -> {
            throw new UnreachablePointException();
        };
        ObjectConverter cvt = converter.withFirstHandler(handler);
        ObjectConvertException e = assertThrows(ObjectConvertException.class, () -> cvt.convert(a, B.class));
        assertTrue(e.getCause() instanceof UnreachablePointException);
    }

    private void testHandlerBreak(ObjectConverter converter, A a) {
        ObjectConverter.Handler handler = (src, srcType, target, converter1, options) ->
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

    private void testWithLastHandler(ObjectConverter converter, A a) {
        class X<T> {}
        ObjectConverter.Handler handler = (src, srcType, target, converter1, options) -> {
            throw new UnreachablePointException();
        };
        ObjectConverter cvt = converter.withFirstHandler(handler);
        ObjectConvertException e = assertThrows(ObjectConvertException.class, () ->
            cvt.convert(a, X.class.getTypeParameters()[0], ConvertOption.strictTargetTypeMode(true)));
        assertTrue(e.getCause() instanceof UnreachablePointException);
    }

    private void testAssignable(ObjectConverter converter) {
        String hello = "hello";
        CharSequence cs = converter.convert(hello, CharSequence.class);
        assertSame(hello, cs);
    }

    private void testStrictTargetType(ObjectConverter converter) throws Exception {
        Type wildLower = ((ParameterizedType) (F.class.getField("l1").getGenericType()))
            .getActualTypeArguments()[0];
        Object obj = new Object();
        assertEquals(obj.toString(), converter.convert(obj, wildLower, ConvertOption.strictTargetTypeMode(false)));
        assertThrows(UnsupportedObjectConvertException.class, () ->
            converter.convert(obj, wildLower, ConvertOption.strictTargetTypeMode(true)));
        assertEquals(converter.convert(obj, wildLower), obj.toString());

        Type upperLower = ((ParameterizedType) (F.class.getField("l2").getGenericType()))
            .getActualTypeArguments()[0];
        assertEquals(obj.toString(), converter.convert(obj, upperLower, ConvertOption.strictTargetTypeMode(false)));
        assertThrows(UnsupportedObjectConvertException.class, () ->
            converter.convert(obj, upperLower, ConvertOption.strictTargetTypeMode(true)));
        assertEquals(converter.convert(obj, upperLower), obj.toString());

        class X<T> {}
        assertThrows(UnsupportedObjectConvertException.class, () ->
            converter.convert(obj, X.class.getTypeParameters()[0], ConvertOption.strictTargetTypeMode(true)));
        assertSame(converter.convert(obj, X.class.getTypeParameters()[0]), obj);
    }

    private void testToEnum(ObjectConverter converter) {
        assertEquals(E.A, converter.convert("A", E.class));
        assertNull(converter.convert("B", E.class));
    }

    private void testToMap(ObjectConverter converter, A a) {
        Map<String, String> map1 = converter.convert(a, new TypeRef<Map<String, String>>() {});
        assertEquals(map1, MapKit.map("first", "1", "second", "2", "third", "3"));

        Map<String, String> map2 = Fs.as(converter.convert(a, A.class, new TypeRef<Map<String, String>>() {}.type()));
        assertEquals(map2, MapKit.map("first", "1", "second", "2", "third", "3"));

        Map<String, String> map3 = converter.convert(a, new TypeRef<Map<String, String>>() {},
            ConvertOption.builderOperatorProvider(BuilderOperatorProvider.newProvider(
                BuilderOperatorProvider.defaultProvider().asHandler())
            )
        );
        assertEquals(map3, MapKit.map("first", "1", "second", "2", "third", "3"));
    }

    private void testConversionErrors(ObjectConverter converter, A a) {
        class Err {}
        assertThrows(ObjectConvertException.class, () -> converter.convert(a, Err.class));
        class N {}
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(a, N.class));
    }

    private void testToString(ObjectConverter converter) {
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
        assertEquals("123", converter.convert(Channels.newChannel(in), String.class));

        CharArrayReader reader = new CharArrayReader("123".toCharArray());
        assertEquals("123", converter.convert(reader, String.class));

        Object x = new Object();
        assertEquals(converter.convert(x, String.class), x.toString());
    }

    private void testStringToOtherTypes(ObjectConverter converter) {
        assertArrayEquals(converter.convert("123", byte[].class), "123".getBytes(CharsKit.defaultCharset()));
        assertEquals(converter.convert("123", ByteBuffer.class), ByteBuffer.wrap("123".getBytes(CharsKit.defaultCharset())));
        assertArrayEquals(converter.convert("123", char[].class), "123".toCharArray());
        assertEquals(converter.convert("123", CharBuffer.class), CharBuffer.wrap("123"));
    }

    private void testToNumber(ObjectConverter converter, A a) {
        assertEquals(123.0, converter.convert(123, double.class));
        assertEquals(123.0, converter.convert(123, int.class, Double.class));
        assertEquals(123, converter.convert("123", int.class));
        assertEquals(123L, converter.convert("123", long.class));
        assertEquals(123L, converter.convert("123", Long.class));
        assertEquals(123.123, converter.convert("123.123", double.class));
        assertEquals("123.12", converter.convert(123.12345, String.class, ConvertOption.numFormatter(NumberFormatter.ofPattern("#.00"))));

        Date now = new Date();
        assertEquals(converter.convert(now, long.class), now.getTime());
        assertEquals(converter.convert(now.toInstant(), Long.class), now.getTime());
        assertEquals(123L, converter.convert(123, long.class));
        assertEquals(123L, converter.convert(123, Long.class));
        assertEquals(new BigDecimal("123"), converter.convert(123, BigDecimal.class));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(a, long.class));
    }

    private void testToBoolean(ObjectConverter converter, A a) {
        assertEquals(true, converter.convert(true, boolean.class));
        assertEquals(false, converter.convert(false, boolean.class));
        assertEquals(true, converter.convert("true", Boolean.class));
        assertEquals(false, converter.convert("true0", Boolean.class));
        assertEquals(false, converter.convert(0, boolean.class));
        assertEquals(true, converter.convert(1, boolean.class));
        assertEquals(true, converter.convert(-1, boolean.class));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(a, boolean.class));
    }

    private void testToTime(ObjectConverter converter, A a) {
        Date now = new Date();
        assertEquals(converter.convert(DateKit.format(now), Date.class), now);
        assertEquals(converter.convert(now, Date.class), now);
        assertEquals(converter.convert(now.toInstant(), Date.class), now);
        assertEquals(converter.convert(now, Instant.class), now.toInstant());
        assertEquals(converter.convert(now.toInstant(), Instant.class), now.toInstant());
        assertEquals(converter.convert(now.getTime(), Date.class), now);
        assertEquals(converter.convert(now.getTime(), long.class, Date.class), now);
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(a, Date.class));
    }

    private void testToArray(ObjectConverter converter) {
        Date now = new Date();
        DateFormatter nowFormat = DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = nowFormat.format(now);
        List<String> strList = ListKit.list("1", "2", "3");

        String[] strArray = new String[]{"1", "2", "3"};
        assertArrayEquals(new int[]{1, 2, 3}, converter.convert(strArray, int[].class));
        assertArrayEquals(new int[]{1, 2, 3}, converter.convert(strList, int[].class));
        assertArrayEquals(new int[]{1, 2, 3},
            Fs.as(converter.convert(strList, new TypeRef<List<String>>() {}.type(), int[].class)));
        assertArrayEquals(new int[]{1, 2, 3}, converter.convert(new IterableString(strList), int[].class));

        Date[] dateArray = new Date[]{now, now, now};
        assertArrayEquals(
            converter.convert(dateArray, String[].class, ConvertOption.dateFormatter(nowFormat)),
            new String[]{nowStr, nowStr, nowStr}
        );

        List<Date> dateList = ListKit.list(now, now, now);
        assertArrayEquals(Fs.as(converter.convert(
                dateList, new TypeRef<List<Date>>() {}.type(), String[].class, ConvertOption.dateFormatter(nowFormat))),
            new String[]{nowStr, nowStr, nowStr}
        );
        assertArrayEquals(Fs.as(converter.convert(
                new IterableDate(dateList), new TypeRef<Iterable<Date>>() {}.type(), String[].class, ConvertOption.dateFormatter(nowFormat))),
            new String[]{nowStr, nowStr, nowStr}
        );

        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert("", int[].class));
        assertThrows(UnsupportedObjectConvertException.class, () ->
            converter.convert("", new TypeRef<List<String>>() {}.type(), int[].class));
    }

    private void testToGenericArray(ObjectConverter converter) {
        Date now = new Date();
        DateFormatter nowFormat = DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = nowFormat.format(now);
        List<String> strList = ListKit.list("1", "2", "3");

        String[] strArray = new String[]{"1", "2", "3"};
        GenericArrayType intsType = TypeKit.arrayType(Integer.class);
        assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.convert(strArray, intsType));
        assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.convert(strList, intsType));
        assertArrayEquals(new Integer[]{1, 2, 3},
            (Integer[]) converter.convert(strList, new TypeRef<List<String>>() {}.type(), intsType));
        assertArrayEquals(new Integer[]{1, 2, 3}, (Integer[]) converter.convert(new IterableString(strList), intsType));

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
        assertArrayEquals((String[]) converter.convert(
                new IterableDate(dateList), new TypeRef<Iterable<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
            new String[]{nowStr, nowStr, nowStr}
        );

        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert("", intsType));
        assertThrows(UnsupportedObjectConvertException.class, () ->
            converter.convert("", new TypeRef<List<String>>() {}.type(), intsType));
    }

    private void testToCollection(ObjectConverter converter) {
        Date now = new Date();
        DateFormatter nowFormat = DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = nowFormat.format(now);
        List<String> strList = ListKit.list("1", "2", "3");

        String[] strArray = new String[]{"1", "2", "3"};
        Type intsType = new TypeRef<List<Integer>>() {}.type();
        assertEquals(converter.convert(strArray, intsType), ListKit.list(1, 2, 3));
        assertEquals(converter.convert(strList, Set.class, List.class), ListKit.list("1", "2", "3"));
        assertEquals(converter.convert(strList, new TypeRef<List<String>>() {}.type(), intsType), ListKit.list(1, 2, 3));
        assertEquals(converter.convert(new IterableString(strList), intsType), ListKit.list(1, 2, 3));

        Type stringsType = new TypeRef<List<String>>() {}.type();
        Date[] dateArray = new Date[]{now, now, now};
        assertEquals(converter.convert(dateArray, stringsType, ConvertOption.dateFormatter(nowFormat)),
            ListKit.list(nowStr, nowStr, nowStr));

        List<Date> dateList = ListKit.list(now, now, now);
        assertEquals(converter.convert(
                dateList, new TypeRef<List<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
            ListKit.list(nowStr, nowStr, nowStr)
        );
        assertEquals(converter.convert(
                new IterableDate(dateList), new TypeRef<Iterable<Date>>() {}.type(), stringsType, ConvertOption.dateFormatter(nowFormat)),
            ListKit.list(nowStr, nowStr, nowStr)
        );

        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert("", intsType));
        assertThrows(UnsupportedObjectConvertException.class, () ->
            converter.convert("", new TypeRef<List<String>>() {}.type(), intsType));
    }

    private void testToTreeMap(DataObject dataObject, Map<String, String> dataMap) {
        Map<String, String> map = ObjectConverter.defaultConverter()
            .convert(dataObject, new TypeRef<TreeMap<String, String>>() {});
        assertEquals(dataMap, map);
    }

    private void testToConcurrentSkipListMap(DataObject dataObject, Map<String, String> dataMap) {
        Map<String, String> map = ObjectConverter.defaultConverter()
            .convert(dataObject, new TypeRef<ConcurrentSkipListMap<String, String>>() {});
        assertEquals(dataMap, map);
    }

    private void testToList() {
        assertEquals(ListKit.list("code", "name"),
            ObjectConverter.defaultConverter().convert(new String[]{"code", "name"}, new TypeRef<LinkedList<String>>() {}));
        assertEquals(ListKit.list("code", "name"),
            ObjectConverter.defaultConverter().convert(new String[]{"code", "name"}, new TypeRef<CopyOnWriteArrayList<String>>() {}));
        assertEquals(SetKit.set("code", "name"),
            ObjectConverter.defaultConverter().convert(new String[]{"code", "name"}, new TypeRef<TreeSet<String>>() {}));
        assertEquals(SetKit.set("code", "name"),
            ObjectConverter.defaultConverter().convert(new String[]{"code", "name"}, new TypeRef<ConcurrentSkipListSet<String>>() {}));
    }

    private void testMapKV() {
        Map<String, Object> map = new HashMap<>();
        map.put("longNum", 1);
        assertEquals(1L, ObjectConverter.defaultConverter().convert(map, MapObject.class).getLongNum());
        assertEquals(1L, ObjectConverter.defaultConverter().convert(map, new TypeRef<MapObject>() {}).getLongNum());
        assertEquals(1L, ((MapObject) ObjectConverter.defaultConverter().convert(map, (Type) MapObject.class)).getLongNum());
    }

    private void testPatternAnnotation() {
        Option<?, DateFormatter> df1 = ConvertKit.getDateFormatterOption("yyyy-MM-dd", ZoneId.systemDefault());
        Option<?, DateFormatter> df2 = ConvertKit.getDateFormatterOption("yyyy-MM-dd", ZoneId.systemDefault());
        Option<?, DateFormatter> df3 = ConvertKit.getDateFormatterOption("yyyy-MM-dd HH:mm:ss", ZoneId.systemDefault());
        assertEquals(df1, df2);
        assertNotEquals(df1, df3);
        assertNotEquals(df2, df3);

        Option<?, NumberFormatter> nf1 = ConvertKit.getNumFormatterOption("#.0000");
        Option<?, NumberFormatter> nf2 = ConvertKit.getNumFormatterOption("#.0000");
        Option<?, NumberFormatter> nf3 = ConvertKit.getNumFormatterOption("#.000000");
        assertEquals(nf1, nf2);
        assertNotEquals(nf1, nf3);
        assertNotEquals(nf2, nf3);
    }

    private void testAnnotationObjectToObject() {
        ZonedDateTime zdt = ZonedDateTime.now();
        Ann1 ann1 = createAnn1(zdt);
        Ann2 ann2 = ObjectConverter.defaultConverter().convert(ann1, Ann2.class);

        assertEquals(
            DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse(ann1.getDate1(), LocalDateTime.class),
            ann2.getDate1()
        );
        assertEquals(
            DateFormatter.newFormatter("yyyy-MM-dd HH:mm:ss", ZoneId.of("UTC+2")).parse(ann1.getDate2(), LocalDateTime.class),
            ann2.getDate2()
        );
        assertEquals(DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ann1.getDate3()), ann2.getDate3());
        assertEquals(
            DateFormatter.newFormatter("YYYY-MM-dd HH:mm:ss", ZoneId.of("Asia/Shanghai")).format(ann1.getDate4()),
            ann2.getDate4()
        );
        assertEquals(NumberFormatter.ofPattern(NumberKit.DEFAULT_PATTERN).format(ann1.getNum1()), ann2.getNum1());
        assertEquals(NumberFormatter.ofPattern("#.000").format(ann1.getNum2()), ann2.getNum2());
        assertEquals(ann1.getNum3().toString(), ann2.getNum3());
        assertEquals(
            DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse(ann1.getComplex1(), Date.class),
            ann2.getComplex1()
        );
    }

    private void testAnnotationMapToObject() {
        ZonedDateTime zdt = ZonedDateTime.now();
        Map<String, Object> ann1 = createAnn1Map(zdt);
        Ann2 ann2 = ObjectConverter.defaultConverter().convert(ann1, Ann2.class);

        assertEquals(
            DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse((CharSequence) ann1.get("date1"), LocalDateTime.class),
            ann2.getDate1()
        );
        assertEquals(
            DateFormatter.newFormatter("yyyy-MM-dd HH:mm:ss", ZoneId.of("UTC+2")).parse((CharSequence) ann1.get("date2"), LocalDateTime.class),
            ann2.getDate2()
        );
        assertEquals(DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((LocalDateTime) ann1.get("date3")), ann2.getDate3());
        assertEquals(
            DateFormatter.newFormatter("YYYY-MM-dd HH:mm:ss", ZoneId.of("Asia/Shanghai")).format((Date) ann1.get("date4")),
            ann2.getDate4()
        );
        assertEquals(((BigDecimal) ann1.get("num1")).toString(), ann2.getNum1());
        assertEquals(NumberFormatter.ofPattern("#.000").format((Double) ann1.get("num2")), ann2.getNum2());
        assertEquals(((BigDecimal) ann1.get("num3")).toString(), ann2.getNum3());
        assertEquals(
            DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).parse((CharSequence) ann1.get("complex1"), Date.class),
            ann2.getComplex1()
        );
    }

    private void testAnnotationNullSupport() {
        Ann1 ann1 = new Ann1();
        Ann2 ann2 = ObjectConverter.defaultConverter().convert(ann1, Ann2.class);
        assertNull(ann2.getDate1());
        assertNull(ann2.getDate2());
        assertNull(ann2.getDate3());
        assertNull(ann2.getDate4());
        assertNull(ann2.getNum1());
        assertEquals(".000", ann2.getNum2());
        assertNull(ann2.getNum3());
        assertNull(ann2.getComplex1());

        Map<String, Object> ann1Map = new HashMap<>();
        Ann2 ann22 = ObjectConverter.defaultConverter().convert(ann1Map, Ann2.class);
        assertNull(ann22.getDate1());
        assertNull(ann22.getDate2());
        assertNull(ann22.getDate3());
        assertNull(ann22.getDate4());
        assertNull(ann22.getNum1());
        assertNull(ann22.getNum2());
        assertNull(ann22.getNum3());
        assertNull(ann22.getComplex1());
    }

    private void testMapParserOptions() {
        assertSame(MapSchemaParser.defaultCachedParser(),
            ConvertOption.getMapSchemaParser(ConvertOption.ignoreNull(true)));
        assertNotEquals(MapSchemaParser.defaultCachedParser(),
            ConvertOption.getMapSchemaParser(ConvertOption.mapSchemaParser(MapSchemaParser.defaultParser())));
        assertSame(MapSchemaParser.defaultParser(),
            ConvertOption.getMapSchemaParser(ConvertOption.mapSchemaParser(MapSchemaParser.defaultParser())));
    }

    private void testObjectParserOptions() {
        assertSame(ObjectSchemaParser.defaultCachedParser(),
            ConvertOption.getObjectSchemaParser(ConvertOption.ignoreNull(true)));
        assertNotEquals(ObjectSchemaParser.defaultCachedParser(),
            ConvertOption.getObjectSchemaParser(ConvertOption.objectSchemaParser(ObjectSchemaParser.defaultParser())));
        assertSame(ObjectSchemaParser.defaultParser(),
            ConvertOption.getObjectSchemaParser(ConvertOption.objectSchemaParser(ObjectSchemaParser.defaultParser())));
    }

    private void testBuilderProviderOptions() {
        assertSame(BuilderOperatorProvider.defaultCachedProvider(),
            ConvertOption.getBuilderOperatorProvider(ConvertOption.ignoreNull(true)));
        assertNotEquals(BuilderOperatorProvider.defaultCachedProvider(),
            ConvertOption.getBuilderOperatorProvider(ConvertOption.builderOperatorProvider(BuilderOperatorProvider.defaultProvider())));
        assertSame(BuilderOperatorProvider.defaultProvider(),
            ConvertOption.getBuilderOperatorProvider(ConvertOption.builderOperatorProvider(BuilderOperatorProvider.defaultProvider())));
    }

    private A createA(String first, String second, String third) {
        return new A(first, second, third);
    }

    private Ann1 createAnn1(ZonedDateTime zdt) {
        Ann1 ann1 = new Ann1();
        ann1.setDate1(DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
        ann1.setDate2(DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zdt));
        ann1.setDate3(zdt.toLocalDateTime());
        ann1.setDate4(Date.from(zdt.toInstant()));
        ann1.setNum1(new BigDecimal("123.456789"));
        ann1.setNum2(123.456789);
        ann1.setNum3(new BigDecimal("123.456789"));
        ann1.setComplex1(DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
        return ann1;
    }

    private Map<String, Object> createAnn1Map(ZonedDateTime zdt) {
        Map<String, Object> ann1 = new HashMap<>();
        ann1.put("date1", DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
        ann1.put("date2", DateFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zdt));
        ann1.put("date3", zdt.toLocalDateTime());
        ann1.put("date4", Date.from(zdt.toInstant()));
        ann1.put("num1", new BigDecimal("123.456789"));
        ann1.put("num2", 123.456789);
        ann1.put("num3", new BigDecimal("123.456789"));
        ann1.put("complex1", DateFormatter.ofPattern(DateKit.DEFAULT_PATTERN).format(zdt));
        return ann1;
    }

    private static class IterableString implements Iterable<String> {
        private final List<String> list;

        IterableString(List<String> list) {
            this.list = list;
        }

        @Override
        public @Nonnull Iterator<String> iterator() {
            return list.iterator();
        }
    }

    private static class IterableDate implements Iterable<Date> {
        private final List<Date> list;

        IterableDate(List<Date> list) {
            this.list = list;
        }

        @Override
        public @Nonnull Iterator<Date> iterator() {
            return list.iterator();
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

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class C {
        private int one;
        private String two;
        private int[] array;
        private Set<Integer> list;
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