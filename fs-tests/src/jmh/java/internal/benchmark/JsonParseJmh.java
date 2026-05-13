package internal.benchmark;

import internal.api.JsonParseApi;
import internal.data.TestJsonData;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.data.json.JsonKit;
import space.sunqian.fs.io.IOKit;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonParseJmh extends AbstractJmhBenchmark {

    private final TestJsonData data = new TestJsonData();
    private final Map<String, Object> map = new LinkedHashMap<>();
    private final String dataJson;
    private final byte[] dataJsonBytes;
    private final String mapJson;
    private final byte[] mapJsonBytes;

    @Param({
        "fs",
        "jackson",
        //"fastjson",
    })
    private String parseType;
    @Param({
        "object",
        "map",
    })
    private String parseTarget;
    private JsonParseApi jsonParseApi;

    {
        // object
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
        this.dataJson = JsonKit.toJsonString(data);
        this.dataJsonBytes = dataJson.getBytes(StandardCharsets.UTF_8);
        // map
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
        this.mapJson = JsonKit.toJsonString(map);
        this.mapJsonBytes = mapJson.getBytes(StandardCharsets.UTF_8);
    }

    @Setup(Level.Trial)
    public void setup() throws Exception {
        this.jsonParseApi = JsonParseApi.createApi(parseType);
    }

    @Benchmark
    public void parseStringJson(Blackhole blackhole) throws Exception {
        if ("object".equals(parseType)) {
            Object data = jsonParseApi.parse(dataJson, TestJsonData.class);
            blackhole.consume(data);
        } else {
            Object map = jsonParseApi.parse(mapJson, Map.class);
            blackhole.consume(map);
        }
    }

    // @Benchmark
    // public void parseInputJson(Blackhole blackhole) throws Exception {
    //     if ("object".equals(parseType)) {
    //         // dataJsonInput.reset();
    //         Object data = jsonParseApi.parse(IOKit.newInputStream(dataJsonBytes), TestJsonData.class);
    //         blackhole.consume(data);
    //     } else {
    //         // mapJsonInput.reset();
    //         Object map = jsonParseApi.parse(IOKit.newInputStream(mapJsonBytes), Map.class);
    //         blackhole.consume(map);
    //     }
    // }
}
