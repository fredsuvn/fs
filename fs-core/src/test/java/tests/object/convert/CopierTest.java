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
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.exception.UnreachablePointException;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConvertException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.convert.ObjectCopyException;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CopierTest implements PrintTest {

    @Test
    public void testCopyProperties() {
        testCopyProperties(ObjectCopier.defaultCopier());
        // just continue
        ObjectCopier.Handler continueHandler = new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }
        };
        testCopyProperties(ObjectCopier.defaultCopier().withFirstHandler(continueHandler));
        {
            // empty handler
            ObjectCopier copier = ObjectCopier.newCopier();
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsA clsA = new ClsA("1", "2", "3");
            Map<String, String> map = new HashMap<>();
            ClsA cls = new ClsA();
            copier.copyProperties(mapA, map);
            assertEquals(Collections.emptyMap(), map);
            copier.copyProperties(mapA, cls);
            assertEquals(new ClsA(), cls);
            copier.copyProperties(clsA, map);
            assertEquals(Collections.emptyMap(), map);
            copier.copyProperties(clsA, cls);
            assertEquals(new ClsA(), cls);
        }
        {
            // include class
            ObjectCopier copier = ObjectCopier.defaultCopier();
            ClsA clsA = new ClsA("1", "2", "3");
            Map<String, String> map = new HashMap<>();
            copier.copyProperties(clsA, map, ConvertOption.includeClass(true));
            assertEquals(MapKit.map("first", "1", "second", "2", "third", "3", "class", ClsA.class), map);
            map.clear();
            ClsB clsB = new ClsB();
            copier.copyProperties(clsA, clsB, ConvertOption.includeClass(true));
            assertEquals(new ClsB(1, 2, 3), clsB);
        }
        {
            // default method
            ClsA a = new ClsA("1", "2", "3");
            ClsB b1 = new ClsB();
            ObjectCopier.defaultCopier().copyProperties(a, b1);
            assertEquals(new ClsB(1, 2, 3), b1);
            ClsB b2 = new ClsB();
            ObjectCopier.defaultCopier().copyProperties(a, b2, ObjectConverter.defaultConverter());
            assertEquals(new ClsB(1, 2, 3), b2);
        }
    }

    private void testCopyProperties(ObjectCopier objectCopier) {
        Type typeA = new TypeRef<Map<String, String>>() {}.type();
        Type typeB = new TypeRef<Map<String, Integer>>() {}.type();

        // no copy
        ObjectCopier.Handler noCopyHandler = new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }
        };

        // key + "2"
        ObjectCopier.Handler keyPlusHandler = new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return ObjectCopier.Handler.super.copyProperty(
                    (srcKey + "2"), srcValue, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return ObjectCopier.Handler.super.copyProperty(
                    (srcKey + "2"), srcValue, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                return ObjectCopier.Handler.super.copyProperty(
                    srcPropertyName + "2", srcProperty, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                return ObjectCopier.Handler.super.copyProperty(
                    srcPropertyName + "2", srcProperty, src, srcSchema, dst, dstSchema, converter, options);
            }
        };

        // value to "1"/"2"
        ObjectCopier.Handler valueChangeHandler = new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                Object value;
                if ("second".equals(srcKey)) {
                    value = "2";
                } else {
                    value = "1";
                }
                return ObjectCopier.Handler.super.copyProperty(
                    srcKey, value, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                Object value;
                if ("second".equals(srcKey)) {
                    value = "2";
                } else {
                    value = "1";
                }
                return ObjectCopier.Handler.super.copyProperty(
                    srcKey, value, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                Object value;
                if ("second".equals(srcPropertyName)) {
                    value = "2";
                } else {
                    value = "1";
                }
                dst.put(srcPropertyName, value);
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                Object value;
                if ("second".equals(srcPropertyName)) {
                    value = "2";
                } else {
                    value = "1";
                }
                dstSchema.getProperty(srcPropertyName).setValue(dst, value);
                return false;
            }
        };

        // error handler
        ObjectCopier.Handler errorHandler = new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();

            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();

            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull ObjectProperty srcProperty, @Nonnull Object src, @Nonnull ObjectSchema srcSchema, @Nonnull Object dst, @Nonnull ObjectSchema dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();

            }
        };

        {
            // map to map
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            Map<String, Integer> mapB = new HashMap<>();
            objectCopier.copyProperties(
                mapA, typeA, mapB, typeB, ConvertOption.schemaParser(MapParser.defaultParser()));
            assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), mapB);
            Map<String, String> mapA2 = new HashMap<>();
            objectCopier
                .withFirstHandler(noCopyHandler)
                .copyProperties(mapA, typeA, mapA2, typeA);
            assertTrue(mapA2.isEmpty());
            objectCopier
                .withFirstHandler(keyPlusHandler)
                .copyProperties(mapA, typeA, mapA2, typeA);
            assertEquals(MapKit.map("first2", "1", "second2", "2", "third2", "3"), mapA2);
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            Map<String, String> nullTo = new HashMap<>();
            objectCopier.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreNull(true));
            assertEquals(MapKit.map("first", "1", "third", "3"), nullTo);
            nullTo.clear();
            objectCopier.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreProperties("first"));
            assertEquals(MapKit.map("second", null, "third", "3"), nullTo);
            nullTo.clear();
            objectCopier
                .withFirstHandler(valueChangeHandler)
                .copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreNull(true));
            assertEquals(MapKit.map("first", "1", "second", "2", "third", "1"), nullTo);
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                objectCopier.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                objectCopier
                    .withFirstHandler(errorHandler)
                    .copyProperties(mapA, typeA, mapA2, typeA));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
        }
        {
            // map to object
            Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsB clsB = new ClsB();
            objectCopier.copyProperties(
                mapA, typeA, clsB, ClsB.class, ConvertOption.schemaParser(ObjectParser.defaultParser()));
            assertEquals(new ClsB(1, 2, 3), clsB);
            ClsA2 clsA2 = new ClsA2();
            objectCopier
                .withFirstHandler(noCopyHandler)
                .copyProperties(mapA, typeA, clsA2, ClsA2.class);
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            objectCopier
                .withFirstHandler(keyPlusHandler)
                .copyProperties(mapA, typeA, clsA2, ClsA2.class);
            assertEquals(new ClsA2("1", null), clsA2);
            ClsA1 clsA12 = new ClsA1();
            objectCopier.copyProperties(mapA, typeA, clsA12, ClsA1.class);
            assertEquals(new ClsA1("1", null), clsA12);
            // ignore
            Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
            ClsA nullTo = new ClsA();
            objectCopier.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
            assertEquals(new ClsA("1", null, "3"), nullTo);
            nullTo.clear();
            objectCopier.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
            assertEquals(new ClsA(null, null, "3"), nullTo);
            nullTo.clear();
            objectCopier
                .withFirstHandler(valueChangeHandler)
                .copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
            assertEquals(new ClsA("1", "2", "1"), nullTo);
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                objectCopier.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                objectCopier
                    .withFirstHandler(errorHandler)
                    .copyProperties(mapA, typeA, clsA2, ClsA2.class));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
        }
        {
            // object to map
            ClsA clsA = new ClsA("1", "2", "3");
            Map<String, Integer> mapB = new HashMap<>();
            objectCopier.copyProperties(clsA, ClsA.class, mapB, typeB);
            assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), mapB);
            Map<String, String> mapA2 = new HashMap<>();
            objectCopier
                .withFirstHandler(noCopyHandler)
                .copyProperties(clsA, ClsA.class, mapA2, typeA);
            assertTrue(mapA2.isEmpty());
            objectCopier
                .withFirstHandler(keyPlusHandler)
                .copyProperties(clsA, ClsA.class, mapA2, typeA);
            assertEquals(MapKit.map("first2", "1", "second2", "2", "third2", "3"), mapA2);
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            Map<String, String> nullTo = new HashMap<>();
            objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreNull(true));
            assertEquals(MapKit.map("first", "1", "third", "3"), nullTo);
            nullTo.clear();
            objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreProperties("first"));
            assertEquals(MapKit.map("second", null, "third", "3"), nullTo);
            nullTo.clear();
            objectCopier
                .withFirstHandler(valueChangeHandler)
                .copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreNull(true));
            assertEquals(MapKit.map("first", "1", "second", "2", "third", "1"), nullTo);
            // errors
            ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
                objectCopier.copyProperties(clsA, ClsA.class, new ErrorMap<>(), typeA)
            );
            assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                objectCopier
                    .withFirstHandler(errorHandler)
                    .copyProperties(clsA, ClsA.class, mapA2, typeA));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
        }
        {
            // object to object
            ClsA clsA = new ClsA("1", "2", "3");
            ClsB clsB = new ClsB();
            objectCopier.copyProperties(clsA, ClsA.class, clsB, ClsB.class);
            assertEquals(new ClsB(1, 2, 3), clsB);
            ClsA2 clsA2 = new ClsA2();
            objectCopier
                .withFirstHandler(noCopyHandler)
                .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class);
            assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);
            objectCopier
                .withFirstHandler(keyPlusHandler)
                .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class);
            assertEquals(new ClsA2("1", null), clsA2);
            ClsA1 clsA12 = new ClsA1();
            objectCopier.copyProperties(clsA, ClsA.class, clsA12, ClsA1.class);
            assertEquals(new ClsA1("1", null), clsA12);
            // ignore
            ClsA nullFrom = new ClsA("1", null, "3");
            ClsA nullTo = new ClsA();
            objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
            assertEquals(new ClsA("1", null, "3"), nullTo);
            nullTo.clear();
            objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
            assertEquals(new ClsA(null, null, "3"), nullTo);
            nullTo.clear();
            objectCopier
                .withFirstHandler(valueChangeHandler)
                .copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
            assertEquals(new ClsA("1", "2", "1"), nullTo);
            // errors
            // ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
            //     objectCopier.copyProperties(clsA, ClsA.class, clsA2, ClsA2.class)
            // );
            // assertTrue(oce1.getCause() instanceof UnsupportedOperationException);
            ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
                objectCopier
                    .withFirstHandler(errorHandler)
                    .copyProperties(clsA, ClsA.class, clsA2, ClsA2.class));
            assertTrue(oce2.getCause() instanceof UnreachablePointException);
        }
        {
            // to raw map
            ClsB clsB = new ClsB(1, 2, 3);
            Map map = new HashMap();
            objectCopier.copyProperties(clsB, map);
            assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), map);
        }
    }

    @Test
    public void testPropertyNameMapper() {
        {
            // map to
            Map<String, String> map1 = MapKit.map("1", "1", "2", "2");
            Map<String, String> map2 = new HashMap<>();
            ObjectCopier.defaultCopier().copyProperties(map1, map2, ConvertOption.propertyNameMapper(
                (name) -> name + "1"));
            assertEquals(MapKit.map("11", "1", "21", "2"), map2);
            Map<Integer, String> map3 = MapKit.map(1, "1", 2, "2");
            Map<Integer, String> map4 = new HashMap<>();
            ObjectCopier.defaultCopier().copyProperties(map3, map4, ConvertOption.propertyNameMapper(
                (name) -> name + "1"));
            assertEquals(MapKit.map(1, "1", 2, "2"), map4);
            Map<String, String> map5 = MapKit.map("first", "1", "second", "2", "third", "3");
            ClsA3 cls6 = new ClsA3();
            ObjectCopier.defaultCopier().copyProperties(map5, cls6, ConvertOption.propertyNameMapper(
                (name) -> name + "1"));
            assertEquals(new ClsA3("1", "2", "3"), cls6);
            Map<Integer, String> map7 = MapKit.map(1, "1", 2, "2", 3, "3");
            ClsA3 cls8 = new ClsA3();
            ObjectCopier.defaultCopier().copyProperties(map7, cls8, ConvertOption.propertyNameMapper(
                (name) -> name + "1"));
            assertEquals(new ClsA3(), cls8);
        }
        {
            // object to
            ClsA cls1 = new ClsA("1", "2", "3");
            Map<String, String> map2 = new HashMap<>();
            ObjectCopier.defaultCopier().copyProperties(cls1, map2, ConvertOption.propertyNameMapper(
                (name) -> name + "1"));
            assertEquals(MapKit.map("first1", "1", "second1", "2", "third1", "3"), map2);
            ClsA3 cls3 = new ClsA3();
            ObjectCopier.defaultCopier().copyProperties(cls1, cls3, ConvertOption.propertyNameMapper(
                (name) -> name + "1"));
            assertEquals(new ClsA3("1", "2", "3"), cls3);
        }
    }

    @Test
    public void testDefaultOptions() {
        ObjectCopier copier = ObjectCopier.defaultCopier();
        ObjectCopier copierOps = copier.withDefaultOptions(
            ConvertOption.ignoreNull(true)
        );
        assertEquals(
            Collections.emptyList(),
            copier.defaultOptions()
        );
        assertEquals(
            ListKit.list(ConvertOption.ignoreNull(true)),
            copierOps.defaultOptions()
        );
        ClsA source = new ClsA("1", null, "3");
        Map<String, Object> dst1 = new HashMap<>();
        copier.copyProperties(source, dst1);
        assertTrue(dst1.containsKey("second"));
        Map<String, Object> dst2 = new HashMap<>();
        copierOps.copyProperties(source, dst2);
        assertFalse(dst2.containsKey("second"));
    }

    @Test
    public void testStrictSourceType() {
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                Object.class,
                ClsA.class
            )
        );
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                Object.class,
                ClsA.class,
                ConvertOption.strictSourceTypeMode(false)
            )
        );
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                List.class.getTypeParameters()[0],
                ClsA.class
            )
        );
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                List.class.getTypeParameters()[0],
                ClsA.class,
                ConvertOption.strictSourceTypeMode(false)
            )
        );
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                ClsA.class
            )
        );
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                ClsA.class
            )
        );
        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                Object.class,
                ClsA.class,
                ConvertOption.strictSourceTypeMode(true)
            )
        );
        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                List.class.getTypeParameters()[0],
                ClsA.class,
                ConvertOption.strictSourceTypeMode(true)
            )
        );

        class XMap extends HashMap<String, String> {
        }
        XMap xmap = new XMap();
        xmap.put("first", "1");
        xmap.put("second", "2");
        xmap.put("third", "3");
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                xmap,
                Object.class,
                ClsA.class
            )
        );
        @Data
        @EqualsAndHashCode
        @AllArgsConstructor
        @NoArgsConstructor
        class XObject {
            private Integer first;
            private Integer second;
            private Integer third;
        }
        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(
                new XObject(1, 2, 3),
                ClsA.class
            )
        );
        assertEquals(
            new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                new ClsB(1, 2, 3),
                XObject.class,
                ClsA.class
            )
        );
        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(
                new ClsB(1, 2, 3),
                XObject.class,
                ClsA.class,
                ConvertOption.strictSourceTypeMode(true)
            )
        );
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

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsDates {
        @Format("yyyy-MM-dd")
        private Date first;
        @Format("HH:mm:ss")
        private Date second;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({
        ElementType.FIELD,
    })
    public @interface Format {
        String value();
    }
}
