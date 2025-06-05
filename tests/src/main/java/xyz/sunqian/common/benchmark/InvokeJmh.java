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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(5)
// @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InvokeJmh {

    private static final Method staticWorld;
    private static final Method instanceWorld;

    static {
        try {
            staticWorld = Hello.class.getMethod("staticWorld", String.class, int.class, double.class);
            instanceWorld = Hello.class.getMethod("instanceWorld", String.class, int.class, double.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Hello hello = new Hello();
    private static final Invocable reflectStatic = Invocable.of(staticWorld, InvocationMode.REFLECTION);
    private static final Invocable methodHandleStatic = Invocable.of(staticWorld, InvocationMode.METHOD_HANDLE);
    private static final Invocable asmStatic = Invocable.of(staticWorld, InvocationMode.ASM);
    private static final Invocable reflectInstance = Invocable.of(instanceWorld, InvocationMode.REFLECTION);
    private static final Invocable methodHandleInstance = Invocable.of(instanceWorld, InvocationMode.METHOD_HANDLE);
    private static final Invocable asmInstance = Invocable.of(instanceWorld, InvocationMode.ASM);
    private static final MethodHandle handleStatic;
    private static final MethodHandle handleInstance;
    private static final MethodHandle handleInstanceAsType;
    private static final MethodHandle handleInstanceBind;

    static {
        try {
            handleStatic = MethodHandles.lookup().unreflect(staticWorld);
            handleInstance = MethodHandles.lookup().unreflect(instanceWorld);
            handleInstanceAsType = MethodHandles.lookup().unreflect(instanceWorld)
                .asType(MethodType.methodType(String.class, new Class<?>[]{Hello.class, String.class, int.class, double.class}));
            handleInstanceBind = MethodHandles.lookup().unreflect(instanceWorld)
                //.asType(MethodType.methodType(String.class, new Class<?>[]{Hello.class, String.class, int.class, double.class}))
                .bindTo(hello);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void reflectStatic() {
        String result = (String) reflectStatic.invoke(null, "hi", 1, 1.0);
    }

    @Benchmark
    public void methodHandleStatic() {
        String result = (String) methodHandleStatic.invoke(null, "hi", 1, 1.0);
    }

    @Benchmark
    public void asmStatic() {
        String result = (String) asmStatic.invoke(null, "hi", 1, 1.0);
    }

    @Benchmark
    public void reflectInstance() {
        String result = (String) reflectInstance.invoke(hello, "hi", 1, 1.0);
    }

    @Benchmark
    public void methodHandleInstance() {
        String result = (String) methodHandleInstance.invoke(hello, "hi", 1, 1.0);
    }

    @Benchmark
    public void asmInstance() {
        String result = (String) asmInstance.invoke(hello, "hi", 1, 1.0);
    }

    @Benchmark
    public void directStatic() {
        String result = Hello.staticWorld("hi", 1, 1.0);
    }

    @Benchmark
    public void directInstance() {
        String result = hello.instanceWorld("hi", 1, 1.0);
    }

    @Benchmark
    public void methodHandleExactStatic() throws Throwable {
        String result = (String) handleStatic.invokeExact("hi", 1, 1.0);
    }

    @Benchmark
    public void methodHandleExactInstance() throws Throwable {
        String result = (String) handleInstance.invokeExact(hello, "hi", 1, 1.0);
    }

    @Benchmark
    public void methodHandleAsTypeInstance() throws Throwable {
        String result = (String) handleInstanceAsType.invoke(hello, "hi", 1, 1.0);
    }

    @Benchmark
    public void methodHandleBindInstance() throws Throwable {
        String result = (String) handleInstanceBind.invoke("hi", 1, 1.0);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // Benchmark                              Mode  Cnt        Score        Error   Units
    // InvokeJmh.asmInstance                 thrpt   15  4155501.016 ±  90799.114  ops/ms
    // InvokeJmh.asmStatic                   thrpt   15  4190451.030 ±  22611.654  ops/ms
    // InvokeJmh.directInstance              thrpt   15  4104759.241 ± 132020.437  ops/ms
    // InvokeJmh.directStatic                thrpt   15  4117534.303 ± 103860.788  ops/ms
    // InvokeJmh.methodHandleAsTypeInstance  thrpt   15  4139389.445 ±  88579.792  ops/ms
    // InvokeJmh.methodHandleBindInstance    thrpt   15  4134561.554 ±  88977.406  ops/ms
    // InvokeJmh.methodHandleExactInstance   thrpt   15  4120026.173 ± 109740.000  ops/ms
    // InvokeJmh.methodHandleExactStatic     thrpt   15  4109771.586 ± 100635.355  ops/ms
    // InvokeJmh.methodHandleInstance        thrpt   15   146004.456 ±    586.632  ops/ms
    // InvokeJmh.methodHandleStatic          thrpt   15   144744.707 ±    570.352  ops/ms
    // InvokeJmh.reflectInstance             thrpt   15   113840.668 ±    883.911  ops/ms
    // InvokeJmh.reflectStatic               thrpt   15   113759.459 ±   1451.507  ops/ms
}
