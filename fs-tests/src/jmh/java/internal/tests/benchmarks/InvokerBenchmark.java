package internal.tests.benchmarks;

import internal.tests.common.Invoker;
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
import space.sunqian.common.runtime.invoke.Invocable;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class InvokerBenchmark {

    @Param({
        "reflect",
        "asm",
        "method_handle",
        "original",
    })
    private String invokeType;

    @Param({
        "static",
        "instance",
    })
    private String methodType;

    private Invocable invocable;

    @Setup(Level.Trial)
    public void setup() {
        this.invocable = Invoker.createInvocable(invokeType, methodType);
    }

    @Benchmark
    public void invoke(Blackhole blackhole) throws Exception {
        Object value = Invoker.createAction(invokeType, methodType).get();
        blackhole.consume(value);
    }
}
