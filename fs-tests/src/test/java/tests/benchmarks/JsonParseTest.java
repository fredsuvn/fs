package tests.benchmarks;

import internal.api.JsonParseApi;
import internal.data.DataGenerator;
import internal.data.TestJsonData;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.data.json.JsonKit;
import space.sunqian.fs.io.IOKit;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonParseTest {

    private final TestJsonData data = DataGenerator.createJsonParseTestData();
    private final String dataJson;
    private final byte[] dataJsonBytes;

    {
        // object
        this.dataJson = JsonKit.toJsonString(data);
        this.dataJsonBytes = dataJson.getBytes(StandardCharsets.UTF_8);
        TestJsonData parsed = JsonKit.parse(dataJson).toObject(TestJsonData.class);
        assertEquals(data, parsed);
    }

    @Test
    public void testJsonParseWithDifferentImplementations() throws Exception {
        testJsonParseImplementation("fs");
        testJsonParseImplementation("jackson");
        testJsonParseImplementation("fastjson");
    }

    private void testJsonParseImplementation(String parseType) throws Exception {
        JsonParseApi parseApi = JsonParseApi.createApi(parseType);
        Object stringParsed = parseApi.parse(dataJson, TestJsonData.class);
        assertEquals(data, stringParsed);
        Object inputParsed = parseApi.parse(IOKit.newInputStream(dataJsonBytes), TestJsonData.class);
        assertEquals(data, inputParsed);
        Object readerParsed = parseApi.parse(new StringReader(dataJson), TestJsonData.class);
        assertEquals(data, readerParsed);
    }
}
