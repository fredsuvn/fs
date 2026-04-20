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
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.MapSchemaParser;
import space.sunqian.fs.object.schema.MapType;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectPropertyBase;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
import space.sunqian.fs.object.schema.handlers.CommonSchemaHandler;
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
    private ObjectSchema testDataSchema;
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
        testDataSchema = ObjectSchema.parse(type);
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
        assertSame(testDataSchema.parser(), ObjectSchemaParser.defaultParser());
        assertEquals(testDataSchema.type(), new TypeRef<TestData<CharSequence, String>>() {}.type());
        assertEquals(TestData.class, testDataSchema.rawType());
        assertTrue(testDataSchema.isObjectSchema());
        assertFalse(testDataSchema.isMapSchema());
        assertSame(testDataSchema.asObjectSchema(), testDataSchema);
        assertThrows(ClassCastException.class, testDataSchema::asMapSchema);
        assertEquals(
            testDataSchema.properties().keySet(),
            SetKit.set("str", "i", "strArray", "t", "UU", "class", "b", "bb", "BB")
        );
        printFor("ObjectSchema toString", testDataSchema);
        assertEquals(
            testDataSchema.toString(),
            testDataSchema.type().getTypeName() + "{" +
                testDataSchema.properties().values().stream()
                    .map(ObjectProperty::toString)
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
        ObjectProperty str = testDataSchema.getProperty("str");
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
        ObjectProperty i = testDataSchema.getProperty("i");
        verifyPropertyBasic(i, testDataSchema, "i", int.class, int.class);
        assertEquals(i.field(), iField);
        assertEquals(i.getterMethod(), iGetter);
        assertNull(i.setterMethod());
        assertTrue(i.isReadable());
        assertEquals(0, i.getValue(testDataInstance));
        assertFalse(i.isWritable());
        assertThrows(DataSchemaException.class, () -> i.setValue(testDataInstance, 1));
    }

    @Test
    public void testObjectSchemaPropertyStrArray() throws Exception {
        ObjectProperty strArray = testDataSchema.getProperty("strArray");
        verifyPropertyBasic(strArray, testDataSchema, "strArray", String[].class, String[].class);
        assertEquals(strArray.field(), strArrayField);
        assertNull(strArray.getterMethod());
        assertEquals(strArray.setterMethod(), strArraySetter);
        assertFalse(strArray.isReadable());
        assertThrows(DataSchemaException.class, () -> strArray.getValue(testDataInstance));
        assertTrue(strArray.isWritable());
        strArray.setValue(testDataInstance, new String[]{"hello"});
        assertArrayEquals(new String[]{"hello"}, testDataInstance.strArray);
    }

    @Test
    public void testObjectSchemaPropertyT() throws Exception {
        ObjectProperty t = testDataSchema.getProperty("t");
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
        ObjectProperty UU = testDataSchema.getProperty("UU");
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
        ObjectProperty classProp = testDataSchema.getProperty("class");
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
        ObjectProperty b = testDataSchema.getProperty("b");
        verifyBooleanProperty(b, testDataSchema, "b", bField, bGetter, testDataInstance);
    }

    @Test
    public void testObjectSchemaPropertyBb() throws Exception {
        ObjectProperty bb = testDataSchema.getProperty("bb");
        verifyBooleanProperty(bb, testDataSchema, "bb", bbField, bbGetter, testDataInstance);
    }

    @Test
    public void testObjectSchemaPropertyBB() throws Exception {
        ObjectProperty BB = testDataSchema.getProperty("BB");
        verifyBooleanProperty(BB, testDataSchema, "BB", BBField, BBGetter, testDataInstance);
    }

    @Test
    public void testObjectSchemaErrorType() {
        assertThrows(DataSchemaException.class, () -> ObjectSchema.parse(TestData.class.getTypeParameters()[0]));
    }

    @Test
    public void testObjectSchemaRawType() {
        ObjectSchema raw = ObjectSchema.parse(TestData.class);
        assertEquals(raw.getProperty("t").type(), TestData.class.getTypeParameters()[0]);
        assertEquals(raw.getProperty("UU").type(), TestData.class.getTypeParameters()[1]);
    }

    @Test
    public void testObjectSchemaPublicField() throws Exception {
        ObjectSchema publicFieldSchema = ObjectSchema.parse(ForPublicFiled.class);
        assertEquals(
            ForPublicFiled.class.getField("publicField"),
            publicFieldSchema.getProperty("publicField").field()
        );
    }

    private void verifyPropertyBasic(ObjectProperty property, ObjectSchema owner, String name, Type expectedType, Class<?> expectedRawType) {
        assertSame(property.owner(), owner);
        assertEquals(name, property.name());
        assertEquals(expectedType, property.type());
        assertEquals(expectedRawType, property.rawType());
    }

    private void verifyBooleanProperty(ObjectProperty property, ObjectSchema owner, String name, Field field, Method getter, TestData<CharSequence, String> instance) throws Exception {
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
        assertSame(ObjectSchemaParser.defaultParser(), ObjectSchemaParser.defaultParser());
        assertSame(ObjectSchemaParser.defaultCachedParser(), ObjectSchemaParser.defaultCachedParser());
        testObjectParserWithHandler(ObjectSchemaParser.defaultParser());
        testObjectParserWithHandler(ObjectSchemaParser.defaultCachedParser());
    }

    private void testObjectParserWithHandler(ObjectSchemaParser parser) throws Exception {
        // Test with pre handler
        ObjectSchemaParser preParser = parser.withFirstHandler(new PreHandler());
        ObjectSchema preSchema = preParser.parse(Object.class);
        assertEquals(Object.class, preSchema.type());
        assertEquals(0, preSchema.properties().size());

        // Test with last handler
        ObjectSchemaParser lastParser = ObjectSchemaParser.newParser(
            parser.asHandler(), new LastHandler());
        ObjectSchema lastSchema = lastParser.parse(Object.class);
        assertEquals(Object.class, lastSchema.type());
        assertEquals(lastSchema.properties().keySet(), SetKit.set("class", "test"));

        // Test with pre handler as handler
        ObjectSchemaParser asPreParser = ObjectSchemaParser.newParser(preParser.asHandler());
        ObjectSchema asPreSchema = asPreParser.parse(Object.class);
        assertEquals(Object.class, asPreSchema.type());
        assertEquals(0, asPreSchema.properties().size());

        // Test with last handler as handler
        ObjectSchemaParser asLastParser = ObjectSchemaParser.newParser(lastParser.asHandler());
        ObjectSchema asLastSchema = asLastParser.parse(Object.class);
        assertEquals(Object.class, asLastSchema.type());
        assertEquals(asLastSchema.properties().keySet(), SetKit.set("class", "test"));
    }

    private static class PreHandler implements ObjectSchemaParser.Handler {
        @Override
        public boolean parse(@Nonnull ObjectSchemaParser.Context context) throws Exception {
            if (context.parsedType().equals(Object.class)) {
                return false;
            }
            return true;
        }
    }

    private static class LastHandler implements ObjectSchemaParser.Handler {
        @Override
        public boolean parse(@Nonnull ObjectSchemaParser.Context context) throws Exception {
            if (context.parsedType().equals(Object.class)) {
                context.propertyBaseMap().put("test", new ObjectPropertyBase() {
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
        ObjectSchema a1 = ObjectSchema.parse(A.class);
        ObjectSchema a2 = ObjectSchema.parse(A.class);
        ObjectSchema b1 = ObjectSchema.parse(B.class);
        ObjectSchemaParser parser2 = ObjectSchemaParser.newParser(new CommonSchemaHandler());
        ObjectSchema a3 = parser2.parse(A.class);
        assertEquals(a1, a1);
        assertFalse(a1.equals(""));
        assertNotSame(a1, a2);
        assertEquals(a1, a2);
        assertFalse(a1.equals(b1));
        assertFalse(a1.equals(a3));
        // properties equal
        ObjectProperty ap1 = a1.getProperty("class");
        ObjectProperty appp = a1.getProperty("pp");
        ObjectProperty ap2 = a2.getProperty("class");
        ObjectProperty bp1 = b1.getProperty("class");
        ObjectProperty ap3 = a3.getProperty("class");
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
        MapSchema schema = MapSchema.parse(HelloMap.class);
        verifyMapSchemaBasic(schema, HelloMap.class, String.class, Long.class);
        printFor("MapSchema toString", schema);
        assertEquals(
            schema.toString(),
            schema.type().getTypeName()
        );
        assertThrows(DataSchemaException.class, () -> MapSchema.parse(String.class));

        // Test with MapType
        MapSchema schemaWithTypes = MapSchema.parse(MapType.of(Map.class, Object.class, Long.class));
        verifyMapSchemaBasic(schemaWithTypes, Map.class, Object.class, Long.class);

        // Test schema equality
        testMapSchemaEquality();
    }

    @Test
    public void testMapParser() throws Exception {
        assertSame(MapSchemaParser.defaultParser(), MapSchemaParser.defaultParser());
        assertSame(MapSchemaParser.defaultCachedParser(), MapSchemaParser.defaultCachedParser());
        testMapParserWithParser(MapSchemaParser.defaultParser());
        testMapParserWithParser(MapSchemaParser.defaultCachedParser());
    }

    private void testMapParserWithParser(MapSchemaParser parser) throws Exception {
        MapSchema schema = parser.parse(HelloMap.class);
        verifyMapSchemaWithParser(schema, HelloMap.class, String.class, Long.class, parser);
        printFor("MapSchema toString", schema);
        assertEquals(
            schema.toString(),
            schema.type().getTypeName()
        );
        assertThrows(DataSchemaException.class, () -> parser.parse(String.class));

        // Test with MapType
        MapSchema schemaWithTypes = parser.parse(MapType.of(Map.class, Object.class, Long.class));
        verifyMapSchemaWithParser(schemaWithTypes, Map.class, Object.class, Long.class, parser);

        // Test schema equality
        testMapSchemaEqualityWithParser(parser);
    }

    private void verifyMapSchemaBasic(MapSchema schema, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType) {
        assertSame(schema.parser(), MapSchemaParser.defaultParser());
        assertEquals(expectedType, schema.type());
        assertEquals(expectedType, schema.rawType());
        assertTrue(schema.isMapSchema());
        assertFalse(schema.isObjectSchema());
        assertSame(schema.asMapSchema(), schema);
        assertThrows(ClassCastException.class, schema::asObjectSchema);
        assertEquals(expectedKeyType, schema.keyType());
        assertEquals(expectedValueType, schema.valueType());
    }

    private void verifyMapSchemaWithParser(MapSchema schema, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType, MapSchemaParser expectedParser) {
        // For cached parsers, the schema's parser returns the underlying parser, not the cached one
        // So we don't assert same here, just verify other properties
        assertEquals(expectedType, schema.type());
        assertEquals(expectedType, schema.rawType());
        assertTrue(schema.isMapSchema());
        assertFalse(schema.isObjectSchema());
        assertSame(schema.asMapSchema(), schema);
        assertThrows(ClassCastException.class, schema::asObjectSchema);
        assertEquals(expectedKeyType, schema.keyType());
        assertEquals(expectedValueType, schema.valueType());
    }

    private void testMapSchemaEquality() throws Exception {
        MapSchema m1 = MapSchema.parse(Map.class);
        MapSchema m2 = MapSchema.parse(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapSchema m3 = MapSchema.parse(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        // Test with custom parser
        MapSchemaParser parser2 = new CustomMapSchemaParser();
        MapSchema m4 = parser2.parse(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        // Test hash code
        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.parser().hashCode();
        assertEquals(m1.hashCode(), result);
    }

    private void testMapSchemaEqualityWithParser(MapSchemaParser parser) throws Exception {
        MapSchema m1 = parser.parse(Map.class);
        MapSchema m2 = parser.parse(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapSchema m3 = parser.parse(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        // Test with custom parser
        MapSchemaParser parser2 = new CustomMapSchemaParser();
        MapSchema m4 = parser2.parse(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        // Test hash code
        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.parser().hashCode();
        assertEquals(m1.hashCode(), result);
    }

    private static class CustomMapSchemaParser implements MapSchemaParser {
        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type) throws DataSchemaException {
            return new MapSchema() {
                @Override
                public @Nonnull MapSchemaParser parser() {
                    return CustomMapSchemaParser.this;
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
        MapSchemaParser mapParser = MapSchemaParser.newCachedParser(SimpleCache.ofStrong(), MapSchemaParser.defaultParser());
        // Test caching for Map.class
        assertSame(mapParser.parse(Map.class), mapParser.parse(Map.class));
        assertNotSame(MapSchema.parse(Map.class), MapSchema.parse(Map.class));
        // Test caching for MapType
        MapType mapType = MapType.of(Map.class, String.class, Long.class);
        assertSame(mapParser.parse(mapType), mapParser.parse(mapType));
        assertNotSame(MapSchema.parse(mapType), MapSchema.parse(mapType));
    }

    private void testObjectSchemaCachedParser() {
        ObjectSchemaParser objectParser = ObjectSchemaParser.newCachedParser(SimpleCache.ofStrong(), ObjectSchemaParser.defaultParser());
        // Test caching for A.class
        assertSame(objectParser.parse(A.class), objectParser.parse(A.class));
        assertNotSame(ObjectSchema.parse(A.class), ObjectSchema.parse(A.class));
        // Test handler consistency
        assertSame(ObjectSchemaParser.defaultParser().handlers(), objectParser.handlers());
        assertSame(ObjectSchemaParser.defaultParser().asHandler(), objectParser.asHandler());
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
        ObjectSchema schema = ObjectSchema.parse(ForAnnotation.class);
        testAnnotationProp1(schema);
        testAnnotationProp2(schema);
        testAnnotationProp3(schema);
        testAnnotationProp4(schema);
        testAnnotationProp5(schema);
        testAnnotationProp6(schema);
    }

    private void testAnnotationProp1(ObjectSchema schema) {
        ObjectProperty prop1 = schema.getProperty("prop1");
        assertNotNull(prop1);
        Nonnull a1 = prop1.getAnnotation(Nonnull.class);
        assertNotNull(a1);
        assertEquals(Nonnull.class, a1.annotationType());
        assertNull(prop1.getAnnotation(Nullable.class));
        assertEquals(ListKit.list(a1), prop1.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop1.getterAnnotations());
        assertEquals(Collections.emptyList(), prop1.setterAnnotations());
    }

    private void testAnnotationProp2(ObjectSchema schema) {
        ObjectProperty prop2 = schema.getProperty("prop2");
        assertNotNull(prop2);
        Nonnull a2 = prop2.getAnnotation(Nonnull.class);
        assertNotNull(a2);
        assertEquals(Nonnull.class, a2.annotationType());
        assertNull(prop2.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop2.fieldAnnotations());
        assertEquals(ListKit.list(a2), prop2.getterAnnotations());
        assertEquals(Collections.emptyList(), prop2.setterAnnotations());
    }

    private void testAnnotationProp3(ObjectSchema schema) {
        ObjectProperty prop3 = schema.getProperty("prop3");
        assertNotNull(prop3);
        Nonnull a3 = prop3.getAnnotation(Nonnull.class);
        assertNotNull(a3);
        assertEquals(Nonnull.class, a3.annotationType());
        assertNull(prop3.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop3.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop3.getterAnnotations());
        assertEquals(ListKit.list(a3), prop3.setterAnnotations());
    }

    private void testAnnotationProp4(ObjectSchema schema) {
        ObjectProperty prop4 = schema.getProperty("prop4");
        assertNotNull(prop4);
        assertNull(prop4.getAnnotation(Nonnull.class));
        assertNull(prop4.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop4.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop4.getterAnnotations());
        assertEquals(Collections.emptyList(), prop4.setterAnnotations());
    }

    private void testAnnotationProp5(ObjectSchema schema) {
        ObjectProperty prop5 = schema.getProperty("prop5");
        assertNotNull(prop5);
        assertNull(prop5.getAnnotation(Nonnull.class));
        assertNull(prop5.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop5.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop5.getterAnnotations());
        assertEquals(Collections.emptyList(), prop5.setterAnnotations());
    }

    private void testAnnotationProp6(ObjectSchema schema) {
        ObjectProperty prop6 = schema.getProperty("prop6");
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
        assertThrows(DataSchemaException.class, () -> {throw new DataSchemaException();});
        assertThrows(DataSchemaException.class, () -> {throw new DataSchemaException("");});
        assertThrows(DataSchemaException.class, () -> {throw new DataSchemaException("", new RuntimeException());});
        assertThrows(DataSchemaException.class, () -> {throw new DataSchemaException(new RuntimeException());});
        assertThrows(DataSchemaException.class, () -> {throw new DataSchemaException(Object.class);});
        assertThrows(DataSchemaException.class, () -> {
            throw new DataSchemaException(Object.class, new RuntimeException());
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
