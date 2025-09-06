package tests.object;

import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.SetKit;
import xyz.sunqian.common.object.data.DataObjectException;
import xyz.sunqian.common.object.data.DataProperty;
import xyz.sunqian.common.object.data.DataPropertyBase;
import xyz.sunqian.common.object.data.DataSchema;
import xyz.sunqian.common.object.data.DataSchemaParser;
import xyz.sunqian.common.runtime.invoke.Invocable;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class DataSchemaTest implements PrintTest {

    @Test
    public void testSchema() throws Exception {
        Type type = new TypeRef<TestData<CharSequence, String>>() {}.type();
        DataSchema schema = DataSchema.parse(type);
        assertSame(schema.parser(), DataSchemaParser.defaultParser());
        assertEquals(schema.type(), type);
        assertEquals(schema.rawType(), TestData.class);
        assertEquals(
            schema.properties().keySet(),
            SetKit.set("str", "i", "strArray", "t", "UU", "class", "b", "bb", "BB")
        );
        printFor("DataSchema toString", schema);
        assertEquals(
            schema.toString(),
            "data: " + schema.type().getTypeName() + "[" +
                schema.properties().values().stream()
                    .map(DataProperty::toString)
                    .collect(Collectors.joining(", "))
                + "]"
        );
        // instance
        TestData<CharSequence, String> instance = new TestData<>();
        assertNull(instance.str);
        assertEquals(instance.i, 0);
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
        DataProperty str = schema.getProperty("str");
        assertSame(str.owner(), schema);
        assertEquals(str.name(), "str");
        assertEquals(str.type(), String.class);
        assertEquals(str.rawType(), String.class);
        assertEquals(str.field(), strField);
        assertEquals(str.getterMethod(), strGetter);
        assertEquals(str.setterMethod(), strSetter);
        assertTrue(str.isReadable());
        assertNull(str.getValue(instance));
        assertTrue(str.isWriteable());
        str.setValue(instance, "hello");
        assertEquals(instance.str, "hello");
        // property i
        DataProperty i = schema.getProperty("i");
        assertSame(i.owner(), schema);
        assertEquals(i.name(), "i");
        assertEquals(i.type(), int.class);
        assertEquals(i.rawType(), int.class);
        assertEquals(i.field(), iField);
        assertEquals(i.getterMethod(), iGetter);
        assertNull(i.setterMethod());
        assertTrue(i.isReadable());
        assertEquals(i.getValue(instance), 0);
        assertFalse(i.isWriteable());
        // property strArray
        DataProperty strArray = schema.getProperty("strArray");
        assertSame(strArray.owner(), schema);
        assertEquals(strArray.name(), "strArray");
        assertEquals(strArray.type(), String[].class);
        assertEquals(strArray.rawType(), String[].class);
        assertEquals(strArray.field(), strArrayField);
        assertNull(strArray.getterMethod());
        assertEquals(strArray.setterMethod(), strArraySetter);
        assertFalse(strArray.isReadable());
        assertTrue(strArray.isWriteable());
        strArray.setValue(instance, new String[]{"hello"});
        assertEquals(instance.strArray, new String[]{"hello"});
        // property t
        DataProperty t = schema.getProperty("t");
        assertSame(t.owner(), schema);
        assertEquals(t.name(), "t");
        assertEquals(t.type(), CharSequence.class);
        assertEquals(t.rawType(), CharSequence.class);
        assertEquals(t.field(), tField);
        assertEquals(t.getterMethod(), tGetter);
        assertEquals(t.setterMethod(), tSetter);
        assertTrue(t.isReadable());
        assertNull(t.getValue(instance));
        assertTrue(t.isWriteable());
        t.setValue(instance, "hello");
        assertEquals(instance.t, "hello");
        // property UU
        DataProperty UU = schema.getProperty("UU");
        assertSame(UU.owner(), schema);
        assertEquals(UU.name(), "UU");
        assertEquals(UU.type(), String.class);
        assertEquals(UU.rawType(), String.class);
        assertNull(UU.field());
        assertEquals(UU.getterMethod(), uGetter);
        assertEquals(UU.setterMethod(), uSetter);
        assertTrue(UU.isReadable());
        assertNull(UU.getValue(instance));
        assertTrue(UU.isWriteable());
        UU.setValue(instance, "hello");
        assertEquals(instance.u, "hello");
        // property class
        DataProperty classProp = schema.getProperty("class");
        assertSame(classProp.owner(), schema);
        assertEquals(classProp.name(), "class");
        assertEquals(classProp.type(), new TypeRef<Class<?>>() {}.type());
        assertEquals(classProp.rawType(), Class.class);
        assertNull(classProp.field());
        assertEquals(classProp.getterMethod(), classGetter);
        assertNull(classProp.setterMethod());
        assertTrue(classProp.isReadable());
        assertEquals(classProp.getValue(instance), TestData.class);
        assertFalse(classProp.isWriteable());
        // properties b, bb, BB
        DataProperty b = schema.getProperty("b");
        assertSame(b.owner(), schema);
        assertEquals(b.name(), "b");
        assertEquals(b.type(), boolean.class);
        assertEquals(b.rawType(), boolean.class);
        assertEquals(b.field(), bField);
        assertEquals(b.getterMethod(), bGetter);
        assertNull(b.setterMethod());
        assertTrue(b.isReadable());
        assertEquals(b.getValue(instance), instance.isB());
        assertFalse(b.isWriteable());
        DataProperty bb = schema.getProperty("bb");
        assertSame(bb.owner(), schema);
        assertEquals(bb.name(), "bb");
        assertEquals(bb.type(), boolean.class);
        assertEquals(bb.rawType(), boolean.class);
        assertEquals(bb.field(), bbField);
        assertEquals(bb.getterMethod(), bbGetter);
        assertNull(bb.setterMethod());
        assertTrue(bb.isReadable());
        assertEquals(bb.getValue(instance), instance.isBb());
        assertFalse(bb.isWriteable());
        DataProperty BB = schema.getProperty("BB");
        assertSame(BB.owner(), schema);
        assertEquals(BB.name(), "BB");
        assertEquals(BB.type(), boolean.class);
        assertEquals(BB.rawType(), boolean.class);
        assertEquals(BB.field(), BBField);
        assertEquals(BB.getterMethod(), BBGetter);
        assertNull(BB.setterMethod());
        assertTrue(BB.isReadable());
        assertEquals(BB.getValue(instance), instance.isBB());
        assertFalse(BB.isWriteable());
        // error type
        expectThrows(DataObjectException.class, () -> DataSchema.parse(TestData.class.getTypeParameters()[0]));
    }

    @Test
    public void testParser() throws Exception {
        class PreHandler implements DataSchemaParser.Handler {
            @Override
            public boolean parse(@Nonnull DataSchemaParser.Context context) throws Exception {
                if (context.dataType().equals(Object.class)) {
                    return false;
                }
                return true;
            }
        }
        DataSchemaParser preParser = DataSchemaParser.defaultParser().withFirstHandler(new PreHandler());
        DataSchema preSchema = preParser.parse(Object.class);
        assertEquals(preSchema.type(), Object.class);
        assertEquals(preSchema.properties().size(), 0);
        class LastHandler implements DataSchemaParser.Handler {
            @Override
            public boolean parse(@Nonnull DataSchemaParser.Context context) throws Exception {
                if (context.dataType().equals(Object.class)) {
                    context.propertyBaseMap().put("test", new DataPropertyBase() {
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
        DataSchemaParser lastParser = DataSchemaParser.defaultParser().withLastHandler(new LastHandler());
        DataSchema lastSchema = lastParser.parse(Object.class);
        assertEquals(lastSchema.type(), Object.class);
        assertEquals(lastSchema.properties().keySet(), SetKit.set("class", "test"));
        DataSchemaParser asPreParser = DataSchemaParser.withHandlers(preParser.asHandler());
        DataSchema asPreSchema = asPreParser.parse(Object.class);
        assertEquals(asPreSchema.type(), Object.class);
        assertEquals(asPreSchema.properties().size(), 0);
        DataSchemaParser asLastParser = DataSchemaParser.withHandlers(lastParser.asHandler());
        DataSchema asLastSchema = asLastParser.parse(Object.class);
        assertEquals(asLastSchema.type(), Object.class);
        assertEquals(asLastSchema.properties().keySet(), SetKit.set("class", "test"));
    }

    @Test
    public void testEqualHashCode() throws Exception {
        {
            // DataObjectException
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException();
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException("");
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException("", new RuntimeException());
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException(new RuntimeException());
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException(Object.class);
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException(Object.class, new RuntimeException());
            });
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // DataObjectException
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException();
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException("");
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException("", new RuntimeException());
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException(new RuntimeException());
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException(Object.class);
            });
            expectThrows(DataObjectException.class, () -> {
                throw new DataObjectException(Object.class, new RuntimeException());
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
}
