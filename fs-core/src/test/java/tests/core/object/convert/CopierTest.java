package tests.core.object.convert;

import internal.utils.ErrorMap;
import internal.utils.TestPrint;
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
import space.sunqian.fs.object.convert.handlers.CommonCopierHandler;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.MapMetaManager;
import space.sunqian.fs.object.meta.PropertyMetaMeta;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaManager;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CopierTest implements TestPrint {

    @Test
    public void testCopyProperties() {
        testCopyProperties(ObjectCopier.defaultCopier());
        testEmptyHandler();
        testIncludeClass();
        testDefaultMethod();
    }

    @Test
    public void testAsHandler() {
        testContinueHandler();
        testEmpty();
    }

    @Test
    public void testPropertyNameMapper() {
        testNameMapperMapTo();
        testNameMapperObjectTo();
    }

    @Test
    public void testDefaultOptions() {
        ObjectCopier copier = ObjectCopier.defaultCopier();
        ObjectCopier copierOps = copier.withDefaultOptions(ConvertOption.ignoreNull(true));

        assertEquals(Collections.emptyList(), copier.defaultOptions());
        assertEquals(ListKit.list(ConvertOption.ignoreNull(true)), copierOps.defaultOptions());

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
        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                Object.class, ClsA.class));

        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                Object.class, ClsA.class, ConvertOption.strictSourceTypeMode(false)));

        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                List.class.getTypeParameters()[0], ClsA.class));

        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                List.class.getTypeParameters()[0], ClsA.class, ConvertOption.strictSourceTypeMode(false)));

        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"), ClsA.class));

        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(
                MapKit.map("first", "1", "second", "2", "third", "3"),
                Object.class, ClsA.class, ConvertOption.strictSourceTypeMode(true)));

        class XMap extends HashMap<String, String> {}
        XMap xmap = new XMap();
        xmap.put("first", "1");
        xmap.put("second", "2");
        xmap.put("third", "3");
        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(xmap, Object.class, ClsA.class));

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
            ObjectConverter.defaultConverter().convert(new XObject(1, 2, 3), ClsA.class));

        assertEquals(new ClsA("1", "2", "3"),
            ObjectConverter.defaultConverter().convert(new ClsB(1, 2, 3), XObject.class, ClsA.class));

        assertThrows(ObjectConvertException.class, () ->
            ObjectConverter.defaultConverter().convert(new ClsB(1, 2, 3), XObject.class, ClsA.class,
                ConvertOption.strictSourceTypeMode(true)));
    }

    @Test
    public void testException() {
        assertThrows(ObjectCopyException.class, () -> {throw new ObjectCopyException();});
        assertThrows(ObjectCopyException.class, () -> {throw new ObjectCopyException("");});
        assertThrows(ObjectCopyException.class, () -> {throw new ObjectCopyException("", new RuntimeException());});
        assertThrows(ObjectCopyException.class, () -> {throw new ObjectCopyException(new RuntimeException());});
    }

    private void testCopyProperties(ObjectCopier objectCopier) {
        Type typeA = new TypeRef<Map<String, String>>() {}.type();
        Type typeB = new TypeRef<Map<String, Integer>>() {}.type();

        ObjectCopier.Handler noCopyHandler = createNoCopyHandler();
        ObjectCopier.Handler keyPlusHandler = createKeyPlusHandler();
        ObjectCopier.Handler valueChangeHandler = createValueChangeHandler();
        ObjectCopier.Handler errorHandler = createErrorHandler();

        testMapToMap(objectCopier, typeA, typeB, noCopyHandler, keyPlusHandler, valueChangeHandler, errorHandler);
        testMapToObject(objectCopier, typeA, noCopyHandler, keyPlusHandler, valueChangeHandler, errorHandler);
        testObjectToMap(objectCopier, typeA, typeB, noCopyHandler, keyPlusHandler, valueChangeHandler, errorHandler);
        testObjectToObject(objectCopier, noCopyHandler, keyPlusHandler, valueChangeHandler, errorHandler);
        testToRawMap(objectCopier);
    }

    private void testEmptyHandler() {
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

    private void testIncludeClass() {
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

    private void testDefaultMethod() {
        ClsA a = new ClsA("1", "2", "3");
        ClsB b1 = new ClsB();
        ObjectCopier.defaultCopier().copyProperties(a, b1);
        assertEquals(new ClsB(1, 2, 3), b1);

        ClsB b2 = new ClsB();
        ObjectCopier.defaultCopier().copyProperties(a, b2, ObjectConverter.defaultConverter());
        assertEquals(new ClsB(1, 2, 3), b2);
    }

    private void testContinueHandler() {
        ObjectCopier.Handler continueHandler = createContinueHandler();
        ObjectCopier copier = ObjectCopier.defaultCopier().withFirstHandler(continueHandler);
        testCopyProperties(copier);
        testCopyProperties(ObjectCopier.newCopier(copier.asHandler()));
    }

    private void testEmpty() {
        ObjectCopier copier = ObjectCopier.newCopier();
        ObjectCopier copier2 = ObjectCopier.newCopier(copier.asHandler());
        Map<String, Integer> srcMap = MapKit.map("first", 1, "second", 2, "third", 3);
        Map<String, Integer> dstMap = new HashMap<>();
        ClsB srcCls = new ClsB(1, 2, 3);
        ClsB dstCls = new ClsB();

        copier2.copyProperties(srcMap, dstMap);
        assertEquals(0, dstMap.size());

        copier2.copyProperties(srcMap, dstCls);
        assertNull(dstCls.getFirst());
        assertNull(dstCls.getSecond());
        assertNull(dstCls.getThird());

        copier2.copyProperties(srcCls, dstMap);
        assertEquals(0, dstMap.size());

        copier2.copyProperties(srcCls, dstCls);
        assertNull(dstCls.getFirst());
        assertNull(dstCls.getSecond());
        assertNull(dstCls.getThird());
    }

    private void testNameMapperMapTo() {
        Map<String, String> map1 = MapKit.map("1", "1", "2", "2");
        Map<String, String> map2 = new HashMap<>();
        ObjectCopier.defaultCopier().copyProperties(map1, map2, ConvertOption.nameMapper(name -> name + "1"));
        assertEquals(MapKit.map("11", "1", "21", "2"), map2);

        Map<Integer, String> map3 = MapKit.map(1, "1", 2, "2");
        Map<Integer, String> map4 = new HashMap<>();
        ObjectCopier.defaultCopier().copyProperties(map3, map4, ConvertOption.nameMapper(name -> name + "1"));
        assertEquals(MapKit.map(1, "1", 2, "2"), map4);

        Map<String, String> map5 = MapKit.map("first", "1", "second", "2", "third", "3");
        ClsA3 cls6 = new ClsA3();
        ObjectCopier.defaultCopier().copyProperties(map5, cls6, ConvertOption.nameMapper(name -> name + "1"));
        assertEquals(new ClsA3("1", "2", "3"), cls6);

        Map<Integer, String> map7 = MapKit.map(1, "1", 2, "2", 3, "3");
        ClsA3 cls8 = new ClsA3();
        ObjectCopier.defaultCopier().copyProperties(map7, cls8, ConvertOption.nameMapper(name -> name + "1"));
        assertEquals(new ClsA3(), cls8);
    }

    private void testNameMapperObjectTo() {
        ClsA cls1 = new ClsA("1", "2", "3");
        Map<String, String> map2 = new HashMap<>();
        ObjectCopier.defaultCopier().copyProperties(cls1, map2, ConvertOption.nameMapper(name -> name + "1"));
        assertEquals(MapKit.map("first1", "1", "second1", "2", "third1", "3"), map2);

        ClsA3 cls3 = new ClsA3();
        ObjectCopier.defaultCopier().copyProperties(cls1, cls3, ConvertOption.nameMapper(name -> name + "1"));
        assertEquals(new ClsA3("1", "2", "3"), cls3);
    }

    private void testMapToMap(ObjectCopier objectCopier, Type typeA, Type typeB,
                              ObjectCopier.Handler noCopyHandler, ObjectCopier.Handler keyPlusHandler,
                              ObjectCopier.Handler valueChangeHandler, ObjectCopier.Handler errorHandler) {
        Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
        Map<String, Integer> mapB = new HashMap<>();
        objectCopier.copyProperties(mapA, typeA, mapB, typeB, ConvertOption.mapSchemaParser(MapMetaManager.defaultManager()));
        assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), mapB);

        Map<String, String> mapA2 = new HashMap<>();
        objectCopier.withFirstHandler(noCopyHandler).copyProperties(mapA, typeA, mapA2, typeA);
        assertTrue(mapA2.isEmpty());

        objectCopier.withFirstHandler(keyPlusHandler).copyProperties(mapA, typeA, mapA2, typeA);
        assertEquals(MapKit.map("first2", "1", "second2", "2", "third2", "3"), mapA2);

        testMapToMapIgnore(objectCopier, typeA, valueChangeHandler);
        testMapToMapErrors(objectCopier, typeA, errorHandler);
    }

    private void testMapToMapIgnore(ObjectCopier objectCopier, Type typeA, ObjectCopier.Handler valueChangeHandler) {
        Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
        Map<String, String> nullTo = new HashMap<>();

        objectCopier.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreNull(true));
        assertEquals(MapKit.map("first", "1", "third", "3"), nullTo);

        nullTo.clear();
        objectCopier.copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreProperties("first"));
        assertEquals(MapKit.map("second", null, "third", "3"), nullTo);

        nullTo.clear();
        objectCopier.withFirstHandler(valueChangeHandler).copyProperties(nullFrom, typeA, nullTo, typeA, ConvertOption.ignoreNull(true));
        assertEquals(MapKit.map("first", "1", "second", "2", "third", "1"), nullTo);
    }

    private void testMapToMapErrors(ObjectCopier objectCopier, Type typeA, ObjectCopier.Handler errorHandler) {
        Map<String, String> mapA2 = new HashMap<>();
        ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.copyProperties(new ErrorMap<>(), typeA, mapA2, typeA));
        assertTrue(oce1.getCause() instanceof UnsupportedOperationException);

        Map<String, String> nonEmptyMap = MapKit.map("first", "1");
        ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.withFirstHandler(errorHandler).copyProperties(nonEmptyMap, typeA, mapA2, typeA));
        assertTrue(oce2.getCause() instanceof UnreachablePointException);
    }

    private void testMapToObject(ObjectCopier objectCopier, Type typeA,
                                 ObjectCopier.Handler noCopyHandler, ObjectCopier.Handler keyPlusHandler,
                                 ObjectCopier.Handler valueChangeHandler, ObjectCopier.Handler errorHandler) {
        Map<String, String> mapA = MapKit.map("first", "1", "second", "2", "third", "3");
        ClsB clsB = new ClsB();
        objectCopier.copyProperties(mapA, typeA, clsB, ClsB.class, ConvertOption.objectSchemaParser(ObjectMetaManager.defaultParser()));
        assertEquals(new ClsB(1, 2, 3), clsB);

        ClsA2 clsA2 = new ClsA2();
        objectCopier.withFirstHandler(noCopyHandler).copyProperties(mapA, typeA, clsA2, ClsA2.class);
        assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);

        objectCopier.withFirstHandler(keyPlusHandler).copyProperties(mapA, typeA, clsA2, ClsA2.class);
        assertEquals(new ClsA2("1", null), clsA2);

        ClsA1 clsA12 = new ClsA1();
        objectCopier.copyProperties(mapA, typeA, clsA12, ClsA1.class);
        assertEquals(new ClsA1("1", null), clsA12);

        testMapToObjectIgnore(objectCopier, typeA, valueChangeHandler);
        testMapToObjectErrors(objectCopier, typeA, errorHandler, clsA2);
    }

    private void testMapToObjectIgnore(ObjectCopier objectCopier, Type typeA, ObjectCopier.Handler valueChangeHandler) {
        Map<String, String> nullFrom = MapKit.map("first", "1", "second", null, "third", "3");
        ClsA nullTo = new ClsA();

        objectCopier.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
        assertEquals(new ClsA("1", null, "3"), nullTo);

        nullTo.clear();
        objectCopier.copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
        assertEquals(new ClsA(null, null, "3"), nullTo);

        nullTo.clear();
        objectCopier.withFirstHandler(valueChangeHandler).copyProperties(nullFrom, typeA, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
        assertEquals(new ClsA("1", "2", "1"), nullTo);
    }

    private void testMapToObjectErrors(ObjectCopier objectCopier, Type typeA, ObjectCopier.Handler errorHandler, ClsA2 clsA2) {
        ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.copyProperties(new ErrorMap<>(), typeA, clsA2, ClsA2.class));
        assertTrue(oce1.getCause() instanceof UnsupportedOperationException);

        ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.withFirstHandler(errorHandler).copyProperties(MapKit.map("first", "1"), typeA, clsA2, ClsA2.class));
        assertTrue(oce2.getCause() instanceof UnreachablePointException);
    }

    private void testObjectToMap(ObjectCopier objectCopier, Type typeA, Type typeB,
                                 ObjectCopier.Handler noCopyHandler, ObjectCopier.Handler keyPlusHandler,
                                 ObjectCopier.Handler valueChangeHandler, ObjectCopier.Handler errorHandler) {
        ClsA clsA = new ClsA("1", "2", "3");
        Map<String, Integer> mapB = new HashMap<>();
        objectCopier.copyProperties(clsA, ClsA.class, mapB, typeB);
        assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), mapB);

        Map<String, String> mapA2 = new HashMap<>();
        objectCopier.withFirstHandler(noCopyHandler).copyProperties(clsA, ClsA.class, mapA2, typeA);
        assertTrue(mapA2.isEmpty());

        objectCopier.withFirstHandler(keyPlusHandler).copyProperties(clsA, ClsA.class, mapA2, typeA);
        assertEquals(MapKit.map("first2", "1", "second2", "2", "third2", "3"), mapA2);

        testObjectToMapIgnore(objectCopier, typeA, valueChangeHandler);
        testObjectToMapErrors(objectCopier, typeA, errorHandler, mapA2);
    }

    private void testObjectToMapIgnore(ObjectCopier objectCopier, Type typeA, ObjectCopier.Handler valueChangeHandler) {
        ClsA nullFrom = new ClsA("1", null, "3");
        Map<String, String> nullTo = new HashMap<>();

        objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreNull(true));
        assertEquals(MapKit.map("first", "1", "third", "3"), nullTo);

        nullTo.clear();
        objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreProperties("first"));
        assertEquals(MapKit.map("second", null, "third", "3"), nullTo);

        nullTo.clear();
        objectCopier.withFirstHandler(valueChangeHandler).copyProperties(nullFrom, ClsA.class, nullTo, typeA, ConvertOption.ignoreNull(true));
        assertEquals(MapKit.map("first", "1", "second", "2", "third", "1"), nullTo);
    }

    private void testObjectToMapErrors(ObjectCopier objectCopier, Type typeA, ObjectCopier.Handler errorHandler, Map<String, String> mapA2) {
        ObjectConvertException oce1 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.copyProperties(new ClsA("1", "2", "3"), ClsA.class, new ErrorMap<>(), typeA));
        assertTrue(oce1.getCause() instanceof UnsupportedOperationException);

        ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.withFirstHandler(errorHandler).copyProperties(new ClsA("1", "2", "3"), ClsA.class, mapA2, typeA));
        assertTrue(oce2.getCause() instanceof UnreachablePointException);
    }

    private void testObjectToObject(ObjectCopier objectCopier,
                                    ObjectCopier.Handler noCopyHandler, ObjectCopier.Handler keyPlusHandler,
                                    ObjectCopier.Handler valueChangeHandler, ObjectCopier.Handler errorHandler) {
        ClsA clsA = new ClsA("1", "2", "3");
        ClsB clsB = new ClsB();
        objectCopier.copyProperties(clsA, ClsA.class, clsB, ClsB.class);
        assertEquals(new ClsB(1, 2, 3), clsB);

        ClsA2 clsA2 = new ClsA2();
        objectCopier.withFirstHandler(noCopyHandler).copyProperties(clsA, ClsA.class, clsA2, ClsA2.class);
        assertTrue(clsA2.getFirst2() == null && clsA2.getSecond2() == null);

        objectCopier.withFirstHandler(keyPlusHandler).copyProperties(clsA, ClsA.class, clsA2, ClsA2.class);
        assertEquals(new ClsA2("1", null), clsA2);

        ClsA1 clsA12 = new ClsA1();
        objectCopier.copyProperties(clsA, ClsA.class, clsA12, ClsA1.class);
        assertEquals(new ClsA1("1", null), clsA12);

        testObjectToObjectIgnore(objectCopier, valueChangeHandler);
        testObjectToObjectErrors(objectCopier, errorHandler, clsA2);
    }

    private void testObjectToObjectIgnore(ObjectCopier objectCopier, ObjectCopier.Handler valueChangeHandler) {
        ClsA nullFrom = new ClsA("1", null, "3");
        ClsA nullTo = new ClsA();

        objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
        assertEquals(new ClsA("1", null, "3"), nullTo);

        nullTo.clear();
        objectCopier.copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreProperties("first"));
        assertEquals(new ClsA(null, null, "3"), nullTo);

        nullTo.clear();
        objectCopier.withFirstHandler(valueChangeHandler).copyProperties(nullFrom, ClsA.class, nullTo, ClsA.class, ConvertOption.ignoreNull(true));
        assertEquals(new ClsA("1", "2", "1"), nullTo);
    }

    private void testObjectToObjectErrors(ObjectCopier objectCopier, ObjectCopier.Handler errorHandler, ClsA2 clsA2) {
        ObjectConvertException oce2 = assertThrows(ObjectConvertException.class, () ->
            objectCopier.withFirstHandler(errorHandler).copyProperties(new ClsA("1", "2", "3"), ClsA.class, clsA2, ClsA2.class));
        assertTrue(oce2.getCause() instanceof UnreachablePointException);
    }

    private void testToRawMap(ObjectCopier objectCopier) {
        ClsB clsB = new ClsB(1, 2, 3);
        Map map = new HashMap();
        objectCopier.copyProperties(clsB, map);
        assertEquals(MapKit.map("first", 1, "second", 2, "third", 3), map);
    }

    private ObjectCopier.Handler createNoCopyHandler() {
        return new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return false;
            }
        };
    }

    private ObjectCopier.Handler createKeyPlusHandler() {
        class Key2Handler extends CommonCopierHandler {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return super.copyProperty((srcKey + "2"), srcValue, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return super.copyProperty((srcKey + "2"), srcValue, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                return super.copyProperty(srcPropertyName + "2", srcProperty, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                return super.copyProperty(srcPropertyName + "2", srcProperty, src, srcSchema, dst, dstSchema, converter, options);
            }
        }
        return new Key2Handler();
    }

    private ObjectCopier.Handler createValueChangeHandler() {
        class Value2Handler extends CommonCopierHandler {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                Object value = "second".equals(srcKey) ? "2" : "1";
                return super.copyProperty(srcKey, value, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                Object value = "second".equals(srcKey) ? "2" : "1";
                return super.copyProperty(srcKey, value, src, srcSchema, dst, dstSchema, converter, options);
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                Object value = "second".equals(srcPropertyName) ? "2" : "1";
                dst.put(srcPropertyName, value);
                return false;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                if ("class".equals(srcPropertyName) || !srcProperty.isReadable()) {
                    return false;
                }
                Object value = "second".equals(srcPropertyName) ? "2" : "1";
                dstSchema.getProperty(srcPropertyName).setValue(dst, value);
                return false;
            }
        }
        return new Value2Handler();
    }

    private ObjectCopier.Handler createErrorHandler() {
        return new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                throw new UnreachablePointException();
            }
        };
    }

    private ObjectCopier.Handler createContinueHandler() {
        return new ObjectCopier.Handler() {
            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }

            @Override
            public boolean copyProperty(@Nonnull Object srcKey, Object srcValue, @Nonnull Map<Object, Object> src, @Nonnull MapMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Map<Object, Object> dst, @Nonnull MapMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }

            @Override
            public boolean copyProperty(@Nonnull String srcPropertyName, @Nonnull PropertyMetaMeta srcProperty, @Nonnull Object src, @Nonnull ObjectMeta srcSchema, @Nonnull Object dst, @Nonnull ObjectMeta dstSchema, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
                return true;
            }
        };
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClsA {
        private String first;
        private String second;
        private String third;

        public void setForth(String forth) {}

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
    @Target({ElementType.FIELD})
    public @interface Format {
        String value();
    }
}