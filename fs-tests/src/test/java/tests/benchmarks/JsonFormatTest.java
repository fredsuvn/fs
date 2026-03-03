package tests.benchmarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import internal.tests.api.JsonFormatApi;
import internal.tests.common.TestPropsData;
import org.junit.jupiter.api.Test;

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
        JsonFormatApi formatApi = JsonFormatApi.createFormat(formatType);
        // object
        TestPropsData data = new TestPropsData();
        data.setI1(1);
        data.setL1(2L);
        data.setStr1("hello");
        data.setIi1(3);
        data.setLl1(4L);
        data.setBb1(new BigDecimal("5.0"));
        data.setI2(1);
        data.setL2(2L);
        data.setStr2("hello");
        data.setIi2(3);
        data.setLl2(4L);
        data.setBb2(new BigDecimal("5.0"));
        String json = formatApi.toJsonString(data);
        TestPropsData parsed = mapper.readValue(json, TestPropsData.class);
        assertEquals(data, parsed);
        // map
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("i1", 1);
        map.put("l1", 2L);
        map.put("str1", "hello");
        map.put("ii1", 3);
        map.put("ll1", 4L);
        map.put("bb1", new BigDecimal("5.0"));
        map.put("i2", 1);
        map.put("l2", 2L);
        map.put("str2", "hello");
        map.put("ii2", 3);
        map.put("ll2", 4L);
        map.put("bb2", new BigDecimal("5.0"));
        String json2 = formatApi.toJsonString(map);
        assertEquals(mapper.writeValueAsString(map), json2);
    }
}
