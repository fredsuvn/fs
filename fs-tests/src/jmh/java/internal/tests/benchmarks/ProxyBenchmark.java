package internal.tests.benchmarks;

import internal.tests.common.ProxyApi;
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
public class ProxyBenchmark {

    @Param({
        "asm",
        "jdk",
        "direct",
    })
    private String proxyType;

    @Param({
        "true",
        "false",
    })
    private String withPrimitive;

    private ProxyApi proxy;

    @Setup(Level.Trial)
    public void setup() {
        this.proxy = ProxyApi.createProxy(proxyType);
    }

    @Benchmark
    public void proxy(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        String value = "true".equals(withPrimitive) ?
            proxy.withPrimitive(random.nextInt(), random.nextLong(), "hello")
            :
            proxy.withoutPrimitive(random.nextInt(), random.nextLong(), "hello");
        blackhole.consume(value);
    }
}
