package tests.object.convert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.collect.ArrayKit;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.object.convert.ConvertOption;
import xyz.sunqian.common.object.convert.DataBuilderFactory;
import xyz.sunqian.common.object.convert.ObjectConvertException;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.convert.UnsupportedObjectConvertException;
import xyz.sunqian.common.runtime.reflect.TypeKit;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ConvertTest implements PrintTest {

    @Test
    public void testObjectConverter() throws Exception {
        testObjectConverter(ObjectConverter.defaultConverter());
    }

    private void testObjectConverter(ObjectConverter converter) throws Exception {
        A a = new A("1", "2", "3");
        assertEquals(converter.convert(a, B.class), new B(1L, 2L, 3L));
        assertEquals(converter.convert(a, A.class, B.class), new B(1L, 2L, 3L));
        expectThrows(UnsupportedObjectConvertException.class, () -> converter.convert(null, B.class));
        ObjectConverter converter2 = ObjectConverter.withHandlers(converter.asHandler());
        assertEquals(converter2.convert(a, B.class), new B(1L, 2L, 3L));
        assertEquals(converter2.convert(a, A.class, B.class), new B(1L, 2L, 3L));
        expectThrows(UnsupportedObjectConvertException.class, () -> converter2.convert(null, B.class));
        {
            // error during handler
            ObjectConverter cvt = converter.withFirstHandler(
                (src, srcType, target, converter1, options) -> {
                    throw new UnreachablePointException();
                });
            ObjectConvertException e = expectThrows(ObjectConvertException.class, () ->
                cvt.convert(a, B.class));
            assertTrue(e.getCause() instanceof UnreachablePointException);
        }
        {
            // break during handler
            ObjectConverter cvt = converter.withFirstHandler(
                (src, srcType, target, converter1, options) ->
                    ObjectConverter.Status.HANDLER_BREAK
            );
            UnsupportedObjectConvertException e = expectThrows(UnsupportedObjectConvertException.class, () ->
                cvt.convert(a, B.class, ConvertOption.IGNORE_NULL));
            assertEquals(e.sourceObject(), a);
            assertEquals(e.sourceObjectType(), A.class);
            assertEquals(e.targetType(), B.class);
            assertSame(e.converter(), cvt);
            assertEquals(e.options(), ArrayKit.array(ConvertOption.IGNORE_NULL));
        }
        {
            // withLastHandler
            class X<T> {}
            ObjectConverter cvt = converter.withLastHandler(
                (src, srcType, target, converter1, options) -> {
                    throw new UnreachablePointException();
                });
            ObjectConvertException e = expectThrows(ObjectConvertException.class, () ->
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
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, wildLower, ConvertOption.STRICT_TYPE_MODE));
            assertEquals(converter.convert(obj, wildLower), obj.toString());
            Type upperLower = ((ParameterizedType) (F.class.getField("l2").getGenericType()))
                .getActualTypeArguments()[0];
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert(obj, upperLower, ConvertOption.STRICT_TYPE_MODE));
            assertEquals(converter.convert(obj, upperLower), obj.toString());
            class X<T> {}
            expectThrows(UnsupportedObjectConvertException.class, () ->
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
            ConvertOption.builderFactory(DataBuilderFactory.newFactory(new HashMap<>())));
        assertEquals(map3, MapKit.map("first", "1", "second", "2", "third", "3"));
        class Err {}
        expectThrows(ObjectConvertException.class, () -> converter.convert(a, Err.class));
        // builder factory
        expectThrows(UnsupportedObjectConvertException.class, () -> converter.convert(a, B.class,
            ConvertOption.builderFactory(new DataBuilderFactory() {

                @Override
                public @Nullable Object newBuilder(@Nonnull Class<?> target) throws Exception {
                    return null;
                }

                @Override
                public @Nonnull Object build(@Nonnull Object builder) throws Exception {
                    return null;
                }
            })));
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
                @NotNull
                @Override
                public Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, int[].class), new int[]{1, 2, 3});
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", int[].class));
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", new TypeRef<List<String>>() {}.type(), int[].class));
        }
        {
            // to generic array
            String[] strArray = new String[]{"1", "2", "3"};
            GenericArrayType genericArrayType = TypeKit.arrayType(Integer.class);
            assertEquals(converter.convert(strArray, genericArrayType), new Integer[]{1, 2, 3});
            List<String> strList = ListKit.list("1", "2", "3");
            assertEquals(converter.convert(strList, genericArrayType), new Integer[]{1, 2, 3});
            assertEquals(
                converter.convert(strList, new TypeRef<List<String>>() {}.type(), genericArrayType),
                new Integer[]{1, 2, 3}
            );
            assertEquals(converter.convert(new Iterable<String>() {
                @NotNull
                @Override
                public Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, genericArrayType), new Integer[]{1, 2, 3});
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", genericArrayType));
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", new TypeRef<List<String>>() {}.type(), genericArrayType));
        }
        {
            // to collection
            String[] strArray = new String[]{"1", "2", "3"};
            Type collectionType = new TypeRef<List<Integer>>() {}.type();
            assertEquals(converter.convert(strArray, collectionType), ListKit.list(1, 2, 3));
            List<String> strList = ListKit.list("1", "2", "3");
            assertEquals(converter.convert(strList, Set.class, List.class), ListKit.list("1", "2", "3"));
            assertEquals(
                converter.convert(strList, new TypeRef<List<String>>() {}.type(), collectionType),
                ListKit.list(1, 2, 3)
            );
            assertEquals(converter.convert(new Iterable<String>() {
                @NotNull
                @Override
                public Iterator<String> iterator() {
                    return strList.iterator();
                }
            }, collectionType), ListKit.list(1, 2, 3));
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", collectionType));
            expectThrows(UnsupportedObjectConvertException.class, () ->
                converter.convert("", new TypeRef<List<String>>() {}.type(), collectionType));
        }
    }

    @Test
    public void testComplexConvert() {
        CA ca = new CA("1", ListKit.list("1", "2", "3"), new A("1", "2", "3"));
        assertEquals(
            ObjectConverter.defaultConverter().convert(ca, CB.class),
            new CB(1, ListKit.list(1, 2, 3), new B(1L, 2L, 3L))
        );
    }

    @Test
    public void testException() {
        {
            // ObjectConversionException
            expectThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException();
            });
            expectThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException("");
            });
            expectThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException("", new RuntimeException());
            });
            expectThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException(new RuntimeException());
            });
            expectThrows(ObjectConvertException.class, () -> {
                throw new ObjectConvertException(Object.class, String.class);
            });
            expectThrows(ObjectConvertException.class, () -> {
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
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CB {
        private Integer p1;
        private List<Integer> p2;
        private B p3;
    }
}
