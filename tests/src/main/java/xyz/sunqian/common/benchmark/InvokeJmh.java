package xyz.sunqian.common.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.invoke.InvocationMode;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InvokeJmh {

    private static final Method staticWorld;
    private static final Method instanceWorld;

    static {
        try {
            staticWorld = Hello.class.getMethod("staticWorld", String.class);
            instanceWorld = Hello.class.getMethod("instanceWorld", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Hello hello = new Hello();
    private static final Invocable reflectStatic = Invocable.of(staticWorld, InvocationMode.REFLECT);
    private static final Invocable methodHandleStatic = Invocable.of(staticWorld, InvocationMode.METHOD_HANDLE);
    private static final Invocable asmStatic = Invocable.of(staticWorld, InvocationMode.ASM);
    private static final Invocable reflectInstance = Invocable.of(instanceWorld, InvocationMode.REFLECT);
    private static final Invocable methodHandleInstance = Invocable.of(instanceWorld, InvocationMode.METHOD_HANDLE);
    private static final Invocable asmInstance = Invocable.of(instanceWorld, InvocationMode.ASM);

    @Benchmark
    public void reflectStatic() {
        reflectStatic.invoke(null, "hi");
    }

    @Benchmark
    public void methodHandleStatic() {
        methodHandleStatic.invoke(null, "hi");
    }

    @Benchmark
    public void asmStatic() {
        asmStatic.invoke(null, "hi");
    }

    @Benchmark
    public void reflectInstance() {
        reflectInstance.invoke(hello, "hi");
    }

    @Benchmark
    public void methodHandleInstance() {
        methodHandleInstance.invoke(hello, "hi");
    }

    @Benchmark
    public void asmInstance() {
        asmInstance.invoke(hello, "hi");
    }

    @Benchmark
    public void directStatic() {
        Hello.staticWorld("hi");
    }

    @Benchmark
    public void directInstance() {
        hello.instanceWorld("hi");
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // # JMH version: 1.37
    // # VM version: JDK 1.8.0_432-432, OpenJDK 64-Bit Server VM, 25.432-b06
    // # VM invoker: C:\Opt\jdk\jdk8\jre\bin\java.exe
    // # VM options: -Dfile.encoding=UTF-8 -Duser.country=CN -Duser.language=zh -Duser.variant
    // Result "xyz.sunqian.common.benchmark.InvokeJmh.reflectStatic":
    //   317078.316 ±(99.9%) 7084.398 ops/ms [Average]
    //   (min, avg, max) = (306808.502, 317078.316, 325064.227), stdev = 6626.750
    //   CI (99.9%): [309993.918, 324162.713] (assumes normal distribution)
    //
    //
    // # Run complete. Total time: 00:20:22
    //
    // Benchmark                        Mode  Cnt        Score        Error   Units
    // InvokeJmh.asmInstance           thrpt   15  4627435.527 ± 125328.477  ops/ms
    // InvokeJmh.asmStatic             thrpt   15  4693775.248 ±  78617.194  ops/ms
    // InvokeJmh.directInstance        thrpt   15  4661974.736 ±  78244.680  ops/ms
    // InvokeJmh.directStatic          thrpt   15  4684965.493 ±  49193.604  ops/ms
    // InvokeJmh.methodHandleInstance  thrpt   15    29070.758 ±    701.008  ops/ms
    // InvokeJmh.methodHandleStatic    thrpt   15    30303.781 ±    438.603  ops/ms
    // InvokeJmh.reflectInstance       thrpt   15   321828.365 ±   4206.796  ops/ms
    // InvokeJmh.reflectStatic         thrpt   15   317078.316 ±   7084.398  ops/ms

    // # JMH version: 1.37
    // # VM version: JDK 17.0.13, OpenJDK 64-Bit Server VM, 17.0.13+11-adhoc..jdk17u
    // # VM invoker: C:\Opt\jdk\jdk17\bin\java.exe
    // # VM options: -Dfile.encoding=UTF-8 -Duser.country=CN -Duser.language=zh -Duser.variant
    //
    // Result "xyz.sunqian.common.benchmark.InvokeJmh.reflectStatic":
    //   313692.060 ±(99.9%) 7592.329 ops/ms [Average]
    //   (min, avg, max) = (295199.506, 313692.060, 318691.181), stdev = 7101.869
    //   CI (99.9%): [306099.731, 321284.388] (assumes normal distribution)
    //
    //
    // # Run complete. Total time: 00:20:20
    //
    // REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
    // why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
    // experiments, perform baseline and negative tests that provide experimental control, make sure
    // the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
    // Do not assume the numbers tell you what you want them to tell.
    //
    // NOTE: Current JVM experimentally supports Compiler Blackholes, and they are in use. Please exercise
    // extra caution when trusting the results, look into the generated code to check the benchmark still
    // works, and factor in a small probability of new VM bugs. Additionally, while comparisons between
    // different JVMs are already problematic, the performance difference caused by different Blackhole
    // modes can be very significant. Please make sure you use the consistent Blackhole mode for comparisons.
    //
    // Benchmark                        Mode  Cnt        Score       Error   Units
    // InvokeJmh.asmInstance           thrpt   15  3017270.724 ± 74150.507  ops/ms
    // InvokeJmh.asmStatic             thrpt   15  3113988.882 ± 45444.219  ops/ms
    // InvokeJmh.directInstance        thrpt   15  3091892.775 ± 62012.541  ops/ms
    // InvokeJmh.directStatic          thrpt   15  3107662.781 ± 44048.221  ops/ms
    // InvokeJmh.methodHandleInstance  thrpt   15    27322.783 ±  1132.257  ops/ms
    // InvokeJmh.methodHandleStatic    thrpt   15    29368.489 ±   609.007  ops/ms
    // InvokeJmh.reflectInstance       thrpt   15   288523.617 ±  4247.338  ops/ms
    // InvokeJmh.reflectStatic         thrpt   15   313692.060 ±  7592.329  ops/ms
}
