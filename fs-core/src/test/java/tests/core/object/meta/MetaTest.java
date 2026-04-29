package tests.core.object.meta;

import internal.utils.TestPrint;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.meta.DataMetaException;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.MapMetaManager;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaManager;
import space.sunqian.fs.object.meta.PropertyMeta;
import space.sunqian.fs.object.meta.PropertyMetaBase;
import space.sunqian.fs.object.meta.handlers.CommonMetaHandler;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetaTest implements TestPrint {

    private TestData<CharSequence, String> testDataInstance;
    private ObjectMeta testDataMeta;
    private Field strField;
    private Field iField;
    private Field strArrayField;
    private Field tField;
    private Field bField;
    private Field bbField;
    private Field BBField;
    private Method strGetter;
    private Method strSetter;
    private Method iGetter;
    private Method strArraySetter;
    private Method tGetter;
    private Method tSetter;
    private Method uGetter;
    private Method uSetter;
    private Method classGetter;
    private Method bGetter;
    private Method bbGetter;
    private Method BBGetter;

    @BeforeEach
    public void setUp() throws Exception {
        Type type = new TypeRef<TestData<CharSequence, String>>() {}.type();
        testDataMeta = ObjectMeta.of(type);
        testDataInstance = new TestData<>();

        // fields
        strField = TestData.class.getDeclaredField("str");
        iField = TestData.class.getDeclaredField("i");
        strArrayField = TestData.class.getDeclaredField("strArray");
        tField = TestData.class.getDeclaredField("t");
        bField = TestData.class.getDeclaredField("b");
        bbField = TestData.class.getDeclaredField("bb");
        BBField = TestData.class.getDeclaredField("BB");

        // methods
        strGetter = TestData.class.getDeclaredMethod("getStr");
        strSetter = TestData.class.getDeclaredMethod("setStr", String.class);
        iGetter = TestData.class.getDeclaredMethod("getI");
        strArraySetter = TestData.class.getDeclaredMethod("setStrArray", String[].class);
        tGetter = TestData.class.getDeclaredMethod("getT");
        tSetter = TestData.class.getDeclaredMethod("setT", Object.class);
        uGetter = TestData.class.getDeclaredMethod("getUU");
        uSetter = TestData.class.getDeclaredMethod("setUU", Object.class);
        classGetter = TestData.class.getMethod("getClass");
        bGetter = TestData.class.getDeclaredMethod("isB");
        bbGetter = TestData.class.getDeclaredMethod("isBb");
        BBGetter = TestData.class.getDeclaredMethod("isBB");
    }

    @Test
    public void testObjectMetaBasicProperties() throws Exception {
        assertSame(testDataMeta.manager(), ObjectMetaManager.defaultManager());
        assertEquals(testDataMeta.type(), new TypeRef<TestData<CharSequence, String>>() {}.type());
        assertEquals(TestData.class, testDataMeta.rawType());
        assertTrue(testDataMeta.isObjectMeta());
        assertFalse(testDataMeta.isMapMeta());
        assertSame(testDataMeta.asObjectMeta(), testDataMeta);
        assertThrows(ClassCastException.class, testDataMeta::asMapMeta);
        assertEquals(
            testDataMeta.properties().keySet(),
            SetKit.set("str", "i", "strArray", "t", "UU", "class", "b", "bb", "BB")
        );
        printFor("ObjectMeta toString", testDataMeta);
        assertEquals(
            testDataMeta.toString(),
            testDataMeta.type().getTypeName() + "{" +
                testDataMeta.properties().values().stream()
                    .map(PropertyMeta::toString)
                    .collect(Collectors.joining(", "))
                + "}"
        );
    }

    @Test
    public void testObjectMetaInstanceInitialState() {
        assertNull(testDataInstance.str);
        assertEquals(0, testDataInstance.i);
        assertNull(testDataInstance.strArray);
        assertNull(testDataInstance.t);
        assertNull(testDataInstance.u);
    }

    @Test
    public void testObjectMetaPropertyStr() throws Exception {
        PropertyMeta str = testDataMeta.getProperty("str");
        verifyPropertyBasic(str, testDataMeta, "str", String.class, String.class);
        assertEquals(str.field(), strField);
        assertEquals(str.getterMethod(), strGetter);
        assertEquals(str.setterMethod(), strSetter);
        assertTrue(str.isReadable());
        assertNull(str.getValue(testDataInstance));
        assertTrue(str.isWritable());
        str.setValue(testDataInstance, "hello");
        assertEquals("hello", testDataInstance.str);
    }

    @Test
    public void testObjectMetaPropertyI() throws Exception {
        PropertyMeta i = testDataMeta.getProperty("i");
        verifyPropertyBasic(i, testDataMeta, "i", int.class, int.class);
        assertEquals(i.field(), iField);
        assertEquals(i.getterMethod(), iGetter);
        assertNull(i.setterMethod());
        assertTrue(i.isReadable());
        assertEquals(0, i.getValue(testDataInstance));
        assertFalse(i.isWritable());
        assertThrows(DataMetaException.class, () -> i.setValue(testDataInstance, 1));
    }

    @Test
    public void testObjectMetaPropertyStrArray() throws Exception {
        PropertyMeta strArray = testDataMeta.getProperty("strArray");
        verifyPropertyBasic(strArray, testDataMeta, "strArray", String[].class, String[].class);
        assertEquals(strArray.field(), strArrayField);
        assertNull(strArray.getterMethod());
        assertEquals(strArray.setterMethod(), strArraySetter);
        assertFalse(strArray.isReadable());
        assertThrows(DataMetaException.class, () -> strArray.getValue(testDataInstance));
        assertTrue(strArray.isWritable());
        strArray.setValue(testDataInstance, new String[]{"hello"});
        assertArrayEquals(new String[]{"hello"}, testDataInstance.strArray);
    }

    @Test
    public void testObjectMetaPropertyT() throws Exception {
        PropertyMeta t = testDataMeta.getProperty("t");
        verifyPropertyBasic(t, testDataMeta, "t", CharSequence.class, CharSequence.class);
        assertEquals(t.field(), tField);
        assertEquals(t.getterMethod(), tGetter);
        assertEquals(t.setterMethod(), tSetter);
        assertTrue(t.isReadable());
        assertNull(t.getValue(testDataInstance));
        assertTrue(t.isWritable());
        t.setValue(testDataInstance, "hello");
        assertEquals("hello", testDataInstance.t);
    }

    @Test
    public void testObjectMetaPropertyUU() throws Exception {
        PropertyMeta UU = testDataMeta.getProperty("UU");
        verifyPropertyBasic(UU, testDataMeta, "UU", String.class, String.class);
        assertNull(UU.field());
        assertEquals(UU.getterMethod(), uGetter);
        assertEquals(UU.setterMethod(), uSetter);
        assertTrue(UU.isReadable());
        assertNull(UU.getValue(testDataInstance));
        assertTrue(UU.isWritable());
        UU.setValue(testDataInstance, "hello");
        assertEquals("hello", testDataInstance.u);
    }

    @Test
    public void testObjectMetaPropertyClass() throws Exception {
        PropertyMeta classProp = testDataMeta.getProperty("class");
        assertSame(classProp.owner(), testDataMeta);
        assertEquals("class", classProp.name());
        assertEquals(classProp.type(), new TypeRef<Class<?>>() {}.type());
        assertEquals(Class.class, classProp.rawType());
        assertNull(classProp.field());
        assertEquals(classProp.getterMethod(), classGetter);
        assertNull(classProp.setterMethod());
        assertTrue(classProp.isReadable());
        assertEquals(TestData.class, classProp.getValue(testDataInstance));
        assertFalse(classProp.isWritable());
    }

    @Test
    public void testObjectMetaPropertyB() throws Exception {
        PropertyMeta b = testDataMeta.getProperty("b");
        verifyBooleanProperty(b, testDataMeta, "b", bField, bGetter, testDataInstance);
    }

    @Test
    public void testObjectMetaPropertyBb() throws Exception {
        PropertyMeta bb = testDataMeta.getProperty("bb");
        verifyBooleanProperty(bb, testDataMeta, "bb", bbField, bbGetter, testDataInstance);
    }

    @Test
    public void testObjectMetaPropertyBB() throws Exception {
        PropertyMeta BB = testDataMeta.getProperty("BB");
        verifyBooleanProperty(BB, testDataMeta, "BB", BBField, BBGetter, testDataInstance);
    }

    @Test
    public void testObjectMetaErrorType() {
        assertThrows(DataMetaException.class, () -> ObjectMeta.of(TestData.class.getTypeParameters()[0]));
    }

    @Test
    public void testObjectMetaRawType() {
        ObjectMeta raw = ObjectMeta.of(TestData.class);
        assertEquals(raw.getProperty("t").type(), TestData.class.getTypeParameters()[0]);
        assertEquals(raw.getProperty("UU").type(), TestData.class.getTypeParameters()[1]);
    }

    @Test
    public void testObjectMetaPublicField() throws Exception {
        ObjectMeta publicFieldMeta = ObjectMeta.of(ForPublicFiled.class);
        assertEquals(
            ForPublicFiled.class.getField("publicField"),
            publicFieldMeta.getProperty("publicField").field()
        );
    }

    private void verifyPropertyBasic(PropertyMeta property, ObjectMeta owner, String name, Type expectedType, Class<?> expectedRawType) {
        assertSame(property.owner(), owner);
        assertEquals(name, property.name());
        assertEquals(expectedType, property.type());
        assertEquals(expectedRawType, property.rawType());
    }

    private void verifyBooleanProperty(PropertyMeta property, ObjectMeta owner, String name, Field field, Method getter, TestData<CharSequence, String> instance) throws Exception {
        verifyPropertyBasic(property, owner, name, boolean.class, boolean.class);
        assertEquals(property.field(), field);
        assertEquals(property.getterMethod(), getter);
        assertNull(property.setterMethod());
        assertTrue(property.isReadable());
        assertEquals(property.getValue(instance), getter.invoke(instance));
        assertFalse(property.isWritable());
    }

    @Test
    public void testObjectManager() throws Exception {
        assertSame(ObjectMetaManager.defaultManager(), ObjectMetaManager.defaultManager());
        assertSame(ObjectMetaManager.defaultCachedManager(), ObjectMetaManager.defaultCachedManager());
        testObjectManagerWithHandler(ObjectMetaManager.defaultManager());
        testObjectManagerWithHandler(ObjectMetaManager.defaultCachedManager());
    }

    private void testObjectManagerWithHandler(ObjectMetaManager manager) throws Exception {
        // Test with pre handler
        ObjectMetaManager preManager = manager.withFirstHandler(new PreHandler());
        ObjectMeta preMeta = preManager.parse(Object.class);
        assertEquals(Object.class, preMeta.type());
        assertEquals(0, preMeta.properties().size());

        // Test with last handler
        ObjectMetaManager lastManager = ObjectMetaManager.newManager(
            manager.asHandler(), new LastHandler());
        ObjectMeta lastMeta = lastManager.parse(Object.class);
        assertEquals(Object.class, lastMeta.type());
        assertEquals(lastMeta.properties().keySet(), SetKit.set("class", "test"));

        // Test with pre handler as handler
        ObjectMetaManager asPreManager = ObjectMetaManager.newManager(preManager.asHandler());
        ObjectMeta asPreMeta = asPreManager.parse(Object.class);
        assertEquals(Object.class, asPreMeta.type());
        assertEquals(0, asPreMeta.properties().size());

        // Test with last handler as handler
        ObjectMetaManager asLastManager = ObjectMetaManager.newManager(lastManager.asHandler());
        ObjectMeta asLastMeta = asLastManager.parse(Object.class);
        assertEquals(Object.class, asLastMeta.type());
        assertEquals(asLastMeta.properties().keySet(), SetKit.set("class", "test"));
    }

    private static class PreHandler implements ObjectMetaManager.Handler {
        @Override
        public boolean parse(@Nonnull ObjectMetaManager.Context context) throws Exception {
            if (context.parsedType().equals(Object.class)) {
                return false;
            }
            return true;
        }
    }

    private static class LastHandler implements ObjectMetaManager.Handler {
        @Override
        public boolean parse(@Nonnull ObjectMetaManager.Context context) throws Exception {
            if (context.parsedType().equals(Object.class)) {
                context.propertyBaseMap().put("test", new PropertyMetaBase() {
                    @Override
                    public @Nonnull String name() {
                        return "test";
                    }

                    @Override
                    public @Nonnull Type type() {
                        return null;
                    }

                    @Override
                    public @Nullable Method getterMethod() {
                        return null;
                    }

                    @Override
                    public @Nullable Method setterMethod() {
                        return null;
                    }

                    @Override
                    public @Nullable Field field() {
                        return null;
                    }

                    @Override
                    public @Nullable Invocable getter() {
                        return null;
                    }

                    @Override
                    public @Nullable Invocable setter() {
                        return null;
                    }
                });
            }
            return true;
        }
    }

    @Test
    public void testEqualHashCode() throws Exception {
        // meta equal
        ObjectMeta a1 = ObjectMeta.of(A.class);
        ObjectMeta a2 = ObjectMeta.of(A.class);
        ObjectMeta b1 = ObjectMeta.of(B.class);
        ObjectMetaManager manager2 = ObjectMetaManager.newManager(new CommonMetaHandler());
        ObjectMeta a3 = manager2.parse(A.class);
        assertEquals(a1, a1);
        assertFalse(a1.equals(""));
        assertNotSame(a1, a2);
        assertEquals(a1, a2);
        assertFalse(a1.equals(b1));
        assertFalse(a1.equals(a3));
        // properties equal
        PropertyMeta ap1 = a1.getProperty("class");
        PropertyMeta appp = a1.getProperty("pp");
        PropertyMeta ap2 = a2.getProperty("class");
        PropertyMeta bp1 = b1.getProperty("class");
        PropertyMeta ap3 = a3.getProperty("class");
        assertTrue(ap1.equals(ap1));
        assertEquals(ap1, ap2);
        assertFalse(ap1.equals(appp));
        assertFalse(ap1.equals(""));
        assertFalse(ap1.equals(bp1));
        assertFalse(ap1.equals(ap3));
        // hash code
        {
            int result = 1;
            result = 31 * result + a1.type().hashCode();
            result = 31 * result + a1.manager().hashCode();
            assertEquals(a1.hashCode(), result);
        }
        {
            int result = 1;
            result = 31 * result + ap1.name().hashCode();
            result = 31 * result + ap1.owner().hashCode();
            assertEquals(ap1.hashCode(), result);
        }
    }

    @Test
    public void testMapMeta() throws Exception {
        MapMeta meta = MapMeta.of(HelloMap.class);
        verifyMapMetaBasic(meta, HelloMap.class, String.class, Long.class);
        printFor("MapMeta toString", meta);
        assertEquals(
            meta.toString(),
            meta.type().getTypeName()
        );
        assertThrows(DataMetaException.class, () -> MapMeta.of(String.class));

        // Test meta equality
        testMapMetaEquality();
    }

    @Test
    public void testMapManager() throws Exception {
        assertSame(MapMetaManager.defaultManager(), MapMetaManager.defaultManager());
        testMapManagerWithManager(MapMetaManager.defaultManager());
    }

    private void testMapManagerWithManager(MapMetaManager manager) throws Exception {
        MapMeta meta = manager.introspect(HelloMap.class);
        verifyMapMetaWithManager(meta, HelloMap.class, String.class, Long.class, manager);
        printFor("MapMeta toString", meta);
        assertEquals(
            meta.toString(),
            meta.type().getTypeName()
        );
        assertThrows(DataMetaException.class, () -> manager.introspect(String.class));

        // Test meta equality
        testMapMetaEqualityWithManager(manager);
    }

    private void verifyMapMetaBasic(MapMeta meta, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType) {
        assertSame(meta.manager(), MapMetaManager.defaultManager());
        assertEquals(expectedType, meta.type());
        assertEquals(expectedType, meta.rawType());
        assertTrue(meta.isMapMeta());
        assertFalse(meta.isObjectMeta());
        assertSame(meta.asMapMeta(), meta);
        assertThrows(ClassCastException.class, meta::asObjectMeta);
        assertEquals(expectedKeyType, meta.keyType());
        assertEquals(expectedValueType, meta.valueType());
    }

    private void verifyMapMetaWithManager(MapMeta meta, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType, MapMetaManager expectedManager) {
        // For cached managers, the meta's manager returns the underlying manager, not the cached one
        // So we don't assert same here, just verify other properties
        assertEquals(expectedType, meta.type());
        assertEquals(expectedType, meta.rawType());
        assertTrue(meta.isMapMeta());
        assertFalse(meta.isObjectMeta());
        assertSame(meta.asMapMeta(), meta);
        assertThrows(ClassCastException.class, meta::asObjectMeta);
        assertEquals(expectedKeyType, meta.keyType());
        assertEquals(expectedValueType, meta.valueType());
    }

    private void testMapMetaEquality() throws Exception {
        MapMeta m1 = MapMeta.of(Map.class);
        MapMeta m2 = MapMeta.of(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapMeta m3 = MapMeta.of(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        // Test with custom manager
        MapMetaManager manager2 = new CustomMapMetaManager();
        MapMeta m4 = manager2.introspect(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        // Test hash code
        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.manager().hashCode();
        assertEquals(m1.hashCode(), result);
    }

    private void testMapMetaEqualityWithManager(MapMetaManager manager) throws Exception {
        MapMeta m1 = manager.introspect(Map.class);
        MapMeta m2 = manager.introspect(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapMeta m3 = manager.introspect(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        // Test with custom manager
        MapMetaManager manager2 = new CustomMapMetaManager();
        MapMeta m4 = manager2.introspect(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        // Test hash code
        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.manager().hashCode();
        assertEquals(m1.hashCode(), result);
    }

    private static class CustomMapMetaManager implements MapMetaManager {
        @Override
        public @Nonnull MapMeta introspect(@Nonnull Type type) throws DataMetaException {
            return new MapMeta() {
                @Override
                public @Nonnull MapMetaManager manager() {
                    return CustomMapMetaManager.this;
                }

                @Override
                public @Nonnull Type keyType() {
                    return Object.class;
                }

                @Override
                public @Nonnull Type valueType() {
                    return Object.class;
                }

                @Override
                public @Nonnull Type type() {
                    return type;
                }
            };
        }

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return Collections.emptyList();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return null;
        }
    }

    @Test
    public void testCachedManager() {
        testMapMetaCachedManager();
        testObjectMetaCachedManager();
    }

    private void testMapMetaCachedManager() {
        MapMetaManager mapManager = MapMetaManager.newManager(ListKit.list(MapMetaManager.defaultManager().asHandler()), SimpleCache.ofStrong());
        // Test caching for Map.class
        assertSame(mapManager.introspect(Map.class), mapManager.introspect(Map.class));
        // assertNotSame(MapMeta.of(Map.class), MapMeta.of(Map.class));
    }

    private void testObjectMetaCachedManager() {
        ObjectMetaManager objectManager = ObjectMetaManager.newManager(SimpleCache.ofStrong(), ObjectMetaManager.defaultManager());
        // Test caching for A.class
        assertSame(objectManager.parse(A.class), objectManager.parse(A.class));
        assertNotSame(ObjectMeta.of(A.class), ObjectMeta.of(A.class));
        // Test handler consistency
        assertSame(ObjectMetaManager.defaultManager().handlers(), objectManager.handlers());
        assertSame(ObjectMetaManager.defaultManager().asHandler(), objectManager.asHandler());
    }

    @Test
    public void testAnnotation() {
        ObjectMeta meta = ObjectMeta.of(ForAnnotation.class);
        testAnnotationProp1(meta);
        testAnnotationProp2(meta);
        testAnnotationProp3(meta);
        testAnnotationProp4(meta);
        testAnnotationProp5(meta);
        testAnnotationProp6(meta);
    }

    private void testAnnotationProp1(ObjectMeta meta) {
        PropertyMeta prop1 = meta.getProperty("prop1");
        assertNotNull(prop1);
        Nonnull a1 = prop1.getAnnotation(Nonnull.class);
        assertNotNull(a1);
        assertEquals(Nonnull.class, a1.annotationType());
        assertNull(prop1.getAnnotation(Nullable.class));
        assertEquals(ListKit.list(a1), prop1.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop1.getterAnnotations());
        assertEquals(Collections.emptyList(), prop1.setterAnnotations());
    }

    private void testAnnotationProp2(ObjectMeta meta) {
        PropertyMeta prop2 = meta.getProperty("prop2");
        assertNotNull(prop2);
        Nonnull a2 = prop2.getAnnotation(Nonnull.class);
        assertNotNull(a2);
        assertEquals(Nonnull.class, a2.annotationType());
        assertNull(prop2.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop2.fieldAnnotations());
        assertEquals(ListKit.list(a2), prop2.getterAnnotations());
        assertEquals(Collections.emptyList(), prop2.setterAnnotations());
    }

    private void testAnnotationProp3(ObjectMeta meta) {
        PropertyMeta prop3 = meta.getProperty("prop3");
        assertNotNull(prop3);
        Nonnull a3 = prop3.getAnnotation(Nonnull.class);
        assertNotNull(a3);
        assertEquals(Nonnull.class, a3.annotationType());
        assertNull(prop3.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop3.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop3.getterAnnotations());
        assertEquals(ListKit.list(a3), prop3.setterAnnotations());
    }

    private void testAnnotationProp4(ObjectMeta meta) {
        PropertyMeta prop4 = meta.getProperty("prop4");
        assertNotNull(prop4);
        assertNull(prop4.getAnnotation(Nonnull.class));
        assertNull(prop4.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop4.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop4.getterAnnotations());
        assertEquals(Collections.emptyList(), prop4.setterAnnotations());
    }

    private void testAnnotationProp5(ObjectMeta meta) {
        PropertyMeta prop5 = meta.getProperty("prop5");
        assertNotNull(prop5);
        assertNull(prop5.getAnnotation(Nonnull.class));
        assertNull(prop5.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop5.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop5.getterAnnotations());
        assertEquals(Collections.emptyList(), prop5.setterAnnotations());
    }

    private void testAnnotationProp6(ObjectMeta meta) {
        PropertyMeta prop6 = meta.getProperty("prop6");
        assertNotNull(prop6);
        assertNull(prop6.getAnnotation(Nonnull.class));
        assertNull(prop6.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop6.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop6.getterAnnotations());
        assertEquals(Collections.emptyList(), prop6.setterAnnotations());
    }

    @Test
    public void testException() {
        testDataMetaExceptionConstructors();
    }

    private void testDataMetaExceptionConstructors() {
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException();});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException("");});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException("", new RuntimeException());});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException(new RuntimeException());});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException(Object.class);});
        assertThrows(DataMetaException.class, () -> {
            throw new DataMetaException(Object.class, new RuntimeException());
        });
    }

    public static class TestData<T, U extends T> implements TestInter<String> {

        @Getter
        @Setter
        private @Nullable String str;

        private int i;

        private String @Nonnull [] strArray;

        @Getter
        @Setter
        private T t;

        private U u;

        @Getter
        private boolean b;
        private boolean bb;
        private boolean BB;

        @Immutable
        public int getI() {
            return i;
        }

        @Immutable
        public void setStrArray(String @Nonnull [] strArray) {
            this.strArray = strArray;
        }

        public U getUU() {
            return u;
        }

        public void setUU(U uu) {
            this.u = uu;
        }

        @Override
        public String getter() {
            return "";
        }

        @Override
        public void setter(String str) {
        }


        public boolean isBb() {
            return bb;
        }

        public boolean isBB() {
            return BB;
        }

        @Override
        public String xxxxx() {
            return TestInter.super.xxxxx();
        }
    }

    public interface TestInter<X> {

        public String getter();

        void setter(String str);

        default X xxxxx() {
            return null;
        }

        default boolean issor() {
            return true;
        }

        default boolean b() {
            return true;
        }

        default boolean b(boolean b) {
            return b;
        }
    }

    public static class A {
        public String getPp() {
            return "pp";
        }
    }

    public static class B {
    }

    public static class HelloMap extends AbstractMap<String, Long> {
        @Override
        public Set<Entry<String, Long>> entrySet() {
            return null;
        }
    }

    @Data
    public static class ForPublicFiled {
        public String publicField;
    }

    @Data
    @NoArgsConstructor
    public static class ForAnnotation {

        @Nonnull
        private String prop1;
        private String prop2;
        private String prop3;
        private String prop4;

        @Nonnull
        public String getProp2() {
            return prop2;
        }

        @Nonnull
        public void setProp3(String prop3) {
            this.prop3 = prop3;
        }

        public String getProp5() {
            return null;
        }

        public void setProp6(String prop6) {
        }
    }
}
