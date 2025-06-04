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
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
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
    private static final Invocable reflectStatic = Invocable.of(staticWorld, InvocationMode.REFLECT);
    private static final Invocable methodHandleStatic = Invocable.of(staticWorld, InvocationMode.METHOD_HANDLE);
    private static final Invocable asmStatic = Invocable.of(staticWorld, InvocationMode.ASM);
    private static final Invocable reflectInstance = Invocable.of(instanceWorld, InvocationMode.REFLECT);
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

    // Benchmark                              Mode  Cnt        Score         Error   Units
    // InvokeJmh.asmInstance                 thrpt    3  4717038.404 ±  707062.778  ops/ms
    // InvokeJmh.asmStatic                   thrpt    3  4372303.139 ± 2109991.160  ops/ms
    // InvokeJmh.directInstance              thrpt    3  4268101.828 ± 2126234.030  ops/ms
    // InvokeJmh.directStatic                thrpt    3  3990327.907 ± 9922186.701  ops/ms
    // InvokeJmh.methodHandleAsTypeInstance  thrpt    3  4412897.112 ± 3404715.501  ops/ms
    // InvokeJmh.methodHandleBindInstance    thrpt    3  4498189.368 ±  227591.141  ops/ms
    // InvokeJmh.methodHandleExactInstance   thrpt    3  4473688.690 ±  224196.975  ops/ms
    // InvokeJmh.methodHandleExactStatic     thrpt    3  4481622.080 ±  450904.856  ops/ms
    // InvokeJmh.methodHandleInstance        thrpt    3    21929.607 ±    2837.477  ops/ms
    // InvokeJmh.methodHandleStatic          thrpt    3    23313.752 ±    2928.568  ops/ms
    // InvokeJmh.reflectInstance             thrpt    3   117828.277 ±   39917.995  ops/ms
    // InvokeJmh.reflectStatic               thrpt    3    99623.128 ±  315006.493  ops/ms
}
