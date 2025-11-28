package internal.tests.benchmarks;

import internal.tests.common.TestPropsData;
import internal.tests.api.PropertiesCopier;
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
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
// @Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
// @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
// @Fork(5)
public class CopyPropertiesBenchmark {

    @Param({
        "fs",
        "apache",
        "hutool",
        //"direct"
    })
    private String copierType;

    private PropertiesCopier copier;

    private final TestPropsData data = new TestPropsData();

    {
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
    }

    @Setup(Level.Trial)
    public void setup() {
        this.copier = PropertiesCopier.createCopier(copierType);
    }

    @Benchmark
    public void copyProperties(Blackhole blackhole) throws Exception {
        TestPropsData copy = new TestPropsData();
        copier.copyProperties(data, copy);
        blackhole.consume(copy);
    }
}
