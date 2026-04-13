package tests.benchmarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import internal.api.JsonFormatApi;
import internal.data.TestJsonData;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFormatTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testFormat() throws Exception {
        testFormat("fs");
        testFormat("jackson");
        testFormat("fastjson");
    }

    private void testFormat(String formatType) throws Exception {
        JsonFormatApi formatApi = JsonFormatApi.createApi(formatType);
        // object
        TestJsonData data = new TestJsonData();
        data.setI1(1);
        data.setL1(2L);
        data.setStr1("hello");
        data.setIi1(3);
        data.setLl1(4L);
        data.setBb1(new BigDecimal("5.0"));
        data.setLa1(new long[]{1L, 2L});
        data.setBa1(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        data.setSa1(ListKit.list("a", "b"));
        data.setI2(1);
        data.setL2(2L);
        data.setStr2("hello");
        data.setIi2(3);
        data.setLl2(4L);
        data.setBb2(new BigDecimal("5.0"));
        data.setLa2(new long[]{1L, 2L});
        data.setBa2(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        data.setSa2(ListKit.list("a", "b"));
        data.setI3(1);
        data.setL3(2L);
        data.setStr3("hello");
        data.setIi3(3);
        data.setLl3(4L);
        data.setBb3(new BigDecimal("5.0"));
        data.setLa3(new long[]{1L, 2L});
        data.setBa3(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        data.setSa3(ListKit.list("a", "b"));
        String json = formatApi.toJsonString(data);
        TestJsonData parsed = mapper.readValue(json, TestJsonData.class);
        assertEquals(data, parsed);
        // map
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("i1", 1);
        map.put("l1", 2L);
        map.put("str1", "hello");
        map.put("ii1", 3);
        map.put("ll1", 4L);
        map.put("bb1", new BigDecimal("5.0"));
        map.put("la1", new long[]{1L, 2L});
        map.put("ba1", new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        map.put("sa1", ListKit.list("a", "b"));
        map.put("i2", 1);
        map.put("l2", 2L);
        map.put("str2", "hello");
        map.put("ii2", 3);
        map.put("ll2", 4L);
        map.put("bb2", new BigDecimal("5.0"));
        map.put("la2", new long[]{1L, 2L});
        map.put("ba2", new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        map.put("sa2", ListKit.list("a", "b"));
        map.put("i3", 1);
        map.put("l3", 2L);
        map.put("str3", "hello");
        map.put("ii3", 3);
        map.put("ll3", 4L);
        map.put("bb3", new BigDecimal("5.0"));
        map.put("la3", new long[]{1L, 2L});
        map.put("ba3", new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        map.put("sa3", ListKit.list("a", "b"));
        String json2 = formatApi.toJsonString(map);
        assertEquals(mapper.writeValueAsString(map), json2);
    }
}
