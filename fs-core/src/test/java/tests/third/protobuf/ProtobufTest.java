package tests.third.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.object.build.BuilderExecutor;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.UnsupportedObjectConvertException;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.reflect.TypeRef;
import space.sunqian.fs.third.protobuf.ProtobufBuilderHandler;
import space.sunqian.fs.third.protobuf.ProtobufConvertHandler;
import space.sunqian.fs.third.protobuf.ProtobufKit;
import space.sunqian.fs.third.protobuf.ProtobufSchemaHandler;
import tests.protobuf.Data;
import tests.protobuf.PbSimple;
import tests.protobuf.xEnum;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProtobufTest implements PrintTest {

    @Test
    public void testProtobuf() {
        printFor("Protobuf Support", ProtobufKit.isAvailable());
    }

    @Test
    public void testSchemaHandler() throws Exception {
        ObjectParser protoParser = ObjectParser.newParser(ProtobufSchemaHandler.getInstance());
        Data.Builder builder = Data.newBuilder()
            .setStr("str")
            .setI32(1)
            .setU64(2)
            .setF32(3)
            .setF64(4)
            .setSf32(5)
            .setSf64(6)
            .setSint32(7)
            .setSint64(8)
            .setBytes(ByteString.copyFromUtf8("bytes"))
            .setBool(true)
            .setFloat(1.1f)
            .setDouble(1.2)
            .putSiMap("siMap", 1)
            .addIList(1)
            .addStrList("strList")
            .setXEnum(xEnum.E1);
        ObjectSchema builderSchema = protoParser.parse(Data.Builder.class);
        printFor("Builder", builderSchema);
        assertEquals(Data.Builder.class, builderSchema.type());
        assertEquals(Data.Builder.class, builderSchema.rawType());
        testSchemaHandler(builderSchema, builder, true);
        Data message = builder.build();
        ObjectSchema messageSchema = protoParser.parse(Data.class);
        printFor("Message", messageSchema);
        assertEquals(Data.class, messageSchema.type());
        assertEquals(Data.class, messageSchema.rawType());
        testSchemaHandler(messageSchema, message, false);

        // writes new values
        builderSchema.getProperty("str").setValue(builder, "newStr");
        builderSchema.getProperty("i32").setValue(builder, 2);
        builderSchema.getProperty("u64").setValue(builder, 3L);
        builderSchema.getProperty("f32").setValue(builder, 4);
        builderSchema.getProperty("f64").setValue(builder, 5L);
        builderSchema.getProperty("sf32").setValue(builder, 6);
        builderSchema.getProperty("sf64").setValue(builder, 7L);
        builderSchema.getProperty("sint32").setValue(builder, 8);
        builderSchema.getProperty("sint64").setValue(builder, 9L);
        builderSchema.getProperty("bytes").setValue(builder, ByteString.copyFromUtf8("newBytes"));
        builderSchema.getProperty("bool").setValue(builder, false);
        builderSchema.getProperty("float").setValue(builder, 2.1f);
        builderSchema.getProperty("double").setValue(builder, 2.2);
        builderSchema.getProperty("siMap").setValue(builder, MapKit.map("siMap", 2));
        builderSchema.getProperty("iList").setValue(builder, ListKit.list(2));
        builderSchema.getProperty("strList").setValue(builder, ListKit.list("newStrList"));
        builderSchema.getProperty("xEnum").setValue(builder, xEnum.E2);
        Data message2 = builder.build();
        assertEquals("newStr", message2.getStr());
        assertEquals(2, message2.getI32());
        assertEquals(3L, message2.getU64());
        assertEquals(4, message2.getF32());
        assertEquals(5L, message2.getF64());
        assertEquals(6, message2.getSf32());
        assertEquals(7L, message2.getSf64());
        assertEquals(8, message2.getSint32());
        assertEquals(9L, message2.getSint64());
        assertEquals("newBytes", message2.getBytes().toStringUtf8());
        assertEquals(false, message2.getBool());
        assertEquals(2.1f, message2.getFloat());
        assertEquals(2.2, message2.getDouble());
        assertEquals(message2.getSiMapMap(), MapKit.map("siMap", 2));
        assertEquals(message2.getIListList(), ListKit.list(2));
        assertEquals(message2.getStrListList(), ListKit.list("newStrList"));
        assertEquals(xEnum.E2, message2.getXEnum());

        // error
        class T<T> {}
        ObjectSchema NonClassSchema = protoParser.parse(T.class.getTypeParameters()[0]);
        assertEquals(0, NonClassSchema.properties().size());
        ObjectSchema NonProtoSchema = protoParser.parse(String.class);
        assertEquals(0, NonProtoSchema.properties().size());
    }

    private void testSchemaHandler(ObjectSchema schema, Object inst, boolean writable) throws Exception {
        assertEquals(
            schema.properties().keySet(),
            SetKit.set("str", "i32", "u64", "f32", "f64", "sf32", "sf64", "sint32", "sint64", "bytes", "bool",
                "float", "double", "siMap", "iList", "strList", "xEnum")
        );
        {
            // str
            ObjectProperty str = schema.getProperty("str");
            assertNotNull(str);
            assertEquals(String.class, str.type());
            assertEquals("str", str.getValue(inst));
            assertTrue(str.isReadable());
            assertEquals(str.isWritable(), writable);
            assertNotNull(str.getterMethod());
            assertNotNull(str.getter());
            if (writable) {
                assertNotNull(str.setterMethod());
                assertNotNull(str.setter());
            } else {
                assertNull(str.setterMethod());
                assertNull(str.setter());
            }
        }
        {
            // i32
            ObjectProperty i32 = schema.getProperty("i32");
            assertNotNull(i32);
            assertEquals(int.class, i32.type());
            assertEquals(1, i32.getValue(inst));
            assertTrue(i32.isReadable());
            assertEquals(i32.isWritable(), writable);
            assertNotNull(i32.getterMethod());
            assertNotNull(i32.getter());
            if (writable) {
                assertNotNull(i32.setterMethod());
                assertNotNull(i32.setter());
            } else {
                assertNull(i32.setterMethod());
                assertNull(i32.setter());
            }
        }
        {
            // u64
            ObjectProperty u64 = schema.getProperty("u64");
            assertNotNull(u64);
            assertEquals(long.class, u64.type());
            assertEquals(2L, u64.getValue(inst));
            assertTrue(u64.isReadable());
            assertEquals(u64.isWritable(), writable);
            assertNotNull(u64.getterMethod());
            assertNotNull(u64.getter());
            if (writable) {
                assertNotNull(u64.setterMethod());
                assertNotNull(u64.setter());
            } else {
                assertNull(u64.setterMethod());
                assertNull(u64.setter());
            }
        }
        {
            // f32
            ObjectProperty f32 = schema.getProperty("f32");
            assertNotNull(f32);
            assertEquals(int.class, f32.type());
            assertEquals(3, f32.getValue(inst));
            assertTrue(f32.isReadable());
            assertEquals(f32.isWritable(), writable);
            assertNotNull(f32.getterMethod());
            assertNotNull(f32.getter());
            if (writable) {
                assertNotNull(f32.setterMethod());
                assertNotNull(f32.setter());
            } else {
                assertNull(f32.setterMethod());
                assertNull(f32.setter());
            }
        }
        {
            // f64
            ObjectProperty f64 = schema.getProperty("f64");
            assertNotNull(f64);
            assertEquals(long.class, f64.type());
            assertEquals(4L, f64.getValue(inst));
            assertTrue(f64.isReadable());
            assertEquals(f64.isWritable(), writable);
            assertNotNull(f64.getterMethod());
            assertNotNull(f64.getter());
            if (writable) {
                assertNotNull(f64.setterMethod());
                assertNotNull(f64.setter());
            } else {
                assertNull(f64.setterMethod());
                assertNull(f64.setter());
            }
        }
        {
            // sf32
            ObjectProperty sf32 = schema.getProperty("sf32");
            assertNotNull(sf32);
            assertEquals(int.class, sf32.type());
            assertEquals(5, sf32.getValue(inst));
            assertTrue(sf32.isReadable());
            assertEquals(sf32.isWritable(), writable);
            assertNotNull(sf32.getterMethod());
            assertNotNull(sf32.getter());
            if (writable) {
                assertNotNull(sf32.setterMethod());
                assertNotNull(sf32.setter());
            } else {
                assertNull(sf32.setterMethod());
                assertNull(sf32.setter());
            }
        }
        {
            // sf64
            ObjectProperty sf64 = schema.getProperty("sf64");
            assertNotNull(sf64);
            assertEquals(long.class, sf64.type());
            assertEquals(6L, sf64.getValue(inst));
            assertTrue(sf64.isReadable());
            assertEquals(sf64.isWritable(), writable);
            assertNotNull(sf64.getterMethod());
            assertNotNull(sf64.getter());
            if (writable) {
                assertNotNull(sf64.setterMethod());
                assertNotNull(sf64.setter());
            } else {
                assertNull(sf64.setterMethod());
                assertNull(sf64.setter());
            }
        }
        {
            // sint32
            ObjectProperty sint32 = schema.getProperty("sint32");
            assertNotNull(sint32);
            assertEquals(int.class, sint32.type());
            assertEquals(7, sint32.getValue(inst));
            assertTrue(sint32.isReadable());
            assertEquals(sint32.isWritable(), writable);
            assertNotNull(sint32.getterMethod());
            assertNotNull(sint32.getter());
            if (writable) {
                assertNotNull(sint32.setterMethod());
                assertNotNull(sint32.setter());
            } else {
                assertNull(sint32.setterMethod());
                assertNull(sint32.setter());
            }
        }
        {
            // sint64
            ObjectProperty sint64 = schema.getProperty("sint64");
            assertNotNull(sint64);
            assertEquals(long.class, sint64.type());
            assertEquals(8L, sint64.getValue(inst));
            assertTrue(sint64.isReadable());
            assertEquals(sint64.isWritable(), writable);
            assertNotNull(sint64.getterMethod());
            assertNotNull(sint64.getter());
            if (writable) {
                assertNotNull(sint64.setterMethod());
                assertNotNull(sint64.setter());
            } else {
                assertNull(sint64.setterMethod());
                assertNull(sint64.setter());
            }
        }
        {
            // bytes
            ObjectProperty bytes = schema.getProperty("bytes");
            assertNotNull(bytes);
            assertEquals(ByteString.class, bytes.type());
            assertEquals(bytes.getValue(inst), ByteString.copyFromUtf8("bytes"));
            assertTrue(bytes.isReadable());
            assertEquals(bytes.isWritable(), writable);
            assertNotNull(bytes.getterMethod());
            assertNotNull(bytes.getter());
            if (writable) {
                assertNotNull(bytes.setterMethod());
                assertNotNull(bytes.setter());
            } else {
                assertNull(bytes.setterMethod());
                assertNull(bytes.setter());
            }
        }
        {
            // bool
            ObjectProperty bool = schema.getProperty("bool");
            assertNotNull(bool);
            assertEquals(boolean.class, bool.type());
            assertEquals(true, bool.getValue(inst));
            assertTrue(bool.isReadable());
            assertEquals(bool.isWritable(), writable);
            assertNotNull(bool.getterMethod());
            assertNotNull(bool.getter());
            if (writable) {
                assertNotNull(bool.setterMethod());
                assertNotNull(bool.setter());
            } else {
                assertNull(bool.setterMethod());
                assertNull(bool.setter());
            }
        }
        {
            // float
            ObjectProperty floatProp = schema.getProperty("float");
            assertNotNull(floatProp);
            assertEquals(float.class, floatProp.type());
            assertEquals(1.1f, floatProp.getValue(inst));
            assertTrue(floatProp.isReadable());
            assertEquals(floatProp.isWritable(), writable);
            assertNotNull(floatProp.getterMethod());
            assertNotNull(floatProp.getter());
            if (writable) {
                assertNotNull(floatProp.setterMethod());
                assertNotNull(floatProp.setter());
            } else {
                assertNull(floatProp.setterMethod());
                assertNull(floatProp.setter());
            }
        }
        {
            // double
            ObjectProperty doubleProp = schema.getProperty("double");
            assertNotNull(doubleProp);
            assertEquals(double.class, doubleProp.type());
            assertEquals(1.2, doubleProp.getValue(inst));
            assertTrue(doubleProp.isReadable());
            assertEquals(doubleProp.isWritable(), writable);
            assertNotNull(doubleProp.getterMethod());
            assertNotNull(doubleProp.getter());
            if (writable) {
                assertNotNull(doubleProp.setterMethod());
                assertNotNull(doubleProp.setter());
            } else {
                assertNull(doubleProp.setterMethod());
                assertNull(doubleProp.setter());
            }
        }
        {
            // siMap
            ObjectProperty siMap = schema.getProperty("siMap");
            assertNotNull(siMap);
            assertEquals(siMap.type(), new TypeRef<Map<String, Integer>>() {}.type());
            assertEquals(siMap.getValue(inst), MapKit.map("siMap", 1));
            assertTrue(siMap.isReadable());
            assertEquals(siMap.isWritable(), writable);
            assertNotNull(siMap.getterMethod());
            assertNotNull(siMap.getter());
            if (writable) {
                assertNull(siMap.setterMethod());
                assertNotNull(siMap.setter());
            } else {
                assertNull(siMap.setterMethod());
                assertNull(siMap.setter());
            }
        }
        {
            // iList
            ObjectProperty iList = schema.getProperty("iList");
            assertNotNull(iList);
            assertEquals(iList.type(), new TypeRef<List<Integer>>() {}.type());
            assertEquals(iList.getValue(inst), ListKit.list(1));
            assertTrue(iList.isReadable());
            assertEquals(iList.isWritable(), writable);
            assertNotNull(iList.getterMethod());
            assertNotNull(iList.getter());
            if (writable) {
                assertNull(iList.setterMethod());
                assertNotNull(iList.setter());
            } else {
                assertNull(iList.setterMethod());
                assertNull(iList.setter());
            }
        }
        {
            // strList
            ObjectProperty strList = schema.getProperty("strList");
            assertNotNull(strList);
            assertEquals(strList.type(), new TypeRef<List<String>>() {}.type());
            assertEquals(strList.getValue(inst), ListKit.list("strList"));
            assertTrue(strList.isReadable());
            assertEquals(strList.isWritable(), writable);
            assertNotNull(strList.getterMethod());
            assertNotNull(strList.getter());
            if (writable) {
                assertNull(strList.setterMethod());
                assertNotNull(strList.setter());
            } else {
                assertNull(strList.setterMethod());
                assertNull(strList.setter());
            }
        }
        {
            // xEnum
            ObjectProperty xEnum = schema.getProperty("xEnum");
            assertNotNull(xEnum);
            assertEquals(xEnum.class, xEnum.type());
            assertEquals(xEnum.getValue(inst), tests.protobuf.xEnum.E1);
            assertTrue(xEnum.isReadable());
            assertEquals(xEnum.isWritable(), writable);
            assertNotNull(xEnum.getterMethod());
            assertNotNull(xEnum.getter());
            if (writable) {
                assertNotNull(xEnum.setterMethod());
                assertNotNull(xEnum.setter());
            } else {
                assertNull(xEnum.setterMethod());
                assertNull(xEnum.setter());
            }
        }
    }

    @Test
    public void testCreatorHandler() {
        BuilderProvider defaultProvider = BuilderProvider.defaultProvider();
        BuilderProvider provider = BuilderProvider
            .newProvider(ProtobufBuilderHandler.getInstance(), defaultProvider.asHandler());
        ObjectParser parser = ObjectParser
            .defaultParser()
            .withFirstHandler(new ProtobufSchemaHandler());
        {
            // type
            BuilderExecutor messageCreator = provider.forType(PbSimple.class);
            assertEquals(PbSimple.class, messageCreator.targetType());
            assertEquals(PbSimple.Builder.class, messageCreator.builderType());
            BuilderExecutor builderCreator = provider.forType(PbSimple.Builder.class);
            assertEquals(PbSimple.Builder.class, builderCreator.targetType());
            assertEquals(PbSimple.Builder.class, builderCreator.builderType());
        }
        {
            // java to pb
            JvSimple jvSimple = new JvSimple("123", 456);
            PbSimple pbSimple = ObjectConverter.defaultConverter().convert(jvSimple, PbSimple.class,
                ConvertOption.builderProvider(provider),
                ConvertOption.schemaParser(parser)
            );
            assertEquals(pbSimple.getP1(), jvSimple.getP1());
            assertEquals(pbSimple.getP2(), jvSimple.getP2());
        }
        {
            // pb to java
            PbSimple pbSimple = PbSimple.newBuilder()
                .setP1("123")
                .setP2(456)
                .build();
            JvSimple jvSimple = ObjectConverter.defaultConverter().convert(pbSimple, JvSimple.class,
                ConvertOption.builderProvider(provider),
                ConvertOption.schemaParser(parser)
            );
            assertEquals(pbSimple.getP1(), jvSimple.getP1());
            assertEquals(pbSimple.getP2(), jvSimple.getP2());
        }
        {
            // java to pb.Builder
            JvSimple jvSimple = new JvSimple("123", 456);
            PbSimple.Builder pbSimpleBuilder = ObjectConverter.defaultConverter().convert(jvSimple, PbSimple.Builder.class,
                ConvertOption.builderProvider(provider),
                ConvertOption.schemaParser(parser)
            );
            assertEquals(pbSimpleBuilder.getP1(), jvSimple.getP1());
            assertEquals(pbSimpleBuilder.getP2(), jvSimple.getP2());
        }
        {
            // pb.Builder to java
            PbSimple.Builder pbSimpleBuilder = PbSimple.newBuilder()
                .setP1("123")
                .setP2(456);
            JvSimple jvSimple = ObjectConverter.defaultConverter().convert(pbSimpleBuilder, JvSimple.class,
                ConvertOption.builderProvider(provider),
                ConvertOption.schemaParser(parser)
            );
            assertEquals(pbSimpleBuilder.getP1(), jvSimple.getP1());
            assertEquals(pbSimpleBuilder.getP2(), jvSimple.getP2());
        }
        {
            // java to java
            JvSimple jvSimple = new JvSimple("123", 456);
            JvT<String> jvT = ObjectConverter.defaultConverter().convert(jvSimple, new TypeRef<JvT<String>>() {},
                ConvertOption.builderProvider(provider),
                ConvertOption.schemaParser(parser)
            );
            assertEquals(jvT.getP1(), jvSimple.getP1());
            assertEquals(jvT.getP2(), jvSimple.getP2());
        }
    }

    @Test
    public void testConvertHandler() {
        ObjectConverter defaultConverter = ObjectConverter.defaultConverter();
        ObjectConverter converter = ObjectConverter
            .newConverter(ProtobufConvertHandler.getInstance(), defaultConverter.asHandler());
        String str = "12313213213";
        ByteString byteString = converter.convert(str, ByteString.class);
        assertEquals(str, byteString.toStringUtf8());
        assertEquals(str, converter.convert(byteString, String.class));
        ProtocolStringList psList = converter.convert(ListKit.list("1", "2", "3"), ProtocolStringList.class);
        assertEquals(psList, ListKit.list("1", "2", "3"));
        assertSame(str, converter.convert(str, String.class));
        assertThrows(UnsupportedObjectConvertException.class, () -> converter.convert(null, List.class));
        ObjectConverter cvt2 = ObjectConverter
            .newConverter(new ProtobufConvertHandler());
        assertThrows(UnsupportedObjectConvertException.class, () ->
            cvt2.convert(str, ByteString.class));
        assertThrows(UnsupportedObjectConvertException.class, () ->
            cvt2.convert(ListKit.list("1", "2", "3"), ProtocolStringList.class));
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JvSimple {
        private String p1;
        private int p2;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JvT<T> {
        private String p1;
        private int p2;
    }
}
