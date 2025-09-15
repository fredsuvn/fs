package tests.third.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.testng.annotations.Test;
import tests.protobuf.Data;
import tests.protobuf.PbSimple;
import tests.protobuf.xEnum;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.collect.SetKit;
import xyz.sunqian.common.object.convert.ConvertOption;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.convert.UnsupportedObjectConvertException;
import xyz.sunqian.common.object.data.ObjectBuilderProvider;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.object.data.ObjectSchema;
import xyz.sunqian.common.object.data.ObjectSchemaParser;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.common.third.protobuf.ProtobufBuilderHandler;
import xyz.sunqian.common.third.protobuf.ProtobufConvertHandler;
import xyz.sunqian.common.third.protobuf.ProtobufKit;
import xyz.sunqian.common.third.protobuf.ProtobufSchemaHandler;
import xyz.sunqian.test.PrintTest;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ProtobufTest implements PrintTest {

    @Test
    public void testProtobuf() {
        printFor("Protobuf Support", ProtobufKit.isAvailable());
    }

    @Test
    public void testSchemaHandler() throws Exception {
        ObjectSchemaParser protoParser = ObjectSchemaParser.newParser(new ProtobufSchemaHandler());
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
        assertEquals(builderSchema.type(), Data.Builder.class);
        assertEquals(builderSchema.rawType(), Data.Builder.class);
        testSchemaHandler(builderSchema, builder, true);
        Data message = builder.build();
        ObjectSchema messageSchema = protoParser.parse(Data.class);
        printFor("Message", messageSchema);
        assertEquals(messageSchema.type(), Data.class);
        assertEquals(messageSchema.rawType(), Data.class);
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
        assertEquals(message2.getStr(), "newStr");
        assertEquals(message2.getI32(), 2);
        assertEquals(message2.getU64(), 3L);
        assertEquals(message2.getF32(), 4);
        assertEquals(message2.getF64(), 5L);
        assertEquals(message2.getSf32(), 6);
        assertEquals(message2.getSf64(), 7L);
        assertEquals(message2.getSint32(), 8);
        assertEquals(message2.getSint64(), 9L);
        assertEquals(message2.getBytes().toStringUtf8(), "newBytes");
        assertEquals(message2.getBool(), false);
        assertEquals(message2.getFloat(), 2.1f);
        assertEquals(message2.getDouble(), 2.2);
        assertEquals(message2.getSiMapMap(), MapKit.map("siMap", 2));
        assertEquals(message2.getIListList(), ListKit.list(2));
        assertEquals(message2.getStrListList(), ListKit.list("newStrList"));
        assertEquals(message2.getXEnum(), xEnum.E2);

        // error
        class T<T> {}
        ObjectSchema NonClassSchema = protoParser.parse(T.class.getTypeParameters()[0]);
        assertEquals(NonClassSchema.properties().size(), 0);
        ObjectSchema NonProtoSchema = protoParser.parse(String.class);
        assertEquals(NonProtoSchema.properties().size(), 0);
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
            assertEquals(str.type(), String.class);
            assertEquals(str.getValue(inst), "str");
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
            assertEquals(i32.type(), int.class);
            assertEquals(i32.getValue(inst), 1);
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
            assertEquals(u64.type(), long.class);
            assertEquals(u64.getValue(inst), 2L);
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
            assertEquals(f32.type(), int.class);
            assertEquals(f32.getValue(inst), 3);
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
            assertEquals(f64.type(), long.class);
            assertEquals(f64.getValue(inst), 4L);
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
            assertEquals(sf32.type(), int.class);
            assertEquals(sf32.getValue(inst), 5);
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
            assertEquals(sf64.type(), long.class);
            assertEquals(sf64.getValue(inst), 6L);
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
            assertEquals(sint32.type(), int.class);
            assertEquals(sint32.getValue(inst), 7);
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
            assertEquals(sint64.type(), long.class);
            assertEquals(sint64.getValue(inst), 8L);
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
            assertEquals(bytes.type(), ByteString.class);
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
            assertEquals(bool.type(), boolean.class);
            assertEquals(bool.getValue(inst), true);
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
            assertEquals(floatProp.type(), float.class);
            assertEquals(floatProp.getValue(inst), 1.1f);
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
            assertEquals(doubleProp.type(), double.class);
            assertEquals(doubleProp.getValue(inst), 1.2);
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
            assertEquals(xEnum.type(), xEnum.class);
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
    public void testBuilderHandler() {
        ObjectBuilderProvider provider = ObjectBuilderProvider
            .defaultProvider()
            .withFirstHandler(new ProtobufBuilderHandler());
        ObjectSchemaParser parser = ObjectSchemaParser
            .defaultParser()
            .withFirstHandler(new ProtobufSchemaHandler());
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
        ObjectConverter converter = ObjectConverter
            .defaultConverter()
            .withFirstHandler(new ProtobufConvertHandler());
        String str = "12313213213";
        ByteString byteString = converter.convert(str, ByteString.class);
        assertEquals(byteString.toStringUtf8(), str);
        assertEquals(converter.convert(byteString, String.class), str);
        ProtocolStringList psList = converter.convert(ListKit.list("1", "2", "3"), ProtocolStringList.class);
        assertEquals(psList, ListKit.list("1", "2", "3"));
        assertSame(converter.convert(str, String.class), str);
        expectThrows(UnsupportedObjectConvertException.class, () -> converter.convert(null, List.class));
        ObjectConverter cvt2 = ObjectConverter
            .newConverter(new ProtobufConvertHandler());
        expectThrows(UnsupportedObjectConvertException.class, () ->
            cvt2.convert(str, ByteString.class));
        expectThrows(UnsupportedObjectConvertException.class, () ->
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
