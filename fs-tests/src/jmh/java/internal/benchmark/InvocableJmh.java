package internal.benchmark;

import internal.api.Invoker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.function.Supplier;

public class InvocableJmh extends AbstractJmhBenchmark {

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

    private Supplier<Object> supplier;

    @Setup(Level.Trial)
    public void setup() {
        this.supplier = Invoker.createSupplier(invokeType, methodType);
    }

    @Benchmark
    public void invoke(Blackhole blackhole) throws Exception {
        Object value = supplier.get();
        blackhole.consume(value);
    }
}
