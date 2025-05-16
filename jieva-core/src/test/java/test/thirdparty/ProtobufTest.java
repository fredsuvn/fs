//package test;
//
//import com.google.protobuf.ByteString;
//import lombok.EqualsAndHashCode;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import test.protobuf.Data;
//import test.protobuf.Enum;
//import test.protobuf.Request;
//import xyz.sunqian.common.base.Jie;
//import xyz.sunqian.common.bean.BeanInfo;
//import xyz.sunqian.common.bean.BeanResolver;
//import xyz.sunqian.common.bean.PropertyInfo;
//import xyz.sunqian.common.data.protobuf.JieProtobuf;
//import xyz.sunqian.common.mapping.Mapper;
//import xyz.sunqian.common.reflect.TypeRef;
//
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//public class ProtobufTest {
//
//    @Test
//    public void testBean() {
//        /*
//        message Data {
//            Enum em = 1;
//            string str = 2;
//            int64 num = 3;
//            repeated string text = 4;
//            map<string, string> entry = 5;
//        }
//         */
//        BeanResolver resolver = JieProtobuf.defaultBeanResolver();
//        BeanInfo dataBean = resolver.resolve(Data.class);
//        Map<String, PropertyInfo> propertyMap = dataBean.getProperties();
//        Assert.assertEquals(propertyMap.size(), 14);
//        Assert.assertEquals(propertyMap.get("em").getType(), Enum.class);
//        Assert.assertEquals(propertyMap.get("str").getType(), String.class);
//        Assert.assertEquals(propertyMap.get("num").getType(), long.class);
//        Assert.assertEquals(propertyMap.get("textList").getType(), new TypeRef<List<String>>() {
//        }.getType());
//        Assert.assertEquals(propertyMap.get("entryMap").getType(), new TypeRef<Map<String, String>>() {
//        }.getType());
//        Assert.assertFalse(propertyMap.get("em").isWriteable());
//        Assert.assertFalse(propertyMap.get("str").isWriteable());
//        Assert.assertFalse(propertyMap.get("num").isWriteable());
//        Assert.assertFalse(propertyMap.get("textList").isWriteable());
//        Assert.assertFalse(propertyMap.get("entryMap").isWriteable());
//
//        BeanInfo dataBuilderBean = resolver.resolve(Data.newBuilder().getClass());
//        Map<String, PropertyInfo> builderPropertyMap = dataBuilderBean.getProperties();
//        Assert.assertEquals(builderPropertyMap.size(), 14);
//        Assert.assertEquals(builderPropertyMap.get("em").getType(), Enum.class);
//        Assert.assertEquals(builderPropertyMap.get("str").getType(), String.class);
//        Assert.assertEquals(builderPropertyMap.get("num").getType(), long.class);
//        Assert.assertEquals(builderPropertyMap.get("textList").getType(), new TypeRef<List<String>>() {
//        }.getType());
//        Assert.assertEquals(builderPropertyMap.get("entryMap").getType(), new TypeRef<Map<String, String>>() {
//        }.getType());
//        Assert.assertTrue(builderPropertyMap.get("em").isWriteable());
//        Assert.assertTrue(builderPropertyMap.get("str").isWriteable());
//        Assert.assertTrue(builderPropertyMap.get("num").isWriteable());
//        Assert.assertTrue(builderPropertyMap.get("textList").isWriteable());
//        Assert.assertTrue(builderPropertyMap.get("entryMap").isWriteable());
//
//        Data.Builder dataBuilder = Data.newBuilder()
//            .setEm(Enum.E2)
//            .setStr("888")
//            .setNum(999)
//            .addAllText(Arrays.asList("2", "4", "6"))
//            .putAllEntry(Jie.newMap("1", "1", "3", "3"));
//        Data data = dataBuilder.build();
//        Assert.assertEquals(propertyMap.get("em").getValue(data), Enum.E2);
//        Assert.assertEquals(propertyMap.get("str").getValue(data), "888");
//        Assert.assertEquals(propertyMap.get("num").getValue(data), 999L);
//        Assert.assertEquals(propertyMap.get("textList").getValue(data), Arrays.asList("2", "4", "6"));
//        Assert.assertEquals(propertyMap.get("entryMap").getValue(data), Jie.newMap("1", "1", "3", "3"));
//        Assert.assertEquals(builderPropertyMap.get("em").getValue(dataBuilder), Enum.E2);
//        Assert.assertEquals(builderPropertyMap.get("str").getValue(dataBuilder), "888");
//        Assert.assertEquals(builderPropertyMap.get("num").getValue(dataBuilder), 999L);
//        Assert.assertEquals(builderPropertyMap.get("textList").getValue(dataBuilder), Arrays.asList("2", "4", "6"));
//        Assert.assertEquals(builderPropertyMap.get("entryMap").getValue(dataBuilder), Jie.newMap("1", "1", "3", "3"));
//        builderPropertyMap.get("em").setValue(dataBuilder, Enum.E1);
//        builderPropertyMap.get("str").setValue(dataBuilder, "777");
//        builderPropertyMap.get("num").setValue(dataBuilder, 888L);
//        builderPropertyMap.get("textList").setValue(dataBuilder, Arrays.asList("3", "5", "7"));
//        builderPropertyMap.get("entryMap").setValue(dataBuilder, Jie.newMap("8", "8", "9", "9"));
//        Data data2 = dataBuilder.build();
//        Assert.assertEquals(propertyMap.get("em").getValue(data2), Enum.E1);
//        Assert.assertEquals(propertyMap.get("str").getValue(data2), "777");
//        Assert.assertEquals(propertyMap.get("num").getValue(data2), 888L);
//        Assert.assertEquals(propertyMap.get("textList").getValue(data2), Arrays.asList("3", "5", "7"));
//        Assert.assertEquals(propertyMap.get("entryMap").getValue(data2), Jie.newMap("8", "8", "9", "9"));
//    }
//
//    @Test
//    public void testConvertBean() {
//        DataDto dataDto = new DataDto();
//        dataDto.setEm(EnumDto.E2);
//        dataDto.setStr("dataDto");
//        dataDto.setTextList(Arrays.asList("11", "22", "33"));
//        dataDto.setNum(777);
//        dataDto.setBytes("123".getBytes());
//        dataDto.setEntryMap(Jie.newMap("88", "99"));
//        dataDto.setFixed32(132);
//        dataDto.setFixed64(164);
//        dataDto.setSfixed32(232);
//        dataDto.setSfixed64(264);
//        dataDto.setUint32(332);
//        dataDto.setUint64(364);
//        dataDto.setSint32(432);
//        dataDto.setSint64(464);
//        RequestDto requestDto = new RequestDto();
//        requestDto.setCode(111);
//        requestDto.setMessage("111");
//        requestDto.setData(dataDto);
//
//        //dto -> protobuf
//        ResponseDto responseDto = JieProtobuf.defaultMapper().map(requestDto, ResponseDto.class);
//        Assert.assertEquals(responseDto.getCode(), "111");
//        Assert.assertEquals(responseDto.getMessage(), 111L);
//        Data data = responseDto.getData();
//        Assert.assertEquals(data.getEm(), Enum.E2);
//        Assert.assertEquals(data.getStr(), dataDto.getStr());
//        Assert.assertEquals(data.getTextList(), dataDto.getTextList());
//        Assert.assertEquals(data.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
//        Assert.assertEquals(data.getEntryMap(), dataDto.getEntryMap());
//        Assert.assertEquals(data.getFixed32(), dataDto.getFixed32());
//        Assert.assertEquals(data.getFixed64(), dataDto.getFixed64());
//        Assert.assertEquals(data.getSfixed32(), dataDto.getSfixed32());
//        Assert.assertEquals(data.getSfixed64(), dataDto.getSfixed64());
//        Assert.assertEquals(data.getUint32(), dataDto.getUint32());
//        Assert.assertEquals(data.getUint64(), dataDto.getUint64());
//        Assert.assertEquals(data.getSint32(), dataDto.getSint32());
//        Assert.assertEquals(data.getSint64(), dataDto.getSint64());
//
//        //dto -> protobuf builder
//        Data.Builder dataBuilder = JieProtobuf.defaultMapper().map(requestDto.getData(), Data.Builder.class);
//        Assert.assertEquals(dataBuilder.getEm(), Enum.E2);
//        Assert.assertEquals(dataBuilder.getStr(), dataDto.getStr());
//        Assert.assertEquals(dataBuilder.getTextList(), dataDto.getTextList());
//        Assert.assertEquals(dataBuilder.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
//        Assert.assertEquals(dataBuilder.getEntryMap(), dataDto.getEntryMap());
//        Assert.assertEquals(dataBuilder.getFixed32(), dataDto.getFixed32());
//        Assert.assertEquals(dataBuilder.getFixed64(), dataDto.getFixed64());
//        Assert.assertEquals(dataBuilder.getSfixed32(), dataDto.getSfixed32());
//        Assert.assertEquals(dataBuilder.getSfixed64(), dataDto.getSfixed64());
//        Assert.assertEquals(dataBuilder.getUint32(), dataDto.getUint32());
//        Assert.assertEquals(dataBuilder.getUint64(), dataDto.getUint64());
//        Assert.assertEquals(dataBuilder.getSint32(), dataDto.getSint32());
//        Assert.assertEquals(dataBuilder.getSint64(), dataDto.getSint64());
//
//        //protobuf -> dto
//        DataDto dataDto1 = JieProtobuf.defaultMapper().map(data, DataDto.class);
//        Assert.assertEquals(data.getEm(), Enum.E2);
//        Assert.assertEquals(data.getStr(), dataDto.getStr());
//        Assert.assertEquals(data.getTextList(), dataDto.getTextList());
//        Assert.assertEquals(data.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
//        Assert.assertEquals(data.getEntryMap(), dataDto.getEntryMap());
//        Assert.assertEquals(data.getFixed32(), dataDto.getFixed32());
//        Assert.assertEquals(data.getFixed64(), dataDto.getFixed64());
//        Assert.assertEquals(data.getSfixed32(), dataDto.getSfixed32());
//        Assert.assertEquals(data.getSfixed64(), dataDto.getSfixed64());
//        Assert.assertEquals(data.getUint32(), dataDto.getUint32());
//        Assert.assertEquals(data.getUint64(), dataDto.getUint64());
//        Assert.assertEquals(data.getSint32(), dataDto.getSint32());
//        Assert.assertEquals(data.getSint64(), dataDto.getSint64());
//
//        //protobuf builder -> dto
//        DataDto dataDto2 = JieProtobuf.defaultMapper().map(dataBuilder, DataDto.class);
//        Assert.assertEquals(dataBuilder.getEm(), Enum.E2);
//        Assert.assertEquals(dataBuilder.getStr(), dataDto.getStr());
//        Assert.assertEquals(dataBuilder.getTextList(), dataDto.getTextList());
//        Assert.assertEquals(dataBuilder.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
//        Assert.assertEquals(dataBuilder.getEntryMap(), dataDto.getEntryMap());
//        Assert.assertEquals(dataBuilder.getFixed32(), dataDto.getFixed32());
//        Assert.assertEquals(dataBuilder.getFixed64(), dataDto.getFixed64());
//        Assert.assertEquals(dataBuilder.getSfixed32(), dataDto.getSfixed32());
//        Assert.assertEquals(dataBuilder.getSfixed64(), dataDto.getSfixed64());
//        Assert.assertEquals(dataBuilder.getUint32(), dataDto.getUint32());
//        Assert.assertEquals(dataBuilder.getUint64(), dataDto.getUint64());
//        Assert.assertEquals(dataBuilder.getSint32(), dataDto.getSint32());
//        Assert.assertEquals(dataBuilder.getSint64(), dataDto.getSint64());
//
//        //protobuf -> protobuf
//        Data.Builder dataBuilder1 = JieProtobuf.defaultMapper().map(data, Data.Builder.class);
//        Assert.assertEquals(dataBuilder1.build(), dataBuilder.build());
//        Data data1 = JieProtobuf.defaultMapper().map(dataBuilder1, Data.class);
//        Assert.assertEquals(data1, dataBuilder1.build());
//
//        Request request = Request.newBuilder()
//            .setCode(3).setMessage(ByteString.copyFromUtf8("xxxx")).setData(data).build();
//        RequestDto requestDto1 = JieProtobuf.defaultMapper().map(request, RequestDto.class);
//        Assert.assertEquals(requestDto1.getCode(), request.getCode());
//        Assert.assertEquals(requestDto1.getMessage(), request.getMessage().toStringUtf8());
//        Assert.assertEquals(requestDto1.getData(), dataDto);
//    }
//
//    @Test
//    public void testConvert() {
//        byte[] bytes = {1, 2, 3, 4};
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        ByteString bs = ByteString.copyFrom(bytes);
//        String str = bs.toStringUtf8();
//        Mapper converter = JieProtobuf.defaultMapper();
//        Assert.assertEquals(converter.map(bytes, ByteBuffer.class), buffer.slice());
//        Assert.assertEquals(converter.map(bs, ByteBuffer.class), buffer.slice());
//        Assert.assertEquals(converter.map(buffer, ByteBuffer.class), buffer.slice());
//        Assert.assertEquals(converter.map(bytes, byte[].class), bytes);
//        Assert.assertEquals(converter.map(bs, byte[].class), bytes);
//        Assert.assertEquals(converter.map(buffer, byte[].class), bytes);
//        Assert.assertEquals(converter.map(bytes, ByteString.class), bs);
//        Assert.assertEquals(converter.map(bs, ByteString.class), bs);
//        Assert.assertEquals(converter.map(buffer, ByteString.class), bs);
//        Assert.assertEquals(converter.map(bs, String.class), str);
//        Assert.assertEquals(converter.map(str, ByteString.class), bs);
//    }
//
//    public enum EnumDto {
//        E1, E2;
//    }
//
//    @lombok.Data
//    @EqualsAndHashCode
//    public static class DataDto {
//        private EnumDto em;
//        private String str;
//        private long num;
//        private List<String> textList;
//        private Map<String, String> entryMap;
//        private int uint32;
//        private long uint64;
//        private int fixed32;
//        private long fixed64;
//        private int sfixed32;
//        private long sfixed64;
//        private int sint32;
//        private long sint64;
//        private byte[] bytes;
//    }
//
//    @lombok.Data
//    @EqualsAndHashCode
//    public static class RequestDto {
//        private int code;
//        private String message;
//        private DataDto data;
//    }
//
//    @lombok.Data
//    @EqualsAndHashCode
//    public static class ResponseDto {
//        private String code;
//        private long message;
//        private Data data;
//    }
//}
