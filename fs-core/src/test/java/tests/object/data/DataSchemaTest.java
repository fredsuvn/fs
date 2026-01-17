package tests.object.data;

import internal.test.PrintTest;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.MapSchemaParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectPropertyBase;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
import space.sunqian.fs.object.schema.handlers.SimpleBeanSchemaHandler;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataSchemaTest implements PrintTest {

    @Test
    public void testObjectSchema() throws Exception {
        Type type = new TypeRef<TestData<CharSequence, String>>() {}.type();
        ObjectSchema schema = ObjectSchema.parse(type);
        assertSame(schema.parser(), ObjectSchemaParser.defaultParser());
        assertEquals(schema.type(), type);
        assertEquals(TestData.class, schema.rawType());
        assertTrue(schema.isObjectSchema());
        assertFalse(schema.isMapSchema());
        assertSame(schema.asObjectSchema(), schema);
        assertThrows(ClassCastException.class, schema::asMapSchema);
        assertEquals(
            schema.properties().keySet(),
            SetKit.set("str", "i", "strArray", "t", "UU", "class", "b", "bb", "BB")
        );
        printFor("ObjectSchema toString", schema);
        assertEquals(
            schema.toString(),
            schema.type().getTypeName() + "{" +
                schema.properties().values().stream()
                    .map(ObjectProperty::toString)
                    .collect(Collectors.joining(", "))
                + "}"
        );
        // instance
        TestData<CharSequence, String> instance = new TestData<>();
        assertNull(instance.str);
        assertEquals(0, instance.i);
        assertNull(instance.strArray);
        assertNull(instance.t);
        assertNull(instance.u);
        // fields
        Field strField = TestData.class.getDeclaredField("str");
        Field iField = TestData.class.getDeclaredField("i");
        Field strArrayField = TestData.class.getDeclaredField("strArray");
        Field tField = TestData.class.getDeclaredField("t");
        // Field uField = TestData.class.getDeclaredField("u");
        Field bField = TestData.class.getDeclaredField("b");
        Field bbField = TestData.class.getDeclaredField("bb");
        Field BBField = TestData.class.getDeclaredField("BB");
        // methods
        Method strGetter = TestData.class.getDeclaredMethod("getStr");
        Method strSetter = TestData.class.getDeclaredMethod("setStr", String.class);
        Method iGetter = TestData.class.getDeclaredMethod("getI");
        Method strArraySetter = TestData.class.getDeclaredMethod("setStrArray", String[].class);
        Method tGetter = TestData.class.getDeclaredMethod("getT");
        Method tSetter = TestData.class.getDeclaredMethod("setT", Object.class);
        Method uGetter = TestData.class.getDeclaredMethod("getUU");
        Method uSetter = TestData.class.getDeclaredMethod("setUU", Object.class);
        Method classGetter = TestData.class.getMethod("getClass");
        Method bGetter = TestData.class.getDeclaredMethod("isB");
        Method bbGetter = TestData.class.getDeclaredMethod("isBb");
        Method BBGetter = TestData.class.getDeclaredMethod("isBB");
        // property str
        ObjectProperty str = schema.getProperty("str");
        assertSame(str.owner(), schema);
        assertEquals("str", str.name());
        assertEquals(String.class, str.type());
        assertEquals(String.class, str.rawType());
        assertEquals(str.field(), strField);
        assertEquals(str.getterMethod(), strGetter);
        assertEquals(str.setterMethod(), strSetter);
        assertTrue(str.isReadable());
        assertNull(str.getValue(instance));
        assertTrue(str.isWritable());
        str.setValue(instance, "hello");
        assertEquals("hello", instance.str);
        // property i
        ObjectProperty i = schema.getProperty("i");
        assertSame(i.owner(), schema);
        assertEquals("i", i.name());
        assertEquals(int.class, i.type());
        assertEquals(int.class, i.rawType());
        assertEquals(i.field(), iField);
        assertEquals(i.getterMethod(), iGetter);
        assertNull(i.setterMethod());
        assertTrue(i.isReadable());
        assertEquals(0, i.getValue(instance));
        assertFalse(i.isWritable());
        assertThrows(DataSchemaException.class, () -> i.setValue(instance, 1));
        // property strArray
        ObjectProperty strArray = schema.getProperty("strArray");
        assertSame(strArray.owner(), schema);
        assertEquals("strArray", strArray.name());
        assertEquals(String[].class, strArray.type());
        assertEquals(String[].class, strArray.rawType());
        assertEquals(strArray.field(), strArrayField);
        assertNull(strArray.getterMethod());
        assertEquals(strArray.setterMethod(), strArraySetter);
        assertFalse(strArray.isReadable());
        assertThrows(DataSchemaException.class, () -> strArray.getValue(instance));
        assertTrue(strArray.isWritable());
        strArray.setValue(instance, new String[]{"hello"});
        assertArrayEquals(new String[]{"hello"}, instance.strArray);
        // property t
        ObjectProperty t = schema.getProperty("t");
        assertSame(t.owner(), schema);
        assertEquals("t", t.name());
        assertEquals(CharSequence.class, t.type());
        assertEquals(CharSequence.class, t.rawType());
        assertEquals(t.field(), tField);
        assertEquals(t.getterMethod(), tGetter);
        assertEquals(t.setterMethod(), tSetter);
        assertTrue(t.isReadable());
        assertNull(t.getValue(instance));
        assertTrue(t.isWritable());
        t.setValue(instance, "hello");
        assertEquals("hello", instance.t);
        // property UU
        ObjectProperty UU = schema.getProperty("UU");
        assertSame(UU.owner(), schema);
        assertEquals("UU", UU.name());
        assertEquals(String.class, UU.type());
        assertEquals(String.class, UU.rawType());
        assertNull(UU.field());
        assertEquals(UU.getterMethod(), uGetter);
        assertEquals(UU.setterMethod(), uSetter);
        assertTrue(UU.isReadable());
        assertNull(UU.getValue(instance));
        assertTrue(UU.isWritable());
        UU.setValue(instance, "hello");
        assertEquals("hello", instance.u);
        // property class
        ObjectProperty classProp = schema.getProperty("class");
        assertSame(classProp.owner(), schema);
        assertEquals("class", classProp.name());
        assertEquals(classProp.type(), new TypeRef<Class<?>>() {}.type());
        assertEquals(Class.class, classProp.rawType());
        assertNull(classProp.field());
        assertEquals(classProp.getterMethod(), classGetter);
        assertNull(classProp.setterMethod());
        assertTrue(classProp.isReadable());
        assertEquals(TestData.class, classProp.getValue(instance));
        assertFalse(classProp.isWritable());
        // properties b, bb, BB
        ObjectProperty b = schema.getProperty("b");
        assertSame(b.owner(), schema);
        assertEquals("b", b.name());
        assertEquals(boolean.class, b.type());
        assertEquals(boolean.class, b.rawType());
        assertEquals(b.field(), bField);
        assertEquals(b.getterMethod(), bGetter);
        assertNull(b.setterMethod());
        assertTrue(b.isReadable());
        assertEquals(b.getValue(instance), instance.isB());
        assertFalse(b.isWritable());
        ObjectProperty bb = schema.getProperty("bb");
        assertSame(bb.owner(), schema);
        assertEquals("bb", bb.name());
        assertEquals(boolean.class, bb.type());
        assertEquals(boolean.class, bb.rawType());
        assertEquals(bb.field(), bbField);
        assertEquals(bb.getterMethod(), bbGetter);
        assertNull(bb.setterMethod());
        assertTrue(bb.isReadable());
        assertEquals(bb.getValue(instance), instance.isBb());
        assertFalse(bb.isWritable());
        ObjectProperty BB = schema.getProperty("BB");
        assertSame(BB.owner(), schema);
        assertEquals("BB", BB.name());
        assertEquals(boolean.class, BB.type());
        assertEquals(boolean.class, BB.rawType());
        assertEquals(BB.field(), BBField);
        assertEquals(BB.getterMethod(), BBGetter);
        assertNull(BB.setterMethod());
        assertTrue(BB.isReadable());
        assertEquals(BB.getValue(instance), instance.isBB());
        assertFalse(BB.isWritable());
        // error type
        assertThrows(DataSchemaException.class, () -> ObjectSchema.parse(TestData.class.getTypeParameters()[0]));
        // raw type
        ObjectSchema raw = ObjectSchema.parse(TestData.class);
        assertEquals(raw.getProperty("t").type(), TestData.class.getTypeParameters()[0]);
        assertEquals(raw.getProperty("UU").type(), TestData.class.getTypeParameters()[1]);

        // public field
        ObjectSchema publicFieldSchema = ObjectSchema.parse(ForPublicFiled.class);
        assertEquals(
            ForPublicFiled.class.getField("publicField"),
            publicFieldSchema.getProperty("publicField").field()
        );
    }

    @Test
    public void testObjectParser() throws Exception {
        class PreHandler implements ObjectSchemaParser.Handler {
            @Override
            public boolean parse(@Nonnull ObjectSchemaParser.Context context) throws Exception {
                if (context.dataType().equals(Object.class)) {
                    return false;
                }
                return true;
            }
        }
        ObjectSchemaParser preParser = ObjectSchemaParser.defaultParser().withFirstHandler(new PreHandler());
        ObjectSchema preSchema = preParser.parse(Object.class);
        assertEquals(Object.class, preSchema.type());
        assertEquals(0, preSchema.properties().size());
        class LastHandler implements ObjectSchemaParser.Handler {
            @Override
            public boolean parse(@Nonnull ObjectSchemaParser.Context context) throws Exception {
                if (context.dataType().equals(Object.class)) {
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
        ObjectSchemaParser lastParser = ObjectSchemaParser.newParser(
            ObjectSchemaParser.defaultParser().asHandler(), new LastHandler());
        ObjectSchema lastSchema = lastParser.parse(Object.class);
        assertEquals(Object.class, lastSchema.type());
        assertEquals(lastSchema.properties().keySet(), SetKit.set("class", "test"));
        ObjectSchemaParser asPreParser = ObjectSchemaParser.newParser(preParser.asHandler());
        ObjectSchema asPreSchema = asPreParser.parse(Object.class);
        assertEquals(Object.class, asPreSchema.type());
        assertEquals(0, asPreSchema.properties().size());
        ObjectSchemaParser asLastParser = ObjectSchemaParser.newParser(lastParser.asHandler());
        ObjectSchema asLastSchema = asLastParser.parse(Object.class);
        assertEquals(Object.class, asLastSchema.type());
        assertEquals(asLastSchema.properties().keySet(), SetKit.set("class", "test"));
    }

    @Test
    public void testEqualHashCode() throws Exception {
        // schema equal
        ObjectSchema a1 = ObjectSchema.parse(A.class);
        ObjectSchema a2 = ObjectSchema.parse(A.class);
        ObjectSchema b1 = ObjectSchema.parse(B.class);
        ObjectSchemaParser parser2 = ObjectSchemaParser.newParser(new SimpleBeanSchemaHandler());
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
        assertSame(schema.parser(), MapSchemaParser.defaultParser());
        assertEquals(HelloMap.class, schema.type());
        assertEquals(HelloMap.class, schema.rawType());
        assertTrue(schema.isMapSchema());
        assertFalse(schema.isObjectSchema());
        assertSame(schema.asMapSchema(), schema);
        assertThrows(ClassCastException.class, schema::asObjectSchema);
        assertEquals(String.class, schema.keyType());
        assertEquals(Long.class, schema.valueType());
        printFor("MapSchema toString", schema);
        assertEquals(
            schema.toString(),
            schema.type().getTypeName()
        );
        assertThrows(DataSchemaException.class, () -> MapSchema.parse(String.class));
        MapSchema schemaWithTypes = MapSchema.parse(Map.class, Object.class, Long.class);
        assertEquals(Map.class, schemaWithTypes.type());
        assertEquals(Map.class, schemaWithTypes.rawType());
        assertSame(schemaWithTypes.parser(), MapSchemaParser.defaultParser());
        assertEquals(Object.class, schemaWithTypes.keyType());
        assertEquals(Long.class, schemaWithTypes.valueType());
        // schema equal
        MapSchema m1 = MapSchema.parse(Map.class);
        MapSchema m2 = MapSchema.parse(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapSchema m3 = MapSchema.parse(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);
        class Parser2 implements MapSchemaParser {
            @Override
            public @Nonnull MapSchema parse(@Nonnull Type type) throws DataSchemaException {
                return new MapSchema() {
                    @Override
                    public @Nonnull MapSchemaParser parser() {
                        return Parser2.this;
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
            public @Nonnull MapSchema parse(
                @Nonnull Type type, @Nonnull Type keyType, @Nonnull Type valueType
            ) throws DataSchemaException {
                return null;
            }
        }
        MapSchemaParser parser2 = new Parser2();
        MapSchema m4 = parser2.parse(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);
        // hash code
        {
            int result = 1;
            result = 31 * result + m1.type().hashCode();
            result = 31 * result + m1.parser().hashCode();
            assertEquals(m1.hashCode(), result);
        }
    }

    @Test
    public void testException() {
        {
            // DataObjectException
            assertThrows(DataSchemaException.class, () -> {
                throw new DataSchemaException();
            });
            assertThrows(DataSchemaException.class, () -> {
                throw new DataSchemaException("");
            });
            assertThrows(DataSchemaException.class, () -> {
                throw new DataSchemaException("", new RuntimeException());
            });
            assertThrows(DataSchemaException.class, () -> {
                throw new DataSchemaException(new RuntimeException());
            });
            assertThrows(DataSchemaException.class, () -> {
                throw new DataSchemaException(Object.class);
            });
            assertThrows(DataSchemaException.class, () -> {
                throw new DataSchemaException(Object.class, new RuntimeException());
            });
        }
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
}
