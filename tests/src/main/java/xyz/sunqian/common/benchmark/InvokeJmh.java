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
@Fork(3)
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
    private static final MethodHandle handleStatic;
    private static final MethodHandle handleInstance;
    private static final MethodHandle handleInstanceAsType;
    private static final MethodHandle handleInstanceBind;

    static {
        try {
            handleStatic = MethodHandles.lookup().unreflect(staticWorld);
            handleInstance = MethodHandles.lookup().unreflect(instanceWorld);
            handleInstanceAsType = MethodHandles.lookup().unreflect(instanceWorld)
                .asType(MethodType.methodType(String.class, new Class<?>[]{Hello.class, String.class}));
            handleInstanceBind = MethodHandles.lookup().unreflect(instanceWorld)
                .asType(MethodType.methodType(String.class, new Class<?>[]{Hello.class, String.class}))
                .bindTo(hello);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void reflectStatic() {
        String result = (String) reflectStatic.invoke(null, "hi");
    }

    @Benchmark
    public void methodHandleStatic() {
        String result = (String) methodHandleStatic.invoke(null, "hi");
    }

    @Benchmark
    public void asmStatic() {
        String result = (String) asmStatic.invoke(null, "hi");
    }

    @Benchmark
    public void reflectInstance() {
        String result = (String) reflectInstance.invoke(hello, "hi");
    }

    @Benchmark
    public void methodHandleInstance() {
        String result = (String) methodHandleInstance.invoke(hello, "hi");
    }

    @Benchmark
    public void asmInstance() {
        String result = (String) asmInstance.invoke(hello, "hi");
    }

    @Benchmark
    public void directStatic() {
        String result = Hello.staticWorld("hi");
    }

    @Benchmark
    public void directInstance() {
        String result = hello.instanceWorld("hi");
    }

    @Benchmark
    public void methodHandleExactStatic() throws Throwable {
        String result = (String) handleStatic.invokeExact("hi");
    }

    @Benchmark
    public void methodHandleExactInstance() throws Throwable {
        String result = (String) handleInstance.invokeExact(hello, "hi");
    }

    @Benchmark
    public void methodHandleAsTypeInstance() throws Throwable {
        String result = (String) handleInstanceAsType.invoke(hello, "hi");
    }

    @Benchmark
    public void methodHandleBindInstance() throws Throwable {
        String result = (String) handleInstanceBind.invoke("hi");
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
