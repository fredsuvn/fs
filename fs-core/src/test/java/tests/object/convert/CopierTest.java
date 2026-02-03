package tests.object.convert;

import internal.test.ErrorMap;
import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.exception.UnreachablePointException;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConvertException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopyException;
import space.sunqian.fs.object.convert.PropertyCopier;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CopierTest implements PrintTest {

    @Test
    public void testCopyProperties() {
        testCopyProperties(PropertyCopier.defaultCopier());
        {
            // default method
            ClsA a = new ClsA("1", "2", "3");
            ClsB b1 = new ClsB();
            PropertyCopier.defaultCopier().copyProperties(a, b1);
            assertEquals(new ClsB(1, 2, 3), b1);
            ClsB b2 = new ClsB();
            PropertyCopier.defaultCopier().copyProperties(a, b2, ObjectConverter.defaultConverter());
            assertEquals(new ClsB(1, 2, 3), b2);
        }
    }

    private void testCopyProperties(PropertyCopier propertyCopier) {
        Type typeA = new TypeRef<Map<String, String>>() {}.type();
        Type typeB = new TypeRef<Map<String, Integer>>() {}.type();
        {
            // map to map
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            Map<String, Integer> mapB = new HashMap<>();
            propertyCopier.copyProperties(
                mapA, typeA, mapB, typeB, ConvertOption.schemaParser(MapParser.defaultParser()));
            assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), mapB);
            Map<String, String> mapA2 = new HashMap<>();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null)
                .copyProperties(mapA, typeA, mapA2, typeA);
            assertTrue(mapA2.isEmpty());
            propertyCopier
                .withPropertyMapper(((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    Map<String, String> ma = Fs.as(src);
                    String key = (String) propertyName;
                    return MapKit.entry(key + "2", ma.get(key));
                }))
                .copyProperties(mapA, typeA, mapA2, typeA);
            assertEquals(MapKit.map("first2", "1", "second2", "2", "third2", "3"), mapA2);
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            Map<String, String> nullTo = new HashMap<>();
            propertyCopier.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.IGNORE_NULL);
            assertEquals(MapKit.map("first", "1", "third", "3"), nullTo);
            nullTo.clear();
            propertyCopier.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreProperties("first"));
            assertEquals(MapKit.map("second", null, "third", "3"), nullTo);
            nullTo.clear();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                })
                .copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreNull());
            assertEquals(MapKit.map("first", "1", "second", "2", "third", "1"), nullTo);
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(mapA, typeA, mapA2, typeA));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(mapA, typeA, mapA2, typeA));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
            assertDoesNotThrow(() ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    })
                    .copyProperties(mapA, typeA, mapA2, typeA)
            );
        }
        {
            // map to object
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsB clsB = new ClsB();
            propertyCopier.copyProperties(
                mapA, typeA, clsB, ClsB.class, ConvertOption.schemaParser(ObjectParser.defaultParser()));
            assertEquals(new ClsB(1, 2, 3), clsB);
            ClsA2 clsA2 = new ClsA2();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null)
                .copyProperties(mapA, typeA, clsA2, ClsA2.class);
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    Map<String, String> ma = Fs.as(src);
                    String key = (String) propertyName;
                    return MapKit.entry(key + "2", ma.get(key));
                })
                .copyProperties(mapA, typeA, clsA2, ClsA2.class);
            assertEquals(new ClsA2("1", null), clsA2);
            ClsA1 clsA12 = new ClsA1();
            propertyCopier.copyProperties(mapA, typeA, clsA12, ClsA1.class);
            assertEquals(new ClsA1("1", null), clsA12);
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            ClsA nullTo = new ClsA();
            propertyCopier.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.IGNORE_NULL);
            assertEquals(new ClsA("1", null, "3"), nullTo);
            nullTo.clear();
            propertyCopier.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
            assertEquals(new ClsA(null, null, "3"), nullTo);
            nullTo.clear();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                })
                .copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.IGNORE_NULL);
            assertEquals(new ClsA("1", "2", "1"), nullTo);
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(mapA, typeA, clsA2, ClsA2.class));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(mapA, typeA, clsA2, ClsA2.class));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
            assertDoesNotThrow(() ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    })
                    .copyProperties(mapA, typeA, clsA2, ClsA2.class)
            );
        }
        {
            // object to map
            ClsA clsA = new ClsA("1", "2", "3");
            Map<String, Integer> mapB = new HashMap<>();
            propertyCopier.copyProperties(clsA, ClsA.class, mapB, typeB);
            assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), mapB);
            Map<String, String> mapA2 = new HashMap<>();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null)
                .copyProperties(clsA, ClsA.class, mapA2, typeA);
            assertTrue(mapA2.isEmpty());
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    ObjectSchema os = Fs.as(srcSchema);
                    String key = (String) propertyName;
                    ObjectProperty op = os.getProperty(key);
                    if (op == null) {
                        return null;
                    }
                    return MapKit.entry(key + "2", op.getValue(src));
                })
                .copyProperties(clsA, ClsA.class, mapA2, typeA);
            assertEquals(MapKit.map("first2", "1", "second2", "2", "third2", "3"), mapA2);
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            Map<String, String> nullTo = new HashMap<>();
            propertyCopier.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.IGNORE_NULL);
            assertEquals(MapKit.map("first", "1", "third", "3"), nullTo);
            nullTo.clear();
            propertyCopier.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreProperties("first"));
            assertEquals(MapKit.map("second", null, "third", "3"), nullTo);
            nullTo.clear();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                })
                .copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.IGNORE_NULL);
            assertEquals(MapKit.map("first", "1", "second", "2", "third", "1"), nullTo);
            // errors
            // ObjectConversionException oce1 = assertThrows(ObjectConversionException.class, () ->
            //     dataMapper.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(clsA, ClsA.class, mapA2, typeA));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(clsA, ClsA.class, mapA2, typeA));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
            assertDoesNotThrow(() ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    })
                    .copyProperties(clsA, ClsA.class, mapA2, typeA)
            );
        }
        {
            // object to object
            ClsA clsA = new ClsA("1", "2", "3");
            ClsB clsB = new ClsB();
            propertyCopier.copyProperties(clsA, ClsA.class, clsB, ClsB.class);
            assertEquals(new ClsB(1, 2, 3), clsB);
            ClsA2 clsA2 = new ClsA2();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null)
                .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class);
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    ObjectSchema os = Fs.as(srcSchema);
                    String key = (String) propertyName;
                    ObjectProperty op = os.getProperty(key);
                    if (op == null) {
                        return null;
                    }
                    return MapKit.entry(key + "2", op.getValue(src));
                })
                .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class);
            assertEquals(new ClsA2("1", null), clsA2);
            ClsA1 clsA12 = new ClsA1();
            propertyCopier.copyProperties(clsA, ClsA.class, clsA12, ClsA1.class);
            assertEquals(new ClsA1("1", null), clsA12);
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            ClsA nullTo = new ClsA();
            propertyCopier.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.IGNORE_NULL);
            assertEquals(new ClsA("1", null, "3"), nullTo);
            nullTo.clear();
            propertyCopier.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
            assertEquals(new ClsA(null, null, "3"), nullTo);
            nullTo.clear();
            propertyCopier
                .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                })
                .copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.IGNORE_NULL);
            assertEquals(new ClsA("1", "2", "1"), nullTo);
            // errors
            // ObjectConversionException oce1 = assertThrows(ObjectConversionException.class, () ->
            //     dataMapper.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new UnreachablePointException();
                    })
                    .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class));
            assertDoesNotThrow(() ->
                propertyCopier
                    .withPropertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        throw new RuntimeException();
                    })
                    .withExceptionHandler((e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    })
                    .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class)
            );
        }
        {
            // to raw map
            ClsB clsB = new ClsB(1, 2, 3);
            Map map = new HashMap();
            propertyCopier.copyProperties(clsB, map);
            assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), map);
        }
    }

    @Test
    public void testOptions() {
        assertSame(ConvertOption.IGNORE_NULL, ConvertOption.IGNORE_NULL.key());
        assertNull(ConvertOption.IGNORE_NULL.value());
        assertSame(ConvertOption.IGNORE_NULL, ConvertOption.ignoreNull());
    }

    @Test
    public void testPropertyNameMapper() {
        {
            // map to
            Map<String, String> map1 = MapKit.map("1", "1", "2", "2");
            Map<String, String> map2 = new HashMap<>();
            PropertyCopier.defaultCopier().copyProperties(map1, map2, ConvertOption.propertyNameMapper(
                (name, type) -> name + "1"));
            assertEquals(MapKit.map("11", "1", "21", "2"), map2);
            Map<Integer, String> map3 = MapKit.map(1, "1", 2, "2");
            Map<Integer, String> map4 = new HashMap<>();
            PropertyCopier.defaultCopier().copyProperties(map3, map4, ConvertOption.propertyNameMapper(
                (name, type) -> name + "1"));
            assertEquals(MapKit.map(1, "1", 2, "2"), map4);
            Map<String, String> map5 = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsA3 cls6 = new ClsA3();
            PropertyCopier.defaultCopier().copyProperties(map5, cls6, ConvertOption.propertyNameMapper(
                (name, type) -> name + "1"));
            assertEquals(new ClsA3("1", "2", "3"), cls6);
            Map<Integer, String> map7 = MapKit.map(1, "1", 2, "2", 3, "3");
            ClsA3 cls8 = new ClsA3();
            PropertyCopier.defaultCopier().copyProperties(map7, cls8, ConvertOption.propertyNameMapper(
                (name, type) -> name + "1"));
            assertEquals(new ClsA3(), cls8);
        }
        {
            // object to
            ClsA cls1 = new ClsA("1", "2", "3");
            Map<String, String> map2 = new HashMap<>();
            PropertyCopier.defaultCopier().copyProperties(cls1, map2, ConvertOption.propertyNameMapper(
                (name, type) -> name + "1"));
            assertEquals(MapKit.map("first1", "1", "second1", "2", "third1", "3"), map2);
            ClsA3 cls3 = new ClsA3();
            PropertyCopier.defaultCopier().copyProperties(cls1, cls3, ConvertOption.propertyNameMapper(
                (name, type) -> name + "1"));
            assertEquals(new ClsA3("1", "2", "3"), cls3);
        }
    }

    @Test
    public void testException() {
        {
            // ObjectCopyException
            assertThrows(ObjectCopyException.class, () -> {
                throw new ObjectCopyException();
            });
            assertThrows(ObjectCopyException.class, () -> {
                throw new ObjectCopyException("");
            });
            assertThrows(ObjectCopyException.class, () -> {
                throw new ObjectCopyException("", new RuntimeException());
            });
            assertThrows(ObjectCopyException.class, () -> {
                throw new ObjectCopyException(new RuntimeException());
            });
        }
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsA {
        private String first;
        private String second;
        private String third;

        public void setForth(String forth) {
            // this.third = forth;
        }

        public void clear() {
            first = null;
            second = null;
            third = null;
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsA1 {
        @Getter
        @Setter
        private String first;
        @Getter
        private String second;
        // private String third;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsA2 {
        @Getter
        @Setter
        private String first2;
        @Getter
        private String second2;
        // private String third2;
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsA3 {
        private String first1;
        private String second1;
        private String third1;
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsB {
        private Integer first;
        private Integer second;
        private Integer third;
    }
}
