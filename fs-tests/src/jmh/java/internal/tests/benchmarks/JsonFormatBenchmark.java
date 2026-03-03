package internal.tests.benchmarks;

import internal.tests.api.JsonFormatApi;
import internal.tests.common.TestPropsData;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class JsonFormatBenchmark {

    @Param({
        "fs",
        "jackson",
        "fastjson",
    })
    private String formatType;

    @Param({
        "object",
        "map",
    })
    private String formatTarget;

    private JsonFormatApi jsomFormat;

    private final TestPropsData data = new TestPropsData();
    private final Map<String, Object> map = new LinkedHashMap<>();

    {
        // object
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
        // map
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
    }

    @Setup(Level.Trial)
    public void setup() throws Exception {
        this.jsomFormat = JsonFormatApi.createFormat(formatType);
    }

    @Benchmark
    public void aspect(Blackhole blackhole) throws Exception {
        String json = "object".equals(formatTarget) ?
            jsomFormat.toJsonString(data) : jsomFormat.toJsonString(map);
        blackhole.consume(json);
    }
}
