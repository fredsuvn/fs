package tests.object.convert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.object.convert.ObjectConversionException;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.convert.UnsupportedObjectConversionException;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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

    public void testObjectConverter(ObjectConverter converter) throws Exception {
        A a = new A("1", "2", "3");
        assertEquals(converter.convert(a, B.class), new B(1L, 2L, 3L));
        assertEquals(converter.convert(a, A.class, B.class), new B(1L, 2L, 3L));
        expectThrows(UnsupportedObjectConversionException.class, () -> converter.convert(null, B.class));
        ObjectConverter converter2 = ObjectConverter.withHandlers(converter.asHandler());
        assertEquals(converter2.convert(a, B.class), new B(1L, 2L, 3L));
        assertEquals(converter2.convert(a, A.class, B.class), new B(1L, 2L, 3L));
        expectThrows(UnsupportedObjectConversionException.class, () -> converter2.convert(null, B.class));
        ObjectConverter converter3 = converter.withFirstHandler(
            (src, srcType, target, converter1, options) -> {
                throw new UnreachablePointException();
            });
        ObjectConversionException e1 = expectThrows(ObjectConversionException.class, () -> converter3.convert(a, B.class));
        assertTrue(e1.getCause() instanceof UnreachablePointException);
        class X<T> {}
        ObjectConverter converter4 = converter.withLastHandler(
            (src, srcType, target, converter1, options) ->
                ObjectConverter.Status.HANDLER_BREAK);
        UnsupportedObjectConversionException e2 = expectThrows(UnsupportedObjectConversionException.class, () ->
            converter4.convert(a, X.class.getTypeParameters()[0]));
        assertEquals(e2.sourceObject(), a);
        assertEquals(e2.sourceObjectType(), A.class);
        assertEquals(e2.targetType(), X.class.getTypeParameters()[0]);
        assertSame(e2.converter(), converter4);
        assertEquals(e2.options(), Option.empty());
        // assignable
        String hello = "hello";
        CharSequence cs = converter.convert(hello, CharSequence.class);
        assertSame(cs, hello);
        Type wildLower = ((ParameterizedType) (F.class.getField("l1").getGenericType()))
            .getActualTypeArguments()[0];
        // assertSame(converter.convert(new Object(), wildLower), AssignableConversionHandler.SUPER);
        // Type wildUpper = ((ParameterizedType) (F.class.getField("l2").getGenericType()))
        //     .getActualTypeArguments()[0];
        expectThrows(UnsupportedObjectConversionException.class, () -> converter.convert(new Object(), wildLower));
        // to map
        Map<String, String> map1 = converter.convert(a, new TypeRef<Map<String, String>>() {});
        assertEquals(map1, MapKit.map("first", "1", "second", "2", "third", "3"));
        Map<String, String> map2 = converter.convert(a, A.class, new TypeRef<Map<String, String>>() {});
        assertEquals(map2, MapKit.map("first", "1", "second", "2", "third", "3"));
        class Err {}
        expectThrows(ObjectConversionException.class, () -> converter.convert(a, Err.class));
        // to enum
        assertEquals(converter.convert("A", E.class), E.A);
        assertNull(converter.convert("B", E.class));
    }

    @Test
    public void testException() {
        {
            // ObjectConversionException
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException();
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException("");
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException("", new RuntimeException());
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException(new RuntimeException());
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException(Object.class, String.class);
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException(Object.class, String.class, new RuntimeException());
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
}
