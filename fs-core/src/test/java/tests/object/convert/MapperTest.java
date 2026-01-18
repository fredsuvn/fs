package tests.object.convert;

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
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConvertException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.PropertiesMapper;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.reflect.TypeRef;
import tests.utils.ErrorMap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapperTest implements PrintTest {

    @Test
    public void testMapping() {
        testMapping(PropertiesMapper.defaultMapper());
        testMapping(PropertiesMapper.newMapper(CacheFunction.ofMap(new HashMap<>())));
        {
            // default method
            ClsA a = new ClsA("1", "2", "3");
            ClsB b1 = new ClsB();
            PropertiesMapper.defaultMapper().copyProperties(a, b1);
            assertEquals(new ClsB(1, 2, 3), b1);
            ClsB b2 = new ClsB();
            PropertiesMapper.defaultMapper().copyProperties(a, b2, ObjectConverter.defaultConverter());
            assertEquals(new ClsB(1, 2, 3), b2);
        }
    }

    private void testMapping(PropertiesMapper propertiesMapper) {
        Type typeA = new TypeRef<Map<String, String>>() {}.type();
        Type typeB = new TypeRef<Map<String, Integer>>() {}.type();
        {
            // map to map
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            Map<String, Integer> mapB = new HashMap<>();
            propertiesMapper.copyProperties(
                mapA, typeA, mapB, typeB, ConvertOption.schemaParser(MapParser.defaultParser()));
            assertEquals(mapB, MapKit.map("first", 1, "second", 2, "third", 3));
            Map<String, String> mapA2 = new HashMap<>();
            propertiesMapper.copyProperties(mapA, typeA, mapA2, typeA, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(mapA2.isEmpty());
            propertiesMapper.copyProperties(mapA, typeA, mapA2, typeA, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    Map<String, String> ma = Fs.as(src);
                    String key = (String) propertyName;
                    return MapKit.entry(key + "2", ma.get(key));
                }));
            assertEquals(mapA2, MapKit.map("first2", "1", "second2", "2", "third2", "3"));
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            Map<String, String> nullTo = new HashMap<>();
            propertiesMapper.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.IGNORE_NULL);
            assertEquals(nullTo, MapKit.map("first", "1", "third", "3"));
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreProperties("first"));
            assertEquals(nullTo, MapKit.map("second", null, "third", "3"));
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.IGNORE_NULL,
                ConvertOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(nullTo, MapKit.map("first", "1", "second", "2", "third", "1"));
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(mapA, typeA, mapA2, typeA,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(mapA, typeA, mapA2, typeA,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
            assertDoesNotThrow(() ->
                propertiesMapper.copyProperties(mapA, typeA, mapA2, typeA,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        }))
            );
        }
        {
            // map to object
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsB clsB = new ClsB();
            propertiesMapper.copyProperties(
                mapA, typeA, clsB, ClsB.class, ConvertOption.schemaParser(ObjectParser.defaultParser()));
            assertEquals(new ClsB(1, 2, 3), clsB);
            ClsA2 clsA2 = new ClsA2();
            propertiesMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            propertiesMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    Map<String, String> ma = Fs.as(src);
                    String key = (String) propertyName;
                    return MapKit.entry(key + "2", ma.get(key));
                }));
            assertEquals(new ClsA2("1", null), clsA2);
            ClsA1 clsA12 = new ClsA1();
            propertiesMapper.copyProperties(mapA, typeA, clsA12, ClsA1.class);
            assertEquals(new ClsA1("1", null), clsA12);
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            ClsA nullTo = new ClsA();
            propertiesMapper.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.IGNORE_NULL);
            assertEquals(new ClsA("1", null, "3"), nullTo);
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
            assertEquals(new ClsA(null, null, "3"), nullTo);
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.IGNORE_NULL,
                ConvertOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(new ClsA("1", "2", "1"), nullTo);
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
            assertDoesNotThrow(() ->
                propertiesMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        }))
            );
        }
        {
            // object to map
            ClsA clsA = new ClsA("1", "2", "3");
            Map<String, Integer> mapB = new HashMap<>();
            propertiesMapper.copyProperties(clsA, ClsA.class, mapB, typeB);
            assertEquals(mapB, MapKit.map("first", 1, "second", 2, "third", 3));
            Map<String, String> mapA2 = new HashMap<>();
            propertiesMapper.copyProperties(clsA, ClsA.class, mapA2, typeA, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(mapA2.isEmpty());
            propertiesMapper.copyProperties(clsA, ClsA.class, mapA2, typeA, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    ObjectSchema os = Fs.as(srcSchema);
                    String key = (String) propertyName;
                    ObjectProperty op = os.getProperty(key);
                    if (op == null) {
                        return null;
                    }
                    return MapKit.entry(key + "2", op.getValue(src));
                }));
            assertEquals(mapA2, MapKit.map("first2", "1", "second2", "2", "third2", "3"));
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            Map<String, String> nullTo = new HashMap<>();
            propertiesMapper.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.IGNORE_NULL);
            assertEquals(nullTo, MapKit.map("first", "1", "third", "3"));
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreProperties("first"));
            assertEquals(nullTo, MapKit.map("second", null, "third", "3"));
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.IGNORE_NULL,
                ConvertOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(nullTo, MapKit.map("first", "1", "second", "2", "third", "1"));
            // errors
            // ObjectConversionException oce1 = assertThrows(ObjectConversionException.class, () ->
            //     dataMapper.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(clsA, ClsA.class, mapA2, typeA,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(clsA, ClsA.class, mapA2, typeA,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
            assertDoesNotThrow(() ->
                propertiesMapper.copyProperties(clsA, ClsA.class, mapA2, typeA,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        }))
            );
        }
        {
            // object to object
            ClsA clsA = new ClsA("1", "2", "3");
            ClsB clsB = new ClsB();
            propertiesMapper.copyProperties(clsA, ClsA.class, clsB, ClsB.class);
            assertEquals(new ClsB(1, 2, 3), clsB);
            ClsA2 clsA2 = new ClsA2();
            propertiesMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            propertiesMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class, ConvertOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    ObjectSchema os = Fs.as(srcSchema);
                    String key = (String) propertyName;
                    ObjectProperty op = os.getProperty(key);
                    if (op == null) {
                        return null;
                    }
                    return MapKit.entry(key + "2", op.getValue(src));
                }));
            assertEquals(new ClsA2("1", null), clsA2);
            ClsA1 clsA12 = new ClsA1();
            propertiesMapper.copyProperties(clsA, ClsA.class, clsA12, ClsA1.class);
            assertEquals(new ClsA1("1", null), clsA12);
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            ClsA nullTo = new ClsA();
            propertiesMapper.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.IGNORE_NULL);
            assertEquals(new ClsA("1", null, "3"), nullTo);
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
            assertEquals(new ClsA(null, null, "3"), nullTo);
            nullTo.clear();
            propertiesMapper.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.IGNORE_NULL,
                ConvertOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(new ClsA("1", "2", "1"), nullTo);
            // errors
            // ObjectConversionException oce1 = assertThrows(ObjectConversionException.class, () ->
            //     dataMapper.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConvertException oce3 = assertThrows(ObjectConvertException.class, () ->
                propertiesMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertDoesNotThrow(() ->
                propertiesMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class,
                    ConvertOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    ConvertOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                        }))
            );
        }
        {
            // to raw map
            ClsB clsB = new ClsB(1, 2, 3);
            Map map = new HashMap();
            propertiesMapper.copyProperties(clsB, map);
            assertEquals(map, MapKit.map("first", 1, "second", 2, "third", 3));
        }
    }

    @Test
    public void testOptions() {
        assertSame(ConvertOption.IGNORE_NULL, ConvertOption.IGNORE_NULL.key());
        assertNull(ConvertOption.IGNORE_NULL.value());
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
    public static class ClsB {
        private Integer first;
        private Integer second;
        private Integer third;
    }
}
