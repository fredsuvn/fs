package tests.object.convert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.testng.annotations.Test;
import tests.utils.ErrorMap;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.object.convert.DataMapper;
import xyz.sunqian.common.object.convert.MappingOption;
import xyz.sunqian.common.object.convert.ObjectConversionException;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.data.MapSchemaParser;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.object.data.ObjectSchema;
import xyz.sunqian.common.object.data.ObjectSchemaParser;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class DataMapperTest implements PrintTest {

    @Test
    public void testMapping() {
        testMapping(DataMapper.defaultMapper());
        testMapping(DataMapper.newMapper(new HashMap<>()));
        {
            // default method
            ClsA a = new ClsA("1", "2", "3");
            ClsB b1 = new ClsB();
            DataMapper.defaultMapper().copyProperties(a, b1);
            assertEquals(b1, new ClsB(1, 2, 3));
            ClsB b2 = new ClsB();
            DataMapper.defaultMapper().copyProperties(a, b2, ObjectConverter.defaultConverter());
            assertEquals(b2, new ClsB(1, 2, 3));
        }
    }

    private void testMapping(DataMapper dataMapper) {
        Type typeA = new TypeRef<Map<String, String>>() {}.type();
        Type typeB = new TypeRef<Map<String, Integer>>() {}.type();
        {
            // map to map
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            Map<String, Integer> mapB = new HashMap<>();
            dataMapper.copyProperties(
                mapA, typeA, mapB, typeB, MappingOption.schemaParser(MapSchemaParser.defaultParser()));
            assertEquals(mapB, MapKit.map("first", 1, "second", 2, "third", 3));
            Map<String, String> mapA2 = new HashMap<>();
            dataMapper.copyProperties(mapA, typeA, mapA2, typeA, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(mapA2.isEmpty());
            dataMapper.copyProperties(mapA, typeA, mapA2, typeA, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    Map<String, String> ma = Jie.as(src);
                    String key = (String) propertyName;
                    return MapKit.entry(key + "2", ma.get(key));
                }));
            assertEquals(mapA2, MapKit.map("first2", "1", "second2", "2", "third2", "3"));
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            Map<String, String> nullTo = new HashMap<>();
            dataMapper.copyProperties(nullFrom, typeA, nullTo, typeA, MappingOption.IGNORE_NULL);
            assertEquals(nullTo, MapKit.map("first", "1", "third", "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, typeA, nullTo, typeA, MappingOption.ignoreProperties("first"));
            assertEquals(nullTo, MapKit.map("second", null, "third", "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, typeA, nullTo, typeA, MappingOption.IGNORE_NULL,
                MappingOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(nullTo, MapKit.map("first", "1", "second", "2", "third", "1"));
            // errors
            ObjectConversionException oce1 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConversionException oce2 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(mapA, typeA, mapA2, typeA,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConversionException oce3 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(mapA, typeA, mapA2, typeA,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    MappingOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
        }
        {
            // map to object
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsB clsB = new ClsB();
            dataMapper.copyProperties(
                mapA, typeA, clsB, ClsB.class, MappingOption.schemaParser(ObjectSchemaParser.defaultParser()));
            assertEquals(clsB, new ClsB(1, 2, 3));
            ClsA2 clsA2 = new ClsA2();
            dataMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            dataMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    Map<String, String> ma = Jie.as(src);
                    String key = (String) propertyName;
                    return MapKit.entry(key + "2", ma.get(key));
                }));
            assertEquals(clsA2, new ClsA2("1", null));
            ClsA1 clsA12 = new ClsA1();
            dataMapper.copyProperties(mapA, typeA, clsA12, ClsA1.class);
            assertEquals(clsA12, new ClsA1("1", null));
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            ClsA nullTo = new ClsA();
            dataMapper.copyProperties(nullFrom, typeA, nullTo, ClsA.class, MappingOption.IGNORE_NULL);
            assertEquals(nullTo, new ClsA("1", null, "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, typeA, nullTo, ClsA.class, MappingOption.ignoreProperties("first"));
            assertEquals(nullTo, new ClsA(null, null, "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, typeA, nullTo, ClsA.class, MappingOption.IGNORE_NULL,
                MappingOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(nullTo, new ClsA("1", "2", "1"));
            // errors
            ObjectConversionException oce1 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConversionException oce2 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConversionException oce3 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(mapA, typeA, clsA2, ClsA2.class,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    MappingOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
        }
        {
            // object to map
            ClsA clsA = new ClsA("1", "2", "3");
            Map<String, Integer> mapB = new HashMap<>();
            dataMapper.copyProperties(clsA, ClsA.class, mapB, typeB);
            assertEquals(mapB, MapKit.map("first", 1, "second", 2, "third", 3));
            Map<String, String> mapA2 = new HashMap<>();
            dataMapper.copyProperties(clsA, ClsA.class, mapA2, typeA, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(mapA2.isEmpty());
            dataMapper.copyProperties(clsA, ClsA.class, mapA2, typeA, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    ObjectSchema os = Jie.as(srcSchema);
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
            dataMapper.copyProperties(nullFrom, ClsA.class, nullTo, typeA, MappingOption.IGNORE_NULL);
            assertEquals(nullTo, MapKit.map("first", "1", "third", "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, ClsA.class, nullTo, typeA, MappingOption.ignoreProperties("first"));
            assertEquals(nullTo, MapKit.map("second", null, "third", "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, ClsA.class, nullTo, typeA, MappingOption.IGNORE_NULL,
                MappingOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(nullTo, MapKit.map("first", "1", "second", "2", "third", "1"));
            // errors
            // ObjectConversionException oce1 = expectThrows(ObjectConversionException.class, () ->
            //     dataMapper.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConversionException oce2 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(clsA, ClsA.class, mapA2, typeA,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConversionException oce3 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(clsA, ClsA.class, mapA2, typeA,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    MappingOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
        }
        {
            // object to object
            ClsA clsA = new ClsA("1", "2", "3");
            ClsB clsB = new ClsB();
            dataMapper.copyProperties(clsA, ClsA.class, clsB, ClsB.class);
            assertEquals(clsB, new ClsB(1, 2, 3));
            ClsA2 clsA2 = new ClsA2();
            dataMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> null));
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            dataMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class, MappingOption.propertyMapper(
                (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    ObjectSchema os = Jie.as(srcSchema);
                    String key = (String) propertyName;
                    ObjectProperty op = os.getProperty(key);
                    if (op == null) {
                        return null;
                    }
                    return MapKit.entry(key + "2", op.getValue(src));
                }));
            assertEquals(clsA2, new ClsA2("1", null));
            ClsA1 clsA12 = new ClsA1();
            dataMapper.copyProperties(clsA, ClsA.class, clsA12, ClsA1.class);
            assertEquals(clsA12, new ClsA1("1", null));
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            ClsA nullTo = new ClsA();
            dataMapper.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, MappingOption.IGNORE_NULL);
            assertEquals(nullTo, new ClsA("1", null, "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, MappingOption.ignoreProperties("first"));
            assertEquals(nullTo, new ClsA(null, null, "3"));
            nullTo.clear();
            dataMapper.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, MappingOption.IGNORE_NULL,
                MappingOption.propertyMapper((propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                    if ("second".equals(propertyName)) {
                        return MapKit.entry(propertyName, "2");
                    } else {
                        return MapKit.entry(propertyName, "1");
                    }
                }));
            assertEquals(nullTo, new ClsA("1", "2", "1"));
            // errors
            // ObjectConversionException oce1 = expectThrows(ObjectConversionException.class, () ->
            //     dataMapper.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConversionException oce2 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
            ObjectConversionException oce3 = expectThrows(ObjectConversionException.class, () ->
                dataMapper.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class,
                    MappingOption.propertyMapper(
                        (propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new RuntimeException();
                        }),
                    MappingOption.exceptionHandler(
                        (e, propertyName, src, srcSchema, dst, dstSchema, converter, options) -> {
                            throw new UnreachablePointException();
                        })));
            assertTrue(oce3.getCause().getCause() instanceof UnreachablePointException);
        }
    }

    @Test
    public void testOptions() {
        assertSame(MappingOption.IGNORE_NULL.key(), MappingOption.IGNORE_NULL);
        assertNull(MappingOption.IGNORE_NULL.value());
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
