package tests.core.object.schema;

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
import space.sunqian.fs.object.meta.MapType;
import space.sunqian.fs.object.meta.PropertyMetaMeta;
import space.sunqian.fs.object.meta.PropertyMetaBase;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaManager;
import space.sunqian.fs.object.meta.handlers.CommonMetaHandler;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
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

public class SchemaTest implements TestPrint {

    private TestData<CharSequence, String> testDataInstance;
    private ObjectMeta testDataSchema;
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
        testDataSchema = ObjectMeta.parse(type);
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
    public void testObjectSchemaBasicProperties() throws Exception {
        assertSame(testDataSchema.parser(), ObjectMetaManager.defaultParser());
        assertEquals(testDataSchema.type(), new TypeRef<TestData<CharSequence, String>>() {}.type());
        assertEquals(TestData.class, testDataSchema.rawType());
        assertTrue(testDataSchema.isObjectMeta());
        assertFalse(testDataSchema.isMapMeta());
        assertSame(testDataSchema.asObjectMeta(), testDataSchema);
        assertThrows(ClassCastException.class, testDataSchema::asMapMeta);
        assertEquals(
            testDataSchema.properties().keySet(),
            SetKit.set("str", "i", "strArray", "t", "UU", "class", "b", "bb", "BB")
        );
        printFor("ObjectSchema toString", testDataSchema);
        assertEquals(
            testDataSchema.toString(),
            testDataSchema.type().getTypeName() + "{" +
                testDataSchema.properties().values().stream()
                    .map(PropertyMetaMeta::toString)
                    .collect(Collectors.joining(", "))
                + "}"
        );
    }

    @Test
    public void testObjectSchemaInstanceInitialState() {
        assertNull(testDataInstance.str);
        assertEquals(0, testDataInstance.i);
        assertNull(testDataInstance.strArray);
        assertNull(testDataInstance.t);
        assertNull(testDataInstance.u);
    }

    @Test
    public void testObjectSchemaPropertyStr() throws Exception {
        PropertyMetaMeta str = testDataSchema.getProperty("str");
        verifyPropertyBasic(str, testDataSchema, "str", String.class, String.class);
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
    public void testObjectSchemaPropertyI() throws Exception {
        PropertyMetaMeta i = testDataSchema.getProperty("i");
        verifyPropertyBasic(i, testDataSchema, "i", int.class, int.class);
        assertEquals(i.field(), iField);
        assertEquals(i.getterMethod(), iGetter);
        assertNull(i.setterMethod());
        assertTrue(i.isReadable());
        assertEquals(0, i.getValue(testDataInstance));
        assertFalse(i.isWritable());
        assertThrows(DataMetaException.class, () -> i.setValue(testDataInstance, 1));
    }

    @Test
    public void testObjectSchemaPropertyStrArray() throws Exception {
        PropertyMetaMeta strArray = testDataSchema.getProperty("strArray");
        verifyPropertyBasic(strArray, testDataSchema, "strArray", String[].class, String[].class);
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
    public void testObjectSchemaPropertyT() throws Exception {
        PropertyMetaMeta t = testDataSchema.getProperty("t");
        verifyPropertyBasic(t, testDataSchema, "t", CharSequence.class, CharSequence.class);
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
    public void testObjectSchemaPropertyUU() throws Exception {
        PropertyMetaMeta UU = testDataSchema.getProperty("UU");
        verifyPropertyBasic(UU, testDataSchema, "UU", String.class, String.class);
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
    public void testObjectSchemaPropertyClass() throws Exception {
        PropertyMetaMeta classProp = testDataSchema.getProperty("class");
        assertSame(classProp.owner(), testDataSchema);
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
    public void testObjectSchemaPropertyB() throws Exception {
        PropertyMetaMeta b = testDataSchema.getProperty("b");
        verifyBooleanProperty(b, testDataSchema, "b", bField, bGetter, testDataInstance);
    }

    @Test
    public void testObjectSchemaPropertyBb() throws Exception {
        PropertyMetaMeta bb = testDataSchema.getProperty("bb");
        verifyBooleanProperty(bb, testDataSchema, "bb", bbField, bbGetter, testDataInstance);
    }

    @Test
    public void testObjectSchemaPropertyBB() throws Exception {
        PropertyMetaMeta BB = testDataSchema.getProperty("BB");
        verifyBooleanProperty(BB, testDataSchema, "BB", BBField, BBGetter, testDataInstance);
    }

    @Test
    public void testObjectSchemaErrorType() {
        assertThrows(DataMetaException.class, () -> ObjectMeta.parse(TestData.class.getTypeParameters()[0]));
    }

    @Test
    public void testObjectSchemaRawType() {
        ObjectMeta raw = ObjectMeta.parse(TestData.class);
        assertEquals(raw.getProperty("t").type(), TestData.class.getTypeParameters()[0]);
        assertEquals(raw.getProperty("UU").type(), TestData.class.getTypeParameters()[1]);
    }

    @Test
    public void testObjectSchemaPublicField() throws Exception {
        ObjectMeta publicFieldSchema = ObjectMeta.parse(ForPublicFiled.class);
        assertEquals(
            ForPublicFiled.class.getField("publicField"),
            publicFieldSchema.getProperty("publicField").field()
        );
    }

    private void verifyPropertyBasic(PropertyMetaMeta property, ObjectMeta owner, String name, Type expectedType, Class<?> expectedRawType) {
        assertSame(property.owner(), owner);
        assertEquals(name, property.name());
        assertEquals(expectedType, property.type());
        assertEquals(expectedRawType, property.rawType());
    }

    private void verifyBooleanProperty(PropertyMetaMeta property, ObjectMeta owner, String name, Field field, Method getter, TestData<CharSequence, String> instance) throws Exception {
        verifyPropertyBasic(property, owner, name, boolean.class, boolean.class);
        assertEquals(property.field(), field);
        assertEquals(property.getterMethod(), getter);
        assertNull(property.setterMethod());
        assertTrue(property.isReadable());
        assertEquals(property.getValue(instance), getter.invoke(instance));
        assertFalse(property.isWritable());
    }

    @Test
    public void testObjectParser() throws Exception {
        assertSame(ObjectMetaManager.defaultParser(), ObjectMetaManager.defaultParser());
        assertSame(ObjectMetaManager.defaultCachedParser(), ObjectMetaManager.defaultCachedParser());
        testObjectParserWithHandler(ObjectMetaManager.defaultParser());
        testObjectParserWithHandler(ObjectMetaManager.defaultCachedParser());
    }

    private void testObjectParserWithHandler(ObjectMetaManager parser) throws Exception {
        // Test with pre handler
        ObjectMetaManager preParser = parser.withFirstHandler(new PreHandler());
        ObjectMeta preSchema = preParser.parse(Object.class);
        assertEquals(Object.class, preSchema.type());
        assertEquals(0, preSchema.properties().size());

        // Test with last handler
        ObjectMetaManager lastParser = ObjectMetaManager.newParser(
            parser.asHandler(), new LastHandler());
        ObjectMeta lastSchema = lastParser.parse(Object.class);
        assertEquals(Object.class, lastSchema.type());
        assertEquals(lastSchema.properties().keySet(), SetKit.set("class", "test"));

        // Test with pre handler as handler
        ObjectMetaManager asPreParser = ObjectMetaManager.newParser(preParser.asHandler());
        ObjectMeta asPreSchema = asPreParser.parse(Object.class);
        assertEquals(Object.class, asPreSchema.type());
        assertEquals(0, asPreSchema.properties().size());

        // Test with last handler as handler
        ObjectMetaManager asLastParser = ObjectMetaManager.newParser(lastParser.asHandler());
        ObjectMeta asLastSchema = asLastParser.parse(Object.class);
        assertEquals(Object.class, asLastSchema.type());
        assertEquals(asLastSchema.properties().keySet(), SetKit.set("class", "test"));
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
        // schema equal
        ObjectMeta a1 = ObjectMeta.parse(A.class);
        ObjectMeta a2 = ObjectMeta.parse(A.class);
        ObjectMeta b1 = ObjectMeta.parse(B.class);
        ObjectMetaManager parser2 = ObjectMetaManager.newParser(new CommonMetaHandler());
        ObjectMeta a3 = parser2.parse(A.class);
        assertEquals(a1, a1);
        assertFalse(a1.equals(""));
        assertNotSame(a1, a2);
        assertEquals(a1, a2);
        assertFalse(a1.equals(b1));
        assertFalse(a1.equals(a3));
        // properties equal
        PropertyMetaMeta ap1 = a1.getProperty("class");
        PropertyMetaMeta appp = a1.getProperty("pp");
        PropertyMetaMeta ap2 = a2.getProperty("class");
        PropertyMetaMeta bp1 = b1.getProperty("class");
        PropertyMetaMeta ap3 = a3.getProperty("class");
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
            result = 31 * result + a1.parser().hashCode();
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
    public void testMapSchema() throws Exception {
        MapMeta schema = MapMeta.of(HelloMap.class);
        verifyMapSchemaBasic(schema, HelloMap.class, String.class, Long.class);
        printFor("MapSchema toString", schema);
        assertEquals(
            schema.toString(),
            schema.type().getTypeName()
        );
        assertThrows(DataMetaException.class, () -> MapMeta.of(String.class));

        // Test with MapType
        MapMeta schemaWithTypes = MapMeta.of(MapType.of(Map.class, Object.class, Long.class));
        verifyMapSchemaBasic(schemaWithTypes, Map.class, Object.class, Long.class);

        // Test schema equality
        testMapSchemaEquality();
    }

    @Test
    public void testMapParser() throws Exception {
        assertSame(MapMetaManager.defaultManager(), MapMetaManager.defaultManager());
        assertSame(MapMetaManager.defaultCachedParser(), MapMetaManager.defaultCachedParser());
        testMapParserWithParser(MapMetaManager.defaultManager());
        testMapParserWithParser(MapMetaManager.defaultCachedParser());
    }

    private void testMapParserWithParser(MapMetaManager parser) throws Exception {
        MapMeta schema = parser.introspect(HelloMap.class);
        verifyMapSchemaWithParser(schema, HelloMap.class, String.class, Long.class, parser);
        printFor("MapSchema toString", schema);
        assertEquals(
            schema.toString(),
            schema.type().getTypeName()
        );
        assertThrows(DataMetaException.class, () -> parser.introspect(String.class));

        // Test with MapType
        MapMeta schemaWithTypes = parser.introspect(MapType.of(Map.class, Object.class, Long.class));
        verifyMapSchemaWithParser(schemaWithTypes, Map.class, Object.class, Long.class, parser);

        // Test schema equality
        testMapSchemaEqualityWithParser(parser);
    }

    private void verifyMapSchemaBasic(MapMeta schema, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType) {
        assertSame(schema.manager(), MapMetaManager.defaultManager());
        assertEquals(expectedType, schema.type());
        assertEquals(expectedType, schema.rawType());
        assertTrue(schema.isMapMeta());
        assertFalse(schema.isObjectMeta());
        assertSame(schema.asMapMeta(), schema);
        assertThrows(ClassCastException.class, schema::asObjectMeta);
        assertEquals(expectedKeyType, schema.keyType());
        assertEquals(expectedValueType, schema.valueType());
    }

    private void verifyMapSchemaWithParser(MapMeta schema, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType, MapMetaManager expectedParser) {
        // For cached parsers, the schema's parser returns the underlying parser, not the cached one
        // So we don't assert same here, just verify other properties
        assertEquals(expectedType, schema.type());
        assertEquals(expectedType, schema.rawType());
        assertTrue(schema.isMapMeta());
        assertFalse(schema.isObjectMeta());
        assertSame(schema.asMapMeta(), schema);
        assertThrows(ClassCastException.class, schema::asObjectMeta);
        assertEquals(expectedKeyType, schema.keyType());
        assertEquals(expectedValueType, schema.valueType());
    }

    private void testMapSchemaEquality() throws Exception {
        MapMeta m1 = MapMeta.of(Map.class);
        MapMeta m2 = MapMeta.of(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapMeta m3 = MapMeta.of(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        // Test with custom parser
        MapMetaManager parser2 = new CustomMapMetaManager();
        MapMeta m4 = parser2.introspect(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        // Test hash code
        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.manager().hashCode();
        assertEquals(m1.hashCode(), result);
    }

    private void testMapSchemaEqualityWithParser(MapMetaManager parser) throws Exception {
        MapMeta m1 = parser.introspect(Map.class);
        MapMeta m2 = parser.introspect(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapMeta m3 = parser.introspect(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        // Test with custom parser
        MapMetaManager parser2 = new CustomMapMetaManager();
        MapMeta m4 = parser2.introspect(Map.class);
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
    }

    @Test
    public void testCachedParser() {
        testMapSchemaCachedParser();
        testObjectSchemaCachedParser();
    }

    private void testMapSchemaCachedParser() {
        MapMetaManager mapParser = MapMetaManager.newCachedParser(SimpleCache.ofStrong(), MapMetaManager.defaultManager());
        // Test caching for Map.class
        assertSame(mapParser.introspect(Map.class), mapParser.introspect(Map.class));
        assertNotSame(MapMeta.of(Map.class), MapMeta.of(Map.class));
        // Test caching for MapType
        MapType mapType = MapType.of(Map.class, String.class, Long.class);
        assertSame(mapParser.introspect(mapType), mapParser.introspect(mapType));
        assertNotSame(MapMeta.of(mapType), MapMeta.of(mapType));
    }

    private void testObjectSchemaCachedParser() {
        ObjectMetaManager objectParser = ObjectMetaManager.newCachedParser(SimpleCache.ofStrong(), ObjectMetaManager.defaultParser());
        // Test caching for A.class
        assertSame(objectParser.parse(A.class), objectParser.parse(A.class));
        assertNotSame(ObjectMeta.parse(A.class), ObjectMeta.parse(A.class));
        // Test handler consistency
        assertSame(ObjectMetaManager.defaultParser().handlers(), objectParser.handlers());
        assertSame(ObjectMetaManager.defaultParser().asHandler(), objectParser.asHandler());
    }

    @Test
    public void testMapType() {
        MapType mapType = MapType.of(Map.class, String.class, Long.class);
        verifyMapTypeBasic(mapType, Map.class, String.class, Long.class);
        verifyMapTypeEquality(mapType);
        verifyMapTypeException();
    }

    private void verifyMapTypeBasic(MapType mapType, Class<?> expectedMapType, Class<?> expectedKeyType, Class<?> expectedValueType) {
        assertEquals(expectedMapType, mapType.mapType());
        assertEquals(expectedKeyType, mapType.keyType());
        assertEquals(expectedValueType, mapType.valueType());
        assertEquals(
            expectedMapType.getName() + "<" + expectedKeyType.getName() + ", " + expectedValueType.getName() + ">",
            mapType.toString()
        );
    }

    private void verifyMapTypeEquality(MapType mapType) {
        assertEquals(mapType, mapType);
        assertEquals(mapType, MapType.of(Map.class, String.class, Long.class));
        assertNotEquals(mapType, MapType.of(Map.class, String.class, Integer.class));
        assertNotEquals(mapType, MapType.of(Map.class, Integer.class, Long.class));
        assertNotEquals(mapType, MapType.of(HashMap.class, String.class, Long.class));
        assertNotEquals(mapType, String.class);
        assertEquals(
            mapType.hashCode(),
            MapType.of(Map.class, String.class, Long.class).hashCode()
        );
    }

    private void verifyMapTypeException() {
        class X<T> {}
        assertThrows(IllegalArgumentException.class, () ->
            MapType.of(X.class.getTypeParameters()[0], String.class, Long.class));
    }

    @Test
    public void testAnnotation() {
        ObjectMeta schema = ObjectMeta.parse(ForAnnotation.class);
        testAnnotationProp1(schema);
        testAnnotationProp2(schema);
        testAnnotationProp3(schema);
        testAnnotationProp4(schema);
        testAnnotationProp5(schema);
        testAnnotationProp6(schema);
    }

    private void testAnnotationProp1(ObjectMeta schema) {
        PropertyMetaMeta prop1 = schema.getProperty("prop1");
        assertNotNull(prop1);
        Nonnull a1 = prop1.getAnnotation(Nonnull.class);
        assertNotNull(a1);
        assertEquals(Nonnull.class, a1.annotationType());
        assertNull(prop1.getAnnotation(Nullable.class));
        assertEquals(ListKit.list(a1), prop1.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop1.getterAnnotations());
        assertEquals(Collections.emptyList(), prop1.setterAnnotations());
    }

    private void testAnnotationProp2(ObjectMeta schema) {
        PropertyMetaMeta prop2 = schema.getProperty("prop2");
        assertNotNull(prop2);
        Nonnull a2 = prop2.getAnnotation(Nonnull.class);
        assertNotNull(a2);
        assertEquals(Nonnull.class, a2.annotationType());
        assertNull(prop2.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop2.fieldAnnotations());
        assertEquals(ListKit.list(a2), prop2.getterAnnotations());
        assertEquals(Collections.emptyList(), prop2.setterAnnotations());
    }

    private void testAnnotationProp3(ObjectMeta schema) {
        PropertyMetaMeta prop3 = schema.getProperty("prop3");
        assertNotNull(prop3);
        Nonnull a3 = prop3.getAnnotation(Nonnull.class);
        assertNotNull(a3);
        assertEquals(Nonnull.class, a3.annotationType());
        assertNull(prop3.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop3.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop3.getterAnnotations());
        assertEquals(ListKit.list(a3), prop3.setterAnnotations());
    }

    private void testAnnotationProp4(ObjectMeta schema) {
        PropertyMetaMeta prop4 = schema.getProperty("prop4");
        assertNotNull(prop4);
        assertNull(prop4.getAnnotation(Nonnull.class));
        assertNull(prop4.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop4.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop4.getterAnnotations());
        assertEquals(Collections.emptyList(), prop4.setterAnnotations());
    }

    private void testAnnotationProp5(ObjectMeta schema) {
        PropertyMetaMeta prop5 = schema.getProperty("prop5");
        assertNotNull(prop5);
        assertNull(prop5.getAnnotation(Nonnull.class));
        assertNull(prop5.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop5.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop5.getterAnnotations());
        assertEquals(Collections.emptyList(), prop5.setterAnnotations());
    }

    private void testAnnotationProp6(ObjectMeta schema) {
        PropertyMetaMeta prop6 = schema.getProperty("prop6");
        assertNotNull(prop6);
        assertNull(prop6.getAnnotation(Nonnull.class));
        assertNull(prop6.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop6.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop6.getterAnnotations());
        assertEquals(Collections.emptyList(), prop6.setterAnnotations());
    }

    @Test
    public void testException() {
        testDataSchemaExceptionConstructors();
    }

    private void testDataSchemaExceptionConstructors() {
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
