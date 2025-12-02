package internal.tests.benchmarks;

import internal.tests.api.Invoker;
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

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class InvokerBenchmark {

    @Param({
        "byReflect",
        "byAsm",
        "byMethodHandle",
        "direct",
    })
    private String invokeType;

    @Param({
        "static",
        "instance",
    })
    private String methodType;

    private Supplier<Object> action;

    @Setup(Level.Trial)
    public void setup() {
        this.action = Invoker.createAction(invokeType, methodType);
    }

    @Benchmark
    public void invoke(Blackhole blackhole) throws Exception {
        Object value = action.get();
        blackhole.consume(value);
    }
}
