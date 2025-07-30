// package xyz.sunqian.common.benchmark;
//
// import org.openjdk.jmh.annotations.Benchmark;
// import org.openjdk.jmh.annotations.BenchmarkMode;
// import org.openjdk.jmh.annotations.Fork;
// import org.openjdk.jmh.annotations.Measurement;
// import org.openjdk.jmh.annotations.Mode;
// import org.openjdk.jmh.annotations.OutputTimeUnit;
// import org.openjdk.jmh.annotations.Scope;
// import org.openjdk.jmh.annotations.State;
// import org.openjdk.jmh.annotations.Warmup;
// import xyz.sunqian.common.invoke.Invocable;
// import xyz.sunqian.common.invoke.InvocationMode;
//
// import java.lang.invoke.MethodHandle;
// import java.lang.invoke.MethodHandles;
// import java.lang.reflect.Method;
// import java.util.concurrent.TimeUnit;
//
// @BenchmarkMode(Mode.Throughput)
// @Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
// @Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
// @Fork(5)
// // @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// // @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// // @Fork(1)
// @State(value = Scope.Benchmark)
// @OutputTimeUnit(TimeUnit.MILLISECONDS)
// public class InvokeJmh {
//
//     private static final Method staticWorld;
//     private static final Method instanceWorld;
//
//     static {
//         try {
//             staticWorld = Hello.class.getMethod("staticWorld", String.class, int.class, double.class);
//             instanceWorld = Hello.class.getMethod("instanceWorld", String.class, int.class, double.class);
//         } catch (NoSuchMethodException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     private static final Hello hello = new Hello();
//     private static final Invocable reflectStatic = Invocable.of(staticWorld, InvocationMode.REFLECTION);
//     private static final Invocable methodHandleStatic = Invocable.of(staticWorld, InvocationMode.METHOD_HANDLE);
//     private static final Invocable asmStatic = Invocable.of(staticWorld, InvocationMode.ASM);
//     private static final Invocable reflectInstance = Invocable.of(instanceWorld, InvocationMode.REFLECTION);
//     private static final Invocable methodHandleInstance = Invocable.of(instanceWorld, InvocationMode.METHOD_HANDLE);
//     private static final Invocable asmInstance = Invocable.of(instanceWorld, InvocationMode.ASM);
//     private static final MethodHandle handleStatic;
//     private static final MethodHandle handleInstance;
//     // private static final MethodHandle handleInstanceAsType;
//     // private static final MethodHandle handleInstanceBind;
//
//     static {
//         try {
//             handleStatic = MethodHandles.lookup().unreflect(staticWorld);
//             handleInstance = MethodHandles.lookup().unreflect(instanceWorld);
//             // handleInstanceAsType = MethodHandles.lookup().unreflect(instanceWorld)
//             //     .asType(MethodType.methodType(String.class, new Class<?>[]{Hello.class, String.class, int.class, double.class}));
//             // handleInstanceBind = MethodHandles.lookup().unreflect(instanceWorld)
//             //     //.asType(MethodType.methodType(String.class, new Class<?>[]{Hello.class, String.class, int.class, double.class}))
//             //     .bindTo(hello);
//         } catch (IllegalAccessException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     @Benchmark
//     public void reflectStatic() {
//         String result = (String) reflectStatic.invoke(null, "hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void methodHandleStatic() {
//         String result = (String) methodHandleStatic.invoke(null, "hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void asmStatic() {
//         String result = (String) asmStatic.invoke(null, "hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void reflectInstance() {
//         String result = (String) reflectInstance.invoke(hello, "hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void methodHandleInstance() {
//         String result = (String) methodHandleInstance.invoke(hello, "hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void asmInstance() {
//         String result = (String) asmInstance.invoke(hello, "hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void directStatic() {
//         String result = Hello.staticWorld("hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void directInstance() {
//         String result = hello.instanceWorld("hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void methodHandleExactStatic() throws Throwable {
//         String result = (String) handleStatic.invokeExact("hello ", 17, 97.123456);
//     }
//
//     @Benchmark
//     public void methodHandleExactInstance() throws Throwable {
//         String result = (String) handleInstance.invokeExact(hello, "hello ", 17, 97.123456);
//     }
//
//     // @Benchmark
//     // public void methodHandleAsTypeInstance() throws Throwable {
//     //     String result = (String) handleInstanceAsType.invoke(hello, "hello ", 17, 97.123456);
//     // }
//     //
//     // @Benchmark
//     // public void methodHandleBindInstance() throws Throwable {
//     //     String result = (String) handleInstanceBind.invoke("hello ", 17, 97.123456);
//     // }
//
//     public static void main(String[] args) throws Exception {
//         org.openjdk.jmh.Main.main(args);
//     }
//
//     // Simple:
//     // Benchmark                             Mode  Cnt        Score       Error   Units
//     // InvokeJmh.asmInstance                thrpt   15   229697.099 ±   728.408  ops/ms
//     // InvokeJmh.asmStatic                  thrpt   15  4827357.714 ± 31006.124  ops/ms
//     // InvokeJmh.directInstance             thrpt   15   229016.045 ±  3764.454  ops/ms
//     // InvokeJmh.directStatic               thrpt   15  4812686.914 ± 63743.421  ops/ms
//     // InvokeJmh.methodHandleExactInstance  thrpt   15   229528.257 ±  2043.629  ops/ms
//     // InvokeJmh.methodHandleExactStatic    thrpt   15  4811603.187 ± 39489.430  ops/ms
//     // InvokeJmh.methodHandleInstance       thrpt   15    93727.319 ±  3966.264  ops/ms
//     // InvokeJmh.methodHandleStatic         thrpt   15   225164.351 ± 11291.816  ops/ms
//     // InvokeJmh.reflectInstance            thrpt   15    76642.955 ±   227.566  ops/ms
//     // InvokeJmh.reflectStatic              thrpt   15   135635.096 ±  1012.306  ops/ms
//
//     // complex:
//     // Benchmark                             Mode  Cnt     Score     Error   Units
//     // InvokeJmh.asmInstance                thrpt   15  6466.218 ±  47.392  ops/ms
//     // InvokeJmh.asmStatic                  thrpt   15  6561.963 ±  49.925  ops/ms
//     // InvokeJmh.directInstance             thrpt   15  6720.759 ± 162.326  ops/ms
//     // InvokeJmh.directStatic               thrpt   15  6950.323 ±  57.689  ops/ms
//     // InvokeJmh.methodHandleExactInstance  thrpt   15  6659.183 ±  52.477  ops/ms
//     // InvokeJmh.methodHandleExactStatic    thrpt   15  7006.169 ±  43.910  ops/ms
//     // InvokeJmh.methodHandleInstance       thrpt   15  6365.741 ± 133.778  ops/ms
//     // InvokeJmh.methodHandleStatic         thrpt   15  6657.156 ±  36.935  ops/ms
//     // InvokeJmh.reflectInstance            thrpt   15  6086.189 ±  51.253  ops/ms
//     // InvokeJmh.reflectStatic              thrpt   15  6172.648 ±  74.860  ops/ms
// }
