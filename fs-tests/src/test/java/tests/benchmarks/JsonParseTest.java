package tests.benchmarks;

import internal.api.JsonParseApi;
import internal.data.TestJsonData;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.data.json.JsonKit;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonParseTest {

    private final TestJsonData testData;
    private final String testJson;

    {
        testData = createTestData();
        testJson = JsonKit.toJsonString(testData);

        // Verify initial serialization/deserialization works
        TestJsonData parsed = JsonKit.parse(testJson).toObject(TestJsonData.class);
        assertEquals(testData, parsed);
    }

    @Test
    public void testJsonParseWithDifferentImplementations() throws Exception {
        testJsonParseImplementation("fs");
        testJsonParseImplementation("jackson");
        testJsonParseImplementation("fastjson");
    }

    private void testJsonParseImplementation(String parseType) throws Exception {
        JsonParseApi parseApi = JsonParseApi.createApi(parseType);
        Object parsed = parseApi.parse(testJson, TestJsonData.class);
        assertEquals(testData, parsed);
    }

    private TestJsonData createTestData() {
        TestJsonData data = new TestJsonData();

        // Set properties for test data
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

        return data;
    }
}
