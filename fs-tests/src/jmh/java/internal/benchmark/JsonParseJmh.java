package internal.benchmark;

import internal.api.JsonParseApi;
import internal.data.DataGenerator;
import internal.data.TestJsonData;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.fs.data.json.JsonKit;
import space.sunqian.fs.io.IOKit;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonParseJmh extends AbstractJmhBenchmark {

    private final TestJsonData data = DataGenerator.createJsonParseTestData();
    private final String dataJson;
    private final byte[] dataJsonBytes;
    private final Map<String, Object> map = DataGenerator.createJsonParseTestMap();
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
        this.dataJson = JsonKit.toJsonString(data);
        this.dataJsonBytes = dataJson.getBytes(StandardCharsets.UTF_8);
        // map
        this.mapJson = JsonKit.toJsonString(map);
        this.mapJsonBytes = mapJson.getBytes(StandardCharsets.UTF_8);
    }

    @Setup(Level.Trial)
    public void setup() throws Exception {
        this.jsonParseApi = JsonParseApi.createApi(parseType);
    }

    // @Benchmark
    // public void parseStringJson(Blackhole blackhole) throws Exception {
    //     if ("object".equals(parseType)) {
    //         Object data = jsonParseApi.parse(dataJson, TestJsonData.class);
    //         blackhole.consume(data);
    //     } else {
    //         Object map = jsonParseApi.parse(mapJson, Map.class);
    //         blackhole.consume(map);
    //     }
    // }

    @Benchmark
    public void parseInputJson(Blackhole blackhole) throws Exception {
        if ("object".equals(parseType)) {
            // dataJsonInput.reset();
            Object data = jsonParseApi.parse(IOKit.newInputStream(dataJsonBytes), TestJsonData.class);
            blackhole.consume(data);
        } else {
            // mapJsonInput.reset();
            Object map = jsonParseApi.parse(IOKit.newInputStream(mapJsonBytes), Map.class);
            blackhole.consume(map);
        }
    }
}
