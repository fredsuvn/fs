package internal.tests.benchmarks;

import internal.tests.api.AspectApi;
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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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
public class AspectBenchmark {

    @Param({
        "asm",
        "direct",
    })
    private String aspectType;

    @Param({
        "true",
        "false",
    })
    private String withPrimitive;

    private AspectApi aspect;

    @Setup(Level.Trial)
    public void setup() {
        this.aspect = AspectApi.createAspect(aspectType);
    }

    @Benchmark
    public void aspect(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        String value = "true".equals(withPrimitive) ?
            aspect.withPrimitive(random.nextInt(), random.nextLong(), "hello")
            :
            aspect.withoutPrimitive(random.nextInt(), random.nextLong(), "hello");
        blackhole.consume(value);
    }
}
